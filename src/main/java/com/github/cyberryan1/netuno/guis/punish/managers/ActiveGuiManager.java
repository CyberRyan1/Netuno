package com.github.cyberryan1.netuno.guis.punish.managers;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// TODO move OpenGui into this file
// TODO redo this class, I don't like it -- needs to contain actual info about the open GUI
public class ActiveGuiManager {

    private static final List<OpenGui> activeGuis = new ArrayList<>();

    public static void addActiveGui( Player staff, OfflinePlayer target ) {
        activeGuis.add( new OpenGui( staff, target ) );
    }

    public static void attemptRemoveActiveGui( Player staff ) {
        for ( OpenGui gui : activeGuis ) {
            if ( gui.getStaff().getUniqueId().equals( staff.getUniqueId() ) ) {
                if ( gui.isCancelDelete() ) {
                    gui.setCancelDelete( false );
                    return;
                }
                activeGuis.remove( gui );
                return;
            }
        }
    }

    public static void forceRemoveActiveGui( Player staff ) {
        for ( OpenGui gui : activeGuis ) {
            if ( gui.getStaff().getUniqueId().equals( staff.getUniqueId() ) ) {
                activeGuis.remove( gui );
                return;
            }
        }
    }

    public static Optional<OpenGui> searchByStaff( Player staff ) {
        for ( OpenGui gui : activeGuis ) {
            if ( gui.getStaff().getUniqueId().equals( staff.getUniqueId() ) ) {
                return Optional.of( gui );
            }
        }

        return Optional.empty();
    }

    public static Optional<OpenGui> searchByTarget( OfflinePlayer target ) {
        for ( OpenGui gui : activeGuis ) {
            if ( gui.getTarget().getUniqueId().equals( target.getUniqueId() ) ) {
                return Optional.of( gui );
            }
        }

        return Optional.empty();
    }

    public static Optional<OpenGui> searchByGui( String guiName ) {
        for ( OpenGui gui : activeGuis ) {
            if ( gui.getCurrentGui().equalsIgnoreCase( guiName ) ) {
                return Optional.of( gui );
            }
        }

        return Optional.empty();
    }
}