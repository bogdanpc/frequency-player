package com.github.bogdanpc.frequencyplayer;

public class Render {


    public void render(State state) {
        var builder = new StringBuilder();
        renderHeader(builder);
        renderFrequencySection(builder, state);
        renderState(builder, state);
        renderControlsSection(builder);
        IO.print(builder);
    }

    void renderHeader(StringBuilder b) {
        b.append("\033[1;1H");
        b.append(System.lineSeparator());
        b.append("\u001B[1;37m((○))  Play frequency ♫♪\u001B[0m");
        b.append("  \u001B[2m");
        b.append("\u001B[0m");
        b.append(System.lineSeparator());
        b.append(System.lineSeparator());
    }

    void renderFrequencySection(StringBuilder b, State state) {
        var bar = new FrequencyBar(state);
        bar.renderFrequencyBar(b);
        b.append(System.lineSeparator());
    }

    void renderControlsSection(StringBuilder b) {
        b.append(System.lineSeparator());
        b.append("\u001B[2m");
        b.append("\u001B[0m");
        b.append("\u001B[1m↑/↓\u001B[0mAdjust frequency    \u001B[1mSPACE\u001B[0m  Play/Pause    \u001B[1mQ\u001B[0m  Quit");
        b.append(System.lineSeparator());
    }

    void renderState(StringBuilder b, State state) {
        b.append(StatusBar.of(state).statusDisplay());
        b.append(System.lineSeparator());
    }
}
