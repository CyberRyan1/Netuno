package com.github.cyberryan1.netuno.utils.yml;

import com.github.cyberryan1.cybercore.utils.yml.YMLReadTemplate;

public class YMLUtils {

    public static void initializeConfigs() {
        getConfigUtils().getYMLManager().initialize();
        getMainPunishUtils().getYMLManager().initialize();
    }

    public static YMLReadTemplate fromName( String ymlName ) {
        return switch ( ymlName.toLowerCase() ) {
            case "config" -> getConfigUtils();
            case "main" -> getMainPunishUtils();
            case "warn" -> getWarnPunishUtils();
            case "mute" -> getMutePunishUtils();
            case "ban" -> getBanPunishUtils();
            default -> null;
        };
    }

    private static ConfigUtils configUtils = new ConfigUtils();
    public static ConfigUtils getConfigUtils() { return configUtils; }
    public static ConfigUtils getConfig() { return configUtils; }

    private static MainPunishUtils mainPunishUtils = new MainPunishUtils();
    public static MainPunishUtils getMainPunishUtils() { return mainPunishUtils; }
    public static MainPunishUtils getMainGui() { return mainPunishUtils; }

    private static WarnPunishUtils warnPunishUtils = new WarnPunishUtils();
    public static WarnPunishUtils getWarnPunishUtils() { return warnPunishUtils; }
    public static WarnPunishUtils getWarnGui() { return warnPunishUtils; }

    private static MutePunishUtils mutePunishUtils = new MutePunishUtils();
    public static MutePunishUtils getMutePunishUtils() { return mutePunishUtils; }
    public static MutePunishUtils getMuteGui() { return mutePunishUtils; }

    private static BanPunishUtils banPunishUtils = new BanPunishUtils();
    public static BanPunishUtils getBanPunishUtils() { return banPunishUtils; }
    public static BanPunishUtils getBanGui() { return banPunishUtils; }
}
