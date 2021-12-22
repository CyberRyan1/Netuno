package com.github.cyberryan1.netuno.guis.punish;

import com.github.cyberryan1.netuno.guis.events.GUIEventInterface;
import com.github.cyberryan1.netuno.guis.events.GUIEventManager;
import com.github.cyberryan1.netuno.guis.events.GUIEventType;
import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netuno.managers.StaffPlayerPunishManager;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class MainPunishGUI {

    private final Inventory gui;
    private final Player staff;
    private final OfflinePlayer target;
    private final String guiName;

    public MainPunishGUI( Player staff, OfflinePlayer target ) {
        this.staff = staff;
        this.target = target;

        this.guiName = ConfigUtils.getColoredStr( "main-gui.inventory-name" ).replace( "[TARGET]", target.getName() );
        gui = Bukkit.createInventory( null, 45, guiName );
        insertItems();
    }

    public void insertItems() {
        // background glass: everywhere
        // items: defined in punishgui.yml file

        ItemStack items[] = new ItemStack[45];
        for ( int index = 0; index < items.length; index++ ) { items[index] = GUIUtils.getBackgroundGlass(); }

        if ( ConfigUtils.getInt( "main-gui.skull.index" ) != -1 ) {
            int index = ConfigUtils.getInt( "main-gui.skull.index" );
            ItemStack skull = new ItemStack( Material.PLAYER_HEAD );
            SkullMeta meta = ( SkullMeta ) skull.getItemMeta();
            meta.setOwningPlayer( target );
            skull.setItemMeta( meta );
            String name = ConfigUtils.getColoredStr( "main-gui.skull.name" );
            items[index] = GUIUtils.setItemName( skull, name.replace( "[TARGET]", target.getName() ) );
        }

        String buttons[] = { "history", "alts", "warn", "mute", "ban", "ipmute", "ipban" };
        for ( String bu : buttons ) {
            int index = ConfigUtils.getInt( "main-gui." + bu + ".index" );
            if ( index >= 0 && index < items.length ) {
                String name = ConfigUtils.getColoredStr( "main-gui." + bu + ".name" );
                ItemStack item = new ItemStack( Material.matchMaterial( ConfigUtils.getStr( "main-gui." + bu + ".item" ) ) );
                items[index] = GUIUtils.setItemName( item, name.replace( "[TARGET]", target.getName() ) );
            }
        }

        gui.setContents( items );
    }

    public void openInventory() {
        if ( StaffPlayerPunishManager.getWhoPunishingTarget( target ) != null ) {
            Player otherStaff = StaffPlayerPunishManager.getWhoPunishingTarget( target );
            if ( otherStaff.equals( staff ) == false ) {
                CommandErrors.sendTargetAlreadyBeingPunished( staff, target.getName(), otherStaff.getName() );
            }
        }

        StaffPlayerPunishManager.addStaffTarget( staff, target );
        staff.openInventory( gui );
        GUIEventManager.addEvent( this );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLICK )
    public void onInventoryClick( InventoryClickEvent event ) {
        if ( staff.getName().equals( event.getWhoClicked().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( guiName ) == false ) { return; }

        event.setCancelled( true );
        if ( event.getClickedInventory() == null || event.getClickedInventory().getType() == InventoryType.PLAYER ) { return; }

        ItemStack item = event.getCurrentItem();
        if ( item == null || item.getType().isAir() ) { return; }
        int eventSlot = event.getSlot();

        if ( eventSlot == ConfigUtils.getInt( "main-gui.history.index" ) ) {
            staff.chat( "/history list " + target.getName() );
        }

        else if ( eventSlot == ConfigUtils.getInt( "main-gui.alts.index" ) ) {
            staff.chat( "/ipinfo " + target.getName() );
        }

        else if ( eventSlot == ConfigUtils.getInt( "main-gui.warn.index" ) ) {
            WarnPunishGUI gui = new WarnPunishGUI( staff, target );
            gui.openInventory();
        }

        else if ( eventSlot == ConfigUtils.getInt( "main-gui.mute.index" ) ) {
            MutePunishGUI gui = new MutePunishGUI( staff, target );
            gui.openInventory();
        }

        else if ( eventSlot == ConfigUtils.getInt( "main-gui.ban.index" ) ) {
            BanPunishGUI gui = new BanPunishGUI( staff, target );
            gui.openInventory();
        }

        else if ( eventSlot == ConfigUtils.getInt( "main-gui.ipmute.index" ) ) {
            IPMutePunishGUI gui = new IPMutePunishGUI( staff, target );
            gui.openInventory();
        }

        else if ( eventSlot == ConfigUtils.getInt( "main-gui.ipban.index" ) ) {
            IPBanPunishGUI gui = new IPBanPunishGUI( staff, target );
            gui.openInventory();
        }

        staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 10, 2 );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_DRAG )
    public void onInventoryDrag( InventoryDragEvent event ) {
        if ( staff.getName().equals( event.getWhoClicked().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( guiName ) == false ) { return; }
        event.setCancelled( true );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLOSE )
    public void onInventoryClose( InventoryCloseEvent event ) {
        if ( staff.getName().equals( event.getPlayer().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( guiName ) == false ) { return; }

        GUIEventManager.removeEvent( this );
        StaffPlayerPunishManager.removeStaff( staff );
    }
}
