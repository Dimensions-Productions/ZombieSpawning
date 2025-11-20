package org.plugin.zombieSpawning;

import org.mineacademy.fo.plugin.SimplePlugin;

public final class ZombieSpawning extends SimplePlugin
{

    @Override
    public void onPluginStart()
    {
        Listener listener = Listener.getInstance();
        getServer().getPluginManager().registerEvents(listener, this);
        ZombieSettings.getInstance().load();
        getCommand("edit").setExecutor(new edit());
        getCommand("deleteregion").setExecutor(new deleteregion());
        getCommand("editregion").setExecutor(new editregion());
        Listener.getInstance().StartZombieRecount();
    }

    @Override
    public void onPluginStop() {

    }

    public static ZombieSpawning getInstance()
    {
        return getPlugin(ZombieSpawning.class);
    }
}
