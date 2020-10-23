// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

public interface PathHandler
{
    void startPath() throws ParseException;
    
    void endPath() throws ParseException;
    
    void movetoRel(final float p0, final float p1) throws ParseException;
    
    void movetoAbs(final float p0, final float p1) throws ParseException;
    
    void closePath() throws ParseException;
    
    void linetoRel(final float p0, final float p1) throws ParseException;
    
    void linetoAbs(final float p0, final float p1) throws ParseException;
    
    void linetoHorizontalRel(final float p0) throws ParseException;
    
    void linetoHorizontalAbs(final float p0) throws ParseException;
    
    void linetoVerticalRel(final float p0) throws ParseException;
    
    void linetoVerticalAbs(final float p0) throws ParseException;
    
    void curvetoCubicRel(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5) throws ParseException;
    
    void curvetoCubicAbs(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5) throws ParseException;
    
    void curvetoCubicSmoothRel(final float p0, final float p1, final float p2, final float p3) throws ParseException;
    
    void curvetoCubicSmoothAbs(final float p0, final float p1, final float p2, final float p3) throws ParseException;
    
    void curvetoQuadraticRel(final float p0, final float p1, final float p2, final float p3) throws ParseException;
    
    void curvetoQuadraticAbs(final float p0, final float p1, final float p2, final float p3) throws ParseException;
    
    void curvetoQuadraticSmoothRel(final float p0, final float p1) throws ParseException;
    
    void curvetoQuadraticSmoothAbs(final float p0, final float p1) throws ParseException;
    
    void arcRel(final float p0, final float p1, final float p2, final boolean p3, final boolean p4, final float p5, final float p6) throws ParseException;
    
    void arcAbs(final float p0, final float p1, final float p2, final boolean p3, final boolean p4, final float p5, final float p6) throws ParseException;
}
