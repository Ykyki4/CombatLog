/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerKickEvent
 */
package me.iiSnipez.CombatLog.Listeners;

import me.iiSnipez.CombatLog.CombatLog;
import me.iiSnipez.CombatLog.Events.PlayerUntagEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public class PlayerKickListener
implements Listener {
    CombatLog plugin;

    public PlayerKickListener(CombatLog plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onKickEvent(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.taggedPlayers.containsKey(player.getName()) && this.plugin.removeTagOnKick && !event.getReason().toLowerCase().contains("spam")) {
            PlayerUntagEvent event2 = new PlayerUntagEvent(player, PlayerUntagEvent.UntagCause.KICK);
            Bukkit.getServer().getPluginManager().callEvent((Event)event2);
        }
    }
}

