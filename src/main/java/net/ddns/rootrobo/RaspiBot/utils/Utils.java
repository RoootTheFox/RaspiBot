package net.ddns.rootrobo.RaspiBot.utils;

import net.ddns.rootrobo.RaspiBot.Main;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.awt.*;
import java.io.IOException;
import java.util.Random;

public class Utils {
    // got that somewhere on stackoverflow
    public static Color getRandomColor() {
        Random random = new Random();
        final float hue = random.nextFloat();
        final float saturation = (random.nextInt(2000) + 1000) / 10000f;
        final float luminance = 0.9f;
        return Color.getHSBColor(hue, saturation, luminance);
    }

    public static int[] getUptime(long uptime) {
        int[] res = new int[4];

        long totalSeconds = (System.currentTimeMillis() / 1000) - (uptime / 1000);
        long days = totalSeconds / 86400;
        long hours = totalSeconds / 3600;
        totalSeconds %= 3600;
        long minutes = totalSeconds / 60;
        int seconds = Math.round(totalSeconds % 60);

        res[0] = Math.toIntExact(Math.round(days));
        res[1] = Math.toIntExact(Math.round(hours));
        res[2] = Math.toIntExact(Math.round(minutes));
        res[3] = Math.toIntExact(Math.round(seconds));

        return res;
    }

    public static String getAvatar(String userID, String avatarID) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://cdn.discordapp.com/avatars/"+userID+"/"+avatarID+".gif");
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException ignored) {
        }
        int code = 0;
        try {
            assert response != null;
            code = response.getStatusLine().getStatusCode();
        } catch (NullPointerException ignored) {
        }
        if(code == 415) {
            return "https://cdn.discordapp.com/avatars/"+userID+"/"+avatarID+".png";
        } else {
            if(code == 404) {
                return null;
            }
            if(code == 200) {
                return "https://cdn.discordapp.com/avatars/"+userID+"/"+avatarID+".gif";
            }
            return null;
        }
    }

    public static String getGuildIcon(String guildID, String iconID) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://cdn.discordapp.com/icons/"+guildID+"/"+iconID+".gif");
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException ignored) {
        }
        int code = 0;
        try {
            assert response != null;
            code = response.getStatusLine().getStatusCode();
        } catch (NullPointerException ignored) {
        }
        if(code == 415) {
            return "https://cdn.discordapp.com/icons/"+guildID+"/"+iconID+".png";
        } else {
            if(code == 404) {
                return null;
            }
            if(code == 200) {
                return "https://cdn.discordapp.com/icons/"+guildID+"/"+iconID+".gif";
            }
            return null;
        }
    }

    public static long getPing() {
        return Main.bot.getRestPing().complete();
    }
}
