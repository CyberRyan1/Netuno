package com.github.cyberryan1.netuno.classes;

import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netuno.utils.Time;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class CombinedReport {

    private final Database DATA = Utils.getDatabase();

    private OfflinePlayer target;
    private ArrayList<Report> reports;
    private long mostRecentDate;

    public CombinedReport( OfflinePlayer target ) {
        this.target = target;

        reports = DATA.getReport( target.getUniqueId().toString() );
        if ( reports.size() > 0 ) {
            mostRecentDate = reports.get( 0 ).getDate();
            for ( Report r : reports ) {
                if ( r.getDate() > mostRecentDate ) { mostRecentDate = r.getDate(); }
            }
        }
    }

    public OfflinePlayer getTarget() { return target; }

    public ArrayList<Report> getAllReports() { return reports; }

    public long getMostRecentDate() { return mostRecentDate; }

    public ItemStack getAsItem() {
        ItemStack skull = new ItemStack( Material.PLAYER_HEAD );
        SkullMeta meta = ( SkullMeta ) skull.getItemMeta();
        meta.setOwningPlayer( target );
        skull.setItemMeta( meta );

        skull = GUIUtils.setItemName( skull, "&6" + target.getName() );

        //      Reason, Amount
        HashMap<String, Integer> reasonAmount = new HashMap<>();
        for ( Report r : reports ) {
            if ( reasonAmount.containsKey( r.getReason() ) ) {
                int amount = reasonAmount.get( r.getReason() ) + 1;
                reasonAmount.replace( r.getReason(), amount );
            }
            else {
                reasonAmount.put( r.getReason(), 1 );
            }
        }

        ArrayList<String> reasons = new ArrayList<>();
        for ( String rea : reasonAmount.keySet() ) {
            int amount = reasonAmount.get( rea );
            reasons.add( Utils.getColored( " &8- &6" + amount + "x &7" + rea ) );
        }

        ArrayList<String> lore = new ArrayList<>();
        lore.add( Utils.getColored( "&6Date: &7" + Time.getDateFromTimestamp( mostRecentDate ) ) );
        lore.add( Utils.getColored( "&6Reason(s):" ) );
        lore.addAll( reasons );

        return GUIUtils.setItemLore( skull, lore );
    }

}
