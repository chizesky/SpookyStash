package ru.enwulf.spookystash;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class HologramManipulator {
   public static void spawn(Location location, String name, String displayName) {
      Hologram hologram = DHAPI.createHologram(name, location.clone().add(0.5D, 2.0D, 0.5D));
      DHAPI.addHologramLine(hologram, SpookyStash.get().getMainConfig().getString("stash_title"));
      DHAPI.addHologramLine(hologram, displayName);
   }

   public static void edit(String name, String displayName, ItemStack material) {
      Hologram hologram = DHAPI.getHologram(name);
      if (hologram != null) {
         DHAPI.setHologramLine(hologram, 0, displayName);
         DHAPI.setHologramLine(hologram, 1, material);
      }

   }

   public static void restore(String name, String displayName) {
      Hologram hologram = DHAPI.getHologram(name);
      if (hologram != null) {
         DHAPI.setHologramLine(hologram, 0, SpookyStash.get().getMainConfig().getString("stash_title"));
         DHAPI.setHologramLine(hologram, 1, displayName);
      }

   }

   public static void moveY(String name, float amount) {
      Hologram hologram = DHAPI.getHologram(name);
      if (hologram != null) {
         DHAPI.moveHologram(hologram, hologram.getLocation().add(0.0D, (double)amount, 0.0D));
      }

   }

   public static void hide(String name) {
      Hologram hologram = DHAPI.getHologram(name);
      if (hologram != null) {
         DHAPI.setHologramLine(hologram, 0, "");
         DHAPI.setHologramLine(hologram, 1, "");
      }

   }

   public static void delete(String name) {
      Hologram hologram = DHAPI.getHologram(name);
      if (hologram != null) {
         hologram.delete();
      }

   }
}
