// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.apache.batik.anim.values.AnimatableNumberOptionalNumberValue;
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.apache.batik.dom.svg.LiveAttributeValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.css.engine.value.ShorthandManager;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.dom.AbstractStylableDocument;
import org.apache.batik.dom.svg.SVGOMException;
import org.w3c.dom.svg.SVGException;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.css.engine.CSSNavigableNode;
import org.w3c.dom.Node;
import org.apache.batik.dom.util.DOMUtilities;
import org.w3c.dom.svg.SVGFitToViewBox;
import org.w3c.dom.Element;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.DOMException;
import org.w3c.dom.Attr;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.parser.UnitProcessor;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.util.DoublyIndexedTable;
import org.apache.batik.dom.svg.ExtendedTraitAccess;
import org.w3c.dom.svg.SVGElement;

public abstract class SVGOMElement extends AbstractElement implements SVGElement, ExtendedTraitAccess, AnimationTarget
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected transient boolean readonly;
    protected String prefix;
    protected transient SVGContext svgContext;
    protected DoublyIndexedTable targetListeners;
    protected UnitProcessor.Context unitContext;
    
    protected SVGOMElement() {
    }
    
    protected SVGOMElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    protected void initializeAllLiveAttributes() {
    }
    
    @Override
    public String getId() {
        if (((SVGOMDocument)this.ownerDocument).isSVG12) {
            final Attr a = this.getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "id");
            if (a != null) {
                return a.getNodeValue();
            }
        }
        return this.getAttributeNS(null, "id");
    }
    
    @Override
    public void setId(final String id) {
        if (((SVGOMDocument)this.ownerDocument).isSVG12) {
            this.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:id", id);
            final Attr a = this.getAttributeNodeNS(null, "id");
            if (a != null) {
                a.setNodeValue(id);
            }
        }
        else {
            this.setAttributeNS(null, "id", id);
        }
    }
    
    @Override
    public String getXMLbase() {
        return this.getAttributeNS("http://www.w3.org/XML/1998/namespace", "base");
    }
    
    @Override
    public void setXMLbase(final String xmlbase) throws DOMException {
        this.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:base", xmlbase);
    }
    
    @Override
    public SVGSVGElement getOwnerSVGElement() {
        for (Element e = CSSEngine.getParentCSSStylableElement(this); e != null; e = CSSEngine.getParentCSSStylableElement(e)) {
            if (e instanceof SVGSVGElement) {
                return (SVGSVGElement)e;
            }
        }
        return null;
    }
    
    @Override
    public SVGElement getViewportElement() {
        for (Element e = CSSEngine.getParentCSSStylableElement(this); e != null; e = CSSEngine.getParentCSSStylableElement(e)) {
            if (e instanceof SVGFitToViewBox) {
                return (SVGElement)e;
            }
        }
        return null;
    }
    
    @Override
    public String getNodeName() {
        if (this.prefix == null || this.prefix.equals("")) {
            return this.getLocalName();
        }
        return this.prefix + ':' + this.getLocalName();
    }
    
    @Override
    public String getNamespaceURI() {
        return "http://www.w3.org/2000/svg";
    }
    
    @Override
    public void setPrefix(final String prefix) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[] { this.getNodeType(), this.getNodeName() });
        }
        if (prefix != null && !prefix.equals("") && !DOMUtilities.isValidName(prefix)) {
            throw this.createDOMException((short)5, "prefix", new Object[] { this.getNodeType(), this.getNodeName(), prefix });
        }
        this.prefix = prefix;
    }
    
    @Override
    protected String getCascadedXMLBase(Node node) {
        String base = null;
        Node n = node.getParentNode();
        while (n != null) {
            if (n.getNodeType() == 1) {
                base = this.getCascadedXMLBase(n);
                break;
            }
            if (n instanceof CSSNavigableNode) {
                n = ((CSSNavigableNode)n).getCSSParentNode();
            }
            else {
                n = n.getParentNode();
            }
        }
        if (base == null) {
            AbstractDocument doc;
            if (node.getNodeType() == 9) {
                doc = (AbstractDocument)node;
            }
            else {
                doc = (AbstractDocument)node.getOwnerDocument();
            }
            base = doc.getDocumentURI();
        }
        while (node != null && node.getNodeType() != 1) {
            node = node.getParentNode();
        }
        if (node == null) {
            return base;
        }
        final Element e = (Element)node;
        final Attr attr = e.getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "base");
        if (attr != null) {
            if (base == null) {
                base = attr.getNodeValue();
            }
            else {
                base = new ParsedURL(base, attr.getNodeValue()).toString();
            }
        }
        return base;
    }
    
    public void setSVGContext(final SVGContext ctx) {
        this.svgContext = ctx;
    }
    
    public SVGContext getSVGContext() {
        return this.svgContext;
    }
    
    public SVGException createSVGException(final short type, final String key, final Object[] args) {
        try {
            return new SVGOMException(type, this.getCurrentDocument().formatMessage(key, args));
        }
        catch (Exception e) {
            return new SVGOMException(type, key);
        }
    }
    
    @Override
    public boolean isReadonly() {
        return this.readonly;
    }
    
    @Override
    public void setReadonly(final boolean v) {
        this.readonly = v;
    }
    
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMElement.xmlTraitInformation;
    }
    
    protected SVGOMAnimatedTransformList createLiveAnimatedTransformList(final String ns, final String ln, final String def) {
        final SVGOMAnimatedTransformList v = new SVGOMAnimatedTransformList(this, ns, ln, def);
        this.liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener(((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener());
        return v;
    }
    
    protected SVGOMAnimatedBoolean createLiveAnimatedBoolean(final String ns, final String ln, final boolean def) {
        final SVGOMAnimatedBoolean v = new SVGOMAnimatedBoolean(this, ns, ln, def);
        this.liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener(((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener());
        return v;
    }
    
    protected SVGOMAnimatedString createLiveAnimatedString(final String ns, final String ln) {
        final SVGOMAnimatedString v = new SVGOMAnimatedString(this, ns, ln);
        this.liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener(((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener());
        return v;
    }
    
    protected SVGOMAnimatedPreserveAspectRatio createLiveAnimatedPreserveAspectRatio() {
        final SVGOMAnimatedPreserveAspectRatio v = new SVGOMAnimatedPreserveAspectRatio(this);
        this.liveAttributeValues.put(null, "preserveAspectRatio", v);
        v.addAnimatedAttributeListener(((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener());
        return v;
    }
    
    protected SVGOMAnimatedMarkerOrientValue createLiveAnimatedMarkerOrientValue(final String ns, final String ln) {
        final SVGOMAnimatedMarkerOrientValue v = new SVGOMAnimatedMarkerOrientValue(this, ns, ln);
        this.liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener(((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener());
        return v;
    }
    
    protected SVGOMAnimatedPathData createLiveAnimatedPathData(final String ns, final String ln, final String def) {
        final SVGOMAnimatedPathData v = new SVGOMAnimatedPathData(this, ns, ln, def);
        this.liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener(((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener());
        return v;
    }
    
    protected SVGOMAnimatedNumber createLiveAnimatedNumber(final String ns, final String ln, final float def) {
        return this.createLiveAnimatedNumber(ns, ln, def, false);
    }
    
    protected SVGOMAnimatedNumber createLiveAnimatedNumber(final String ns, final String ln, final float def, final boolean allowPercentage) {
        final SVGOMAnimatedNumber v = new SVGOMAnimatedNumber(this, ns, ln, def, allowPercentage);
        this.liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener(((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener());
        return v;
    }
    
    protected SVGOMAnimatedNumberList createLiveAnimatedNumberList(final String ns, final String ln, final String def, final boolean canEmpty) {
        final SVGOMAnimatedNumberList v = new SVGOMAnimatedNumberList(this, ns, ln, def, canEmpty);
        this.liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener(((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener());
        return v;
    }
    
    protected SVGOMAnimatedPoints createLiveAnimatedPoints(final String ns, final String ln, final String def) {
        final SVGOMAnimatedPoints v = new SVGOMAnimatedPoints(this, ns, ln, def);
        this.liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener(((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener());
        return v;
    }
    
    protected SVGOMAnimatedLengthList createLiveAnimatedLengthList(final String ns, final String ln, final String def, final boolean emptyAllowed, final short dir) {
        final SVGOMAnimatedLengthList v = new SVGOMAnimatedLengthList(this, ns, ln, def, emptyAllowed, dir);
        this.liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener(((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener());
        return v;
    }
    
    protected SVGOMAnimatedInteger createLiveAnimatedInteger(final String ns, final String ln, final int def) {
        final SVGOMAnimatedInteger v = new SVGOMAnimatedInteger(this, ns, ln, def);
        this.liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener(((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener());
        return v;
    }
    
    protected SVGOMAnimatedEnumeration createLiveAnimatedEnumeration(final String ns, final String ln, final String[] val, final short def) {
        final SVGOMAnimatedEnumeration v = new SVGOMAnimatedEnumeration(this, ns, ln, val, def);
        this.liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener(((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener());
        return v;
    }
    
    protected SVGOMAnimatedLength createLiveAnimatedLength(final String ns, final String ln, final String val, final short dir, final boolean nonneg) {
        final SVGOMAnimatedLength v = new SVGOMAnimatedLength(this, ns, ln, val, dir, nonneg);
        this.liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener(((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener());
        return v;
    }
    
    protected SVGOMAnimatedRect createLiveAnimatedRect(final String ns, final String ln, final String value) {
        final SVGOMAnimatedRect v = new SVGOMAnimatedRect(this, ns, ln, value);
        this.liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener(((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener());
        return v;
    }
    
    @Override
    public boolean hasProperty(final String pn) {
        final AbstractStylableDocument doc = (AbstractStylableDocument)this.ownerDocument;
        final CSSEngine eng = doc.getCSSEngine();
        return eng.getPropertyIndex(pn) != -1 || eng.getShorthandIndex(pn) != -1;
    }
    
    @Override
    public boolean hasTrait(final String ns, final String ln) {
        return false;
    }
    
    @Override
    public boolean isPropertyAnimatable(final String pn) {
        final AbstractStylableDocument doc = (AbstractStylableDocument)this.ownerDocument;
        final CSSEngine eng = doc.getCSSEngine();
        int idx = eng.getPropertyIndex(pn);
        if (idx != -1) {
            final ValueManager[] vms = eng.getValueManagers();
            return vms[idx].isAnimatableProperty();
        }
        idx = eng.getShorthandIndex(pn);
        if (idx != -1) {
            final ShorthandManager[] sms = eng.getShorthandManagers();
            return sms[idx].isAnimatableProperty();
        }
        return false;
    }
    
    @Override
    public final boolean isAttributeAnimatable(final String ns, final String ln) {
        final DoublyIndexedTable t = this.getTraitInformationTable();
        final TraitInformation ti = (TraitInformation)t.get(ns, ln);
        return ti != null && ti.isAnimatable();
    }
    
    @Override
    public boolean isPropertyAdditive(final String pn) {
        final AbstractStylableDocument doc = (AbstractStylableDocument)this.ownerDocument;
        final CSSEngine eng = doc.getCSSEngine();
        int idx = eng.getPropertyIndex(pn);
        if (idx != -1) {
            final ValueManager[] vms = eng.getValueManagers();
            return vms[idx].isAdditiveProperty();
        }
        idx = eng.getShorthandIndex(pn);
        if (idx != -1) {
            final ShorthandManager[] sms = eng.getShorthandManagers();
            return sms[idx].isAdditiveProperty();
        }
        return false;
    }
    
    @Override
    public boolean isAttributeAdditive(final String ns, final String ln) {
        return true;
    }
    
    @Override
    public boolean isTraitAnimatable(final String ns, final String tn) {
        return false;
    }
    
    @Override
    public boolean isTraitAdditive(final String ns, final String tn) {
        return false;
    }
    
    @Override
    public int getPropertyType(final String pn) {
        final AbstractStylableDocument doc = (AbstractStylableDocument)this.ownerDocument;
        final CSSEngine eng = doc.getCSSEngine();
        final int idx = eng.getPropertyIndex(pn);
        if (idx != -1) {
            final ValueManager[] vms = eng.getValueManagers();
            return vms[idx].getPropertyType();
        }
        return 0;
    }
    
    @Override
    public final int getAttributeType(final String ns, final String ln) {
        final DoublyIndexedTable t = this.getTraitInformationTable();
        final TraitInformation ti = (TraitInformation)t.get(ns, ln);
        if (ti != null) {
            return ti.getType();
        }
        return 0;
    }
    
    @Override
    public Element getElement() {
        return this;
    }
    
    @Override
    public void updatePropertyValue(final String pn, final AnimatableValue val) {
    }
    
    @Override
    public void updateAttributeValue(final String ns, final String ln, final AnimatableValue val) {
        final LiveAttributeValue a = this.getLiveAttributeValue(ns, ln);
        ((AbstractSVGAnimatedValue)a).updateAnimatedValue(val);
    }
    
    @Override
    public void updateOtherValue(final String type, final AnimatableValue val) {
    }
    
    @Override
    public AnimatableValue getUnderlyingValue(final String ns, final String ln) {
        final LiveAttributeValue a = this.getLiveAttributeValue(ns, ln);
        if (!(a instanceof AnimatedLiveAttributeValue)) {
            return null;
        }
        return ((AnimatedLiveAttributeValue)a).getUnderlyingValue(this);
    }
    
    protected AnimatableValue getBaseValue(final SVGAnimatedInteger n, final SVGAnimatedInteger on) {
        return new AnimatableNumberOptionalNumberValue(this, (float)n.getBaseVal(), (float)on.getBaseVal());
    }
    
    protected AnimatableValue getBaseValue(final SVGAnimatedNumber n, final SVGAnimatedNumber on) {
        return new AnimatableNumberOptionalNumberValue(this, n.getBaseVal(), on.getBaseVal());
    }
    
    @Override
    public short getPercentageInterpretation(final String ns, final String an, final boolean isCSS) {
        if ((isCSS || ns == null) && (an.equals("baseline-shift") || an.equals("font-size"))) {
            return 0;
        }
        if (isCSS) {
            return 3;
        }
        final DoublyIndexedTable t = this.getTraitInformationTable();
        final TraitInformation ti = (TraitInformation)t.get(ns, an);
        if (ti != null) {
            return ti.getPercentageInterpretation();
        }
        return 3;
    }
    
    protected final short getAttributePercentageInterpretation(final String ns, final String ln) {
        return 3;
    }
    
    @Override
    public boolean useLinearRGBColorInterpolation() {
        return false;
    }
    
    @Override
    public float svgToUserSpace(final float v, final short type, final short pcInterp) {
        if (this.unitContext == null) {
            this.unitContext = new UnitContext();
        }
        if (pcInterp == 0 && type == 2) {
            return 0.0f;
        }
        return UnitProcessor.svgToUserSpace(v, type, (short)(3 - pcInterp), this.unitContext);
    }
    
    @Override
    public void addTargetListener(final String ns, final String an, final boolean isCSS, final AnimationTargetListener l) {
        if (!isCSS) {
            if (this.targetListeners == null) {
                this.targetListeners = new DoublyIndexedTable();
            }
            LinkedList ll = (LinkedList)this.targetListeners.get(ns, an);
            if (ll == null) {
                ll = new LinkedList();
                this.targetListeners.put(ns, an, ll);
            }
            ll.add(l);
        }
    }
    
    @Override
    public void removeTargetListener(final String ns, final String an, final boolean isCSS, final AnimationTargetListener l) {
        if (!isCSS) {
            final LinkedList ll = (LinkedList)this.targetListeners.get(ns, an);
            ll.remove(l);
        }
    }
    
    void fireBaseAttributeListeners(final String ns, final String ln) {
        if (this.targetListeners != null) {
            final LinkedList ll = (LinkedList)this.targetListeners.get(ns, ln);
            for (final Object aLl : ll) {
                final AnimationTargetListener l = (AnimationTargetListener)aLl;
                l.baseValueChanged(this, ns, ln, false);
            }
        }
    }
    
    @Override
    protected Node export(final Node n, final AbstractDocument d) {
        super.export(n, d);
        final SVGOMElement e = (SVGOMElement)n;
        e.prefix = this.prefix;
        e.initializeAllLiveAttributes();
        return n;
    }
    
    @Override
    protected Node deepExport(final Node n, final AbstractDocument d) {
        super.deepExport(n, d);
        final SVGOMElement e = (SVGOMElement)n;
        e.prefix = this.prefix;
        e.initializeAllLiveAttributes();
        return n;
    }
    
    @Override
    protected Node copyInto(final Node n) {
        super.copyInto(n);
        final SVGOMElement e = (SVGOMElement)n;
        e.prefix = this.prefix;
        e.initializeAllLiveAttributes();
        return n;
    }
    
    @Override
    protected Node deepCopyInto(final Node n) {
        super.deepCopyInto(n);
        final SVGOMElement e = (SVGOMElement)n;
        e.prefix = this.prefix;
        e.initializeAllLiveAttributes();
        return n;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable();
        t.put(null, "id", new TraitInformation(false, 16));
        t.put("http://www.w3.org/XML/1998/namespace", "base", new TraitInformation(false, 10));
        t.put("http://www.w3.org/XML/1998/namespace", "space", new TraitInformation(false, 15));
        t.put("http://www.w3.org/XML/1998/namespace", "id", new TraitInformation(false, 16));
        t.put("http://www.w3.org/XML/1998/namespace", "lang", new TraitInformation(false, 45));
        SVGOMElement.xmlTraitInformation = t;
    }
    
    protected class UnitContext implements UnitProcessor.Context
    {
        @Override
        public Element getElement() {
            return SVGOMElement.this;
        }
        
        @Override
        public float getPixelUnitToMillimeter() {
            return SVGOMElement.this.getSVGContext().getPixelUnitToMillimeter();
        }
        
        @Override
        public float getPixelToMM() {
            return this.getPixelUnitToMillimeter();
        }
        
        @Override
        public float getFontSize() {
            return SVGOMElement.this.getSVGContext().getFontSize();
        }
        
        @Override
        public float getXHeight() {
            return 0.5f;
        }
        
        @Override
        public float getViewportWidth() {
            return SVGOMElement.this.getSVGContext().getViewportWidth();
        }
        
        @Override
        public float getViewportHeight() {
            return SVGOMElement.this.getSVGContext().getViewportHeight();
        }
    }
}
