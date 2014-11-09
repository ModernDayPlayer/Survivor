package io.anw.Survivor.Listeners.Game;

import io.anw.Core.Bukkit.Utils.Chat.MessageUtils;
import io.anw.Core.Bukkit.Utils.Math.MathUtils;
import io.anw.Core.Bukkit.Utils.Misc.FireworkEffectPlayer;
import io.anw.Core.Bukkit.Utils.Misc.SoundPlayer;
import io.anw.Core.Bukkit.Utils.Objects.Particle;
import io.anw.Core.Bukkit.Utils.Reflection.ReflectionUtils;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import io.anw.Survivor.API.Utils.RandomUtils;
import io.anw.Survivor.Game.Game;
import io.anw.Survivor.Game.GameState;
import io.anw.Survivor.Main;
import io.anw.Survivor.Utils.Math.Sphere;
import net.minecraft.server.v1_7_R3.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.*;

public class AbilityHandlers implements Listener {

    private List<UUID> cooldown = new ArrayList<>();
    private List<UUID> runnerCooldown = new ArrayList<>();
    private List<UUID> torcherCooldown = new ArrayList<>();
    private List<UUID> freezeRayCooldown = new ArrayList<>();
    private List<UUID> zombieCooldown = new ArrayList<>();
    private List<UUID> skeletonCooldown = new ArrayList<>();
    private List<UUID> witherCooldown = new ArrayList<>();

    private List<UUID> frozenPlayers = new ArrayList<>();
    private static Map<Integer, Integer> arrowTasks = new HashMap<>();

    ///  THROWABLE EXPLOSIVES  \\\
    ///  THROWABLE EXPLOSIVES  \\\
    ///  THROWABLE EXPLOSIVES  \\\

