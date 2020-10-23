// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.anim.values.AnimatablePaintValue;
import java.awt.Paint;
import org.apache.batik.anim.values.AnimatableColorValue;
import java.awt.Color;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.anim.values.AnimatableAngleOrIdentValue;
import org.apache.batik.anim.values.AnimatableAngleValue;
import org.apache.batik.anim.values.AnimatableNumberOrIdentValue;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.anim.values.AnimatableLengthOrIdentValue;
import org.apache.batik.css.engine.value.StringValue;
import org.apache.batik.anim.values.AnimatableStringValue;
import org.apache.batik.anim.values.AnimatablePathDataValue;
import org.apache.batik.parser.PathHandler;
import org.apache.batik.parser.PathArrayProducer;
import org.apache.batik.parser.PathParser;
import org.apache.batik.anim.values.AnimatablePointListValue;
import org.apache.batik.parser.PointsHandler;
import org.apache.batik.parser.PointsParser;
import org.apache.batik.anim.values.AnimatableRectValue;
import org.apache.batik.anim.values.AnimatableNumberListValue;
import org.apache.batik.parser.NumberListHandler;
import org.apache.batik.parser.FloatArrayProducer;
import org.apache.batik.parser.NumberListParser;
import org.apache.batik.anim.values.AnimatableLengthListValue;
import org.apache.batik.parser.LengthListHandler;
import org.apache.batik.parser.LengthArrayProducer;
import org.apache.batik.parser.LengthListParser;
import org.apache.batik.anim.values.AnimatableLengthValue;
import org.apache.batik.parser.DefaultLengthHandler;
import org.apache.batik.parser.LengthHandler;
import org.apache.batik.parser.LengthParser;
import org.apache.batik.anim.values.AnimatablePreserveAspectRatioValue;
import org.apache.batik.parser.PreserveAspectRatioHandler;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.DefaultPreserveAspectRatioHandler;
import org.apache.batik.parser.PreserveAspectRatioParser;
import org.apache.batik.anim.values.AnimatableNumberOrPercentageValue;
import org.apache.batik.anim.values.AnimatableNumberValue;
import org.apache.batik.anim.values.AnimatableIntegerValue;
import org.apache.batik.anim.values.AnimatableBooleanValue;
import java.util.Arrays;
import java.lang.ref.WeakReference;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.w3c.dom.Node;
import org.apache.batik.anim.timing.TimedElement;
import org.w3c.dom.events.EventTarget;
import java.util.HashSet;
import org.apache.batik.anim.AnimationException;
import java.util.Date;
import java.util.Calendar;
import org.apache.batik.anim.timing.TimedDocumentRoot;
import org.apache.batik.util.RunnableQueue;
import org.apache.batik.css.engine.value.Value;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.anim.dom.SVGStylableElement;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.anim.dom.AnimationTarget;
import org.w3c.dom.Element;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.w3c.dom.Document;
import java.util.Set;
import org.apache.batik.css.engine.StyleMap;
import java.util.LinkedList;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.anim.AnimationEngine;

public class SVGAnimationEngine extends AnimationEngine
{
    protected BridgeContext ctx;
    protected CSSEngine cssEngine;
    protected boolean started;
    protected AnimationTickRunnable animationTickRunnable;
    protected float initialStartTime;
    protected UncomputedAnimatableStringValueFactory uncomputedAnimatableStringValueFactory;
    protected AnimatableLengthOrIdentFactory animatableLengthOrIdentFactory;
    protected AnimatableNumberOrIdentFactory animatableNumberOrIdentFactory;
    protected Factory[] factories;
    protected boolean isSVG12;
    protected LinkedList initialBridges;
    protected StyleMap dummyStyleMap;
    protected AnimationThread animationThread;
    protected int animationLimitingMode;
    protected float animationLimitingAmount;
    protected static final Set animationEventNames11;
    protected static final Set animationEventNames12;
    
