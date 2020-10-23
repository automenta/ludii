/*
 * Decompiled with CFR 0.150.
 */
package grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PackageInfo {
    protected String path = "";
    protected List<GrammarRule> rules = new ArrayList<>();

    public PackageInfo(String path) {
        this.path = path;
    }

    public String path() {
        return this.path;
    }

    public String shortName() {
        String[] subs = this.path.split("\\.");
        if (subs.length == 0) {
            return this.path;
        }
        return subs[subs.length - 1];
    }

    public List<GrammarRule> rules() {
        return Collections.unmodifiableList(this.rules);
    }

    public void add(GrammarRule rule) {
        this.rules.add(rule);
    }

    public void add(int n, GrammarRule rule) {
        this.rules.add(n, rule);
    }

    public void remove(int n) {
        this.rules.remove(n);
    }

    public void listAlphabetically() {
        this.rules.sort(Comparator.comparing(a -> a.lhs().grammarLabel()));
    }

    public String toString() {
        String str = "";
        str = str + "//";
        while (str.length() < 80) {
            str = str + "-";
        }
        str = str + "\n";
        str = str + "// " + this.path + "\n\n";
        int numUsed = 0;
        for (GrammarRule rule : this.rules) {
            if (!rule.lhs().usedInGrammar() && !rule.lhs().usedInMetadata() || rule.rhs() == null || rule.rhs().isEmpty()) continue;
            str = str + rule.toString() + "\n";
            ++numUsed;
        }
        str = str + "\n";
        if (numUsed == 0) {
            return "";
        }
        return str;
    }
}

