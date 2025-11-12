package ru.enwulf.spookystash.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {
    private static final Pattern SIMPLE_HEX_PATTERN = Pattern.compile("&#([0-9a-fA-F]{6})");

    public static String colorize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        Matcher matcher = SIMPLE_HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hex = matcher.group(1);
            String replacement = ChatColor.of("#" + hex).toString();
            matcher.appendReplacement(buffer, replacement);
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    public static Component color(String text) {
        if (text == null || text.isEmpty()) {
            return Component.text("");
        }
        String colored = colorize(text);
        return LegacyComponentSerializer.legacyAmpersand().deserialize(colored);
    }
}