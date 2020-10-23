// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.io.ObjectStreamException;
import java.io.Serializable;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import java.util.Iterator;
import java.awt.geom.GeneralPath;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import org.apache.batik.gvt.text.AttributedCharacterSpanIterator;
import org.apache.batik.gvt.text.TextPaintInfo;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;
import org.apache.batik.gvt.Selectable;
import org.apache.batik.gvt.AbstractGraphicsNode;

public class TextNode extends AbstractGraphicsNode implements Selectable
{
    public static final AttributedCharacterIterator.Attribute PAINT_INFO;
    protected Point2D location;
    protected AttributedCharacterIterator aci;
    protected String text;
    protected Mark beginMark;
    protected Mark endMark;
    protected List textRuns;
    protected TextPainter textPainter;
    private Rectangle2D geometryBounds;
    private Rectangle2D primitiveBounds;
    private Shape outline;
    
    public TextNode() {
        this.location = new Point2D.Float(0.0f, 0.0f);
        this.beginMark = null;
        this.endMark = null;
        this.textPainter = StrokingTextPainter.getInstance();
    }
    
    public void setTextPainter(final TextPainter textPainter) {
        if (textPainter == null) {
            this.textPainter = StrokingTextPainter.getInstance();
        }
        else {
            this.textPainter = textPainter;
        }
    }
    
    public TextPainter getTextPainter() {
        return this.textPainter;
    }
    
    public List getTextRuns() {
        return this.textRuns;
    }
    
    public void setTextRuns(final List textRuns) {
        this.textRuns = textRuns;
    }
    
    public String getText() {
        if (this.text != null) {
            return this.text;
        }
        if (this.aci == null) {
            this.text = "";
        }
        else {
            final StringBuffer buf = new StringBuffer(this.aci.getEndIndex());
            for (char c = this.aci.first(); c != '\uffff'; c = this.aci.next()) {
                buf.append(c);
            }
            this.text = buf.toString();
        }
        return this.text;
    }
    
    public void setLocation(final Point2D newLocation) {
        this.fireGraphicsNodeChangeStarted();
        this.invalidateGeometryCache();
        this.location = newLocation;
        this.fireGraphicsNodeChangeCompleted();
    }
    
    public Point2D getLocation() {
        return this.location;
    }
    
    public void swapTextPaintInfo(final TextPaintInfo newInfo, final TextPaintInfo oldInfo) {
        this.fireGraphicsNodeChangeStarted();
        this.invalidateGeometryCache();
        oldInfo.set(newInfo);
        this.fireGraphicsNodeChangeCompleted();
    }
    
    public void setAttributedCharacterIterator(final AttributedCharacterIterator newAci) {
        this.fireGraphicsNodeChangeStarted();
        this.invalidateGeometryCache();
        this.aci = newAci;
        this.text = null;
        this.textRuns = null;
        this.fireGraphicsNodeChangeCompleted();
    }
    
    public AttributedCharacterIterator getAttributedCharacterIterator() {
        return this.aci;
    }
    
    @Override
    protected void invalidateGeometryCache() {
        super.invalidateGeometryCache();
        this.primitiveBounds = null;
        this.geometryBounds = null;
        this.outline = null;
    }
    
    @Override
    public Rectangle2D getPrimitiveBounds() {
        if (this.primitiveBounds == null && this.aci != null) {
            this.primitiveBounds = this.textPainter.getBounds2D(this);
        }
        return this.primitiveBounds;
    }
    
    @Override
    public Rectangle2D getGeometryBounds() {
        if (this.geometryBounds == null && this.aci != null) {
            this.geometryBounds = this.textPainter.getGeometryBounds(this);
        }
        return this.geometryBounds;
    }
    
    @Override
    public Rectangle2D getSensitiveBounds() {
        return this.getGeometryBounds();
    }
    
    @Override
    public Shape getOutline() {
        if (this.outline == null && this.aci != null) {
            this.outline = this.textPainter.getOutline(this);
        }
        return this.outline;
    }
    
    public Mark getMarkerForChar(final int index, final boolean beforeChar) {
        return this.textPainter.getMark(this, index, beforeChar);
    }
    
