package tech.reisu1337.blockshuffle.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import tech.reisu1337.blockshuffle.BlockShuffle;
import tech.reisu1337.blockshuffle.events.PlayerListener;

public class BlockShuffleCommand implements CommandExecutor {
    private final String startMessage;
    private final String startError;
    private final String stopGame;
    private final String stopError;
    private final PlayerListener playerListener;
    private final BlockShuffle plugin;

    public BlockShuffleCommand(PlayerListener playerListener, BlockShuffle plugin, YamlConfiguration settings) {
        this.startMessage = settings.getString("start");
        this.startError = settings.getString("starterror");
        this.stopGame = settings.getString("stopgame");
        this.stopError = settings.getString("stoperror");
        this.playerListener = playerListener;
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("blockshuffle")) {
            if (args.length == 0){
                sender.sendMessage("To start the game, try /blockshuffle start");
                return true;
            }
            if (args[0].equalsIgnoreCase("start")) {
                if (this.plugin.isInProgress()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6<BlockShuffle> " + "&4" + this.startError));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6<BlockShuffle> " + "&2" + this.startMessage));
                    this.playerListener.startGame();
                    this.plugin.setInProgress(true);
                }
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (!this.plugin.isInProgress()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6<BlockShuffle> " + "&4" + this.stopError));
                } else {
                    this.playerListener.resetGame();
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6<BlockShuffle> " + "&2" + this.stopGame));
                }
            } else {
                sender.sendMessage("To start the game, try /blockshuffle start");
            }
        }
        return true;
    }
}
