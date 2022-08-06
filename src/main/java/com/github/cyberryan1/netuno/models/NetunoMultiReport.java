package com.github.cyberryan1.netuno.models;

import com.github.cyberryan1.cybercore.utils.CoreItemUtils;
import com.github.cyberryan1.netunoapi.models.reports.NReport;
import com.github.cyberryan1.netunoapi.models.time.NDate;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NetunoMultiReport {

    private final OfflinePlayer target;
    private final OfflinePlayer reporter;
    private final long date;

    private List<NReport> reports;

    // Represents multiple reports for the same target, from the same reporter, and have the EXACT same timestamp
    public NetunoMultiReport( List<NReport> reports ) {
        this.reports = reports;

        if ( reports.size() == 0 ) {
            this.target = null;
            this.reporter = null;
            this.date = -1L;
        }
        else {
            this.target = reports.get( 0 ).getPlayer();
            this.reporter = reports.get( 0 ).getReporter();
            this.date = reports.get( 0 ).getTimestamp();
        }
    }

    public OfflinePlayer getTarget() { return target; }

    public OfflinePlayer getReporter() { return reporter; }

    public long getDate() { return date; }

    public List<NReport> getReports() { return reports; }

    public List<String> getReasons() {
        List<String> toReturn = new ArrayList<>();
        for( NReport nr : reports ) {
            toReturn.add( nr.getReason() );
        }
        return toReturn;
    }

    public ItemStack getAsItem() {
        ItemStack skull = CoreItemUtils.getPlayerSkull( reporter );

        skull = CoreItemUtils.setItemName( skull, "&pReporter: &s" + reporter.getName() );

        skull = CoreItemUtils.addItemLore( skull, "&pDate: &s" + new NDate( date ).getDateString(), "&pReason(s):" );
        for ( String r : getReasons() ) {
            skull = CoreItemUtils.addItemLore( skull, " &8- &s" + r );
        }

        return skull;
    }
}
