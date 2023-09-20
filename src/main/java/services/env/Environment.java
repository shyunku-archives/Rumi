package services.env;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Environment {
    // GPT 없이 사전 계산한 "상대가 루미에게 말을 걸었을 likelihood"가
    // 이 값을 넘어야 루미에게 전달됨
    public static final double RUMI_PREDICT_CERTAINTY_THRESHOLD = 0.3;
    public static final String VERSION = "0.1.0";

    // Clova
    public static final String CLOVA_CLIENT_ID;
    public static final String CLOVA_CLIENT_SECRET;

    // Discord
    public static final String DISCORD_BOT_TOKEN;
    public static final String DISCORD_CLIENT_ID;

    // OpenAI
    public static final String OPENAI_API_KEY;

    // System
    public static final boolean PRODUCTION_MODE;

    static {
        try {
            FileInputStream envFileStream = new FileInputStream("src/main/resources/env.json");
            String content = new String(envFileStream.readAllBytes());
            JSONObject object = new JSONObject(content);

            JSONObject clovaProps = object.getJSONObject("clova");
            CLOVA_CLIENT_ID = clovaProps.getString("client_id");
            CLOVA_CLIENT_SECRET = clovaProps.getString("client_secret");

            JSONObject discordProps = object.getJSONObject("discord");
            DISCORD_BOT_TOKEN = discordProps.getString("bot_token");
            DISCORD_CLIENT_ID = discordProps.getString("client_id");

            JSONObject openaiProps = object.getJSONObject("openai");
            OPENAI_API_KEY = openaiProps.getString("api_key");

            PRODUCTION_MODE = object.getBoolean("production");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
