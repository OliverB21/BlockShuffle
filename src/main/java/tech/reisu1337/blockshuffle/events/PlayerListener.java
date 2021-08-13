package tech.reisu1337.blockshuffle.events;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerListener implements Listener {
    private String playerOnBlock;

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        playerOnBlock = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getBlockData().getAsString();
        //Bukkit.broadcastMessage(playerOnBlock);
    }
}
