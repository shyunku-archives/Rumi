package util;

import exceptions.AudioChannelNotFoundException;
import exceptions.MemberNotFoundException;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.managers.AudioManager;

public class JdaUtil {
    public static String removeMention(String message) {
        // 사용자 멘션 제거
        message = message.replaceAll("<@!?[0-9]+>", "");
        // 역할 멘션 제거
        message = message.replaceAll("<@&[0-9]+>", "");
        // 채널 멘션 제거
        message = message.replaceAll("<#[0-9]+>", "");
        return message;
    }
    public static void LeaveCurrentAudioChannel(Guild guild) {
        try {
            GuildVoiceState voiceState = guild.getSelfMember().getVoiceState();
            if(voiceState == null) {
                throw new Exception("voiceState is null");
            }
            AudioChannel connectedChannel = voiceState.getChannel();
            if (connectedChannel != null) {
                guild.getAudioManager().closeAudioConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void JoinAudioChannel(AudioChannel audioChannel) throws AudioChannelNotFoundException {
        if(audioChannel == null) throw new AudioChannelNotFoundException();
        Guild guild = audioChannel.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        ConnectionStatus status = audioManager.getConnectionStatus();
        switch (status) {
            case CONNECTED:
            case CONNECTING_ATTEMPTING_UDP_DISCOVERY:
                AudioChannel connectedChannel = audioManager.getConnectedChannel();
                assert connectedChannel != null;
                if(connectedChannel.getIdLong() != audioChannel.getIdLong()) {
                    audioManager.closeAudioConnection();
                }
                break;
        }
        audioManager.openAudioConnection(audioChannel);
    }

    public static AudioChannel GetUserAudioChannel(Member member) throws MemberNotFoundException {
        if(member == null) throw new MemberNotFoundException();
        GuildVoiceState voiceState = member.getVoiceState();
        if(voiceState == null) return null;
        return voiceState.getChannel();
    }

    public static class Styler {
        public static String Bold(String str){
            return wrap(str, "**");
        }

        public static String Block(String str){
            return wrap(str, "`");
        }

        public static String Link(String label, String url){
            return String.format("[%s](%s)",label, url);
        }

        public static String Link(String url){
            return String.format("[%s](%s)", url, url);
        }

        public static String Box(String str){
            return wrap(str, "```");
        }

        public static String Italic(String str){
            return wrap(str, "*");
        }

        public static String Underline(String str){
            return wrap(str, "__");
        }

        public static String wrap(String str, String wrapper){
            return wrapper + str + wrapper;
        }
    }
}