    public SVGAnimationEngine(final Document doc, final BridgeContext ctx) {
        super(doc);
        this.uncomputedAnimatableStringValueFactory = new UncomputedAnimatableStringValueFactory();
        this.animatableLengthOrIdentFactory = new AnimatableLengthOrIdentFactory();
        this.animatableNumberOrIdentFactory = new AnimatableNumberOrIdentFactory(false);
        this.factories = new Factory[] { null, new AnimatableIntegerValueFactory(), new AnimatableNumberValueFactory(), new AnimatableLengthValueFactory(), null, new AnimatableAngleValueFactory(), new AnimatableColorValueFactory(), new AnimatablePaintValueFactory(), null, null, this.uncomputedAnimatableStringValueFactory, null, null, new AnimatableNumberListValueFactory(), new AnimatableLengthListValueFactory(), this.uncomputedAnimatableStringValueFactory, this.uncomputedAnimatableStringValueFactory, this.animatableLengthOrIdentFactory, this.uncomputedAnimatableStringValueFactory, this.uncomputedAnimatableStringValueFactory, this.uncomputedAnimatableStringValueFactory, this.uncomputedAnimatableStringValueFactory, new AnimatablePathDataFactory(), this.uncomputedAnimatableStringValueFactory, null, this.animatableNumberOrIdentFactory, this.uncomputedAnimatableStringValueFactory, null, new AnimatableNumberOrIdentFactory(true), new AnimatableAngleOrIdentFactory(), null, new AnimatablePointListValueFactory(), new AnimatablePreserveAspectRatioValueFactory(), null, this.uncomputedAnimatableStringValueFactory, null, null, null, null, this.animatableLengthOrIdentFactory, this.animatableLengthOrIdentFactory, this.animatableLengthOrIdentFactory, this.animatableLengthOrIdentFactory, this.animatableLengthOrIdentFactory, this.animatableNumberOrIdentFactory, null, null, new AnimatableNumberOrPercentageValueFactory(), null, new AnimatableBooleanValueFactory(), new AnimatableRectValueFactory() };
        this.initialBridges = new LinkedList();
        this.ctx = ctx;
        final SVGOMDocument d = (SVGOMDocument)doc;
        this.cssEngine = d.getCSSEngine();
        this.dummyStyleMap = new StyleMap(this.cssEngine.getNumberOfProperties());
        this.isSVG12 = d.isSVG12();
    }
    
    @Override
    public void dispose() {
        synchronized (this) {
            this.pause();
            super.dispose();
        }
    }
    
    public void addInitialBridge(final SVGAnimationElementBridge b) {
        if (this.initialBridges != null) {
            this.initialBridges.add(b);
        }
    }
    
    public boolean hasStarted() {
        return this.started;
    }
    
    public AnimatableValue parseAnimatableValue(final Element animElt, final AnimationTarget target, final String ns, final String ln, final boolean isCSS, final String s) {
        final SVGOMElement elt = (SVGOMElement)target.getElement();
        int type;
        if (isCSS) {
            type = elt.getPropertyType(ln);
        }
        else {
            type = elt.getAttributeType(ns, ln);
        }
        final Factory factory = this.factories[type];
        if (factory == null) {
            final String an = (ns == null) ? ln : ('{' + ns + '}' + ln);
            throw new BridgeException(this.ctx, animElt, "attribute.not.animatable", new Object[] { target.getElement().getNodeName(), an });
        }
        return this.factories[type].createValue(target, ns, ln, isCSS, s);
    }
    
    public AnimatableValue getUnderlyingCSSValue(final Element animElt, final AnimationTarget target, final String pn) {
        final ValueManager[] vms = this.cssEngine.getValueManagers();
        final int idx = this.cssEngine.getPropertyIndex(pn);
        if (idx == -1) {
            return null;
        }
        final int type = vms[idx].getPropertyType();
        final Factory factory = this.factories[type];
        if (factory == null) {
            throw new BridgeException(this.ctx, animElt, "attribute.not.animatable", new Object[] { target.getElement().getNodeName(), pn });
        }
        final SVGStylableElement e = (SVGStylableElement)target.getElement();
        final CSSStyleDeclaration over = e.getOverrideStyle();
        final String oldValue = over.getPropertyValue(pn);
        if (oldValue != null) {
            over.removeProperty(pn);
        }
        final Value v = this.cssEngine.getComputedStyle(e, null, idx);
        if (oldValue != null && !oldValue.equals("")) {
            over.setProperty(pn, oldValue, null);
        }
        return this.factories[type].createValue(target, pn, v);
    }
    
    @Override
    public void pause() {
        super.pause();
        final UpdateManager um = this.ctx.getUpdateManager();
        if (um != null) {
            um.getUpdateRunnableQueue().setIdleRunnable(null);
        }
    }
    
    @Override
    public void unpause() {
        super.unpause();
        final UpdateManager um = this.ctx.getUpdateManager();
        if (um != null) {
            um.getUpdateRunnableQueue().setIdleRunnable(this.animationTickRunnable);
        }
    }
    
    @Override
    public float getCurrentTime() {
        final boolean p = this.pauseTime != 0L;
        this.unpause();
        final float t = this.timedDocumentRoot.getCurrentTime();
        if (p) {
            this.pause();
        }
        return Float.isNaN(t) ? 0.0f : t;
    }
    
    @Override
    public float setCurrentTime(final float t) {
        if (this.started) {
            final float ret = super.setCurrentTime(t);
            if (this.animationTickRunnable != null) {
                this.animationTickRunnable.resume();
            }
            return ret;
        }
        this.initialStartTime = t;
        return 0.0f;
    }
    
    @Override
    protected TimedDocumentRoot createDocumentRoot() {
        return new AnimationRoot();
    }
    
