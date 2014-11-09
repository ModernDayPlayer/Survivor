package io.anw.Survivor.Listeners.Game.Sessions.Models;

import io.anw.Core.Bukkit.Utils.Chat.StringUtils;
import io.anw.Core.Bukkit.Utils.ItemStack.ItemUtils;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public enum ModelType {

    ZOMBIE("&eZombie Model", "&eZombie Model", DisguiseType.ZOMBIE, Material.ROTTEN_FLESH, 0, false, "Transform into a &6Zombie Model &7when on the Mutant team!"),
    SKELETON("&eSkeleton Model", "&eSkeleton Model", DisguiseType.SKELETON, Material.BOW, 1000, false, "Transform into a &6Skeleton Model &7when on the Mutant team!"),
    ENDERMAN("&eEnderman Model", "&eEnderman Model", DisguiseType.ENDERMAN, Material.EYE_OF_ENDER, 1500, false, "Transform into an &6Enderman Model &7when on the Mutant team!"),
    CREEPER("&eCreeper Model", "&eCreeper Model", DisguiseType.CREEPER, Material.TNT, 2000, false, "Transform into a &6Creeper Model &7when on the Mutant team!"),
    WITHER_SKELETON("&elWither Skeleton Model", "&elWither Skeleton Model", DisguiseType.WITHER_SKELETON, Material.ARROW, 500, true, "Transform into a &6Wither Skeleton Model &7when on the Mutant team!"),
    ZOMBIE_PIGMAN("&eZombie Pigman Model", "&eZombie Pigman Model", DisguiseType.PIG_ZOMBIE, Material.GOLD_SWORD, 1000, true, "Transform into a &6Zombie Pigman Model &7when on the Mutant team!"),
    BLAZE("&eBlaze Model", "&eBlaze Model", DisguiseType.BLAZE, Material.BLAZE_POWDER, 1500, true, "Transform into a &6Blaze Model &7when on the Mutant team!");

    /**
     * Get an item stack by a fuzzy search
     *
     * @param checkStack Item stack to check
     * @return Fuzzy searched shop item
     */
    public static ModelType getItemByItemStack(ItemStack checkStack) {
        if (!checkStack.hasItemMeta()) {
            return null;
        }

        for (ModelType item : values()) {
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
    private DisguiseType type;
    private int cost;
    private boolean vip;
    private ArrayList<String> lore;

    ModelType(String name, String itemStackName, DisguiseType disguiseType, Material material, int cost, boolean vip, String... lore) {
        this.name = name;
        this.itemStackName = itemStackName;
        this.type = disguiseType;
        this.material = material;
        this.cost = cost;
        this.vip = vip;
        constructLore(lore);
    }

    /**
     * Get the name of the model
     *
     * @return Name of the model
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
     * Get the disguise type of the model
     *
     * @return Disguise type of the model
     */
    public DisguiseType getDisguiseType() {
        return this.type;
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
     * Get the cost of the model
     *
     * @return Cost of the model
     */
    public int getCost() {
        return this.cost;
    }

    /**
    * Check if a model type is only for donators
    *
    * @return True if the model is for donators
    */
    public boolean isDonator() {
        return this.vip;
    }

    /**
     * Get the lore of the shop item
     *
     * @return Shop item's lore
     */
    public ArrayList<String> getLore() {
        return this.lore;
    }

    private void constructLore(String[] description) {
        this.lore = new ArrayList<>();
        for (String descriptionLine : description) {
            getLore().add(StringUtils.colorize("&7" + descriptionLine));
        }
        getLore().add("");
        getLore().add("&7Item Type: &6Model");
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
