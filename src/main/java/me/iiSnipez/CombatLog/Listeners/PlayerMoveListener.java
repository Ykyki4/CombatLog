/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.massivecraft.factions.Board
 *  com.massivecraft.factions.FLocation
 *  com.massivecraft.factions.entity.BoardColl
 *  com.massivecraft.factions.entity.Faction
 *  com.massivecraft.factions.entity.FactionColl
 *  com.massivecraft.massivecore.ps.PS
 *  com.sk89q.worldguard.bukkit.WGBukkit
 *  com.sk89q.worldguard.protection.ApplicableRegionSet
 *  com.sk89q.worldguard.protection.flags.DefaultFlag
 *  com.sk89q.worldguard.protection.flags.StateFlag
 *  com.sk89q.worldguard.protection.flags.StateFlag$State
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerMoveEvent
 */
package me.iiSnipez.CombatLog.Listeners;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.massivecore.ps.PS;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import me.iiSnipez.CombatLog.CombatLog;
import me.iiSnipez.CombatLog.Events.PlayerUntagEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener
implements Listener {
    CombatLog plugin;
    Faction factionIn;

    public PlayerMoveListener(CombatLog plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location l = player.getLocation();
        if (this.plugin.taggedPlayers.containsKey(player.getName())) {
            if (this.plugin.usesFactions && this.plugin.removeTagInSafeZone) {
                if (this.plugin.useNewFactions) {
                    this.factionIn = BoardColl.get().getFactionAt(PS.valueOf((Location) l));
                    if (this.factionIn.equals((Object) FactionColl.get().getSafezone())) {
                        PlayerUntagEvent event1 = new PlayerUntagEvent(player, PlayerUntagEvent.UntagCause.SAFE_AREA);
                        Bukkit.getServer().getPluginManager().callEvent((Event) event1);
                        return;
                    }
                }
                if (this.plugin.usesWorldGuard && this.plugin.removeTagInPvPDisabledArea) {
                    ApplicableRegionSet ars = WGBukkit.getPlugin().getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation());
                    if (ars.queryState(null, new StateFlag[]{DefaultFlag.PVP}) == StateFlag.State.DENY) {
                        PlayerUntagEvent event1 = new PlayerUntagEvent(player, PlayerUntagEvent.UntagCause.SAFE_AREA);
                        Bukkit.getServer().getPluginManager().callEvent((Event) event1);
                    }
                }
                if (this.plugin.removeFlyEnabled) {
                    this.plugin.removeFly(player);
                }
                if (this.plugin.removeDisguiseEnabled) {
                    this.plugin.removeDisguise(player);
                }
            }
        }
    }
}

