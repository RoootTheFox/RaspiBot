package net.ddns.rootrobo.RaspiBot.utils;

import net.ddns.rootrobo.RaspiBot.Main;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.jar.JarFile;

public class Utils {
    // got that somewhere on stackoverflow
    public static Color getRandomColor() {
        Random random = new Random();
        final float hue = random.nextFloat();
        final float saturation = (random.nextInt(2000) + 1000) / 10000f;
        final float luminance = 0.9f;
        return Color.getHSBColor(hue, saturation, luminance);
    }

    public static int[] getUptime(long uptimeMillis) {
        int[] res = new int[4];

        long totalSeconds = (System.currentTimeMillis() / 1000) - (uptimeMillis / 1000);
        long days = totalSeconds / 86400;
        totalSeconds %= 86400;
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

    public static InputStream getInputStreamFromBotJar(String name) {
        String filePath = null;
        try {
            filePath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if(filePath == null) return null;

        InputStream stream = null;

        try {
            JarFile jar = new JarFile(filePath);
            stream = jar.getInputStream(jar.getEntry(name));
        } catch (FileNotFoundException e) {
            filePath = filePath.replace(File.separator+"classes"+File.separator+"java"+File.separator+"main", File.separator+"resources"+File.separator+"main");

            try {
                stream = new FileInputStream(new File(filePath, name));
            } catch (FileNotFoundException fileNotFoundException) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stream;
    }

    public static int streamSize(InputStream in) {
        int chunk;
        int size = 0;
        try {
            byte[] buffer = new byte[1024];
            while((chunk = in.read(buffer)) != -1){
                size += chunk;
            }
        } catch (IOException ignored) {
            return Integer.MAX_VALUE;
        }
        return size;
    }
}
