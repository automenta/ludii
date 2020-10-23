// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import java.util.Iterator;
import java.util.ArrayList;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.apache.batik.parser.ParseException;
import java.util.List;

public abstract class AbstractSVGList
{
    protected boolean valid;
    protected List itemList;
    
    protected abstract String getItemSeparator();
    
    protected abstract SVGItem createSVGItem(final Object p0);
    
    protected abstract void doParse(final String p0, final ListHandler p1) throws ParseException;
    
    protected abstract void checkItemType(final Object p0) throws SVGException;
    
    protected abstract String getValueAsString();
    
    protected abstract void setAttributeValue(final String p0);
    
    protected abstract DOMException createDOMException(final short p0, final String p1, final Object[] p2);
    
    public int getNumberOfItems() {
        this.revalidate();
        if (this.itemList != null) {
            return this.itemList.size();
        }
        return 0;
    }
    
    public void clear() throws DOMException {
        this.revalidate();
        if (this.itemList != null) {
            this.clear(this.itemList);
            this.resetAttribute();
        }
    }
    
    protected SVGItem initializeImpl(final Object newItem) throws DOMException, SVGException {
        this.checkItemType(newItem);
        if (this.itemList == null) {
            this.itemList = new ArrayList(1);
        }
        else {
            this.clear(this.itemList);
        }
        final SVGItem item = this.removeIfNeeded(newItem);
        this.itemList.add(item);
        item.setParent(this);
        this.resetAttribute();
        return item;
    }
    
    protected SVGItem getItemImpl(final int index) throws DOMException {
        this.revalidate();
        if (index < 0 || this.itemList == null || index >= this.itemList.size()) {
            throw this.createDOMException((short)1, "index.out.of.bounds", new Object[] { index });
        }
        return this.itemList.get(index);
    }
    
    protected SVGItem insertItemBeforeImpl(final Object newItem, int index) throws DOMException, SVGException {
        this.checkItemType(newItem);
        this.revalidate();
        if (index < 0) {
            throw this.createDOMException((short)1, "index.out.of.bounds", new Object[] { index });
        }
        if (index > this.itemList.size()) {
            index = this.itemList.size();
        }
        final SVGItem item = this.removeIfNeeded(newItem);
        this.itemList.add(index, item);
        item.setParent(this);
        this.resetAttribute();
        return item;
    }
    
    protected SVGItem replaceItemImpl(final Object newItem, final int index) throws DOMException, SVGException {
        this.checkItemType(newItem);
        this.revalidate();
        if (index < 0 || index >= this.itemList.size()) {
            throw this.createDOMException((short)1, "index.out.of.bounds", new Object[] { index });
        }
        final SVGItem item = this.removeIfNeeded(newItem);
        this.itemList.set(index, item);
        item.setParent(this);
        this.resetAttribute();
        return item;
    }
    
    protected SVGItem removeItemImpl(final int index) throws DOMException {
        this.revalidate();
        if (index < 0 || index >= this.itemList.size()) {
            throw this.createDOMException((short)1, "index.out.of.bounds", new Object[] { index });
        }
        final SVGItem item = this.itemList.remove(index);
        item.setParent(null);
        this.resetAttribute();
        return item;
    }
    
    protected SVGItem appendItemImpl(final Object newItem) throws DOMException, SVGException {
        this.checkItemType(newItem);
        this.revalidate();
        final SVGItem item = this.removeIfNeeded(newItem);
        this.itemList.add(item);
        item.setParent(this);
        if (this.itemList.size() <= 1) {
            this.resetAttribute();
        }
        else {
            this.resetAttribute(item);
        }
        return item;
    }
    
    protected SVGItem removeIfNeeded(final Object newItem) {
        SVGItem item;
        if (newItem instanceof SVGItem) {
            item = (SVGItem)newItem;
            if (item.getParent() != null) {
                item.getParent().removeItem(item);
            }
        }
        else {
            item = this.createSVGItem(newItem);
        }
        return item;
    }
    
    protected void revalidate() {
        if (this.valid) {
            return;
        }
        try {
            final ListBuilder builder = new ListBuilder(this);
            this.doParse(this.getValueAsString(), builder);
            final List parsedList = builder.getList();
            if (parsedList != null) {
                this.clear(this.itemList);
            }
            this.itemList = parsedList;
        }
        catch (ParseException e) {
            this.itemList = null;
        }
        this.valid = true;
    }
    
    protected void setValueAsString(final List value) throws DOMException {
        String finalValue = null;
        final Iterator it = value.iterator();
        if (it.hasNext()) {
            SVGItem item = it.next();
            final StringBuffer buf = new StringBuffer(value.size() * 8);
            buf.append(item.getValueAsString());
            while (it.hasNext()) {
                item = it.next();
                buf.append(this.getItemSeparator());
                buf.append(item.getValueAsString());
            }
            finalValue = buf.toString();
        }
        this.setAttributeValue(finalValue);
        this.valid = true;
    }
    
    public void itemChanged() {
        this.resetAttribute();
    }
    
    protected void resetAttribute() {
        this.setValueAsString(this.itemList);
    }
    
    protected void resetAttribute(final SVGItem item) {
        final String newValue = this.getValueAsString() + this.getItemSeparator() + item.getValueAsString();
        this.setAttributeValue(newValue);
        this.valid = true;
    }
    
    public void invalidate() {
        this.valid = false;
    }
    
    protected void removeItem(final SVGItem item) {
        if (this.itemList.contains(item)) {
            this.itemList.remove(item);
            item.setParent(null);
            this.resetAttribute();
        }
    }
    
    protected void clear(final List list) {
        if (list == null) {
            return;
        }
        for (final Object aList : list) {
            final SVGItem item = (SVGItem)aList;
            item.setParent(null);
        }
        list.clear();
    }
}
