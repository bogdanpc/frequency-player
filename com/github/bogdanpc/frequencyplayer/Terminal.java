package com.github.bogdanpc.frequencyplayer;

/**
 * Controls terminal raw mode for direct keyboard input without line buffering.
 * Provides platform-specific implementations for POSIX (macOS/Linux) and Windows.
 */
public interface Terminal {

    void enableRawMode();

    void disableRawMode();

    static Terminal create() {
        var os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return new WindowsTerminal();
        }
        return new PosixTerminal(os);
    }
}
