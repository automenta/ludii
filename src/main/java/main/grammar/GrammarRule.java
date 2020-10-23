// 
// Decompiled by Procyon v0.5.36
// 

package main.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GrammarRule
{
    private Symbol lhs;
    private final List<Clause> rhs;
    public static final int MAX_LINE_WIDTH = 80;
    public static final String IMPLIES = " ::= ";
    public static final int TAB_LHS = 10;
    public static final int TAB_RHS;
    
    public GrammarRule(final Symbol lhs) {
        this.lhs = null;
        this.rhs = new ArrayList<>();
        (this.lhs = lhs).setRule(this);
    }
    
    public Symbol lhs() {
        return this.lhs;
    }
    
    public List<Clause> rhs() {
        if (this.rhs == null) {
            return null;
        }
        return Collections.unmodifiableList(this.rhs);
    }
    
    public void add(final Clause clause) {
        this.rhs.add(clause);
    }
    
    public void remove(final int n) {
        this.rhs.remove(n);
    }
    
    public boolean containsClause(final Clause clause) {
        final String str = clause.toString();
        for (final Clause clauseR : this.rhs) {
            if (clauseR.toString().equals(str)) {
                return true;
            }
        }
        return false;
    }
    
    public void alphabetiseClauses() {
        Collections.sort(this.rhs, (a, b) -> a.symbol().keyword().compareTo(b.symbol().keyword()));
        for (int n = 0; n < this.rhs.size(); ++n) {
            final Clause clause = this.rhs.get(n);
            if (clause.args() != null) {
                this.rhs.remove(n);
                this.rhs.add(0, clause);
            }
        }
    }
    
    @Override
    public String toString() {
        String ruleStr = "";
        if (this.lhs == null) {
            return "** No LHS. **";
        }
        ruleStr += ((this.lhs.type() == Symbol.SymbolType.Constant) ? this.lhs.grammarLabel() : this.lhs.toString(true));
        final boolean isInts = ruleStr.equals("<int>{<int>}");
        if (isInts) {
            ruleStr = "<ints>";
        }
        while (ruleStr.length() < 10) {
            ruleStr += " ";
        }
        ruleStr += " ::= ";
        String rhsStr = "";
        if (isInts) {
            rhsStr = "{<int>}";
        }
        for (final Clause clause : this.rhs) {
            if (clause.isHidden()) {
                continue;
            }
            if (!rhsStr.isEmpty()) {
                rhsStr += " | ";
            }
            final String expStr = clause.toString();
            rhsStr += expStr;
        }
        ruleStr += rhsStr;
        String tab = "";
        for (int c = 0; c < GrammarRule.TAB_RHS; ++c) {
            tab += " ";
        }
        int lastBreakAt = 0;
        for (int c2 = 0; c2 < ruleStr.length(); ++c2) {
            if (c2 - lastBreakAt > 80) {
                int barAt;
                for (barAt = c2; barAt > 2 && ruleStr.charAt(barAt - 2) != '|'; --barAt) {}
                if (barAt < lastBreakAt + tab.length()) {
                    for (barAt = c2; barAt < ruleStr.length() && ruleStr.charAt(barAt) != '|'; ++barAt) {}
                }
                if (barAt > 0 && barAt < ruleStr.length()) {
                    ruleStr = ruleStr.substring(0, barAt) + "\n" + tab + ruleStr.substring(barAt);
                }
                lastBreakAt = barAt;
            }
        }
        return ruleStr;
    }
    
    static {
        TAB_RHS = 10 + " ::= ".length();
    }
}
