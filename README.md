# Stormforge: Never Ending Rain + Tailcat

An unofficial, ready-to-import **Stormforge 2.3.0** modpack with a local Forge port of **Tailcat 0.1.1** for sharing single-player worlds with friends.

- Minecraft **1.20.1**, Forge **47.4.10**, Java **17**.
- All 173 original Stormforge download entries and original overrides are preserved. The only added mod is Tailcat.
- This repository is a fork of [Tailcat for Minecraft](https://github.com/tailscale/tailcat-for-minecraft), not an official Stormforge source repository. Stormforge does not list a source repository on Modrinth.
- Tailcat is a prototype, not an officially supported Tailscale product. This Forge port is unofficial.

## Install in Modrinth App

1. [Download the ready-to-import pack](https://github.com/divyamk/stormforge-tailcat/releases/download/stormforge-2.3.0-tailcat-0.1.1/Stormforge-Tailcat-2.3.0-0.1.1.mrpack).
2. Open Modrinth App → **Create new instance / New instance** → **Import from file**, then select the downloaded `.mrpack`. Labels may vary by app version; opening the file with Modrinth also works when file associations are configured.
3. Name the new instance **Stormforge + Tailcat**. Let Modrinth finish downloading the pack and its managed Java runtime.
4. Sign into your own Minecraft account if necessary, then launch the new instance.

The pack already contains Tailcat. Do not install the original Fabric Tailcat JAR into this Forge pack.

### If Stormforge 2.3.0 is already installed

Download [only the Forge Tailcat JAR](https://github.com/divyamk/stormforge-tailcat/releases/download/stormforge-2.3.0-tailcat-0.1.1/tailcat-for-minecraft-0.1.1-forge-1.20.1.jar). Open that instance in Modrinth → **Content → Upload files**, select the JAR, and restart Minecraft. If another Tailcat JAR is already installed, replace it so only v0.1.1 is enabled. Do not change existing worlds or unrelated mods.

## Join your friend's world

**Host:** launch Stormforge 2.3.0 with this Tailcat port, open the world, press Escape, then click **Share with Tailcat** at the upper right. Copy the `mcl1_...` invitation and send it privately to your friend. Keep the world and game open.

**Friend:** launch the new instance → **Multiplayer → Connect with Tailcat** at the upper right → paste the invitation → connect. Both players must use the same Stormforge version. No separate Tailscale installation or router port forwarding is required.

If the first connection times out, retry once after a few seconds; the relay needed a retry in the local transport test. If sharing was stopped or Minecraft restarted, request a fresh invitation. Treat the invitation as access to the live world, and do not post it in GitHub issues or logs.

## Let GPT set it up

Copy the prompt in [SETUP_WITH_GPT.md](SETUP_WITH_GPT.md) into your friend's GPT/Codex. A chat with no computer access can guide them, but cannot operate Modrinth for them.

## Validation and limits

- Forge build and all eight unit tests pass.
- The six original native helpers match their bundled checksums.
- The macOS ARM helper passed an invalid-invitation check and a host/join data-forwarding test (on retry).
- v0.1.1 launched in the host's Stormforge instance through the title and world-creation screens. It fixes v0.1.0's missing `pack.mcmeta`, which triggered Forge's loading-warning screen and Crash Assistant.
- Actual multiplayer gameplay between two separate computers has not yet been tested. Windows, Intel macOS, and Linux helpers are included but were not executed in this session.

## Source and build

The original Fabric source remains in `mod/`; the Forge 1.20.1 port is in `forge/` and reuses the original native assets. With JDK 17:

```sh
cd forge
./gradlew build
```

The reobfuscated release JAR is produced in `forge/build/libs/`. Release artifacts have SHA-256 hashes in `SHA256SUMS`.

## Attribution

[Stormforge: Never Ending Rain](https://modrinth.com/modpack/stormforge) is the upstream pack, pinned to [2.3.0](https://modrinth.com/modpack/stormforge/version/k5ownM36), listed under MIT on Modrinth. Its constituent mods retain their own licenses and are downloaded from their original URLs by Modrinth. Upstream metadata and download hash are recorded in `pack/upstream.json`.

Tailcat for Minecraft and the original native helpers are from Tailscale's BSD-3-Clause project; see [LICENSE](LICENSE) and [original README](README-UPSTREAM.md). This fork is not endorsed by Tailscale or the Stormforge authors.

To rebuild the importable pack after building the Forge JAR:

```sh
python3 scripts/package-stormforge.py forge/build/libs/tailcat-for-minecraft-0.1.1-forge-1.20.1.jar Stormforge-Tailcat-2.3.0-0.1.1.mrpack
```

The packager verifies the upstream SHA-512 before preserving its contents and adding Tailcat. ZIP timestamps can change the archive checksum between builds; `SHA256SUMS` describes the uploaded release assets.
