package com.github.cyberryan1.netuno.guis.ipinfo;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberGuiUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayer;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netuno.guis.history.HistoryListGUI;
import com.github.cyberryan1.netuno.guis.utils.SortBy;
import com.github.cyberryan1.netunoapi.models.alts.TempUuidIpEntry;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import com.github.cyberryan1.netunoapi.models.punishments.PunishmentType;
import com.github.cyberryan1.netunoapi.utils.PunishmentUtils;
import org.bukkit.Bukkit;
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

public class AltsListGUI {

    private final Gui gui;
    private final Player staff;
    private final NetunoPlayer target;
    private final int page;
    private final SortBy sort;
    private List<NetunoPlayer> alts;
    private final List<NetunoPlayer> punishedAlts;

    public AltsListGUI( Player staff, OfflinePlayer target, int page, SortBy sort ) {
        this.staff = staff;
        this.target = NetunoPlayerCache.getOrLoad( target.getUniqueId().toString() );
        this.page = page;
        this.sort = sort;

        String ip = "";
        if ( target.isOnline() ) { ip = target.getPlayer().getAddress().getAddress().getHostAddress(); }
        else {
            List<TempUuidIpEntry> entries = new ArrayList<>( ApiNetuno.getData().getTempAltsDatabase().queryByUuid( target.getUniqueId() ) );
            ip = entries.get( 0 ).getIp();
        }
        this.alts = ApiNetuno.getInstance().getAltCache().queryAccounts( ip ).stream()
                .map( uuid -> NetunoPlayerCache.getOrLoad( uuid.toString() ) )
                .collect( Collectors.toList() );

        this.punishedAlts = this.alts.stream()
                .filter( a -> PunishmentUtils.anyActive( a.getPunishments() ) )
                .collect( Collectors.toList() );

        sort();

        gui = new Gui( "&p" + target.getName() + "&s's Alts", 6, CyberGuiUtils.getBackgroundGlass() );
        insertItems();
    }

    public AltsListGUI( Player staff, OfflinePlayer target, int page ) {
        this( staff, target, page, SortBy.ALPHABETICAL );
    }

