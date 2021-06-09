package net.ddns.rootrobo.RaspiBot.utils;

import net.ddns.rootrobo.RaspiBot.Main;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;

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
            return target;
        }
        if(args[0].startsWith("<")) {
            try {
                target = Main.bot.getUserById(args[0].replace("<@!", "").replace(">", "").replace("<@", ""));
                if(target != null) {
                    return target;
                }
            } catch (NumberFormatException ignored) {
            }
        } else {
            try {
                target = Main.bot.getUserById(args[0]);
                if(target != null) {
                    return target;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        try {
            target = Main.bot.getUserByTag(args[0]);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
        return target;
    }

    public static String replaceMentions(String message) {
        String[] msg_split = message.split("<@");

        ArrayList<String[]> replacements = new ArrayList<>();

        for (String str : msg_split) {
            if(str.contains(">")) {
                String id = str.substring(0, str.indexOf(">"));

                try {
                    Long.parseLong(id.substring(1));
                } catch (NumberFormatException ignored) {
                    continue;
                }

                if(id.startsWith("&")) {
                    // role mention
                    Role role = Main.bot.getRoleById(id.substring(1));
                    if(role != null) {
                        String original = "<@"+id+">";
                        String name = "@"+role.getName();
                        replacements.add(new String[]{original, name});
                    }
                } else if(id.startsWith("!")) {
                    // user mention
                    User user = Main.bot.getUserById(id.substring(1));
                    if(user != null) {
                        String original = "<@"+id+">";
                        String name = "@"+user.getAsTag();
                        replacements.add(new String[]{original, name});
                    }
                }
            }
        }

        for (String[] replacement : replacements) {
            message = message.replace(replacement[0], replacement[1]);
        }

        return message;
    }

    public static String replaceChannelMentions(String message) {
        String[] msg_split = message.split("<#");

        ArrayList<String[]> replacements = new ArrayList<>();

        for (String str : msg_split) {
            if(str.contains(">")) {
                String id = str.substring(0, str.indexOf(">"));

                try {
                    Long.parseLong(id.substring(1));
                } catch (NumberFormatException ignored) {
                    continue;
                }

                TextChannel textChannel = Main.bot.getTextChannelById(id);
                if(textChannel != null) {
                    String original = "<#"+id+">";
                    String name = "#"+textChannel.getName();
                    replacements.add(new String[]{original, name});
                } else {
                    VoiceChannel voiceChannel = Main.bot.getVoiceChannelById(id);
                    if(voiceChannel != null) {
                        String original = "<#"+id+">";
                        String name = "\\\uD83D\uDD08"+voiceChannel.getName();
                        replacements.add(new String[]{original, name});
                    }
                }
            }
        }

        for (String[] replacement : replacements) {
            message = message.replace(replacement[0], replacement[1]);
        }

        return message;
    }
}
