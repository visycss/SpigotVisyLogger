package com.visy.SpigotVisylogger.spigotVisylogger;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MovementListener implements Listener {

    private final SpigotVisylogger plugin;
    private final Map<UUID, LocationData> playerLocations;
    private int taskId = -1;

    public MovementListener(SpigotVisylogger plugin) {
        this.plugin = plugin;
        this.playerLocations = new HashMap<>();
        startSpeedCheckTask();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Игнорируем в креативе или в полете
        if (player.getGameMode() == GameMode.CREATIVE ||
                player.getGameMode() == GameMode.SPECTATOR ||
                player.isFlying()) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null || from.getWorld() != to.getWorld()) {
            return;
        }

        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        LocationData locationData = playerLocations.computeIfAbsent(playerId, k -> new LocationData());
        locationData.updateLocation(to, currentTime);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        playerLocations.put(playerId, new LocationData());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        playerLocations.remove(playerId);
    }

    private void startSpeedCheckTask() {
        int interval = plugin.getConfigManager().getSpeedCheckInterval();

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                checkPlayerSpeed(player);
            }
        }, interval, interval);
    }

    private void checkPlayerSpeed(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE ||
                player.getGameMode() == GameMode.SPECTATOR ||
                player.isFlying()) {
            return;
        }

        UUID playerId = player.getUniqueId();
        LocationData locationData = playerLocations.get(playerId);

        if (locationData != null) {
            double speed = locationData.calculateSpeed();
            if (speed > 0) {
                plugin.getActivityTracker().recordMovement(player, speed);
            }
        }
    }

    public void shutdown() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    private static class LocationData {
        private Location lastLocation;
        private long lastTime;
        private double currentSpeed = 0.0;

        public void updateLocation(Location location, long time) {
            if (lastLocation != null && lastTime > 0) {
                double distance = lastLocation.distance(location);
                long timeDiff = time - lastTime;

                if (timeDiff > 0) {
                    // Скорость в блоках/секунду
                    currentSpeed = (distance * 1000.0) / timeDiff;
                }
            }

            lastLocation = location.clone();
            lastTime = time;
        }

        public double calculateSpeed() {
            return currentSpeed;
        }
    }
}