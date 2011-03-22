/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bekvon.bukkit.bandages;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

/**
 *
 * @author Administrator
 */
public class BandageEntityListener extends EntityListener {

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            Entity e1 = e.getEntity();
            Entity e2 = e.getDamager();
            if(e1 instanceof Player && e2 instanceof Player)
            {
                Player p1 = (Player) e1;
                Player p2 = (Player) e2;
                if(p2.getItemInHand().getTypeId() == Bandages.getManager().getItemId())
                {
                    event.setCancelled(true);
                    Bandages.getManager().playerBandagePlayerEvent(p2, p1);
                }
            }
        }
        super.onEntityDamage(event);
    }
}
