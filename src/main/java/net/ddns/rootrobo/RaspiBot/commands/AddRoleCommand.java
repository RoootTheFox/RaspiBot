package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.ddns.rootrobo.RaspiBot.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.util.Arrays;

@SuppressWarnings("unused")
public class AddRoleCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        if(msg.getMember() == null) return;

        Member target = msg.getMentionedMembers().get(0);
        Role role = null;
        try {
            role = msg.getMentionedRoles().get(0);
        } catch (IndexOutOfBoundsException ignored) {
        }

        if(target == null) {
            Message errorMSG = new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setTitle("Error")
                    .setColor(EmbedUtils.ERROR_COLOR)
                    .setDescription("Please specify the member you want to add a role to!")
                    .build()).build();
            msg.getChannel().sendMessage(errorMSG).complete();
            return;
        }

        if(!msg.getMember().canInteract(target)) {
            Message errorMSG = new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setTitle("Error")
                    .setColor(EmbedUtils.ERROR_COLOR)
                    .setDescription("You can't interact with that member!")
                    .build()).build();
            msg.getChannel().sendMessage(errorMSG).complete();
            return;
        }

        if(!msg.getGuild().getSelfMember().canInteract(target)) {
            Message errorMSG = new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setTitle("Error")
                    .setColor(EmbedUtils.ERROR_COLOR)
                    .setDescription("I can't interact with that member!")
                    .build()).build();
            msg.getChannel().sendMessage(errorMSG).complete();
            return;
        }

        if(role == null) {
            args = Arrays.copyOfRange(args, 1, args.length);
            role = msg.getGuild().getRolesByName(String.join(" ", args), true).get(0);
            if(role == null) {
                Message errorMSG = new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setTitle("Error")
                        .setColor(EmbedUtils.ERROR_COLOR)
                        .setDescription("Could not find a role!")
                        .build()).build();
                msg.getChannel().sendMessage(errorMSG).complete();
                return;
            }
        }

        Role finalRole = role;
        guild.addRoleToMember(target, role).queue(
                success -> {
                    Message successMSG = new MessageBuilder().setEmbed(new EmbedBuilder()
                            .setTitle("Success")
                            .setColor(EmbedUtils.SUCCESS_COLOR)
                            .setDescription(target.getEffectiveName() +" has been given the "+ finalRole.getAsMention()+" role.")
                            .build()).build();
                    msg.getChannel().sendMessage(successMSG).queue();
                }
        );
    }

    @Override
    public String getName() { return "addrole"; }

    @Override
    public String getDescription() { return null; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return Permission.ADMINISTRATOR; }
}
