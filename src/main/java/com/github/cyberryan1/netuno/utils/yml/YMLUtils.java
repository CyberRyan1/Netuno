package com.github.cyberryan1.netuno.utils.yml;

public class YMLUtils {

    public static void initializeConfigs() {
        getConfigUtils().getYMLManager().initialize();
        getMainPunishUtils().getYMLManager().initialize();
    }

    private static ConfigUtils configUtils = new ConfigUtils();
    public static ConfigUtils getConfigUtils() { return configUtils; }
    public static ConfigUtils getConfig() { return configUtils; }

    private static MainPunishUtils mainPunishUtils = new MainPunishUtils();
    public static MainPunishUtils getMainPunishUtils() { return mainPunishUtils; }
    public static MainPunishUtils getMainGui() { return mainPunishUtils; }
}
