package com.github.cyberryan1.netuno.api.services;

import java.util.List;

/**
 * Used for the settings related to chat, i.e. if chat is
 * disabled or the chat slow duration
 *
 * @author Ryan
 */
public interface ApiChatService {

    /**
     * @return True if chat is disabled, false otherwise
     */
    boolean isChatDisabled();

    /**
     * @param chatDisabled Whether to disable chat (true) or
     *                     enable it (false)
     */
    void setChatDisabled( boolean chatDisabled );

    /**
     * @return The chat slowdown, in seconds
     */
    int getChatSlowdown();

    /**
     * @param chatSlowdown The chat slowdown, in seconds
     */
    void setChatSlowdown( int chatSlowdown );

    /**
     * @return The watchlist
     */
    List<String> getWatchlist();
}