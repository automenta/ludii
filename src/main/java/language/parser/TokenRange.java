// 
// Decompiled by Procyon v0.5.36
// 

package language.parser;

public class TokenRange
{
    final int from;
    final int to;
    
    public TokenRange(final int from, final int to) {
        this.from = from;
        this.to = to;
    }
    
    public int from() {
        return this.from;
    }
    
    public int to() {
        return this.to;
    }
}
