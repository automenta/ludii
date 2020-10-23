// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

public class DefaultPathHandler implements PathHandler
{
    public static final PathHandler INSTANCE;
    
    protected DefaultPathHandler() {
    }
    
    @Override
    public void startPath() throws ParseException {
    }
    
    @Override
    public void endPath() throws ParseException {
    }
    
    @Override
    public void movetoRel(final float x, final float y) throws ParseException {
    }
    
    @Override
    public void movetoAbs(final float x, final float y) throws ParseException {
    }
    
    @Override
    public void closePath() throws ParseException {
    }
    
    @Override
    public void linetoRel(final float x, final float y) throws ParseException {
    }
    
    @Override
    public void linetoAbs(final float x, final float y) throws ParseException {
    }
    
    @Override
    public void linetoHorizontalRel(final float x) throws ParseException {
    }
    
    @Override
    public void linetoHorizontalAbs(final float x) throws ParseException {
    }
    
    @Override
    public void linetoVerticalRel(final float y) throws ParseException {
    }
    
    @Override
    public void linetoVerticalAbs(final float y) throws ParseException {
    }
    
    @Override
    public void curvetoCubicRel(final float x1, final float y1, final float x2, final float y2, final float x, final float y) throws ParseException {
    }
    
    @Override
    public void curvetoCubicAbs(final float x1, final float y1, final float x2, final float y2, final float x, final float y) throws ParseException {
    }
    
    @Override
    public void curvetoCubicSmoothRel(final float x2, final float y2, final float x, final float y) throws ParseException {
    }
    
    @Override
    public void curvetoCubicSmoothAbs(final float x2, final float y2, final float x, final float y) throws ParseException {
    }
    
    @Override
    public void curvetoQuadraticRel(final float x1, final float y1, final float x, final float y) throws ParseException {
    }
    
    @Override
    public void curvetoQuadraticAbs(final float x1, final float y1, final float x, final float y) throws ParseException {
    }
    
    @Override
    public void curvetoQuadraticSmoothRel(final float x, final float y) throws ParseException {
    }
    
    @Override
    public void curvetoQuadraticSmoothAbs(final float x, final float y) throws ParseException {
    }
    
    @Override
    public void arcRel(final float rx, final float ry, final float xAxisRotation, final boolean largeArcFlag, final boolean sweepFlag, final float x, final float y) throws ParseException {
    }
    
    @Override
    public void arcAbs(final float rx, final float ry, final float xAxisRotation, final boolean largeArcFlag, final boolean sweepFlag, final float x, final float y) throws ParseException {
    }
    
    static {
        INSTANCE = new DefaultPathHandler();
    }
}
