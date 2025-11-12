package ru.enwulf.spookystash.utils.file;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.enwulf.spookystash.SpookyStash;
import ru.enwulf.spookystash.utils.ColorUtils;

public class BasicConfiguration {
    private final File file;
    private final YamlConfiguration configuration;
    private final String name;

    public BasicConfiguration(JavaPlugin plugin, String name, boolean overwrite) {
        this.name = name;
        this.file = new File(plugin.getDataFolder(), name + ".yml");
        if (!this.file.exists() || overwrite) {
            plugin.saveResource(name + ".yml", overwrite);
        }

        this.configuration = new YamlConfiguration();

        try {
            this.configuration.loadFromString(Files.toString(this.file, StandardCharsets.UTF_8));
        } catch (IOException | InvalidConfigurationException var5) {
            var5.printStackTrace();
        }
    }

    public BasicConfiguration(JavaPlugin plugin, String name) {
        this(plugin, name, false);
    }

    public String getString(String path) {
        return this.configuration.contains(path) ? ColorUtils.colorize(this.configuration.getString(path)) : null;
    }

    public Object get(String path) {
        return this.configuration.contains(path) ? this.configuration.get(path) : null;
    }

    public List<String> getStringList(String path) {
        if (!this.configuration.contains(path)) {
            return Collections.emptyList();
        } else {
            List<String> toReturn = new ArrayList<>();
            Iterator<String> var3 = this.configuration.getStringList(path).iterator();

            while(var3.hasNext()) {
                String string = var3.next();
                toReturn.add(ColorUtils.colorize(string));
            }

            return toReturn;
        }
    }

    public void reload() {
        File file = new File(SpookyStash.get().getDataFolder(), this.getName() + ".yml");
        try {
            this.configuration.load(file);
        } catch (InvalidConfigurationException | IOException var3) {
            var3.printStackTrace();
        }
    }

    public void save() {
        File folder = SpookyStash.get().getDataFolder();
        try {
            this.configuration.save(new File(folder, this.getName() + ".yml"));
        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }

    public File getFile() {
        return this.file;
    }

    public YamlConfiguration getConfiguration() {
        return this.configuration;
    }

    public String getName() {
        return this.name;
    }
}