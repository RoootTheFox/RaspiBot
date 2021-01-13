package net.ddns.rootrobo.RaspiBot.stuff;

public interface ConsoleCommand {
    void run(String command, String[] args);
    String getName();
    String getDescription();
    String[] getAliases();
    boolean getRequiresReady();
}