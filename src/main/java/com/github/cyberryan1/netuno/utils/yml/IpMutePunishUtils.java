package com.github.cyberryan1.netuno.utils.yml;

import com.github.cyberryan1.cybercore.spigot.config.YmlLoader;
import com.github.cyberryan1.cybercore.spigot.config.YmlReader;

public class IpMutePunishUtils extends YmlReader {

    public IpMutePunishUtils() {
        super( new YmlLoader(  "guis/ipmute_punish_gui.yml", "ipmute_punish_gui_default.yml" ) );
    }
}