    @EventHandler
    public void throwableExplosives(PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        e.setCancelled(true);

        try {
            if(!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
            if (e.getItem().getType().equals(Material.TNT) && e.getItem().getItemMeta().getDisplayName().contains("Griefer's Explosives")) {
                if (Game.getInstance().getState() != GameState.In_Game) return;
                if (!cooldown.contains(UUIDUtility.getUUID(player.getName()))) {
                    player.setItemInHand(player.getItemInHand().getAmount() > 1 ? new ItemStack(Material.TNT, player.getItemInHand().getAmount() - 1) : null);
                    TNTPrimed tntPrimed = player.getWorld().spawn(player.getLocation().add(0.5, 0, 0.5), TNTPrimed.class);
                    tntPrimed.setVelocity(player.getLocation().getDirection().multiply(2));
                    tntPrimed.setMetadata("explode", new FixedMetadataValue(Main.getInstance(), false));

                    cooldown.add(UUIDUtility.getUUID(player.getName()));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            cooldown.remove(UUIDUtility.getUUID(player.getName()));
                        }
                    }.runTaskLater(Main.getInstance(), 20 * 15);
                } else {
                    MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Please wait &615 &7seconds for your &6Griefer's Explosives &7ability to charge...");
                }
            }
        } catch (NullPointerException ignored) { }
    }

    @EventHandler
    public void throwableExplosivesExplode(EntityExplodeEvent e) {
        e.setCancelled(true);

        if (e.getEntityType() == EntityType.PRIMED_TNT || e.getEntityType() == EntityType.FIREBALL) {
            if (e.getEntity().hasMetadata("explode")) {
                Particle.HUGE_EXPLOSION.play(e.getLocation(), (float) Math.random());

                for (Player player : Bukkit.getOnlinePlayers()) {
                    SoundPlayer.play(player, Sound.ZOMBIE_WOODBREAK, 25, 1F);
                }
            }
        }
    }

    ///  TORCHER  \\\
    ///  TORCHER  \\\
    ///  TORCHER  \\\

    @EventHandler
    public void torcher(PlayerInteractEvent e) {
        final Player player = e.getPlayer();

        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            try {
                if (e.getItem().getType().equals(Material.BLAZE_ROD) && e.getItem().getItemMeta().getDisplayName().contains("Raging Flameballs")) {
                    if (Game.getInstance().getState() != GameState.In_Game) return;
                    if (!torcherCooldown.contains(UUIDUtility.getUUID(player.getName()))) {
                        Projectile projectile = player.launchProjectile(Fireball.class);
                        projectile.setVelocity(projectile.getVelocity().multiply(3));

                        for (int x = 0; x < RandomUtils.getRandom(2, 3); x++) {
                            Fireball fireball = player.getWorld().spawn(projectile.getLocation().add(Math.random(), 0.0D, Math.random()), Fireball.class);
                            fireball.setVelocity(projectile.getVelocity());
                            fireball.setShooter(player);
                            fireball.setIsIncendiary(false);
                            fireball.setBounce(true);
                            fireball.setYield(0);
                            fireball.setMetadata("explode", new FixedMetadataValue(Main.getInstance(), false));
                        }

                        SoundPlayer.play(player, Sound.WITHER_SHOOT, 25, 100);
                        torcherCooldown.add(UUIDUtility.getUUID(player.getName()));

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                torcherCooldown.remove(UUIDUtility.getUUID(player.getName()));
                            }
                        }.runTaskLater(Main.getInstance(), 20 * 15);
                    } else {
                        MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Please wait &615 &7seconds for your &6Raging Flameballs &7ability to charge...");
                    }
                }
            } catch (NullPointerException ignored) { }
        }
    }

    ///  RUNNER  \\\
    ///  RUNNER  \\\
    ///  RUNNER  \\\

    @EventHandler
    public void runner(PlayerInteractEvent e) {
        final Player player = e.getPlayer();

        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            try {
                if (e.getItem().getType().equals(Material.STICK) && e.getItem().getItemMeta().getDisplayName().contains("Hyperspeed Activator")) {
                    if (Game.getInstance().getState() != GameState.In_Game) return;
                    if (!runnerCooldown.contains(UUIDUtility.getUUID(player.getName()))) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 2));
                        runnerCooldown.add(UUIDUtility.getUUID(player.getName()));

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                runnerCooldown.remove(UUIDUtility.getUUID(player.getName()));
                            }
                        }.runTaskLater(Main.getInstance(), 20 * 25);
                    } else {
                        MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Please wait &625 &7seconds for your &6Hyperspeed Activator &7ability to charge...");
                    }
                }
            } catch (NullPointerException ignored) { }
        }
    }

    ///  ARCANIST  \\\
    ///  ARCANIST  \\\
    ///  ARCANIST  \\\

    @EventHandler
    public void arcanist(PlayerInteractEvent e) {
        final Player player = e.getPlayer();

        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            try {
                if (e.getItem().getType().equals(Material.IRON_HOE) && e.getItem().getItemMeta().getDisplayName().contains("Freeze Ray")) {
                    if (Game.getInstance().getState() != GameState.In_Game) return;
                    if (!freezeRayCooldown.contains(UUIDUtility.getUUID(player.getName()))) {

                        Projectile projectile = player.launchProjectile(Snowball.class);
                        projectile.setVelocity(projectile.getVelocity().multiply(2));

                        for (int x = 0; x < RandomUtils.getRandom(4, 5); x++) {
                            Snowball snowball = player.getWorld().spawn(projectile.getLocation().add(Math.random(), 0.0D, Math.random()), Snowball.class);
                            snowball.setVelocity(projectile.getVelocity());
                            snowball.setShooter(player);
                            snowball.setMetadata("freezeRay", new FixedMetadataValue(Main.getInstance(), false));
                        }

                        SoundPlayer.play(player, Sound.WITHER_SHOOT, 25, 100);
                        player.setMetadata("freezeRay", new FixedMetadataValue(Main.getInstance(), false));
                        freezeRayCooldown.add(UUIDUtility.getUUID(player.getName()));

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.removeMetadata("freezeRay", Main.getInstance());
                                freezeRayCooldown.remove(UUIDUtility.getUUID(player.getName()));
                            }
                        }.runTaskLater(Main.getInstance(), 20 * 30);
                    } else {
                        MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Please wait &630 &7seconds for your &6Freeze Ray &7ability to charge...");
                    }
                }
            } catch (NullPointerException ignored) { }
        }
    }


    /*
    @EventHandler
    public void freezeRayHit(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Projectile && e.getEntity() instanceof Player) {
            if (((Projectile) e.getDamager()).getShooter() instanceof Player) {

                Player damager = (Player) ((Projectile) e.getDamager()).getShooter();
                Player damaged = (Player) e.getEntity();

                if (!damager.hasMetadata("freezeRay")) return;

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

                MessageUtils.message(damager, "You have &b&lFROZEN &6" + damaged.getName() + "&7 using your freeze ray!");

                SurvivorAPI.setPoints(UUIDUtility.getUUID(damager.getName()), SurvivorAPI.getPoints(UUIDUtility.getUUID(damager.getName())) + Game.getInstance().getVoteValue(damager));
                DatabaseManager.getTokenAPI().addTokens(UUIDUtility.getUUID(damager.getName()), Game.getInstance().getTokenAmount(damager));
            }
        }
    }
    */

    @EventHandler
    public void freezeRayHitGround(ProjectileHitEvent e) {
        Projectile projectile = e.getEntity();
        if (!(projectile instanceof Snowball)) return;

        final Snowball snowball = (Snowball) projectile;
        if (!snowball.hasMetadata("freezeRay")) return;

        final Sphere sphere = new Sphere(snowball.getLocation(), 5);
        List<Material> materials = new ArrayList<>();
        materials.add(Material.AIR);
        sphere.fill(Material.ICE, (byte) 0, materials, null);
        snowball.getLocation().getWorld().playSound(snowball.getLocation(), Sound.GLASS, 10.0F, 1.0F);

        for (LivingEntity entity : MathUtils.getNearbyEntities(snowball.getLocation(), 5)) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                frozenPlayers.add(UUIDUtility.getUUID(player.getName()));
                Main.getInstance().getConditionManager().setCanMoveIndividual(player, false);

                MessageUtils.message(player, " ");
                MessageUtils.message(player, " ");
                MessageUtils.message(player, "&e&m----------------------------------------------------");
                MessageUtils.message(player, "You have been &b&lFROZEN &7by a &aSurvivor's &6Freeze Ray&7!");
                MessageUtils.message(player, "You will be thawed in &65 seconds!");
                MessageUtils.message(player, "&e&m----------------------------------------------------");
                MessageUtils.message(player, " ");
                MessageUtils.message(player, " ");
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                List<Material> materials = new ArrayList<>();
                materials.add(Material.ICE);
                sphere.breakBlocks(materials, null);
                snowball.getLocation().getWorld().playSound(snowball.getLocation(), Sound.GLASS, 10.0F, 1.0F);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (frozenPlayers.contains(UUIDUtility.getUUID(player.getName()))) {
                        Main.getInstance().getConditionManager().setCanMoveIndividual(player, true);

                        MessageUtils.message(player, " ");
                        MessageUtils.message(player, "&e&m----------------------------------------------------");
                        MessageUtils.message(player, "You have been &c&lTHAWED&7! You can now move!");
                        MessageUtils.message(player, "&e&m----------------------------------------------------");
                        MessageUtils.message(player, " ");
                    }
                }
            }
        }.runTaskLater(Main.getInstance(), 20 * 5);
    }

    // ZOMBIE
    // ZOMBIE
    // ZOMBIE

    @EventHandler
    public void zombieNetherHealer(PlayerInteractEvent e) {
        final Player player = e.getPlayer();

        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            try {
                if (e.getItem().getType().equals(Material.NETHER_STAR) && e.getItem().getItemMeta().getDisplayName().contains("Zombie's Jaws")) {
                    if (Game.getInstance().getState() != GameState.In_Game) return;
                    if (!zombieCooldown.contains(UUIDUtility.getUUID(player.getName()))) {

                        //TODO: Soon

                        SoundPlayer.play(player, Sound.LEVEL_UP, 5);
                        zombieCooldown.add(UUIDUtility.getUUID(player.getName()));

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                zombieCooldown.remove(UUIDUtility.getUUID(player.getName()));
                            }
                        }.runTaskLater(Main.getInstance(), 20 * 30);
                    } else {
                        MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Please wait &630 &7seconds for your &6Zombie's Jaws &7ability to charge...");
                    }
                }
            } catch (NullPointerException ignored) { }
        }
    }

    // SKELETON EXPLOSIVES
    // SKELETON EXPLOSIVES
    // SKELETON EXPLOSIVES

    @EventHandler
    public void fragballAndArrowHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow && arrowTasks.containsKey(e.getEntity().getEntityId())) {
            Arrow a = (Arrow) e.getEntity();
            Bukkit.getScheduler().cancelTask(arrowTasks.get(a.getEntityId()));
            arrowTasks.remove(a.getEntityId());
            Location l = a.getLocation();

            a.setMetadata("explode", new FixedMetadataValue(Main.getInstance(), false));
            a.getWorld().createExplosion(a.getLocation(), 5.5F);
            a.remove();
        }
    }

    @EventHandler
    public void crossBowShoot(final EntityShootBowEvent e) {
        final Player player = (Player) e.getEntity();

        try {
            if (e.getBow().getItemMeta().getDisplayName().contains("The Bow of the Creepers")) {
                if (Game.getInstance().getState() != GameState.In_Game) return;
                if (!skeletonCooldown.contains(UUIDUtility.getUUID(player.getName()))) {
                    BukkitTask task = new BukkitRunnable() {
                        @Override
                        public void run() {
                            Location l = e.getProjectile().getLocation();

                            PacketPlayOutWorldParticles arrowParticles = new PacketPlayOutWorldParticles("flame", (float) l.getX(), (float) l.getY(), (float) l.getZ(), 0, 0, 0, 0, 1);

                            for(Player player : Bukkit.getOnlinePlayers()) {
                                ReflectionUtils.sendPacket(player, arrowParticles);
                            }
                        }
                    }.runTaskTimer(Main.getInstance(), 0, 1);
                    arrowTasks.put(e.getProjectile().getEntityId(), task.getTaskId());
                    skeletonCooldown.add(UUIDUtility.getUUID(player.getName()));

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            skeletonCooldown.remove(UUIDUtility.getUUID(player.getName()));
                        }
                    }.runTaskLater(Main.getInstance(), 20 * 10);
                } else {
                    MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Please wait &610 &7seconds for your &6Bow of the Creepers &7ability to charge...");
                }
            }
        } catch (NullPointerException ignored) { }

    }

    @EventHandler
    public void fragBallExplode(EntityExplodeEvent e) {
        e.setCancelled(true);

        for(Block block : e.blockList()) {
            if(block.getRelative(BlockFace.UP).getType() == Material.AIR && block.getType().isSolid()) {
                FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation().add(0, 1, 0), block.getType(), block.getData());
                double x = (block.getLocation().getX() - e.getLocation().getX()) / 3,
                        y = 1,
                        z = (block.getLocation().getZ() - e.getLocation().getZ()) / 3;
                fallingBlock.setVelocity(new Vector(x, y, z).normalize());
                fallingBlock.setMetadata("explode", new FixedMetadataValue(Main.getInstance(), false));
                fallingBlock.setDropItem(false);
                e.setYield(0F);
            }
        }
    }

    @EventHandler
    public void fragBallFallingBlock(final EntityChangeBlockEvent event) {
        if ((event.getEntityType() == EntityType.FALLING_BLOCK)) {
            if(event.getEntity().hasMetadata("explode")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.STEP_SOUND, event.getBlock().getType().getId());
                        event.getBlock().setType(Material.AIR);
                    }
                }.runTaskLater(Main.getInstance(), 1);
            }
        }
    }

    // NETHER STAFF
    // NETHER STAFF
    // NETHER STAFF

    @EventHandler
    public void witherstaff(PlayerInteractEvent e) {
        final Player player = e.getPlayer();

        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            try {
                if (e.getItem().getType().equals(Material.STICK) && e.getItem().getItemMeta().getDisplayName().contains("Wrath of the Wither")) {
                    if (Game.getInstance().getState() != GameState.In_Game) return;
                    if (!witherCooldown.contains(UUIDUtility.getUUID(player.getName()))) {
                        SoundPlayer.play(player, Sound.WITHER_SPAWN, 45);
                        final BlockIterator blockIterator = new BlockIterator(player, 25);

                        while (blockIterator.hasNext()) {
                            Block iteratedBlock = blockIterator.next();
                            iteratedBlock.getWorld().playSound(iteratedBlock.getLocation(), Sound.FUSE, 1, 100);
                            PacketPlayOutWorldParticles magicParticles = new PacketPlayOutWorldParticles("witchMagic", (float) iteratedBlock.getLocation().getX(), (float) iteratedBlock.getLocation().getY(), (float) iteratedBlock.getLocation().getZ(), (float) Math.random(), (float) Math.random(), (float) Math.random(), 0, 5);

                            for (Player online : Bukkit.getOnlinePlayers()) {
                                ReflectionUtils.sendPacket(online, magicParticles);
                            }

                            HashSet<LivingEntity> livingEntities = MathUtils.getNearbyEntities(iteratedBlock.getLocation(), 3);
                            if (!blockIterator.hasNext() || iteratedBlock.getType().isSolid() || (livingEntities.size() > 0 && !livingEntities.contains(player))) {
                                //iteratedBlock.getWorld().playEffect(iteratedBlock.getLocation(), Effect.EXPLOSION_HUGE, 25);
                                Particle.HUGE_EXPLOSION.play(iteratedBlock.getLocation(), 25);
                                iteratedBlock.getWorld().playSound(iteratedBlock.getLocation(), Sound.EXPLODE, 50, 1);

                                break;
                            }
                        }

                        Projectile projectile = player.launchProjectile(WitherSkull.class);
                        projectile.setVelocity(projectile.getVelocity().multiply(3));

                        for (int x = 0; x < RandomUtils.getRandom(1, 3); x++) {
                            WitherSkull ws = player.getWorld().spawn(projectile.getLocation().add(Math.random(), 0.0D, Math.random()), WitherSkull.class);
                            ws.setVelocity(projectile.getVelocity());
                            ws.setShooter(player);
                            ws.setIsIncendiary(false);
                            ws.setBounce(true);
                            ws.setYield(0F);
                            ws.setMetadata("customskull", new FixedMetadataValue(Main.getInstance(), false));
                        }

                        witherCooldown.add(UUIDUtility.getUUID(player.getName()));

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                witherCooldown.remove(UUIDUtility.getUUID(player.getName()));
                            }
                        }.runTaskLater(Main.getInstance(), 20 * 35);
                    } else {
                        MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Please wait &635 &7seconds for your &6Wrath of the Wither &7ability to charge...");
                    }
                }
            } catch (NullPointerException ignored) { }
        }
    }



    @EventHandler
    public void witherExplode(EntityExplodeEvent e) {
        e.setCancelled(true);

        if (e.getEntity() instanceof WitherSkull) {
            WitherSkull ws = (WitherSkull) e.getEntity();
            Location loc = e.getLocation();

            if (ws.hasMetadata("customskull")) {
                ws.removeMetadata("customskull", Main.getInstance());
            }

            for (LivingEntity en : MathUtils.getNearbyEntities(loc, 4)) {
                if (en instanceof Player) {
                    Player player = (Player) en;
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 15, 3));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 15, 2));

                    FireworkEffectPlayer.playToLocation(player.getLocation(), FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.GRAY).withFlicker().build());
                }
            }
        }
    }

}
