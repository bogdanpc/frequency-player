package com.github.bogdanpc.frequencyplayer;

import java.io.IOException;

import static java.lang.System.in;

public class InputHandler {

    public InputEvent read() throws IOException {
        var key = in.read();
        return switch (key) {
            case 27 -> handleEscapeSequence();
            case 32 -> new KeyEvent(KeyEvent.KeyType.SPACE, ' ');
            case 3 -> KeyEvent.ctrlC();
            case 4 -> KeyEvent.ctrlD();
            default -> new KeyEvent(KeyEvent.KeyType.CHARACTER, (char) key);
        };
    }

    private InputEvent handleEscapeSequence() throws IOException {
        var nextKey = in.read();

        if (nextKey == '[') {
            var code = in.read();
            return switch (code) {
                case 'A' -> new KeyEvent(KeyEvent.KeyType.ARROW_UP);
                case 'B' -> new KeyEvent(KeyEvent.KeyType.ARROW_DOWN);
                default -> new KeyEvent(KeyEvent.KeyType.ESCAPE);
            };
        }
        return new KeyEvent(KeyEvent.KeyType.ESCAPE);
    }
}
