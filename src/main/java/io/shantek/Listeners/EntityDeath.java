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

        if (mobDropConfig != null) {
            // Clear the default drops
            event.getDrops().clear();

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
        int amount = random.nextInt(max - min + 1) + min;

        plugin.getLogger().info("Player " + playerName + " is using looting level: " + lootingLevel);
        plugin.getLogger().info("Initial drop range: " + min + " to " + max + " (base drop amount before looting): " + amount);

        if (plugin.pluginConfig.isLootingMultiplierEnabled() && lootingLevel > 0) {
            double multiplier = 1.0 + (lootingLevel * 0.10);
            amount = (int) Math.floor(amount * multiplier);
            plugin.getLogger().info("Looting multiplier applied: " + multiplier + ", adjusted drop amount: " + amount);
        }

        if (amount > 0) {
            ItemStack itemStack = new ItemStack(material, amount);
            killedEntity.getWorld().dropItemNaturally(killedEntity.getLocation(), itemStack);
            plugin.getLogger().info("Final drop amount for " + itemName + ": " + amount);
        } else {
            plugin.getLogger().info("No items dropped for " + itemName + " (final drop amount: " + amount + ")");
        }

        if (killedEntity instanceof LivingEntity livingEntity) {
            if (livingEntity.getKiller() != null) {
                livingEntity.getKiller().sendMessage(
                        "Debug: Drop calculation for " + killedEntity.getType().name() + "\n" +
                                "Initial drop range: " + min + " to " + max + "\n" +
                                "Looting level: " + lootingLevel + "\n" +
                                "Multiplier applied: " + (plugin.pluginConfig.isLootingMultiplierEnabled() ? lootingLevel * 0.10 : 0) + "\n" +
                                "Final drop amount: " + amount
                );
            }
        }
    }
}
