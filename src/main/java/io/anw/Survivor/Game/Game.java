package io.anw.Survivor.Game;

import io.anw.Core.Bukkit.Utils.Chat.MessageUtils;
import io.anw.Core.Bukkit.Utils.Chat.StringUtils;
import io.anw.Core.Bukkit.Utils.ItemStack.ItemUtils;
import io.anw.Core.Bukkit.Utils.Misc.BossBarUtils;
import io.anw.Core.Bukkit.Utils.Misc.FireworkEffectPlayer;
import io.anw.Core.Bukkit.Utils.Misc.SoundPlayer;
import io.anw.Core.Bukkit.Utils.Objects.Particle;
import io.anw.Core.Bukkit.Utils.UUID.NameUtility;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import io.anw.Permissions.API.PermissionsAPI;
import io.anw.Permissions.Objects.Group;
import io.anw.Survivor.API.SurvivorAPI;
import io.anw.Survivor.Utils.AGameUtils;
import io.anw.Survivor.Listeners.Game.Sessions.SessionManager;
import io.anw.Survivor.Main;
import io.anw.Survivor.Objects.SurvivorMap;
import io.anw.Survivor.Utils.LocationUtils;
import io.anw.Survivor.Utils.SQL.DatabaseManager;
import io.anw.Survivor.Utils.TimeUtil;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.util.*;

public class Game {

    private static Game instance = new Game();
    public static Game getInstance() {
        return instance;
    }

    private List<UUID> inGame = new ArrayList<>();
    private List<UUID> survivors = new ArrayList<>();
    private List<UUID> mutants = new ArrayList<>();
    private List<UUID> resurrected = new ArrayList<>();
    private List<UUID> haveResurrected = new ArrayList<>();
    private List<UUID> alreadyChosenStartingMutants = new ArrayList<>();

    private GameState state = GameState.Waiting;

    private SurvivorMap map;
    private Map<SurvivorMap, Integer> mapVotes = new HashMap<>();
    private Map<UUID, SurvivorMap> playerMapVotes = new HashMap<>();
    private List<UUID> voted = new ArrayList<>();

    private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    private Objective gameObjective = scoreboard.registerNewObjective("survivor", "dummy");

    private BukkitTask task;

    private int votingCountdown = 60;
    private int chooseMutantCountdown = 10;
    private int gameCountdown = Main.getInstance().getConfigManager().getConfigFile("config.yml").getInt("Game-Time-Seconds");

    public List<UUID> getInGame() {
        return this.inGame;
    }

    public List<UUID> getSurvivors() {
        return this.survivors;
    }

    public List<UUID> getMutants() {
        return this.mutants;
    }

    public List<UUID> getResurrectors() {
        return this.resurrected;
    }

    public List<UUID> getHaveResurrected() {
        return this.haveResurrected;
    }

