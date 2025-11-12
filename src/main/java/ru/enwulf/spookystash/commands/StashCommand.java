package ru.enwulf.spookystash.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.enwulf.spookystash.EditMenu;
import ru.enwulf.spookystash.HologramManipulator;
import ru.enwulf.spookystash.SpookyStash;
import ru.enwulf.spookystash.stash.Stash;
import ru.enwulf.spookystash.utils.InventoryUtil;
import ru.enwulf.spookystash.utils.ItemUtil;
import ru.enwulf.spookystash.utils.PlayerUtils;
import ru.enwulf.spookystash.utils.file.BasicConfiguration;

public class StashCommand implements CommandExecutor, TabCompleter {
   public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
      if (args.length < 1) {
         this.sendHelpMessage(commandSender);
         return true;
      } else if (!args[0].equalsIgnoreCase("create") && !args[0].equalsIgnoreCase("delete") && !args[0].equalsIgnoreCase("edit") && !args[0].equalsIgnoreCase("change")) {
         if (!commandSender.hasPermission("spookystash.admin")) {
            return false;
         } else {
            String var8 = args[0].toLowerCase();
            byte var9 = -1;
            switch(var8.hashCode()) {
            case -2131381752:
               if (var8.equals("changefish")) {
                  var9 = 7;
               }
               break;
            case -2130988637:
               if (var8.equals("changesoul")) {
                  var9 = 8;
               }
               break;
            case -1535775394:
               if (var8.equals("givesilver")) {
                  var9 = 2;
               }
               break;
            case -934641255:
               if (var8.equals("reload")) {
                  var9 = 0;
               }
               break;
            case -800545122:
               if (var8.equals("givesplinter")) {
                  var9 = 4;
               }
               break;
            case -643789062:
               if (var8.equals("takesoul")) {
                  var9 = 9;
               }
               break;
            case -211794348:
               if (var8.equals("takesilver")) {
                  var9 = 3;
               }
               break;
            case 41749934:
               if (var8.equals("givekey")) {
                  var9 = 1;
               }
               break;
            case 234920468:
               if (var8.equals("takesplinter")) {
                  var9 = 5;
               }
               break;
            case 1294495876:
               if (var8.equals("givesoul")) {
                  var9 = 10;
               }
               break;
            case 2018000957:
               if (var8.equals("changesplinter")) {
                  var9 = 6;
               }
            }

            switch(var9) {
            case 0:
               this.reloadSpookyStash(commandSender);
               break;
            case 1:
               this.giveKey(commandSender, args);
               break;
            case 2:
               this.giveSilver(commandSender, args);
               break;
            case 3:
               this.takeSilver(commandSender, args);
               break;
            case 4:
               this.giveSplinter(commandSender, args);
               break;
            case 5:
               this.takeSplinter(commandSender, args);
               break;
            case 6:
               this.changeSplinter(commandSender, args);
               break;
            case 7:
               this.changeFish(commandSender, args);
               break;
            case 8:
               this.changeSoul(commandSender, args);
               break;
            case 9:
               this.takeSoul(commandSender, args);
               break;
            case 10:
               this.giveSoul(commandSender, args);
               break;
            default:
               commandSender.sendMessage("§cКоманда не найдена.");
            }

            return true;
         }
      } else if (commandSender instanceof Player) {
         Player player = (Player)commandSender;
         String var6 = args[0].toLowerCase();
         byte var7 = -1;
         switch(var6.hashCode()) {
         case -1361636432:
            if (var6.equals("change")) {
               var7 = 3;
            }
            break;
         case -1352294148:
            if (var6.equals("create")) {
               var7 = 0;
            }
            break;
         case -1335458389:
            if (var6.equals("delete")) {
               var7 = 1;
            }
            break;
         case 3108362:
            if (var6.equals("edit")) {
               var7 = 2;
            }
         }

         switch(var7) {
         case 0:
            if (!commandSender.hasPermission("spookystash.admin")) {
               return false;
            }

            this.createStash(player, (String[])Arrays.copyOfRange(args, 1, args.length));
            break;
         case 1:
            if (!commandSender.hasPermission("spookystash.admin")) {
               return false;
            }

            this.deleteStash(player, args);
            break;
         case 2:
            if (!commandSender.hasPermission("spookystash.admin")) {
               return false;
            }

            this.editStash(player, args);
            break;
         case 3:
            this.headChange(player, (String[])Arrays.copyOfRange(args, 1, args.length));
         }

         return true;
      } else {
         commandSender.sendMessage("§cДанная команда не доступа с консоли.");
         return false;
      }
   }

   private void sendHelpMessage(CommandSender sender) {
      sender.sendMessage("§6/stash reload - §fПерезагрузить конфиг.");
      sender.sendMessage("§6/stash create <НазвТайник> <ОтображаемоеИмя> - §fСоздать тайник.");
      sender.sendMessage("§6/stash delete <НазвТайник> - §fУдалить тайник.");
      sender.sendMessage("§6/stash edit <НазвТайник> - §fОткрыть гуи редактора");
      sender.sendMessage("§6/stash givekey <Ник> <НазвТайника> <Кол-во> - §fВыдать игроку ключ");
      sender.sendMessage("§6/stash givesilver <Ник> <Кол-во> - §f Выдать игроку серебро");
      sender.sendMessage("§6/stash takesilver <Ник> <Кол-во> - §fЗабрать серебро с инвентаря игрока");
   }

   private void reloadSpookyStash(CommandSender sender) {
      SpookyStash.get().reload();
      sender.sendMessage("§aSpookyStash успешно перезагружен");
   }

   private void createStash(Player player, String[] args) {
      if (args.length < 2) {
         player.sendMessage("§6Использование: §e/stash create [НазвТайника] [ОтображаемоеИмя]");
      } else {
         String stashName = args[0];
         if (stashName.matches(".*[а-яА-я].*")) {
            player.sendMessage("§cЗапрещены русские символы в названии тайника!");
         } else {
            String displayName = String.join(" ", (CharSequence[])Arrays.copyOfRange(args, 1, args.length));
            Stash stash = new Stash(stashName);
            if (!stash.isExists() && !stash.isExistsInConfig()) {
               stash.setDisplayName(displayName);
               stash.create(player);
            } else {
               player.sendMessage("§cТайник с таким именем уже существует!");
            }

         }
      }
   }

   private void deleteStash(Player player, String[] args) {
      if (args.length < 2) {
         player.sendMessage("§7Введите правильное имя тайника.");
      } else {
         String stashName = args[1];
         Stash stash = Stash.getByName(stashName);
         if (stash.isExists()) {
            stash.delete();
            HologramManipulator.delete(stash.getName());
            player.getWorld().getBlockAt(stash.getLocation()).setType(Material.AIR);
         } else {
            player.sendMessage("§cТайника с таким именем не существует.");
         }

      }
   }

   private void editStash(Player player, String[] args) {
      if (args.length < 2) {
         player.sendMessage("§7Введите правильное имя тайника.");
      } else {
         String stashName = args[1];
         Stash byName = Stash.getByName(stashName);
         if (byName != null) {
            EditMenu menu = new EditMenu(stashName);
            menu.create();
            menu.open(player);
         } else {
            player.sendMessage("§cТайник с таким именем не найден!.");
         }

      }
   }

   private void giveKey(CommandSender sender, String[] args) {
      if (args.length < 4) {
         sender.sendMessage("§6Использование: §e/stash givekey [Ник] [НазвТайника] [Кол-во]");
      } else {
         String playerName = args[1];
         String stashName = args[2];
         int keysCount = this.parseInt(args[3], sender);
         Stash stash = Stash.getByName(stashName);
         if (stash == null) {
            sender.sendMessage("§cНе найден тайник с таким именем");
         } else {
            Player playerExact = Bukkit.getPlayerExact(playerName);
            if (playerExact == null) {
               sender.sendMessage("§cИгрок не найден!");
            } else {
               stash.getKey().give(playerExact, keysCount);
               if (sender instanceof Player) {
                  sender.sendMessage(String.format("§6Игроку %s выдано §f%d §6ключей от тайника %s", playerExact.getName(), keysCount, stash.getName()));
               }

            }
         }
      }
   }

   private void giveSilver(CommandSender sender, String[] args) {
      if (args.length < 3) {
         sender.sendMessage("§7Использование: §e/stash givesilver [Ник] [Кол-Во]");
      } else {
         String playerName = args[1];
         int count = this.parseInt(args[2], sender);
         Player playerExact = Bukkit.getPlayerExact(playerName);
         if (playerExact == null) {
            sender.sendMessage("§cИгрок не найден!");
         } else {
            ItemStack silver = ItemUtil.silverItem();
            silver.setAmount(count);
            if (InventoryUtil.isInventoryFull(playerExact)) {
               playerExact.getWorld().dropItemNaturally(playerExact.getLocation(), silver);
            } else {
               playerExact.getInventory().addItem(new ItemStack[]{silver});
            }

         }
      }
   }

   private void takeSilver(CommandSender sender, String[] args) {
      if (args.length < 3) {
         sender.sendMessage("§7Использование: §e/stash takesilver [Ник] [Кол-Во]");
      } else {
         String playerName = args[1];
         int count = this.parseInt(args[2], sender);
         Player playerExact = Bukkit.getPlayerExact(playerName);
         if (playerExact == null) {
            sender.sendMessage("§cИгрок не найден!");
         } else {
            ItemStack silverItem = ItemUtil.silverItem();
            silverItem.setAmount(count);
            int totalSilverCount = 0;
            ItemStack[] var8 = playerExact.getInventory().getContents();
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               ItemStack item = var8[var10];
               if (ItemUtil.isSilverItem(item)) {
                  totalSilverCount += item.getAmount();
               }
            }

            if (totalSilverCount < count) {
               PlayerUtils.sendMsg(playerExact, SpookyStash.get().getMainConfig().getString("messages.notEnoughSilver"));
               playerExact.playSound(playerExact.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1.0F);
            } else {
               ConfigurationSection commandsSection = SpookyStash.get().getMainConfig().getConfiguration().getConfigurationSection("commands");
               if (commandsSection != null) {
                  Iterator var14 = commandsSection.getKeys(false).iterator();

                  while(var14.hasNext()) {
                     String key = (String)var14.next();
                     int amount = commandsSection.getInt(key + ".amount");
                     if (amount == count) {
                        String command = commandsSection.getString(key + ".command").replaceAll("%player%", playerExact.getName());
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                        break;
                     }
                  }
               }

               removeSilverItems(playerExact, count);
               PlayerUtils.sendMsg(playerExact, SpookyStash.get().getMainConfig().getString("messages.thanksForSilver"));
               playerExact.playSound(playerExact.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 0.5F);
            }
         }
      }
   }

   public static void removeSilverItems(Player player, int count) {
      ItemStack[] contents = player.getInventory().getContents();
      int remainingCount = count;

      for(int i = 0; i < contents.length && remainingCount > 0; ++i) {
         ItemStack item = contents[i];
         if (ItemUtil.isSilverItem(item)) {
            int amountToRemove = Math.min(item.getAmount(), remainingCount);
            item.setAmount(item.getAmount() - amountToRemove);
            remainingCount -= amountToRemove;
            if (item.getAmount() <= 0) {
               contents[i] = null;
            }
         }
      }

      player.getInventory().setContents(contents);
   }

   private void headChange(Player player, String[] args) {
      if (args.length >= 3 && args.length <= 4) {
         BasicConfiguration mainConfig = SpookyStash.get().getMainConfig();
         YamlConfiguration config = mainConfig.getConfiguration();
         String headType = args[1].toLowerCase();
         int option = this.parseInt(args[2], player);
         boolean all = args.length == 4 && args[3].equalsIgnoreCase("all");
         String path = "change." + headType + "." + option;
         if (config.contains(path)) {
            if (config.contains("change." + headType)) {
               ItemStack head;
               try {
                  head = new ItemStack((Material)Objects.requireNonNull(Material.getMaterial(headType.toUpperCase())));
               } catch (IllegalArgumentException var14) {
                  return;
               }

               int headCount = all ? this.getHeadCount(player, head) : 1;
               if (headCount == 1 && !player.getInventory().contains(head.getType(), 1) || all && headCount == 0) {
                  PlayerUtils.sendMessage(player, SpookyStash.get().getMainConfig().getString("messages.change_nohead"));
                  player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
               } else {
                  Economy eco = SpookyStash.get().getEco();
                  int rewardAmount;
                  if (config.contains(path + ".vault")) {
                     rewardAmount = config.getInt(path + ".vault") * headCount;
                     eco.depositPlayer(player, (double)rewardAmount);
                  } else if (config.contains(path + ".diamond")) {
                     rewardAmount = config.getInt(path + ".diamond") * headCount;
                     InventoryUtil.giveItem(player, new ItemStack(Material.DIAMOND), rewardAmount);
                  } else if (config.contains(path + ".silver")) {
                     rewardAmount = config.getInt(path + ".silver") * headCount;
                     ItemStack silver = ItemUtil.silverItem();
                     InventoryUtil.giveItem(player, silver, rewardAmount);
                  }

                  this.removeItemsByMaterial(player, head.getType(), headCount);
                  player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                  PlayerUtils.sendMessage(player, SpookyStash.get().getMainConfig().getString("messages.change_success"));
               }
            }
         }
      }
   }

   private void removeItemsByMaterial(Player player, Material material, int amount) {
      ItemStack[] contents = player.getInventory().getContents();
      int remaining = amount;

      for(int i = 0; i < contents.length; ++i) {
         ItemStack item = contents[i];
         if (item != null && item.getType() == material) {
            int itemAmount = item.getAmount();
            if (itemAmount > remaining) {
               item.setAmount(itemAmount - remaining);
               boolean var9 = false;
               break;
            }

            player.getInventory().setItem(i, (ItemStack)null);
            remaining -= itemAmount;
         }
      }

   }

   private int getHeadCount(Player player, ItemStack head) {
      int count = 0;
      ItemStack[] var4 = player.getInventory().getContents();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ItemStack item = var4[var6];
         if (item != null && item.getType() == head.getType()) {
            count += item.getAmount();
         }
      }

      return count;
   }

   private int parseInt(String arg, CommandSender sender) {
      try {
         return Integer.parseInt(arg);
      } catch (NumberFormatException var4) {
         sender.sendMessage("§cКоличество должно быть числом.");
         return -1;
      }
   }

   private void giveSplinter(CommandSender sender, String[] args) {
      if (args.length < 3) {
         sender.sendMessage("§7Использование: §e/stash givesplinter [Ник] [Кол-во]");
      } else {
         String playerName = args[1];
         int count = this.parseInt(args[2], sender);
         Player playerExact = Bukkit.getPlayerExact(playerName);
         if (playerExact == null) {
            sender.sendMessage("§cИгрок не найден!");
         } else {
            ItemStack splinter = ItemUtil.splinterItem();
            InventoryUtil.giveItem(playerExact, splinter, count);
         }
      }
   }

   private void takeSplinter(CommandSender sender, String[] args) {
      if (args.length < 3) {
         sender.sendMessage("§7Использование: §e/stash takesplinter [Ник] [Кол-во]");
      } else {
         String playerName = args[1];
         int count = this.parseInt(args[2], sender);
         Player playerExact = Bukkit.getPlayerExact(playerName);
         if (playerExact == null) {
            sender.sendMessage("§cИгрок не найден!");
         } else {
            int totalSplinterCount = 0;
            ItemStack[] var7 = playerExact.getInventory().getContents();
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               ItemStack item = var7[var9];
               if (ItemUtil.isSplinterItem(item)) {
                  totalSplinterCount += item.getAmount();
               }
            }

            if (totalSplinterCount < count) {
               PlayerUtils.sendMsg(playerExact, SpookyStash.get().getMainConfig().getString("messages.notEnoughsSplinter"));
               playerExact.playSound(playerExact.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1.0F);
            } else {
               removeSplinterItems(playerExact, count);
               playerExact.playSound(playerExact.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5F, 1.0F);
            }
         }
      }
   }

   public static void removeSplinterItems(Player player, int count) {
      ItemStack[] contents = player.getInventory().getContents();
      int remainingCount = count;

      for(int i = 0; i < contents.length && remainingCount > 0; ++i) {
         ItemStack item = contents[i];
         if (ItemUtil.isSplinterItem(item)) {
            int amountToRemove = Math.min(item.getAmount(), remainingCount);
            item.setAmount(item.getAmount() - amountToRemove);
            remainingCount -= amountToRemove;
            if (item.getAmount() <= 0) {
               contents[i] = null;
            }
         }
      }

      player.getInventory().setContents(contents);
   }

   private void changeSplinter(CommandSender sender, String[] args) {
      if (args.length < 3) {
         sender.sendMessage("§7Использование: §e/stash changesplinter [Ник] [Кол-во]");
      } else {
         String playerName = args[1];
         int count = this.parseInt(args[2], sender);
         Player playerExact = Bukkit.getPlayerExact(playerName);
         if (playerExact == null) {
            sender.sendMessage("§cИгрок не найден!");
         } else {
            ConfigurationSection changesplinter = SpookyStash.get().getMainConfig().getConfiguration().getConfigurationSection("changesplinter.splinter");
            if (changesplinter == null) {
               sender.sendMessage("§cНастройки обмена не найдены.");
            } else {
               int totalSplinterCount = 0;
               ItemStack[] var8 = playerExact.getInventory().getContents();
               int var9 = var8.length;

               int requiredSplinters;
               for(requiredSplinters = 0; requiredSplinters < var9; ++requiredSplinters) {
                  ItemStack item = var8[requiredSplinters];
                  if (ItemUtil.isSplinterItem(item)) {
                     totalSplinterCount += item.getAmount();
                  }
               }

               if (totalSplinterCount < count) {
                  PlayerUtils.sendMsg(playerExact, SpookyStash.get().getMainConfig().getString("messages.notEnoughsSplinter"));
                  playerExact.playSound(playerExact.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1.0F);
               } else {
                  Iterator var16 = changesplinter.getKeys(false).iterator();

                  while(var16.hasNext()) {
                     String key = (String)var16.next();
                     requiredSplinters = Integer.parseInt(key);
                     if (requiredSplinters == count) {
                        List<String> commands = changesplinter.getStringList(key + ".commands");
                        if (!commands.isEmpty()) {
                           String command = ((String)commands.get((new Random()).nextInt(commands.size()))).replaceAll("%player%", playerExact.getName());
                           if (InventoryUtil.isInventoryFull(playerExact)) {
                              String itemName = command.split(" ")[2].trim();
                              int amount = Integer.parseInt(command.split(" ")[3].trim());
                              ItemStack item = new ItemStack(Material.getMaterial(itemName.toUpperCase()), amount);
                              playerExact.getWorld().dropItemNaturally(playerExact.getLocation(), item);
                           } else {
                              Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                           }
                        }
                        break;
                     }
                  }

                  removeSplinterItems(playerExact, count);
                  playerExact.playSound(playerExact.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5F, 1.0F);
               }
            }
         }
      }
   }

   private void changeSoul(CommandSender sender, String[] args) {
      if (args.length < 3) {
         sender.sendMessage("§7Использование: §e/stash changesoul [Ник] [Кол-во]");
      } else {
         String playerName = args[1];
         int count = this.parseInt(args[2], sender);
         Player playerExact = Bukkit.getPlayerExact(playerName);
         if (playerExact == null) {
            sender.sendMessage("§cИгрок не найден!");
         } else {
            ConfigurationSection changesplinter = SpookyStash.get().getMainConfig().getConfiguration().getConfigurationSection("changesoul.soul");
            if (changesplinter == null) {
               sender.sendMessage("§cНастройки обмена не найдены.");
            } else {
               int totalSplinterCount = 0;
               ItemStack[] var8 = playerExact.getInventory().getContents();
               int var9 = var8.length;

               int requiredSplinters;
               for(requiredSplinters = 0; requiredSplinters < var9; ++requiredSplinters) {
                  ItemStack item = var8[requiredSplinters];
                  if (ItemUtil.isSoulItem(item)) {
                     totalSplinterCount += item.getAmount();
                  }
               }

               if (totalSplinterCount < count) {
                  PlayerUtils.sendMsg(playerExact, SpookyStash.get().getMainConfig().getString("messages.notEnoughsSoul"));
                  playerExact.playSound(playerExact.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1.0F);
               } else {
                  Iterator var16 = changesplinter.getKeys(false).iterator();

                  while(var16.hasNext()) {
                     String key = (String)var16.next();
                     requiredSplinters = Integer.parseInt(key);
                     if (requiredSplinters == count) {
                        List<String> commands = changesplinter.getStringList(key + ".commands");
                        if (!commands.isEmpty()) {
                           String command = ((String)commands.get((new Random()).nextInt(commands.size()))).replaceAll("%player%", playerExact.getName());
                           if (InventoryUtil.isInventoryFull(playerExact)) {
                              String itemName = command.split(" ")[2].trim();
                              int amount = Integer.parseInt(command.split(" ")[3].trim());
                              ItemStack item = new ItemStack(Material.getMaterial(itemName.toUpperCase()), amount);
                              playerExact.getWorld().dropItemNaturally(playerExact.getLocation(), item);
                           } else {
                              Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                           }
                        }
                        break;
                     }
                  }

                  removeSoulItems(playerExact, count);
                  playerExact.playSound(playerExact.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5F, 1.0F);
               }
            }
         }
      }
   }

   public static void removeSoulItems(Player player, int count) {
      ItemStack[] contents = player.getInventory().getContents();
      int remainingCount = count;

      for(int i = 0; i < contents.length && remainingCount > 0; ++i) {
         ItemStack item = contents[i];
         if (ItemUtil.isSoulItem(item)) {
            int amountToRemove = Math.min(item.getAmount(), remainingCount);
            item.setAmount(item.getAmount() - amountToRemove);
            remainingCount -= amountToRemove;
            if (item.getAmount() <= 0) {
               contents[i] = null;
            }
         }
      }

      player.getInventory().setContents(contents);
   }

   private void takeSoul(CommandSender sender, String[] args) {
      if (args.length < 3) {
         sender.sendMessage("§7Использование: §e/stash takesoul [Ник] [Кол-во]");
      } else {
         String playerName = args[1];
         int count = this.parseInt(args[2], sender);
         Player playerExact = Bukkit.getPlayerExact(playerName);
         if (playerExact == null) {
            sender.sendMessage("§cИгрок не найден!");
         } else {
            int totalSplinterCount = 0;
            ItemStack[] var7 = playerExact.getInventory().getContents();
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               ItemStack item = var7[var9];
               if (ItemUtil.isSoulItem(item)) {
                  totalSplinterCount += item.getAmount();
               }
            }

            if (totalSplinterCount < count) {
               PlayerUtils.sendMsg(playerExact, SpookyStash.get().getMainConfig().getString("messages.notEnoughsSoul"));
               playerExact.playSound(playerExact.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1.0F);
            } else {
               removeSoulItems(playerExact, count);
               playerExact.playSound(playerExact.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5F, 1.0F);
            }
         }
      }
   }

   private void giveSoul(CommandSender sender, String[] args) {
      if (args.length < 3) {
         sender.sendMessage("§7Использование: §e/stash givesoul [Ник] [Кол-во]");
      } else {
         String playerName = args[1];
         int count = this.parseInt(args[2], sender);
         Player playerExact = Bukkit.getPlayerExact(playerName);
         if (playerExact == null) {
            sender.sendMessage("§cИгрок не найден!");
         } else {
            ItemStack splinter = ItemUtil.soulItem();
            InventoryUtil.giveItem(playerExact, splinter, count);
         }
      }
   }

   private void changeFish(CommandSender player, String[] args) {
      if (args.length < 3) {
         player.sendMessage("§7Использование: §e/stash changefish [Ник] [КакаяРыба] [1/all]");
      } else {
         String playerName = args[1];
         String fishType = args[2].toLowerCase();
         boolean all = args.length == 4 && args[3].equalsIgnoreCase("all");
         int fishCount = all ? -1 : this.parseInt(args[3], player);
         BasicConfiguration mainConfig = SpookyStash.get().getMainConfig();
         YamlConfiguration config = mainConfig.getConfiguration();
         String path = "changefish." + fishType;
         if (!config.contains(path)) {
            player.sendMessage("§cНеизвестный тип рыбы.");
         } else {
            Material fishMaterial;
            try {
               fishMaterial = Material.valueOf(fishType.toUpperCase());
            } catch (IllegalArgumentException var15) {
               player.sendMessage("§cНеверный тип рыбы.");
               return;
            }

            Player targetPlayer = Bukkit.getPlayerExact(playerName);
            if (targetPlayer == null) {
               player.sendMessage("§cИгрок не найден!");
            } else {
               int availableFishCount = this.getItemCount(targetPlayer, fishMaterial);
               if (fishCount == -1) {
                  fishCount = availableFishCount;
               } else if (fishCount > availableFishCount) {
                  PlayerUtils.sendMessage(targetPlayer, SpookyStash.get().getMainConfig().getString("messages.noFish"));
                  targetPlayer.playSound(targetPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                  return;
               }

               Economy eco = SpookyStash.get().getEco();
               int rewardAmount = config.getInt(path + ".vault") * fishCount;
               eco.depositPlayer(targetPlayer, (double)rewardAmount);
               this.removeItems(targetPlayer, fishMaterial, fishCount);
               PlayerUtils.sendMessage(targetPlayer, SpookyStash.get().getMainConfig().getString("messages.fishSold"));
               targetPlayer.playSound(targetPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
            }
         }
      }
   }

   private int getItemCount(Player player, Material material) {
      int count = 0;
      ItemStack[] var4 = player.getInventory().getContents();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ItemStack item = var4[var6];
         if (item != null && item.getType() == material) {
            count += item.getAmount();
         }
      }

      return count;
   }

   private void removeItems(Player player, Material material, int count) {
      ItemStack[] contents = player.getInventory().getContents();
      int remainingCount = count;

      for(int i = 0; i < contents.length && remainingCount > 0; ++i) {
         ItemStack item = contents[i];
         if (item != null && item.getType() == material) {
            int amountToRemove = Math.min(item.getAmount(), remainingCount);
            item.setAmount(item.getAmount() - amountToRemove);
            remainingCount -= amountToRemove;
            if (item.getAmount() <= 0) {
               contents[i] = null;
            }
         }
      }

      player.getInventory().setContents(contents);
   }

   public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
      if (sender.hasPermission("spookystash.admin") && !(sender instanceof ConsoleCommandSender) && args != null && args.length != 0) {
         List<String> completions = new ArrayList();
         if (args.length == 1) {
            completions.add("reload");
            completions.add("create");
            completions.add("delete");
            completions.add("edit");
            completions.add("givekey");
            completions.add("givesilver");
            completions.add("takesilver");
            completions.add("givesplinter");
            completions.add("takesplinter");
            completions.add("changesplinter");
         } else {
            String var6;
            byte var7;
            Iterator var8;
            Stash stash;
            if (args.length == 2) {
               var6 = args[0].toLowerCase();
               var7 = -1;
               switch(var6.hashCode()) {
               case -1335458389:
                  if (var6.equals("delete")) {
                     var7 = 0;
                  }
                  break;
               case -800545122:
                  if (var6.equals("givesplinter")) {
                     var7 = 3;
                  }
                  break;
               case 3108362:
                  if (var6.equals("edit")) {
                     var7 = 1;
                  }
                  break;
               case 41749934:
                  if (var6.equals("givekey")) {
                     var7 = 2;
                  }
                  break;
               case 234920468:
                  if (var6.equals("takesplinter")) {
                     var7 = 4;
                  }
                  break;
               case 2018000957:
                  if (var6.equals("changesplinter")) {
                     var7 = 5;
                  }
               }

               switch(var7) {
               case 0:
               case 1:
                  var8 = SpookyStash.get().getStashes().iterator();

                  while(var8.hasNext()) {
                     stash = (Stash)var8.next();
                     completions.add(stash.getName());
                  }

                  return completions;
               case 2:
               case 3:
               case 4:
               case 5:
                  var8 = Bukkit.getOnlinePlayers().iterator();

                  while(var8.hasNext()) {
                     Player onlinePlayer = (Player)var8.next();
                     completions.add(onlinePlayer.getName());
                  }
               }
            } else if (args.length == 3) {
               var6 = args[0].toLowerCase();
               var7 = -1;
               switch(var6.hashCode()) {
               case -800545122:
                  if (var6.equals("givesplinter")) {
                     var7 = 1;
                  }
                  break;
               case 41749934:
                  if (var6.equals("givekey")) {
                     var7 = 0;
                  }
                  break;
               case 234920468:
                  if (var6.equals("takesplinter")) {
                     var7 = 2;
                  }
               }

               switch(var7) {
               case 0:
               case 1:
               case 2:
                  var8 = SpookyStash.get().getStashes().iterator();

                  while(var8.hasNext()) {
                     stash = (Stash)var8.next();
                     completions.add(stash.getName());
                  }
               }
            }
         }

         return completions;
      } else {
         return Collections.emptyList();
      }
   }
}
