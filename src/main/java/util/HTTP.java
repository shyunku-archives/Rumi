package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HTTP {
    public static String get(String url) {
        throw new UnsupportedOperationException();
    }

    public static <T> T post(String targetUrl, Object data, HttpRequestOption option, Class<T> type) throws IOException {
        HttpURLConnection conn = null;
        String jsonData = new ObjectMapper().writeValueAsString(data);
//            Logger.debug("Posting: " + jsonData);
        byte[] postData = jsonData.getBytes(StandardCharsets.UTF_8);

        URL url = new URL(targetUrl);
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", option.contentType);
        if(option.bearerToken != null) {
            conn.setRequestProperty("Authorization", "Bearer " + option.bearerToken);
        }
        conn.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.write(postData);
        wr.flush();
        wr.close();

        if(conn.getResponseCode() >= 400) {
            InputStream is = conn.getErrorStream();
            BufferedReader rd = new BufferedReader(new java.io.InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line.trim());
                response.append("\\n");
            }
            rd.close();
            throw new RuntimeException("Error response from " + targetUrl + ": " + response);
        }

        InputStream is = conn.getInputStream();
        BufferedReader rd = new BufferedReader(new java.io.InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\n');
        }
        rd.close();

        String rawResponse = response.toString();
        ObjectMapper mapper = new ObjectMapper();
//            Logger.debug("rawResponse: " + rawResponse);
        T a = mapper.readValue(rawResponse, type);
        Logger.debug("formatted: " + new ObjectMapper().writeValueAsString(a));
        return a;
    }

    public static class HttpRequestOption {
        public String contentType;
        public String bearerToken;

        public HttpRequestOption(String contentType, String bearerToken) {
            this.contentType = (contentType != null) ? contentType : "application/json";
            this.bearerToken = bearerToken;
        }
    }
}
