package ru.enwulf.spookystash.stash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.enwulf.spookystash.HologramManipulator;
import ru.enwulf.spookystash.SpookyStash;
import ru.enwulf.spookystash.utils.InventoryUtil;
import ru.enwulf.spookystash.utils.LocationUtil;
import ru.enwulf.spookystash.utils.file.BasicConfiguration;

public final class Stash {
    private String title;
    private final String name;
    private String displayName;
    private Location location;
    private Inventory content;
    private boolean canOpen = true;
    private StashKey key;
    private BasicConfiguration config = SpookyStash.get().getCache();

    public Stash(String name) {
        this.name = name;
        this.key = new StashKey(this);
    }

    public void create(Player player) {
        Location targetBlockLocation = LocationUtil.getTargetBlockLocation(player);
        this.location = targetBlockLocation;
        if (targetBlockLocation == null) {
            player.sendMessage("§cНе найдено место для установки.");
        } else {
            Material heldItem = player.getInventory().getItemInMainHand().getType();
            if (heldItem.isBlock() && heldItem != Material.AIR) {
                this.spawn(targetBlockLocation, heldItem);
                player.sendMessage(String.format("§6Тайник \"§f%s§6\" успешно создан [%d, %d, %d]", this.name, (int)targetBlockLocation.getX(), (int)targetBlockLocation.getY(), (int)targetBlockLocation.getZ()));
                SpookyStash.get().getStashes().add(this);
                HologramManipulator.spawn(this.getLocation(), this.getName(), this.getDisplayName());
                this.save();
            } else {
                player.sendMessage("§cВозьмите блок в руку!");
            }
        }
    }

    private void spawn(Location location, Material material) {
        World world = location.getWorld();
        Block blockAt = world.getBlockAt(location);
        blockAt.setType(material);
        blockAt.setMetadata("SpookyType", new FixedMetadataValue(SpookyStash.get(), "SpookyStashData." + this.getName()));
    }

