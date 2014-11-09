package io.anw.Survivor.Commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import io.anw.Core.Bukkit.Utils.Chat.MessageUtils;
import io.anw.Survivor.Game.Game;
import io.anw.Survivor.Game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommands {

    @Command(
            aliases = {"forcestop"},
            desc = "Force stops a Survivor game!",
            max = 0
    )
    @CommandPermissions({"op"})
    public static void forceStop(CommandContext args, CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (Game.getInstance().getState() == GameState.In_Game) {
                for (Player pl : Bukkit.getOnlinePlayers()) {
                    MessageUtils.messagePrefix(pl, MessageUtils.MessageType.BAD, "Survivor is being force stopped!");
                }

                Game.getInstance().endGame();
            } else {
                MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Please wait until the game has started to do this!");
            }
        }
    }

}
