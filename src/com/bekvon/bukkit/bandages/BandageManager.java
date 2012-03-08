/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bekvon.bukkit.bandages;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;



/**
 *
 * @author Administrator
 */
public class BandageManager {
    private final Object sync = new Object();
    private Map<Player, Player> playerMap;
    private Map<Player, Player> recieverPlayerMap;
    private Map<Player, Long> timeStamp;
    private int itemId;
    private int amountRequired;
    private int healamount;
    private int timedelay;
    private int maxhealth;
    private boolean enabled;
    private boolean movementAllowed;

    public BandageManager()
    {
        playerMap = Collections.synchronizedMap(new HashMap<Player,Player>());
        recieverPlayerMap = Collections.synchronizedMap(new HashMap<Player,Player>());
        timeStamp = Collections.synchronizedMap(new HashMap<Player,Long>());
    }

    public void playerMovement(Player player)
    {
        if(!movementAllowed)
        {
            synchronized(sync)
            {
                Player sender = null;
                Player reciever = null;
                if(recieverPlayerMap.containsKey(player))
                {
                    sender = recieverPlayerMap.remove(player);
                    reciever = playerMap.remove(sender);
                }
                else if(playerMap.containsKey(player))
                {
                    reciever = playerMap.remove(player);
                    sender = recieverPlayerMap.remove(reciever);
                }
                if(sender != null && reciever != null)
                {
                    timeStamp.remove(sender);
                    if(sender.isOnline())
                        sender.sendMessage("§cMovement has cancelled bandages.");
                    if(reciever.isOnline() && !reciever.equals(sender))
                        reciever.sendMessage("§cMovement has cancelled bandages.");
                }
            }
        }
    }
    
    public void loadConfig(Configuration config)
    {
        config.options().copyDefaults(true);
        itemId=config.getInt("itemId", Material.PAPER.getId());
        timedelay = config.getInt("bandageDelay", 4) * 1000;
        maxhealth = config.getInt("maxHealth", 20);
        movementAllowed = !config.getBoolean("requireStandStill", true);
        amountRequired = config.getInt("amountRequired", 1);
        healamount = config.getInt("healAmount", 5);
    }

    public void playerBandagePlayerEvent(Player sender, Player reciever)
    {
        ItemStack item = sender.getItemInHand();
        if(item.getTypeId() == itemId)
        {
            if(!Bandages.hasAuthority(sender, "bandages.use", true))
            {
                sender.sendMessage("§cYou cant use bandages.");
                return;
            }
            if(reciever.getHealth()>= maxhealth)
            {
                sender.sendMessage("§cTarget is already at full heatlh!");
                return;
            }

            if(!(item.getAmount() >= amountRequired))
            {
                sender.sendMessage("§cYou are not holding enough bandages in your hand.");
                return;
            }
            synchronized(sync)
            {
                if(playerMap.containsKey(sender))
                {
                    sender.sendMessage("§cYou are already bandaging somone.");
                    return;
                }
                if(recieverPlayerMap.containsKey(reciever))
                {
                    sender.sendMessage("§cSomone else is already bandaging that player.");
                    return;
                }
                timeStamp.put(sender, System.currentTimeMillis());
                playerMap.put(sender, reciever);
                recieverPlayerMap.put(reciever, sender);
            }
            sender.sendMessage("§eYou begin applying bandages to §f" + reciever.getName());
            reciever.sendMessage(sender.getName() + "§e has begun bandaging you.");
        }
    }

    public void playerBandageEvent(Player player) {
        ItemStack item = player.getItemInHand();
        if (item.getTypeId() == itemId) {
            if (!Bandages.hasAuthority(player, "bandages.use", true)) {
                player.sendMessage("§cYou cant use bandages.");
                return;
            }
            if (player.getHealth() >= maxhealth) {
                player.sendMessage("§cYou are already at full health!");
                return;
            }
            if (item.getAmount() >= amountRequired) {
                synchronized (sync) {
                    if (playerMap.containsKey(player)) {
                        player.sendMessage("§cYou are already applying bandages!");
                    } else if (recieverPlayerMap.containsKey(player)) {
                        player.sendMessage("§cCan't bandage while somone is applying bandages to you!");
                    } else {
                        player.sendMessage("§aYou begin applying bandages...");
                        timeStamp.put(player, System.currentTimeMillis());
                        playerMap.put(player, player);
                        recieverPlayerMap.put(player, player);
                    }
                }
            } else {
                player.sendMessage("§cYou are not holding enough bandages in your hand.");
            }
        }
    }

    public void startThread() {
        enabled = true;
        Thread runThread = new Thread(new Runnable() {
            public void run() {
                while (enabled) {
                    try {
                        Set<Entry<Player, Long>> set = timeStamp.entrySet();
                        synchronized (sync) {
                            Iterator<Entry<Player, Long>> it = set.iterator();
                            while (it.hasNext()) {
                                Entry<Player, Long> next = it.next();
                                if ((System.currentTimeMillis() - timedelay) > next.getValue()) {
                                    Player reciever = playerMap.remove(next.getKey());
                                    Player sender = recieverPlayerMap.remove(reciever);
                                    it.remove();
                                    if (sender != null && reciever != null && sender.isOnline() && reciever.isOnline()) {
                                        ItemStack item = sender.getInventory().getItemInHand();
                                        if (item.getTypeId() == itemId && item.getAmount() >= amountRequired) {
                                            if (item.getAmount() == amountRequired) {
                                                sender.getInventory().remove(item);
                                            } else {
                                                item.setAmount(item.getAmount() - amountRequired);
                                            }
                                            if (reciever.getHealth() + healamount > maxhealth)
                                                reciever.setHealth(maxhealth);
                                            else
                                                reciever.setHealth(healamount + reciever.getHealth());
                                            if (reciever.equals(sender)) {
                                                sender.sendMessage("§aYou finish applying bandages.");
                                            } else {
                                                sender.sendMessage("§aYou finish applying bandages on §f" + reciever.getName());
                                                reciever.sendMessage(sender.getName() + "§a has finished bandaging you.");
                                            }
                                        } else {
                                            sender.sendMessage("§cNot enough bandages in your hands!");
                                            if (!reciever.equals(sender))
                                                reciever.sendMessage(sender.getName() + "§c has stopped applying bandages to you.");
                                        }
                                    }
                                }
                            }
                        }
                        Thread.sleep(1000);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
        runThread.start();
    }

    public void killThread()
    {
        enabled = false;
    }

    public int getItemId()
    {
        return itemId;
    }

}
