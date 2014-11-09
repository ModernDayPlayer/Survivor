package io.anw.Survivor.Listeners.Game.Sessions;

import io.anw.Survivor.Listeners.Game.Sessions.Ability.AbilityType;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Boots.BootsType;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Chestplate.ChestplateType;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Helmet.HelmetType;
import io.anw.Survivor.Listeners.Game.Sessions.Armor.Leggings.LeggingsType;
import io.anw.Survivor.Listeners.Game.Sessions.Models.ModelType;
import io.anw.Survivor.Listeners.Game.Sessions.Weapons.WeaponType;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Session {

    private UUID player;

    private AbilityType abilityType;
    private WeaponType weaponType;
    private ModelType modelType;

    private HelmetType helmetType;
    private ChestplateType chestplateType;
    private LeggingsType leggingsType;
    private BootsType bootsType;

    public Session(UUID uuid) {
        this.player = uuid;
    }

    /**
     * Get the player of the session
     *
     * @return Owner of session
     */
    public UUID getPlayer() {
        return this.player;
    }

    /**
     * Get the ability type of the session
     *
     * @return Sets the ability type of the session
     */
    public AbilityType getAbilityType() {
        return this.abilityType;
    }

    /**
     * Set the ability type of the session
     *
     * @param type AbilityType to set for the session
     */
    public void setAbilityType(AbilityType type) {
        this.abilityType = type;
    }

    /**
     * Get the weapon type of the session
     *
     * @return Sets the weapon type of the session
     */
    public WeaponType getWeaponType() {
        return this.weaponType;
    }

    /**
     * Set the weapon type of the session
     *
     * @param type WeaponType to set for the session
     */
    public void setWeaponType(WeaponType type) {
        this.weaponType = type;
    }

    /**
     * Get the model type of the session
     *
     * @return Sets the model type of the session
     */
    public ModelType getModelType() {
        return this.modelType;
    }

    /**
     * Set the model type of the session
     *
     * @param type ModelType to set for the session
     */
    public void setModelType(ModelType type) {
        this.modelType = type;
    }

    /**
     * Get the weapon type of the session
     *
     * @return Sets the weapon type of the session
     */
    public HelmetType getHelmetType() {
        return this.helmetType;
    }

    /**
     * Set the weapon type of the session
     *
     * @param type HelmetType to set for the session
     */
    public void setHelmetType(HelmetType type) {
        this.helmetType = type;
    }

    /**
     * Get the chestplate type of the session
     *
     * @return Sets the chestplate type of the session
     */
    public ChestplateType getChestplateType() {
        return this.chestplateType;
    }

    /**
     * Set the chestplate type of the session
     *
     * @param type ChestplateType to set for the session
     */
    public void setChestplateType(ChestplateType type) {
        this.chestplateType = type;
    }

    /**
     * Get the leggings type of the session
     *
     * @return Sets the leggings type of the session
     */
    public LeggingsType getLeggingsType() {
        return this.leggingsType;
    }

    /**
     * Set the leggings type of the session
     *
     * @param type LeggingsType to set for the session
     */
    public void setLeggingsType(LeggingsType type) {
        this.leggingsType = type;
    }

    /**
     * Get the boots type of the session
     *
     * @return Sets the boots type of the session
     */
    public BootsType getBootsType() {
        return this.bootsType;
    }

    /**
     * Set the boots type of the session
     *
     * @param type BootsType to set for the session
     */
    public void setBootsType(BootsType type) {
        this.bootsType = type;
    }

    /**
     * Get the ability item from the ability type of the session
     *
     * @return AbilityHandler Item
     */
    public ItemStack getAbility() {
        return getAbilityType().getItemStack();
    }

    /**
     * Get the weapon item from the weapon type of the session
     *
     * @return Weapon item
     */
    public ItemStack getWeapon() {
        return getWeaponType().getItemStack();
    }

    /**
     * Get the helmet item from the armor type of the session
     *
     * @return Helmet item
     */
    public ItemStack getHelmet() {
        return getHelmetType().getItemStack();
    }
    /**
     * Get the chestplate item from the armor type of the session
     *
     * @return Chestplate item
     */
    public ItemStack getChestplate() {
        return getChestplateType().getItemStack();
    }

    /**
     * Get the leggings item from the armor type of the session
     *
     * @return Leggings item
     */
    public ItemStack getLeggings() {
        return getLeggingsType().getItemStack();
    }

    /**
     * Get the boots item from the armor type of the session
     *
     * @return Boots item
     */
    public ItemStack getBoots() {
        return getBootsType().getItemStack();
    }

    /**
     * Get the Mutant model used for the session
     *
     * @return Name of DisguiseType
     */
    public String getModel() {
        return getModelType().getDisguiseType().name();
    }

}
