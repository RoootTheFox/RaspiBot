package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class StealEmoteCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        if(args.length == 0) {
            msg.getChannel().sendMessage("Please put at least one emote to download!").complete();
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

        for (Emote emote : e) {
           urls.add(emote.getImageUrl());
        }

        StringBuilder out = new StringBuilder();
        for (String url : urls) {
            out.append(url).append("\n");
        }
        msg.getChannel().sendMessage(out.toString()).complete();
    }

    @Override
    public String getName() { return "stealemote"; }

    @Override
    public String getDescription() { return null; }

    @Override
    public String[] getAliases() { return new String[]{"stealemoji", "stealemotes", "downloademote", "downloademoji"}; }

    @Override
    public Permission getPermission() { return null; }
}