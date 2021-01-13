package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.ddns.rootrobo.RaspiBot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("unused")
public class PingCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild, TextChannel channel) {
        msg.getChannel().sendTyping().queue();
        int[] uptime = Utils.getUptime(Main.UPTIME);

        int days = uptime[0];
        int hours = uptime[1];
        int minutes = uptime[2];
        int seconds = uptime[3];

        String up = days+" days, "+hours+" hours, "+minutes+" minutes, "+seconds+" seconds";

        long ping = Utils.getPing();

        EmbedBuilder out = new EmbedBuilder().setTitle("Pong!");
        out.addField("Gateway Ping", ping+"ms", false);
        out.addField("Uptime", up, false);
        out.setColor(new Color(0xFF6AFF47, true));

        msg.getTextChannel().sendMessage(out.build()).queue();
    }

    @Override
    public String getName() { return "ping"; }

    @Override
    public String getDescription() { return "Shows the current Gateway Ping and the Uptime of the bot."; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return null; }
}
