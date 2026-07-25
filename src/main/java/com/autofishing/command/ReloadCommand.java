package com.autofishing.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import com.autofishing.AutoFishingPlugin;

public class ReloadCommand extends Command {

    private final AutoFishingPlugin plugin;

    public ReloadCommand(AutoFishingPlugin plugin) {
        super("reautofish", "Reload AutoFishing configuration", "/reautofish");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        try {
            if (!sender.isOp() && !(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage("§c[AutoFishing] No permission.");
                return true;
            }

            plugin.reloadPluginConfig();
            sender.sendMessage("§a[AutoFishing] Configuration reloaded.");
            return true;
        } catch (Exception e) {
            sender.sendMessage("§c[AutoFishing] Error: " + e.getMessage());
            plugin.getLogger().error("ReloadCommand error", e);
            return true;
        }
    }
}
