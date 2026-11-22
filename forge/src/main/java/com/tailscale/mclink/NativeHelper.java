package com.tailscale.mclink;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.HexFormat;

public final class NativeHelper {
    private NativeHelper() {}

    public static Path extract() throws IOException {
        String override = System.getenv("MCLINK_HELPER");
        if (override != null && !override.isBlank()) {
            if (FMLEnvironment.production) {
                throw new IOException("MCLINK_HELPER is only available in a development environment");
            }
            Path executable = Path.of(override).toAbsolutePath();
            if (!Files.isRegularFile(executable) || !Files.isExecutable(executable)) {
                throw new IOException("MCLINK_HELPER is not an executable file: " + executable);
            }
            return executable;
        }
        Platform platform = Platform.current();
        String relative = platform.resourceDirectory() + "/" + platform.executableName();
        String resource = "/assets/mclink/native/" + relative;
        JsonObject sums = checksums();
        return extractPackaged(platform, "0.1.1-forge-1.20.1", resource, relative, sums, platform.executableName());
    }

    public static Path extractPskJoin() throws IOException {
        Platform platform = Platform.current();
        if (!"darwin-arm64".equals(platform.resourceDirectory())) {
            throw new IOException("This local Tailcat patch supports only Apple silicon Macs");
        }
        String name = "mclink-psk-join";
        String relative = platform.resourceDirectory() + "/" + name;
        String base = "/assets/mclink/psk-native/";
        JsonObject sums = readChecksums(base + "checksums.json");
        return extractPackaged(platform, "0.1.1-forge-1.20.1-psk-v0.7.0", base + relative,
                relative, sums, name);
    }

    public static Path extractAutomaticJoin() throws IOException {
        Platform platform = Platform.current();
        if (!"darwin-arm64".equals(platform.resourceDirectory())) {
            throw new IOException("Automatic mode supports only Apple silicon Macs");
        }
        String name = "mclink-psk-join-automatic";
        String relative = platform.resourceDirectory() + "/" + name;
        String base = "/assets/mclink/automatic-native/";
        return extractPackaged(platform, "0.1.5-forge-1.20.1-automatic-mss1000", base + relative,
                relative, readChecksums(base + "checksums.json"), name);
    }

    private static Path extractPackaged(Platform platform, String version, String resource,
                                        String relative, JsonObject sums, String executableName) throws IOException {
        if (!sums.has(relative) || !sums.get(relative).isJsonPrimitive()) throw new IOException("Missing checksum for " + relative);
        String expected = sums.get(relative).getAsString();
        Path dir = FMLPaths.GAMEDIR.get().resolve("tailcat-for-minecraft").resolve(version)
                .resolve(platform.resourceDirectory());
        Files.createDirectories(dir);
        Path destination = dir.resolve(executableName);
        Path lockPath = dir.resolve("extract.lock");
        try (FileChannel channel = FileChannel.open(lockPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             FileLock ignored = channel.lock()) {
            if (Files.isRegularFile(destination) && sha256(destination).equalsIgnoreCase(expected)) return destination;
            Path temporary = Files.createTempFile(dir, "mclink-helper-", ".tmp");
            try {
                try (InputStream in = NativeHelper.class.getResourceAsStream(resource)) {
                    if (in == null) {
                        throw new IOException("This mod JAR does not contain a helper for " + platform.resourceDirectory());
                    }
                    Files.copy(in, temporary, StandardCopyOption.REPLACE_EXISTING);
                }
                if (!sha256(temporary).equalsIgnoreCase(expected)) {
                    throw new IOException("Packaged helper checksum mismatch for " + relative);
                }
                if (!platform.os().equals("windows") && !temporary.toFile().setExecutable(true, true)) {
                    throw new IOException("Could not make helper executable: " + temporary);
                }
                try {
                    Files.move(temporary, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                } catch (AtomicMoveNotSupportedException e) {
                    Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING);
                }
                return destination;
            } finally {
                Files.deleteIfExists(temporary);
            }
        }
    }

    static JsonObject checksums() throws IOException {
        return readChecksums("/assets/mclink/native/checksums.json");
    }

    private static JsonObject readChecksums(String resource) throws IOException {
        try (InputStream in = NativeHelper.class.getResourceAsStream(resource)) {
            if (in == null) throw new IOException("Missing native checksums");
            return JsonParser.parseString(new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }

    static String sha256(Path path) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream in = Files.newInputStream(path)) { in.transferTo(new java.security.DigestOutputStream(java.io.OutputStream.nullOutputStream(), digest)); }
            return HexFormat.of().formatHex(digest.digest());
        } catch (java.security.NoSuchAlgorithmException impossible) { throw new AssertionError(impossible); }
    }
}
