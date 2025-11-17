package org.plugin.zombieSpawning;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class edit implements CommandExecutor, TabExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings)
    {
        if(!(commandSender instanceof Player player))
        {
            commandSender.sendMessage("Only players can run this command !");
            return true;
        }

        ItemStack itemStack = new ItemStack(Material.STICK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta != null)
        {
            itemMeta.setCustomModelData(786786);
            itemStack.setItemMeta(itemMeta);
            player.getInventory().addItem(itemStack);
        }
        else
        {
            player.sendMessage("failed to set customModelData");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings)
    {
        return List.of();
    }
}
