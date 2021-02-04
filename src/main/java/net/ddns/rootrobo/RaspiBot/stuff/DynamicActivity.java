package net.ddns.rootrobo.RaspiBot.stuff;

import com.google.gson.*;
import net.ddns.rootrobo.RaspiBot.Main;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import java.io.*;
import java.util.*;

public class DynamicActivity {
    public static boolean shouldrun = false;

    private static Thread thread;
    private static final ArrayList<Map<String, String>> activities = new ArrayList<>();
    public static void start() {
        thread = new Thread(() -> {
            JsonArray activityArray;
            if(!new File("activity.json").exists()) {
                FileWriter writer;
                try {
                    writer = new FileWriter(new File("activity.json"));
                    ArrayList<HashMap<String, String>> defaultactivities = new ArrayList<>();
                    HashMap<String, String> defaultactivity = new HashMap<>();
                    defaultactivity.put("text", "test");
                    defaultactivity.put("type", "PLAYING");
                    defaultactivities.add(defaultactivity);

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonConfig = gson.toJson(defaultactivities);

                    writer.write(jsonConfig);
                    writer.flush();
                    writer.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("activity.json"))));
                StringBuilder builder = new StringBuilder();
                String line;
                try {
                    try {
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    } catch (NullPointerException ignored) {
                    }
                } catch (IOException ignored) {
                }

                activityArray = JsonParser.parseString(builder.toString()).getAsJsonArray();
                for (JsonElement jsonElement : activityArray) {
                    Map<String, String> a = new HashMap<>();
                    a.put("text", jsonElement.getAsJsonObject().get("text").getAsString());
                    a.put("type", jsonElement.getAsJsonObject().get("type").getAsString());
                    activities.add(a);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            while (shouldrun) {
                for (Map<String, String> activity : activities) {
                    String text = activity.get("text");
                    int channelCount = 0;
                    for (Guild guild : Main.bot.getGuilds()) {
                        channelCount = channelCount + guild.getChannels().size();
                    }
                    int guildCount = Main.bot.getGuilds().size();
                    int userCount = Main.bot.getUsers().size();

                    // check if we are on the top.gg bot server
                    Guild dblGuild = Main.bot.getGuildById(532753259740266506L);
                    if (dblGuild != null) {
                        guildCount = guildCount - 1;
                        channelCount = channelCount - dblGuild.getChannels().size();
                        userCount = userCount - dblGuild.getMemberCount();
                    }

                    text = text.replace("{version}", Main.VERSION.getFullVersionString())
                            .replace("{guilds}", String.valueOf(guildCount))
                            .replace("{channels}", String.valueOf(channelCount))
                            .replace("{users}", String.valueOf(userCount));
                    Activity.ActivityType type = Activity.ActivityType.valueOf(activity.get("type").toUpperCase().replace("PLAYING", "DEFAULT"));
                    Main.bot.getPresence().setActivity(Activity.of(type, text));

                    // also update DBL info
                    if (Main.USE_DBL) {
                        Main.dbl_api.setStats(guildCount); // set guild count
                    }

                    if (!shouldrun) break;
                    try {
                        //noinspection BusyWait
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!shouldrun) break;
                }
            }
        });
        shouldrun = true;
        thread.setName("activityThread");
        thread.start();
    }
}
