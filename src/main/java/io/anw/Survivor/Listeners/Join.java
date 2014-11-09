package io.anw.Survivor.Listeners;

import io.anw.Core.Bukkit.Utils.Chat.StringUtils;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import io.anw.Survivor.API.SurvivorAPI;
import io.anw.Survivor.Utils.AGameUtils;
import io.anw.Survivor.Game.Game;
import io.anw.Survivor.Game.GameState;
import io.anw.Survivor.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Join implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void login(AsyncPlayerPreLoginEvent e) {
        if (Game.getInstance().getState() == GameState.Waiting || Game.getInstance().getState() == GameState.Voting) {
            if (Game.getInstance().getInGame().size() == Main.getInstance().getConfigManager().getConfigFile("config.yml").getInt("Max-Players")) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, StringUtils.colorize("&c&lKICKED : &7This server is currently full! Purchase &aVIP &7for reserved slots!"));
            }
        } else {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, StringUtils.colorize("&c&lKICKED : &7This game has already started!"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void join(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        e.setJoinMessage(null);

        if (!SurvivorAPI.doesExist(UUIDUtility.getUUID(player.getName()))) {
            SurvivorAPI.enterPlayer(UUIDUtility.getUUID(player.getName()));
        }

        if (Game.getInstance().getState() == GameState.Waiting || Game.getInstance().getState() == GameState.Voting) {
            if (Game.getInstance().getInGame().size() == Main.getInstance().getConfig().getInt("Max-Players")) {
                player.kickPlayer(StringUtils.colorize("&cThis game is currently in progress, come back later!"));
            } else {
                AGameUtils.initializePlayer(player);

                if (Game.getInstance().getState() == GameState.Waiting) {
                    if (Game.getInstance().getInGame().size() == Main.getInstance().getConfig().getInt("Minimum-Start")) {
                        Game.getInstance().startVoting();
                    }
                }
            }
        } else {
            player.kickPlayer(StringUtils.colorize("&cThis game is currently in progress, come back later!"));
        }

        if (Game.getInstance().getState() == GameState.Waiting) {
            Game.getInstance().updateScoreboardWaiting();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                player.setScoreboard(Game.getInstance().getScoreboard());
            }
        }.runTaskLater(Main.getInstance(), 20);

        //DatabaseManager.getInstance().updatePlayers(Game.getInstance().getInGame().size());
    }

}
