package com.github.cyberryan1.netuno.apimplement.models.alts.redo;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.netuno.apimplement.database.helpers.AltSecurityLevel;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netunoapi.models.alts.TempAltCache;
import com.github.cyberryan1.netunoapi.models.alts.TempAltGroup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class NNetunoAltsCache implements TempAltCache {

    private AltSecurityLevel securityLevel = AltSecurityLevel.HIGH;
    private final Cache<Integer, TempAltGroup> CACHE = Caffeine.newBuilder()
            .maximumSize( 1000 )
            .expireAfterAccess( 30, TimeUnit.MINUTES )
            .build();

    public void initialize() {
        CyberLogUtils.logInfo( "[ALTS CACHE] Initializing the alts cache..." );

        for ( Player player : Bukkit.getOnlinePlayers() ) {
            loadPlayer( player.getUniqueId(), player.getAddress().getAddress().getHostAddress() );
        }

        CyberLogUtils.logInfo( "[ALTS CACHE] Loaded a total of " + CACHE.estimatedSize() + " alt groups" );
    }

    public void save() {

    }

    public void loadPlayer( UUID uuid, String ip ) {
        // * From here out, we are saying fuck the security level system

        final Set<TempAltGroup> knownGroups = new HashSet<>();
        // Giving some initial data to start the search from
        knownGroups.addAll( multiSearchByIp( ip ) );
        knownGroups.addAll( multiSearchByUuid( uuid ) );

        // Creating a list where all groups will go into, even if
        //      they have already been found and are in the
        //      knownGroups variable
        final Set<TempAltGroup> resultantGroups = new HashSet<>();
        boolean addedUnique = true;
        while ( addedUnique ) {
            // Clearing any previously used variables
            resultantGroups.clear();

            // For each IP and UUID within each group in the knownGroups variable,
            //      doing a multisearch on them and adding the results into
            //      one list variable
            for ( TempAltGroup group : knownGroups ) {
                resultantGroups.add( group );
                for ( String i : group.getIpList() ) {
                    resultantGroups.addAll( multiSearchByIp( i ) );
                }
                for ( UUID u : group.getUuidList() ) {
                    resultantGroups.addAll( multiSearchByUuid( u ) );
                }
            }

            // Adding all the resultant groups to the known groups
            // If this yields false, then there were no unique groups
            //      added to the known group list, therefore we can
            //      end the search
            addedUnique = knownGroups.addAll( resultantGroups );
        }

        TempAltGroup groupToLoad = knownGroups.stream().findAny().orElseThrow( NullPointerException::new );
        if ( knownGroups.size() > 1 ) { groupToLoad = combineGroups( groupToLoad, knownGroups ); }
    }

    public Optional<TempAltGroup> searchByGroupId( int i ) {

    }

    public Optional<TempAltGroup> searchByUuid( UUID uuid ) {

    }

    public List<TempAltGroup> multiSearchByUuid( UUID uuid ) {

    }

    public Optional<TempAltGroup> searchByIp( String ip ) {

    }

    public List<TempAltGroup> multiSearchByIp( String ip ) {

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

    // Returns a CLONE of mainGroup variable, with the created edits
    private TempAltGroup combineGroups( TempAltGroup mainGroup, Set<TempAltGroup> otherGroups ) {
        TempAltGroup returning = new TempAltGroup( mainGroup.getGroupId() );
        Set<String> ips = new HashSet<>( mainGroup.getIpList() );
        Set<UUID> uuids = new HashSet<>( mainGroup.getUuidList() );

        for ( TempAltGroup group : otherGroups ) {
            if ( group.equals( mainGroup ) ) { continue; }
            for ( String ip : group.getIpList() ) {
                if ( returning.getIpList().contains( ip ) == false ) {
                    returning.getIpList().add( ip );
                }
            }

            for ( UUID uuid : group.getUuidList() ) {
                if ( returning.getUuidList().contains( uuid ) == false ) {
                    returning.getUuidList().add( uuid );
                }
            }
        }

        returning.getIpList().addAll( ips );
        returning.getUuidList().addAll( uuids );
        return returning;
    }
}
