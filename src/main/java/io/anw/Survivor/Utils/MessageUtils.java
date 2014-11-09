package io.anw.Survivor.Utils;

import io.anw.Core.Bukkit.Utils.Chat.StringUtils;
import org.bukkit.entity.Player;

public class MessageUtils {

    public static void message(Player player, String... messages) {
        for (String message : messages) {
            player.sendMessage(StringUtils.colorize("&8(&c&lSURVIVOR&8): &7" + message));
        }
    }

}
