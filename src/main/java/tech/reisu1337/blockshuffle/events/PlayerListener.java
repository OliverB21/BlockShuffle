package tech.reisu1337.blockshuffle.events;

//import org.apache.commons.lang.WordUtils;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        plugin.setRoundWon(false);
        for (Player player : Bukkit.getOnlinePlayers()) {
            Material randomBlock = this.getRandomMaterial();
            String playerOnBlock2 = randomBlock.toString().replaceAll("_", " ");
            playerOnBlock2 = WordUtils.swapCase(playerOnBlock2).toLowerCase(Locale.ROOT);
            playerOnBlock2 = WordUtils.capitalize(playerOnBlock2);
            this.userMaterialMap.put(player.getUniqueId(), randomBlock);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6<BlockShuffle> " + "&4" + player.getName() + "&f, you need to stand on &d" + playerOnBlock2));
        }
    }

    private Material getRandomMaterial() {
        int randomIndex = this.random.nextInt(this.materials.size() - 1);
        return this.materials.get(randomIndex);
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Material playerOnBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getBlockData().getMaterial();
        if (this.userMaterialMap.get(player.getUniqueId()) == playerOnBlock) {
            if (!this.plugin.isRoundWon()) {
                plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6<BlockShuffle> " + "&2" + player.getName() + "&f has won!"));
                plugin.setRoundWon(true);
            }
            this.userMaterialMap.remove(event.getPlayer().getUniqueId());
            if (this.userMaterialMap.size() == 0) {
                this.plugin.setInProgress(false);
            }
        }
        //playerOnBlock2 = playerOnBlock.replaceAll("_", " ");
        //playerOnBlock2 = WordUtils.swapCase(playerOnBlock2).toLowerCase(Locale.ROOT);
        //playerOnBlock2 = WordUtils.capitalize(playerOnBlock2);
        //Bukkit.broadcastMessage(event.getPlayer().getName() + " is stood on " + playerOnBlock);
    }

    public Map<UUID, Material> getUserMaterialMap() {
        return this.userMaterialMap;
    }
}
