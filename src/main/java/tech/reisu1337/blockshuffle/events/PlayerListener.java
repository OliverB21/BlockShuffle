package tech.reisu1337.blockshuffle.events;

import com.google.common.collect.Sets;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tech.reisu1337.blockshuffle.BlockShuffle;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PlayerListener implements Listener {
    private final Map<UUID, Material> userMaterialMap = new ConcurrentHashMap<>();
    private final Set<UUID> completedUsers = Sets.newConcurrentHashSet();
    private final Set<UUID> usersInGame = Sets.newConcurrentHashSet();
    private final Random random = new Random();
    private final List<Material> materials;
    private final BlockShuffle plugin;
    private int ticksInRound = 6000;
    private final int ticksPerSecond = 20;
    private int countdownTask;
    private int scheduledTask;
    private BossBar bossBar;
    private double progress = 1.0;
    private double time;

    public PlayerListener(YamlConfiguration settings, BlockShuffle plugin) {
        this.materials = settings.getStringList("materials").stream().map(Material::getMaterial).collect(Collectors.toList());
        this.plugin = plugin;
    }

    public void startGame() {
        plugin.setRoundWon(false);
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.usersInGame.add(player.getUniqueId());
        }
        this.bossBar = this.createBossBar();
        this.nextRound();
    }

    public void resetGame() {
        this.ticksInRound = 6000;
        this.userMaterialMap.clear();
        this.usersInGame.clear();
        this.plugin.setInProgress(false);
    }

    private void nextRound() {
        if (this.ticksInRound != 6000) {
            if (this.completedUsers.size() <= 1) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6<BlockShuffle> &f" + createWinnerMessage()));
                this.resetGame();
                this.resetBossBar();
                return;
            } else {
                for (UUID uuid : this.usersInGame) {
                    if (!this.completedUsers.contains(uuid)) {
                        usersInGame.remove(uuid);
                        Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&', "&6<BlockShuffle> " + "&4" + Bukkit.getPlayer(uuid).getName() + ",&f you have been knocked out!"));
                    }
                }
            }
            this.completedUsers.clear();
            this.resetBossBar();
        }
        this.bossBar.setVisible(true);
        for (UUID uuid : this.usersInGame) {
            Player player = Bukkit.getPlayer(uuid);
            Material randomBlock = this.getRandomMaterial();
            String playerOnBlock2 = randomBlock.toString().replaceAll("_", " ");
            playerOnBlock2 = WordUtils.swapCase(playerOnBlock2).toLowerCase(Locale.ROOT);
            playerOnBlock2 = WordUtils.capitalize(playerOnBlock2);
            this.userMaterialMap.put(uuid, randomBlock);
            BlockShuffle.LOGGER.log(Level.INFO, player.getName() + " got " + playerOnBlock2);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6<BlockShuffle> " + "&4" + player.getName() + ",&f you have " + this.ticksInRound / 1200 + " mins to stand on &d" + playerOnBlock2));
        }
        this.scheduledTask = Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, this::nextRound, this.ticksInRound);
        this.countdownTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, this::decreaseBossBar, 0, 20);
        this.ticksInRound -= 600;
    }

    private String createWinnerMessage() {
        //The game is over! The winner(s) are A, B, C etc
        StringJoiner stringJoiner = new StringJoiner(", ", "The game is over, the winners are: ", "!");
        for (UUID uuid : this.usersInGame) {
            stringJoiner.add(Bukkit.getPlayer(uuid).getName());
        }
        return stringJoiner.toString();
    }

    private BossBar createBossBar() {
        BossBar bossBar = Bukkit.createBossBar("Time left: ", BarColor.PINK, BarStyle.SOLID);
        for (UUID uuid : this.usersInGame) {
            Player player = Bukkit.getPlayer(uuid);
            bossBar.addPlayer(player);
        }
        return bossBar;
    }

    private void decreaseBossBar() {
        this.time = 1.0 / (this.ticksInRound/this.ticksPerSecond);
        this.bossBar.setProgress(this.progress);
        this.progress = this.progress - this.time;
    }

    private void resetBossBar() {
        this.bossBar.setVisible(false);
        Bukkit.getScheduler().cancelTask(countdownTask);
        this.progress = 1;
        this.bossBar.setProgress(this.progress);
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
                plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6<BlockShuffle> " + "&2" + player.getName() + "&f has found their block!"));
                this.completedUsers.add(player.getUniqueId());
            }
            this.userMaterialMap.remove(event.getPlayer().getUniqueId());
            if (this.userMaterialMap.size() == 0) {
                this.plugin.setInProgress(false);
            }
            if (this.completedUsers.size() == this.usersInGame.size()) {
                Bukkit.getScheduler().cancelTask(this.scheduledTask);
                this.resetBossBar();
                this.nextRound();
            }
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        if (this.usersInGame.contains(playerUUID)) {
            this.usersInGame.remove(playerUUID);
            this.completedUsers.remove(playerUUID);
            if (this.usersInGame.size() == 0) {
                Bukkit.getScheduler().cancelTask(this.scheduledTask);
                Bukkit.getScheduler().cancelTask(this.countdownTask);
                this.resetBossBar();
                this.resetGame();
            }
        }
    }
}