package io.anw.Survivor.Listeners.Game.Sessions.Armor.Leggings;

import io.anw.Core.Bukkit.Utils.Chat.StringUtils;
import io.anw.Core.Bukkit.Utils.ItemStack.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public enum LeggingsType {

    LEATHER_LEGGINGS("&eLeather Leggings", "&6&lLeather Leggings", Material.LEATHER_LEGGINGS, 0, false, "A &6Leather Leggings &7to use as armor!"),
    GOLDEN_LEGGINGS("&eGolden Leggings", "&e&lGolden Leggings", Material.GOLD_LEGGINGS, 1000, false, "A &eGolden Leggings &7to use as armor!"),
    IRON_LEGGINGS("&eIron Leggings", "&f&lIron Leggings", Material.IRON_LEGGINGS, 1500, false, "An &fIron Leggings &7to use as armor!"),
    DIAMOND_LEGGINGS("&eDiamond Leggings", "&b&lDiamond Leggings", Material.DIAMOND_LEGGINGS, 2000, true, "An &bDiamond Leggings &7to use as armor!");

    /**
     * Get an item stack by a fuzzy search
     *
     * @param checkStack Item stack to check
     * @return Fuzzy searched shop item
     */
    public static LeggingsType getItemByItemStack(ItemStack checkStack) {
        if (!checkStack.hasItemMeta()) {
            return null;
        }

        for (LeggingsType item : values()) {
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

    LeggingsType(String name, String itemStackName, Material material, int cost, boolean vip, String... lore) {
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
        getLore().add("&7Item Type: &6Armor");
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
