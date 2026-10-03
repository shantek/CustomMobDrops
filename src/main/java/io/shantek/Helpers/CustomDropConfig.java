package io.shantek.Helpers;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CustomDropConfig {

    private final JavaPlugin plugin;
    private final Map<EntityType, MobDropConfig> entityDrops = new LinkedHashMap<>();
    private final Functions functions;
    private File configFile;

    public CustomDropConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        this.functions = new Functions(plugin);
    }

    public void loadConfig() {
        loadConfig(null);
    }

    public void loadConfig(CommandSender sender) {
        entityDrops.clear();

        configFile = new File(plugin.getDataFolder(), "custom-drops.yml");
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
                ConfigurationSection mobSection = mobsSection.getConfigurationSection(entity);
                if (mobSection == null) continue;

                boolean enabled = mobSection.getBoolean("enabled", true);

                // Load drop entries as an ordered list (higher-tier first)
                List<DropEntryConfig> entries = new ArrayList<>();
                List<?> entriesList = mobSection.getList("drops");
                if (entriesList == null || entriesList.isEmpty()) {
                    functions.sendMessage(sender, "No 'drops' list found for entity: " + entity, true);
                } else {
                    for (Object obj : entriesList) {
                        if (!(obj instanceof Map)) continue;
                        @SuppressWarnings("unchecked")
                        Map<String, Object> entryMap = (Map<String, Object>) obj;
                        DropEntryConfig entry = loadDropEntry(entryMap, entity, sender);
                        if (entry != null) entries.add(entry);
                    }
                }

                if (!entries.isEmpty() || !enabled) {
                    entityDrops.put(entityType, new MobDropConfig(enabled, entries));
                    if (enabled) {
                        plugin.getLogger().info("Configured drops for " + entityType.name() + ": " + entries.size() + " entr" + (entries.size() == 1 ? "y" : "ies") + ".");
                    } else {
                        plugin.getLogger().info("Loaded " + entityType.name() + " (disabled).");
                    }
                }

            } catch (IllegalArgumentException e) {
                functions.sendMessage(sender, "Invalid entity type in custom-drops.yml: " + entity, true);
            }
        }
    }

    private DropEntryConfig loadDropEntry(Map<String, Object> map, String entity, CommandSender sender) {
        // permission (optional)
        String permission = map.containsKey("permission") ? String.valueOf(map.get("permission")) : null;

        // keep-vanilla-drops (optional, default false)
        boolean keepVanilla = map.containsKey("keep-vanilla-drops") && Boolean.TRUE.equals(map.get("keep-vanilla-drops"));

        // xp-multiplier (optional)
        double xpMultiplier = 1.0;
        if (map.containsKey("xp-multiplier")) {
            try {
                xpMultiplier = Double.parseDouble(String.valueOf(map.get("xp-multiplier")));
            } catch (NumberFormatException e) {
                functions.sendMessage(sender, "Invalid xp-multiplier in drop entry for: " + entity, true);
            }
        }

        // drop-all (optional, default false)
        boolean dropAll = map.containsKey("drop-all") && Boolean.TRUE.equals(map.get("drop-all"));

        // items list
        List<DropItemConfig> items = new ArrayList<>();
        Object itemsObj = map.get("items");
        if (!(itemsObj instanceof List)) {
            functions.sendMessage(sender, "Drop entry for " + entity + " is missing 'items' list.", true);
            return null;
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itemsList = (List<Map<String, Object>>) itemsObj;
        int totalWeight = 0;

        for (Map<String, Object> itemMap : itemsList) {
            String itemName = itemMap.containsKey("item") ? String.valueOf(itemMap.get("item")) : null;
            if (itemName == null) continue;

            Material material = Material.getMaterial(itemName.toUpperCase());
            if (material == null) {
                functions.sendMessage(sender, "Invalid material in drop entry for " + entity + ": " + itemName, true);
                continue;
            }

            int min = itemMap.containsKey("min") ? (int) itemMap.get("min") : 1;
            int max = itemMap.containsKey("max") ? (int) itemMap.get("max") : 1;
            if (min > max) {
                functions.sendMessage(sender, "Invalid min/max for item " + itemName + " in " + entity + " (min > max), skipping.", true);
                continue;
            }

            // Weight: default 1 if missing or invalid
            int weight = 1;
            if (itemMap.containsKey("weight")) {
                try {
                    int w = Integer.parseInt(String.valueOf(itemMap.get("weight")));
                    if (w > 0) weight = w;
                } catch (NumberFormatException ignored) {}
            }

            totalWeight += weight;
            items.add(new DropItemConfig(itemName, min, max, weight));
            plugin.getLogger().info("  Item: " + itemName + " (" + min + "-" + max + ", weight: " + weight + ")");
        }

        if (items.isEmpty()) return null;

        return new DropEntryConfig(permission, keepVanilla, xpMultiplier, dropAll, items, totalWeight);
    }

    /**
     * Returns the first matching drop entry for the given entity type and killer.
     * Entries are evaluated in config order (highest tier first).
     * If killer is null, only entries with no permission requirement are matched.
     */
    public DropEntryConfig getMatchingEntry(EntityType entityType, Player killer) {
        MobDropConfig mobConfig = entityDrops.get(entityType);
        if (mobConfig == null || !mobConfig.isEnabled()) return null;

        for (DropEntryConfig entry : mobConfig.getEntries()) {
            if (entry.getPermission() == null) {
                return entry; // No permission required — matches everyone
            }
            if (killer != null && killer.hasPermission(entry.getPermission())) {
                return entry;
            }
        }
        return null;
    }

    /**
     * Sets the enabled state for a mob and persists it to custom-drops.yml.
     */
    public boolean setMobEnabled(EntityType entityType, boolean enabled, CommandSender sender) {
        if (!entityDrops.containsKey(entityType)) return false;

        entityDrops.get(entityType).setEnabled(enabled);

        // Persist to file
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        String path = "mobs." + entityType.name().toLowerCase() + ".enabled";
        config.set(path, enabled);
        try {
            config.save(configFile);
        } catch (IOException e) {
            functions.sendMessage(sender, "Failed to save drop config: " + e.getMessage(), true);
            return false;
        }
        return true;
    }

    public Map<EntityType, MobDropConfig> getEntityDrops() {
        return entityDrops;
    }

    // -------------------------------------------------------------------------
    // Inner classes
    // -------------------------------------------------------------------------

    public static class MobDropConfig {
        private boolean enabled;
        private final List<DropEntryConfig> entries;

        public MobDropConfig(boolean enabled, List<DropEntryConfig> entries) {
            this.enabled = enabled;
            this.entries = entries;
        }

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public List<DropEntryConfig> getEntries() { return entries; }
    }

    public static class DropEntryConfig {
        private final String permission;
        private final boolean keepVanillaDrops;
        private final double xpMultiplier;
        private final boolean dropAll;
        private final List<DropItemConfig> items;
        private final int totalWeight;

        public DropEntryConfig(String permission, boolean keepVanillaDrops, double xpMultiplier,
                               boolean dropAll, List<DropItemConfig> items, int totalWeight) {
            this.permission = permission;
            this.keepVanillaDrops = keepVanillaDrops;
            this.xpMultiplier = xpMultiplier;
            this.dropAll = dropAll;
            this.items = items;
            this.totalWeight = totalWeight;
        }

        public String getPermission() { return permission; }
        public boolean isKeepVanillaDrops() { return keepVanillaDrops; }
        public double getXpMultiplier() { return xpMultiplier; }
        public boolean isDropAll() { return dropAll; }
        public List<DropItemConfig> getItems() { return items; }
        public int getTotalWeight() { return totalWeight; }
    }

    public static class DropItemConfig {
        private final String item;
        private final int min;
        private final int max;
        private final int weight;

        public DropItemConfig(String item, int min, int max, int weight) {
            this.item = item;
            this.min = min;
            this.max = max;
            this.weight = weight;
        }

        public String getItem() { return item; }
        public int getMin() { return min; }
        public int getMax() { return max; }
        public int getWeight() { return weight; }
    }
}
