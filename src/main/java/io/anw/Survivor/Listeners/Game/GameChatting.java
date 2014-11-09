package io.anw.Survivor.Listeners.Game;

import io.anw.Core.Bukkit.Utils.Chat.StringUtils;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import io.anw.Survivor.API.SurvivorAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class GameChatting implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void chat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        e.setFormat(StringUtils.colorize("&6(&e" + SurvivorAPI.getPoints(UUIDUtility.getUUID(player.getName())) + "&6) ") + e.getFormat());
    }

}
