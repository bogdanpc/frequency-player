package com.github.bogdanpc.frequencyplayer;

class FrequencyBar {

    public static final int MIN_FREQ = 20;
    public static final int MAX_FREQ = 20000;
    public static final double LOG_STEP_PERCENTAGE = 0.05;
    private static final int BAR_LENGTH = 60;
    private static final String EMPTY_CHAR = "\u001B[2m─\u001B[0m";
    private final State state;

    public FrequencyBar(State state) {
        this.state = state;
    }

    /**
     * Render the UI bar for frequency
     * - Calculate animated position for smooth transitions
     * - Enhanced position indicator with animation effect or Empty portion with subtle styling and transition hints
     */
    void renderFrequencyBar(StringBuilder sb) {
        var targetPosition = calculatePosition(state.frequency());

        sb.append("|");

        for (int i = 0; i < BAR_LENGTH; i++) {
            if (i == targetPosition) {
                var indicator = getPositionIndicator(state.frequency());
                sb.append(indicator);
            } else {
                sb.append(EMPTY_CHAR);
            }
        }
        sb.append("|");
        sb.append(System.lineSeparator());
        sb.append(renderFrequencyScale());
        sb.append(System.lineSeparator());
    }

    String getPositionIndicator(int frequency) {
        return AnsiColor.codeOfFrequency(frequency) + "●\u001B[0m";
    }

    int calculatePosition(int frequency) {
        var sqrtMin = Math.sqrt(MIN_FREQ);
        var sqrtMax = Math.sqrt(MAX_FREQ);
        var sqrtCurrent = Math.sqrt(frequency);
        return (int) (((sqrtCurrent - sqrtMin) / (sqrtMax - sqrtMin)) * BAR_LENGTH);
    }

    String renderFrequencyScale() {
        return String.format("\u001B[2m%5dHz%50dHz\u001B[0m", MIN_FREQ, MAX_FREQ);
    }
}
