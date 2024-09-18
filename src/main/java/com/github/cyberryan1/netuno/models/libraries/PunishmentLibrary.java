package com.github.cyberryan1.netuno.models.libraries;

import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.models.Punishment;
import com.github.cyberryan1.netuno.utils.settings.Settings;

import java.util.List;

/**
 * A library for punishments
 *
 * @author Ryan
 */
public class PunishmentLibrary {

    /**
     * @param list A list of punishments
     * @return The punishment with the highest duration remaining
     *         from the provided list
     */
    public static Punishment getPunishmentWithHighestDurationRemaining( List<Punishment> list ) {
        Punishment highest = list.get( 0 );
        for ( int index = 1; index < list.size() && highest.getDurationRemaining() != ApiPunishment.PERMANENT_PUNISHMENT_LENGTH; index++ ) {
            if ( list.get( index ).getDurationRemaining() == ApiPunishment.PERMANENT_PUNISHMENT_LENGTH ) return list.get( index );
            if ( highest.getDurationRemaining() < list.get( index ).getDurationRemaining() ) {
                highest = list.get( index );
            }
        }
        return highest;
    }

    /**
     * @param list A list of punishments
     * @return The punishment with the highest length from the
     *         provided list
     */
    public static Punishment getPunishmentWithHighestOriginalLength( List<Punishment> list ) {
        Punishment highest = list.get( 0 );
        for ( int index = 1; index < list.size() && highest.getLength() != ApiPunishment.PERMANENT_PUNISHMENT_LENGTH; index++ ) {
            if ( list.get( index ).getLength() == ApiPunishment.PERMANENT_PUNISHMENT_LENGTH ) return list.get( index );
            if ( highest.getLength() < list.get( index ).getLength() ) {
                highest = list.get( index );
            }
        }
        return highest;
    }

