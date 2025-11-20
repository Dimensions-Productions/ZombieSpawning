package org.plugin.zombieSpawning;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Listener implements org.bukkit.event.Listener
{
    private final static Listener instance = new Listener();

    private final Map<UUID, Location> loc1Save = new ConcurrentHashMap<>();
    private final Map<UUID, Location> loc2Save = new ConcurrentHashMap<>();

    private final Map<String, Integer> regionZombieCounts = new ConcurrentHashMap<>();
    private final Map<String, BukkitRunnable> regionTasks = new ConcurrentHashMap<>();
    private NamespacedKey regionKey;
    private final Random random = new Random();
    private boolean isRunning = false;

    public void recountZombies()
    {
        Map<String, java.util.List<Entity>> regionEntities = new HashMap<>();
        ZombieSettings settings = ZombieSettings.getInstance();

        for(World world : Bukkit.getWorlds())
        {
            for(Entity entity : world.getEntities())
            {
                if(entity.getType() == EntityType.ZOMBIE)
                {
                    String regionId = null;
                    if(entity.getPersistentDataContainer().has(regionKey, PersistentDataType.STRING))
                    {
                        regionId = entity.getPersistentDataContainer().get(regionKey, PersistentDataType.STRING);
                    }
                    else
                    {
                        regionId = settings.getRegionIdAtLocation(entity.getLocation());
                        if(regionId != null)
                        {
                            entity.getPersistentDataContainer().set(regionKey, PersistentDataType.STRING, regionId);
                        }
                    }

                    if(regionId != null)
                    {
                        regionEntities.putIfAbsent(regionId, new java.util.ArrayList<>());
                        regionEntities.get(regionId).add(entity);
                    }
                }
            }
        }

        regionZombieCounts.clear();

        for(Map.Entry<String, java.util.List<Entity>> entry : regionEntities.entrySet())
        {
            String regionId = entry.getKey();
            java.util.List<Entity> zombies = entry.getValue();
            int count = zombies.size();
            int limit = settings.getLimit(regionId);

            if(count > limit)
            {
                int toRemove = count - limit;
                //Bukkit.broadcastMessage("§c[Cleanup] Removing " + toRemove + " excess zombies from " + regionId);
                for(int i = 0; i < toRemove; i++)
                {
                    Entity zombieToRemove = zombies.get(i);
                    zombieToRemove.remove();
                }
                count = limit;
            }

            regionZombieCounts.put(regionId, count);
            //Bukkit.broadcastMessage("[Count]Current zombies: " + count + " In: " + regionId);
        }
    }

    public void StartZombieRecount()
    {
        if (isRunning) return;
        isRunning = true;
        this.regionKey = new NamespacedKey(ZombieSpawning.getInstance(), "region_id");
        ZombieSettings settings = ZombieSettings.getInstance();

        recountZombies();

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                recountZombies();
            }
        }.runTaskTimer(ZombieSpawning.getInstance(), 600L, 600L);

        for(String regionId : settings.getAllRegionIds())
        {
            StartRegionTask(regionId);
        }
    }

    public void StartRegionTask(String regionId)
    {
        ZombieSettings settings = ZombieSettings.getInstance();
        long interval = settings.getInterval(regionId);
        int spawnAmount = settings.getAmount(regionId);

        BukkitRunnable task = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if(!settings.isRegionSet(regionId))
                {
                    cancel();
                    return;
                }

                int limit = settings.getLimit(regionId);
                int current = regionZombieCounts.getOrDefault(regionId, 0);
                if(current == 0)
                {
                    //Bukkit.broadcastMessage("-->Recounting Zombies because current is 0");
                    recountZombies();
                    current = regionZombieCounts.getOrDefault(regionId, 0);
                }

                //Bukkit.broadcastMessage("[BeforeSpawn]Current zombies: " + current + " In: " + regionId);
                if(current < limit)
                {
                    int needed = limit - current;
                    int toSpawn = Math.min(needed, spawnAmount);
                    for(int i = 0; i < toSpawn; i++)
                    {
                        SpawnZombie(regionId, settings);
                    }
                }
            }
        };
        task.runTaskTimer(ZombieSpawning.getInstance(), interval, interval);
        regionTasks.put(regionId, task);
    }

    public void RestartRegionTask(String regionId)
    {
        regionTasks.get(regionId).cancel();
        StartRegionTask(regionId);
    }

    private void SpawnZombie(String regionId, ZombieSettings settings)
    {
        Location[] pos = settings.getRegionEdges(regionId);
        if (pos.length < 2 || pos[0] == null) return;

        World world = pos[0].getWorld();
        if (world == null) return;

        int x1 = pos[0].getBlockX();
        int z1 = pos[0].getBlockZ();
        int x2 = pos[1].getBlockX();
        int z2 = pos[1].getBlockZ();

        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        int x = random.nextInt(maxX - minX + 1) + minX;
        int z = random.nextInt(maxZ - minZ + 1) + minZ;

        if(!world.isChunkLoaded(x >> 4, z >> 4))
        {
            return;
        }

        int y = world.getHighestBlockYAt(x, z) + 1;

        Zombie zombie = (Zombie) world.spawnEntity(new Location(world, x + 0.5, y, z + 0.5), EntityType.ZOMBIE);
        zombie.setRemoveWhenFarAway(false);
        zombie.getPersistentDataContainer().set(regionKey, PersistentDataType.STRING, regionId);

        regionZombieCounts.merge(regionId, 1, Integer::sum);

        //Bukkit.broadcastMessage("[Spawn]Current zombies: " + regionZombieCounts.get(regionId) + " In: " + regionId);
    }

    @EventHandler
    public void OnEntityDeath(EntityDeathEvent event)
    {
        if (event.getEntity().getType() == EntityType.ZOMBIE && regionKey != null)
        {
            if (event.getEntity().getPersistentDataContainer().has(regionKey, PersistentDataType.STRING))
            {
                String regionId = event.getEntity().getPersistentDataContainer().get(regionKey, PersistentDataType.STRING);
                regionZombieCounts.computeIfPresent(regionId, (k, v) -> v > 0 ? v - 1 : 0);
                //Bukkit.broadcastMessage("[Death]Current zombies: " + regionZombieCounts.get(regionId) + " In: " + regionId);
            }
        }
    }

    @EventHandler
    public void PlayerMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        if(player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) return;
        Location from = event.getFrom();
        Location to = event.getTo();

        if(to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ())
        {
            return;
        }
        String fromRegionId = ZombieSettings.getInstance().getRegionIdAtLocation(from);
        String toRegionId = ZombieSettings.getInstance().getRegionIdAtLocation(to);

        if(toRegionId != null && (fromRegionId == null || !fromRegionId.equals(toRegionId)) && (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR))
        {
            String title = "Entered: " + toRegionId;
            player.sendTitle("", title);
            Location[] locations = ZombieSettings.getInstance().getRegionEdges(toRegionId);
            for(Location loc : locations)
            {
                World world = loc.getWorld();
                if (world == null) continue;
                int startY = loc.getBlockY() + 1;
                int max_y = 200;
                for(int y = startY; y <= max_y; y++)
                {
                    Block block = new Location(world, loc.getBlockX(), y, loc.getBlockZ()).getBlock();
                    if(block.getType() == Material.GLOWSTONE) continue;
                    final BlockData originalBlockData = block.getBlockData();
                    block.setType(Material.GLOWSTONE);

                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            block.setBlockData(originalBlockData, true);
                        }
                    }.runTaskLater(ZombieSpawning.getInstance(), 200L);
                }
            }
        }
        else if (fromRegionId != null && toRegionId == null)
        {
            String title = "Entered: Wilderness";
            player.sendTitle("", title);
        }
    }

    @EventHandler
    public void PlayerClick(PlayerInteractEvent event)
    {
        if(event.getHand() == EquipmentSlot.OFF_HAND) return;
        Player player = event.getPlayer();
        if(player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) return;
        UUID uuid = player.getUniqueId();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if(itemStack.getType() != Material.STICK) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return;
        if(!(itemMeta.hasCustomModelData())) return;
        if((itemMeta.getCustomModelData() != 786786)) return;
        event.setCancelled(true);
        Location loc;
        if(event.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            assert event.getClickedBlock() != null;
            loc = event.getClickedBlock().getLocation();
            loc1Save.put(uuid, loc);
            player.sendMessage("Location 1: x=" + loc.getX() + ", y=" + loc.getY());
        }
        else if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            assert event.getClickedBlock() != null;
            loc = event.getClickedBlock().getLocation();
            loc2Save.put(uuid, loc);
            player.sendMessage("Location 2: x=" + loc.getX() + ", y=" + loc.getY());
            player.sendMessage("Enter region name:");
        }
    }

    @EventHandler
    public void PlayerChat(AsyncPlayerChatEvent event)
    {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        if(loc1Save.containsKey(playerId) && loc2Save.containsKey(playerId))
        {
            String regionId = event.getMessage().trim();
            event.setCancelled(true);

            Location loc1 = loc1Save.get(playerId);
            Location loc2 = loc2Save.get(playerId);

            String worldName = loc1.getWorld().getName();
            int x1 = loc1.getBlockX();
            int z1 = loc1.getBlockZ();
            int x2 = loc2.getBlockX();
            int z2 = loc2.getBlockZ();
            String conflictingId = ZombieSettings.getInstance().isConflicting(regionId, worldName, x1, z1, x2, z2);

            if(conflictingId != null)
            {
                loc1Save.remove(playerId);
                loc2Save.remove(playerId);
                player.sendMessage("Creation failed, New region overlaps with existing region with id: " + conflictingId);
                return;
            }

            ZombieSettings.getInstance().createRegion(regionId, loc1.getWorld().getName(), loc1.getBlockX(), loc1.getBlockZ(), loc2.getBlockX(), loc2.getBlockZ(), 5, 100, 1);

            StartRegionTask(regionId);

            loc1Save.remove(playerId);
            loc2Save.remove(playerId);

            player.sendMessage("Region " + regionId + " created");
        }
    }

    public static Listener getInstance()
    {
        return instance;
    }
}