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

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@SuppressWarnings("unused")
public class GlobalChatClearCommand implements Command {
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
            Message embed = new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setTitle("No Permissions")
                    .setDescription("You need to be a Global Chat Moderator or higher to use this command!")
                    .setThumbnail(authorAvatarURL)
                    .setColor(new Color(EmbedUtils.ERROR_COLOR))
                    .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)
                    .build()).build();
            msg.getTextChannel().sendMessage(embed).submit();
            return;
        }

        String language = null;
        String SQL_QUERY2 = "SELECT * FROM globalchat_channels WHERE guild = '"+msg.getGuild().getId()+"'";
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY2);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                String globalchat_english = rs.getString("channel_english");
                String globalchat_german = rs.getString("channel_german");
                if(globalchat_english == null && globalchat_german == null) {
                    return;
                } else {
                    if(msg.getChannel().getId().equals(globalchat_english)) {
                        language = "english";
                        break;
                    } else if (msg.getChannel().getId().equals(globalchat_german)) {
                        language = "german";
                        break;
                    } else {
                        return;
                    }
                }
            }
        } catch (SQLException ignored) {
        }

        for (Guild g : Main.bot.getGuilds()) {
            String SQL_QUERY3 = "SELECT channel_"+language+" FROM globalchat_channels WHERE guild = '"+g.getId()+"'";
            try (Connection con = DataSource.getConnection();
                 PreparedStatement pst = con.prepareStatement(SQL_QUERY3);
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String globalChatID = rs.getString("channel_"+language);
                    if(!(globalChatID == null)) {
                        TextChannel t = g.getTextChannelById(globalChatID);
                        if (t == null) continue;

                        OffsetDateTime maxDate = OffsetDateTime.now().minus(2, ChronoUnit.WEEKS);
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        List<Message> e = t.getHistory().retrievePast(100).complete();
                                        e.removeIf(message -> message.getTimeCreated().isBefore(maxDate));
                                        t.purgeMessages(e);
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

    @Override
    public String getName() { return "gcclear"; }

    @Override
    public String getDescription() { return "clear the global chat"; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return null; }
}
