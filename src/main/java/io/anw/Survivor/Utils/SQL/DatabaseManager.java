package io.anw.Survivor.Utils.SQL;

import io.anw.Core.Bukkit.Utils.Objects.TokenAPI;
import io.anw.Core.Main.BlobUtils;
import io.anw.Core.Main.SQL.DatabaseConnection;
import io.anw.Core.Main.SQL.DatabaseConnectionFactory;
import io.anw.Survivor.Game.GameState;
import io.anw.Survivor.Listeners.Game.Sessions.Ability.AbilityProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Boots.BootsProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Boots.BootsType;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Boots.MutantBootsProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Chestplate.ChestplateProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Chestplate.ChestplateType;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Chestplate.MutantChestplateProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Helmet.HelmetProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Helmet.HelmetType;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Helmet.MutantHelmetProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Leggings.LeggingsProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Leggings.LeggingsType;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Leggings.MutantLeggingsProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Models.ModelProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Models.ModelType;
import io.anw.Survivor.Listeners.Game.Sessions.Weapons.MutantWeaponProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Weapons.WeaponProfile;
import io.anw.Survivor.Listeners.Game.Sessions.Weapons.WeaponType;
import io.anw.Survivor.Main;
import io.anw.Survivor.Listeners.Game.Sessions.Ability.AbilityType;
import io.anw.Survivor.Utils.LoggingUtils;
import io.anw.Survivor.Utils.SerializableItemStack;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager extends DatabaseConnection {

    public DatabaseManager(DatabaseConnectionFactory factory) {
        super(factory);
    }

    private static DatabaseManager instance = new DatabaseManager(
        DatabaseConnectionFactory.builder()
            .withHost(Main.getInstance().Config.getString("MySQL.Address"))
            .withPort(Main.getInstance().Config.getInt("MySQL.Port"))
            .withDatabase(Main.getInstance().Config.getString("MySQL.Database"))
            .withUsername(Main.getInstance().Config.getString("MySQL.Username"))
            .withPassword(Main.getInstance().Config.getString("MySQL.Password"))
    );
    public static DatabaseManager getInstance() {
        return instance;
    }

    private static TokenAPI tokenAPI = new TokenAPI(getInstance());
    public static TokenAPI getTokenAPI() {
        return tokenAPI;
    }

    /**
     * Javadocs coming soon!
     */

    public void checkDatabase() {
        try {
            this.getStatement().execute("CREATE TABLE IF NOT EXISTS survivorData(username VARCHAR(255), kills INTEGER, deaths INTEGER, wins INTEGER, points INTEGER, gamesPlayed INTEGER, resurrectionScrolls INTEGER, abilityType VARCHAR(255), weaponType VARCHAR(255), helmetType VARCHAR(255), chestplateType VARCHAR(255), leggingsType VARCHAR(255), bootsType VARCHAR(255), mutantWeaponType VARCHAR(255), mutantHelmetType VARCHAR(255), mutantChestplateType VARCHAR(255), mutantLeggingsType VARCHAR(255), mutantBootsType VARCHAR(255), model VARCHAR(255), abilities LONGTEXT, weapons LONGTEXT, helmets LONGTEXT, chestplates LONGTEXT, leggings LONGTEXT, boots LONGTEXT, mutantWeapons LONGTEXT, mutantHelmets LONGTEXT, mutantChestplates LONGTEXT, mutantLeggings LONGTEXT, mutantBoots LONGTEXT, models LONGTEXT)");
            this.getStatement().execute("CREATE TABLE IF NOT EXISTS serverData(server VARCHAR(255), type VARCHAR(255), state VARCHAR(255))");
            LoggingUtils.log("Connected to database successfully!");
        } catch (SQLException | ClassNotFoundException e) {
            LoggingUtils.log(Level.SEVERE, "Database connection failed! Shutting down plugin...");
            Main.getInstance().getPluginLoader().disablePlugin(Main.getInstance());
            e.printStackTrace();
        }
    }

    public void enterPlayer(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT COUNT(*) FROM survivorData WHERE username='" + player.toString() + "';");
            rs.next();

            if(rs.getInt(1) == 0) {
                this.getStatement().execute("INSERT INTO survivorData(username, kills, deaths, wins, points, gamesPlayed, resurrectionScrolls, abilityType, weaponType, helmetType, chestplateType, leggingsType, bootsType,  mutantWeaponType, mutantHelmetType, mutantChestplateType, mutantLeggingsType, mutantBootsType, model, abilities, weapons, helmets, chestplates, leggings, boots, mutantWeapons, mutantHelmets, mutantChestplates, mutantLeggings, mutantBoots, models) VALUES('" + player.toString() + "', 0, 0, 0, 0, 0, 0, 'HYPERSPEED', 'WOODEN_SWORD', 'LEATHER_HELMET', 'LEATHER_CHESTPLATE', 'LEATHER_LEGGINGS', 'LEATHER_BOOTS', 'WOODEN_SWORD', 'LEATHER_HELMET', 'LEATHER_CHESTPLATE', 'LEATHER_LEGGINGS', 'LEATHER_BOOTS', 'ZOMBIE', '" + BlobUtils.toString(new AbilityProfile(player)) + "', '" + BlobUtils.toString(new WeaponProfile(player)) + "', '" + BlobUtils.toString(new HelmetProfile(player)) + "', '" + BlobUtils.toString(new ChestplateProfile(player)) + "', '" + BlobUtils.toString(new LeggingsProfile(player)) + "', '" + BlobUtils.toString(new BootsProfile(player)) + "', '" + BlobUtils.toString(new MutantWeaponProfile(player)) + "', '" + BlobUtils.toString(new MutantHelmetProfile(player)) + "', '" + BlobUtils.toString(new MutantChestplateProfile(player)) + "', '" + BlobUtils.toString(new MutantLeggingsProfile(player)) + "', '" + BlobUtils.toString(new MutantBootsProfile(player)) + "', '" + BlobUtils.toString(new ModelProfile(player)) + "')");
                LoggingUtils.log("Entered player " + player.toString() + " into the database!");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean doesExist(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT COUNT(*) FROM survivorData WHERE username='" + player.toString() + "';");
            rs.next();

            return rs.getInt(1) == 1;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getKills(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT kills FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setKills(UUID player, int kills) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT kills FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET kills=" + kills + " WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getDeaths(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT deaths FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setDeaths(UUID player, int deaths) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT deaths FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET deaths=" + deaths + " WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getWins(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT wins FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setWins(UUID player, int wins) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT wins FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET wins=" + wins + " WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getPoints(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT points FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setPoints(UUID player, int points) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT points FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET points=" + points + " WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getGamesPlayed(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT gamesPlayed FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setGamesPlayed(UUID player, int gamesPlayed) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT gamesPlayed FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET gamesPlayed=" + gamesPlayed + " WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getResurrectionScrolls(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT resurrectionScrolls FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setResurrectionScrolls(UUID player, int resurrectionScrolls) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT resurrectionScrolls FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET resurrectionScrolls=" + resurrectionScrolls + " WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
    public String getModel(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT model FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setModel(UUID player, DisguiseType type) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT model FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET model='" + type.toString() + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    */

    /**
     * Get the ability of a player
     *
     * @param player UUID of player to get ability for
     * @return Player's ability
     */
    public AbilityType getAbility(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT abilityType FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return AbilityType.valueOf(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return AbilityType.HYPERSPEED;
    }

    public void setAbility(UUID player, AbilityType type) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT abilityType FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET abilityType='" + type.toString() + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the weapon of a player
     *
     * @param player UUID of player to get weapon for
     * @return Player's weapon
     */
    public WeaponType getWeapon(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT weaponType FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return WeaponType.valueOf(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return WeaponType.WOODEN_SWORD;
    }

    public void setWeapon(UUID player, WeaponType type) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT weaponType FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET weaponType='" + type.toString() + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the weapon of a player
     *
     * @param player UUID of player to get weapon for
     * @return Player's weapon
     */
    public WeaponType getMutantWeapon(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantWeaponType FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return WeaponType.valueOf(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return WeaponType.WOODEN_SWORD;
    }

    public void setMutantWeapon(UUID player, WeaponType type) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantWeaponType FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET mutantWeaponType='" + type.toString() + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the model of a player
     *
     * @param player UUID of player to get model for
     * @return Player's model
     */
    public ModelType getModel(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT model FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return ModelType.valueOf(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ModelType.ZOMBIE;
    }

    public void setModel(UUID player, ModelType type) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT model FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET model='" + type.toString() + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the helmet of a player
     *
     * @param player UUID of player to get helmet for
     * @return Player's helmet
     */
    public HelmetType getHelmet(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT helmetType FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return HelmetType.valueOf(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return HelmetType.LEATHER_HELMET;
    }

    public void setHelmet(UUID player, HelmetType type) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT helmetType FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET helmetType='" + type.toString() + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the chestplate of a player
     *
     * @param player UUID of player to get chestplate for
     * @return Player's chestplate
     */
    public ChestplateType getChestplate(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT chestplateType FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return ChestplateType.valueOf(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ChestplateType.LEATHER_CHESTPLATE;
    }

    public void setChestplate(UUID player, ChestplateType type) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT chestplateType FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET chestplateType='" + type.toString() + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the leggings of a player
     *
     * @param player UUID of player to get leggings for
     * @return Player's leggings
     */
    public LeggingsType getLeggings(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT leggingsType FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return LeggingsType.valueOf(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return LeggingsType.LEATHER_LEGGINGS;
    }

    public void setLeggings(UUID player, LeggingsType type) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT leggingsType FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET leggingsType='" + type.toString() + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get the boots of a player
     *
     * @param player UUID of player to get boots for
     * @return Player's boots
     */
    public BootsType getBoots(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT bootsType FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return BootsType.valueOf(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return BootsType.LEATHER_BOOTS;
    }

    public void setBoots(UUID player, BootsType type) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT bootsType FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET bootsType='" + type.toString() + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the helmet of a player
     *
     * @param player UUID of player to get helmet for
     * @return Player's helmet
     */
    public HelmetType getMutantHelmet(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantHelmetType FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return HelmetType.valueOf(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return HelmetType.LEATHER_HELMET;
    }

    public void setMutantHelmet(UUID player, HelmetType type) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantHelmetType FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET mutantHelmetType='" + type.toString() + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the chestplate of a player
     *
     * @param player UUID of player to get chestplate for
     * @return Player's chestplate
     */
    public ChestplateType getMutantChestplate(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantChestplateType FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return ChestplateType.valueOf(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ChestplateType.LEATHER_CHESTPLATE;
    }

    public void setMutantChestplate(UUID player, ChestplateType type) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantChestplateType FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET mutantChestplateType='" + type.toString() + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the leggings of a player
     *
     * @param player UUID of player to get leggings for
     * @return Player's leggings
     */
    public LeggingsType getMutantLeggings(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantLeggingsType FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return LeggingsType.valueOf(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return LeggingsType.LEATHER_LEGGINGS;
    }

    public void setMutantLeggings(UUID player, LeggingsType type) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantLeggingsType FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET mutantLeggingsType='" + type.toString() + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get the boots of a player
     *
     * @param player UUID of player to get boots for
     * @return Player's boots
     */
    public BootsType getMutantBoots(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantBootsType FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return BootsType.valueOf(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return BootsType.LEATHER_BOOTS;
    }

    public void setMutantBoots(UUID player, BootsType type) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantBootsType FROM survivorData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET mutantBootsType='" + type.toString() + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a player's ability profile
     *
     * @param player UUID of player to get profile for
     * @return Player's ability profile
     */
    public AbilityProfile getAbilityProfile(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT abilities FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return (AbilityProfile) BlobUtils.fromString(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Set a player's ability profile
     *
     * @param player  UUID of player to set ability profile for
     * @param profile Profile to set
     */
    public void setAbilityProfile(UUID player, AbilityProfile profile) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT abilities FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET abilities='" + BlobUtils.toString(profile) + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a player's weapon profile
     *
     * @param player UUID of player to get profile for
     * @return Player's weapon profile
     */
    public WeaponProfile getWeaponProfile(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT weapons FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return (WeaponProfile) BlobUtils.fromString(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Set a player's weapon profile
     *
     * @param player  UUID of player to set weapon profile for
     * @param profile Profile to set
     */
    public void setWeaponProfile(UUID player, WeaponProfile profile) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT weapons FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET weapons='" + BlobUtils.toString(profile) + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a player's mode profile
     *
     * @param player UUID of player to get profile for
     * @return Player's model profile
     */
    public ModelProfile getModelProfile(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT models FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return (ModelProfile) BlobUtils.fromString(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Set a player's model profile
     *
     * @param player  UUID of player to set model profile for
     * @param profile Profile to set
     */
    public void setModelProfile(UUID player, ModelProfile profile) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT models FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET models='" + BlobUtils.toString(profile) + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a player's helmet profile
     *
     * @param player UUID of player to get helmet for
     * @return Player's helmet profile
     */
    public HelmetProfile getHelmetProfile(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT helmets FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return (HelmetProfile) BlobUtils.fromString(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Set a player's helmet profile
     *
     * @param player  UUID of player to set helmet profile for
     * @param profile Profile to set
     */
    public void setHelmetProfile(UUID player, HelmetProfile profile) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT helmets FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET helmets='" + BlobUtils.toString(profile) + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a player's chestplate profile
     *
     * @param player UUID of player to get chestplate for
     * @return Player's chestplate profile
     */
    public ChestplateProfile getChestplateProfile(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT chestplates FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return (ChestplateProfile) BlobUtils.fromString(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Set a player's chestplate profile
     *
     * @param player  UUID of player to set chestplate profile for
     * @param profile Profile to set
     */
    public void setChestplateProfile(UUID player, ChestplateProfile profile) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT chestplates FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET chestplates='" + BlobUtils.toString(profile) + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a player's leggings profile
     *
     * @param player UUID of player to get leggings for
     * @return Player's leggings profile
     */
    public LeggingsProfile getLeggingsProfile(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT leggings FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return (LeggingsProfile) BlobUtils.fromString(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Set a player's leggings profile
     *
     * @param player  UUID of player to set leggings profile for
     * @param profile Profile to set
     */
    public void setLeggingsProfile(UUID player, LeggingsProfile profile) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT leggings FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET leggings='" + BlobUtils.toString(profile) + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a player's boots profile
     *
     * @param player UUID of player to get boots for
     * @return Player's boots profile
     */
    public BootsProfile getBootsProfile(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT boots FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return (BootsProfile) BlobUtils.fromString(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Set a player's boots profile
     *
     * @param player  UUID of player to set boots profile for
     * @param profile Profile to set
     */
    public void setBootsProfile(UUID player, BootsProfile profile) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT boots FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET boots='" + BlobUtils.toString(profile) + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a player's weapon profile
     *
     * @param player UUID of player to get profile for
     * @return Player's weapon profile
     */
    public MutantWeaponProfile getMutantWeaponProfile(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantWeapons FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return (MutantWeaponProfile) BlobUtils.fromString(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Set a player's weapon profile
     *
     * @param player  UUID of player to set weapon profile for
     * @param profile Profile to set
     */
    public void setMutantWeaponProfile(UUID player, MutantWeaponProfile profile) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantWeapons FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET mutantWeapons='" + BlobUtils.toString(profile) + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a player's helmet profile
     *
     * @param player UUID of player to get helmet for
     * @return Player's helmet profile
     */
    public MutantHelmetProfile getMutantHelmetProfile(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantHelmets FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return (MutantHelmetProfile) BlobUtils.fromString(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Set a player's helmet profile
     *
     * @param player  UUID of player to set helmet profile for
     * @param profile Profile to set
     */
    public void setMutantHelmetProfile(UUID player, MutantHelmetProfile profile) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantHelmets FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET mutantHelmets='" + BlobUtils.toString(profile) + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a player's chestplate profile
     *
     * @param player UUID of player to get chestplate for
     * @return Player's chestplate profile
     */
    public MutantChestplateProfile getMutantChestplateProfile(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantChestplates FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return (MutantChestplateProfile) BlobUtils.fromString(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Set a player's chestplate profile
     *
     * @param player  UUID of player to set chestplate profile for
     * @param profile Profile to set
     */
    public void setMutantChestplateProfile(UUID player, MutantChestplateProfile profile) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantChestplates FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET mutantChestplates='" + BlobUtils.toString(profile) + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a player's leggings profile
     *
     * @param player UUID of player to get leggings for
     * @return Player's leggings profile
     */
    public MutantLeggingsProfile getMutantLeggingsProfile(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantLeggings FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return (MutantLeggingsProfile) BlobUtils.fromString(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Set a player's leggings profile
     *
     * @param player  UUID of player to set leggings profile for
     * @param profile Profile to set
     */
    public void setMutantLeggingsProfile(UUID player, MutantLeggingsProfile profile) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantLeggings FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET mutantLeggings='" + BlobUtils.toString(profile) + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a player's boots profile
     *
     * @param player UUID of player to get boots for
     * @return Player's boots profile
     */
    public MutantBootsProfile getMutantBootsProfile(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantBoots FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                return (MutantBootsProfile) BlobUtils.fromString(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Set a player's boots profile
     *
     * @param player  UUID of player to set boots profile for
     * @param profile Profile to set
     */
    public void setMutantBootsProfile(UUID player, MutantBootsProfile profile) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT mutantBoots FROM survivorData WHERE username='" + player.toString() + "';");

            if (rs.next()) {
                this.getStatement().execute("UPDATE survivorData SET mutantBoots='" + BlobUtils.toString(profile) + "' WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the array of the armor for a player
     *
     * @param player UUID of player to get wardrobe for
     * @return The array for player's armor
     */
    public ItemStack[] getArmor(UUID player) {
        ItemStack[] stacks = new ItemStack[] { null, null, null, null };

        try {
            ResultSet helmet = this.getStatement().executeQuery("SELECT helmetType FROM survivorData WHERE username='" + player.toString() + "';");
            ResultSet chestplate = this.getStatement().executeQuery("SELECT chestplateType FROM survivorData WHERE username='" + player.toString() + "';");
            ResultSet leggings = this.getStatement().executeQuery("SELECT leggingsType FROM survivorData WHERE username='" + player.toString() + "';");
            ResultSet boots = this.getStatement().executeQuery("SELECT bootsType FROM survivorData WHERE username='" + player.toString() + "';");

            if (helmet.next()) {
                stacks[0] = ((SerializableItemStack) BlobUtils.fromString(helmet.getString(1))).toItemStack();
            }

            if (chestplate.next()) {
                stacks[1] = ((SerializableItemStack) BlobUtils.fromString(chestplate.getString(1))).toItemStack();
            }

            if (leggings.next()) {
                stacks[2] = ((SerializableItemStack) BlobUtils.fromString(leggings.getString(1))).toItemStack();
            }

            if (boots.next()) {
                stacks[3] = ((SerializableItemStack) BlobUtils.fromString(boots.getString(1))).toItemStack();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return stacks;
    }

    /**
     * Set a player's Survivor armor in the database
     *
     * @param player   UUID of player to set wardrobe for
     * @param armor Array of ItemStack containing armor to set
     */
    public void setArmor(UUID player, ItemStack[] armor) {
        if (!(armor.length == 4)) {
            throw new IllegalArgumentException("The item stack for the armor must be set at 4!");
        }

        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT COUNT(*) FROM survivorData WHERE username='" + player.toString() + "'");
            rs.next();

            if (rs.getInt(1) == 1) {
                this.getStatement().execute("UPDATE survivorData SET helmetType='" + (armor[0] == null ? "NONE" : BlobUtils.toString(new SerializableItemStack(armor[0]))) + "' WHERE username='" + player.toString() + "'");
                this.getStatement().execute("UPDATE survivorData SET chestplateType='" + (armor[1] == null ? "NONE" : BlobUtils.toString(new SerializableItemStack(armor[1]))) + "' WHERE username='" + player.toString() + "'");
                this.getStatement().execute("UPDATE survivorData SET leggingsType='" + (armor[2] == null ? "NONE" : BlobUtils.toString(new SerializableItemStack(armor[2]))) + "' WHERE username='" + player.toString() + "'");
                this.getStatement().execute("UPDATE survivorData SET bootsType='" + (armor[3] == null ? "NONE" : BlobUtils.toString(new SerializableItemStack(armor[3]))) + "' WHERE username='" + player.toString() + "'");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Update the state of the current server
     *
     * @param state State to update server to
     */
    public void updateState(GameState state) {
        try {
            if (!Main.getInstance().Config.getString("Current-Server").equalsIgnoreCase("none")) {
                ResultSet exists = this.getStatement().executeQuery("SELECT COUNT(*) FROM serverData WHERE server='" + Main.getInstance().Config.getString("Current-Server") + "'");
                exists.next();

                if (exists.getInt(1) == 0) {
                    this.getStatement().execute("INSERT INTO serverData (server, type, state) VALUES ('" + Main.getInstance().Config.getString("Current-Server") + "', 'survivor', '" + state.getName() + "')");
                }

                ResultSet rs = this.getStatement().executeQuery("SELECT state FROM serverData WHERE server='" + Main.getInstance().Config.getString("Current-Server") + "';");

                if (rs.next()) {
                    this.getStatement().execute("UPDATE serverData SET state='" + state.getName() + "' WHERE server='" + Main.getInstance().Config.getString("Current-Server") + "'");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}