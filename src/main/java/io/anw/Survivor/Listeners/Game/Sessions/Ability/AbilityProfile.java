package io.anw.Survivor.Listeners.Game.Sessions.Ability;

import io.anw.Core.Main.BlobUtils;
import io.anw.Survivor.Utils.SQL.DatabaseManager;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class AbilityProfile implements Serializable {

    private static final long serialVersionUID = 379709013223344363L;
    private UUID player;
    private ArrayList<AbilityType> ownedAbilities = new ArrayList<>();

    public AbilityProfile(UUID uuid) {
        this.player = uuid;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public void saveProfile() {
        DatabaseManager.getInstance().setAbilityProfile(player, this);
    }

    public ArrayList<AbilityType> getOwnedAbilities() {
        return this.ownedAbilities;
    }

    public boolean hasItem(AbilityType item) {
        return getOwnedAbilities().contains(item);
    }

    public static AbilityProfile getProfile(UUID uuid) {
        return DatabaseManager.getInstance().getAbilityProfile(uuid);
    }

    public void save() {
        try {
            ResultSet rs = DatabaseManager.getInstance().getStatement().executeQuery("SELECT abilities FROM survivorData WHERE uuid='" + player.toString() + "';");

            if (rs.next()) {
                DatabaseManager.getInstance().getStatement().execute("UPDATE survivorData SET abilities='" + BlobUtils.toString(this) + "' WHERE uuid='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
