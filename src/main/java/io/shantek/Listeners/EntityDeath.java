package io.shantek.Listeners;

import io.shantek.CustomMobDrops;
import org.bukkit.Material;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class EntityDeath implements Listener {

    public CustomMobDrops customMobDrops;
    public EntityDeath(CustomMobDrops customMobDrops) {
        this.customMobDrops = customMobDrops;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Shulker && customMobDrops.pluginEnabled) {
            //int numShells = false;
            int numShells;
            if (customMobDrops.minShells == customMobDrops.maxShells) {
                numShells = customMobDrops.minShells;
            } else {
                numShells = ThreadLocalRandom.current().nextInt(customMobDrops.minShells, customMobDrops.maxShells + 1);
            }

            if (customMobDrops.debugMode) {
                customMobDrops.getLogger().info("Shulker should drop " + (numShells + 1) + " shells");
            }

            event.getDrops().clear();

            for(int i = 0; i <= numShells; ++i) {
                event.getDrops().add(new ItemStack(Material.SHULKER_SHELL));
            }
        }

    }

}
