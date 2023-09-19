package services.rumi;

import java.util.HashMap;

public class Status {
    // all status are in range [-1, 1]
    // positive properties
    private StdRanged happy = new StdRanged();
    private StdRanged excited = new StdRanged();
    private StdRanged surprised = new StdRanged();
    private StdRanged calm = new StdRanged();

    // negative properties
    private StdRanged sad = new StdRanged();
    private StdRanged angry = new StdRanged();
    private StdRanged disgusted = new StdRanged();
    private StdRanged fearful = new StdRanged();

    // emotion properties (for user) <JDA::userId, UserLikability>
    private final HashMap<String, Emotion> emotions = new HashMap<>();

    public Status() {
    }

    public Emotion getEmotionForUser(String userId) {
        if (!emotions.containsKey(userId)) {
            emotions.put(userId, new Emotion());
        }
        return emotions.get(userId);
    }

    public void setEmotionForUser(String userId, Emotion emotion) {
        emotions.put(userId, emotion);
    }

    public StdRanged getHappy() {
        return happy;
    }

    public void setHappy(StdRanged happy) {
        this.happy = happy;
    }

    public StdRanged getExcited() {
        return excited;
    }

    public void setExcited(StdRanged excited) {
        this.excited = excited;
    }

    public StdRanged getSurprised() {
        return surprised;
    }

    public void setSurprised(StdRanged surprised) {
        this.surprised = surprised;
    }

    public StdRanged getCalm() {
        return calm;
    }

    public void setCalm(StdRanged calm) {
        this.calm = calm;
    }

    public StdRanged getSad() {
        return sad;
    }

    public void setSad(StdRanged sad) {
        this.sad = sad;
    }

    public StdRanged getAngry() {
        return angry;
    }

    public void setAngry(StdRanged angry) {
        this.angry = angry;
    }

    public StdRanged getDisgusted() {
        return disgusted;
    }

    public void setDisgusted(StdRanged disgusted) {
        this.disgusted = disgusted;
    }

    public StdRanged getFearful() {
        return fearful;
    }

    public void setFearful(StdRanged fearful) {
        this.fearful = fearful;
    }
}
