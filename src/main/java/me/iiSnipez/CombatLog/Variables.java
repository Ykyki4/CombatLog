/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  de.robingrether.idisguise.api.DisguiseAPI
 *  org.bukkit.Bukkit
 */
package me.iiSnipez.CombatLog;

import de.robingrether.idisguise.api.DisguiseAPI;
import me.iiSnipez.CombatLog.CombatLog;
import org.bukkit.Bukkit;

public class Variables {
    CombatLog plugin;

    public Variables(CombatLog plugin) {
        this.plugin = plugin;
    }

    public void getValues() {
        this.plugin.logInfo("[CombatLog] Loading config.yml");
        this.plugin.updateCheckEnabled = this.plugin.clConfig.getCLConfig().getBoolean("UpdateCheck");
        this.plugin.autoDownloadEnabled = this.plugin.clConfig.getCLConfig().getBoolean("AutoDownload");
        this.plugin.MOTDEnabled = this.plugin.clConfig.getCLConfig().getBoolean("MOTD");
        this.plugin.broadcastEnabled = this.plugin.clConfig.getCLConfig().getBoolean("Broadcast");
        this.plugin.tagDuration = this.plugin.clConfig.getCLConfig().getInt("Tag-Duration");
        if (this.plugin.clConfig.getCLConfig().getStringList("Remove-Modes").contains("fly")) {
            this.plugin.removeFlyEnabled = true;
        }
        if (this.plugin.clConfig.getCLConfig().getStringList("Remove-Modes").contains("disguise")) {
            this.plugin.removeDisguiseEnabled = true;
        }
        this.plugin.removeTagOnKick = this.plugin.clConfig.getCLConfig().getBoolean("Remove-Tag.onKick");
        this.plugin.removeTagOnLagout = this.plugin.clConfig.getCLConfig().getBoolean("Remove-Tag.onLagout");
        this.plugin.removeTagInSafeZone = this.plugin.clConfig.getCLConfig().getBoolean("Remove-Tag.inSafeZone");
        this.plugin.removeTagInPvPDisabledArea = this.plugin.clConfig.getCLConfig().getBoolean("Remove-Tag.inPvPDisabledArea");
        this.plugin.removeInvisPotion = this.plugin.clConfig.getCLConfig().getBoolean("Remove-Invis-Potion");
        this.plugin.useActionBar = this.plugin.clConfig.getCLConfig().getBoolean("ActionBar");
        if (Bukkit.getServer().getVersion().contains("1.7")) {
            this.plugin.useActionBar = false;
        }
        this.plugin.useBossBar = this.plugin.clConfig.getCLConfig().getBoolean("BossBar");
        this.plugin.blockCommandsEnabled = this.plugin.clConfig.getCLConfig().getBoolean("Block-Commands");
        this.plugin.whitelistModeEnabled = this.plugin.clConfig.getCLConfig().getBoolean("Whitelist-Mode");
        this.plugin.commandNames = this.plugin.clConfig.getCLConfig().getStringList("Commands");
        this.plugin.executeCommandsEnabled = this.plugin.clConfig.getCLConfig().getBoolean("Execute-Commands");
        this.plugin.executeCommandList = this.plugin.clConfig.getCLConfig().getStringList("ExecutedCommands");
        this.plugin.blockTeleportationEnabled = this.plugin.clConfig.getCLConfig().getBoolean("Block-Teleportation");
        this.plugin.disableWorldNames = this.plugin.clConfig.getCLConfig().getStringList("Disabled-Worlds");
        this.plugin.killEnabled = this.plugin.clConfig.getCLConfig().getBoolean("Kill");
        this.plugin.updateCheckMessage = this.plugin.clConfig.getCLConfig().getString("UpdateCheckMessage");
        this.plugin.updateCheckMessageEnabled = !this.plugin.updateCheckMessage.equalsIgnoreCase("false");
        this.plugin.MOTDMessage = this.plugin.clConfig.getCLConfig().getString("MOTDMessage");
        this.plugin.MOTDMessageEnabled = !this.plugin.MOTDMessage.equalsIgnoreCase("false");
        this.plugin.broadcastMessage = this.plugin.clConfig.getCLConfig().getString("BroadcastMessage");
        this.plugin.broadcastMessageEnabled = !this.plugin.broadcastMessage.equalsIgnoreCase("false");
        this.plugin.taggerMessage = this.plugin.clConfig.getCLConfig().getString("TaggerMessage");
        this.plugin.taggerMessageEnabled = !this.plugin.taggerMessage.equalsIgnoreCase("false");
        this.plugin.taggedMessage = this.plugin.clConfig.getCLConfig().getString("TaggedMessage");
        this.plugin.taggedMessageEnabled = !this.plugin.taggedMessage.equalsIgnoreCase("false");
        this.plugin.untagMessage = this.plugin.clConfig.getCLConfig().getString("UntagMessage");
        this.plugin.untagMessageEnabled = !this.plugin.untagMessage.equalsIgnoreCase("false");
        this.plugin.tagTimeMessage = this.plugin.clConfig.getCLConfig().getString("InCombatMessage");
        this.plugin.tagTimeMessageEnabled = !this.plugin.tagTimeMessage.equalsIgnoreCase("false");
        this.plugin.notInCombatMessage = this.plugin.clConfig.getCLConfig().getString("NotInCombatMessage");
        this.plugin.notInCombatMessageEnabled = !this.plugin.notInCombatMessage.equalsIgnoreCase("false");
        this.plugin.actionBarInCombatMessage = this.plugin.clConfig.getCLConfig().getString("ActionBarInCombatMessage");
        this.plugin.actionBarUntagMessage = this.plugin.clConfig.getCLConfig().getString("ActionBarUntagMessage");
        this.plugin.removeModesMessage = this.plugin.clConfig.getCLConfig().getString("RemoveModesMessage");
        this.plugin.removeModesMessageEnabled = !this.plugin.removeModesMessage.equalsIgnoreCase("false");
        this.plugin.removeInvisMessage = this.plugin.clConfig.getCLConfig().getString("RemoveInvisMessage");
        this.plugin.removeInvisMessageEnabled = !this.plugin.removeInvisMessage.equalsIgnoreCase("false");
        this.plugin.blockCommandsMessage = this.plugin.clConfig.getCLConfig().getString("BlockCommandsMessage");
        this.plugin.blockCommandsMessageEnabled = !this.plugin.blockCommandsMessage.equalsIgnoreCase("false");
        this.plugin.blockTeleportationMessage = this.plugin.clConfig.getCLConfig().getString("BlockTeleportationMessage");
        this.plugin.blockTeleportationMessageEnabled = !this.plugin.blockTeleportationMessage.equalsIgnoreCase("false");
        this.plugin.killMessage = this.plugin.clConfig.getCLConfig().getString("KillMessage");
        this.plugin.killMessageEnabled = !this.plugin.killMessage.equalsIgnoreCase("false");
        this.plugin.newActionBar = Bukkit.getVersion().contains("1.12");
    }

    public DisguiseAPI initDis() {
        return (DisguiseAPI)Bukkit.getServer().getServicesManager().getRegistration(DisguiseAPI.class).getProvider();
    }
}

