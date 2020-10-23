/*
 * Decompiled with CFR 0.150.
 */
package collections;

import root.StringRoutines;

import java.util.regex.Pattern;

public class StringPair {
    private final String key;
    private final String value;

    public StringPair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String key() {
        return this.key;
    }

    public String value() {
        return this.value;
    }

    public String toString() {
        return "{ " + StringRoutines.quote(this.key) + " " + StringRoutines.quote(this.value) + " }";
    }

    public static StringPair fromString(String str) {
        String s = str.replaceAll(Pattern.quote("{"), "");
        s = s.replaceAll(Pattern.quote("}"), "");
        s = s.replaceAll(Pattern.quote("\""), "");
        s = s.trim();
        String[] split = s.split(Pattern.quote(" "));
        return new StringPair(split[0], split[1]);
    }
}

