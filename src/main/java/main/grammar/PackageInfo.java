// 
// Decompiled by Procyon v0.5.36
// 

package main.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PackageInfo
{
    protected String path;
    protected List<GrammarRule> rules;
    
    public PackageInfo(final String path) {
        this.path = "";
        this.rules = new ArrayList<>();
        this.path = path;
    }
    
    public String path() {
        return this.path;
    }
    
    public String shortName() {
        final String[] subs = this.path.split("\\.");
        if (subs.length == 0) {
            return this.path;
        }
        return subs[subs.length - 1];
    }
    
    public List<GrammarRule> rules() {
        return Collections.unmodifiableList(this.rules);
    }
    
    public void add(final GrammarRule rule) {
        this.rules.add(rule);
    }
    
    public void add(final int n, final GrammarRule rule) {
        this.rules.add(n, rule);
    }
    
    public void remove(final int n) {
        this.rules.remove(n);
    }
    
    public void listAlphabetically() {
        Collections.sort(this.rules, (a, b) -> a.lhs().grammarLabel().compareTo(b.lhs().grammarLabel()));
    }
    
    @Override
    public String toString() {
        String str;
        for (str = "", str += "//"; str.length() < 80; str += "-") {}
        str += "\n";
        str = str + "// " + this.path + "\n\n";
        int numUsed = 0;
        for (final GrammarRule rule : this.rules) {
            if (!rule.lhs().usedInGrammar() && !rule.lhs().usedInMetadata()) {
                continue;
            }
            if (rule.rhs() == null) {
                continue;
            }
            if (rule.rhs().isEmpty()) {
                continue;
            }
            str = str + rule.toString() + "\n";
            ++numUsed;
        }
        str += "\n";
        if (numUsed == 0) {
            return "";
        }
        return str;
    }
}
