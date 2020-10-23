// 
// Decompiled by Procyon v0.5.36
// 

package language.parser;

public class Define
{
    private final String tag;
    private String expression;
    private final boolean parameterised;
    
    public Define(final String tag, final String expression) {
        this.tag = tag;
        this.expression = expression;
        this.parameterised = expression.contains("#");
    }
    
    public String tag() {
        return this.tag;
    }
    
    public String expression() {
        return this.expression;
    }
    
    public void setExpression(final String expr) {
        this.expression = expr;
    }
    
    public boolean parameterised() {
        return this.parameterised;
    }
    
    @Override
    public String toString() {
        String str = "";
        str = str + "{tag:" + this.tag + ", expression:" + this.expression + ", parameterised:" + this.parameterised + "}";
        return str;
    }
}
