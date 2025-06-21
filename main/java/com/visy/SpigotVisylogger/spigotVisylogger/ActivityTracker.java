package com.visy.SpigotVisylogger.spigotVisylogger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ActivityTracker {

    private final SpigotVisylogger plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    private int taskId = -1;

    public ActivityTracker(SpigotVisylogger plugin) {
        this.plugin = plugin;
        this.playerDataMap = new ConcurrentHashMap<>();
        startCleanupTask();
    }

    public void recordMining(Player player, Material material, int amount) {
        UUID playerId = player.getUniqueId();
        PlayerData data = playerDataMap.computeIfAbsent(playerId, k -> new PlayerData());

        long currentTime = System.currentTimeMillis();
        data.addMiningRecord(material, amount, currentTime);

        // Проверяем, нужно ли отправить уведомление
        checkMiningActivity(player, material, data);
    }

    public void recordMovement(Player player, double speed) {
        UUID playerId = player.getUniqueId();
        PlayerData data = playerDataMap.computeIfAbsent(playerId, k -> new PlayerData());

        data.setLastSpeed(speed);

        // Проверяем скорость
        if (speed > plugin.getConfigManager().getMaxSpeed()) {
            notifyOperators(plugin.getConfigManager().getPrefix() +
                    plugin.getConfigManager().getSpeedAlertMessage()
                            .replace("{player}", player.getName())
                            .replace("{speed}", String.format("%.2f", speed)));
        }
    }

    private void checkMiningActivity(Player player, Material material, PlayerData data) {
        Map<Material, Integer> watchedMaterials = plugin.getConfigManager().getWatchedMaterials();

        if (!watchedMaterials.containsKey(material)) {
            return;
        }

        int threshold = watchedMaterials.get(material);
        int timeWindow = plugin.getConfigManager().getTimeWindow() * 1000; // в миллисекундах
        long currentTime = System.currentTimeMillis();

        // Подсчитываем количество добытого материала за временное окно
        int totalAmount = data.getMiningAmount(material, currentTime - timeWindow, currentTime);

        if (totalAmount >= threshold) {
            // Отправляем уведомление операторам
            String message = plugin.getConfigManager().getPrefix() +
                    plugin.getConfigManager().getMiningAlertMessage()
                            .replace("{player}", player.getName())
                            .replace("{amount}", String.valueOf(totalAmount))
                            .replace("{material}", material.name().toLowerCase().replace("_", " "))
                            .replace("{time}", String.valueOf(plugin.getConfigManager().getTimeWindow()));

            notifyOperators(message);
        }
    }

    private void notifyOperators(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                player.sendMessage(message);
            }
        }

        // Также записываем в консоль
        plugin.getLogger().info(message.replaceAll("§[0-9a-fk-or]", ""));
    }

    private void startCleanupTask() {
        // Запускаем задачу очистки старых данных каждые 5 минут
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            long cutoffTime = System.currentTimeMillis() - (plugin.getConfigManager().getTimeWindow() * 2 * 1000);

            for (PlayerData data : playerDataMap.values()) {
                data.cleanup(cutoffTime);
            }
        }, 6000L, 6000L); // 5 минут = 6000 тиков
    }

    public void shutdown() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    public PlayerData getPlayerData(UUID playerId) {
        return playerDataMap.get(playerId);
    }

    // Внутренний класс для хранения данных игрока
    public static class PlayerData {
        private final Map<Material, List<MiningRecord>> miningRecords;
        private double lastSpeed = 0.0;

        public PlayerData() {
            this.miningRecords = new HashMap<>();
        }

        public void addMiningRecord(Material material, int amount, long timestamp) {
            miningRecords.computeIfAbsent(material, k -> new ArrayList<>())
                    .add(new MiningRecord(amount, timestamp));
        }

        public int getMiningAmount(Material material, long startTime, long endTime) {
            List<MiningRecord> records = miningRecords.get(material);
            if (records == null) return 0;

            return records.stream()
                    .filter(record -> record.timestamp >= startTime && record.timestamp <= endTime)
                    .mapToInt(record -> record.amount)
                    .sum();
        }

        public void setLastSpeed(double speed) {
            this.lastSpeed = speed;
        }

        public double getLastSpeed() {
            return lastSpeed;
        }

        public void cleanup(long cutoffTime) {
            miningRecords.values().forEach(records ->
                    records.removeIf(record -> record.timestamp < cutoffTime));
        }

        private static class MiningRecord {
            final int amount;
            final long timestamp;

            MiningRecord(int amount, long timestamp) {
                this.amount = amount;
                this.timestamp = timestamp;
            }
        }
    }
}