    /**
     * Gets a settings message of the provided punishment type
     * for the specific setting type
     *
     * @param punishmentType The punishment type
     * @param settingType    The setting type
     * @return A {@link Settings} element
     */
    public static Settings getSettingForMessageType( ApiPunishment.PunType punishmentType, MessageSetting settingType ) {
        return switch ( punishmentType ) {
            case WARN -> {
                yield switch ( settingType ) {
                    case PERMISSION -> Settings.WARN_PERMISSION;
                    case BROADCAST -> Settings.WARN_BROADCAST;
                    case STAFF_BROADCAST ->
                            Settings.WARN_STAFF_BROADCAST;
                    case MESSAGE -> Settings.WARN_MESSAGE;
                    case ATTEMPT -> null;
                    case JOIN_NOTIFICATION ->
                            Settings.WARN_MESSAGE;
                    case EXPIRE -> null;
                    case EXPIRE_STAFF -> null;
                    case SOUND_TARGET ->
                            Settings.SOUND_PUNISHMENT_WARN_TARGET;
                    case SOUND_GLOBAL ->
                            Settings.SOUND_PUNISHMENT_WARN_GLOBAL;
                    case SOUND_STAFF ->
                            Settings.SOUND_PUNISHMENT_WARN_STAFF;
                };
            }

            case KICK -> {
                yield switch ( settingType ) {
                    case PERMISSION -> Settings.KICK_PERMISSION;
                    case BROADCAST -> Settings.KICK_BROADCAST;
                    case STAFF_BROADCAST ->
                            Settings.KICK_STAFF_BROADCAST;
                    case MESSAGE -> Settings.KICK_KICKED_LINES;
                    case ATTEMPT -> null;
                    case JOIN_NOTIFICATION -> null;
                    case EXPIRE -> null;
                    case EXPIRE_STAFF -> null;
                    case SOUND_TARGET -> null;
                    case SOUND_GLOBAL ->
                            Settings.SOUND_PUNISHMENT_KICK_GLOBAL;
                    case SOUND_STAFF ->
                            Settings.SOUND_PUNISHMENT_KICK_STAFF;
                };
            }

            case MUTE -> {
                yield switch ( settingType ) {
                    case PERMISSION -> Settings.MUTE_PERMISSION;
                    case BROADCAST -> Settings.MUTE_BROADCAST;
                    case STAFF_BROADCAST ->
                            Settings.MUTE_STAFF_BROADCAST;
                    case MESSAGE -> Settings.MUTE_MESSAGE;
                    case ATTEMPT -> Settings.MUTE_ATTEMPT;
                    case JOIN_NOTIFICATION ->
                            Settings.MUTE_ATTEMPT;
                    case EXPIRE -> Settings.MUTE_EXPIRE;
                    case EXPIRE_STAFF ->
                            Settings.MUTE_EXPIRE_STAFF;
                    case SOUND_TARGET ->
                            Settings.SOUND_PUNISHMENT_MUTE_TARGET;
                    case SOUND_GLOBAL ->
                            Settings.SOUND_PUNISHMENT_MUTE_GLOBAL;
                    case SOUND_STAFF ->
                            Settings.SOUND_PUNISHMENT_MUTE_STAFF;
                };
            }

            case UNMUTE -> {
                yield switch ( settingType ) {
                    case PERMISSION ->
                            Settings.UNMUTE_PERMISSION;
                    case BROADCAST -> Settings.UNMUTE_BROADCAST;
                    case STAFF_BROADCAST ->
                            Settings.UNMUTE_STAFF_BROADCAST;
                    case MESSAGE -> Settings.UNMUTE_MESSAGE;
                    case ATTEMPT -> null;
                    case JOIN_NOTIFICATION ->
                            Settings.UNMUTE_MESSAGE;
                    case EXPIRE -> null;
                    case EXPIRE_STAFF -> null;
                    case SOUND_TARGET ->
                            Settings.SOUND_PUNISHMENT_UNMUTE_TARGET;
                    case SOUND_GLOBAL ->
                            Settings.SOUND_PUNISHMENT_UNMUTE_GLOBAL;
                    case SOUND_STAFF ->
                            Settings.SOUND_PUNISHMENT_UNMUTE_STAFF;
                };
            }

            case BAN -> {
                yield switch ( settingType ) {
                    case PERMISSION -> Settings.BAN_PERMISSION;
                    case BROADCAST -> Settings.BAN_BROADCAST;
                    case STAFF_BROADCAST ->
                            Settings.BAN_STAFF_BROADCAST;
                    case MESSAGE -> Settings.BAN_MESSAGE;
                    case ATTEMPT -> Settings.BAN_ATTEMPT;
                    case JOIN_NOTIFICATION -> null;
                    case EXPIRE -> Settings.BAN_EXPIRE;
                    case EXPIRE_STAFF ->
                            Settings.BAN_EXPIRE_STAFF;
                    case SOUND_TARGET -> null;
                    case SOUND_GLOBAL ->
                            Settings.SOUND_PUNISHMENT_BAN_GLOBAL;
                    case SOUND_STAFF ->
                            Settings.SOUND_PUNISHMENT_BAN_STAFF;
                };
            }

            case UNBAN -> {
                yield switch ( settingType ) {
                    case PERMISSION -> Settings.UNBAN_PERMISSION;
                    case BROADCAST -> Settings.UNBAN_BROADCAST;
                    case STAFF_BROADCAST ->
                            Settings.UNBAN_STAFF_BROADCAST;
                    case MESSAGE -> Settings.UNBAN_MESSAGE;
                    case ATTEMPT -> null;
                    case JOIN_NOTIFICATION ->
                            Settings.UNBAN_MESSAGE;
                    case EXPIRE -> null;
                    case EXPIRE_STAFF -> null;
                    case SOUND_TARGET -> null;
                    case SOUND_GLOBAL ->
                            Settings.SOUND_PUNISHMENT_UNBAN_GLOBAL;
                    case SOUND_STAFF ->
                            Settings.SOUND_PUNISHMENT_UNBAN_STAFF;
                };
            }

            case IPMUTE -> {
                yield switch ( settingType ) {
                    case PERMISSION ->
                            Settings.IPMUTE_PERMISSION;
                    case BROADCAST -> Settings.IPMUTE_BROADCAST;
                    case STAFF_BROADCAST ->
                            Settings.IPMUTE_STAFF_BROADCAST;
                    case MESSAGE -> Settings.IPMUTE_MESSAGE;
                    case ATTEMPT -> Settings.IPMUTE_ATTEMPT;
                    case JOIN_NOTIFICATION ->
                            Settings.IPMUTE_ATTEMPT;
                    case EXPIRE -> Settings.IPMUTE_EXPIRE;
                    case EXPIRE_STAFF ->
                            Settings.IPMUTE_EXPIRE_STAFF;
                    case SOUND_TARGET ->
                            Settings.SOUND_PUNISHMENT_IPMUTE_TARGET;
                    case SOUND_GLOBAL ->
                            Settings.SOUND_PUNISHMENT_IPMUTE_GLOBAL;
                    case SOUND_STAFF ->
                            Settings.SOUND_PUNISHMENT_IPMUTE_STAFF;
                };
            }

            case UNIPMUTE -> {
                yield switch ( settingType ) {
                    case PERMISSION ->
                            Settings.UNIPMUTE_PERMISSION;
                    case BROADCAST ->
                            Settings.UNIPMUTE_BROADCAST;
                    case STAFF_BROADCAST ->
                            Settings.UNIPMUTE_STAFF_BROADCAST;
                    case MESSAGE -> Settings.UNIPMUTE_MESSAGE;
                    case ATTEMPT -> null;
                    case JOIN_NOTIFICATION ->
                            Settings.UNIPMUTE_MESSAGE;
                    case EXPIRE -> null;
                    case EXPIRE_STAFF -> null;
                    case SOUND_TARGET ->
                            Settings.SOUND_PUNISHMENT_IPMUTE_TARGET;
                    case SOUND_GLOBAL ->
                            Settings.SOUND_PUNISHMENT_IPMUTE_GLOBAL;
                    case SOUND_STAFF ->
                            Settings.SOUND_PUNISHMENT_IPMUTE_STAFF;
                };
            }

            case IPBAN -> {
                yield switch ( settingType ) {
                    case PERMISSION -> Settings.IPBAN_PERMISSION;
                    case BROADCAST -> Settings.IPBAN_BROADCAST;
                    case STAFF_BROADCAST ->
                            Settings.IPBAN_STAFF_BROADCAST;
                    case MESSAGE -> Settings.IPBAN_MESSAGE;
                    case ATTEMPT -> Settings.IPBAN_ATTEMPT;
                    case JOIN_NOTIFICATION -> null;
                    case EXPIRE -> Settings.IPBAN_EXPIRE;
                    case EXPIRE_STAFF ->
                            Settings.IPBAN_EXPIRE_STAFF;
                    case SOUND_TARGET -> null;
                    case SOUND_GLOBAL ->
                            Settings.SOUND_PUNISHMENT_IPBAN_GLOBAL;
                    case SOUND_STAFF ->
                            Settings.SOUND_PUNISHMENT_IPBAN_STAFF;
                };
            }

            case UNIPBAN -> {
                yield switch ( settingType ) {
                    case PERMISSION ->
                            Settings.UNIPBAN_PERMISSION;
                    case BROADCAST -> Settings.UNIPBAN_BROADCAST;
                    case STAFF_BROADCAST ->
                            Settings.UNIPBAN_STAFF_BROADCAST;
                    case MESSAGE -> Settings.UNIPBAN_MESSAGE;
                    case ATTEMPT -> null;
                    case JOIN_NOTIFICATION ->
                            Settings.UNIPBAN_MESSAGE;
                    case EXPIRE -> null;
                    case EXPIRE_STAFF -> null;
                    case SOUND_TARGET -> null;
                    case SOUND_GLOBAL ->
                            Settings.SOUND_PUNISHMENT_UNIPBAN_GLOBAL;
                    case SOUND_STAFF ->
                            Settings.SOUND_PUNISHMENT_UNIPBAN_STAFF;
                };
            }
        };
    }

    public enum MessageSetting {
        /**
         * The permission to execute this punishment
         */
        PERMISSION,

        /**
         * The broadcast sent to all non-staff players when this
         * type of punishment is executed
         */
        BROADCAST,

        /**
         * The broadcast sent to all players when this type of
         * punishment is executed
         */
        STAFF_BROADCAST,

        /**
         * The message sent to the user when this type of
         * punishment is executed
         */
        MESSAGE,

        /**
         * The message sent to the user when their action is
         * denied because they are punished
         */
        ATTEMPT,

        /**
         * The message sent to the user on join when they have
         * been punished while offline
         */
        JOIN_NOTIFICATION,

        /**
         * The message sent to the user when this type of
         * punishment expires
         */
        EXPIRE,

        /**
         * The message sent to online staff when this type of
         * punishment expires
         */
        EXPIRE_STAFF,

        /**
         * The sound that is played to the target when this type
         * of punishment is executed
         */
        SOUND_TARGET,

        /**
         * The sound that is played to non-staff when this type
         * of punishment is executed
         */
        SOUND_GLOBAL,

        /**
         * The sound that is played to staff when this type of
         * punishment is executed
         */
        SOUND_STAFF;
    }
}