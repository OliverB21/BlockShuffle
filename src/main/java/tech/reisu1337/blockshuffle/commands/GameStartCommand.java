package tech.reisu1337.blockshuffle.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.graalvm.compiler.nodes.cfg.Block;
import tech.reisu1337.blockshuffle.BlockShuffle;

public class GameStartCommand implements CommandExecutor {
    private final String startMessage;
    private final BlockShuffle plugin;
    private final String startError;

    public GameStartCommand(String startMessage, BlockShuffle plugin, String startError) {
        this.startMessage = startMessage;
        this.plugin = plugin;
        this.startError = startError;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("startblockshuffle")) {
            if (this.plugin.isInProgress()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4" + this.startError));
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2" + this.startMessage));
                this.plugin.setInProgress(true);
            }
        }
        return true;
    }
}