    public void setSelection(final Mark begin, final Mark end) {
        if (begin.getTextNode() != this || end.getTextNode() != this) {
            throw new RuntimeException("Markers not from this TextNode");
        }
        this.beginMark = begin;
        this.endMark = end;
    }
    
    @Override
    public boolean selectAt(final double x, final double y) {
        this.beginMark = this.textPainter.selectAt(x, y, this);
        return true;
    }
    
    @Override
    public boolean selectTo(final double x, final double y) {
        final Mark tmpMark = this.textPainter.selectTo(x, y, this.beginMark);
        if (tmpMark == null) {
            return false;
        }
        if (tmpMark != this.endMark) {
            this.endMark = tmpMark;
            return true;
        }
        return false;
    }
    
    @Override
    public boolean selectAll(final double x, final double y) {
        this.beginMark = this.textPainter.selectFirst(this);
        this.endMark = this.textPainter.selectLast(this);
        return true;
    }
    
    @Override
    public Object getSelection() {
        Object o = null;
        if (this.aci == null) {
            return o;
        }
        final int[] ranges = this.textPainter.getSelected(this.beginMark, this.endMark);
        if (ranges != null && ranges.length > 1) {
            if (ranges[0] > ranges[1]) {
                final int temp = ranges[1];
                ranges[1] = ranges[0];
                ranges[0] = temp;
            }
            o = new AttributedCharacterSpanIterator(this.aci, ranges[0], ranges[1] + 1);
        }
        return o;
    }
    
    @Override
    public Shape getHighlightShape() {
        Shape highlightShape = this.textPainter.getHighlightShape(this.beginMark, this.endMark);
        final AffineTransform t = this.getGlobalTransform();
        highlightShape = t.createTransformedShape(highlightShape);
        return highlightShape;
    }
    
    @Override
    public void primitivePaint(final Graphics2D g2d) {
        final Shape clip = g2d.getClip();
        if (clip != null && !(clip instanceof GeneralPath)) {
            g2d.setClip(new GeneralPath(clip));
        }
        this.textPainter.paint(this, g2d);
    }
    
    @Override
    public boolean contains(final Point2D p) {
        if (!super.contains(p)) {
            return false;
        }
        final List list = this.getTextRuns();
        for (final Object aList : list) {
            final StrokingTextPainter.TextRun run = (StrokingTextPainter.TextRun)aList;
            final TextSpanLayout layout = run.getLayout();
            final float x = (float)p.getX();
            final float y = (float)p.getY();
            final TextHit textHit = layout.hitTestChar(x, y);
            if (textHit != null && this.contains(p, layout.getBounds2D())) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean contains(final Point2D p, final Rectangle2D b) {
        if (b == null || !b.contains(p)) {
            return false;
        }
        switch (this.pointerEventType) {
            case 0:
            case 1:
            case 2:
            case 3: {
                return this.isVisible;
            }
            case 4:
            case 5:
            case 6:
            case 7: {
                return true;
            }
            case 8: {
                return false;
            }
            default: {
                return false;
            }
        }
    }
    
    static {
        PAINT_INFO = GVTAttributedCharacterIterator.TextAttribute.PAINT_INFO;
    }
    
    public static final class Anchor implements Serializable
    {
        public static final int ANCHOR_START = 0;
        public static final int ANCHOR_MIDDLE = 1;
        public static final int ANCHOR_END = 2;
        public static final Anchor START;
        public static final Anchor MIDDLE;
        public static final Anchor END;
        private int type;
        
        private Anchor(final int type) {
            this.type = type;
        }
        
        public int getType() {
            return this.type;
        }
        
        private Object readResolve() throws ObjectStreamException {
            switch (this.type) {
                case 0: {
                    return Anchor.START;
                }
                case 1: {
                    return Anchor.MIDDLE;
                }
                case 2: {
                    return Anchor.END;
                }
                default: {
                    throw new RuntimeException("Unknown Anchor type");
                }
            }
        }
        
        static {
            START = new Anchor(0);
            MIDDLE = new Anchor(1);
            END = new Anchor(2);
        }
    }
}
