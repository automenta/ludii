// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGPathSegArcRel;
import org.w3c.dom.svg.SVGPathSegArcAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothRel;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothRel;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticRel;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicRel;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs;
import org.w3c.dom.svg.SVGPathSegLinetoVerticalRel;
import org.w3c.dom.svg.SVGPathSegLinetoVerticalAbs;
import org.w3c.dom.svg.SVGPathSegLinetoHorizontalRel;
import org.w3c.dom.svg.SVGPathSegLinetoHorizontalAbs;
import org.w3c.dom.svg.SVGPathSegLinetoRel;
import org.w3c.dom.svg.SVGPathSegLinetoAbs;
import org.w3c.dom.svg.SVGPathSegMovetoRel;
import org.w3c.dom.svg.SVGPathSegMovetoAbs;
import org.w3c.dom.svg.SVGPathSegClosePath;
import org.w3c.dom.svg.SVGPathSegList;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.apache.batik.dom.svg.SVGPathSegConstants;
import org.w3c.dom.svg.SVGPathElement;

public class SVGOMPathElement extends SVGGraphicsElement implements SVGPathElement, SVGPathSegConstants
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedPathData d;
    
    protected SVGOMPathElement() {
    }
    
    public SVGOMPathElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.d = this.createLiveAnimatedPathData(null, "d", "");
    }
    
    @Override
    public String getLocalName() {
        return "path";
    }
    
    @Override
    public SVGAnimatedNumber getPathLength() {
        throw new UnsupportedOperationException("SVGPathElement.getPathLength is not implemented");
    }
    
    @Override
    public float getTotalLength() {
        return SVGPathSupport.getTotalLength(this);
    }
    
    @Override
    public SVGPoint getPointAtLength(final float distance) {
        return SVGPathSupport.getPointAtLength(this, distance);
    }
    
    @Override
    public int getPathSegAtLength(final float distance) {
        return SVGPathSupport.getPathSegAtLength(this, distance);
    }
    
    public SVGOMAnimatedPathData getAnimatedPathData() {
        return this.d;
    }
    
    @Override
    public SVGPathSegList getPathSegList() {
        return this.d.getPathSegList();
    }
    
    @Override
    public SVGPathSegList getNormalizedPathSegList() {
        return this.d.getNormalizedPathSegList();
    }
    
    @Override
    public SVGPathSegList getAnimatedPathSegList() {
        return this.d.getAnimatedPathSegList();
    }
    
    @Override
    public SVGPathSegList getAnimatedNormalizedPathSegList() {
        return this.d.getAnimatedNormalizedPathSegList();
    }
    
    @Override
    public SVGPathSegClosePath createSVGPathSegClosePath() {
        return new SVGPathSegClosePath() {
            @Override
            public short getPathSegType() {
                return 1;
            }
            
            @Override
            public String getPathSegTypeAsLetter() {
                return "z";
            }
        };
    }
    
    @Override
    public SVGPathSegMovetoAbs createSVGPathSegMovetoAbs(final float x_value, final float y_value) {
        return new SVGPathSegMovetoAbs() {
            protected float x = x_value;
            protected float y = y_value;
            
            @Override
            public short getPathSegType() {
                return 2;
            }
            
            @Override
            public String getPathSegTypeAsLetter() {
                return "M";
            }
            
            @Override
            public float getX() {
                return this.x;
            }
            
            @Override
            public void setX(final float x) {
                this.x = x;
            }
            
            @Override
            public float getY() {
                return this.y;
            }
            
            @Override
            public void setY(final float y) {
                this.y = y;
            }
        };
    }
    
    @Override
    public SVGPathSegMovetoRel createSVGPathSegMovetoRel(final float x_value, final float y_value) {
        return new SVGPathSegMovetoRel() {
            protected float x = x_value;
            protected float y = y_value;
            
            @Override
            public short getPathSegType() {
                return 3;
            }
            
            @Override
            public String getPathSegTypeAsLetter() {
                return "m";
            }
            
            @Override
            public float getX() {
                return this.x;
            }
            
            @Override
            public void setX(final float x) {
                this.x = x;
            }
            
            @Override
            public float getY() {
                return this.y;
            }
            
            @Override
            public void setY(final float y) {
                this.y = y;
            }
        };
    }
    
    @Override
    public SVGPathSegLinetoAbs createSVGPathSegLinetoAbs(final float x_value, final float y_value) {
        return new SVGPathSegLinetoAbs() {
            protected float x = x_value;
            protected float y = y_value;
            
            @Override
            public short getPathSegType() {
                return 4;
            }
            
            @Override
            public String getPathSegTypeAsLetter() {
                return "L";
            }
            
            @Override
            public float getX() {
                return this.x;
            }
            
            @Override
            public void setX(final float x) {
                this.x = x;
            }
            
            @Override
            public float getY() {
                return this.y;
            }
            
            @Override
            public void setY(final float y) {
                this.y = y;
            }
        };
    }
    
    @Override
    public SVGPathSegLinetoRel createSVGPathSegLinetoRel(final float x_value, final float y_value) {
        return new SVGPathSegLinetoRel() {
            protected float x = x_value;
            protected float y = y_value;
            
            @Override
            public short getPathSegType() {
                return 5;
            }
            
            @Override
            public String getPathSegTypeAsLetter() {
                return "l";
            }
            
            @Override
            public float getX() {
                return this.x;
            }
            
            @Override
            public void setX(final float x) {
                this.x = x;
            }
            
            @Override
            public float getY() {
                return this.y;
            }
            
            @Override
            public void setY(final float y) {
                this.y = y;
            }
        };
    }
    
    @Override
    public SVGPathSegLinetoHorizontalAbs createSVGPathSegLinetoHorizontalAbs(final float x_value) {
        return new SVGPathSegLinetoHorizontalAbs() {
            protected float x = x_value;
            
            @Override
            public short getPathSegType() {
                return 12;
            }
            
            @Override
            public String getPathSegTypeAsLetter() {
                return "H";
            }
            
            @Override
            public float getX() {
                return this.x;
            }
            
            @Override
            public void setX(final float x) {
                this.x = x;
            }
        };
    }
    
    @Override
    public SVGPathSegLinetoHorizontalRel createSVGPathSegLinetoHorizontalRel(final float x_value) {
        return new SVGPathSegLinetoHorizontalRel() {
            protected float x = x_value;
            
            @Override
            public short getPathSegType() {
                return 13;
            }
            
            @Override
            public String getPathSegTypeAsLetter() {
                return "h";
            }
            
            @Override
            public float getX() {
                return this.x;
            }
            
            @Override
            public void setX(final float x) {
                this.x = x;
            }
        };
    }
    
    @Override
    public SVGPathSegLinetoVerticalAbs createSVGPathSegLinetoVerticalAbs(final float y_value) {
        return new SVGPathSegLinetoVerticalAbs() {
            protected float y = y_value;
            
            @Override
            public short getPathSegType() {
                return 14;
            }
            
            @Override
            public String getPathSegTypeAsLetter() {
                return "V";
            }
            
            @Override
            public float getY() {
                return this.y;
            }
            
            @Override
            public void setY(final float y) {
                this.y = y;
            }
        };
    }
    
    @Override
    public SVGPathSegLinetoVerticalRel createSVGPathSegLinetoVerticalRel(final float y_value) {
        return new SVGPathSegLinetoVerticalRel() {
            protected float y = y_value;
            
            @Override
            public short getPathSegType() {
                return 15;
            }
            
            @Override
            public String getPathSegTypeAsLetter() {
                return "v";
            }
            
            @Override
            public float getY() {
                return this.y;
            }
            
            @Override
            public void setY(final float y) {
                this.y = y;
            }
        };
    }
    
    @Override
    public SVGPathSegCurvetoCubicAbs createSVGPathSegCurvetoCubicAbs(final float x_value, final float y_value, final float x1_value, final float y1_value, final float x2_value, final float y2_value) {
        return new SVGPathSegCurvetoCubicAbs() {
            protected float x = x_value;
            protected float y = y_value;
            protected float x1 = x1_value;
            protected float y1 = y1_value;
            protected float x2 = x2_value;
            protected float y2 = y2_value;
            
            @Override
            public short getPathSegType() {
                return 6;
            }
            
            @Override
            public String getPathSegTypeAsLetter() {
                return "C";
            }
            
            @Override
            public float getX() {
                return this.x;
            }
            
            @Override
            public void setX(final float x) {
                this.x = x;
            }
            
            @Override
            public float getY() {
                return this.y;
            }
            
            @Override
            public void setY(final float y) {
                this.y = y;
            }
            
            @Override
            public float getX1() {
                return this.x1;
            }
            
            @Override
            public void setX1(final float x1) {
                this.x1 = x1;
            }
            
            @Override
            public float getY1() {
                return this.y1;
            }
            
            @Override
            public void setY1(final float y1) {
                this.y1 = y1;
            }
            
            @Override
            public float getX2() {
                return this.x2;
            }
            
            @Override
            public void setX2(final float x2) {
                this.x2 = x2;
            }
            
            @Override
            public float getY2() {
                return this.y2;
            }
            
            @Override
            public void setY2(final float y2) {
                this.y2 = y2;
            }
        };
    }
    
    @Override
    public SVGPathSegCurvetoCubicRel createSVGPathSegCurvetoCubicRel(final float x_value, final float y_value, final float x1_value, final float y1_value, final float x2_value, final float y2_value) {
        return new SVGPathSegCurvetoCubicRel() {
            protected float x = x_value;
            protected float y = y_value;
            protected float x1 = x1_value;
            protected float y1 = y1_value;
            protected float x2 = x2_value;
            protected float y2 = y2_value;
            
            @Override
            public short getPathSegType() {
                return 7;
            }
            
            @Override
            public String getPathSegTypeAsLetter() {
                return "c";
            }
            
            @Override
            public float getX() {
                return this.x;
            }
            
            @Override
            public void setX(final float x) {
                this.x = x;
            }
            
            @Override
            public float getY() {
                return this.y;
            }
            
            @Override
            public void setY(final float y) {
                this.y = y;
            }
            
            @Override
            public float getX1() {
                return this.x1;
            }
            
            @Override
            public void setX1(final float x1) {
                this.x1 = x1;
            }
            
            @Override
            public float getY1() {
                return this.y1;
            }
            
            @Override
            public void setY1(final float y1) {
                this.y1 = y1;
            }
            
            @Override
            public float getX2() {
                return this.x2;
            }
            
            @Override
            public void setX2(final float x2) {
                this.x2 = x2;
            }
            
            @Override
            public float getY2() {
                return this.y2;
            }
            
            @Override
            public void setY2(final float y2) {
                this.y2 = y2;
            }
        };
    }
    
    @Override
    public SVGPathSegCurvetoQuadraticAbs createSVGPathSegCurvetoQuadraticAbs(final float x_value, final float y_value, final float x1_value, final float y1_value) {
        return new SVGPathSegCurvetoQuadraticAbs() {
            protected float x = x_value;
            protected float y = y_value;
            protected float x1 = x1_value;
            protected float y1 = y1_value;
            
            @Override
            public short getPathSegType() {
                return 8;
            }
            
            @Override
            public String getPathSegTypeAsLetter() {
                return "Q";
            }
            
            @Override
            public float getX() {
                return this.x;
            }
            
            @Override
            public void setX(final float x) {
                this.x = x;
            }
            
            @Override
            public float getY() {
                return this.y;
            }
            
            @Override
            public void setY(final float y) {
                this.y = y;
            }
            
            @Override
            public float getX1() {
                return this.x1;
            }
            
            @Override
            public void setX1(final float x1) {
                this.x1 = x1;
            }
            
            @Override
            public float getY1() {
                return this.y1;
            }
            
            @Override
            public void setY1(final float y1) {
                this.y1 = y1;
            }
        };
    }
    
    @Override
    public SVGPathSegCurvetoQuadraticRel createSVGPathSegCurvetoQuadraticRel(final float x_value, final float y_value, final float x1_value, final float y1_value) {
        return new SVGPathSegCurvetoQuadraticRel() {
            protected float x = x_value;
            protected float y = y_value;
            protected float x1 = x1_value;
            protected float y1 = y1_value;
            
            @Override
            public short getPathSegType() {
                return 9;
            }
            
            @Override
            public String getPathSegTypeAsLetter() {
                return "q";
            }
            
            @Override
            public float getX() {
                return this.x;
            }
            
            @Override
            public void setX(final float x) {
                this.x = x;
            }
            
            @Override
            public float getY() {
                return this.y;
            }
            
            @Override
            public void setY(final float y) {
                this.y = y;
            }
            
            @Override
            public float getX1() {
                return this.x1;
            }
            
            @Override
            public void setX1(final float x1) {
                this.x1 = x1;
            }
            
            @Override
            public float getY1() {
                return this.y1;
            }
            
            @Override
            public void setY1(final float y1) {
                this.y1 = y1;
            }
        };
    }
    
    @Override
    public SVGPathSegCurvetoCubicSmoothAbs createSVGPathSegCurvetoCubicSmoothAbs(final float x_value, final float y_value, final float x2_value, final float y2_value) {
        return new SVGPathSegCurvetoCubicSmoothAbs() {
            protected float x = x_value;
            protected float y = y_value;
            protected float x2 = x2_value;
            protected float y2 = y2_value;
            
            @Override
            public short getPathSegType() {
                return 16;
            }
            
            @Override
            public String getPathSegTypeAsLetter() {
                return "S";
            }
            
            @Override
            public float getX() {
                return this.x;
            }
            
            @Override
            public void setX(final float x) {
                this.x = x;
            }
            
            @Override
            public float getY() {
                return this.y;
            }
            
            @Override
            public void setY(final float y) {
                this.y = y;
            }
            
            @Override
            public float getX2() {
                return this.x2;
            }
            
            @Override
            public void setX2(final float x2) {
                this.x2 = x2;
            }
            
            @Override
            public float getY2() {
                return this.y2;
            }
            
            @Override
            public void setY2(final float y2) {
                this.y2 = y2;
            }
        };
    }
    
    @Override
    public SVGPathSegCurvetoCubicSmoothRel createSVGPathSegCurvetoCubicSmoothRel(final float x_value, final float y_value, final float x2_value, final float y2_value) {
        return new SVGPathSegCurvetoCubicSmoothRel() {
            protected float x = x_value;
            protected float y = y_value;
            protected float x2 = x2_value;
            protected float y2 = y2_value;
            
            @Override
            public short getPathSegType() {
                return 17;
            }
            
            @Override
            public String getPathSegTypeAsLetter() {
                return "s";
            }
            
            @Override
            public float getX() {
                return this.x;
            }
            
            @Override
            public void setX(final float x) {
                this.x = x;
            }
            
            @Override
            public float getY() {
                return this.y;
            }
            
            @Override
            public void setY(final float y) {
                this.y = y;
            }
            
            @Override
            public float getX2() {
                return this.x2;
            }
            
            @Override
            public void setX2(final float x2) {
                this.x2 = x2;
            }
            
            @Override
            public float getY2() {
                return this.y2;
            }
            
            @Override
            public void setY2(final float y2) {
                this.y2 = y2;
            }
        };
    }
    
    @Override
    public SVGPathSegCurvetoQuadraticSmoothAbs createSVGPathSegCurvetoQuadraticSmoothAbs(final float x_value, final float y_value) {
        return new SVGPathSegCurvetoQuadraticSmoothAbs() {
            protected float x = x_value;
            protected float y = y_value;
            
            @Override
            public short getPathSegType() {
                return 18;
            }
            
            @Override
            public String getPathSegTypeAsLetter() {
                return "T";
            }
            
            @Override
            public float getX() {
                return this.x;
            }
            
            @Override
            public void setX(final float x) {
                this.x = x;
            }
            
            @Override
            public float getY() {
                return this.y;
            }
            
            @Override
            public void setY(final float y) {
                this.y = y;
            }
        };
    }
    
    @Override
    public SVGPathSegCurvetoQuadraticSmoothRel createSVGPathSegCurvetoQuadraticSmoothRel(final float x_value, final float y_value) {
        return new SVGPathSegCurvetoQuadraticSmoothRel() {
            protected float x = x_value;
            protected float y = y_value;
            
            @Override
            public short getPathSegType() {
                return 19;
            }
            
            @Override
            public String getPathSegTypeAsLetter() {
                return "t";
            }
            
            @Override
            public float getX() {
                return this.x;
            }
            
            @Override
            public void setX(final float x) {
                this.x = x;
            }
            
            @Override
            public float getY() {
                return this.y;
            }
            
            @Override
            public void setY(final float y) {
                this.y = y;
            }
        };
    }
    
    @Override
    public SVGPathSegArcAbs createSVGPathSegArcAbs(final float x_value, final float y_value, final float r1_value, final float r2_value, final float angle_value, final boolean largeArcFlag_value, final boolean sweepFlag_value) {
        return new SVGPathSegArcAbs() {
            protected float x = x_value;
            protected float y = y_value;
            protected float r1 = r1_value;
            protected float r2 = r2_value;
            protected float angle = angle_value;
            protected boolean largeArcFlag = largeArcFlag_value;
            protected boolean sweepFlag = sweepFlag_value;
            
            @Override
            public short getPathSegType() {
                return 10;
            }
            
            @Override
            public String getPathSegTypeAsLetter() {
                return "A";
            }
            
            @Override
            public float getX() {
                return this.x;
            }
            
            @Override
            public void setX(final float x) {
                this.x = x;
            }
            
            @Override
            public float getY() {
                return this.y;
            }
            
            @Override
            public void setY(final float y) {
                this.y = y;
            }
            
            @Override
            public float getR1() {
                return this.r1;
            }
            
            @Override
            public void setR1(final float r1) {
                this.r1 = r1;
            }
            
            @Override
            public float getR2() {
                return this.r2;
            }
            
            @Override
            public void setR2(final float r2) {
                this.r2 = r2;
            }
            
            @Override
            public float getAngle() {
                return this.angle;
            }
            
            @Override
            public void setAngle(final float angle) {
                this.angle = angle;
            }
            
            @Override
            public boolean getLargeArcFlag() {
                return this.largeArcFlag;
            }
            
            @Override
            public void setLargeArcFlag(final boolean largeArcFlag) {
                this.largeArcFlag = largeArcFlag;
            }
            
            @Override
            public boolean getSweepFlag() {
                return this.sweepFlag;
            }
            
            @Override
            public void setSweepFlag(final boolean sweepFlag) {
                this.sweepFlag = sweepFlag;
            }
        };
    }
    
    @Override
    public SVGPathSegArcRel createSVGPathSegArcRel(final float x_value, final float y_value, final float r1_value, final float r2_value, final float angle_value, final boolean largeArcFlag_value, final boolean sweepFlag_value) {
        return new SVGPathSegArcRel() {
            protected float x = x_value;
            protected float y = y_value;
            protected float r1 = r1_value;
            protected float r2 = r2_value;
            protected float angle = angle_value;
            protected boolean largeArcFlag = largeArcFlag_value;
            protected boolean sweepFlag = sweepFlag_value;
            
            @Override
            public short getPathSegType() {
                return 11;
            }
            
            @Override
            public String getPathSegTypeAsLetter() {
                return "a";
            }
            
            @Override
            public float getX() {
                return this.x;
            }
            
            @Override
            public void setX(final float x) {
                this.x = x;
            }
            
            @Override
            public float getY() {
                return this.y;
            }
            
            @Override
            public void setY(final float y) {
                this.y = y;
            }
            
            @Override
            public float getR1() {
                return this.r1;
            }
            
            @Override
            public void setR1(final float r1) {
                this.r1 = r1;
            }
            
            @Override
            public float getR2() {
                return this.r2;
            }
            
            @Override
            public void setR2(final float r2) {
                this.r2 = r2;
            }
            
            @Override
            public float getAngle() {
                return this.angle;
            }
            
            @Override
            public void setAngle(final float angle) {
                this.angle = angle;
            }
            
            @Override
            public boolean getLargeArcFlag() {
                return this.largeArcFlag;
            }
            
            @Override
            public void setLargeArcFlag(final boolean largeArcFlag) {
                this.largeArcFlag = largeArcFlag;
            }
            
            @Override
            public boolean getSweepFlag() {
                return this.sweepFlag;
            }
            
            @Override
            public void setSweepFlag(final boolean sweepFlag) {
                this.sweepFlag = sweepFlag;
            }
        };
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMPathElement();
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGOMPathElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGGraphicsElement.xmlTraitInformation);
        t.put(null, "d", new TraitInformation(true, 22));
        t.put(null, "pathLength", new TraitInformation(true, 2));
        SVGOMPathElement.xmlTraitInformation = t;
    }
}
