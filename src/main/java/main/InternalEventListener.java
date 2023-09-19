package main;

import com.fasterxml.jackson.core.JsonProcessingException;
import exceptions.MemberNotFoundException;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceStreamEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.Presence;
import org.jetbrains.annotations.NotNull;
import services.env.Environment;
import services.rumi.Emotion;
import services.rumi.RumiAcceptEvent;
import services.rumi.RumiUtil;
import util.JdaUtil;
import util.Logger;

import javax.annotation.Nonnull;
import java.util.List;

public class InternalEventListener extends ListenerAdapter {
    @Override
    public void onReady(@Nonnull ReadyEvent e) {
        Logger.info("Rumi Bot is now ready.");

        Presence presence = e.getJDA().getPresence();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent e) {
        Guild guild = e.getGuild();
        Member sender = e.getMember();
        TextChannel textChannel = e.getTextChannel();
        Message message = e.getMessage();
        String displayedMessage = message.getContentDisplay();
        String rawMessage = message.getContentRaw();

        if(sender == null) return;
        if(rawMessage.length() == 0) return;
        Logger.debugf("[%s/%s] %s: %s", guild.getName(), textChannel.getName(), sender.getEffectiveName(), displayedMessage);

        Emotion emotion = RumiBot.soul.status.getEmotionForUser(sender.getId());

        // check if this is mentioned message
        boolean rumiMentioned = false;
        List<Member> mentionedMembers = message.getMentions().getMembers();
        if (!mentionedMembers.isEmpty()) {
            for (Member mentionedMember : mentionedMembers) {
                if(mentionedMember.getId().equals(Environment.DISCORD_CLIENT_ID)){
                    rumiMentioned = true;
                    break;
                }
            }
        }

        if(rumiMentioned) {
            Logger.debugf("Rumi mentioned by %s", sender.getEffectiveName());
            try {
                AudioChannel audioChannel = JdaUtil.GetUserAudioChannel(sender);
                if(audioChannel == null) {
                    // just chat
                    RumiAcceptEvent acceptEvent = new RumiAcceptEvent(sender, RumiUtil.wrapBlock(sender.getEffectiveName() + " mentioned rumi.", "SYSTEM"));
                    String response = RumiBot.soul.respond(acceptEvent);
                    if(!response.isEmpty()) {
                        textChannel.sendMessage(response).queue();
                    }
                    return;
                }

                // join audio channel (if rumi has enough friendliness and willing to join audio channel for sender)
                // this willingness is determined by the emotion of rumi for the sender
                if(emotion.getFriendliness().getCurrent() > -0.5) {
                    audioChannel.getGuild().getAudioManager().openAudioConnection(audioChannel);
                }
            } catch (MemberNotFoundException ex) {
                throw new RuntimeException(ex);
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
