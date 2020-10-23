// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim;

import org.apache.batik.anim.timing.TimegraphListener;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.values.AnimatableValue;
import java.util.Calendar;
import java.util.Iterator;
import org.apache.batik.anim.dom.AnimationTargetListener;
import org.apache.batik.util.DoublyIndexedTable;
import org.apache.batik.anim.dom.AnimationTarget;
import java.util.Map;
import java.util.HashMap;
import org.apache.batik.anim.timing.TimedDocumentRoot;
import org.w3c.dom.Document;

public abstract class AnimationEngine
{
    public static final short ANIM_TYPE_XML = 0;
    public static final short ANIM_TYPE_CSS = 1;
    public static final short ANIM_TYPE_OTHER = 2;
    protected Document document;
    protected TimedDocumentRoot timedDocumentRoot;
    protected long pauseTime;
    protected HashMap targets;
    protected HashMap animations;
    protected Listener targetListener;
    protected static final Map.Entry[] MAP_ENTRY_ARRAY;
    
    public AnimationEngine(final Document doc) {
        this.targets = new HashMap();
        this.animations = new HashMap();
        this.targetListener = new Listener();
        this.document = doc;
        this.timedDocumentRoot = this.createDocumentRoot();
    }
    
    public void dispose() {
        for (final Object o : this.targets.entrySet()) {
            final Map.Entry e = (Map.Entry)o;
            final AnimationTarget target = e.getKey();
            final TargetInfo info = e.getValue();
            for (final DoublyIndexedTable.Entry e2 : info.xmlAnimations) {
                final String namespaceURI = (String)e2.getKey1();
                final String localName = (String)e2.getKey2();
                final Sandwich sandwich = (Sandwich)e2.getValue();
                if (sandwich.listenerRegistered) {
                    target.removeTargetListener(namespaceURI, localName, false, this.targetListener);
                }
            }
            for (final Map.Entry e3 : info.cssAnimations.entrySet()) {
                final String propertyName = e3.getKey();
                final Sandwich sandwich2 = e3.getValue();
                if (sandwich2.listenerRegistered) {
                    target.removeTargetListener(null, propertyName, true, this.targetListener);
                }
            }
        }
    }
    
    public void pause() {
        if (this.pauseTime == 0L) {
            this.pauseTime = System.currentTimeMillis();
        }
    }
    
    public void unpause() {
        if (this.pauseTime != 0L) {
            final Calendar begin = this.timedDocumentRoot.getDocumentBeginTime();
            final int dt = (int)(System.currentTimeMillis() - this.pauseTime);
            begin.add(14, dt);
            this.pauseTime = 0L;
        }
    }
    
    public boolean isPaused() {
        return this.pauseTime != 0L;
    }
    
    public float getCurrentTime() {
        return this.timedDocumentRoot.getCurrentTime();
    }
    
    public float setCurrentTime(final float t) {
        final boolean p = this.pauseTime != 0L;
        this.unpause();
        final Calendar begin = this.timedDocumentRoot.getDocumentBeginTime();
        final float now = this.timedDocumentRoot.convertEpochTime(System.currentTimeMillis());
        begin.add(14, (int)((now - t) * 1000.0f));
        if (p) {
            this.pause();
        }
        return this.tick(t, true);
    }
    
    public void addAnimation(final AnimationTarget target, final short type, final String ns, final String an, final AbstractAnimation anim) {
        this.timedDocumentRoot.addChild(anim.getTimedElement());
        final AnimationInfo animInfo = this.getAnimationInfo(anim);
        animInfo.type = type;
        animInfo.attributeNamespaceURI = ns;
        animInfo.attributeLocalName = an;
        animInfo.target = target;
        this.animations.put(anim, animInfo);
        final Sandwich sandwich = this.getSandwich(target, type, ns, an);
        if (sandwich.animation == null) {
            anim.lowerAnimation = null;
            anim.higherAnimation = null;
        }
        else {
            sandwich.animation.higherAnimation = anim;
            anim.lowerAnimation = sandwich.animation;
            anim.higherAnimation = null;
        }
        sandwich.animation = anim;
        if (anim.lowerAnimation == null) {
            sandwich.lowestAnimation = anim;
        }
    }
    
