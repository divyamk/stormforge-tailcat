# Stormforge: Never Ending Rain + Tailcat

Unofficial Tailcat Forge client for **Stormforge 2.3.0** ([pack version k5ownM36](https://modrinth.com/modpack/stormforge/version/k5ownM36)), Minecraft **1.20.1**, Forge **47.4.10**, and **arm64 Java 17**.

## Current update: Tailcat 0.1.3

**Modern Tailcat v0.7.0 invitations: Apple silicon Macs, macOS 13 or newer only.** Use an arm64 Java 17 runtime. This is the exact client JAR verified to authenticate and enter a live server through **Multiplayer → Connect with Tailcat**.

[Download release 0.1.3](https://github.com/divyamk/stormforge-tailcat/releases/tag/tailcat-forge-0.1.3) · [Give the update instructions to your bot](UPDATE_WITH_GPT.md)

File: `tailcat-for-minecraft-0.1.3-forge-1.20.1.jar`

SHA-256:

```text
3e037a498b8966b9c7e29978fa251784c0a8069d1ba66518f2f62b61cde16efd
```

1. Confirm the existing profile is Stormforge 2.3.0, Minecraft 1.20.1, Forge 47.4.10, running arm64 Java 17 on macOS 13+.
2. Quit Minecraft yourself and wait for its process to exit. Back up the existing Tailcat JAR outside the profile's `mods` folder.
3. Download the JAR from the release, verify its SHA-256, and replace only the existing Tailcat JAR. Keep exactly one enabled Tailcat JAR. Preserve all other mods, settings, and worlds.
4. Launch that same profile, sign in with your own Minecraft account, and use **Multiplayer → Connect with Tailcat**. Paste the server invitation privately into the game. Never put it in chat, source control, shell arguments, screenshots, or logs.
5. Success means reaching the authenticated server world. If Forge reports incompatible or missing channels, report the exact mod/channel names to the server owner; do not remove mods or disable authentication to work around it.

Rollback: quit Minecraft, remove this JAR, and restore the verified previous Tailcat JAR from the backup.

## Remaining limitation

This release fixes the initial authenticated button join. Snapback and disconnections have still been observed during heavier chunk/world traffic. It is not a guarantee of sustained traffic stability; server-side investigation is separate. If this happens, report the non-sensitive symptom to the server owner rather than repeatedly reinstalling or changing security/network settings.

## What changed

Modern raw `tc…` invitations use a separate helper built from the official Tailcat v0.7.0 Go SDK. The invitation reaches the helper over stdin. SDK logging is suppressed to keep credentials private. The helper binds only loopback and connects to virtual Minecraft port 25565.

Only the modern helper subprocess sets `TS_DEBUG_ALWAYS_USE_DERP=true`. This keeps it on the invitation's existing encrypted DERP relay and avoids the burst-delivery stall reproduced with automatic path selection. No system networking, server setting, account authentication, or security control is changed. Relay use can have higher latency than a working direct path.

Legacy `mcl1_…` joining and **Share with Tailcat** hosting keep their existing behavior and six original native binaries. Those binaries cover macOS, Linux, and Windows on amd64 and arm64, but they do **not** provide modern-invitation support outside Apple silicon. This release deliberately rejects modern invitations on other platforms. Do not infer platform support from the legacy binaries.

## Validation

- Java 17 Forge build and 10 Java tests passed.
- Go helper regression tests passed, including private-input validation and a 4 MiB bidirectional-copy/half-close fixture.
- An isolated client-only relay control delivered the login-shaped 726,597-byte server burst, another 1.5 MiB to the client, and 1 MiB back plus acknowledgement, with the fixture server unchanged.
- The released JAR completed a real authenticated button join into a live world on Apple silicon.
- Other operating systems/architectures have not received a live modern-helper test. This release does not ship or enable them.

The server must independently match the pack's required mod channels. The successful server also included MezzConfig 0.6.3 and Pretty Rain 1.1.3. This client update contains neither server configuration nor server-only patches.

## Source and build

The original Fabric source is in `mod/`. The Forge port is in `forge/`. The modern SDK helper source and pinned dependencies are in `forge/psk-join/`; its checked binary and checksum are in `forge/src/main/resources/assets/mclink/psk-native/`.

The shipped helper was built with Go 1.27.4, `CGO_ENABLED=0`, `GOOS=darwin`, `GOARCH=arm64`, `-trimpath`, `-tags=ts_omit_ssh`. To rebuild it from `forge/psk-join/`:

```sh
CGO_ENABLED=0 GOOS=darwin GOARCH=arm64 go build -trimpath -tags=ts_omit_ssh -o ../src/main/resources/assets/mclink/psk-native/darwin-arm64/mclink-psk-join .
go test ./...
```

If rebuilding the helper, update its SHA-256 in the adjacent `checksums.json`. Then, using JDK 17:

```sh
cd forge
./gradlew test build
```

The reobfuscated JAR is in `forge/build/libs/`. Rebuilding can change ZIP timestamps and the JAR checksum; use the published asset checksum when installing the verified release.

## Earlier pack and recipe add-on

The [original importable pack](https://github.com/divyamk/stormforge-tailcat/releases/tag/stormforge-2.3.0-tailcat-0.1.1) bundles Tailcat 0.1.1; it does not contain this modern-server fix. Existing profiles should receive the single-JAR update above.

The separate [recipe compatibility add-on](https://github.com/divyamk/stormforge-tailcat/releases/tag/recipe-compat-0.1.0) and [its guide](FIX_JEI_WITH_GPT.md) address the EMI/TMRV crafting-table rendering issue. They are separate from this transport update.

## Attribution and license

This is a fork of [Tailcat for Minecraft](https://github.com/tailscale/tailcat-for-minecraft), not an official Stormforge source repository. It is not endorsed by Tailscale or the pack authors. Tailcat is a prototype, not an officially supported Tailscale product.

Tailcat for Minecraft and its original native helpers are BSD-3-Clause; see [LICENSE](LICENSE) and [original README](README-UPSTREAM.md). The modern helper uses the official [Tailcat SDK](https://github.com/tailscale/tailcat). Dependency notices accompany the release in `THIRD_PARTY_LICENSES.txt`; retain them when redistributing its binaries.

[Stormforge](https://modrinth.com/modpack/stormforge) is a separate modpack. Its constituent mods retain their own licenses; this release distributes only the Tailcat client update.
