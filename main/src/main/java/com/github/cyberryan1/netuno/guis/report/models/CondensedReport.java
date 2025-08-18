package com.github.cyberryan1.netuno.guis.report.models;

import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiReport;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class CondensedReport {

    private final OfflinePlayer player;
    private final List<ApiReport> reports;
    private final Map<String, Integer> reasonCounts = new HashMap<>();

    public CondensedReport( UUID player ) {
        this.player = Bukkit.getOfflinePlayer( player );
        this.reports = Netuno.REPORT_SERVICE.getReportsAgainst( player );

        for ( ApiReport report : reports ) {
            List<String> reasonList = report.getReasons();
            for ( String reason : reasonList ) {
                reasonCounts.put( reason, reasonCounts.getOrDefault( reason, 0 ) + 1 );
            }
        }

        // Sort reasonCounts by value in descending order
        Map<String, Integer> sorted = reasonCounts.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        reasonCounts.clear();
        reasonCounts.putAll( sorted );
    }

    public OfflinePlayer getPlayer() { return player; }

    public ItemStack asGuiItem() {
        List<String> sorted = reasonCounts.entrySet()
                .stream()
                .sorted( Map.Entry.<String, Integer>comparingByValue( Comparator.reverseOrder() ) )
                .map( entry -> "&p" + entry.getValue() + "x &s" + entry.getKey() )
                .toList();

        ItemStack item = CyberItemUtils.getPlayerSkull( player );
        String name = "&p" + player.getName() + ( player.isOnline() ? ( " &8(&aonline&8)" ) : ( " &8(&coffline&8)" ) );
        item = CyberItemUtils.setItemName( item, name );

        List<String> lore = new ArrayList<>();
        for ( String str : sorted ) {
            lore.add( CyberColorUtils.getColored( str ) );
        }
        item = CyberItemUtils.setItemLore( item, lore );

        return item;
    }

    public int getReasonCount( String reason ) {
        return reasonCounts.getOrDefault( reason, 0 );
    }
}