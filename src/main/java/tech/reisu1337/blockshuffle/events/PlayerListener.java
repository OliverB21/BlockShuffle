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
    private int bossBarTask;
    private int roundEndTask;
    private BossBar bossBar;
    private long roundStartTime;
    private String materialPath;

    public PlayerListener(YamlConfiguration settings, BlockShuffle plugin) {
        this.materials = settings.getStringList(materialPath).stream().map(Material::getMaterial).collect(Collectors.toList());
        this.plugin = plugin;
    }

    public void startGame() {
        plugin.setRoundWon(false);
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.usersInGame.add(player.getUniqueId());
        }
        this.bossBar = this.createBossBar();
        this.nextRound();
        this.bossBarTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, this::updateBossBar, 0, 20);
    }

    public void resetGame() {
        this.ticksInRound = 6000;
        this.userMaterialMap.clear();
        this.usersInGame.clear();
        this.completedUsers.clear();
        this.plugin.setInProgress(false);
        this.bossBar.removeAll();
        Bukkit.getScheduler().cancelTask(this.roundEndTask);
        Bukkit.getScheduler().cancelTask(this.bossBarTask);
    }

    private void nextRound() {
        if (this.ticksInRound != 6000) {
            if (this.completedUsers.size() <= 1) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6<BlockShuffle> &f" + this.createWinnerMessage()));
                this.resetGame();
                return;
            } else {
                for (UUID uuid : this.usersInGame) {
                    if (!this.completedUsers.contains(uuid)) {
                        usersInGame.remove(uuid);
                        userMaterialMap.remove(uuid);
                        Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&', "&6<BlockShuffle> " + "&4" + Bukkit.getPlayer(uuid).getName() + ",&f you have been knocked out!"));
                    }
                }
            }
            this.completedUsers.clear();
        }
        this.bossBar.setVisible(true);
        this.roundStartTime = System.currentTimeMillis();
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
        this.roundEndTask = Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, this::nextRound, this.ticksInRound);
        this.ticksInRound -= 600;
    }

    private String createWinnerMessage() {
        //The game is over! The winner(s) are A, B, C etc
        StringJoiner stringJoiner = new StringJoiner(", ", "The game is over, the winners are: ", "!");
        if (this.completedUsers.size() > 0) {
            for (UUID uuid : this.completedUsers) {
                stringJoiner.add(Bukkit.getPlayer(uuid).getName());
            }
        } else {
            for (UUID uuid : this.usersInGame) {
                stringJoiner.add(Bukkit.getPlayer(uuid).getName());
            }
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

    private void updateBossBar() {
        long timeSinceRoundStart = System.currentTimeMillis() - this.roundStartTime;
        long millisInRound = ((this.ticksInRound + 600) / 20) * 1000;
        long millisRemaining = millisInRound - timeSinceRoundStart;

        double progress = millisRemaining / (double) millisInRound;

        this.bossBar.setProgress(progress);
        this.bossBar.setTitle("Time Remaining in BlockShuffle Round: " + millisRemaining / 1000 + "secs");
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
                Bukkit.getScheduler().cancelTask(this.roundEndTask);
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
                this.resetGame();
            }
        }
    }

    public void setMaterialPath(String materialPath) {
        this.materialPath = materialPath;
    }
}