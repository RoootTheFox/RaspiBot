package net.ddns.rootrobo.RaspiBot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

// i literally copy-pasted this from https://github.com/frankred/json-config-file because I am lazy
public class Config {
    public String token;
    public String prefix;
    public String alt_prefix;
    public String mysql_username;
    public String mysql_host;
    public String mysql_password;
    public String mysql_database;
    public String topgg_token;

    public Config() {
        this.token = "TOKEN";
        this.prefix = "/";
        this.alt_prefix = "-";
        this.mysql_username = "MySQL USERNAME";
        this.mysql_host = "localhost";
        this.mysql_password = "MySQL PASSWORD";
        this.mysql_database = "MySQL DATABASE";
        this.topgg_token = "TOPGG_TOKEN";
    }

    private static Config instance;

    public static Config getInstance() {
        if (instance == null) {
            instance = fromDefaults();
        }
        return instance;
    }

    public static void load(File file) {
        instance = fromFile(file);

        if (instance == null) {
            instance = fromDefaults();
        }
    }

    public static void load(String file) {
        load(new File(file));
    }

    private static Config fromDefaults() {
        return new Config();
    }

    public void toFile(String file) {
        toFile(new File(file));
    }

    public void toFile(File file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonConfig = gson.toJson(this);
        FileWriter writer;
        try {
            writer = new FileWriter(file);
            writer.write(jsonConfig);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Config fromFile(File configFile) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(configFile)));
            return gson.fromJson(reader, Config.class);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