    public void removeAnimation(final AbstractAnimation anim) {
        this.timedDocumentRoot.removeChild(anim.getTimedElement());
        final AbstractAnimation nextHigher = anim.higherAnimation;
        if (nextHigher != null) {
            nextHigher.markDirty();
        }
        this.moveToBottom(anim);
        if (anim.higherAnimation != null) {
            anim.higherAnimation.lowerAnimation = null;
        }
        final AnimationInfo animInfo = this.getAnimationInfo(anim);
        final Sandwich sandwich = this.getSandwich(animInfo.target, animInfo.type, animInfo.attributeNamespaceURI, animInfo.attributeLocalName);
        if (sandwich.animation == anim) {
            sandwich.animation = null;
            sandwich.lowestAnimation = null;
            sandwich.shouldUpdate = true;
        }
    }
    
    protected Sandwich getSandwich(final AnimationTarget target, final short type, final String ns, final String an) {
        final TargetInfo info = this.getTargetInfo(target);
        Sandwich sandwich;
        if (type == 0) {
            sandwich = (Sandwich)info.xmlAnimations.get(ns, an);
            if (sandwich == null) {
                sandwich = new Sandwich();
                info.xmlAnimations.put(ns, an, sandwich);
            }
        }
        else if (type == 1) {
            sandwich = info.cssAnimations.get(an);
            if (sandwich == null) {
                sandwich = new Sandwich();
                info.cssAnimations.put(an, sandwich);
            }
        }
        else {
            sandwich = info.otherAnimations.get(an);
            if (sandwich == null) {
                sandwich = new Sandwich();
                info.otherAnimations.put(an, sandwich);
            }
        }
        return sandwich;
    }
    
    protected TargetInfo getTargetInfo(final AnimationTarget target) {
        TargetInfo info = this.targets.get(target);
        if (info == null) {
            info = new TargetInfo();
            this.targets.put(target, info);
        }
        return info;
    }
    
    protected AnimationInfo getAnimationInfo(final AbstractAnimation anim) {
        AnimationInfo info = this.animations.get(anim);
        if (info == null) {
            info = new AnimationInfo();
            this.animations.put(anim, info);
        }
        return info;
    }
    
    protected float tick(final float time, final boolean hyperlinking) {
        final float waitTime = this.timedDocumentRoot.seekTo(time, hyperlinking);
        final Map.Entry[] arr$;
        final Map.Entry[] targetEntries = arr$ = (Map.Entry[])this.targets.entrySet().toArray(AnimationEngine.MAP_ENTRY_ARRAY);
        for (final Map.Entry e : arr$) {
            final AnimationTarget target = e.getKey();
            final TargetInfo info = e.getValue();
            for (final DoublyIndexedTable.Entry e2 : info.xmlAnimations) {
                final String namespaceURI = (String)e2.getKey1();
                final String localName = (String)e2.getKey2();
                final Sandwich sandwich = (Sandwich)e2.getValue();
                if (sandwich.shouldUpdate || (sandwich.animation != null && sandwich.animation.isDirty)) {
                    AnimatableValue av = null;
                    boolean usesUnderlying = false;
                    final AbstractAnimation anim = sandwich.animation;
                    if (anim != null) {
                        av = anim.getComposedValue();
                        usesUnderlying = sandwich.lowestAnimation.usesUnderlyingValue();
                        anim.isDirty = false;
                    }
                    if (usesUnderlying && !sandwich.listenerRegistered) {
                        target.addTargetListener(namespaceURI, localName, false, this.targetListener);
                        sandwich.listenerRegistered = true;
                    }
                    else if (!usesUnderlying && sandwich.listenerRegistered) {
                        target.removeTargetListener(namespaceURI, localName, false, this.targetListener);
                        sandwich.listenerRegistered = false;
                    }
                    target.updateAttributeValue(namespaceURI, localName, av);
                    sandwich.shouldUpdate = false;
                }
            }
            for (final Map.Entry e3 : info.cssAnimations.entrySet()) {
                final String propertyName = e3.getKey();
                final Sandwich sandwich2 = e3.getValue();
                if (sandwich2.shouldUpdate || (sandwich2.animation != null && sandwich2.animation.isDirty)) {
                    AnimatableValue av2 = null;
                    boolean usesUnderlying2 = false;
                    final AbstractAnimation anim2 = sandwich2.animation;
                    if (anim2 != null) {
                        av2 = anim2.getComposedValue();
                        usesUnderlying2 = sandwich2.lowestAnimation.usesUnderlyingValue();
                        anim2.isDirty = false;
                    }
                    if (usesUnderlying2 && !sandwich2.listenerRegistered) {
                        target.addTargetListener(null, propertyName, true, this.targetListener);
                        sandwich2.listenerRegistered = true;
                    }
                    else if (!usesUnderlying2 && sandwich2.listenerRegistered) {
                        target.removeTargetListener(null, propertyName, true, this.targetListener);
                        sandwich2.listenerRegistered = false;
                    }
                    if (usesUnderlying2) {
                        target.updatePropertyValue(propertyName, null);
                    }
                    if (!usesUnderlying2 || av2 != null) {
                        target.updatePropertyValue(propertyName, av2);
                    }
                    sandwich2.shouldUpdate = false;
                }
            }
            for (final Map.Entry e3 : info.otherAnimations.entrySet()) {
                final String type = e3.getKey();
                final Sandwich sandwich2 = e3.getValue();
                if (sandwich2.shouldUpdate || (sandwich2.animation != null && sandwich2.animation.isDirty)) {
                    AnimatableValue av2 = null;
                    final AbstractAnimation anim3 = sandwich2.animation;
                    if (anim3 != null) {
                        av2 = sandwich2.animation.getComposedValue();
                        anim3.isDirty = false;
                    }
                    target.updateOtherValue(type, av2);
                    sandwich2.shouldUpdate = false;
                }
            }
        }
        return waitTime;
    }
    
