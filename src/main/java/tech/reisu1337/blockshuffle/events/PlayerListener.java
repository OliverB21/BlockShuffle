package tech.reisu1337.blockshuffle.events;

//import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {
    private final Map<UUID, Material> userMaterialMap = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage("Welcome to the server, " + event.getPlayer().getName());
        userMaterialMap.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation().getBlock().getBlockData().getMaterial());
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        String playerOnBlock = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getBlockData().getAsString();
        //Bukkit.broadcastMessage(playerOnBlock);
    }

    public Map<UUID, Material> getUserMaterialMap() {
        return this.userMaterialMap;
    }
}
