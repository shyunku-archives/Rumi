package services.rumi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import util.GPT;

import java.util.List;

public class Rumi {
    public Status status = new Status();

    public Rumi() {
    }

    public String respond(RumiAcceptEvent e) throws JsonProcessingException {
        RumiResponseEvent response = getResponseFromGPT(e);
        this.applyEmotionTransition(response);
        return response.getReply();
    }

    public RumiResponseEvent getResponseFromGPT(RumiAcceptEvent e) throws JsonProcessingException {
        String eventString = new ObjectMapper().writeValueAsString(e);
        GPT.Request.CompletionRequest gptReq = new GPT.Request.CompletionRequest(
                GPT.Model.GPT_4,
                List.of(new GPT.Request.CompletionMessage(GPT.Role.USER, eventString, e.getTalker())),
                0.8,
                false
        );
        GPT.Response.CompletionResponse gptResp = GPT.response(gptReq);
        return new RumiResponseEvent(e.getTalker(), e.getTalkerId(), gptResp.choices.get(0).text, null);
    }

    private void applyEmotionTransition(RumiResponseEvent e) {
        Emotion emotion = this.status.getEmotionForUser(e.getTargetId());
        if(e.getEmotionWeight() == null) return;
        emotion.applyTransition(e.getEmotionWeight());
        this.status.setEmotionForUser(e.getTargetId(), emotion);
    }
}
