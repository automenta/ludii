// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.timing;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class TimeContainer extends TimedElement
{
    protected List children;
    
    public TimeContainer() {
        this.children = new LinkedList();
    }
    
    public void addChild(final TimedElement e) {
        if (e == this) {
            throw new IllegalArgumentException("recursive datastructure not allowed here!");
        }
        this.children.add(e);
        (e.parent = this).setRoot(e, this.root);
        this.root.fireElementAdded(e);
        this.root.currentIntervalWillUpdate();
    }
    
    protected void setRoot(final TimedElement e, final TimedDocumentRoot root) {
        e.root = root;
        if (e instanceof TimeContainer) {
            final TimeContainer c = (TimeContainer)e;
            for (final Object aChildren : c.children) {
                final TimedElement te = (TimedElement)aChildren;
                this.setRoot(te, root);
            }
        }
    }
    
    public void removeChild(final TimedElement e) {
        this.children.remove(e);
        e.parent = null;
        this.setRoot(e, null);
        this.root.fireElementRemoved(e);
        this.root.currentIntervalWillUpdate();
    }
    
    public TimedElement[] getChildren() {
        return this.children.toArray(new TimedElement[this.children.size()]);
    }
    
    @Override
    protected float sampleAt(final float parentSimpleTime, final boolean hyperlinking) {
        super.sampleAt(parentSimpleTime, hyperlinking);
        return this.sampleChildren(parentSimpleTime, hyperlinking);
    }
    
    protected float sampleChildren(final float parentSimpleTime, final boolean hyperlinking) {
        float mint = Float.POSITIVE_INFINITY;
        for (final Object aChildren : this.children) {
            final TimedElement e = (TimedElement)aChildren;
            final float t = e.sampleAt(parentSimpleTime, hyperlinking);
            if (t < mint) {
                mint = t;
            }
        }
        return mint;
    }
    
    @Override
    protected void reset(final boolean clearCurrentBegin) {
        super.reset(clearCurrentBegin);
        for (final Object aChildren : this.children) {
            final TimedElement e = (TimedElement)aChildren;
            e.reset(clearCurrentBegin);
        }
    }
    
    @Override
    protected boolean isConstantAnimation() {
        return false;
    }
    
    public abstract float getDefaultBegin(final TimedElement p0);
}
