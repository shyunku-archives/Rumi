package services.rumi;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Emotion {
    @JsonSerialize(using = StdRanged.Serializer.class)
    @JsonDeserialize(using = StdRanged.Deserializer.class)
    private StdRanged likability = new StdRanged();
    @JsonSerialize(using = StdRanged.Serializer.class)
    @JsonDeserialize(using = StdRanged.Deserializer.class)
    private StdRanged friendliness = new StdRanged();
    @JsonSerialize(using = StdRanged.Serializer.class)
    @JsonDeserialize(using = StdRanged.Deserializer.class)
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
