package io.anw.Survivor.Utils;

import io.anw.Core.Bukkit.Utils.Chat.StringUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public final class ItemHandler {

    public static String toString(ItemStack i) {
        StringBuilder f = new StringBuilder();
        f.append("type=" + i.getType() + ";");

        if (i.getDurability() != 0) {
            f.append("dura=" + i.getDurability() + ";");
        }

        f.append("amount=" + i.getAmount() + ";");

        if (!i.getEnchantments().isEmpty()) {
            f.append("enchantments=");
            int in = 1;
            for (Map.Entry<Enchantment, Integer> key : i.getEnchantments().entrySet()) {
                f.append(key.getKey().getName() + ":" + key.getValue());

                if (in != i.getEnchantments().size()) {
                    f.append("&");
                }

                in++;
            }

            f.append(";");
        }

        if (i.hasItemMeta()) {
            ItemMeta m = i.getItemMeta();

            if (m.hasDisplayName()) {
                f.append("name=" + m.getDisplayName() + ";");
            }

            if (m instanceof LeatherArmorMeta) {
                LeatherArmorMeta me = (LeatherArmorMeta) m;
                int r = me.getColor().getRed();
                int g = me.getColor().getGreen();
                int b = me.getColor().getBlue();
                f.append("rgb=" + r + "," + g + "," + b);
            }

            if (m.hasLore()) {
                f.append("lore=");
                StringBuilder lore = new StringBuilder();

                for (String s : m.getLore()) {
                    lore.append("line:" + s);
                }

                f.append(lore.toString().replaceFirst("line:", ""));
            }

            if (m instanceof SkullMeta) {
                SkullMeta me = (SkullMeta) m;
                if (me.hasOwner())
                    f.append("owner=" + me.getOwner());
            }
        }

        return f.toString();
    }

    public static ItemStack fromString(String s) {
        ItemStack i;
        Material type = Material.AIR;
        short dura = 0;
        int amount = 1;
        Map<Enchantment, Integer> enchants = new HashMap<>();
        String cName = null;
        String[] rgb = null;
        List<String> lore = new ArrayList<>();
        String owner = null;

        for (String d : s.split(";")) {
            String[] id = d.split("=");
            if (id[0].equalsIgnoreCase("type")) {
                type = Material.getMaterial(id[1]);
            }
            else if (id[0].equalsIgnoreCase("dura")) {
                dura = Short.parseShort(id[1]);
            }
            else if (id[0].equalsIgnoreCase("amount")) {
                amount = Integer.parseInt(id[1]);
            }
            else if (id[0].equalsIgnoreCase("enchantments")) {
                for (String en : id[1].split("&")) {
                    String[] ench = en.split(":");
                    enchants.put(Enchantment.getByName(ench[0]), Integer.parseInt(ench[1]));
                }
            }
            else if (id[0].equalsIgnoreCase("name")) {
                cName = StringUtils.colorize(id[1]);
            }
            else if (id[0].equalsIgnoreCase("rgb")) {
                rgb = id[1].split(",");
            }
            else if (id[0].equalsIgnoreCase("lore")) {
                lore = Arrays.asList(id[1].split("line:"));
            }
            else if (id[0].equalsIgnoreCase("owner")) {
                owner = id[1];
            }
        }

        i = new ItemStack(type, amount);

        if (dura != 0) {
            i.setDurability(dura);
        }

        ItemMeta m = i.getItemMeta();

        if (cName != null) {
            m.setDisplayName(cName);
        }
        if (rgb != null) {
            ((LeatherArmorMeta) m).setColor(Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));
        }
        if (!lore.isEmpty()) {
            m.setLore(lore);
        }
        if (owner != null) {
            ((SkullMeta) m).setOwner(owner);
        }

        i.setItemMeta(m);
        i.addUnsafeEnchantments(enchants);
        return i;
    }

}
