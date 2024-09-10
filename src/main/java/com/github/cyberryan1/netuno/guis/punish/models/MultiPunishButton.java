package com.github.cyberryan1.netuno.guis.punish.models;

import java.util.ArrayList;
import java.util.List;

// TODO javadoc
public class MultiPunishButton {

    private String guiPath;

    private List<SinglePunishButton> buttons = new ArrayList<>();

    public MultiPunishButton( String guiPath, String ymlName ) {
        this.guiPath = guiPath;

        for ( int index = 10; index <= 34; index++ ) {
            // Skipping the first, middle, and last columns in the GUI
            if ( index % 9 == 0 || index % 9 == 4 || index % 9 == 8 ) { continue; }

            buttons.add( new SinglePunishButton( guiPath + "." + index, ymlName ) );
        }
    }

    public List<SinglePunishButton> getButtons() {
        return buttons;
    }
}