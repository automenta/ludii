/*
 * Decompiled with CFR 0.150.
 */
package grammar;

import options.GameOptions;
import options.Ruleset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Description {
    private String raw = null;
    private String expanded = null;
    private String metadata = null;
    private final GameOptions gameOptions = new GameOptions();
    private final List<Ruleset> rulesets = new ArrayList<>();
    private final TokenForest tokenForest = new TokenForest();
    private ParseItem parseTree = null;

    public Description(String raw) {
        this.raw = raw;
    }

    public String raw() {
        return this.raw;
    }

    public void setRaw(String str) {
        this.raw = str;
    }

    public String expanded() {
        return this.expanded;
    }

    public void setExpanded(String str) {
        this.expanded = str;
    }

    public String metadata() {
        return this.metadata;
    }

    public void setMetadata(String str) {
        this.metadata = str;
    }

    public GameOptions gameOptions() {
        return this.gameOptions;
    }

    public List<Ruleset> rulesets() {
        return Collections.unmodifiableList(this.rulesets);
    }

    public TokenForest tokenForest() {
        return this.tokenForest;
    }

    public ParseItem parseTree() {
        return this.parseTree;
    }

    public void setParseTree(ParseItem tree) {
        this.parseTree = tree;
    }

    public void clearRulesets() {
        this.rulesets.clear();
    }

    public void add(Ruleset ruleset) {
        this.rulesets.add(ruleset);
    }

    public void createParseTree() {
        this.parseTree = Description.createParseTree(this.tokenForest.tokenTree(), null);
    }

    private static ParseItem createParseTree(Token token, ParseItem parent) {
        ParseItem item = new ParseItem(token, parent);
        for (Token arg : token.arguments()) {
            item.add(Description.createParseTree(arg, item));
        }
        return item;
    }

    public int autoSelectRuleset(List<String> selectedOptions) {
        List<String> allActiveOptions = this.gameOptions.allOptionStrings(selectedOptions);
        for (int i = 0; i < this.rulesets.size(); ++i) {
            boolean fullMatch = true;
            for (String requiredOpt : this.rulesets.get(i).optionSettings()) {
                if (allActiveOptions.contains(requiredOpt)) continue;
                fullMatch = false;
                break;
            }
            if (!fullMatch) continue;
            return i;
        }
        return -1;
    }
}

