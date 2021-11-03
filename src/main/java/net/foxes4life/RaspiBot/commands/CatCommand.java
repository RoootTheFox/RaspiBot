package net.foxes4life.RaspiBot.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.foxes4life.RaspiBot.stuff.Command;
import net.foxes4life.RaspiBot.utils.EmbedUtils;
import net.foxes4life.RaspiBot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
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

@SuppressWarnings("unused")
public class CatCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://meme-api.herokuapp.com/gimme/cats");
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
        JsonElement bodyJson = JsonParser.parseString(body);
        if(!bodyJson.isJsonObject()) return;
        JsonObject jsonObject = bodyJson.getAsJsonObject();

        String postLink = jsonObject.get("postLink").getAsString();
        String postTitle = jsonObject.get("title").getAsString();
        String postImageURL = jsonObject.get("url").getAsString();

        Message message = new MessageBuilder().setEmbed(new EmbedBuilder()
                .setAuthor(postTitle, postLink)
                .setImage(postImageURL)
                .setColor(Utils.getRandomColor())
                .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)
                .build()).build();
        msg.getChannel().sendMessage(message).complete();
    }

    @Override
    public String getName() { return "cat"; }

    @Override
    public String getDescription() { return "Sends some cat pics"; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return null; }


}
