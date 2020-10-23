// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.TransformListHandler;
import org.apache.batik.parser.TransformListParser;
import java.awt.geom.AffineTransform;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGTransformList;

public abstract class AbstractSVGTransformList extends AbstractSVGList implements SVGTransformList
{
    public static final String SVG_TRANSFORMATION_LIST_SEPARATOR = "";
    
    @Override
    protected String getItemSeparator() {
        return "";
    }
    
    protected abstract SVGException createSVGException(final short p0, final String p1, final Object[] p2);
    
    @Override
    public SVGTransform initialize(final SVGTransform newItem) throws DOMException, SVGException {
        return (SVGTransform)this.initializeImpl(newItem);
    }
    
    @Override
    public SVGTransform getItem(final int index) throws DOMException {
        return (SVGTransform)this.getItemImpl(index);
    }
    
    @Override
    public SVGTransform insertItemBefore(final SVGTransform newItem, final int index) throws DOMException, SVGException {
        return (SVGTransform)this.insertItemBeforeImpl(newItem, index);
    }
    
    @Override
    public SVGTransform replaceItem(final SVGTransform newItem, final int index) throws DOMException, SVGException {
        return (SVGTransform)this.replaceItemImpl(newItem, index);
    }
    
    @Override
    public SVGTransform removeItem(final int index) throws DOMException {
        return (SVGTransform)this.removeItemImpl(index);
    }
    
    @Override
    public SVGTransform appendItem(final SVGTransform newItem) throws DOMException, SVGException {
        return (SVGTransform)this.appendItemImpl(newItem);
    }
    
    @Override
    public SVGTransform createSVGTransformFromMatrix(final SVGMatrix matrix) {
        final SVGOMTransform transform = new SVGOMTransform();
        transform.setMatrix(matrix);
        return transform;
    }
    
