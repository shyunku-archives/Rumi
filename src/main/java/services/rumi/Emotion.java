package services.rumi;

public class Emotion {
    private StdRanged likability = new StdRanged();
    private StdRanged friendliness = new StdRanged();
    private StdRanged trust = new StdRanged();

    public Emotion() {
    }

    public StdRanged getLikability() {
        return likability;
    }

    public StdRanged getFriendliness() {
        return friendliness;
    }

    public StdRanged getTrust() {
        return trust;
    }

    public void applyTransition(Emotion emotion) {
        this.likability.fluctuate(emotion.likability);
        this.friendliness.fluctuate(emotion.friendliness);
        this.trust.fluctuate(emotion.trust);
    }
}
