package com.github.bogdanpc.frequencyplayer;

/** Signals an unrecoverable error that should terminate the application. */
public class TerminalException extends RuntimeException {
    public TerminalException(String message) {
        super(message);
    }
    public TerminalException(Throwable cause) {
        super(cause);
    }
}
