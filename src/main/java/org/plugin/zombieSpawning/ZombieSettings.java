package org.plugin.zombieSpawning;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class ZombieSettings
{
    private final static ZombieSettings instance = new ZombieSettings();

    private File file;
    private YamlConfiguration config;

    private ZombieSettings()
    {

    }

    public void load()
    {
        file = new File(ZombieSpawning.getInstance().getDataFolder(), "chunks.yml");

        if(!(file.exists()))
        {
            ZombieSpawning.getInstance().saveResource("chunks.yml", false);
        }

        config = new YamlConfiguration();
        config.options().parseComments(true);

        try
        {
            config.load(file);
        }
        catch (Exception e)
        {
            Bukkit.getLogger().severe("Failed to load chunks.yml configuration file!");
            e.printStackTrace();
        }
    }

    private static String getChunkKey(Chunk chunk)
    {
        return chunk.getWorld().getName() + ";" + chunk.getX() + ";" + chunk.getZ();
    }

    public void setLimit(Chunk chunk, int newLimit)
    {
        String key = getChunkKey(chunk);
        config.set(key + ".limit", newLimit);
        save();
    }

    public int getLimit(Chunk chunk)
    {
        String key = getChunkKey(chunk);
        return config.getInt(key + ".limit", 5);
    }

    public boolean isChunkSet(Chunk chunk)
    {
        String key = getChunkKey(chunk);
        return config.isSet(key);
    }

    public void save()
    {
        try
        {
            this.config.save(this.file);
        }
        catch (Exception e1)
        {
            Bukkit.getLogger().severe("Could not save chunks.yml: " + e1.getMessage());
            e1.printStackTrace();
        }
    }

    public static ZombieSettings getInstance()
    {
        return instance;
    }
}
