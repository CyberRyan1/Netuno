package com.github.cyberryan1.netuno.utils.yml;

import com.github.cyberryan1.cybercore.managers.YmlManager;
import com.github.cyberryan1.cybercore.utils.yml.YMLReadTemplate;

public class BanPunishUtils extends YMLReadTemplate {

    public BanPunishUtils() {
        super.setYMLManager( new YmlManager( "guis/ban_punish_gui.yml", "ban_punish_gui_default.yml" ) );
    }
}