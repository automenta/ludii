// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.apache.batik.parser.DefaultPathHandler;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothRel;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothRel;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothAbs;
import org.w3c.dom.svg.SVGPathSegLinetoVerticalRel;
import org.w3c.dom.svg.SVGPathSegLinetoVerticalAbs;
import org.w3c.dom.svg.SVGPathSegLinetoHorizontalRel;
import org.w3c.dom.svg.SVGPathSegLinetoHorizontalAbs;
import org.w3c.dom.svg.SVGPathSegArcRel;
import org.w3c.dom.svg.SVGPathSegArcAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticRel;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicRel;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs;
import org.w3c.dom.svg.SVGPathSegLinetoRel;
import org.w3c.dom.svg.SVGPathSegLinetoAbs;
import org.w3c.dom.svg.SVGPathSegMovetoRel;
import org.w3c.dom.svg.SVGPathSegMovetoAbs;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathHandler;
import org.apache.batik.parser.PathParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGPathSeg;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGPathSegList;

public abstract class AbstractSVGPathSegList extends AbstractSVGList implements SVGPathSegList, SVGPathSegConstants
{
    public static final String SVG_PATHSEG_LIST_SEPARATOR = " ";
    
    protected AbstractSVGPathSegList() {
    }
    
    @Override
    protected String getItemSeparator() {
        return " ";
    }
    
    protected abstract SVGException createSVGException(final short p0, final String p1, final Object[] p2);
    
    @Override
    public SVGPathSeg initialize(final SVGPathSeg newItem) throws DOMException, SVGException {
        return (SVGPathSeg)this.initializeImpl(newItem);
    }
    
    @Override
    public SVGPathSeg getItem(final int index) throws DOMException {
        return (SVGPathSeg)this.getItemImpl(index);
    }
    
    @Override
    public SVGPathSeg insertItemBefore(final SVGPathSeg newItem, final int index) throws DOMException, SVGException {
        return (SVGPathSeg)this.insertItemBeforeImpl(newItem, index);
    }
    
    @Override
    public SVGPathSeg replaceItem(final SVGPathSeg newItem, final int index) throws DOMException, SVGException {
        return (SVGPathSeg)this.replaceItemImpl(newItem, index);
    }
    
    @Override
    public SVGPathSeg removeItem(final int index) throws DOMException {
        return (SVGPathSeg)this.removeItemImpl(index);
    }
    
    @Override
    public SVGPathSeg appendItem(final SVGPathSeg newItem) throws DOMException, SVGException {
        return (SVGPathSeg)this.appendItemImpl(newItem);
    }
    
    @Override
    protected SVGItem createSVGItem(final Object newItem) {
        final SVGPathSeg pathSeg = (SVGPathSeg)newItem;
        return this.createPathSegItem(pathSeg);
    }
    
    @Override
    protected void doParse(final String value, final ListHandler handler) throws ParseException {
        final PathParser pathParser = new PathParser();
        final PathSegListBuilder builder = new PathSegListBuilder(handler);
        pathParser.setPathHandler(builder);
        pathParser.parse(value);
    }
    
    @Override
    protected void checkItemType(final Object newItem) {
        if (!(newItem instanceof SVGPathSeg)) {
            this.createSVGException((short)0, "expected SVGPathSeg", null);
        }
    }
    
    protected SVGPathSegItem createPathSegItem(final SVGPathSeg pathSeg) {
        SVGPathSegItem pathSegItem = null;
        final short type = pathSeg.getPathSegType();
        switch (type) {
            case 10:
            case 11: {
                pathSegItem = new SVGPathSegArcItem(pathSeg);
                break;
            }
            case 1: {
                pathSegItem = new SVGPathSegItem(pathSeg);
                break;
            }
            case 6:
            case 7: {
                pathSegItem = new SVGPathSegCurvetoCubicItem(pathSeg);
                break;
            }
            case 16:
            case 17: {
                pathSegItem = new SVGPathSegCurvetoCubicSmoothItem(pathSeg);
                break;
            }
            case 8:
            case 9: {
                pathSegItem = new SVGPathSegCurvetoQuadraticItem(pathSeg);
                break;
            }
            case 18:
            case 19: {
                pathSegItem = new SVGPathSegCurvetoQuadraticSmoothItem(pathSeg);
                break;
            }
            case 2:
            case 3:
            case 4:
            case 5: {
                pathSegItem = new SVGPathSegMovetoLinetoItem(pathSeg);
                break;
            }
            case 12:
            case 13: {
                pathSegItem = new SVGPathSegLinetoHorizontalItem(pathSeg);
                break;
            }
            case 14:
            case 15: {
                pathSegItem = new SVGPathSegLinetoVerticalItem(pathSeg);
                break;
            }
        }
        return pathSegItem;
    }
    
