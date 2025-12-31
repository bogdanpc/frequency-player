package com.github.bogdanpc.frequencyplayer;

public record State(
        int frequency,
        int previousFrequency,
        AppStatus status) {

    public enum AppStatus {
        PLAYING,
        EXITING,
        ERROR,
        PAUSE
    }

    public static State of(int initialFrequency) {
        var freq = clampFrequency(initialFrequency);
        return new State(freq, freq, AppStatus.PAUSE);
    }

    private static int clampFrequency(int frequency) {
        return Math.clamp(frequency, FrequencyBar.MIN_FREQ, FrequencyBar.MAX_FREQ);
    }

    public State update(InputEvent e) {

        return switch (e) {
            case KeyEvent(var type, var ch) -> switch (type) {
                case ARROW_DOWN -> onFrequencyDown(frequency);
                case ARROW_UP -> onFrequencyUp(frequency);
                case SPACE -> withStatus(status == AppStatus.PAUSE ? AppStatus.PLAYING : AppStatus.PAUSE);
                case CHARACTER -> onCharacter(ch);
                case ESCAPE -> this;
                case CTRL_C, CTRL_D -> withStatus(AppStatus.EXITING);
            };
        };
    }

    private State onCharacter(char ch) {
        return switch (ch) {
            case 'q', 'Q' -> withStatus(AppStatus.EXITING);
            case 'p', 'P' -> withStatus(AppStatus.PAUSE);
            default -> this;
        };
    }

    private State onFrequencyUp(int frequency) {
        var newFreq = clampFrequency(frequency + calculateFrequencyStep(frequency));
        return new State(newFreq, frequency, status);
    }

    private State onFrequencyDown(int frequency) {
        var newFreq = clampFrequency(frequency - calculateFrequencyStep(frequency));
        return new State(newFreq, frequency, status);
    }

    public State withStatus(AppStatus newStatus) {
        if (status.equals(newStatus)) {
            return this;
        }
        return new State(frequency, previousFrequency, newStatus);
    }

    /**
     * Logarithmic steps for natural audio perception
     */
    int calculateFrequencyStep(int frequency) {
        var step = (int) Math.max(1, frequency * FrequencyBar.LOG_STEP_PERCENTAGE);
        return Math.min(step, 1000);
    }
}
