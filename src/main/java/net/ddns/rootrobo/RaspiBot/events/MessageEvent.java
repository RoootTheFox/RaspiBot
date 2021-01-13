package net.ddns.rootrobo.RaspiBot.events;

import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.log.LogFormatter;
import net.ddns.rootrobo.RaspiBot.mysql.DataSource;
import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.ddns.rootrobo.RaspiBot.stuff.CommandManager;
import net.ddns.rootrobo.RaspiBot.utils.EmbedUtils;
import net.ddns.rootrobo.RaspiBot.utils.GlobalChat;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("unused")
public class MessageEvent extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        if(!(event.getChannelType() == ChannelType.TEXT)) return;

        String content = event.getMessage().getContentRaw();
        if(content.startsWith(Main.PREFIX) && !(content.startsWith(Main.PREFIX+Main.PREFIX))) {
            content = content.substring(content.indexOf(Main.PREFIX)+Main.PREFIX.length());
            String[] args = content.split(" ");
            String command = args[0];
            args = Arrays.copyOfRange(args, 1, args.length);

            Command cmd = CommandManager.getCommandByName(command);
            Command cmd2 = CommandManager.getCommandByAlias(command);

            Command run = null;
            if(cmd != null) {
                run = cmd;
            }
            if(cmd2 != null) {
                run = cmd2;
            }

            if(!(run == null)) {
                Permission permission = run.getPermission();
                Member member = event.getMessage().getGuild().getMember(event.getAuthor());
                if(!(permission == null)) {
                    if(member == null) return; // just to be safe
                    if(!member.hasPermission(permission)) {
                        event.getTextChannel().sendMessage(EmbedUtils.getNoPermissionEmbed(event.getMessage(), permission)).complete();
                        Main.LOGGER.info(LogFormatter.ANSI_YELLOW+event.getMessage().getAuthor().getAsTag() + " lacked the permission "+LogFormatter.ANSI_CYAN+permission.name()+LogFormatter.ANSI_YELLOW+" to run "+command);
                        return;
                    }
                }
                Main.LOGGER.info(event.getAuthor().getAsTag() + " issued bot command: " + Main.PREFIX + content);
                run.run(event.getMessage(), args, event.getGuild(), event.getTextChannel());
                return;
            }
        }

        // GLOBAL CHAT
        String SQL_QUERY = "SELECT * FROM globalchat_channels WHERE guild = '"+event.getGuild().getId()+"'";
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                String globalchat_english = rs.getString("channel_english");
                String globalchat_german = rs.getString("channel_german");
                if(globalchat_english == null && globalchat_german == null) {
                    return;
                } else {
                    if(event.getChannel().getId().equals(globalchat_english)) {
                        GlobalChat.langSend("english", event, con);
                    } else if (event.getChannel().getId().equals(globalchat_german)) {
                        GlobalChat.langSend("german", event, con);
                    }
                }
            }
        } catch (SQLException ignored) {
        }
    }
}