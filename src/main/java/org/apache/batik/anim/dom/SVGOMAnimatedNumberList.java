// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.apache.batik.dom.svg.SVGNumberItem;
import java.util.Iterator;
import org.w3c.dom.svg.SVGNumber;
import org.apache.batik.parser.ParseException;
import org.apache.batik.dom.svg.ListHandler;
import org.apache.batik.dom.svg.AbstractSVGList;
import org.apache.batik.dom.svg.ListBuilder;
import java.util.ArrayList;
import org.apache.batik.dom.svg.SVGItem;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.DOMException;
import org.apache.batik.dom.svg.AbstractSVGNumberList;
import org.w3c.dom.Attr;
import org.apache.batik.anim.values.AnimatableNumberListValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.w3c.dom.Element;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.w3c.dom.svg.SVGNumberList;
import org.w3c.dom.svg.SVGAnimatedNumberList;

public class SVGOMAnimatedNumberList extends AbstractSVGAnimatedValue implements SVGAnimatedNumberList
{
    protected BaseSVGNumberList baseVal;
    protected AnimSVGNumberList animVal;
    protected boolean changing;
    protected String defaultValue;
    protected boolean emptyAllowed;
    
    public SVGOMAnimatedNumberList(final AbstractElement elt, final String ns, final String ln, final String defaultValue, final boolean emptyAllowed) {
        super(elt, ns, ln);
        this.defaultValue = defaultValue;
        this.emptyAllowed = emptyAllowed;
    }
    
    @Override
    public SVGNumberList getBaseVal() {
        if (this.baseVal == null) {
            this.baseVal = new BaseSVGNumberList();
        }
        return this.baseVal;
    }
    
    @Override
    public SVGNumberList getAnimVal() {
        if (this.animVal == null) {
            this.animVal = new AnimSVGNumberList();
        }
        return this.animVal;
    }
    
    public void check() {
        if (!this.hasAnimVal) {
            if (this.baseVal == null) {
                this.baseVal = new BaseSVGNumberList();
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
        final SVGNumberList nl = this.getBaseVal();
        final int n = nl.getNumberOfItems();
        final float[] numbers = new float[n];
        for (int i = 0; i < n; ++i) {
            numbers[i] = nl.getItem(n).getValue();
        }
        return new AnimatableNumberListValue(target, numbers);
    }
    
    @Override
    protected void updateAnimatedValue(final AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        }
        else {
            this.hasAnimVal = true;
            final AnimatableNumberListValue animNumList = (AnimatableNumberListValue)val;
            if (this.animVal == null) {
                this.animVal = new AnimSVGNumberList();
            }
            this.animVal.setAnimatedValue(animNumList.getNumbers());
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
    
    public class BaseSVGNumberList extends AbstractSVGNumberList
    {
        protected boolean missing;
        protected boolean malformed;
        
        @Override
        protected DOMException createDOMException(final short type, final String key, final Object[] args) {
            return SVGOMAnimatedNumberList.this.element.createDOMException(type, key, args);
        }
        
        @Override
        protected SVGException createSVGException(final short type, final String key, final Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedNumberList.this.element).createSVGException(type, key, args);
        }
        
        @Override
        protected Element getElement() {
            return SVGOMAnimatedNumberList.this.element;
        }
        
        @Override
        protected String getValueAsString() {
            final Attr attr = SVGOMAnimatedNumberList.this.element.getAttributeNodeNS(SVGOMAnimatedNumberList.this.namespaceURI, SVGOMAnimatedNumberList.this.localName);
            if (attr == null) {
                return SVGOMAnimatedNumberList.this.defaultValue;
            }
            return attr.getValue();
        }
        
        @Override
        protected void setAttributeValue(final String value) {
            try {
                SVGOMAnimatedNumberList.this.changing = true;
                SVGOMAnimatedNumberList.this.element.setAttributeNS(SVGOMAnimatedNumberList.this.namespaceURI, SVGOMAnimatedNumberList.this.localName, value);
            }
            finally {
                SVGOMAnimatedNumberList.this.changing = false;
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
            if (s == null || (isEmpty && !SVGOMAnimatedNumberList.this.emptyAllowed)) {
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
    
    protected class AnimSVGNumberList extends AbstractSVGNumberList
    {
        public AnimSVGNumberList() {
            this.itemList = new ArrayList(1);
        }
        
        @Override
        protected DOMException createDOMException(final short type, final String key, final Object[] args) {
            return SVGOMAnimatedNumberList.this.element.createDOMException(type, key, args);
        }
        
        @Override
        protected SVGException createSVGException(final short type, final String key, final Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedNumberList.this.element).createSVGException(type, key, args);
        }
        
        @Override
        protected Element getElement() {
            return SVGOMAnimatedNumberList.this.element;
        }
        
        @Override
        public int getNumberOfItems() {
            if (SVGOMAnimatedNumberList.this.hasAnimVal) {
                return super.getNumberOfItems();
            }
            return SVGOMAnimatedNumberList.this.getBaseVal().getNumberOfItems();
        }
        
        @Override
        public SVGNumber getItem(final int index) throws DOMException {
            if (SVGOMAnimatedNumberList.this.hasAnimVal) {
                return super.getItem(index);
            }
            return SVGOMAnimatedNumberList.this.getBaseVal().getItem(index);
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
            throw SVGOMAnimatedNumberList.this.element.createDOMException((short)7, "readonly.number.list", null);
        }
        
        @Override
        public SVGNumber initialize(final SVGNumber newItem) throws DOMException, SVGException {
            throw SVGOMAnimatedNumberList.this.element.createDOMException((short)7, "readonly.number.list", null);
        }
        
        @Override
        public SVGNumber insertItemBefore(final SVGNumber newItem, final int index) throws DOMException, SVGException {
            throw SVGOMAnimatedNumberList.this.element.createDOMException((short)7, "readonly.number.list", null);
        }
        
        @Override
        public SVGNumber replaceItem(final SVGNumber newItem, final int index) throws DOMException, SVGException {
            throw SVGOMAnimatedNumberList.this.element.createDOMException((short)7, "readonly.number.list", null);
        }
        
        @Override
        public SVGNumber removeItem(final int index) throws DOMException {
            throw SVGOMAnimatedNumberList.this.element.createDOMException((short)7, "readonly.number.list", null);
        }
        
        @Override
        public SVGNumber appendItem(final SVGNumber newItem) throws DOMException {
            throw SVGOMAnimatedNumberList.this.element.createDOMException((short)7, "readonly.number.list", null);
        }
        
        protected void setAnimatedValue(final float[] values) {
            int size;
            int i;
            for (size = this.itemList.size(), i = 0; i < size && i < values.length; ++i) {
                final SVGNumberItem n = this.itemList.get(i);
                n.setValue(values[i]);
            }
            while (i < values.length) {
                this.appendItemImpl(new SVGNumberItem(values[i]));
                ++i;
            }
            while (size > values.length) {
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
