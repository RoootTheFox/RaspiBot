package net.ddns.foxsquad.RaspiBot.commands;

import net.ddns.foxsquad.RaspiBot.Main;
import net.ddns.foxsquad.RaspiBot.stuff.Command;
import net.ddns.foxsquad.RaspiBot.utils.NetUtils;
import net.ddns.foxsquad.RaspiBot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("unused")
public class AddEmoteCommand implements Command {
    private static final int maxSize = 262144;
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        int addedAttachments = 0;
        int errors = 0;

        msg.getChannel().sendTyping().queue();
        if(args.length == 0) {
            if(msg.getAttachments().size() > 0) {
                for (Message.Attachment attachment : msg.getAttachments()) {
                    if(attachment.isImage()) {
                        try {
                            InputStream inp = attachment.retrieveInputStream().get();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = inp.read(buffer)) > -1 ) {
                                baos.write(buffer, 0, len);
                            }
                            baos.flush();
                            byte[] arr = baos.toByteArray();

                            long size = Utils.streamSize(new ByteArrayInputStream(arr));
                            if(size >= maxSize) {
                                errors++;
                                break;
                            }

                            Icon icon = Icon.from(new ByteArrayInputStream(arr), Icon.IconType.PNG);
                            msg.getGuild().createEmote(attachment.getFileName().substring(0, attachment.getFileName().lastIndexOf(".")).replace(".", ""), icon).complete();
                            addedAttachments++;
                        } catch (InterruptedException | ExecutionException | ErrorResponseException | IOException e) {
                            Main.LOGGER.info(e.getMessage());
                            errors++;
                        }
                    } else if(attachment.isVideo()) {
                        try {
                            InputStream inp = attachment.retrieveInputStream().get();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = inp.read(buffer)) > -1 ) {
                                baos.write(buffer, 0, len);
                            }
                            baos.flush();
                            byte[] arr = baos.toByteArray();

                            long size = Utils.streamSize(new ByteArrayInputStream(arr));
                            if(size >= maxSize) {
                                errors++;
                                break;
                            }

                            Icon icon = Icon.from(new ByteArrayInputStream(arr), Icon.IconType.GIF);
                            msg.getGuild().createEmote(attachment.getFileName().substring(0, attachment.getFileName().lastIndexOf(".")).replace(".", ""), icon).complete();
                            addedAttachments++;
                        } catch (InterruptedException | ExecutionException | ErrorResponseException | IOException e) {
                            Main.LOGGER.info(e.getMessage());
                            errors++;
                        }
                    }
                }
                msg.getChannel().sendMessage("Added "+addedAttachments+" emotes!\n("+errors+" error(s))").complete();
            } else {
                msg.getChannel().sendMessage("Please put at least one emote to steal!").complete();
            }
            return;
        }

        String agent = NetUtils.getRandomUserAgent();

        StringBuilder newArgs = new StringBuilder();
        for (String s : args) {
            String multipleEmotes = s.replace("><", "> <");
            newArgs.append(multipleEmotes).append(" ");
        }
        args = newArgs.toString().split(" ");

        newArgs = new StringBuilder();

        for (String s : args) {
            String emote = s.replaceAll(" ", "");
            if(!emote.equalsIgnoreCase("")) {
                newArgs.append(emote).append(" ");
            }
        }

        args = newArgs.toString().split(" ");

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

        if(args.length > 10) {
            msg.getChannel().sendMessage("Too many emotes! You can only add 10 emotes at once!").complete();
            return;
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
                            request.setHeader("User-Agent", agent);
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
                        request.setHeader("User-Agent", agent);
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
                // emote id
                HttpGet request = new HttpGet("https://cdn.discordapp.com/emojis/"+arg+".gif");
                request.setHeader("User-Agent", agent);
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
                    request2.setHeader("User-Agent", agent);
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
                CloseableHttpClient c = HttpClients.createDefault();
                HttpGet httpGet = new HttpGet(emote.getImageUrl());
                httpGet.setHeader("User-Agent", agent);
                try (CloseableHttpResponse response1 = c.execute(httpGet)) {
                    final HttpEntity entity = response1.getEntity();
                    if (entity != null) {
                        try (InputStream inputStream = entity.getContent()) {
                            long size = entity.getContentLength();
                            if(size >= maxSize) {
                                errors++;
                                break;
                            }
                            Icon icon = Icon.from(inputStream);
                            msg.getGuild().createEmote(emote.getName(), icon).complete();
                            addedEmotes++;
                        }
                    }
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        StringBuilder out = new StringBuilder();
        for (String url : urls) {
            String name = url.substring(url.indexOf("/"));
            name = "u_"+name.substring(0, name.indexOf(".")-1);

            try {
                CloseableHttpClient c = HttpClients.createDefault();
                HttpGet httpGet = new HttpGet(url);
                httpGet.setHeader("User-Agent", agent);
                try (CloseableHttpResponse response1 = c.execute(httpGet)) {
                    final HttpEntity entity = response1.getEntity();
                    if (entity != null) {
                        try (InputStream inputStream = entity.getContent()) {
                            long size = entity.getContentLength();
                            if(size >= maxSize) {
                                errors++;
                                break;
                            }
                            Icon icon = Icon.from(inputStream);
                            msg.getGuild().createEmote(name, icon).complete();
                            addedEmotes++;
                        }
                    }
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        msg.getChannel().sendMessage("Added "+addedEmotes+" emotes!\n("+errors+" error(s))").complete();
    }

    @Override
    public String getName() { return "addemote"; }

    @Override
    public String getDescription() { return "Lets you easily add emotes to your server."; }

    @Override
    public String[] getAliases() { return new String[]{"addemoji", "addemojis", "addemotes"}; }

    @Override
    public Permission getPermission() { return Permission.MANAGE_EMOTES; }
}