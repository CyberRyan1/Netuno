package com.github.cyberryan1.netuno.utils.settings;

import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * A factory for filling in all the variables in a setting message
 * with the correct information. Can be used to retrieve the
 * final message, or you can send the message directly. <br>
 * This will only work for settings that have a value type of
 * {@link SettingsEntry.EntryType#STRING} or
 * {@link SettingsEntry.EntryType#STRING_LIST}
 *
 * @author Ryan
 */
public class SettingsVariableFactory {

    private final SettingsEntry.EntryType type;

    private String message = null;
    private String[] messageList = null;

    /**
     * @param setting The setting to work with
     * @throws IllegalArgumentException if the setting does not
     * have a value type of {@link SettingsEntry.EntryType#STRING}
     * or {@link SettingsEntry.EntryType#STRING_LIST}
     */
    public SettingsVariableFactory( Settings setting ) {
        type = setting.getValueType();

        if ( type == SettingsEntry.EntryType.STRING ) {
            message = setting.string();
        }
        else if ( type == SettingsEntry.EntryType.STRING_LIST ) {
            this.messageList = new String[setting.stringlist().length];
            for ( int j = 0; j < setting.stringlist().length; j++ ) {
                messageList[j] = setting.stringlist()[j];
            }
        }
        else throw new IllegalArgumentException( "Settings variable type must have a value type of " +
                    "SettingsEntry.EntryType.STRING or SettingsEntry.EntryType.STRING_LIST" );
    }

    private void replace( String variable, String value ) {
        if ( type == SettingsEntry.EntryType.STRING ) {
            message = message.replace( variable, value );
        } else if ( type == SettingsEntry.EntryType.STRING_LIST ) {
            for ( int i = 0; i < messageList.length; i++ ) {
                messageList[i] = messageList[i].replace( variable, value );
            }
        }
    }

    public SettingsVariableFactory player( OfflinePlayer player ) {
        replace( "[PLAYER]", player.getName() );
        return this;
    }

    public SettingsVariableFactory player( String name ) {
        replace( "[PLAYER]", name );
        return this;
    }

    public SettingsVariableFactory staff( OfflinePlayer player ) {
        replace( "[STAFF]", player.getName() );
        return this;
    }

    public SettingsVariableFactory staff( String name ) {
        replace( "[STAFF]", name );
        return this;
    }

    public SettingsVariableFactory target( OfflinePlayer player ) {
        replace( "[TARGET]", player.getName() );
        return this;
    }

    public SettingsVariableFactory target( String name ) {
        replace( "[TARGET]", name );
        return this;
    }

    public SettingsVariableFactory reason( String reason ) {
        replace( "[REASON]", reason );
        return this;
    }

    public SettingsVariableFactory remain( String remain ) {
        replace( "[REMAIN]", remain );
        return this;
    }

    public SettingsVariableFactory length( String length ) {
        replace( "[LENGTH]", length );
        return this;
    }

    public String getMsg() {
        return message;
    }

    public String getColoredMsg() {
        return CyberColorUtils.getColored( message );
    }

    public String[] getMsgList() {
        return messageList;
    }

    public String[] getColoredMsgList() {
        return CyberColorUtils.getColored( messageList );
    }

    /**
     * Sends the message to the player. A message will not be
     * sent if the message is blank
     * @param player The command sender to send the message to
     */
    public void sendMsg( CommandSender player ) {
        sendMsg( player, false );
    }

    /**
     * @param player The command sender to send the message to
     * @param sendIfBlank True to send the message to the
     *                    command sender even if it is all
     *                    blank, false otherwise
     */
    public void sendMsg( CommandSender player, boolean sendIfBlank ) {
        if ( sendIfBlank == false && isBlank() ) return;
        if ( type == SettingsEntry.EntryType.STRING ) CyberMsgUtils.sendMsg( player, getColoredMsg() );
        else CyberMsgUtils.sendMsg( player, getColoredMsgList() );
    }

    /**
     * Sends the message to all players who satisfy the provided
     * predicate. A message will not be sent if the message is
     * blank
     * @param predicate The predicate of players to send the
     *                  message to
     */
    public void sendMsg( Predicate<? super Player> predicate ) {
        sendMsg( predicate, false );
    }

    /**
     * Sends the message to all players who satisfy the provided
     * predicate.
     * @param predicate The predicate of players to send the
     *                  message to
     * @param sendIfBlank True to send the message to the
     *                    command sender even if it is all
     *                    blank, false otherwise
     */
    public void sendMsg( Predicate<? super Player> predicate, boolean sendIfBlank ) {
        if ( sendIfBlank == false && isBlank() ) { return; }
        if ( type == SettingsEntry.EntryType.STRING ) CyberMsgUtils.broadcast( getColoredMsg(), predicate );
        else CyberMsgUtils.broadcast( Arrays.asList( getColoredMsgList() ), predicate );
    }

    private boolean isBlank() {
        if ( type == SettingsEntry.EntryType.STRING ) {
            return message.isBlank();
        }

        for ( String s : messageList ) {
            if ( s.isBlank() == false ) return false;
        }
        return true;
    }
}