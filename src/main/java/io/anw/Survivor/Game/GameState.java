package io.anw.Survivor.Game;

public enum GameState {
    Waiting("Waiting"), Voting("Voting"), Starting("Starting"), In_Game("In Game"), Restarting("Restarting");

    private String name;

    GameState(String name) {
        this.name = name;
    }

    /**
     * Get the proper name of the game state
     *
     * @return Name of state
     */
    public String getName() {
        return this.name;
    }
}
