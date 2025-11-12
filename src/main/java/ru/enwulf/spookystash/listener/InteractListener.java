package ru.enwulf.spookystash.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import ru.enwulf.spookystash.SpookyStash;
import ru.enwulf.spookystash.stash.Stash;
import ru.enwulf.spookystash.utils.ItemUtil;
import ru.enwulf.spookystash.utils.PlayerUtils;

public class InteractListener implements Listener {
   @EventHandler
   private void on(BlockPlaceEvent event) {
      if (ItemUtil.isSoulItem(event.getItemInHand())) {
         event.setCancelled(true);
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      Block clickedBlock = event.getClickedBlock();
      if (clickedBlock != null && clickedBlock.getType() != Material.AIR) {
         if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && ItemUtil.isSoulItem(event.getItem())) {
            event.setCancelled(true);
         }

         if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (!clickedBlock.getMetadata("SpookyType").isEmpty()) {
               if (clickedBlock.hasMetadata("SpookyType")) {
                  String metaData = ((MetadataValue)clickedBlock.getMetadata("SpookyType").get(0)).asString();
                  if (metaData.contains("SpookyStashData")) {
                     event.setCancelled(true);
                     String stashName = metaData.split("\\.")[1];
                     Stash stash = Stash.getByName(stashName);
                     ItemStack itemInHand = player.getInventory().getItemInMainHand();
                     if (stash.getKey().isKey(itemInHand)) {
                        if (!stash.canOpen()) {
                           PlayerUtils.sendMsg(player, SpookyStash.get().getMainConfig().getString("messages.otherOpening"));
                           return;
                        }

                        stash.open(player);
                        if (itemInHand.getAmount() > 1) {
                           itemInHand.setAmount(itemInHand.getAmount() - 1);
                        } else {
                           player.getInventory().removeItem(new ItemStack[]{stash.getKey().getItem()});
                        }

                        SpookyStash.get().getStashHandler().openings.put(player, stash);
                        PlayerUtils.sendMsg(player, SpookyStash.get().getMainConfig().getString("messages.openingStash"));
                     } else {
                        if (!stash.getKey().isStashKey(itemInHand)) {
                           PlayerUtils.sendMsg(player, SpookyStash.get().getMainConfig().getString("messages.clickToUnlock"));
                           return;
                        }

                        PlayerUtils.sendMsg(player, SpookyStash.get().getMainConfig().getString("messages.lockpickMismatch"));
                     }

                     event.setCancelled(true);
                  }
               }

            }
         }
      }
   }
}
