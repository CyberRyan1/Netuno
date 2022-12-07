package com.github.cyberryan1.netuno.utils.yml;

import com.github.cyberryan1.cybercore.spigot.config.YmlLoader;
import com.github.cyberryan1.cybercore.spigot.config.YmlReader;

public class MutePunishUtils extends YmlReader {

    public MutePunishUtils() {
        super( new YmlLoader(  "guis/mute_punish_gui.yml", "mute_punish_gui_default.yml" ) );
    }
}