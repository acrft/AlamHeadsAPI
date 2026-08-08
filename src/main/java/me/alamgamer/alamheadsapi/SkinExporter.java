package me.alamgamer.alamheadsapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.property.SkinProperty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileWriter;
import java.util.Optional;

public class SkinExporter {

    public static void export() {
        if (!AlamHeadsAPI.getInstance().getConfig().getBoolean("cache.save-json", true))
            return;
        try {

            SkinsRestorer api = SkinsRestorerProvider.get();

            JsonObject root = new JsonObject();


            for (Player player : Bukkit.getOnlinePlayers()) {

                Optional<SkinProperty> skin =
                        api.getPlayerStorage().getSkinForPlayer(
                                player.getUniqueId(),
                                player.getName()
                        );

                if (skin.isEmpty())
                    continue;

                JsonObject playerData = new JsonObject();

                playerData.addProperty("uuid", player.getUniqueId().toString());
                playerData.addProperty("value", skin.get().getValue());
                playerData.addProperty("signature", skin.get().getSignature());

                root.add(player.getName(), playerData);

            }

            File folder = AlamHeadsAPI.getInstance().getDataFolder();
            if (!folder.exists() && !folder.mkdirs()) {
                AlamHeadsAPI.getInstance().getLogger().warning("Couldn't create plugin folder.");
            }

            File file = new File(folder, "skins.json");

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(gson.toJson(root));
            }

        } catch (Exception e) {
            AlamHeadsAPI.getInstance().getLogger().severe(e.getMessage());
        }

    }

}