    public static class SVGPathSegMovetoLinetoItem extends SVGPathSegItem implements SVGPathSegMovetoAbs, SVGPathSegMovetoRel, SVGPathSegLinetoAbs, SVGPathSegLinetoRel
    {
        public SVGPathSegMovetoLinetoItem(final short type, final String letter, final float x, final float y) {
            super(type, letter);
            this.setX(x);
            this.setY(y);
        }
        
        public SVGPathSegMovetoLinetoItem(final SVGPathSeg pathSeg) {
            switch (this.type = pathSeg.getPathSegType()) {
                case 5: {
                    this.letter = "l";
                    this.setX(((SVGPathSegLinetoRel)pathSeg).getX());
                    this.setY(((SVGPathSegLinetoRel)pathSeg).getY());
                    break;
                }
                case 4: {
                    this.letter = "L";
                    this.setX(((SVGPathSegLinetoAbs)pathSeg).getX());
                    this.setY(((SVGPathSegLinetoAbs)pathSeg).getY());
                    break;
                }
                case 3: {
                    this.letter = "m";
                    this.setX(((SVGPathSegMovetoRel)pathSeg).getX());
                    this.setY(((SVGPathSegMovetoRel)pathSeg).getY());
                    break;
                }
                case 2: {
                    this.letter = "M";
                    this.setX(((SVGPathSegMovetoAbs)pathSeg).getX());
                    this.setY(((SVGPathSegMovetoAbs)pathSeg).getY());
                    break;
                }
            }
        }
        
        @Override
        public void setX(final float x) {
            super.setX(x);
            this.resetAttribute();
        }
        
        @Override
        public void setY(final float y) {
            super.setY(y);
            this.resetAttribute();
        }
        
        @Override
        protected String getStringValue() {
            return this.letter + ' ' + Float.toString(this.getX()) + ' ' + Float.toString(this.getY());
        }
    }
    
    public static class SVGPathSegCurvetoCubicItem extends SVGPathSegItem implements SVGPathSegCurvetoCubicAbs, SVGPathSegCurvetoCubicRel
    {
        public SVGPathSegCurvetoCubicItem(final short type, final String letter, final float x1, final float y1, final float x2, final float y2, final float x, final float y) {
            super(type, letter);
            this.setX(x);
            this.setY(y);
            this.setX1(x1);
            this.setY1(y1);
            this.setX2(x2);
            this.setY2(y2);
        }
        
        public SVGPathSegCurvetoCubicItem(final SVGPathSeg pathSeg) {
            switch (this.type = pathSeg.getPathSegType()) {
                case 6: {
                    this.letter = "C";
                    this.setX(((SVGPathSegCurvetoCubicAbs)pathSeg).getX());
                    this.setY(((SVGPathSegCurvetoCubicAbs)pathSeg).getY());
                    this.setX1(((SVGPathSegCurvetoCubicAbs)pathSeg).getX1());
                    this.setY1(((SVGPathSegCurvetoCubicAbs)pathSeg).getY1());
                    this.setX2(((SVGPathSegCurvetoCubicAbs)pathSeg).getX2());
                    this.setY2(((SVGPathSegCurvetoCubicAbs)pathSeg).getY2());
                    break;
                }
                case 7: {
                    this.letter = "c";
                    this.setX(((SVGPathSegCurvetoCubicRel)pathSeg).getX());
                    this.setY(((SVGPathSegCurvetoCubicRel)pathSeg).getY());
                    this.setX1(((SVGPathSegCurvetoCubicRel)pathSeg).getX1());
                    this.setY1(((SVGPathSegCurvetoCubicRel)pathSeg).getY1());
                    this.setX2(((SVGPathSegCurvetoCubicRel)pathSeg).getX2());
                    this.setY2(((SVGPathSegCurvetoCubicRel)pathSeg).getY2());
                    break;
                }
            }
        }
        
        @Override
        public void setX(final float x) {
            super.setX(x);
            this.resetAttribute();
        }
        
        @Override
        public void setY(final float y) {
            super.setY(y);
            this.resetAttribute();
        }
        
