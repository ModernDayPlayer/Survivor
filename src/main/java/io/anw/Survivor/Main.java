package io.anw.Survivor;

import io.anw.Core.Bukkit.AuroraPlugin;
import io.anw.Core.Bukkit.Utils.Bungee.BungeeManager;
import io.anw.Core.Bukkit.Utils.Bungee.BungeeRequestManager;
import io.anw.Survivor.API.Commands.List;
import io.anw.Survivor.API.Utils.Conditions;
import io.anw.Survivor.Commands.GameCommands;
import io.anw.Survivor.Commands.Hub;
import io.anw.Survivor.Commands.LocationCommands;
import io.anw.Survivor.Commands.Stats;
import io.anw.Survivor.Game.Game;
import io.anw.Survivor.Game.GameState;
import io.anw.Survivor.Listeners.Game.AbilityHandlers;
import io.anw.Survivor.Listeners.Game.BowHandler;
import io.anw.Survivor.Listeners.Game.Checks.GeneralChecks;
import io.anw.Survivor.Listeners.Game.DamageHandler;
import io.anw.Survivor.Listeners.Game.GameChatting;
import io.anw.Survivor.Listeners.Inventory.Voting;
import io.anw.Survivor.Listeners.Items;
import io.anw.Survivor.Listeners.Join;
import io.anw.Survivor.Listeners.Leave;
import io.anw.Survivor.Shop.SurvivorShop;
import io.anw.Survivor.Utils.RedFactory;
import io.anw.Survivor.Utils.SQL.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;

import java.io.File;

public class Main extends AuroraPlugin {

    private static Main instance;
    public static Main getInstance() {
        return instance;
    }

    private static BungeeManager bungeeManager;
    public static BungeeManager getBungeeManager() {
        return bungeeManager;
    }

    public File configFile;
    public FileConfiguration Config;
    public File dataFile;
    public FileConfiguration Data;

    private RedFactory redFactory;
    public RedFactory getRedFactory() {
        return redFactory;
    }

    private Conditions conditionManager = new Conditions(this);
    public Conditions getConditionManager() {
        return this.conditionManager;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        bungeeManager = new BungeeRequestManager(this);

        configFile = new File(getDataFolder(), "config.yml");
        dataFile = new File(getDataFolder(), "data.yml");
        getConfigManager().addFile(configFile);
        getConfigManager().addFile(dataFile);
        try {
            getConfigManager().firstRun();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Config = getConfigManager().getConfigFile("config.yml");
        Data = getConfigManager().getConfigFile("data.yml");
        getConfigManager().load();
        getConfigManager().save();

        DatabaseManager.getInstance().checkDatabase();

        addCommandClass(GameCommands.class);
        addCommandClass(LocationCommands.class);
        addCommandClass(Hub.class);
        addCommandClass(Stats.class);
        addCommandClass(List.class);
        registerCommands();

        // GAME - CHECKS
        addListener(new GeneralChecks());

        // GAME - INVENTORY MENUS
        addListener(new SurvivorShop());
        addListener(new Voting());

        // GAME - REGULAR
        addListener(new AbilityHandlers());
        addListener(new BowHandler());
        addListener(new DamageHandler());
        addListener(new GameChatting());

        // GAME - JOIN/LEAVE
        addListener(new Join());
        addListener(new Leave());

        // GAME - ITEMS
        addListener(new Items());

        addListener(getConditionManager());
        registerListeners();

        Game.getInstance().setState(GameState.Waiting);
        Game.getInstance().initScoreboardWaiting();

        redFactory = new RedFactory(this);
    }

    @Override
    public void onDisable() {
        DatabaseManager.getInstance().updateState(GameState.Restarting);

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                entity.remove();
            }
        }
    }

}
