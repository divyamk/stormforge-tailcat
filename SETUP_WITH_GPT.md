# Paste this into your GPT / Codex

Help me install my friend's Stormforge + Tailcat pack in Modrinth App so I can join their world. If you have computer access, perform the installation; otherwise give me step-by-step instructions.

Repository: https://github.com/divyamk/stormforge-tailcat
Pinned release: https://github.com/divyamk/stormforge-tailcat/releases/tag/stormforge-2.3.0-tailcat-0.1.1
Ready-to-import pack: https://github.com/divyamk/stormforge-tailcat/releases/download/stormforge-2.3.0-tailcat-0.1.1/Stormforge-Tailcat-2.3.0-0.1.1.mrpack
Checksums: https://github.com/divyamk/stormforge-tailcat/releases/download/stormforge-2.3.0-tailcat-0.1.1/SHA256SUMS

Required versions: Stormforge: Never Ending Rain 2.3.0, Minecraft 1.20.1, Forge 47.4.10, Java 17, and Tailcat Forge port 0.1.1. Use this pinned release, not the newest Stormforge version or the original Fabric Tailcat mod.

1. Check whether Modrinth App and an exact Stormforge 2.3.0 instance are already installed. Preserve my existing worlds, accounts, and unrelated instances.
2. If I do not already have that exact pack, download the .mrpack above, verify its SHA-256 against SHA256SUMS, and import it as a new instance named “Stormforge + Tailcat.” Tailcat is already included. Let Modrinth install the required dependencies and managed Java runtime.
3. If I already have an unmodified Stormforge 2.3.0 instance, add only this JAR through the instance's Content → Upload files control, or its mods folder if needed:
   https://github.com/divyamk/stormforge-tailcat/releases/download/stormforge-2.3.0-tailcat-0.1.1/tailcat-for-minecraft-0.1.1-forge-1.20.1.jar
   Verify its checksum and ensure only one Tailcat JAR is enabled. If my existing instance has other custom mods, prefer a new imported instance to avoid a mismatch with the host.
4. Let me personally complete Minecraft account sign-in if needed. Do not ask me to paste passwords or session tokens into chat.
5. Launch the instance and check that it reaches the title screen without a Forge loading error. Open Multiplayer and verify the “Connect with Tailcat” button. Stop there until I have a fresh invitation from my friend.
6. Explain that my friend must open their world, pause, click “Share with Tailcat,” and send me the invitation privately. I paste it into Multiplayer → Connect with Tailcat. Keep the invitation out of public posts, screenshots, and logs. The host must keep Minecraft and the world open.
7. If the first connection times out, retry after a few seconds. If it still fails, inspect the current logs and check matching versions and whether the host is still sharing. Do not disable authentication, firewalls, or other security settings to troubleshoot.

No Tailscale app or port forwarding should be needed. This is an unofficial prototype port: build/startup and a local transport test passed, but two-computer gameplay has not yet been verified. Tell me exactly what you verified and any remaining blocker.

If the crafting-table recipe view shows “Error Rendering,” follow https://github.com/divyamk/stormforge-tailcat/blob/main/FIX_JEI_WITH_GPT.md to install the separate EMI/TMRV compatibility add-on. The original .mrpack does not contain that add-on.
