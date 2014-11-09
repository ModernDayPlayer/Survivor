package io.anw.Survivor.Listeners.Game.Sessions.Weapons;

import io.anw.Core.Bukkit.Utils.Chat.StringUtils;
import io.anw.Core.Bukkit.Utils.ItemStack.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public enum WeaponType {

    WOODEN_SWORD("&eWooden Sword", "&6&lWooden Sword", Material.WOOD_SWORD, 0, false, "A &6Wooden Sword &7to use when on the Survivor team!"),
    STONE_SWORD("&eStone Sword", "&7&lStone Sword", Material.STONE_SWORD, 500, false, "A &6Stone Sword &7to use when on the Survivor team!"),
    IRON_SWORD("&eIron Sword", "&f&lIron Sword", Material.IRON_SWORD, 1000, false, "An &6Iron Sword &7to use when on the Survivor team!"),
    DIAMOND_SWORD("&eDiamond Sword", "&b&lDiamond Sword", Material.DIAMOND_SWORD, 2000, true, "A &6Diamond Sword &7to use when on the Survivor team!");

    /**
     * Get an item stack by a fuzzy search
     *
     * @param checkStack Item stack to check
     * @return Fuzzy searched shop item
     */
    public static WeaponType getItemByItemStack(ItemStack checkStack) {
        if (!checkStack.hasItemMeta()) {
            return null;
        }

        for (WeaponType item : values()) {
            ItemStack itemStack = item.getItemStack();
            if (checkStack.getItemMeta().getDisplayName().contains(itemStack.getItemMeta().getDisplayName()) && itemStack.getType() == checkStack.getType() && itemStack.getDurability() == checkStack.getDurability() && itemStack.getAmount() == checkStack.getAmount()) {
                return item;
            }
        }
        return null;
    }

    private String name;
    private String itemStackName;
    private Material material;
    private int cost;
    private boolean vip;
    private ArrayList<String> lore;

    WeaponType(String name, String itemStackName, Material material, int cost, boolean vip, String... lore) {
        this.name = name;
        this.itemStackName = itemStackName;
        this.material = material;
        this.cost = cost;
        this.vip = vip;
        constructLore(lore);
    }

    /**
     * Get the menu name for the ability
     *
     * @return Menu name for the ability
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the ItemStack name for the ability
     *
     * @return ItemStack name for the ability
     */
    public String getItemStackName() {
        return this.itemStackName;
    }

    /**
     * Get the material of the shop item itemstack
     *
     * @return Material of shop item
     */
    public Material getMaterial() {
        return this.material;
    }

    /**
     * Get the cost of the weapon
     *
     * @return Cost of the weapon
     */
    public int getCost() {
        return this.cost;
    }

    /**
     * Get the lore of the shop item
     *
     * @return Shop item's lore
     */
    public ArrayList<String> getLore() {
        return this.lore;
    }

    /**
     * Check if a model type is only for donators
     *
     * @return True if the model is for donators
     */
    public boolean isDonator() {
        return this.vip;
    }

    private void constructLore(String[] description) {
        this.lore = new ArrayList<>();
        for (String descriptionLine : description) {
            getLore().add(StringUtils.colorize("&7" + descriptionLine));
        }
        getLore().add("");
        getLore().add("&7Item Type: &6Weapon");
        getLore().add("&7VIP Item?: &6" + (isDonator() ? "Yes" : "No"));
        getLore().add("&7Price: &6" + getCost() + " Tokens");
    }

    /**
     * Get the item stack representing the shop item in the menus
     *
     * @return Shop Item's item stack
     */
    public ItemStack getItemStack() {
        return ItemUtils.createItemStack(
                getItemStackName(),
                getLore(),
                getMaterial()
        );

        //return stack;
    }

    /**
     * Get the item stack with no lore
     *
     * @return Shop Item's item stack with no lore
     */
    public ItemStack getItemStackNoLore() {
        ItemStack stack = getItemStack();
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(null);
        stack.setItemMeta(meta);

        return stack;
    }

}
