package org.plugin.zombieSpawning;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

public class editMenu extends Menu
{
    @Position(16)
    private final Button refresh;

    @Position(10)
    private final Button grass;

    @Position(13)
    private final Button limit;

    @Position(12)
    private final Button increase;

    @Position(14)
    private final Button decrease;

    @Position(4)
    private final Button interval;

    @Position(3)
    private final Button increase2;

    @Position(5)
    private final Button decrease2;

    @Position(22)
    private final Button amount;

    @Position(21)
    private final Button increase3;

    @Position(23)
    private final Button decrease3;

    private final String regionId;
    private final boolean hasRegion;
    private final String world;
    private final int Climit;
    private final int Cinterval;
    private final int Camount;

    public editMenu(Player player)
    {
        setTitle("&f&lRegion edit");
        setSize(9*3);
        setAllowShift(false);

        this.regionId = ZombieSettings.getInstance().getRegionIdAtLocation(player.getLocation());
        this.hasRegion = (this.regionId != null);

        if(hasRegion)
        {
            this.world = ZombieSettings.getInstance().getWorld(regionId);
            this.Climit = ZombieSettings.getInstance().getLimit(regionId);
            this.Cinterval = ZombieSettings.getInstance().getInterval(regionId);
            this.Camount = ZombieSettings.getInstance().getAmount(regionId);
        }
        else
        {
            this.world = "None";
            this.Climit = -1;
            this.Cinterval = -1;
            this.Camount = -1;
        }

        this.refresh = new Button()
        {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType click)
            {
                Listener.getInstance().RestartRegionTask(regionId);
            }

            @Override
            public ItemStack getItem()
            {
                return ItemCreator.of(
                        CompMaterial.REDSTONE_TORCH,
                        "§lRefresh region task",
                        "",
                        "§lThis button is important!",
                        "§7Click this to refresh and apply changes",
                        "§7made to interval and amount!"
                ).glow(true).make();
            }
        };

        this.grass = new Button()
        {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType)
            {

            }

            @Override
            public ItemStack getItem()
            {
                return ItemCreator.of(
                        CompMaterial.GRASS_BLOCK,
                        "§lRegion Id: "+ (hasRegion ? regionId : "No Region"),
                        "",
                        "§7World: "+world
                ).glow(true).make();
            }
        };

        this.limit = new Button()
        {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType)
            {

            }

            @Override
            public ItemStack getItem()
            {
                return ItemCreator.of(
                        CompMaterial.ZOMBIE_HEAD,
                        "§lCurrent limit§r: "+Climit
                ).glow(true).make();
            }
        };

        this.increase = new Button()
        {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType)
            {
                if(Climit != -1)
                {
                    ZombieSettings.getInstance().setLimit(regionId, Climit+1);
                }
                else
                {
                    Bukkit.broadcastMessage("Limit was -1 for: " + regionId);
                }

                new editMenu(player).displayTo(player);
            }

            @Override
            public ItemStack getItem()
            {
                return ItemCreator.of(
                        CompMaterial.LIME_STAINED_GLASS_PANE,
                        "§lIncrease",
                        "",
                        "§7Click to increase §llimit§r of region"
                ).glow(true).make();
            }
        };

        this.decrease = new Button()
        {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType)
            {
                if(Climit != -1)
                {
                    ZombieSettings.getInstance().setLimit(regionId, Climit-1);
                }
                else
                {
                    Bukkit.broadcastMessage("Limit was -1 for: " + regionId);
                }

                new editMenu(player).displayTo(player);
            }

            @Override
            public ItemStack getItem()
            {
                return ItemCreator.of(
                        CompMaterial.RED_STAINED_GLASS_PANE,
                        "§lDecrease",
                        "",
                        "§7Click to decrease §llimit§r of region"
                ).glow(true).make();
            }
        };

        this.interval = new Button()
        {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType)
            {

            }

            @Override
            public ItemStack getItem()
            {
                return ItemCreator.of(
                        CompMaterial.ZOMBIE_HEAD,
                        "§lCurrent interval§r: "+Cinterval
                ).glow(true).make();
            }
        };

        this.increase2 = new Button()
        {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType)
            {
                if(Cinterval != -1)
                {
                    ZombieSettings.getInstance().setInterval(regionId, Cinterval+1);
                }
                else
                {
                    Bukkit.broadcastMessage("interval was -1 for: " + regionId);
                }

                new editMenu(player).displayTo(player);
            }

            @Override
            public ItemStack getItem()
            {
                return ItemCreator.of(
                        CompMaterial.LIME_STAINED_GLASS_PANE,
                        "§lIncrease",
                        "",
                        "§7Click to increase §linterval§r of region"
                ).glow(true).make();
            }
        };

        this.decrease2 = new Button()
        {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType)
            {
                if(Cinterval != -1)
                {
                    ZombieSettings.getInstance().setInterval(regionId, Cinterval-1);
                }
                else
                {
                    Bukkit.broadcastMessage("interval was -1 for: " + regionId);
                }

                new editMenu(player).displayTo(player);
            }

            @Override
            public ItemStack getItem()
            {
                return ItemCreator.of(
                        CompMaterial.RED_STAINED_GLASS_PANE,
                        "§lDecrease",
                        "",
                        "§7Click to decrease §linterval§r of region"
                ).glow(true).make();
            }
        };

        this.amount = new Button()
        {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType)
            {

            }

            @Override
            public ItemStack getItem()
            {
                return ItemCreator.of(
                        CompMaterial.ZOMBIE_HEAD,
                        "§lCurrent amount§r: "+Camount
                ).glow(true).make();
            }
        };

        this.increase3 = new Button()
        {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType)
            {
                if(Camount != -1)
                {
                    ZombieSettings.getInstance().setAmount(regionId, Camount+1);
                }
                else
                {
                    Bukkit.broadcastMessage("amount was -1 for: " + regionId);
                }

                new editMenu(player).displayTo(player);
            }

            @Override
            public ItemStack getItem()
            {
                return ItemCreator.of(
                        CompMaterial.LIME_STAINED_GLASS_PANE,
                        "§lIncrease",
                        "",
                        "§7Click to increase §lamount§r of region"
                ).glow(true).make();
            }
        };

        this.decrease3 = new Button()
        {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType)
            {
                if(Camount != -1)
                {
                    ZombieSettings.getInstance().setAmount(regionId, Camount-1);
                }
                else
                {
                    Bukkit.broadcastMessage("amount was -1 for: " + regionId);
                }

                new editMenu(player).displayTo(player);
            }

            @Override
            public ItemStack getItem()
            {
                return ItemCreator.of(
                        CompMaterial.RED_STAINED_GLASS_PANE,
                        "§lDecrease",
                        "",
                        "§7Click to decrease §lamount§r of region"
                ).glow(true).make();
            }
        };
    }
}
