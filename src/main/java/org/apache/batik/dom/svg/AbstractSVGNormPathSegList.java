// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import java.awt.geom.PathIterator;
import java.awt.geom.Arc2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;
import org.apache.batik.parser.DefaultPathHandler;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathHandler;
import org.apache.batik.parser.PathParser;

public abstract class AbstractSVGNormPathSegList extends AbstractSVGPathSegList
{
    protected AbstractSVGNormPathSegList() {
    }
    
    @Override
    protected void doParse(final String value, final ListHandler handler) throws ParseException {
        final PathParser pathParser = new PathParser();
        final NormalizedPathSegListBuilder builder = new NormalizedPathSegListBuilder(handler);
        pathParser.setPathHandler(builder);
        pathParser.parse(value);
    }
    
    protected static class NormalizedPathSegListBuilder extends DefaultPathHandler
    {
        protected ListHandler listHandler;
        protected SVGPathSegGenericItem lastAbs;
        
        public NormalizedPathSegListBuilder(final ListHandler listHandler) {
            this.listHandler = listHandler;
        }
        
        @Override
        public void startPath() throws ParseException {
            this.listHandler.startList();
            this.lastAbs = new SVGPathSegGenericItem((short)2, "M", 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        }
        
        @Override
        public void endPath() throws ParseException {
            this.listHandler.endList();
        }
        
        @Override
        public void movetoRel(final float x, final float y) throws ParseException {
            this.movetoAbs(this.lastAbs.getX() + x, this.lastAbs.getY() + y);
        }
        
        @Override
        public void movetoAbs(final float x, final float y) throws ParseException {
            this.listHandler.item(new SVGPathSegMovetoLinetoItem((short)2, "M", x, y));
            this.lastAbs.setX(x);
            this.lastAbs.setY(y);
            this.lastAbs.setPathSegType((short)2);
        }
        
        @Override
        public void closePath() throws ParseException {
            this.listHandler.item(new SVGPathSegItem((short)1, "z"));
        }
        
        @Override
        public void linetoRel(final float x, final float y) throws ParseException {
            this.linetoAbs(this.lastAbs.getX() + x, this.lastAbs.getY() + y);
        }
        
        @Override
        public void linetoAbs(final float x, final float y) throws ParseException {
            this.listHandler.item(new SVGPathSegMovetoLinetoItem((short)4, "L", x, y));
            this.lastAbs.setX(x);
            this.lastAbs.setY(y);
            this.lastAbs.setPathSegType((short)4);
        }
        
        @Override
        public void linetoHorizontalRel(final float x) throws ParseException {
            this.linetoAbs(this.lastAbs.getX() + x, this.lastAbs.getY());
        }
        
        @Override
        public void linetoHorizontalAbs(final float x) throws ParseException {
            this.linetoAbs(x, this.lastAbs.getY());
        }
        
        @Override
        public void linetoVerticalRel(final float y) throws ParseException {
            this.linetoAbs(this.lastAbs.getX(), this.lastAbs.getY() + y);
        }
        
        @Override
        public void linetoVerticalAbs(final float y) throws ParseException {
            this.linetoAbs(this.lastAbs.getX(), y);
        }
        
        @Override
        public void curvetoCubicRel(final float x1, final float y1, final float x2, final float y2, final float x, final float y) throws ParseException {
            this.curvetoCubicAbs(this.lastAbs.getX() + x1, this.lastAbs.getY() + y1, this.lastAbs.getX() + x2, this.lastAbs.getY() + y2, this.lastAbs.getX() + x, this.lastAbs.getY() + y);
        }
        
        @Override
        public void curvetoCubicAbs(final float x1, final float y1, final float x2, final float y2, final float x, final float y) throws ParseException {
            this.listHandler.item(new SVGPathSegCurvetoCubicItem((short)6, "C", x1, y1, x2, y2, x, y));
            this.lastAbs.setValue(x1, y1, x2, y2, x, y);
            this.lastAbs.setPathSegType((short)6);
        }
        
        @Override
        public void curvetoCubicSmoothRel(final float x2, final float y2, final float x, final float y) throws ParseException {
            this.curvetoCubicSmoothAbs(this.lastAbs.getX() + x2, this.lastAbs.getY() + y2, this.lastAbs.getX() + x, this.lastAbs.getY() + y);
        }
        
        @Override
        public void curvetoCubicSmoothAbs(final float x2, final float y2, final float x, final float y) throws ParseException {
            if (this.lastAbs.getPathSegType() == 6) {
                this.curvetoCubicAbs(this.lastAbs.getX() + (this.lastAbs.getX() - this.lastAbs.getX2()), this.lastAbs.getY() + (this.lastAbs.getY() - this.lastAbs.getY2()), x2, y2, x, y);
            }
            else {
                this.curvetoCubicAbs(this.lastAbs.getX(), this.lastAbs.getY(), x2, y2, x, y);
            }
        }
        
        @Override
        public void curvetoQuadraticRel(final float x1, final float y1, final float x, final float y) throws ParseException {
            this.curvetoQuadraticAbs(this.lastAbs.getX() + x1, this.lastAbs.getY() + y1, this.lastAbs.getX() + x, this.lastAbs.getY() + y);
        }
        
        @Override
        public void curvetoQuadraticAbs(final float x1, final float y1, final float x, final float y) throws ParseException {
            this.curvetoCubicAbs(this.lastAbs.getX() + 2.0f * (x1 - this.lastAbs.getX()) / 3.0f, this.lastAbs.getY() + 2.0f * (y1 - this.lastAbs.getY()) / 3.0f, x + 2.0f * (x1 - x) / 3.0f, y + 2.0f * (y1 - y) / 3.0f, x, y);
            this.lastAbs.setX1(x1);
            this.lastAbs.setY1(y1);
            this.lastAbs.setPathSegType((short)8);
        }
        
        @Override
        public void curvetoQuadraticSmoothRel(final float x, final float y) throws ParseException {
            this.curvetoQuadraticSmoothAbs(this.lastAbs.getX() + x, this.lastAbs.getY() + y);
        }
        
        @Override
        public void curvetoQuadraticSmoothAbs(final float x, final float y) throws ParseException {
            if (this.lastAbs.getPathSegType() == 8) {
                this.curvetoQuadraticAbs(this.lastAbs.getX() + (this.lastAbs.getX() - this.lastAbs.getX1()), this.lastAbs.getY() + (this.lastAbs.getY() - this.lastAbs.getY1()), x, y);
            }
            else {
                this.curvetoQuadraticAbs(this.lastAbs.getX(), this.lastAbs.getY(), x, y);
            }
        }
        
        @Override
        public void arcRel(final float rx, final float ry, final float xAxisRotation, final boolean largeArcFlag, final boolean sweepFlag, final float x, final float y) throws ParseException {
            this.arcAbs(rx, ry, xAxisRotation, largeArcFlag, sweepFlag, this.lastAbs.getX() + x, this.lastAbs.getY() + y);
        }
        
        @Override
        public void arcAbs(final float rx, final float ry, final float xAxisRotation, final boolean largeArcFlag, final boolean sweepFlag, final float x, final float y) throws ParseException {
            if (rx == 0.0f || ry == 0.0f) {
                this.linetoAbs(x, y);
                return;
            }
            final double x2 = this.lastAbs.getX();
            final double y2 = this.lastAbs.getY();
            if (x2 == x && y2 == y) {
                return;
            }
            final Arc2D arc = ExtendedGeneralPath.computeArc(x2, y2, rx, ry, xAxisRotation, largeArcFlag, sweepFlag, x, y);
            if (arc == null) {
                return;
            }
            final AffineTransform t = AffineTransform.getRotateInstance(Math.toRadians(xAxisRotation), arc.getCenterX(), arc.getCenterY());
            final Shape s = t.createTransformedShape(arc);
            final PathIterator pi = s.getPathIterator(new AffineTransform());
            final float[] d = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
            int i = -1;
            while (!pi.isDone()) {
                i = pi.currentSegment(d);
                switch (i) {
                    case 3: {
                        this.curvetoCubicAbs(d[0], d[1], d[2], d[3], d[4], d[5]);
                        break;
                    }
                }
                pi.next();
            }
            this.lastAbs.setPathSegType((short)10);
        }
    }
    
    protected static class SVGPathSegGenericItem extends SVGPathSegItem
    {
        public SVGPathSegGenericItem(final short type, final String letter, final float x1, final float y1, final float x2, final float y2, final float x, final float y) {
            super(type, letter);
            this.setX1(x2);
            this.setY1(y2);
            this.setX2(x2);
            this.setY2(y2);
            this.setX(x);
            this.setY(y);
        }
        
        public void setValue(final float x1, final float y1, final float x2, final float y2, final float x, final float y) {
            this.setX1(x2);
            this.setY1(y2);
            this.setX2(x2);
            this.setY2(y2);
            this.setX(x);
            this.setY(y);
        }
        
        public void setValue(final float x, final float y) {
            this.setX(x);
            this.setY(y);
        }
        
        public void setPathSegType(final short type) {
            this.type = type;
        }
    }
}
