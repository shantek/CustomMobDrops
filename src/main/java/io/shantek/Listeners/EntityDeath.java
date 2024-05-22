package io.shantek.Listeners;

import io.shantek.CustomDrops;
import io.shantek.Helpers.CustomDropConfig;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class EntityDeath implements Listener {

    private final Random random = new Random();
    private final CustomDrops plugin;

    public EntityDeath(CustomDrops plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity killedEntity = event.getEntity();
        EntityType entityType = killedEntity.getType();
        plugin.getLogger().info("Entity died: " + entityType.name());

        CustomDropConfig.MobDropConfig mobDropConfig = plugin.customDropConfig.getDrops(entityType);

        if (mobDropConfig != null) {
            // Clear the default drops
            event.getDrops().clear();
            plugin.getLogger().info("Custom drops being processed for " + entityType.name());

            List<CustomDropConfig.DropItemConfig> drops = mobDropConfig.getDrops();
            boolean dropAll = mobDropConfig.isDropAll();

            plugin.getLogger().info("Drop all items: " + dropAll);
            if (dropAll) {
                for (CustomDropConfig.DropItemConfig drop : drops) {
                    plugin.getLogger().info("Processing drop: " + drop.getItem() + " (min: " + drop.getMin() + ", max: " + drop.getMax() + ")");
                    processDrop(drop, killedEntity);
                }
            } else {
                if (!drops.isEmpty()) {
                    CustomDropConfig.DropItemConfig drop = drops.get(random.nextInt(drops.size()));
                    plugin.getLogger().info("Randomly selected drop: " + drop.getItem() + " (min: " + drop.getMin() + ", max: " + drop.getMax() + ")");
                    processDrop(drop, killedEntity);
                } else {
                    plugin.getLogger().info("No drops configured for " + entityType.name());
                }
            }
        } else {
            plugin.getLogger().info("No custom drops configured for " + entityType.name());
        }
    }

    private void processDrop(CustomDropConfig.DropItemConfig drop, Entity killedEntity) {
        String itemName = drop.getItem();
        Material material = Material.getMaterial(itemName.toUpperCase());

        if (material == null) {
            plugin.getLogger().severe("Invalid material in custom-drops.yml for entity " + killedEntity.getType().name() + ": " + itemName);
            return;
        }

        int min = drop.getMin();
        int max = drop.getMax();
        plugin.getLogger().info("Dropping " + itemName + " with amount between " + min + " and " + max);

        int amount = random.nextInt(max - min + 1) + min;
        plugin.getLogger().info("Calculated drop amount: " + amount);
        if (amount > 0) {
            ItemStack itemStack = new ItemStack(material, amount);
            killedEntity.getWorld().dropItemNaturally(killedEntity.getLocation(), itemStack);
            plugin.getLogger().info("Dropped " + amount + " of " + itemName);
        } else {
            plugin.getLogger().info("Amount is zero, not dropping " + itemName);
        }
    }
}
