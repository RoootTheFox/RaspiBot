package net.ddns.rootrobo.RaspiBot.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@SuppressWarnings("unused")
public class MCProfileCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        if(args.length == 0) {
            msg.getChannel().sendMessage("Please specify a username!").complete();
            return;
        }
        String username = args[0];

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://api.mojang.com/users/profiles/minecraft/"+username);
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException ignored) {
        }
        BufferedReader rd = null;
        StringBuilder result;
        try {
            assert response != null;
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        result = new StringBuilder();
        String line;
        try {
            try {
                while (true) {
                    assert rd != null;
                    if ((line = rd.readLine()) == null) break;
                    result.append(line);
                }
            } catch (NullPointerException ignored) {
            }
        } catch (IOException ignored) {
        }
        String body = result.toString();
        JsonElement jsonBody = JsonParser.parseString(body);
        if(!jsonBody.isJsonObject()) return;
        JsonObject json = jsonBody.getAsJsonObject();

        String UUID = json.get("id").getAsString();

        HttpGet request2 = new HttpGet("https://sessionserver.mojang.com/session/minecraft/profile/"+UUID);
        HttpResponse response2 = null;
        try {
            response2 = client.execute(request2);
        } catch (IOException ignored) {
        }
        BufferedReader rd2 = null;
        StringBuilder result2;
        try {
            assert response2 != null;
            rd2 = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        result2 = new StringBuilder();
        String line2;
        try {
            try {
                while (true) {
                    assert rd2 != null;
                    if ((line2 = rd2.readLine()) == null) break;
                    result2.append(line2);
                }
            } catch (NullPointerException ignored) {
            }
        } catch (IOException ignored) {
        }
        String body2 = result2.toString();

        JsonElement jsonBody2 = JsonParser.parseString(body2);
        if(!jsonBody2.isJsonObject()) return;
        JsonObject json2 = jsonBody2.getAsJsonObject();

        String SkinBase64 = json2.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
        String skin = new String(Base64.getDecoder().decode(SkinBase64), StandardCharsets.UTF_8);

        JsonElement jsonElement = JsonParser.parseString(skin);
        if(!jsonElement.isJsonObject()) return;
        JsonObject json3 = jsonElement.getAsJsonObject();

        String skinURL = json3.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();
        msg.getChannel().sendMessage(skinURL).complete();
    }

    @Override
    public String getName() { return "mcprofile"; }

    @Override
    public String getDescription() { return "WIP"; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return null; }
}
