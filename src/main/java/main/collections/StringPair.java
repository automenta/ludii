// 
// Decompiled by Procyon v0.5.36
// 

package main.collections;

import main.StringRoutines;

import java.util.regex.Pattern;

public class StringPair
{
    private final String key;
    private final String value;
    
    public StringPair(final String key, final String value) {
        this.key = key;
        this.value = value;
    }
    
    public String key() {
        return this.key;
    }
    
    public String value() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return "{ " + StringRoutines.quote(this.key) + " " + StringRoutines.quote(this.value) + " }";
    }
    
    public static StringPair fromString(final String str) {
        String s = str.replaceAll(Pattern.quote("{"), "");
        s = s.replaceAll(Pattern.quote("}"), "");
        s = s.replaceAll(Pattern.quote("\""), "");
        s = s.trim();
        final String[] split = s.split(Pattern.quote(" "));
        return new StringPair(split[0], split[1]);
    }
}
