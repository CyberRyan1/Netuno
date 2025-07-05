package com.github.cyberryan1.netuno.utils.settings;

import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Settings {

    //
    // General Section
    //

    STAFF_PERMISSION( "general.staff-perm", SettingsEntry.EntryType.STRING ),
    ALL_PERMISSIONS( "general.all-perms", SettingsEntry.EntryType.STRING ),
    PRIMARY_COLOR( "general.primary-color", SettingsEntry.EntryType.STRING ),
    SECONDARY_COLOR( "general.secondary-color", SettingsEntry.EntryType.STRING ),
    PERM_DENIED_MSG( "general.perm-denied-msg", SettingsEntry.EntryType.STRING ),
    SILENT_PREFIX( "general.silent-prefix", SettingsEntry.EntryType.STRING ),
    SILENT_PERMISSION( "general.silent-perm", SettingsEntry.EntryType.STRING ),
    STAFF_PUNISHMENTS( "general.staff-punishments", SettingsEntry.EntryType.BOOLEAN ),
    RELOAD_PERMISSION( "general.reload-perm", SettingsEntry.EntryType.STRING ),

    //
    // Moderation Commands
    //

    // Warn Command
    WARN_PERMISSION( "warn.perm", SettingsEntry.EntryType.STRING ),
    WARN_BROADCAST( "warn.broadcast", SettingsEntry.EntryType.STRING_LIST ),
    WARN_STAFF_BROADCAST( "warn.staff-broadcast", SettingsEntry.EntryType.STRING_LIST ),
    WARN_MESSAGE( "warn.message", SettingsEntry.EntryType.STRING_LIST ),

    // Kick command
    KICK_PERMISSION( "kick.perm", SettingsEntry.EntryType.STRING ),
    KICK_BROADCAST( "kick.broadcast", SettingsEntry.EntryType.STRING_LIST ),
    KICK_STAFF_BROADCAST( "kick.staff-broadcast", SettingsEntry.EntryType.STRING_LIST ),
    KICK_KICKED_LINES( "kick.kicked-lines", SettingsEntry.EntryType.STRING_LIST ),

    // Mute command
    MUTE_PERMISSION( "mute.perm", SettingsEntry.EntryType.STRING ),
    MUTE_BROADCAST( "mute.broadcast", SettingsEntry.EntryType.STRING_LIST ),
    MUTE_STAFF_BROADCAST( "mute.staff-broadcast", SettingsEntry.EntryType.STRING_LIST ),
    MUTE_MESSAGE( "mute.message", SettingsEntry.EntryType.STRING_LIST ),
    MUTE_ATTEMPT( "mute.attempt", SettingsEntry.EntryType.STRING_LIST ),
    MUTE_EXPIRE( "mute.expire", SettingsEntry.EntryType.STRING_LIST ),
    MUTE_EXPIRE_STAFF( "mute.expire-staff", SettingsEntry.EntryType.STRING_LIST ),
    MUTE_BLOCKED_COMMANDS( "mute.blocked-cmds", SettingsEntry.EntryType.STRING_LIST ),
    MUTE_BLOCKED_COMMAND_MESSAGE( "mute.blocked-cmd-msg", SettingsEntry.EntryType.STRING ),

    // Unmute command
    UNMUTE_PERMISSION( "unmute.perm", SettingsEntry.EntryType.STRING ),
    UNMUTE_BROADCAST( "unmute.broadcast", SettingsEntry.EntryType.STRING_LIST ),
    UNMUTE_STAFF_BROADCAST( "unmute.staff-broadcast", SettingsEntry.EntryType.STRING_LIST ),
    UNMUTE_MESSAGE( "unmute.message", SettingsEntry.EntryType.STRING_LIST ),

    // Ban command
    BAN_PERMISSION( "ban.perm", SettingsEntry.EntryType.STRING ),
    BAN_BROADCAST( "ban.broadcast", SettingsEntry.EntryType.STRING_LIST ),
    BAN_MAX_TIME_ENABLED( "ban.max-time-enable", SettingsEntry.EntryType.BOOLEAN ),
    BAN_MAX_TIME_LENGTH( "ban.max-time-length", SettingsEntry.EntryType.STRING ),
    BAN_MAX_TIME_BYPASS_PERMISSION( "ban.max-time-bypass", SettingsEntry.EntryType.STRING ),
    BAN_STAFF_BROADCAST( "ban.staff-broadcast", SettingsEntry.EntryType.STRING_LIST ),
    BAN_MESSAGE( "ban.banned-lines", SettingsEntry.EntryType.STRING_LIST ),
    BAN_ATTEMPT( "ban.attempt", SettingsEntry.EntryType.STRING_LIST ),
    BAN_EXPIRE( "ban.expire", SettingsEntry.EntryType.STRING_LIST ),
    BAN_EXPIRE_STAFF( "ban.expire-staff", SettingsEntry.EntryType.STRING_LIST ),

    // Unban command
    UNBAN_PERMISSION( "unban.perm", SettingsEntry.EntryType.STRING ),
    UNBAN_BROADCAST( "unban.broadcast", SettingsEntry.EntryType.STRING_LIST ),
    UNBAN_STAFF_BROADCAST( "unban.staff-broadcast", SettingsEntry.EntryType.STRING_LIST ),
    UNBAN_MESSAGE( "unban.message", SettingsEntry.EntryType.STRING_LIST ),

    // Ipmute command
    IPMUTE_PERMISSION( "ipmute.perm", SettingsEntry.EntryType.STRING ),
    IPMUTE_BROADCAST( "ipmute.broadcast", SettingsEntry.EntryType.STRING_LIST ),
    IPMUTE_STAFF_BROADCAST( "ipmute.staff-broadcast", SettingsEntry.EntryType.STRING_LIST ),
    IPMUTE_MESSAGE( "ipmute.message", SettingsEntry.EntryType.STRING_LIST ),
    IPMUTE_ATTEMPT( "ipmute.attempt", SettingsEntry.EntryType.STRING_LIST ),
    IPMUTE_EXPIRE( "ipmute.expire", SettingsEntry.EntryType.STRING_LIST ),
    IPMUTE_EXPIRE_STAFF( "ipmute.expire-staff", SettingsEntry.EntryType.STRING_LIST ),

    // Ipunmute command
    UNIPMUTE_PERMISSION( "unipmute.perm", SettingsEntry.EntryType.STRING ),
    UNIPMUTE_BROADCAST( "unipmute.broadcast", SettingsEntry.EntryType.STRING_LIST ),
    UNIPMUTE_STAFF_BROADCAST( "unipmute.staff-broadcast", SettingsEntry.EntryType.STRING_LIST ),
    UNIPMUTE_MESSAGE( "unipmute.message", SettingsEntry.EntryType.STRING_LIST ),

    // Ipban command
    IPBAN_PERMISSION( "ipban.perm", SettingsEntry.EntryType.STRING ),
    IPBAN_BROADCAST( "ipban.broadcast", SettingsEntry.EntryType.STRING_LIST ),
    IPBAN_STAFF_BROADCAST( "ipban.staff-broadcast", SettingsEntry.EntryType.STRING_LIST ),
    IPBAN_MESSAGE( "ipban.banned-lines", SettingsEntry.EntryType.STRING_LIST ),
    IPBAN_ATTEMPT( "ipban.attempt", SettingsEntry.EntryType.STRING_LIST ),
    IPBAN_EXPIRE( "ipban.expire", SettingsEntry.EntryType.STRING_LIST ),
    IPBAN_EXPIRE_STAFF( "ipban.expire-staff", SettingsEntry.EntryType.STRING_LIST ),

    // Unipban command
    UNIPBAN_PERMISSION( "unipban.perm", SettingsEntry.EntryType.STRING ),
    UNIPBAN_BROADCAST( "unipban.broadcast", SettingsEntry.EntryType.STRING_LIST ),
    UNIPBAN_STAFF_BROADCAST( "unipban.staff-broadcast", SettingsEntry.EntryType.STRING_LIST ),
    UNIPBAN_MESSAGE( "unipban.message", SettingsEntry.EntryType.STRING_LIST ),

    // Punish command
    PUNISH_PERMISSION( "punish.perm", SettingsEntry.EntryType.STRING ),
    PUNISH_OTHER_STAFF( "punish.staff-punish", SettingsEntry.EntryType.BOOLEAN ),

    PUNISH_INSTANT_ENABLED( "punish.instant.enabled", SettingsEntry.EntryType.BOOLEAN ),
    PUNISH_INSTANT_PERMISSION( "punish.instant.perm", SettingsEntry.EntryType.STRING ),
    PUNISH_INSTANT_COOLDOWN( "punish.instant.cooldown", SettingsEntry.EntryType.INT ),

    PUNISH_LOADING_ITEM_MATERIAL( "punish.loading-item.material", SettingsEntry.EntryType.MATERIAL ),
    PUNISH_LOADING_ITEM_NAME( "punish.loading-item.name", SettingsEntry.EntryType.STRING ),

    //
    // Alt Management
    //

    // Ipinfo command
    IPINFO_PERMISSION( "ipinfo.perm", SettingsEntry.EntryType.STRING ),
    IPINFO_EXEMPT_PERMISSION( "ipinfo.exempt-perm", SettingsEntry.EntryType.STRING ),
    IPINFO_NOTIFS( "ipinfo.notifs", SettingsEntry.EntryType.BOOLEAN ),
    IPINFO_NOTIFS_MESSAGE( "ipinfo.notif-msg", SettingsEntry.EntryType.STRING_LIST ),
    IPINFO_NOTIF_HOVER_MESSAGE( "ipinfo.notif-hover", SettingsEntry.EntryType.STRING ),
    IPINFO_NOTIF_IF_IPPUNISHED( "ipinfo.notif-if-ippunished", SettingsEntry.EntryType.BOOLEAN ),
    IPINFO_NOTIFS_SOUND_ENABLED( "ipinfo.notif-sounds.enabled", SettingsEntry.EntryType.BOOLEAN ),
    IPINFO_NOTIFS_SOUND_TRIGGERS( "ipinfo.notif-sounds.triggers", SettingsEntry.EntryType.STRING ),
    IPINFO_STRICTNESS( "ipinfo.strictness", SettingsEntry.EntryType.STRING ),

    //
    // Chat Management
    //
    CHAT_COMMAND_PERMISSION( "chat.perm", SettingsEntry.EntryType.STRING ),

    // Mutechat command
    MUTECHAT_PERMISSION( "chat.mute.perm", SettingsEntry.EntryType.STRING ),
    MUTECHAT_BYPASS_PERMISSION( "chat.mute.bypass-perm", SettingsEntry.EntryType.STRING ),
    MUTECHAT_CHAT_ENABLE_BROADCAST( "chat.mute.enable-broadcast", SettingsEntry.EntryType.STRING_LIST ),
    MUTECHAT_CHAT_DISABLE_BROADCAST( "chat.mute.disable-broadcast", SettingsEntry.EntryType.STRING_LIST ),
    MUTECHAT_ATTEMPT( "chat.mute.attempt", SettingsEntry.EntryType.STRING_LIST ),

    // Clearchat command
    CLEARCHAT_PERMISSION( "chat.clear.perm", SettingsEntry.EntryType.STRING ),
    CLEARCHAT_BROADCAST( "chat.clear.broadcast", SettingsEntry.EntryType.STRING ),
    CLEARCHAT_STAFF_BYPASS( "chat.clear.staff-bypass", SettingsEntry.EntryType.BOOLEAN ),
    CLEARCHAT_STAFF_BROADCAST( "chat.clear.staff-broadcast", SettingsEntry.EntryType.STRING ),

    // Chatslow command
    CHATSLOW_PERMISSION( "chat.slow.perm", SettingsEntry.EntryType.STRING ),
    CHATSLOW_BYPASS_PERMISSION( "chat.slow.bypass-perm", SettingsEntry.EntryType.STRING ),
    CHATSLOW_DEFAULT_VALUE( "chat.slow.default-value", SettingsEntry.EntryType.INT ),
    CHATSLOW_BROADCAST( "chat.slow.broadcast", SettingsEntry.EntryType.STRING ),
    CHATSLOW_MESSAGE( "chat.slow.msg", SettingsEntry.EntryType.STRING ),

    //
    // History
    //
    HISTORY_PERMISSION( "history.perm", SettingsEntry.EntryType.STRING ),
    HISTORY_REASON_PERMISSION( "history.reason.perm", SettingsEntry.EntryType.STRING ),
    HISTORY_TIME_PERMISSION( "history.time.perm", SettingsEntry.EntryType.STRING ),
    HISTORY_DELETE_PERMISSION( "history.delete.perm", SettingsEntry.EntryType.STRING ),
    HISTORY_STAFF_LIST_PERMISSION( "history.staff-list.perm", SettingsEntry.EntryType.STRING ),
    HISTORY_ROLLBACK_PLAYER_PERMISSION( "history.rollback.player-perm", SettingsEntry.EntryType.STRING ),
    HISTORY_ROLLBACK_STAFF_PERMISSION( "history.rollback.staff-perm",  SettingsEntry.EntryType.STRING ),
    HISTORY_RESET_PERMISSION( "history.reset.perm", SettingsEntry.EntryType.STRING ),

    //
    // Signs
    //
    ALLOW_SIGNS_WHILE_PUNISHED( "signs.allow-while-muted", SettingsEntry.EntryType.BOOLEAN ),
    SIGN_WHILE_PUNISHED_MESSAGE( "signs.sign-while-muted-attempt", SettingsEntry.EntryType.STRING ),
    SIGN_NOTIFS_ENABLED( "signs.notifs", SettingsEntry.EntryType.BOOLEAN ),
    SIGN_NOTIFS_PERMISSION( "signs.notifs-perm", SettingsEntry.EntryType.STRING ),
    SIGN_NOTIFS_MESSAGE( "signs.notifs-msg", SettingsEntry.EntryType.STRING_LIST ),

    //
    // Reports
    //
    REPORT_PERMISSION( "report.perm", SettingsEntry.EntryType.STRING ),
    REPORT_REASONS_LIST( "report.reasons", SettingsEntry.EntryType.STRING_LIST ),
    REPORT_CONFIRM_MESSAGE( "report.confirm-msg", SettingsEntry.EntryType.STRING_LIST ),
    REPORT_STAFF_MESSAGE( "report.staff-msg", SettingsEntry.EntryType.STRING_LIST ),
    REPORT_SOUND_PLAYER( "report.sounds.player", SettingsEntry.EntryType.SOUND ),
    REPORT_SOUND_STAFF( "report.sounds.staff", SettingsEntry.EntryType.SOUND ),

    //
    // Report GUI (for the /report (player) command)
    //
    REPORT_GUI_SUBMIT_REPORT( "report.gui.submit-report", SettingsEntry.EntryType.MATERIAL ),
    REPORT_GUI_RESET_SELECTIONS( "report.gui.reset-selections", SettingsEntry.EntryType.MATERIAL ),
    REPORT_GUI_SELECTED_REASON( "report.gui.selected-reason", SettingsEntry.EntryType.MATERIAL ),
    REPORT_GUI_UNSELECTED_REASON( "report.gui.unselected-reason", SettingsEntry.EntryType.MATERIAL ),

    //
    // View Reports
    //
    VIEW_REPORT_PERMISSION( "view-reports.perm", SettingsEntry.EntryType.STRING ),
    VIEW_REPORT_EXPIRE_TIME_HOURS( "view-reports.delete-after", SettingsEntry.EntryType.INT ),

    //
    // Watchlist
    //
    WATCHLIST_VIEW_PERMISSION( "watchlist.view-perm", SettingsEntry.EntryType.STRING ),
    WATCHLIST_EDIT_PERMISSION( "watchlist.edit-perm", SettingsEntry.EntryType.STRING ),
    WATCHLIST_NOTIFS_VIEW_PERMISSION( "watchlist.notifs.perm", SettingsEntry.EntryType.STRING ),
    WATCHLIST_NOTIFS_PREFIX( "watchlist.notifs.prefix", SettingsEntry.EntryType.STRING ),
    WATCHLIST_NOTIFS_SOUND( "watchlist.notifs.sound", SettingsEntry.EntryType.SOUND ),

    // * Special note: Anything related to the punishment GUI is done in it's own file

    //
    // Database
    //
    DATABASE_USE_SQLITE( "database.use-sqlite", SettingsEntry.EntryType.BOOLEAN ),
    DATABASE_SQL_HOST( "database.sql.host", SettingsEntry.EntryType.STRING ),
    DATABASE_SQL_PORT( "database.sql.port", SettingsEntry.EntryType.INT ),
    DATABASE_SQL_DATABASE( "database.sql.database", SettingsEntry.EntryType.STRING ),
    DATABASE_SQL_USERNAME( "database.sql.username", SettingsEntry.EntryType.STRING ),
    DATABASE_SQL_PASSWORD( "database.sql.password", SettingsEntry.EntryType.STRING ),


    CACHE_ALTS_SAVE_EVERY( "database.cache.alts.save-every", SettingsEntry.EntryType.INT ),
    CACHE_REPORTS_SAVE_EVERY( "database.cache.reports.save-every", SettingsEntry.EntryType.INT ),

    //
    // Sounds
    //
    SOUND_PUNISHMENT_WARN_STAFF( "sounds.punishments.warn.staff-sound", SettingsEntry.EntryType.SOUND ),
    SOUND_PUNISHMENT_WARN_TARGET( "sounds.punishments.warn.target-sound", SettingsEntry.EntryType.SOUND ),
    SOUND_PUNISHMENT_WARN_GLOBAL( "sounds.punishments.warn.global-sound", SettingsEntry.EntryType.SOUND ),

    SOUND_PUNISHMENT_KICK_STAFF( "sounds.punishments.kick.staff-sound", SettingsEntry.EntryType.SOUND ),
    SOUND_PUNISHMENT_KICK_GLOBAL( "sounds.punishments.kick.global-sound", SettingsEntry.EntryType.SOUND ),

    SOUND_PUNISHMENT_MUTE_STAFF( "sounds.punishments.mute.staff-sound", SettingsEntry.EntryType.SOUND ),
    SOUND_PUNISHMENT_MUTE_TARGET( "sounds.punishments.mute.target-sound", SettingsEntry.EntryType.SOUND ),
    SOUND_PUNISHMENT_MUTE_GLOBAL( "sounds.punishments.mute.global-sound", SettingsEntry.EntryType.SOUND ),

    SOUND_PUNISHMENT_UNMUTE_STAFF( "sounds.punishments.unmute.staff-sound", SettingsEntry.EntryType.SOUND ),
    SOUND_PUNISHMENT_UNMUTE_TARGET( "sounds.punishments.unmute.target-sound", SettingsEntry.EntryType.SOUND ),
    SOUND_PUNISHMENT_UNMUTE_GLOBAL( "sounds.punishments.unmute.global-sound", SettingsEntry.EntryType.SOUND ),

    SOUND_PUNISHMENT_BAN_STAFF( "sounds.punishments.ban.staff-sound", SettingsEntry.EntryType.SOUND ),
    SOUND_PUNISHMENT_BAN_GLOBAL( "sounds.punishments.ban.global-sound", SettingsEntry.EntryType.SOUND ),

    SOUND_PUNISHMENT_UNBAN_STAFF( "sounds.punishments.unban.staff-sound", SettingsEntry.EntryType.SOUND ),
    SOUND_PUNISHMENT_UNBAN_GLOBAL( "sounds.punishments.unban.global-sound", SettingsEntry.EntryType.SOUND ),

    SOUND_PUNISHMENT_IPMUTE_STAFF( "sounds.punishments.ipmute.staff-sound", SettingsEntry.EntryType.SOUND ),
    SOUND_PUNISHMENT_IPMUTE_TARGET( "sounds.punishments.ipmute.target-sound", SettingsEntry.EntryType.SOUND ),
    SOUND_PUNISHMENT_IPMUTE_GLOBAL( "sounds.punishments.ipmute.global-sound", SettingsEntry.EntryType.SOUND ),

    SOUND_PUNISHMENT_UNIPMUTE_STAFF( "sounds.punishments.unipmute.staff-sound", SettingsEntry.EntryType.SOUND ),
    SOUND_PUNISHMENT_UNIPMUTE_TARGET( "sounds.punishments.unipmute.target-sound", SettingsEntry.EntryType.SOUND ),
    SOUND_PUNISHMENT_UNIPMUTE_GLOBAL( "sounds.punishments.unipmute.global-sound", SettingsEntry.EntryType.SOUND ),

    SOUND_PUNISHMENT_IPBAN_STAFF( "sounds.punishments.ipban.staff-sound", SettingsEntry.EntryType.SOUND ),
    SOUND_PUNISHMENT_IPBAN_GLOBAL( "sounds.punishments.ipban.global-sound", SettingsEntry.EntryType.SOUND ),

    SOUND_PUNISHMENT_UNIPBAN_STAFF( "sounds.punishments.unipban.staff-sound", SettingsEntry.EntryType.SOUND ),
    SOUND_PUNISHMENT_UNIPBAN_GLOBAL( "sounds.punishments.unipban.global-sound", SettingsEntry.EntryType.SOUND ),

    SOUND_PUNISHED_ATTEMPT_MUTED( "sounds.punished-attempt.muted", SettingsEntry.EntryType.SOUND ),
    SOUND_PUNISHED_ATTEMPT_IPMUTED( "sounds.punished-attempt.ipmuted", SettingsEntry.EntryType.SOUND ),
    ;

    private String path;
    private SettingsEntry value;
    private SettingsEntry.EntryType type;
    Settings( String path, SettingsEntry.EntryType valueType ) {
        this.path = path;
        this.value = new SettingsEntry( path, valueType );
        this.type = valueType;
    }

    public void reload() {
        this.value = new SettingsEntry( this.path, this.value.getValueType() );
    }

    public String getPath() { return this.path; }

    public SettingsEntry getValue() { return this.value; }
    
    public SettingsEntry.EntryType getValueType() { return this.type; }

    public int integer() { return value.integer(); }

    public String string() { return value.string(); }

    public String coloredString() { return CyberColorUtils.getColored( value.string() ); }

    public float getFloat() { return value.getFloat(); }

    public double getDouble() { return value.getDouble(); }

    public long getLong() { return value.getLong(); }

    public boolean bool() { return value.bool(); }

    public Material material() { return value.material(); }

    public String[] stringlist() { return value.stringlist(); }

    public SoundSettingEntry sound() { return value.sound(); }

    public String[] coloredStringlist() {
        String[] toReturn = new String[ stringlist().length ];
        for ( int i = 0; i < stringlist().length; i++ ) {
            toReturn[i] = CyberColorUtils.getColored( stringlist()[i] );
        }
        return toReturn;
    }

    public List<String> arraylist() { return new ArrayList<>( Arrays.asList( stringlist() ) ); }

    public List<String> coloredArraylist() { return new ArrayList<>( Arrays.asList( coloredStringlist() ) ); }
}