package ru.enwulf.spookystash.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class PlayerUtils {
    public static void sendMsg(Player player, String message) {
        player.sendMessage(ColorUtils.color(message));
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(ColorUtils.color(message));
    }
}