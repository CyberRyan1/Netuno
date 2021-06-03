package com.github.cyberryan1.netuno.utils;

import java.util.ArrayList;

public class IPPunishment extends Punishment {

    private ArrayList<String> altList = new ArrayList<>();

    public IPPunishment() {
        super();
    }

    public ArrayList<String> getAltList() { return altList; }

    public void addAlt( String altUUID ) { altList.add( altUUID ); }

    public void setAltList( ArrayList<String> alts ) { altList = alts; }

    public String getAltListAsString() { return Utils.formatListIntoString( ( String[] ) altList.toArray() ); }

    public void setAltListFromString( String alts ) {
        String split[] = alts.split( ", " );
        for ( String uuid : split ) {
            addAlt( uuid );
        }
    }
}
