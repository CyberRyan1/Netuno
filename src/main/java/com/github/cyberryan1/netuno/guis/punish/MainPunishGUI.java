package com.github.cyberryan1.netuno.guis.punish;

import com.github.cyberryan1.cybercore.CyberCore;
import com.github.cyberryan1.cybercore.helpers.gui.GUI;
import com.github.cyberryan1.cybercore.helpers.gui.GUIItem;
import com.github.cyberryan1.cybercore.utils.CoreGUIUtils;
import com.github.cyberryan1.cybercore.utils.VaultUtils;
import com.github.cyberryan1.netuno.guis.history.HistoryListGUI;
import com.github.cyberryan1.netuno.guis.ipinfo.AltsListGUI;
import com.github.cyberryan1.netuno.guis.punish.utils.MainButton;
import com.github.cyberryan1.netuno.guis.punish.utils.PunishSettings;
import com.github.cyberryan1.netuno.managers.StaffPlayerPunishManager;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class MainPunishGUI {

    private final GUI gui;
    private final Player staff;
    private final OfflinePlayer target;

    public MainPunishGUI( Player staff, OfflinePlayer target ) {
        this.staff = staff;
        this.target = target;

        this.gui = new GUI( PunishSettings.MAIN_INVENTORY_NAME.coloredString().replace( "[TARGET]", target.getName() ),
                5, CoreGUIUtils.getBackgroundGlass() );
        insertItems();
    }

    public void insertItems() {
        // items are defined in the config.yml file

        MainButton skull = PunishSettings.MAIN_SKULL_BUTTON.mainButton();
        if ( skull.getIndex() != -1 ) {
            gui.setItem( skull.getIndex(), new GUIItem( skull.getItem( this.target ), skull.getIndex() ) );
        }

        MainButton history = PunishSettings.MAIN_HISTORY_BUTTON.mainButton();
        if ( history.getIndex() != -1 ) {
            gui.setItem( history.getIndex(), new GUIItem( history.getItem( this.target ), history.getIndex(), () -> {
                staff.closeInventory();
                HistoryListGUI historyList = new HistoryListGUI( this.target, this.staff, 1 );
                historyList.open();
            } ) );
        }

        MainButton alts = PunishSettings.MAIN_ALTS_BUTTON.mainButton();
        if ( alts.getIndex() != -1 ) {
            gui.setItem( alts.getIndex(), new GUIItem( alts.getItem( this.target ), alts.getIndex(), () -> {
                staff.closeInventory();
                AltsListGUI altsList = new AltsListGUI( this.staff, this.target, 1 );
                altsList.open();
            } ) );
        }

        StaffPlayerPunishManager.setStaffSilent( this.staff, false );
        final MainButton silent = ( VaultUtils.hasPerms( this.staff, Settings.SILENT_PERMISSION.string() ) ) ?
                PunishSettings.MAIN_SILENT_DISABLED_BUTTON.mainButton() :
                PunishSettings.MAIN_SILENT_NO_PERMS_BUTTON.mainButton();
        if ( silent.getIndex() != -1 ) {
            gui.setItem( silent.getIndex(), new GUIItem( silent.getItem( this.target ), silent.getIndex(),
                    ( guiItem ) -> {
                if ( guiItem.getType() == PunishSettings.MAIN_SILENT_NO_PERMS_BUTTON.mainButton().getItem( this.target ).getType() ) {
                    return;
                }

                if ( guiItem.getType() == PunishSettings.MAIN_SILENT_DISABLED_BUTTON.mainButton().getItem( this.target ).getType() ) {
                    guiItem.setItem( PunishSettings.MAIN_SILENT_ENABLED_BUTTON.mainButton().getItem( this.target ) );
                    StaffPlayerPunishManager.setStaffSilent( this.staff, true );
                }
                else {
                    guiItem.setItem( PunishSettings.MAIN_SILENT_DISABLED_BUTTON.mainButton().getItem( this.target ) );
                    StaffPlayerPunishManager.setStaffSilent( this.staff, false );
                }

                gui.updateItem( guiItem );
            } ) );
        }

        MainButton warn = PunishSettings.MAIN_WARN_BUTTON.mainButton();
        if ( warn.getIndex() != -1 ) {
            gui.setItem( warn.getIndex(), new GUIItem( warn.getItem( this.target ), warn.getIndex(), () -> {
                staff.closeInventory();
                WarnPunishGUI g = new WarnPunishGUI( this.staff, this.target );
                g.open();
                StaffPlayerPunishManager.addStaffTarget( this.staff, this.target );
            } ) );
        }

        MainButton mute = PunishSettings.MAIN_MUTE_BUTTON.mainButton();
        if ( mute.getIndex() != -1 ) {
            gui.setItem( mute.getIndex(), new GUIItem( mute.getItem( this.target ), mute.getIndex(), () -> {
                staff.closeInventory();
                MutePunishGUI g = new MutePunishGUI( this.staff, this.target );
                g.open();
                StaffPlayerPunishManager.addStaffTarget( this.staff, this.target );
            } ) );
        }

        MainButton ban = PunishSettings.MAIN_BAN_BUTTON.mainButton();
        if ( ban.getIndex() != -1 ) {
            gui.setItem( ban.getIndex(), new GUIItem( ban.getItem( this.target ), ban.getIndex(), () -> {
                staff.closeInventory();
                BanPunishGUI g = new BanPunishGUI( this.staff, this.target );
                g.open();
                StaffPlayerPunishManager.addStaffTarget( this.staff, this.target );
            } ) );
        }

        MainButton ipmute = PunishSettings.MAIN_IPMUTE_BUTTON.mainButton();
        if ( ipmute.getIndex() != -1 ) {
            gui.setItem( ipmute.getIndex(), new GUIItem( ipmute.getItem( this.target ), ipmute.getIndex(), () -> {
                staff.closeInventory();
                IpMutePunishGUI g = new IpMutePunishGUI( this.staff, this.target );
                g.open();
                StaffPlayerPunishManager.addStaffTarget( this.staff, this.target );
            } ) );
        }

        MainButton ipban = PunishSettings.MAIN_IPBAN_BUTTON.mainButton();
        if ( ipban.getIndex() != -1 ) {
            gui.setItem( ipban.getIndex(), new GUIItem( ipban.getItem( this.target ), ipban.getIndex(), () -> {
                staff.closeInventory();
                IpBanPunishGUI g = new IpBanPunishGUI( this.staff, this.target );
                g.open();
                StaffPlayerPunishManager.addStaffTarget( this.staff, this.target );
            } ) );
        }

        gui.createInventory();
    }

    public void open() {
        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
            gui.openInventory( this.staff );
            gui.setCloseAction( () -> {
                Bukkit.getScheduler().runTaskLaterAsynchronously( CyberCore.getPlugin(), () -> {
                    if ( StaffPlayerPunishManager.containsStaff( this.staff ) == false
                            && StaffPlayerPunishManager.containsStaffSilent( this.staff ) ) {
                        StaffPlayerPunishManager.removeStaffSilent( this.staff );
                    }
                }, 4L );
            } );
        } );
    }
}