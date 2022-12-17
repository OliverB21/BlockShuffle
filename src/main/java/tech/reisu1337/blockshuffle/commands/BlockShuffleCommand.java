package tech.reisu1337.blockshuffle.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import tech.reisu1337.blockshuffle.BlockShuffle;
import tech.reisu1337.blockshuffle.events.PlayerListener;
import tech.reisu1337.blockshuffle.menus.BlockShuffleMenu;

public class BlockShuffleCommand implements CommandExecutor {
    private final String stopGame;
    private final String stopError;

    private final PlayerListener playerListener;
    private final BlockShuffleMenu blockShuffleMenu;
    private final BlockShuffle plugin;

    public BlockShuffleCommand(PlayerListener playerListener, BlockShuffleMenu blockShuffleMenu, BlockShuffle plugin, YamlConfiguration settings) {
        this.stopGame = settings.getString("stopgame");
        this.stopError = settings.getString("stoperror");

        this.playerListener = playerListener;
        this.blockShuffleMenu = blockShuffleMenu;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("blockshuffle")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("you cannot execute this command from console.");
                return true;
            }
            if (!player.hasPermission("blockshuffle.admin")) {
                player.sendMessage(ChatColor.RED + "you do not have permission to execute this command.");
                return true;
            }
            if (args.length == 0) {
                this.blockShuffleMenu.show(player);
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (!this.plugin.isInProgress()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6<BlockShuffle> " + "&4" + this.stopError));
                } else {
                    this.playerListener.resetGame();
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6<BlockShuffle> " + "&2" + this.stopGame));
                }
            } else {
                sender.sendMessage("To start the game, try /blockshuffle");
            }
        }
        return true;
    }
}
