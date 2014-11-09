package io.anw.Survivor.Listeners.Game.Sessions.Ability;

import io.anw.Core.Bukkit.Utils.Chat.StringUtils;
import io.anw.Core.Bukkit.Utils.ItemStack.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public enum AbilityType {

    HYPERSPEED("&eHyperspeed", "&e&lHyperspeed Activator &7(Right Click)",  Material.STICK, 0, false, "Use your hyperspeed and outrun your enemies or reach your foe to kill before someone else!"),
    GRIEFER("&eGriefer's Explosives", "&c&lGriefer's Explosives &7(Right Click)",  Material.TNT, 1000, false, "Become a pro griefer! Kill your foes with your highly explosive devices!"),
    EXPLOSIVE_BOW("&eThe Bow of the Creepers", "&a&lThe Bow of the Creepers &7(Right Click)", Material.BOW, 1500, false, "Purchase this rare bow once used by the Creeper King and witness its power!"),
    FIREBALL("&eRaging Flameballs", "&6&lRaging Flameballs &7(Right Click)", Material.BLAZE_ROD, 2000, false, "Hurl balls of flame at your enemies and watch them burn!"),
    WITHER_WRATH("&eWrath of the Wither", "&7&lWrath of the Wither &7(Right Click)", Material.IRON_BARDING, 2500, false, "Make your foes scream at your Wrath of the Wither ability!"),
    FREEZE_RAY("&eFreeze Ray", "&b&lFreeze Ray &7(Right Click)", Material.IRON_HOE, 3000, false, "Freeze your enemies in a sphere of ice!");

    /**
     * Get an item stack by a fuzzy search
     *
     * @param checkStack Item stack to check
     * @return Fuzzy searched shop item
     */
    public static AbilityType getItemByItemStack(ItemStack checkStack) {
        if (!checkStack.hasItemMeta()) {
            return null;
        }

        for (AbilityType item : values()) {
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

    AbilityType(String name, String itemStackName, Material material, int cost, boolean vip, String... lore) {
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
        getLore().add("&7Item Type: &6Ability");
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