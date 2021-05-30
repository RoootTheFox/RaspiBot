package de.tdrstudios.jarargs;

/**
 * This is for a future patch by /TDRMinecraft
 */
public class JarArgument {

    protected JarArgument(String s) {
        argument = s;
    }
    private static String argument;
    public static String getArgument() {
        return argument;
    }

    // TODO: 30.05.2021

    @Override
    public String toString() {
        return argument;
    }
}
