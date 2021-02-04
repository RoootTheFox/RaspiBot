package net.ddns.rootrobo.RaspiBot.utils;

import net.ddns.rootrobo.RaspiBot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

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


}
