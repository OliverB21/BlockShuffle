package tech.reisu1337.blockshuffle.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GameStartCommand implements CommandExecutor {
    private final String startMessage;

    public GameStartCommand(String startMessage) {
        this.startMessage = startMessage;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("startblockshuffle")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4" + this.startMessage));
        }
        return true;
    }
}
