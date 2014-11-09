package io.anw.Survivor.Utils;

import io.anw.Survivor.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtils {

    /**
     * Get the main lobby location for the spectators / waiting and voting stages of the game
     *
     * @return Waiting Lobby location
     */
    public static Location getWaitingLobbyLocation() {
        return new Location(
                Bukkit.getWorld(Main.getInstance().Data.getString("LOBBY_SPAWN.WORLD")),
                Main.getInstance().Data.getDouble("LOBBY_SPAWN.X"),
                Main.getInstance().Data.getDouble("LOBBY_SPAWN.Y"),
                Main.getInstance().Data.getDouble("LOBBY_SPAWN.Z"),
                Main.getInstance().Data.getInt("LOBBY_SPAWN.YAW"),
                Main.getInstance().Data.getInt("LOBBY_SPAWN.PITCH")
        );
    }


    /**
     * Set the waiting lobby spawn
     *
     * @param location Location to set the waiting lobby spawn at
     */
    public static void setWaitingLobbyLocation(Location location) {
        Main.getInstance().Data.set("LOBBY_SPAWN.WORLD", location.getWorld().getName());
        Main.getInstance().Data.set("LOBBY_SPAWN.X", location.getX());
        Main.getInstance().Data.set("LOBBY_SPAWN.Y", location.getY());
        Main.getInstance().Data.set("LOBBY_SPAWN.Z", location.getZ());
        Main.getInstance().Data.set("LOBBY_SPAWN.YAW", location.getYaw());
        Main.getInstance().Data.set("LOBBY_SPAWN.PITCH", location.getPitch());

        Main.getInstance().getConfigManager().save();
        LoggingUtils.log("Lobby location set at " + (int) location.getX() + ", " + (int) location.getY() + ", " + (int) location.getZ() + " in world " + location.getWorld().getName() + "!");
    }

}
