package com.github.cyberryan1.netuno.classes;

import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Time;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PrePunishment {

    private OfflinePlayer target = null;
    private Player staff = null;
    private boolean consoleSender = false;
    private String type = null;
    private String lengthMsg = null;
    private String reasonMsg = null;

    public PrePunishment() {
    }

    public PrePunishment( OfflinePlayer target, String type, String reasonMsg ) {
        this.target = target;
        this.type = type;
        this.reasonMsg = reasonMsg;
    }

    public PrePunishment( OfflinePlayer target, String type, String lengthMsg, String reasonMsg ) {
        this.target = target;
        this.type = type;
        this.lengthMsg = lengthMsg;
        this.reasonMsg = reasonMsg;
    }

    public PrePunishment( OfflinePlayer target, Player staff, String type, String lengthMsg, String reasonMsg ) {
        this.target = target;
        this.staff = staff;
        this.type = type;
        this.lengthMsg = lengthMsg;
        this.reasonMsg = reasonMsg;
    }

    public void setTarget( OfflinePlayer target ) { this.target = target; }

    public void setStaff( Player staff ) { this.staff = staff; }

    public void setType( String type ) { this.type = type; }

    public void setLength( String lengthMsg ) { this.lengthMsg = lengthMsg; }

    public void setReason( String reasonMsg ) { this.reasonMsg = reasonMsg; }

    public void setConsoleSender( boolean b ) { this.consoleSender = b; }

    public OfflinePlayer getTarget() { return target; }

    public Player getStaff() { return staff; }

    public String getType() { return type; }

    public String getLength() { return lengthMsg; }

    public String getReason() { return reasonMsg; }

    public boolean isConsoleSender() { return consoleSender; }

    public void executePunishment() {
        Punishment pun = new Punishment();
        pun.setPlayerUUID( target.getUniqueId().toString() );
        pun.setStaffUUID( consoleSender ? "CONSOLE" : staff.getUniqueId().toString() );
        pun.setType( type );
        pun.setDate( Time.getCurrentTimestamp() );

        // calculate and set the length of the punishment, as long as a length is provided with the punishment
        if ( type.equalsIgnoreCase( "warn" ) == false && type.equalsIgnoreCase( "kick" ) == false ) {
            pun.setLength( Time.getTimestampFromLength( lengthMsg ) );
            pun.setActive( true );
        }
        else {
            pun.setLength( -1L );
        }

        pun.setReason( reasonMsg );
        if ( reasonMsg.contains( "-s" ) ) {
            if ( consoleSender || VaultUtils.hasPerms( staff, ConfigUtils.getStr( "general.silent-perm" ) ) ) {
                pun.setReason( reasonMsg.replaceAll( "-s", "" ) );
            }
        }

        int id = -1;
        if ( type.equalsIgnoreCase( "ipmute" ) || type.equalsIgnoreCase( "ipban" ) ) {
            IPPunishment ippun = pun.toIPPunishment();
            ArrayList<String> alts = new ArrayList<>();
            for ( OfflinePlayer p : Utils.getDatabase().getAllAlts( ippun.getPlayerUUID() ) ) {
                alts.add( p.getUniqueId().toString() );
            }

            id = Utils.getDatabase().addIPPunishment( ippun );
        }
        else {
            id = Utils.getDatabase().addPunishment( pun );
        }

        // see if the player needs a notif added because they were punished while offline
        // only does this for warns
        if ( type.equalsIgnoreCase( "warn" ) && target.isOnline() == false ) {
            Utils.getDatabase().addNotif( id, target.getUniqueId().toString() );
        }

        // kick the target from the server if they are online and the punishment does kick them from the server
        if ( target.isOnline() ) {
            if ( type.equalsIgnoreCase( "kick" ) ) {
                target.getPlayer().kickPlayer( ConfigUtils.replaceAllVariables(
                        ConfigUtils.getColoredStrFromList( "kick.kicked-lines" ), pun
                ) );
            }

            else if ( type.equalsIgnoreCase( "ban" ) ) {
                target.getPlayer().kickPlayer( ConfigUtils.replaceAllVariables(
                        ConfigUtils.getColoredStrFromList( "ban.banned-lines" ), pun
                ) );
            }

            else if ( type.equalsIgnoreCase( "ipban" ) ) {
                target.getPlayer().kickPlayer( ConfigUtils.replaceAllVariables(
                        ConfigUtils.getColoredStrFromList( "ipban.banned-lines" ), pun
                ) );

                for ( OfflinePlayer alt : Utils.getDatabase().getAllAlts( pun.getPlayerUUID() ) ) {
                    if ( alt.isOnline() ) {
                        alt.getPlayer().kickPlayer( ConfigUtils.replaceAllVariables(
                                ConfigUtils.getColoredStrFromList( "ipban.banned-lines" ), pun
                        ) );
                    }
                }
            }
        }

        doBroadcasts( pun );
    }

    private void doBroadcasts( Punishment pun ) {
        String typeLowercase = pun.getType().toLowerCase();
        boolean silent = false;
        if ( pun.getReason().contains( "-s" ) ) {
            if ( consoleSender || VaultUtils.hasPerms( staff, ConfigUtils.getStr( "general.silent-perm" ) ) ) {
                pun.setReason( pun.getReason().replaceAll( "-s", "" ) );
                silent = true;
            }
        }

        // send the punishment to the target, if the target is online and the punishment does not kick them from the server
        if ( target.isOnline() ) {
            if ( typeLowercase.equals( "warn" ) || typeLowercase.equals( "mute" ) || typeLowercase.equals( "ipmute" ) ) {
                if ( ConfigUtils.checkListNotEmpty( typeLowercase + ".message" ) ) {
                    Player targetOnline = target.getPlayer();
                    String msg = ConfigUtils.getColoredStrFromList( typeLowercase + ".message" );
                    Utils.sendAnyMsg( targetOnline, ConfigUtils.replaceAllVariables( msg, pun ) );

                    if ( typeLowercase.equals( "ipmute" ) ) {
                        for ( OfflinePlayer alt : Utils.getDatabase().getAllAlts( pun.getPlayerUUID() ) ) {
                            if ( alt.isOnline() && alt.getPlayer().equals( target.getPlayer() ) == false ) {
                                Utils.sendAnyMsg( alt.getPlayer(), ConfigUtils.replaceAllVariables(
                                        ConfigUtils.getColoredStrFromList( "ipmute.message" ), pun
                                ) );
                            }
                        }
                    }
                }
            }
        }

        // announce the punishment to everyone who are not staff, unless if the punishment is silent
        // will send to staff if the staff broadcast is empty
        // will not send to the target unless the punishment doesn't kick them and the target punishment msg is empty
        boolean sendToStaff = ConfigUtils.checkListNotEmpty( typeLowercase + ".staff-broadcast" );
        if ( silent == false ) {
            if ( ConfigUtils.checkListNotEmpty( typeLowercase + ".broadcast" ) ) {
                String broadcast = ConfigUtils.getColoredStrFromList( typeLowercase + ".broadcast" );
                broadcast = ConfigUtils.replaceAllVariables( broadcast, pun );

                for ( Player p : Bukkit.getOnlinePlayers() ) {
                    if ( VaultUtils.hasPerms( p, ConfigUtils.getStr( "general.staff-perm" ) ) == false || sendToStaff == false ) {
                        if ( p.equals( target.getPlayer() ) == false ) {
                            Utils.sendAnyMsg( p, broadcast );
                        }

                        else if ( typeLowercase.equals( "warn" ) || typeLowercase.equals( "mute" ) ) {
                            if ( ConfigUtils.checkListNotEmpty( typeLowercase + ".message" ) == false ) {
                                Utils.sendAnyMsg( p, broadcast );
                            }
                        }
                    }
                }
            }
        }

        if ( sendToStaff ) {
            String broadcast = "";

            if ( silent ) {
                String prefix = ConfigUtils.getColoredStr( "general.silent-prefix" );
                ArrayList<String> list = ConfigUtils.getColoredStrList( typeLowercase + ".staff-broadcast" );
                for ( int i = 0; i < list.size(); i++ ) {
                    if ( list.get( i ).equals( "" ) == false && list.get( i ).equals( "\n" ) == false ) {
                        broadcast += prefix + list.get( i );
                    }
                    else {
                        broadcast += list.get( i );
                    }
                }
            }

            else {
                broadcast = ConfigUtils.getColoredStrFromList( typeLowercase + ".staff-broadcast" );
            }

            broadcast = ConfigUtils.replaceAllVariables( broadcast, pun );

            for ( Player p : Bukkit.getOnlinePlayers() ) {
                if ( VaultUtils.hasPerms( p, ConfigUtils.getStr( "general.staff-perm" ) ) ) {
                    Utils.sendAnyMsg( p, broadcast );
                }
            }
        }
    }
}