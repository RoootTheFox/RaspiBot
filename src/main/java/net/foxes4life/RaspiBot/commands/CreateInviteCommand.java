package net.foxes4life.RaspiBot.commands;

import net.foxes4life.RaspiBot.stuff.Command;
import net.foxes4life.RaspiBot.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Message;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class CreateInviteCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        Invite invite = msg.getTextChannel().createInvite().setMaxUses(2).setMaxAge(1L, TimeUnit.DAYS).complete();
        Message successMSG = new MessageBuilder().setEmbed(new EmbedBuilder()
                .setTitle("Invite created!")
                .setColor(EmbedUtils.SUCCESS_COLOR)
                .setDescription("Successfully created an invite! \nThe invite can be used 2 times and is valid for 24 hours: ["+invite.getCode()+"](https://discord.gg/"+invite.getCode()+")")
                .build()).build();
        msg.getChannel().sendMessage(successMSG).complete();
    }

    @Override
    public String getName() { return "createinvite"; }

    @Override
    public String getDescription() { return "Creates a temporary invite for the current channel."; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return Permission.CREATE_INSTANT_INVITE; }
}
