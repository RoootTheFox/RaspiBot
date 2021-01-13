package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class AddEmoteCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild, TextChannel channel) {
        if(args.length == 0) {
            channel.sendMessage("Please put at least one emote to download!").complete();
            return;
        }
        ArrayList<String> urls = new ArrayList<>();
        List<Emote> e = msg.getEmotes();
        HttpClient client = HttpClientBuilder.create().build();

        ArrayList<String> emoteIdentifiers = new ArrayList<>();
        for (Emote emote : e) {
            String identifier;
            if(emote.isAnimated()) {
                identifier = "<a:"+emote.getName()+":"+emote.getId()+">";
            } else {
                identifier = "<:"+emote.getName()+":"+emote.getId()+">";
            }
            emoteIdentifiers.add(identifier);
        }

        for (String arg : args) {
            if(arg.startsWith("<") && arg.endsWith(">")) {
                if(!emoteIdentifiers.contains(arg)) {
                    if(arg.startsWith("<a:")) {
                        Main.LOGGER.info("Emoji animated");
                        arg = arg.substring(3);
                        if(arg.indexOf(":") != 0) {
                            arg = arg.substring(arg.indexOf(":")+1).replace(">", "");
                            HttpGet request = new HttpGet("https://cdn.discordapp.com/emojis/"+arg+".gif");
                            HttpResponse response;
                            try {
                                response = client.execute(request);
                            } catch (IOException ignored) {
                                continue;
                            }
                            if(response == null) continue;
                            if(response.getStatusLine().getStatusCode() == 200) {
                                urls.add("https://cdn.discordapp.com/emojis/"+arg+".gif");
                            }
                            continue;
                        }
                    }
                    if (arg.startsWith("<:")) {
                        Main.LOGGER.info("emoji not animated");
                        arg = arg.substring(2);
                        HttpGet request = new HttpGet("https://cdn.discordapp.com/emojis/"+arg+".png");
                        HttpResponse response;
                        try {
                            response = client.execute(request);
                        } catch (IOException ignored) {
                            continue;
                        }
                        if(response == null) continue;
                        if(response.getStatusLine().getStatusCode() == 200) {
                            urls.add("https://cdn.discordapp.com/emojis/"+arg+".png");
                        }
                    }
                }
            } else {
                Main.LOGGER.info("emoji ID");
                HttpGet request = new HttpGet("https://cdn.discordapp.com/emojis/"+arg+".gif");
                HttpResponse response;
                try {
                    response = client.execute(request);
                } catch (IOException ignored) {
                    continue;
                }
                if(response == null) continue;
                if(response.getStatusLine().getStatusCode() == 200) {
                    urls.add("https://cdn.discordapp.com/emojis/"+arg+".gif");
                } else {
                    HttpGet request2 = new HttpGet("https://cdn.discordapp.com/emojis/"+arg+".png");
                    HttpResponse response2;
                    try {
                        response2 = client.execute(request2);
                    } catch (IOException ignored) {
                        continue;
                    }
                    if(response2 == null) continue;
                    if(response2.getStatusLine().getStatusCode() == 200) {
                        urls.add("https://cdn.discordapp.com/emojis/"+arg+".png");
                    }
                }
            }
        }

        int addedEmotes = 0;
        for (Emote emote : e) {
            try {
                InputStream input = new URL(emote.getImageUrl()).openStream();
                Icon icon = Icon.from(input);
                msg.getGuild().createEmote(emote.getName(), icon).complete();
                addedEmotes++;
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        StringBuilder out = new StringBuilder();
        for (String url : urls) {
            String name = url.substring(url.indexOf("/"));
            name = "u_"+name.substring(0, name.indexOf(".")-1);

            try {
                InputStream input = new URL(url).openStream();
                Icon icon = Icon.from(input);
                msg.getGuild().createEmote(name, icon).complete();
                addedEmotes++;
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        channel.sendMessage("Added "+addedEmotes+" emotes!").complete();
    }

    @Override
    public String getName() { return "addemote"; }

    @Override
    public String getDescription() { return null; }

    @Override
    public String[] getAliases() { return new String[]{"addemoji", "addemojis", "addemotes"}; }

    @Override
    public Permission getPermission() { return Permission.MANAGE_EMOTES; }
}