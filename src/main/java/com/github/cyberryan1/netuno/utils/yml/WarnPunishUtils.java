package com.github.cyberryan1.netuno.utils.yml;

import com.github.cyberryan1.cybercore.spigot.config.YmlLoader;
import com.github.cyberryan1.cybercore.spigot.config.YmlReader;

public class WarnPunishUtils extends YmlReader {

    public WarnPunishUtils() {
        super( new YmlLoader(  "guis/warn_punish_gui.yml", "warn_punish_gui_default.yml" ) );
    }
}