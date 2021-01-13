package net.ddns.rootrobo.RaspiBot.console.commands;

import net.ddns.rootrobo.RaspiBot.stuff.ConsoleCommand;
import net.ddns.rootrobo.RaspiBot.utils.GlobalChat;

import java.lang.String;
import java.util.Arrays;

@SuppressWarnings("unused")
public class GlobalChatSayCommand implements ConsoleCommand {
    @Override
    public void run(String command, String[] args) {
        String language = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);
        String text = String.join(" ", args);
        GlobalChat.send(null, String.join(" ", args), language, null, false, false, false, true);
    }

    @Override
    public String getName() { return "say"; }

    @Override
    public String getDescription() { return "Global Chat say Command"; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public boolean getRequiresReady() { return true; }
}