    public void open(final Player player) {
        if (this.canOpen) {
            SpookyStash.get().getStashHandler().addOpener(player, this);
            this.canOpen = false;
            final ItemStack randomItem = SpookyStash.get().getStashHandler().getRandomItemFromStash(this);
            if (randomItem == null) {
                player.sendMessage("§cТехническая ошибка... Кейс пустой");
                return;
            }

            final Location stashLoc = this.location.clone().add(0.5D, 0.6D, 0.5D);
            final BukkitTask particleTask = SpookyStash.get().getStashHandler().spawnFireParticles(stashLoc, 6);
            HologramManipulator.hide(this.getName());

            (new BukkitRunnable() {
                public void run() {
                    stashLoc.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0F, 0.7F);
                    Component component = randomItem.getItemMeta().displayName();
                    String title;
                    if (component != null) {
                        title = LegacyComponentSerializer.legacyAmpersand().serialize(component);
                    } else {
                        title = "§6" + randomItem.getType();
                    }

                    HologramManipulator.moveY(Stash.this.getName(), -0.2F);
                    HologramManipulator.edit(Stash.this.getName(), title, randomItem);
                    SpookyStash.get().getStashHandler().spawnFireworkParticles(stashLoc);

                    if (InventoryUtil.isInventoryFull(player)) {
                        player.getWorld().dropItemNaturally(player.getLocation(), randomItem);
                    } else {
                        player.getInventory().addItem(new ItemStack[]{randomItem});
                    }

                    particleTask.cancel();

                    (new BukkitRunnable() {
                        public void run() {
                            HologramManipulator.restore(Stash.this.getName(), Stash.this.getDisplayName());
                            HologramManipulator.moveY(Stash.this.getName(), 0.2F);
                            SpookyStash.get().getStashHandler().openings.put(player, null);
                            stashLoc.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 0.01F);
                            Stash.this.canOpen = true;
                        }
                    }).runTaskLater(SpookyStash.get(), 100L);
                }
            }).runTaskLater(SpookyStash.get(), 30L);
        }
    }

    public boolean isExistsInConfig() {
        return this.config.getConfiguration().contains("stashes." + this.name);
    }

    public boolean isExists() {
        return SpookyStash.get().getStashes().contains(this);
    }

    public boolean canOpen() {
        return this.canOpen;
    }

    public static void init() {
        YamlConfiguration config = SpookyStash.get().getCache().getConfiguration();
        BasicConfiguration mainConfig = SpookyStash.get().getMainConfig();
        ConfigurationSection stashes1 = config.getConfigurationSection("stashes");
        if (stashes1 != null) {
            ConfigurationSection stashes2 = mainConfig.getConfiguration().getConfigurationSection("stashes");
            if (stashes2 != null) {
                Set<String> stashes = config.getConfigurationSection("stashes").getKeys(false);
                if (!stashes.isEmpty()) {
                    Iterator<String> var5 = stashes.iterator();

                    while(var5.hasNext()) {
                        String key = var5.next();
                        String path = "stashes." + key;
                        Stash stash = new Stash(key);
                        stash.setKey(new StashKey(stash));
                        Location deserializeLocation = LocationUtil.deserialize(config.getString(path + ".location"));
                        stash.setLocation(deserializeLocation);
                        String stashName = mainConfig.getString("stashes." + stash.getName() + ".stash_name");
                        stash.setDisplayName(stashName);
                        HologramManipulator.spawn(Objects.requireNonNull(deserializeLocation), stash.getName(), stash.getDisplayName());
                        World world = stash.location.getWorld();
                        world.getBlockAt(deserializeLocation).setMetadata("SpookyType", new FixedMetadataValue(SpookyStash.get(), "SpookyStashData." + key));
                        SpookyStash.get().getStashes().add(stash);
                    }
                }
            }
        }
    }

    public void save() {
        YamlConfiguration configuration = this.config.getConfiguration();
        YamlConfiguration configurationMain = SpookyStash.get().getMainConfig().getConfiguration();
        configurationMain.set("stashes." + this.name + ".stash_name", this.getDisplayName());
        configuration.set("stashes." + this.name + ".location", LocationUtil.serialize(this.location));
        configuration.set("stashes." + this.name + ".contents", "");
        this.getKey().save();
        SpookyStash.get().getCache().save();
        SpookyStash.get().getCache().reload();
    }

    public ItemStack[] getContent() {
        YamlConfiguration configuration = this.config.getConfiguration();
        String o = (String)configuration.get("stashes." + this.name + ".contents");
        if (o != null && !o.isEmpty()) {
            try {
                ItemStack[] itemStacks = InventoryUtil.itemStackArrayFromBase64(o);
                List<ItemStack> itemList = new ArrayList<>();
                for (ItemStack itemStack : itemStacks) {
                    if (itemStack != null) {
                        itemList.add(itemStack);
                    }
                }
                return itemList.toArray(new ItemStack[0]);
            } catch (IOException var9) {
                throw new RuntimeException(var9);
            }
        } else {
            return null;
        }
    }

    public static Stash getByName(String name) {
        return SpookyStash.get().getStashes().stream()
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public void delete() {
        YamlConfiguration configuration = SpookyStash.get().getCache().getConfiguration();
        YamlConfiguration mainConfig = SpookyStash.get().getMainConfig().getConfiguration();
        SpookyStash.get().getStashes().remove(this);
        configuration.set("stashes." + this.getName(), null);
        mainConfig.set("stashes." + this.getName(), null);

        try {
            configuration.save(SpookyStash.get().getCache().getFile());
            mainConfig.save(SpookyStash.get().getMainConfig().getFile());
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Location getLocation() {
        return this.location;
    }

    public boolean isCanOpen() {
        return this.canOpen;
    }

    public StashKey getKey() {
        return this.key;
    }

    public BasicConfiguration getConfig() {
        return this.config;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setContent(Inventory content) {
        this.content = content;
    }

    public void setKey(StashKey key) {
        this.key = key;
    }
}