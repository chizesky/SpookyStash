package ru.enwulf.spookystash.handlers;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.enwulf.spookystash.SpookyStash;
import ru.enwulf.spookystash.stash.Stash;

public class StashHandler {
    public HashMap<Player, Stash> openings = new HashMap();

    public BukkitTask spawnFireParticles(final Location location, final int durationTicks) {
        final World world = location.getWorld();
        BukkitTask particleTask = (new BukkitRunnable() {
            int ticksPassed = 0;

            public void run() {
                world.spawnParticle(Particle.LAVA, location, 20, 0.0D, 1.0D, 0.0D);
                world.spawnParticle(Particle.FLAME, location, 20, 0.3D, 0.5D, 0.3D, 0.01D);
                StashHandler.this.playSound(location);
                ++this.ticksPassed;
                if (this.ticksPassed >= durationTicks) {
                    this.cancel();
                }

            }
        }).runTaskTimer(SpookyStash.get(), 0L, 8L);
        return particleTask;
    }

    public void spawnFireworkParticles(Location location) {
        location.getWorld().spawnParticle(Particle.END_ROD, location.clone().add(0.0D, 1.0D, 0.0D), 15, 0.0D, 0.0D, 0.0D, 0.1D);
    }

    public void playSound(Location location) {
        location.getWorld().playSound(location, Sound.BLOCK_LAVA_POP, 1.0F, 1.0F);
    }

    public ItemStack getRandomItemFromStash(Stash stash) {
        ItemStack[] content = stash.getContent();
        if (content != null && content.length != 0) {
            int randomIndex = ThreadLocalRandom.current().nextInt(content.length);
            return content[randomIndex];
        } else {
            return new ItemStack(Material.DIRT);
        }
    }

    public void addOpener(Player player, Stash stash) {
        this.openings.put(player, stash);
    }

    public boolean isBusy(Player stash) {
        return this.openings.get(stash) == null;
    }
}
