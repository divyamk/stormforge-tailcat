package main

import "testing"

func TestAutomaticArgumentGuard(t *testing.T) {
	for _, a := range [][]string{nil, {"--minecraft-mss=0"}, {"--minecraft-mss=999"}, {"--minecraft-mss=1000", "extra"}} {
		if configureAutomatic(a) {
			t.Fatal("invalid Automatic arguments accepted")
		}
	}
	if !configureAutomatic([]string{"--minecraft-mss=1000"}) {
		t.Fatal("Automatic configuration rejected")
	}
}
