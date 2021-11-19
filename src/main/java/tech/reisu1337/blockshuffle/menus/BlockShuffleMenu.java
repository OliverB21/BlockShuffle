package tech.reisu1337.blockshuffle.menus;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.reisu1337.blockshuffle.BlockShuffle;
import tech.reisu1337.blockshuffle.events.PlayerListener;

public class BlockShuffleMenu implements InventoryHolder, Listener {
    private final Inventory inventory = Bukkit.createInventory(this, InventoryType.HOPPER, "Blockshuffle!");
    private final PlayerListener playerListener;

    private final String startMessage;
    private final String startError;
    private final BlockShuffle plugin;

    public BlockShuffleMenu(PlayerListener playerListener, YamlConfiguration settings, BlockShuffle plugin) {
        this.playerListener = playerListener;

        this.startMessage = settings.getString("start");
        this.startError = settings.getString("starterror");
        this.plugin = plugin;

        ItemStack simpleItem = new ItemStack(Material.STONE);
        ItemMeta simpleItemMeta = simpleItem.getItemMeta();
        simpleItemMeta.setDisplayName("Simple Blockshuffle");
        simpleItem.setItemMeta(simpleItemMeta);

        ItemStack baseItem = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta baseItemMeta = baseItem.getItemMeta();
        baseItemMeta.setDisplayName("Blockshuffle");
        baseItem.setItemMeta(baseItemMeta);

        ItemStack netherItem = new ItemStack(Material.NETHERRACK);
        ItemMeta netherItemMeta = netherItem.getItemMeta();
        netherItemMeta.setDisplayName("Nether Blockshuffle");
        netherItem.setItemMeta(netherItemMeta);

        ItemStack colourItem = new ItemStack(Material.LIME_WOOL);
        ItemMeta colourItemMeta = colourItem.getItemMeta();
        colourItemMeta.setDisplayName("Colour Blockshuffle");
        colourItem.setItemMeta(colourItemMeta);

        ItemStack userItem = new ItemStack(Material.BOOK);
        ItemMeta userItemMeta = userItem.getItemMeta();
        userItemMeta.setDisplayName("Custom Blockshuffle");
        userItem.setItemMeta(userItemMeta);

        this.inventory.addItem(
                baseItem,
                simpleItem,
                netherItem,
                colourItem,
                userItem
        );
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof BlockShuffleMenu))
            return;

        event.setCancelled(true);
        int slot = event.getRawSlot();

        switch (slot) {
            case 0 -> this.playerListener.setMaterialPath("materials");
            case 1 -> this.playerListener.setMaterialPath("simple");
            case 2 -> this.playerListener.setMaterialPath("nether_materials");
            case 3 -> this.playerListener.setMaterialPath("colour_materials");
            case 4 -> this.playerListener.setMaterialPath("user_materials");
        }
        Player player = (Player) event.getWhoClicked();

        if (this.plugin.isInProgress()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6<BlockShuffle> " + "&4" + this.startError));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6<BlockShuffle> " + "&2" + this.startMessage));
            this.playerListener.startGame();
            this.plugin.setInProgress(true);
        }
        player.closeInventory();
    }

    public void show(Player player) {
        player.openInventory(this.inventory);
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}
