package com.github.cyberryan1.netuno.utils.yml;

import com.github.cyberryan1.cybercore.spigot.config.YmlLoader;
import com.github.cyberryan1.cybercore.spigot.config.YmlReader;

public class BanPunishUtils extends YmlReader {

    public BanPunishUtils() {
        super( new YmlLoader(  "guis/ban_punish_gui.yml", "ban_punish_gui_default.yml" ) );
    }
}