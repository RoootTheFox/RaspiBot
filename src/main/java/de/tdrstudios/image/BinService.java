package de.tdrstudios.image;

import net.ddns.rootrobo.RaspiBot.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.utils.AttachmentOption;

import java.io.File;
import java.util.concurrent.ExecutionException;

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
        //Debug:
        EmbedUtils.EmbedField[] embedFields = new EmbedUtils.EmbedField[message.getAttachments().size()];
        for (int i = 0; i < message.getAttachments().size(); i++) {
            embedFields[i] = new EmbedUtils.EmbedField("#" + i, message.getAttachments().get(i).getUrl());
        }
        EmbedUtils.getDebugEmbed("Payload:", embedFields).send(getBinChannel());
        //:Debug


        return cdn[0];
    }

    public String getBinFromFile(File file) throws ExecutionException, InterruptedException {
        System.out.println("Attatchment: " + getAttachmentFromFile(file).toString());
        return getAttachmentFromFile(file).getUrl();
    }

    public boolean clean(Message... messages) {
        binChannel.purgeMessages(messages);
        return !binChannel.getHistory().getRetrievedHistory().contains(messages);
    }

    public void clean() {
        binChannel.purgeMessages(binChannel.getHistory().getRetrievedHistory());
    }

    public void alert(String message) {
        EmbedUtils.sendTextEmbed("Bin - Alert", message, getBinChannel());
        System.out.println("[BIN]: " + message);
    }
}
