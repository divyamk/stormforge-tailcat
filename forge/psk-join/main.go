package main

import (
	"bufio"
	"context"
	"encoding/json"
	"errors"
	"io"
	"net"
	"os"
	"os/signal"
	"regexp"
	"sync"
	"syscall"
	"time"

	"github.com/tailscale/tailcat"
)

const maxAddress = 8192
const minecraftPort = 25565

var validAddress = regexp.MustCompile(`^tc[A-Za-z0-9_-]+$`)

type event struct {
	Type    string `json:"type"`
	Mode    string `json:"mode,omitempty"`
	Address string `json:"address,omitempty"`
	Code    string `json:"code,omitempty"`
	Message string `json:"message,omitempty"`
}

func readAddress(r *bufio.Reader) (tailcat.Addr, error) {
	line, err := r.ReadSlice('\n')
	if err != nil || len(line) > maxAddress+1 || len(line) < 4 ||
		!validAddress.Match(line[:len(line)-1]) {
		return "", errors.New("invalid Tailcat address")
	}
	addr := tailcat.Addr(string(line[:len(line)-1]))
	ci, err := tailcat.ParseAddr(addr)
	if err != nil || ci.ServerDiscoPublic.IsZero() {
		return "", errors.New("invalid or unsupported Tailcat address")
	}
	return addr, nil
}

func main() {
	enc := json.NewEncoder(os.Stdout)
	emit := func(e event) { _ = enc.Encode(e) }
	defer emit(event{Type: "stopped"})
	ctx, cancel := signal.NotifyContext(context.Background(), os.Interrupt, syscall.SIGTERM)
	defer cancel()
	reader := bufio.NewReaderSize(os.Stdin, maxAddress+2)
	address, err := readAddress(reader)
	if err != nil {
		emit(event{Type: "error", Code: "invalid_invite", Message: "Invalid or unsupported Tailcat address."})
		return
	}
	go func() { _, _ = io.Copy(io.Discard, reader); cancel() }()
	// Do not log SDK messages: an address contains the peer's pre-shared key.
	c := &tailcat.Client{Server: address, Logf: func(string, ...any) {}}
	defer c.Close()
	ln, err := net.Listen("tcp4", "127.0.0.1:0")
	if err != nil {
		emit(event{Type: "error", Code: "helper_failed", Message: "Could not open a local Minecraft connection."})
		return
	}
	defer ln.Close()
	emit(event{Type: "ready", Mode: "join", Address: ln.Addr().String()})
	if err := serve(ctx, ln, func(ctx context.Context) (net.Conn, error) {
		return c.DialTCPPort(ctx, minecraftPort)
	}); err != nil && ctx.Err() == nil {
		emit(event{Type: "error", Code: "helper_failed", Message: "The local Minecraft connection stopped."})
	}
}

func serve(ctx context.Context, ln net.Listener, dial func(context.Context) (net.Conn, error)) error {
	ctx, cancel := context.WithCancel(ctx)
	var wg sync.WaitGroup
	defer wg.Wait()
	defer cancel()
	stop := context.AfterFunc(ctx, func() { _ = ln.Close() })
	defer stop()
	slots := make(chan struct{}, 64)
	for {
		local, err := ln.Accept()
		if err != nil {
			return err
		}
		select {
		case slots <- struct{}{}:
		default:
			_ = local.Close()
			continue
		}
		wg.Add(1)
		go func() {
			defer wg.Done()
			defer func() { <-slots }()
			defer local.Close()
			stopLocal := context.AfterFunc(ctx, func() { _ = local.Close() })
			defer stopLocal()
			dialCtx, finish := context.WithTimeout(ctx, 15*time.Second)
			remote, err := dial(dialCtx)
			finish()
			if err != nil {
				return
			}
			defer remote.Close()
			stopRemote := context.AfterFunc(ctx, func() { _ = remote.Close() })
			defer stopRemote()
			tailcat.ProxyConns(local, remote)
		}()
	}
}
