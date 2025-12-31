package com.github.bogdanpc.frequencyplayer;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;

/**
 * Windows terminal implementation using kernel32 Console API via Foreign Function API.
 */
class WindowsTerminal implements Terminal {

    private static final int STD_INPUT_HANDLE = -10;
    private static final int ENABLE_LINE_INPUT = 0x0002;
    private static final int ENABLE_ECHO_INPUT = 0x0004;
    private static final int ENABLE_PROCESSED_INPUT = 0x0001;

    private final MethodHandle getStdHandle;
    private final MethodHandle getConsoleMode;
    private final MethodHandle setConsoleMode;
    private final Arena arena;

    private MemorySegment consoleHandle;
    private int originalMode;
    private boolean rawModeEnabled;

    WindowsTerminal() {
        var linker = Linker.nativeLinker();

        SymbolLookup kernel32;
        try {
            kernel32 = SymbolLookup.libraryLookup("kernel32.dll", Arena.global());
        } catch (IllegalArgumentException _) {
            throw new TerminalException("kernel32.dll not found - not running on Windows?");
        }

        var getStdHandleSymbol = kernel32.find("GetStdHandle")
                .orElseThrow(() -> new TerminalException("GetStdHandle not found"));
        var getConsoleModeSymbol = kernel32.find("GetConsoleMode")
                .orElseThrow(() -> new TerminalException("GetConsoleMode not found"));
        var setConsoleModeSymbol = kernel32.find("SetConsoleMode")
                .orElseThrow(() -> new TerminalException("SetConsoleMode not found"));

        getStdHandle = linker.downcallHandle(getStdHandleSymbol,
                FunctionDescriptor.of(ADDRESS, JAVA_INT));
        getConsoleMode = linker.downcallHandle(getConsoleModeSymbol,
                FunctionDescriptor.of(JAVA_INT, ADDRESS, ADDRESS));
        setConsoleMode = linker.downcallHandle(setConsoleModeSymbol,
                FunctionDescriptor.of(JAVA_INT, ADDRESS, JAVA_INT));

        arena = Arena.ofShared();
    }

    @Override
    public void enableRawMode() {
        if (rawModeEnabled) {
            return;
        }

        try {
            consoleHandle = (MemorySegment) getStdHandle.invoke(STD_INPUT_HANDLE);

            var modePtr = arena.allocate(JAVA_INT);
            var result = (int) getConsoleMode.invoke(consoleHandle, modePtr);
            if (result == 0) {
                throw new TerminalException("GetConsoleMode failed");
            }

            originalMode = modePtr.get(JAVA_INT, 0);
            int rawMode = originalMode & ~(ENABLE_LINE_INPUT | ENABLE_ECHO_INPUT | ENABLE_PROCESSED_INPUT);

            result = (int) setConsoleMode.invoke(consoleHandle, rawMode);
            if (result == 0) {
                throw new TerminalException("SetConsoleMode failed");
            }

            rawModeEnabled = true;
        } catch (TerminalException e) {
            throw e;
        } catch (Throwable e) {
            throw new TerminalException(e);
        }
    }

    @Override
    public void disableRawMode() {
        if (!rawModeEnabled) {
            return;
        }

        try {
            setConsoleMode.invoke(consoleHandle, originalMode);
            rawModeEnabled = false;
        } catch (Throwable e) {
            throw new TerminalException(e);
        }
    }
}
