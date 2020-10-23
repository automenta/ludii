// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.util.Calendar;

public class DefaultTimingSpecifierHandler implements TimingSpecifierHandler
{
    public static final TimingSpecifierHandler INSTANCE;
    
    protected DefaultTimingSpecifierHandler() {
    }
    
    @Override
    public void offset(final float offset) {
    }
    
    @Override
    public void syncbase(final float offset, final String syncbaseID, final String timeSymbol) {
    }
    
    @Override
    public void eventbase(final float offset, final String eventbaseID, final String eventType) {
    }
    
    @Override
    public void repeat(final float offset, final String syncbaseID) {
    }
    
    @Override
    public void repeat(final float offset, final String syncbaseID, final int repeatIteration) {
    }
    
    @Override
    public void accesskey(final float offset, final char key) {
    }
    
    @Override
    public void accessKeySVG12(final float offset, final String keyName) {
    }
    
    @Override
    public void mediaMarker(final String syncbaseID, final String markerName) {
    }
    
    @Override
    public void wallclock(final Calendar time) {
    }
    
    @Override
    public void indefinite() {
    }
    
    static {
        INSTANCE = new DefaultTimingSpecifierHandler();
    }
}
