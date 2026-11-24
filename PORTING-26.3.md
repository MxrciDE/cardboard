# Cardboard 26.3 port – work branch

This branch is a safe working branch based on the official `ver/26.2` upstream source.
It is intentionally not presented as a working 26.3 build.

## Target supplied by the user

- Minecraft 26.3
- Fabric Loader 0.20.1
- Fabric API 0.166.2+26.3
- Java 25
- Paper API target from the user's 26.3 project: 26.3.build.256-stable

## Confirmed upstream baseline

The upstream `ver/26.2` branch currently declares:

- Minecraft 26.2
- Fabric Loader 0.19.3
- Fabric API 0.156.0+26.2
- Paper API 26.2.build.110-stable
- local dependency `libs/iCommon-Fabric-26.2.jar`

The user's separate 26.3 source archive already changes Minecraft/Fabric metadata, but its compile log reports 100 Java errors. Therefore changing version strings alone is not a port.

## Porting order

1. Import the user's actual 26.3 source tree into this branch, preserving their selected versions and project changes.
2. Replace/port the iCommon 26.2 dependency and validate all its mixin targets.
3. Resolve Minecraft symbol/package changes (examples from the supplied compiler log: Holder, MapItemColor, RedStoneWireBlock, EnderMan, SignItem, FuelValues, PotionBrewing, ServerboundSwingPacket).
4. Resolve changed Minecraft method signatures and access changes.
5. Implement new Paper API abstract methods and resolve Bukkit/Paper adapter changes.
6. Fix CraftLegacy/DataFixer runtime initialization and run a clean startup + plugin test.
7. Publish a jar only after CI build and runtime validation.

## CI

A Java 25 GitHub Actions build workflow is added on this branch. It first validates the upstream baseline and provides a repeatable build environment. A successful baseline build is not evidence that the 26.3 port is complete.
