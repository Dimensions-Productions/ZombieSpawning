package org.plugin.zombieSpawning;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Listener implements org.bukkit.event.Listener
{
    private final static Listener instance = new Listener();
    private final Map<UUID, Location> loc1Save = new ConcurrentHashMap<>();
    private final Map<UUID, Location> loc2Save = new ConcurrentHashMap<>();

    @EventHandler
    public void PlayerMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ())
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
        UUID uuid = player.getUniqueId();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return;
        if((itemStack.getType() != Material.STICK) && (itemMeta.getCustomModelData() != 786786)) return;
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

            ZombieSettings.getInstance().createRegion(regionId, loc1.getWorld().getName(), loc1.getBlockX(), loc1.getBlockZ(), loc2.getBlockX(), loc2.getBlockZ(), 5);
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
