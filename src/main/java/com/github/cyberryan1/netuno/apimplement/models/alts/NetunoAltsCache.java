//package com.github.cyberryan1.netuno.apimplement.models.alts;
//
//import com.github.cyberryan1.cybercore.spigot.CyberCore;
//import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
//import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
//import com.github.cyberryan1.netuno.apimplement.database.helpers.AltSecurityLevel;
//import com.github.cyberryan1.netuno.utils.Duplex;
//import com.github.cyberryan1.netuno.utils.settings.Settings;
//import com.github.cyberryan1.netunoapi.models.alts.NAltEntry;
//import com.github.cyberryan1.netunoapi.models.alts.NAltGroup;
//import com.github.cyberryan1.netunoapi.models.alts.NAltLoader;
//import org.bukkit.Bukkit;
//import org.bukkit.OfflinePlayer;
//import org.bukkit.entity.Player;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//public class NetunoAltsCache implements NAltLoader {
//
//    public static final List<NAltGroup> cache = new ArrayList<>();
//    //                        Alt Entry
//    //                                   Previous Group ID (-1 if none)
//    private final List<Duplex<NAltEntry, Integer>> editedCache = new ArrayList<>();
//    private final List<NAltGroup> removedCache = new ArrayList<>();
//
//    private AltSecurityLevel securityLevel = AltSecurityLevel.HIGH;
//
//    public void initialize() {
//        CyberLogUtils.logInfo( "[ALTS CACHE] Initializing the alts cache..." );
//
//        for ( Player player : Bukkit.getOnlinePlayers() ) {
//            loadPlayer( player.getUniqueId(), player.getAddress().getAddress().getHostAddress() );
//        }
//
//        // ! debug
//        CyberLogUtils.logWarn( "[ALTS CACHE] ---" );
//        for ( NAltGroup group : cache ) {
//            CyberLogUtils.logWarn( "[ALTS CACHE] " + group.getGroupId() + " : " );
//            for ( NAltEntry entry : group.getEntries() ) {
//                CyberLogUtils.logWarn( "[ALTS CACHE]     " + entry.getUuid() + " : " + entry.getIp() );
//            }
//            CyberLogUtils.logWarn( "[ALTS CACHE] ---" );
//        }
//        // ! end debug
//
//        // Repeat every 5 minutes
//        Bukkit.getScheduler().runTaskTimerAsynchronously( CyberCore.getPlugin(), this::save, 6000, 6000 );
//
//        CyberLogUtils.logInfo( "[ALTS CACHE] Loaded a total of " + cache.size() + " alt groups" );
//    }
//
//    public void save() {
//        CyberLogUtils.logInfo( "[ALTS CACHE] Saving the alts cache..." );
//
//        for ( NAltGroup group : removedCache ) {
//            for ( NAltEntry entry : group.getEntries() ) {
//                ApiNetuno.getData().getAlts().deleteEntry( entry );
//            }
//        }
//
//        int saved = 0;
//        int updated = 0;
//        for ( Duplex<NAltEntry, Integer> item : editedCache ) {
//            CyberLogUtils.logWarn( "item: " + item.getFirst().getUuid() + " | " + item.getFirst().getIp() + " !!! " + item.getSecond() );
//            if ( item.getSecond() < 0 ) {
//                ApiNetuno.getData().getAlts().saveNewEntry( item.getFirst() );
//                saved++;
//            }
//            else {
//                ApiNetuno.getData().getAlts().updateEntryGroupId( item.getFirst(), item.getSecond() );
//                updated++;
//            }
//        }
//
//        CyberLogUtils.logInfo( "[ALTS CACHE] Saved " + saved + " new entries, updated " + updated + " entries, and deleted "
//                + removedCache.size() + " entries" );
//        removedCache.clear();
//        editedCache.clear();
//    }
//
//    public void loadPlayer( UUID uuid, String ip ) {
//        if ( this.securityLevel == AltSecurityLevel.LOW ) {
//            // Get the group the player's joined IP is in
//            final Optional<NAltGroup> queryGroup = searchByIp( ip );
//
//            // If the group is already cached, add the player to it
//            if ( queryGroup.isPresent() ) {
//                NAltGroup group = queryGroup.get();
//                NAltEntry entryAttempt = new NAltEntry( uuid.toString(), ip, group.getGroupId() );
//                if ( group.containsEntry( entryAttempt ) == false ) {
//                    group.addEntry( entryAttempt );
//                    this.editedCache.add( new Duplex<>( entryAttempt, -1 ) );
//                }
//            }
//
//            else {
//                // If the group is not cached, see if it is in the database
//                // Get the group from the database
//                final Optional<NAltGroup> queryGroup2 = ApiNetuno.getData().getAlts().queryGroupByIp( ip );
//
//                // If the group is in the database, add the player to it and load the group into the cache
//                if ( queryGroup2.isPresent() ) {
//                    loadGroupFromDatabase( queryGroup2.get(), uuid, ip );
//                }
//
//                // Otherwise create a new group, add the player to it, and load the group into the cache
//                else {
//                    createNewGroup( uuid, ip );
//                }
//            }
//        }
//
//        else if ( this.securityLevel == AltSecurityLevel.MEDIUM ) {
//            // List for all the groups that the player's joined IP is in
//            final List<NAltGroup> groups = new ArrayList<>();
//
//            // Adding all the groups that the player's joined IP is in
//            for ( NAltGroup group : this.searchManyByIp( ip ) ) {
//                if ( group.getIps().contains( ip ) ) {
//                    groups.add( group );
//                }
//            }
//
//            // If the groups list is empty, then the player's IP is not in any cached groups
//            if ( groups.size() == 0 ) {
//                // Query the database for the player's previous alt group
//                final Optional<NAltGroup> queryGroup = ApiNetuno.getData().getAlts().queryGroupByUuid( uuid );
//
//                // If the group is in the database, load the group into the cache and add the player to it
//                if ( queryGroup.isPresent() ) {
//                    loadGroupFromDatabase( queryGroup.get(), uuid, ip );
//                }
//
//                // Otherwise we create a new group for the player, add the player to it, and load them into the cache
//                else {
//                    createNewGroup( uuid, ip );
//                }
//            }
//
//            // If groups.size() == 1, then the player's IP is already in one group and we do nothing
//
//            // If the groups list has more than one group, then the player's IP is in multiple groups
//            // So we combine all of these groups into a singular group and save all the edited entries to the database
//            else if ( groups.size() > 1 ) {
//                final NAltGroup baseGroup = groups.get( 0 );
//
//                for ( int index = 1; index <= groups.size(); index++ ) {
//                    combineGroup( baseGroup, groups.get( index ) );
//                    this.cache.remove( groups.get( index ) );
//                }
//
//                // Adding the player's UUID and current IP to the base group, if it doesn't already exist
//                NAltEntry entry = new NAltEntry( uuid.toString(), ip, baseGroup.getGroupId() );
//                if ( baseGroup.containsEntry( entry ) ) {
//                    baseGroup.addEntry( entry );
//                    this.editedCache.add( new Duplex<>( entry, -1 ) );
//                }
//
//                // ? Unsure if the edits to the baseGroup will actually be saved to the instance of it in the cache
//            }
//        }
//
//        else if ( this.securityLevel == AltSecurityLevel.HIGH ) {
//            // List for all the groups that the player or their joined IP is in
//            final List<NAltGroup> groups = new ArrayList<>();
//
//            // Adding all the groups that the player's UUID is in to the list
//            for ( NAltGroup group : this.searchManyByUuid( uuid ) ) {
//                if ( groups.contains( group ) == false ) {
//                    groups.add( group );
//                }
//            }
//
//            // Adding all the groups that the player's IP is in to the list
//            for ( NAltGroup group : this.searchManyByIp( ip ) ) {
//                if ( groups.contains( group ) == false ) {
//                    groups.add( group );
//                }
//            }
//
//            // If the groups list is empty, then the player's UUID nor IP is not in any cached groups
//            if ( groups.size() == 0 ) {
//                // Query the database for the player's previous alt group
//                final Optional<NAltGroup> queryGroup = ApiNetuno.getData().getAlts().queryGroupByUuid( uuid );
//
//                // If the group is in the database, load the group into the cache and add the player to it
//                if ( queryGroup.isPresent() ) {
//                    loadGroupFromDatabase( queryGroup.get(), uuid, ip );
//                }
//
//                // Otherwise we create a new group for the player, add the player to it, and load them into the cache
//                else {
//                    createNewGroup( uuid, ip );
//                }
//            }
//
//            // If groups.size() == 1, then the player OR their IP are already in a singular existing group
//            // So we need to see if the group in the list contains the entry for the player's UUID and current IP
//            // If it doesn't, then we add it and save it to the database
//            else if ( groups.size() == 1 ) {
//                final NAltGroup baseGroup = groups.get( 0 );
//                final NAltEntry entry = new NAltEntry( uuid.toString(), ip, baseGroup.getGroupId() );
//                if ( baseGroup.containsEntry( entry ) == false ) {
//                    baseGroup.addEntry( entry );
//                    this.editedCache.add( new Duplex<>( entry, -1 ) );
//                }
//            }
//
//            // If the groups list has more than one group, then either the player's UUID and/or IP are in multiple groups
//            // So we combine all of these groups into a singular group and save all the edited entries to the database
//            else {
//                final NAltGroup baseGroup = groups.get( 0 );
//
//                for ( int index = 1; index < groups.size(); index++ ) {
//                    combineGroup( baseGroup, groups.get( index ) );
//                    this.cache.remove( groups.get( index ) );
//                }
//
//                // Adding the player's UUID and current IP to the base group, if it doesn't already exist
//                NAltEntry entry = new NAltEntry( uuid.toString(), ip, baseGroup.getGroupId() );
//                if ( baseGroup.containsEntry( entry ) ) {
//                    baseGroup.addEntry( entry );
//                    this.editedCache.add( new Duplex<>( entry, -1 ) );
//                }
//
//                // ? Unsure if the edits to the baseGroup will actually be saved to the instance of it in the cache
//            }
//        }
//    }
//
//    public Optional<NAltGroup> searchByGroupId( int groupId ) {
//        for ( NAltGroup group : cache ) {
//            if ( group.getGroupId() == groupId ) {
//                return Optional.of( group );
//            }
//        }
//        return Optional.empty();
//    }
//
//    public Optional<NAltGroup> searchByUuid( UUID uuid ) {
//        for ( NAltGroup group : cache ) {
//            if ( group.getUuids().contains( uuid ) ) {
//                return Optional.of( group );
//            }
//        }
//        return Optional.empty();
//    }
//
//    public List<NAltGroup> searchManyByUuid( UUID uuid ) {
//        List<NAltGroup> groups = new ArrayList<>();
//        for ( NAltGroup group : cache ) {
//            if ( group.getUuids().contains( uuid ) ) {
//                groups.add( group );
//            }
//        }
//        return groups;
//    }
//
//    public Optional<NAltGroup> searchByIp( String ip ) {
//        for ( NAltGroup group : cache ) {
//            if ( group.getIps().contains( ip ) ) {
//                return Optional.of( group );
//            }
//        }
//        return Optional.empty();
//    }
//
//    public List<NAltGroup> searchManyByIp( String ip ) {
//        List<NAltGroup> groups = new ArrayList<>();
//        for ( NAltGroup group : cache ) {
//            if ( group.getIps().contains( ip ) ) {
//                groups.add( group );
//            }
//        }
//        return groups;
//    }
//
//    //
//    // Methods that are not used in the interface
//    //
//
//    public void loadPlayer( OfflinePlayer player ) {
//        if ( player.isOnline() ) {
//            loadPlayer( player.getUniqueId(), player.getPlayer().getAddress().getAddress().getHostAddress() );
//        }
//        else if ( searchByUuid( player.getUniqueId() ).isPresent() == false ) {
//            ApiNetuno.getData().getAlts().queryGroupByUuid( player.getUniqueId() ).ifPresent( cache::add );
//        }
//    }
//
//    /**
//     * @param level The alt security level to set to
//     */
//    public void setSecurityLevel( AltSecurityLevel level ) {
//        CyberLogUtils.logInfo( "[ALTS CACHE] Alt Strictness Level: " + level.name() );
//        this.securityLevel = level;
//    }
//
//    public void reloadSecurityLevel() {
//        this.securityLevel = switch ( Settings.IPINFO_STRICTNESS.string().toUpperCase() ) {
//            case "LOW" -> AltSecurityLevel.LOW;
//            case "HIGH" -> AltSecurityLevel.HIGH;
//            default -> AltSecurityLevel.MEDIUM;
//        };
//    }
//
//    /**
//     * @return The alt security level
//     */
//    public AltSecurityLevel getSecurityLevel() {
//        return securityLevel;
//    }
//
//    private void combineGroup( NAltGroup baseGroup, NAltGroup group ) {
//        for ( NAltEntry entry : group.getEntries() ) {
//            if ( baseGroup.getEntries().contains( entry ) == false ) {
//                NAltEntry entryCopy = entry.copy();
//                entryCopy.setGroupId( baseGroup.getGroupId() );
//                baseGroup.addEntry( entryCopy );
//                this.editedCache.add( new Duplex<>( entryCopy, entry.getGroupId() ) );
//            }
//        }
//    }
//
//    private void createNewGroup( UUID uuid, String ip ) {
//        NAltGroup newGroup = new NAltGroup( ApiNetuno.getData().getAlts().getNextGroupId() );
//        NAltEntry entry = new NAltEntry( uuid, ip, newGroup.getGroupId() );
//        newGroup.addEntry( entry );
//        this.editedCache.add( new Duplex<>( entry, -1 ) );
//        this.cache.add( newGroup );
//    }
//
//    private void loadGroupFromDatabase( NAltGroup group, UUID uuid, String ip ) {
//        NAltEntry entryAttempt = new NAltEntry( uuid, ip, group.getGroupId() );
//        if ( group.containsEntry( entryAttempt ) == false ) {
//            group.addEntry( entryAttempt );
//            this.editedCache.add( new Duplex<>( entryAttempt, -1 ) );
//        }
//        this.cache.add( group );
//    }
//}