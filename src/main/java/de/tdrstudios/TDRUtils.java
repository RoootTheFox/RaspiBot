package de.tdrstudios;

/**
 * This Utils are written by /TDRMinecraft
 */
public class TDRUtils {
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
}
