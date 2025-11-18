package org.plugin.zombieSpawning;

import org.bukkit.plugin.java.JavaPlugin;

public final class ZombieSpawning extends JavaPlugin {

    @Override
    public void onEnable()
    {
        Listener listener = Listener.getInstance();
        getServer().getPluginManager().registerEvents(listener, this);
        ZombieSettings.getInstance().load();
        getCommand("edit").setExecutor(new edit());
        getCommand("deleteregion").setExecutor(new deleteregion());
        Listener.getInstance().StartZombieRecount();
    }

    @Override
    public void onDisable() {

    }

    public static ZombieSpawning getInstance()
    {
        return getPlugin(ZombieSpawning.class);
    }
}
