package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.mysql.DataSource;
import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.ddns.rootrobo.RaspiBot.utils.EmbedUtils;
import net.ddns.rootrobo.RaspiBot.utils.GlobalChat;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class GlobalChatCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        if(args.length < 2) {
            msg.getTextChannel().sendMessage("global create <language> | global set <language> #channel").complete();
            return;
        }

        if(args[0].equalsIgnoreCase("create")) {
            String language = GlobalChat.getLanguageByCode(args[1]);
            if(language == null) {
                msg.getChannel().sendMessage("Unkown language Code! Please use `DE` or `EN`!\nIf you want another language to be added, please DM the author of this bot.").complete();
                return;
            }

            msg.getGuild().createTextChannel("\uD83C\uDF0Dglobaler-chat").queue(
                    success -> {
                        String SQL_QUERY = "INSERT INTO globalchat_channels SET `guild` = '"+msg.getGuild().getId()+"', `channel_"+language+"` = '"+success.getId()+"' ON DUPLICATE KEY UPDATE `guild` = '"+msg.getGuild().getId()+"', `channel_"+language+"` = '"+success.getId()+"'";
                        try {
                            Connection con = DataSource.getConnection();
                            PreparedStatement pst = con.prepareStatement(SQL_QUERY);
                            pst.executeQuery();
                            Message embed = new MessageBuilder().setEmbed(new EmbedBuilder()
                                    .setTitle("Success!")
                                    .setDescription("The global chat channel (<#"+success.getId()+">) with language `"+language+"` has been successfully created!") // Message
                                    .setColor(new Color(EmbedUtils.SUCCESS_COLOR)) // Color
                                    .setTimestamp(OffsetDateTime.now()) // Timestamp
                                    .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON) // Footer

                                    .build()).build();
                            msg.getTextChannel().sendMessage(embed).queue();
                        } catch (SQLException ignored) {
                        }
                    }
            );
        }
        if(args[0].equalsIgnoreCase("set")) {
            String languageCode = args[1];

            String language = GlobalChat.getLanguageByCode(languageCode);
            if(language == null) {
                msg.getChannel().sendMessage("Unkown language Code! Please use `DE` or `EN`!\nIf you want another language to be added, please DM the author of this bot.").complete();
                return;
            }

            System.out.println(language);
            TextChannel globalChannel = msg.getMentionedChannels().get(0);
            if(globalChannel == null) {
                Message embed = new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Could not set the global Chat Channel! Please tag a valid channel\n("+Main.PREFIX+this.getName()+" set #channel") // Message
                        .setColor(new Color(EmbedUtils.ERROR_COLOR)) // Color
                        .setTimestamp(OffsetDateTime.now()) // Timestamp
                        .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON) // Footer

                        .build()).build();
                msg.getTextChannel().sendMessage(embed).queue();
                return;
            }
            String CHECK_QUERY = "SELECT * FROM globalchat_channels WHERE guild = '"+msg.getGuild().getId()+"'";
            try {
                Connection con = DataSource.getConnection();
                PreparedStatement pst = con.prepareStatement(CHECK_QUERY);
                ResultSet rs = pst.executeQuery();
                ArrayList<String> languages = GlobalChat.getLanguages();
                languages.remove(language);
                while (rs.next()) {
                    for (String lang : languages) {
                        String check = rs.getString("channel_"+lang);
                        if(check.equals(globalChannel.getId())) {
                            Message embed = new MessageBuilder().setEmbed(new EmbedBuilder()
                                    .setTitle("Error!")
                                    .setDescription("This channel is already set to another language!") // Message
                                    .setColor(new Color(EmbedUtils.ERROR_COLOR)) // Color
                                    .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON) // Footer

                                    .build()).build();
                            msg.getTextChannel().sendMessage(embed).complete();
                            return;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            String SQL_QUERY = "INSERT INTO globalchat_channels SET `guild` = '"+msg.getGuild().getId()+"', `channel_"+language+"` = '"+globalChannel.getId()+"' ON DUPLICATE KEY UPDATE `guild` = '"+msg.getGuild().getId()+"', `channel_"+language+"` = '"+globalChannel.getId()+"'";
            try {
                Connection con = DataSource.getConnection();
                PreparedStatement pst = con.prepareStatement(SQL_QUERY);
                pst.executeQuery();
                Message embed = new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setTitle("Success!")
                        .setDescription("The global chat channel (`"+language+"`) has been successfully set to <#"+globalChannel.getId()+">!") // Message
                        .setColor(new Color(EmbedUtils.SUCCESS_COLOR)) // Color
                        .setTimestamp(OffsetDateTime.now()) // Timestamp
                        .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON) // Footer

                        .build()).build();
                msg.getTextChannel().sendMessage(embed).queue();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getName() { return "global"; }

    @Override
    public String getDescription() { return "Create and remove global chat channels. For usage, run /global"; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return Permission.ADMINISTRATOR; }
}
