package com.github.cyberryan1.netuno.classes;

import com.github.cyberryan1.netuno.utils.Utils;

import java.util.ArrayList;

public class IPPunishment extends Punishment {

    private ArrayList<String> altList = new ArrayList<>();

    public IPPunishment() {
        super();
    }

    public ArrayList<String> getAltList() { return altList; }

    public void addAlt( String altUUID ) { altList.add( altUUID ); }

    public void setAltList( ArrayList<String> alts ) { altList = alts; }

    public String getAltListAsString() {
        String toReturn[] = new String[ altList.size() ];
        return Utils.formatListIntoString( altList.toArray( toReturn ) );
    }

    public void setAltListFromString( String alts ) {
        String split[] = alts.split( ", " );
        for ( String uuid : split ) {
            addAlt( uuid );
        }
    }
}
