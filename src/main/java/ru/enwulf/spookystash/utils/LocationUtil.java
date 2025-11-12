package ru.enwulf.spookystash.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public final class LocationUtil {
    public static Location getTargetBlockLocation(Player player) {
        Block targetBlock = player.getTargetBlock(7);
        return targetBlock != null && !targetBlock.getType().isAir() ? targetBlock.getLocation().add(0.0D, 1.0D, 0.0D) : null;
    }

    public static String serialize(Location location) {
        if (location == null) {
            return "null";
        } else {
            String var10000 = location.getWorld().getName();
            return var10000 + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch();
        }
    }

    public static Location deserialize(String source) {
        if (source == null) {
            return null;
        } else {
            String[] split = source.split(":");
            World world = Bukkit.getServer().getWorld(split[0]);
            return world == null ? null : new Location(world, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
        }
    }

    private LocationUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