    public void start(final long documentStartTime) {
        if (this.started) {
            return;
        }
        this.started = true;
        try {
            try {
                final Calendar cal = Calendar.getInstance();
                cal.setTime(new Date(documentStartTime));
                this.timedDocumentRoot.resetDocument(cal);
                final Object[] bridges = this.initialBridges.toArray();
                this.initialBridges = null;
                for (final Object bridge2 : bridges) {
                    final SVGAnimationElementBridge bridge3 = (SVGAnimationElementBridge)bridge2;
                    bridge3.initializeAnimation();
                }
                for (final Object bridge4 : bridges) {
                    final SVGAnimationElementBridge bridge3 = (SVGAnimationElementBridge)bridge4;
                    bridge3.initializeTimedElement();
                }
                final UpdateManager um = this.ctx.getUpdateManager();
                if (um != null) {
                    final RunnableQueue q = um.getUpdateRunnableQueue();
                    q.setIdleRunnable(this.animationTickRunnable = new AnimationTickRunnable(q, this));
                    if (this.initialStartTime != 0.0f) {
                        this.setCurrentTime(this.initialStartTime);
                    }
                }
            }
            catch (AnimationException ex) {
                throw new BridgeException(this.ctx, ex.getElement().getElement(), ex.getMessage());
            }
        }
        catch (Exception ex2) {
            if (this.ctx.getUserAgent() == null) {
                ex2.printStackTrace();
            }
            else {
                this.ctx.getUserAgent().displayError(ex2);
            }
        }
    }
    
    public void setAnimationLimitingNone() {
        this.animationLimitingMode = 0;
    }
    
    public void setAnimationLimitingCPU(final float pc) {
        this.animationLimitingMode = 1;
        this.animationLimitingAmount = pc;
    }
    
    public void setAnimationLimitingFPS(final float fps) {
        this.animationLimitingMode = 2;
        this.animationLimitingAmount = fps;
    }
    
    static {
        animationEventNames11 = new HashSet();
        animationEventNames12 = new HashSet();
        final String[] eventNamesCommon = { "click", "mousedown", "mouseup", "mouseover", "mousemove", "mouseout", "beginEvent", "endEvent" };
        final String[] eventNamesSVG11 = { "DOMSubtreeModified", "DOMNodeInserted", "DOMNodeRemoved", "DOMNodeRemovedFromDocument", "DOMNodeInsertedIntoDocument", "DOMAttrModified", "DOMCharacterDataModified", "SVGLoad", "SVGUnload", "SVGAbort", "SVGError", "SVGResize", "SVGScroll", "repeatEvent" };
        final String[] eventNamesSVG12 = { "load", "resize", "scroll", "zoom" };
        for (final String anEventNamesCommon : eventNamesCommon) {
            SVGAnimationEngine.animationEventNames11.add(anEventNamesCommon);
            SVGAnimationEngine.animationEventNames12.add(anEventNamesCommon);
        }
        for (final String anEventNamesSVG11 : eventNamesSVG11) {
            SVGAnimationEngine.animationEventNames11.add(anEventNamesSVG11);
        }
        for (final String anEventNamesSVG12 : eventNamesSVG12) {
            SVGAnimationEngine.animationEventNames12.add(anEventNamesSVG12);
        }
    }
    
    protected class AnimationRoot extends TimedDocumentRoot
    {
        public AnimationRoot() {
            super(!SVGAnimationEngine.this.isSVG12, SVGAnimationEngine.this.isSVG12);
        }
        
        @Override
        protected String getEventNamespaceURI(final String eventName) {
            if (!SVGAnimationEngine.this.isSVG12) {
                return null;
            }
            if (eventName.equals("focusin") || eventName.equals("focusout") || eventName.equals("activate") || SVGAnimationEngine.animationEventNames12.contains(eventName)) {
                return "http://www.w3.org/2001/xml-events";
            }
            return null;
        }
        
        @Override
        protected String getEventType(final String eventName) {
            if (eventName.equals("focusin")) {
                return "DOMFocusIn";
            }
            if (eventName.equals("focusout")) {
                return "DOMFocusOut";
            }
            if (eventName.equals("activate")) {
                return "DOMActivate";
            }
            if (SVGAnimationEngine.this.isSVG12) {
                if (SVGAnimationEngine.animationEventNames12.contains(eventName)) {
                    return eventName;
                }
            }
            else if (SVGAnimationEngine.animationEventNames11.contains(eventName)) {
                return eventName;
            }
            return null;
        }
        
        @Override
        protected String getRepeatEventName() {
            return "repeatEvent";
        }
        
        @Override
        protected void fireTimeEvent(final String eventType, final Calendar time, final int detail) {
            AnimationSupport.fireTimeEvent((EventTarget)SVGAnimationEngine.this.document, eventType, time, detail);
        }
        
        @Override
        protected void toActive(final float begin) {
        }
        
        @Override
        protected void toInactive(final boolean stillActive, final boolean isFrozen) {
        }
        
        @Override
        protected void removeFill() {
        }
        
        @Override
        protected void sampledAt(final float simpleTime, final float simpleDur, final int repeatIteration) {
        }
        
        @Override
        protected void sampledLastValue(final int repeatIteration) {
        }
        
