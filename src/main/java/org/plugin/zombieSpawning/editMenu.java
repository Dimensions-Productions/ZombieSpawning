//package org.plugin.zombieSpawning;
//
//import org.bukkit.Sound;
//import org.bukkit.entity.Player;
//import org.bukkit.event.inventory.ClickType;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.permissions.PermissionAttachment;
//import org.mineacademy.fo.menu.Menu;
//import org.mineacademy.fo.menu.button.Button;
//import org.mineacademy.fo.menu.button.annotation.Position;
//import org.mineacademy.fo.menu.model.ItemCreator;
//import org.mineacademy.fo.remain.CompMaterial;
//
//
//public class UserClassSelectMenu extends Menu
//{
//
//    @Position(11)
//    private final Button water;
//
//    @Position(15)
//    private final Button fire;
//
//    @Position(33)
//    private final Button wind;
//
//    @Position(29)
//    private final Button ground;
//
//    public UserClassSelectMenu()
//    {
//        setTitle("&f&lMage Upgrade");
//        setSize(9*5);
//        setAllowShift(false);
//
//        this.water = new Button()
//        {
//            @Override
//            public void onClickedInMenu(Player player, Menu menu, ClickType clickType)
//            {
//                PermissionAttachment attachment = player.addAttachment(MageBase.getInstance());
//                attachment.setPermission("magebase.class.fire", false);
//                attachment.setPermission("magebase.class.wind", false);
//                attachment.setPermission("magebase.class.water", true);
//                attachment.setPermission("magebase.class.ground", false);
//                attachment.setPermission("magebase.class.summoning", false);
//
//                PlayerSettings.getInstance().SetPlayerClass(player.getUniqueId(), "water");
//                player.sendMessage("You are now a §b§lWater Mage");
//                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2f, 0.7f);
//            }
//
//            @Override
//            public ItemStack getItem()
//            {
//                return ItemCreator.of(
//                        CompMaterial.WATER_BUCKET,
//                        "§b§lWater",
//                        "",
//                        "§7Power to control water and wield it",
//                        "§7as you see fit"
//                ).glow(true).make();
//            }
//        };
//
//        this.fire = new Button()
//        {
//            @Override
//            public void onClickedInMenu(Player player, Menu menu, ClickType clickType)
//            {
//                PermissionAttachment attachment = player.addAttachment(MageBase.getInstance());
//                attachment.setPermission("magebase.class.water", false);
//                attachment.setPermission("magebase.class.wind", false);
//                attachment.setPermission("magebase.class.fire", true);
//                attachment.setPermission("magebase.class.ground", false);
//                attachment.setPermission("magebase.class.summoning", false);
//
//                PlayerSettings.getInstance().SetPlayerClass(player.getUniqueId(), "fire");
//                player.sendMessage("You are now a §4§lFire Mage");
//                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2f, 0.7f);
//            }
//
//            @Override
//            public ItemStack getItem()
//            {
//                return ItemCreator.of(
//                        CompMaterial.LAVA_BUCKET,
//                        "§4§lFire",
//                        "",
//                        "§7Power to manipulate fire and use it",
//                        "§7to incinerate anything and anyone"
//                ).glow(true).make();
//            }
//        };
//
//        this.wind = new Button()
//        {
//            @Override
//            public void onClickedInMenu(Player player, Menu menu, ClickType clickType)
//            {
//                PermissionAttachment attachment = player.addAttachment(MageBase.getInstance());
//                attachment.setPermission("magebase.class.water", false);
//                attachment.setPermission("magebase.class.fire", false);
//                attachment.setPermission("magebase.class.wind", true);
//                attachment.setPermission("magebase.class.ground", false);
//                attachment.setPermission("magebase.class.summoning", false);
//
//                PlayerSettings.getInstance().SetPlayerClass(player.getUniqueId(), "wind");
//                player.sendMessage("You are now a §lWind Mage");
//                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2f, 0.7f);
//            }
//
//            @Override
//            public ItemStack getItem()
//            {
//                return ItemCreator.of(
//                        CompMaterial.WIND_CHARGE,
//                        "§lWind",
//                        "",
//                        "§7TPower to exploit air and utilize it",
//                        "§7to aid your work"
//                ).glow(true).make();
//            }
//        };
//
//        this.ground = new Button()
//        {
//            @Override
//            public void onClickedInMenu(Player player, Menu menu, ClickType clickType)
//            {
//                PermissionAttachment attachment = player.addAttachment(MageBase.getInstance());
//                attachment.setPermission("magebase.class.water", false);
//                attachment.setPermission("magebase.class.fire", false);
//                attachment.setPermission("magebase.class.wind", false);
//                attachment.setPermission("magebase.class.ground", true);
//                attachment.setPermission("magebase.class.summoning", false);
//
//                PlayerSettings.getInstance().SetPlayerClass(player.getUniqueId(), "ground");
//                player.sendMessage("You are now a §6§lGround Mage");
//                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2f, 0.7f);
//            }
//
//            @Override
//            public ItemStack getItem()
//            {
//                return ItemCreator.of(
//                        CompMaterial.STONE,
//                        "§6§lGround",
//                        "",
//                        "§7Power to influence the very earth and",
//                        "§7bend it to your will"
//                ).glow(true).make();
//            }
//        };
//    }
//}
