// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.events.EventTarget;

public interface SVGPathElement extends SVGElement, SVGTests, SVGLangSpace, SVGExternalResourcesRequired, SVGStylable, SVGTransformable, EventTarget, SVGAnimatedPathData
{
    SVGAnimatedNumber getPathLength();
    
    float getTotalLength();
    
    SVGPoint getPointAtLength(final float p0);
    
    int getPathSegAtLength(final float p0);
    
    SVGPathSegClosePath createSVGPathSegClosePath();
    
    SVGPathSegMovetoAbs createSVGPathSegMovetoAbs(final float p0, final float p1);
    
    SVGPathSegMovetoRel createSVGPathSegMovetoRel(final float p0, final float p1);
    
    SVGPathSegLinetoAbs createSVGPathSegLinetoAbs(final float p0, final float p1);
    
    SVGPathSegLinetoRel createSVGPathSegLinetoRel(final float p0, final float p1);
    
    SVGPathSegCurvetoCubicAbs createSVGPathSegCurvetoCubicAbs(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5);
    
    SVGPathSegCurvetoCubicRel createSVGPathSegCurvetoCubicRel(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5);
    
    SVGPathSegCurvetoQuadraticAbs createSVGPathSegCurvetoQuadraticAbs(final float p0, final float p1, final float p2, final float p3);
    
    SVGPathSegCurvetoQuadraticRel createSVGPathSegCurvetoQuadraticRel(final float p0, final float p1, final float p2, final float p3);
    
    SVGPathSegArcAbs createSVGPathSegArcAbs(final float p0, final float p1, final float p2, final float p3, final float p4, final boolean p5, final boolean p6);
    
    SVGPathSegArcRel createSVGPathSegArcRel(final float p0, final float p1, final float p2, final float p3, final float p4, final boolean p5, final boolean p6);
    
    SVGPathSegLinetoHorizontalAbs createSVGPathSegLinetoHorizontalAbs(final float p0);
    
    SVGPathSegLinetoHorizontalRel createSVGPathSegLinetoHorizontalRel(final float p0);
    
    SVGPathSegLinetoVerticalAbs createSVGPathSegLinetoVerticalAbs(final float p0);
    
    SVGPathSegLinetoVerticalRel createSVGPathSegLinetoVerticalRel(final float p0);
    
    SVGPathSegCurvetoCubicSmoothAbs createSVGPathSegCurvetoCubicSmoothAbs(final float p0, final float p1, final float p2, final float p3);
    
    SVGPathSegCurvetoCubicSmoothRel createSVGPathSegCurvetoCubicSmoothRel(final float p0, final float p1, final float p2, final float p3);
    
    SVGPathSegCurvetoQuadraticSmoothAbs createSVGPathSegCurvetoQuadraticSmoothAbs(final float p0, final float p1);
    
    SVGPathSegCurvetoQuadraticSmoothRel createSVGPathSegCurvetoQuadraticSmoothRel(final float p0, final float p1);
}
