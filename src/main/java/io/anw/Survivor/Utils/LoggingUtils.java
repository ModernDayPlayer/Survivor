package io.anw.Survivor.Utils;

import io.anw.Survivor.Main;

import java.util.logging.Level;

public class LoggingUtils {

    /**
     * Log a message with a pre-defined default logging level
     *
     * @param message Message to log
     */
    public static void log(String message) {
        log(Level.INFO, message);
    }

    /**
     * Log something to the console
     *
     * @param level Level to log the message at
     * @param message Message to log
     */
    public static void log(Level level, String message) {
        Main.getInstance().getLogger().log(level, message);
    }

}
