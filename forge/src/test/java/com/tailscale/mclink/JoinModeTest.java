package com.tailscale.mclink;

import java.nio.file.Path;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JoinModeTest {
    @Test void relayIsDefaultAndSelectionIsExplicit() {
        assertEquals(JoinMode.RELAY, JoinMode.defaultMode());
        assertEquals(JoinMode.AUTOMATIC, JoinMode.defaultMode().next());
        assertEquals(JoinMode.RELAY, JoinMode.AUTOMATIC.next());
    }

    @Test void actualChildrenReceiveIsolatedModes() throws Exception {
        ProcessBuilder relay = child();
        ProcessBuilder automatic = child();
        relay.environment().put("TS_DEBUG_ALWAYS_USE_DERP", "false");
        automatic.environment().put("TS_DEBUG_ALWAYS_USE_DERP", "true");
        var originalAuto = new HashMap<>(automatic.environment());
        JoinMode.RELAY.configure(relay);
        assertEquals(originalAuto, automatic.environment());
        JoinMode.AUTOMATIC.configure(automatic);
        assertEquals("true|kept", output(relay));
        assertEquals("unset|kept", output(automatic));
        assertEquals("true", relay.environment().get("TS_DEBUG_ALWAYS_USE_DERP"));
    }

    @Test void onlyTheForceVariableChanges() {
        ProcessBuilder p = child();
        p.environment().put("TS_DEBUG_ALWAYS_USE_DERP", "true");
        var expected = new HashMap<>(p.environment());
        expected.remove("TS_DEBUG_ALWAYS_USE_DERP");
        JoinMode.AUTOMATIC.configure(p);
        assertEquals(expected, p.environment());
        expected.put("TS_DEBUG_ALWAYS_USE_DERP", "true");
        JoinMode.RELAY.configure(p);
        assertEquals(expected, p.environment());
    }

    @Test void automaticCommandIsExplicitAndRelayUnchanged() {
        assertArrayEquals(new String[] { "helper" }, JoinMode.RELAY.modernCommand("helper"));
        assertArrayEquals(new String[] { "helper", "--minecraft-mss=1000" }, JoinMode.AUTOMATIC.modernCommand("helper"));
    }

    private static ProcessBuilder child() {
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        String classes = Path.of(JoinModeTest.class.getProtectionDomain().getCodeSource().getLocation().getPath()).toString();
        ProcessBuilder p = new ProcessBuilder(java, "-cp", classes, EnvProbe.class.getName());
        p.environment().put("TAILCAT_MODE_TEST_SENTINEL", "kept");
        return p;
    }

    private static String output(ProcessBuilder p) throws Exception {
        Process child = p.start();
        String value = new String(child.getInputStream().readAllBytes()).trim();
        assertEquals(0, child.waitFor());
        return value;
    }

    public static class EnvProbe {
        public static void main(String[] args) {
            String force = System.getenv("TS_DEBUG_ALWAYS_USE_DERP");
            System.out.print((force == null ? "unset" : force) + "|" + System.getenv("TAILCAT_MODE_TEST_SENTINEL"));
        }
    }
}
