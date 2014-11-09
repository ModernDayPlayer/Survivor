package io.anw.Survivor.API.Utils;

import io.anw.Core.Bukkit.AuroraPlugin;
import io.anw.Core.Bukkit.Utils.Chat.MessageUtils;
import io.anw.Core.Bukkit.Utils.Misc.SoundPlayer;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Conditions implements Listener {

    private List<UUID> HIDDEN_PLAYERS = new ArrayList<>(), NO_CHAT = new ArrayList<>(), CAN_MOVE = new ArrayList<>();
    private boolean CHAT_ENABLED_GLOBAL = true, CAN_MOVE_GLOBAL = true;
    private AuroraPlugin PLUGIN;

    public Conditions(AuroraPlugin plugin) {
        this.PLUGIN = plugin;
    }

    /**
     * Get the hidden players
     *
     * @return Hidden players list
     */
    public List<UUID> getHiddenPlayers() {
        return this.HIDDEN_PLAYERS;
    }

    /**
     * Get a list of players that can't chat
     *
     * @return Non-chatting players
     */
    public List<UUID> getNoChat() {
        return this.NO_CHAT;
    }

    /**
     * Get a list of players that can't move
     *
     * @return Non-moving players
     */
    public List<UUID> getNoMove() {
        return this.CAN_MOVE;
    }

    /**
     * Set the chat enabled globally or not
     *
     * @param flag Whether or not the chat is enabled
     */
    public void setNoChatGlobal(boolean flag) {
        this.CHAT_ENABLED_GLOBAL = flag;
    }

    /**
     * Check if chat is enabled globally
     *
     * @return true if chat is enabled globally
     */
    public boolean isNoChatGlobal() {
        return !this.CHAT_ENABLED_GLOBAL;
    }

    /**
     * Set no chat for a player
     *
     * @param player Player to toggle chat for
     * @param flag If the player should be able to chat
     */
    public void setNoChatIndividual(Player player, boolean flag) {
        if (flag) {
            getNoChat().add(UUIDUtility.getUUID(player.getName()));
        } else {
            if (getNoChat().contains(UUIDUtility.getUUID(player.getName()))) {
                getNoChat().remove(UUIDUtility.getUUID(player.getName()));
            }
        }
    }

    /**
     * Check if player's CANT move
     *
     * @return true if players can't move
     */
    public boolean isNoMove() {
        return !this.CAN_MOVE_GLOBAL;
    }

    /**
     * Set if players can move or not globally
     *
     * @param flag Whether or not the players can move
     */
    public void setCanMoveGlobal(boolean flag) {
        this.CAN_MOVE_GLOBAL = flag;
    }

    /**
     * Set no move for a player
     *
     * @param player Player to toggle move for
     * @param flag If the player should be able to move
     */
    public void setCanMoveIndividual(Player player, boolean flag) {
        if (flag) {
            getNoMove().add(UUIDUtility.getUUID(player.getName()));
        } else {
            if (getNoMove().contains(UUIDUtility.getUUID(player.getName()))) {
                getNoMove().remove(UUIDUtility.getUUID(player.getName()));
            }
        }
    }

    /**
     * Add a hidden player to the list
     *
     * @param player Player to toggle hidden
     */
    public void addHiddenPlayer(Player player) {
        getHiddenPlayers().add(UUIDUtility.getUUID(player.getName()));
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online != player) {
                online.hidePlayer(player);
            }
        }

        SoundPlayer.play(player, Sound.NOTE_BASS, 25);
        MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You are now &6&lINVISIBLE&7!");
    }

    /**
     * Remove a hidden player from the list
     *
     * @param player Player to toggle hidden
     */
    public void removeHiddenPlayer(Player player) {
        getHiddenPlayers().remove(UUIDUtility.getUUID(player.getName()));
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online != player) {
                online.showPlayer(player);
            }
        }

        SoundPlayer.play(player, Sound.NOTE_PLING, 25);
        MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "You are now &6&lVISIBLE&7!");
    }

    /*
     * EVENT HANDLING
     */

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (getHiddenPlayers().contains(UUIDUtility.getUUID(online.getName())) && online != player) {
                player.hidePlayer(online);
            }
        }
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (isNoChatGlobal() || getNoChat().contains(UUIDUtility.getUUID(player.getName()))) {
            e.setCancelled(true);
            MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You can not chat right now!");
            SoundPlayer.play(player, Sound.NOTE_BASS, 25);
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (isNoMove() || getNoMove().contains(UUIDUtility.getUUID(player.getName()))) {
            if (e.getTo().getX() != e.getFrom().getX() || e.getTo().getZ() != e.getFrom().getZ()) {
                e.setTo(e.getFrom());
            }
        }
    }

}
