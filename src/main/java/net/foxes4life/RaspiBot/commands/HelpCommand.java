package net.foxes4life.RaspiBot.commands;

import net.foxes4life.RaspiBot.Main;
import net.foxes4life.RaspiBot.stuff.Command;
import net.foxes4life.RaspiBot.stuff.CommandManager;
import net.foxes4life.RaspiBot.utils.EmbedUtils;
import net.foxes4life.RaspiBot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.util.*;

@SuppressWarnings("unused")
public class HelpCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        EmbedBuilder eb = getEmptyHelp();

        ArrayList<String[]> e = new ArrayList<>();
        for (Map.Entry<String, Command> entry : CommandManager.commands.entrySet()) {
            if(msg.getMember() == null) return;
            if(msg.getMember().hasPermission(entry.getValue().getPermission())) {
                if(entry.getValue().getIsPublic() || msg.getAuthor().getId().equals(Main.DEVELOPER_ID)) {
                    String desc = entry.getValue().getDescription();
                    if(desc == null) {
                        desc = "*No description provided*";
                    }
                    e.add(new String[]{entry.getKey(), desc});
                }
            }
        }

        ArrayList<EmbedBuilder> embeds = new ArrayList<>();

        e.sort(Comparator.comparing(strings -> strings[0]));

        int count = 0;
        for (String[] strings : e) {
            count++;
            if (count > 24) {
                embeds.add(eb);
                eb = getEmptyHelp();
                eb.addField(Main.PREFIX + strings[0], strings[1], true);
                count = 1;
            } else {
                eb.addField(Main.PREFIX + strings[0], strings[1], true);
            }
        }
        embeds.add(eb);

        for (int i = 0; i < embeds.size(); i++) {
            EmbedBuilder eb2 = embeds.get(i);
            eb2.setTitle(eb2.build().getTitle()+" (Page "+(i+1)+")");
            msg.getChannel().sendMessage(eb2.build()).queue();
        }
    }

    @Override
    public String getName() { return "help"; }

    @Override
    public String getDescription() { return "Shows a list of commands."; }

    @Override
    public String[] getAliases() { return new String[]{"?", "h"}; }

    @Override
    public Permission getPermission() { return null; }

    private EmbedBuilder getEmptyHelp() {
        return new EmbedBuilder().setTitle("Commands")
                .setColor(Utils.getRandomColor())
                .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON);
    }
}
