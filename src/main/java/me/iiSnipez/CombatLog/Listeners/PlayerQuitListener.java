/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerQuitEvent
 */
package me.iiSnipez.CombatLog.Listeners;

import me.iiSnipez.CombatLog.CombatLog;
import me.iiSnipez.CombatLog.Events.PlayerCombatLogEvent;
import me.iiSnipez.CombatLog.Events.PlayerUntagEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener
implements Listener {
    CombatLog plugin;
    public static String disconnectMsg = "";

    public PlayerQuitListener(CombatLog plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerOuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("combatlog.bypass")) {
            return;
        }
        if (!player.hasPermission("combatlog.bypass") && this.plugin.taggedPlayers.containsKey(player.getName())) {
            if (this.plugin.removeTagOnLagout && !this.playerCombatLogged()) {
                PlayerUntagEvent event1 = new PlayerUntagEvent(player, PlayerUntagEvent.UntagCause.LAGOUT);
                Bukkit.getServer().getPluginManager().callEvent((Event)event1);
                return;
            }
            if (this.plugin.broadcastEnabled) {
                this.plugin.broadcastMsg(this.plugin.translateText(this.plugin.broadcastMessage.replace("<name>", player.getName())));
            }
            if (this.plugin.executeCommandsEnabled) {
                for (String s : this.plugin.executeCommandList) {
                    Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getServer().getConsoleSender(), s.replaceAll("<name>", player.getName()));
                }
            }
            if (this.plugin.killEnabled) {
                player.setHealth(0.0);
                this.plugin.killPlayers.add(player.getUniqueId().toString());
            }
            PlayerCombatLogEvent event1 = new PlayerCombatLogEvent(this.plugin, player);
            this.plugin.getServer().getPluginManager().callEvent((Event)event1);
            PlayerUntagEvent event2 = new PlayerUntagEvent(player, PlayerUntagEvent.UntagCause.COMBATLOG);
            Bukkit.getServer().getPluginManager().callEvent((Event)event2);
            ++this.plugin.combatlogs;
        }
    }

    public static void setDisconnectMsg(String msg) {
        disconnectMsg = msg;
    }

    public boolean playerCombatLogged() {
        return !disconnectMsg.equalsIgnoreCase("disconnect.overflow") && !disconnectMsg.equalsIgnoreCase("disconnect.genericreason") && !disconnectMsg.equalsIgnoreCase("disconnect.timeout");
    }
}

