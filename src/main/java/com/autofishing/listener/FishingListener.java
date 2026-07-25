package com.autofishing.listener;

import cn.nukkit.Player;
import cn.nukkit.entity.item.EntityFishingHook;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFishingRod;
import com.autofishing.AutoFishingPlugin;
import com.autofishing.config.AutoFishingConfig;
import com.autofishing.data.PlayerDataManager;
import me.onebone.economyapi.EconomyAPI;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FishingListener implements Listener {

    private final AutoFishingPlugin plugin;
    private final Map<UUID, Boolean> autoReeling = new ConcurrentHashMap<>();
    // Cooldown after "经济不足" to avoid spam (2 seconds = 40 ticks)
    private final Map<UUID, Long> noMoneyCooldown = new ConcurrentHashMap<>();

    public FishingListener(AutoFishingPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getScheduler().scheduleRepeatingTask(plugin, this::checkAllFishers, 30);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        autoReeling.remove(uuid);
        noMoneyCooldown.remove(uuid);
    }

    private void checkAllFishers() {
        for (Player player : plugin.getServer().getOnlinePlayers().values()) {
            UUID uuid = player.getUniqueId();

            if (autoReeling.containsKey(uuid)) {
                continue;
            }

            EntityFishingHook hook = player.fishing;
            if (hook == null || hook.closed || !hook.caught) {
                noMoneyCooldown.remove(uuid);
                continue;
            }

            // Skip if on "经济不足" cooldown
            Long cooldownUntil = noMoneyCooldown.get(uuid);
            if (cooldownUntil != null && System.currentTimeMillis() < cooldownUntil) {
                continue;
            }

            // Quick checks on main thread (memory reads, no IO)
            PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().get(uuid.toString());
            if (!data.autoEnabled) {
                continue;
            }
            if (data.sneakOnly && !player.isSneaking()) {
                continue;
            }

            // Mark BEFORE scheduling to prevent duplicate tasks from next ticks
            autoReeling.put(uuid, true);

            // Handle on main thread — economy + reel + recast
            handleCatch(player);
        }
    }

    private void handleCatch(Player player) {
        UUID uuid = player.getUniqueId();

        // Re-validate after potential delay
        EntityFishingHook hook = player.fishing;
        if (hook == null || hook.closed || !hook.caught) {
            autoReeling.remove(uuid);
            return;
        }

        Item handItem = player.getInventory().getItemInHand();
        if (!(handItem instanceof ItemFishingRod rod)) {
            autoReeling.remove(uuid);
            return;
        }

        // World check
        AutoFishingConfig cfg = plugin.getPluginConfig();
        if (!cfg.getAllowWorlds().contains(player.getLevel().getName())) {
            autoReeling.remove(uuid);
            return;
        }

        // Economy — FIRST, before any game state changes
        double cost = cfg.getCost();
        if (cost > 0) {
            EconomyAPI eco = EconomyAPI.getInstance();
            if (eco.myMoney(player) < cost) {
                player.sendMessage("经济不足");
                noMoneyCooldown.put(uuid, System.currentTimeMillis() + 3000);
                autoReeling.remove(uuid);
                return;
            }
            eco.reduceMoney(player, cost);
            noMoneyCooldown.remove(uuid);
        } else if (cost < 0) {
            EconomyAPI.getInstance().addMoney(player, -cost);
        }

        // Step 1: Reel in
        // stopFishing(true) -> reelLine() -> generates loot -> drops item + XP -> closes hook -> player.fishing = null
        player.stopFishing(true);

        // Damage rod
        rod.setDamage(rod.getDamage() + 1);
        if (rod.getDamage() >= rod.getMaxDurability()) {
            player.getInventory().clear(player.getInventory().getHeldItemIndex());
            autoReeling.remove(uuid);
            return;
        }
        player.getInventory().setItemInHand(rod);

        // Step 2: Recast after 10 ticks
        final Item currentRod = player.getInventory().getItemInHand();
        plugin.getServer().getScheduler().scheduleDelayedTask(plugin, () -> {
            autoReeling.remove(uuid);
            if (!player.isOnline() || !player.isAlive()) return;
            if (!(player.getInventory().getItemInHand() instanceof ItemFishingRod)) return;
            player.startFishing(currentRod);
        }, 10);
    }
}
