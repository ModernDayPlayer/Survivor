package io.anw.Survivor.Utils;

import io.anw.Core.Bukkit.Utils.Chat.StringUtils;
import io.anw.Core.Bukkit.Utils.ItemStack.ItemUtils;
import io.anw.Core.Bukkit.Utils.Misc.BossBarUtils;
import io.anw.Core.Bukkit.Utils.Misc.RankUtils;
import io.anw.Core.Bukkit.Utils.Misc.SoundPlayer;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import io.anw.Survivor.Game.Game;
import io.anw.Survivor.Game.GameState;
import io.anw.Survivor.Main;
import io.anw.Survivor.Utils.SQL.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class AGameUtils {
    private static String prefix = StringUtils.colorize("&8(&c&lSURVIVOR&8): &7");

    /**
     * Broadcast to the game
     *
     * @param messages Messages to broadcast
     */
    public static void broadcast(String... messages) {
        for (String message : messages) {
            Bukkit.broadcastMessage(prefix + StringUtils.colorize(message));
        }
    }

    /**
     * Broadcast to the game
     *
     * @param messages Messages to broadcast
     */
    public static void broadcastMessage(String... messages) {
        for (String message : messages) {
            Bukkit.broadcastMessage(StringUtils.colorize(message));
        }
    }

    public static void initializePlayer(final Player player) {
        if (Game.getInstance().getState() == GameState.Waiting) {
            BossBarUtils.setBar(player, "&6&lMCAURORA.NET &8&l- &7&lWaiting &7on &7&l" + Main.getInstance().getConfigManager().getConfigFile("config.yml").getString("Server-Name"), 200);
        }

        if (Game.getInstance().getState() == GameState.Voting) {
            BossBarUtils.setBar(player, "&6&lMCAURORA.NET &8&l- &7&lVoting &7on &7&l" + Main.getInstance().getConfigManager().getConfigFile("config.yml").getString("Server-Name"), 200);
        }

        Game.getInstance().addToGame(player);
        Game.getInstance().addToSurvivors(player);

        player.setFireTicks(0);
        player.setMaxHealth(20.0D);
        player.setHealth(20.0D);
        player.setAllowFlight(false);
        player.setFlying(false);

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0);
        player.setGameMode(GameMode.ADVENTURE);

        ItemStack kits = ItemUtils.createItemStack(
                "&b&lCreate a Class &7(Right Click)",
                Arrays.asList(
                        "&7Right click to open the Create a Class menu!"
                ), Material.CHEST
        );

        ItemStack maps = ItemUtils.createItemStack(
                "&6&lVote for a Map &7(Right Click)",
                Arrays.asList(
                        "&7Right click to open the map voting menu!"
                ), Material.ENCHANTED_BOOK
        );

        ItemStack resurrectionScroll = ItemUtils.createItemStack(
                "&c&lResurrection Scrolls &7| &b" + DatabaseManager.getInstance().getResurrectionScrolls(UUIDUtility.getUUID(player.getName())) + " &6Owned! &7(Right Click)",
                Arrays.asList(
                        "&7Right click to open the Resurrection Scrolls menu!"
                ), Material.PAPER
        );

        ItemStack clock = ItemUtils.createItemStack(
                "&a&lReturn to Lobby &7(Right Click)",
                Arrays.asList(
                        "&7Right click to return to the lobby!"
                ),
                Material.WATCH
        );

        player.getInventory().setItem(0, kits);
        player.getInventory().setItem(1, maps);
        player.getInventory().setItem(2, resurrectionScroll);
        player.getInventory().setItem(8, clock);

        broadcast(RankUtils.getRankColor(Bukkit.getPlayer(player.getName())) + player.getName() + " &7has joined &6Survivor&7! &6(&e" + Game.getInstance().getInGame().size() + "&6/&e" + Main.getInstance().Config.getInt("Max-Players") + "&6)");

        new BukkitRunnable() {
            @Override
            public void run() {
                Game.getInstance().teleportToLobby(player);
                SoundPlayer.play(player, Sound.ENDERMAN_TELEPORT, 25);
            }
        }.runTaskLater(Main.getInstance(), 0);
    }

    public static void unregisterPlayer(Player player) {
        Game.getInstance().removeFromGame(player);
        Game.getInstance().removeFromSurvivors(player);
        Game.getInstance().removeFromMutants(player);
    }

    public static void initializeSpectator(Player player) {
        //TODO: Later release.
    }
}
