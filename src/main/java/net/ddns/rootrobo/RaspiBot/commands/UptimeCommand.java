package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.ddns.rootrobo.RaspiBot.utils.EmbedUtils;
import net.ddns.rootrobo.RaspiBot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class UptimeCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        int[] uptime = Utils.getUptime(Main.UPTIME);
        int days = uptime[0];
        int hours = uptime[1];
        int minutes = uptime[2];
        int seconds = uptime[3];

        String up = days+" days\n"+hours+" hours\n"+minutes+" minutes\n"+seconds+" seconds\n";
        Message uptimeMessage = new MessageBuilder().setEmbed(new EmbedBuilder()
                .setTitle("Uptime")
                .setDescription(up)
                .setColor(Utils.getRandomColor())
                .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)
        .build()).build();
        msg.getChannel().sendMessage(uptimeMessage).complete();
    }

    @Override
    public String getName() { return "uptime"; }

    @Override
    public String getDescription() { return "Shows the uptime of the bot."; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return null; }
}
