// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.w3c.dom.svg.SVGPathSeg;
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
import org.apache.batik.parser.PathHandler;
import org.w3c.dom.svg.SVGPathSegList;

public abstract class SVGAnimatedPathDataSupport
{
    public static void handlePathSegList(final SVGPathSegList p, final PathHandler h) {
        final int n = p.getNumberOfItems();
        h.startPath();
        for (int i = 0; i < n; ++i) {
            final SVGPathSeg seg = p.getItem(i);
            switch (seg.getPathSegType()) {
                case 1: {
                    h.closePath();
                    break;
                }
                case 2: {
                    final SVGPathSegMovetoAbs s = (SVGPathSegMovetoAbs)seg;
                    h.movetoAbs(s.getX(), s.getY());
                    break;
                }
                case 3: {
                    final SVGPathSegMovetoRel s2 = (SVGPathSegMovetoRel)seg;
                    h.movetoRel(s2.getX(), s2.getY());
                    break;
                }
                case 4: {
                    final SVGPathSegLinetoAbs s3 = (SVGPathSegLinetoAbs)seg;
                    h.linetoAbs(s3.getX(), s3.getY());
                    break;
                }
                case 5: {
                    final SVGPathSegLinetoRel s4 = (SVGPathSegLinetoRel)seg;
                    h.linetoRel(s4.getX(), s4.getY());
                    break;
                }
                case 6: {
                    final SVGPathSegCurvetoCubicAbs s5 = (SVGPathSegCurvetoCubicAbs)seg;
                    h.curvetoCubicAbs(s5.getX1(), s5.getY1(), s5.getX2(), s5.getY2(), s5.getX(), s5.getY());
                    break;
                }
                case 7: {
                    final SVGPathSegCurvetoCubicRel s6 = (SVGPathSegCurvetoCubicRel)seg;
                    h.curvetoCubicRel(s6.getX1(), s6.getY1(), s6.getX2(), s6.getY2(), s6.getX(), s6.getY());
                    break;
                }
                case 8: {
                    final SVGPathSegCurvetoQuadraticAbs s7 = (SVGPathSegCurvetoQuadraticAbs)seg;
                    h.curvetoQuadraticAbs(s7.getX1(), s7.getY1(), s7.getX(), s7.getY());
                    break;
                }
                case 9: {
                    final SVGPathSegCurvetoQuadraticRel s8 = (SVGPathSegCurvetoQuadraticRel)seg;
                    h.curvetoQuadraticRel(s8.getX1(), s8.getY1(), s8.getX(), s8.getY());
                    break;
                }
                case 10: {
                    final SVGPathSegArcAbs s9 = (SVGPathSegArcAbs)seg;
                    h.arcAbs(s9.getR1(), s9.getR2(), s9.getAngle(), s9.getLargeArcFlag(), s9.getSweepFlag(), s9.getX(), s9.getY());
                    break;
                }
                case 11: {
                    final SVGPathSegArcRel s10 = (SVGPathSegArcRel)seg;
                    h.arcRel(s10.getR1(), s10.getR2(), s10.getAngle(), s10.getLargeArcFlag(), s10.getSweepFlag(), s10.getX(), s10.getY());
                    break;
                }
                case 12: {
                    final SVGPathSegLinetoHorizontalAbs s11 = (SVGPathSegLinetoHorizontalAbs)seg;
                    h.linetoHorizontalAbs(s11.getX());
                    break;
                }
                case 13: {
                    final SVGPathSegLinetoHorizontalRel s12 = (SVGPathSegLinetoHorizontalRel)seg;
                    h.linetoHorizontalRel(s12.getX());
                    break;
                }
                case 14: {
                    final SVGPathSegLinetoVerticalAbs s13 = (SVGPathSegLinetoVerticalAbs)seg;
                    h.linetoVerticalAbs(s13.getY());
                    break;
                }
                case 15: {
                    final SVGPathSegLinetoVerticalRel s14 = (SVGPathSegLinetoVerticalRel)seg;
                    h.linetoVerticalRel(s14.getY());
                    break;
                }
                case 16: {
                    final SVGPathSegCurvetoCubicSmoothAbs s15 = (SVGPathSegCurvetoCubicSmoothAbs)seg;
                    h.curvetoCubicSmoothAbs(s15.getX2(), s15.getY2(), s15.getX(), s15.getY());
                    break;
                }
                case 17: {
                    final SVGPathSegCurvetoCubicSmoothRel s16 = (SVGPathSegCurvetoCubicSmoothRel)seg;
                    h.curvetoCubicSmoothRel(s16.getX2(), s16.getY2(), s16.getX(), s16.getY());
                    break;
                }
                case 18: {
                    final SVGPathSegCurvetoQuadraticSmoothAbs s17 = (SVGPathSegCurvetoQuadraticSmoothAbs)seg;
                    h.curvetoQuadraticSmoothAbs(s17.getX(), s17.getY());
                    break;
                }
                case 19: {
                    final SVGPathSegCurvetoQuadraticSmoothRel s18 = (SVGPathSegCurvetoQuadraticSmoothRel)seg;
                    h.curvetoQuadraticSmoothRel(s18.getX(), s18.getY());
                    break;
                }
            }
        }
        h.endPath();
    }
}
