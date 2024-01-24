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

    STAFF_PERMISSION( "general.staff-perm", "string" ),
    ALL_PERMISSIONS( "general.all-perms", "string" ),
    PRIMARY_COLOR( "general.primary-color", "string" ),
    SECONDARY_COLOR( "general.secondary-color", "string" ),
    PERM_DENIED_MSG( "general.perm-denied-msg", "string" ),
    SILENT_PREFIX( "general.silent-prefix", "string" ),
    SILENT_PERMISSION( "general.silent-perm", "string" ),
    STAFF_PUNISHMENTS( "general.staff-punishments", "boolean" ),
    RELOAD_PERMISSION( "general.reload-perm", "string" ),

    //
    // Moderation Commands
    //

    // Warn Command
    WARN_PERMISSION( "warn.perm", "string" ),
    WARN_BROADCAST( "warn.broadcast", "strlist" ),
    WARN_STAFF_BROADCAST( "warn.staff-broadcast", "strlist" ),
    WARN_MESSAGE( "warn.message", "strlist" ),

    // Kick command
    KICK_PERMISSION( "kick.perm", "string" ),
    KICK_BROADCAST( "kick.broadcast", "strlist" ),
    KICK_STAFF_BROADCAST( "kick.staff-broadcast", "strlist" ),
    KICK_KICKED_LINES( "kick.kicked-lines", "strlist" ),

    // Mute command
    MUTE_PERMISSION( "mute.perm", "string" ),
    MUTE_BROADCAST( "mute.broadcast", "strlist" ),
    MUTE_STAFF_BROADCAST( "mute.staff-broadcast", "strlist" ),
    MUTE_MESSAGE( "mute.message", "strlist" ),
    MUTE_ATTEMPT( "mute.attempt", "strlist" ),
    MUTE_EXPIRE( "mute.expire", "strlist" ),
    MUTE_EXPIRE_STAFF( "mute.expire-staff", "strlist" ),
    MUTE_BLOCKED_COMMANDS( "mute.blocked-cmds", "strlist" ),
    MUTE_BLOCKED_COMMAND_MESSAGE( "mute.blocked-cmd-message", "str" ),

    // Unmute command
    UNMUTE_PERMISSION( "unmute.perm", "string" ),
    UNMUTE_BROADCAST( "unmute.broadcast", "strlist" ),
    UNMUTE_STAFF_BROADCAST( "unmute.staff-broadcast", "strlist" ),
    UNMUTE_MESSAGE( "unmute.message", "strlist" ),

    // Ban command
    BAN_PERMISSION( "ban.perm", "string" ),
    BAN_BROADCAST( "ban.broadcast", "strlist" ),
    BAN_MAX_TIME_ENABLED( "ban.max-time-enable", "boolean" ),
    BAN_MAX_TIME_LENGTH( "ban.max-time-length", "string" ),
    BAN_MAX_TIME_BYPASS_PERMISSION( "ban.max-time-bypass", "string" ),
    BAN_STAFF_BROADCAST( "ban.staff-broadcast", "strlist" ),
    BAN_MESSAGE( "ban.banned-lines", "strlist" ),
    BAN_ATTEMPT( "ban.attempt", "strlist" ),
    BAN_EXPIRE( "ban.expire", "strlist" ),
    BAN_EXPIRE_STAFF( "ban.expire-staff", "strlist" ),

    // Unban command
    UNBAN_PERMISSION( "unban.perm", "string" ),
    UNBAN_BROADCAST( "unban.broadcast", "strlist" ),
    UNBAN_STAFF_BROADCAST( "unban.staff-broadcast", "strlist" ),
    UNBAN_MESSAGE( "unban.message", "strlist" ),

    // Ipmute command
    IPMUTE_PERMISSION( "ipmute.perm", "string" ),
    IPMUTE_BROADCAST( "ipmute.broadcast", "strlist" ),
    IPMUTE_STAFF_BROADCAST( "ipmute.staff-broadcast", "strlist" ),
    IPMUTE_MESSAGE( "ipmute.message", "strlist" ),
    IPMUTE_ATTEMPT( "ipmute.attempt", "strlist" ),
    IPMUTE_EXPIRE( "ipmute.expire", "strlist" ),
    IPMUTE_EXPIRE_STAFF( "ipmute.expire-staff", "strlist" ),

    // Ipunmute command
    UNIPMUTE_PERMISSION( "unipmute.perm", "string" ),
    UNIPMUTE_BROADCAST( "unipmute.broadcast", "strlist" ),
    UNIPMUTE_STAFF_BROADCAST( "unipmute.staff-broadcast", "strlist" ),
    UNIPMUTE_MESSAGE( "unipmute.message", "strlist" ),

    // Ipban command
    IPBAN_PERMISSION( "ipban.perm", "string" ),
    IPBAN_BROADCAST( "ipban.broadcast", "strlist" ),
    IPBAN_STAFF_BROADCAST( "ipban.staff-broadcast", "strlist" ),
    IPBAN_MESSAGE( "ipban.banned-lines", "strlist" ),
    IPBAN_ATTEMPT( "ipban.attempt", "strlist" ),
    IPBAN_EXPIRE( "ipban.expire", "strlist" ),
    IPBAN_EXPIRE_STAFF( "ipban.expire-staff", "strlist" ),

    // Unipban command
    UNIPBAN_PERMISSION( "unipban.perm", "string" ),
    UNIPBAN_BROADCAST( "unipban.broadcast", "strlist" ),
    UNIPBAN_STAFF_BROADCAST( "unipban.staff-broadcast", "strlist" ),
    UNIPBAN_MESSAGE( "unipban.message", "strlist" ),

    // Punish command
    PUNISH_PERMISSION( "punish.perm", "string" ),
    PUNISH_OTHER_STAFF( "punish.staff-punish", "boolean" ),

    //
    // Alt Management
    //

    // Ipinfo command
    IPINFO_PERMISSION( "ipinfo.perm", "string" ),
    IPINFO_EXEMPT_PERMISSION( "ipinfo.exempt-perm", "string" ),
    IPINFO_NOTIFS( "ipinfo.notifs", "boolean" ),
    IPINFO_NOTIFS_MESSAGE( "ipinfo.notif-msg", "strlist" ),
    IPINFO_NOTIF_HOVER_MESSAGE( "ipinfo.notif-hover", "string" ),
    IPINFO_NOTIFS_SOUND_ENABLED( "ipinfo.notif-sounds.enabled", "boolean" ),
    IPINFO_NOTIFS_SOUND_TRIGGERS( "ipinfo.notif-sounds.triggers", "string" ),
    IPINFO_STRICTNESS( "ipinfo.strictness", "string" ),

    //
    // Chat Management
    //

    // Mutechat command
    MUTECHAT_PERMISSION( "mutechat.perm", "string" ),
    MUTECHAT_BYPASS_PERMISSION( "mutechat.bypass-perm", "string" ),
    MUTECHAT_CHAT_ENABLE_BROADCAST( "mutechat.enable-broadcast", "strlist" ),
    MUTECHAT_CHAT_DISABLE_BROADCAST( "mutechat.disable-broadcast", "strlist" ),
    MUTECHAT_ATTEMPT( "mutechat.attempt", "strlist" ),

    // Clearchat command
    CLEARCHAT_PERMISSION( "clearchat.perm", "string" ),
    CLEARCHAT_BROADCAST( "clearchat.broadcast", "string" ),
    CLEARCHAT_STAFF_BYPASS( "clearchat.staff-bypass", "boolean" ),
    CLEARCHAT_STAFF_BROADCAST( "clearchat.staff-broadcast", "string" ),

    // Chatslow command
    CHATSLOW_PERMISSION( "chatslow.perm", "string" ),
    CHATSLOW_BYPASS_PERMISSION( "chatslow.bypass-perm", "string" ),
    CHATSLOW_DEFAULT_VALUE( "chatslow.default-value", "int" ),
    CHATSLOW_BROADCAST( "chatslow.broadcast", "string" ),
    CHATSLOW_MESSAGE( "chatslow.msg", "string" ),

    //
    // History
    //
    HISTORY_PERMISSION( "history.perm", "string" ),
    HISTORY_REASON_PERMISSION( "history.reason.perm", "string" ),
    HISTORY_TIME_PERMISSION( "history.time.perm", "string" ),
    HISTORY_DELETE_PERMISSION( "history.delete.perm", "string" ),
    HISTORY_RESET_PERMISSION( "history.reset.perm", "string" ),

    //
    // Signs
    //
    ALLOW_SIGNS_WHILE_PUNISHED( "signs.allow-while-muted", "boolean" ),
    SIGN_WHILE_PUNISHED_MESSAGE( "signs.sign-while-muted-attempt", "string" ),
    SIGN_NOTIFS_ENABLED( "signs.notifs", "boolean" ),
    SIGN_NOTIFS_PERMISSION( "signs.notifs-perm", "string" ),
    SIGN_NOTIFS_MESSAGE( "signs.notifs-msg", "strlist" ),

    //
    // Reports
    //
    REPORT_PERMISSION( "report.perm", "string" ),
    REPORT_REASONS_LIST( "report.reasons", "strlist" ),
    REPORT_CONFIRM_MESSAGE( "report.confirm-msg", "strlist" ),
    REPORT_STAFF_MESSAGE( "report.staff-msg", "strlist" ),
    REPORT_VIEW_PERMISSION( "reports.perm", "string" ),
    REPORT_EXPIRE_TIME( "reports.delete-after", "int" ),

    // * Special note: Anything related to the punishment GUI is done in it's own file

    //
    // Database
    //
    DATABASE_USE_SQLITE( "database.use-sqlite", "boolean" ),
    DATABASE_SQL_HOST( "database.sql.host", "string" ),
    DATABASE_SQL_PORT( "database.sql.port", "int" ),
    DATABASE_SQL_DATABASE( "database.sql.database", "string" ),
    DATABASE_SQL_USERNAME( "database.sql.username", "string" ),
    DATABASE_SQL_PASSWORD( "database.sql.password", "string" ),


    CACHE_ALTS_SAVE_EVERY( "database.cache.alts.save-every", "int" ),
    CACHE_REPORTS_SAVE_EVERY( "database.cache.reports.save-every", "int" ),

    //
    // Sounds
    //
    SOUND_PUNISHMENT_WARN_STAFF( "sounds.punishments.warn.staff-sound", "sound" ),
    SOUND_PUNISHMENT_WARN_TARGET( "sounds.punishments.warn.target-sound", "sound" ),
    SOUND_PUNISHMENT_WARN_GLOBAL( "sounds.punishments.warn.global-sound", "sound" ),

    SOUND_PUNISHMENT_KICK_STAFF( "sounds.punishments.kick.staff-sound", "sound" ),
    SOUND_PUNISHMENT_KICK_GLOBAL( "sounds.punishments.kick.global-sound", "sound" ),

    SOUND_PUNISHMENT_MUTE_STAFF( "sounds.punishments.mute.staff-sound", "sound" ),
    SOUND_PUNISHMENT_MUTE_TARGET( "sounds.punishments.mute.target-sound", "sound" ),
    SOUND_PUNISHMENT_MUTE_GLOBAL( "sounds.punishments.mute.global-sound", "sound" ),

    SOUND_PUNISHMENT_UNMUTE_STAFF( "sounds.punishments.unmute.staff-sound", "sound" ),
    SOUND_PUNISHMENT_UNMUTE_TARGET( "sounds.punishments.unmute.target-sound", "sound" ),
    SOUND_PUNISHMENT_UNMUTE_GLOBAL( "sounds.punishments.unmute.global-sound", "sound" ),

    SOUND_PUNISHMENT_BAN_STAFF( "sounds.punishments.ban.staff-sound", "sound" ),
    SOUND_PUNISHMENT_BAN_GLOBAL( "sounds.punishments.ban.global-sound", "sound" ),

    SOUND_PUNISHMENT_UNBAN_STAFF( "sounds.punishments.unban.staff-sound", "sound" ),
    SOUND_PUNISHMENT_UNBAN_GLOBAL( "sounds.punishments.unban.global-sound", "sound" ),

    SOUND_PUNISHMENT_IPMUTE_STAFF( "sounds.punishments.ipmute.staff-sound", "sound" ),
    SOUND_PUNISHMENT_IPMUTE_TARGET( "sounds.punishments.ipmute.target-sound", "sound" ),
    SOUND_PUNISHMENT_IPMUTE_GLOBAL( "sounds.punishments.ipmute.global-sound", "sound" ),

    SOUND_PUNISHMENT_UNIPMUTE_STAFF( "sounds.punishments.unipmute.staff-sound", "sound" ),
    SOUND_PUNISHMENT_UNIPMUTE_TARGET( "sounds.punishments.unipmute.target-sound", "sound" ),
    SOUND_PUNISHMENT_UNIPMUTE_GLOBAL( "sounds.punishments.unipmute.global-sound", "sound" ),

    SOUND_PUNISHMENT_IPBAN_STAFF( "sounds.punishments.ipban.staff-sound", "sound" ),
    SOUND_PUNISHMENT_IPBAN_GLOBAL( "sounds.punishments.ipban.global-sound", "sound" ),

    SOUND_PUNISHMENT_UNIPBAN_STAFF( "sounds.punishments.unipban.staff-sound", "sound" ),
    SOUND_PUNISHMENT_UNIPBAN_GLOBAL( "sounds.punishments.unipban.global-sound", "sound" ),

    SOUND_PUNISHED_ATTEMPT_MUTED( "sounds.punished-attempt.muted", "sound" ),
    SOUND_PUNISHED_ATTEMPT_IPMUTED( "sounds.punished-attempt.ipmuted", "sound" ),
    ;

    private String path;
    private SettingsEntry value;
    Settings( String path, String valueType ) {
        this.path = path;
        this.value = new SettingsEntry( path, valueType );
    }

    public void reload() {
        this.value = new SettingsEntry( this.path, this.value.getValueType() );
    }

    public String getPath() { return this.path; }

    public SettingsEntry getValue() { return this.value; }

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