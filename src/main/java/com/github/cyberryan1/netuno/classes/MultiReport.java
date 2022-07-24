package com.github.cyberryan1.netuno.classes;

import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netuno.utils.Time;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MultiReport {

    private final OfflinePlayer target;
    private final OfflinePlayer reporter;
    private final long date;

    private ArrayList<SingleReport> reports;

    // Represents multiple reports for the same target, from the same reporter, and have the EXACT same timestamp
    public MultiReport( ArrayList<SingleReport> reports ) {
        this.reports = reports;

        if ( reports.size() == 0 ) {
            this.target = null;
            this.reporter = null;
            this.date = -1L;
        }
        else {
            this.target = reports.get( 0 ).getTarget();
            this.reporter = reports.get( 0 ).getReporter();
            this.date = reports.get( 0 ).getDate();
        }
    }

    public OfflinePlayer getTarget() { return target; }

    public OfflinePlayer getReporter() { return reporter; }

    public long getDate() { return date; }

    public ArrayList<SingleReport> getReports() { return reports; }

    public ArrayList<String> getReasons() {
        ArrayList<String> toReturn = new ArrayList<>();
        for( SingleReport sr : reports ) {
            toReturn.add( sr.getReason() );
        }
        return toReturn;
    }

    public ItemStack getAsItem() {
        ItemStack skull = GUIUtils.getPlayerSkull( reporter );

        skull = GUIUtils.setItemName( skull, "&pReporter: &s" + reporter.getName() );

        skull = GUIUtils.addItemLore( skull, "&pDate: &s" + Time.getDateFromTimestamp( date ), "&pReason(s):" );
        for ( String r : getReasons() ) {
            skull = GUIUtils.addItemLore( skull, " &8- &s" + r );
        }

        return skull;
    }
}
