package io.anw.Survivor.Objects;

import io.anw.Survivor.Main;
import io.anw.Survivor.Utils.LoggingUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class SurvivorMap {

    private String name;
    private String author;
    private String link;

    public SurvivorMap(String name, String author, String link) {
        if (Main.getInstance().Data.getString("Maps." + name) == null) {
            throw new NullPointerException("Map is null");
        }

        this.name = name;
        this.author = author;
        this.link = link;
    }

    /**
     * Get the name of the map
     *
     * @return The name of the map
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the author of the map
     *
     * @return The author of the map
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * Get the link for the map's author (YouTube, Twitter, etc.)
     *
     * @return The link for the map
     */
    public String getLink() {
        return this.link;
    }

    /**
     * Get the world of the map
     *
     * @return The world of the map
     */
    public World getWorld() {
        return Bukkit.getWorld(Main.getInstance().Data.getString("Maps." + name + ".World"));
    }

    /**
     * Adds a spawn to a map
     *
     * @param location The map's spawn location
     */
    public void addSpawn(Location location) {
        int spawn = Main.getInstance().Data.getInt("Maps." + name + ".Number-Of-Spawns") + 1;
        Main.getInstance().Data.set("Maps." + name + ".Spawns.Spawn" + spawn + ".WORLD", location.getWorld().getName());
        Main.getInstance().Data.set("Maps." + name + ".Spawns.Spawn" + spawn + ".X", location.getX());
        Main.getInstance().Data.set("Maps." + name + ".Spawns.Spawn" + spawn + ".Y", location.getY());
        Main.getInstance().Data.set("Maps." + name + ".Spawns.Spawn" + spawn + ".Z", location.getZ());
        Main.getInstance().Data.set("Maps." + name + ".Spawns.Spawn" + spawn + ".YAW", location.getYaw());
        Main.getInstance().Data.set("Maps." + name + ".Spawns.Spawn" + spawn + ".PITCH", location.getPitch());
        Main.getInstance().Data.set("Maps." + name + ".Number-Of-Spawns", spawn);

        Main.getInstance().getConfigManager().save();
        LoggingUtils.log("New spawn location for map " + name + " set at " + (int) location.getX() + ", " + (int) location.getY() + ", " + (int) location.getZ() + " in world " + location.getWorld().getName() + "!");
    }

    /**
     * Get the spawns for the map
     *
     * @return The map's spawns
     */
    public List<Location> getSpawns() {
        List<Location> locations = new ArrayList<>();
        int number_of_spawns = Main.getInstance().Data.getInt("Maps." + name + ".Number-Of-Spawns");
        for(int x = 1; x <= number_of_spawns; x++) {
            locations.add(new Location(
                    Bukkit.getWorld(Main.getInstance().Data.getString("Maps." + name + ".Spawns.Spawn" + x + ".WORLD")),
                    Main.getInstance().Data.getDouble("Maps." + name + ".Spawns.Spawn" + x + ".X"),
                    Main.getInstance().Data.getDouble("Maps." + name + ".Spawns.Spawn" + x + ".Y"),
                    Main.getInstance().Data.getDouble("Maps." + name + ".Spawns.Spawn" + x + ".Z"),
                    Main.getInstance().Data.getInt("Maps." + name + ".Spawns.Spawn" + x + ".YAW"),
                    Main.getInstance().Data.getInt("Maps." + name + ".Spawns.Spawn" + x + ".PITCH")
            ));
        }

        return locations;
    }

    /**
     * Get all maps from the Survivor data file
     *
     * @return All Survivor maps
     */
    public static SurvivorMap[] getAllMaps() {
        List<SurvivorMap> mapList = new ArrayList<>();
        for(String map : Main.getInstance().Data.getConfigurationSection("Maps").getKeys(false)) {
            mapList.add(new SurvivorMap(
                    map,
                    Main.getInstance().Data.getString("Maps." + map + ".Author"),
                    Main.getInstance().Data.getString("Maps." + map + ".Link")
            ));
        }

        return mapList.toArray(new SurvivorMap[mapList.size()]);
    }

    /**
     * Get a Survivor map from a name
     *
     * @param name Name of map
     * @return Survivor Map from name in Data configuration
     */
    public static SurvivorMap getMap(String name) {
        if(Main.getInstance().Data.getConfigurationSection("Maps." + name) == null) {
            throw new NullPointerException("Map is null");
        }

        return new SurvivorMap(
                name,
                Main.getInstance().Data.getString("Maps." + name + ".Author"),
                Main.getInstance().Data.getString("Maps." + name + ".Link")
        );
    }

}
