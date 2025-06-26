package com.github.cyberryan1.netuno.services;

import com.github.cyberryan1.netuno.api.services.ApiChatService;
import com.github.cyberryan1.netuno.database.SettingsDatabase;
import com.github.cyberryan1.netuno.utils.settings.Settings;

import java.util.Optional;


/**
 * Service class that manages chat-related settings such as chat
 * disable status and chat slowdown duration. Settings are
 * persisted in the database.
 *
 * @author Ryan
 */
public class ChatService implements ApiChatService {
    
    private boolean chatDisabled = false;
    private int chatSlowdown = 0;

    /**
     * Constructs a new ChatService and loads the current
     * settings from the database. If settings are not found in
     * the database, defaults to chat being enabled with no
     * slowdown.
     */
    public ChatService() {
        Optional<String> opt = SettingsDatabase.getSetting( SettingsDatabase.NAME_CHAT_DISABLED );
        if ( opt.isPresent() ) chatDisabled = Boolean.parseBoolean( opt.get() );
        else chatDisabled = false;
        
        opt = SettingsDatabase.getSetting( SettingsDatabase.NAME_CHAT_SLOW );
        if ( opt.isPresent() ) chatSlowdown = Integer.parseInt( opt.get() );
        else chatSlowdown = Settings.CHATSLOW_DEFAULT_VALUE.integer();
    }

    /**
     * Saves the current chat settings to the database, including
     * both the chat disabled status and chat slowdown duration.
     */
    public void save() {
        SettingsDatabase.saveSetting( SettingsDatabase.NAME_CHAT_DISABLED, String.valueOf( chatDisabled ) );
        SettingsDatabase.saveSetting( SettingsDatabase.NAME_CHAT_SLOW, String.valueOf( chatSlowdown ) );
    }
    
    /**
     * @return True if chat is disabled, false otherwise
     */
    @Override
    public boolean isChatDisabled() {
        return chatDisabled;
    }

    /**
     * @param chatDisabled Whether to disable chat (true) or
     *                     enable it (false)
     */
    @Override
    public void setChatDisabled( boolean chatDisabled ) {
        this.chatDisabled = chatDisabled;
    }

    /**
     * @return The chat slowdown, in seconds
     */
    @Override
    public int getChatSlowdown() {
        return chatSlowdown;
    }

    /**
     * @param chatSlowdown The chat slowdown, in seconds
     */
    @Override
    public void setChatSlowdown( int chatSlowdown ) {
        this.chatSlowdown = chatSlowdown;
    }
}