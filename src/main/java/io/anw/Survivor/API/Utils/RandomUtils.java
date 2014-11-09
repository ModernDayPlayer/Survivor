package io.anw.Survivor.API.Utils;

import io.anw.Survivor.Main;

public class RandomUtils {

    /**
     * Get a random number between two numbers
     *
     * @param lower Lower number of set
     * @param upper Higher number of set
     * @return Random number between lower and upper
     */
    public static int getRandom(int lower, int upper) {
        return Main.getInstance().rand().nextInt(upper - lower) + lower;
    }

}
