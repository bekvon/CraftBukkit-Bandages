/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bekvon.bukkit.bandages;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author Administrator
 */
public class BandagePlayerListener implements Listener {

    @EventHandler()
    public void onPlayerMove(PlayerMoveEvent event) {
        Bandages.getManager().playerMovement(event.getPlayer());
    }

    @EventHandler()
    public void onPlayerInteract(PlayerInteractEvent event) {
        Bandages.getManager().playerBandageEvent(event.getPlayer());
    }
}
