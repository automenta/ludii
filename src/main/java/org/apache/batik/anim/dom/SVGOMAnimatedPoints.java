// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.apache.batik.dom.svg.SVGPointItem;
import java.util.Iterator;
import org.apache.batik.parser.ParseException;
import java.util.ArrayList;
import org.apache.batik.dom.svg.ListHandler;
import org.apache.batik.dom.svg.AbstractSVGList;
import org.apache.batik.dom.svg.ListBuilder;
import org.apache.batik.dom.svg.SVGItem;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.DOMException;
import org.apache.batik.dom.svg.AbstractSVGPointList;
import org.w3c.dom.Attr;
import org.w3c.dom.svg.SVGPoint;
import org.apache.batik.anim.values.AnimatablePointListValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.w3c.dom.Element;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.w3c.dom.svg.SVGPointList;
import org.w3c.dom.svg.SVGAnimatedPoints;

public class SVGOMAnimatedPoints extends AbstractSVGAnimatedValue implements SVGAnimatedPoints
{
    protected BaseSVGPointList baseVal;
    protected AnimSVGPointList animVal;
    protected boolean changing;
    protected String defaultValue;
    
    public SVGOMAnimatedPoints(final AbstractElement elt, final String ns, final String ln, final String defaultValue) {
        super(elt, ns, ln);
        this.defaultValue = defaultValue;
    }
    
    @Override
    public SVGPointList getPoints() {
        if (this.baseVal == null) {
            this.baseVal = new BaseSVGPointList();
        }
        return this.baseVal;
    }
    
    @Override
    public SVGPointList getAnimatedPoints() {
        if (this.animVal == null) {
            this.animVal = new AnimSVGPointList();
        }
        return this.animVal;
    }
    
    public void check() {
        if (!this.hasAnimVal) {
            if (this.baseVal == null) {
                this.baseVal = new BaseSVGPointList();
            }
            this.baseVal.revalidate();
            if (this.baseVal.missing) {
                throw new LiveAttributeException(this.element, this.localName, (short)0, null);
            }
            if (this.baseVal.malformed) {
                throw new LiveAttributeException(this.element, this.localName, (short)1, this.baseVal.getValueAsString());
            }
        }
    }
    
    @Override
    public AnimatableValue getUnderlyingValue(final AnimationTarget target) {
        final SVGPointList pl = this.getPoints();
        final int n = pl.getNumberOfItems();
        final float[] points = new float[n * 2];
        for (int i = 0; i < n; ++i) {
            final SVGPoint p = pl.getItem(i);
            points[i * 2] = p.getX();
            points[i * 2 + 1] = p.getY();
        }
        return new AnimatablePointListValue(target, points);
    }
    
