package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.util.Calendar;

@SuppressWarnings("unused")
public class FakeBoostCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        Message message = msg.getChannel().sendMessage("<a:xenon:662002293759148032> boosting this server, pls wait").complete();
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            }
            message.editMessage("FAILED! Buy Nitro! :joy:").complete();
        }).start();
    }

    @Override
    public String getName() { return "boost"; }

    @Override
    public String getDescription() { return "BOOST YOUR SERVER FOR FREE 1000% REAL NO FAKE WORKING "+Calendar.getInstance().get(Calendar.YEAR); }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return null; }
}
