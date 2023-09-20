package services.rumi;

import net.dv8tion.jda.api.entities.Message;

public class LastCertainMessageInfo {
    private long messageIndex;
    private Message message;

    public LastCertainMessageInfo(long messageIndex, Message message) {
        this.messageIndex = messageIndex;
        this.message = message;
    }

    public long getMessageIndex() {
        return messageIndex;
    }

    public Message getMessage() {
        return message;
    }
}
