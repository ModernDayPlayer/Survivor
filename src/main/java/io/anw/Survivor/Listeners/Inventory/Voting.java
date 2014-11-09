package io.anw.Survivor.Listeners.Inventory;

import io.anw.Core.Bukkit.Utils.Chat.MessageUtils;
import io.anw.Core.Bukkit.Utils.ItemStack.ItemUtils;
import io.anw.Core.Bukkit.Utils.Misc.RankUtils;
import io.anw.Core.Bukkit.Utils.Misc.SoundPlayer;
import io.anw.Core.Bukkit.Utils.Objects.InventoryMenu;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import io.anw.Survivor.Utils.AGameUtils;
import io.anw.Survivor.Game.Game;
import io.anw.Survivor.Game.GameState;
import io.anw.Survivor.Main;
import io.anw.Survivor.Objects.SurvivorMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.List;

public class Voting implements Listener {

    @EventHandler
    public void voteMenu(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        try {
            if(!(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)) return;
            if(e.getItem().getType().equals(Material.ENCHANTED_BOOK) && e.getItem().getItemMeta().getDisplayName().contains("Vote for a Map")) {
                if(Game.getInstance().getState() == GameState.Voting) {
                    if(!Game.getInstance().getPlayerMapVotes().containsKey(UUIDUtility.getUUID(player.getName()))) {
                        InventoryMenu menu = new InventoryMenu("Vote for a Map", 1);
                        for(SurvivorMap map : SurvivorMap.getAllMaps()) {
                            menu.addItem(ItemUtils.createItemStack("&b&l" + map.getName(), Arrays.asList("&7Click to vote for &6" + map.getName() + " &7by &6" + map.getAuthor() + "&7!"), Material.EMPTY_MAP));
                        }
                        menu.addItem(ItemUtils.createItemStack("&b&l&oRandom Map", Arrays.asList("&7Vote for a random map!"), Material.MAP));

                        menu.open(player);
                        SoundPlayer.play(player, Sound.NOTE_PLING, 5);
                    } else {
                        MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You have already voted!");
                        SoundPlayer.play(player, Sound.NOTE_BASS, 10);
                    }
                } else {
                    MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Please wait until the Voting stage has begun in order to vote for a map!");
                    SoundPlayer.play(player, Sound.NOTE_BASS, 10);
                }
            }
        } catch (NullPointerException ignored) {  }
    }

    @EventHandler
    public void voteMenuClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getInventory().getTitle().equalsIgnoreCase("Vote for a Map")) {
            e.setCancelled(true);
            player.closeInventory();

            if (e.getCurrentItem().hasItemMeta()) {
                SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 5);
                String title = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                SurvivorMap map;

                if (title.contains("Random Map")) {
                    List<SurvivorMap> maps = Arrays.asList(SurvivorMap.getAllMaps());
                    int rand = Main.getInstance().rand().nextInt(maps.size());
                    map = Game.getInstance().getMapInVotes(maps.get(rand).getName());
                    Game.getInstance().getPlayerMapVotes().put(UUIDUtility.getUUID(player.getName()), map);
                    Game.getInstance().getMapInVotes(ChatColor.stripColor(map.getName()));
                } else {
                    map = Game.getInstance().getMapInVotes(ChatColor.stripColor(title));
                    Game.getInstance().getPlayerMapVotes().put(UUIDUtility.getUUID(player.getName()), map);
                }

                AGameUtils.broadcast(RankUtils.getRankColor(Bukkit.getPlayer(UUIDUtility.getUUID(player.getName()))) + "" + player.getName() + " &7voted for &6" + map.getName() + "&7! &6(&ex" + Game.getInstance().getVoteValue(player) + "&6)");
                int cv = Game.getInstance().getMapVotes().get(map);
                Game.getInstance().getMapVotes().remove(map);
                Game.getInstance().getMapVotes().put(map, cv + Game.getInstance().getVoteValue(player));
            }

            Game.getInstance().updateScoreboardVotes();
        }
    }

}
