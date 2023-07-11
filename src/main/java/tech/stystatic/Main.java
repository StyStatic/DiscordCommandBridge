package tech.stystatic;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tech.stystatic.Sockets.TransferServer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static JDA jda;
    public static String token;
    public static String guildid;
    public static String channelname;
    public static String ip;
    public static String port;
    public static Boolean prefixmode;
    public static String prefix;

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        String jarPath = Main.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
                .getPath();
        String ParentPath = (new File(jarPath)).getParentFile().getPath();

        JSONParser jsonParser = new JSONParser();

        // ParentPath + "/config.json"
        // "src/main/resources/config.json"
        try (FileReader reader = new FileReader(ParentPath + "/config.json")) {
            Object obj = jsonParser.parse(reader);
            JSONObject tokenJson = (JSONObject) obj;
            token = (String) tokenJson.get("token");
            guildid = (String) tokenJson.get("guildid");
            channelname = (String) tokenJson.get("channelname");
            ip = (String) tokenJson.get("ip");
            port = (String) tokenJson.get("port");
            prefixmode = (Boolean) tokenJson.get("prefixmode");
            prefix = (String) tokenJson.get("prefix");


        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        jda = JDABuilder.createLight(token)
                .setActivity(Activity.listening("StyStatic SMP"))
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .build();

        jda.awaitReady();

        TransferServer server = new TransferServer();
        server.start();
    }
}