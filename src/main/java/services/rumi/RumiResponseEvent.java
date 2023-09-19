package services.rumi;

public class RumiResponseEvent {
    private String targetName;
    private String targetId;
    private String reply;
    private Emotion emotionWeight;

    public RumiResponseEvent(String targetName, String targetId, String reply, Emotion emotionWeight) {
        this.targetName = targetName;
        this.targetId = targetId;
        this.reply = reply;
        this.emotionWeight = emotionWeight;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getReply() {
        return reply;
    }

    public Emotion getEmotionWeight() {
        return emotionWeight;
    }
}
