package io.anw.Survivor.Listeners.Game;

import io.anw.Survivor.Game.Game;
import io.anw.Survivor.Game.GameState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BowHandler implements Listener {

    @EventHandler
    public void bowFire(EntityShootBowEvent e) {
        if(e.getEntity() instanceof Player) {
            if(!(Game.getInstance().getState().equals(GameState.In_Game))) {
                e.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void interact(PlayerInteractEvent e) {
        try {
            if(!(e.getItem().getType() == Material.BOW)) {
                e.setCancelled(true);
            }
        } catch (NullPointerException ignored) {  }
    }

}
