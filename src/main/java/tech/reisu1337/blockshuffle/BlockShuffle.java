package tech.reisu1337.blockshuffle;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import tech.reisu1337.blockshuffle.commands.BlockShuffleCommand;
import tech.reisu1337.blockshuffle.events.PlayerListener;

import java.io.File;
import java.util.logging.Logger;

public final class BlockShuffle extends JavaPlugin {
    private File settingsFile;
    private boolean inProgress;
    private boolean roundWon = false;

    public static Logger LOGGER;

    @Override
    public void onEnable() {
        LOGGER = this.getLogger();
        this.settingsFile = this.getDataFolder().toPath().resolve("settings.yml").toFile();
        this.createSettingsFile();

        YamlConfiguration settings = YamlConfiguration.loadConfiguration(this.settingsFile);

        PlayerListener playerListener = new PlayerListener(settings, this);
        this.getServer().getPluginManager().registerEvents(playerListener, this);

        this.getCommand("blockshuffle").setExecutor(new BlockShuffleCommand(playerListener, this, settings));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void createSettingsFile() {
        if (!this.settingsFile.exists()) {
            this.saveResource("settings.yml", false);
        }
    }

    public boolean isInProgress() {
        return this.inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }


    public boolean isRoundWon() {
        return this.roundWon;
    }

    public void setRoundWon(boolean roundWon) {
        this.roundWon = roundWon;
    }
}

/*
 __ __ __
|YY|OO|BB|
|RR|WW|RR|
|WW|BB|GG|
 ‾‾ ‾‾ ‾‾
*/