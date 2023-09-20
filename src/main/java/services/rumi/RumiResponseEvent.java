package services.rumi;

public class RumiResponseEvent {
    private String targetId;
    private double certainty;

    private double finishCertainty;
    private String reply;
    private Emotion emotionWeight;

    public RumiResponseEvent() {
    }

    public double getCertainty() {
        return certainty;
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

    public double getFinishCertainty() {
        return finishCertainty;
    }
}