        @Override
        public void setX1(final float x1) {
            super.setX1(x1);
            this.resetAttribute();
        }
        
        @Override
        public void setY1(final float y1) {
            super.setY1(y1);
            this.resetAttribute();
        }
        
        @Override
        public void setX2(final float x2) {
            super.setX2(x2);
            this.resetAttribute();
        }
        
        @Override
        public void setY2(final float y2) {
            super.setY2(y2);
            this.resetAttribute();
        }
        
        @Override
        protected String getStringValue() {
            return this.letter + ' ' + Float.toString(this.getX1()) + ' ' + Float.toString(this.getY1()) + ' ' + Float.toString(this.getX2()) + ' ' + Float.toString(this.getY2()) + ' ' + Float.toString(this.getX()) + ' ' + Float.toString(this.getY());
        }
    }
    
    public static class SVGPathSegCurvetoQuadraticItem extends SVGPathSegItem implements SVGPathSegCurvetoQuadraticAbs, SVGPathSegCurvetoQuadraticRel
    {
        public SVGPathSegCurvetoQuadraticItem(final short type, final String letter, final float x1, final float y1, final float x, final float y) {
            super(type, letter);
            this.setX(x);
            this.setY(y);
            this.setX1(x1);
            this.setY1(y1);
        }
        
        public SVGPathSegCurvetoQuadraticItem(final SVGPathSeg pathSeg) {
            switch (this.type = pathSeg.getPathSegType()) {
                case 8: {
                    this.letter = "Q";
                    this.setX(((SVGPathSegCurvetoQuadraticAbs)pathSeg).getX());
                    this.setY(((SVGPathSegCurvetoQuadraticAbs)pathSeg).getY());
                    this.setX1(((SVGPathSegCurvetoQuadraticAbs)pathSeg).getX1());
                    this.setY1(((SVGPathSegCurvetoQuadraticAbs)pathSeg).getY1());
                    break;
                }
                case 9: {
                    this.letter = "q";
                    this.setX(((SVGPathSegCurvetoQuadraticRel)pathSeg).getX());
                    this.setY(((SVGPathSegCurvetoQuadraticRel)pathSeg).getY());
                    this.setX1(((SVGPathSegCurvetoQuadraticRel)pathSeg).getX1());
                    this.setY1(((SVGPathSegCurvetoQuadraticRel)pathSeg).getY1());
                    break;
                }
            }
        }
        
        @Override
        public void setX(final float x) {
            super.setX(x);
            this.resetAttribute();
        }
        
        @Override
        public void setY(final float y) {
            super.setY(y);
            this.resetAttribute();
        }
        
        @Override
        public void setX1(final float x1) {
            super.setX1(x1);
            this.resetAttribute();
        }
        
        @Override
        public void setY1(final float y1) {
            super.setY1(y1);
            this.resetAttribute();
        }
        
        @Override
        protected String getStringValue() {
            return this.letter + ' ' + Float.toString(this.getX1()) + ' ' + Float.toString(this.getY1()) + ' ' + Float.toString(this.getX()) + ' ' + Float.toString(this.getY());
        }
    }
    
    public static class SVGPathSegArcItem extends SVGPathSegItem implements SVGPathSegArcAbs, SVGPathSegArcRel
    {
        public SVGPathSegArcItem(final short type, final String letter, final float r1, final float r2, final float angle, final boolean largeArcFlag, final boolean sweepFlag, final float x, final float y) {
            super(type, letter);
            this.setX(x);
            this.setY(y);
            this.setR1(r1);
            this.setR2(r2);
            this.setAngle(angle);
            this.setLargeArcFlag(largeArcFlag);
            this.setSweepFlag(sweepFlag);
        }
        
