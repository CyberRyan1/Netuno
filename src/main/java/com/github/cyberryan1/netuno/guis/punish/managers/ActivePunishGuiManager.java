package com.github.cyberryan1.netuno.guis.punish.managers;

import com.github.cyberryan1.netuno.guis.punish.PunishmentSpecificGui;
import com.github.cyberryan1.netuno.guis.punish.models.PunGuiType;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Used to keep track of what punishment GUIs are currently
 * open by staff members. This is useful in preventing other
 * staff members from punishing the same player
 *
 * @author Ryan
 */
public class ActivePunishGuiManager {

    private final List<PunishmentSpecificGui> OPEN_GUIS = new ArrayList<>();

    /**
     * @param gui The GUI to add as an open punishment GUI
     */
    public void addOpenGui( PunishmentSpecificGui gui ) {
        OPEN_GUIS.add( gui );
    }

    /**
     * Removes any open punishment GUIs with the provided
     * player as the staff
     *
     * @param staff The staff member to remove
     */
    public void removeOpenGuiByStaff( Player staff ) {
        OPEN_GUIS.removeIf( gui -> gui.getStaff().getUniqueId().equals( staff.getUniqueId() ) );
    }

    /**
     * Removes any open punishment GUIs with the provided player
     * as the target
     *
     * @param target The target to remove
     */
    public void removeOpenGuiByTarget( OfflinePlayer target ) {
        OPEN_GUIS.removeIf( gui -> gui.getTarget().getUniqueId().equals( target.getUniqueId() ) );
    }

    /**
     * @param staff The staff member to search by
     * @return An optional containing the open punishment GUI
     * with the provided staff member if there is one, otherwise
     * returns an empty optional
     */
    public Optional<PunishmentSpecificGui> searchByStaff( Player staff ) {
        return OPEN_GUIS.stream()
                .filter( gui -> gui.getStaff().getUniqueId()
                        .equals( staff.getUniqueId() ) )
                .findFirst();
    }

    /**
     * @param target The target to search by
     * @param type The type of punishment GUI to search by
     * @return An optional containing the open punishment GUI
     * with the provided target and punishment GUI type if there
     * is one, otherwise returns an empty optional
     */
    public Optional<PunishmentSpecificGui> searchByTargetAndType( OfflinePlayer target, PunGuiType type ) {
        return OPEN_GUIS.stream()
                .filter( gui -> gui.getTarget().getUniqueId()
                        .equals( target.getUniqueId() ) && gui.getType() == type )
                .findFirst();
    }
}