package com.github.cyberryan1.netuno.utils.yml;

import com.github.cyberryan1.cybercore.managers.YmlManager;
import com.github.cyberryan1.cybercore.utils.yml.YMLReadTemplate;

public class WarnPunishUtils extends YMLReadTemplate {

    public WarnPunishUtils() {
        super.setYMLManager( new YmlManager( "guis/warn_punish_gui.yml", "warn_punish_gui_default.yml" ) );
    }
}