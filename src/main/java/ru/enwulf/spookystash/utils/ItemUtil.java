package ru.enwulf.spookystash.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.enwulf.spookystash.SpookyStash;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemUtil {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([0-9a-fA-F]{6})");
    private static final Pattern AMPERSAND_HEX_PATTERN = Pattern.compile("&x(&[0-9a-fA-F]){6}");

    public static ItemStack splinterItem() {
        ItemStack itemStack = new ItemStack(Material.FLINT);
        ItemMeta itemMeta = itemStack.getItemMeta();

        String name = SpookyStash.get().getMainConfig().getConfiguration().getString("splinter_name");
        List<String> loreConfig = SpookyStash.get().getMainConfig().getConfiguration().getStringList("splinter_lore");

        itemMeta.displayName(parseComponent(name));

        if (loreConfig != null && !loreConfig.isEmpty() && !isEmptyLore(loreConfig)) {
            List<Component> loreComponents = new ArrayList<>();
            for (String line : loreConfig) {
                loreComponents.add(parseComponent(line));
            }
            itemMeta.lore(loreComponents);
        }

        itemMeta.addEnchant(Enchantment.LUCK, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack silverItem() {
        ItemStack itemStack = new ItemStack(Material.IRON_NUGGET);
        ItemMeta itemMeta = itemStack.getItemMeta();

        String name = SpookyStash.get().getMainConfig().getConfiguration().getString("silver_name");
        List<String> loreConfig = SpookyStash.get().getMainConfig().getConfiguration().getStringList("silver_lore");

        itemMeta.displayName(parseComponent(name));

        if (loreConfig != null && !loreConfig.isEmpty() && !isEmptyLore(loreConfig)) {
            List<Component> loreComponents = new ArrayList<>();
            for (String line : loreConfig) {
                loreComponents.add(parseComponent(line));
            }
            itemMeta.lore(loreComponents);
        }

        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack soulItem() {
        ItemStack itemStack = new ItemStack(Material.SOUL_LANTERN);
        ItemMeta itemMeta = itemStack.getItemMeta();

        String name = SpookyStash.get().getMainConfig().getConfiguration().getString("soul_name");
        List<String> loreConfig = SpookyStash.get().getMainConfig().getConfiguration().getStringList("soul_lore");

        itemMeta.displayName(parseComponent(name));

        if (loreConfig != null && !loreConfig.isEmpty() && !isEmptyLore(loreConfig)) {
            List<Component> loreComponents = new ArrayList<>();
            for (String line : loreConfig) {
                loreComponents.add(parseComponent(line));
            }
            itemMeta.lore(loreComponents);
        }

        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addEnchant(Enchantment.LUCK, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private static boolean isEmptyLore(List<String> lore) {
        if (lore == null || lore.isEmpty()) {
            return true;
        }
        if (lore.size() == 1 && lore.get(0).trim().equals("{}")) {
            return true;
        }
        return false;
    }

    private static Component parseComponent(String text) {
        if (text == null || text.isEmpty()) {
            return Component.text("");
        }

        text = applyAmpersandHexColors(text);
        text = applyHexColors(text);
        text = ChatColor.translateAlternateColorCodes('&', text);

        Component component = LegacyComponentSerializer.legacySection().deserialize(text);
        return component.decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false);
    }

    private static String applyAmpersandHexColors(String text) {
        Matcher matcher = AMPERSAND_HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String matched = matcher.group();
            String hex = matched.replace("&x", "").replace("&", "");

            StringBuilder hexColor = new StringBuilder("#");
            for (int i = 0; i < hex.length(); i++) {
                hexColor.append(hex.charAt(i));
            }

            String replacement = net.md_5.bungee.api.ChatColor.of(hexColor.toString()).toString();
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }

    private static String applyHexColors(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hex = matcher.group(1);
            String replacement = net.md_5.bungee.api.ChatColor.of("#" + hex).toString();
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }

    private static String componentToPlainText(Component component) {
        if (component == null) return "";
        String text = LegacyComponentSerializer.legacySection().serialize(component);
        return ChatColor.stripColor(text);
    }

    public static boolean isSplinterItem(ItemStack item) {
        if (item != null && item.getType() == Material.FLINT && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName() && meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
                String expectedName = SpookyStash.get().getMainConfig().getConfiguration().getString("splinter_name");
                Component itemName = meta.displayName();
                Component expectedComponent = parseComponent(expectedName);

                String itemPlain = componentToPlainText(itemName);
                String expectedPlain = componentToPlainText(expectedComponent);

                return itemPlain.equals(expectedPlain);
            }
        }
        return false;
    }

    public static boolean isSilverItem(ItemStack item) {
        if (item != null && item.getType() == Material.IRON_NUGGET && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName() && meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
                String expectedName = SpookyStash.get().getMainConfig().getConfiguration().getString("silver_name");
                Component itemName = meta.displayName();
                Component expectedComponent = parseComponent(expectedName);

                String itemPlain = componentToPlainText(itemName);
                String expectedPlain = componentToPlainText(expectedComponent);

                return itemPlain.equals(expectedPlain);
            }
        }
        return false;
    }

    public static boolean isSoulItem(ItemStack item) {
        if (item != null && item.getType() == Material.SOUL_LANTERN && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName() && meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
                String expectedName = SpookyStash.get().getMainConfig().getConfiguration().getString("soul_name");
                Component itemName = meta.displayName();
                Component expectedComponent = parseComponent(expectedName);

                String itemPlain = componentToPlainText(itemName);
                String expectedPlain = componentToPlainText(expectedComponent);

                return itemPlain.equals(expectedPlain);
            }
        }
        return false;
    }
}