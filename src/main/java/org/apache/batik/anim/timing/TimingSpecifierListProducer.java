// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.timing;

import java.util.Calendar;
import org.apache.batik.parser.TimingSpecifierListHandler;
import org.apache.batik.parser.TimingSpecifierListParser;
import java.util.LinkedList;
import org.apache.batik.parser.DefaultTimingSpecifierListHandler;

public class TimingSpecifierListProducer extends DefaultTimingSpecifierListHandler
{
    protected LinkedList timingSpecifiers;
    protected TimedElement owner;
    protected boolean isBegin;
    
    public TimingSpecifierListProducer(final TimedElement owner, final boolean isBegin) {
        this.timingSpecifiers = new LinkedList();
        this.owner = owner;
        this.isBegin = isBegin;
    }
    
    public TimingSpecifier[] getTimingSpecifiers() {
        return this.timingSpecifiers.toArray(new TimingSpecifier[this.timingSpecifiers.size()]);
    }
    
    public static TimingSpecifier[] parseTimingSpecifierList(final TimedElement owner, final boolean isBegin, final String spec, final boolean useSVG11AccessKeys, final boolean useSVG12AccessKeys) {
        final TimingSpecifierListParser p = new TimingSpecifierListParser(useSVG11AccessKeys, useSVG12AccessKeys);
        final TimingSpecifierListProducer pp = new TimingSpecifierListProducer(owner, isBegin);
        p.setTimingSpecifierListHandler(pp);
        p.parse(spec);
        final TimingSpecifier[] specs = pp.getTimingSpecifiers();
        return specs;
    }
    
    @Override
    public void offset(final float offset) {
        final TimingSpecifier ts = new OffsetTimingSpecifier(this.owner, this.isBegin, offset);
        this.timingSpecifiers.add(ts);
    }
    
    @Override
    public void syncbase(final float offset, final String syncbaseID, final String timeSymbol) {
        final TimingSpecifier ts = new SyncbaseTimingSpecifier(this.owner, this.isBegin, offset, syncbaseID, timeSymbol.charAt(0) == 'b');
        this.timingSpecifiers.add(ts);
    }
    
    @Override
    public void eventbase(final float offset, final String eventbaseID, final String eventType) {
        final TimingSpecifier ts = new EventbaseTimingSpecifier(this.owner, this.isBegin, offset, eventbaseID, eventType);
        this.timingSpecifiers.add(ts);
    }
    
    @Override
    public void repeat(final float offset, final String syncbaseID) {
        final TimingSpecifier ts = new RepeatTimingSpecifier(this.owner, this.isBegin, offset, syncbaseID);
        this.timingSpecifiers.add(ts);
    }
    
    @Override
    public void repeat(final float offset, final String syncbaseID, final int repeatIteration) {
        final TimingSpecifier ts = new RepeatTimingSpecifier(this.owner, this.isBegin, offset, syncbaseID, repeatIteration);
        this.timingSpecifiers.add(ts);
    }
    
    @Override
    public void accesskey(final float offset, final char key) {
        final TimingSpecifier ts = new AccesskeyTimingSpecifier(this.owner, this.isBegin, offset, key);
        this.timingSpecifiers.add(ts);
    }
    
    @Override
    public void accessKeySVG12(final float offset, final String keyName) {
        final TimingSpecifier ts = new AccesskeyTimingSpecifier(this.owner, this.isBegin, offset, keyName);
        this.timingSpecifiers.add(ts);
    }
    
    @Override
    public void mediaMarker(final String syncbaseID, final String markerName) {
        final TimingSpecifier ts = new MediaMarkerTimingSpecifier(this.owner, this.isBegin, syncbaseID, markerName);
        this.timingSpecifiers.add(ts);
    }
    
    @Override
    public void wallclock(final Calendar time) {
        final TimingSpecifier ts = new WallclockTimingSpecifier(this.owner, this.isBegin, time);
        this.timingSpecifiers.add(ts);
    }
    
    @Override
    public void indefinite() {
        final TimingSpecifier ts = new IndefiniteTimingSpecifier(this.owner, this.isBegin);
        this.timingSpecifiers.add(ts);
    }
}
