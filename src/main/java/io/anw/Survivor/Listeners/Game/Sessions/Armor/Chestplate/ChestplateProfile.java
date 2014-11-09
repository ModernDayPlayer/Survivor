package io.anw.Survivor.Listeners.Game.Sessions.Armor.Chestplate;

import io.anw.Core.Main.BlobUtils;
import io.anw.Survivor.Utils.SQL.DatabaseManager;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class ChestplateProfile implements Serializable {

    private static final long serialVersionUID = 2315682239776423586L;
    private UUID player;
    private ArrayList<ChestplateType> ownedChestplates = new ArrayList<>();

    public ChestplateProfile(UUID uuid) {
        this.player = uuid;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public void saveProfile() {
        DatabaseManager.getInstance().setChestplateProfile(player, this);
    }

    public ArrayList<ChestplateType> getOwnedChestplates() {
        return this.ownedChestplates;
    }

    public boolean hasItem(ChestplateType item) {
        return getOwnedChestplates().contains(item);
    }

    public static ChestplateProfile getProfile(UUID uuid) {
        return DatabaseManager.getInstance().getChestplateProfile(uuid);
    }

    public void save() {
        try {
            ResultSet rs = DatabaseManager.getInstance().getStatement().executeQuery("SELECT chestplates FROM survivorData WHERE uuid='" + player.toString() + "';");

            if (rs.next()) {
                DatabaseManager.getInstance().getStatement().execute("UPDATE survivorData SET chestplates='" + BlobUtils.toString(this) + "' WHERE uuid='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}