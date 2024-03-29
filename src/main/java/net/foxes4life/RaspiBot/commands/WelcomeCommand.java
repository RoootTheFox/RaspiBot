package net.foxes4life.RaspiBot.commands;

import net.dv8tion.jda.api.entities.Role;
import net.foxes4life.RaspiBot.Main;
import net.foxes4life.RaspiBot.mysql.DataSource;
import net.foxes4life.RaspiBot.stuff.Command;
import net.foxes4life.RaspiBot.utils.EmbedUtils;
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
public class WelcomeCommand implements Command { // looking at this class... yeah i think i should just recode the bot
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        String help = "message <MSG> | channel <#channel> | role <@role>";
        if(args.length == 0) {
            msg.getTextChannel().sendMessage(help).complete();
            return;
        }

        if(args[0].equalsIgnoreCase("channel")) {
            TextChannel welcomeChannel = msg.getMentionedChannels().get(0);
            if(welcomeChannel == null) {
                Message embed = new MessageBuilder().setEmbeds(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Could not set the Welcome Channel! Please tag a valid channel\n("+ Main.PREFIX+this.getName()+" set #channel") // Message
                        .setColor(new Color(EmbedUtils.ERROR_COLOR))
                        .setTimestamp(OffsetDateTime.now())
                        .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)

                        .build()).build();
                msg.getTextChannel().sendMessage(embed).queue();
                return;
            }

            String SQL_QUERY = "INSERT INTO server_data SET `server_id` = '"+msg.getGuild().getId()+"', `welcome_channel` = '"+welcomeChannel.getId()+"' ON DUPLICATE KEY UPDATE `server_id` = '"+msg.getGuild().getId()+"', `welcome_channel` = '"+ welcomeChannel.getId()+"'";
            try {
                Connection con = DataSource.getConnection();
                PreparedStatement pst = con.prepareStatement(SQL_QUERY);
                pst.executeQuery();
                Message embed = new MessageBuilder().setEmbeds(new EmbedBuilder()
                        .setTitle("Success!")
                        .setDescription("The welcome message channel has been successfully set to <#"+welcomeChannel.getId()+">!") // Message
                        .setColor(new Color(EmbedUtils.SUCCESS_COLOR))
                        .setTimestamp(OffsetDateTime.now())
                        .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)

                        .build()).build();
                msg.getTextChannel().sendMessage(embed).queue();
            } catch (SQLException ignored) {
            }
            return;
        }

        if(args[0].equalsIgnoreCase("message")) {
            String[] a = Arrays.copyOfRange(args, 1, args.length);
            String text = String.join(" ", a);
            String SQL_QUERY = "INSERT INTO server_data SET `server_id` = '"+msg.getGuild().getId()+"', `welcome_msg` = '"+text+"' ON DUPLICATE KEY UPDATE `server_id` = '"+msg.getGuild().getId()+"', `welcome_msg` = '"+text+"'";
            try {
                Connection con = DataSource.getConnection();
                PreparedStatement pst = con.prepareStatement(SQL_QUERY);
                pst.executeQuery();
                Message embed = new MessageBuilder().setEmbeds(new EmbedBuilder()
                        .setTitle("Success!")
                        .setDescription("The welcome message has been successfully set to `"+text+"`!")
                        .setColor(new Color(0x00FF00))
                        .setTimestamp(OffsetDateTime.now())
                        .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)

                        .build()).build();
                msg.getTextChannel().sendMessage(embed).queue();
            } catch (SQLException ignored) {
            }
            return;
        }

        if(args[0].equalsIgnoreCase("role")) {
            String[] a = Arrays.copyOfRange(args, 1, args.length);
            String text = String.join(" ", a);
            Role role = null;
            if(!msg.getMentionedRoles().isEmpty()) role = msg.getMentionedRoles().get(0);
            if(role == null) {
                role = guild.getRoleById(text);

                if(role == null) {
                    Message embed = new MessageBuilder().setEmbeds(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("Could not set the welcome role! Please tag a valid role\n("+ Main.PREFIX+this.getName()+" role #channel") // Message
                            .setColor(new Color(EmbedUtils.ERROR_COLOR))
                            .setTimestamp(OffsetDateTime.now())
                            .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)

                            .build()).build();
                    msg.getTextChannel().sendMessage(embed).queue();
                    return;
                }
            }

            String SQL_QUERY = "INSERT INTO server_data SET `server_id` = '"+msg.getGuild().getId()+"', `welcome_role` = '"+text+"' ON DUPLICATE KEY UPDATE `server_id` = '"+msg.getGuild().getId()+"', `welcome_role` = '"+text+"'";
            try {
                Connection con = DataSource.getConnection();
                PreparedStatement pst = con.prepareStatement(SQL_QUERY);
                pst.executeQuery();
                Message embed = new MessageBuilder().setEmbeds(new EmbedBuilder()
                        .setTitle("Success!")
                        .setDescription("The welcome role has been successfully set to `"+role.getName()+"`!")
                        .setColor(new Color(0x00FF00))
                        .setTimestamp(OffsetDateTime.now())
                        .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)

                        .build()).build();
                msg.getTextChannel().sendMessage(embed).queue();
            } catch (SQLException ignored) {
            }
            return;
        }

        msg.getTextChannel().sendMessage(help).complete();
    }

    @Override
    public String getName() { return "welcome"; }

    @Override
    public String getDescription() { return "configure welcome messages/roles"; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return Permission.ADMINISTRATOR; }
}
