// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import java.awt.geom.AffineTransform;

public class SVGOMMatrix extends AbstractSVGMatrix
{
    protected AffineTransform affineTransform;
    
    public SVGOMMatrix(final AffineTransform at) {
        this.affineTransform = at;
    }
    
    @Override
    protected AffineTransform getAffineTransform() {
        return this.affineTransform;
    }
}