        @Override
        protected TimedElement getTimedElementById(final String id) {
            return AnimationSupport.getTimedElementById(id, SVGAnimationEngine.this.document);
        }
        
        @Override
        protected EventTarget getEventTargetById(final String id) {
            return AnimationSupport.getEventTargetById(id, SVGAnimationEngine.this.document);
        }
        
        @Override
        protected EventTarget getAnimationEventTarget() {
            return null;
        }
        
        @Override
        protected EventTarget getRootEventTarget() {
            return (EventTarget)SVGAnimationEngine.this.document;
        }
        
        @Override
        public Element getElement() {
            return null;
        }
        
        @Override
        public boolean isBefore(final TimedElement other) {
            return false;
        }
        
        @Override
        protected void currentIntervalWillUpdate() {
            if (SVGAnimationEngine.this.animationTickRunnable != null) {
                SVGAnimationEngine.this.animationTickRunnable.resume();
            }
        }
    }
    
    protected static class DebugAnimationTickRunnable extends AnimationTickRunnable
    {
        float t;
        
        public DebugAnimationTickRunnable(final RunnableQueue q, final SVGAnimationEngine eng) {
            super(q, eng);
            this.t = 0.0f;
            this.waitTime = Long.MAX_VALUE;
            new Thread() {
                @Override
                public void run() {
                    final BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
                    System.out.println("Enter times.");
                    while (true) {
                        String s;
                        try {
                            s = r.readLine();
                        }
                        catch (IOException e) {
                            s = null;
                        }
                        if (s == null) {
                            System.exit(0);
                        }
                        DebugAnimationTickRunnable.this.t = Float.parseFloat(s);
                        DebugAnimationTickRunnable.this.resume();
                    }
                }
            }.start();
        }
        
        @Override
        public void resume() {
            this.waitTime = 0L;
            final Object lock = this.q.getIteratorLock();
            synchronized (lock) {
                lock.notify();
            }
        }
        
        @Override
        public long getWaitTime() {
            final long wt = this.waitTime;
            this.waitTime = Long.MAX_VALUE;
            return wt;
        }
        
        @Override
        public void run() {
            final SVGAnimationEngine eng = this.getAnimationEngine();
            synchronized (eng) {
                try {
                    try {
                        eng.tick(this.t, false);
                    }
                    catch (AnimationException ex) {
                        throw new BridgeException(eng.ctx, ex.getElement().getElement(), ex.getMessage());
                    }
                }
                catch (Exception ex2) {
                    if (eng.ctx.getUserAgent() == null) {
                        ex2.printStackTrace();
                    }
                    else {
                        eng.ctx.getUserAgent().displayError(ex2);
                    }
                }
            }
        }
    }
    
    protected static class AnimationTickRunnable implements RunnableQueue.IdleRunnable
    {
        protected Calendar time;
        protected long waitTime;
        protected RunnableQueue q;
        private static final int NUM_TIMES = 8;
        protected long[] times;
        protected long sumTime;
        protected int timeIndex;
        protected WeakReference engRef;
        protected static final int MAX_EXCEPTION_COUNT = 10;
        protected int exceptionCount;
        
        public AnimationTickRunnable(final RunnableQueue q, final SVGAnimationEngine eng) {
            this.time = Calendar.getInstance();
            this.times = new long[8];
            this.q = q;
            this.engRef = new WeakReference((T)eng);
            Arrays.fill(this.times, 100L);
            this.sumTime = 800L;
        }
        
        public void resume() {
            this.waitTime = 0L;
            final Object lock = this.q.getIteratorLock();
            synchronized (lock) {
                lock.notify();
            }
        }
        
        @Override
        public long getWaitTime() {
            return this.waitTime;
        }
        
        @Override
        public void run() {
            final SVGAnimationEngine eng = this.getAnimationEngine();
            synchronized (eng) {
                final int animationLimitingMode = eng.animationLimitingMode;
                final float animationLimitingAmount = eng.animationLimitingAmount;
                try {
                    try {
                        final long before = System.currentTimeMillis();
                        this.time.setTime(new Date(before));
                        final float t = eng.timedDocumentRoot.convertWallclockTime(this.time);
                        final float t2 = eng.tick(t, false);
                        final long after = System.currentTimeMillis();
                        long dur = after - before;
                        if (dur == 0L) {
                            dur = 1L;
                        }
                        this.sumTime -= this.times[this.timeIndex];
                        this.sumTime += dur;
                        this.times[this.timeIndex] = dur;
                        this.timeIndex = (this.timeIndex + 1) % 8;
                        if (t2 == Float.POSITIVE_INFINITY) {
                            this.waitTime = Long.MAX_VALUE;
                        }
                        else {
                            this.waitTime = before + (long)(t2 * 1000.0f) - 1000L;
                            if (this.waitTime < after) {
                                this.waitTime = after;
                            }
                            if (animationLimitingMode != 0) {
                                final float ave = this.sumTime / 8.0f;
                                float delay;
                                if (animationLimitingMode == 1) {
                                    delay = ave / animationLimitingAmount - ave;
                                }
                                else {
                                    delay = 1000.0f / animationLimitingAmount - ave;
                                }
                                final long newWaitTime = after + (long)delay;
                                if (newWaitTime > this.waitTime) {
                                    this.waitTime = newWaitTime;
                                }
                            }
                        }
                    }
                    catch (AnimationException ex) {
                        throw new BridgeException(eng.ctx, ex.getElement().getElement(), ex.getMessage());
                    }
                    this.exceptionCount = 0;
                }
                catch (Exception ex2) {
                    if (++this.exceptionCount < 10) {
                        if (eng.ctx.getUserAgent() == null) {
                            ex2.printStackTrace();
                        }
                        else {
                            eng.ctx.getUserAgent().displayError(ex2);
                        }
                    }
                }
                if (animationLimitingMode == 0) {
                    try {
                        Thread.sleep(1L);
                    }
                    catch (InterruptedException ex3) {}
                }
            }
        }
        
