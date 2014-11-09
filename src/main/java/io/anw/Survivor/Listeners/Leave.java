package io.anw.Survivor.Listeners;

import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import io.anw.Survivor.Utils.AGameUtils;
import io.anw.Survivor.Game.Game;
import io.anw.Survivor.Game.GameState;
import io.anw.Survivor.Main;
import io.anw.Survivor.Utils.SQL.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Leave implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void leave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        e.setQuitMessage(null);

        AGameUtils.unregisterPlayer(player);

        if (Game.getInstance().getState() == GameState.Waiting) {
            Game.getInstance().updateScoreboardWaiting();
        }

        if (Game.getInstance().getState() == GameState.Voting) {
            if (Game.getInstance().getPlayerMapVotes().get(UUIDUtility.getUUID(player.getName())) != null) {
                int cv = Game.getInstance().getMapVotes().get(Game.getInstance().getPlayerMapVotes().get(UUIDUtility.getUUID(player.getName())));
                Game.getInstance().getMapVotes().remove(Game.getInstance().getPlayerMapVotes().get(UUIDUtility.getUUID(player.getName())));
                Game.getInstance().getMapVotes().put(Game.getInstance().getPlayerMapVotes().get(UUIDUtility.getUUID(player.getName())), cv - Game.getInstance().getVoteValue(player));
                Game.getInstance().getVoted().remove(UUIDUtility.getUUID(player.getName()));
                Game.getInstance().updateScoreboardVotes();
            }
        }

        if (Game.getInstance().getState() == GameState.Starting) {
            Game.getInstance().updateScoreboardStarting();
        }

        if (Game.getInstance().getState() == GameState.In_Game) {
            if (Game.getInstance().getInGame().size() == 1) {
                AGameUtils.broadcast("All other players have left the game, thanks for playing! You've been awarded &62 &7tokens for staying!");
                DatabaseManager.getTokenAPI().addTokens(UUIDUtility.getUUID(Bukkit.getPlayer(Game.getInstance().getInGame().get(0)).getName()), 2);

                Game.getInstance().endGame();
            }

            else if (Game.getInstance().getMutants().size() == 0) {
                AGameUtils.broadcast("The previous &cMutant has left! Picking a random &aSurvivor to move to the &cMutant team...");

                int rand = Main.getInstance().rand().nextInt(Game.getInstance().getInGame().size());
                Player target = Bukkit.getPlayer(Game.getInstance().getInGame().get(rand));
                Game.getInstance().killPlayer(target, false, Game.DeathType.INFECTION, (String) null);
            }

            Game.getInstance().updateScoreboardGame();
        }

        //DatabaseManager.getInstance().updatePlayers(Game.getInstance().getInGame().size());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void kicked(PlayerKickEvent e) {
        Player player = e.getPlayer();
        e.setLeaveMessage(null);

        AGameUtils.unregisterPlayer(player);

        if (Game.getInstance().getState() == GameState.Waiting) {
            Game.getInstance().updateScoreboardWaiting();
        }

        if (Game.getInstance().getState() == GameState.Voting) {
            if (Game.getInstance().getPlayerMapVotes().get(UUIDUtility.getUUID(player.getName())) != null) {
                int cv = Game.getInstance().getMapVotes().get(Game.getInstance().getPlayerMapVotes().get(UUIDUtility.getUUID(player.getName())));
                Game.getInstance().getMapVotes().remove(Game.getInstance().getPlayerMapVotes().get(UUIDUtility.getUUID(player.getName())));
                Game.getInstance().getMapVotes().put(Game.getInstance().getPlayerMapVotes().get(UUIDUtility.getUUID(player.getName())), cv - Game.getInstance().getVoteValue(player));
                Game.getInstance().getVoted().remove(UUIDUtility.getUUID(player.getName()));
                Game.getInstance().updateScoreboardVotes();
            }
        }

        if (Game.getInstance().getState() == GameState.Starting) {
            Game.getInstance().updateScoreboardStarting();
        }

        if (Game.getInstance().getState() == GameState.In_Game) {
            if (Game.getInstance().getInGame().size() == 1) {
                AGameUtils.broadcast("All other players have left the game, thanks for playing! You've been awarded &a2 &7tokens for staying!");
                DatabaseManager.getTokenAPI().addTokens(UUIDUtility.getUUID(Bukkit.getPlayer(Game.getInstance().getInGame().get(0)).getName()), 2);

                Game.getInstance().endGame();
            }

            else if (Game.getInstance().getMutants().size() == 0) {
                AGameUtils.broadcast("The previous &cMutant has left! Picking a random &aSurvivor to move to the &cMutant team...");

                int rand = Main.getInstance().rand().nextInt(Game.getInstance().getInGame().size());
                Player target = Bukkit.getPlayer(Game.getInstance().getInGame().get(rand));
                Game.getInstance().killPlayer(target, false, Game.DeathType.INFECTION, (String) null);
            }

            Game.getInstance().updateScoreboardGame();
        }

        //DatabaseManager.getInstance().updatePlayers(Game.getInstance().getInGame().size());
    }

}
