# Emotecraft Animation Compatibility — Fabric 1.21.10

Independent, client-side compatibility patch for **Emotecraft** and **Entity Model Features (EMF)**, targeting Minecraft **1.21.10** on Fabric.

## Purpose

When a player performs an Emotecraft emote, EMF-based resource-pack animations can compete with the emote and distort the player's pose. This mod uses EMF's documented `registerPauseCondition` extension point to request that EMF pause its animations **only for a player currently performing an emote**. The pause ends automatically when Emotecraft reports that the player is no longer emoting.

This is **a new implementation** using extension APIs, not a redistribution or copy of catman0221's All-Rights-Reserved mod.

## Requirements

- Minecraft 1.21.10
- Fabric Loader 0.17.2 or newer
- Fabric API compatible with 1.21.10
- Java 21
- Emotecraft and EMF on the client for the patch to do anything

Install the built JAR into the client's `mods` folder, alongside Emotecraft, EMF, ETF and any animation resource packs you use. No server-side installation is needed.

## Building

Use Gradle 9.2.1 and JDK 21:

```bash
gradle build
```

Find the distributable file in `build/libs/` (exclude the `-sources.jar` file).

GitHub Actions also builds the project and uploads the JAR as an artifact.

## Status / limitations

**Experimental — not yet validated in-game.** Compilation and GitHub Actions CI do not prove that an emote renders correctly in every combination of Emotecraft, EMF and resource packs.

- Uses EMF's public pause-condition API; if this method is removed or renamed the mod logs a warning and does nothing.
- Detects active Emotecraft emotes through known player interfaces at runtime. If neither interface exists, detection will not work; check `latest.log`.
- EMF still decides whether other non-emote animations are paused.
- No original Emotecraft Fix Animate Texture source code is included.

## Acknowledgments

Emotecraft: KosmX. Entity Model Features: Traben. Original separate mod concept: Catman0221. This repository is not affiliated with or endorsed by those projects.

## License

MIT — applies only to the new code in this repository.
