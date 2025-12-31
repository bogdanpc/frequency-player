package com.github.bogdanpc.frequencyplayer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.bogdanpc.frequencyplayer.FrequencyBar.MAX_FREQ;
import static com.github.bogdanpc.frequencyplayer.FrequencyBar.MIN_FREQ;

public class FrequencyPlayer implements AutoCloseable {
    private final Render render;
    private final InputHandler inputHandler;
    private final Terminal terminal;
    private final Integer initialFrequency;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    private FrequencyPlayer(Integer initialFrequency) {
        this.initialFrequency = initialFrequency;
        terminal = Terminal.create();
        terminal.enableRawMode();
        render = new Render();
        inputHandler = new InputHandler();

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    if (closed.compareAndSet(false, true)) {
                        System.err.println("\nShutting down...");
                        cleanup();
                    }
                })
        );
    }

    /**
     * Run main loop
     */
    void run() throws IOException {
        var sharedState = new AtomicReference<>(State.of(initialFrequency));
        render.render(sharedState.get());

        Thread.ofVirtual().start(() -> SoundGenerator.play(sharedState));

        while (sharedState.get().status() != State.AppStatus.EXITING) {
            var event = inputHandler.read();
            var currentState = sharedState.get();
            var newState = currentState.update(event);

            if (!newState.equals(currentState)) {
                sharedState.set(newState);
                render.render(newState);
            }
        }
    }

    public static void start() {
        start(MIN_FREQ);
    }

    public static void start(Integer initialFrequency) {
        try (var matcher = new FrequencyPlayer(initialFrequency)) {
            matcher.run();
        } catch (Exception e) {
            System.err.println("Application error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static boolean valid(int frequency) {
        if (frequency < MIN_FREQ || frequency > MAX_FREQ) {
            System.err.println("Frequency must be between 20 and 20000 Hz. Got: " + frequency + ". Using default.");
            return false;
        }
        return true;
    }

    private void cleanup() {
        System.out.print("\033[2J");
        System.out.print("\033[H");
        terminal.disableRawMode();
        System.out.flush();
        System.err.flush();
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            cleanup();
        }
    }
}
