package io.anw.Survivor.Utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author EvilPeanut
 */
public class SerializableItemStack implements Serializable {

    private static final long serialVersionUID = -1615979267987762332L;

    public int amount, typeID;
    public byte data;
    public short durability;
    public String displayName;
    public Map<Integer, Integer> enchantmentList = new HashMap<>();
    public List<String> lore;

    public SerializableItemStack(ItemStack itemStack) {
        amount = itemStack.getAmount();
        durability = itemStack.getDurability();
        for(Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            enchantmentList.put(enchantment.getId(), itemStack.getEnchantments().get(enchantment));
        }
        typeID = itemStack.getTypeId();
        data = itemStack.getData().getData();
        displayName = itemStack.getItemMeta().getDisplayName();
        lore = itemStack.getItemMeta().getLore();
    }

    /**
     * Convert the object to an itemstack
     *
     * @return Converted itemstack
     */
    public ItemStack toItemStack() {
        ItemStack newStack = new ItemStack(typeID, amount);
        newStack.setData(new MaterialData(typeID, data));
        newStack.setDurability(durability);
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        for(Integer integer : enchantmentList.keySet()) {
            enchantments.put(Enchantment.getById(integer), enchantmentList.get(integer));
        }
        newStack.addUnsafeEnchantments(enchantments);
        ItemMeta newMeta = newStack.getItemMeta();
        newMeta.setLore(lore);
        newMeta.setDisplayName(displayName);
        newStack.setItemMeta(newMeta);
        return newStack;
    }

}