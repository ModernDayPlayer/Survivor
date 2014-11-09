package io.anw.Survivor.Listeners.Game.Sessions;

import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import io.anw.Survivor.Listeners.Game.Sessions.Ability.AbilityType;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Boots.BootsType;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Chestplate.ChestplateType;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Helmet.HelmetType;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Leggings.LeggingsType;
import io.anw.Survivor.Listeners.Game.Sessions.Models.ModelType;
import io.anw.Survivor.Listeners.Game.Sessions.Weapons.WeaponType;
import io.anw.Survivor.Utils.SQL.DatabaseManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private static Map<UUID, Session> storedSessions = new HashMap<>();
    private static Map<UUID, Session> storedMutantSession = new HashMap<>();

    /**
     * Gets the stored session of a player
     *
     * @param player Player to get UUID from
     * @return Session associated with player's UUID object
     */
    public static Session getSurvivorSession(Player player) {
        UUID player_uuid = UUIDUtility.getUUID(player.getName());

        if (!storedSessions.containsKey(player_uuid)) {
            setSurvivorSession(player_uuid, createSurvivorSession(player_uuid,
                    DatabaseManager.getInstance().getAbility(player_uuid),
                    DatabaseManager.getInstance().getWeapon(player_uuid),

                    DatabaseManager.getInstance().getHelmet(player_uuid),
                    DatabaseManager.getInstance().getChestplate(player_uuid),
                    DatabaseManager.getInstance().getLeggings(player_uuid),
                    DatabaseManager.getInstance().getBoots(player_uuid)));
        }

        return storedSessions.get(player_uuid);
    }

    /**
     * Gets the stored session of a player
     *
     * @param player Player to get UUID from
     * @return Session associated with player's UUID object
     */
    public static Session getMutantSession(Player player) {
        UUID player_uuid = UUIDUtility.getUUID(player.getName());

        if (!storedMutantSession.containsKey(player_uuid)) {
            setMutantSession(player_uuid, createMutantSession(player_uuid,
                    DatabaseManager.getInstance().getModel(player_uuid),
                    DatabaseManager.getInstance().getMutantWeapon(player_uuid),

                    DatabaseManager.getInstance().getMutantHelmet(player_uuid),
                    DatabaseManager.getInstance().getMutantChestplate(player_uuid),
                    DatabaseManager.getInstance().getMutantLeggings(player_uuid),
                    DatabaseManager.getInstance().getMutantBoots(player_uuid)));
        }

        return storedMutantSession.get(player_uuid);
    }

    /**
     * Set the session for a player
     *
     * @param player  UUID of player to set session for
     * @param session Session to set for player
     */
    private static void setSurvivorSession(UUID player, Session session) {
        if (storedMutantSession.containsKey(player)) {
            storedMutantSession.remove(player);
        }
        if (storedSessions.containsKey(player)) {
            storedSessions.remove(player);
        }

        storedSessions.put(player, session);
    }

    /**
     * Set the session for a player
     *
     * @param player  UUID of player to set session for
     * @param session Session to set for player
     */
    private static void setMutantSession(UUID player, Session session) {
        if (storedSessions.containsKey(player)) {
            storedSessions.remove(player);
        }
        if (storedMutantSession.containsKey(player)) {
            storedMutantSession.remove(player);
        }
        storedMutantSession.put(player, session);
    }

    /**
     * Creates a session object
     *
     * @param player     UUID of player to create session for
     * @param abilityType    Type of ability to set
     * @param weaponType Type of weapon to set
     * @param helmetType Type of helmet to set
     * @param chestplateType Type of chestplate to set
     * @param leggingsType Type of leggings to set
     * @param bootsType Type of boots to set
     * @return New session based on parameters
     */
    public static Session createSurvivorSession(UUID player, AbilityType abilityType, WeaponType weaponType, HelmetType helmetType, ChestplateType chestplateType, LeggingsType leggingsType, BootsType bootsType) {
        Session session = new Session(player);
        session.setAbilityType(abilityType);
        session.setWeaponType(weaponType);

        session.setHelmetType(helmetType);
        session.setChestplateType(chestplateType);
        session.setLeggingsType(leggingsType);
        session.setBootsType(bootsType);

        return session;
    }

    /**
     * Creates a session object
     *
     * @param player     UUID of player to create session for
     * @param weaponType Type of weapon to set
     * @param modelType Type of model to set
     * @param helmetType Type of helmet to set
     * @param chestplateType Type of chestplate to set
     * @param leggingsType Type of leggings to set
     * @param bootsType Type of boots to set
     * @return New session based on parameters
     */
    public static Session createMutantSession(UUID player, ModelType modelType, WeaponType weaponType, HelmetType helmetType, ChestplateType chestplateType, LeggingsType leggingsType, BootsType bootsType) {
        Session session = new Session(player);
        session.setModelType(modelType);
        session.setWeaponType(weaponType);

        session.setHelmetType(helmetType);
        session.setChestplateType(chestplateType);
        session.setLeggingsType(leggingsType);
        session.setBootsType(bootsType);

        return session;
    }

}
