package net.foxes4life.RaspiBot.commands;

import net.foxes4life.RaspiBot.stuff.Command;
import net.foxes4life.RaspiBot.utils.EmbedUtils;
import net.foxes4life.RaspiBot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@SuppressWarnings("unused")
public class ClearCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        OffsetDateTime maxDate = OffsetDateTime.now().minus(2, ChronoUnit.WEEKS);
        int maxMSGs;
        try {
            maxMSGs = Integer.parseInt(args[0]);
        } catch (IndexOutOfBoundsException ignored) {
            maxMSGs = 100;
        }

        if(maxMSGs < 1 || 100 < maxMSGs) {
            Message errorMSG = new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setTitle("Error")
                    .setColor(EmbedUtils.ERROR_COLOR)
                    .setDescription("The amount of messages to delete has to be between 1 and 100.")
                    .build()).build();
            msg.getChannel().sendMessage(errorMSG).complete();
            return;
        }

        int finalMaxMSGs = maxMSGs;
        new Thread(() -> {
            List<Message> msgs = msg.getChannel().getHistory().retrievePast(finalMaxMSGs).complete();
            msgs.removeIf(message -> message.getTimeCreated().isBefore(maxDate));
            msg.getChannel().purgeMessages(msgs);
            MessageUtils.selfDestruct(msg.getChannel(), "Deleted "+msgs.size()+" messages!", 2500);
        }).start();
    }

    @Override
    public String getName() { return "purge"; }

    @Override
    public String getDescription() { return "Purges a specified amount (or 100) messages in the current channel."; }

    @Override
    public String[] getAliases() { return new String[]{"clear"}; }

    @Override
    public Permission getPermission() { return Permission.MESSAGE_MANAGE; }
}
