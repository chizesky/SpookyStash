package ru.enwulf.spookystash.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import ru.enwulf.spookystash.utils.ItemUtil;

public class CraftingListener implements Listener {
   @EventHandler
   public void onPrepareItemCraft(PrepareItemCraftEvent event) {
      CraftingInventory inventory = event.getInventory();
      ItemStack[] items = inventory.getMatrix();
      ItemStack[] var4 = items;
      int var5 = items.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ItemStack item = var4[var6];
         if (ItemUtil.isSplinterItem(item) || ItemUtil.isSilverItem(item)) {
            event.getInventory().setResult((ItemStack)null);
            return;
         }
      }

   }
}
