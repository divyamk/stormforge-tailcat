# Tailcat for Minecraft 0.1.5 — Forge 1.20.1 / Apple silicon

Adds a separate Automatic-mode helper to improve sustained Minecraft connections on the tested Apple silicon setup. Automatic uses smaller TCP segments for the Minecraft connection while retaining Tailcat discovery and encrypted relay fallback. Authenticated in-game joining and more than two minutes of continuous gameplay were verified on one Apple silicon Mac; the player reported that gameplay was working well.

Open **Multiplayer → Connect with Tailcat**, paste the invitation privately, then select **Mode: Automatic (try direct)** before connecting. **Relay remains the default** and is available as the original fallback. The existing Relay helper and all six legacy native helpers are byte-for-byte unchanged; legacy joining and hosting remain unchanged.

Supported configuration: Apple silicon Mac, macOS 13+, arm64 Java 17; Stormforge: Never Ending Rain! 2.3.0 (Modrinth version k5ownM36), Minecraft 1.20.1, Forge 47.4.10. This new Automatic helper is not provided for Intel Macs, Windows or Linux. No system networking, public ports, authentication, server settings, other mods or worlds are changed by this client update.

Artifact: tailcat-for-minecraft-0.1.5-forge-1.20.1.jar
SHA-256: 15b8ede488c45f82889a735a6cea4a0c29b996ed9a7e8c5bb2e243b4d2e3750f

Validation: 14 Forge tests and 6 Go tests passed, including Minecraft-port-only segmentation and unchanged non-game behavior. The packaged Relay and legacy binaries were compared with 0.1.4 and match exactly. The exact released JAR passed authenticated joining and more than two minutes of sustained gameplay on one Mac/network path. These results do not guarantee performance on other networks.

Installation: quit Minecraft and verify it has exited; verify the downloaded JAR checksum; back up the current Tailcat JAR outside mods and verify that backup; replace only Tailcat, keeping exactly one enabled Tailcat JAR. Preserve the original filename/checksum for rollback. Launch the existing profile and select Automatic explicitly. Obtain and enter invitations privately in-game; never put them in commands, logs or repository files.

Rollback: with Minecraft closed, restore the verified prior Tailcat JAR. Automatic uses a separate versioned helper cache, preserving the existing Relay/legacy cache paths. No server rollback is required for this client-only change.
