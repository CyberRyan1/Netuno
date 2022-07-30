package com.github.cyberryan1.netuno.guis.punish.utils;

import com.github.cyberryan1.netuno.utils.yml.YMLUtils;

import java.util.ArrayList;
import java.util.List;

public class MultiPunishButton {

    private String guiPath;

    private List<SinglePunishButton> buttons = new ArrayList<>();

    public MultiPunishButton( String guiPath ) {
        this.guiPath = guiPath;

        for ( String key : YMLUtils.getConfig().getKeys( guiPath + "." ) ) {
            if ( key.equalsIgnoreCase( guiPath + ".inventory_name" ) || key.equalsIgnoreCase( guiPath + ".permission" ) ) { continue; }
            buttons.add( new SinglePunishButton( key ) );
        }
    }

    public List<SinglePunishButton> getButtons() {
        return buttons;
    }
}