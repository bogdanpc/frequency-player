package com.github.bogdanpc.frequencyplayer;

public enum AnsiColor {
    BLUE("\u001B[1;34m"),
    CYAN("\u001B[1;36m"),
    GREEN("\u001B[1;32m"),
    YELLOW("\u001B[1;33m"),
    BRIGHT_YELLOW("\u001B[1;93m"),
    RED("\u001B[1;31m"),
    MAGENTA("\u001B[1;35m"),
    WHITE("\u001B[1;37m");

    private final String s;

    AnsiColor(String s) {
        this.s = s;
    }

    public String code() {
        return s;
    }

    static AnsiColor forFrequency(int frequency) {
        if (frequency < 82) return BLUE;
        if (frequency < 261) return CYAN;
        if (frequency < 523) return GREEN;
        if (frequency < 2093) return YELLOW;
        if (frequency < 4186) return BRIGHT_YELLOW;
        if (frequency < 8370) return RED;
        if (frequency < 11372) return MAGENTA;
        return WHITE;
    }

    static String codeOfFrequency(int frequency) {
        return forFrequency(frequency).code();
    }
}
