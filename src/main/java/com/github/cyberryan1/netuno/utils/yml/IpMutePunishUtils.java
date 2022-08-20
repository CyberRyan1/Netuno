package com.github.cyberryan1.netuno.utils.yml;

import com.github.cyberryan1.cybercore.managers.YmlManager;
import com.github.cyberryan1.cybercore.utils.yml.YMLReadTemplate;

public class IpMutePunishUtils extends YMLReadTemplate {

    public IpMutePunishUtils() {
        super.setYMLManager( new YmlManager( "guis/ipmute_punish_gui.yml", "ipmute_punish_gui_default.yml" ) );
    }
}