        protected SVGAnimationEngine getAnimationEngine() {
            return (SVGAnimationEngine)this.engRef.get();
        }
    }
    
    protected class AnimationThread extends Thread
    {
        protected Calendar time;
        protected RunnableQueue runnableQueue;
        protected Ticker ticker;
        
        protected AnimationThread() {
            this.time = Calendar.getInstance();
            this.runnableQueue = SVGAnimationEngine.this.ctx.getUpdateManager().getUpdateRunnableQueue();
            this.ticker = new Ticker();
        }
        
        @Override
        public void run() {
            while (true) {
                this.time.setTime(new Date());
                this.ticker.t = SVGAnimationEngine.this.timedDocumentRoot.convertWallclockTime(this.time);
                try {
                    this.runnableQueue.invokeAndWait(this.ticker);
                }
                catch (InterruptedException e) {}
            }
        }
        
        protected class Ticker implements Runnable
        {
            protected float t;
            
            @Override
            public void run() {
                AnimationEngine.this.tick(this.t, false);
            }
        }
    }
    
    protected abstract class CSSValueFactory implements Factory
    {
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String ns, final String ln, final boolean isCSS, final String s) {
            return this.createValue(target, ln, this.createCSSValue(target, ln, s));
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String pn, Value v) {
            final CSSStylableElement elt = (CSSStylableElement)target.getElement();
            v = this.computeValue(elt, pn, v);
            return this.createAnimatableValue(target, pn, v);
        }
        
        protected abstract AnimatableValue createAnimatableValue(final AnimationTarget p0, final String p1, final Value p2);
        
        protected Value createCSSValue(final AnimationTarget t, final String pn, final String s) {
            final CSSStylableElement elt = (CSSStylableElement)t.getElement();
            final Value v = SVGAnimationEngine.this.cssEngine.parsePropertyValue(elt, pn, s);
            return this.computeValue(elt, pn, v);
        }
        
