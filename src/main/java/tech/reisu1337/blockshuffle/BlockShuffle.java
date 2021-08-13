package tech.reisu1337.blockshuffle;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Steerable;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;
import tech.reisu1337.blockshuffle.commands.GameStartCommand;
import tech.reisu1337.blockshuffle.events.PlayerListener;

import java.io.File;

public final class BlockShuffle extends JavaPlugin {
    private File settingsFile;
    private String startMessage;
    private String startError;
    private boolean inProgress;

    @Override
    public void onEnable() {
        this.settingsFile = this.getDataFolder().toPath().resolve("settings.yml").toFile();
        this.createSettingsFile();

        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        YamlConfiguration settings = YamlConfiguration.loadConfiguration(this.settingsFile);
        this.startMessage = settings.getString("start");
        this.startError = settings.getString("starterror");
        this.getCommand("startblockshuffle").setExecutor(new GameStartCommand(this.startMessage, this, this.startError));

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

}
