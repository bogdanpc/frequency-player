// java --enable-native-access=ALL-UNNAMED play.java [frequency]

import com.github.bogdanpc.frequencyplayer.FrequencyPlayer;

void main(String[] args) {
    var initialFrequency = parseFrequency(args);
    if (initialFrequency == null) {
        FrequencyPlayer.start();
    } else {
        FrequencyPlayer.start(initialFrequency);
    }
}

Integer parseFrequency(String[] args) {
    if (args.length == 0) {
        return null;
    }
    try {
        var frequency = Integer.valueOf(args[0]);
        if (FrequencyPlayer.valid(frequency)) {
            System.err.println("Frequency must be between 20 and 20000 Hz. Got: " + frequency + ". Using default.");
            return null;
        }
        return frequency;
    } catch (NumberFormatException e) {
        System.err.println("Invalid frequency: " + args[0] + ". Must be an integer between 20 and 20000.");
        return null;
    }
}