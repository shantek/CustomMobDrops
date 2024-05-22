package io.shantek.Helpers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomDropConfig {

    private final JavaPlugin plugin;
    private final Map<EntityType, MobDropConfig> entityDrops = new HashMap<>();

    public CustomDropConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        entityDrops.clear(); // Clear existing data

        File configFile = new File(plugin.getDataFolder(), "custom-drops.yml");
        if (!configFile.exists()) {
            plugin.saveResource("custom-drops.yml", false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection mobsSection = config.getConfigurationSection("mobs");

        if (mobsSection == null) {
            plugin.getLogger().severe("No 'mobs' section found in custom-drops.yml");
            return;
        }

        for (String entity : mobsSection.getKeys(false)) {
            try {
                EntityType entityType = EntityType.valueOf(entity.toUpperCase());
                boolean dropAll = mobsSection.getBoolean(entity + ".drop-all", false);
                ConfigurationSection dropsSection = mobsSection.getConfigurationSection(entity + ".drops");
                if (dropsSection == null) {
                    plugin.getLogger().severe("No 'drops' section found for entity: " + entity);
                } else {
                    List<DropItemConfig> drops = loadDrops(dropsSection);
                    entityDrops.put(entityType, new MobDropConfig(dropAll, drops));
                    plugin.getLogger().info("Configured drops for " + entityType.name() + ": " + drops.size() + " items.");
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().severe("Invalid entity type in custom-drops.yml: " + entity);
            }
        }
    }

    private List<DropItemConfig> loadDrops(ConfigurationSection dropsSection) {
        List<DropItemConfig> drops = new ArrayList<>();
        for (String item : dropsSection.getKeys(false)) {
            int min = dropsSection.getInt(item + ".min");
            int max = dropsSection.getInt(item + ".max");
            if (min <= max) {
                drops.add(new DropItemConfig(item, min, max));
                plugin.getLogger().info("Added drop: " + item + " (min: " + min + ", max: " + max + ")");
            } else {
                plugin.getLogger().severe("Invalid drop configuration for item: " + item + " (min: " + min + ", max: " + max + ")");
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
