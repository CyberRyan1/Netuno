package com.github.cyberryan1.netuno.models;

import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netunoapi.models.reports.NReport;
import com.github.cyberryan1.netunoapi.models.time.NDate;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NetunoCombinedReport {

    private OfflinePlayer target;
    private List<NReport> reports;
    private long mostRecentDate;

    public NetunoCombinedReport( OfflinePlayer target ) {
        this.target = target;

        reports = ApiNetuno.getData().getNetunoReports().getReports( target );
        if ( reports.size() > 0 ) {
            mostRecentDate = reports.get( 0 ).getTimestamp();
            for ( NReport nr : reports ) {
                if ( nr.getTimestamp() > mostRecentDate ) { mostRecentDate = nr.getTimestamp(); }
            }
        }
    }

    public OfflinePlayer getTarget() { return target; }

    public List<NReport> getAllReports() { return reports; }

    public long getMostRecentDate() { return mostRecentDate; }

    public ItemStack getAsItem() {
        ItemStack skull = CyberItemUtils.getPlayerSkull( target );

        skull = CyberItemUtils.setItemName( skull, "&p" + target.getName() );

        //      Reason, Amount
        HashMap<String, Integer> reasonAmount = new HashMap<>();
        for ( NReport nr : reports ) {
            if ( reasonAmount.containsKey( nr.getReason() ) ) {
                int amount = reasonAmount.get( nr.getReason() ) + 1;
                reasonAmount.replace( nr.getReason(), amount );
            }
            else {
                reasonAmount.put( nr.getReason(), 1 );
            }
        }

        List<String> reasons = new ArrayList<>();
        for ( String rea : reasonAmount.keySet() ) {
            int amount = reasonAmount.get( rea );
            reasons.add( CyberColorUtils.getColored( " &8- &p" + amount + "x &s" + rea ) );
        }

        ArrayList<String> lore = new ArrayList<>();
        lore.add( CyberColorUtils.getColored( "&pDate: &s" + new NDate( mostRecentDate ).getDateString() ) );
        lore.add( CyberColorUtils.getColored( "&pReason(s):" ) );
        lore.addAll( reasons );

        return CyberItemUtils.setItemLore( skull, lore );
    }

}
