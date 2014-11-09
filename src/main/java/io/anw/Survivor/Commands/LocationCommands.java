package io.anw.Survivor.Commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import io.anw.Core.Bukkit.Utils.Chat.MessageUtils;
import io.anw.Survivor.Objects.SurvivorMap;
import io.anw.Survivor.Utils.LocationUtils;
import io.anw.Survivor.Utils.LoggingUtils;
import io.anw.Survivor.Utils.MapUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LocationCommands {

    @Command(
            aliases = {"setlobby"},
            desc = "Set the lobby command!",
            max = 0
    )
    @CommandPermissions({"op"})
    public static void setLobby(CommandContext args, CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Location l = player.getLocation();
            LocationUtils.setWaitingLobbyLocation(l);

            MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Set main lobby spawn point!");
        }
    }

    @Command(
            aliases = {"addmap"},
            desc = "Create a new Survivor map!",
            min = 3,
            max = 3,
            usage = "[name] [author] [link]"
    )
    @CommandPermissions({"op"})
    public static void addMap(CommandContext args, CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            String name = args.getString(0), author = args.getString(1), link = args.getString(2);
            World world = player.getLocation().getWorld();
            MapUtils.addMap(name, author, link, world);

            MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Added map &6" + name + " &7in world &6" + world.getName() + " &7by &6" + author + " &7with the link &6" + link + "&7!");
            LoggingUtils.log("Successfully added map " + name + " by author " + author + " with link " + link + " at world " + world.getName() + "!");
        }
    }

    @Command(
            aliases = {"addspawn"},
            desc = "Add a spawnpoint for a map!",
            min = 1,
            max = 1,
            usage = "[map]"
    )
    @CommandPermissions({"op"})
    public static void addSpawn(CommandContext args, CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Location l = player.getLocation();
            SurvivorMap map = SurvivorMap.getMap(args.getString(0));
            map.addSpawn(l);

            MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Added a spawn to map &6" + map.getName() + "!");
        }
    }

}
