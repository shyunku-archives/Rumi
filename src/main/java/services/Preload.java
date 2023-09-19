package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import util.GPT;
import util.Logger;

import java.util.List;

public class Preload {
    public static void initialize() {
        // send GPT request to warm up the model
        GPT.Request.CompletionRequest gptReq = new GPT.Request.CompletionRequest(
                GPT.Model.GPT_4,
                List.of(new GPT.Request.CompletionMessage(GPT.Role.SYSTEM, "input prompt for initialize", "Developer")),
                1,
                false
        );
        try {
            GPT.Response.CompletionResponse gptResp = GPT.response(gptReq);
            String stringified = new ObjectMapper().writeValueAsString(gptResp);
            Logger.debugf("GPT INIT: %s", stringified);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
