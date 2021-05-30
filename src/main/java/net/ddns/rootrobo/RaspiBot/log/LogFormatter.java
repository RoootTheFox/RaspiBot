package net.ddns.rootrobo.RaspiBot.log;

import de.tdrstudios.TDRUtils;
import net.ddns.rootrobo.RaspiBot.Main;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    public static final boolean enableColoredConsole = !Main.ARGUMENT_MANAGER.hasArgument("-noColor");
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public LogFormatter() {
        System.out.println("enableColoredConsole = " + enableColoredConsole);
    }

    public String format(LogRecord record) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String timestamp = sdf.format(Date.from(Instant.now()));
        String message = record.getMessage();
        Level level = record.getLevel();
        StringBuilder builder = new StringBuilder();
        if(enableColoredConsole) {
            if (level == Level.WARNING) builder.append(ANSI_YELLOW);
            if (level == Level.SEVERE) builder.append(ANSI_RED);
            if(level == Level.CONFIG) builder.append(ANSI_CYAN);
            builder.append("[")
                    .append(timestamp)
                    .append("] ");
            builder.append(ANSI_RESET);
            builder.append(message);


            builder.append(ANSI_RESET); // reset formatting after message
        }else {
         message = message.replace(ANSI_RESET, "").replace(ANSI_BLACK, "").replace(ANSI_RED, "").replace(ANSI_GREEN, "")
                    .replace(ANSI_YELLOW, "").replace(ANSI_BLUE, "").replace(ANSI_PURPLE, "").replace(ANSI_CYAN, "").replace(ANSI_WHITE, "");
            builder.append("[").append(timestamp).append("] ").append("[" + level.getName() + "] ").append(message);
        }
        builder.append("\n");
        return builder.toString();
    }

}