        public SVGPathSegArcItem(final SVGPathSeg pathSeg) {
            switch (this.type = pathSeg.getPathSegType()) {
                case 10: {
                    this.letter = "A";
                    this.setX(((SVGPathSegArcAbs)pathSeg).getX());
                    this.setY(((SVGPathSegArcAbs)pathSeg).getY());
                    this.setR1(((SVGPathSegArcAbs)pathSeg).getR1());
                    this.setR2(((SVGPathSegArcAbs)pathSeg).getR2());
                    this.setAngle(((SVGPathSegArcAbs)pathSeg).getAngle());
                    this.setLargeArcFlag(((SVGPathSegArcAbs)pathSeg).getLargeArcFlag());
                    this.setSweepFlag(((SVGPathSegArcAbs)pathSeg).getSweepFlag());
                    break;
                }
                case 11: {
                    this.letter = "a";
                    this.setX(((SVGPathSegArcRel)pathSeg).getX());
                    this.setY(((SVGPathSegArcRel)pathSeg).getY());
                    this.setR1(((SVGPathSegArcRel)pathSeg).getR1());
                    this.setR2(((SVGPathSegArcRel)pathSeg).getR2());
                    this.setAngle(((SVGPathSegArcRel)pathSeg).getAngle());
                    this.setLargeArcFlag(((SVGPathSegArcRel)pathSeg).getLargeArcFlag());
                    this.setSweepFlag(((SVGPathSegArcRel)pathSeg).getSweepFlag());
                    break;
                }
            }
        }
        
        @Override
        public void setX(final float x) {
            super.setX(x);
            this.resetAttribute();
        }
        
        @Override
        public void setY(final float y) {
            super.setY(y);
            this.resetAttribute();
        }
        
        @Override
        public void setR1(final float r1) {
            super.setR1(r1);
            this.resetAttribute();
        }
        
        @Override
        public void setR2(final float r2) {
            super.setR2(r2);
            this.resetAttribute();
        }
        
        @Override
        public void setAngle(final float angle) {
            super.setAngle(angle);
            this.resetAttribute();
        }
        
        @Override
        public boolean getSweepFlag() {
            return this.isSweepFlag();
        }
        
        @Override
        public void setSweepFlag(final boolean sweepFlag) {
            super.setSweepFlag(sweepFlag);
            this.resetAttribute();
        }
        
        @Override
        public boolean getLargeArcFlag() {
            return this.isLargeArcFlag();
        }
        
        @Override
        public void setLargeArcFlag(final boolean largeArcFlag) {
            super.setLargeArcFlag(largeArcFlag);
            this.resetAttribute();
        }
        
        @Override
        protected String getStringValue() {
            return this.letter + ' ' + Float.toString(this.getR1()) + ' ' + Float.toString(this.getR2()) + ' ' + Float.toString(this.getAngle()) + ' ' + (this.isLargeArcFlag() ? "1" : "0") + ' ' + (this.isSweepFlag() ? "1" : "0") + ' ' + Float.toString(this.getX()) + ' ' + Float.toString(this.getY());
        }
    }
    
    public static class SVGPathSegLinetoHorizontalItem extends SVGPathSegItem implements SVGPathSegLinetoHorizontalAbs, SVGPathSegLinetoHorizontalRel
    {
        public SVGPathSegLinetoHorizontalItem(final short type, final String letter, final float value) {
            super(type, letter);
            this.setX(value);
        }
        
        public SVGPathSegLinetoHorizontalItem(final SVGPathSeg pathSeg) {
            switch (this.type = pathSeg.getPathSegType()) {
                case 12: {
                    this.letter = "H";
                    this.setX(((SVGPathSegLinetoHorizontalAbs)pathSeg).getX());
                    break;
                }
                case 13: {
                    this.letter = "h";
                    this.setX(((SVGPathSegLinetoHorizontalRel)pathSeg).getX());
                    break;
                }
            }
        }
        
        @Override
        public void setX(final float x) {
            super.setX(x);
            this.resetAttribute();
        }
        
        @Override
        protected String getStringValue() {
            return this.letter + ' ' + Float.toString(this.getX());
        }
    }
    
    public static class SVGPathSegLinetoVerticalItem extends SVGPathSegItem implements SVGPathSegLinetoVerticalAbs, SVGPathSegLinetoVerticalRel
    {
        public SVGPathSegLinetoVerticalItem(final short type, final String letter, final float value) {
            super(type, letter);
            this.setY(value);
        }
        
        public SVGPathSegLinetoVerticalItem(final SVGPathSeg pathSeg) {
            switch (this.type = pathSeg.getPathSegType()) {
                case 14: {
                    this.letter = "V";
                    this.setY(((SVGPathSegLinetoVerticalAbs)pathSeg).getY());
                    break;
                }
                case 15: {
                    this.letter = "v";
                    this.setY(((SVGPathSegLinetoVerticalRel)pathSeg).getY());
                    break;
                }
            }
        }
        
        @Override
        public void setY(final float y) {
            super.setY(y);
            this.resetAttribute();
        }
        
