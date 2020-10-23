// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.timing;

public interface TimegraphListener
{
    void elementAdded(final TimedElement p0);
    
    void elementRemoved(final TimedElement p0);
    
    void elementActivated(final TimedElement p0, final float p1);
    
    void elementFilled(final TimedElement p0, final float p1);
    
    void elementDeactivated(final TimedElement p0, final float p1);
    
    void intervalCreated(final TimedElement p0, final Interval p1);
    
    void intervalRemoved(final TimedElement p0, final Interval p1);
    
    void intervalChanged(final TimedElement p0, final Interval p1);
    
    void intervalBegan(final TimedElement p0, final Interval p1);
    
    void elementRepeated(final TimedElement p0, final int p1, final float p2);
    
    void elementInstanceTimesChanged(final TimedElement p0, final float p1);
}