    public void toActive(final AbstractAnimation anim, final float begin) {
        this.moveToTop(anim);
        anim.isActive = true;
        anim.beginTime = begin;
        anim.isFrozen = false;
        this.pushDown(anim);
        anim.markDirty();
    }
    
    protected void pushDown(final AbstractAnimation anim) {
        final TimedElement e = anim.getTimedElement();
        AbstractAnimation top = null;
        boolean moved = false;
        while (anim.lowerAnimation != null && (anim.lowerAnimation.isActive || anim.lowerAnimation.isFrozen) && (anim.lowerAnimation.beginTime > anim.beginTime || (anim.lowerAnimation.beginTime == anim.beginTime && e.isBefore(anim.lowerAnimation.getTimedElement())))) {
            final AbstractAnimation higher = anim.higherAnimation;
            final AbstractAnimation lower = anim.lowerAnimation;
            final AbstractAnimation lowerLower = lower.lowerAnimation;
            if (higher != null) {
                higher.lowerAnimation = lower;
            }
            if (lowerLower != null) {
                lowerLower.higherAnimation = anim;
            }
            lower.lowerAnimation = anim;
            lower.higherAnimation = higher;
            anim.lowerAnimation = lowerLower;
            anim.higherAnimation = lower;
            if (!moved) {
                top = lower;
                moved = true;
            }
        }
        if (moved) {
            final AnimationInfo animInfo = this.getAnimationInfo(anim);
            final Sandwich sandwich = this.getSandwich(animInfo.target, animInfo.type, animInfo.attributeNamespaceURI, animInfo.attributeLocalName);
            if (sandwich.animation == anim) {
                sandwich.animation = top;
            }
            if (anim.lowerAnimation == null) {
                sandwich.lowestAnimation = anim;
            }
        }
    }
    
    public void toInactive(final AbstractAnimation anim, final boolean isFrozen) {
        anim.isActive = false;
        anim.isFrozen = isFrozen;
        anim.markDirty();
        if (!isFrozen) {
            anim.value = null;
            anim.beginTime = Float.NEGATIVE_INFINITY;
            this.moveToBottom(anim);
        }
    }
    
    public void removeFill(final AbstractAnimation anim) {
        anim.isActive = false;
        anim.isFrozen = false;
        anim.value = null;
        anim.markDirty();
        this.moveToBottom(anim);
    }
    
