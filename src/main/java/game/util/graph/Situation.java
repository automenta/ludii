// 
// Decompiled by Procyon v0.5.36
// 

package game.util.graph;

import math.RCL;

public class Situation
{
    private final RCL rcl;
    String label;
    
    public Situation() {
        this.rcl = new RCL();
        this.label = "";
    }
    
    public RCL rcl() {
        return this.rcl;
    }
    
    public String label() {
        return this.label;
    }
    
    public void setLabel(final String str) {
        this.label = str;
    }
}
