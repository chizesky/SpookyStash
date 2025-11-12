package ru.enwulf.spookystash.stash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.enwulf.spookystash.SpookyStash;
import ru.enwulf.spookystash.utils.ColorUtils;
import ru.enwulf.spookystash.utils.file.BasicConfiguration;

public class StashKey {
    private final Stash stash;
    private Material material;
    private String displayName;
    private List<String> lore;
    private ItemMeta itemMeta;

    public StashKey(Stash stash) {
        this.stash = stash;
        this.material = Material.TRIPWIRE_HOOK;
    }

    public void give(Player player, int count) {
        ItemStack item = this.getItem();
        item.setAmount(count);
        Inventory inventory = player.getInventory();
        HashMap<Integer, ItemStack> remainingItems = inventory.addItem(item);
        if (!remainingItems.isEmpty()) {
            player.getWorld().dropItem(player.getLocation(), remainingItems.get(0));
        }
    }

    public boolean isKey(ItemStack itemStack) {
        return this.getItem().isSimilar(itemStack);
    }

    public boolean isStashKey(ItemStack itemStack) {
        if (itemStack != null && itemStack.getType() == this.material) {
            ItemMeta stackMeta = itemStack.getItemMeta();
            if (stackMeta == null) {
                return false;
            } else {
                return stackMeta.hasLore() &&
                        stackMeta.hasDisplayName() &&
                        stackMeta.getLore().get(1).contains("хранилище") &&
                        stackMeta.hasEnchants() &&
                        stackMeta.getEnchants().equals(this.itemMeta.getEnchants()) &&
                        stackMeta.getItemFlags().equals(this.itemMeta.getItemFlags());
            }
        } else {
            return false;
        }
    }

    public ItemStack getItem() {
        ItemStack itemStack = new ItemStack(this.material);
        this.itemMeta = itemStack.getItemMeta();

        if (this.itemMeta != null) {
            BasicConfiguration config = SpookyStash.get().getMainConfig();
            String formatKey = config.getString("format_key");

            this.displayName = config.getString("stashes." + this.stash.getName() + ".key_name")
                    .replace("<format>", formatKey)
                    .replace("<displayName>", this.stash.getDisplayName());

            List<String> lines = new ArrayList<>();
            config.getStringList("stashes." + this.stash.getName() + ".lore").forEach(s -> {
                lines.add(s.replace("<displayName>", this.stash.getDisplayName()));
            });

            this.itemMeta.setDisplayName(this.displayName);
            this.itemMeta.setLore(lines);
            this.itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            this.itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            this.itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            this.itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
            itemStack.setItemMeta(this.itemMeta);
        }

        return itemStack;
    }

    public void save() {
        YamlConfiguration configurationMain = SpookyStash.get().getMainConfig().getConfiguration();
        configurationMain.set("format_key", "§b[★] §3Отмычка ");
        configurationMain.set("silver_name", "<gradient:#00FFFF:#0084F4>Серебро</gradient>");
        configurationMain.set("stashes." + this.getStash().getName() + ".key_name", "<format> к <displayName>");
        configurationMain.set("stashes." + this.getStash().getName() + ".lore", List.of(
                "§9Этой отмычкой можно",
                "§9Открыть хранилище",
                "§9<displayName>"
        ));
        SpookyStash.get().getMainConfig().save();
        SpookyStash.get().getMainConfig().reload();
    }

    public Stash getStash() {
        return this.stash;
    }

    public Material getMaterial() {
        return this.material;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public ItemMeta getItemMeta() {
        return this.itemMeta;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public void setItemMeta(ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
    }
}