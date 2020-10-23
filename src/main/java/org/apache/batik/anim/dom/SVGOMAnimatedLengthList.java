// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import java.util.Iterator;
import org.apache.batik.parser.ParseException;
import org.apache.batik.dom.svg.ListHandler;
import org.apache.batik.dom.svg.AbstractSVGList;
import org.apache.batik.dom.svg.ListBuilder;
import java.util.ArrayList;
import org.apache.batik.dom.svg.SVGItem;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Attr;
import org.w3c.dom.svg.SVGLength;
import org.apache.batik.anim.values.AnimatableLengthListValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.w3c.dom.Element;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.w3c.dom.svg.SVGLengthList;
import org.w3c.dom.svg.SVGAnimatedLengthList;

public class SVGOMAnimatedLengthList extends AbstractSVGAnimatedValue implements SVGAnimatedLengthList
{
    protected BaseSVGLengthList baseVal;
    protected AnimSVGLengthList animVal;
    protected boolean changing;
    protected String defaultValue;
    protected boolean emptyAllowed;
    protected short direction;
    
    public SVGOMAnimatedLengthList(final AbstractElement elt, final String ns, final String ln, final String defaultValue, final boolean emptyAllowed, final short direction) {
        super(elt, ns, ln);
        this.defaultValue = defaultValue;
        this.emptyAllowed = emptyAllowed;
        this.direction = direction;
    }
    
    @Override
    public SVGLengthList getBaseVal() {
        if (this.baseVal == null) {
            this.baseVal = new BaseSVGLengthList();
        }
        return this.baseVal;
    }
    
    @Override
    public SVGLengthList getAnimVal() {
        if (this.animVal == null) {
            this.animVal = new AnimSVGLengthList();
        }
        return this.animVal;
    }
    
    public void check() {
        if (!this.hasAnimVal) {
            if (this.baseVal == null) {
                this.baseVal = new BaseSVGLengthList();
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
        final SVGLengthList ll = this.getBaseVal();
        final int n = ll.getNumberOfItems();
        final short[] types = new short[n];
        final float[] values = new float[n];
        for (int i = 0; i < n; ++i) {
            final SVGLength l = ll.getItem(i);
            types[i] = l.getUnitType();
            values[i] = l.getValueInSpecifiedUnits();
        }
        return new AnimatableLengthListValue(target, types, values, target.getPercentageInterpretation(this.getNamespaceURI(), this.getLocalName(), false));
    }
    
    @Override
    protected void updateAnimatedValue(final AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        }
        else {
            this.hasAnimVal = true;
            final AnimatableLengthListValue animLengths = (AnimatableLengthListValue)val;
            if (this.animVal == null) {
                this.animVal = new AnimSVGLengthList();
            }
            this.animVal.setAnimatedValue(animLengths.getLengthTypes(), animLengths.getLengthValues());
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
    
    public class BaseSVGLengthList extends AbstractSVGLengthList
    {
        protected boolean missing;
        protected boolean malformed;
        
        public BaseSVGLengthList() {
            super(SVGOMAnimatedLengthList.this.direction);
        }
        
        @Override
        protected DOMException createDOMException(final short type, final String key, final Object[] args) {
            return SVGOMAnimatedLengthList.this.element.createDOMException(type, key, args);
        }
        
        @Override
        protected SVGException createSVGException(final short type, final String key, final Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedLengthList.this.element).createSVGException(type, key, args);
        }
        
        @Override
        protected Element getElement() {
            return SVGOMAnimatedLengthList.this.element;
        }
        
        @Override
        protected String getValueAsString() {
            final Attr attr = SVGOMAnimatedLengthList.this.element.getAttributeNodeNS(SVGOMAnimatedLengthList.this.namespaceURI, SVGOMAnimatedLengthList.this.localName);
            if (attr == null) {
                return SVGOMAnimatedLengthList.this.defaultValue;
            }
            return attr.getValue();
        }
        
        @Override
        protected void setAttributeValue(final String value) {
            try {
                SVGOMAnimatedLengthList.this.changing = true;
                SVGOMAnimatedLengthList.this.element.setAttributeNS(SVGOMAnimatedLengthList.this.namespaceURI, SVGOMAnimatedLengthList.this.localName, value);
            }
            finally {
                SVGOMAnimatedLengthList.this.changing = false;
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
            final boolean isEmpty = s != null && s.length() == 0;
            if (s == null || (isEmpty && !SVGOMAnimatedLengthList.this.emptyAllowed)) {
                this.missing = true;
                return;
            }
            if (isEmpty) {
                this.itemList = new ArrayList(1);
            }
            else {
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
                    this.valid = true;
                    this.malformed = true;
                }
            }
        }
    }
    
    protected class AnimSVGLengthList extends AbstractSVGLengthList
    {
        public AnimSVGLengthList() {
            super(SVGOMAnimatedLengthList.this.direction);
            this.itemList = new ArrayList(1);
        }
        
        @Override
        protected DOMException createDOMException(final short type, final String key, final Object[] args) {
            return SVGOMAnimatedLengthList.this.element.createDOMException(type, key, args);
        }
        
        @Override
        protected SVGException createSVGException(final short type, final String key, final Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedLengthList.this.element).createSVGException(type, key, args);
        }
        
        @Override
        protected Element getElement() {
            return SVGOMAnimatedLengthList.this.element;
        }
        
        @Override
        public int getNumberOfItems() {
            if (SVGOMAnimatedLengthList.this.hasAnimVal) {
                return super.getNumberOfItems();
            }
            return SVGOMAnimatedLengthList.this.getBaseVal().getNumberOfItems();
        }
        
        @Override
        public SVGLength getItem(final int index) throws DOMException {
            if (SVGOMAnimatedLengthList.this.hasAnimVal) {
                return super.getItem(index);
            }
            return SVGOMAnimatedLengthList.this.getBaseVal().getItem(index);
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
            throw SVGOMAnimatedLengthList.this.element.createDOMException((short)7, "readonly.length.list", null);
        }
        
        @Override
        public SVGLength initialize(final SVGLength newItem) throws DOMException, SVGException {
            throw SVGOMAnimatedLengthList.this.element.createDOMException((short)7, "readonly.length.list", null);
        }
        
        @Override
        public SVGLength insertItemBefore(final SVGLength newItem, final int index) throws DOMException, SVGException {
            throw SVGOMAnimatedLengthList.this.element.createDOMException((short)7, "readonly.length.list", null);
        }
        
        @Override
        public SVGLength replaceItem(final SVGLength newItem, final int index) throws DOMException, SVGException {
            throw SVGOMAnimatedLengthList.this.element.createDOMException((short)7, "readonly.length.list", null);
        }
        
        @Override
        public SVGLength removeItem(final int index) throws DOMException {
            throw SVGOMAnimatedLengthList.this.element.createDOMException((short)7, "readonly.length.list", null);
        }
        
        @Override
        public SVGLength appendItem(final SVGLength newItem) throws DOMException {
            throw SVGOMAnimatedLengthList.this.element.createDOMException((short)7, "readonly.length.list", null);
        }
        
        protected void setAnimatedValue(final short[] types, final float[] values) {
            int size;
            int i;
            for (size = this.itemList.size(), i = 0; i < size && i < types.length; ++i) {
                final SVGLengthItem l = this.itemList.get(i);
                l.unitType = types[i];
                l.value = values[i];
                l.direction = this.direction;
            }
            while (i < types.length) {
                this.appendItemImpl(new SVGLengthItem(types[i], values[i], this.direction));
                ++i;
            }
            while (size > types.length) {
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
