package me.alamgamer.alamheadsapi;

import org.bukkit.plugin.java.JavaPlugin;

public final class AlamHeadsAPI extends JavaPlugin {
    private static AlamHeadsAPI instance;

    private AvatarServer avatarServer;

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();

        try {
            avatarServer = new AvatarServer(this);
            avatarServer.start();
            var command = getCommand("alamheads");
            if (command != null) {
                command.setExecutor(new AlamHeadsCommand(this));
            }
            SkinExporter.export();

            getServer().getPluginManager().registerEvents(new PlayerListener(), this);

            getServer().getScheduler().runTaskTimer(
                    this,
                    SkinExporter::export,
                    20L,
                    20L * 60
            );

            getLogger().info("Avatar API started");
        } catch (Exception e) {
            getLogger().log(java.util.logging.Level.SEVERE, "Unexpected error", e);
        }
    }

    @Override
    public void onDisable() {
        if (avatarServer != null) {
            avatarServer.stop();
        }
    }

    public static AlamHeadsAPI getInstance() {
        return instance;
    }

    public AvatarServer getAvatarServer() {
        return avatarServer;
    }

    public void setAvatarServer(AvatarServer avatarServer) {
        this.avatarServer = avatarServer;
    }
}