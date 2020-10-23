// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import java.util.Iterator;
import org.w3c.dom.svg.SVGTransform;
import org.apache.batik.parser.ParseException;
import org.apache.batik.dom.svg.ListHandler;
import org.apache.batik.dom.svg.AbstractSVGList;
import org.apache.batik.dom.svg.ListBuilder;
import org.apache.batik.dom.svg.SVGItem;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.DOMException;
import org.apache.batik.dom.svg.AbstractSVGTransformList;
import org.w3c.dom.Attr;
import java.util.List;
import org.apache.batik.anim.values.AnimatableTransformListValue;
import java.util.ArrayList;
import org.apache.batik.anim.values.AnimatableValue;
import org.w3c.dom.Element;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.w3c.dom.svg.SVGTransformList;
import org.w3c.dom.svg.SVGAnimatedTransformList;

public class SVGOMAnimatedTransformList extends AbstractSVGAnimatedValue implements SVGAnimatedTransformList
{
    protected BaseSVGTransformList baseVal;
    protected AnimSVGTransformList animVal;
    protected boolean changing;
    protected String defaultValue;
    
    public SVGOMAnimatedTransformList(final AbstractElement elt, final String ns, final String ln, final String defaultValue) {
        super(elt, ns, ln);
        this.defaultValue = defaultValue;
    }
    
    @Override
    public SVGTransformList getBaseVal() {
        if (this.baseVal == null) {
            this.baseVal = new BaseSVGTransformList();
        }
        return this.baseVal;
    }
    
    @Override
    public SVGTransformList getAnimVal() {
        if (this.animVal == null) {
            this.animVal = new AnimSVGTransformList();
        }
        return this.animVal;
    }
    
    public void check() {
        if (!this.hasAnimVal) {
            if (this.baseVal == null) {
                this.baseVal = new BaseSVGTransformList();
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
        final SVGTransformList tl = this.getBaseVal();
        final int n = tl.getNumberOfItems();
        final List v = new ArrayList(n);
        for (int i = 0; i < n; ++i) {
            v.add(tl.getItem(i));
        }
        return new AnimatableTransformListValue(target, v);
    }
    
    @Override
    protected void updateAnimatedValue(final AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        }
        else {
            this.hasAnimVal = true;
            final AnimatableTransformListValue aval = (AnimatableTransformListValue)val;
            if (this.animVal == null) {
                this.animVal = new AnimSVGTransformList();
            }
            this.animVal.setAnimatedValue(aval.getTransforms());
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
    
    public class BaseSVGTransformList extends AbstractSVGTransformList
    {
        protected boolean missing;
        protected boolean malformed;
        
        @Override
        protected DOMException createDOMException(final short type, final String key, final Object[] args) {
            return SVGOMAnimatedTransformList.this.element.createDOMException(type, key, args);
        }
        
        @Override
        protected SVGException createSVGException(final short type, final String key, final Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedTransformList.this.element).createSVGException(type, key, args);
        }
        
        @Override
        protected String getValueAsString() {
            final Attr attr = SVGOMAnimatedTransformList.this.element.getAttributeNodeNS(SVGOMAnimatedTransformList.this.namespaceURI, SVGOMAnimatedTransformList.this.localName);
            if (attr == null) {
                return SVGOMAnimatedTransformList.this.defaultValue;
            }
            return attr.getValue();
        }
        
        @Override
        protected void setAttributeValue(final String value) {
            try {
                SVGOMAnimatedTransformList.this.changing = true;
                SVGOMAnimatedTransformList.this.element.setAttributeNS(SVGOMAnimatedTransformList.this.namespaceURI, SVGOMAnimatedTransformList.this.localName, value);
            }
            finally {
                SVGOMAnimatedTransformList.this.changing = false;
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
    
    protected class AnimSVGTransformList extends AbstractSVGTransformList
    {
        public AnimSVGTransformList() {
            this.itemList = new ArrayList(1);
        }
        
        @Override
        protected DOMException createDOMException(final short type, final String key, final Object[] args) {
            return SVGOMAnimatedTransformList.this.element.createDOMException(type, key, args);
        }
        
        @Override
        protected SVGException createSVGException(final short type, final String key, final Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedTransformList.this.element).createSVGException(type, key, args);
        }
        
        @Override
        public int getNumberOfItems() {
            if (SVGOMAnimatedTransformList.this.hasAnimVal) {
                return super.getNumberOfItems();
            }
            return SVGOMAnimatedTransformList.this.getBaseVal().getNumberOfItems();
        }
        
        @Override
        public SVGTransform getItem(final int index) throws DOMException {
            if (SVGOMAnimatedTransformList.this.hasAnimVal) {
                return super.getItem(index);
            }
            return SVGOMAnimatedTransformList.this.getBaseVal().getItem(index);
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
            throw SVGOMAnimatedTransformList.this.element.createDOMException((short)7, "readonly.transform.list", null);
        }
        
        @Override
        public SVGTransform initialize(final SVGTransform newItem) throws DOMException, SVGException {
            throw SVGOMAnimatedTransformList.this.element.createDOMException((short)7, "readonly.transform.list", null);
        }
        
        @Override
        public SVGTransform insertItemBefore(final SVGTransform newItem, final int index) throws DOMException, SVGException {
            throw SVGOMAnimatedTransformList.this.element.createDOMException((short)7, "readonly.transform.list", null);
        }
        
        @Override
        public SVGTransform replaceItem(final SVGTransform newItem, final int index) throws DOMException, SVGException {
            throw SVGOMAnimatedTransformList.this.element.createDOMException((short)7, "readonly.transform.list", null);
        }
        
        @Override
        public SVGTransform removeItem(final int index) throws DOMException {
            throw SVGOMAnimatedTransformList.this.element.createDOMException((short)7, "readonly.transform.list", null);
        }
        
        @Override
        public SVGTransform appendItem(final SVGTransform newItem) throws DOMException {
            throw SVGOMAnimatedTransformList.this.element.createDOMException((short)7, "readonly.transform.list", null);
        }
        
        @Override
        public SVGTransform consolidate() {
            throw SVGOMAnimatedTransformList.this.element.createDOMException((short)7, "readonly.transform.list", null);
        }
        
        protected void setAnimatedValue(final Iterator it) {
            int size;
            int i;
            for (size = this.itemList.size(), i = 0; i < size && it.hasNext(); ++i) {
                final SVGTransformItem t = this.itemList.get(i);
                t.assign(it.next());
            }
            while (it.hasNext()) {
                this.appendItemImpl(new SVGTransformItem(it.next()));
                ++i;
            }
            while (size > i) {
                this.removeItemImpl(--size);
            }
        }
        
        protected void setAnimatedValue(final SVGTransform transform) {
            int size = this.itemList.size();
            while (size > 1) {
                this.removeItemImpl(--size);
            }
            if (size == 0) {
                this.appendItemImpl(new SVGTransformItem(transform));
            }
            else {
                final SVGTransformItem t = this.itemList.get(0);
                t.assign(transform);
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
