package io.anw.Survivor.Listeners;

import io.anw.Survivor.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class Items implements Listener {

    @EventHandler
    public void playerClock(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            try {
                if (e.getItem().getType().equals(Material.WATCH) && e.getItem().getItemMeta().getDisplayName().contains("Return to Lobby")) {
                    Main.getBungeeManager().sendToServer(player, "lobby");
                }
            } catch (NullPointerException ignored) { }
        }
    }

}
