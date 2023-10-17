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

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerUntagEvent
extends Event
implements Cancellable {
    private Player player;
    private boolean cancelled;
    private final UntagCause cause;
    private static HandlerList handlerList = new HandlerList();

    public PlayerUntagEvent(Player player, UntagCause uc) {
        this.player = player;
        this.cause = uc;
    }

    public Player getPlayer() {
        return this.player;
    }

    public UntagCause getCause() {
        return this.cause;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public static enum UntagCause {
        COMBATLOG,
        KICK,
        TIME_EXPIRE,
        DEATH,
        LAGOUT,
        SAFE_AREA;

    }
}

