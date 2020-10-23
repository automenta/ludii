/*
 * Decompiled with CFR 0.150.
 */
package options;

public class OptionArgument {
    private final String name;
    private final String expression;

    public OptionArgument(String name, String expression) {
        this.name = name;
        this.expression = expression;
    }

    public String name() {
        return this.name;
    }

    public String expression() {
        return this.expression;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.name != null) {
            sb.append(this.name).append(":");
        }
        sb.append("<").append(this.expression).append(">");
        return sb.toString();
    }
}

