package com.github.bogdanpc.frequencyplayer;

public record KeyEvent(KeyType type, char character) implements InputEvent {

    public enum KeyType {
        ARROW_UP,
        ARROW_DOWN,
        CHARACTER,
        ESCAPE,
        SPACE,
        CTRL_C,
        CTRL_D
    }

    public KeyEvent(KeyType type) {
        this(type, '\0');
    }

    public static KeyEvent ctrlC() {
        return new KeyEvent(KeyType.CTRL_C);
    }

    public static KeyEvent ctrlD() {
        return new KeyEvent(KeyType.CTRL_D);
    }
}
