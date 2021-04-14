package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.ddns.rootrobo.RaspiBot.stuff.CommandManager;
import net.ddns.rootrobo.RaspiBot.utils.EmbedUtils;
import net.ddns.rootrobo.RaspiBot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

@SuppressWarnings("unused")
public class HelpCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Commands")
                .setColor(Utils.getRandomColor())
                .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON);

        ArrayList<String[]> e = new ArrayList<>();
        for (Map.Entry<String, Command> entry : CommandManager.commands.entrySet()) {
            String desc = entry.getValue().getDescription();
            if(desc == null) {
                desc = "*No description provided*";
            }
            e.add(new String[]{entry.getKey(), desc});
        }

        e.sort(Comparator.comparing(strings -> strings[0]));

        for (String[] entry : e) {
            eb.addField(Main.PREFIX+entry[0], entry[1], true);
        }

        Message message = new MessageBuilder().setEmbed(eb.build()).build();
        msg.getChannel().sendMessage(message).complete();
    }

    @Override
    public String getName() { return "help"; }

    @Override
    public String getDescription() { return "high effort revolutionary help command"; }

    @Override
    public String[] getAliases() { return new String[]{"?", "h"}; }

    @Override
    public Permission getPermission() { return null; }
}
