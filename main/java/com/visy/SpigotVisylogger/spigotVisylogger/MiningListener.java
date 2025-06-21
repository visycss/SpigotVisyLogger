package com.visy.SpigotVisylogger.spigotVisylogger;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class MiningListener implements Listener {

    private final SpigotVisylogger plugin;

    public MiningListener(SpigotVisylogger plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Игнорируем креативный режим
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        // Проверяем, отслеживается ли этот материал
        Material blockType = block.getType();
        if (!plugin.getConfigManager().getWatchedMaterials().containsKey(blockType)) {
            return;
        }

        // Подсчитываем количество дропа
        int dropAmount = calculateDropAmount(block, player);

        if (dropAmount > 0) {
            plugin.getActivityTracker().recordMining(player, blockType, dropAmount);

            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getLogger().info(String.format("Игрок %s добыл %d %s",
                        player.getName(), dropAmount, blockType.name()));
            }
        }
    }

    private int calculateDropAmount(Block block, Player player) {
        // Симулируем дроп блока
        Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand(), player);

        int totalAmount = 0;
        for (ItemStack drop : drops) {
            if (drop.getType() == block.getType() || isRelatedDrop(block.getType(), drop.getType())) {
                totalAmount += drop.getAmount();
            }
        }

        return totalAmount;
    }

    private boolean isRelatedDrop(Material blockType, Material dropType) {
        // Проверяем связанные дропы (например, алмазная руда -> алмаз)
        switch (blockType) {
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
                return dropType == Material.DIAMOND;
            case EMERALD_ORE:
            case DEEPSLATE_EMERALD_ORE:
                return dropType == Material.EMERALD;
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
            case NETHER_GOLD_ORE:
                return dropType == Material.GOLD_INGOT || dropType == Material.RAW_GOLD || dropType == Material.GOLD_NUGGET;
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
                return dropType == Material.IRON_INGOT || dropType == Material.RAW_IRON;
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
                return dropType == Material.COAL;
            case REDSTONE_ORE:
            case DEEPSLATE_REDSTONE_ORE:
                return dropType == Material.REDSTONE;
            case LAPIS_ORE:
            case DEEPSLATE_LAPIS_ORE:
                return dropType == Material.LAPIS_LAZULI;
            case COPPER_ORE:
            case DEEPSLATE_COPPER_ORE:
                return dropType == Material.RAW_COPPER;
            case ANCIENT_DEBRIS:
                return dropType == Material.NETHERITE_SCRAP;
            default:
                return dropType == blockType;
        }
    }
}
