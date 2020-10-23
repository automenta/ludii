// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.Iterator;
import org.apache.batik.anim.dom.AnimationTarget;
import java.util.LinkedList;
import org.apache.batik.anim.dom.AnimationTargetListener;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.apache.batik.anim.dom.SVGAnimationTargetContext;

public abstract class AnimatableSVGBridge extends AbstractSVGBridge implements SVGAnimationTargetContext
{
    protected Element e;
    protected BridgeContext ctx;
    protected HashMap targetListeners;
    
    @Override
    public void addTargetListener(final String pn, final AnimationTargetListener l) {
        if (this.targetListeners == null) {
            this.targetListeners = new HashMap();
        }
        LinkedList ll = this.targetListeners.get(pn);
        if (ll == null) {
            ll = new LinkedList();
            this.targetListeners.put(pn, ll);
        }
        ll.add(l);
    }
    
    @Override
    public void removeTargetListener(final String pn, final AnimationTargetListener l) {
        final LinkedList ll = this.targetListeners.get(pn);
        ll.remove(l);
    }
    
    protected void fireBaseAttributeListeners(final String pn) {
        if (this.targetListeners != null) {
            final LinkedList ll = this.targetListeners.get(pn);
            if (ll != null) {
                for (final Object aLl : ll) {
                    final AnimationTargetListener l = (AnimationTargetListener)aLl;
                    l.baseValueChanged((AnimationTarget)this.e, null, pn, true);
                }
            }
        }
    }
}
