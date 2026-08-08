package me.alamgamer.alamheadsapi;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.property.SkinProperty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class AvatarServer {

    private final AlamHeadsAPI plugin;
    private HttpServer server;

    public AvatarServer(AlamHeadsAPI plugin) {
        this.plugin = plugin;
    }

    public void start() throws IOException {
        int port = plugin.getConfig().getInt("server.port", 8080);

        server = HttpServer.create(new InetSocketAddress(port), 0);

        String message = plugin.getConfig().getString(
                "messages.api-started",
                "Avatar API running on port %port%"
        );

        plugin.getLogger().info(
                message.replace("%port%", String.valueOf(port))
        );

        server.createContext("/avatar", exchange -> {

            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            String path = exchange.getRequestURI().getPath();

            if (path.equals("/avatar")) {
                send(exchange, 400,
                        "{\"error\":\"Use /avatar/<player>\"}");
                return;
            }

            String playerName = path.substring("/avatar/".length());

            Player player = Bukkit.getPlayerExact(playerName);

            if (player == null) {
                send(exchange, 404,
                        "{\"error\":\"Player not online\"}");
                return;
            }

            try {

                SkinsRestorer skinsRestorer = SkinsRestorerProvider.get();

                Optional<SkinProperty> property =
                        skinsRestorer
                                .getPlayerStorage()
                                .getSkinForPlayer(
                                        player.getUniqueId(),
                                        player.getName()
                                );

                if (property.isEmpty()) {
                    send(exchange,404,
                            "{\"error\":\"Skin not found\"}");
                    return;
                }

                SkinProperty skin = property.get();

                String json =
                        "{"
                                + "\"name\":\"" + player.getName() + "\","
                                + "\"uuid\":\"" + player.getUniqueId() + "\","
                                + "\"value\":\"" + skin.getValue() + "\","
                                + "\"signature\":\"" + skin.getSignature() + "\""
                                + "}";

                send(exchange,200,json);

            } catch (Exception ex) {

                plugin.getLogger().log(java.util.logging.Level.SEVERE, "AvatarServer Error", ex);

                send(exchange,500,
                        "{\"error\":\""+ex.getMessage()+"\"}");
            }

        });
        server.createContext("/skins.json", exchange -> {

            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            java.io.File file = new java.io.File(
                    plugin.getDataFolder(),
                    "skins.json"
            );

            if (!file.exists()) {
                send(exchange,404,"{}");
                return;
            }

            String json = java.nio.file.Files.readString(file.toPath());

            send(exchange,200,json);
        });
        server.start();
    }

    private void send(HttpExchange exchange,int code,String text)
            throws IOException {

        byte[] data=text.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(code,data.length);

        OutputStream os=exchange.getResponseBody();

        os.write(data);

        os.close();
    }
    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
}