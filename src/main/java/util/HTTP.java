package util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTP {
    public static String get(String url) {
        throw new UnsupportedOperationException();
    }

    public static <T> T post(String targetUrl, String data, Class<T> type) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(targetUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(data);
            wr.flush();
            wr.close();

            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new java.io.InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            String rawResponse = response.toString();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(rawResponse, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
