package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class SayCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        String text = String.join(" ", args);
        msg.delete().queue();
        msg.getChannel().sendMessage(text).queue();
    }

    @Override
    public String getName() { return "say"; }

    @Override
    public String getDescription() { return "Make the bot say what you want."; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return null; }
}
