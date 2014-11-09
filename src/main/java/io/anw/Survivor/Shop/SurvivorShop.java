package io.anw.Survivor.Shop;

import io.anw.Core.Bukkit.Utils.Chat.MessageUtils;
import io.anw.Core.Bukkit.Utils.Chat.StringUtils;
import io.anw.Core.Bukkit.Utils.ItemStack.ItemUtils;
import io.anw.Core.Bukkit.Utils.Misc.SoundPlayer;
import io.anw.Core.Bukkit.Utils.Objects.InventoryMenu;
import io.anw.Core.Bukkit.Utils.Objects.Particle;
import io.anw.Core.Bukkit.Utils.Purchases.IPurchase;
import io.anw.Core.Bukkit.Utils.Purchases.PendingPurchase;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import io.anw.Survivor.Listeners.Game.Sessions.Ability.AbilityProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Ability.AbilityType;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Boots.BootsProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Boots.BootsType;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Boots.MutantBootsProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Chestplate.ChestplateProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Chestplate.ChestplateType;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Chestplate.MutantChestplateProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Helmet.HelmetProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Helmet.HelmetType;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Helmet.MutantHelmetProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Leggings.LeggingsProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Leggings.LeggingsType;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Leggings.MutantLeggingsProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Models.ModelProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Models.ModelType;
import io.anw.Survivor.Listeners.Game.Sessions.Weapons.MutantWeaponProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Weapons.WeaponProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Weapons.WeaponType;
import io.anw.Survivor.Utils.SQL.DatabaseManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class SurvivorShop implements Listener {

    // yay finally we used a proper way to create + reference inventories!
    private static final InventoryMenu mainMenu = new InventoryMenu(StringUtils.colorize("&6&lSURVIVOR &8» &7Choose Team"), 1)
            .setItem(2, ItemUtils.createItemStack(
                    "&e&lSurvivor",
                    Arrays.asList("&7Create a Class for the Survivor team!"),
                    Material.BOW
            ))
            .setItem(6, ItemUtils.createItemStack(
                    "&e&lMutant",
                    Arrays.asList("&7Create a Class for the Mutant team!"),
                    Material.ROTTEN_FLESH
            ));

    private static final InventoryMenu survivorClassMenu = new InventoryMenu(StringUtils.colorize("&6&lSURVIVOR &8» &7Survivor"), 1)
            .setItem(2, ItemUtils.createItemStack(
                    "&e&lWeapons",
                    Arrays.asList("&7Choose a weapon for your class!"),
                    Material.DIAMOND_SWORD
            ))
            .setItem(4, ItemUtils.createItemStack(
                    "&e&lArmor",
                    Arrays.asList("&7Pick some armor for your class!"),
                    Material.IRON_CHESTPLATE
            ))
            .setItem(6, ItemUtils.createItemStack(
                    "&e&lAbilities",
                    Arrays.asList("&7Choose your special ability for your class!"),
                    Material.BLAZE_POWDER
            ));

    private static final InventoryMenu mutantClassMenu = new InventoryMenu(StringUtils.colorize("&6&lSURVIVOR &8» &7Mutant"), 1)
            .setItem(2, ItemUtils.createItemStack(
                    "&e&lWeapons",
                    Arrays.asList("&7Choose a weapon for your class!"),
                    Material.DIAMOND_SWORD
            ))
            .setItem(4, ItemUtils.createItemStack(
                    "&e&lArmor",
                    Arrays.asList("&7Pick some armor for your class!"),
                    Material.IRON_CHESTPLATE
            ))
            .setItem(6, ItemUtils.createItemStack(
                    "&e&lModels",
                    Arrays.asList("&7Choose a disguise model to use for your class!"),
                    Material.BLAZE_POWDER
            ));

    private static final InventoryMenu armorMenu = new InventoryMenu(StringUtils.colorize("&6&lSURVIVOR &8» &7Armor"), 1)
            .setItem(1, ItemUtils.createItemStack(
                    "&e&lHelmets",
                    Arrays.asList("&7Choose a helmet for your class' armor!"),
                    Material.GOLD_HELMET
            ))
            .setItem(3, ItemUtils.createItemStack(
                    "&e&lChestplates",
                    Arrays.asList("&7Choose a chestplate for your class' armor!"),
                    Material.IRON_CHESTPLATE
            ))
            .setItem(5, ItemUtils.createItemStack(
                    "&e&lLeggings",
                    Arrays.asList("&7Choose leggings for your class' armor!"),
                    Material.CHAINMAIL_LEGGINGS
            ))
            .setItem(7, ItemUtils.createItemStack(
                    "&e&lBoots",
                    Arrays.asList("&7Choose boots for your class' armor!"),
                    Material.DIAMOND_BOOTS
            ));

    private static final InventoryMenu mutantArmorMenu = new InventoryMenu(StringUtils.colorize("&6&lMUTANT &8» &7Armor"), 1)
            .setItem(1, ItemUtils.createItemStack(
                    "&e&lHelmets",
                    Arrays.asList("&7Choose a helmet for your class' armor!"),
                    Material.GOLD_HELMET
            ))
            .setItem(3, ItemUtils.createItemStack(
                    "&e&lChestplates",
                    Arrays.asList("&7Choose a chestplate for your class' armor!"),
                    Material.IRON_CHESTPLATE
            ))
            .setItem(5, ItemUtils.createItemStack(
                    "&e&lLeggings",
                    Arrays.asList("&7Choose leggings for your class' armor!"),
                    Material.CHAINMAIL_LEGGINGS
            ))
            .setItem(7, ItemUtils.createItemStack(
                    "&e&lBoots",
                    Arrays.asList("&7Choose boots for your class' armor!"),
                    Material.DIAMOND_BOOTS
            ));

    public static void openSurvivorWeaponsMenu(Player player) {
        InventoryMenu menu = new InventoryMenu(StringUtils.colorize("&6&lSURVIVOR &8» &7Weapons"), 6);
        for (WeaponType item : WeaponType.values()) {
            ItemStack stack = item.getItemStack();
            ItemMeta meta = item.getItemStack().getItemMeta();
            meta.setDisplayName(WeaponProfile.getProfile(UUIDUtility.getUUID(player.getName())).hasItem(item) ? StringUtils.colorize("&a&lEQUIP &r") + meta.getDisplayName() : StringUtils.colorize("&6&lBUY &r") + meta.getDisplayName());
            stack.setItemMeta(meta);
            menu.addItem(stack);
        }
        for (int x = 45; x < 54; x++) {
            if (x >= 48 && x <= 50) {
                menu.setItem(x, ItemUtils.createItemStack("&c&lBack", Arrays.asList("&7Return to the main Create a Class menu!"), Material.REDSTONE));
            } else {
                ItemStack set = ItemUtils.createItemStack("&0", Material.STAINED_GLASS_PANE);
                set.setDurability((short) 15);
                menu.setItem(x, set);
            }
        }

        menu.open(player);
        SoundPlayer.play(player, Sound.NOTE_PLING, 25);
        Particle.LAVA.play(player, player.getLocation(), (float) Math.random());
    }

    public static void openMutantWeaponsMenu(Player player) {
        InventoryMenu menu = new InventoryMenu(StringUtils.colorize("&6&lMUTANT &8» &7Weapons"), 6);
        for (WeaponType item : WeaponType.values()) {
            ItemStack stack = item.getItemStack();
            ItemMeta meta = item.getItemStack().getItemMeta();
            meta.setDisplayName(MutantWeaponProfile.getProfile(UUIDUtility.getUUID(player.getName())).hasItem(item) ? StringUtils.colorize("&a&lEQUIP &r") + meta.getDisplayName() : StringUtils.colorize("&6&lBUY &r") + meta.getDisplayName());
            stack.setItemMeta(meta);
            menu.addItem(stack);
        }
        for (int x = 45; x < 54; x++) {
            if (x >= 48 && x <= 50) {
                menu.setItem(x, ItemUtils.createItemStack("&c&lBack", Arrays.asList("&7Return to the main Create a Class menu!"), Material.REDSTONE));
            } else {
                ItemStack set = ItemUtils.createItemStack("&0", Material.STAINED_GLASS_PANE);
                set.setDurability((short) 15);
                menu.setItem(x, set);
            }
        }

        menu.open(player);
        SoundPlayer.play(player, Sound.NOTE_PLING, 25);
        Particle.LAVA.play(player, player.getLocation(), (float) Math.random());
    }

    public static void openAbilitiesMenu(Player player) {
        InventoryMenu menu = new InventoryMenu(StringUtils.colorize("&6&lSURVIVOR &8» &7Abilities"), 6);
        for (AbilityType item : AbilityType.values()) {
            ItemStack stack = item.getItemStack();
            ItemMeta meta = item.getItemStack().getItemMeta();
            meta.setDisplayName(AbilityProfile.getProfile(UUIDUtility.getUUID(player.getName())).hasItem(item) ? StringUtils.colorize("&a&lEQUIP &r") + meta.getDisplayName() : StringUtils.colorize("&6&lBUY &r") + meta.getDisplayName());
            stack.setItemMeta(meta);
            menu.addItem(stack);
        }
        for (int x = 45; x < 54; x++) {
            if (x >= 48 && x <= 50) {
                menu.setItem(x, ItemUtils.createItemStack("&c&lBack", Arrays.asList("&7Return to the main Create a Class menu!"), Material.REDSTONE));
            } else {
                ItemStack set = ItemUtils.createItemStack("&0", Material.STAINED_GLASS_PANE);
                set.setDurability((short) 15);
                menu.setItem(x, set);
            }
        }

        menu.open(player);
        SoundPlayer.play(player, Sound.NOTE_PLING, 25);
        Particle.LAVA.play(player, player.getLocation(), (float) Math.random());
    }

    public static void openModelsMenu(Player player) {
        InventoryMenu menu = new InventoryMenu(StringUtils.colorize("&6&lMUTANT &8» &7Models"), 6);
        for (ModelType type : ModelType.values()) {
            ItemStack stack = type.getItemStack();
            ItemMeta meta = type.getItemStack().getItemMeta();
            meta.setDisplayName(ModelProfile.getProfile(UUIDUtility.getUUID(player.getName())).hasModel(type) ? StringUtils.colorize("&a&lEQUIP &r") + meta.getDisplayName() : StringUtils.colorize("&6&lBUY &r") + meta.getDisplayName());
            stack.setItemMeta(meta);
            menu.addItem(stack);
        }
        for (int x = 45; x < 54; x++) {
            if (x >= 48 && x <= 50) {
                menu.setItem(x, ItemUtils.createItemStack("&c&lBack", Arrays.asList("&7Return to the main Create a Class menu!"), Material.REDSTONE));
            } else {
                ItemStack set = ItemUtils.createItemStack("&0", Material.STAINED_GLASS_PANE);
                set.setDurability((short) 15);
                menu.setItem(x, set);
            }
        }

        menu.open(player);
        SoundPlayer.play(player, Sound.NOTE_PLING, 25);
        Particle.LAVA.play(player, player.getLocation(), (float) Math.random());
    }

    public static void openResurrectionScroll(Player player) {
        InventoryMenu menu = new InventoryMenu(StringUtils.colorize("&6&lResurrection Scroll"), 2);

        ItemStack resurrectionScroll = ItemUtils.createItemStack("&c&lResurrection Scroll",
                Arrays.asList(
                        StringUtils.colorize("&cUse your resurrection scroll to resurrect temporaily"),
                        StringUtils.colorize("&cas a Survivor! You will be transformed into a Survivor,"),
                        StringUtils.colorize("&cand will have only 30 seconds to kill another Mutant and"),
                        StringUtils.colorize("return to the Survivor team."),
                        " ",
                        StringUtils.colorize("&cBe careful! Do your task quickly, or else you will be caught"),
                        StringUtils.colorize("&cby the god of the Mutants and remain a Mutant for the remainder"),
                        StringUtils.colorize("&cof the game! Decide your fate."),
                        " ",
                        StringUtils.colorize("&8&l> &e&l4500 &7Tokens!"),
                        StringUtils.colorize("&a&lAMOUNT OWNED: &b" + DatabaseManager.getInstance().getResurrectionScrolls(UUIDUtility.getUUID(player.getName())))),
                Material.PAPER, DatabaseManager.getInstance().getResurrectionScrolls(UUIDUtility.getUUID(player.getName())));

        menu.setItem(8, resurrectionScroll);
        menu.open(player);
        SoundPlayer.play(player, Sound.NOTE_PLING, 25);
    }

    public static void openSurvivorHelmetMenu(Player player) {
        InventoryMenu menu = new InventoryMenu(StringUtils.colorize("&6&lSURVIVOR &8» &7Helmets"), 6);
        for (HelmetType item : HelmetType.values()) {
            ItemStack stack = item.getItemStack();
            ItemMeta meta = item.getItemStack().getItemMeta();
            meta.setDisplayName(HelmetProfile.getProfile(UUIDUtility.getUUID(player.getName())).hasItem(item) ? StringUtils.colorize("&a&lEQUIP &r") + meta.getDisplayName() : StringUtils.colorize("&6&lBUY &r") + meta.getDisplayName());
            stack.setItemMeta(meta);
            menu.addItem(stack);
        }
        for (int x = 45; x < 54; x++) {
            if (x >= 48 && x <= 50) {
                menu.setItem(x, ItemUtils.createItemStack("&c&lBack", Arrays.asList("&7Return to the main Create a Class menu!"), Material.REDSTONE));
            } else {
                ItemStack set = ItemUtils.createItemStack("&0", Material.STAINED_GLASS_PANE);
                set.setDurability((short) 15);
                menu.setItem(x, set);
            }
        }

        menu.open(player);
        SoundPlayer.play(player, Sound.NOTE_PLING, 25);
        Particle.LAVA.play(player, player.getLocation(), (float) Math.random());
    }

    public static void openMutantHelmetMenu(Player player) {
        InventoryMenu menu = new InventoryMenu(StringUtils.colorize("&6&lMUTANT &8» &7Helmets"), 6);
        for (HelmetType item : HelmetType.values()) {
            ItemStack stack = item.getItemStack();
            ItemMeta meta = item.getItemStack().getItemMeta();
            meta.setDisplayName(MutantHelmetProfile.getProfile(UUIDUtility.getUUID(player.getName())).hasItem(item) ? StringUtils.colorize("&a&lEQUIP &r") + meta.getDisplayName() : StringUtils.colorize("&6&lBUY &r") + meta.getDisplayName());
            stack.setItemMeta(meta);
            menu.addItem(stack);
        }
        for (int x = 45; x < 54; x++) {
            if (x >= 48 && x <= 50) {
                menu.setItem(x, ItemUtils.createItemStack("&c&lBack", Arrays.asList("&7Return to the main Create a Class menu!"), Material.REDSTONE));
            } else {
                ItemStack set = ItemUtils.createItemStack("&0", Material.STAINED_GLASS_PANE);
                set.setDurability((short) 15);
                menu.setItem(x, set);
            }
        }

        menu.open(player);
        SoundPlayer.play(player, Sound.NOTE_PLING, 25);
        Particle.LAVA.play(player, player.getLocation(), (float) Math.random());
    }

    public static void openSurvivorChestplateMenu(Player player) {
        InventoryMenu menu = new InventoryMenu(StringUtils.colorize("&6&lSURVIVOR &8» &7Chestplates"), 6);
        for (ChestplateType item : ChestplateType.values()) {
            ItemStack stack = item.getItemStack();
            ItemMeta meta = item.getItemStack().getItemMeta();
            meta.setDisplayName(ChestplateProfile.getProfile(UUIDUtility.getUUID(player.getName())).hasItem(item) ? StringUtils.colorize("&a&lEQUIP &r") + meta.getDisplayName() : StringUtils.colorize("&6&lBUY &r") + meta.getDisplayName());
            stack.setItemMeta(meta);
            menu.addItem(stack);
        }
        for (int x = 45; x < 54; x++) {
            if (x >= 48 && x <= 50) {
                menu.setItem(x, ItemUtils.createItemStack("&c&lBack", Arrays.asList("&7Return to the main Create a Class menu!"), Material.REDSTONE));
            } else {
                ItemStack set = ItemUtils.createItemStack("&0", Material.STAINED_GLASS_PANE);
                set.setDurability((short) 15);
                menu.setItem(x, set);
            }
        }

        menu.open(player);
        SoundPlayer.play(player, Sound.NOTE_PLING, 25);
        Particle.LAVA.play(player, player.getLocation(), (float) Math.random());
    }

    public static void openMutantChestplateMenu(Player player) {
        InventoryMenu menu = new InventoryMenu(StringUtils.colorize("&6&lMUTANT &8» &7Chestplates"), 6);
        for (ChestplateType item : ChestplateType.values()) {
            ItemStack stack = item.getItemStack();
            ItemMeta meta = item.getItemStack().getItemMeta();
            meta.setDisplayName(MutantChestplateProfile.getProfile(UUIDUtility.getUUID(player.getName())).hasItem(item) ? StringUtils.colorize("&a&lEQUIP &r") + meta.getDisplayName() : StringUtils.colorize("&6&lBUY &r") + meta.getDisplayName());
            stack.setItemMeta(meta);
            menu.addItem(stack);
        }
        for (int x = 45; x < 54; x++) {
            if (x >= 48 && x <= 50) {
                menu.setItem(x, ItemUtils.createItemStack("&c&lBack", Arrays.asList("&7Return to the main Create a Class menu!"), Material.REDSTONE));
            } else {
                ItemStack set = ItemUtils.createItemStack("&0", Material.STAINED_GLASS_PANE);
                set.setDurability((short) 15);
                menu.setItem(x, set);
            }
        }

        menu.open(player);
        SoundPlayer.play(player, Sound.NOTE_PLING, 25);
        Particle.LAVA.play(player, player.getLocation(), (float) Math.random());
    }

    public static void openSurvivorLeggingsMenu(Player player) {
        InventoryMenu menu = new InventoryMenu(StringUtils.colorize("&6&lSURVIVOR &8» &7Leggings"), 6);
        for (LeggingsType item : LeggingsType.values()) {
            ItemStack stack = item.getItemStack();
            ItemMeta meta = item.getItemStack().getItemMeta();
            meta.setDisplayName(LeggingsProfile.getProfile(UUIDUtility.getUUID(player.getName())).hasItem(item) ? StringUtils.colorize("&a&lEQUIP &r") + meta.getDisplayName() : StringUtils.colorize("&6&lBUY &r") + meta.getDisplayName());
            stack.setItemMeta(meta);
            menu.addItem(stack);
        }
        for (int x = 45; x < 54; x++) {
            if (x >= 48 && x <= 50) {
                menu.setItem(x, ItemUtils.createItemStack("&c&lBack", Arrays.asList("&7Return to the main Create a Class menu!"), Material.REDSTONE));
            } else {
                ItemStack set = ItemUtils.createItemStack("&0", Material.STAINED_GLASS_PANE);
                set.setDurability((short) 15);
                menu.setItem(x, set);
            }
        }

        menu.open(player);
        SoundPlayer.play(player, Sound.NOTE_PLING, 25);
        Particle.LAVA.play(player, player.getLocation(), (float) Math.random());
    }

    public static void openMutantLeggingsMenu(Player player) {
        InventoryMenu menu = new InventoryMenu(StringUtils.colorize("&6&lMUTANT &8» &7Leggings"), 6);
        for (LeggingsType item : LeggingsType.values()) {
            ItemStack stack = item.getItemStack();
            ItemMeta meta = item.getItemStack().getItemMeta();
            meta.setDisplayName(MutantLeggingsProfile.getProfile(UUIDUtility.getUUID(player.getName())).hasItem(item) ? StringUtils.colorize("&a&lEQUIP &r") + meta.getDisplayName() : StringUtils.colorize("&6&lBUY &r") + meta.getDisplayName());
            stack.setItemMeta(meta);
            menu.addItem(stack);
        }
        for (int x = 45; x < 54; x++) {
            if (x >= 48 && x <= 50) {
                menu.setItem(x, ItemUtils.createItemStack("&c&lBack", Arrays.asList("&7Return to the main Create a Class menu!"), Material.REDSTONE));
            } else {
                ItemStack set = ItemUtils.createItemStack("&0", Material.STAINED_GLASS_PANE);
                set.setDurability((short) 15);
                menu.setItem(x, set);
            }
        }

        menu.open(player);
        SoundPlayer.play(player, Sound.NOTE_PLING, 25);
        Particle.LAVA.play(player, player.getLocation(), (float) Math.random());
    }

    public static void openSurvivorBootsMenu(Player player) {
        InventoryMenu menu = new InventoryMenu(StringUtils.colorize("&6&lSURVIVOR &8» &7Boots"), 6);
        for (BootsType item : BootsType.values()) {
            ItemStack stack = item.getItemStack();
            ItemMeta meta = item.getItemStack().getItemMeta();
            meta.setDisplayName(BootsProfile.getProfile(UUIDUtility.getUUID(player.getName())).hasItem(item) ? StringUtils.colorize("&a&lEQUIP &r") + meta.getDisplayName() : StringUtils.colorize("&6&lBUY &r") + meta.getDisplayName());
            stack.setItemMeta(meta);
            menu.addItem(stack);
        }
        for (int x = 45; x < 54; x++) {
            if (x >= 48 && x <= 50) {
                menu.setItem(x, ItemUtils.createItemStack("&c&lBack", Arrays.asList("&7Return to the main Create a Class menu!"), Material.REDSTONE));
            } else {
                ItemStack set = ItemUtils.createItemStack("&0", Material.STAINED_GLASS_PANE);
                set.setDurability((short) 15);
                menu.setItem(x, set);
            }
        }

        menu.open(player);
        SoundPlayer.play(player, Sound.NOTE_PLING, 25);
        Particle.LAVA.play(player, player.getLocation(), (float) Math.random());
    }

    public static void openMutantBootsMenu(Player player) {
        InventoryMenu menu = new InventoryMenu(StringUtils.colorize("&6&lSURVIVOR &8» &7Boots"), 6);
        for (BootsType item : BootsType.values()) {
            ItemStack stack = item.getItemStack();
            ItemMeta meta = item.getItemStack().getItemMeta();
            meta.setDisplayName(MutantBootsProfile.getProfile(UUIDUtility.getUUID(player.getName())).hasItem(item) ? StringUtils.colorize("&a&lEQUIP &r") + meta.getDisplayName() : StringUtils.colorize("&6&lBUY &r") + meta.getDisplayName());
            stack.setItemMeta(meta);
            menu.addItem(stack);
        }
        for (int x = 45; x < 54; x++) {
            if (x >= 48 && x <= 50) {
                menu.setItem(x, ItemUtils.createItemStack("&c&lBack", Arrays.asList("&7Return to the main Create a Class menu!"), Material.REDSTONE));
            } else {
                ItemStack set = ItemUtils.createItemStack("&0", Material.STAINED_GLASS_PANE);
                set.setDurability((short) 15);
                menu.setItem(x, set);
            }
        }

        menu.open(player);
        SoundPlayer.play(player, Sound.NOTE_PLING, 25);
        Particle.LAVA.play(player, player.getLocation(), (float) Math.random());
    }

    @EventHandler
    public void openClassMenu(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        try {
            if (e.getItem() != null && e.getItem().getItemMeta().getDisplayName().contains("Create a Class") && e.getItem().getType() == Material.CHEST) {
                if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
                mainMenu.open(player);
                SoundPlayer.play(player, Sound.NOTE_PLING, 25);
            }
        } catch (NullPointerException ignored) { }
    }

    @EventHandler
    public void openResurrectionScrollMenu(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        try {
            if (e.getItem() != null && e.getItem().getItemMeta().getDisplayName().contains("Resurrection Scroll") && e.getItem().getType() == Material.PAPER) {
                if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
                openResurrectionScroll(player);
                SoundPlayer.play(player, Sound.NOTE_PLING, 25);
            }
        } catch (NullPointerException ignored) { }
    }

    @EventHandler
    public void mainMenuClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player player = (Player) e.getWhoClicked();
            if (e.getInventory().getTitle().contains("Categories")) {
                if (e.getCurrentItem().hasItemMeta()) {
                    e.setCancelled(true);
                    SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 25);

                    if (e.getRawSlot() == 2) {
                        survivorClassMenu.open(player);
                    } else if (e.getRawSlot() == 6) {
                        mutantClassMenu.open(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void survivorMenuClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player player = (Player) e.getWhoClicked();
            if (e.getInventory().getTitle().contains("Survivor")) {
                if (e.getCurrentItem().hasItemMeta()) {
                    e.setCancelled(true);
                    SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 25);

                    if (e.getRawSlot() == 2) {
                        openSurvivorWeaponsMenu(player);
                    } else if (e.getRawSlot() == 4) {
                        armorMenu.open(player);
                    } else if (e.getRawSlot() == 6) {
                        openAbilitiesMenu(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void mutantMenuClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player player = (Player) e.getWhoClicked();
            if (e.getInventory().getTitle().contains("Mutant")) {
                if (e.getCurrentItem().hasItemMeta()) {
                    e.setCancelled(true);
                    SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 25);

                    if (e.getRawSlot() == 2) {
                        openMutantWeaponsMenu(player);
                    } else if (e.getRawSlot() == 4) {
                        mutantArmorMenu.open(player);
                    } else if (e.getRawSlot() == 6) {
                        openModelsMenu(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void armorMenuClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player player = (Player) e.getWhoClicked();
            if (e.getInventory().getTitle().contains("&6&lSURVIVOR &8» &7Armor")) {
                if (e.getCurrentItem().hasItemMeta()) {
                    e.setCancelled(true);
                    SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 25);

                    if (e.getRawSlot() == 1) {
                        openSurvivorHelmetMenu(player);
                    } else if (e.getRawSlot() == 3) {
                        openSurvivorChestplateMenu(player);
                    } else if (e.getRawSlot() == 5) {
                        openSurvivorLeggingsMenu(player);
                    } else if (e.getRawSlot() == 7) {
                        openSurvivorBootsMenu(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void mutantArmorClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player player = (Player) e.getWhoClicked();
            if (e.getInventory().getTitle().contains("&6&lMUTANT &8» &7Armor")) {
                if (e.getCurrentItem().hasItemMeta()) {
                    e.setCancelled(true);
                    SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 25);

                    if (e.getRawSlot() == 1) {
                        openMutantHelmetMenu(player);
                    } else if (e.getRawSlot() == 3) {
                        openMutantChestplateMenu(player);
                    } else if (e.getRawSlot() == 5) {
                        openMutantLeggingsMenu(player);
                    } else if (e.getRawSlot() == 7) {
                        openMutantBootsMenu(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void weaponClick(final InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            final Player player = (Player) e.getWhoClicked();
            if (e.getInventory().getTitle().contains("&6&lSURVIVOR &8» &7Weapons")) {
                if (e.getCurrentItem().hasItemMeta()) {
                    e.setCancelled(true);
                    SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 25);

                    for (final WeaponType item : WeaponType.values()) {
                        if (WeaponType.getItemByItemStack(e.getCurrentItem()) == item) {
                            final WeaponProfile profile = WeaponProfile.getProfile(UUIDUtility.getUUID(player.getName()));
                            if (profile.hasItem(item)) {
                                DatabaseManager.getInstance().setWeapon(UUIDUtility.getUUID(player.getName()), WeaponType.valueOf(item.name()));
                                MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Equipped weapon &6" + ChatColor.stripColor(item.getName()) + " &7for your &6Survivor &7class!");

                                player.closeInventory();
                            } else {
                                if (DatabaseManager.getTokenAPI().getTokens(profile.getPlayer()) >= item.getCost()) {
                                    new PendingPurchase(player, new IPurchase() {
                                        @Override
                                        public void onPurchase() {
                                            DatabaseManager.getTokenAPI().removeTokens(profile.getPlayer(), item.getCost());
                                            profile.getOwnedWeapons().add(item);
                                            profile.save();

                                            DatabaseManager.getInstance().setWeapon(UUIDUtility.getUUID(player.getName()), WeaponType.valueOf(item.name()));
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Purchased & equipped weapon &6" + ChatColor.stripColor(item.getName()) + " &7for your &6Survivor &7class!");
                                        }

                                        @Override
                                        public void onCancel() {
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Cancelled purchase for weapon &6" + ChatColor.stripColor(item.getName()) + "&7!");
                                        }

                                        @Override
                                        public ItemStack getItemStack() {
                                            return item.getItemStack();
                                        }
                                    }).openMenu(player);
                                } else {
                                    MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You don't have enough tokens for this weapon!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void mutantWeaponClick(final InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            final Player player = (Player) e.getWhoClicked();
            if (e.getInventory().getTitle().contains("&6&lMUTANT &8» &7Weapons")) {
                if (e.getCurrentItem().hasItemMeta()) {
                    e.setCancelled(true);
                    SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 25);

                    for (final WeaponType item : WeaponType.values()) {
                        if (WeaponType.getItemByItemStack(e.getCurrentItem()) == item) {
                            final MutantWeaponProfile profile = MutantWeaponProfile.getProfile(UUIDUtility.getUUID(player.getName()));
                            if (profile.hasItem(item)) {
                                DatabaseManager.getInstance().setMutantWeapon(UUIDUtility.getUUID(player.getName()), WeaponType.valueOf(item.name()));
                                MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Equipped weapon &6" + ChatColor.stripColor(item.getName()) + " &7for your &6Mutant &7class!");

                                player.closeInventory();
                            } else {
                                if (DatabaseManager.getTokenAPI().getTokens(profile.getPlayer()) >= item.getCost()) {
                                    new PendingPurchase(player, new IPurchase() {
                                        @Override
                                        public void onPurchase() {
                                            DatabaseManager.getTokenAPI().removeTokens(profile.getPlayer(), item.getCost());
                                            profile.getOwnedWeapons().add(item);
                                            profile.save();

                                            DatabaseManager.getInstance().setMutantWeapon(UUIDUtility.getUUID(player.getName()), WeaponType.valueOf(item.name()));
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Purchased & equipped weapon &6" + ChatColor.stripColor(item.getName()) + " &7for your &6Mutant &7class!");
                                        }

                                        @Override
                                        public void onCancel() {
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Cancelled purchase for weapon &6" + ChatColor.stripColor(item.getName()) + "&7!");
                                        }

                                        @Override
                                        public ItemStack getItemStack() {
                                            return item.getItemStack();
                                        }
                                    }).openMenu(player);
                                } else {
                                    MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You don't have enough tokens for this &6Weapon&7!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void abilityClick(final InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            final Player player = (Player) e.getWhoClicked();
            if (e.getInventory().getTitle().contains("Abilities")) {
                if (e.getCurrentItem().hasItemMeta()) {
                    e.setCancelled(true);
                    SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 25);

                    for (final AbilityType item : AbilityType.values()) {
                        if (AbilityType.getItemByItemStack(e.getCurrentItem()) == item) {
                            final AbilityProfile profile = AbilityProfile.getProfile(UUIDUtility.getUUID(player.getName()));
                            if (profile.hasItem(item)) {
                                DatabaseManager.getInstance().setAbility(UUIDUtility.getUUID(player.getName()), AbilityType.valueOf(item.name()));
                                MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Equipped ability &6" + ChatColor.stripColor(item.getName()) + " &7for your &aSurvivor &7class!");

                                player.closeInventory();
                            } else {
                                if (DatabaseManager.getTokenAPI().getTokens(profile.getPlayer()) >= item.getCost()) {
                                    new PendingPurchase(player, new IPurchase() {
                                        @Override
                                        public void onPurchase() {
                                            DatabaseManager.getTokenAPI().removeTokens(profile.getPlayer(), item.getCost());
                                            profile.getOwnedAbilities().add(item);
                                            profile.save();

                                            DatabaseManager.getInstance().setAbility(UUIDUtility.getUUID(player.getName()), AbilityType.valueOf(item.name()));
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Purchased & equipped ability &6" + ChatColor.stripColor(item.getName()) + " &7for your &aSurvivor &7class!");
                                        }

                                        @Override
                                        public void onCancel() {
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Cancelled purchase for ability &6" + ChatColor.stripColor(item.getName()) + "&7!");
                                        }

                                        @Override
                                        public ItemStack getItemStack() {
                                            return item.getItemStack();
                                        }
                                    }).openMenu(player);
                                } else {
                                    MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You don't have enough tokens for this &6Ability&7!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void modelClick(final InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            final Player player = (Player) e.getWhoClicked();
            if (e.getInventory().getTitle().contains("Models")) {
                if (e.getCurrentItem().hasItemMeta()) {
                    e.setCancelled(true);
                    SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 25);

                    for (final ModelType type : ModelType.values()) {
                        if (ModelType.getItemByItemStack(e.getCurrentItem()) == type) {
                            final ModelProfile profile = ModelProfile.getProfile(UUIDUtility.getUUID(player.getName()));
                            if (profile.hasModel(type)) {
                                DatabaseManager.getInstance().setModel(UUIDUtility.getUUID(player.getName()), ModelType.valueOf(type.name()));
                                MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Equipped model &6" + ChatColor.stripColor(type.getName()) + " &7for your &cMutant &7class!");

                                player.closeInventory();
                            } else {
                                if (DatabaseManager.getTokenAPI().getTokens(profile.getPlayer()) >= type.getCost()) {
                                    new PendingPurchase(player, new IPurchase() {
                                        @Override
                                        public void onPurchase() {
                                            DatabaseManager.getTokenAPI().removeTokens(profile.getPlayer(), type.getCost());
                                            profile.getOwnedModels().add(type);
                                            profile.save();

                                            DatabaseManager.getInstance().setModel(UUIDUtility.getUUID(player.getName()), ModelType.valueOf(type.name()));
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Purchased & equipped model &6" + ChatColor.stripColor(type.getName()) + " &7for your &cMutant &7class!");
                                        }

                                        @Override
                                        public void onCancel() {
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Cancelled purchase for model &6" + ChatColor.stripColor(type.getName()) + "&7!");
                                        }

                                        @Override
                                        public ItemStack getItemStack() {
                                            return type.getItemStack();
                                        }
                                    }).openMenu(player);
                                } else {
                                    MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You don't have enough tokens for this &6Model&7!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void helmetClick(final InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            final Player player = (Player) e.getWhoClicked();
            if (e.getInventory().getTitle().contains("&6&lSURVIVOR &8» Helmets")) {
                if (e.getCurrentItem().hasItemMeta()) {
                    e.setCancelled(true);
                    SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 25);

                    for (final HelmetType item : HelmetType.values()) {
                        if (HelmetType.getItemByItemStack(e.getCurrentItem()) == item) {
                            final HelmetProfile profile = HelmetProfile.getProfile(UUIDUtility.getUUID(player.getName()));
                            if (profile.hasItem(item)) {
                                DatabaseManager.getInstance().setHelmet(UUIDUtility.getUUID(player.getName()), HelmetType.valueOf(item.name()));
                                MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Equipped helmet &6" + ChatColor.stripColor(item.getName()) + " &7for your &7class!");

                                player.closeInventory();
                            } else {
                                if (DatabaseManager.getTokenAPI().getTokens(profile.getPlayer()) >= item.getCost()) {
                                    new PendingPurchase(player, new IPurchase() {
                                        @Override
                                        public void onPurchase() {
                                            DatabaseManager.getTokenAPI().removeTokens(profile.getPlayer(), item.getCost());
                                            profile.getOwnedHelmets().add(item);
                                            profile.saveProfile();

                                            DatabaseManager.getInstance().setHelmet(UUIDUtility.getUUID(player.getName()), HelmetType.valueOf(item.name()));
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Purchased & equipped helmet &6" + ChatColor.stripColor(item.getName()) + " &7for your &7class!");
                                        }

                                        @Override
                                        public void onCancel() {
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Cancelled purchase for helmet &6" + ChatColor.stripColor(item.getName()) + "&7!");
                                        }

                                        @Override
                                        public ItemStack getItemStack() {
                                            return item.getItemStack();
                                        }
                                    }).openMenu(player);
                                } else {
                                    MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You don't have enough tokens for this &6Armor&7!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void mutantHelmetClick(final InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            final Player player = (Player) e.getWhoClicked();
            if (e.getInventory().getTitle().contains("&6&lMUTANT &8» Helmets")) {
                if (e.getCurrentItem().hasItemMeta()) {
                    e.setCancelled(true);
                    SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 25);

                    for (final HelmetType item : HelmetType.values()) {
                        if (HelmetType.getItemByItemStack(e.getCurrentItem()) == item) {
                            final MutantHelmetProfile profile = MutantHelmetProfile.getProfile(UUIDUtility.getUUID(player.getName()));
                            if (profile.hasItem(item)) {
                                DatabaseManager.getInstance().setMutantHelmet(UUIDUtility.getUUID(player.getName()), HelmetType.valueOf(item.name()));
                                MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Equipped helmet &6" + ChatColor.stripColor(item.getName()) + " &7for your &6Mutant &7class!");

                                player.closeInventory();
                            } else {
                                if (DatabaseManager.getTokenAPI().getTokens(profile.getPlayer()) >= item.getCost()) {
                                    new PendingPurchase(player, new IPurchase() {
                                        @Override
                                        public void onPurchase() {
                                            DatabaseManager.getTokenAPI().removeTokens(profile.getPlayer(), item.getCost());
                                            profile.getOwnedHelmets().add(item);
                                            profile.saveProfile();

                                            DatabaseManager.getInstance().setMutantHelmet(UUIDUtility.getUUID(player.getName()), HelmetType.valueOf(item.name()));
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Purchased & equipped helmet &6" + ChatColor.stripColor(item.getName()) + " &7for your &6Mutant &7class!");
                                        }

                                        @Override
                                        public void onCancel() {
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Cancelled purchase for helmet &6" + ChatColor.stripColor(item.getName()) + "&7!");
                                        }

                                        @Override
                                        public ItemStack getItemStack() {
                                            return item.getItemStack();
                                        }
                                    }).openMenu(player);
                                } else {
                                    MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You don't have enough tokens for this &6Armor&7!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void chestplateClick(final InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            final Player player = (Player) e.getWhoClicked();
            if (e.getInventory().getTitle().contains("&6&lSURVIVOR &8» Chestplates")) {
                if (e.getCurrentItem().hasItemMeta()) {
                    e.setCancelled(true);
                    SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 25);

                    for (final ChestplateType item : ChestplateType.values()) {
                        if (ChestplateType.getItemByItemStack(e.getCurrentItem()) == item) {
                            final ChestplateProfile profile = ChestplateProfile.getProfile(UUIDUtility.getUUID(player.getName()));
                            if (profile.hasItem(item)) {
                                DatabaseManager.getInstance().setChestplate(UUIDUtility.getUUID(player.getName()), ChestplateType.valueOf(item.name()));
                                MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Equipped chestplate &6" + ChatColor.stripColor(item.getName()) + " &7for your &6Survivor &7class!");

                                player.closeInventory();
                            } else {
                                if (DatabaseManager.getTokenAPI().getTokens(profile.getPlayer()) >= item.getCost()) {
                                    new PendingPurchase(player, new IPurchase() {
                                        @Override
                                        public void onPurchase() {
                                            DatabaseManager.getTokenAPI().removeTokens(profile.getPlayer(), item.getCost());
                                            profile.getOwnedChestplates().add(item);
                                            profile.saveProfile();

                                            DatabaseManager.getInstance().setChestplate(UUIDUtility.getUUID(player.getName()), ChestplateType.valueOf(item.name()));
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Purchased & equipped chestplate &6" + ChatColor.stripColor(item.getName()) + " &7for your &6Survivor &7class!");
                                        }

                                        @Override
                                        public void onCancel() {
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Cancelled purchase for chestplate &6" + ChatColor.stripColor(item.getName()) + "&7!");
                                        }

                                        @Override
                                        public ItemStack getItemStack() {
                                            return item.getItemStack();
                                        }
                                    }).openMenu(player);
                                } else {
                                    MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You don't have enough tokens for this &6Armor&7!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void mutantChestplateClick(final InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            final Player player = (Player) e.getWhoClicked();
            if (e.getInventory().getTitle().contains("&6&lMUTANT &8» Chestplates")) {
                if (e.getCurrentItem().hasItemMeta()) {
                    e.setCancelled(true);
                    SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 25);

                    for (final ChestplateType item : ChestplateType.values()) {
                        if (ChestplateType.getItemByItemStack(e.getCurrentItem()) == item) {
                            final MutantChestplateProfile profile = MutantChestplateProfile.getProfile(UUIDUtility.getUUID(player.getName()));
                            if (profile.hasItem(item)) {
                                DatabaseManager.getInstance().setMutantChestplate(UUIDUtility.getUUID(player.getName()), ChestplateType.valueOf(item.name()));
                                MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Equipped chestplate &6" + ChatColor.stripColor(item.getName()) + " &7for your &6Mutant &7class!");

                                player.closeInventory();
                            } else {
                                if (DatabaseManager.getTokenAPI().getTokens(profile.getPlayer()) >= item.getCost()) {
                                    new PendingPurchase(player, new IPurchase() {
                                        @Override
                                        public void onPurchase() {
                                            DatabaseManager.getTokenAPI().removeTokens(profile.getPlayer(), item.getCost());
                                            profile.getOwnedChestplates().add(item);
                                            profile.saveProfile();

                                            DatabaseManager.getInstance().setMutantChestplate(UUIDUtility.getUUID(player.getName()), ChestplateType.valueOf(item.name()));
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Purchased & equipped chestplate &6" + ChatColor.stripColor(item.getName()) + " &7for your &6Mutant &7class!");
                                        }

                                        @Override
                                        public void onCancel() {
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Cancelled purchase for chestplate &6" + ChatColor.stripColor(item.getName()) + "&7!");
                                        }

                                        @Override
                                        public ItemStack getItemStack() {
                                            return item.getItemStack();
                                        }
                                    }).openMenu(player);
                                } else {
                                    MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You don't have enough tokens for this &6Armor&7!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void leggingsClick(final InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            final Player player = (Player) e.getWhoClicked();
            if (e.getInventory().getTitle().contains("&6&lSURVIVOR &8» Leggings")) {
                if (e.getCurrentItem().hasItemMeta()) {
                    e.setCancelled(true);
                    SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 25);

                    for (final LeggingsType item : LeggingsType.values()) {
                        if (LeggingsType.getItemByItemStack(e.getCurrentItem()) == item) {
                            final LeggingsProfile profile = LeggingsProfile.getProfile(UUIDUtility.getUUID(player.getName()));
                            if (profile.hasItem(item)) {
                                DatabaseManager.getInstance().setLeggings(UUIDUtility.getUUID(player.getName()), LeggingsType.valueOf(item.name()));
                                MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Equipped leggings &6" + ChatColor.stripColor(item.getName()) + " &7for your &6Survivor &7class!");

                                player.closeInventory();
                            } else {
                                if (DatabaseManager.getTokenAPI().getTokens(profile.getPlayer()) >= item.getCost()) {
                                    new PendingPurchase(player, new IPurchase() {
                                        @Override
                                        public void onPurchase() {
                                            DatabaseManager.getTokenAPI().removeTokens(profile.getPlayer(), item.getCost());
                                            profile.getOwnedLeggings().add(item);
                                            profile.saveProfile();

                                            DatabaseManager.getInstance().setLeggings(UUIDUtility.getUUID(player.getName()), LeggingsType.valueOf(item.name()));
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Purchased & equipped leggings &6" + ChatColor.stripColor(item.getName()) + " &7for your &6Survivor &7class!");
                                        }

                                        @Override
                                        public void onCancel() {
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Cancelled purchase for leggings &6" + ChatColor.stripColor(item.getName()) + "&7!");
                                        }

                                        @Override
                                        public ItemStack getItemStack() {
                                            return item.getItemStack();
                                        }
                                    }).openMenu(player);
                                } else {
                                    MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You don't have enough tokens for these &6Armor&7!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void mutantLeggingsClick(final InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            final Player player = (Player) e.getWhoClicked();
            if (e.getInventory().getTitle().contains("&6&lMUTANT &8» Leggings")) {
                if (e.getCurrentItem().hasItemMeta()) {
                    e.setCancelled(true);
                    SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 25);

                    for (final LeggingsType item : LeggingsType.values()) {
                        if (LeggingsType.getItemByItemStack(e.getCurrentItem()) == item) {
                            final MutantLeggingsProfile profile = MutantLeggingsProfile.getProfile(UUIDUtility.getUUID(player.getName()));
                            if (profile.hasItem(item)) {
                                DatabaseManager.getInstance().setMutantLeggings(UUIDUtility.getUUID(player.getName()), LeggingsType.valueOf(item.name()));
                                MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Equipped leggings &6" + ChatColor.stripColor(item.getName()) + " &7for your &6Mutant &7class!");

                                player.closeInventory();
                            } else {
                                if (DatabaseManager.getTokenAPI().getTokens(profile.getPlayer()) >= item.getCost()) {
                                    new PendingPurchase(player, new IPurchase() {
                                        @Override
                                        public void onPurchase() {
                                            DatabaseManager.getTokenAPI().removeTokens(profile.getPlayer(), item.getCost());
                                            profile.getOwnedLeggings().add(item);
                                            profile.saveProfile();

                                            DatabaseManager.getInstance().setMutantLeggings(UUIDUtility.getUUID(player.getName()), LeggingsType.valueOf(item.name()));
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Purchased & equipped leggings &6" + ChatColor.stripColor(item.getName()) + " &7for your &6Mutant &7class!");
                                        }

                                        @Override
                                        public void onCancel() {
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Cancelled purchase for leggings &6" + ChatColor.stripColor(item.getName()) + "&7!");
                                        }

                                        @Override
                                        public ItemStack getItemStack() {
                                            return item.getItemStack();
                                        }
                                    }).openMenu(player);
                                } else {
                                    MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You don't have enough tokens for this &6Armor&7!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void bootsClick(final InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            final Player player = (Player) e.getWhoClicked();
            if (e.getInventory().getTitle().contains("&6&lSURVIVOR &8» Boots")) {
                if (e.getCurrentItem().hasItemMeta()) {
                    e.setCancelled(true);
                    SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 25);

                    for (final BootsType item : BootsType.values()) {
                        if (BootsType.getItemByItemStack(e.getCurrentItem()) == item) {
                            final BootsProfile profile = BootsProfile.getProfile(UUIDUtility.getUUID(player.getName()));
                            if (profile.hasItem(item)) {
                                DatabaseManager.getInstance().setBoots(UUIDUtility.getUUID(player.getName()), BootsType.valueOf(item.name()));
                                MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Equipped boots &6" + ChatColor.stripColor(item.getName()) + " &7for your &6Survivor &7class!");

                                player.closeInventory();
                            } else {
                                if (DatabaseManager.getTokenAPI().getTokens(profile.getPlayer()) >= item.getCost()) {
                                    new PendingPurchase(player, new IPurchase() {
                                        @Override
                                        public void onPurchase() {
                                            DatabaseManager.getTokenAPI().removeTokens(profile.getPlayer(), item.getCost());
                                            profile.getOwnedBoots().add(item);
                                            profile.saveProfile();

                                            DatabaseManager.getInstance().setBoots(UUIDUtility.getUUID(player.getName()), BootsType.valueOf(item.name()));
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Purchased & equipped boots &6" + ChatColor.stripColor(item.getName()) + " &7for your &6Survivor &7class!");
                                        }

                                        @Override
                                        public void onCancel() {
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Cancelled purchase for boots &6" + ChatColor.stripColor(item.getName()) + "&7!");
                                        }

                                        @Override
                                        public ItemStack getItemStack() {
                                            return item.getItemStack();
                                        }
                                    }).openMenu(player);
                                } else {
                                    MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You don't have enough tokens for this &6Armor!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void mutantBootsClick(final InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            final Player player = (Player) e.getWhoClicked();
            if (e.getInventory().getTitle().contains("&6&lMUTANT &8» Boots")) {
                if (e.getCurrentItem().hasItemMeta()) {
                    e.setCancelled(true);
                    SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 25);

                    for (final BootsType item : BootsType.values()) {
                        if (BootsType.getItemByItemStack(e.getCurrentItem()) == item) {
                            final MutantBootsProfile profile = MutantBootsProfile.getProfile(UUIDUtility.getUUID(player.getName()));
                            if (profile.hasItem(item)) {
                                DatabaseManager.getInstance().setMutantBoots(UUIDUtility.getUUID(player.getName()), BootsType.valueOf(item.name()));
                                MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Equipped boots &6" + ChatColor.stripColor(item.getName()) + " &7for your &6Mutant &7class!");

                                player.closeInventory();
                            } else {
                                if (DatabaseManager.getTokenAPI().getTokens(profile.getPlayer()) >= item.getCost()) {
                                    new PendingPurchase(player, new IPurchase() {
                                        @Override
                                        public void onPurchase() {
                                            DatabaseManager.getTokenAPI().removeTokens(profile.getPlayer(), item.getCost());
                                            profile.getOwnedBoots().add(item);
                                            profile.saveProfile();

                                            DatabaseManager.getInstance().setMutantBoots(UUIDUtility.getUUID(player.getName()), BootsType.valueOf(item.name()));
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Purchased & equipped boots &6" + ChatColor.stripColor(item.getName()) + " &7for your &6Mutant &7class!");
                                        }

                                        @Override
                                        public void onCancel() {
                                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Cancelled purchase for boots &6" + ChatColor.stripColor(item.getName()) + "&7!");
                                        }

                                        @Override
                                        public ItemStack getItemStack() {
                                            return item.getItemStack();
                                        }
                                    }).openMenu(player);
                                } else {
                                    MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You don't have enough tokens for this &6Armor!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void ressurectionScroll(final InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            final Player player = (Player) e.getWhoClicked();
            if (e.getInventory().getTitle().contains("Resurrection Scroll")) {
                if (e.getCurrentItem().hasItemMeta()) {
                    e.setCancelled(true);
                    SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 25);

                    new PendingPurchase(player, new IPurchase() {
                        @Override
                        public void onPurchase() {
                            if (DatabaseManager.getTokenAPI().getTokens(UUIDUtility.getUUID(player.getName())) >= 4500) {
                                DatabaseManager.getTokenAPI().removeTokens(UUIDUtility.getUUID(player.getName()), 4500);
                                DatabaseManager.getInstance().setResurrectionScrolls(UUIDUtility.getUUID(player.getName()), DatabaseManager.getInstance().getResurrectionScrolls(UUIDUtility.getUUID(player.getName())) + 1);

                                MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "You purchased a &cResurrection Scroll&7! You now have &b" + DatabaseManager.getInstance().getResurrectionScrolls(UUIDUtility.getUUID(player.getName())) + " &cResurrection Scroll(s)&7!");
                            } else {
                                MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You don't have enough tokens to buy a &cResurrection Scroll&7!");
                            }
                        }

                        @Override
                        public void onCancel() {
                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Cancelled purchase for &cResurrection Scroll&7!");
                        }

                        @Override
                        public ItemStack getItemStack() {
                            ItemStack set = e.getCurrentItem();
                            ItemMeta meta = set.getItemMeta();
                            meta.setLore(null);
                            set.setItemMeta(meta);
                            return set;
                        }
                    }).openMenu(player);
                }
            }
        }
    }
}