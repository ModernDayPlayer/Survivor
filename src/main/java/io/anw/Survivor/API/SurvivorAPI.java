package io.anw.Survivor.API;

import io.anw.Survivor.Listeners.Game.Sessions.Ability.AbilityType;
import io.anw.Survivor.Utils.SQL.DatabaseManager;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;

import java.util.UUID;

public class SurvivorAPI {

    public static void enterPlayer(UUID player) {
        DatabaseManager.getInstance().enterPlayer(player);
    }

    public static boolean doesExist(UUID player) {
        return DatabaseManager.getInstance().doesExist(player);
    }

    public static int getKills(UUID player) {
        return DatabaseManager.getInstance().getKills(player);
    }

    public static void setKills(UUID player, int kills) {
        DatabaseManager.getInstance().setKills(player, kills);
    }

    public static int getDeaths(UUID player) {
        return DatabaseManager.getInstance().getDeaths(player);
    }

    public static void setDeaths(UUID player, int deaths) {
        DatabaseManager.getInstance().setDeaths(player, deaths);
    }

    public static int getWins(UUID player) {
        return DatabaseManager.getInstance().getWins(player);
    }

    public static void setWins(UUID player, int wins) {
        DatabaseManager.getInstance().setWins(player, wins);
    }

    public static int getPoints(UUID player) {
        return DatabaseManager.getInstance().getPoints(player);
    }

    public static void setPoints(UUID player, int points) {
        DatabaseManager.getInstance().setPoints(player, points);
    }

    public static int getGamesPlayed(UUID player) {
        return DatabaseManager.getInstance().getGamesPlayed(player);
    }

    public static void setGamesPlayed(UUID player, int gamesPlayed) {
        DatabaseManager.getInstance().setGamesPlayed(player, gamesPlayed);
    }

    public static AbilityType getAbility(UUID player) {
        return DatabaseManager.getInstance().getAbility(player);
    }

}
