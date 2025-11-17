package org.plugin.zombieSpawning;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
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

    public String getRegionIdAtLocation(Location loc)
    {
        String worldName = loc.getWorld().getName();
        int checkX = loc.getBlockX();
        int checkZ = loc.getBlockZ();

        Set<String> regionKeys = config.getKeys(false);

        for (String id : regionKeys)
        {
            if (!config.getString(id + ".world", "").equals(worldName))
            {
                continue;
            }

            String pos1Str = config.getString(id + ".pos1", "0,0");
            String pos2Str = config.getString(id + ".pos2", "0,0");

            try
            {
                String[] p1 = pos1Str.split(",");
                String[] p2 = pos2Str.split(",");

                int x1 = Integer.parseInt(p1[0]);
                int z1 = Integer.parseInt(p1[1]);
                int x2 = Integer.parseInt(p2[0]);
                int z2 = Integer.parseInt(p2[1]);

                int minX = Math.min(x1, x2);
                int maxX = Math.max(x1, x2);
                int minZ = Math.min(z1, z2);
                int maxZ = Math.max(z1, z2);

                if(checkX >= minX && checkX <= maxX && checkZ >= minZ && checkZ <= maxZ)
                {
                    return id;
                }
            }
            catch(NumberFormatException e)
            {
                Bukkit.getLogger().warning("Corrupted coordinates in region: " + id);
            }
        }
        return null;
    }

    public int getLimit(Location loc)
    {
        String regionId = getRegionIdAtLocation(loc);
        if (regionId != null)
        {
            return config.getInt(regionId + ".limit", 5);
        }

        return 5;
    }

    public void createRegion(String id, String worldName, int x1, int z1, int x2, int z2, int limit)
    {
        config.set(id + ".world", worldName);
        config.set(id + ".pos1", x1 + "," + z1);
        config.set(id + ".pos2", x2 + "," + z2);
        config.set(id + ".limit", limit);
        save();
    }

    public void deleteRegion(String id)
    {
        config.set(id, null);
        save();
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
