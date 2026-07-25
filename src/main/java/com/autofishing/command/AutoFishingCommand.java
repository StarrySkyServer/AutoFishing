package com.autofishing.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.window.FormWindowCustom;
import com.autofishing.AutoFishingPlugin;
import com.autofishing.data.PlayerDataManager;

public class AutoFishingCommand extends Command {

    private final AutoFishingPlugin plugin;

    public AutoFishingCommand(AutoFishingPlugin plugin) {
        super("autofishing", "Open auto fishing settings", "/autofishing");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        try {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cThis command can only be used by players.");
                return true;
            }

            String uuid = player.getUniqueId().toString();
            PlayerDataManager.PlayerData data = plugin.getPlayerDataManager().get(uuid);

            double cost = plugin.getPluginConfig().getCost();
            String costDesc;
            if (cost == 0) {
                costDesc = "free";
            } else if (cost < 0) {
                costDesc = (cost) + "经济";
            } else {
                costDesc = (cost) + "经济";
            }

            FormWindowCustom form = new FormWindowCustom("AutoFishing Settings");
            form.addElement(new ElementToggle("启用自动钓鱼（每次" + costDesc + ")", data.autoEnabled));
            form.addElement(new ElementToggle("仅潜行时启用", data.sneakOnly));

            form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
                if (form.wasClosed()) {
                    return;
                }

                boolean newAuto = form.getResponse().getToggleResponse(0);
                boolean newSneak = form.getResponse().getToggleResponse(1);

                PlayerDataManager.PlayerData newData = new PlayerDataManager.PlayerData();
                newData.autoEnabled = newAuto;
                newData.sneakOnly = newAuto && newSneak;
                plugin.getPlayerDataManager().set(uuid, newData);

                plugin.getServer().getScheduler().scheduleAsyncTask(plugin,
                        new cn.nukkit.scheduler.AsyncTask() {
                            @Override
                            public void onRun() {
                                plugin.getPlayerDataManager().saveAll();
                            }
                        });
                player.sendMessage("§a[AutoFishing] 已保存设置");
            }));

            player.showFormWindow(form);
            return true;
        } catch (Exception e) {
            sender.sendMessage("§c[AutoFishing] Error: " + e.getMessage());
            plugin.getLogger().error("AutoFishingCommand error", e);
            return true;
        }
    }
}
