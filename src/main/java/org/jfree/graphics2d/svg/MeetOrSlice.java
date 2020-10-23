// 
// Decompiled by Procyon v0.5.36
// 

package org.jfree.graphics2d.svg;

public enum MeetOrSlice
{
    MEET("meet"), 
    SLICE("slice");
    
    private final String label;
    
    MeetOrSlice(final String label) {
        this.label = label;
    }
    
    @Override
    public String toString() {
        return this.label;
    }
}
