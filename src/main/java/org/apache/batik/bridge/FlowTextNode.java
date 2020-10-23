// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

public class FlowTextNode extends TextNode
{
    public FlowTextNode() {
        this.textPainter = FlowTextPainter.getInstance();
    }
    
    @Override
    public void setTextPainter(final TextPainter textPainter) {
        if (textPainter == null) {
            this.textPainter = FlowTextPainter.getInstance();
        }
        else {
            this.textPainter = textPainter;
        }
    }
}
