package tech.reisu1337.blockshuffle.events;

//import org.apache.commons.lang.WordUtils;
//import org.bukkit.Bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import tech.reisu1337.blockshuffle.BlockShuffle;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerListener implements Listener {
    private final Map<UUID, Material> userMaterialMap = new HashMap<>();
    private final Random random = new Random();
    private final List<Material> materials;
    private final BlockShuffle plugin;

    public PlayerListener(YamlConfiguration settings, BlockShuffle plugin) {
        this.materials = settings.getStringList("materials").stream().map(Material::getMaterial).collect(Collectors.toList());
        this.plugin = plugin;
    }

    public void startGame() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Material randomBlock = this.getRandomMaterial();
            this.userMaterialMap.put(player.getUniqueId(), randomBlock);
            player.sendMessage(player.getName() + ", you need to stand on " + randomBlock.toString());
        }
    }

    private Material getRandomMaterial() {
        int randomIndex = this.random.nextInt(this.materials.size() - 1);
        return this.materials.get(randomIndex);
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Material playerOnBlock = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getBlockData().getMaterial();
        if (this.userMaterialMap.get(event.getPlayer().getUniqueId()) == playerOnBlock) {
            player.sendMessage(player.getName() + ", you stood on " + playerOnBlock.toString());
            this.userMaterialMap.remove(event.getPlayer().getUniqueId());
            if (this.userMaterialMap.size() == 0) {
                this.plugin.setInProgress(false);
            }
        }
        //playerOnBlock = playerOnBlock.replaceAll("_", " ");
        //playerOnBlock = WordUtils.swapCase(playerOnBlock).toLowerCase(Locale.ROOT);
        //playerOnBlock = WordUtils.capitalize(playerOnBlock);
        //Bukkit.broadcastMessage(event.getPlayer().getName() + " is stood on " + playerOnBlock);
    }

    public Map<UUID, Material> getUserMaterialMap() {
        return this.userMaterialMap;
    }
}
