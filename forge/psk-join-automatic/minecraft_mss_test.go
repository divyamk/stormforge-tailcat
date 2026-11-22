//go:build tailcat_minecraft_mss

package main

import (
	"context"
	"gvisor.dev/gvisor/pkg/tcpip"
	"gvisor.dev/gvisor/pkg/tcpip/adapters/gonet"
	"gvisor.dev/gvisor/pkg/tcpip/header"
	"gvisor.dev/gvisor/pkg/tcpip/link/channel"
	"gvisor.dev/gvisor/pkg/tcpip/network/ipv6"
	"gvisor.dev/gvisor/pkg/tcpip/stack"
	"gvisor.dev/gvisor/pkg/tcpip/transport/tcp"
	"io"
	"net"
	"sync/atomic"
	"testing"
	"time"
)

func fixtureIPv6Stack(t *testing.T, last byte) (*stack.Stack, *channel.Endpoint, tcpip.Address) {
	a := [16]byte{0xfd}
	a[15] = last
	addr := tcpip.AddrFrom16(a)
	s := stack.New(stack.Options{NetworkProtocols: []stack.NetworkProtocolFactory{ipv6.NewProtocol}, TransportProtocols: []stack.TransportProtocolFactory{tcp.NewProtocol}})
	ep := channel.New(512, 1280, "")
	if s.CreateNIC(1, ep) != nil {
		t.Fatal("nic")
	}
	if s.AddProtocolAddress(1, tcpip.ProtocolAddress{Protocol: ipv6.ProtocolNumber, AddressWithPrefix: addr.WithPrefix()}, stack.AddressProperties{}) != nil {
		t.Fatal("address")
	}
	s.SetRouteTable([]tcpip.Route{{Destination: header.IPv6EmptySubnet, NIC: 1}})
	t.Cleanup(func() { ep.Close(); s.Close(); s.Wait() })
	return s, ep, addr
}
func TestFixtureMSSOnWire(t *testing.T) {
	defer gonet.ConfigureMinecraftMSS(0)
	baseline := map[uint16]int64{}
	for _, setting := range []int{0, 1000} {
		for _, port := range []uint16{25565, 25678} {
			if !gonet.ConfigureMinecraftMSS(setting) {
				t.Fatal("configuration")
			}
			ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
			cs, cl, _ := fixtureIPv6Stack(t, 1)
			ss, sl, sa := fixtureIPv6Stack(t, 2)
			var synMSS, maxData, dataPackets atomic.Int64
			go relayTestPackets(ctx, cl, sl, func(p *stack.PacketBuffer) bool {
				v := p.ToView()
				defer v.Release()
				b := v.AsSlice()
				if len(b) >= 60 {
					h := header.TCP(b[40:])
					if h.Flags()&header.TCPFlagSyn != 0 && h.Flags()&header.TCPFlagAck == 0 {
						synMSS.Store(int64(header.ParseSynOptions(h.Options(), false).MSS))
					}
				}
				return true
			})
			go relayTestPackets(ctx, sl, cl, func(p *stack.PacketBuffer) bool {
				v := p.ToView()
				defer v.Release()
				b := v.AsSlice()
				if len(b) >= 60 {
					h := header.TCP(b[40:])
					n := int64(len(b) - 40 - int(h.DataOffset()))
					if n > 0 {
						dataPackets.Add(1)
						for old := maxData.Load(); n > old; old = maxData.Load() {
							if maxData.CompareAndSwap(old, n) {
								break
							}
						}
					}
				}
				return true
			})
			ln, e := gonet.ListenTCP(ss, tcpip.FullAddress{NIC: 1, Addr: sa, Port: port}, ipv6.ProtocolNumber)
			if e != nil {
				t.Fatal("listen")
			}
			accepted := make(chan net.Conn, 1)
			go func() {
				c, e := ln.Accept()
				if e == nil {
					accepted <- c
				}
			}()
			c, e := gonet.DialContextTCP(ctx, cs, tcpip.FullAddress{NIC: 1, Addr: sa, Port: port}, ipv6.ProtocolNumber)
			if e != nil {
				t.Fatal("dial")
			}
			var peer net.Conn
			select {
			case peer = <-accepted:
			case <-ctx.Done():
				t.Fatal("accept timeout")
			}
			_ = c.SetDeadline(time.Now().Add(3 * time.Second))
			_ = peer.SetDeadline(time.Now().Add(3 * time.Second))
			payload := make([]byte, 65536)
			for i := range payload {
				payload[i] = byte(i*31 + 17)
			}
			sent := make(chan bool, 1)
			go func() { n, e := peer.Write(payload); sent <- e == nil && n == len(payload) }()
			got := make([]byte, len(payload))
			_, err := io.ReadFull(c, got)
			if err != nil || !<-sent {
				t.Fatal("transfer")
			}
			for i := range got {
				if got[i] != payload[i] {
					t.Fatal("generated data mismatch")
				}
			}
			want := int64(1220)
			if setting == 1000 && port == 25565 {
				want = 1000
			}
			if synMSS.Load() != want {
				t.Fatalf("SYN MSS got%d want%d", synMSS.Load(), want)
			}
			if maxData.Load() <= 0 || maxData.Load() > want || dataPackets.Load() < 2 {
				t.Fatal("segment bounds")
			}
			if setting == 0 {
				baseline[port] = maxData.Load()
			} else if port == 25565 {
				if maxData.Load() >= baseline[port] {
					t.Fatal("bulk segments did not shrink")
				}
			} else if maxData.Load() != baseline[port] {
				t.Fatal("control segment size changed")
			}
			t.Logf("setting=%d bulk=%t syn_mss=%d max_payload=%d packets=%d verified_bytes=%d", setting, port == 25565, synMSS.Load(), maxData.Load(), dataPackets.Load(), len(got))
			c.Close()
			peer.Close()
			ln.Close()
			cancel()
		}
	}
	if gonet.ConfigureMinecraftMSS(999) {
		t.Fatal("unsupported MSS accepted")
	}
}

func relayTestPackets(ctx context.Context, src, dst *channel.Endpoint, packet func(*stack.PacketBuffer) bool) {
	for {
		pkt := src.ReadContext(ctx)
		if pkt == nil {
			return
		}
		forward := packet == nil || packet(pkt)
		if forward {
			in := stack.NewPacketBuffer(stack.PacketBufferOptions{Payload: pkt.ToBuffer()})
			dst.InjectInbound(pkt.NetworkProtocolNumber, in)
			in.DecRef()
		}
		pkt.DecRef()
	}
}
