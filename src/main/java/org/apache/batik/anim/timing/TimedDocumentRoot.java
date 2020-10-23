// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.timing;

import java.util.Iterator;
import java.util.LinkedList;
import org.apache.batik.util.DoublyIndexedSet;
import java.util.Calendar;

public abstract class TimedDocumentRoot extends TimeContainer
{
    protected Calendar documentBeginTime;
    protected boolean useSVG11AccessKeys;
    protected boolean useSVG12AccessKeys;
    protected DoublyIndexedSet propagationFlags;
    protected LinkedList listeners;
    protected boolean isSampling;
    protected boolean isHyperlinking;
    
    public TimedDocumentRoot(final boolean useSVG11AccessKeys, final boolean useSVG12AccessKeys) {
        this.propagationFlags = new DoublyIndexedSet();
        this.listeners = new LinkedList();
        this.root = this;
        this.useSVG11AccessKeys = useSVG11AccessKeys;
        this.useSVG12AccessKeys = useSVG12AccessKeys;
    }
    
    @Override
    protected float getImplicitDur() {
        return Float.POSITIVE_INFINITY;
    }
    
    @Override
    public float getDefaultBegin(final TimedElement child) {
        return 0.0f;
    }
    
    public float getCurrentTime() {
        return this.lastSampleTime;
    }
    
    public boolean isSampling() {
        return this.isSampling;
    }
    
    public boolean isHyperlinking() {
        return this.isHyperlinking;
    }
    
    public float seekTo(final float time, final boolean hyperlinking) {
        this.isSampling = true;
        this.lastSampleTime = time;
        this.isHyperlinking = hyperlinking;
        this.propagationFlags.clear();
        float mint = Float.POSITIVE_INFINITY;
        final TimedElement[] arr$;
        final TimedElement[] es = arr$ = this.getChildren();
        for (final TimedElement e1 : arr$) {
            final float t = e1.sampleAt(time, hyperlinking);
            if (t < mint) {
                mint = t;
            }
        }
        boolean needsUpdates;
        do {
            needsUpdates = false;
            for (final TimedElement e2 : es) {
                if (e2.shouldUpdateCurrentInterval) {
                    needsUpdates = true;
                    final float t2 = e2.sampleAt(time, hyperlinking);
                    if (t2 < mint) {
                        mint = t2;
                    }
                }
            }
        } while (needsUpdates);
        this.isSampling = false;
        if (hyperlinking) {
            this.root.currentIntervalWillUpdate();
        }
        return mint;
    }
    
    public void resetDocument(final Calendar documentBeginTime) {
        if (documentBeginTime == null) {
            this.documentBeginTime = Calendar.getInstance();
        }
        else {
            this.documentBeginTime = documentBeginTime;
        }
        this.reset(true);
    }
    
    public Calendar getDocumentBeginTime() {
        return this.documentBeginTime;
    }
    
    public float convertEpochTime(final long t) {
        final long begin = this.documentBeginTime.getTime().getTime();
        return (t - begin) / 1000.0f;
    }
    
    public float convertWallclockTime(final Calendar time) {
        final long begin = this.documentBeginTime.getTime().getTime();
        final long t = time.getTime().getTime();
        return (t - begin) / 1000.0f;
    }
    
    public void addTimegraphListener(final TimegraphListener l) {
        this.listeners.add(l);
    }
    
    public void removeTimegraphListener(final TimegraphListener l) {
        this.listeners.remove(l);
    }
    
    void fireElementAdded(final TimedElement e) {
        for (final Object listener : this.listeners) {
            ((TimegraphListener)listener).elementAdded(e);
        }
    }
    
    void fireElementRemoved(final TimedElement e) {
        for (final Object listener : this.listeners) {
            ((TimegraphListener)listener).elementRemoved(e);
        }
    }
    
    boolean shouldPropagate(final Interval i, final TimingSpecifier ts, final boolean isBegin) {
        final InstanceTime it = isBegin ? i.getBeginInstanceTime() : i.getEndInstanceTime();
        if (this.propagationFlags.contains(it, ts)) {
            return false;
        }
        this.propagationFlags.add(it, ts);
        return true;
    }
    
    protected void currentIntervalWillUpdate() {
    }
    
    protected abstract String getEventNamespaceURI(final String p0);
    
    protected abstract String getEventType(final String p0);
    
    protected abstract String getRepeatEventName();
}
