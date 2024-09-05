//package com.github.cyberryan1.netuno.models;
//
//import com.github.cyberryan1.netuno.api.services.ApiPlayerService;
//import com.github.cyberryan1.netuno.models.helpers.PlayerLoginLogoutCache;
//import org.bukkit.OfflinePlayer;
//
//import java.util.UUID;
//import java.util.concurrent.CompletableFuture;
//
//// TODO header javadoc
//public class PlayerService implements ApiPlayerService {
//
//    private final PlayerLoginLogoutCache<NPlayer> CACHE = new PlayerLoginLogoutCache<>();
//
//    public PlayerService() {
//        this.CACHE.setLoginScript( event -> {
//            // TODO
//        } );
//        this.CACHE.setUpdateScript( ( uuid, data ) -> {
//            // TODO
//        } );
//        this.CACHE.setRemovalScript( ( uuid, data ) -> {
//            // TODO
//        } );
//    }
//
//    /**
//     * @param player A player
//     * @return The player and all of their Netuno data
//     */
//    @Override
//    public CompletableFuture<ApiPlayerService> getPlayer( OfflinePlayer player ) {
//        return getPlayer( player.getUniqueId() );
//    }
//
//    /**
//     * @param uuid A player's uuid
//     * @return The player and all of their Netuno data
//     */
//    @Override
//    public CompletableFuture<ApiPlayerService> getPlayer( UUID uuid ) {
//        // TODO
//    }
//}