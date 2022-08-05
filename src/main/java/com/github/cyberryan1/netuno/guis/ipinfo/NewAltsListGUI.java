package com.github.cyberryan1.netuno.guis.ipinfo;

import com.github.cyberryan1.cybercore.helpers.gui.GUI;
import com.github.cyberryan1.cybercore.helpers.gui.GUIItem;
import com.github.cyberryan1.cybercore.utils.CoreGUIUtils;
import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.netuno.api.ApiNetuno;
import com.github.cyberryan1.netuno.api.models.players.NetunoPlayer;
import com.github.cyberryan1.netuno.api.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netuno.guis.history.NewHistoryListGUI;
import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netuno.guis.utils.SortBy;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import com.github.cyberryan1.netunoapi.models.punishments.PunishmentType;
import com.github.cyberryan1.netunoapi.utils.PunishmentUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NewAltsListGUI {

    private final GUI gui;
    private final Player staff;
    private final NetunoPlayer target;
    private final int page;
    private final SortBy sort;
    private List<NetunoPlayer> alts;
    private final List<NetunoPlayer> punishedAlts;

    public NewAltsListGUI( Player staff, OfflinePlayer target, int page, SortBy sort ) {
        this.staff = staff;
        this.target = NetunoPlayerCache.getOrLoad( target.getUniqueId().toString() );
        this.page = page;
        this.sort = sort;
        this.alts = ApiNetuno.getData().getNetunoAlts().getAlts( target ).stream()
                .map( a -> NetunoPlayerCache.getOrLoad( a.getUniqueId().toString() ) )
                .collect( Collectors.toList() );
        this.punishedAlts = this.alts.stream()
                .filter( a -> PunishmentUtils.anyActive( a.getPunishments() ) )
                .collect( Collectors.toList() );

        sort();

        gui = new GUI( "&p" + target.getName() + "&s's Alts", 6, CoreGUIUtils.getBackgroundGlass() );
        insertItems();
    }

    public NewAltsListGUI( Player staff, OfflinePlayer target, int page ) {
        this( staff, target, page, SortBy.ALPHABETICAL );
    }

    public void open() {
        gui.openInventory( staff );
    }

    private void insertItems() {
        // White glass: 10-16, 19-25, 28-34
        // Alt heads: 10-16, 19-25, 28-34
        // Sort Hopper: 40
        // Current Page Paper: 49
        // Back book: 47 || Next book: 51

        int altIndex = 21 * ( page - 1 );
        int guiIndex = 10;
        for ( int row = 0; row < 3; row++ ) {
            for ( int col = 0; col < 7; col++ ) {
                if ( altIndex >= alts.size() ) {
                    gui.setItem( guiIndex, new GUIItem( Material.WHITE_STAINED_GLASS, "&f", guiIndex ) );
                }
                else {
                    final int finalAltIndex = altIndex;
                    gui.setItem( guiIndex, new GUIItem( getAltSkull( altIndex ), guiIndex, () -> {
                        if ( punishedAlts.contains( alts.get( finalAltIndex ) ) ) {
                            NewHistoryListGUI listGui = new NewHistoryListGUI( alts.get( finalAltIndex ).getPlayer(), staff, 1 );
                            listGui.open();
                            staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 10, 2 );
                        }
                    } ) );
                }

                guiIndex++;
                altIndex++;
            }

            guiIndex += 2;
        }

        // Sort Hopper
        gui.setItem( 40, new GUIItem( getSortHopper( this.sort ), 40, () -> {
            staff.closeInventory();
            NewAltsListGUI newGui = new NewAltsListGUI( staff, target.getPlayer(), page, getNextSort( this.sort ) );
            newGui.open();
        } ) );

        // Current Page Paper
        gui.setItem( 49, new GUIItem( getCurrentPagePaper(), 49 ) );

        // Previous Book
        if ( page >= 2 ) {
            gui.setItem( 49, new GUIItem( CoreGUIUtils.createItem( Material.BOOK, "&pPrevious Page" ), 49, () -> {
                staff.closeInventory();
                NewAltsListGUI newGui = new NewAltsListGUI( staff, target.getPlayer(), page - 1, this.sort );
                newGui.open();
            } ) );
        }

        // Next Book
        int maxPage = ( int ) Math.ceil( alts.size() / 21.0 );
        if ( page < maxPage ) {
            gui.setItem( 51, new GUIItem( CoreGUIUtils.createItem( Material.BOOK, "&pNext Page" ), 51, () -> {
                staff.closeInventory();
                NewAltsListGUI newGui = new NewAltsListGUI( staff, target.getPlayer(), page + 1, this.sort );
                newGui.open();
            } ) );
        }

        gui.createInventory();
    }

    private ItemStack getAltSkull( int index ) {
        NetunoPlayer account = alts.get( index );
        ItemStack skull = GUIUtils.getPlayerSkull( account.getPlayer() );

        if ( punishedAlts.contains( account ) ) {
            skull = CoreGUIUtils.setItemName( skull, "&c" + account.getPlayer().getName() );
            ArrayList<String> lore = new ArrayList<>();
            //ArrayList<Punishment> accountPuns = Utils.getDatabase().getAllActivePunishments( account.getUniqueId().toString() );
            List<NPunishment> accountPuns = PunishmentUtils.getActive( account.getPunishments() );

            for ( NPunishment pun : accountPuns ) {
                if ( pun.getPunishmentType() == PunishmentType.MUTE && lore.contains( CoreUtils.getColored( "&8- &sMuted" ) ) == false ) {
                    lore.add( CoreUtils.getColored( "&8- &sMuted" ) );
                }
                else if ( pun.getPunishmentType() == PunishmentType.BAN && lore.contains( CoreUtils.getColored( "&8- &sBanned" ) ) == false ) {
                    lore.add( CoreUtils.getColored( "&8- &sBanned" ) );
                }
                else if ( pun.getPunishmentType() == PunishmentType.IPMUTE && lore.contains( CoreUtils.getColored( "&8- &sIP Muted" ) ) == false ) {
                    lore.add( CoreUtils.getColored( "&8- &sIP Muted" ) );
                }
                else if ( pun.getPunishmentType() == PunishmentType.IPBAN && lore.contains( CoreUtils.getColored( "&8- &sIP Banned" ) ) == false ) {
                    lore.add( CoreUtils.getColored( "&8- &sIP Banned" ) );
                }
            }

            skull = CoreGUIUtils.setItemLore( skull, lore );
            skull.addUnsafeEnchantment( Enchantment.PROTECTION_ENVIRONMENTAL, 1 );
            SkullMeta meta = ( SkullMeta ) skull.getItemMeta();
            meta.addItemFlags( ItemFlag.HIDE_ENCHANTS );
            skull.setItemMeta( meta );
        }
        else { skull = CoreGUIUtils.setItemName( skull, "&s" + account.getPlayer().getName() ); }

        return skull;
    }

    private ItemStack getSortHopper( SortBy sort ) {
        if ( sort == SortBy.ALPHABETICAL ) {
            ItemStack hopper = CoreGUIUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pA -> Z" );
            return CoreGUIUtils.addItemLore( hopper, "&sNext Sort: &pFirst Join -> Last Join", "&sClick to change sort method" );
        }

        else if ( sort == SortBy.FIRST_DATE ) {
            ItemStack hopper = CoreGUIUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pFirst Join -> Last Join" );
            return CoreGUIUtils.addItemLore( hopper, "&sNext Sort: &pLast Join -> First Join", "&sClick to change sort method" );
        }

        else if ( sort == SortBy.LAST_DATE ) {
            ItemStack hopper = CoreGUIUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pLast Join -> First Join" );
            return CoreGUIUtils.addItemLore( hopper, "&sNext Sort: &pPunished -> Not Punished", "&sClick to change sort method" );
        }

        else if ( sort == SortBy.FIRST_PUNISHED ) {
            ItemStack hopper = CoreGUIUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pPunished -> Not Punished" );
            return CoreGUIUtils.addItemLore( hopper, "&sNext Sort: &pNot Punished -> Punished", "&sClick to change sort method" );
        }

        else if ( sort == SortBy.LAST_PUNISHED ) {
            ItemStack hopper = CoreGUIUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pNot Punished -> Punished" );
            return CoreGUIUtils.addItemLore( hopper, "&sNext Sort: &pA -> Z", "&sClick to change sort method" );
        }

        return null;
    }

    private ItemStack getCurrentPagePaper() {
        int maxPage = ( int ) Math.ceil( alts.size() / 21.0 );
        return CoreGUIUtils.createItem( Material.PAPER, "&sPage: &p" + page + "&s/&p" + maxPage );
    }

    private SortBy getNextSort( SortBy sort ) {
        return switch ( sort ) {
            case ALPHABETICAL -> SortBy.FIRST_DATE;
            case FIRST_DATE -> SortBy.LAST_DATE;
            case LAST_DATE -> SortBy.FIRST_PUNISHED;
            case FIRST_PUNISHED -> SortBy.LAST_PUNISHED;
            default -> SortBy.ALPHABETICAL;
        };
    }

    private void sort() {
        if ( sort == SortBy.ALPHABETICAL ) {
            alts = alts.stream()
                    .filter(  alt -> alt.getPlayer().getName() != null )
                    .sorted( ( a1, a2 ) -> a1.getPlayer().getName().compareToIgnoreCase( a2.getPlayer().getName() ) )
                    .collect( Collectors.toList() );
        }

        else if ( sort == SortBy.FIRST_DATE || sort == SortBy.LAST_DATE ) {
            alts = alts.stream()
                    .filter( ( alt ) -> ( alt.getPlayer().getName() != null ) )
                    .sorted( ( a1, a2 ) -> ( int ) (
                            sort == SortBy.FIRST_DATE ? ( a1.getPlayer().getFirstPlayed() - a2.getPlayer().getFirstPlayed() )
                                    : ( a2.getPlayer().getFirstPlayed() - a1.getPlayer().getFirstPlayed() )
                    ) )
                    .collect( Collectors.toList() );
        }

        else if ( sort == SortBy.FIRST_PUNISHED || sort == SortBy.LAST_PUNISHED ) {
            alts = alts.stream()
                    .filter( ( alt ) -> ( alt.getPlayer().getName() != null ) )
                    .sorted( ( a1, a2 ) -> (
                            sort == SortBy.FIRST_PUNISHED ?
                                    ( punishedAlts.contains( a1 ) == punishedAlts.contains( a2 ) ? 0 :
                                            ( punishedAlts.contains( a1 ) ? -1 : 1 ) ) :
                                    ( punishedAlts.contains( a1 ) == punishedAlts.contains( a2 ) ? 0 :
                                            ( punishedAlts.contains( a1 ) ? 1 : -1 ) )
                    ) )
                    .collect( Collectors.toList() );
        }
    }
}