package com.tailscale.mclink;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JoinInputTest {
    @Test void preservesLegacyAndAllowsRawAddress() {
        assertTrue(JoinInput.accepts(" mcl1_example "));
        assertFalse(JoinInput.isPskAddress("mcl1_example"));
        assertTrue(JoinInput.accepts(" tcValidExample_1-2 "));
        assertTrue(JoinInput.isPskAddress("tcValidExample_1-2"));
    }

    @Test void refusesUnexpectedRawInput() {
        assertFalse(JoinInput.accepts("tcWith Spaces"));
        assertFalse(JoinInput.accepts("tcWith\nNewline"));
        assertFalse(JoinInput.accepts("https://example.com"));
        assertFalse(JoinInput.accepts("tc" + "A".repeat(8191)));
    }
}
