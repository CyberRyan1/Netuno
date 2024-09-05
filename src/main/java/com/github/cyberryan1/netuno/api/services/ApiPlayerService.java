//package com.github.cyberryan1.netuno.api.services;
//
//import org.bukkit.OfflinePlayer;
//
//import java.util.UUID;
//import java.util.concurrent.CompletableFuture;
//
///**
// * Used to query for players and view/edit their data
// *
// * @author Ryan
// */
//public interface ApiPlayerService {
//
//    /**
//     * @param player A player
//     * @return The player and all of their Netuno data
//     */
//    CompletableFuture<ApiPlayerService> getPlayer( OfflinePlayer player );
//
//    /**
//     * @param uuid A player's uuid
//     * @return The player and all of their Netuno data
//     */
//    CompletableFuture<ApiPlayerService> getPlayer( UUID uuid );
//}