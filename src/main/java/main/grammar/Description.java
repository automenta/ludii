// 
// Decompiled by Procyon v0.5.36
// 

package main.grammar;

import main.options.GameOptions;
import main.options.Ruleset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Description
{
    private String raw;
    private String expanded;
    private String metadata;
    private final GameOptions gameOptions;
    private final List<Ruleset> rulesets;
    private final TokenForest tokenForest;
    private ParseItem parseTree;
    
    public Description(final String raw) {
        this.raw = null;
        this.expanded = null;
        this.metadata = null;
        this.gameOptions = new GameOptions();
        this.rulesets = new ArrayList<>();
        this.tokenForest = new TokenForest();
        this.parseTree = null;
        this.raw = raw;
    }
    
    public String raw() {
        return this.raw;
    }
    
    public void setRaw(final String str) {
        this.raw = str;
    }
    
    public String expanded() {
        return this.expanded;
    }
    
    public void setExpanded(final String str) {
        this.expanded = str;
    }
    
    public String metadata() {
        return this.metadata;
    }
    
    public void setMetadata(final String str) {
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
    
    public void setParseTree(final ParseItem tree) {
        this.parseTree = tree;
    }
    
    public void clearRulesets() {
        this.rulesets.clear();
    }
    
    public void add(final Ruleset ruleset) {
        this.rulesets.add(ruleset);
    }
    
    public void createParseTree() {
        this.parseTree = createParseTree(this.tokenForest.tokenTree(), null);
    }
    
    private static ParseItem createParseTree(final Token token, final ParseItem parent) {
        final ParseItem item = new ParseItem(token, parent);
        for (final Token arg : token.arguments()) {
            item.add(createParseTree(arg, item));
        }
        return item;
    }
    
    public int autoSelectRuleset(final List<String> selectedOptions) {
        final List<String> allActiveOptions = this.gameOptions.allOptionStrings(selectedOptions);
        for (int i = 0; i < this.rulesets.size(); ++i) {
            boolean fullMatch = true;
            for (final String requiredOpt : this.rulesets.get(i).optionSettings()) {
                if (!allActiveOptions.contains(requiredOpt)) {
                    fullMatch = false;
                    break;
                }
            }
            if (fullMatch) {
                return i;
            }
        }
        return -1;
    }
}
