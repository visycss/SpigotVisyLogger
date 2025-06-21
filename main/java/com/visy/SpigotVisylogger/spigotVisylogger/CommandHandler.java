package com.visy.SpigotVisylogger.spigotVisylogger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandHandler implements CommandExecutor {

    private final SpigotVisylogger plugin;

    public CommandHandler(SpigotVisylogger plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("§cУ вас нет прав для использования этой команды!");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                handleReload(sender);
                break;
            case "info":
                if (args.length > 1) {
                    handleInfo(sender, args[1]);
                } else {
                    sender.sendMessage("§cИспользование: /visylogger info <игрок>");
                }
                break;
            case "help":
                sendHelp(sender);
                break;
            default:
                sender.sendMessage("§cНеизвестная команда! Используйте /visylogger help");
                break;
        }

        return true;
    }

    private void handleReload(CommandSender sender) {
        try {
            plugin.getConfigManager().loadConfig();
            sender.sendMessage("§aКонфигурация успешно перезагружена!");
        } catch (Exception e) {
            sender.sendMessage("§cОшибка при перезагрузке конфигурации: " + e.getMessage());
            plugin.getLogger().severe("Ошибка при перезагрузке конфигурации: " + e.getMessage());
        }
    }

    private void handleInfo(CommandSender sender, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage("§cИгрок не найден!");
            return;
        }

        UUID playerId = target.getUniqueId();
        ActivityTracker.PlayerData data = plugin.getActivityTracker().getPlayerData(playerId);

        if (data == null) {
            sender.sendMessage("§eДанных об активности игрока " + playerName + " нет.");
            return;
        }

        sender.sendMessage("§6=== Информация об игроке " + playerName + " ===");
        sender.sendMessage("§eПоследняя зафиксированная скорость: §c" +
                String.format("%.2f", data.getLastSpeed()) + " §eм/с");

        // Здесь можно добавить больше информации о добыче
        sender.sendMessage("§eДля получения детальной статистики добычи используйте логи сервера.");
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== SpigotVisylogger - Помощь ===");
        sender.sendMessage("§e/visylogger reload §7- Перезагрузить конфигурацию");
        sender.sendMessage("§e/visylogger info <игрок> §7- Показать информацию об игроке");
        sender.sendMessage("§e/visylogger help §7- Показать эту справку");
    }
}
