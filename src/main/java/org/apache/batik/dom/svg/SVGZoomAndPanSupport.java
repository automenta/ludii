// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.w3c.dom.DOMException;
import org.apache.batik.dom.AbstractNode;
import org.w3c.dom.Element;
import org.apache.batik.util.SVGConstants;

public class SVGZoomAndPanSupport implements SVGConstants
{
    protected SVGZoomAndPanSupport() {
    }
    
    public static void setZoomAndPan(final Element elt, final short val) throws DOMException {
        switch (val) {
            case 1: {
                elt.setAttributeNS(null, "zoomAndPan", "disable");
                break;
            }
            case 2: {
                elt.setAttributeNS(null, "zoomAndPan", "magnify");
                break;
            }
            default: {
                throw ((AbstractNode)elt).createDOMException((short)13, "zoom.and.pan", new Object[] { val });
            }
        }
    }
    
    public static short getZoomAndPan(final Element elt) {
        final String s = elt.getAttributeNS(null, "zoomAndPan");
        if (s.equals("magnify")) {
            return 2;
        }
        return 1;
    }
}
