package de.tdrstudios.image;

import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.utils.AttachmentOption;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class BinService {
    public BinService(MessageChannel binChannel) {
        this.binChannel = binChannel;

    }

    private MessageChannel binChannel;

    public MessageChannel getBinChannel() {
        return binChannel;
    }

    public Message.Attachment getAttachmentFromFile(File file) throws ExecutionException, InterruptedException {
        final Message.Attachment[] cdn = new Message.Attachment[1];
        Message message = binChannel.sendFile(file, System.currentTimeMillis() + ".png", AttachmentOption.SPOILER).submit().get();

        Message.Attachment attachment = message.getAttachments().get(0);
        cdn[0] = attachment;
        message.getChannel().sendTyping().queue();
        message.delete().queueAfter(6, TimeUnit.SECONDS);
        return cdn[0];
    }

    public String getBinFromFile(File file) throws ExecutionException, InterruptedException {
        System.out.println("Attatchment: " + getAttachmentFromFile(file).toString());
        return getAttachmentFromFile(file).getUrl();
    }

    public boolean clean(Message... messages) {
        Main.LOGGER.warning("Trying to delete " + messages.length + " Messages in the bin Channel!");
        binChannel.purgeMessages(messages);
        return !binChannel.getHistory().getRetrievedHistory().contains(messages);
    }

    public void clean() {
        Main.LOGGER.warning("Trying to delete " + binChannel.getHistory().size() + " Messages in the bin Channel!");
        binChannel.purgeMessages(binChannel.getHistory().getRetrievedHistory());
    }

    public void alert(String message) {
        EmbedUtils.sendTempTextEmbed("Bin - Alert", message, getBinChannel(), 1); //TODO: Switch to 5min
        System.out.println("[BIN]: " + message);

    }
    public void alert(String message, int minutes) {
        EmbedUtils.sendTempTextEmbed("Bin - Alert", message, getBinChannel(), minutes);
        System.out.println("[BIN]: " + message);

    }

    protected static class DeleteThread implements Runnable {
        public DeleteThread(Message... pMessages) {
            for (int i = 0; i < messages.length; i++) {

            }
        }
        private Message[] messages;

        public Message[] getMessages() {
            return messages;
        }

        @Override
        public void run() {
            for (Message message : getMessages()) {
                //message.delete().queueAfter(6, "s");
            }
        }
    }
}
