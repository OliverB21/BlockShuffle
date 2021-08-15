package tech.reisu1337.blockshuffle.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.graalvm.compiler.nodes.cfg.Block;
import tech.reisu1337.blockshuffle.BlockShuffle;
import tech.reisu1337.blockshuffle.events.PlayerListener;

public class GameStartCommand implements CommandExecutor {
    private final String startMessage;
    private final BlockShuffle plugin;
    private final String startError;
    private final PlayerListener playerListener;

    public GameStartCommand(PlayerListener playerListener, String startMessage, BlockShuffle plugin, String startError) {
        this.startMessage = startMessage;
        this.plugin = plugin;
        this.startError = startError;
        this.playerListener = playerListener;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("startblockshuffle")) {
            if (this.plugin.isInProgress()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6<BlockShuffle> " + "&4" + this.startError));
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6<BlockShuffle> " + "&2" + this.startMessage));
                this.playerListener.startGame();
                this.plugin.setInProgress(true);
            }
        }
        return true;
    }
}
