package com.github.cyberryan1.netuno.classes;

import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netuno.utils.Time;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

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
        ItemStack skull = new ItemStack( Material.PLAYER_HEAD );
        SkullMeta meta = ( SkullMeta ) skull.getItemMeta();
        meta.setOwningPlayer( reporter );
        skull.setItemMeta( meta );

        skull = GUIUtils.setItemName( skull, "&6Reporter: &7" + reporter.getName() );

        skull = GUIUtils.addItemLore( skull, "&6Date: &7" + Time.getDateFromTimestamp( date ), "&6Reason(s):" );
        for ( String r : getReasons() ) {
            skull = GUIUtils.addItemLore( skull, " &8- &7" + r );
        }

        return skull;
    }
}