        protected Value computeValue(CSSStylableElement elt, final String pn, Value v) {
            final ValueManager[] vms = SVGAnimationEngine.this.cssEngine.getValueManagers();
            final int idx = SVGAnimationEngine.this.cssEngine.getPropertyIndex(pn);
            if (idx != -1) {
                if (v.getCssValueType() == 0) {
                    elt = CSSEngine.getParentCSSStylableElement(elt);
                    if (elt != null) {
                        return SVGAnimationEngine.this.cssEngine.getComputedStyle(elt, null, idx);
                    }
                    return vms[idx].getDefaultValue();
                }
                else {
                    v = vms[idx].computeValue(elt, null, SVGAnimationEngine.this.cssEngine, idx, SVGAnimationEngine.this.dummyStyleMap, v);
                }
            }
            return v;
        }
    }
    
    protected static class AnimatableBooleanValueFactory implements Factory
    {
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String ns, final String ln, final boolean isCSS, final String s) {
            return new AnimatableBooleanValue(target, "true".equals(s));
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String pn, final Value v) {
            return new AnimatableBooleanValue(target, "true".equals(v.getCssText()));
        }
    }
    
    protected static class AnimatableIntegerValueFactory implements Factory
    {
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String ns, final String ln, final boolean isCSS, final String s) {
            return new AnimatableIntegerValue(target, Integer.parseInt(s));
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String pn, final Value v) {
            return new AnimatableIntegerValue(target, Math.round(v.getFloatValue()));
        }
    }
    
    protected static class AnimatableNumberValueFactory implements Factory
    {
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String ns, final String ln, final boolean isCSS, final String s) {
            return new AnimatableNumberValue(target, Float.parseFloat(s));
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String pn, final Value v) {
            return new AnimatableNumberValue(target, v.getFloatValue());
        }
    }
    
    protected static class AnimatableNumberOrPercentageValueFactory implements Factory
    {
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String ns, final String ln, final boolean isCSS, final String s) {
            float v;
            boolean pc;
            if (s.charAt(s.length() - 1) == '%') {
                v = Float.parseFloat(s.substring(0, s.length() - 1));
                pc = true;
            }
            else {
                v = Float.parseFloat(s);
                pc = false;
            }
            return new AnimatableNumberOrPercentageValue(target, v, pc);
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String pn, final Value v) {
            switch (v.getPrimitiveType()) {
                case 2: {
                    return new AnimatableNumberOrPercentageValue(target, v.getFloatValue(), true);
                }
                case 1: {
                    return new AnimatableNumberOrPercentageValue(target, v.getFloatValue());
                }
                default: {
                    return null;
                }
            }
        }
    }
    
    protected static class AnimatablePreserveAspectRatioValueFactory implements Factory
    {
        protected short align;
        protected short meetOrSlice;
        protected PreserveAspectRatioParser parser;
        protected DefaultPreserveAspectRatioHandler handler;
        
        public AnimatablePreserveAspectRatioValueFactory() {
            this.parser = new PreserveAspectRatioParser();
            this.handler = new DefaultPreserveAspectRatioHandler() {
                @Override
                public void startPreserveAspectRatio() throws ParseException {
                    AnimatablePreserveAspectRatioValueFactory.this.align = 0;
                    AnimatablePreserveAspectRatioValueFactory.this.meetOrSlice = 0;
                }
                
                @Override
                public void none() throws ParseException {
                    AnimatablePreserveAspectRatioValueFactory.this.align = 1;
                }
                
                @Override
                public void xMaxYMax() throws ParseException {
                    AnimatablePreserveAspectRatioValueFactory.this.align = 10;
                }
                
                @Override
                public void xMaxYMid() throws ParseException {
                    AnimatablePreserveAspectRatioValueFactory.this.align = 7;
                }
                
                @Override
                public void xMaxYMin() throws ParseException {
                    AnimatablePreserveAspectRatioValueFactory.this.align = 4;
                }
                
                @Override
                public void xMidYMax() throws ParseException {
                    AnimatablePreserveAspectRatioValueFactory.this.align = 9;
                }
                
                @Override
                public void xMidYMid() throws ParseException {
                    AnimatablePreserveAspectRatioValueFactory.this.align = 6;
                }
                
                @Override
                public void xMidYMin() throws ParseException {
                    AnimatablePreserveAspectRatioValueFactory.this.align = 3;
                }
                
                @Override
                public void xMinYMax() throws ParseException {
                    AnimatablePreserveAspectRatioValueFactory.this.align = 8;
                }
                
                @Override
                public void xMinYMid() throws ParseException {
                    AnimatablePreserveAspectRatioValueFactory.this.align = 5;
                }
                
                @Override
                public void xMinYMin() throws ParseException {
                    AnimatablePreserveAspectRatioValueFactory.this.align = 2;
                }
                
                @Override
                public void meet() throws ParseException {
                    AnimatablePreserveAspectRatioValueFactory.this.meetOrSlice = 1;
                }
                
                @Override
                public void slice() throws ParseException {
                    AnimatablePreserveAspectRatioValueFactory.this.meetOrSlice = 2;
                }
            };
            this.parser.setPreserveAspectRatioHandler(this.handler);
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String ns, final String ln, final boolean isCSS, final String s) {
            try {
                this.parser.parse(s);
                return new AnimatablePreserveAspectRatioValue(target, this.align, this.meetOrSlice);
            }
            catch (ParseException e) {
                return null;
            }
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String pn, final Value v) {
            return null;
        }
    }
    
    protected static class AnimatableLengthValueFactory implements Factory
    {
        protected short type;
        protected float value;
        protected LengthParser parser;
        protected LengthHandler handler;
        
        public AnimatableLengthValueFactory() {
            this.parser = new LengthParser();
            this.handler = new DefaultLengthHandler() {
                @Override
                public void startLength() throws ParseException {
                    AnimatableLengthValueFactory.this.type = 1;
                }
                
                @Override
                public void lengthValue(final float v) throws ParseException {
                    AnimatableLengthValueFactory.this.value = v;
                }
                
                @Override
                public void em() throws ParseException {
                    AnimatableLengthValueFactory.this.type = 3;
                }
                
                @Override
                public void ex() throws ParseException {
                    AnimatableLengthValueFactory.this.type = 4;
                }
                
                @Override
                public void in() throws ParseException {
                    AnimatableLengthValueFactory.this.type = 8;
                }
                
                @Override
                public void cm() throws ParseException {
                    AnimatableLengthValueFactory.this.type = 6;
                }
                
                @Override
                public void mm() throws ParseException {
                    AnimatableLengthValueFactory.this.type = 7;
                }
                
                @Override
                public void pc() throws ParseException {
                    AnimatableLengthValueFactory.this.type = 10;
                }
                
                @Override
                public void pt() throws ParseException {
                    AnimatableLengthValueFactory.this.type = 9;
                }
                
                @Override
                public void px() throws ParseException {
                    AnimatableLengthValueFactory.this.type = 5;
                }
                
                @Override
                public void percentage() throws ParseException {
                    AnimatableLengthValueFactory.this.type = 2;
                }
                
                @Override
                public void endLength() throws ParseException {
                }
            };
            this.parser.setLengthHandler(this.handler);
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String ns, final String ln, final boolean isCSS, final String s) {
            final short pcInterp = target.getPercentageInterpretation(ns, ln, isCSS);
            try {
                this.parser.parse(s);
                return new AnimatableLengthValue(target, this.type, this.value, pcInterp);
            }
            catch (ParseException e) {
                return null;
            }
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String pn, final Value v) {
            return new AnimatableIntegerValue(target, Math.round(v.getFloatValue()));
        }
    }
    
    protected static class AnimatableLengthListValueFactory implements Factory
    {
        protected LengthListParser parser;
        protected LengthArrayProducer producer;
        
        public AnimatableLengthListValueFactory() {
            this.parser = new LengthListParser();
            this.producer = new LengthArrayProducer();
            this.parser.setLengthListHandler(this.producer);
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String ns, final String ln, final boolean isCSS, final String s) {
            try {
                final short pcInterp = target.getPercentageInterpretation(ns, ln, isCSS);
                this.parser.parse(s);
                return new AnimatableLengthListValue(target, this.producer.getLengthTypeArray(), this.producer.getLengthValueArray(), pcInterp);
            }
            catch (ParseException e) {
                return null;
            }
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String pn, final Value v) {
            return null;
        }
    }
    
    protected static class AnimatableNumberListValueFactory implements Factory
    {
        protected NumberListParser parser;
        protected FloatArrayProducer producer;
        
        public AnimatableNumberListValueFactory() {
            this.parser = new NumberListParser();
            this.producer = new FloatArrayProducer();
            this.parser.setNumberListHandler(this.producer);
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String ns, final String ln, final boolean isCSS, final String s) {
            try {
                this.parser.parse(s);
                return new AnimatableNumberListValue(target, this.producer.getFloatArray());
            }
            catch (ParseException e) {
                return null;
            }
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String pn, final Value v) {
            return null;
        }
    }
    
    protected static class AnimatableRectValueFactory implements Factory
    {
        protected NumberListParser parser;
        protected FloatArrayProducer producer;
        
        public AnimatableRectValueFactory() {
            this.parser = new NumberListParser();
            this.producer = new FloatArrayProducer();
            this.parser.setNumberListHandler(this.producer);
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String ns, final String ln, final boolean isCSS, final String s) {
            try {
                this.parser.parse(s);
                final float[] r = this.producer.getFloatArray();
                if (r.length != 4) {
                    return null;
                }
                return new AnimatableRectValue(target, r[0], r[1], r[2], r[3]);
            }
            catch (ParseException e) {
                return null;
            }
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String pn, final Value v) {
            return null;
        }
    }
    
    protected static class AnimatablePointListValueFactory implements Factory
    {
        protected PointsParser parser;
        protected FloatArrayProducer producer;
        
        public AnimatablePointListValueFactory() {
            this.parser = new PointsParser();
            this.producer = new FloatArrayProducer();
            this.parser.setPointsHandler(this.producer);
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String ns, final String ln, final boolean isCSS, final String s) {
            try {
                this.parser.parse(s);
                return new AnimatablePointListValue(target, this.producer.getFloatArray());
            }
            catch (ParseException e) {
                return null;
            }
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String pn, final Value v) {
            return null;
        }
    }
    
    protected static class AnimatablePathDataFactory implements Factory
    {
        protected PathParser parser;
        protected PathArrayProducer producer;
        
        public AnimatablePathDataFactory() {
            this.parser = new PathParser();
            this.producer = new PathArrayProducer();
            this.parser.setPathHandler(this.producer);
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String ns, final String ln, final boolean isCSS, final String s) {
            try {
                this.parser.parse(s);
                return new AnimatablePathDataValue(target, this.producer.getPathCommands(), this.producer.getPathParameters());
            }
            catch (ParseException e) {
                return null;
            }
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String pn, final Value v) {
            return null;
        }
    }
    
    protected static class UncomputedAnimatableStringValueFactory implements Factory
    {
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String ns, final String ln, final boolean isCSS, final String s) {
            return new AnimatableStringValue(target, s);
        }
        
        @Override
        public AnimatableValue createValue(final AnimationTarget target, final String pn, final Value v) {
            return new AnimatableStringValue(target, v.getCssText());
        }
    }
    
    protected class AnimatableLengthOrIdentFactory extends CSSValueFactory
    {
        @Override
        protected AnimatableValue createAnimatableValue(final AnimationTarget target, final String pn, final Value v) {
            if (v instanceof StringValue) {
                return new AnimatableLengthOrIdentValue(target, v.getStringValue());
            }
            final short pcInterp = target.getPercentageInterpretation(null, pn, true);
            final FloatValue fv = (FloatValue)v;
            return new AnimatableLengthOrIdentValue(target, fv.getPrimitiveType(), fv.getFloatValue(), pcInterp);
        }
    }
    
    protected class AnimatableNumberOrIdentFactory extends CSSValueFactory
    {
        protected boolean numericIdents;
        
        public AnimatableNumberOrIdentFactory(final boolean numericIdents) {
            this.numericIdents = numericIdents;
        }
        
        @Override
        protected AnimatableValue createAnimatableValue(final AnimationTarget target, final String pn, final Value v) {
            if (v instanceof StringValue) {
                return new AnimatableNumberOrIdentValue(target, v.getStringValue());
            }
            final FloatValue fv = (FloatValue)v;
            return new AnimatableNumberOrIdentValue(target, fv.getFloatValue(), this.numericIdents);
        }
    }
    
    protected class AnimatableAngleValueFactory extends CSSValueFactory
    {
        @Override
        protected AnimatableValue createAnimatableValue(final AnimationTarget target, final String pn, final Value v) {
            final FloatValue fv = (FloatValue)v;
            short unit = 0;
            switch (fv.getPrimitiveType()) {
                case 1:
                case 11: {
                    unit = 2;
                    break;
                }
                case 12: {
                    unit = 3;
                    break;
                }
                case 13: {
                    unit = 4;
                    break;
                }
                default: {
                    return null;
                }
            }
            return new AnimatableAngleValue(target, fv.getFloatValue(), unit);
        }
    }
    
    protected class AnimatableAngleOrIdentFactory extends CSSValueFactory
    {
        @Override
        protected AnimatableValue createAnimatableValue(final AnimationTarget target, final String pn, final Value v) {
            if (v instanceof StringValue) {
                return new AnimatableAngleOrIdentValue(target, v.getStringValue());
            }
            final FloatValue fv = (FloatValue)v;
            short unit = 0;
            switch (fv.getPrimitiveType()) {
                case 1:
                case 11: {
                    unit = 2;
                    break;
                }
                case 12: {
                    unit = 3;
                    break;
                }
                case 13: {
                    unit = 4;
                    break;
                }
                default: {
                    return null;
                }
            }
            return new AnimatableAngleOrIdentValue(target, fv.getFloatValue(), unit);
        }
    }
    
    protected class AnimatableColorValueFactory extends CSSValueFactory
    {
        @Override
        protected AnimatableValue createAnimatableValue(final AnimationTarget target, final String pn, final Value v) {
            final Paint p = PaintServer.convertPaint(target.getElement(), null, v, 1.0f, SVGAnimationEngine.this.ctx);
            if (p instanceof Color) {
                final Color c = (Color)p;
                return new AnimatableColorValue(target, c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);
            }
            return null;
        }
    }
    
    protected class AnimatablePaintValueFactory extends CSSValueFactory
    {
        protected AnimatablePaintValue createColorPaintValue(final AnimationTarget t, final Color c) {
            return AnimatablePaintValue.createColorPaintValue(t, c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);
        }
        
        @Override
        protected AnimatableValue createAnimatableValue(final AnimationTarget target, final String pn, final Value v) {
            Label_0270: {
                if (v.getCssValueType() == 1) {
                    switch (v.getPrimitiveType()) {
                        case 21: {
                            return AnimatablePaintValue.createNonePaintValue(target);
                        }
                        case 25: {
                            final Paint p = PaintServer.convertPaint(target.getElement(), null, v, 1.0f, SVGAnimationEngine.this.ctx);
                            return this.createColorPaintValue(target, (Color)p);
                        }
                        case 20: {
                            return AnimatablePaintValue.createURIPaintValue(target, v.getStringValue());
                        }
                    }
                }
                else {
                    final Value v2 = v.item(0);
                    switch (v2.getPrimitiveType()) {
                        case 25: {
                            final Paint p2 = PaintServer.convertPaint(target.getElement(), null, v, 1.0f, SVGAnimationEngine.this.ctx);
                            return this.createColorPaintValue(target, (Color)p2);
                        }
                        case 20: {
                            final Value v3 = v.item(1);
                            switch (v3.getPrimitiveType()) {
                                case 21: {
                                    return AnimatablePaintValue.createURINonePaintValue(target, v2.getStringValue());
                                }
                                case 25: {
                                    final Paint p3 = PaintServer.convertPaint(target.getElement(), null, v.item(1), 1.0f, SVGAnimationEngine.this.ctx);
                                    return this.createColorPaintValue(target, (Color)p3);
                                }
                                default: {
                                    break Label_0270;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            return null;
        }
    }
    
    protected class AnimatableStringValueFactory extends CSSValueFactory
    {
        @Override
        protected AnimatableValue createAnimatableValue(final AnimationTarget target, final String pn, final Value v) {
            return new AnimatableStringValue(target, v.getCssText());
        }
    }
    
    protected interface Factory
    {
        AnimatableValue createValue(final AnimationTarget p0, final String p1, final String p2, final boolean p3, final String p4);
        
        AnimatableValue createValue(final AnimationTarget p0, final String p1, final Value p2);
    }
}
