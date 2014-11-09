package io.anw.Survivor.Listeners.Game;

import io.anw.Core.Bukkit.Utils.Chat.MessageUtils;
import io.anw.Core.Bukkit.Utils.ItemStack.ItemUtils;
import io.anw.Core.Bukkit.Utils.Misc.BossBarUtils;
import io.anw.Core.Bukkit.Utils.Misc.SoundPlayer;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import io.anw.Survivor.API.SurvivorAPI;
import io.anw.Survivor.Utils.AGameUtils;
import io.anw.Survivor.Game.Game;
import io.anw.Survivor.Game.GameState;
import io.anw.Survivor.Main;
import io.anw.Survivor.Utils.SQL.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DamageHandler implements Listener {

    private int resurrectCountdown = 30;
    private BukkitTask task;
    private List<UUID> gamesPlayed = new ArrayList<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void damage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (!(Game.getInstance().getState() == GameState.In_Game)) {
                e.setCancelled(true);
            } else {
                if (e.getDamage() >= ((Player) e.getEntity()).getHealth()
                        && e.getCause() != EntityDamageEvent.DamageCause.CONTACT
                        && e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK
                        && e.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
                    Player player = (Player) e.getEntity();
                    Game.getInstance().killPlayer(player, true, Game.getInstance().getMutants().contains(UUIDUtility.getUUID(player.getName())) ? Game.DeathType.SURVIVOR : Game.DeathType.INFECTION, "Damage");
                    Game.getInstance().updateScoreboardGame();

                    if (Game.getInstance().getMutants().size() == Game.getInstance().getInGame().size()) {
                        AGameUtils.broadcast("&cThe &c&lMUTANTS &chave infected the &a&lSURVIVORS &cto win the game!");

                        for (Player online : Bukkit.getOnlinePlayers()) {
                            if (Game.getInstance().getMutants().contains(UUIDUtility.getUUID(online.getName()))) {
                                SurvivorAPI.setWins(UUIDUtility.getUUID(online.getName()), SurvivorAPI.getWins(UUIDUtility.getUUID(online.getName())) + 1);

                                if (!gamesPlayed.contains(UUIDUtility.getUUID(online.getName()))) {
                                    gamesPlayed.add(UUIDUtility.getUUID(online.getName()));
                                    SurvivorAPI.setGamesPlayed(UUIDUtility.getUUID(online.getName()), SurvivorAPI.getGamesPlayed(UUIDUtility.getUUID(online.getName())) + 1);
                                }
                            }

                            if (BossBarUtils.hasBar(online)) {
                                BossBarUtils.destroyDragon(online);
                            }

                            BossBarUtils.setBar(online, "&8(&c&lSURVIVOR&8): &c&lTHE MUTANTS HAVE WON!", 200);
                        }

                        Game.getInstance().endGame();
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player damaged = (Player) e.getEntity();
            Player damager = (Player) e.getDamager();

            if (Game.getInstance().getInGame().contains(UUIDUtility.getUUID(damager.getName())) && !Game.getInstance().getMutants().contains(UUIDUtility.getUUID(damaged.getName()))) {
                e.setCancelled(true);
            } // Survivors can't hit survivors
            else if (Game.getInstance().getMutants().contains(UUIDUtility.getUUID(damager.getName())) && Game.getInstance().getMutants().contains(UUIDUtility.getUUID(damaged.getName()))) {
                e.setCancelled(true);
            } // Mutants can't hit mutants
            if ((Game.getInstance().getMutants().contains(UUIDUtility.getUUID(damager.getName())) && !Game.getInstance().getMutants().contains(UUIDUtility.getUUID(damaged.getName()))) && Game.getInstance().getState() == GameState.In_Game) {
                e.setCancelled(false);
            } // Mutants CAN hit survivors

            for(ItemStack armor : damaged.getInventory().getArmorContents()) {
                if(armor != null) {
                    armor.setDurability((short) 0);
                }
            }
            damager.getItemInHand().setDurability((short) 0);

            if (e.getDamage() >= damaged.getHealth() && Game.getInstance().getResurrectors().contains(UUIDUtility.getUUID(damaged.getName()))) {
                DatabaseManager.getTokenAPI().addTokens(UUIDUtility.getUUID(damager.getName()), Game.getInstance().getVoteValue(damager));
                SurvivorAPI.setPoints(UUIDUtility.getUUID(damager.getName()), SurvivorAPI.getPoints(UUIDUtility.getUUID(damager.getName())) + Game.getInstance().getVoteValue(damager));
                SurvivorAPI.setKills(UUIDUtility.getUUID(damager.getName()), SurvivorAPI.getKills(UUIDUtility.getUUID(damager.getName())) + 1);

                Game.getInstance().killPlayer(damaged, true, Game.DeathType.RESURRECTOR_KILLED, damager.getName());

                DatabaseManager.getTokenAPI().addTokens(UUIDUtility.getUUID(damager.getName()), Game.getInstance().getTokenAmount(damager));
                Game.getInstance().updateScoreboardGame();

                if (!gamesPlayed.contains(UUIDUtility.getUUID(damaged.getName()))) {
                    gamesPlayed.add(UUIDUtility.getUUID(damaged.getName()));
                    SurvivorAPI.setGamesPlayed(UUIDUtility.getUUID(damaged.getName()), SurvivorAPI.getGamesPlayed(UUIDUtility.getUUID(damaged.getName())) + 1);
                }

                Bukkit.getScheduler().cancelTask(task.getTaskId());
            }

           else if (e.getDamage() >= ((Player) e.getEntity()).getHealth() && !Game.getInstance().getResurrectors().contains(UUIDUtility.getUUID(damaged.getName()))) {
                DatabaseManager.getTokenAPI().addTokens(UUIDUtility.getUUID(damager.getName()), Game.getInstance().getVoteValue(damager));
                SurvivorAPI.setPoints(UUIDUtility.getUUID(damager.getName()), SurvivorAPI.getPoints(UUIDUtility.getUUID(damager.getName())) + Game.getInstance().getVoteValue(damager));
                SurvivorAPI.setKills(UUIDUtility.getUUID(damager.getName()), SurvivorAPI.getKills(UUIDUtility.getUUID(damager.getName())) + 1);
                Game.getInstance().killPlayer(damaged, true, Game.getInstance().getMutants().contains(UUIDUtility.getUUID(damaged.getName())) ? Game.DeathType.SURVIVOR : Game.DeathType.INFECTION, damager.getName());
                DatabaseManager.getTokenAPI().addTokens(UUIDUtility.getUUID(damager.getName()), Game.getInstance().getTokenAmount(damager));
                Game.getInstance().updateScoreboardGame();

                if (!gamesPlayed.contains(UUIDUtility.getUUID(damaged.getName()))) {
                    gamesPlayed.add(UUIDUtility.getUUID(damaged.getName()));
                    SurvivorAPI.setGamesPlayed(UUIDUtility.getUUID(damaged.getName()), SurvivorAPI.getGamesPlayed(UUIDUtility.getUUID(damaged.getName())) + 1);
                }

                if (Game.getInstance().getMutants().size() == Game.getInstance().getInGame().size()) {
                    AGameUtils.broadcast("&7The &c&lMUTANTS &7have infected the &a&lSURVIVORS &7to win the game!");

                    for (Player online : Bukkit.getOnlinePlayers()) {
                        if (Game.getInstance().getMutants().contains(UUIDUtility.getUUID(online.getName()))) {
                            SurvivorAPI.setWins(UUIDUtility.getUUID(online.getName()), SurvivorAPI.getWins(UUIDUtility.getUUID(online.getName())) + 1);
                        }

                        if (BossBarUtils.hasBar(online)) {
                            BossBarUtils.destroyDragon(online);
                        }

                        BossBarUtils.setBar(online, "&8(&c&lSURVIVOR&8): &c&lTHE MUTANTS HAVE WON!", 200);
                    }

                    Game.getInstance().endGame();
                }
            }
        }

        else if (e.getDamager() instanceof Projectile && e.getEntity() instanceof Player) {
            if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
                Player damager = (Player) ((Projectile) e.getDamager()).getShooter();
                Player damaged = (Player) e.getEntity();

                if (Game.getInstance().getInGame().contains(UUIDUtility.getUUID(damager.getName())) && !Game.getInstance().getMutants().contains(UUIDUtility.getUUID(damaged.getName()))) {
                    e.setCancelled(true);
                } // Survivors can't hit survivors
                else if (Game.getInstance().getMutants().contains(UUIDUtility.getUUID(damager.getName())) && Game.getInstance().getMutants().contains(UUIDUtility.getUUID(damaged.getName()))) {
                    e.setCancelled(true);
                } // Mutants can't hit mutants
                if ((Game.getInstance().getMutants().contains(UUIDUtility.getUUID(damager.getName())) && !Game.getInstance().getMutants().contains(UUIDUtility.getUUID(damaged.getName()))) && Game.getInstance().getState() == GameState.In_Game) {
                    e.setCancelled(false);
                } // Mutants CAN hit survivors

                for (ItemStack armor : damaged.getInventory().getArmorContents()) {
                    if (armor != null) {
                        armor.setDurability((short) 0);
                    }
                }
                damager.getItemInHand().setDurability((short) 0);

                if (e.getDamage() >= damaged.getHealth() && Game.getInstance().getResurrectors().contains(UUIDUtility.getUUID(damaged.getName()))) {
                    DatabaseManager.getTokenAPI().addTokens(UUIDUtility.getUUID(damager.getName()), Game.getInstance().getVoteValue(damager));
                    SurvivorAPI.setPoints(UUIDUtility.getUUID(damager.getName()), SurvivorAPI.getPoints(UUIDUtility.getUUID(damager.getName())) + Game.getInstance().getVoteValue(damager));
                    SurvivorAPI.setKills(UUIDUtility.getUUID(damager.getName()), SurvivorAPI.getKills(UUIDUtility.getUUID(damager.getName())) + 1);

                    Game.getInstance().killPlayer(damaged, true, Game.DeathType.RESURRECTOR_KILLED, damager.getName());

                    DatabaseManager.getTokenAPI().addTokens(UUIDUtility.getUUID(damager.getName()), Game.getInstance().getTokenAmount(damager));
                    Game.getInstance().updateScoreboardGame();

                    if (!gamesPlayed.contains(UUIDUtility.getUUID(damaged.getName()))) {
                        gamesPlayed.add(UUIDUtility.getUUID(damaged.getName()));
                        SurvivorAPI.setGamesPlayed(UUIDUtility.getUUID(damaged.getName()), SurvivorAPI.getGamesPlayed(UUIDUtility.getUUID(damaged.getName())) + 1);
                    }

                    Bukkit.getScheduler().cancelTask(task.getTaskId());
                }

                else if (e.getDamage() >= ((Player) e.getEntity()).getHealth() && !Game.getInstance().getResurrectors().contains(UUIDUtility.getUUID(damaged.getName()))) {
                    DatabaseManager.getTokenAPI().addTokens(UUIDUtility.getUUID(damager.getName()), Game.getInstance().getVoteValue(damager));
                    SurvivorAPI.setPoints(UUIDUtility.getUUID(damager.getName()), SurvivorAPI.getPoints(UUIDUtility.getUUID(damager.getName())) + Game.getInstance().getVoteValue(damager));
                    SurvivorAPI.setKills(UUIDUtility.getUUID(damager.getName()), SurvivorAPI.getKills(UUIDUtility.getUUID(damager.getName())) + 1);
                    Game.getInstance().killPlayer(damaged, true, Game.getInstance().getMutants().contains(UUIDUtility.getUUID(damaged.getName())) ? Game.DeathType.SURVIVOR : Game.DeathType.INFECTION, damager.getName());
                    DatabaseManager.getTokenAPI().addTokens(UUIDUtility.getUUID(damager.getName()), Game.getInstance().getTokenAmount(damager));
                    Game.getInstance().updateScoreboardGame();

                    if (!gamesPlayed.contains(UUIDUtility.getUUID(damaged.getName()))) {
                        gamesPlayed.add(UUIDUtility.getUUID(damaged.getName()));
                        SurvivorAPI.setGamesPlayed(UUIDUtility.getUUID(damaged.getName()), SurvivorAPI.getGamesPlayed(UUIDUtility.getUUID(damaged.getName())) + 1);
                    }

                    if (Game.getInstance().getMutants().size() == Game.getInstance().getInGame().size()) {
                        AGameUtils.broadcast("&cThe &c&lMUTANTS &chave infected the &a&lSURVIVORS &cto win the game!");
                        for (Player online : Bukkit.getOnlinePlayers()) {
                            if (Game.getInstance().getMutants().contains(UUIDUtility.getUUID(online.getName()))) {
                                SurvivorAPI.setWins(UUIDUtility.getUUID(online.getName()), SurvivorAPI.getWins(UUIDUtility.getUUID(online.getName())) + 1);
                            }

                            if (BossBarUtils.hasBar(online)) {
                                BossBarUtils.destroyDragon(online);
                            }

                            BossBarUtils.setBar(online, "&8(&c&lSURVIVOR&8): &c&lTHE MUTANTS HAVE WON!", 200);
                        }

                        Game.getInstance().endGame();
                    }
                }
            }
        }
    }

    // RESURRECTION SCROLL
    // RESURRECTION SCROLL
    // RESURRECTION SCROLL

    @EventHandler
    public void resurrectionScroll(PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        try {
            if (e.getItem() != null && e.getItem().getItemMeta().getDisplayName().contains("Resurrection Scroll") && e.getItem().getType() == Material.PAPER) {
                if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
                if (Game.getInstance().getState() != GameState.In_Game) return;

                if (Game.getInstance().getResurrectors().contains(UUIDUtility.getUUID(player.getName()))
                        || Game.getInstance().getHaveResurrected().contains(UUIDUtility.getUUID(player.getName()))
                        || Game.getInstance().getSurvivors().contains(UUIDUtility.getUUID(player.getName()))) {
                    return;
                }

                MessageUtils.message(player, " ");
                MessageUtils.message(player, "&e&m----------------------------------------------------");
                AGameUtils.broadcast("&a&l" + player.getName() + " &7has used a &c&lRESURRECTION SCROLL&7!");

                for (UUID uuid : Game.getInstance().getMutants()) {
                    Player mutantPlayer = Bukkit.getPlayer(uuid);
                    MessageUtils.message(mutantPlayer, "&c&lMUTANTS: &7He/she has resurrected as a Survivor! Don't let them live, don't let them kill you! Don't let him become a Survivor again!");
                    MessageUtils.message(mutantPlayer, "&c&lMUTANTS: &7You have &62 &7minutes! &a&lGOOD LUCK!");
                }

                for (UUID uuid : Game.getInstance().getSurvivors()) {
                    Player survivorPlayer = Bukkit.getPlayer(uuid);
                    MessageUtils.message(survivorPlayer, "&a&lSURVIVORS: &7He/she has resurrected as a Survivor! Assist them in killing a &cMutant &7 at all costs!");
                    MessageUtils.message(survivorPlayer, "&a&lSURVIVORS: &7Succeed, and you will have a fellow Survivor join you again. &a&lGOOD LUCK!");
                }

                MessageUtils.message(player, "&e&m----------------------------------------------------");
                MessageUtils.message(player, " ");

                MessageUtils.message(player, " ");
                MessageUtils.message(player, " ");
                MessageUtils.message(player, " ");
                MessageUtils.message(player, "&e&m----------------------------------------------------");
                MessageUtils.message(player, "You have used a &c&lRESURRECTION SCROLL&7!");
                MessageUtils.message(player, "You have &62 &7minutes to slay another &cMutant &7in order to truly resurrect as a fellow &aSurvivor&7!");
                MessageUtils.message(player, "Keep track of your time! If you don't slay a &cMutant &7in time, you will transform back into a &cMutant &7again for the rest of the game!");
                MessageUtils.message(player, "&e&m----------------------------------------------------");
                MessageUtils.message(player, " ");
                MessageUtils.message(player, " ");
                MessageUtils.message(player, " ");

                Game.getInstance().teleportToRandomSpot(player);
                player.setHealth(player.getMaxHealth());
                player.setFireTicks(0);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 2, 10));
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 10));
                player.getInventory().clear();
                player.setFoodLevel(20);
                player.getInventory().setArmorContents(null);
                Game.removeDisguisePlayer(player);

                player.getInventory().setItem(0, ItemUtils.createItemStack("&b&lThe Resurrection Sword", Material.DIAMOND_SWORD));
                player.getInventory().setHelmet(ItemUtils.createItemStack("&b&lThe Resurrection Helmet", Material.DIAMOND_HELMET));

                Game.getInstance().addToResurrectors(player);
                Game.getInstance().addToHaveResurrected(player);

                if (Main.getInstance().getRedFactory().isRed(player)) {
                    Main.getInstance().getRedFactory().addRed(player);
                } else {
                    Main.getInstance().getRedFactory().removeRed(player);
                }

                task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        if (resurrectCountdown == 30) {
                            MessageUtils.message(player, "&e&m----------------------------------------------------");
                            io.anw.Survivor.Utils.MessageUtils.message(player, "You have &630 seconds &7left to kill a &c&lMUTANT &7in order to resurrect!");
                            MessageUtils.message(player, "&e&m----------------------------------------------------");

                            SoundPlayer.play(player, Sound.NOTE_PLING, 25);
                        }

                        if (resurrectCountdown == 15) {
                            MessageUtils.message(player, "&e&m----------------------------------------------------");
                            io.anw.Survivor.Utils.MessageUtils.message(player, "You have &615 seconds &7left to kill a &c&lMUTANT &7in order to resurrect!");
                            MessageUtils.message(player, "&e&m----------------------------------------------------");

                            SoundPlayer.play(player, Sound.NOTE_PLING, 25);
                        }

                        if (resurrectCountdown <= 5 && resurrectCountdown != 0) {
                            MessageUtils.message(player, "&e&m----------------------------------------------------");
                            io.anw.Survivor.Utils.MessageUtils.message(player, "You have &6" + resurrectCountdown + " &7seconds left to kill a &c&lMUTANT &7in order to resurrect!");
                            MessageUtils.message(player, "&e&m----------------------------------------------------");

                            SoundPlayer.play(player, Sound.NOTE_PLING, 25);
                        }

                        if (resurrectCountdown == 0) {
                            task.cancel();

                            MessageUtils.message(player, "&e&m----------------------------------------------------");
                            io.anw.Survivor.Utils.MessageUtils.message(player, "You did not kill a &c&lMUTANT &7in time! The god of the &cMutants &7has caught you, the &c&lRESURRECTION &7has failed.");
                            MessageUtils.message(player, "&e&m----------------------------------------------------");

                            Game.getInstance().killPlayer(player, false, Game.DeathType.RESURRECTOR_NORMAL, "");
                        }

                        resurrectCountdown--;
                    }
                }, 0L, 20L);

                SoundPlayer.play(player, Sound.LEVEL_UP, 25);
            }
        } catch (NullPointerException ignored) { }
    }

}
