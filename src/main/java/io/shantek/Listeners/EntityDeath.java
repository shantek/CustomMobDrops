package io.shantek.Listeners;

import io.shantek.CustomMobDrops;
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

            if (dropAll) {
                for (CustomDropConfig.DropItemConfig drop : drops) {
                    processDrop(drop, killedEntity);
                }
            } else {
                if (!drops.isEmpty()) {
                    CustomDropConfig.DropItemConfig drop = drops.get(random.nextInt(drops.size()));
                    processDrop(drop, killedEntity);
                }
            }
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
        int amount = random.nextInt(max - min + 1) + min;

        if (amount > 0) {
            ItemStack itemStack = new ItemStack(material, amount);
            killedEntity.getWorld().dropItemNaturally(killedEntity.getLocation(), itemStack);
        }
    }
}
