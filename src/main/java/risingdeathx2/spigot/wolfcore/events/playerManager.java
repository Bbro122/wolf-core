package risingdeathx2.spigot.wolfcore.events;

import java.net.http.WebSocket.Listener;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import dev.dejvokep.boostedyaml.YamlDocument;
import risingdeathx2.spigot.wolfcore.core;
import risingdeathx2.spigot.wolfcore.classes.PlayerData;

public class playerManager implements Listener {
    public Map<UUID, PlayerData> players = new HashMap<>();
    public core core;

    public playerManager(core core) {
        this.core = core;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        YamlDocument data = core.getConfig(player.getUniqueId().toString(), core.getDataFolder().toPath().resolve("userdata"));
        if (data == null) {
            core.getLogger().warning("[Wolf-Core] Player data not found for " + player.getName());
            player.kickPlayer("§4§lError: §cPlayer data not found, please contact an administrator.");
            return;
        }
        data.set("timestamp.login", System.currentTimeMillis());
        players.put(player.getUniqueId(), new PlayerData(player, data));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        YamlDocument data = players.get(player.getUniqueId()).data;
        data.set("timestamp.logout", System.currentTimeMillis());
        try {
            data.save();
        } catch (Exception e) {
            core.getLogger().warning("[Wolf-Core] Failed to save data for " + player.getName());
        }
        if (players.remove(player.getUniqueId()) == null) {
            core.getLogger().warning("[Wolf-Core] Player left without data: " + player.getName());
        }
    }
}
