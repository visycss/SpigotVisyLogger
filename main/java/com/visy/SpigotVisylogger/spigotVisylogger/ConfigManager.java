package com.visy.SpigotVisylogger.spigotVisylogger;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final SpigotVisylogger plugin;
    private final Map<Material, Integer> watchedMaterials;

    public ConfigManager(SpigotVisylogger plugin) {
        this.plugin = plugin;
        this.watchedMaterials = new HashMap<>();
        loadConfig();
    }

    public void loadConfig() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        watchedMaterials.clear();

        // Загружаем материалы для отслеживания
        List<String> materials = config.getStringList("watched-materials.materials");
        for (String materialStr : materials) {
            try {
                String[] parts = materialStr.split(":");
                Material material = Material.valueOf(parts[0].toUpperCase());
                int threshold = Integer.parseInt(parts[1]);
                watchedMaterials.put(material, threshold);
            } catch (Exception e) {
                plugin.getLogger().warning("Неверный формат материала: " + materialStr);
            }
        }

        plugin.getLogger().info("Загружено " + watchedMaterials.size() + " материалов для отслеживания");
    }

    public Map<Material, Integer> getWatchedMaterials() {
        return watchedMaterials;
    }

    public int getTimeWindow() {
        return plugin.getConfig().getInt("time-window", 300); // 5 минут по умолчанию
    }

    public double getMaxSpeed() {
        return plugin.getConfig().getDouble("max-speed", 10.0);
    }

    public int getSpeedCheckInterval() {
        return plugin.getConfig().getInt("speed-check-interval", 20); // 1 секунда
    }

    public boolean isDebugEnabled() {
        return plugin.getConfig().getBoolean("debug", false);
    }

    public String getPrefix() {
        return plugin.getConfig().getString("messages.prefix", "§c[VisyLogger]§r ");
    }

    public String getMiningAlertMessage() {
        return plugin.getConfig().getString("messages.mining-alert",
                "§e{player} добыл §c{amount}§e {material} §eза §c{time}§e секунд!");
    }

    public String getSpeedAlertMessage() {
        return plugin.getConfig().getString("messages.speed-alert",
                "§e{player} §eдвижется подозрительно быстро! Скорость: §c{speed}§e м/с");
    }
}