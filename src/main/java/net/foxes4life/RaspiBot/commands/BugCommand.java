package net.foxes4life.RaspiBot.commands;

import net.foxes4life.RaspiBot.Main;
import net.foxes4life.RaspiBot.stuff.Command;
import net.foxes4life.RaspiBot.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.Objects;

@SuppressWarnings("unused")
public class BugCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        String reporter = msg.getAuthor().getAsTag();
        String bug = String.join(" ", args);
        if(bug.length() < 20) {
            MessageEmbed error = new EmbedBuilder()
                    .setTitle("Error")
                    .setColor(new Color(EmbedUtils.ERROR_COLOR))
                    .setDescription("A bug report has to be at least 20 characters long!")
                    .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)
                    .build();
            msg.getChannel().sendMessage(error).complete();
            return;
        }

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Bug Report")
                .setAuthor(reporter)
                .setDescription(bug)
                .setColor(new Color(0xFF7F00))
                .build();

        try {
            Objects.requireNonNull(Main.bot.getUserById(Main.DEVELOPER_ID)).openPrivateChannel().complete().sendMessage(embed).complete();
            MessageEmbed success = new EmbedBuilder()
                    .setTitle("Bug reported")
                    .setColor(new Color(EmbedUtils.SUCCESS_COLOR))
                    .setDescription("Your bug was successfully reported! I will try to fix the issue as soon as possible! Thank you!")
                    .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)
                    .build();
            msg.getChannel().sendMessage(success).complete();
        } catch (Exception ignored) {
            MessageEmbed embed1 = new EmbedBuilder()
                    .setTitle("Error")
                    .setColor(new Color(EmbedUtils.ERROR_COLOR))
                    .setDescription("Your Bug could NOT be reported! Please DM this error to the developer of this bot!")
                    .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)
                    .build();
            msg.getChannel().sendMessage(embed1).complete();
        }
    }

    @Override
    public String getName() { return "bug"; }

    @Override
    public String getDescription() { return "Report bugs"; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return null; }
}
