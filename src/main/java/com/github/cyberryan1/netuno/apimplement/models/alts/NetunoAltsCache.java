package com.github.cyberryan1.netuno.apimplement.models.alts;

import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.apimplement.database.helpers.AltSecurityLevel;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netunoapi.models.alts.NAltEntry;
import com.github.cyberryan1.netunoapi.models.alts.NAltGroup;
import com.github.cyberryan1.netunoapi.models.alts.NAltLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NetunoAltsCache implements NAltLoader {

    private final List<NAltGroup> cache = new ArrayList<>();

    private AltSecurityLevel securityLevel = AltSecurityLevel.HIGH;

    public void initialize() {
        CyberLogUtils.logInfo( "[ALTS CACHE] Initializing the alts cache..." );
        List<NAltEntry> savedEntries = ApiNetuno.getData().getAlts().queryAllEntries();

        CyberLogUtils.logInfo( "[ALTS CACHE] Loading " + savedEntries.size() + " entries..." );
        for ( NAltEntry entry : savedEntries ) {
            Optional<NAltGroup> group = search( entry.getGroupId() );
            if ( group.isPresent() ) { group.get().addEntry( entry ); }
            else {
                NAltGroup newGroup = new NAltGroup( entry.getGroupId() );
                newGroup.addEntry( entry );
                cache.add( newGroup );
            }
        }

        CyberLogUtils.logInfo( "[ALTS CACHE] Loaded a total of " + cache.size() + " alt groups" );
    }

    public void loadPlayer( UUID uuid, String ip ) {
        if ( this.securityLevel == AltSecurityLevel.LOW ) {
            // Get the group the player's joined IP is in
            final Optional<NAltGroup> group = search( ip );

            // If the group exists, add the player to it
            if ( group.isPresent() ) {
                NAltEntry entry = new NAltEntry( uuid.toString(), ip, group.get().getGroupId() );
                group.get().addEntry( entry );
                ApiNetuno.getData().getAlts().saveNewEntry( entry );
            }

            // Otherwise create a new group and add the player to it
            else {
                NAltGroup newGroup = new NAltGroup( ApiNetuno.getData().getAlts().getNextGroupId() );
                NAltEntry entry = new NAltEntry( uuid.toString(), ip, newGroup.getGroupId() );
                newGroup.addEntry( entry );
                cache.add( newGroup );
                ApiNetuno.getData().getAlts().saveNewEntry( entry );
            }
        }

        else if ( this.securityLevel == AltSecurityLevel.MEDIUM ) {
            // List for all the groups that the player's joined IP is in
            final List<NAltGroup> groups = new ArrayList<>();

            // Adding all the groups that the player's joined IP is in
            for ( NAltGroup group : this.searchForMany( ip ) ) {
                if ( group.getIps().contains( ip ) ) {
                    groups.add( group );
                }
            }

            // If the groups list is empty, then the player's IP is not in any groups
            // So we create a new group for them and save it to the database
            if ( groups.size() == 0 ) {
                NAltGroup newGroup = new NAltGroup( ApiNetuno.getData().getAlts().getNextGroupId() );
                NAltEntry entry = new NAltEntry( uuid.toString(), ip, newGroup.getGroupId() );
                newGroup.addEntry( entry );
                cache.add( newGroup );
                ApiNetuno.getData().getAlts().saveNewEntry( entry );
            }

            // If groups.size() == 1, then the player's IP is in one group

            // If the groups list has more than one group, then the player's IP is in multiple groups
            // So we combine all of these groups into a singular group and save all the edited entries to the database
            else if ( groups.size() > 1 ) {
                final NAltGroup baseGroup = groups.get( 0 );
                final List<NAltEntry> editedEntries = new ArrayList<>();

                for ( int index = 1; index <= groups.size(); index++ ) {
                    combineGroup( baseGroup, groups.get( index ) );
                    editedEntries.addAll( groups.get( index ).getEntries() );
                    this.cache.remove( groups.get( index ) );
                }

                // Adding the player's UUID and current IP to the base group
                NAltEntry entry = new NAltEntry( uuid.toString(), ip, baseGroup.getGroupId() );
                baseGroup.addEntry( entry );
                ApiNetuno.getData().getAlts().saveNewEntry( entry );

                // Saving all the edited entries to the database
                for ( NAltEntry editedEntry : editedEntries ) {
                    ApiNetuno.getData().getAlts().updateEntryGroupId( editedEntry, baseGroup.getGroupId() );
                }

                // ? Unsure if the edits to the baseGroup will actually be saved to the instance of it in the cache
            }
        }

        else if ( this.securityLevel == AltSecurityLevel.HIGH ) {
            // List for all the groups that the player or their joined IP is in
            final List<NAltGroup> groups = new ArrayList<>();

            // Adding all the groups that the player's UUID is in to the list
            for ( NAltGroup group : this.searchForMany( uuid ) ) {
                if ( groups.contains( group ) == false ) {
                    groups.add( group );
                }
            }

            // Adding all the groups that the player's IP is in to the list
            for ( NAltGroup group : this.searchForMany( ip ) ) {
                if ( groups.contains( group ) == false ) {
                    groups.add( group );
                }
            }

            // If the groups list is empty, then the player's UUID nor IP is not in any groups
            // So we create a new group for them and save it to the database
            if ( groups.size() == 0 ) {
                NAltGroup newGroup = new NAltGroup( ApiNetuno.getData().getAlts().getNextGroupId() );
                NAltEntry entry = new NAltEntry( uuid.toString(), ip, newGroup.getGroupId() );
                newGroup.addEntry( entry );
                cache.add( newGroup );
                ApiNetuno.getData().getAlts().saveNewEntry( entry );
            }

            // If groups.size() == 1, then the player OR their IP are already in a singular existing group
            // So we need to see if the group in the list contains the entry for the player's UUID and current IP
            // If it doesn't, then we add it and save it to the database
            else if ( groups.size() == 1 ) {
                final NAltGroup baseGroup = groups.get( 0 );

                boolean found = false;
                for ( NAltEntry entry : baseGroup.getEntries() ) {
                    if ( entry.getUuid().equals( uuid.toString() ) && entry.getIp().equals( ip ) ) {
                        found = true;
                        break;
                    }
                }

                if ( found == false ) {
                    NAltEntry entry = new NAltEntry( uuid.toString(), ip, baseGroup.getGroupId() );
                    baseGroup.addEntry( entry );
                    ApiNetuno.getData().getAlts().saveNewEntry( entry );
                }
            }

            // If the groups list has more than one group, then either the player's UUID and/or IP are in multiple groups
            // So we combine all of these groups into a singular group and save all the edited entries to the database
            else {
                final NAltGroup baseGroup = groups.get( 0 );
                final List<NAltEntry> editedEntries = new ArrayList<>();

                for ( int index = 1; index < groups.size(); index++ ) {
                    combineGroup( baseGroup, groups.get( index ) );
                    editedEntries.addAll( groups.get( index ).getEntries() );
                    this.cache.remove( groups.get( index ) );
                }

                // Adding the player's UUID and current IP to the base group
                NAltEntry entry = new NAltEntry( uuid.toString(), ip, baseGroup.getGroupId() );
                baseGroup.addEntry( entry );
                ApiNetuno.getData().getAlts().saveNewEntry( entry );

                // Saving all the edited entries to the database
                for ( NAltEntry editedEntry : editedEntries ) {
                    ApiNetuno.getData().getAlts().updateEntryGroupId( editedEntry, baseGroup.getGroupId() );
                }

                // ? Unsure if the edits to the baseGroup will actually be saved to the instance of it in the cache
            }
        }
    }

    private void combineGroup( NAltGroup baseGroup, NAltGroup group ) {
        for ( NAltEntry entry : group.getEntries() ) {
            if ( baseGroup.getEntries().contains( entry ) == false ) {
                NAltEntry entryCopy = entry.copy();
                entryCopy.setGroupId( baseGroup.getGroupId() );
                baseGroup.addEntry( entryCopy );
            }
        }
    }

    public Optional<NAltGroup> search( int groupId ) {
        for ( NAltGroup group : cache ) {
            if ( group.getGroupId() == groupId ) {
                return Optional.of( group );
            }
        }
        return Optional.empty();
    }

    public Optional<NAltGroup> search( UUID uuid ) {
        for ( NAltGroup group : cache ) {
            if ( group.getUuids().contains( uuid ) ) {
                return Optional.of( group );
            }
        }
        return Optional.empty();
    }

    public List<NAltGroup> searchForMany( UUID uuid ) {
        List<NAltGroup> groups = new ArrayList<>();
        for ( NAltGroup group : cache ) {
            if ( group.getUuids().contains( uuid ) ) {
                groups.add( group );
            }
        }
        return groups;
    }

    public Optional<NAltGroup> search( String ip ) {
        for ( NAltGroup group : cache ) {
            if ( group.getIps().contains( ip ) ) {
                return Optional.of( group );
            }
        }
        return Optional.empty();
    }

    public List<NAltGroup> searchForMany( String ip ) {
        List<NAltGroup> groups = new ArrayList<>();
        for ( NAltGroup group : cache ) {
            if ( group.getIps().contains( ip ) ) {
                groups.add( group );
            }
        }
        return groups;
    }

    /**
     * @param level The alt security level to set to
     */
    public void setSecurityLevel( AltSecurityLevel level ) {
        CyberLogUtils.logInfo( "[ALTS CACHE] Alt Strictness Level: " + level.name() );
        this.securityLevel = level;
    }

    public void reloadSecurityLevel() {
        this.securityLevel = switch ( Settings.IPINFO_STRICTNESS.string().toUpperCase() ) {
            case "LOW" -> AltSecurityLevel.LOW;
            case "HIGH" -> AltSecurityLevel.HIGH;
            default -> AltSecurityLevel.MEDIUM;
        };
    }

    /**
     * @return The alt security level
     */
    public AltSecurityLevel getSecurityLevel() {
        return securityLevel;
    }
}
