package net.ddns.rootrobo.RaspiBot.stuff;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface Command {
    void run(Message msg, String[] args, Guild guild) throws IOException, ExecutionException, InterruptedException;
    String getName();
    String getDescription();
    String[] getAliases();
    Permission getPermission();
}