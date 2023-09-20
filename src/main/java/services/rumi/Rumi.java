package services.rumi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.RumiBot;
import util.GPT;
import util.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Rumi {
    public Status status = new Status();
    public static GPT.Request.CompletionMessage instruction;

    private final HashMap<String, LastCertainMessageInfo> lastMentionedMessages = new HashMap<>();
    private final HashMap<String, List<ConversationEntity>> conversations = new HashMap<>();

    public Rumi() {
    }

    public void registerLastMentionedMessage(String userId, LastCertainMessageInfo mentionInfo) {
        lastMentionedMessages.put(userId, mentionInfo);
    }

    public void removeLastMentionedMessage(String userId) {
        lastMentionedMessages.remove(userId);
    }

    public void registerConversation(String userId, ConversationEntity conversation) {
        if(!conversations.containsKey(userId)) {
            conversations.put(userId, new ArrayList<>());
        }
        conversations.get(userId).add(conversation);
    }

    public void removeUncertainConversation(String userId) {
        if(!conversations.containsKey(userId)) return;
        List<ConversationEntity> conversation = conversations.get(userId);
        if(conversation.isEmpty()) return;
        ConversationEntity lastConversation = conversation.get(conversation.size() - 1);
        if(!lastConversation.name.equals(GPT.Role.USER)) return;
        // delete
        conversation.remove(conversation.size() - 1);
    }

    // calculates the likelihood of "if the user is talking to rumi"
    public double getCertaintyForAcceptingForRumi(String userId) {
        if(!lastMentionedMessages.containsKey(userId)) return 0;
        LastCertainMessageInfo mentionInfo = lastMentionedMessages.get(userId);
        // slowly reduced by time elapsed
        double timeElapsed = (System.currentTimeMillis() - mentionInfo.getMessage().getTimeCreated().toInstant().toEpochMilli()) / 1000.0;
        double timeCertainty = 1 / (timeElapsed / 5000 + 1);
        double timeCertaintyClamped = Math.max(0, Math.min(1, timeCertainty));
        // slowly reduced by number of messages sent by others while conversation
        long messageIndexDiff = RumiBot.globalMessageIndex - mentionInfo.getMessageIndex();
        double messageIndexCertainty = 10.0 / (messageIndexDiff + 8);
        double messageIndexCertaintyClamped = Math.max(0, Math.min(1, messageIndexCertainty));
        return Math.sqrt(timeCertaintyClamped * messageIndexCertaintyClamped);
    }

    public String respond(RumiAcceptEvent e) throws JsonProcessingException {
        RumiResponseEvent response = null;
        try {
            response = getResponseFromGPT(e);
            if(response.getReply().isEmpty() || response.getCertainty() < 0.5) {
                Logger.debugf("Rumi is not certain enough to respond to %s (certainty: %f%%)", e.getTalker(), response.getCertainty() * 100);
                this.removeLastMentionedMessage(e.getTalkerId());
                return null;
            }
            if(response.getFinishCertainty() > 0.5) {
                Logger.debugf("Rumi is certain enough to finish conversation with %s (certainty: %f%%)", e.getTalker(), response.getFinishCertainty() * 100);
                this.removeLastMentionedMessage(e.getTalkerId());
                this.removeUncertainConversation(e.getTalkerId());
            }
            // add response to history
            String rawReply = response.getReply();
            String reply = String.format("%s [certainty: %.0f%%, finish: %.0f%%]", rawReply, response.getCertainty() * 100, response.getFinishCertainty() * 100);
            this.registerConversation(e.getTalker(), new ConversationEntity(GPT.Role.ASSISTANT, rawReply));
            this.applyEmotionTransition(response);
            return reply;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public RumiResponseEvent getResponseFromGPT(RumiAcceptEvent e) throws IOException {
        ArrayList<GPT.Request.CompletionMessage> messages = new ArrayList<>();
        messages.add(Rumi.instruction);
        List<ConversationEntity> recentConversations = conversations.getOrDefault(e.getTalker(), new ArrayList<>());
        // slice old
        if(recentConversations.size() > 20) {
            recentConversations = recentConversations.subList(recentConversations.size() - 20, recentConversations.size());
        }
        for (ConversationEntity conversation : recentConversations) {
            messages.add(new GPT.Request.CompletionMessage(conversation.name, conversation.content, "user"));
        }
        // add current content
        messages.add(new GPT.Request.CompletionMessage(GPT.Role.USER, e.toString(), "user"));

        GPT.Request.CompletionRequest gptReq = new GPT.Request.CompletionRequest(
                GPT.Model.GPT_4,
                messages,
                1.2,
                0.5,
                0.5,
                false
        );
        GPT.Response.CompletionResponse gptResp = GPT.response(gptReq);
        return new ObjectMapper().readValue(gptResp.choices.get(0).message.content, RumiResponseEvent.class);
    }

    private void applyEmotionTransition(RumiResponseEvent e) {
        Emotion emotion = this.status.getEmotionForUser(e.getTargetId());
        if(e.getEmotionWeight() == null) return;
        emotion.applyTransition(e.getEmotionWeight());
        this.status.setEmotionForUser(e.getTargetId(), emotion);
    }
}
