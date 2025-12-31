package com.github.bogdanpc.frequencyplayer;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;

/**
 * POSIX terminal implementation using tcgetattr/tcsetattr via Foreign Function API.
 * Supports macOS and Linux with platform-specific struct layouts and flag values.
 */
class PosixTerminal implements Terminal {

    private static final int STDIN_FD = 0;
    private static final int TCSAFLUSH = 2;

    private final int termiosSize;
    private final int lflagOffset;
    private final int iflagOffset;
    private final int icanon;
    private final int echo;
    private final int icrnl;
    private final int ixon;

    private final MethodHandle tcgetattr;
    private final MethodHandle tcsetattr;
    private final Arena arena;
    private MemorySegment originalTermios;
    private boolean rawModeEnabled;

    PosixTerminal(String os) {
        var isMacOs = os.contains("mac");
        if (isMacOs) {
            termiosSize = 72;
            lflagOffset = 24;
            iflagOffset = 0;
            icanon = 0x100;
            echo = 0x8;
            icrnl = 0x100;
            ixon = 0x200;
        } else {
            termiosSize = 60;
            lflagOffset = 12;
            iflagOffset = 0;
            icanon = 0x2;
            echo = 0x8;
            icrnl = 0x100;
            ixon = 0x400;
        }

        var linker = Linker.nativeLinker();
        var stdlib = linker.defaultLookup();

        var tcGetAttrSymbol = stdlib.find("tcgetattr")
                .orElseThrow(() -> new TerminalException("tcgetattr not found"));
        var tcSetAttrSymbol = stdlib.find("tcsetattr")
                .orElseThrow(() -> new TerminalException("tcsetattr not found"));

        tcgetattr = linker.downcallHandle(tcGetAttrSymbol,
                FunctionDescriptor.of(JAVA_INT, JAVA_INT, ADDRESS));
        tcsetattr = linker.downcallHandle(tcSetAttrSymbol,
                FunctionDescriptor.of(JAVA_INT, JAVA_INT, JAVA_INT, ADDRESS));

        arena = Arena.ofShared();
    }

    @Override
    public void enableRawMode() {
        if (rawModeEnabled) {
            return;
        }

        originalTermios = arena.allocate(termiosSize);

        try {
            var result = (int) tcgetattr.invoke(STDIN_FD, originalTermios);
            if (result != 0) {
                throw new TerminalException("tcgetattr failed");
            }
        } catch (TerminalException e) {
            throw e;
        } catch (Throwable e) {
            throw new TerminalException(e);
        }

        var modifiedTermios = arena.allocate(termiosSize);
        MemorySegment.copy(originalTermios, 0, modifiedTermios, 0, termiosSize);

        // Disable canonical mode and echo
        int lflag = modifiedTermios.get(JAVA_INT, lflagOffset);
        lflag &= ~(icanon | echo);
        modifiedTermios.set(JAVA_INT, lflagOffset, lflag);

        // Disable CR-to-NL translation and XON/XOFF flow control
        int iflag = modifiedTermios.get(JAVA_INT, iflagOffset);
        iflag &= ~(icrnl | ixon);
        modifiedTermios.set(JAVA_INT, iflagOffset, iflag);

        try {
            tcsetattr.invoke(STDIN_FD, TCSAFLUSH, modifiedTermios);
            rawModeEnabled = true;
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
            tcsetattr.invoke(STDIN_FD, TCSAFLUSH, originalTermios);
            rawModeEnabled = false;
        } catch (Throwable e) {
            throw new TerminalException(e);
        }
    }
}
