package net.ddns.rootrobo.RaspiBot.utils;

import net.ddns.rootrobo.RaspiBot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class EmbedUtils {
    public static final int ERROR_COLOR = 15400960; //epic red
    public static final int SUCCESS_COLOR = 65280; //epic lime
    public static String FOOTER_TEXT = "RaspiBot | Coded by "+ Main.DEVELOPER_NAME;
    public static String FOOTER_ICON = null;

    public static MessageEmbed getSelfNoPermissionEmbed(User user, String for_this) {
        String AvatarURL = Main.bot.getSelfUser().getAvatarUrl();
        if(AvatarURL == null) {
            AvatarURL= Main.bot.getSelfUser().getDefaultAvatarUrl();
        }

        return new EmbedBuilder()
                .setTitle(MessageUtils.RED_X_MARK + " Missing Permissions")
                .setDescription(user.getAsMention()+", I don't have enough permissions "+for_this)
                .setColor(new Color(ERROR_COLOR))
                .setThumbnail(AvatarURL)
                .setFooter(FOOTER_TEXT, FOOTER_ICON)
                .build();
    }

    public static MessageEmbed getNoPermissionEmbed(User user, Permission permission) {
        String AvatarURL = user.getAvatarUrl();
        if(AvatarURL == null) AvatarURL = user.getDefaultAvatarUrl();

        return new EmbedBuilder()
                .setTitle(MessageUtils.FORBIDDEN_SIGN+ " Missing Permissions")
                .setDescription(user.getAsMention()+", you need the permission `"+permission.name()+"` to run this command!")
                .setColor(new Color(ERROR_COLOR))
                .setThumbnail(AvatarURL)
                .setFooter(FOOTER_TEXT, FOOTER_ICON)
                .build();
    }

    public static EmbedBuilder createEmbedBuilder(Color color) {
        return new EmbedBuilder()
                .setColor(color)
                .setFooter(FOOTER_TEXT, FOOTER_ICON);
    }

    public static EmbedBuilder createMentionEmbedBuilder(Color color, User user) {
        String profile = user.getAvatarUrl();
        if(profile == null) profile = user.getDefaultAvatarUrl();
        return new EmbedBuilder()
                .setColor(color)
                .setDescription(user.getAsMention())
                .setThumbnail(profile)
                .setFooter(FOOTER_TEXT, FOOTER_ICON);
    }

    public static DebugEmbedBuilder getDebugEmbed(String text, EmbedField[] fields) {
        DebugEmbedBuilder embedBuilder = new DebugEmbedBuilder();
        embedBuilder.setColor(Color.GRAY).setTitle("Debug").setDescription(text);
        for (EmbedField embedField : fields) {
            embedField.append(embedBuilder, false);
        }
        embedBuilder.setFooter(FOOTER_TEXT, FOOTER_ICON);
        return embedBuilder;
    }

    public static DebugEmbedBuilder getDebugEmbed(String text) {
        DebugEmbedBuilder embedBuilder = new DebugEmbedBuilder();
        embedBuilder.setColor(Color.GRAY).setTitle("Debug").setDescription(text);
        embedBuilder.setFooter(FOOTER_TEXT, FOOTER_ICON);
        return embedBuilder;
    }

    public static void sendDebugEmbed(MessageEmbed embed, MessageChannel messageChannel) {
        if(Main.enableDebugEmbeds)
            messageChannel.sendMessage(embed);
    }

    public static MessageEmbed sendTempTextEmbed(String title, String text, MessageChannel channel, int minutes) {
        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle(title).setDescription(text).setFooter(FOOTER_TEXT, FOOTER_ICON);
        MessageEmbed embed = embedBuilder.build();
        channel.sendMessage(embed).queue(message -> {
            message.delete().queueAfter(minutes, TimeUnit.MINUTES);
        });
        return embed;
    }
    public static MessageEmbed sendTextEmbed(String title, String text, MessageChannel channel) {
        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle(title).setDescription(text).setFooter(FOOTER_TEXT, FOOTER_ICON);
        MessageEmbed embed = embedBuilder.build();
        channel.sendMessage(embed).queue();
        return embed;
    }

    public static class EmbedField{
        private String name;
        private String description;

        public EmbedField(String name, String description) {
            setName(name);
            setDescription(description);
        }
        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDescription(String description) {
            this.description = description;
        }
        public boolean append(EmbedBuilder embedBuilder, boolean inLine) {
            embedBuilder.addField(getName(), getDescription(),inLine);
            return true;
        }
    }

    public static class DebugEmbedBuilder extends EmbedBuilder {
        public void send(MessageChannel channel) {
            System.out.println("[DEBUG]: " + toString());
            if(Main.enableDebugEmbeds) {
                channel.sendMessage(build()).queue();
            }
        }

        @Override
        public String toString() {
            MessageEmbed embed = build();
            StringBuilder builder = new StringBuilder();
            builder.append(embed.getTitle() + " <> " + embed.getDescription() + "\n");
            for (MessageEmbed.Field field : embed.getFields()) {
                builder.append(field.getName() + "/" + field.getValue() + "\n");
            }
            builder.append(embed.getFooter().getText());
            return builder.toString();
        }
    }


}
