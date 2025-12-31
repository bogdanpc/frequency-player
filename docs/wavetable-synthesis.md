# Guide to the audio tehcniques used

## Wave Cycle

One complete repetition of a sine waveform:

```
     +1 ──      ╭───╮
               ╱     ╲
      0 ──────╱───────╲───────
                       ╲     ╱
     -1 ──              ╰───╯
```

A Cycle length is the samples per cycle = `sampleRate / frequency`

| Frequency | Sample Rate | Cycle Length |
|-----------|-------------|--------------|
| 1000 Hz   | 44100 Hz    | 44 samples   |
| 440 Hz    | 44100 Hz    | 100 samples  |
| 100 Hz    | 44100 Hz    | 441 samples  |

## Sample Format (16-bit Stereo)

Each audio sample = 4 bytes:

```
┌─────────────────────────┬─────────────────────────┐
│    Left Channel (2B)    │    Right Channel (2B)   │
├────────────┬────────────┼────────────┬────────────┤
│  Low byte  │  High byte │  Low byte  │  High byte │
│  (sample   │  (sample   │  (sample   │  (sample   │
│   & 0xFF)  │   >> 8)    │   & 0xFF)  │   >> 8)    │
└────────────┴────────────┴────────────┴────────────┘
   offset+0     offset+1     offset+2     offset+3
```

Buffer math: `samplesPerBuffer = bufferSize / 4`

With 4096-byte buffer → 1024 samples per buffer.
