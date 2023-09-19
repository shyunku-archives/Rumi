package services.rumi;

public class RumiUtil {
    public static String wrapBlock(String content, String key) {
        return String.format("{{%s}}%s{{%s}}", key, content, key);
    }

    public static String unwrapBlock(String content, String key) {
        return content.replace(String.format("{{%s}}", key), "").trim();
    }
}
