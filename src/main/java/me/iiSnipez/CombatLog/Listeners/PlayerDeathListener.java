/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.PlayerDeathEvent
 */
package me.iiSnipez.CombatLog.Listeners;

import me.iiSnipez.CombatLog.CombatLog;
import me.iiSnipez.CombatLog.Events.PlayerUntagEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener
implements Listener {
    public CombatLog plugin;

    public PlayerDeathListener(CombatLog plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        if (this.plugin.taggedPlayers.containsKey(player.getName())) {
            PlayerUntagEvent event1 = new PlayerUntagEvent(player, PlayerUntagEvent.UntagCause.DEATH);
            Bukkit.getServer().getPluginManager().callEvent((Event)event1);
        }
        if (this.plugin.killPlayers.contains(player.getUniqueId().toString())) {
            this.plugin.killPlayers.remove(player.getUniqueId().toString());
        }
    }
}

