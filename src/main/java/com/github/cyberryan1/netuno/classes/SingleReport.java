package com.github.cyberryan1.netuno.classes;

import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netuno.utils.Time;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class SingleReport {

    private OfflinePlayer target;
    private OfflinePlayer reporter;
    private long date;
    private String reason;

    public SingleReport( OfflinePlayer target, OfflinePlayer reporter, String reason ) {
        this.target = target;
        this.reporter = reporter;
        this.date = Time.getCurrentTimestamp();
        this.reason = reason;
    }

    public SingleReport() {
        this.target = null;
        this.reporter = null;
        this.date = Time.getCurrentTimestamp();
        this.reason = null;
    }

    public OfflinePlayer getTarget() { return target; }

    public OfflinePlayer getReporter() { return reporter; }

    public long getDate() { return date; }

    public String getReason() { return reason; }

    public void setTarget( OfflinePlayer target ) { this.target = target; }

    public void setReporter( OfflinePlayer reporter ) { this.reporter = reporter; }

    public void setDate( Long date ) { this.date = date; }

    public void setReason( String reason ) { this.reason = reason; }

    public ItemStack getAsItem() {
        ItemStack skull = GUIUtils.getPlayerSkull( target );

        skull = GUIUtils.setItemName( skull, "&p" + target.getName() );
        ArrayList<String> lore = new ArrayList<>();
        lore.add( CoreUtils.getColored( "&pDate: &s" + Time.getDateFromTimestamp( date ) ) );
        lore.add( CoreUtils.getColored( "&pReporter: &s" + reporter.getName() ) );
        lore.add( CoreUtils.getColored( "&pReason: &s" + reason ) );

        return GUIUtils.setItemLore( skull, lore );
    }
}
