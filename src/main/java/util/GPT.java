package util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpRequest;
import java.util.List;

public class GPT {
    public static Response.CompletionResponse response(Request.CompletionRequest request) throws JsonProcessingException {
        String requestJson = new ObjectMapper().writeValueAsString(request);
        String url = "https://api.openai.com/v1/chat/completions";
        return HTTP.post(url, requestJson, Response.CompletionResponse.class);
    }

    public static class Request {
        public static class CompletionMessage {
            public String role;
            public String prompt;
            public String name;

            public CompletionMessage(String role, String prompt, String name) {
                this.role = role;
                this.prompt = prompt;
                this.name = name;
            }
        }

        public static class CompletionRequest {
            public String model;
            public List<CompletionMessage> messages;
            public double temperature;
            @JsonProperty("top_p")
            public double topP;
            public double presencePenalty;
            public double frequencyPenalty;
            public String stop;
            public boolean stream;

            public CompletionRequest(String model, List<CompletionMessage> messages, double temperature, double topP, double presencePenalty, double frequencyPenalty, String stop, boolean stream) {
                this.model = model;
                this.messages = messages;
                this.temperature = temperature;
                this.topP = topP;
                this.presencePenalty = presencePenalty;
                this.frequencyPenalty = frequencyPenalty;
                this.stop = stop;
                this.stream = stream;
            }

            public CompletionRequest(String model, List<CompletionMessage> messages, double temperature, boolean stream) {
                this.model = model;
                this.messages = messages;
                this.temperature = temperature;
                this.stream = stream;
            }
        }
    }

   public static class Response {
       public static class Choice {
           public String text;
           public int index;
           public Object logprobs;
           public String finishReason;

           public Choice(String text, int index, Object logprobs, String finishReason) {
               this.text = text;
               this.index = index;
               this.logprobs = logprobs;
               this.finishReason = finishReason;
           }
       }

       public static class Usage {
           public long promptTokens;
           public long completionTokens;
           public long totalTokens;

           public Usage(long promptTokens, long completionTokens, long totalTokens) {
               this.promptTokens = promptTokens;
               this.completionTokens = completionTokens;
               this.totalTokens = totalTokens;
           }
       }

       public static class CompletionResponse {
           public String id;
           public String object;
           public long created;
           public String model;
           public List<Choice> choices;

           public CompletionResponse(String id, String object, long created, String model, List<Choice> choices) {
               this.id = id;
               this.object = object;
               this.created = created;
               this.model = model;
               this.choices = choices;
           }
       }
   }

   public static class Role {
        public static String SYSTEM = "system";
        public static String USER = "user";
        public static String ASSISTANT = "assistant";
        public static String FUNCTION = "function";
   }

    public static class Model {
        public static String GPT_4 = "gpt-4";
        public static String GPT_4_0613 = "gpt-4-0613";
        public static String GPT_4_32k = "gpt-4-32k"; // not available
        public static String GPT_4_32k_0613 = "gpt-4-32k-0613"; // not available
        public static String GPT_3_5_TURBO = "gpt-3.5-turbo";
        public static String GPT_3_5_TURBO_16k = "gpt-3.5-turbo-16k";
    }
}