    protected void moveToTop(final AbstractAnimation anim) {
        final AnimationInfo animInfo = this.getAnimationInfo(anim);
        final Sandwich sandwich = this.getSandwich(animInfo.target, animInfo.type, animInfo.attributeNamespaceURI, animInfo.attributeLocalName);
        sandwich.shouldUpdate = true;
        if (anim.higherAnimation == null) {
            return;
        }
        if (anim.lowerAnimation == null) {
            sandwich.lowestAnimation = anim.higherAnimation;
        }
        else {
            anim.lowerAnimation.higherAnimation = anim.higherAnimation;
        }
        anim.higherAnimation.lowerAnimation = anim.lowerAnimation;
        if (sandwich.animation != null) {
            sandwich.animation.higherAnimation = anim;
        }
        anim.lowerAnimation = sandwich.animation;
        anim.higherAnimation = null;
        sandwich.animation = anim;
    }
    
    protected void moveToBottom(final AbstractAnimation anim) {
        if (anim.lowerAnimation == null) {
            return;
        }
        final AnimationInfo animInfo = this.getAnimationInfo(anim);
        final Sandwich sandwich = this.getSandwich(animInfo.target, animInfo.type, animInfo.attributeNamespaceURI, animInfo.attributeLocalName);
        final AbstractAnimation nextLower = anim.lowerAnimation;
        nextLower.markDirty();
        anim.lowerAnimation.higherAnimation = anim.higherAnimation;
        if (anim.higherAnimation != null) {
            anim.higherAnimation.lowerAnimation = anim.lowerAnimation;
        }
        else {
            sandwich.animation = nextLower;
            sandwich.shouldUpdate = true;
        }
        sandwich.lowestAnimation.lowerAnimation = anim;
        anim.higherAnimation = sandwich.lowestAnimation;
        anim.lowerAnimation = null;
        sandwich.lowestAnimation = anim;
        if (sandwich.animation.isDirty) {
            sandwich.shouldUpdate = true;
        }
    }
    
    public void addTimegraphListener(final TimegraphListener l) {
        this.timedDocumentRoot.addTimegraphListener(l);
    }
    
    public void removeTimegraphListener(final TimegraphListener l) {
        this.timedDocumentRoot.removeTimegraphListener(l);
    }
    
    public void sampledAt(final AbstractAnimation anim, final float simpleTime, final float simpleDur, final int repeatIteration) {
        anim.sampledAt(simpleTime, simpleDur, repeatIteration);
    }
    
    public void sampledLastValue(final AbstractAnimation anim, final int repeatIteration) {
        anim.sampledLastValue(repeatIteration);
    }
    
    protected abstract TimedDocumentRoot createDocumentRoot();
    
    static {
        MAP_ENTRY_ARRAY = new Map.Entry[0];
    }
    
    protected class Listener implements AnimationTargetListener
    {
        @Override
        public void baseValueChanged(final AnimationTarget t, final String ns, final String ln, final boolean isCSS) {
            final short type = (short)(isCSS ? 1 : 0);
            final Sandwich sandwich = AnimationEngine.this.getSandwich(t, type, ns, ln);
            sandwich.shouldUpdate = true;
            AbstractAnimation anim;
            for (anim = sandwich.animation; anim.lowerAnimation != null; anim = anim.lowerAnimation) {}
            anim.markDirty();
        }
    }
    
    protected static class TargetInfo
    {
        public DoublyIndexedTable xmlAnimations;
        public HashMap cssAnimations;
        public HashMap otherAnimations;
        
        protected TargetInfo() {
            this.xmlAnimations = new DoublyIndexedTable();
            this.cssAnimations = new HashMap();
            this.otherAnimations = new HashMap();
        }
    }
    
    protected static class Sandwich
    {
        public AbstractAnimation animation;
        public AbstractAnimation lowestAnimation;
        public boolean shouldUpdate;
        public boolean listenerRegistered;
    }
    
    protected static class AnimationInfo
    {
        public AnimationTarget target;
        public short type;
        public String attributeNamespaceURI;
        public String attributeLocalName;
    }
}
