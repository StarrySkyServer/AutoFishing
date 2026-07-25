package com.autofishing;

import cn.nukkit.plugin.PluginBase;
import com.autofishing.command.AutoFishingCommand;
import com.autofishing.command.ReloadCommand;
import com.autofishing.config.AutoFishingConfig;
import com.autofishing.data.PlayerDataManager;
import com.autofishing.listener.FishingListener;

public class AutoFishingPlugin extends PluginBase {

    private static AutoFishingPlugin instance;
    private AutoFishingConfig config;
    private PlayerDataManager playerDataManager;

    public static AutoFishingPlugin getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        config = AutoFishingConfig.load(this);

        playerDataManager = new PlayerDataManager(this);
        playerDataManager.loadAll();

        getServer().getPluginManager().registerEvents(new FishingListener(this), this);
        getServer().getCommandMap().register(this.getName(), new AutoFishingCommand(this));
        getServer().getCommandMap().register(this.getName(), new ReloadCommand(this));

        getLogger().info("AutoFishing enabled");
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.saveAll();
        }
        getLogger().info("AutoFishing disabled");
    }

    public AutoFishingConfig getPluginConfig() {
        return config;
    }

    public void reloadPluginConfig() {
        config = AutoFishingConfig.load(this);
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}
