/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package me.iiSnipez.CombatLog.Events;

import me.iiSnipez.CombatLog.CombatLog;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerCombatLogEvent
extends Event
implements Cancellable {
    private boolean cancelled;
    private Player player;
    CombatLog plugin;
    private static final HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

    public PlayerCombatLogEvent(CombatLog plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public long getTagTimeRemaining() {
        return (long)this.plugin.tagDuration - (this.plugin.getCurrentTime() - Long.valueOf(this.plugin.taggedPlayers.get(this.player.getName())));
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}

