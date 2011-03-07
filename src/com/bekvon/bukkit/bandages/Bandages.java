/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bekvon.bukkit.bandages;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Administrator
 */
public class Bandages extends JavaPlugin {

    private final BandagesListener listener = new BandagesListener(this);;
    public PermissionHandler authority;
    public boolean forceStandStill=true;
    private boolean firstRun = true;

    public void onDisable() {
        listener.stopListening();
        Logger.getLogger("Minecraft").log(Level.INFO,"[Bandages] Disabled!");
    }

    public void onEnable() {
        this.getConfiguration().load();
        forceStandStill = this.getConfiguration().getBoolean("requireStandStill", true);
        listener.startListening();
        if(firstRun)
        {
            firstRun = false;
            getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ITEM, listener, Priority.Normal, this);
            getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, listener, Priority.Normal, this);
            checkPermissions();
        }
        Logger.getLogger("Minecraft").log(Level.INFO,"[Bandages] Enabled! Version:" + this.getDescription().getVersion() + " by bekvon");
    }

    private void checkPermissions() {
        Plugin p = getServer().getPluginManager().getPlugin("Permissions");
        if (p != null) {
            authority = ((Permissions) p).getHandler();
            Logger.getLogger("Minecraft").log(Level.INFO, "[Bandages] Found Permissions Plugin!");
        } else {
            authority = null;
            Logger.getLogger("Minecraft").log(Level.INFO, "[Bandages] Permissions Plugin NOT Found!");
        }
    }
}
