package io.anw.Survivor.Listeners.Game.Sessions.Armor.Boots;

import io.anw.Core.Main.BlobUtils;
import io.anw.Survivor.Utils.SQL.DatabaseManager;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class BootsProfile implements Serializable {

    private static final long serialVersionUID = 8535609999598102500L;
    private UUID player;
    private ArrayList<BootsType> ownedBoots = new ArrayList<>();

    public BootsProfile(UUID uuid) {
        this.player = uuid;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public void saveProfile() {
        DatabaseManager.getInstance().setBootsProfile(player, this);
    }

    public ArrayList<BootsType> getOwnedBoots() {
        return this.ownedBoots;
    }

    public boolean hasItem(BootsType item) {
        return getOwnedBoots().contains(item);
    }

    public static BootsProfile getProfile(UUID uuid) {
        return DatabaseManager.getInstance().getBootsProfile(uuid);
    }

    public void save() {
        try {
            ResultSet rs = DatabaseManager.getInstance().getStatement().executeQuery("SELECT boots FROM survivorData WHERE uuid='" + player.toString() + "';");

            if (rs.next()) {
                DatabaseManager.getInstance().getStatement().execute("UPDATE survivorData SET boots='" + BlobUtils.toString(this) + "' WHERE uuid='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

