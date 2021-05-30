package de.tdrstudios;

import de.tdrstudios.image.BinService;
import de.tdrstudios.jarargs.ArgumentManager;
import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.config.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This Utils are written by /TDRMinecraft
 */
public class TDRUtils {

    public static final int DEFAULT_BUFFER_SIZE = 8192;

    private final static BinService binService = new BinService(Main.bot.getGuildById(Config.getInstance().bin_guild).getTextChannelById(Config.getInstance().bin_channel));


    public static BinService getBinService() {
        return binService;
    }

    /**
     * @param l
     * @return The number of digits the long has!
     */
    public static int getLength(long l) {
        if(l > 0)
        return (int)(Math.log10(l)+1);
        else
            return (int)(Math.log10(l - 2*l)+1);
    }

    /**
     * @param d
     * @return The number of digits the long has in front of the '.'!
     */
    public static int getLength(double d) {
        if(d > 0)
            return (int)(Math.log10(d)+1);
        else
            return (int)(Math.log10(d - 2*d)+1);
    }

    /**
     * @param i
     * @return The number of digits the long has!
     */
    public static int getLength(int i) {
        if(i > 0)
            return (int)(Math.log10(i)+1);
        else
            return (int)(Math.log10(i - 2*i)+1);
    }

    public static void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {

        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }

    }
}
