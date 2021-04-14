package net.ddns.rootrobo.RaspiBot.events;

import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.mysql.DataSource;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@SuppressWarnings("unused")
public class GuildMemberLeaveEvent extends ListenerAdapter {
    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        String SQL_QUERY = "SELECT * FROM server_data WHERE server_id = '"+event.getGuild().getId()+"'";
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                String leaveChannelID = rs.getString("leave_channel");
                if(leaveChannelID == null) return;
                String leave_msg = rs.getString("leave_msg");
                if(!(leave_msg == null)) {
                    leave_msg = leave_msg
                            .replace("{server}", event.getGuild().getName())
                            .replace("{members}", String.valueOf(event.getGuild().getMemberCount()))
                            .replace("{user}", Objects.requireNonNull(event.getMember()).getUser().getAsTag());
                    TextChannel channel = Main.bot.getTextChannelById(leaveChannelID);
                    if(channel != null) {
                        channel.sendMessage(leave_msg).complete();
                    }
                }
            }
        } catch (SQLException ignored) {
        }
    }
}