        @Override
        protected String getStringValue() {
            return this.letter + ' ' + Float.toString(this.getY());
        }
    }
    
    public static class SVGPathSegCurvetoCubicSmoothItem extends SVGPathSegItem implements SVGPathSegCurvetoCubicSmoothAbs, SVGPathSegCurvetoCubicSmoothRel
    {
        public SVGPathSegCurvetoCubicSmoothItem(final short type, final String letter, final float x2, final float y2, final float x, final float y) {
            super(type, letter);
            this.setX(x);
            this.setY(y);
            this.setX2(x2);
            this.setY2(y2);
        }
        
        public SVGPathSegCurvetoCubicSmoothItem(final SVGPathSeg pathSeg) {
            switch (this.type = pathSeg.getPathSegType()) {
                case 16: {
                    this.letter = "S";
                    this.setX(((SVGPathSegCurvetoCubicSmoothAbs)pathSeg).getX());
                    this.setY(((SVGPathSegCurvetoCubicSmoothAbs)pathSeg).getY());
                    this.setX2(((SVGPathSegCurvetoCubicSmoothAbs)pathSeg).getX2());
                    this.setY2(((SVGPathSegCurvetoCubicSmoothAbs)pathSeg).getY2());
                    break;
                }
                case 17: {
                    this.letter = "s";
                    this.setX(((SVGPathSegCurvetoCubicSmoothRel)pathSeg).getX());
                    this.setY(((SVGPathSegCurvetoCubicSmoothRel)pathSeg).getY());
                    this.setX2(((SVGPathSegCurvetoCubicSmoothRel)pathSeg).getX2());
                    this.setY2(((SVGPathSegCurvetoCubicSmoothRel)pathSeg).getY2());
                    break;
                }
            }
        }
        
        @Override
        public void setX(final float x) {
            super.setX(x);
            this.resetAttribute();
        }
        
        @Override
        public void setY(final float y) {
            super.setY(y);
            this.resetAttribute();
        }
        
        @Override
        public void setX2(final float x2) {
            super.setX2(x2);
            this.resetAttribute();
        }
        
        @Override
        public void setY2(final float y2) {
            super.setY2(y2);
            this.resetAttribute();
        }
        
        @Override
        protected String getStringValue() {
            return this.letter + ' ' + Float.toString(this.getX2()) + ' ' + Float.toString(this.getY2()) + ' ' + Float.toString(this.getX()) + ' ' + Float.toString(this.getY());
        }
    }
    
    public static class SVGPathSegCurvetoQuadraticSmoothItem extends SVGPathSegItem implements SVGPathSegCurvetoQuadraticSmoothAbs, SVGPathSegCurvetoQuadraticSmoothRel
    {
        public SVGPathSegCurvetoQuadraticSmoothItem(final short type, final String letter, final float x, final float y) {
            super(type, letter);
            this.setX(x);
            this.setY(y);
        }
        
        public SVGPathSegCurvetoQuadraticSmoothItem(final SVGPathSeg pathSeg) {
            switch (this.type = pathSeg.getPathSegType()) {
                case 18: {
                    this.letter = "T";
                    this.setX(((SVGPathSegCurvetoQuadraticSmoothAbs)pathSeg).getX());
                    this.setY(((SVGPathSegCurvetoQuadraticSmoothAbs)pathSeg).getY());
                    break;
                }
                case 19: {
                    this.letter = "t";
                    this.setX(((SVGPathSegCurvetoQuadraticSmoothRel)pathSeg).getX());
                    this.setY(((SVGPathSegCurvetoQuadraticSmoothRel)pathSeg).getY());
                    break;
                }
            }
        }
        
        @Override
        public void setX(final float x) {
            super.setX(x);
            this.resetAttribute();
        }
        
        @Override
        public void setY(final float y) {
            super.setY(y);
            this.resetAttribute();
        }
        
        @Override
        protected String getStringValue() {
            return this.letter + ' ' + Float.toString(this.getX()) + ' ' + Float.toString(this.getY());
        }
    }
    
    protected static class PathSegListBuilder extends DefaultPathHandler
    {
        protected ListHandler listHandler;
        
        public PathSegListBuilder(final ListHandler listHandler) {
            this.listHandler = listHandler;
        }
        
        @Override
        public void startPath() throws ParseException {
            this.listHandler.startList();
        }
        
        @Override
        public void endPath() throws ParseException {
            this.listHandler.endList();
        }
        
