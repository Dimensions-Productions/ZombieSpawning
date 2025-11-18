package org.plugin.zombieSpawning;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

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

        for(String id : regionKeys)
        {
            if(!config.getString(id + ".world", "").equals(worldName))
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

    public Location[] getRegionEdges(String id)
    {
        String[] og = config.getString(id + ".pos1", "0,0").split(",");
        World world = Bukkit.getWorld(config.getString(id + ".world", "world"));
        int x = Integer.parseInt(og[0]);
        int z = Integer.parseInt(og[1]);
        int y = 50;
        String[] og2 = config.getString(id + ".pos2", "0,0").split(",");
        int x2 = Integer.parseInt(og2[0]);
        int z2 = Integer.parseInt(og2[1]);
        Location[] locations = new Location[4];
        locations[0] = new Location(world, x, y, z);
        locations[1] = new Location(world, x2, y, z2);
        locations[2] = new Location(world, x, y, z2);
        locations[3] = new Location(world, x2, y, z);
        return locations;
    }

    public String isConflicting(String newRegionId, String newWorldName, int B_x1, int B_z1, int B_x2, int B_z2)
    {
        int B_minX = Math.min(B_x1, B_x2);
        int B_maxX = Math.max(B_x1, B_x2);
        int B_minZ = Math.min(B_z1, B_z2);
        int B_maxZ = Math.max(B_z1, B_z2);

        ConfigurationSection regionsSection = config.getConfigurationSection("");
        if (regionsSection == null) return null;
        Set<String> allRegionIds = regionsSection.getKeys(false);

        for(String existingId : allRegionIds)
        {
            if(existingId.equalsIgnoreCase(newRegionId))
            {
                return existingId;
            }

            try
            {
                String[] p1 = config.getString(existingId + ".pos1", "0,0").split(",");
                String[] p2 = config.getString(existingId + ".pos2", "0,0").split(",");
                int A_x1 = Integer.parseInt(p1[0]);
                int A_z1 = Integer.parseInt(p1[1]);
                int A_x2 = Integer.parseInt(p2[0]);
                int A_z2 = Integer.parseInt(p2[1]);
                int A_minX = Math.min(A_x1, A_x2);
                int A_maxX = Math.max(A_x1, A_x2);
                int A_minZ = Math.min(A_z1, A_z2);
                int A_maxZ = Math.max(A_z1, A_z2);

                if(checkOverlap(A_minX, A_maxX, B_minX, B_maxX, A_minZ, A_maxZ, B_minZ, B_maxZ))
                {
                    return existingId;
                }

            }
            catch(NumberFormatException e)
            {
                Bukkit.getLogger().warning("Corrupted coordinates for region: " + existingId + ". Skipping overlap check for this region");
            }
        }
        return null;
    }

    private boolean checkOverlap(int A_minX, int A_maxX, int B_minX, int B_maxX, int A_minZ, int A_maxZ, int B_minZ, int B_maxZ)
    {
        if(A_maxX < B_minX || B_maxX < A_minX)
        {
            return false;
        }
        if(A_maxZ < B_minZ || B_maxZ < A_minZ)
        {
            return false;
        }
        return true;
    }

    public void save()
    {
        try
        {
            this.config.save(this.file);
        }
        catch(Exception e1)
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
