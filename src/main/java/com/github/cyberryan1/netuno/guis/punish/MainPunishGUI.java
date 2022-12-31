package com.github.cyberryan1.netuno.guis.punish;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.CyberGuiUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.guis.history.HistoryListGUI;
import com.github.cyberryan1.netuno.guis.ipinfo.AltsListGUI;
import com.github.cyberryan1.netuno.guis.punish.managers.ActiveGuiManager;
import com.github.cyberryan1.netuno.guis.punish.managers.OpenGui;
import com.github.cyberryan1.netuno.guis.punish.utils.MainButton;
import com.github.cyberryan1.netuno.guis.punish.utils.PunishSettings;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MainPunishGUI {

    private final Gui gui;
    private final Player staff;
    private final OfflinePlayer target;

    public MainPunishGUI( Player staff, OfflinePlayer target ) {
        this.staff = staff;
        this.target = target;

        this.gui = new Gui( PunishSettings.MAIN_INVENTORY_NAME.coloredString().replace( "[TARGET]", target.getName() ),
                5, CyberGuiUtils.getBackgroundGlass() );
        insertItems();
    }

    public void insertItems() {
        // items are defined in the config.yml file

        MainButton skull = PunishSettings.MAIN_SKULL_BUTTON.mainButton();
        if ( skull.getIndex() != -1 ) {
            gui.addItem( new GuiItem( skull.getItem( this.target ), skull.getIndex() ) );
        }

        MainButton history = PunishSettings.MAIN_HISTORY_BUTTON.mainButton();
        if ( history.getIndex() != -1 ) {
            gui.addItem( new GuiItem( history.getItem( this.target ), history.getIndex(), ( itemClicked ) -> {
                staff.closeInventory();

                if ( CyberVaultUtils.hasPerms( this.staff, Settings.HISTORY_PERMISSION.string() ) == false ) {
                    CyberMsgUtils.sendMsg( this.staff, Settings.PERM_DENIED_MSG.string() );
                    return;
                }

                HistoryListGUI historyList = new HistoryListGUI( this.target, this.staff, 1 );
                historyList.open();
            } ) );
        }

        MainButton alts = PunishSettings.MAIN_ALTS_BUTTON.mainButton();
        if ( alts.getIndex() != -1 ) {
            gui.addItem( new GuiItem( alts.getItem( this.target ), alts.getIndex(), ( itemClicked ) -> {
                staff.closeInventory();

                if ( CyberVaultUtils.hasPerms( this.staff, Settings.IPINFO_PERMISSION.string() ) == false ) {
                    CyberMsgUtils.sendMsg( this.staff, Settings.PERM_DENIED_MSG.string() );
                    return;
                }

                AltsListGUI altsList = new AltsListGUI( this.staff, this.target, 1 );
                altsList.open();
            } ) );
        }

        final MainButton silent = ( CyberVaultUtils.hasPerms( this.staff, Settings.SILENT_PERMISSION.string() ) ) ?
                PunishSettings.MAIN_SILENT_DISABLED_BUTTON.mainButton() :
                PunishSettings.MAIN_SILENT_NO_PERMS_BUTTON.mainButton();
        if ( silent.getIndex() != -1 ) {
            gui.addItem( new GuiItem( silent.getItem( this.target ), silent.getIndex(),
                    ( guiItem ) -> {
                if ( guiItem.getType() == PunishSettings.MAIN_SILENT_NO_PERMS_BUTTON.mainButton().getItem( this.target ).getType() ) {
                    return;
                }

                if ( guiItem.getType() == PunishSettings.MAIN_SILENT_DISABLED_BUTTON.mainButton().getItem( this.target ).getType() ) {
                    guiItem.setItem( PunishSettings.MAIN_SILENT_ENABLED_BUTTON.mainButton().getItem( this.target ) );
                    ActiveGuiManager.searchByStaff( this.staff ).ifPresent( gui -> gui.setSilent( true ) );
                }
                else {
                    guiItem.setItem( PunishSettings.MAIN_SILENT_DISABLED_BUTTON.mainButton().getItem( this.target ) );
                    ActiveGuiManager.searchByStaff( this.staff ).ifPresent( gui -> gui.setSilent( false ) );
                }

                gui.updateItem( guiItem );
            } ) );
        }

        MainButton warn = PunishSettings.MAIN_WARN_BUTTON.mainButton();
        if ( warn.getIndex() != -1 ) {
            final ItemStack item = getCorrectItem( warn.getItem( this.target ), "warn" );
            gui.addItem( new GuiItem( item, warn.getIndex(), ( itemClicked ) -> {
                if ( CyberVaultUtils.hasPerms( this.staff, PunishSettings.WARN_PERMISSION.string() ) == false
                        || CyberVaultUtils.hasPerms( this.staff, Settings.WARN_PERMISSION.string() ) == false ) {
                    CyberMsgUtils.sendMsg( this.staff, Settings.PERM_DENIED_MSG.string() );
                    staff.closeInventory();
                    return;
                }

                final OpenGui otherGui = ActiveGuiManager.searchByGui( "warn" ).orElse( null );
                if ( otherGui != null ) {
                    CyberMsgUtils.sendMsg( this.staff, "&p" + this.target.getName() +
                            " &sis already being warned by &p" + otherGui.getStaff().getName() );
                    return;
                }

                ActiveGuiManager.searchByStaff( this.staff ).ifPresent( gui -> {
                    gui.setCancelDelete( true );
                    gui.setCurrentGui( "warn" );
                } );

                staff.closeInventory();
                WarnPunishGUI g = new WarnPunishGUI( this.staff, this.target );
                g.open();
            } ) );
        }

        MainButton mute = PunishSettings.MAIN_MUTE_BUTTON.mainButton();
        if ( mute.getIndex() != -1 ) {
            final ItemStack item = getCorrectItem( mute.getItem( this.target ), "mute" );
            gui.addItem( new GuiItem( item, mute.getIndex(), ( itemClicked ) -> {
                if ( CyberVaultUtils.hasPerms( this.staff, PunishSettings.MUTE_PERMISSION.string() ) == false
                        || CyberVaultUtils.hasPerms( this.staff, Settings.MUTE_PERMISSION.string() ) == false ) {
                    CyberMsgUtils.sendMsg( this.staff, Settings.PERM_DENIED_MSG.string() );
                    staff.closeInventory();
                    return;
                }

                final OpenGui otherGui = ActiveGuiManager.searchByGui( "mute" ).orElse( null );
                if ( otherGui != null ) {
                    CyberMsgUtils.sendMsg( this.staff, "&p" + this.target.getName() +
                            " &sis already being muted by &p" + otherGui.getStaff().getName() );
                    return;
                }

                ActiveGuiManager.searchByStaff( this.staff ).ifPresent( gui -> {
                    gui.setCancelDelete( true );
                    gui.setCurrentGui( "mute" );
                } );

                staff.closeInventory();
                MutePunishGUI g = new MutePunishGUI( this.staff, this.target );
                g.open();
            } ) );
        }

        MainButton ban = PunishSettings.MAIN_BAN_BUTTON.mainButton();
        if ( ban.getIndex() != -1 ) {
            final ItemStack item = getCorrectItem( ban.getItem( this.target ), "ban" );
            gui.addItem( new GuiItem( item, ban.getIndex(), ( itemClicked ) -> {
                if ( CyberVaultUtils.hasPerms( this.staff, PunishSettings.BAN_PERMISSION.string() ) == false ||
                        CyberVaultUtils.hasPerms( this.staff, Settings.BAN_PERMISSION.string() ) == false ) {
                    CyberMsgUtils.sendMsg( this.staff, Settings.PERM_DENIED_MSG.string() );
                    staff.closeInventory();
                    return;
                }

                final OpenGui otherGui = ActiveGuiManager.searchByGui( "ban" ).orElse( null );
                if ( otherGui != null ) {
                    CyberMsgUtils.sendMsg( this.staff, "&p" + this.target.getName() +
                            " &sis already being banned by &p" + otherGui.getStaff().getName() );
                    return;
                }

                ActiveGuiManager.searchByStaff( this.staff ).ifPresent( gui -> {
                    gui.setCancelDelete( true );
                    gui.setCurrentGui( "ban" );
                } );

                staff.closeInventory();
                BanPunishGUI g = new BanPunishGUI( this.staff, this.target );
                g.open();
            } ) );
        }

        MainButton ipmute = PunishSettings.MAIN_IPMUTE_BUTTON.mainButton();
        if ( ipmute.getIndex() != -1 ) {
            final ItemStack item = getCorrectItem( ipmute.getItem( this.target ), "ipmute" );
            gui.addItem( new GuiItem( item, ipmute.getIndex(), ( itemClicked ) -> {
                if ( CyberVaultUtils.hasPerms( this.staff, PunishSettings.IPMUTE_PERMISSION.string() ) == false
                        || CyberVaultUtils.hasPerms( this.staff, Settings.IPMUTE_PERMISSION.string() ) == false ) {
                    CyberMsgUtils.sendMsg( this.staff, Settings.PERM_DENIED_MSG.string() );
                    staff.closeInventory();
                    return;
                }

                final OpenGui otherGui = ActiveGuiManager.searchByGui( "ipmute" ).orElse( null );
                if ( otherGui != null ) {
                    CyberMsgUtils.sendMsg( this.staff, "&p" + this.target.getName() +
                            " &sis already being IP muted by &p" + otherGui.getStaff().getName() );
                    return;
                }

                ActiveGuiManager.searchByStaff( this.staff ).ifPresent( gui -> {
                    gui.setCancelDelete( true );
                    gui.setCurrentGui( "ipmute" );
                } );

                staff.closeInventory();
                IpMutePunishGUI g = new IpMutePunishGUI( this.staff, this.target );
                g.open();
            } ) );
        }

        MainButton ipban = PunishSettings.MAIN_IPBAN_BUTTON.mainButton();
        if ( ipban.getIndex() != -1 ) {
            final ItemStack item = getCorrectItem( ipban.getItem( this.target ), "ipban" );
            gui.addItem( new GuiItem( item, ipban.getIndex(), ( itemClicked ) -> {
                if ( CyberVaultUtils.hasPerms( this.staff, PunishSettings.IPBAN_PERMISSION.string() ) == false ||
                        CyberVaultUtils.hasPerms( this.staff, Settings.IPBAN_PERMISSION.string() ) == false ) {
                    CyberMsgUtils.sendMsg( this.staff, Settings.PERM_DENIED_MSG.string() );
                    staff.closeInventory();
                    return;
                }

                final OpenGui otherGui = ActiveGuiManager.searchByGui( "ipban" ).orElse( null );
                if ( otherGui != null ) {
                    CyberMsgUtils.sendMsg( this.staff, "&p" + this.target.getName() +
                            " &sis already being IP banned by &p" + otherGui.getStaff().getName() );
                    return;
                }

                ActiveGuiManager.searchByStaff( this.staff ).ifPresent( gui -> {
                    gui.setCancelDelete( true );
                    gui.setCurrentGui( "ipban" );
                } );

                staff.closeInventory();
                IpBanPunishGUI g = new IpBanPunishGUI( this.staff, this.target );
                g.open();
            } ) );
        }
    }

    public void open() {
        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
            gui.openInventory( this.staff );
            gui.setCloseEvent( () -> {
                ActiveGuiManager.attemptRemoveActiveGui( this.staff );
            } );
            ActiveGuiManager.addActiveGui( this.staff, this.target );
        } );
    }

    private ItemStack getCorrectItem( ItemStack item, String guiType ) {
        final OpenGui otherGui = ActiveGuiManager.searchByGui( guiType ).orElse( null );
        if ( otherGui == null ) { return item; }

        ItemStack newItem = item.clone();
        final String newName = PunishSettings.MAIN_IN_USE_NAME.coloredString()
                .replace( "[STAFF]", otherGui.getStaff().getName() )
                .replace( "[TYPE]", getCorrectPunishmentForm( guiType ) );
        final String newLore = PunishSettings.MAIN_IN_USE_LORE.coloredString()
                .replace( "[STAFF]", otherGui.getStaff().getName() )
                .replace( "[TYPE]", getCorrectPunishmentForm( guiType ) );
        final Material newMaterial = PunishSettings.MAIN_IN_USE_ITEM.material();
        final boolean newGlow = PunishSettings.MAIN_IN_USE_GLOW.bool();

        if ( newName.length() > 0 ) { CyberItemUtils.setItemName( newItem, newName ); }
        if ( newLore.length() > 0 ) { CyberItemUtils.setItemLore( newItem, newLore ); }
        if ( newMaterial != Material.AIR ) { newItem.setType( newMaterial ); }
        if ( newGlow ) {
            newItem.addUnsafeEnchantment( Enchantment.DURABILITY, 1 );
            ItemMeta meta = newItem.getItemMeta();
            meta.addItemFlags( ItemFlag.HIDE_ENCHANTS );
            newItem.setItemMeta( meta );
        }

        return newItem;
    }

    private String getCorrectPunishmentForm( String guiType ) {
        return switch ( guiType.toLowerCase() ) {
            case "warn" -> "warned";
            case "mute" -> "muted";
            case "ban" -> "banned";
            case "ipmute" -> "IP muted";
            case "ipban" -> "IP banned";
            default -> null;
        };
    }
}