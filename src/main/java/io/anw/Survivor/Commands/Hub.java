package io.anw.Survivor.Commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import io.anw.Survivor.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Hub {

    @Command(
            aliases = {"lobby", "hub", "leave"},
            desc = "Go back to the hub!",
            max = 0
    )
    public static void lobby(CommandContext args, CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Main.getBungeeManager().sendToServer(player, "lobby");
        }
    }

}
