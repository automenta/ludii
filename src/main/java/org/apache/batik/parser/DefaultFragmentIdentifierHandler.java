// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

public class DefaultFragmentIdentifierHandler extends DefaultPreserveAspectRatioHandler implements FragmentIdentifierHandler
{
    public static final FragmentIdentifierHandler INSTANCE;
    
    protected DefaultFragmentIdentifierHandler() {
    }
    
    @Override
    public void startFragmentIdentifier() throws ParseException {
    }
    
    @Override
    public void idReference(final String s) throws ParseException {
    }
    
    @Override
    public void viewBox(final float x, final float y, final float width, final float height) throws ParseException {
    }
    
    @Override
    public void startViewTarget() throws ParseException {
    }
    
    @Override
    public void viewTarget(final String name) throws ParseException {
    }
    
    @Override
    public void endViewTarget() throws ParseException {
    }
    
    @Override
    public void startTransformList() throws ParseException {
    }
    
    @Override
    public void matrix(final float a, final float b, final float c, final float d, final float e, final float f) throws ParseException {
    }
    
    @Override
    public void rotate(final float theta) throws ParseException {
    }
    
    @Override
    public void rotate(final float theta, final float cx, final float cy) throws ParseException {
    }
    
    @Override
    public void translate(final float tx) throws ParseException {
    }
    
    @Override
    public void translate(final float tx, final float ty) throws ParseException {
    }
    
    @Override
    public void scale(final float sx) throws ParseException {
    }
    
    @Override
    public void scale(final float sx, final float sy) throws ParseException {
    }
    
    @Override
    public void skewX(final float skx) throws ParseException {
    }
    
    @Override
    public void skewY(final float sky) throws ParseException {
    }
    
    @Override
    public void endTransformList() throws ParseException {
    }
    
    @Override
    public void zoomAndPan(final boolean magnify) {
    }
    
    @Override
    public void endFragmentIdentifier() throws ParseException {
    }
    
    static {
        INSTANCE = new DefaultFragmentIdentifierHandler();
    }
}
