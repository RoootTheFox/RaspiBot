package net.foxes4life.RaspiBot.commands;

import net.foxes4life.RaspiBot.stuff.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class GuildInfoCommand implements Command {

    @Override
    public void run(Message msg, String[] args, Guild guild) {
        List<Member> m = guild.getMembers();
        List<Member> members = new ArrayList<>(m);
        members.removeIf(member -> member.getUser().isBot());

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        EmbedBuilder info = new EmbedBuilder().setAuthor(guild.getName(), null, guild.getIconUrl());
        info.addField("Name", guild.getName(), true);
        info.addField("ID", guild.getId(), true);
        info.addField("Owner", Objects.requireNonNull(guild.getOwner()).getUser().getAsTag(), true);
        info.addField("Region", guild.getRegion().getEmoji()+" "+guild.getRegion().getName(), true);
        info.addField("Total members", String.valueOf(guild.getMemberCount()), true);
        info.addField("Humans", String.valueOf(members.size()), true);
        info.addField("Bots", String.valueOf(guild.getMemberCount()-members.size()), true);
        info.addField("Verification level", guild.getVerificationLevel().name(), true);
        info.addField("Channels", String.valueOf(guild.getChannels().size()), true);
        info.addField("Roles", String.valueOf(guild.getRoles().size()), true);
        info.addField("Emotes", String.valueOf(guild.getEmotes().size()), true);
        info.addField("Creation date", guild.getTimeCreated().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")), true);
        info.setThumbnail(guild.getIconUrl());

        msg.getChannel().sendMessage(info.build()).complete();
    }

    @Override
    public String getName() { return "guild-info";
    }

    @Override
    public String getDescription() { return "View information about the current guild."; }

    @Override
    public String[] getAliases() { return new String[]{"guildinfo", "server-info", "serverinfo", "dcinfo"}; }

    @Override
    public Permission getPermission() { return null; }
}
