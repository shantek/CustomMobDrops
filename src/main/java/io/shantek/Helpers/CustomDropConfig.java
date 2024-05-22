package io.shantek.Helpers;

import org.bukkit.Bukkit;
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
    private final Map<String, MobDropConfig> entityDrops = new HashMap<>();

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

        if (mobsSection != null) {
            for (String entity : mobsSection.getKeys(false)) {
                try {
                    EntityType entityType = EntityType.valueOf(entity.toUpperCase());
                    boolean dropAll = mobsSection.getBoolean(entity + ".drop-all", false);
                    List<DropItemConfig> drops = new ArrayList<>();

                    ConfigurationSection dropsSection = mobsSection.getConfigurationSection(entity + ".drops");
                    if (dropsSection != null) {
                        for (String dropKey : dropsSection.getKeys(false)) {
                            String item = dropsSection.getString(dropKey + ".item");
                            int min = dropsSection.getInt(dropKey + ".min");
                            int max = dropsSection.getInt(dropKey + ".max");
                            drops.add(new DropItemConfig(item, min, max));
                        }
                    }

                    entityDrops.put(entityType.name(), new MobDropConfig(dropAll, drops));
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().severe("Invalid entity type in custom-drops.yml: " + entity);
                }
            }
        }
    }

    public MobDropConfig getDrops(String entityType) {
        return entityDrops.get(entityType.toUpperCase());
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
