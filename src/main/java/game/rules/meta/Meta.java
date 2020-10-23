// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.meta;

import annotations.Or;
import util.BaseLudeme;
import util.Context;

import java.io.Serializable;

public class Meta extends BaseLudeme implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final MetaRule[] rules;
    
    public Meta(@Or final MetaRule[] rules, @Or final MetaRule rule) {
        int numNonNull = 0;
        if (rules != null) {
            ++numNonNull;
        }
        if (rule != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Exactly one Or parameter must be non-null.");
        }
        if (rules != null) {
            this.rules = rules;
        }
        else {
            (this.rules = new MetaRule[1])[0] = rule;
        }
    }
    
    public MetaRule[] rules() {
        return this.rules;
    }
    
    public void eval(final Context context) {
        for (final MetaRule rule : this.rules) {
            rule.eval(context);
        }
    }
}
