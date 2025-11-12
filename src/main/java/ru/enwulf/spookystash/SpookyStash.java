package ru.enwulf.spookystash;

import eu.decentsoftware.holograms.api.DHAPI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import ru.enwulf.spookystash.commands.StashCommand;
import ru.enwulf.spookystash.handlers.StashHandler;
import ru.enwulf.spookystash.listener.CraftingListener;
import ru.enwulf.spookystash.listener.InteractListener;
import ru.enwulf.spookystash.listener.StashListener;
import ru.enwulf.spookystash.stash.Stash;
import ru.enwulf.spookystash.utils.file.BasicConfiguration;

public final class SpookyStash extends JavaPlugin {
    private final Set<Stash> stashes = new HashSet();
    public BasicConfiguration cache;
    public BasicConfiguration mainConfig;
    private StashHandler stashHandler;
    private Economy eco;

    public void onEnable() {
        this.setupEconomy();
        this.getCommand("stash").setExecutor(new StashCommand());
        this.getCommand("stash").setTabCompleter(new StashCommand());
        get().getStashes();
        this.loadConfig();
        this.stashHandler = new StashHandler();
        Stash.init();
        this.getServer().getPluginManager().registerEvents(new InteractListener(), this);
        this.getServer().getPluginManager().registerEvents(new StashListener(), this);
        this.getServer().getPluginManager().registerEvents(new CraftingListener(), this);
    }

    public void onDisable() {
        this.stashes.forEach((stash) -> {
            DHAPI.removeHologram(stash.getName());
        });
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            this.eco = (Economy)rsp.getProvider();
        }
    }

    public void reload() {
        this.cache.reload();
        this.mainConfig.reload();
        Iterator var1 = this.getStashes().iterator();

        while(var1.hasNext()) {
            Stash stash = (Stash)var1.next();
            stash.setDisplayName(this.mainConfig.getString("stashes." + stash.getName() + ".stash_name"));
            HologramManipulator.restore(stash.getName(), stash.getDisplayName());
        }

    }

    public void loadConfig() {
        this.cache = new BasicConfiguration(this, "cache");
        this.mainConfig = new BasicConfiguration(this, "config");
    }

    public boolean isKeyInStashes(ItemStack itemStack) {
        Iterator var2 = this.stashes.iterator();

        Stash stash;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            stash = (Stash)var2.next();
        } while(!stash.getKey().isKey(itemStack));

        return true;
    }

    public static SpookyStash get() {
        return (SpookyStash)getPlugin(SpookyStash.class);
    }

    public BasicConfiguration getCache() {
        return this.cache;
    }

    public BasicConfiguration getMainConfig() {
        return this.mainConfig;
    }

    public StashHandler getStashHandler() {
        return this.stashHandler;
    }

    public Economy getEco() {
        return this.eco;
    }

    public Set<Stash> getStashes() {
        return this.stashes;
    }
}
