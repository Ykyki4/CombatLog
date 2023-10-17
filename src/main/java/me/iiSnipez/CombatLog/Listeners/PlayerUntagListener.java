/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 */
package me.iiSnipez.CombatLog.Listeners;

import me.iiSnipez.CombatLog.CombatLog;
import me.iiSnipez.CombatLog.Events.PlayerUntagEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerUntagListener
implements Listener {
    CombatLog plugin;

    public PlayerUntagListener(CombatLog plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onUntag(PlayerUntagEvent event) {
        Player player = event.getPlayer();
        String name = event.getPlayer().getName();
        if (event.getCause().equals((Object)PlayerUntagEvent.UntagCause.TIME_EXPIRE)) {
            if (this.plugin.useActionBar) {
                if (this.plugin.newActionBar) {
                    this.plugin.aBar.sendActionBarNew(player, this.plugin.actionBarUntagMessage);
                } else {
                    this.plugin.aBar.sendActionBarOld(player, this.plugin.actionBarUntagMessage);
                }
            }
            if (this.plugin.untagMessageEnabled) {
                player.sendMessage(this.plugin.translateText(this.plugin.untagMessage));
            }
            this.plugin.taggedPlayers.remove(name);
        } else if (event.getCause().equals((Object)PlayerUntagEvent.UntagCause.COMBATLOG)) {
            this.plugin.taggedPlayers.remove(name);
        } else if (event.getCause().equals((Object)PlayerUntagEvent.UntagCause.DEATH)) {
            if (this.plugin.untagMessageEnabled) {
                player.sendMessage(this.plugin.translateText(this.plugin.untagMessage));
            }
            this.plugin.taggedPlayers.remove(name);
        } else if (event.getCause().equals((Object)PlayerUntagEvent.UntagCause.KICK)) {
            this.plugin.taggedPlayers.remove(name);
        } else if (event.getCause().equals((Object)PlayerUntagEvent.UntagCause.LAGOUT)) {
            this.plugin.taggedPlayers.remove(name);
        } else if (event.getCause().equals((Object)PlayerUntagEvent.UntagCause.SAFE_AREA)) {
            if (this.plugin.untagMessageEnabled) {
                player.sendMessage(this.plugin.translateText(this.plugin.untagMessage));
            }
            if (this.plugin.useActionBar) {
                if (this.plugin.newActionBar) {
                    this.plugin.aBar.sendActionBarNew(player, this.plugin.actionBarUntagMessage);
                } else {
                    this.plugin.aBar.sendActionBarOld(player, this.plugin.actionBarUntagMessage);
                }
            }
            this.plugin.taggedPlayers.remove(name);
        }
    }
}

