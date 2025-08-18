package com.github.cyberryan1.netuno.guis.alts;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberGuiUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPlayer;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.guis.history.HistoryListGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The alts GUI that is displayed to staff when they execute the
 * <code>/ipinfo (player)</code> command. Contains information
 * on who their alts are and if their alts have any punishments.
 *
 * @author Ryan
 */
public class AltsGui {

    private static BukkitTask blinkTask = null;
    private static List<AltsGui> openGuis = new ArrayList<>();
    private static boolean blinkState = false;

    private final Gui gui;
    private final Player staff;
    private final OfflinePlayer target;
    private final int page;

    private List<ApiPlayer> sortedAccounts; // This isn't updated until in insertItems()
    private List<Integer> punishedSkullsIndex = new ArrayList<>(); // where each of the punished player's skulls are within the GUI

    /**
     * @param staff The staff member
     * @param target The target player
     * @param page The page of the GUI
     */
    public AltsGui( Player staff, OfflinePlayer target, int page ) {
        this.staff = staff;
        this.target = target;
        this.page = page;

        gui = new Gui( "&p" + target.getName() + "&s's Alts", 6, CyberGuiUtils.getBackgroundGlass() );
        insertItems();

        if ( blinkTask == null ) {
            blinkTask = Bukkit.getScheduler().runTaskTimer( CyberCore.getPlugin(), () -> {
                for ( AltsGui g : openGuis ) {
                    if ( g.sortedAccounts == null ) continue;

                    int altIndex = 0;
                    for ( int index : g.punishedSkullsIndex ) {
                        GuiItem guiItem = g.gui.getItem( index );
                        if ( blinkState ) {
                            guiItem.getItem().setType( Material.REDSTONE_BLOCK );
                        }
                        else {
                            ApiPlayer currentAccount = g.sortedAccounts.get( altIndex );
                            guiItem.setItem( g.getAltSkull( currentAccount ) );
                        }

                        g.gui.updateItem( guiItem );
                        altIndex++;
                    }
                }

                blinkState = !blinkState;
            }, 20L, 20L );
        }
    }

    /**
     * @param staff The staff member
     * @param target The target player
     */
    public AltsGui( Player staff, OfflinePlayer target ) {
        this( staff, target, 0 );
    }