    @Override
    protected void updateAnimatedValue(final AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        }
        else {
            this.hasAnimVal = true;
            final AnimatablePointListValue animPointList = (AnimatablePointListValue)val;
            if (this.animVal == null) {
                this.animVal = new AnimSVGPointList();
            }
            this.animVal.setAnimatedValue(animPointList.getNumbers());
        }
        this.fireAnimatedAttributeListeners();
    }
    
    @Override
    public void attrAdded(final Attr node, final String newv) {
        if (!this.changing && this.baseVal != null) {
            this.baseVal.invalidate();
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }
    
    @Override
    public void attrModified(final Attr node, final String oldv, final String newv) {
        if (!this.changing && this.baseVal != null) {
            this.baseVal.invalidate();
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }
    
    @Override
    public void attrRemoved(final Attr node, final String oldv) {
        if (!this.changing && this.baseVal != null) {
            this.baseVal.invalidate();
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }
    
    protected class BaseSVGPointList extends AbstractSVGPointList
    {
        protected boolean missing;
        protected boolean malformed;
        
        @Override
        protected DOMException createDOMException(final short type, final String key, final Object[] args) {
            return SVGOMAnimatedPoints.this.element.createDOMException(type, key, args);
        }
        
        @Override
        protected SVGException createSVGException(final short type, final String key, final Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedPoints.this.element).createSVGException(type, key, args);
        }
        
        @Override
        protected String getValueAsString() {
            final Attr attr = SVGOMAnimatedPoints.this.element.getAttributeNodeNS(SVGOMAnimatedPoints.this.namespaceURI, SVGOMAnimatedPoints.this.localName);
            if (attr == null) {
                return SVGOMAnimatedPoints.this.defaultValue;
            }
            return attr.getValue();
        }
        
        @Override
        protected void setAttributeValue(final String value) {
            try {
                SVGOMAnimatedPoints.this.changing = true;
                SVGOMAnimatedPoints.this.element.setAttributeNS(SVGOMAnimatedPoints.this.namespaceURI, SVGOMAnimatedPoints.this.localName, value);
            }
            finally {
                SVGOMAnimatedPoints.this.changing = false;
            }
        }
        
        @Override
        protected void resetAttribute() {
            super.resetAttribute();
            this.missing = false;
            this.malformed = false;
        }
        
        @Override
        protected void resetAttribute(final SVGItem item) {
            super.resetAttribute(item);
            this.missing = false;
            this.malformed = false;
        }
        
        @Override
        protected void revalidate() {
            if (this.valid) {
                return;
            }
            this.valid = true;
            this.missing = false;
            this.malformed = false;
            final String s = this.getValueAsString();
            if (s == null) {
                this.missing = true;
                return;
            }
            try {
                final ListBuilder builder = new ListBuilder(this);
                this.doParse(s, builder);
                if (builder.getList() != null) {
                    this.clear(this.itemList);
                }
                this.itemList = builder.getList();
            }
            catch (ParseException e) {
                this.itemList = new ArrayList(1);
                this.malformed = true;
            }
        }
    }
    
    protected class AnimSVGPointList extends AbstractSVGPointList
    {
        public AnimSVGPointList() {
            this.itemList = new ArrayList(1);
        }
        
        @Override
        protected DOMException createDOMException(final short type, final String key, final Object[] args) {
            return SVGOMAnimatedPoints.this.element.createDOMException(type, key, args);
        }
        
        @Override
        protected SVGException createSVGException(final short type, final String key, final Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedPoints.this.element).createSVGException(type, key, args);
        }
        
        @Override
        public int getNumberOfItems() {
            if (SVGOMAnimatedPoints.this.hasAnimVal) {
                return super.getNumberOfItems();
            }
            return SVGOMAnimatedPoints.this.getPoints().getNumberOfItems();
        }
        
        @Override
        public SVGPoint getItem(final int index) throws DOMException {
            if (SVGOMAnimatedPoints.this.hasAnimVal) {
                return super.getItem(index);
            }
            return SVGOMAnimatedPoints.this.getPoints().getItem(index);
        }
        
        @Override
        protected String getValueAsString() {
            if (this.itemList.size() == 0) {
                return "";
            }
            final StringBuffer sb = new StringBuffer(this.itemList.size() * 8);
            final Iterator i = this.itemList.iterator();
            if (i.hasNext()) {
                sb.append(i.next().getValueAsString());
            }
            while (i.hasNext()) {
                sb.append(this.getItemSeparator());
                sb.append(i.next().getValueAsString());
            }
            return sb.toString();
        }
        
        @Override
        protected void setAttributeValue(final String value) {
        }
        
        @Override
        public void clear() throws DOMException {
            throw SVGOMAnimatedPoints.this.element.createDOMException((short)7, "readonly.point.list", null);
        }
        
        @Override
        public SVGPoint initialize(final SVGPoint newItem) throws DOMException, SVGException {
            throw SVGOMAnimatedPoints.this.element.createDOMException((short)7, "readonly.point.list", null);
        }
        
        @Override
        public SVGPoint insertItemBefore(final SVGPoint newItem, final int index) throws DOMException, SVGException {
            throw SVGOMAnimatedPoints.this.element.createDOMException((short)7, "readonly.point.list", null);
        }
        
        @Override
        public SVGPoint replaceItem(final SVGPoint newItem, final int index) throws DOMException, SVGException {
            throw SVGOMAnimatedPoints.this.element.createDOMException((short)7, "readonly.point.list", null);
        }
        
        @Override
        public SVGPoint removeItem(final int index) throws DOMException {
            throw SVGOMAnimatedPoints.this.element.createDOMException((short)7, "readonly.point.list", null);
        }
        
        @Override
        public SVGPoint appendItem(final SVGPoint newItem) throws DOMException {
            throw SVGOMAnimatedPoints.this.element.createDOMException((short)7, "readonly.point.list", null);
        }
        
        protected void setAnimatedValue(final float[] pts) {
            int size;
            int i;
            for (size = this.itemList.size(), i = 0; i < size && i < pts.length / 2; ++i) {
                final SVGPointItem p = this.itemList.get(i);
                p.setX(pts[i * 2]);
                p.setY(pts[i * 2 + 1]);
            }
            while (i < pts.length / 2) {
                this.appendItemImpl(new SVGPointItem(pts[i * 2], pts[i * 2 + 1]));
                ++i;
            }
            while (size > pts.length / 2) {
                this.removeItemImpl(--size);
            }
        }
        
        @Override
        protected void resetAttribute() {
        }
        
        @Override
        protected void resetAttribute(final SVGItem item) {
        }
        
        @Override
        protected void revalidate() {
            this.valid = true;
        }
    }
}
