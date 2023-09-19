package services.rumi;

import main.RumiBot;
import net.dv8tion.jda.api.entities.Member;

public class RumiAcceptEvent {
    private String talker;
    private String talkerId;
    private String message;
    private Emotion emotion;

    public RumiAcceptEvent(String talker, String talkerId, String message, Emotion emotion) {
        this.talker = talker;
        this.talkerId = talkerId;
        this.message = message;
        this.emotion = emotion;
    }

    public RumiAcceptEvent(Member member, String message) {
        this.talker = member.getEffectiveName();
        this.talkerId = member.getUser().getId();
        this.message = message;
        this.emotion = RumiBot.soul.status.getEmotionForUser(talkerId);
    }

    public String getTalker() {
        return talker;
    }

    public String getTalkerId() {
        return talkerId;
    }

    public String getMessage() {
        return message;
    }

    public Emotion getEmotion() {
        return emotion;
    }
}
