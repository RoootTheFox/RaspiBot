package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.mysql.DataSource;
import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.ddns.rootrobo.RaspiBot.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class GlobalChatModCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild, TextChannel channel) {
        boolean isBanned = false;
        boolean isOwner = false;
        boolean isAdmin = false;
        boolean isMod = false;
        String authorAvatarURL = msg.getAuthor().getAvatarUrl();
        if(authorAvatarURL == null) authorAvatarURL = msg.getAuthor().getDefaultAvatarUrl();

        String SQL_QUERY = "SELECT * FROM globalchat_userdata WHERE userid = '"+msg.getAuthor().getId()+"'";
        try {
            PreparedStatement pst = DataSource.getConnection().prepareStatement(SQL_QUERY);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                isOwner = rs.getBoolean("isOwner");
            }
        } catch (SQLException ignored) {
        }

        if(!(isOwner)) {
            Message embed = new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setTitle("No Permissions")
                    .setDescription("You need to be a GlobalChat Owner to run this command!")
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

        if(target == null) {
            if(args[0].startsWith("<")) {
                try {
                    target = Main.bot.getUserById(args[0].replace("<@!", "").replace(">", "").replace("<@", ""));
                } catch (NumberFormatException ignored) {
                }
            } else {
                try {
                    target = Main.bot.getUserById(args[0]);
                } catch (NumberFormatException ignored) {
                }
            }
            if(target == null) {
                target = Main.bot.getUserByTag(args[0]);
            }
            if(target == null) {
                Message embed = new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setTitle("Error")
                        .setDescription("Could not find the User you are looking for.")
                        .setThumbnail(authorAvatarURL)
                        .setColor(new Color(0xEB0000))
                        .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)

                        .build()).build();
                msg.getTextChannel().sendMessage(embed).submit();
                return;
            }
        }

        String AvatarURL = target.getAvatarUrl();
        if(AvatarURL == null) AvatarURL = target.getDefaultAvatarUrl();

        String SQL_QUERY2 = "INSERT INTO globalchat_userdata SET `userid` = '"+target.getId()+"', `username` = \""+target.getAsTag()+"\", isMod = true ON DUPLICATE KEY UPDATE `userid` = '"+target.getId()+"', `username` = \""+target.getAsTag()+"\", isMod = true";
        try {
            PreparedStatement pst = DataSource.getConnection().prepareStatement(SQL_QUERY2);
            pst.executeQuery();

            Message embed = new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setTitle("Success")
                    .setDescription(target.getAsTag() +" is now a GlobalChat Moderator.")
                    .setThumbnail(AvatarURL)
                    .setColor(new Color(EmbedUtils.SUCCESS_COLOR))
                    .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)
                    .build()).build();
            msg.getTextChannel().sendMessage(embed).submit();
        } catch (SQLException ignored) {
        }
    }

    @Override
    public String getName() { return "gcmod"; }

    @Override
    public String getDescription() { return null; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return null; }
}
