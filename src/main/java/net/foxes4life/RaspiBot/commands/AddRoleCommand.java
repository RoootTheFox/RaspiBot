package net.foxes4life.RaspiBot.commands;

import net.foxes4life.RaspiBot.stuff.Command;
import net.foxes4life.RaspiBot.utils.EmbedUtils;
import net.foxes4life.RaspiBot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class AddRoleCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        if(msg.getMember() == null) return;

        List<Member> targets = msg.getMentionedMembers();
        List<Role> roles = msg.getMentionedRoles();
        //Member target = msg.getMentionedMembers().get(0);

        if(targets.size() == 0) {
            Message errorMSG = new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setTitle("Error")
                    .setColor(EmbedUtils.ERROR_COLOR)
                    .setDescription("Please specify the member you want to add a role to!")
                    .build()).build();
            msg.getChannel().sendMessage(errorMSG).complete();
            return;
        }

        if(roles.size() == 0) {
            Message errorMSG = new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setTitle("Error")
                    .setColor(EmbedUtils.ERROR_COLOR)
                    .setDescription("Please specify a role you want to add!")
                    .build()).build();
            msg.getChannel().sendMessage(errorMSG).complete();
            return;
        }

        ArrayList<Member> inaccessible_targets = new ArrayList<>();

        for (Member target : targets) {
            if(!msg.getMember().canInteract(target)) {
                inaccessible_targets.add(target);
            }
        }
        StringBuilder inaccessible = new StringBuilder();
        for (Member ia_target : inaccessible_targets) {
            inaccessible.append(ia_target.getAsMention()).append("\n");
        }
        if(inaccessible_targets.size() != 0) {
            Message errorMSG = new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setTitle("Error")
                    .setColor(EmbedUtils.ERROR_COLOR)
                    .setDescription("You can't interact with the following member(s): \n"+inaccessible)
                    .build()).build();
            msg.getChannel().sendMessage(errorMSG).complete();
            return;
        }

        for (Member target : targets) {
            if(!msg.getGuild().getSelfMember().canInteract(target)) {
                inaccessible_targets.add(target);
            }
        }
        for (Member ia_target : inaccessible_targets) {
            inaccessible.append(ia_target.getAsMention()).append("\n");
        }
        if(inaccessible_targets.size() != 0) {
            Message errorMSG = new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setTitle("Error")
                    .setColor(EmbedUtils.ERROR_COLOR)
                    .setDescription("I can't interact with the following member(s): \n"+inaccessible)
                    .build()).build();
            msg.getChannel().sendMessage(errorMSG).complete();
            return;
        }


        ArrayList<Role> inaccessible_roles = new ArrayList<>();

        for (Role role : roles) {
            if(!msg.getMember().canInteract(role)) {
                inaccessible_roles.add(role);
            }
        }
        for (Role ia_role : inaccessible_roles) {
            inaccessible.append(ia_role.getAsMention()).append("\n");
        }
        if(inaccessible_roles.size() != 0) {
            Message errorMSG = new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setTitle("Error")
                    .setColor(EmbedUtils.ERROR_COLOR)
                    .setDescription("You can't interact with the following role(s): \n"+inaccessible)
                    .build()).build();
            msg.getChannel().sendMessage(errorMSG).complete();
            return;
        }

        for (Role role : roles) {
            if(!msg.getGuild().getSelfMember().canInteract(role)) {
                inaccessible_roles.add(role);
            }
        }
        for (Role ia_role : inaccessible_roles) {
            inaccessible.append(ia_role.getAsMention()).append("\n");
        }
        if(inaccessible_roles.size() != 0) {
            Message errorMSG = new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setTitle("Error")
                    .setColor(EmbedUtils.ERROR_COLOR)
                    .setDescription("I can't interact with the following role(s): \n"+inaccessible)
                    .build()).build();
            msg.getChannel().sendMessage(errorMSG).complete();
            return;
        }

        AtomicInteger added = new AtomicInteger();

        for (int i = 0; i < targets.size(); i++) {
            if(i == targets.size()-1) {
                for (int i1 = 0; i1 < roles.size(); i1++) {
                    if(i1 == roles.size()-1) {
                        guild.addRoleToMember(targets.get(i), roles.get(i1)).complete(); // final role and member
                    } else {
                        guild.addRoleToMember(targets.get(i), roles.get(i1)).queue(success -> added.getAndIncrement());
                    }
                }
            } else {
                for (Role role : roles) {
                    guild.addRoleToMember(targets.get(i), role).queue(success -> added.getAndIncrement());
                }
            }
        }

        Message successMSG = new MessageBuilder().setEmbed(new EmbedBuilder()
                .setTitle(MessageUtils.CHECK_MARK+" Success")
                .setColor(EmbedUtils.SUCCESS_COLOR)
                .setDescription("Successfully completed "+ added +" role additions.")
                .build()).build();
        msg.getChannel().sendMessage(successMSG).complete();
    }

    @Override
    public String getName() { return "addrole"; }

    @Override
    public String getDescription() { return "Lets you add roles to one or multiple members."; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return Permission.ADMINISTRATOR; }
}
