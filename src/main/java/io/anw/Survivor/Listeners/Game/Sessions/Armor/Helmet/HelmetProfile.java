package io.anw.Survivor.Listeners.Game.Sessions.Armor.Helmet;

import io.anw.Survivor.Utils.SQL.DatabaseManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class HelmetProfile implements Serializable {

    private static final long serialVersionUID = 2315682239776423586L;
    private UUID player;
    private ArrayList<HelmetType> ownedHelmets = new ArrayList<>();

    public HelmetProfile(UUID uuid) {
        this.player = uuid;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public void saveProfile() {
        DatabaseManager.getInstance().setHelmetProfile(player, this);
    }

    public ArrayList<HelmetType> getOwnedHelmets() {
        return this.ownedHelmets;
    }

    public boolean hasItem(HelmetType item) {
        return getOwnedHelmets().contains(item);
    }

    public static HelmetProfile getProfile(UUID uuid) {
        return DatabaseManager.getInstance().getHelmetProfile(uuid);
    }
}
