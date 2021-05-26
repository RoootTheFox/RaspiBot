package net.ddns.rootrobo.RaspiBot.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class NetUtils {
    private static final String[] BROWSERS = new String[]{"Chrome", "Firefox", "Opera"};
    private static final String[] OPERATING_SYSTEMS = new String[]{"Windows NT 10.0", "X11; Ubuntu; Linux x86_x64"};

    public static String getRandomUserAgent() {
        String browser = getRandomBrowser();
        String os = getRandomSystem();
        String browser_webkit = "";
        String browserVersion = "";

        StringBuilder agent = new StringBuilder();
        switch (browser.toUpperCase()) {
            case "FIREFOX": {
                agent.append("Mozilla/5.0");
                browserVersion = "50.0";
                browser_webkit = "AppleWebKit/537.36";
                break;
            }

            case "CHROME": {
                agent.append("Mozilla/5.0");
                browserVersion = "72.0.2626.121";
                browser_webkit = "AppleWebKit/537.36 (KHTML, like Gecko)";
                break;
            }

            case "CHROMIUM": {
                agent.append("Mozilla/5.0");
                browserVersion = "12.0.742.112";
                browser_webkit = "AppleWebKit/537.30 (KHTML, like Gecko)";
                break;
            }

            case "OPERA": {
                agent.append("Opera/9.80");
                browserVersion = "";
                browser_webkit = "Presto/2.12.388 Version/12.18";
                break;
            }

            default: {
                agent.append("Mozilla/5.0");
            }
        };

        // add OS
        agent.append(" (").append(os).append(") ");
        // add Webkit
        agent.append(browser_webkit).append(" ");
        // add Version
        if (!browser.toLowerCase().contains("opera")) {
            agent.append(browser).append("/").append(browserVersion);
        }
        return agent.toString();
    }

    private static String getRandomBrowser() {
        return BROWSERS[new Random().nextInt(BROWSERS.length)];
    }

    private static String getRandomSystem() {
        return OPERATING_SYSTEMS[new Random().nextInt(OPERATING_SYSTEMS.length)];
    }

    public static InputStream getStreamFromUrl(String url) throws IOException {
        URLConnection conn = new URL(url).openConnection();
        conn.setUseCaches(true);
        conn.setRequestProperty("User-Agent", NetUtils.getRandomUserAgent());
        conn.connect();
        return conn.getInputStream();
    }
}
