package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.ddns.rootrobo.RaspiBot.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import java.awt.Color;

@SuppressWarnings("unused")
public class InviteCommand implements Command {
    private static final String inviteLink = "https://discord.com/api/oauth2/authorize?client_id=622020397449216000&permissions=8&scope=bot%20applications.commands";
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        MessageEmbed embed = new EmbedBuilder()
                .setTitle(Main.bot.getSelfUser().getName())
                .setDescription("Thanks for using RaspiBot! To invite the bot to your server, click :link: [this link]("+inviteLink+").")
                .setColor(new Color(0, 255, 0))
                .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)

                .build();
        msg.getChannel().sendMessage(embed).complete();
    }

    @Override
    public String getName() { return "invite"; }

    @Override
    public String getDescription() { return "Get the URL to invite this bot to your server."; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return null; }
}
