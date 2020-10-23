// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGMatrix;
import java.awt.geom.AffineTransform;

public class SVGOMTransform extends AbstractSVGTransform
{
    public SVGOMTransform() {
        this.affineTransform = new AffineTransform();
    }
    
    @Override
    protected SVGMatrix createMatrix() {
        return new AbstractSVGMatrix() {
            @Override
            protected AffineTransform getAffineTransform() {
                return SVGOMTransform.this.affineTransform;
            }
            
            @Override
            public void setA(final float a) throws DOMException {
                SVGOMTransform.this.setType((short)1);
                super.setA(a);
            }
            
            @Override
            public void setB(final float b) throws DOMException {
                SVGOMTransform.this.setType((short)1);
                super.setB(b);
            }
            
            @Override
            public void setC(final float c) throws DOMException {
                SVGOMTransform.this.setType((short)1);
                super.setC(c);
            }
            
            @Override
            public void setD(final float d) throws DOMException {
                SVGOMTransform.this.setType((short)1);
                super.setD(d);
            }
            
            @Override
            public void setE(final float e) throws DOMException {
                SVGOMTransform.this.setType((short)1);
                super.setE(e);
            }
            
            @Override
            public void setF(final float f) throws DOMException {
                SVGOMTransform.this.setType((short)1);
                super.setF(f);
            }
        };
    }
}
