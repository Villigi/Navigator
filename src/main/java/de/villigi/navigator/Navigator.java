package de.villigi.navigator;

import de.villigi.navigator.config.ConfigManager;
import de.villigi.navigator.listener.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Navigator extends JavaPlugin {

    private static Navigator instance;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager(this);
        configManager.createConfigFile();

        getServer().getPluginManager().registerEvents(new Listener(), this);

        configManager.loadItems();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Navigator getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
