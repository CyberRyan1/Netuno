package com.github.cyberryan1.netuno.guis.punish;

import com.github.cyberryan1.cybercore.CyberCore;
import com.github.cyberryan1.cybercore.helpers.gui.GUI;
import com.github.cyberryan1.cybercore.helpers.gui.GUIItem;
import com.github.cyberryan1.cybercore.utils.CoreGUIUtils;
import com.github.cyberryan1.netuno.guis.history.NewHistoryListGUI;
import com.github.cyberryan1.netuno.guis.ipinfo.NewAltsListGUI;
import com.github.cyberryan1.netuno.guis.punish.utils.MainButton;
import com.github.cyberryan1.netuno.guis.punish.utils.PunishSettings;
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
                NewHistoryListGUI historyList = new NewHistoryListGUI( this.target, this.staff, 1 );
                historyList.open();
            } ) );
        }

        MainButton alts = PunishSettings.MAIN_ALTS_BUTTON.mainButton();
        if ( alts.getIndex() != -1 ) {
            gui.setItem( alts.getIndex(), new GUIItem( alts.getItem( this.target ), alts.getIndex(), () -> {
                staff.closeInventory();
                NewAltsListGUI altsList = new NewAltsListGUI( this.staff, this.target, 1 );
                altsList.open();
            } ) );
        }

        MainButton warn = PunishSettings.MAIN_WARN_BUTTON.mainButton();
        if ( warn.getIndex() != -1 ) {
            gui.setItem( warn.getIndex(), new GUIItem( warn.getItem( this.target ), warn.getIndex(), () -> {
                staff.closeInventory();
                WarnPunishGUI g = new WarnPunishGUI( this.staff, this.target );
                g.open();
            } ) );
        }

        MainButton mute = PunishSettings.MAIN_MUTE_BUTTON.mainButton();
        if ( mute.getIndex() != -1 ) {
            gui.setItem( mute.getIndex(), new GUIItem( mute.getItem( this.target ), mute.getIndex(), () -> {
                staff.closeInventory();
                MutePunishGUI g = new MutePunishGUI( this.staff, this.target );
                g.open();
            } ) );
        }

        MainButton ban = PunishSettings.MAIN_BAN_BUTTON.mainButton();
        if ( ban.getIndex() != -1 ) {
            gui.setItem( ban.getIndex(), new GUIItem( ban.getItem( this.target ), ban.getIndex(), () -> {
                staff.closeInventory();
                BanPunishGUI g = new BanPunishGUI( this.staff, this.target );
                g.open();
            } ) );
        }

        MainButton ipmute = PunishSettings.MAIN_IPMUTE_BUTTON.mainButton();
        if ( ipmute.getIndex() != -1 ) {
            gui.setItem( ipmute.getIndex(), new GUIItem( ipmute.getItem( this.target ), ipmute.getIndex(), () -> {
                staff.closeInventory();
                IpMutePunishGUI g = new IpMutePunishGUI( this.staff, this.target );
                g.open();
            } ) );
        }

        MainButton ipban = PunishSettings.MAIN_IPBAN_BUTTON.mainButton();
        if ( ipban.getIndex() != -1 ) {
            gui.setItem( ipban.getIndex(), new GUIItem( ipban.getItem( this.target ), ipban.getIndex(), () -> {
                staff.closeInventory();
                IpBanPunishGUI g = new IpBanPunishGUI( this.staff, this.target );
                g.open();
            } ) );
        }

        gui.createInventory();
    }

    public void open() {
        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
            gui.openInventory( this.staff );
        } );
    }
}