package net.foxes4life.RaspiBot.console.commands;

import net.foxes4life.RaspiBot.Main;
import net.foxes4life.RaspiBot.stuff.ConsoleCommand;
import net.foxes4life.RaspiBot.utils.GlobalChat;

import java.lang.String;
import java.util.Arrays;

@SuppressWarnings("unused")
public class GlobalChatSayCommand implements ConsoleCommand {
    @Override
    public void run(String command, String[] args) {
        if(args.length < 1) {
            Main.LOGGER.warning("You have to specify the language!");
            Main.LOGGER.warning("Example: say EN Hello world!");
            return;
        }
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
