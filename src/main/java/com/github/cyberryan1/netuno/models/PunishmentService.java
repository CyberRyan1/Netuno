//package com.github.cyberryan1.netuno.models;
//
//import com.github.cyberryan1.netuno.api.models.ApiPunishment;
//import com.github.cyberryan1.netuno.api.services.ApiPunishmentService;
//import com.github.cyberryan1.netuno.database.PunishmentsDatabase;
//import org.bukkit.OfflinePlayer;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//import java.util.concurrent.CompletableFuture;
//import java.util.stream.Collectors;
//
///**
// * A way to manage punishments. Also comes with a cache to
// * reduce database queries <br><br>
// *
// * <i>Note: the adding and removing of the cache here is
// * managed within the {@link PlayerService} class, which
// * uses {@link com.github.cyberryan1.netuno.models.helpers.PlayerLoginLogoutCache}
// * to manage its own cache. Whenever a player is loaded from
// * that cache, their punishments are loaded into this cache,
// * and when a player is removed from that cache, their
// * punishments are removed from this cache</i>
// *
// * @author Ryan
// */
//public class PunishmentService implements ApiPunishmentService {
//
//    final List<Punishment> CACHE = new ArrayList<>();
//
//    /**
//     * Searches through a cache of punishments first. If nothing
//     * is found, then queries the database.
//     *
//     * @param id A punishment ID
//     * @return The punishment with the given id, empty otherwise
//     */
//    @Override
//    public CompletableFuture<Optional<ApiPunishment>> getPunishment( int id ) {
//        for ( Punishment pun : this.CACHE ) {
//            if ( pun.getId() == id ) return CompletableFuture.supplyAsync( () -> Optional.of( pun ) );
//        }
//        return CompletableFuture.supplyAsync( () -> Optional.ofNullable( PunishmentsDatabase.getPunishment( id ) ) );
//    }
//
//    /**
//     * Searches through a cache of punishments first. If nothing
//     * is found, then queries the database.
//     *
//     * @param uuid A player's uuid
//     * @return A list of punishments the given player has
//     */
//    @Override
//    public CompletableFuture<List<ApiPunishment>> getPunishments( UUID uuid ) {
//        List<ApiPunishment> toReturn = this.CACHE.stream()
//                .filter( pun -> pun.getPlayerUuid().equals( uuid ) )
//                .map( pun -> ( ApiPunishment ) pun )
//                .collect( Collectors.toList() );
//        if ( toReturn.isEmpty() == false ) return CompletableFuture.supplyAsync( () -> toReturn );
//
//        return CompletableFuture.supplyAsync( () ->
//                convertPunishmentsToApiPunishments( PunishmentsDatabase.getPunishments( uuid.toString() ) ) );
//    }
//
//    /**
//     * Searches through a cache of punishments first. If nothing
//     * is found, then queries the database.
//     *
//     * @param player A player
//     * @return A list of punishments the given player has
//     */
//    @Override
//    public CompletableFuture<List<ApiPunishment>> getPunishments( OfflinePlayer player ) {
//        return getPunishments( player.getUniqueId() );
//    }
//
//    /**
//     * This will always have to query the database, as searching
//     * a cache can mean that some elements will drop out of the
//     * cache while others remain within it, causing a nightmare
//     *
//     * @param referenceId A reference ID
//     * @return A list of punishments that have the given reference ID
//     */
//    public CompletableFuture<List<ApiPunishment>> getPunishmentsByReferenceId( int referenceId ) {
//        return CompletableFuture.supplyAsync( () ->
//                convertPunishmentsToApiPunishments( PunishmentsDatabase.getPunishmentsFromReference( referenceId ) ) );
//    }
//
//    @Override
//    public PunishmentBuilder punishmentBuilder() {
//        return new PunBuilder();
//    }
//
//    static class PunBuilder implements PunishmentBuilder {
//
//        private Punishment punishment;
//
//        public PunBuilder() {
//            punishment = new Punishment();
//        }
//
//        @Override
//        public PunishmentBuilder setPlayer( UUID uuid ) {
//            punishment.setPlayer( uuid );
//            return this;
//        }
//
//        @Override
//        public PunishmentBuilder setPlayer( OfflinePlayer player ) {
//            punishment.setPlayer( player.getUniqueId() );
//            return this;
//        }
//
//        @Override
//        public PunishmentBuilder setStaff( UUID uuid ) {
//            punishment.setStaff( uuid );
//            return this;
//        }
//
//        @Override
//        public PunishmentBuilder setStaff( OfflinePlayer player ) {
//            punishment.setStaff( player.getUniqueId() );
//            return this;
//        }
//
//        @Override
//        public PunishmentBuilder setType( ApiPunishment.PunType type ) {
//            punishment.setType( type );
//            return this;
//        }
//
//        @Override
//        public PunishmentBuilder setLength( long length ) {
//            punishment.setLength( length );
//            return this;
//        }
//
//        @Override
//        public PunishmentBuilder setReason( String reason ) {
//            punishment.setReason( reason );
//            return this;
//        }
//
//        @Override
//        public ApiPunishment build() {
//            return punishment;
//        }
//    }
//
//    private List<ApiPunishment> convertPunishmentsToApiPunishments( List<Punishment> list ) {
//        return list.stream()
//                .map( p -> ( ApiPunishment ) p )
//                .collect( Collectors.toList() );
//    }
//}
