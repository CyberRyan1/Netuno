package com.github.cyberryan1.netuno;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.github.cyberryan1.cybercore.CyberCore;
import com.github.cyberryan1.cybercore.helpers.command.helper.CommandHelper;
import com.github.cyberryan1.cybercore.utils.VaultUtils;
import com.github.cyberryan1.netuno.api.ApiNetuno;
import com.github.cyberryan1.netuno.commands.*;
import com.github.cyberryan1.netuno.guis.history.HistoryEditManager;
import com.github.cyberryan1.netuno.listeners.*;
import com.github.cyberryan1.netuno.managers.ChatslowManager;
import com.github.cyberryan1.netuno.skriptelements.conditions.RegisterConditions;
import com.github.cyberryan1.netuno.skriptelements.expressions.RegisterExpressions;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* notes in to do:
    - ! = bug needs to be fixed
    - * = working on
    - space = will work on later
    - ? = unsure if doing
    - % = needs testing */
// ! TODO history and alts may not work if they have none in Punish GUI

// TODO add silent option in punish GUI
// TODO add option to disable books for muted/ipmuted players

// ? TODO add a "protected" permission (basically not allowing them to be punished)
// ? TODO staffchat command & prefix
// ? TODO add a way to convert vanilla bans to netuno bans
public final class Netuno extends JavaPlugin {

    public static final List<CommandHelper> registeredCommands = new ArrayList<>();

    private ChatslowManager chatslowManager;

    // Skript
    public SkriptAddon addon;
    public boolean enabled = true;

    @Override
    public void onEnable() {
        // Initialize things
        CyberCore.setPlugin( this );
        new VaultUtils();

        // Update/reload config files
        YMLUtils.getConfig().getYMLManager().initialize();

        // Set the primary & secondary colors from the config
        CyberCore.setPrimaryColor( Settings.PRIMARY_COLOR.string() );
        CyberCore.setSecondaryColor( Settings.SECONDARY_COLOR.string() );

        ApiNetuno.setupInstance();
        chatslowManager = new ChatslowManager();

        registerSkript();
        registerCommands();
        registerEvents();
    }

    @Override
    public void onDisable() {
        ApiNetuno.deleteInstance();
    }

    private void registerSkript() {
        try {
            addon = Skript.registerAddon( this );
            try {
                addon.loadClasses( "com.github.cyberryan1", "skriptelements" );
            } catch ( IOException e ) {
                Utils.logWarn( "Could not enable as a skript addon, will still enable without this syntax!" );
                enabled = false;
            }
            Utils.logInfo( "Successfully enabled as a skript addon" );
            RegisterExpressions.register();
            RegisterConditions.register();
        } catch ( NoClassDefFoundError error ) {
            Utils.logWarn( "Could not enable as a skript addon, will still enable without this syntax!" );
            enabled = false;
        }
    }

    private void registerCommands() {
        registeredCommands.add( new NetunoCmd() );
        registeredCommands.add( new Ban() );
        registeredCommands.add( new Chatslow() );
        registeredCommands.add( new Clearchat() );
        registeredCommands.add( new History() );
        registeredCommands.add( new IPBan() );
        registeredCommands.add( new IPInfo() );
        registeredCommands.add( new IPMute() );
        registeredCommands.add( new Kick() );
        registeredCommands.add( new Mute() );
        registeredCommands.add( new Mutechat() );
        registeredCommands.add( new Punish() );
        registeredCommands.add( new Report() );
        registeredCommands.add( new Reports() );
        registeredCommands.add( new Togglesigns() );
        registeredCommands.add( new Unban() );
        registeredCommands.add( new UnIPBan() );
        registeredCommands.add( new UnIPMute() );
        registeredCommands.add( new Unmute() );
        registeredCommands.add( new Warn() );
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents( new JoinListener(), this );
        this.getServer().getPluginManager().registerEvents( new ChatListener(), this );
        this.getServer().getPluginManager().registerEvents( new LeaveListener(), this );
        this.getServer().getPluginManager().registerEvents( new SignChangeListener(), this );
        this.getServer().getPluginManager().registerEvents( new CommandListener(), this );
        this.getServer().getPluginManager().registerEvents( new HistoryEditManager(), this );
    }

    private void registerApi() {

    }

    private void unregisterApi() {

    }
}
