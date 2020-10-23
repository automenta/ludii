/*
 * Decompiled with CFR 0.150.
 */
package grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GrammarRule {
    private Symbol lhs = null;
    private final List<Clause> rhs = new ArrayList<>();
    public static final int MAX_LINE_WIDTH = 80;
    public static final String IMPLIES = " ::= ";
    public static final int TAB_LHS = 10;
    public static final int TAB_RHS = 10 + " ::= ".length();

    public GrammarRule(Symbol lhs) {
        this.lhs = lhs;
        lhs.setRule(this);
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

    public void add(Clause clause) {
        this.rhs.add(clause);
    }

    public void remove(int n) {
        this.rhs.remove(n);
    }

    public boolean containsClause(Clause clause) {
        String str = clause.toString();
        for (Clause clauseR : this.rhs) {
            if (!clauseR.toString().equals(str)) continue;
            return true;
        }
        return false;
    }

    public void alphabetiseClauses() {
        this.rhs.sort(Comparator.comparing(a -> a.symbol().keyword()));
        for (int n = 0; n < this.rhs.size(); ++n) {
            Clause clause = this.rhs.get(n);
            if (clause.args() == null) continue;
            this.rhs.remove(n);
            this.rhs.add(0, clause);
        }
    }

    public String toString() {
        String ruleStr = "";
        if (this.lhs == null) {
            return "** No LHS. **";
        }
        boolean isInts = (ruleStr = ruleStr + (this.lhs.type() == Symbol.SymbolType.Constant ? this.lhs.grammarLabel() : this.lhs.toString(true))).equals("<int>{<int>}");
        if (isInts) {
            ruleStr = "<ints>";
        }
        while (ruleStr.length() < 10) {
            ruleStr = ruleStr + " ";
        }
        ruleStr = ruleStr + IMPLIES;
        String rhsStr = "";
        if (isInts) {
            rhsStr = "{<int>}";
        }
        for (Clause clause : this.rhs) {
            if (clause.isHidden()) continue;
            if (!rhsStr.isEmpty()) {
                rhsStr = rhsStr + " | ";
            }
            String expStr = clause.toString();
            rhsStr = rhsStr + expStr;
        }
        ruleStr = ruleStr + rhsStr;
        String tab = "";
        for (int c = 0; c < TAB_RHS; ++c) {
            tab = tab + " ";
        }
        int lastBreakAt = 0;
        for (int c = 0; c < ruleStr.length(); ++c) {
            int barAt;
            if (c - lastBreakAt <= 80) continue;
            for (barAt = c; barAt > 2 && ruleStr.charAt(barAt - 2) != '|'; --barAt) {
            }
            if (barAt < lastBreakAt + tab.length()) {
                for (barAt = c; barAt < ruleStr.length() && ruleStr.charAt(barAt) != '|'; ++barAt) {
                }
            }
            if (barAt > 0 && barAt < ruleStr.length()) {
                ruleStr = ruleStr.substring(0, barAt) + "\n" + tab + ruleStr.substring(barAt);
            }
            lastBreakAt = barAt;
        }
        return ruleStr;
    }
}

