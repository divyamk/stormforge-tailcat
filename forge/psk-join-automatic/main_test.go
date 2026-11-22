package main

import (
	"bufio"
	"context"
	"io"
	"net"
	"strings"
	"testing"
	"time"
)

func TestInvalidAddressIsNotEchoed(t *testing.T) {
	for _, raw := range []string{"not-an-address", "tcPRIVATE-invalid-token", "tc" + strings.Repeat("A", maxAddress) + "\n"} {
		_, err := readAddress(bufio.NewReaderSize(strings.NewReader(raw), maxAddress+2))
		if err == nil || strings.Contains(err.Error(), raw) {
			t.Fatal("missing sanitized error")
		}
	}
}

func TestLoopbackBridge(t *testing.T) {
	ln, err := net.Listen("tcp4", "127.0.0.1:0")
	if err != nil {
		t.Fatal(err)
	}
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()
	stopped := make(chan struct{})
	go func() {
		defer close(stopped)
		_ = serve(ctx, ln, func(context.Context) (net.Conn, error) {
			a, b := net.Pipe()
			go func() {
				defer b.Close()
				p := make([]byte, 4)
				if _, e := io.ReadFull(b, p); e == nil {
					_, _ = b.Write(p)
				}
			}()
			return a, nil
		})
	}()
	c, err := net.DialTimeout("tcp4", ln.Addr().String(), 2*time.Second)
	if err != nil {
		t.Fatal(err)
	}
	defer c.Close()
	_ = c.SetDeadline(time.Now().Add(2 * time.Second))
	if _, err = c.Write([]byte("ping")); err != nil {
		t.Fatal(err)
	}
	p := make([]byte, 4)
	if _, err = io.ReadFull(c, p); err != nil || string(p) != "ping" {
		t.Fatal("mock gateway did not echo:", err)
	}
	cancel()
	select {
	case <-stopped:
	case <-time.After(2 * time.Second):
		t.Fatal("listener did not stop")
	}
}
