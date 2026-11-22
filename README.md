# Stormforge: Never Ending Rain + Tailcat

Unofficial Forge client for **Stormforge 2.3.0** (Modrinth version **k5ownM36**), Minecraft **1.20.1**, Forge **47.4.10**.

## Current update: Tailcat 0.1.5

**Apple silicon Macs, macOS 13+, arm64 Java 17 only for modern invitations.**

[Download 0.1.5](https://github.com/divyamk/stormforge-tailcat/releases/tag/tailcat-forge-0.1.5) · [Update prompt for your bot](UPDATE_WITH_GPT.md) · [Release notes](RELEASE_NOTES.md)

File: `tailcat-for-minecraft-0.1.5-forge-1.20.1.jar`

SHA-256: `15b8ede488c45f82889a735a6cea4a0c29b996ed9a7e8c5bb2e243b4d2e3750f`

Quit Minecraft, verify it has exited, and back up your current Tailcat JAR outside mods. Verify this download's checksum and replace only Tailcat, keeping exactly one enabled Tailcat JAR. Preserve all other mods, saves and settings.

Launch the same profile and open **Multiplayer → Connect with Tailcat**. Paste the invitation privately in-game, then select **Mode: Automatic (try direct)** and connect. **Relay remains the default and original fallback.** Never put invitations or login tokens in commands, logs, screenshots or source control. Rollback: with Minecraft closed, restore the verified previous JAR.

## What changed

Automatic uses a separate Mac helper that advertises TCP MSS 1000 before connecting to virtual Minecraft port 25565. This keeps its Minecraft TCP segments smaller. Normal Tailcat discovery and encrypted relay fallback remain available. It does not force a physical route or change the host MTU, VPN, public ports, server settings or authentication.

The Relay helper and all six original legacy native binaries remain byte-for-byte unchanged. Legacy joining and hosting are unchanged. Legacy binaries for other platforms do not imply modern-invitation support on those platforms.

Invitations reach helpers through stdin; SDK logging is suppressed. Helpers listen only on local loopback and connect to the existing authorized virtual Minecraft port. No diagnostic telemetry is shipped.

## Validation and limits

- 14 Forge Java tests and six Automatic-helper Go tests passed.
- Local SYN checks verified MSS 1000 on virtual port 25565 and unchanged behavior on an unrelated port.
- The exact released JAR completed authenticated in-game joining and more than two minutes of continuous gameplay on one Apple silicon Mac/network path; the player reported improved gameplay.
- This is not a guarantee for other networks, nor a claim that a particular upstream component caused the earlier failures. Server-side mod compatibility remains the server owner's responsibility.

## Source and build

The original Fabric source is in `mod/`; the Forge client is in `forge/`. The unchanged Relay helper is in `forge/psk-join/`. Automatic source, the precise pinned gVisor patch and reconstruction instructions are in [forge/psk-join-automatic/BUILD_AUTOMATIC.md](forge/psk-join-automatic/BUILD_AUTOMATIC.md). Its checked binary and checksum are in `forge/src/main/resources/assets/mclink/automatic-native/`.

The shipped Automatic helper was built with Go 1.27.4, `CGO_ENABLED=0`, Darwin arm64, `-trimpath` and `-tags=tailcat_minecraft_mss`. The Forge JAR was built with Java 17. Rebuilding the JAR can change ZIP metadata/checksums; install the verified release asset rather than assuming rebuilt bytes match it.

## Earlier pack and recipe add-on

The [original importable pack](https://github.com/divyamk/stormforge-tailcat/releases/tag/stormforge-2.3.0-tailcat-0.1.1) bundles Tailcat 0.1.1; it does not contain this modern-server fix. Existing profiles should receive the single-JAR update above.

The separate [recipe compatibility add-on](https://github.com/divyamk/stormforge-tailcat/releases/tag/recipe-compat-0.1.0) and [its guide](FIX_JEI_WITH_GPT.md) address the EMI/TMRV crafting-table rendering issue. They are separate from this transport update.

## Attribution and license

This is a fork of [Tailcat for Minecraft](https://github.com/tailscale/tailcat-for-minecraft), not an official Stormforge source repository. It is not endorsed by Tailscale or the pack authors. Tailcat is a prototype, not an officially supported Tailscale product.

Tailcat for Minecraft and its original native helpers are BSD-3-Clause; see [LICENSE](LICENSE) and [original README](README-UPSTREAM.md). The modern helper uses the official [Tailcat SDK](https://github.com/tailscale/tailcat). Dependency notices accompany the release in `THIRD_PARTY_LICENSES.txt`; retain them when redistributing its binaries.

[Stormforge](https://modrinth.com/modpack/stormforge) is a separate modpack. Its constituent mods retain their own licenses; this release distributes only the Tailcat client update.
