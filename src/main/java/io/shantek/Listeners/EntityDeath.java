package io.shantek.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class EntityDeath {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Shulker && this.pluginEnabled && this.pluginEnabled) {
            int numShells = false;
            int numShells;
            if (this.minShells == this.maxShells) {
                numShells = this.minShells;
            } else {
                numShells = ThreadLocalRandom.current().nextInt(this.minShells, this.maxShells + 1);
            }

            if (this.debugMode) {
                this.getLogger().info("Shulker should drop " + (numShells + 1) + " shells");
            }

            event.getDrops().clear();

            for(int i = 0; i <= numShells; ++i) {
                event.getDrops().add(new ItemStack(Material.SHULKER_SHELL));
            }
        }

    }

}
