package com.github.cyberryan1.netuno.utils.yml;

import com.github.cyberryan1.cybercore.managers.YmlManager;
import com.github.cyberryan1.cybercore.utils.yml.YMLReadTemplate;

public class MutePunishUtils extends YMLReadTemplate {

    public MutePunishUtils() {
        super.setYMLManager( new YmlManager( "guis/mute_punish_gui.yml", "mute_punish_gui_default.yml" ) );
    }
}