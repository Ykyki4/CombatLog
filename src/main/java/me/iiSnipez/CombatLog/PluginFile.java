/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package me.iiSnipez.CombatLog;

import java.io.File;
import java.io.IOException;
import me.iiSnipez.CombatLog.CombatLog;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PluginFile
extends YamlConfiguration {
    public CombatLog plugin;
    public FileConfiguration clConfig = null;
    public File clConfigFile = null;

    public PluginFile(CombatLog plugin) {
        this.plugin = plugin;
    }

    public void reloadCLConfig() {
        if (this.clConfigFile == null) {
            this.clConfigFile = new File(this.plugin.getDataFolder(), "config.yml");
        }
        this.clConfig = YamlConfiguration.loadConfiguration((File)this.clConfigFile);
    }

    public FileConfiguration getCLConfig() {
        if (this.clConfig == null) {
            this.reloadCLConfig();
        }
        return this.clConfig;
    }

    public void saveCLConfig() {
        if (this.clConfig == null || this.clConfigFile == null) {
            return;
        }
        try {
            this.getCLConfig().save(this.clConfigFile);
        }
        catch (IOException ex) {
            this.plugin.log.warning("Could not save config to " + this.clConfigFile);
            this.plugin.log.warning(ex.getMessage());
        }
    }

    public void saveDefault() {
        if (!this.clConfigFile.exists()) {
            this.plugin.saveResource("config.yml", false);
        }
    }
}

