package net.foxes4life.RaspiBot.events;

import net.dv8tion.jda.api.entities.Role;
import net.foxes4life.RaspiBot.Main;
import net.foxes4life.RaspiBot.mysql.DataSource;
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
                String welcome_role = rs.getString("welcome_role");
                if(welcome_role != null) {
                    Role role = event.getGuild().getRoleById(welcome_role);
                    if(role != null) {
                        event.getGuild().addRoleToMember(event.getMember(), role).complete();
                    }
                }
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
