package com.tailscale.mclink;

import java.util.regex.Pattern;

final class JoinInput {
    private static final Pattern ADDRESS = Pattern.compile("tc[A-Za-z0-9_-]+");

    private JoinInput() {}

    static boolean isPskAddress(String s) {
        return s != null && s.length() <= 8192 && ADDRESS.matcher(s).matches();
    }

    static boolean accepts(String s) {
        if (s == null) return false;
        String v = s.trim();
        return v.startsWith("mcl1_") || isPskAddress(v);
    }
}
