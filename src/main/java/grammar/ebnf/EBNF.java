/*
 * Decompiled with CFR 0.150.
 */
package grammar.ebnf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EBNF {
    private final Map<String, EBNFRule> rules = new HashMap<>();

    public EBNF(String grammar) {
        this.interpret(grammar);
    }

    public Map<String, EBNFRule> rules() {
        return Collections.unmodifiableMap(this.rules);
    }

    public static boolean isTerminal(String token) {
        return token.charAt(0) != '<' && token.charAt(token.length() - 1) != '>' && !token.trim().contains(" ");
    }

    public void interpret(String grammar) {
        int c;
        int n;
        String[] split = grammar.trim().split("\n");
        for (n = 0; n < split.length; ++n) {
            c = split[n].indexOf("//");
            if (c < 0) continue;
            split[n] = split[n].substring(0, c);
        }
        for (n = split.length - 1; n >= 1; --n) {
            c = split[n].indexOf("::=");
            if (c >= 0) continue;
            int n2 = n - 1;
            split[n2] = split[n2] + " " + split[n].trim();
            split[n] = "";
        }
        for (n = 0; n < split.length; ++n) {
            if (!split[n].contains("::=")) continue;
            String strRule = split[n];
            while (strRule.contains("  ")) {
                strRule = strRule.replaceAll("  ", " ");
            }
            EBNFRule rule = new EBNFRule(strRule);
            this.rules.put(rule.lhs(), rule);
        }
    }
}

