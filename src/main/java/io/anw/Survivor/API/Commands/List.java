package io.anw.Survivor.API.Commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import io.anw.Survivor.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class List {

    @Command(
            aliases = {"list"},
            desc = "Displays a list of online players!",
            max = 0
    )
    public static void list(CommandContext args, CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            StringBuilder stringBuilder = new StringBuilder();
            for (Player online : Bukkit.getOnlinePlayers()) {
                stringBuilder.append("&a").append(online.getName()).append(online == Bukkit.getOnlinePlayers()[Bukkit.getOnlinePlayers().length - 1] ? "" : "&7, ");
            }

            MessageUtils.message(player, "&6&lONLINE (" + Bukkit.getOnlinePlayers().length + ") : " + stringBuilder.toString());
        }
    }

}
