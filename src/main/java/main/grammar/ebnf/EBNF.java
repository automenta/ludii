// 
// Decompiled by Procyon v0.5.36
// 

package main.grammar.ebnf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EBNF
{
    private final Map<String, EBNFRule> rules;
    
    public EBNF(final String grammar) {
        this.rules = new HashMap<>();
        this.interpret(grammar);
    }
    
    public Map<String, EBNFRule> rules() {
        return Collections.unmodifiableMap(this.rules);
    }
    
    public static boolean isTerminal(final String token) {
        return token.charAt(0) != '<' && token.charAt(token.length() - 1) != '>' && !token.trim().contains(" ");
    }
    
    public void interpret(final String grammar) {
        final String[] split = grammar.trim().split("\n");
        for (int n = 0; n < split.length; ++n) {
            final int c = split[n].indexOf("//");
            if (c >= 0) {
                split[n] = split[n].substring(0, c);
            }
        }
        for (int n = split.length - 1; n >= 1; --n) {
            final int c = split[n].indexOf("::=");
            if (c < 0) {
                final StringBuilder sb = new StringBuilder();
                final String[] array = split;
                final int n2 = n - 1;
                array[n2] = sb.append(array[n2]).append(" ").append(split[n].trim()).toString();
                split[n] = "";
            }
        }
        for (int n = 0; n < split.length; ++n) {
            if (split[n].contains("::=")) {
                String strRule;
                for (strRule = split[n]; strRule.contains("  "); strRule = strRule.replaceAll("  ", " ")) {}
                final EBNFRule rule = new EBNFRule(strRule);
                this.rules.put(rule.lhs(), rule);
            }
        }
    }
}
