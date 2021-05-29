package net.ddns.rootrobo.RaspiBot.commands;

import de.tdrstudios.TDRUtils;
import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.config.Config;
import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.ddns.rootrobo.RaspiBot.utils.EmbedUtils;
import net.ddns.rootrobo.RaspiBot.utils.NetUtils;
import net.ddns.rootrobo.RaspiBot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.internal.requests.Route;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("unused")
public class ShipCommand implements Command {
    private static final boolean allowFullPercentage = Config.getInstance().ship_allowfullpercentage;



    ShipRandom random;
    HashMap<long[], long[]> ships = new HashMap<>();
    private int percentage;

    protected int getPercentage() {
        return percentage;
    }

    @Override
    public void run(Message msg, String[] args, Guild guild) throws IOException, ExecutionException, InterruptedException {
        if(msg.getMentionedUsers().size() == 0) {
            msg.getChannel().sendMessage(
                    "Usage: "+Main.PREFIX+getName()+" @user"+"\n"+
                    "or "+Main.PREFIX+getName()+" @User @User").complete();
            return;
        }

        User user0 = msg.getAuthor();
        User user1 = msg.getMentionedUsers().get(0);

        if(msg.getMentionedUsers().size() >= 2) {
            user0 = msg.getMentionedUsers().get(0);
            user1 = msg.getMentionedUsers().get(1);
        }


        System.out.println(user0.getAsTag() + " " + percentage + "% " + user1.getAsTag());

        random = new ShipRandom(user0, user1);

        long id0 = user0.getIdLong();
        long id1 = user1.getIdLong();

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(id0);
        ids.add(id1);
        ids.sort(null);

        if(id0 == id1) {
            EmbedBuilder embedBuilder = EmbedUtils.createMentionEmbedBuilder(Color.RED, user0);
            embedBuilder.setTitle("Error!");
            embedBuilder.appendDescription("You should search for more friends - dont date yourself. That will go wrong! Trust us!");

            msg.getChannel().sendMessage(embedBuilder.build()).queue();
            return;
        }
        if((id0 == msg.getAuthor().getIdLong() && id1 == Main.bot.getSelfUser().getIdLong()) || (id1 == msg.getAuthor().getIdLong() && id0 == Main.bot.getSelfUser().getIdLong())) {
            EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.GREEN).setTitle("I :hearts: you");
            embedBuilder.setThumbnail(Main.bot.getSelfUser().getAvatarUrl());
            embedBuilder.setDescription("Im your Bot and its my task to love you at **every** time!");
            embedBuilder.setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON);
            msg.getChannel().sendMessage(embedBuilder.build()).queue();
            return;
        }
        //EmbedUtils.DebugEmbedBuilder debugEmbedBuilder =  EmbedUtils.getDebugEmbed("ShipInfo:", new EmbedUtils.EmbedField[]{new EmbedUtils.EmbedField("Ship0", user0.getAsMention()), new EmbedUtils.EmbedField("Ship1", user1.getAsMention())});
        //debugEmbedBuilder.send(msg.getChannel());


        long[] ship = new long[]{ids.get(0), ids.get(1)};

        // int percentage; // CodeStream Marker:9357205234
        if(isShipped(ship)) {
            percentage = getShip(ship);
        } else {
            //percentage = random.nextInt(101); // CodeStream Marker:3209741231002
            ships.put(ship, random.nextShip());
            percentage = random.getLastPercentage();
        }

        //percentage = 69;

        InputStream base_stream;
        if(percentage == 69) {
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
            avatar0 = imageToCircle(ImageIO.read(NetUtils.getStreamFromUrl(avatar0_url)));
        } catch (IOException e) {
            Main.LOGGER.severe("Could not load avatar0!");
            e.printStackTrace();
            return;
        }
        avatar0 = Utils.resizeImage(avatar0, BufferedImage.TYPE_INT_ARGB, 512, 512);
        g2d.drawImage(avatar0, 47, 133, null);

        String avatar1_url = Utils.getAvatar(user1)+"?size=512";
        BufferedImage avatar1;
        try {
            avatar1 = imageToCircle(ImageIO.read(NetUtils.getStreamFromUrl(avatar1_url)));
        } catch (IOException e) {
            Main.LOGGER.severe("Could not load avatar1!");
            e.printStackTrace();
            return;
        }
        avatar1 = Utils.resizeImage(avatar1, BufferedImage.TYPE_INT_ARGB, 512, 512);
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
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.ORANGE).setTitle(Main.bot.getSelfUser().getName() + "s Dating Service");
        embedBuilder.setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON);
        File file = new File("temp.png");
        TDRUtils.copyInputStreamToFile(is, file);
        String url = TDRUtils.getBinService().getBinFromFile(file);
        TDRUtils.getBinService().alert("URL: " + url);
        embedBuilder.setImage(url);
      //  msg.getChannel().sendFile(is, "ship.png").complete();
        msg.getChannel().sendMessage(embedBuilder.build()).queue();

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
        BufferedImage circleBuffer = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circleBuffer.createGraphics();
        int cutout = 4;
        g2.setClip(new Ellipse2D.Float(cutout, cutout, width-cutout, width-cutout));
        g2.drawImage(in, cutout/2, cutout/2, width, width, null);
        g2.dispose();

        return circleBuffer;
    }

    protected static class ShipRandom extends Random {

        private User user1;
        private User user2;

        public User getUser1() {
            return user1;
        }

        public User getUser2() {
            return user2;
        }

        protected void setUser1(User user1) {
            this.user1 = user1;
        }
        protected void setUser2(User user2) {
            this.user2 = user2;
        }

        public ShipRandom(User user1, User user2){
            setUser1(user1);
            setUser2(user2);
            super.setSeed(genSeed());
        }


        public long[] nextShip() {
            int percentage;
            if(allowFullPercentage)
             percentage = nextInt(101);
            else {
                percentage = nextInt(100);
                if(percentage == 0)
                    percentage =  1;
            }
            lastPercentage = percentage;
            return new long[]{System.currentTimeMillis(), percentage};
        }

        public long genSeed() {
            long seed = getUser1().getIdLong() * getUser2().getIdLong();
            seed = seed / TDRUtils.getLength(seed); // Make Number smaller...
            return seed;
        }

        private int lastPercentage = 0;

        public int getLastPercentage() {
            return lastPercentage;
        }
    }
}
