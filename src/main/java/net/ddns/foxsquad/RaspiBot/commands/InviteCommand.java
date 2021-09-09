package net.ddns.foxsquad.RaspiBot.commands;

import net.ddns.foxsquad.RaspiBot.Main;
import net.ddns.foxsquad.RaspiBot.stuff.Command;
import net.ddns.foxsquad.RaspiBot.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import java.awt.Color;

@SuppressWarnings("unused")
public class InviteCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        String inviteLink = "https://discord.com/api/oauth2/authorize?client_id=" + Main.bot.getSelfUser().getId() + "&permissions=8&scope=bot%20applications.commands";
        String botName = Main.bot.getSelfUser().getName();

        MessageEmbed embed = new EmbedBuilder()
                .setTitle(botName)
                .setDescription("Thanks for using " + botName + "! To invite the bot to your server, click :link: [this link]("+inviteLink+").")
                .setColor(new Color(0, 255, 0))
                .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)
                .build();

        msg.getChannel().sendMessage(embed).complete();
    }

    @Override
    public String getName() { return "invite"; }

    @Override
    public String getDescription() { return "Get the link to invite this bot to your server."; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return null; }
}
