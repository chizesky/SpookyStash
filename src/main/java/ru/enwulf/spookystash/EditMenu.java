package ru.enwulf.spookystash;

import java.io.IOException;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ru.enwulf.spookystash.utils.ColorUtils;
import ru.enwulf.spookystash.utils.InventoryUtil;
import ru.enwulf.spookystash.utils.file.BasicConfiguration;

public class EditMenu implements InventoryHolder {
    private Inventory inventory;
    private String edit;
    private String content;

    public EditMenu(String edit) {
        this.edit = edit;
        String title = ColorUtils.colorize("[Stash]: " + edit);
        this.inventory = Bukkit.createInventory(this, 54, Component.text(title));
    }

    public void create() {
        BasicConfiguration saves = SpookyStash.get().getCache();
        this.content = saves.getConfiguration().getString("stashes." + this.edit + ".contents");
        if (this.content != null && this.content.length() >= 20) {
            ItemStack[] itemStacks;
            try {
                itemStacks = InventoryUtil.itemStackArrayFromBase64(this.content);
            } catch (IOException var4) {
                return;
            }

            if (itemStacks.length != 0) {
                for(int i = 0; i < itemStacks.length; i++) {
                    this.inventory.setItem(i, itemStacks[i]);
                }
            }
        }
    }

    public void open(Player player) {
        player.openInventory(this.inventory);
    }

    public void save(Inventory inventory) {
        YamlConfiguration configuration = SpookyStash.get().getCache().getConfiguration();
        configuration.set("stashes." + this.edit + ".contents", InventoryUtil.itemStackArrayToBase64(inventory.getContents()));
        SpookyStash.get().getCache().save();
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public String getEdit() {
        return this.edit;
    }

    public String getContent() {
        return this.content;
    }
}