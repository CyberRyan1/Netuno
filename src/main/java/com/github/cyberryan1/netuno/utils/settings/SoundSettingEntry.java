package com.github.cyberryan1.netuno.utils.settings;

import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

public class SoundSettingEntry {

    private final String startingPath;
    private final boolean enabled;
    private final Sound sound;
    private final float volume;
    private final float pitch;

    public SoundSettingEntry( String startingPath ) {
        if ( startingPath.endsWith( "." ) == false ) { startingPath += "."; }
        this.startingPath = startingPath;
        this.enabled = YMLUtils.getConfig().getBool( startingPath + "enabled" );
        if ( this.enabled == false ) { this.sound = null; }
        else { this.sound = Sound.valueOf( YMLUtils.getConfig().getStr( startingPath + "sound" ) ); }
        this.volume = YMLUtils.getConfig().getFloat( startingPath + "volume" );
        this.pitch = YMLUtils.getConfig().getFloat( startingPath + "pitch" );
    }

    /**
     * Plays this sound to the given player, provided {@link #isEnabled()}
     * is true.
     * @param player The player
     */
    public void playSound( Player player ) {
        playSound( player, false );
    }

    /**
     * Plays this sound to the player.
     * @param player Player
     * @param force If true, then this sound will be played no matter what.
     *              Otherwise, this sound will only be played if {@link #isEnabled()}
     *              yields true.
     */
    public void playSound( Player player, boolean force ) {
        if ( ( force || this.enabled ) == false ) { return; }
        player.playSound( player.getLocation(), sound, volume, pitch );
    }

    /**
     * Plays a sound for all online players who match the given predicate
     * @param predicate The predicate to test against
     */
    public void playSoundMany( Predicate<? super Player> predicate ) {
        if ( this.enabled == false ) { return; }
        Bukkit.getOnlinePlayers().stream()
                .filter( predicate )
                .forEach( player -> playSound( player, false ) );
    }

    public String getStartingPath() {
        return startingPath;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Sound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }
}