    public void open() {
        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
            gui.openInventory( staff );
        } );
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
                    gui.addItem( new GuiItem( Material.WHITE_STAINED_GLASS_PANE, "&f", guiIndex ) );
                }
                else {
                    final int finalAltIndex = altIndex;
                    gui.addItem( new GuiItem( getAltSkull( altIndex ), guiIndex, ( item ) -> {
                        if ( punishedAlts.contains( alts.get( finalAltIndex ) ) ) {
                            HistoryListGUI listGui = new HistoryListGUI( alts.get( finalAltIndex ).getPlayer(), staff, 1 );
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
        gui.addItem( new GuiItem( getSortHopper( this.sort ), 40, ( item ) -> {
            staff.closeInventory();
            AltsListGUI newGui = new AltsListGUI( staff, target.getPlayer(), page, getNextSort( this.sort ) );
            newGui.open();
        } ) );

        // Current Page Paper
        gui.addItem( new GuiItem( getCurrentPagePaper(), 49 ) );

        // Previous Book
        if ( page >= 2 ) {
            gui.addItem( new GuiItem( Material.BOOK, "&pPrevious Page", 49, ( item ) -> {
                staff.closeInventory();
                AltsListGUI newGui = new AltsListGUI( staff, target.getPlayer(), page - 1, this.sort );
                newGui.open();
            } ) );
        }

        // Next Book
        int maxPage = ( int ) Math.ceil( alts.size() / 21.0 );
        if ( page < maxPage ) {
            gui.addItem( new GuiItem( Material.BOOK, "&pNext Page", 51, ( item ) -> {
                staff.closeInventory();
                AltsListGUI newGui = new AltsListGUI( staff, target.getPlayer(), page + 1, this.sort );
                newGui.open();
            } ) );
        }
    }

    private ItemStack getAltSkull( int index ) {
        NetunoPlayer account = alts.get( index );
        ItemStack skull = CyberItemUtils.getPlayerSkull( account.getPlayer() );

        if ( punishedAlts.contains( account ) ) {
            skull = CyberItemUtils.setItemName( skull, "&c" + account.getPlayer().getName() );
            ArrayList<String> lore = new ArrayList<>();
            //ArrayList<Punishment> accountPuns = Utils.getDatabase().getAllActivePunishments( account.getUniqueId().toString() );
            List<NPunishment> accountPuns = PunishmentUtils.getActive( account.getPunishments() );

            for ( NPunishment pun : accountPuns ) {
                if ( pun.getPunishmentType() == PunishmentType.MUTE && lore.contains( CyberColorUtils.getColored( "&8- &sMuted" ) ) == false ) {
                    lore.add( CyberColorUtils.getColored( "&8- &sMuted" ) );
                }
                else if ( pun.getPunishmentType() == PunishmentType.BAN && lore.contains( CyberColorUtils.getColored( "&8- &sBanned" ) ) == false ) {
                    lore.add( CyberColorUtils.getColored( "&8- &sBanned" ) );
                }
                else if ( pun.getPunishmentType() == PunishmentType.IPMUTE && lore.contains( CyberColorUtils.getColored( "&8- &sIP Muted" ) ) == false ) {
                    lore.add( CyberColorUtils.getColored( "&8- &sIP Muted" ) );
                }
                else if ( pun.getPunishmentType() == PunishmentType.IPBAN && lore.contains( CyberColorUtils.getColored( "&8- &sIP Banned" ) ) == false ) {
                    lore.add( CyberColorUtils.getColored( "&8- &sIP Banned" ) );
                }
            }

            skull = CyberItemUtils.setItemLore( skull, lore );
            skull.addUnsafeEnchantment( Enchantment.PROTECTION_ENVIRONMENTAL, 1 );
            SkullMeta meta = ( SkullMeta ) skull.getItemMeta();
            meta.addItemFlags( ItemFlag.HIDE_ENCHANTS );
            skull.setItemMeta( meta );
        }
        else { skull = CyberItemUtils.setItemName( skull, "&s" + account.getPlayer().getName() ); }

        return skull;
    }

    private ItemStack getSortHopper( SortBy sort ) {
        if ( sort == SortBy.ALPHABETICAL ) {
            ItemStack hopper = CyberItemUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pA -> Z" );
            return CyberItemUtils.addItemLore( hopper, "&sNext Sort: &pFirst Join -> Last Join", "&sClick to change sort method" );
        }

        else if ( sort == SortBy.FIRST_DATE ) {
            ItemStack hopper = CyberItemUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pFirst Join -> Last Join" );
            return CyberItemUtils.addItemLore( hopper, "&sNext Sort: &pLast Join -> First Join", "&sClick to change sort method" );
        }

        else if ( sort == SortBy.LAST_DATE ) {
            ItemStack hopper = CyberItemUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pLast Join -> First Join" );
            return CyberItemUtils.addItemLore( hopper, "&sNext Sort: &pPunished -> Not Punished", "&sClick to change sort method" );
        }

        else if ( sort == SortBy.FIRST_PUNISHED ) {
            ItemStack hopper = CyberItemUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pPunished -> Not Punished" );
            return CyberItemUtils.addItemLore( hopper, "&sNext Sort: &pNot Punished -> Punished", "&sClick to change sort method" );
        }

        else if ( sort == SortBy.LAST_PUNISHED ) {
            ItemStack hopper = CyberItemUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pNot Punished -> Punished" );
            return CyberItemUtils.addItemLore( hopper, "&sNext Sort: &pA -> Z", "&sClick to change sort method" );
        }

        return null;
    }

    private ItemStack getCurrentPagePaper() {
        int maxPage = ( int ) Math.ceil( alts.size() / 21.0 );
        return CyberItemUtils.createItem( Material.PAPER, "&sPage: &p" + page + "&s/&p" + maxPage );
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