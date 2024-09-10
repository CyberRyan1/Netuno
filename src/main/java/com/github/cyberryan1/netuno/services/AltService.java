package com.github.cyberryan1.netuno.services;

import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPlayer;
import com.github.cyberryan1.netuno.api.services.ApiAltService;
import com.github.cyberryan1.netuno.database.IpListDatabase;
import com.github.cyberryan1.netuno.models.PlayerIpsRecord;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * A service to manage alts. This will load all alt entries from
 * the database, allow updating of alt entries, and more.
 *
 * @author Ryan
 */
public class AltService implements ApiAltService {

    private final List<PlayerIpsRecord> ALL_PLAYERS_JOINED_IPS = new ArrayList<>();

    /**
     * Initializes this service. Note that this will execute a
     * (most likely) large query from the database.
     */
    public void initialize() {
        Map<UUID, List<String>> storedRows = IpListDatabase.getAllEntries();
        for ( Map.Entry<UUID, List<String>> entry : storedRows.entrySet() ) {
            this.ALL_PLAYERS_JOINED_IPS.add( new PlayerIpsRecord( entry.getKey(), entry.getValue() ) );
        }
    }

    /**
     * @param uuid The player's UUID
     * @return A list of all other accounts the provided player
     *         has joined the server with. This list will NOT
     *         contain the provided player
     */
    @Override
    public List<UUID> getAlts( UUID uuid ) {
        // Declaring all lists that we will use
        List<UUID> foundAccounts = new ArrayList<>();
        List<String> searchedIps = new ArrayList<>();
        List<UUID> unsearchedAccounts = new ArrayList<>();
        // Adding this player's UUID to the unsearched accounts
        //      list as a starting point
        unsearchedAccounts.add( uuid );

        while ( unsearchedAccounts.size() > 0 ) {
            // Getting the first element in unsearchedAccounts
            UUID account = unsearchedAccounts.get( 0 );

            // Getting the stored IP record for account
            Optional<PlayerIpsRecord> optionalAccountIpRecord = getPlayerFromIpRecords( account );
            if ( optionalAccountIpRecord.isEmpty() )
                throw new NullPointerException( "Could not find any stored IPs for player with uuid \"" + account.toString() + "\"" );
            PlayerIpsRecord accountIpRecord = optionalAccountIpRecord.get();

            // Adding the account to foundAccounts
            foundAccounts.add( account );

            for ( String storedIp : accountIpRecord.getIps() ) {
                // We want to skip any IPs that have already been searched
                if ( searchedIps.contains( storedIp ) ) continue;
                searchedIps.add( storedIp );

                // Getting a list of all accounts that have joined
                //      the server with storedIp
                List<PlayerIpsRecord> accountsOnStoredIp = getRecordsWithIp( storedIp );
                for ( PlayerIpsRecord rec : accountsOnStoredIp ) {
                    if ( foundAccounts.contains( rec.getPlayer() ) == false && unsearchedAccounts.contains( rec.getPlayer() ) == false ) {
                        unsearchedAccounts.add( rec.getPlayer() );
                    }
                }
            }

            unsearchedAccounts.remove( account );
        }

        return foundAccounts;
    }

    /**
     * @param player The player
     * @return A list of all other accounts the provided player
     *         has joined the server with. This list will NOT
     *         contain the provided player
     */
    @Override
    public List<UUID> getAlts( OfflinePlayer player ) {
        return getAlts( player.getUniqueId() );
    }

    /**
     * Searches for other accounts the provided player has joined
     * the server with. Note that this method is different from
     * the other
     * <code>getAlts()</code> methods as it will load the alt
     * accounts into
     * {@link
     * com.github.cyberryan1.netuno.api.services.ApiNetunoService}'s
     * cache and return the {@link ApiPlayer} associated with
     * each alt. <br><br>
     *
     * <b>IMPORTANT</b> This may cause lag. Recommended to run
     * this async
     *
     * @param player The player
     * @return A list of {@link ApiPlayer}
     */
    @Override
    public CompletableFuture<List<ApiPlayer>> getAlts( ApiPlayer player ) {
        return CompletableFuture.supplyAsync( () -> {
            final List<UUID> ALTS = getAlts( player.getUuid() );
            List<ApiPlayer> toReturn = new ArrayList<>();
            for ( UUID uuid : ALTS ) {
                // Since this is already async, I think we can use
                //      .thenAccept rather than .thenAcceptAsync
                Netuno.SERVICE.getPlayer( uuid ).thenAccept( toReturn::add );
            }
            return toReturn;
        } );
    }

