package com.github.bogdanpc.frequencyplayer;

class StatusBar {
    private final State state;

    private StatusBar(State state) {
        this.state = state;
    }

    public static StatusBar of(State state) {
        return new StatusBar(state);
    }

    public String statusDisplay() {
        var audioStatus = getAudioStatusDisplay();

        var freq = String.format("  %s%s\u001B[0m", AnsiColor.codeOfFrequency(state.frequency()), state.frequency());
        var statusText = String.format("\u001B[0m| %s | %s Hz |", audioStatus, freq);
        return String.format("%-48s", statusText);
    }

    String getAudioStatusDisplay() {
        if (state.status() == State.AppStatus.ERROR) {
            return "\u001B[31mNo Audio\u001B[0m";
        }

        if (state.status() == State.AppStatus.PLAYING) {
            return "\u001B[32m▶ PLAYING\u001B[0m";
        } else {
            return "\u001B[33m⏸ PAUSED \u001B[0m";
        }
    }
}
