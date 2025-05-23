package io.shantek.Listeners;

import io.shantek.CustomMobDrops;
import io.shantek.Helpers.CustomDropConfig;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;

import java.util.List;
import java.util.Random;

public class EntityDeath implements Listener {

    private final Random random = new Random();
    private final CustomMobDrops plugin;

    public EntityDeath(CustomMobDrops plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!plugin.pluginConfig.isCustomMobDropsEnabled()) {
            return;
        }


        Entity killedEntity = event.getEntity();
        EntityType entityType = killedEntity.getType();

        CustomDropConfig.MobDropConfig mobDropConfig = plugin.customDropConfig.getDrops(entityType);

        if (mobDropConfig == null || !mobDropConfig.isEnabled()) {
            return;
        }


        // Should we cancel the vanilla drops?
        if (mobDropConfig.cancelVanillaDrops) {
            event.getDrops().clear(); // Remove vanilla drops
        }

        List<CustomDropConfig.DropItemConfig> drops = mobDropConfig.getDrops();
        boolean dropAll = mobDropConfig.isDropAll();

        int lootingLevel = event.getEntity().getKiller() != null
                ? event.getEntity().getKiller().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS)
                : 0;

        if (dropAll) {
            for (CustomDropConfig.DropItemConfig drop : drops) {
                processDrop(drop, killedEntity, lootingLevel, event.getEntity().getKiller().getName());
            }
        } else {
            if (!drops.isEmpty()) {
                CustomDropConfig.DropItemConfig drop = drops.get(random.nextInt(drops.size()));
                processDrop(drop, killedEntity, lootingLevel, event.getEntity().getKiller().getName());
            }
        }
    }


    private void processDrop(CustomDropConfig.DropItemConfig drop, Entity killedEntity, int lootingLevel, String playerName) {
        String itemName = drop.getItem();
        Material material = Material.getMaterial(itemName.toUpperCase());

        if (material == null) {
            plugin.getLogger().severe("Invalid material in custom-drops.yml for entity " + killedEntity.getType().name() + ": " + itemName);
            return;
        }

        int min = drop.getMin();
        int max = drop.getMax();
        int range = max - min + 1;
        int amount;

        if (plugin.pluginConfig.isLootingMultiplierEnabled()) {
            if (lootingLevel > 0) {
                double lootingFactor = Math.pow(random.nextDouble(), 1.0 / (1.0 + lootingLevel * 0.5));
                amount = (int) (min + lootingFactor * range);

                int bonusAmount = lootingLevel * (range / 15);
                amount += bonusAmount;

                if (amount > max) {
                    amount = max;
                }
            } else {
                double lootingBias = 0.75;
                double randomValue = Math.pow(random.nextDouble(), 1.0 + lootingBias);
                amount = (int) (min + randomValue * range);
            }
        } else {
            amount = random.nextInt(range) + min;
        }

        if (plugin.pluginConfig.isDebuggingEnabled()) {  // Only show debug info if debugging is enabled
            plugin.getLogger().info("Player " + playerName + " is using looting level: " + lootingLevel);
            plugin.getLogger().info("Initial drop range: " + min + " to " + max);
            plugin.getLogger().info("Final drop amount: " + amount);
        }

        if (amount > 0) {
            ItemStack itemStack = new ItemStack(material, amount);
            killedEntity.getWorld().dropItemNaturally(killedEntity.getLocation(), itemStack);
        }

        if (plugin.pluginConfig.isDebuggingEnabled() && killedEntity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) killedEntity;
            if (livingEntity.getKiller() != null) {
                livingEntity.getKiller().sendMessage(
                        "Debug: Drop calculation for " + killedEntity.getType().name() + "\n" +
                                "Initial drop range: " + min + " to " + max + "\n" +
                                "Looting level: " + lootingLevel + "\n" +
                                "Final drop amount: " + amount
                );
            }
        }
    }


}
