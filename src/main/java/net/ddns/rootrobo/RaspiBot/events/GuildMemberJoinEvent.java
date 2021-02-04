package net.ddns.rootrobo.RaspiBot.events;

import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.mysql.DataSource;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class GuildMemberJoinEvent extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent event) {
        String SQL_QUERY = "SELECT * FROM server_data WHERE server_id = '"+event.getGuild().getId()+"'";
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                String welcomeChannelID = rs.getString("welcome_channel");
                if(welcomeChannelID == null) return;
                String welcome_msg = rs.getString("welcome_msg");
                if(!(welcome_msg == null)) {
                    welcome_msg = welcome_msg
                            .replace("{server}", event.getGuild().getName())
                            .replace("{user}", event.getMember().getUser().getAsMention())
                            .replace("{members}", String.valueOf(event.getGuild().getMemberCount()));
                    TextChannel channel = Main.bot.getTextChannelById(welcomeChannelID);
                    if(channel != null) {
                        channel.sendMessage(welcome_msg).complete();
                    }
                }
            }
        } catch (SQLException ignored) {
        }
    }
}
