// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

public interface TransformListHandler
{
    void startTransformList() throws ParseException;
    
    void matrix(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5) throws ParseException;
    
    void rotate(final float p0) throws ParseException;
    
    void rotate(final float p0, final float p1, final float p2) throws ParseException;
    
    void translate(final float p0) throws ParseException;
    
    void translate(final float p0, final float p1) throws ParseException;
    
    void scale(final float p0) throws ParseException;
    
    void scale(final float p0, final float p1) throws ParseException;
    
    void skewX(final float p0) throws ParseException;
    
    void skewY(final float p0) throws ParseException;
    
    void endTransformList() throws ParseException;
}
