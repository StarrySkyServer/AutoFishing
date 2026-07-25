package com.autofishing.config;

import cn.nukkit.plugin.Plugin;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Header("################################")
@Header("#   AutoFishing Configuration   #")
@Header("################################")
public class AutoFishingConfig extends OkaeriConfig {

    @Comment({
            "每次自动钓鱼的经济消耗",
            "  0   = 免费",
            "  -1  = 每次获得 1 经济",
            "  >0  = 每次扣除对应数值"
    })
    private double cost = 0;

    @Comment("允许使用自动钓鱼的世界列表")
    private List<String> allowWorlds = Arrays.asList("world", "the_end");

    public double getCost() {
        return cost;
    }

    public List<String> getAllowWorlds() {
        return allowWorlds;
    }

    public static AutoFishingConfig load(Plugin plugin) {
        plugin.getDataFolder().mkdirs();
        return ConfigManager.create(AutoFishingConfig.class, it -> {
            it.configure(opt -> {
                opt.configurer(new YamlSnakeYamlConfigurer());
                opt.bindFile(new File(plugin.getDataFolder(), "config.yml"));
                opt.removeOrphans(true);
            });
            it.saveDefaults();
            it.load(true);
        });
    }
}
