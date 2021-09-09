package net.ddns.foxsquad.RaspiBot.utils;

import net.ddns.foxsquad.RaspiBot.Main;
import net.ddns.foxsquad.RaspiBot.mysql.DataSource;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GlobalChat {
    private static final String MOD_PREFIX = "[Mod] ";
    private static final int MOD_COLOR = 65535;

    private static final String ADMIN_PREFIX = "[Admin] ";
    private static final int ADMIN_COLOR = 65280;

    private static final String OWNER_PREFIX = "[Owner] ";
    private static final int OWNER_COLOR = 65280;

    private static final int DEFAULT_COLOR = 7506394;
    private static final int CONSOLE_COLOR = 16711680;

    public static void send(Message msg, String text, String language, String imgURL, boolean isMod, boolean isAdmin, boolean isOwner, boolean isConsole) {
        if(msg == null) {
            isConsole = true;
        }

        if(msg != null && msg.getReferencedMessage() != null) {
            Message ref = msg.getReferencedMessage();
            StringBuilder ref_text = new StringBuilder(ref.getContentRaw());
            String refauthor = null;
            if(ref.getEmbeds().size() > 0) {
                for (MessageEmbed embed : ref.getEmbeds()) {
                    ref_text.append("\n").append(embed.getDescription());
                    if(embed.getAuthor() != null) {
                        int ref_color = embed.getColorRaw();
                        refauthor = embed.getAuthor().getName();
                        assert refauthor != null;

                        if(ref_color == MOD_COLOR && refauthor.startsWith(MOD_PREFIX)) {
                            refauthor = "@"+refauthor.substring(MOD_PREFIX.length());
                        }
                        if(ref_color == ADMIN_COLOR && refauthor.startsWith(ADMIN_PREFIX)) {
                            refauthor = "@"+refauthor.substring(ADMIN_PREFIX.length());
                        }
                        if(ref_color == OWNER_COLOR && refauthor.startsWith(OWNER_PREFIX)) {
                            refauthor = "@"+refauthor.substring(OWNER_PREFIX.length());
                        }
                        if(ref_color == DEFAULT_COLOR) {
                            refauthor = "@"+refauthor;
                        }

                    }
                }
            }

            if(ref_text.toString().startsWith("\n")) {
                ref_text = new StringBuilder(ref_text.substring(1));
            }

            if (refauthor != null && refauthor.equals("")) {
                refauthor = ref.getAuthor().getName();
            }

            String[] ref_text_split = ref_text.toString().split("\n");
            for (int i = 0; i < ref_text_split.length; i++) {
                ref_text_split[i] = "> " + ref_text_split[i] + "\n";
            }
            ref_text = new StringBuilder(String.join("\n", ref_text_split) + refauthor);
            text = ref_text + "\n" + text;
        }

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

                        int color = DEFAULT_COLOR;
                        String author;
                        if (!isConsole) {
                            author = msg.getAuthor().getAsTag();
                            if (isOwner) {
                                author = OWNER_PREFIX + author;
                                color = OWNER_COLOR;
                            } else if (isAdmin) {
                                author = ADMIN_PREFIX + author;
                                color = ADMIN_COLOR;
                            } else if (isMod) {
                                author = MOD_PREFIX + author;
                                color = MOD_COLOR;
                            }
                        } else {
                            author = "CONSOLE";
                            color = CONSOLE_COLOR;
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
        StringBuilder finalMSG = new StringBuilder(event.getMessage().getContentRaw()
                .replace("\u200B", "")
                .replace("@everyone", "@\u200Beveryone")
                .replace("@here", "@\u200Bhere"));

        if(!(event.getMessage().getType() == MessageType.DEFAULT ||
                event.getMessage().getType() == MessageType.APPLICATION_COMMAND ||
                event.getMessage().getType() == MessageType.INLINE_REPLY)) {
            System.out.println("invalid message, type: "+event.getMessage().getType().name());
            return;
        }


        try {
            event.getMessage().delete().queueAfter(Utils.getPing()+100, TimeUnit.MILLISECONDS);
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

        if (MessageUtils.containsLink(strippedMSG) && !(isAdmin || isMod ||
                ( event.getAuthor().getDiscriminator().equals("0000") && event.getGuild().getOwnerId().equals(Main.DEVELOPER_ID)))) {
            event.getTextChannel().sendMessage(event.getAuthor().getAsTag() + ", don't share links here!").complete();
            return;
        }

        String imgURL = null;
        if (event.getMessage().getAttachments().size() > 0) {
            if (event.getMessage().getAttachments().get(0).isImage()) {
                imgURL = event.getMessage().getAttachments().get(0).getProxyUrl();
            }
        }

        if(event.getMessage().getEmbeds().size() > 0) {
            finalMSG.append("\n");
            for (MessageEmbed embed : event.getMessage().getEmbeds()) {
                finalMSG.append("```");
                if(embed.getImage() != null && imgURL == null) {
                    imgURL = embed.getImage().getUrl();

                    if(embed.getAuthor() != null) {
                        if(embed.getAuthor().getName() != null && !embed.getAuthor().getName().equals("")) {
                            finalMSG.append("\n**").append(embed.getAuthor().getName()).append("**");
                        }
                    }
                    if(embed.getTitle() != null) {
                        if(!embed.getTitle().equals("")) {
                            finalMSG.append("\n**").append(embed.getTitle()).append("**");
                        }
                    }
                }
                if(embed.getDescription() != null && !embed.getDescription().equals("")) {
                    finalMSG.append(embed.getDescription()).append("\n");
                }

                if(embed.getFields().size() > 0) {
                    for (MessageEmbed.Field field : embed.getFields()) {
                        if(field.getName() != null && field.getValue() != null && !(field.getValue().equals("") || field.getValue().equals(""))) {
                            finalMSG.append("**__").append(field.getName()).append("__**").append("\n");
                            finalMSG.append(field.getValue()).append("\n");
                        }
                    }
                }
                finalMSG.append("```");
            }

        }

        String finalMsg_str = MessageUtils.replaceMentions(finalMSG.toString());
        finalMsg_str = MessageUtils.replaceChannelMentions(finalMsg_str);

        if(finalMsg_str.endsWith("\n")) {
            finalMsg_str = finalMsg_str.substring(0, finalMsg_str.length()-1);
        }

        GlobalChat.send(event.getMessage(), finalMsg_str, language, imgURL, isMod, isAdmin, isOwner, false);
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