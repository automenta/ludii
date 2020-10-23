// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

public class TextHit
{
    private int charIndex;
    private boolean leadingEdge;
    
    public TextHit(final int charIndex, final boolean leadingEdge) {
        this.charIndex = charIndex;
        this.leadingEdge = leadingEdge;
    }
    
    public int getCharIndex() {
        return this.charIndex;
    }
    
    public boolean isLeadingEdge() {
        return this.leadingEdge;
    }
}
