/*
 * Decompiled with CFR 0.150.
 */
package grammar.ebnf;

public class EBNFClauseArg
extends EBNFClause {
    private boolean isOptional = false;
    private int orGroup = 0;
    private String parameterName = null;
    private int nesting = 0;

    public EBNFClauseArg(String input, boolean isOptional, int orGroup) {
        this.isOptional = isOptional;
        this.orGroup = orGroup;
        this.decompose(input);
    }

    public boolean isOptional() {
        return this.isOptional;
    }

    public int orGroup() {
        return this.orGroup;
    }

    public String parameterName() {
        return this.parameterName;
    }

    public int nesting() {
        return this.nesting;
    }

    @Override
    void decompose(String input) {
        String str = input.trim();
        int colonAt = str.indexOf(':');
        if (colonAt >= 0) {
            this.parameterName = str.substring(0, colonAt).trim();
            str = str.substring(colonAt + 1).trim();
        }
        while (str.charAt(0) == '{') {
            if (str.charAt(str.length() - 1) != '}') {
                System.out.println("** No closing brace for array in: " + str);
                return;
            }
            ++this.nesting;
            str = str.substring(1, str.length() - 1).trim();
        }
        this.token = str.trim();
    }

    @Override
    public String toString() {
        int n;
        StringBuilder sb = new StringBuilder();
        if (this.parameterName != null) {
            sb.append(this.parameterName + ":");
        }
        for (n = 0; n < this.nesting; ++n) {
            sb.append("{");
        }
        sb.append(this.token);
        for (n = 0; n < this.nesting; ++n) {
            sb.append("}");
        }
        return sb.toString();
    }
}

