package io.shantek.Helpers;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomDropConfig {

    private final JavaPlugin plugin;
    private final Map<EntityType, MobDropConfig> entityDrops = new HashMap<>();
    private final Functions functions;

    public CustomDropConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        this.functions = new Functions(plugin);
    }

    public void loadConfig() {
        loadConfig(null);
    }

    public void loadConfig(CommandSender sender) {
        entityDrops.clear(); // Clear existing data

        File configFile = new File(plugin.getDataFolder(), "custom-drops.yml");
        if (!configFile.exists()) {
            plugin.saveResource("custom-drops.yml", false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection mobsSection = config.getConfigurationSection("mobs");

        if (mobsSection == null) {
            functions.sendMessage(sender, "No 'mobs' section found in custom-drops.yml", true);
            return;
        }

        for (String entity : mobsSection.getKeys(false)) {
            try {
                EntityType entityType = EntityType.valueOf(entity.toUpperCase());
                boolean dropAll = mobsSection.getBoolean(entity + ".drop-all", false);
                ConfigurationSection dropsSection = mobsSection.getConfigurationSection(entity + ".drops");
                if (dropsSection == null) {
                    functions.sendMessage(sender, "No 'drops' section found for entity: " + entity, true);
                } else {
                    List<DropItemConfig> drops = loadDrops(dropsSection, entity, sender);
                    if (!drops.isEmpty()) {
                        entityDrops.put(entityType, new MobDropConfig(dropAll, drops));
                        plugin.getLogger().info("Configured drops for " + entityType.name() + ": " + drops.size() + " items.");
                    }
                }
            } catch (IllegalArgumentException e) {
                functions.sendMessage(sender, "Invalid entity type in custom-drops.yml: " + entity, true);
            }
        }
    }

    private List<DropItemConfig> loadDrops(ConfigurationSection dropsSection, String entity, CommandSender sender) {
        List<DropItemConfig> drops = new ArrayList<>();
        for (String item : dropsSection.getKeys(false)) {
            Material material = Material.getMaterial(item.toUpperCase());
            if (material == null) {
                functions.sendMessage(sender, "Invalid material in custom-drops.yml for entity " + entity + ": " + item, true);
                continue;
            }

            int min = dropsSection.getInt(item + ".min");
            int max = dropsSection.getInt(item + ".max");
            if (min <= max) {
                drops.add(new DropItemConfig(item, min, max));
                plugin.getLogger().info("Added drop: " + item + " (min: " + min + ", max: " + max + ")");
            } else {
                functions.sendMessage(sender, "Invalid drop configuration for item: " + item + " (min: " + min + ", max: " + max + ")", true);
            }
        }
        return drops;
    }

    public MobDropConfig getDrops(EntityType entityType) {
        return entityDrops.get(entityType);
    }

    public static class MobDropConfig {
        private final boolean dropAll;
        private final List<DropItemConfig> drops;

        public MobDropConfig(boolean dropAll, List<DropItemConfig> drops) {
            this.dropAll = dropAll;
            this.drops = drops;
        }

        public boolean isDropAll() {
            return dropAll;
        }

        public List<DropItemConfig> getDrops() {
            return drops;
        }
    }

    public static class DropItemConfig {
        private final String item;
        private final int min;
        private final int max;

        public DropItemConfig(String item, int min, int max) {
            this.item = item;
            this.min = min;
            this.max = max;
        }

        public String getItem() {
            return item;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }
    }
}
