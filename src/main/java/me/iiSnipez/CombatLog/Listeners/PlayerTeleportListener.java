/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerTeleportEvent
 *  org.bukkit.event.player.PlayerTeleportEvent$TeleportCause
 */
package me.iiSnipez.CombatLog.Listeners;

import me.iiSnipez.CombatLog.CombatLog;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener
implements Listener {
    CombatLog plugin;

    public PlayerTeleportListener(CombatLog plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.taggedPlayers.containsKey(player.getName()) && this.plugin.blockTeleportationEnabled) {
            if (Bukkit.getVersion().contains("1.12") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.9")) {
                if (event.getCause().equals((Object)PlayerTeleportEvent.TeleportCause.COMMAND) || event.getCause().equals((Object)PlayerTeleportEvent.TeleportCause.END_GATEWAY) || event.getCause().equals((Object)PlayerTeleportEvent.TeleportCause.END_PORTAL) || event.getCause().equals((Object)PlayerTeleportEvent.TeleportCause.ENDER_PEARL) || event.getCause().equals((Object)PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) || event.getCause().equals((Object)PlayerTeleportEvent.TeleportCause.PLUGIN) || event.getCause().equals((Object)PlayerTeleportEvent.TeleportCause.SPECTATE) || event.getCause().equals((Object)PlayerTeleportEvent.TeleportCause.UNKNOWN) || event.getCause().equals((Object)PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT)) {
                    event.setCancelled(true);
                    if (this.plugin.blockTeleportationMessageEnabled) {
                        player.sendMessage(this.plugin.translateText(this.plugin.blockTeleportationMessage));
                    }
                }
            } else if (event.getCause().equals((Object)PlayerTeleportEvent.TeleportCause.COMMAND) || event.getCause().equals((Object)PlayerTeleportEvent.TeleportCause.END_PORTAL) || event.getCause().equals((Object)PlayerTeleportEvent.TeleportCause.ENDER_PEARL) || event.getCause().equals((Object)PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) || event.getCause().equals((Object)PlayerTeleportEvent.TeleportCause.PLUGIN) || event.getCause().equals((Object)PlayerTeleportEvent.TeleportCause.SPECTATE) || event.getCause().equals((Object)PlayerTeleportEvent.TeleportCause.UNKNOWN)) {
                event.setCancelled(true);
                if (this.plugin.blockTeleportationMessageEnabled) {
                    player.sendMessage(this.plugin.translateText(this.plugin.blockTeleportationMessage));
                }
            }
        }
    }
}

