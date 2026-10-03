package io.shantek.Listeners;

import io.shantek.CustomMobDrops;
import io.shantek.Helpers.CustomDropConfig;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;

import java.util.List;
import java.util.Random;

public class EntityDeath implements Listener {

    // Looked up by key: the LOOT_BONUS_MOBS field was renamed to LOOTING in 1.20.5,
    // and the key "minecraft:looting" works across all supported versions
    private static final Enchantment LOOTING = Enchantment.getByKey(NamespacedKey.minecraft("looting"));

    private final Random random = new Random();
    private final CustomMobDrops plugin;

    public EntityDeath(CustomMobDrops plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!plugin.pluginConfig.isCustomMobDropsEnabled()) return;

        Entity killedEntity = event.getEntity();
        EntityType entityType = killedEntity.getType();

        // Resolve killer — may be null for non-player kills
        Player killer = event.getEntity().getKiller();
        String killerName = killer != null ? killer.getName() : "Unknown";

        // Find the first matching drop entry for this mob and killer
        CustomDropConfig.DropEntryConfig entry = plugin.customDropConfig.getMatchingEntry(entityType, killer);
        if (entry == null) return;

        // Handle vanilla drops
        if (!entry.isKeepVanillaDrops()) {
            event.getDrops().clear();
        }

        // Looting level — 0 if no player killer
        int lootingLevel = killer != null && LOOTING != null
                ? killer.getInventory().getItemInMainHand().getEnchantmentLevel(LOOTING)
                : 0;

        // Process item drops
        if (entry.isDropAll()) {
            for (CustomDropConfig.DropItemConfig item : entry.getItems()) {
                processDrop(item, killedEntity, lootingLevel, killerName);
            }
        } else {
            // Weighted random selection
            CustomDropConfig.DropItemConfig selected = selectWeightedItem(entry);
            if (selected != null) {
                processDrop(selected, killedEntity, lootingLevel, killerName);
            }
        }

        // Apply XP multiplier if set and there's a player killer
        if (killer != null && entry.getXpMultiplier() != 1.0) {
            int vanilla = event.getDroppedExp();
            int bonus = (int) Math.round(vanilla * entry.getXpMultiplier()) - vanilla;
            event.setDroppedExp(vanilla + Math.max(0, bonus));

            if (plugin.pluginConfig.isDebuggingEnabled()) {
                plugin.getLogger().info("XP multiplier " + entry.getXpMultiplier() + "x applied: " + vanilla + " -> " + event.getDroppedExp());
            }
        }
    }

    private CustomDropConfig.DropItemConfig selectWeightedItem(CustomDropConfig.DropEntryConfig entry) {
        List<CustomDropConfig.DropItemConfig> items = entry.getItems();
        if (items.isEmpty()) return null;

        int roll = random.nextInt(entry.getTotalWeight());
        int cumulative = 0;
        for (CustomDropConfig.DropItemConfig item : items) {
            cumulative += item.getWeight();
            if (roll < cumulative) return item;
        }
        return items.get(items.size() - 1);
    }

    private void processDrop(CustomDropConfig.DropItemConfig drop, Entity killedEntity, int lootingLevel, String killerName) {
        int min = drop.getMin();
        int max = drop.getMax();
        int range = max - min + 1;
        int amount;

        if (plugin.pluginConfig.isLootingMultiplierEnabled() && lootingLevel > 0) {
            // With looting: bias towards higher end of range
            double lootingFactor = Math.pow(random.nextDouble(), 1.0 / (1.0 + lootingLevel * 0.5));
            amount = (int) (min + lootingFactor * range);
            int bonusAmount = (int) Math.round(lootingLevel * range / 15.0);
            amount = Math.min(amount + bonusAmount, max);
        } else {
            // No looting: uniform distribution
            amount = random.nextInt(range) + min;
        }

        if (plugin.pluginConfig.isDebuggingEnabled()) {
            plugin.getLogger().info("Player " + killerName + " | Looting: " + lootingLevel + " | Drop: " + drop.getItem() + " | Range: " + min + "-" + max + " | Amount: " + amount);
        }

        if (amount > 0) {
            ItemStack itemStack = new ItemStack(drop.getMaterial(), amount);
            killedEntity.getWorld().dropItemNaturally(killedEntity.getLocation(), itemStack);
        }

        if (plugin.pluginConfig.isDebuggingEnabled()) {
            LivingEntity living = (LivingEntity) killedEntity;
            Player player = living.getKiller();
            if (player != null) {
                player.sendMessage("§7[Debug] " + killedEntity.getType().name() + " → " + drop.getItem() +
                        " x" + amount + " (looting " + lootingLevel + ", range " + min + "-" + max + ")");
            }
        }
    }
}