    /**
     * @param ip An IP
     * @return A list of all records from the that contain the
     *         provided IP
     */
    @Override
    public List<UUID> getPlayersWithIp( String ip ) {
        return getRecordsWithIp( ip ).stream()
                .map( PlayerIpsRecord::getPlayer )
                .collect( Collectors.toList() );
    }

    /**
     * @param uuid A player's UUID
     * @return An list of all the IPs the provided player has
     *         joined the server with. If the player has never
     *         joined the server before, an empty list is
     *         returned
     */
    @Override
    public List<String> getPlayerIps( UUID uuid ) {
        return getPlayerFromIpRecords( uuid ).orElseThrow().getIps();
    }

    /**
     * @return A list of all players who have ever joined the
     *         server which maps to a list of all the IPs they
     *         have joined the server with
     */
    @Override
    public Map<UUID, List<String>> getAllPlayersJoinedIps() {
        Map<UUID, List<String>> toReturn = new HashMap<>();

        for ( PlayerIpsRecord pil : this.ALL_PLAYERS_JOINED_IPS ) {
            toReturn.put( pil.getPlayer(), pil.getIps() );
        }

        return toReturn;
    }

    /**
     * Adds a new IP address to the provided player's record. If
     * there are no records for the player, creates a new record
     * for them. This will also update the database to reflect
     * these changes
     *
     * @param uuid The player's UUID
     * @param ip   The IP address
     */
    public void addNewIpAddress( UUID uuid, String ip ) {
        PlayerIpsRecord record = getPlayerFromIpRecords( uuid ).orElse( null );

        // If player has never joined the server before, we create
        //      a new record for them, add it to the record list,
        //      and save it into the database
        if ( record == null ) {
            record = new PlayerIpsRecord( uuid );
            record.addIp( ip );
            this.ALL_PLAYERS_JOINED_IPS.add( record );
            IpListDatabase.saveEntry( uuid, ip );
        }

        // If the record already contains the provided IP, throw
        //      an error
        else if ( record.getIps().contains( ip ) ) {
            throw new IllegalArgumentException( "Player with UUID \"" + uuid.toString()
                    + "\" already has IP address " + ip + " logged" );
        }

        // Otherwise, we add the IP to the player's record and
        //      save it into the database
        else {
            record.addIp( ip );
            IpListDatabase.saveEntry( uuid, ip );
        }
    }

    /**
     * @return A list of {@link PlayerIpsRecord} that is loaded
     *         in this instance
     */
    public List<PlayerIpsRecord> getPlayerIpsRecords() {
        return this.ALL_PLAYERS_JOINED_IPS;
    }

    /**
     * @param ip An IP
     * @return A list of all records from the
     *         {@link #getPlayerIpsRecords()} that contain the
     *         provided IP
     */
    public List<PlayerIpsRecord> getRecordsWithIp( String ip ) {
        return this.ALL_PLAYERS_JOINED_IPS.stream()
                .filter( rec -> rec.getIps().stream()
                        .anyMatch( element -> element.equals( ip ) ) )
                .collect( Collectors.toList() );
    }

    /**
     * @param uuid A player's UUID
     * @return An optional containing the provided player's
     *         {@link PlayerIpsRecord} if it was found in
     *         {@link #getPlayerIpsRecords()}, otherwise returns
     *         an empty optional
     */
    public Optional<PlayerIpsRecord> getPlayerFromIpRecords( UUID uuid ) {
        return this.ALL_PLAYERS_JOINED_IPS.stream()
                .filter( rec -> rec.getPlayer().equals( uuid ) )
                .findFirst();
    }
}