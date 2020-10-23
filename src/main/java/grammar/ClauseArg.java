/*
 * Decompiled with CFR 0.150.
 */
package grammar;

public class ClauseArg {
    private String label = null;
    private Symbol symbol = null;
    private final boolean optional;
    private final int orGroup;
    private final int andGroup;
    private int nesting = 0;

    public ClauseArg(Symbol symbol, String label, boolean optional, int orGroup, int andGroup) {
        this.symbol = symbol;
        this.label = label == null ? null : label;
        this.optional = optional;
        this.orGroup = orGroup;
        this.andGroup = andGroup;
    }

    public ClauseArg(ClauseArg other) {
        this.label = other.label == null ? null : other.label;
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

    public void setSymbol(Symbol val) {
        this.symbol = val;
    }

    public int nesting() {
        return this.nesting;
    }

    public void setNesting(int val) {
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
                str = str + "[UNKNOWN]";
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

