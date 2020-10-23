// 
// Decompiled by Procyon v0.5.36
// 

package main.grammar;

public class ClauseArg
{
    private String label;
    private Symbol symbol;
    private final boolean optional;
    private final int orGroup;
    private final int andGroup;
    private int nesting;
    
    public ClauseArg(final Symbol symbol, final String label, final boolean optional, final int orGroup, final int andGroup) {
        this.label = null;
        this.symbol = null;
        this.nesting = 0;
        this.symbol = symbol;
        this.label = ((label == null) ? null : label);
        this.optional = optional;
        this.orGroup = orGroup;
        this.andGroup = andGroup;
    }
    
    public ClauseArg(final ClauseArg other) {
        this.label = null;
        this.symbol = null;
        this.nesting = 0;
        this.label = ((other.label == null) ? null : other.label);
        this.symbol = other.symbol;
        this.optional = other.optional;
        this.orGroup = other.orGroup;
        this.andGroup = other.andGroup;
        this.nesting = other.nesting;
    }
    
    public String label() {
        return this.label;
    }
    
    public Symbol symbol() {
        return this.symbol;
    }
    
    public void setSymbol(final Symbol val) {
        this.symbol = val;
    }
    
    public int nesting() {
        return this.nesting;
    }
    
    public void setNesting(final int val) {
        this.nesting = val;
    }
    
    public boolean optional() {
        return this.optional;
    }
    
    public int orGroup() {
        return this.orGroup;
    }
    
    public int andGroup() {
        return this.andGroup;
    }
    
    @Override
    public String toString() {
        String str = "";
        if (this.symbol == null) {
            return "NULL";
        }
        switch (this.symbol.type()) {
            case Primitive: {
                str = this.symbol.keyword();
                break;
            }
            case Constant: {
                str = this.symbol.keyword();
                break;
            }
            case Predefined:
            case Class: {
                str = "<" + this.symbol.grammarLabel() + ">";
                break;
            }
            default: {
                str += "[UNKNOWN]";
                break;
            }
        }
        for (int n = 0; n < this.nesting; ++n) {
            str = "{" + str + "}";
        }
        if (this.label != null) {
            String labelSafe = this.label;
            if (Character.isUpperCase(labelSafe.charAt(0))) {
                labelSafe = Character.toLowerCase(labelSafe.charAt(0)) + labelSafe.substring(1);
            }
            str = labelSafe + ":" + str;
        }
        if (this.optional) {
            str = "[" + str + "]";
        }
        return str;
    }
}
