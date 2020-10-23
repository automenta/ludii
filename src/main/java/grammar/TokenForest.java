/*
 * Decompiled with CFR 0.150.
 */
package grammar;

import root.StringRoutines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TokenForest {
    private final List<Token> tokenTrees = new ArrayList<>();

    public List<Token> tokenTrees() {
        return Collections.unmodifiableList(this.tokenTrees);
    }

    public Token tokenTree() {
        return this.tokenTrees.isEmpty() ? null : this.tokenTrees.get(0);
    }

    public void clearTokenTrees() {
        this.tokenTrees.clear();
    }

    public void populate(String strIn, Report report) {
        int c;
        this.tokenTrees.clear();
        if (strIn == null || strIn.isEmpty()) {
            report.addError("Empty string in TokenForest.populate().");
            return;
        }
        String str = strIn.trim();
        while ((c = str.indexOf("(")) >= 0) {
            int cc = StringRoutines.matchingBracketAt(str, c);
            if (cc < 0) {
                report.addError("Couldn't close clause '" + Report.clippedString(str.substring(c), 20) + "'.");
                return;
            }
            this.tokenTrees.add(new Token(str.substring(c), report));
            str = str.substring(cc + 1).trim();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Token token : this.tokenTrees) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(token.toString());
        }
        return sb.toString();
    }
}

