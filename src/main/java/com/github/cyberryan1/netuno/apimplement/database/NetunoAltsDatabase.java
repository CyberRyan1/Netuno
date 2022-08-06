package com.github.cyberryan1.netuno.apimplement.database;

import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.netuno.apimplement.database.helpers.AltSecurityLevel;
import com.github.cyberryan1.netunoapi.database.AltsDatabase;
import com.github.cyberryan1.netunoapi.models.alts.NAltGroup;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NetunoAltsDatabase implements AltsDatabase {

    private final String TABLE_NAME = "alts";
    private final String TYPE_LIST = "(id, item, type)";
    private final String UNKNOWN_LIST = "(?, ?, ?)";

    private final List<NAltGroup> cache = new ArrayList<>();
    private AltSecurityLevel securityLevel = AltSecurityLevel.HIGH;

    /**
     * @return The cache of punishments
     */
    public List<NAltGroup> getCache() {
        return cache;
    }

    /**
     * @param level The alt security level to set to
     */
    public void setSecurityLevel( AltSecurityLevel level ) {
        CoreUtils.logInfo( "[ALTS CACHE] Alt Strictness Level: " + level.name() );
        this.securityLevel = level;
    }

    /**
     * @return The alt security level
     */
    public AltSecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    /**
     * Initializes the cache
     */
    public void initializeCache() {
        CoreUtils.logInfo( "[ALTS CACHE] Initializing the cache..." );

        CoreUtils.logInfo( "[ALTS CACHE] Getting all IPs and players from the database..." );
        try {
            Statement stmt = ConnectionManager.CONN.createStatement();
            stmt.execute( "SELECT * FROM " + TABLE_NAME + ";" );

            ResultSet rs = stmt.getResultSet();
            while ( rs.next() ) {
                final int groupId = rs.getInt( "id" );
                final String item = rs.getString( "item" );
                final String type = rs.getString( "type" );

                NAltGroup group = cache.stream()
                        .filter( g -> g.getGroupId() == groupId )
                        .findFirst()
                        .orElse( null );
                if ( group == null ) {
                    group = new NAltGroup();
                    group.setGroupId( groupId );
                    cache.add( group );
                }

                if ( type.equalsIgnoreCase( "uuid" ) ) { group.addAlt( item ); }
                else if ( type.equalsIgnoreCase( "ip" ) ) { group.addIp( item ); }
            }

            stmt.close();
            rs.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
        CoreUtils.logInfo( "[ALTS CACHE] Successfully retrieved all IPs and players from the database" );

        CoreUtils.logInfo( "[ALTS CACHE] Loading the alts of all online players..." );
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            loadPlayer( player );
        }
        CoreUtils.logInfo( "[ALTS CACHE] Successfully loaded the alts of " + Bukkit.getOnlinePlayers().size() + " online players" );

        CoreUtils.logInfo( "[ALTS CACHE] Alt cache successfully initialized with a current size of " + cache.size() );
    }

    /**
     * Loads a player's uuid and their current IP into the cache
     * @param player The player to load
     */
    public void loadPlayer( Player player ) {
        loadPlayer( player.getUniqueId().toString(), player.getAddress().getAddress().getHostAddress() );
    }

    /**
     * Loads a player uuid and their current IP into the cache
     * @param playerUuid The player uuid
     * @param playerIp The player ip
     */
    public void loadPlayer( String playerUuid, String playerIp ) {
        if ( securityLevel == AltSecurityLevel.LOW ) {
            List<NAltGroup> groupList = cache.stream()
                    .filter( g -> g.getIpList().contains( playerIp ) )
                    .collect( Collectors.toList() );

            if ( groupList.isEmpty() == false ) {
                groupList.get( 0 ).addAlt( playerUuid );
            }

            else {
                NAltGroup group = new NAltGroup();
                group.setGroupId( getNextAvailableId() );
                group.addIp( playerIp );
                group.addAlt( playerUuid );
                cache.add( group );
            }
        }

        else if ( securityLevel == AltSecurityLevel.MEDIUM ) {
            List<NAltGroup> playerSearch = cache.stream()
                    .filter( g -> g.getAltUuids().contains( playerUuid ) )
                    .collect( Collectors.toList() );

            NAltGroup group;
            if ( playerSearch.isEmpty() == false ) {
                group = merge( playerSearch );
                if ( group.containsIp( playerIp ) == false ) { group.addIp( playerIp ); }

                cache.removeAll( playerSearch );
            }

            else {
                group = new NAltGroup();
                group.setGroupId( getNextAvailableId() );
                group.addIp( playerIp );
                group.addAlt( playerUuid );
            }
            cache.add( group );
        }

        else if ( securityLevel == AltSecurityLevel.HIGH ) {
            List<NAltGroup> groupsSearched = new ArrayList<>();
            List<String> altsSearched = new ArrayList<>();
            List<String> altsToSearch = new ArrayList<>();
            List<String> ipsSearched = new ArrayList<>();
            List<String> ipsToSearch = new ArrayList<>();

            altsToSearch.add( playerUuid );
            ipsToSearch.add( playerIp );

            while ( altsToSearch.size() > 0 || ipsToSearch.size() > 0 ) {
                String altUuid = altsToSearch.size() == 0 ? null : altsToSearch.remove( 0 );
                String altIp = ipsToSearch.size() == 0 ? null : ipsToSearch.remove( 0 );
                if ( altUuid != null ) { altsSearched.add( altUuid ); }
                if ( altIp != null ) { ipsSearched.add( altIp ); }

                cache.stream()
                        .filter( g -> ( altUuid != null && g.getAltUuids().contains( altUuid ) )
                                || ( altIp != null && g.getIpList().contains( altIp ) ) )
                        .distinct()
                        .forEach( group -> {
                            if ( groupsSearched.contains( group ) == false ) { groupsSearched.add( group ); }

                            group.getAltUuids().stream()
                                    .filter( alt -> altsSearched.contains( alt ) == false )
                                    .forEach( altsToSearch::add );
                            group.getIpList().stream()
                                    .filter( ip -> ipsSearched.contains( ip ) == false )
                                    .forEach( ipsToSearch::add );
                        } );
            }

            NAltGroup group = new NAltGroup();
            group.setGroupId( getNextAvailableId() );
            for ( String a : altsSearched ) {
                if ( group.containsAlt( a ) == false ) { group.addAlt( a ); }
            }
            for ( String i : ipsSearched ) {
                if ( group.containsIp( i ) == false ) { group.addIp( i ); }
            }

            cache.removeIf( groupsSearched::contains );
            cache.add( group );
        }
    }

    /**
     * Returns a list of UUID strings of all alts a player
     * has logged in the cache, including their own.
     * @param player The player to get the alts of
     * @return The alts of the player
     */
    public List<String> getAltUuids( OfflinePlayer player ) {
        return cache.stream()
                .map( NAltGroup::getAltUuids )
                .filter( altUuids -> altUuids.contains( player.getUniqueId().toString() ) )
                .findFirst()
                .orElse( new ArrayList<>() );
    }

    /**
     * Returns a list of {@link OfflinePlayer} objects of all
     * alts a player has logged in the cache, including themselves.
     * @param player The player to get the alts of
     * @return The alts of the player
     */
    public List<OfflinePlayer> getAlts( OfflinePlayer player ) {
        return getAltUuids( player ).stream()
                .map( uuid -> Bukkit.getOfflinePlayer( UUID.fromString( uuid ) ) )
                .collect( Collectors.toList() );
    }

    /**
     * Returns the {@link NAltGroup} that contains the given
     * player UUID.
     * @param playerUuid The player UUID to get the group of
     * @return The group of the player
     */
    public NAltGroup getAltGroup( String playerUuid ) {
        return cache.stream()
                .filter( group -> group.containsAlt( playerUuid ) )
                .findFirst()
                .orElse( null );
    }

    /**
     * Saves all elements from the cache into the database. Note that
     * first this method deletes all entries from the database and then
     * it inserts all elements from the cache into the database.
     */
    public void saveAll() {
        CoreUtils.logInfo( "[ALTS CACHE] Saving all elements in the alt cache to the database..." );
        deleteAll();

        int count = 0;
        for ( NAltGroup group : cache ) {
            for ( String ip : group.getIpList() ) { saveIp( group.getGroupId(), ip ); count++; }
            for ( String uuid : group.getAltUuids() ) { saveUuid( group.getGroupId(), uuid ); count++; }
        }

        CoreUtils.logInfo( "[ALTS CACHE] Successfully saved " + cache.size()
                + " different alt groups containing " + count + " IPs and UUIDs to the database" );
    }

    /**
     * Saves an ip into the database for a given group id
     * @param groupId The group id
     * @param ipAddress The ip address
     */
    public void saveIp( int groupId, String ipAddress ) {
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "INSERT INTO " + TABLE_NAME + "(id,item,type) VALUES (?,?,?);" );
            ps.setInt( 1, groupId );
            ps.setString( 2, ipAddress );
            ps.setString( 3, "ip" );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Saves an uuid into the database for a given group id
     * @param groupId The group id
     * @param uuid The uuid
     */
    public void saveUuid( int groupId, String uuid ) {
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "INSERT INTO " + TABLE_NAME + "(id,item,type) VALUES(?,?,?);" );
            ps.setInt( 1, groupId );
            ps.setString( 2, uuid );
            ps.setString( 3, "uuid" );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Deletes all entries from the database
     */
    public void deleteAll() {
        try {
            Statement stmt = ConnectionManager.CONN.createStatement();
            stmt.execute( "DELETE FROM " + TABLE_NAME + ";" );
            stmt.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Merges a list of {@link NAltGroup} into one. The returned
     * alt group's group ID is the lowest group ID of the given
     * list.
     * @param groupList The list of alt groups
     * @return The merged alt group
     */
    private NAltGroup merge( List<NAltGroup> groupList ) {
        if ( groupList.size() == 1 ) { return groupList.get( 0 ); }
        NAltGroup toReturn = new NAltGroup();
        int lowestId = Integer.MAX_VALUE;

        for ( NAltGroup group : groupList ) {
            group.getAltUuids().forEach( altUuid -> {
                if ( toReturn.containsAlt( altUuid ) == false ) {
                    toReturn.addAlt( altUuid );
                }
            } );
            group.getIpList().forEach( ip -> {
                if ( toReturn.containsIp( ip ) == false ) {
                    toReturn.addIp( ip );
                }
            } );

            if ( group.getGroupId() < lowestId ) { lowestId = group.getGroupId(); }
        }

        toReturn.setGroupId( lowestId );
        return toReturn;
    }

    /**
     * @return The next available group id
     */
    private int getNextAvailableId() {
        int toReturn = cache.size() + 1;
        boolean continueWhile = true;

        while ( continueWhile ) {
            final int x = toReturn;
            if ( cache.stream().anyMatch( g -> g.getGroupId() == x ) ) { toReturn++; }
            else { continueWhile = false; }
        }

        return toReturn;
    }
}