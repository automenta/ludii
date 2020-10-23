// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.NumberListHandler;
import org.apache.batik.parser.NumberListParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGNumber;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGNumberList;

public abstract class AbstractSVGNumberList extends AbstractSVGList implements SVGNumberList
{
    public static final String SVG_NUMBER_LIST_SEPARATOR = " ";
    
    @Override
    protected String getItemSeparator() {
        return " ";
    }
    
    protected abstract SVGException createSVGException(final short p0, final String p1, final Object[] p2);
    
    protected abstract Element getElement();
    
    protected AbstractSVGNumberList() {
    }
    
    @Override
    public SVGNumber initialize(final SVGNumber newItem) throws DOMException, SVGException {
        return (SVGNumber)this.initializeImpl(newItem);
    }
    
    @Override
    public SVGNumber getItem(final int index) throws DOMException {
        return (SVGNumber)this.getItemImpl(index);
    }
    
    @Override
    public SVGNumber insertItemBefore(final SVGNumber newItem, final int index) throws DOMException, SVGException {
        return (SVGNumber)this.insertItemBeforeImpl(newItem, index);
    }
    
    @Override
    public SVGNumber replaceItem(final SVGNumber newItem, final int index) throws DOMException, SVGException {
        return (SVGNumber)this.replaceItemImpl(newItem, index);
    }
    
    @Override
    public SVGNumber removeItem(final int index) throws DOMException {
        return (SVGNumber)this.removeItemImpl(index);
    }
    
    @Override
    public SVGNumber appendItem(final SVGNumber newItem) throws DOMException, SVGException {
        return (SVGNumber)this.appendItemImpl(newItem);
    }
    
    @Override
    protected SVGItem createSVGItem(final Object newItem) {
        final SVGNumber l = (SVGNumber)newItem;
        return new SVGNumberItem(l.getValue());
    }
    
    @Override
    protected void doParse(final String value, final ListHandler handler) throws ParseException {
        final NumberListParser NumberListParser = new NumberListParser();
        final NumberListBuilder builder = new NumberListBuilder(handler);
        NumberListParser.setNumberListHandler(builder);
        NumberListParser.parse(value);
    }
    
    @Override
    protected void checkItemType(final Object newItem) throws SVGException {
        if (!(newItem instanceof SVGNumber)) {
            this.createSVGException((short)0, "expected SVGNumber", null);
        }
    }
    
    protected static class NumberListBuilder implements NumberListHandler
    {
        protected ListHandler listHandler;
        protected float currentValue;
        
        public NumberListBuilder(final ListHandler listHandler) {
            this.listHandler = listHandler;
        }
        
        @Override
        public void startNumberList() throws ParseException {
            this.listHandler.startList();
        }
        
        @Override
        public void startNumber() throws ParseException {
            this.currentValue = 0.0f;
        }
        
        @Override
        public void numberValue(final float v) throws ParseException {
            this.currentValue = v;
        }
        
        @Override
        public void endNumber() throws ParseException {
            this.listHandler.item(new SVGNumberItem(this.currentValue));
        }
        
        @Override
        public void endNumberList() throws ParseException {
            this.listHandler.endList();
        }
    }
}
