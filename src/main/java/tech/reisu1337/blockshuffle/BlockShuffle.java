package tech.reisu1337.blockshuffle;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Steerable;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;
import tech.reisu1337.blockshuffle.commands.GameStartCommand;

import java.io.File;

public final class BlockShuffle extends JavaPlugin {
    private File settingsFile;
    private String startMessage;

    @Override
    public void onEnable() {
        this.settingsFile = this.getDataFolder().toPath().resolve("settings.yml").toFile();
        this.createSettingsFile();

        YamlConfiguration settings = YamlConfiguration.loadConfiguration(this.settingsFile);
        this.startMessage = settings.getString("start");
        this.getCommand("startblockshuffle").setExecutor(new GameStartCommand(this.startMessage));

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
}
