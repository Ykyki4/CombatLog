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

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTagEvent
extends Event
implements Cancellable {
    private Entity damager;
    private Entity damagee;
    private int time;
    private boolean cancelled;
    private static HandlerList handlerList = new HandlerList();

    public PlayerTagEvent(Entity damager, Entity damagee, int time) {
        this.damager = damager;
        this.damagee = damagee;
        this.time = time;
    }

    public Entity getDamager() {
        return this.damager;
    }

    public Entity getDamagee() {
        return this.damagee;
    }

    public int getTagTime() {
        return this.time;
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
}