    public List<UUID> getVoted() {
        return this.voted;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Objective getGameObjective() {
        return gameObjective;
    }

    public Map<SurvivorMap, Integer> getMapVotes() {
        return this.mapVotes;
    }

    public SurvivorMap getMap() {
        return this.map;
    }

    public Map<UUID, SurvivorMap> getPlayerMapVotes() {
        return this.playerMapVotes;
    }

    public Set<SurvivorMap> getMapsInVotes() {
        return mapVotes.keySet();
    }

    public SurvivorMap getMapInVotes(String name) {
        for (SurvivorMap map : getMapsInVotes()) {
            if (map.getName().equalsIgnoreCase(name)) {
                return map;
            }
        }

        return null;
    }

    public void addToGame(Player player) {
        inGame.add(UUIDUtility.getUUID(player.getName()));
    }

    public void removeFromGame(Player player) {
        if (inGame.contains(UUIDUtility.getUUID(player.getName()))) {
            inGame.remove(UUIDUtility.getUUID(player.getName()));
        }
    }

    public void addToSurvivors(Player player) {
        survivors.add(UUIDUtility.getUUID(player.getName()));
    }

    public void removeFromSurvivors(Player player) {
        if (survivors.contains(UUIDUtility.getUUID(player.getName()))) {
            survivors.remove(UUIDUtility.getUUID(player.getName()));
        }
    }

    public void addToMutants(Player player) {
        mutants.add(UUIDUtility.getUUID(player.getName()));
    }

    public void removeFromMutants(Player player) {
        if(mutants.contains(UUIDUtility.getUUID(player.getName()))) {
            mutants.remove(UUIDUtility.getUUID(player.getName()));
        }
    }

    public void addToResurrectors(Player player) {
        resurrected.add(UUIDUtility.getUUID(player.getName()));
    }

    public void removefromResurrectors(Player player) {
        if (resurrected.contains(UUIDUtility.getUUID(player.getName()))) {
            resurrected.remove(UUIDUtility.getUUID(player.getName()));
        }
    }

    public void addToHaveResurrected(Player player) {
        haveResurrected.add(UUIDUtility.getUUID(player.getName()));
    }

    /*
    ############################################################
    # +------------------------------------------------------+ #
    # |               Misclleanous Game Methods              | #
    # +------------------------------------------------------+ #
    ############################################################
     */


    public GameState getState() {
        return this.state;
    }

    public void setState(GameState state) {
        this.state = state;
        DatabaseManager.getInstance().updateState(state);
    }

    public void teleportToLobby(Player player) {
        if (getLobbySpawn() != null) {
            player.teleport(getLobbySpawn());
        } else {
            MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "&c&lERROR : &7Lobby spawn not set! Please notify an admin!");
        }
    }

    public Location getLobbySpawn() {
        return Main.getInstance().Data.getString("LOBBY_SPAWN.WORLD") == null ? null : LocationUtils.getWaitingLobbyLocation();
    }

    public int getTokenAmount(Player player) {
        UUID player_uuid = UUIDUtility.getUUID(player.getName());
        String group = PermissionsAPI.getUser(player_uuid).getGroup().getGroupType().getName();
        return group.equals("default") ? 1 : group.equals("VIP") ? 2 : group.equals("EVIP") ? 3 : 4;
    }


    /*
    ############################################################
    # +------------------------------------------------------+ #
    # |                   Scoreboard Methods                 | #
    # +------------------------------------------------------+ #
    ############################################################
     */

