package com.github.cyberryan1.netuno.utils.yml;

import com.github.cyberryan1.cybercore.spigot.config.YmlLoader;
import com.github.cyberryan1.cybercore.spigot.config.YmlReader;

public class MainPunishUtils extends YmlReader {

    public MainPunishUtils() {
        super( new YmlLoader( "guis/main_punish_gui.yml", "main_punish_gui_default.yml" ) );
    }
}