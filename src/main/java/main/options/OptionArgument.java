// 
// Decompiled by Procyon v0.5.36
// 

package main.options;

public class OptionArgument
{
    private final String name;
    private final String expression;
    
    public OptionArgument(final String name, final String expression) {
        this.name = ((name == null) ? null : name);
        this.expression = expression;
    }
    
    public String name() {
        return this.name;
    }
    
    public String expression() {
        return this.expression;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (this.name != null) {
            sb.append(this.name + ":");
        }
        sb.append("<" + this.expression + ">");
        return sb.toString();
    }
}
