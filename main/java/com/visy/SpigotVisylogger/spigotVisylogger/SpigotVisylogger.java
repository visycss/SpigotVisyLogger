package com.visy.SpigotVisylogger.spigotVisylogger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpigotVisylogger extends JavaPlugin {

    private static SpigotVisylogger instance;
    private ActivityTracker activityTracker;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;

        // Создаем файл конфигурации
        saveDefaultConfig();

        // Инициализируем менеджеры
        configManager = new ConfigManager(this);
        activityTracker = new ActivityTracker(this);

        // Регистрируем слушатели событий
        getServer().getPluginManager().registerEvents(new MiningListener(this), this);
        getServer().getPluginManager().registerEvents(new MovementListener(this), this);

        // Регистрируем команды
        getCommand("visylogger").setExecutor(new CommandHandler(this));

        getLogger().info("SpigotVisylogger включен!");
    }

    @Override
    public void onDisable() {
        if (activityTracker != null) {
            activityTracker.shutdown();
        }
        getLogger().info("SpigotVisylogger отключен!");
    }

    public static SpigotVisylogger getInstance() {
        return instance;
    }

    public ActivityTracker getActivityTracker() {
        return activityTracker;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}