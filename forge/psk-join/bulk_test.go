package main

import (
	"bytes"
	"context"
	"crypto/sha256"
	"fmt"
	"io"
	"net"
	"testing"
	"time"
)

func TestBulkLoginCopyFixture(t *testing.T) {
	ln, err := net.Listen("tcp4", "127.0.0.1:0")
	if err != nil {
		t.Fatal(err)
	}
	backend, err := net.Listen("tcp4", "127.0.0.1:0")
	if err != nil {
		ln.Close()
		t.Fatal(err)
	}
	defer backend.Close()
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()
	stopped := make(chan error, 1)
	go func() {
		stopped <- serve(ctx, ln, func(ctx context.Context) (net.Conn, error) {
			return (&net.Dialer{}).DialContext(ctx, "tcp4", backend.Addr().String())
		})
	}()
	payload := make([]byte, 4<<20)
	for i := range payload {
		payload[i] = byte(i ^ (i >> 8) ^ (i >> 15))
	}
	serverResult := make(chan error, 1)
	go func() {
		serverResult <- func() error {
			conn, err := backend.Accept()
			if err != nil {
				return err
			}
			defer conn.Close()
			conn.SetDeadline(time.Now().Add(6 * time.Second))
			request := make([]byte, 16)
			if _, err := io.ReadFull(conn, request); err != nil {
				return err
			}
			if !bytes.Equal(request, []byte("login-challenge!")) {
				return fmt.Errorf("wrong mock login request")
			}
			for i := 0; i < 40; i++ {
				start, end := i*len(payload)/40, (i+1)*len(payload)/40
				if _, err := conn.Write(payload[start:end]); err != nil {
					return err
				}
			}
			var ack [3]byte
			if _, err := io.ReadFull(conn, ack[:]); err != nil {
				return err
			}
			if string(ack[:]) != "ACK" {
				return fmt.Errorf("missing mock login acknowledgement")
			}
			if err := conn.(*net.TCPConn).CloseWrite(); err != nil {
				return err
			}
			var fin [1]byte
			if n, err := conn.Read(fin[:]); n != 0 || err != io.EOF {
				return fmt.Errorf("missing client half-close; bytes=%d, error=%v", n, err)
			}
			return nil
		}()
	}()
	conn, err := net.DialTimeout("tcp4", ln.Addr().String(), 2*time.Second)
	if err != nil {
		t.Fatal(err)
	}
	defer conn.Close()
	conn.SetDeadline(time.Now().Add(6 * time.Second))
	if _, err := conn.Write([]byte("login-challenge!")); err != nil {
		t.Fatal(err)
	}
	h := sha256.New()
	if n, err := io.CopyN(h, conn, int64(len(payload))); err != nil || n != int64(len(payload)) {
		t.Fatalf("payload incomplete: %d: %v", n, err)
	}
	expected := sha256.Sum256(payload)
	if !bytes.Equal(h.Sum(nil), expected[:]) {
		t.Fatal("bulk payload hash mismatch")
	}
	if _, err := conn.Write([]byte("ACK")); err != nil {
		t.Fatal(err)
	}
	if err := conn.(*net.TCPConn).CloseWrite(); err != nil {
		t.Fatal(err)
	}
	var fin [1]byte
	if n, err := conn.Read(fin[:]); n != 0 || err != io.EOF {
		t.Fatalf("missing server half-close: %d: %v", n, err)
	}
	select {
	case err := <-serverResult:
		if err != nil {
			t.Fatal(err)
		}
	case <-time.After(2 * time.Second):
		t.Fatal("server fixture did not finish")
	}
	cancel()
	select {
	case <-stopped:
	case <-time.After(2 * time.Second):
		t.Fatal("helper did not stop")
	}
}
