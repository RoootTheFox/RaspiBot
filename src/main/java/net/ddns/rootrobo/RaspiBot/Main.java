package net.ddns.rootrobo.RaspiBot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ddns.rootrobo.RaspiBot.config.Config;
import net.ddns.rootrobo.RaspiBot.console.Console;
import net.ddns.rootrobo.RaspiBot.log.LogFormatter;
import net.ddns.rootrobo.RaspiBot.mysql.DataSource;
import net.ddns.rootrobo.RaspiBot.stuff.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.jar.JarFile;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class Main {
    public static int EVENTCOUNT = 0;
    public static int COMMANDCOUNT = 0;
    public static int CONSOLECOMMANDCOUNT = 0;
    public static String PREFIX;
    public static String DEVELOPER_NAME;
    public static String DEVELOPER_ID = "512242962407882752";
    public static JDA bot = null;
    public static long UPTIME = 0;
    public static Version VERSION = new Version("0.0.0");
    public static String MYSQL_HOST = "";
    public static String MYSQL_USER = "";
    public static String MYSQL_PASS = "";
    public static String MYSQL_DB = "";

    public static boolean READY = false;

    protected static Config config = new Config();

    public static Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws SQLException {
        System.out.println("Starting ...");
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LogFormatter());
        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(handler);

        // read stuff from Bot JAR
        String jarPath = "RaspiBot.jar";
        try {
            jarPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        try {
            JarFile jar = new JarFile(jarPath);
            InputStream botJsonStream = jar.getInputStream(jar.getEntry("raspibot.json"));

            BufferedReader rd;
            StringBuffer result;
            rd = new BufferedReader(new InputStreamReader(botJsonStream));

            result = new StringBuffer();
            String line;

            try {
                try {
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                } catch (NullPointerException ignored) {
                }
            } catch (IOException ignored) {
            }

            String botJson = result.toString();

            JsonObject botInfo = JsonParser.parseString(botJson).getAsJsonObject();
            JsonObject versionInfo = botInfo.get("version").getAsJsonObject();

            String versionRelease = versionInfo.get("release").getAsString();
            VersionType versionType = VersionType.valueOf(versionInfo.get("type").getAsString());

            VERSION = new Version(versionRelease).setType(versionType);

            LOGGER.info("Starting RaspiBot v"+VERSION.getFullVersionString());
        } catch (FileNotFoundException e) {
            LOGGER.warning("Could not load bot Json! If this is a development environment, you can ignore this message.");
            LOGGER.info("Starting RaspiBot v"+VERSION.getVersionString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Config.load("config.json"); // load config
        config = Config.getInstance(); // get config instance
        config.toFile("config.json"); // save config to create file if it doesn't exist
        PREFIX = config.prefix;

        Console.start(); // start listening for console input

        String token = config.token;
        if(token.equals("<put your token here>")) {
            Main.LOGGER.info("You need to specify a token!");
            System.exit(0);
        }

        MYSQL_HOST = config.mysql_host;
        MYSQL_USER = config.mysql_username;
        MYSQL_PASS = config.mysql_password;
        MYSQL_DB = config.mysql_database;

        JDABuilder jda = JDABuilder.createDefault(token);
        EventManager.registerEvents(jda);

        jda.setMemberCachePolicy(MemberCachePolicy.ALL);

        EVENTCOUNT = EventManager.getEventCount();
        CommandManager.register();
        COMMANDCOUNT = CommandManager.getCommandCount();

        ConsoleCommandManager.register();
        CONSOLECOMMANDCOUNT = ConsoleCommandManager.getCommandCount();

        jda.enableIntents(EnumSet.allOf(GatewayIntent.class)); //requires privileged gateway intends (enable at discord.com/developers/applications, might need your bot to be verified)
        jda.setStatus(OnlineStatus.IDLE);
        jda.setActivity(Activity.playing("Booting up ..."));

        try {
            bot = jda.build();
        } catch (LoginException e) {
            Main.LOGGER.severe(e.getMessage());
            Main.LOGGER.severe("Could not perform login! Exiting ...");
            System.exit(1);
        }

        DataSource.getConnection(); // connect to mysql
    }

    public static void shutdown() {
        Main.bot.getPresence().setActivity(Activity.playing("Shutting down ..."));
        Main.bot.getPresence().setStatus(OnlineStatus.OFFLINE);

        LOGGER.info("Shutting down bot client ...");
        bot.shutdownNow();
        LOGGER.info("Closing MySQL Connection ...");
        try {
            DataSource.getConnection().close();
        } catch (SQLException ignored) {
        }
        LOGGER.info("Stopping Console Thread ...");
        Console.stop();
        LOGGER.info("Exiting ...");
        System.exit(0);
    }
}