    @Override
    public SVGTransform consolidate() {
        this.revalidate();
        final int size = this.itemList.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return this.getItem(0);
        }
        SVGTransformItem t = (SVGTransformItem)this.getItemImpl(0);
        final AffineTransform at = (AffineTransform)t.affineTransform.clone();
        for (int i = 1; i < size; ++i) {
            t = (SVGTransformItem)this.getItemImpl(i);
            at.concatenate(t.affineTransform);
        }
        final SVGOMMatrix matrix = new SVGOMMatrix(at);
        return this.initialize(this.createSVGTransformFromMatrix(matrix));
    }
    
    public AffineTransform getAffineTransform() {
        final AffineTransform at = new AffineTransform();
        for (int i = 0; i < this.getNumberOfItems(); ++i) {
            final SVGTransformItem item = (SVGTransformItem)this.getItem(i);
            at.concatenate(item.affineTransform);
        }
        return at;
    }
    
    @Override
    protected SVGItem createSVGItem(final Object newItem) {
        return new SVGTransformItem((SVGTransform)newItem);
    }
    
    @Override
    protected void doParse(final String value, final ListHandler handler) throws ParseException {
        final TransformListParser transformListParser = new TransformListParser();
        final TransformListBuilder builder = new TransformListBuilder(handler);
        transformListParser.setTransformListHandler(builder);
        transformListParser.parse(value);
    }
    
    @Override
    protected void checkItemType(final Object newItem) {
        if (!(newItem instanceof SVGTransform)) {
            this.createSVGException((short)0, "expected.transform", null);
        }
    }
    
    public static class SVGTransformItem extends AbstractSVGTransform implements SVGItem
    {
        protected boolean xOnly;
        protected boolean angleOnly;
        protected AbstractSVGList parent;
        protected String itemStringValue;
        
        public SVGTransformItem() {
        }
        
        public SVGTransformItem(final SVGTransform transform) {
            this.assign(transform);
        }
        
        protected void resetAttribute() {
            if (this.parent != null) {
                this.itemStringValue = null;
                this.parent.itemChanged();
            }
        }
        
        @Override
        public void setParent(final AbstractSVGList list) {
            this.parent = list;
        }
        
        @Override
        public AbstractSVGList getParent() {
            return this.parent;
        }
        
        @Override
        public String getValueAsString() {
            if (this.itemStringValue == null) {
                this.itemStringValue = this.getStringValue();
            }
            return this.itemStringValue;
        }
        
        public void assign(final SVGTransform transform) {
            this.type = transform.getType();
            final SVGMatrix matrix = transform.getMatrix();
            switch (this.type) {
                case 2: {
                    this.setTranslate(matrix.getE(), matrix.getF());
                    break;
                }
                case 3: {
                    this.setScale(matrix.getA(), matrix.getD());
                    break;
                }
                case 4: {
                    if (matrix.getE() == 0.0f) {
                        this.rotate(transform.getAngle());
                        break;
                    }
                    this.angleOnly = false;
                    if (matrix.getA() == 1.0f) {
                        this.setRotate(transform.getAngle(), matrix.getE(), matrix.getF());
                        break;
                    }
                    if (transform instanceof AbstractSVGTransform) {
                        final AbstractSVGTransform internal = (AbstractSVGTransform)transform;
                        this.setRotate(internal.getAngle(), internal.getX(), internal.getY());
                        break;
                    }
                    break;
                }
                case 5: {
                    this.setSkewX(transform.getAngle());
                    break;
                }
                case 6: {
                    this.setSkewY(transform.getAngle());
                    break;
                }
                case 1: {
                    this.setMatrix(matrix);
                    break;
                }
            }
        }
        
        protected void translate(final float x) {
            this.xOnly = true;
            this.setTranslate(x, 0.0f);
        }
        
        protected void rotate(final float angle) {
            this.angleOnly = true;
            this.setRotate(angle, 0.0f, 0.0f);
        }
        
        protected void scale(final float x) {
            this.xOnly = true;
            this.setScale(x, x);
        }
        
        protected void matrix(final float a, final float b, final float c, final float d, final float e, final float f) {
            this.setMatrix(new SVGOMMatrix(new AffineTransform(a, b, c, d, e, f)));
        }
        
        @Override
        public void setMatrix(final SVGMatrix matrix) {
            super.setMatrix(matrix);
            this.resetAttribute();
        }
        
        @Override
        public void setTranslate(final float tx, final float ty) {
            super.setTranslate(tx, ty);
            this.resetAttribute();
        }
        
        @Override
        public void setScale(final float sx, final float sy) {
            super.setScale(sx, sy);
            this.resetAttribute();
        }
        
        @Override
        public void setRotate(final float angle, final float cx, final float cy) {
            super.setRotate(angle, cx, cy);
            this.resetAttribute();
        }
        
        @Override
        public void setSkewX(final float angle) {
            super.setSkewX(angle);
            this.resetAttribute();
        }
        
        @Override
        public void setSkewY(final float angle) {
            super.setSkewY(angle);
            this.resetAttribute();
        }
        
        @Override
        protected SVGMatrix createMatrix() {
            return new AbstractSVGMatrix() {
                @Override
                protected AffineTransform getAffineTransform() {
                    return SVGTransformItem.this.affineTransform;
                }
                
                @Override
                public void setA(final float a) throws DOMException {
                    SVGTransformItem.this.type = 1;
                    super.setA(a);
                    SVGTransformItem.this.resetAttribute();
                }
                
                @Override
                public void setB(final float b) throws DOMException {
                    SVGTransformItem.this.type = 1;
                    super.setB(b);
                    SVGTransformItem.this.resetAttribute();
                }
                
                @Override
                public void setC(final float c) throws DOMException {
                    SVGTransformItem.this.type = 1;
                    super.setC(c);
                    SVGTransformItem.this.resetAttribute();
                }
                
                @Override
                public void setD(final float d) throws DOMException {
                    SVGTransformItem.this.type = 1;
                    super.setD(d);
                    SVGTransformItem.this.resetAttribute();
                }
                
                @Override
                public void setE(final float e) throws DOMException {
                    SVGTransformItem.this.type = 1;
                    super.setE(e);
                    SVGTransformItem.this.resetAttribute();
                }
                
                @Override
                public void setF(final float f) throws DOMException {
                    SVGTransformItem.this.type = 1;
                    super.setF(f);
                    SVGTransformItem.this.resetAttribute();
                }
            };
        }
        
        protected String getStringValue() {
            final StringBuffer buf = new StringBuffer();
            switch (this.type) {
                case 2: {
                    buf.append("translate(");
                    buf.append((float)this.affineTransform.getTranslateX());
                    if (!this.xOnly) {
                        buf.append(' ');
                        buf.append((float)this.affineTransform.getTranslateY());
                    }
                    buf.append(')');
                    break;
                }
                case 4: {
                    buf.append("rotate(");
                    buf.append(this.angle);
                    if (!this.angleOnly) {
                        buf.append(' ');
                        buf.append(this.x);
                        buf.append(' ');
                        buf.append(this.y);
                    }
                    buf.append(')');
                    break;
                }
                case 3: {
                    buf.append("scale(");
                    buf.append((float)this.affineTransform.getScaleX());
                    if (!this.xOnly) {
                        buf.append(' ');
                        buf.append((float)this.affineTransform.getScaleY());
                    }
                    buf.append(')');
                    break;
                }
                case 5: {
                    buf.append("skewX(");
                    buf.append(this.angle);
                    buf.append(')');
                    break;
                }
                case 6: {
                    buf.append("skewY(");
                    buf.append(this.angle);
                    buf.append(')');
                    break;
                }
                case 1: {
                    buf.append("matrix(");
                    final double[] matrix = new double[6];
                    this.affineTransform.getMatrix(matrix);
                    for (int i = 0; i < 6; ++i) {
                        if (i != 0) {
                            buf.append(' ');
                        }
                        buf.append((float)matrix[i]);
                    }
                    buf.append(')');
                    break;
                }
            }
            return buf.toString();
        }
    }
    
    protected static class TransformListBuilder implements TransformListHandler
    {
        protected ListHandler listHandler;
        
        public TransformListBuilder(final ListHandler listHandler) {
            this.listHandler = listHandler;
        }
        
        @Override
        public void startTransformList() throws ParseException {
            this.listHandler.startList();
        }
        
        @Override
        public void matrix(final float a, final float b, final float c, final float d, final float e, final float f) throws ParseException {
            final SVGTransformItem item = new SVGTransformItem();
            item.matrix(a, b, c, d, e, f);
            this.listHandler.item(item);
        }
        
        @Override
        public void rotate(final float theta) throws ParseException {
            final SVGTransformItem item = new SVGTransformItem();
            item.rotate(theta);
            this.listHandler.item(item);
        }
        
        @Override
        public void rotate(final float theta, final float cx, final float cy) throws ParseException {
            final SVGTransformItem item = new SVGTransformItem();
            item.setRotate(theta, cx, cy);
            this.listHandler.item(item);
        }
        
        @Override
        public void translate(final float tx) throws ParseException {
            final SVGTransformItem item = new SVGTransformItem();
            item.translate(tx);
            this.listHandler.item(item);
        }
        
        @Override
        public void translate(final float tx, final float ty) throws ParseException {
            final SVGTransformItem item = new SVGTransformItem();
            item.setTranslate(tx, ty);
            this.listHandler.item(item);
        }
        
        @Override
        public void scale(final float sx) throws ParseException {
            final SVGTransformItem item = new SVGTransformItem();
            item.scale(sx);
            this.listHandler.item(item);
        }
        
        @Override
        public void scale(final float sx, final float sy) throws ParseException {
            final SVGTransformItem item = new SVGTransformItem();
            item.setScale(sx, sy);
            this.listHandler.item(item);
        }
        
        @Override
        public void skewX(final float skx) throws ParseException {
            final SVGTransformItem item = new SVGTransformItem();
            item.setSkewX(skx);
            this.listHandler.item(item);
        }
        
        @Override
        public void skewY(final float sky) throws ParseException {
            final SVGTransformItem item = new SVGTransformItem();
            item.setSkewY(sky);
            this.listHandler.item(item);
        }
        
        @Override
        public void endTransformList() throws ParseException {
            this.listHandler.endList();
        }
    }
}
