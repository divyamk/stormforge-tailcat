#!/usr/bin/env python3
"""Rebuild the release .mrpack from the pinned upstream pack and a Forge JAR."""
import argparse
import hashlib
import io
import json
from pathlib import Path
import urllib.request
import zipfile

parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument('jar', type=Path)
parser.add_argument('output', type=Path)
args = parser.parse_args()
root = Path(__file__).resolve().parents[1]
metadata = json.loads((root / 'pack/upstream.json').read_text())
request = urllib.request.Request(metadata['download']['url'], headers={'User-Agent': 'stormforge-tailcat-packager/0.1.1'})
with urllib.request.urlopen(request, timeout=120) as response:
    data = response.read()
if hashlib.sha512(data).hexdigest() != metadata['download']['hashes']['sha512']:
    raise SystemExit('Upstream pack checksum mismatch')
with zipfile.ZipFile(args.jar) as jar:
    if json.loads(jar.read('pack.mcmeta'))['pack']['pack_format'] != 15:
        raise SystemExit('Unexpected resource-pack format')
    if b'0.1.1-forge-1.20.1' not in jar.read('META-INF/mods.toml'):
        raise SystemExit('Expected the Tailcat Forge 0.1.1 JAR')
with zipfile.ZipFile(io.BytesIO(data)) as upstream, zipfile.ZipFile(args.output, 'w', zipfile.ZIP_DEFLATED) as out:
    for item in upstream.infolist():
        if item.filename != 'modrinth.index.json':
            out.writestr(item, upstream.read(item.filename))
    out.write(root / 'pack/modrinth.index.json', 'modrinth.index.json')
    out.write(args.jar, 'overrides/mods/tailcat-for-minecraft-0.1.1-forge-1.20.1.jar')
print(hashlib.sha256(args.output.read_bytes()).hexdigest(), args.output.name)
