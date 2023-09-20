package main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import services.Preload;
import services.env.Environment;
import services.redis.RedisClient;
import services.rumi.Rumi;
import services.rumi.Status;
import util.Logger;

import javax.security.auth.login.LoginException;

public class RumiBot {
    public static Rumi soul = new Rumi();
    public static long globalMessageIndex = 0;

    public static void main(String[] args) throws LoginException {
        Logger.info("Starting Rumi Bot...");

        try {
            RedisClient redisClient = new RedisClient();
            redisClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        Preload.initialize();

        JDABuilder builder = JDABuilder.createDefault(Environment.DISCORD_BOT_TOKEN)
                .enableIntents(GatewayIntent.GUILD_MESSAGES)
                .disableIntents(GatewayIntent.DIRECT_MESSAGES)
//                .enableIntents(GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new InternalEventListener());

        if (Environment.PRODUCTION_MODE) {
            builder.setActivity(Activity.playing(Environment.VERSION + "v - 정상 운영"));
            builder.setStatus(OnlineStatus.ONLINE);
        } else {
            builder.setActivity(Activity.playing(Environment.VERSION + "v - 점검"));
            builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        }

        builder.build();
    }
}
