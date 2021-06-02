package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.ddns.rootrobo.RaspiBot.utils.MessageUtils;
import net.ddns.rootrobo.RaspiBot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

@SuppressWarnings("unused")
public class AvatarCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        User u;

        if(args.length == 0) {
            u = msg.getAuthor();
        } else {
            u = MessageUtils.getFirstUser(msg, args);
            if(u == null) {
                msg.getChannel().sendMessage("Invalid user!").complete();
                return;
            }
        }


        String avatarURL = u.getAvatarUrl();
        if(avatarURL == null) avatarURL = u.getDefaultAvatarUrl();

        Message message = new MessageBuilder().setEmbed(new EmbedBuilder()
                .setTitle(u.getName()+"'s Avatar")
                .setImage(avatarURL)
                .setColor(Utils.getRandomColor())
        .build()).build();
        msg.getChannel().sendMessage(message).complete();
    }

    @Override
    public String getName() { return "avatar"; }

    @Override
    public String getDescription() { return "Show the avatar of a specified user."; }

    @Override
    public String[] getAliases() { return new String[]{"pfp"}; }

    @Override
    public Permission getPermission() { return null; }
}
