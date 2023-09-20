package services.rumi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.RumiBot;
import net.dv8tion.jda.api.entities.Member;

public class RumiAcceptEvent {
    private String talker;
    private String talkerId;
    private boolean isVoice;
    private double certainty;
    private String message;
    private Emotion emotion;

    public RumiAcceptEvent(String talker, String talkerId, boolean isVoice, double certainty, String message, Emotion emotion) {
        this.talker = talker;
        this.talkerId = talkerId;
        this.isVoice = isVoice;
        this.certainty = certainty;
        this.message = message;
        this.emotion = emotion;
    }

    public RumiAcceptEvent(Member member, boolean isVoice, double certainty, String message) {
        this.talker = member.getEffectiveName();
        this.talkerId = member.getUser().getId();
        this.isVoice = isVoice;
        this.certainty = certainty;
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

    public boolean isVoice() {
        return isVoice;
    }

    public double getCertainty() {
        return certainty;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
