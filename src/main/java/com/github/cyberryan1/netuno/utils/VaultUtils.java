package com.github.cyberryan1.netuno.utils;

import com.github.cyberryan1.netuno.errors.LuckPermsVaultUnsafeNeededException;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultUtils {

    public static Permission permissions = null;

    public VaultUtils() {
        if ( setupPermissions() == false ) {
            Utils.logError( "Disabled due to no Vault dependency found!" );
            Utils.getPluginManager().disablePlugin( Utils.getPlugin() );
            return;
        }

        checkPluginsForBugs();
    }

    public static Permission getPermissions() { return permissions; }

    // Check if an online player has a certain permission
    public static boolean hasPerms( Player player, String perm ) { return hasPerms( ( ( OfflinePlayer ) player ), perm ); }

    // Check if a command sender has permissions
    public static boolean hasPerms( CommandSender sender, String perm ) {
        if ( sender instanceof OfflinePlayer ) {
            return hasPerms( ( ( OfflinePlayer ) sender ), perm );
        }

        return permissions.has( sender, perm );
    }

    // Check if an offline player has a certain permission
    public static boolean hasPerms( OfflinePlayer player, String perm ) {
        if ( player.isOp() ) { return true; }

        if ( player.isOnline() == false && LuckPermsVaultUnsafeNeededException.isLuckpermsVaultUnsafe() == false ) {
            Utils.logError( LuckPermsVaultUnsafeNeededException.LUCKPERMS_VAULT_UNSAFE_NEEDED );
            LuckPermsVaultUnsafeNeededException ex = new LuckPermsVaultUnsafeNeededException();
            ex.printStackTrace();
        }

        if ( permissions.playerHas( null, player, ConfigUtils.getStr( "general.all-perms" ) ) ) { return true; }
        if ( permissions.playerHas( null, player, "*" ) ) { return true; }
        return permissions.playerHas( null, player, perm );
    }

    private boolean setupPermissions() {
        if ( Utils.getPluginManager().getPlugin( "Vault" ) == null ) {
            return false;
        }

        RegisteredServiceProvider<Permission> rsp = Utils.getPlugin().getServer().getServicesManager().getRegistration( Permission.class );
        permissions = rsp.getProvider();
        return permissions != null;
    }

    // Check if the permissions plugin may have some bugs
    private void checkPluginsForBugs() {
        if ( LuckPermsVaultUnsafeNeededException.isLuckpermsVaultUnsafe() == false ) {
            Utils.logError( LuckPermsVaultUnsafeNeededException.LUCKPERMS_VAULT_UNSAFE_NEEDED );
        }
    }
}
