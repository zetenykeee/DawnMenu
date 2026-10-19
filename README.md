# DawnMenu

A client-side Fabric utility mod targeting Minecraft **Java Edition 26.2**.

## Current prototype
- Press **K** to save your current position as a personal waypoint.
- Press **J** to list saved waypoints in chat.
- Default keys can be changed in Minecraft's Controls settings under Miscellaneous.

Waypoints currently stay in memory and are cleared when the game is restarted. This is an early prototype; no compiled JAR has been verified yet.

## Build
The repository includes a GitHub Actions workflow intended to build the JAR and upload it as a workflow artifact. Once a successful run finishes, open **Actions → Build DawnMenu → latest run → Artifacts**.

For a local build with Java 25 and Gradle 9.5.1 installed, run:

```bash
gradle build
```

The JAR should be written to `build/libs/`.

## Boundaries
This project focuses on personal waypoints and legitimate building utilities. It does not include X-ray, hidden chest/player-base finding, ESP for locating hidden player assets, or anti-cheat evasion.
