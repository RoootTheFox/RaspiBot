package net.ddns.foxsquad.RaspiBot.commands;

import net.ddns.foxsquad.RaspiBot.stuff.Command;
import net.ddns.foxsquad.RaspiBot.utils.Utils;
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
public class MCSkinCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        if(args.length == 0) {
            msg.getChannel().sendMessage("Please specify a username!").complete();
            return;
        }

        String username = args[0];
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://namemc.com/profile/"+username);
        request.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:90.0) Gecko/20100101 Firefox/90.0"); // random user agent lol

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

        // scrape out the namemc skin id
        String skinID = body.substring(body.indexOf("a href=\"/skin/")+14);
        skinID = skinID.substring(0, skinID.indexOf(">")-1);

        // fix username capitalisation
        username = body.substring(body.indexOf("<title>")+7);
        username = username.substring(0, username.indexOf(" |"));

        if(skinID.equals("")) {
            // player doesn't exist (or error, fuck you namemc :v)
            msg.getChannel().sendMessage("This player doesn't exist or does not have a skin!\n" +
                    "(it may also be possible that NameMC is blocking our requests)").complete();
            return;
        }

        String skinURL = "https://render.namemc.com/skin/3d/body.png?skin="+skinID+"&model=classic&theta=40&phi=25&time=286&width=600&height=800.png";

        Message message = new MessageBuilder().setEmbed(new EmbedBuilder()
                .setTitle(username+"'s Skin")
                .setColor(Utils.getRandomColor())
                .setImage(skinURL)
        .build()).build();

        msg.getChannel().sendMessage(message).complete();
    }

    @Override
    public String getName() { return "mcskin"; }

    @Override
    public String getDescription() { return "Shows the Skin of a Minecraft player."; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return null; }
}
