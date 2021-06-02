package net.ddns.rootrobo.RaspiBot.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.ddns.rootrobo.RaspiBot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

// WORK IN PROGRESS
@SuppressWarnings("unused")
public class InviteInfoCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        if(args.length == 0) return;
        String code = args[0]
                .replace("https://", "")
                .replace("http://", "")
                .replace("discord.gg/", "")
                .replace("/", "");

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://discord.com/api/v8/invites/"+code);
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException ignored) {
        }
        BufferedReader rd = null;
        StringBuilder result;
        try {
            assert response != null;
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        result = new StringBuilder();
        String line;
        try {
            try {
                while (true) {
                    assert rd != null;
                    if ((line = rd.readLine()) == null) break;
                    result.append(line);
                }
            } catch (NullPointerException ignored) {
            }
        } catch (IOException ignored) {
        }
        String body = result.toString();
        JsonElement JasonElement = JsonParser.parseString(body);
        if(!JasonElement.isJsonObject()) return;
        JsonObject Jason = JasonElement.getAsJsonObject();
        JsonObject JasonGuild = Jason.get("guild").getAsJsonObject();

        // get guild info
        String GuildName = JasonGuild.get("name").getAsString();
        String GuildID = JasonGuild.get("id").getAsString();

        String GuildInviteSplash;
        try {
            GuildInviteSplash = JasonGuild.get("splash").getAsString();
        } catch (UnsupportedOperationException ignored) {
            GuildInviteSplash = null;
        }

        if(GuildInviteSplash != null) GuildInviteSplash = "https://cdn.discordapp.com/splashes/"+GuildID+"/"+GuildInviteSplash+".png";
        String GuildIconID;
        String GuildIcon = null;

        try {
            GuildIconID = JasonGuild.get("icon").getAsString();
        } catch (UnsupportedOperationException ignored) {
            GuildIconID = null;
        }
        if(GuildIconID != null) GuildIcon = Utils.getGuildIcon(GuildID, GuildIconID);

        JsonObject JasonInviter = null;
        String InviterID;
        String InviterAvatarID;
        String InviterAvatar = "";
        String InviterName;
        String InviterTag = "";
        try {
            JasonInviter = Jason.get("inviter").getAsJsonObject();
            InviterID = JasonInviter.get("id").getAsString();
            InviterAvatarID = JasonInviter.get("avatar").getAsString();
            InviterAvatar = Utils.getAvatar(InviterID, InviterAvatarID);
            InviterName = JasonInviter.get("username").getAsString();
            InviterTag = InviterName+"#"+JasonInviter.get("discriminator").getAsString();
        } catch (NullPointerException ignored) {
        }

        Main.LOGGER.info("Guild Name: "+GuildName);
        Main.LOGGER.info("Inviter Avatar URL: "+InviterAvatar);
        Main.LOGGER.info("Server Icon URL: "+GuildIcon);

        EmbedBuilder E = new EmbedBuilder().setTitle("Invite: "+GuildName);

        if(JasonInviter != null) {
            if(InviterAvatar != null) {
                E.setFooter("Invited by: "+InviterTag, InviterAvatar);
            } else {
                E.setFooter("Invited by: "+InviterTag);
            }
        } else {
            E.setFooter("(Vanity URL)");
        }

        if(GuildIcon != null) {
            E.setThumbnail(GuildIcon);
        }

        String GuildDesc = "";
        try {
            GuildDesc = JasonGuild.get("description").getAsString();
        } catch (UnsupportedOperationException ignored) {
        }

        ArrayList<String> GuildFeaturesArrayList = new ArrayList<>();
        String[] GuildFeatures = new String[]{};
        try {
            for (JsonElement jsonElement : JasonGuild.get("features").getAsJsonArray()) {
                GuildFeaturesArrayList.add(jsonElement.getAsString());
            }
            GuildFeatures = GuildFeaturesArrayList.toArray(new String[0]);
        } catch (UnsupportedOperationException ignored) {
        }

        for(String g : GuildFeatures) {
            Main.LOGGER.info("FEATURE:"+g);
        }

        if(GuildDesc != null) E.addField("Description", GuildDesc, true);

        // send embed pog
        Message e = new MessageBuilder().setEmbed(E.build()).build();

        msg.getChannel().sendMessage(e).complete();
    }

    @Override
    public String getName() { return "inviteinfo";
    }

    @Override
    public String getDescription() { return "(WIP) Displays information about an invite link."; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return null; }
}
