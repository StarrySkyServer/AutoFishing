package com.autofishing.data;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerDataManager {

    private final File dataFile;
    private final Config config;
    private final Map<String, PlayerData> cache = new HashMap<>();

    public PlayerDataManager(Plugin plugin) {
        plugin.getDataFolder().mkdirs();
        this.dataFile = new File(plugin.getDataFolder(), "players.yml");
        this.config = new Config(dataFile, Config.YAML);
    }

    public void loadAll() {
        cache.clear();
        Map<String, Object> root = config.getAll();
        for (Map.Entry<String, Object> entry : root.entrySet()) {
            if (entry.getValue() instanceof Map<?, ?> map) {
                PlayerData data = new PlayerData();
                data.autoEnabled = Boolean.TRUE.equals(map.get("autoEnabled"));
                data.sneakOnly = Boolean.TRUE.equals(map.get("sneakOnly"));
                cache.put(entry.getKey(), data);
            }
        }
    }

    public void saveAll() {
        LinkedHashMap<String, Object> root = new LinkedHashMap<>();
        for (Map.Entry<String, PlayerData> entry : cache.entrySet()) {
            LinkedHashMap<String, Object> playerMap = new LinkedHashMap<>();
            playerMap.put("autoEnabled", entry.getValue().autoEnabled);
            playerMap.put("sneakOnly", entry.getValue().sneakOnly);
            root.put(entry.getKey(), playerMap);
        }
        config.setAll(root);
        config.save(dataFile);
    }

    public PlayerData get(String uuid) {
        return cache.computeIfAbsent(uuid, k -> new PlayerData());
    }

    public void set(String uuid, PlayerData data) {
        cache.put(uuid, data);
    }

    public static class PlayerData {
        public boolean autoEnabled = false;
        public boolean sneakOnly = false;
    }
}
