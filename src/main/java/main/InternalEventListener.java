package main;

import com.fasterxml.jackson.core.JsonProcessingException;
import exceptions.MemberNotFoundException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceStreamEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.managers.Presence;
import org.jetbrains.annotations.NotNull;
import services.env.Environment;
import services.rumi.*;
import util.GPT;
import util.JdaUtil;
import util.Logger;

import javax.annotation.Nonnull;
import java.util.List;

public class InternalEventListener extends ListenerAdapter {
    @Override
    public void onReady(@Nonnull ReadyEvent e) {
        Logger.info("Rumi Bot is now ready.");

        JDA jda = e.getJDA();
        List<Guild> guilds = jda.getGuilds();
        Presence presence = e.getJDA().getPresence();
        for (Guild g : guilds) {
            g.updateCommands().addCommands(
                    Commands.slash("status", "루미의 나에 대한 감정을 확인합니다.")
            ).queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {
        String eventName = e.getName();
        switch (eventName) {
            case "status":
                try {
                    Member member = e.getMember();
                    Emotion emotion = RumiBot.soul.status.getEmotionForUser(member.getId());
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle(String.format("루미의 %s에 대한 감정", member.getEffectiveName()));
                    eb.setDescription("루미는 당신을 어떻게 생각하고 있을까요? 감정 지표는 (-100 ~ 100) 사이의 값으로 표현됩니다.");
                    eb.addField("호감도",
                            JdaUtil.Styler.Block(String.format("%d", (int)(emotion.getLikability().getCurrent() * 100))), true);
                    eb.addField("친밀도",
                            JdaUtil.Styler.Block(String.format("%d", (int)(emotion.getFriendliness().getCurrent() * 100))), true);
                    eb.addField("신뢰도",
                            JdaUtil.Styler.Block(String.format("%d", (int)(emotion.getTrust().getCurrent() * 100))), true);
                    MessageEmbed embed = eb.build();
                    // send direct message
                    e.getUser().openPrivateChannel().complete().sendMessageEmbeds(embed).queue();
                    e.reply("DM으로 내용을 전송했습니다.").queue();
                } catch (Exception ex) {
                    Logger.error(ex.getMessage());
                }
                break;
        }
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent e) {
        if(!e.isFromGuild()) return;
        Guild guild = e.getGuild();
        Member sender = e.getMember();
        TextChannel textChannel = e.getTextChannel();
        Message message = e.getMessage();
        String displayedMessage = message.getContentDisplay();
        String rawMessage = message.getContentRaw();
        long messageIndex = RumiBot.globalMessageIndex++;


        if(sender == null) return;
        if(rawMessage.length() == 0) return;
        if(sender.getUser().isBot()) return;
        Logger.debugf("[%s/%s] %s(%s): %s",
                guild.getName(),
                textChannel.getName(),
                sender.getEffectiveName(),
                sender.getUser().getId(),
                displayedMessage
        );

        Emotion emotion = RumiBot.soul.status.getEmotionForUser(sender.getId());

        // check if this is mentioned message
        boolean rumiMentioned = false;
        boolean rumiIncluded = displayedMessage.contains("루미");
        List<Member> mentionedMembers = message.getMentions().getMembers();
        if (!mentionedMembers.isEmpty()) {
            for (Member mentionedMember : mentionedMembers) {
                if(mentionedMember.getId().equals(Environment.DISCORD_CLIENT_ID)){
                    rumiMentioned = true;
                    break;
                }
            }
        }

        if(rumiMentioned || rumiIncluded) {
            Logger.debugf("Rumi mentioned by %s", sender.getEffectiveName());
            LastCertainMessageInfo mentionInfo = new LastCertainMessageInfo(
                    messageIndex,
                    message
            );
            RumiBot.soul.registerLastMentionedMessage(sender.getUser().getId(), mentionInfo);

            if(rumiMentioned) {
                try {
                    AudioChannel audioChannel = JdaUtil.GetUserAudioChannel(sender);

                    if (audioChannel != null) {
                        // join audio channel (if rumi has enough friendliness and willing to join audio channel for sender)
                        // this willingness is determined by the emotion of rumi for the sender
                        if (emotion.getFriendliness().getCurrent() > -0.5) {
                            audioChannel.getGuild().getAudioManager().openAudioConnection(audioChannel);
                        } else {
                            Logger.debugf("Rumi is not willing to join audio channel for %s, (friendliness: %.2f)",
                                    sender.getEffectiveName(), emotion.getFriendliness().getCurrent());
                        }
                    }
                } catch (MemberNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        double certainty = RumiBot.soul.getCertaintyForAcceptingForRumi(sender.getId());
        if(rumiMentioned) {
            certainty = 1.0;
        } else if (rumiIncluded) {
            certainty = 0.8;
        }

        if(certainty < Environment.RUMI_PREDICT_CERTAINTY_THRESHOLD) {
            if(certainty > 0) {
                Logger.debugf("Rumi is not willing to respond to this. (certainty: %.3f%%)", certainty * 100);
            } else {
                // ignored
                Logger.debugf("Rumi ignored this message. (certainty: %.3f%%)", certainty * 100);
            }
        } else {
            try {
                // get message content with removing mention in this scope
                String messageContent = JdaUtil.removeMention(rawMessage);
                // just chat
                String content = "";
                if(rumiMentioned) {
                    content = "Action:(대상이 루미를 부름) + ";
                } else if (rumiIncluded) {
                    content = "Action:(대상이 메시지 중 루미를 언급함) + ";
                }
                content = content + messageContent;
                RumiAcceptEvent acceptEvent = new RumiAcceptEvent(
                        sender,
                        false,
                        certainty,
                        content
                );

                Logger.debug("Rumi is thinking response about this message...");
                String response = RumiBot.soul.respond(acceptEvent);
                if (response != null) {
                    textChannel.sendMessage(response).queue();
                }
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent e) {
        super.onGuildVoiceJoin(e);

        User joiner = e.getMember().getUser();
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent e) {
        super.onGuildVoiceLeave(e);
    }

    @Override
    public void onGuildVoiceStream(@NotNull GuildVoiceStreamEvent e) {
        super.onGuildVoiceStream(e);
    }
}
