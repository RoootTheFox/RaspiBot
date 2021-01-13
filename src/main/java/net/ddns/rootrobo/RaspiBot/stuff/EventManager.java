package net.ddns.rootrobo.RaspiBot.stuff;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.log.LogFormatter;
import net.dv8tion.jda.api.JDABuilder;

public class EventManager {
    private static int eventcount = 0;
    public static void registerEvents(JDABuilder jda) {
        String pkg = "net.ddns.rootrobo.RaspiBot.events";
        try (ScanResult scanResult =
                     new ClassGraph()
                             .acceptPackages(pkg)
                             .enableAllInfo()
                             .scan()) {
            for (ClassInfo classInfo : scanResult.getAllClasses()) {
                String eventType;
                eventType = classInfo.getMethodInfo().get(0).getTypeDescriptor().toString();
                eventType = eventType.substring(eventType.indexOf("("));
                eventType = eventType.substring(eventType.lastIndexOf(".")+1);
                eventType = eventType.substring(0, eventType.lastIndexOf(")"));

                @SuppressWarnings("unchecked") // every class in the event package extends ListenerAdapter
                Class<? extends net.dv8tion.jda.api.hooks.ListenerAdapter> event = (Class<? extends net.dv8tion.jda.api.hooks.ListenerAdapter>) classInfo.loadClass();

                try {
                    jda.addEventListeners(event.newInstance());
                    Main.LOGGER.info(LogFormatter.ANSI_GREEN+"Successfully registered event: " + eventType + " ("+classInfo.getMethodInfo().get(0).getName()+")");
                    eventcount++;
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static int getEventCount() {
        return eventcount;
    }
}
