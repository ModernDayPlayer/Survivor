package io.anw.Survivor.Listeners.Game.Sessions.Armor.Helmet;

import io.anw.PluginAPI.BlobUtils;
import io.anw.Survivor.Utils.SQL.DatabaseManager;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class MutantHelmetProfile implements Serializable {

    private static final long serialVersionUID = 2315682239776423586L;
    private UUID player;
    private ArrayList<HelmetType> ownedHelmets = new ArrayList<>();

    public MutantHelmetProfile(UUID uuid) {
        this.player = uuid;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public void saveProfile() {
        DatabaseManager.getInstance().setMutantHelmetProfile(player, this);
    }

    public ArrayList<HelmetType> getOwnedHelmets() {
        return this.ownedHelmets;
    }

    public boolean hasItem(HelmetType item) {
        return getOwnedHelmets().contains(item);
    }

    public static MutantHelmetProfile getProfile(UUID uuid) {
        return DatabaseManager.getInstance().getMutantHelmetProfile(uuid);
    }

    public void save() {
        try {
            ResultSet rs = DatabaseManager.getInstance().getStatement().executeQuery("SELECT mutantHelmets FROM survivorData WHERE uuid='" + player.toString() + "';");

            if (rs.next()) {
                DatabaseManager.getInstance().getStatement().execute("UPDATE survivorData SET mutantHelmets='" + BlobUtils.toString(this) + "' WHERE uuid='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
