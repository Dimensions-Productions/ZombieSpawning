package org.plugin.zombieSpawning;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;


public class Listener implements org.bukkit.event.Listener
{
    private final static Listener instance = new Listener();

    @EventHandler
    public void Spawn(EntitySpawnEvent event)
    {
        if(event.getEntity() instanceof Monster monster && !(monster instanceof Zombie))
        {
            event.setCancelled(true);
        }
    }

    public static Listener getInstance()
    {
        return instance;
    }
}
