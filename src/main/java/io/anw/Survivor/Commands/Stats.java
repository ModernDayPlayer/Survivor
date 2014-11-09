package io.anw.Survivor.Commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import io.anw.Core.Bukkit.Utils.Chat.MessageUtils;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import io.anw.Survivor.API.SurvivorAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Stats {

    @Command(
            aliases = {"stats"},
            desc = "Check your Survivor stats!",
            min = 0,
            max = 1
    )
    @CommandPermissions({"op"})
    public static void survivorStats(CommandContext args, CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            String targetName = args.getString(0);

            if (args.argsLength() == 0) {
                MessageUtils.message(player, "&e&m----------------------------------------------------", "&a&l" + targetName + "'s Stats", "");
                MessageUtils.message(player, "&6&lKILLS &7» &e" + SurvivorAPI.getKills(UUIDUtility.getUUID(targetName)));
                MessageUtils.message(player, "&6&lDEATHS &7» &e" + SurvivorAPI.getDeaths(UUIDUtility.getUUID(targetName)));
                MessageUtils.message(player, "&6&lWINS &7» &e" + SurvivorAPI.getWins(UUIDUtility.getUUID(targetName)));
                MessageUtils.message(player, "&6&lPOINTS &7» &e" + SurvivorAPI.getPoints(UUIDUtility.getUUID(targetName)));
                MessageUtils.message(player, "&6&lGAMES PLAYED &7» &e" + SurvivorAPI.getGamesPlayed(UUIDUtility.getUUID(targetName)));
                MessageUtils.message(player, "&e&m----------------------------------------------------");
            } else {
                MessageUtils.message(player, "&e&m----------------------------------------------------", "&a&lYour Stats", "");
                MessageUtils.message(player, "&6&lKILLS &7» &e" + SurvivorAPI.getKills(UUIDUtility.getUUID(player.getName())));
                MessageUtils.message(player, "&6&lDEATHS &7» &e" + SurvivorAPI.getDeaths(UUIDUtility.getUUID(player.getName())));
                MessageUtils.message(player, "&6&lWINS &7» &e" + SurvivorAPI.getWins(UUIDUtility.getUUID(player.getName())));
                MessageUtils.message(player, "&6&lPOINTS &7» &e" + SurvivorAPI.getPoints(UUIDUtility.getUUID(player.getName())));
                MessageUtils.message(player, "&6&lGAMES PLAYED &7» &e" + SurvivorAPI.getGamesPlayed(UUIDUtility.getUUID(player.getName())));
                MessageUtils.message(player, "&e&m----------------------------------------------------");
            }
        }
    }

}
