package net.foxes4life.RaspiBot.commands;

import net.foxes4life.RaspiBot.Main;
import net.foxes4life.RaspiBot.mysql.DataSource;
import net.foxes4life.RaspiBot.stuff.Command;
import net.foxes4life.RaspiBot.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class GlobalChatBanCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        boolean isBanned = false;
        boolean isAdmin = false;
        boolean isMod = false;
        String authorAvatarURL = msg.getAuthor().getAvatarUrl();
        if(authorAvatarURL == null) authorAvatarURL = msg.getAuthor().getDefaultAvatarUrl();

        String SQL_QUERY = "SELECT * FROM globalchat_userdata WHERE userid = '"+msg.getAuthor().getId()+"'";
        try {
            PreparedStatement pst = DataSource.getConnection().prepareStatement(SQL_QUERY);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                isMod = rs.getBoolean("isMod");
                isAdmin = rs.getBoolean("isAdmin");
            }
        } catch (SQLException ignored) {
        }

        if(!(isMod || isAdmin)) {
            Message embed = new MessageBuilder().setEmbeds(new EmbedBuilder()
                    .setTitle("No Permissions")
                    .setDescription("You need to be a Global Chat Moderator or higher to use this command!")
                    .setThumbnail(authorAvatarURL)
                    .setColor(new Color(EmbedUtils.ERROR_COLOR))
                    .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)
                    .build()).build();
            msg.getTextChannel().sendMessage(embed).submit();
            return;
        }

        User target = null;
        try {
            target = msg.getMentionedUsers().get(0);
        } catch (IndexOutOfBoundsException ignored) {
        }
        String args0;
        try {
            args0 = args[0];
        } catch (IndexOutOfBoundsException e) {
            msg.getChannel().sendMessage(e.toString()).complete();
            return;
        }


        if(target == null) {
            if(args0.startsWith("<")) {
                try {
                    target = Main.bot.getUserById(args0.replace("<@!", "").replace(">", "").replace("<@", ""));
                } catch (NumberFormatException ignored) {
                }
            } else {
                try {
                    target = Main.bot.getUserById(args0);
                } catch (NumberFormatException ignored) {
                }
            }
            if(target == null) {
                target = Main.bot.getUserByTag(args0);
            }
            if(target == null) {
                Message embed = new MessageBuilder().setEmbeds(new EmbedBuilder()
                        .setTitle("Error")
                        .setDescription("Could not find the User you are looking for.")
                        .setThumbnail(authorAvatarURL)
                        .setColor(EmbedUtils.ERROR_COLOR)
                        .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)

                        .build()).build();
                msg.getTextChannel().sendMessage(embed).submit();
                return;
            }
        }

        String AvatarURL = target.getAvatarUrl();
        if(AvatarURL == null) AvatarURL = target.getDefaultAvatarUrl();

        String SQL_QUERY2 = "INSERT INTO globalchat_userdata SET `userid` = '"+target.getId()+"', `username` = \""+target.getAsTag()+"\", isBanned = true ON DUPLICATE KEY UPDATE `userid` = '"+target.getId()+"', `username` = \""+target.getAsTag()+"\", isBanned = true";
        try {
            PreparedStatement pst = DataSource.getConnection().prepareStatement(SQL_QUERY2);
            pst.executeQuery();

            Message embed = new MessageBuilder().setEmbeds(new EmbedBuilder()
                    .setTitle("Success")
                    .setDescription(target.getAsTag() +" has been banned from the Global Chat.")
                    .setThumbnail(AvatarURL)
                    .setColor(new Color(EmbedUtils.SUCCESS_COLOR))
                    .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)
                    .build()).build();
            msg.getTextChannel().sendMessage(embed).submit();
        } catch (SQLException ignored) {
        }
    }

    @Override
    public String getName() { return "gcban"; }

    @Override
    public String getDescription() { return "Lets Global Chat Mods ban people from using the Global Chat."; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return null; }
}
