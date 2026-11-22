# Automatic helper source reconstruction

This is the source of the separately packaged Automatic-only helper. Relay source/binary remains unchanged. No runtime diagnostic observers are included.

Pinned sources: Tailcat v0.7.0 (15ab9e68bfc6534a61797d7af28cedd42b54a3a5), gVisor v0.0.0-20260915211658-a6f909f08a72. The exact gVisor changes are in patches/gvisor-minecraft-mss.patch; upstream licensing is retained in patches/GVISOR_LICENSE and the release's THIRD_PARTY_LICENSES.txt.

Use Go 1.27.4. The tested go.mod replacement is relative to a bounded build workspace with this layout:

```text
WORK/source/forge/psk-join-automatic/   (this directory)
WORK/gvisor/                           (exact pinned module plus supplied patch)
```

Create a fresh temporary WORK directory. Copy this directory to the location above. Obtain the exact pinned gVisor module through Go's module downloader and verify its module/ZIP sums against this directory's go.sum, then copy it to WORK/gvisor. Make only the copied source writable and apply patches/gvisor-minecraft-mss.patch with patch -p1 from WORK/gvisor. Do not edit your shared module cache. Preserve the relative go.mod replacement; no dependency version change is needed.

From WORK/source/forge/psk-join-automatic:

```sh
go mod verify
GOMAXPROCS=2 GOMEMLIMIT=1GiB go test -p=2 -mod=readonly -tags=tailcat_minecraft_mss ./...
GOMAXPROCS=2 GOMEMLIMIT=1GiB go vet -p=2 -mod=readonly -tags=tailcat_minecraft_mss ./...
GOMAXPROCS=2 GOMEMLIMIT=1GiB CGO_ENABLED=0 GOOS=darwin GOARCH=arm64 go build -p=2 -mod=readonly -trimpath -tags=tailcat_minecraft_mss -o mclink-psk-join-automatic .
```

The verified shipped native SHA-256 is f903e433b5e9919623f8a56b12fdd66252b2e3623c687b3e21ea531a61e9d9d3. The helper accepts only --minecraft-mss=1000 on Darwin arm64, configures the option before any dial, and applies it only to virtual TCP25565. Other ports retain normal behavior.

For a deliberate rebuild, place the output in forge/src/main/resources/assets/mclink/automatic-native/darwin-arm64/ and update only that resource's checksums.json. Using JDK17, run ./gradlew test jar from forge; the reobfJar task produces the distributable. Do not overwrite Relay or legacy resources. Rebuilds are not permission to modify an installed game profile.
