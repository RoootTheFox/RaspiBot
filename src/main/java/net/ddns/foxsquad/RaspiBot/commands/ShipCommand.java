package net.ddns.foxsquad.RaspiBot.commands;

import net.ddns.foxsquad.RaspiBot.Main;
import net.ddns.foxsquad.RaspiBot.stuff.Command;
import net.ddns.foxsquad.RaspiBot.utils.NetUtils;
import net.ddns.foxsquad.RaspiBot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

@SuppressWarnings("unused")
public class ShipCommand implements Command {
    Random random = new Random();
    HashMap<long[], long[]> ships = new HashMap<>();
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        if(msg.getMentionedUsers().size() == 0) {
            msg.getChannel().sendMessage(
                    "Usage: "+ Main.PREFIX+getName()+" @user"+"\n"+
                    "or "+Main.PREFIX+getName()+" @User @User").complete();
            return;
        }

        User user0 = msg.getAuthor();
        User user1 = msg.getMentionedUsers().get(0);

        if(msg.getMentionedUsers().size() >= 2) {
            user0 = msg.getMentionedUsers().get(0);
            user1 = msg.getMentionedUsers().get(1);
        }

        long id0 = user0.getIdLong();
        long id1 = user1.getIdLong();

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(id0);
        ids.add(id1);
        ids.sort(null);

        long[] ship = new long[]{ids.get(0), ids.get(1)};

        int percentage;
        if(isShipped(ship)) {
            percentage = getShip(ship);
        } else {
            percentage = random.nextInt(101);
            ships.put(ship, new long[]{System.currentTimeMillis(), percentage});
        }

        // nothing :)
        if(ship[0] == 467730889640640523L && ship[1] == 582588305288200281L) {
            percentage = 100;
        }

        InputStream base_stream;
        if(percentage == 69) { // no i am not sorry (nice btw)
            base_stream = Utils.getInputStreamFromBotJar("ship_base_69.png");
        } else {
            base_stream = Utils.getInputStreamFromBotJar("ship_base.png");
        }

        if(base_stream == null) {
            Main.LOGGER.severe("Could not load base image!");
            return;
        }

        BufferedImage base;
        try {
            base = ImageIO.read(base_stream);
        } catch (IOException ignored) {
            Main.LOGGER.severe("Could not load base image!");
            return;
        }

        int width = base.getWidth();
        int height = base.getHeight();

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(base, 0, 0, null);

        Font font;

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(Utils.getInputStreamFromBotJar("Roboto-Regular.ttf")));
        } catch (FontFormatException | IOException | NullPointerException e) {
            Main.LOGGER.severe(e.getMessage());
            font = Font.getFont("Arial");
        }

        font = font.deriveFont(Font.BOLD, 45);

        // START DRAWING TEXT

        // Name of User 0
        g2d.setColor(Color.BLUE);
        g2d.setFont(font);
        String text0 = user0.getAsTag();
        int text0_x = 50;
        int text0_y = 690;
        g2d.drawString(text0, text0_x, text0_y);

        // Name of User 1
        g2d.setColor(Color.BLUE);
        g2d.setFont(font);
        String text1 = user1.getAsTag();
        int text1_x = 810;
        int text1_y = 690;
        g2d.drawString(text1, text1_x, text1_y);

        // Percentage
        g2d.setColor(Color.WHITE);
        if(percentage == 69) {
            g2d.setFont(font.deriveFont(Font.BOLD, 60));
        } else {
            g2d.setFont(font);
        }
        FontMetrics fm = g2d.getFontMetrics();
        String ship_percent = percentage+"%";
        int ship_x = (img.getWidth() / 2) - (fm.getStringBounds(ship_percent, g2d).getBounds().width / 2);
        int ship_y = ((img.getHeight() / 2) + (fm.getStringBounds(ship_percent, g2d).getBounds().height / 2));
        g2d.drawString(ship_percent, ship_x, ship_y);

        // DONE DRAWING TEXT
        // START DRAWING PROFILE PICTURES

        String avatar0_url = Utils.getAvatar(user0)+"?size=512";
        BufferedImage avatar0;
        try {
            avatar0 = ImageIO.read(NetUtils.getStreamFromUrl(avatar0_url));
            avatar0 = Utils.resizeImage(avatar0, 512, 512);
            avatar0 = imageToCircle(avatar0);
        } catch (IOException e) {
            Main.LOGGER.severe("Could not load avatar0!");
            e.printStackTrace();
            return;
        }

        g2d.drawImage(avatar0, 47, 133, null);

        String avatar1_url = Utils.getAvatar(user1)+"?size=512";
        BufferedImage avatar1;
        try {
            avatar1 = ImageIO.read(NetUtils.getStreamFromUrl(avatar1_url));
            avatar1 = Utils.resizeImage(avatar1, 512, 512);
            avatar1 = imageToCircle(avatar1);
        } catch (IOException e) {
            Main.LOGGER.severe("Could not load avatar1!");
            e.printStackTrace();
            return;
        }
        g2d.drawImage(avatar1, 805, 133, null);

        // FINISH RENDERING AND SEND IMAGE
        g2d.dispose();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", out);
        } catch (IOException e) {
            Main.LOGGER.severe("Could not render image!");
            return;
        }
        InputStream is = new ByteArrayInputStream(out.toByteArray());
        msg.getChannel().sendFile(is, "ship.png").complete();
    }

    @Override
    public String getName() {
        return "ship";
    }

    @Override
    public String getDescription() {
        return "Ships two users";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public Permission getPermission() {
        return null;
    }

    private boolean isShipped(long[] ship) {
        Iterator<Map.Entry<long[], long[]>> it = ships.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<long[], long[]> pair = it.next();
            long[] ids = pair.getKey();

            int matches = 0;
            for (int i = 0; i < ids.length; i++) {
                if(ids[i] == ship[i]) {
                    matches++;
                }
            }
            if (matches == ids.length) {
                if(System.currentTimeMillis() - pair.getValue()[0] > 43200000L) { // cache ships for 12 hours
                    it.remove();
                    ships.remove(pair.getKey());
                    return false;
                }
                return true;
            }
            it.remove();
        }
        return false;
    }

    private int getShip(long[] ship) {
        Iterator<Map.Entry<long[], long[]>> it = ships.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<long[], long[]> pair = it.next();
            long[] ids = pair.getKey();

            int matches = 0;
            for (int i = 0; i < ids.length; i++) {
                if(ids[i] == ship[i]) {
                    matches++;
                }
            }
            if (matches == ids.length) {
                return Math.toIntExact(pair.getValue()[1]);
            }
            it.remove();
        }
        return -1;
    }

    private BufferedImage imageToCircle(BufferedImage in) {
        int width = in.getWidth();
        int height = in.getHeight();
        BufferedImage circleBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circleBuffer.createGraphics();
        int cutout = 4;
        g2.setClip(new Ellipse2D.Float(cutout, cutout, width-cutout, width-cutout));
        g2.drawImage(in, cutout/2, cutout/2, width, height, null);
        g2.dispose();

        return circleBuffer;
    }
}
