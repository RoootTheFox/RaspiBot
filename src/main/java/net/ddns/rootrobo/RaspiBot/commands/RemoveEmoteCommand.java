package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

@SuppressWarnings("unused")
public class RemoveEmoteCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        msg.getChannel().sendTyping().queue();
        List<Emote> emotes = msg.getEmotes();
        for (String arg : args) {
            if(!arg.startsWith("<")) {
                try {
                    long id = Long.parseLong(arg);
                    Emote emote = msg.getGuild().getEmoteById(id);
                    if(emote == null) break;
                    emotes.add(emote);
                } catch (NumberFormatException ignored) {}
            }
        }

        int deleted = 0;

        for (int i = 0; i < emotes.size(); i++) {
            if(i == emotes.size()-1) {
                emotes.get(i).delete().complete();
            } else {
                emotes.get(i).delete().queue();
            }
            deleted++;
        }

        msg.getChannel().sendMessage("Deleted " +deleted+ " emotes.").complete();
    }

    @Override
    public String getName() {
        return "rmemote";
    }

    @Override
    public String getDescription() {
        return "Deletes Emotes from the server.";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"delemote", "delemoji", "rmemoji"};
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_EMOTES;
    }
}
