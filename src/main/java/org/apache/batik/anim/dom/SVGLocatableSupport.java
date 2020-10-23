// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.svg.SVGException;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.AffineTransform;
import org.apache.batik.dom.svg.AbstractSVGMatrix;
import org.w3c.dom.svg.SVGMatrix;
import org.apache.batik.dom.svg.SVGContext;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGFitToViewBox;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.Element;

public class SVGLocatableSupport
{
    public static SVGElement getNearestViewportElement(final Element e) {
        Element elt = e;
        while (elt != null) {
            elt = CSSEngine.getParentCSSStylableElement(elt);
            if (elt instanceof SVGFitToViewBox) {
                break;
            }
        }
        return (SVGElement)elt;
    }
    
    public static SVGElement getFarthestViewportElement(final Element elt) {
        final Element rootSVG = elt.getOwnerDocument().getDocumentElement();
        if (elt == rootSVG) {
            return null;
        }
        return (SVGElement)rootSVG;
    }
    
    public static SVGRect getBBox(final Element elt) {
        final SVGOMElement svgelt = (SVGOMElement)elt;
        final SVGContext svgctx = svgelt.getSVGContext();
        if (svgctx == null) {
            return null;
        }
        if (svgctx.getBBox() == null) {
            return null;
        }
        return new SVGRect() {
            @Override
            public float getX() {
                return (float)svgelt.getSVGContext().getBBox().getX();
            }
            
            @Override
            public void setX(final float x) throws DOMException {
                throw svgelt.createDOMException((short)7, "readonly.rect", null);
            }
            
            @Override
            public float getY() {
                return (float)svgelt.getSVGContext().getBBox().getY();
            }
            
            @Override
            public void setY(final float y) throws DOMException {
                throw svgelt.createDOMException((short)7, "readonly.rect", null);
            }
            
            @Override
            public float getWidth() {
                return (float)svgelt.getSVGContext().getBBox().getWidth();
            }
            
            @Override
            public void setWidth(final float width) throws DOMException {
                throw svgelt.createDOMException((short)7, "readonly.rect", null);
            }
            
            @Override
            public float getHeight() {
                return (float)svgelt.getSVGContext().getBBox().getHeight();
            }
            
            @Override
            public void setHeight(final float height) throws DOMException {
                throw svgelt.createDOMException((short)7, "readonly.rect", null);
            }
        };
    }
    
    public static SVGMatrix getCTM(final Element elt) {
        final SVGOMElement svgelt = (SVGOMElement)elt;
        return new AbstractSVGMatrix() {
            @Override
            protected AffineTransform getAffineTransform() {
                return svgelt.getSVGContext().getCTM();
            }
        };
    }
    
    public static SVGMatrix getScreenCTM(final Element elt) {
        final SVGOMElement svgelt = (SVGOMElement)elt;
        return new AbstractSVGMatrix() {
            @Override
            protected AffineTransform getAffineTransform() {
                final SVGContext context = svgelt.getSVGContext();
                final AffineTransform ret = context.getGlobalTransform();
                final AffineTransform scrnTrans = context.getScreenTransform();
                if (scrnTrans != null) {
                    ret.preConcatenate(scrnTrans);
                }
                return ret;
            }
        };
    }
    
    public static SVGMatrix getTransformToElement(final Element elt, final SVGElement element) throws SVGException {
        final SVGOMElement currentElt = (SVGOMElement)elt;
        final SVGOMElement targetElt = (SVGOMElement)element;
        return new AbstractSVGMatrix() {
            @Override
            protected AffineTransform getAffineTransform() {
                AffineTransform cat = currentElt.getSVGContext().getGlobalTransform();
                if (cat == null) {
                    cat = new AffineTransform();
                }
                AffineTransform tat = targetElt.getSVGContext().getGlobalTransform();
                if (tat == null) {
                    tat = new AffineTransform();
                }
                final AffineTransform at = new AffineTransform(cat);
                try {
                    at.preConcatenate(tat.createInverse());
                    return at;
                }
                catch (NoninvertibleTransformException ex) {
                    throw currentElt.createSVGException((short)2, "noninvertiblematrix", null);
                }
            }
        };
    }
}
