// 
// Decompiled by Procyon v0.5.36
// 

package main.grammar;

import main.StringRoutines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TokenForest
{
    private final List<Token> tokenTrees;
    
    public TokenForest() {
        this.tokenTrees = new ArrayList<>();
    }
    
    public List<Token> tokenTrees() {
        return Collections.unmodifiableList(this.tokenTrees);
    }
    
    public Token tokenTree() {
        return this.tokenTrees.isEmpty() ? null : this.tokenTrees.get(0);
    }
    
    public void clearTokenTrees() {
        this.tokenTrees.clear();
    }
    
    public void populate(final String strIn, final Report report) {
        this.tokenTrees.clear();
        if (strIn == null || strIn.isEmpty()) {
            report.addError("Empty string in TokenForest.populate().");
            return;
        }
        String str = strIn.trim();
        while (true) {
            final int c = str.indexOf("(");
            if (c < 0) {
                return;
            }
            final int cc = StringRoutines.matchingBracketAt(str, c);
            if (cc < 0) {
                report.addError("Couldn't close clause '" + Report.clippedString(str.substring(c), 20) + "'.");
                return;
            }
            this.tokenTrees.add(new Token(str.substring(c), report));
            str = str.substring(cc + 1).trim();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Token token : this.tokenTrees) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(token.toString());
        }
        return sb.toString();
    }
}