    public void initScoreboardWaiting() {
        for (OfflinePlayer player : scoreboard.getPlayers()) {
            scoreboard.resetScores(player);
        }

        gameObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        gameObjective.setDisplayName(StringUtils.colorize("&6Survivor &7: &aWaiting"));

        Score s = gameObjective.getScore(Bukkit.getOfflinePlayer(StringUtils.colorize("&aWaiting")));
        s.setScore(Game.getInstance().getInGame().size());

        Score s1 = gameObjective.getScore(Bukkit.getOfflinePlayer(StringUtils.colorize("&cMinimum")));
        s1.setScore(Main.getInstance().getConfigManager().getConfigFile("config.yml").getInt("Minimum-Start"));

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(getScoreboard());
        }
    }

    public void updateScoreboardWaiting() {
        Score s = gameObjective.getScore(Bukkit.getOfflinePlayer(StringUtils.colorize("&aWaiting")));
        s.setScore(Game.getInstance().getInGame().size());
    }

    private void initScoreboardVote() {
        for (OfflinePlayer player : scoreboard.getPlayers()) {
            scoreboard.resetScores(player);
        }

        gameObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        gameObjective.setDisplayName(StringUtils.colorize("&6Survivor &7: &aVoting"));

        for(SurvivorMap map : SurvivorMap.getAllMaps()) {
            Score s = gameObjective.getScore(Bukkit.getOfflinePlayer(StringUtils.colorize("&b&l" + map.getName())));
            s.setScore(0);
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(getScoreboard());
        }
    }


    public void updateScoreboardVotes() {
        for(SurvivorMap map : SurvivorMap.getAllMaps()) {
            Score s = gameObjective.getScore(Bukkit.getOfflinePlayer(StringUtils.colorize("&b&l" + map.getName())));
            s.setScore(mapVotes.get(getMapInVotes(map.getName())));
        }
    }

    private void initScoreboardStarting() {
        for (OfflinePlayer player : scoreboard.getPlayers()) {
            scoreboard.resetScores(player);
        }

        gameObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        gameObjective.setDisplayName(StringUtils.colorize("&6Survivor &7: &aStarting"));

        Score s1 = gameObjective.getScore(Bukkit.getOfflinePlayer(StringUtils.colorize("&aSurvivors")));
        s1.setScore(getSurvivors().size());

        Score s2 = gameObjective.getScore(Bukkit.getOfflinePlayer(StringUtils.colorize("&cMutants")));
        s2.setScore(getMutants().size());

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(getScoreboard());
        }
    }

    public void updateScoreboardStarting() {
        Score s1 = gameObjective.getScore(Bukkit.getOfflinePlayer(StringUtils.colorize("&aSurvivors")));
        s1.setScore(getSurvivors().size());

        Score s2 = gameObjective.getScore(Bukkit.getOfflinePlayer(StringUtils.colorize("&cMutants")));
        s2.setScore(getMutants().size());
    }

    private void initScoreboardGame() {
        for (OfflinePlayer player : scoreboard.getPlayers()) {
            scoreboard.resetScores(player);
        }

        gameObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        gameObjective.setDisplayName(StringUtils.colorize("&6Survivor &7: &aIn-Game"));

        Score s1 = gameObjective.getScore(Bukkit.getOfflinePlayer(StringUtils.colorize("&aSurvivors")));
        s1.setScore(getSurvivors().size());

        Score s2 = gameObjective.getScore(Bukkit.getOfflinePlayer(StringUtils.colorize("&cMutants")));
        s2.setScore(getMutants().size());

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(getScoreboard());
        }
    }

    public void updateScoreboardGame() {
        Score s1 = gameObjective.getScore(Bukkit.getOfflinePlayer(StringUtils.colorize("&aSurvivors")));
        s1.setScore(getSurvivors().size());

        Score s2 = gameObjective.getScore(Bukkit.getOfflinePlayer(StringUtils.colorize("&cMutants")));
        s2.setScore(getMutants().size());
    }

    /*
    ############################################################
    # +------------------------------------------------------+ #
    # |                   Voting Handling                    | #
    # +------------------------------------------------------+ #
    ############################################################
     */

    public int getVoteValue(Player player) {
        Group group = PermissionsAPI.getUser(UUIDUtility.getUUID(player.getName())).getGroup();
        return group.getGroupType().getName().equalsIgnoreCase("default") ? Main.getInstance().getConfigManager().getConfigFile("config.yml").getInt("Vote-Token-Amount-Default") : group.getGroupType().getName().equalsIgnoreCase("VIP") ? Main.getInstance().getConfigManager().getConfigFile("config.yml").getInt("Vote-Token-Amount-VIP") : group.getGroupType().getName().equalsIgnoreCase("EVIP") ? Main.getInstance().getConfigManager().getConfigFile("config.yml").getInt("Vote-Token-Amount-EVIP") : Main.getInstance().getConfigManager().getConfigFile("config.yml").getInt("Vote-Token-Amount-Staff");
    }

    public SurvivorMap getHighestVote(Map<SurvivorMap, Integer> map) {
        SurvivorMap highest = null;
        for(SurvivorMap survivorMap : map.keySet()) {
            if(highest == null || map.get(survivorMap) > map.get(highest)) {
                highest = survivorMap;
            }
        }
        return highest;
    }

    public void startVoting() {
        initScoreboardVote();
        setState(GameState.Voting);
        AGameUtils.broadcast("Voting has started! Vote for your favorite map!");

        for(Player player : Bukkit.getOnlinePlayers()) {
            SoundPlayer.play(player, Sound.NOTE_PLING, 25);

            if (BossBarUtils.hasBar(player)) {
                BossBarUtils.destroyDragon(player);
            }

            BossBarUtils.setBar(player, "&6&lMCAURORA.NET &8&l- &7&lVoting &7on &7&l" + Main.getInstance().getConfigManager().getConfigFile("config.yml").getString("Server-Name"), 200);
        }

        for(SurvivorMap map : SurvivorMap.getAllMaps()) {
            mapVotes.put(map, 0);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if(votingCountdown == 45 || votingCountdown == 30 || votingCountdown == 15 || (votingCountdown <= 5 && votingCountdown > 0)) {
                    AGameUtils.broadcast("You have &6" + votingCountdown + " &7seconds left to vote!");
                    if(votingCountdown <= 5 && votingCountdown != 0) {
                        for(Player player : Bukkit.getOnlinePlayers()) {
                            SoundPlayer.play(player, Sound.NOTE_PLING, 25);
                        }
                    }
                }

                if(votingCountdown == 50 || votingCountdown == 35 || votingCountdown == 20 || votingCountdown == 10) {
                    AGameUtils.broadcastMessage("&e&m----------------------------------------------------", "&a&lCurrent Map Votes", "");
                    for (SurvivorMap map : getMapsInVotes()) {
                        AGameUtils.broadcastMessage("&b" + map.getName() + " &7» &e" + getMapVotes().get(map));
                    }
                    AGameUtils.broadcastMessage("&e&m----------------------------------------------------");

                    for (Player online : Bukkit.getOnlinePlayers()) {
                        SoundPlayer.play(online, Sound.NOTE_SNARE_DRUM, 5);
                    }
                }

                if(votingCountdown == 0) {
                    if (getInGame().size() < Main.getInstance().Config.getInt("Minimum-Start")) {
                        votingCountdown = 30;
                        AGameUtils.broadcast("There are not enough players to start the game, resetting countdown...");
                        AGameUtils.broadcast("Required Players &8&l: &6" + Main.getInstance().Config.getInt("Minimum-Start"));
                        return;
                    }

                    this.cancel();
                    SurvivorMap winningMap = getHighestVote(getMapVotes());
                    map = winningMap;

                    AGameUtils.broadcast("Voting has finished! The map &6" + winningMap.getName() + " &7by &6" + winningMap.getAuthor() + " &7won the vote!");
                    AGameUtils.broadcast("&oLoading map &6&o" + winningMap.getName() + "&7&o...");

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (BossBarUtils.hasBar(player)) {
                            BossBarUtils.destroyDragon(player);
                        }

                        BossBarUtils.setBar(player, "&7&m-----&r &6&lGAME STARTING &7&m-----", 200.0F);
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (BossBarUtils.hasBar(player)) {
                                    BossBarUtils.destroyDragon(player);
                                }
                            }

                            startMutate();
                        }
                    }.runTaskLater(Main.getInstance(), 20 * 5);
                }

                votingCountdown--;
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    /*
    ############################################################
    # +------------------------------------------------------+ #
    # |                      Game Methods                    | #
    # +------------------------------------------------------+ #
    ############################################################
     */

    public void teleportToRandomSpot(Player player) {
        int rand = Main.getInstance().rand().nextInt(getMap().getSpawns().size());
        player.teleport(getMap().getSpawns().get(rand).add(0, 1.5, 0));
        SoundPlayer.play(player, Sound.FIREWORK_LARGE_BLAST, 5);
        Particle.FIREWORKS_SPARK.play(player.getLocation());
    }

    @SuppressWarnings("deprecation")
    public static void disguisePlayer(Player player) {
        if (!DisguiseAPI.isDisguised(player)) {
            MobDisguise disguise = new MobDisguise(DisguiseType.valueOf(SessionManager.getMutantSession(player).getModel()));
            DisguiseAPI.disguiseToAll(player, disguise);
        } else {
            DisguiseAPI.undisguiseToAll(player);
            disguisePlayer(player);
        }
    }

    public static void removeDisguisePlayer(Player p) {
        DisguiseAPI.undisguiseToAll(p);
    }

    @SuppressWarnings("deprecation")
    public static boolean isPlayerDisguised(Player pl) {
        return DisguiseAPI.isDisguised(pl);
    }

    public void startMutate() {
        initScoreboardStarting();
        setState(GameState.Starting);

        for (Player player : Bukkit.getOnlinePlayers()) {
            teleportToRandomSpot(player);

            player.getInventory().clear();

            player.getInventory().setItem(0, SessionManager.getSurvivorSession(player).getWeapon());
            player.getInventory().setItem(1, ItemUtils.createItemStack("&a&lSurvivor's Bow", Material.BOW));
            player.getInventory().setItem(8, ItemUtils.createItemStack("&a&lSurvivor's Ammo", Material.ARROW, 25));

            player.getInventory().setItem(2, SessionManager.getSurvivorSession(player).getAbility());

            player.getInventory().setHelmet(SessionManager.getSurvivorSession(player).getHelmet());
            player.getInventory().setChestplate(SessionManager.getSurvivorSession(player).getChestplate());
            player.getInventory().setLeggings(SessionManager.getSurvivorSession(player).getLeggings());
            player.getInventory().setBoots(SessionManager.getSurvivorSession(player).getBoots());
        }

        new BukkitRunnable() {
            float minusBar = 0.0F;

            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.setLevel(chooseMutantCountdown);
                    player.setExp(0);
                }

                if (chooseMutantCountdown == 10) {
                    AGameUtils.broadcastMessage("&e&m----------------------------------------------------", "&a&lMap Information", "");
                    AGameUtils.broadcastMessage("&bMap Name &7» &e" + getMap().getName());
                    AGameUtils.broadcastMessage("&bMap Author &7» &e" + getMap().getAuthor());
                    AGameUtils.broadcastMessage("&bLink &7» &e" + getMap().getLink());
                    AGameUtils.broadcastMessage("&e&m----------------------------------------------------");
                    AGameUtils.broadcastMessage(" ");
                    AGameUtils.broadcast("Mutating has started! A random mutant will be chosen in &610 &7seconds. Your objective is to survive the infection!");

                    for(Player online : Bukkit.getOnlinePlayers()) {
                        SoundPlayer.play(online, Sound.NOTE_SNARE_DRUM, 5);
                    }
                }

                else if (chooseMutantCountdown <= 5 && chooseMutantCountdown != 0) {
                    AGameUtils.broadcast("The mutant(s) will be chosen in &6" + chooseMutantCountdown + " &7seconds...");

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        SoundPlayer.play(player, Sound.NOTE_PLING, 25);
                    }
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (BossBarUtils.hasBar(player)) {
                        BossBarUtils.destroyDragon(player);
                    }

                    BossBarUtils.setBar(player, "&c&lThe mutant(s) will be chosen in &a&l" + chooseMutantCountdown + " &c&lseconds...", 200.0F - minusBar);
                }

                if (chooseMutantCountdown == 0) {
                    this.cancel();
                    if (getInGame().size() >= Main.getInstance().getConfigManager().getConfigFile("config.yml").getInt("Minimum-Start")) {
                        double peopleToInfect = getInGame().size() * (Main.getInstance().getConfigManager().getConfigFile("config.yml").getInt("Percent-Of-People-To-Mutate") / 100.0f);
                        if (peopleToInfect == 0) {
                            peopleToInfect = 1;
                        }

                        while (getMutants().size() < peopleToInfect) {
                            int rand = Main.getInstance().rand().nextInt(getInGame().size());
                            Player player = Bukkit.getPlayer(getInGame().get(rand));
                            alreadyChosenStartingMutants.add(UUIDUtility.getUUID(player.getName()));

                            if (alreadyChosenStartingMutants.contains(UUIDUtility.getUUID(player.getName()))) {
                                continue;
                            }

                            killPlayer(player, false, DeathType.INFECTION, (String) null);
                        }

                        StringBuilder sbMutants = new StringBuilder();
                        int i = 0;

                        for (UUID uuid : getMutants()) {
                            i++;
                            sbMutants.append(NameUtility.getName(uuid));

                            if (i == getMutants().size()) {
                                sbMutants.append(".");
                            } else {
                                sbMutants.append(", ");
                            }
                        }

                        AGameUtils.broadcast("&7The starting &c&lMUTANT(s) &7that have been chosen are: &c&l" + sbMutants.toString());
                        //broadcastGame(MessageUtils.MessageType.GOOD, "&c&l" + player.getName() + " &7has been chosen as the starting mutant!");

                        alreadyChosenStartingMutants.clear();
                        setState(GameState.In_Game);
                        startGameCountDown();
                    } else {
                        AGameUtils.broadcast("Not enough players to start! We'll give you a 2 token consolation for playing.");

                        for (Player online : Bukkit.getOnlinePlayers()) {
                            DatabaseManager.getTokenAPI().addTokens(UUIDUtility.getUUID(online.getName()), 2);
                        }

                        endGame();
                    }
                }

                chooseMutantCountdown--;
                minusBar += 200.0F / 10.0F;
                minusBar = minusBar >= 200.0F ? 199.0F : minusBar;
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    public void startGameCountDown() {
        initScoreboardGame();

        task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                float minusBar = 0.0F;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.setLevel(gameCountdown);
                    player.setExp(0);

                    if (BossBarUtils.hasBar(player)) {
                        BossBarUtils.destroyDragon(player);
                    }

                    BossBarUtils.setBar(player, "&8(&c&lSURVIVOR&8): &6&l" + TimeUtil.formatTime(gameCountdown) + " &7&luntil the game ends!", 200.0F - minusBar);
                }

                if(gameCountdown == 0) {
                    task.cancel();
                    AGameUtils.broadcast("The &a&lSURVIVORS have prevailed against the &c&lMUTANTS&7! &a&lSURVIVORS WIN!");
                    for(Player online : Bukkit.getOnlinePlayers()) {
                        if (!Game.getInstance().getMutants().contains(UUIDUtility.getUUID(online.getName()))) {
                            SurvivorAPI.setWins(UUIDUtility.getUUID(online.getName()), SurvivorAPI.getWins(UUIDUtility.getUUID(online.getName())) + 1);
                        }

                        if (BossBarUtils.hasBar(online)) {
                            BossBarUtils.destroyDragon(online);
                        }

                        BossBarUtils.setBar(online, "&8(&c&lSURVIVOR&8): &a&lTHE SURVIVORS HAVE WON!", 200);
                    }
                    endGame();
                }
                gameCountdown--;
                minusBar += 200.0F / Main.getInstance().getConfigManager().getConfigFile("config.yml").getInt("Game-Time-Seconds");
                minusBar = minusBar >= 200.0F ? 199.0F : minusBar;
            }
        }, 0L, 20L);
    }

    public void endGame() {
        Bukkit.getScheduler().cancelTask(task.getTaskId());

        for (UUID uuid : getInGame()) {
            Player player = Bukkit.getPlayer(uuid);
            player.setVelocity(new Vector(0, 1.5, 0));
            player.setAllowFlight(true);
            player.setFlying(true);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setMaxHealth(20F);
            player.setHealth(20F);
            player.setFoodLevel(20);
            player.setFireTicks(0);
            player.setLevel(0);
            player.setExp(0F);
            removeDisguisePlayer(player);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            Main.getInstance().getConditionManager().setNoChatIndividual(player, false);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : getInGame()) {
                    Player player = Bukkit.getPlayer(uuid);
                    DatabaseManager.getTokenAPI().addTokens(UUIDUtility.getUUID(player.getName()), Game.getInstance().getTokenAmount(player));
                    FireworkEffectPlayer.playToLocation(player.getLocation().add(0, 12.5, 0), FireworkEffect.builder().withColor(Color.fromRGB(Main.getInstance().rand().nextInt(255), Main.getInstance().rand().nextInt(255), Main.getInstance().rand().nextInt(255))).with(FireworkEffect.Type.BALL_LARGE).withFlicker().build());
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);


        setState(GameState.Restarting);
        AGameUtils.broadcast("You have been playing &6" + getMap().getName() + " &7by &6" + map.getAuthor() + "&7!");

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    Main.getBungeeManager().sendToServer(online, "lobby");
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().shutdown();
                    }
                }.runTaskLater(Main.getInstance(), 20);
            }
        }.runTaskLater(Main.getInstance(), 20 * 10);
    }

    /*
    ############################################################
    # +------------------------------------------------------+ #
    # |                   Killing Methods                    | #
    # +------------------------------------------------------+ #
    ############################################################
     */

    public static enum DeathType { INFECTION, SURVIVOR, RESURRECTOR_KILLED, RESURRECTOR_NORMAL }

    public void killPlayer(final Player player, boolean message, DeathType type, UUID killer) {
        teleportToRandomSpot(player);
        player.setMaxHealth(20.0);
        player.setHealth(20.0);
        player.setFireTicks(0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 2, 10));
        player.getInventory().clear();
        player.setFoodLevel(20);
        player.setLevel(gameCountdown);
        player.setExp(0);
        player.getInventory().setArmorContents(null);

        player.getInventory().setItem(0, SessionManager.getMutantSession(player).getWeapon());

        player.getInventory().setHelmet(SessionManager.getMutantSession(player).getHelmet());
        player.getInventory().setChestplate(SessionManager.getMutantSession(player).getChestplate());
        player.getInventory().setLeggings(SessionManager.getMutantSession(player).getLeggings());
        player.getInventory().setBoots(SessionManager.getMutantSession(player).getBoots());

        if (type == DeathType.INFECTION) {
            if (DatabaseManager.getInstance().getResurrectionScrolls(UUIDUtility.getUUID(player.getName())) != 0 && !haveResurrected.contains(UUIDUtility.getUUID(player.getName()))) {
                player.getInventory().setItem(8, ItemUtils.createItemStack("&c&lResurrection Scroll &7(Right Click)", Arrays.asList("&cUse your resurrection scroll to resurrect temporaily as a Survivor! You will be transformed into a Survivor, and will have only 30 seconds to kill another Mutant and return to the Survivor team. Be careful! Do your task quickly, or else you will be caught by the god of the Mutants and remain a Mutant for the remainder of the game! Decide your fate."), Material.PAPER));
            }

            disguisePlayer(player);
            removeFromSurvivors(player);
            addToMutants(player);
            SoundPlayer.play(player, Sound.ZOMBIE_INFECT);
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 10));

            if (message) {
                AGameUtils.broadcast("&a&l" + player.getName() + " &7has been infected by &c&l" + killer.toString() + "&7!");
            }
        }

        else if (type == DeathType.SURVIVOR) {
            if (message) {
                AGameUtils.broadcast("&c&l" + player.getName() + " &7was killed by &a&l" + killer.toString() + "&7!");
            }
        }

        else if (type == DeathType.RESURRECTOR_KILLED) {
            if (Main.getInstance().getRedFactory().isRed(player)) {
                Main.getInstance().getRedFactory().removeRed(player);
            }

            disguisePlayer(player);
            removefromResurrectors(player);

            if (!haveResurrected.contains(UUIDUtility.getUUID(player.getName()))) {
                haveResurrected.add(UUIDUtility.getUUID(player.getName()));
            }

            SoundPlayer.play(player, Sound.GHAST_SCREAM);
            SoundPlayer.play(player, Sound.ZOMBIE_INFECT);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 10));

            AGameUtils.broadcast("&c&l" + player.getName() + " &7was killed by &a&l" + killer.toString() + "&7!");
            AGameUtils.broadcast("&a&l" + killer.toString() + " &7stopped &c&l" + player.getName() + "&7's &c&lRESURRECTION&7!");
        }

        else if (type == DeathType.RESURRECTOR_NORMAL) {
            if (Main.getInstance().getRedFactory().isRed(player)) {
                Main.getInstance().getRedFactory().removeRed(player);
            }

            disguisePlayer(player);
            removefromResurrectors(player);

            if (!haveResurrected.contains(UUIDUtility.getUUID(player.getName()))) {
                haveResurrected.add(UUIDUtility.getUUID(player.getName()));
            }

            SoundPlayer.play(player, Sound.GHAST_SCREAM);
            SoundPlayer.play(player, Sound.ZOMBIE_INFECT);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 10));

            AGameUtils.broadcast("&c&l" + player.getName() + "&7's &c&lRESURRECTION &7has failed! They did not kill a &cMutant &7in time!");
        }

        SurvivorAPI.setDeaths(UUIDUtility.getUUID(player.getName()), SurvivorAPI.getDeaths(UUIDUtility.getUUID(player.getName())) + 1);
    }

    public void killPlayer(final Player player, boolean message, DeathType type, String killer) {
        teleportToRandomSpot(player);
        player.setMaxHealth(20F);
        player.setHealth(20F);
        player.setFireTicks(0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 2, 10));
        player.getInventory().clear();
        player.setFoodLevel(20);
        player.setLevel(gameCountdown);
        player.setExp(0);
        player.getInventory().setArmorContents(null);

        if (DatabaseManager.getInstance().getResurrectionScrolls(UUIDUtility.getUUID(player.getName())) != 0 && !haveResurrected.contains(UUIDUtility.getUUID(player.getName()))) {
            DatabaseManager.getInstance().setResurrectionScrolls(UUIDUtility.getUUID(player.getName()), DatabaseManager.getInstance().getResurrectionScrolls(UUIDUtility.getUUID(player.getName())) - 1);
            player.getInventory().setItem(8, ItemUtils.createItemStack("&c&lResurrection Scroll &7(Right Click)", Arrays.asList("&cUse your resurrection scroll to resurrect temporaily",
                    "as a Survivor! You will be transformed into a Survivor, ",
                    "and will have only 2 minutes to kill another Mutant and return to the",
                    "Survivor team. Be careful! Do your task quickly, or else you will be caught",
                    "by the god of the Mutants and remain a Mutant for the remainder of the",
                    "game! Decide your fate."), Material.PAPER));
        }

        player.getInventory().setItem(0, SessionManager.getMutantSession(player).getWeapon());

        player.getInventory().setHelmet(SessionManager.getMutantSession(player).getHelmet());
        player.getInventory().setChestplate(SessionManager.getMutantSession(player).getChestplate());
        player.getInventory().setLeggings(SessionManager.getMutantSession(player).getLeggings());
        player.getInventory().setBoots(SessionManager.getMutantSession(player).getBoots());

        if (type == DeathType.INFECTION) {
            disguisePlayer(player);
            removeFromSurvivors(player);
            addToMutants(player);
            SoundPlayer.play(player, Sound.ZOMBIE_INFECT);
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 10));

            if (message) {
                AGameUtils.broadcast("&a&l" + player.getName() + " &7has been infected by &c&l" + killer + "&7!");
            }
        }

        else if (type == DeathType.SURVIVOR) {
            if (message) {
                AGameUtils.broadcast("&c&l" + player.getName() + " &7was killed by &a&l" + killer + "&7!");
            }
        }

        else if (type == DeathType.RESURRECTOR_KILLED) {
            disguisePlayer(player);
            removefromResurrectors(player);
            SoundPlayer.play(player, Sound.GHAST_SCREAM);
            SoundPlayer.play(player, Sound.ZOMBIE_INFECT);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 10));

            AGameUtils.broadcast("&c&l" + player.getName() + " &7was killed by &a&l" + killer + "&7!");
            AGameUtils.broadcast("&a&l" + killer + " &7stopped &c&l" + player.getName() + "&7's &c&lRESURRECTION&7!");
        }

        else if (type == DeathType.RESURRECTOR_NORMAL) {
            disguisePlayer(player);
            removefromResurrectors(player);
            SoundPlayer.play(player, Sound.GHAST_SCREAM);
            SoundPlayer.play(player, Sound.ZOMBIE_INFECT);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 10));

            AGameUtils.broadcast("&c&l" + player.getName() + "&7's &c&lRESURRECTION &7has failed! They did not kill a &cMutant &7in time!");
        }


        SurvivorAPI.setDeaths(UUIDUtility.getUUID(player.getName()), SurvivorAPI.getDeaths(UUIDUtility.getUUID(player.getName())) + 1);
    }

}
