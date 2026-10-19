# DawnMenu

A small Minecraft client-side utility mod project.

## Planned features
- In-game menu and configurable menu key
- Custom keybinds
- Waypoints for locations the player chooses
- Schematic assistance for building in single-player worlds or servers where these tools are explicitly allowed

## Out of scope
This project will not include X-ray, hidden-chest or other-player base finding, ESP for locating hidden player assets, or anti-cheat evasion.

## Compatibility
Target Minecraft version: **26.2** (as requested).
The exact Fabric Loader, Fabric API, mappings, and Java versions still need to be confirmed for this Minecraft release before a build can be verified. No compiled JAR is provided yet.

## Build
Once the matching Fabric toolchain is configured, run:

```bash
./gradlew build
```

The compiled mod JAR should appear in `build/libs/`.
