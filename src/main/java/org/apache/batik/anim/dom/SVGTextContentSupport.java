// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.apache.batik.dom.svg.SVGOMPoint;
import java.awt.geom.Point2D;
import org.w3c.dom.svg.SVGPoint;
import java.awt.geom.Rectangle2D;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGRect;
import org.apache.batik.dom.svg.SVGTextContent;
import org.w3c.dom.Element;

public class SVGTextContentSupport
{
    public static int getNumberOfChars(final Element elt) {
        final SVGOMElement svgelt = (SVGOMElement)elt;
        return ((SVGTextContent)svgelt.getSVGContext()).getNumberOfChars();
    }
    
    public static SVGRect getExtentOfChar(final Element elt, final int charnum) {
        final SVGOMElement svgelt = (SVGOMElement)elt;
        if (charnum < 0 || charnum >= getNumberOfChars(elt)) {
            throw svgelt.createDOMException((short)1, "", null);
        }
        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        return new SVGRect() {
            @Override
            public float getX() {
                return (float)SVGTextContentSupport.getExtent(svgelt, context, charnum).getX();
            }
            
            @Override
            public void setX(final float x) throws DOMException {
                throw svgelt.createDOMException((short)7, "readonly.rect", null);
            }
            
            @Override
            public float getY() {
                return (float)SVGTextContentSupport.getExtent(svgelt, context, charnum).getY();
            }
            
            @Override
            public void setY(final float y) throws DOMException {
                throw svgelt.createDOMException((short)7, "readonly.rect", null);
            }
            
            @Override
            public float getWidth() {
                return (float)SVGTextContentSupport.getExtent(svgelt, context, charnum).getWidth();
            }
            
            @Override
            public void setWidth(final float width) throws DOMException {
                throw svgelt.createDOMException((short)7, "readonly.rect", null);
            }
            
            @Override
            public float getHeight() {
                return (float)SVGTextContentSupport.getExtent(svgelt, context, charnum).getHeight();
            }
            
            @Override
            public void setHeight(final float height) throws DOMException {
                throw svgelt.createDOMException((short)7, "readonly.rect", null);
            }
        };
    }
    
    protected static Rectangle2D getExtent(final SVGOMElement svgelt, final SVGTextContent context, final int charnum) {
        final Rectangle2D r2d = context.getExtentOfChar(charnum);
        if (r2d == null) {
            throw svgelt.createDOMException((short)1, "", null);
        }
        return r2d;
    }
    
    public static SVGPoint getStartPositionOfChar(final Element elt, final int charnum) throws DOMException {
        final SVGOMElement svgelt = (SVGOMElement)elt;
        if (charnum < 0 || charnum >= getNumberOfChars(elt)) {
            throw svgelt.createDOMException((short)1, "", null);
        }
        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        return new SVGTextPoint(svgelt) {
            @Override
            public float getX() {
                return (float)SVGTextContentSupport.getStartPos(this.svgelt, context, charnum).getX();
            }
            
            @Override
            public float getY() {
                return (float)SVGTextContentSupport.getStartPos(this.svgelt, context, charnum).getY();
            }
        };
    }
    
    protected static Point2D getStartPos(final SVGOMElement svgelt, final SVGTextContent context, final int charnum) {
        final Point2D p2d = context.getStartPositionOfChar(charnum);
        if (p2d == null) {
            throw svgelt.createDOMException((short)1, "", null);
        }
        return p2d;
    }
    
    public static SVGPoint getEndPositionOfChar(final Element elt, final int charnum) throws DOMException {
        final SVGOMElement svgelt = (SVGOMElement)elt;
        if (charnum < 0 || charnum >= getNumberOfChars(elt)) {
            throw svgelt.createDOMException((short)1, "", null);
        }
        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        return new SVGTextPoint(svgelt) {
            @Override
            public float getX() {
                return (float)SVGTextContentSupport.getEndPos(this.svgelt, context, charnum).getX();
            }
            
            @Override
            public float getY() {
                return (float)SVGTextContentSupport.getEndPos(this.svgelt, context, charnum).getY();
            }
        };
    }
    
    protected static Point2D getEndPos(final SVGOMElement svgelt, final SVGTextContent context, final int charnum) {
        final Point2D p2d = context.getEndPositionOfChar(charnum);
        if (p2d == null) {
            throw svgelt.createDOMException((short)1, "", null);
        }
        return p2d;
    }
    
    public static void selectSubString(final Element elt, final int charnum, final int nchars) {
        final SVGOMElement svgelt = (SVGOMElement)elt;
        if (charnum < 0 || charnum >= getNumberOfChars(elt)) {
            throw svgelt.createDOMException((short)1, "", null);
        }
        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        context.selectSubString(charnum, nchars);
    }
    
    public static float getRotationOfChar(final Element elt, final int charnum) {
        final SVGOMElement svgelt = (SVGOMElement)elt;
        if (charnum < 0 || charnum >= getNumberOfChars(elt)) {
            throw svgelt.createDOMException((short)1, "", null);
        }
        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        return context.getRotationOfChar(charnum);
    }
    
    public static float getComputedTextLength(final Element elt) {
        final SVGOMElement svgelt = (SVGOMElement)elt;
        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        return context.getComputedTextLength();
    }
    
    public static float getSubStringLength(final Element elt, final int charnum, final int nchars) {
        final SVGOMElement svgelt = (SVGOMElement)elt;
        if (charnum < 0 || charnum >= getNumberOfChars(elt)) {
            throw svgelt.createDOMException((short)1, "", null);
        }
        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        return context.getSubStringLength(charnum, nchars);
    }
    
    public static int getCharNumAtPosition(final Element elt, final float x, final float y) throws DOMException {
        final SVGOMElement svgelt = (SVGOMElement)elt;
        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        return context.getCharNumAtPosition(x, y);
    }
    
    public static class SVGTextPoint extends SVGOMPoint
    {
        SVGOMElement svgelt;
        
        SVGTextPoint(final SVGOMElement elem) {
            this.svgelt = elem;
        }
        
        @Override
        public void setX(final float x) throws DOMException {
            throw this.svgelt.createDOMException((short)7, "readonly.point", null);
        }
        
        @Override
        public void setY(final float y) throws DOMException {
            throw this.svgelt.createDOMException((short)7, "readonly.point", null);
        }
    }
}
