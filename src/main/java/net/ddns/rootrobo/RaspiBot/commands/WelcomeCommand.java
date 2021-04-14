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
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Arrays;

@SuppressWarnings("unused")
public class WelcomeCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild, TextChannel channel) {
        if(args.length == 0) {
            msg.getTextChannel().sendMessage("message <MSG> | channel <#channel>").complete();
            return;
        }

        if(args[0].equalsIgnoreCase("channel")) {
            TextChannel welcomeChannel = msg.getMentionedChannels().get(0);
            if(welcomeChannel == null) {
                Message embed = new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Could not set the Welcome Channel! Please tag a valid channel\n("+Main.PREFIX+this.getName()+" set #channel") // Message
                        .setColor(new Color(EmbedUtils.ERROR_COLOR)) // Color
                        .setTimestamp(OffsetDateTime.now()) // Timestamp
                        .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON) // Footer

                        .build()).build();
                msg.getTextChannel().sendMessage(embed).queue();
                return;
            }
            String SQL_QUERY = "INSERT INTO server_data SET `server_id` = '"+msg.getGuild().getId()+"', `welcome_channel` = '"+welcomeChannel.getId()+"' ON DUPLICATE KEY UPDATE `server_id` = '"+msg.getGuild().getId()+"', `welcome_channel` = '"+ welcomeChannel.getId()+"'";
            try {
                Connection con = DataSource.getConnection();
                PreparedStatement pst = con.prepareStatement(SQL_QUERY);
                pst.executeQuery();
                Message embed = new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setTitle("Success!")
                        .setDescription("The welcome message channel has been successfully set to <#"+welcomeChannel.getId()+">!") // Message
                        .setColor(new Color(EmbedUtils.SUCCESS_COLOR)) // Color
                        .setTimestamp(OffsetDateTime.now()) // Timestamp
                        .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON) // Footer

                        .build()).build();
                msg.getTextChannel().sendMessage(embed).queue();
            } catch (SQLException ignored) {
            }
        }

        if(args[0].equalsIgnoreCase("message")) {
            String[] a = Arrays.copyOfRange(args, 1, args.length);
            String text = String.join(" ", a);
            String SQL_QUERY = "INSERT INTO server_data SET `server_id` = '"+msg.getGuild().getId()+"', `welcome_msg` = '"+text+"' ON DUPLICATE KEY UPDATE `server_id` = '"+msg.getGuild().getId()+"', `welcome_msg` = '"+text+"'";
            try {
                Connection con = DataSource.getConnection();
                PreparedStatement pst = con.prepareStatement(SQL_QUERY);
                pst.executeQuery();
                Message embed = new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setTitle("Success!")
                        .setDescription("The welcome message has been successfully set to `"+text+"`!") // Message
                        .setColor(new Color(0x00FF00)) // Color
                        .setTimestamp(OffsetDateTime.now()) // Timestamp
                        .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON) // Footer

                        .build()).build();
                msg.getTextChannel().sendMessage(embed).queue();
            } catch (SQLException ignored) {
            }
        }
    }

    @Override
    public String getName() { return "welcome"; }

    @Override
    public String getDescription() { return "welcome set channel | welcome message MESSAGE"; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return Permission.ADMINISTRATOR; }
}