    /**
     * Inserts all items into this GUI
     */
    public void insertItems() {
        // White glass: 10-16, 19-25, 28-34
        // Alt heads: 10-16, 19-25, 28-34
        // Current Page Paper: 49
        // Back book: 47 || Next book: 51

        Netuno.SERVICE.getPlayer( this.target ).thenAccept( apiTarget -> {
            Netuno.ALT_SERVICE.getAlts( apiTarget ).thenAccept( apiAlts -> {
                sortedAccounts = getSortedAccounts( apiAlts );

                // Alt skulls (or white glass)
                int altIndex = 21 * ( page - 1 );
                int guiIndex = 10;
                for ( int row = 0; row < 3; row++ ) {
                    for ( int col = 0; col < 7; col++ ) {
                        // If there is no more accounts, set the remaining space to white glass
                        if ( altIndex >= sortedAccounts.size() ) {
                            gui.updateItem( new GuiItem( Material.WHITE_STAINED_GLASS_PANE, "&f", guiIndex ) );
                        }
                        else {
                            ApiPlayer currentAccount = sortedAccounts.get( altIndex );
                            if ( currentAccount.isPunished() ) punishedSkullsIndex.add( guiIndex );

                            final int finalAltIndex = altIndex;
                            gui.updateItem( new GuiItem( getAltSkull( currentAccount ), guiIndex, ( item ) -> {
                                // * In previous versions, we only allowed the staff to click on this
                                // *    account's skull to see their history if this account has an
                                // *    active punishment
                                // * In this version, we should let them see their history no matter
                                // *    if they are currently punished or not

                                HistoryListGui listGui = new HistoryListGui( staff, sortedAccounts.get( finalAltIndex ).getPlayer() );
                                listGui.open();
                                staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 10, 2 );
                            } ) );
                        }

                        guiIndex++;
                        altIndex++;
                    }

                    guiIndex += 2;
                }

                // Current Page Paper
                int maxPage = ( int ) Math.ceil( sortedAccounts.size() / 21.0 );
                ItemStack currentPagePaper = CyberItemUtils.createItem( Material.PAPER, "&sPage: &p" + page + "&s/&p" + maxPage );
                gui.updateItem( new GuiItem( currentPagePaper, 49 ) );

                // Previous Book
                if ( page >= 2 ) {
                    gui.updateItem( new GuiItem( Material.BOOK, "&pPrevious Page", 47, ( item ) -> {
                        staff.closeInventory();
                        AltsGui newGui = new AltsGui( staff, target.getPlayer(), page - 1 );
                        newGui.open();
                    } ) );
                }

                // Next Book
                if ( page < maxPage ) {
                    gui.updateItem( new GuiItem( Material.BOOK, "&pNext Page", 51, ( item ) -> {
                        staff.closeInventory();
                        AltsGui newGui = new AltsGui( staff, target.getPlayer(), page + 1 );
                        newGui.open();
                    } ) );
                }
            } ).exceptionally( Netuno.FUTURE_ERROR_HANDLING );
        } ).exceptionally( Netuno.FUTURE_ERROR_HANDLING );
    }

    public void open() {
        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
            gui.openInventory( this.staff );
            gui.setCloseEvent( ( inv ) -> {
                openGuis.remove( this );
            } );
            openGuis.add( this );
        } );
    }

    /**
     * Gets the skull of an account with all needed information.
     * If the account is punished, then this skull will have lore
     * saying which punishments it has.
     *
     * @param account The account
     * @return The skull
     */
    private ItemStack getAltSkull( ApiPlayer account ) {
        ItemStack skull = CyberItemUtils.getPlayerSkull( account.getPlayer() );

        if ( account.isPunished() ) {
            skull = CyberItemUtils.setItemName( skull, "&c" + account.getPlayer().getName() );
            ArrayList<String> lore = new ArrayList<>();

            List<ApiPunishment> activePuns = account.getActivePunishments();
            for ( ApiPunishment pun : activePuns ) {
                if ( pun.getType() == ApiPunishment.PunType.MUTE && lore.contains( CyberColorUtils.getColored( "&8- &sMuted" ) ) == false ) {
                    lore.add( CyberColorUtils.getColored( "&8- &sMuted" ) );
                }
                else if ( pun.getType() == ApiPunishment.PunType.BAN && lore.contains( CyberColorUtils.getColored( "&8- &sBanned" ) ) == false ) {
                    lore.add( CyberColorUtils.getColored( "&8- &sBanned" ) );
                }
                else if ( pun.getType() == ApiPunishment.PunType.IPMUTE && lore.contains( CyberColorUtils.getColored( "&8- &sIP Muted" ) ) == false ) {
                    lore.add( CyberColorUtils.getColored( "&8- &sIP Muted" ) );
                }
                else if ( pun.getType() == ApiPunishment.PunType.IPBAN && lore.contains( CyberColorUtils.getColored( "&8- &sIP Banned" ) ) == false ) {
                    lore.add( CyberColorUtils.getColored( "&8- &sIP Banned" ) );
                }
            }

            skull = CyberItemUtils.setItemLore( skull, lore );
            skull.addUnsafeEnchantment( Enchantment.PROTECTION, 1 );
            SkullMeta meta = ( SkullMeta ) skull.getItemMeta();
            meta.addItemFlags( ItemFlag.HIDE_ENCHANTS );
            skull.setItemMeta( meta );
        }
        else { skull = CyberItemUtils.setItemName( skull, "&s" + account.getPlayer().getName() ); }

        return skull;
    }

    /**
     * Sorts the provided list such that the players who are
     * punished appear first
     * @param list The list to sort
     * @return The sorted list
     */
    public List<ApiPlayer> getSortedAccounts( List<ApiPlayer> list ) {
        return list.stream()
                .filter( ( alt ) -> ( alt.getPlayer().getName() != null ) )
                .sorted( ( a1, a2 ) -> {
                    if ( a1.getActivePunishments().isEmpty() == a2.getActivePunishments().isEmpty() ) return 0;
                    if ( a1.getActivePunishments().isEmpty() == false ) return -1;
                    return 1;
                } )
                .collect( Collectors.toList() );
    }
}
