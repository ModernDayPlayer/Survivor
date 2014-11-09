package io.anw.Survivor.Listeners.Game.Sessions.Armor.Leggings;

import io.anw.Core.Main.BlobUtils;
import io.anw.Survivor.Utils.SQL.DatabaseManager;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class LeggingsProfile implements Serializable {

    private static final long serialVersionUID = 555111282512246839L;
    private UUID player;
    private ArrayList<LeggingsType> ownedLeggings = new ArrayList<>();

    public LeggingsProfile(UUID uuid) {
        this.player = uuid;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public void saveProfile() {
        DatabaseManager.getInstance().setLeggingsProfile(player, this);
    }

    public ArrayList<LeggingsType> getOwnedLeggings() {
        return this.ownedLeggings;
    }

    public boolean hasItem(LeggingsType item) {
        return getOwnedLeggings().contains(item);
    }

    public static LeggingsProfile getProfile(UUID uuid) {
        return DatabaseManager.getInstance().getLeggingsProfile(uuid);
    }

    public void save() {
        try {
            ResultSet rs = DatabaseManager.getInstance().getStatement().executeQuery("SELECT leggings FROM survivorData WHERE uuid='" + player.toString() + "';");

            if (rs.next()) {
                DatabaseManager.getInstance().getStatement().execute("UPDATE survivorData SET leggings='" + BlobUtils.toString(this) + "' WHERE uuid='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

