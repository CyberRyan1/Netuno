package com.github.cyberryan1.netuno.guis.punish.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds all the buttons that can be inserted into a punishment
 * GUI
 *
 * @author Ryan
 */
public class MultiPunishButton {

    private String guiPath;

    private List<SinglePunishButton> buttons = new ArrayList<>();

    /**
     * @param guiPath The path that leads to this GUI within its
     *                config file
     * @param guiType The type of GUI these buttons are for
     */
    public MultiPunishButton( String guiPath, String guiType ) {
        this.guiPath = guiPath;

        for ( int index = 10; index <= 34; index++ ) {
            // Skipping the first, middle, and last columns in the GUI
            if ( index % 9 == 0 || index % 9 == 4 || index % 9 == 8 ) { continue; }

            buttons.add( new SinglePunishButton( guiPath + "." + index, guiType ) );
        }
    }

    public List<SinglePunishButton> getButtons() {
        return buttons;
    }
}