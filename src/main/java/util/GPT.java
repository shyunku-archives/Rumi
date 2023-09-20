package util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import services.env.Environment;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.List;

public class GPT {
    public static Response.CompletionResponse response(Request.CompletionRequest request) throws IOException {
//        String requestJson = new ObjectMapper().writeValueAsString(request);
        String url = "https://api.openai.com/v1/chat/completions";
        return HTTP.post(
                url,
                request,
                new HTTP.HttpRequestOption(null, Environment.OPENAI_API_KEY),
                Response.CompletionResponse.class
        );
    }

    public static class Request {
        public static class CompletionMessage {
            public String role;
            public String content;
            public String name;

            public CompletionMessage(String role, String content, String name) {
                this.role = role;
                this.content = content;
                this.name = name;
            }
        }

        public static class CompletionRequest {
            public String model;
            public List<CompletionMessage> messages;
            public double temperature;
            @JsonProperty("top_p")
            public double topP;
            @JsonProperty("presence_penalty")
            public double presencePenalty;
            @JsonProperty("frequency_penalty")
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

            public CompletionRequest(String model, List<CompletionMessage> messages, double temperature, double presencePenalty, double frequencyPenalty, boolean stream) {
                this.model = model;
                this.messages = messages;
                this.temperature = temperature;
                this.presencePenalty = presencePenalty;
                this.frequencyPenalty = frequencyPenalty;
                this.stream = stream;
            }
        }
    }

   public static class Response {
        public static class Message {
            public String role;
            public String content;

            public Message() {
            }

            public Message(String role, String content) {
                this.role = role;
                this.content = content;
            }
        }
       public static class Choice {
           public int index;
           public Message message;
            @JsonProperty("finish_reason")
           public String finishReason;

           public Choice() {
           }
       }

       public static class Usage {
            @JsonProperty("prompt_tokens")
           public long promptTokens;
            @JsonProperty("completion_tokens")
           public long completionTokens;
            @JsonProperty("total_tokens")
           public long totalTokens;

           public Usage() {
           }
       }

       public static class CompletionResponse {
           public String id;
           public String object;
           public long created;
           public String model;
           @JsonProperty("choices")
           public List<Choice> choices;
           public Usage usage;

           public CompletionResponse() {
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
