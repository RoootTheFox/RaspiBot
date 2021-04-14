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
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("unused")
public class GlobalChatModListCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        ArrayList<User> mods = new ArrayList<>();
        HashMap<String, String> mods2 = new HashMap<>();
        String SQL_QUERY2 = "SELECT * FROM globalchat_userdata WHERE isMod = true";
        try {
            PreparedStatement pst = DataSource.getConnection().prepareStatement(SQL_QUERY2);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                mods.add(Main.bot.getUserById(rs.getString("userid")));
                mods2.put(rs.getString("username"), rs.getString("userid"));
            }
        } catch (SQLException ignored) {
        }
        StringBuilder modlist = new StringBuilder();

        ArrayList<String> modsArray = new ArrayList<>();
        for (Map.Entry<String, String> entry : mods2.entrySet()) {
            String name = null;
            try {
                name = Objects.requireNonNull(Main.bot.getUserById(entry.getValue())).getAsTag();
            } catch (NullPointerException ignored) {
            }
            if(name == null) {
                name = entry.getKey();
            }

            modsArray.add(name);
        }

        modsArray.sort(Comparator.comparing(strings -> strings));

        for (String entry : modsArray) {
            modlist.append(entry).append("\n");
        }

        Message embed = new MessageBuilder().setEmbed(new EmbedBuilder()
                .setTitle("Global Chat Moderators ("+mods.size()+")")
                .setDescription(modlist.toString())
                .setColor(new Color(EmbedUtils.SUCCESS_COLOR))
                .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)
                .build()).build();
        msg.getTextChannel().sendMessage(embed).submit();

    }

    @Override
    public String getName() { return "gcmodlist"; }

    @Override
    public String getDescription() { return null; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return null; }
}
