// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.util.Calendar;

public interface TimingSpecifierHandler
{
    void offset(final float p0);
    
    void syncbase(final float p0, final String p1, final String p2);
    
    void eventbase(final float p0, final String p1, final String p2);
    
    void repeat(final float p0, final String p1);
    
    void repeat(final float p0, final String p1, final int p2);
    
    void accesskey(final float p0, final char p1);
    
    void accessKeySVG12(final float p0, final String p1);
    
    void mediaMarker(final String p0, final String p1);
    
    void wallclock(final Calendar p0);
    
    void indefinite();
}
