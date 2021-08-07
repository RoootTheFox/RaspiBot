package net.ddns.rootrobo.RaspiBot.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.ddns.rootrobo.RaspiBot.utils.EmbedUtils;
import net.ddns.rootrobo.RaspiBot.utils.Utils;
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
public class MemeCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://meme-api.herokuapp.com/gimme");
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
        JsonElement memeJsonElement = JsonParser.parseString(body);
        if(!memeJsonElement.isJsonObject()) return;
        JsonObject memeJson = memeJsonElement.getAsJsonObject();

        String memeLink = memeJson.get("postLink").getAsString();
        String memeTitle = memeJson.get("title").getAsString();
        String memeImgURL = memeJson.get("url").getAsString();

        Message message = new MessageBuilder().setEmbed(new EmbedBuilder()
                .setAuthor(memeTitle, memeLink)
                .setImage(memeImgURL)
                .setColor(Utils.getRandomColor())
                .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)
        .build()).build();
        msg.getChannel().sendMessage(message).complete();
    }

    @Override
    public String getName() { return "meme"; }

    @Override
    public String getDescription() { return "Sends some memes."; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return null; }
}
