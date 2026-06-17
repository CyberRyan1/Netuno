package com.github.cyberryan1.netuno.guis.punish;

import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.CyberGuiUtils;
import com.github.cyberryan1.cybercore.spigot.utils.factories.items.CyberItemFactory;
import com.github.cyberryan1.netuno.guis.punish.models.SinglePunishButton;
import com.github.cyberryan1.netuno.utils.TimestampUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Allows a staff member to change the duration of a GUI punishment
 * before the punishment is executed.
 *
 * @author Ryan
 */
public class ChangeDurationGui {

    private final PunishmentSpecificGui punGui;
    private final SinglePunishButton button;
    private final int previousPunishmentCount;

    private final Gui gui;

    /**
     * Creating a GUI that allows the staff member to multiply a
     * specific punishment's duration by a multiplier
     *
     * @param punGui       The punishment GUI the staff member
     *                     came from
     * @param button       The button that opened this GUI
     * @param prevPunCount The number of previous punishments
     */
    public ChangeDurationGui( PunishmentSpecificGui punGui, SinglePunishButton button, int prevPunCount ) {
        this.punGui = punGui;
        this.button = button;
        this.previousPunishmentCount = prevPunCount;

        this.gui = new Gui( punGui.getGuiName(), 5, CyberGuiUtils.getBackgroundGlass() );

        insertItems();
    }

    /**
     * Inserts all the items into the GUI
     */
    public void insertItems() {
        button.loadIntoInventory( this.gui, 13, this.punGui, false );

        List<IndexEntry> entries = List.of(
                new IndexEntry( Material.BLUE_DYE, 0.25f, 28 ),
                new IndexEntry( Material.CYAN_DYE, 0.5f, 29 ),
                new IndexEntry( Material.LIGHT_BLUE_DYE, 0.75f, 30 ),
                new IndexEntry( Material.WHITE_DYE, 1f, 31 ),
                new IndexEntry( Material.PINK_DYE, 1.5f, 32 ),
                new IndexEntry( Material.MAGENTA_DYE, 2f, 33 ),
                new IndexEntry( Material.PURPLE_DYE, 2.5f, 34 )
        );

        final long currentDuration = PunishmentGuiExecutor.getScaledDuration( this.button, this.previousPunishmentCount );
        for ( IndexEntry entry : entries ) {
            ItemStack item = new CyberItemFactory( entry.material() )
                    .name( "&p" + entry.multiplier() + "x" )
                    .addLore( "&sNew duration: &p" + TimestampUtils.durationToString( ( long ) ( currentDuration * entry.multiplier() ) ) )
                    .build();

            gui.addItem( new GuiItem( item, entry.index(), i -> {
                PunishmentGuiExecutor.executePunish( this.button, this.punGui.getStaff(), this.punGui.getTarget(), entry.multiplier(), this.punGui.isSilent() );
                this.punGui.getStaff().closeInventory();
            } ) );
        }
    }

    /**
     * Opens the GUI to the staff member
     */
    public void open() {
        gui.openInventory( this.punGui.getStaff() );
    }

    private record IndexEntry( Material material, float multiplier, int index ) {}
}