package main

import (
	"bufio"
	"strings"
	"testing"

	"github.com/tailscale/tailcat"
	"tailscale.com/types/key"
)

func TestPskAddressFromPrivateInput(t *testing.T) {
	node := key.NewNode()
	ci := tailcat.ConnInfo{
		ServerPublic:      tailcat.NodePublic{NodePublic: node.Public()},
		ServerDiscoPublic: tailcat.DiscoPublicForNode(node),
		PresharedKey:      tailcat.NewPresharedKey(),
		RegionID:          10,
	}
	addr := ci.Addr()
	val, err := readAddress(bufio.NewReader(strings.NewReader(string(addr) + "\n")))
	if err != nil || val != addr {
		t.Fatal("failed to load a PSK address without changing it")
	}
	info, err := tailcat.ParseAddr(val)
	if err != nil || info.PresharedKey.IsZero() || info.PresharedKey != ci.PresharedKey {
		t.Fatal("failed to preserve the ephemeral PSK")
	}
	// Corrupt value is representative of a pasted invitation. Nothing can be echoed.
	const marker = "tcBAD-TEST--"
	_, err = readAddress(bufio.NewReader(strings.NewReader(marker + "\n")))
	if err == nil || strings.Contains(err.Error(), marker) {
		t.Fatal("bad input was not rejected silently")
	}
}
