package com.github.bogdanpc.frequencyplayer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class SoundGenerator {

    private SoundGenerator() {
        // static methods only
    }

    static void play(AtomicReference<State> sharedState) {
        float sampleRate = 44100f;
        int bufferSize = 4096;
        var af = new AudioFormat(sampleRate, 16, 2, true, false);

        try (var sdl = AudioSystem.getSourceDataLine(af)) {
            sdl.open(af, bufferSize);
            sdl.start();
            var buffer = new byte[bufferSize];
            int samplesPerBuffer = bufferSize / 4;

            int hz = 0;
            int cycleLength = 0;
            short[] waveTable = new short[0];
            double phase = 0.0;  // 0.0 to 1.0: position in wave cycle

            while (sharedState.get().status() != State.AppStatus.EXITING) {
                var currentState = sharedState.get();
                int currentHz = currentState.frequency();

                if (hz != currentHz) {
                    hz = currentHz;
                    cycleLength = (int) (sampleRate / hz);
                    waveTable = createWaveTable(cycleLength);
                }

                if (currentState.status() == State.AppStatus.PLAYING) {
                    double phaseIncrement = 1.0 / cycleLength;

                    for (int i = 0; i < samplesPerBuffer; i++) {
                        int tableIndex = (int) (phase * cycleLength) % cycleLength;
                        short sample = waveTable[tableIndex];

                        int offset = i * 4;
                        buffer[offset] = (byte) (sample & 0xFF);
                        buffer[offset + 1] = (byte) ((sample >> 8) & 0xFF);
                        buffer[offset + 2] = (byte) (sample & 0xFF);
                        buffer[offset + 3] = (byte) ((sample >> 8) & 0xFF);

                        phase += phaseIncrement;
                        if (phase >= 1.0) phase -= 1.0;
                    }

                    sdl.write(buffer, 0, bufferSize);
                } else {
                    LockSupport.parkNanos(10_000_000L);  // 10ms park when paused
                }
            }

            sdl.drain();
            sdl.stop();
        } catch (LineUnavailableException e) {
            throw new PlayException(e);
        }
    }

    /**
     * A wave cycle = one complete repetition of a waveform pattern.
     * Wavetable stores sample values.
     */
    private static short[] createWaveTable(int cycleLength) {
        var waveTable = new short[cycleLength];

        for (int i = 0; i < cycleLength; i++) {
            double angle = i / (double) cycleLength * 2.0 * Math.PI;
            waveTable[i] = (short) (Math.sin(angle) * Short.MAX_VALUE * 0.5);
        }

        return waveTable;
    }
}
