package ru.enwulf.spookystash.listener;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ru.enwulf.spookystash.EditMenu;
import ru.enwulf.spookystash.SpookyStash;
import ru.enwulf.spookystash.stash.Stash;

public class StashListener implements Listener {
   @EventHandler
   public void onInventoryClose(InventoryCloseEvent event) {
      HumanEntity var3 = event.getPlayer();
      if (var3 instanceof Player) {
         Player player = (Player)var3;
         if (player.hasPermission("spookystash.admin")) {
            InventoryHolder var4 = event.getInventory().getHolder();
            if (var4 instanceof EditMenu) {
               EditMenu e = (EditMenu)var4;
               e.save(e.getInventory());
               Stash.getByName(e.getEdit()).setContent(e.getInventory());
            }
         }

      }
   }

   @EventHandler
   public void onBlockPlace(BlockPlaceEvent event) {
      ItemStack placedItem = event.getItemInHand();
      if (SpookyStash.get().isKeyInStashes(placedItem)) {
         event.setCancelled(true);
      }

   }
}
