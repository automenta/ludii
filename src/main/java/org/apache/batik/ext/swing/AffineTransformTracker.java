// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.swing;

import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.awt.event.ActionListener;

class AffineTransformTracker implements ActionListener, Serializable
{
    JAffineTransformChooser chooser;
    AffineTransform txf;
    
    public AffineTransformTracker(final JAffineTransformChooser c) {
        this.chooser = c;
    }
    
    @Override
    public void actionPerformed(final ActionEvent e) {
        this.txf = this.chooser.getAffineTransform();
    }
    
    public AffineTransform getAffineTransform() {
        return this.txf;
    }
    
    public void reset() {
        this.txf = null;
    }
}
