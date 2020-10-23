// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

public interface FragmentIdentifierHandler extends PreserveAspectRatioHandler, TransformListHandler
{
    void startFragmentIdentifier() throws ParseException;
    
    void idReference(final String p0) throws ParseException;
    
    void viewBox(final float p0, final float p1, final float p2, final float p3) throws ParseException;
    
    void startViewTarget() throws ParseException;
    
    void viewTarget(final String p0) throws ParseException;
    
    void endViewTarget() throws ParseException;
    
    void zoomAndPan(final boolean p0);
    
    void endFragmentIdentifier() throws ParseException;
}
