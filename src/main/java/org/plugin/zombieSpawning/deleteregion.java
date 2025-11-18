package org.plugin.zombieSpawning;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class deleteregion implements CommandExecutor, TabExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings)
    {
        if(!(commandSender instanceof Player player))
        {
            commandSender.sendMessage("Only players can run this command !");
            return true;
        }

        Location playerLoc = player.getLocation();
        String regionId = ZombieSettings.getInstance().getRegionIdAtLocation(playerLoc);
        ZombieSettings.getInstance().deleteRegion(regionId);
        player.sendMessage("Deleted region: " + regionId);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings)
    {
        return List.of();
    }
}
