# Update an Apple silicon Mac for a modern Tailcat server

Copy the following prompt into a bot with authorized access to your Mac. It contains no server invitation.

---

Please update my existing Stormforge Minecraft profile so I can join my friend's server through **Multiplayer → Connect with Tailcat**.

First verify this is an Apple silicon Mac running macOS 13+, with an arm64 Java 17 runtime. Locate my existing **Stormforge: Never Ending Rain! 2.3.0** profile (Modrinth version **k5ownM36**), Minecraft **1.20.1**, Forge **47.4.10**. If the platform or profile differs, stop and explain the mismatch before changing anything. Do not create a replacement world or alter unrelated mods.

Use this release only:
https://github.com/divyamk/stormforge-tailcat/releases/tag/tailcat-forge-0.1.3

Download:
https://github.com/divyamk/stormforge-tailcat/releases/download/tailcat-forge-0.1.3/tailcat-for-minecraft-0.1.3-forge-1.20.1.jar

Required SHA-256:
`3e037a498b8966b9c7e29978fa251784c0a8069d1ba66518f2f62b61cde16efd`

Ask me to quit Minecraft if it is running, then verify it has exited. Back up my current Tailcat JAR outside the mods folder, record its checksum and original filename, and verify the backup before replacing anything. Verify the downloaded JAR's exact SHA-256 and version, then replace only the Tailcat client mod, leaving exactly one enabled Tailcat JAR. Preserve all saves, other mods, launcher/account settings, and server settings. Keep a clear rollback path to the verified backup. Do not install server-only compatibility patches or disable authentication.

This is the verified Forge 0.1.3 build for modern Tailcat invitations on Apple silicon. It keeps only its modern helper subprocess on the existing encrypted relay. Legacy joining/hosting remains unchanged. It does not require public ports or system network changes.

When ready, have me launch the same profile and use **Multiplayer → Connect with Tailcat** once. I will obtain the invitation privately from my friend and paste it directly into the game. Do not request, read, echo, log, commit, or send the invitation or any login token. Do not join by terminal instead of the in-game button.

Verify success by actual authenticated entry into the server world. If there is an error, capture only its non-sensitive reason and mod/channel names. Do not repeatedly retry, remove mods, update the whole pack, or weaken security. Report the blocker and next narrow action. The server owner is responsible for server-side compatibility.

---
