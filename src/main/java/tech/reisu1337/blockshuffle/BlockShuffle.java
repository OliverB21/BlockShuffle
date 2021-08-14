package tech.reisu1337.blockshuffle;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import tech.reisu1337.blockshuffle.commands.GameStartCommand;
import tech.reisu1337.blockshuffle.events.PlayerListener;

import java.io.File;
import java.util.Objects;

public final class BlockShuffle extends JavaPlugin {
    private File settingsFile;
    private boolean inProgress;

    @Override
    public void onEnable() {
        this.settingsFile = this.getDataFolder().toPath().resolve("settings.yml").toFile();
        this.createSettingsFile();

        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        YamlConfiguration settings = YamlConfiguration.loadConfiguration(this.settingsFile);
        String startMessage = settings.getString("start");
        String startError = settings.getString("starterror");
        Objects.requireNonNull(this.getCommand("startblockshuffle")).setExecutor(new GameStartCommand(startMessage, this, startError));

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
