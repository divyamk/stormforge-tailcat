package com.tailscale.mclink;

/** Per-attempt policy for the modern helper only. */
public enum JoinMode {
    RELAY("Mode: Relay (default)", "Uses the encrypted relay."),
    AUTOMATIC("Mode: Automatic (try direct)", "Tries direct; relay remains available.");

    private final String label;
    private final String hint;

    JoinMode(String label, String hint) {
        this.label = label;
        this.hint = hint;
    }

    public static JoinMode defaultMode() { return RELAY; }
    public JoinMode next() { return this == RELAY ? AUTOMATIC : RELAY; }
    public String label() { return label; }
    public String hint() { return hint; }

    String[] modernCommand(String executable) {
        return this == AUTOMATIC ? new String[] { executable, "--minecraft-mss=1000" } : new String[] { executable };
    }

    void configure(ProcessBuilder child) {
        if (this == RELAY) {
            child.environment().put("TS_DEBUG_ALWAYS_USE_DERP", "true");
        } else {
            child.environment().remove("TS_DEBUG_ALWAYS_USE_DERP");
        }
    }
}
