package com.github.cyberryan1.netuno.models;

import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPlayer;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.database.PunishmentsDatabase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Represents a player and all of their respective Netuno data
 *
 * @author Ryan
 */
public class NPlayer implements ApiPlayer {

    private final UUID uuid;

    private List<ApiPunishment> loadedPunishments = new ArrayList<>();

    /**
     * Note that after this constructor is finished,
     * {@link #reloadData()} is called. You may want to
     * run this constructor async to avoid lag
     * @param uuid The UUID of the player
     */
    public NPlayer( UUID uuid ) {
        this.uuid = uuid;

        reloadData();
    }

    /**
     * Note that after this constructor is finished,
     * {@link #reloadData()} is called. You may want to
     * run this constructor async to avoid lag
     * @param player The player
     */
    public NPlayer( OfflinePlayer player ) {
        this( player.getUniqueId() );
    }

    /**
     * @return The UUID of the player represented
     */
    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * @return The player represented
     */
    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer( this.uuid );
    }

    /**
     * Reloads the data for this player. Should be
     * ran async to avoid lag.
     */
    public void reloadData() {
        // Loading punishments
        this.loadedPunishments.clear();
        this.loadedPunishments.addAll( PunishmentsDatabase.getPunishments( uuid.toString() ) );
    }

    /**
     * @return A list of all known accounts this player has
     *         joined the server with. The list will NOT contain
     *         this player
     */
    @Override
    public List<UUID> getAlts() {
        // Declaring all lists that we will use
        List<UUID> foundAccounts = new ArrayList<>();
        List<String> searchedIps = new ArrayList<>();
        List<UUID> unsearchedAccounts = new ArrayList<>();
        // Adding this player's UUID to the unsearched accounts
        //      list as a starting point
        unsearchedAccounts.add( this.uuid );

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
     * @param ip An IP
     * @return A list of all records from the
     *         {@link NetunoService#getPlayerIpsRecords()} that
     *         contain the provided IP
     */
    private List<PlayerIpsRecord> getRecordsWithIp( String ip ) {
        return Netuno.SERVICE.getPlayerIpsRecords().stream()
                .filter( rec -> rec.getIps().stream()
                        .anyMatch( element -> element.equals( ip ) ) )
                .collect( Collectors.toList() );
    }

    /**
     * @param uuid A player's UUID
     * @return An optional containing the provided player's
     *         {@link PlayerIpsRecord} if it was found in
     *         {@link NetunoService#getPlayerIpsRecords()},
     *         otherwise returns an empty optional
     */
    private Optional<PlayerIpsRecord> getPlayerFromIpRecords( UUID uuid ) {
        return Netuno.SERVICE.getPlayerIpsRecords().stream()
                .filter( rec -> rec.getPlayer().equals( uuid ) )
                .findFirst();
    }

    /**
     * @return List of all punishments of this player
     */
    @Override
    public List<ApiPunishment> getPunishments() {
        return this.loadedPunishments;
    }

    /**
     * @return List of all active punishments of this player
     */
    @Override
    public List<ApiPunishment> getActivePunishments() {
        return this.loadedPunishments.stream().filter( ApiPunishment::isActive ).collect( Collectors.toList() );
    }
}