        @Override
        public void movetoRel(final float x, final float y) throws ParseException {
            this.listHandler.item(new SVGPathSegMovetoLinetoItem((short)3, "m", x, y));
        }
        
        @Override
        public void movetoAbs(final float x, final float y) throws ParseException {
            this.listHandler.item(new SVGPathSegMovetoLinetoItem((short)2, "M", x, y));
        }
        
        @Override
        public void closePath() throws ParseException {
            this.listHandler.item(new SVGPathSegItem((short)1, "z"));
        }
        
        @Override
        public void linetoRel(final float x, final float y) throws ParseException {
            this.listHandler.item(new SVGPathSegMovetoLinetoItem((short)5, "l", x, y));
        }
        
        @Override
        public void linetoAbs(final float x, final float y) throws ParseException {
            this.listHandler.item(new SVGPathSegMovetoLinetoItem((short)4, "L", x, y));
        }
        
        @Override
        public void linetoHorizontalRel(final float x) throws ParseException {
            this.listHandler.item(new SVGPathSegLinetoHorizontalItem((short)13, "h", x));
        }
        
        @Override
        public void linetoHorizontalAbs(final float x) throws ParseException {
            this.listHandler.item(new SVGPathSegLinetoHorizontalItem((short)12, "H", x));
        }
        
        @Override
        public void linetoVerticalRel(final float y) throws ParseException {
            this.listHandler.item(new SVGPathSegLinetoVerticalItem((short)15, "v", y));
        }
        
        @Override
        public void linetoVerticalAbs(final float y) throws ParseException {
            this.listHandler.item(new SVGPathSegLinetoVerticalItem((short)14, "V", y));
        }
        
        @Override
        public void curvetoCubicRel(final float x1, final float y1, final float x2, final float y2, final float x, final float y) throws ParseException {
            this.listHandler.item(new SVGPathSegCurvetoCubicItem((short)7, "c", x1, y1, x2, y2, x, y));
        }
        
        @Override
        public void curvetoCubicAbs(final float x1, final float y1, final float x2, final float y2, final float x, final float y) throws ParseException {
            this.listHandler.item(new SVGPathSegCurvetoCubicItem((short)6, "C", x1, y1, x2, y2, x, y));
        }
        
        @Override
        public void curvetoCubicSmoothRel(final float x2, final float y2, final float x, final float y) throws ParseException {
            this.listHandler.item(new SVGPathSegCurvetoCubicSmoothItem((short)17, "s", x2, y2, x, y));
        }
        
        @Override
        public void curvetoCubicSmoothAbs(final float x2, final float y2, final float x, final float y) throws ParseException {
            this.listHandler.item(new SVGPathSegCurvetoCubicSmoothItem((short)16, "S", x2, y2, x, y));
        }
        
        @Override
        public void curvetoQuadraticRel(final float x1, final float y1, final float x, final float y) throws ParseException {
            this.listHandler.item(new SVGPathSegCurvetoQuadraticItem((short)9, "q", x1, y1, x, y));
        }
        
        @Override
        public void curvetoQuadraticAbs(final float x1, final float y1, final float x, final float y) throws ParseException {
            this.listHandler.item(new SVGPathSegCurvetoQuadraticItem((short)8, "Q", x1, y1, x, y));
        }
        
        @Override
        public void curvetoQuadraticSmoothRel(final float x, final float y) throws ParseException {
            this.listHandler.item(new SVGPathSegCurvetoQuadraticSmoothItem((short)19, "t", x, y));
        }
        
        @Override
        public void curvetoQuadraticSmoothAbs(final float x, final float y) throws ParseException {
            this.listHandler.item(new SVGPathSegCurvetoQuadraticSmoothItem((short)18, "T", x, y));
        }
        
        @Override
        public void arcRel(final float rx, final float ry, final float xAxisRotation, final boolean largeArcFlag, final boolean sweepFlag, final float x, final float y) throws ParseException {
            this.listHandler.item(new SVGPathSegArcItem((short)11, "a", rx, ry, xAxisRotation, largeArcFlag, sweepFlag, x, y));
        }
        
        @Override
        public void arcAbs(final float rx, final float ry, final float xAxisRotation, final boolean largeArcFlag, final boolean sweepFlag, final float x, final float y) throws ParseException {
            this.listHandler.item(new SVGPathSegArcItem((short)10, "A", rx, ry, xAxisRotation, largeArcFlag, sweepFlag, x, y));
        }
    }
}
