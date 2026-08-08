package me.alamgamer.alamheadsapi;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Bukkit.getScheduler().runTaskLater(
                AlamHeadsAPI.getPlugin(AlamHeadsAPI.class),
                SkinExporter::export,
                40L // انتظر ثانيتين
        );

    }
}