package com.github.cyberryan1.netuno.utils.yml;

import com.github.cyberryan1.cybercore.managers.YmlManager;
import com.github.cyberryan1.cybercore.utils.yml.YMLReadTemplate;

public class IpBanPunishUtils extends YMLReadTemplate {

    public IpBanPunishUtils() {
        super.setYMLManager( new YmlManager( "guis/ipban_punish_gui.yml", "ipban_punish_gui_default.yml" ) );
    }
}