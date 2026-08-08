package me.alamgamer.alamheadsapi;
import org.jetbrains.annotations.NotNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.io.File;

public class AlamHeadsCommand implements CommandExecutor {

    private final AlamHeadsAPI plugin;

    public AlamHeadsCommand(AlamHeadsAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {

        if (!sender.hasPermission("alamheads.admin")) {
            sender.sendMessage("§cYou don't have permission.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§2====== AlamHeadsAPI ======");
            sender.sendMessage("§a/alamheads reload");
            sender.sendMessage("§a/alamheads status");
            sender.sendMessage("§a/alamheads export");
            sender.sendMessage("§a/alamheads version");
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "reload":

                plugin.reloadConfig();

                if (plugin.getAvatarServer() != null) {
                    plugin.getAvatarServer().stop();
                }

                try {
                    plugin.setAvatarServer(new AvatarServer(plugin));
                    plugin.getAvatarServer().start();
                } catch (Exception e) {
                    plugin.getLogger().log(java.util.logging.Level.SEVERE, "Unexpected error", e);
                    sender.sendMessage("§cFailed to restart API.");
                    return true;
                }

                sender.sendMessage("§aConfiguration reloaded.");
                break;

            case "export":

                SkinExporter.export();
                sender.sendMessage("§aSkins exported successfully.");
                break;

            case "version":

                sender.sendMessage("§2====== Version ======");
                sender.sendMessage("§aPlugin: §f" + plugin.getPluginMeta().getVersion());
                sender.sendMessage("§aJava: §f" + System.getProperty("java.version"));
                sender.sendMessage("§aServer: §f" + plugin.getServer().getVersion());
                break;

            case "status":

                File file = new File(plugin.getDataFolder(), "skins.json");

                sender.sendMessage("§2====== AlamHeadsAPI Status ======");
                sender.sendMessage("§aPlugin: §fRunning");
                sender.sendMessage("§aHTTP Server: §fRunning");
                sender.sendMessage("§aPort: §f" + plugin.getConfig().getInt("server.port", 8080));
                sender.sendMessage("§aPlayers Cached: §f" + plugin.getServer().getOnlinePlayers().size());
                sender.sendMessage("§askins.json: §f" + (file.exists() ? "Found" : "Missing"));
                sender.sendMessage("§aSize: §f" + (file.exists() ? file.length() + " bytes" : "0 bytes"));
                sender.sendMessage("§aPublic API: §f" + plugin.getConfig().getBoolean("api.public", true));
                sender.sendMessage("§aVersion: §f" + plugin.getPluginMeta().getVersion());
                break;

            default:

                sender.sendMessage("§cUnknown subcommand.");
                sender.sendMessage("§7Use §e/alamheads");
                break;
        }

        return true;
    }
}