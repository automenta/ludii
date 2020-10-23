// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.apache.batik.anim.values.AnimatableMotionPointValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGElement;
import org.apache.batik.dom.AbstractDocument;
import java.awt.geom.AffineTransform;
import org.apache.batik.util.DoublyIndexedTable;
import org.apache.batik.dom.svg.SVGMotionAnimatableElement;
import org.w3c.dom.svg.SVGTextElement;

public class SVGOMTextElement extends SVGOMTextPositioningElement implements SVGTextElement, SVGMotionAnimatableElement
{
    protected static final String X_DEFAULT_VALUE = "0";
    protected static final String Y_DEFAULT_VALUE = "0";
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedTransformList transform;
    protected AffineTransform motionTransform;
    
    protected SVGOMTextElement() {
    }
    
    public SVGOMTextElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.transform = this.createLiveAnimatedTransformList(null, "transform", "");
    }
    
    @Override
    public String getLocalName() {
        return "text";
    }
    
    @Override
    public SVGElement getNearestViewportElement() {
        return SVGLocatableSupport.getNearestViewportElement(this);
    }
    
    @Override
    public SVGElement getFarthestViewportElement() {
        return SVGLocatableSupport.getFarthestViewportElement(this);
    }
    
    @Override
    public SVGRect getBBox() {
        return SVGLocatableSupport.getBBox(this);
    }
    
    @Override
    public SVGMatrix getCTM() {
        return SVGLocatableSupport.getCTM(this);
    }
    
    @Override
    public SVGMatrix getScreenCTM() {
        return SVGLocatableSupport.getScreenCTM(this);
    }
    
    @Override
    public SVGMatrix getTransformToElement(final SVGElement element) throws SVGException {
        return SVGLocatableSupport.getTransformToElement(this, element);
    }
    
    @Override
    public SVGAnimatedTransformList getTransform() {
        return this.transform;
    }
    
    @Override
    protected String getDefaultXValue() {
        return "0";
    }
    
    @Override
    protected String getDefaultYValue() {
        return "0";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMTextElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMTextElement.xmlTraitInformation;
    }
    
    @Override
    public AffineTransform getMotionTransform() {
        return this.motionTransform;
    }
    
    @Override
    public void updateOtherValue(final String type, final AnimatableValue val) {
        if (type.equals("motion")) {
            if (this.motionTransform == null) {
                this.motionTransform = new AffineTransform();
            }
            if (val == null) {
                this.motionTransform.setToIdentity();
            }
            else {
                final AnimatableMotionPointValue p = (AnimatableMotionPointValue)val;
                this.motionTransform.setToTranslation(p.getX(), p.getY());
                this.motionTransform.rotate(p.getAngle());
            }
            final SVGOMDocument d = (SVGOMDocument)this.ownerDocument;
            d.getAnimatedAttributeListener().otherAnimationChanged(this, type);
        }
        else {
            super.updateOtherValue(type, val);
        }
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMTextPositioningElement.xmlTraitInformation);
        t.put(null, "transform", new TraitInformation(true, 9));
        SVGOMTextElement.xmlTraitInformation = t;
    }
}
