package net.ddns.rootrobo.RaspiBot.utils;

import net.ddns.rootrobo.RaspiBot.Main;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class MessageUtils {
    public static final String CHECK_MARK = ":white_check_mark:";
    public static final String RED_X_MARK = ":x:";
    public static final String FORBIDDEN_SIGN = ":no_entry:";
    public static String[] domainEndings = new String[]{"net", "org", "com", "de", "io", "gg"};
    public static void selfDestruct(MessageChannel channel, String content, long delay) {
        new Thread(() -> {
            Message msg = channel.sendMessage(content).complete();
            try {
                Thread.sleep(delay);
                msg.delete().complete();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static boolean containsLink(String s) {
        if(s.contains("http://") ||
                s.contains("https://") ||
                s.contains("www.")) return true;
        for (String ending : domainEndings) {
            if(s.contains("."+ending)) return true;
        }
        return false;
    }

    public static User getFirstUser(Message msg, String[] args) {
        User target = null;
        try {
            target = msg.getMentionedUsers().get(0);
        } catch (IndexOutOfBoundsException ignored) {
        }

        if (target != null) {
            Main.LOGGER.info("TRY 1: "+target.getId());
            return target;
        }
        if(args[0].startsWith("<")) {
            try {
                target = Main.bot.getUserById(args[0].replace("<@!", "").replace(">", "").replace("<@", ""));
                if(target != null) {
                    Main.LOGGER.info("TRY 2: "+ target.getId());
                    return target;
                }
            } catch (NumberFormatException ignored) {
            }
        } else {
            try {
                target = Main.bot.getUserById(args[0]);
                if(target != null) {
                    Main.LOGGER.info("TRY 3: " + target.getId());
                    return target;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        if(target == null) {
            try {
                target = Main.bot.getUserByTag(args[0]);
            } catch (IllegalArgumentException ignored) {
                return null;
            }
            if (target != null) {
                System.out.println("TRY 4: " + target.getId());
                return target;
            }
        }
        return target;
    }
}
