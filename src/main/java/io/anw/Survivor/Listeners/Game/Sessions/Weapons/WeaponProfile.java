package io.anw.Survivor.Listeners.Game.Sessions.Weapons;

import io.anw.Core.Main.BlobUtils;
import io.anw.Survivor.Utils.SQL.DatabaseManager;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class WeaponProfile implements Serializable {

    private static final long serialVersionUID = -1074381577522141229L;
    private UUID player;
    private ArrayList<WeaponType> ownedWeapons = new ArrayList<>();

    public WeaponProfile(UUID uuid) {
        this.player = uuid;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public void saveProfile() {
        DatabaseManager.getInstance().setWeaponProfile(player, this);
    }

    public ArrayList<WeaponType> getOwnedWeapons() {
        return this.ownedWeapons;
    }

    public boolean hasItem(WeaponType item) {
        return getOwnedWeapons().contains(item);
    }

    public static WeaponProfile getProfile(UUID uuid) {
        return DatabaseManager.getInstance().getWeaponProfile(uuid);
    }

    public void save() {
        try {
            ResultSet rs = DatabaseManager.getInstance().getStatement().executeQuery("SELECT weapons FROM survivorData WHERE uuid='" + player.toString() + "';");

            if (rs.next()) {
                DatabaseManager.getInstance().getStatement().execute("UPDATE survivorData SET weapons='" + BlobUtils.toString(this) + "' WHERE uuid='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
