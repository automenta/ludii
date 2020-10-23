/*
 * Decompiled with CFR 0.150.
 */
package grammar.ebnf;

import main.StringRoutines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EBNFRule {
    private String lhs = "?";
    private final List<EBNFClause> rhs = new ArrayList<>();

    public EBNFRule(String input) {
        this.decompose(input);
    }

    public String lhs() {
        return this.lhs;
    }

    public List<EBNFClause> rhs() {
        return Collections.unmodifiableList(this.rhs);
    }

    void decompose(String input) {
        String[] sides = input.split("::=");
        this.lhs = sides[0].trim();
        String str = sides[1].trim();
        str = str.replaceAll("<<", "<#");
        str = str.replaceAll("<>", "<@");
        str = str.replaceAll("<>", "#>");
        if ((str = str.replaceAll(">>", "@>")).isEmpty()) {
            System.out.println("** Empty RHS for rule: " + input);
            return;
        }
        int c = 0;
        do {
            int cc;
            if (str.charAt(c) == '(' || str.charAt(c) == '<') {
                cc = StringRoutines.matchingBracketAt(str, c);
                if (cc < 0) {
                    System.out.println("** Failed to load clause from: " + str);
                    return;
                }
            } else {
                for (cc = c + 1; cc < str.length() && str.charAt(cc) != ' '; ++cc) {
                }
            }
            if (cc >= str.length()) {
                --cc;
            }
            String strClause = str.substring(c, cc + 1).trim();
            strClause = strClause.replaceAll("#", "<");
            strClause = strClause.replaceAll("@", ">");
            EBNFClause clause = new EBNFClause(strClause);
            this.rhs.add(clause);
            for (c = cc + 1; c < str.length() && (str.charAt(c) == ' ' || str.charAt(c) == '|'); ++c) {
            }
        } while (c < str.length());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.lhs);
        sb.append(" ::= ");
        for (int c = 0; c < this.rhs.size(); ++c) {
            EBNFClause clause = this.rhs.get(c);
            if (c > 0) {
                sb.append(" | ");
            }
            sb.append(clause);
        }
        return sb.toString();
    }
}

