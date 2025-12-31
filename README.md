# Frequency Player

A Java terminal audio frequency play app. Generates and play frequency sound from 20Hz to 20kHz.

## Requirements

- Java 25 or later

## Installation

1. Clone the repository:

```bash
git clone https://github.com/bogdanpc/frequency-player.git
cd frequency-player
```

2. Verify Java version:

```bash
java --version
```

## Usage

Run the application:

```bash
java --enable-native-access=ALL-UNNAMED play.java
```

Optionally, specify an initial frequency (in Hz):

```bash
java --enable-native-access=ALL-UNNAMED play.java 440
```

### Controls

| Key          | Action             |
|--------------|--------------------|
| `Space`      | Play/Pause         |
| `Arrow Up`   | Increase frequency |
| `Arrow Down` | Decrease frequency |
| `q` / `Q`    | Quit               |
| `p` / `P`    | Pause              |
| `Ctrl+C`     | Exit               |

## License

MIT
