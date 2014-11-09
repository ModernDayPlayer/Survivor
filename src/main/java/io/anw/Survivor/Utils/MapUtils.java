package io.anw.Survivor.Utils;

import io.anw.Survivor.Main;
import org.bukkit.World;

public class MapUtils {

    /**
     * Add a map to the data file
     *
     * @param name Name of map
     * @param author Author of map
     * @param link Link to map
     */
    public static void addMap(String name, String author, String link, World world) {
        Main.getInstance().getConfigManager().getConfigFile("data.yml").set("Maps." + name + ".Author", author);
        Main.getInstance().getConfigManager().getConfigFile("data.yml").set("Maps." + name + ".Link", link);
        Main.getInstance().getConfigManager().getConfigFile("data.yml").set("Maps." + name + ".Number-Of-Spawns", 0);
        Main.getInstance().getConfigManager().getConfigFile("data.yml").set("Maps." + name + ".World", world.getName());

        Main.getInstance().getConfigManager().save();
    }

}
