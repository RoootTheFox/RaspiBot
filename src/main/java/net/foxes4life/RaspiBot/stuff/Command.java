package net.foxes4life.RaspiBot.stuff;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

public interface Command {
    void run(Message msg, String[] args, Guild guild);
    String getName();
    String getDescription();
    String[] getAliases();
    Permission getPermission();
    default boolean getIsPublic() {
        return true;
    }
}