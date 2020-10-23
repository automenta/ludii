// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PointsHandler;
import org.apache.batik.parser.PointsParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGPointList;

public abstract class AbstractSVGPointList extends AbstractSVGList implements SVGPointList
{
    public static final String SVG_POINT_LIST_SEPARATOR = " ";
    
    @Override
    protected String getItemSeparator() {
        return " ";
    }
    
    protected abstract SVGException createSVGException(final short p0, final String p1, final Object[] p2);
    
    @Override
    public SVGPoint initialize(final SVGPoint newItem) throws DOMException, SVGException {
        return (SVGPoint)this.initializeImpl(newItem);
    }
    
    @Override
    public SVGPoint getItem(final int index) throws DOMException {
        return (SVGPoint)this.getItemImpl(index);
    }
    
    @Override
    public SVGPoint insertItemBefore(final SVGPoint newItem, final int index) throws DOMException, SVGException {
        return (SVGPoint)this.insertItemBeforeImpl(newItem, index);
    }
    
    @Override
    public SVGPoint replaceItem(final SVGPoint newItem, final int index) throws DOMException, SVGException {
        return (SVGPoint)this.replaceItemImpl(newItem, index);
    }
    
    @Override
    public SVGPoint removeItem(final int index) throws DOMException {
        return (SVGPoint)this.removeItemImpl(index);
    }
    
    @Override
    public SVGPoint appendItem(final SVGPoint newItem) throws DOMException, SVGException {
        return (SVGPoint)this.appendItemImpl(newItem);
    }
    
    @Override
    protected SVGItem createSVGItem(final Object newItem) {
        final SVGPoint point = (SVGPoint)newItem;
        return new SVGPointItem(point.getX(), point.getY());
    }
    
    @Override
    protected void doParse(final String value, final ListHandler handler) throws ParseException {
        final PointsParser pointsParser = new PointsParser();
        final PointsListBuilder builder = new PointsListBuilder(handler);
        pointsParser.setPointsHandler(builder);
        pointsParser.parse(value);
    }
    
    @Override
    protected void checkItemType(final Object newItem) throws SVGException {
        if (!(newItem instanceof SVGPoint)) {
            this.createSVGException((short)0, "expected.point", null);
        }
    }
    
    protected static class PointsListBuilder implements PointsHandler
    {
        protected ListHandler listHandler;
        
        public PointsListBuilder(final ListHandler listHandler) {
            this.listHandler = listHandler;
        }
        
        @Override
        public void startPoints() throws ParseException {
            this.listHandler.startList();
        }
        
        @Override
        public void point(final float x, final float y) throws ParseException {
            this.listHandler.item(new SVGPointItem(x, y));
        }
        
        @Override
        public void endPoints() throws ParseException {
            this.listHandler.endList();
        }
    }
}
