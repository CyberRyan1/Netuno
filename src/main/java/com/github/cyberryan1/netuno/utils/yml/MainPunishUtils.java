package com.github.cyberryan1.netuno.utils.yml;

import com.github.cyberryan1.cybercore.managers.YmlManager;
import com.github.cyberryan1.cybercore.utils.yml.YMLReadTemplate;

public class MainPunishUtils extends YMLReadTemplate {

    public MainPunishUtils() {
        setYMLManager( new YmlManager( "guis/main_punish_gui.yml", "main_punish_gui_default.yml" ) );
    }
}