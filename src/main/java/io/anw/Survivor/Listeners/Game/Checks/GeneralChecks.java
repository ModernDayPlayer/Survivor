package io.anw.Survivor.Listeners.Game.Checks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class GeneralChecks implements Listener {


    @EventHandler(priority = EventPriority.LOWEST)
    public void wheat(PlayerInteractEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (e.getAction() == Action.PHYSICAL) {
            Block block = e.getClickedBlock();
            if (block == null) {
                return;
            }
            int blockType = block.getTypeId();
            if (blockType == Material.getMaterial(59).getId()) {
                e.setUseInteractedBlock(Event.Result.DENY);
                e.setCancelled(true);

                block.setTypeId(blockType);
                block.setData(block.getData());
            }
        }
        if (e.getAction() == Action.PHYSICAL) {
            Block block = e.getClickedBlock();
            if (block == null) {
                return;
            }
            int blockType = block.getTypeId();
            if (blockType == Material.getMaterial(60).getId()) {
                e.setUseInteractedBlock(Event.Result.DENY);
                e.setCancelled(true);

                block.setType(Material.getMaterial(60));
                block.setData(block.getData());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void foodLoss(FoodLevelChangeEvent e) {
        e.setFoodLevel(20);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void creatureSpawnEvent(CreatureSpawnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void blockBreak(BlockBreakEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void blockPlace(BlockPlaceEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void itemPickup(PlayerPickupItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void itemDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void explode(EntityExplodeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void weatherChange(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void inventoryClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void ignite(BlockIgniteEvent e) {
        e.setCancelled(true);
    }

}
