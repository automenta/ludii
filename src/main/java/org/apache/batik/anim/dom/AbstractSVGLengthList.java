// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.LengthListHandler;
import org.apache.batik.parser.LengthListParser;
import org.apache.batik.dom.svg.ListHandler;
import org.apache.batik.dom.svg.SVGItem;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGLengthList;
import org.apache.batik.dom.svg.AbstractSVGList;

public abstract class AbstractSVGLengthList extends AbstractSVGList implements SVGLengthList
{
    protected short direction;
    public static final String SVG_LENGTH_LIST_SEPARATOR = " ";
    
    @Override
    protected String getItemSeparator() {
        return " ";
    }
    
    protected abstract SVGException createSVGException(final short p0, final String p1, final Object[] p2);
    
    protected abstract Element getElement();
    
    protected AbstractSVGLengthList(final short direction) {
        this.direction = direction;
    }
    
    @Override
    public SVGLength initialize(final SVGLength newItem) throws DOMException, SVGException {
        return (SVGLength)this.initializeImpl(newItem);
    }
    
    @Override
    public SVGLength getItem(final int index) throws DOMException {
        return (SVGLength)this.getItemImpl(index);
    }
    
    @Override
    public SVGLength insertItemBefore(final SVGLength newItem, final int index) throws DOMException, SVGException {
        return (SVGLength)this.insertItemBeforeImpl(newItem, index);
    }
    
    @Override
    public SVGLength replaceItem(final SVGLength newItem, final int index) throws DOMException, SVGException {
        return (SVGLength)this.replaceItemImpl(newItem, index);
    }
    
    @Override
    public SVGLength removeItem(final int index) throws DOMException {
        return (SVGLength)this.removeItemImpl(index);
    }
    
    @Override
    public SVGLength appendItem(final SVGLength newItem) throws DOMException, SVGException {
        return (SVGLength)this.appendItemImpl(newItem);
    }
    
    @Override
    protected SVGItem createSVGItem(final Object newItem) {
        final SVGLength l = (SVGLength)newItem;
        return new SVGLengthItem(l.getUnitType(), l.getValueInSpecifiedUnits(), this.direction);
    }
    
    @Override
    protected void doParse(final String value, final ListHandler handler) throws ParseException {
        final LengthListParser lengthListParser = new LengthListParser();
        final LengthListBuilder builder = new LengthListBuilder(handler);
        lengthListParser.setLengthListHandler(builder);
        lengthListParser.parse(value);
    }
    
    @Override
    protected void checkItemType(final Object newItem) throws SVGException {
        if (!(newItem instanceof SVGLength)) {
            this.createSVGException((short)0, "expected.length", null);
        }
    }
    
    protected class SVGLengthItem extends AbstractSVGLength implements SVGItem
    {
        protected AbstractSVGList parentList;
        
        public SVGLengthItem(final short type, final float value, final short direction) {
            super(direction);
            this.unitType = type;
            this.value = value;
        }
        
        @Override
        protected SVGOMElement getAssociatedElement() {
            return (SVGOMElement)AbstractSVGLengthList.this.getElement();
        }
        
        @Override
        public void setParent(final AbstractSVGList list) {
            this.parentList = list;
        }
        
        @Override
        public AbstractSVGList getParent() {
            return this.parentList;
        }
        
        @Override
        protected void reset() {
            if (this.parentList != null) {
                this.parentList.itemChanged();
            }
        }
    }
    
    protected class LengthListBuilder implements LengthListHandler
    {
        protected ListHandler listHandler;
        protected float currentValue;
        protected short currentType;
        
        public LengthListBuilder(final ListHandler listHandler) {
            this.listHandler = listHandler;
        }
        
        @Override
        public void startLengthList() throws ParseException {
            this.listHandler.startList();
        }
        
        @Override
        public void startLength() throws ParseException {
            this.currentType = 1;
            this.currentValue = 0.0f;
        }
        
        @Override
        public void lengthValue(final float v) throws ParseException {
            this.currentValue = v;
        }
        
        @Override
        public void em() throws ParseException {
            this.currentType = 3;
        }
        
        @Override
        public void ex() throws ParseException {
            this.currentType = 4;
        }
        
        @Override
        public void in() throws ParseException {
            this.currentType = 8;
        }
        
        @Override
        public void cm() throws ParseException {
            this.currentType = 6;
        }
        
        @Override
        public void mm() throws ParseException {
            this.currentType = 7;
        }
        
        @Override
        public void pc() throws ParseException {
            this.currentType = 10;
        }
        
        @Override
        public void pt() throws ParseException {
            this.currentType = 3;
        }
        
        @Override
        public void px() throws ParseException {
            this.currentType = 5;
        }
        
        @Override
        public void percentage() throws ParseException {
            this.currentType = 2;
        }
        
        @Override
        public void endLength() throws ParseException {
            this.listHandler.item(new SVGLengthItem(this.currentType, this.currentValue, AbstractSVGLengthList.this.direction));
        }
        
        @Override
        public void endLengthList() throws ParseException {
            this.listHandler.endList();
        }
    }
}
