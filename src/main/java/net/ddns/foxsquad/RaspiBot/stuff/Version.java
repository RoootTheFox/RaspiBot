package net.ddns.foxsquad.RaspiBot.stuff;

public class Version {
    public int[] v = new int[]{0, 0, 0};
    public VersionType type = VersionType.UNKOWN;

    public Version(String ver) {
        String[] vsa = ver.split("\\.");
        for (int i = 0; i < vsa.length; i++) {
            int p = Integer.parseInt(vsa[i]);
            v[i] = p;
        }
    }

    @SuppressWarnings("unused")
    public int[] getVersion() { return v; }

    public String getVersionString() {
        StringBuilder versionStringBuilder = new StringBuilder();
        for (int i : v) {
            versionStringBuilder.append(i);
            versionStringBuilder.append(".");
        }
        String versionString = versionStringBuilder.toString();
        while(versionString.endsWith(".")) {
            versionString = versionString.substring(0, versionString.length()-1);
        }

        return versionString;
    }

    public String getFullVersionString() {
        StringBuilder versionStringBuilder = new StringBuilder();
        for (int i : v) {
            versionStringBuilder.append(i);
            versionStringBuilder.append(".");
        }

        String versionString = versionStringBuilder.toString();
        while(versionString.endsWith(".")) {
            versionString = versionString.substring(0, versionString.length()-1);
        }

        return versionString+"-"+type;
    }

    public Version setType(VersionType type) {
        this.type = type;
        return this;
    }
}