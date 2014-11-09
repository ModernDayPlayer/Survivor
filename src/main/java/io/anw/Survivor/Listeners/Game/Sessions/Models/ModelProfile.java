package io.anw.Survivor.Listeners.Game.Sessions.Models;

import io.anw.Core.Main.BlobUtils;
import io.anw.Survivor.Utils.SQL.DatabaseManager;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class ModelProfile implements Serializable {

    private static final long serialVersionUID = -5582672716020483155L;
    private UUID player;
    private ArrayList<ModelType> ownedModels = new ArrayList<>();

    public ModelProfile(UUID uuid) {
        this.player = uuid;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public void saveProfile() {
        DatabaseManager.getInstance().setModelProfile(player, this);
    }

    public ArrayList<ModelType> getOwnedModels() {
        return this.ownedModels;
    }

    public boolean hasModel(ModelType type) {
        return getOwnedModels().contains(type);
    }

    public static ModelProfile getProfile(UUID uuid) {
        return DatabaseManager.getInstance().getModelProfile(uuid);
    }

    public void save() {
        try {
            ResultSet rs = DatabaseManager.getInstance().getStatement().executeQuery("SELECT models FROM survivorData WHERE uuid='" + player.toString() + "';");

            if (rs.next()) {
                DatabaseManager.getInstance().getStatement().execute("UPDATE survivorData SET models='" + BlobUtils.toString(this) + "' WHERE uuid='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
