package net.ddns.rootrobo.RaspiBot.events;

import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.log.LogFormatter;
import net.ddns.rootrobo.RaspiBot.stuff.DynamicActivity;
import net.ddns.rootrobo.RaspiBot.utils.EmbedUtils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Objects;

@SuppressWarnings("unused")
public class ReadyEvent extends ListenerAdapter {
    @Override
    public void onReady(@Nonnull net.dv8tion.jda.api.events.ReadyEvent event) {
        Main.DEVELOPER_NAME = Objects.requireNonNull(Main.bot.getUserById(Main.DEVELOPER_ID)).getAsTag();
        EmbedUtils.FOOTER_ICON = Main.bot.getSelfUser().getAvatarUrl();
        if(EmbedUtils.FOOTER_ICON == null) {
            EmbedUtils.FOOTER_ICON = Main.bot.getSelfUser().getDefaultAvatarUrl();
        }
        Main.UPTIME = System.currentTimeMillis();

        Main.LOGGER.info("---------- [ "+ LogFormatter.ANSI_RED+"RASPIBOT"+LogFormatter.ANSI_RESET+" ] ----------");
        Main.LOGGER.info("Events: " + Main.EVENTCOUNT);
        Main.LOGGER.info("Commands: " + Main.COMMANDCOUNT);
        Main.LOGGER.info("Console Commands: " + Main.CONSOLECOMMANDCOUNT);
        Main.LOGGER.info("Prefix: "+Main.PREFIX);
        Main.LOGGER.info("---------- [ "+ LogFormatter.ANSI_RED+"RASPIBOT"+LogFormatter.ANSI_RESET+" ] ----------");

        Main.LOGGER.info(LogFormatter.ANSI_GREEN+"BOT IS READY!"+LogFormatter.ANSI_RESET);

        Main.READY = true;
        Main.bot.getPresence().setStatus(OnlineStatus.ONLINE);

        DynamicActivity.start();

        // (debug) uncomment to get a list of guilds
        //for (Guild guild : Main.bot.getGuilds()) { System.out.println("M:"+guild.getMemberCount()+" N:"+guild.getName()+" I:"+guild.getId()); }
    }
}
