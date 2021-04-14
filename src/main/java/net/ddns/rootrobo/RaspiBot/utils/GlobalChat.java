package net.ddns.rootrobo.RaspiBot.utils;

import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.mysql.DataSource;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;

public class GlobalChat {
    public static void send(Message msg, String text, String language, String imgURL, boolean isMod, boolean isAdmin, boolean isOwner, boolean isConsole) {
        if (!isConsole) {
            Main.LOGGER.info("[GlobalChat/"+language.toUpperCase()+"] [" + msg.getGuild().getName() + " @" + msg.getAuthor().getName() + "]: " + text);
        } else {
            Main.LOGGER.info("[GlobalChat/"+language.toUpperCase()+"] [CONSOLE]: " + text);
        }

        for (Guild guild : Main.bot.getGuilds()) {
            String SQL_QUERY = "SELECT * FROM globalchat_channels WHERE guild = '" + guild.getId() + "'";
            try (Connection con = DataSource.getConnection();
                 PreparedStatement pst = con.prepareStatement(SQL_QUERY);
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String globalChatID = rs.getString("channel_"+language);
                    if (!(globalChatID == null || globalChatID.equals(""))) {
                        TextChannel t = guild.getTextChannelById(globalChatID);
                        if (t == null) continue;

                        String AvatarURL;
                        if (!isConsole) {
                            AvatarURL = msg.getAuthor().getAvatarUrl();
                            if (AvatarURL == null) {
                                AvatarURL = msg.getAuthor().getDefaultAvatarUrl();
                            } else {
                                AvatarURL = AvatarURL+"?size=512"; // better quality
                            }
                        } else {
                            AvatarURL = Main.bot.getSelfUser().getAvatarUrl();
                            if (AvatarURL == null) {
                                AvatarURL = Main.bot.getSelfUser().getDefaultAvatarUrl();
                            }
                        }

                        int color = 7506394;
                        String author;
                        if (!isConsole) {
                            author = msg.getAuthor().getAsTag();
                        } else {
                            author = "CONSOLE";
                        }
                        if (isMod) {
                            author = "[Mod] " + msg.getAuthor().getAsTag();
                            color = 65535;
                        }
                        if (isAdmin) {
                            author = "[Admin] " + msg.getAuthor().getAsTag();
                            color = 65280;
                        }
                        if (isOwner) {
                            author = "[Owner] " + msg.getAuthor().getAsTag();
                            color = 65280;
                        }
                        if (isConsole) {
                            color = 16711680;
                        }
                        EmbedBuilder embed = new EmbedBuilder()
                                .setDescription(text) // Message
                                .setColor(new Color(color)) // Color
                                .setTimestamp(OffsetDateTime.now()) // Timestamp
                                .setThumbnail(AvatarURL) // pfp of author
                                .setAuthor(author, null, null); // Name of author

                        if (!isConsole) {
                            embed.setFooter(msg.getGuild().getName(), msg.getGuild().getIconUrl()); // Guild Name + Icon
                        } else {
                            embed.setFooter("CONSOLE", AvatarURL);
                        }
                        if (imgURL != null) {
                            embed.setImage(imgURL);
                        }

                        Message send = new MessageBuilder().setEmbed(embed.build()).build();
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        t.sendMessage(send).queue();
                                    }
                                },
                                0
                        );
                    }
                }
            } catch (SQLException ignored) {
            }
        }
    }

    public static void langSend(String language, MessageReceivedEvent event, Connection con) {
        String strippedMSG = event.getMessage().getContentStripped()
                .replace("\\", "")
                .replace("\u200B", "") // remove zero width spaces
                .replace(" ", "")
                .replace(",", "."); // convert commas to dots
        String finalMSG = event.getMessage().getContentRaw()
                .replace("\u200B", "")
                .replace("@everyone", "@\u200Beveryone")
                .replace("@here", "@\u200Bhere");

        try {
            event.getMessage().delete().queue();
        } catch (ErrorResponseException ignored) {
        }

        //update or create entry
        String SQL_QUERY2 = "INSERT INTO globalchat_userdata SET `userid` = '" + event.getAuthor().getId() + "', `username` = \"" + event.getAuthor().getAsTag() + "\" ON DUPLICATE KEY UPDATE `userid` = '" + event.getAuthor().getId() + "', `username` = \"" + event.getAuthor().getAsTag() + "\"";
        try {
            PreparedStatement pst2 = con.prepareStatement(SQL_QUERY2);
            pst2.executeQuery();
        } catch (SQLException ignored) {
        }

        boolean isBanned = false;
        boolean isOwner = false;
        boolean isAdmin = false;
        boolean isMod = false;

        // check if user is admin, mod or banned
        String SQL_QUERY3 = "SELECT * FROM globalchat_userdata WHERE userid = '" + event.getAuthor().getId() + "'";
        try {
            PreparedStatement pst3 = con.prepareStatement(SQL_QUERY3);
            ResultSet rs3 = pst3.executeQuery();
            while (rs3.next()) {
                isBanned = rs3.getBoolean("isBanned");
                isMod = rs3.getBoolean("isMod");
                isAdmin = rs3.getBoolean("isAdmin");
                isOwner = rs3.getBoolean("isOwner");
            }
        } catch (SQLException ignored) {
        }
        if (isBanned && !(isAdmin)) {
            event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + ", you are banned!").complete();
            return;
        }

        if (MessageUtils.containsLink(strippedMSG) && !(isAdmin || isMod)) {
            event.getTextChannel().sendMessage(event.getAuthor().getAsTag() + ", don't share links here!").complete();
            return;
        }
        String imgURL = null;
        if (event.getMessage().getAttachments().size() > 0) {
            if (event.getMessage().getAttachments().get(0).isImage()) {
                imgURL = event.getMessage().getAttachments().get(0).getProxyUrl();
            }
        }

        GlobalChat.send(event.getMessage(), finalMSG, language, imgURL, isMod, isAdmin, isOwner, false);
    }

    public static String getLanguageByCode(String lang_code) {
        switch (lang_code.toLowerCase()) {
            default: {
                return null;
            }
            case "en": {
                return "english";
            }
            case "de": {
                return "german";
            }
        }
    }

    public static ArrayList<String> getLanguages() {
        ArrayList<String> languages = new ArrayList<>();
        languages.add("german");
        languages.add("english");
        return languages;
    }
}