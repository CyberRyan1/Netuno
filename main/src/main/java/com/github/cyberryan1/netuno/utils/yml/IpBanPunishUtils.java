package com.github.cyberryan1.netuno.utils.yml;

import com.github.cyberryan1.cybercore.spigot.config.YmlLoader;
import com.github.cyberryan1.cybercore.spigot.config.YmlReader;

public class IpBanPunishUtils extends YmlReader {

    public IpBanPunishUtils() {
        super( new YmlLoader(  "guis/ipban_punish_gui.yml", "ipban_punish_gui_default.yml" ) );
    }
}