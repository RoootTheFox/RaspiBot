package de.tdrstudios.image;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.utils.AttachmentOption;

import java.io.File;

public class BinService {
    public BinService(MessageChannel binChannel) {
        this.binChannel = binChannel;
    }
    private MessageChannel binChannel;

    public MessageChannel getBinChannel() {
        return binChannel;
    }
    public Message.Attachment getAttachmentFromFile(File file) {
        final Message.Attachment[] cdn = new Message.Attachment[1];
        binChannel.sendFile(file, String.valueOf(System.currentTimeMillis()), AttachmentOption.SPOILER).queue(message -> {
            Message.Attachment attachment = message.getAttachments().get(0);
            cdn[0] = attachment;
        });
        return cdn[0];
    }

    public String getBinFromFile(File file) {
       return getAttachmentFromFile(file).getUrl();
    }
    public boolean clean(Message... messages) {
        binChannel.purgeMessages(messages);
        return  !binChannel.getHistory().getRetrievedHistory().contains(messages);
    }
    public void clean() {
        binChannel.purgeMessages(binChannel.getHistory().getRetrievedHistory());
    }

}
