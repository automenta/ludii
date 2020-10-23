// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.Iterator;
import java.util.Collection;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.DocumentEvent;
import org.apache.batik.dom.events.AbstractEvent;
import org.apache.batik.gvt.RootGraphicsNode;
import java.awt.image.BufferedImage;
import org.apache.batik.gvt.event.GraphicsNodeChangeListener;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.bridge.svg12.SVG12BridgeContext;
import org.apache.batik.bridge.svg12.DefaultXBLManager;
import org.apache.batik.bridge.svg12.SVG12ScriptingEnvironment;
import org.apache.batik.anim.dom.SVGOMDocument;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import org.apache.batik.util.EventDispatcher;
import java.util.TimerTask;
import java.util.Timer;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.UpdateTracker;
import java.util.List;
import org.apache.batik.util.RunnableQueue;
import org.w3c.dom.Document;

public class UpdateManager
{
    static final int MIN_REPAINT_TIME;
    protected BridgeContext bridgeContext;
    protected Document document;
    protected RunnableQueue updateRunnableQueue;
    protected RunnableQueue.RunHandler runHandler;
    protected volatile boolean running;
    protected volatile boolean suspendCalled;
    protected List listeners;
    protected ScriptingEnvironment scriptingEnvironment;
    protected RepaintManager repaintManager;
    protected UpdateTracker updateTracker;
    protected GraphicsNode graphicsNode;
    protected boolean started;
    protected BridgeContext[] secondaryBridgeContexts;
    protected ScriptingEnvironment[] secondaryScriptingEnvironments;
    protected int minRepaintTime;
    long outOfDateTime;
    List suspensionList;
    int nextSuspensionIndex;
    long allResumeTime;
    Timer repaintTriggerTimer;
    TimerTask repaintTimerTask;
    static EventDispatcher.Dispatcher startedDispatcher;
    static EventDispatcher.Dispatcher stoppedDispatcher;
    static EventDispatcher.Dispatcher suspendedDispatcher;
    static EventDispatcher.Dispatcher resumedDispatcher;
    static EventDispatcher.Dispatcher updateStartedDispatcher;
    static EventDispatcher.Dispatcher updateCompletedDispatcher;
    static EventDispatcher.Dispatcher updateFailedDispatcher;
    
    public UpdateManager(final BridgeContext ctx, final GraphicsNode gn, final Document doc) {
        this.listeners = Collections.synchronizedList(new LinkedList<Object>());
        this.outOfDateTime = 0L;
        this.suspensionList = new ArrayList();
        this.nextSuspensionIndex = 1;
        this.allResumeTime = -1L;
        this.repaintTriggerTimer = null;
        this.repaintTimerTask = null;
        (this.bridgeContext = ctx).setUpdateManager(this);
        this.document = doc;
        this.updateRunnableQueue = RunnableQueue.createRunnableQueue();
        this.runHandler = this.createRunHandler();
        this.updateRunnableQueue.setRunHandler(this.runHandler);
        this.graphicsNode = gn;
        this.scriptingEnvironment = this.initializeScriptingEnvironment(this.bridgeContext);
        this.secondaryBridgeContexts = ctx.getChildContexts().clone();
        this.secondaryScriptingEnvironments = new ScriptingEnvironment[this.secondaryBridgeContexts.length];
        for (int i = 0; i < this.secondaryBridgeContexts.length; ++i) {
            final BridgeContext resCtx = this.secondaryBridgeContexts[i];
            if (((SVGOMDocument)resCtx.getDocument()).isSVG12()) {
                resCtx.setUpdateManager(this);
                final ScriptingEnvironment se = this.initializeScriptingEnvironment(resCtx);
                this.secondaryScriptingEnvironments[i] = se;
            }
        }
        this.minRepaintTime = UpdateManager.MIN_REPAINT_TIME;
    }
    
    public int getMinRepaintTime() {
        return this.minRepaintTime;
    }
    
    public void setMinRepaintTime(final int minRepaintTime) {
        this.minRepaintTime = minRepaintTime;
    }
    
    protected ScriptingEnvironment initializeScriptingEnvironment(final BridgeContext ctx) {
        final SVGOMDocument d = (SVGOMDocument)ctx.getDocument();
        ScriptingEnvironment se;
        if (d.isSVG12()) {
            se = new SVG12ScriptingEnvironment(ctx);
            d.setXBLManager(ctx.xblManager = new DefaultXBLManager(d, ctx));
        }
        else {
            se = new ScriptingEnvironment(ctx);
        }
        return se;
    }
    
    public synchronized void dispatchSVGLoadEvent() throws InterruptedException {
        this.dispatchSVGLoadEvent(this.bridgeContext, this.scriptingEnvironment);
        for (int i = 0; i < this.secondaryScriptingEnvironments.length; ++i) {
            final BridgeContext ctx = this.secondaryBridgeContexts[i];
            if (((SVGOMDocument)ctx.getDocument()).isSVG12()) {
                final ScriptingEnvironment se = this.secondaryScriptingEnvironments[i];
                this.dispatchSVGLoadEvent(ctx, se);
            }
        }
        this.secondaryBridgeContexts = null;
        this.secondaryScriptingEnvironments = null;
    }
    
    protected void dispatchSVGLoadEvent(final BridgeContext ctx, final ScriptingEnvironment se) {
        se.loadScripts();
        se.dispatchSVGLoadEvent();
        if (ctx.isSVG12() && ctx.xblManager != null) {
            final SVG12BridgeContext ctx2 = (SVG12BridgeContext)ctx;
            ctx2.addBindingListener();
            ctx2.xblManager.startProcessing();
        }
    }
    
    public void dispatchSVGZoomEvent() throws InterruptedException {
        this.scriptingEnvironment.dispatchSVGZoomEvent();
    }
    
    public void dispatchSVGScrollEvent() throws InterruptedException {
        this.scriptingEnvironment.dispatchSVGScrollEvent();
    }
    
    public void dispatchSVGResizeEvent() throws InterruptedException {
        this.scriptingEnvironment.dispatchSVGResizeEvent();
    }
    
    public void manageUpdates(final ImageRenderer r) {
        this.updateRunnableQueue.preemptLater(new Runnable() {
            @Override
            public void run() {
                synchronized (UpdateManager.this) {
                    UpdateManager.this.running = true;
                    UpdateManager.this.updateTracker = new UpdateTracker();
                    final RootGraphicsNode root = UpdateManager.this.graphicsNode.getRoot();
                    if (root != null) {
                        root.addTreeGraphicsNodeChangeListener(UpdateManager.this.updateTracker);
                    }
                    UpdateManager.this.repaintManager = new RepaintManager(r);
                    final UpdateManagerEvent ev = new UpdateManagerEvent(UpdateManager.this, null, null);
                    UpdateManager.this.fireEvent(UpdateManager.startedDispatcher, ev);
                    UpdateManager.this.started = true;
                }
            }
        });
        this.resume();
    }
    
    public BridgeContext getBridgeContext() {
        return this.bridgeContext;
    }
    
    public RunnableQueue getUpdateRunnableQueue() {
        return this.updateRunnableQueue;
    }
    
    public RepaintManager getRepaintManager() {
        return this.repaintManager;
    }
    
    public UpdateTracker getUpdateTracker() {
        return this.updateTracker;
    }
    
    public Document getDocument() {
        return this.document;
    }
    
    public ScriptingEnvironment getScriptingEnvironment() {
        return this.scriptingEnvironment;
    }
    
    public synchronized boolean isRunning() {
        return this.running;
    }
    
    public synchronized void suspend() {
        if (this.updateRunnableQueue.getQueueState() == RunnableQueue.RUNNING) {
            this.updateRunnableQueue.suspendExecution(false);
        }
        this.suspendCalled = true;
    }
    
    public synchronized void resume() {
        if (this.updateRunnableQueue.getQueueState() != RunnableQueue.RUNNING) {
            this.updateRunnableQueue.resumeExecution();
        }
    }
    
    public void interrupt() {
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                synchronized (UpdateManager.this) {
                    if (UpdateManager.this.started) {
                        UpdateManager.this.dispatchSVGUnLoadEvent();
                    }
                    else {
                        UpdateManager.this.running = false;
                        UpdateManager.this.scriptingEnvironment.interrupt();
                        UpdateManager.this.updateRunnableQueue.getThread().halt();
                    }
                }
            }
        };
        try {
            this.updateRunnableQueue.preemptLater(r);
            this.updateRunnableQueue.resumeExecution();
        }
        catch (IllegalStateException ex) {}
    }
    
    public void dispatchSVGUnLoadEvent() {
        if (!this.started) {
            throw new IllegalStateException("UpdateManager not started.");
        }
        this.updateRunnableQueue.preemptLater(new Runnable() {
            @Override
            public void run() {
                synchronized (UpdateManager.this) {
                    final AbstractEvent evt = (AbstractEvent)((DocumentEvent)UpdateManager.this.document).createEvent("SVGEvents");
                    String type;
                    if (UpdateManager.this.bridgeContext.isSVG12()) {
                        type = "unload";
                    }
                    else {
                        type = "SVGUnload";
                    }
                    evt.initEventNS("http://www.w3.org/2001/xml-events", type, false, false);
                    ((EventTarget)UpdateManager.this.document.getDocumentElement()).dispatchEvent(evt);
                    UpdateManager.this.running = false;
                    UpdateManager.this.scriptingEnvironment.interrupt();
                    UpdateManager.this.updateRunnableQueue.getThread().halt();
                    UpdateManager.this.bridgeContext.dispose();
                    final UpdateManagerEvent ev = new UpdateManagerEvent(UpdateManager.this, null, null);
                    UpdateManager.this.fireEvent(UpdateManager.stoppedDispatcher, ev);
                }
            }
        });
        this.resume();
    }
    
    public void updateRendering(final AffineTransform u2d, final boolean dbr, final Shape aoi, final int width, final int height) {
        this.repaintManager.setupRenderer(u2d, dbr, aoi, width, height);
        final List l = new ArrayList(1);
        l.add(aoi);
        this.updateRendering(l, false);
    }
    
    public void updateRendering(final AffineTransform u2d, final boolean dbr, final boolean cpt, final Shape aoi, final int width, final int height) {
        this.repaintManager.setupRenderer(u2d, dbr, aoi, width, height);
        final List l = new ArrayList(1);
        l.add(aoi);
        this.updateRendering(l, cpt);
    }
    
    protected void updateRendering(final List areas, final boolean clearPaintingTransform) {
        try {
            UpdateManagerEvent ev = new UpdateManagerEvent(this, this.repaintManager.getOffScreen(), null);
            this.fireEvent(UpdateManager.updateStartedDispatcher, ev);
            final Collection c = this.repaintManager.updateRendering(areas);
            final List l = new ArrayList(c);
            ev = new UpdateManagerEvent(this, this.repaintManager.getOffScreen(), l, clearPaintingTransform);
            this.fireEvent(UpdateManager.updateCompletedDispatcher, ev);
        }
        catch (ThreadDeath td) {
            final UpdateManagerEvent ev2 = new UpdateManagerEvent(this, null, null);
            this.fireEvent(UpdateManager.updateFailedDispatcher, ev2);
            throw td;
        }
        catch (Throwable t) {
            final UpdateManagerEvent ev2 = new UpdateManagerEvent(this, null, null);
            this.fireEvent(UpdateManager.updateFailedDispatcher, ev2);
        }
    }
    
    protected void repaint() {
        if (!this.updateTracker.hasChanged()) {
            this.outOfDateTime = 0L;
            return;
        }
        final long ctime = System.currentTimeMillis();
        if (ctime < this.allResumeTime) {
            this.createRepaintTimer();
            return;
        }
        if (this.allResumeTime > 0L) {
            this.releaseAllRedrawSuspension();
        }
        if (ctime - this.outOfDateTime < this.minRepaintTime) {
            synchronized (this.updateRunnableQueue.getIteratorLock()) {
                final Iterator i = this.updateRunnableQueue.iterator();
                while (i.hasNext()) {
                    if (!(i.next() instanceof NoRepaintRunnable)) {
                        return;
                    }
                }
            }
        }
        final List dirtyAreas = this.updateTracker.getDirtyAreas();
        this.updateTracker.clear();
        if (dirtyAreas != null) {
            this.updateRendering(dirtyAreas, false);
        }
        this.outOfDateTime = 0L;
    }
    
    public void forceRepaint() {
        if (!this.updateTracker.hasChanged()) {
            this.outOfDateTime = 0L;
            return;
        }
        final List dirtyAreas = this.updateTracker.getDirtyAreas();
        this.updateTracker.clear();
        if (dirtyAreas != null) {
            this.updateRendering(dirtyAreas, false);
        }
        this.outOfDateTime = 0L;
    }
    
    void createRepaintTimer() {
        if (this.repaintTimerTask != null) {
            return;
        }
        if (this.allResumeTime < 0L) {
            return;
        }
        if (this.repaintTriggerTimer == null) {
            this.repaintTriggerTimer = new Timer(true);
        }
        long delay = this.allResumeTime - System.currentTimeMillis();
        if (delay < 0L) {
            delay = 0L;
        }
        this.repaintTimerTask = new RepaintTimerTask(this);
        this.repaintTriggerTimer.schedule(this.repaintTimerTask, delay);
    }
    
    void resetRepaintTimer() {
        if (this.repaintTimerTask == null) {
            return;
        }
        if (this.allResumeTime < 0L) {
            return;
        }
        if (this.repaintTriggerTimer == null) {
            this.repaintTriggerTimer = new Timer(true);
        }
        long delay = this.allResumeTime - System.currentTimeMillis();
        if (delay < 0L) {
            delay = 0L;
        }
        this.repaintTimerTask = new RepaintTimerTask(this);
        this.repaintTriggerTimer.schedule(this.repaintTimerTask, delay);
    }
    
    int addRedrawSuspension(final int max_wait_milliseconds) {
        final long resumeTime = System.currentTimeMillis() + max_wait_milliseconds;
        final SuspensionInfo si = new SuspensionInfo(this.nextSuspensionIndex++, resumeTime);
        if (resumeTime > this.allResumeTime) {
            this.allResumeTime = resumeTime;
            this.resetRepaintTimer();
        }
        this.suspensionList.add(si);
        return si.getIndex();
    }
    
    void releaseAllRedrawSuspension() {
        this.suspensionList.clear();
        this.allResumeTime = -1L;
        this.resetRepaintTimer();
    }
    
    boolean releaseRedrawSuspension(final int index) {
        if (index > this.nextSuspensionIndex) {
            return false;
        }
        if (this.suspensionList.size() == 0) {
            return true;
        }
        int lo = 0;
        int hi = this.suspensionList.size() - 1;
        while (lo < hi) {
            final int mid = lo + hi >> 1;
            final SuspensionInfo si = this.suspensionList.get(mid);
            final int idx = si.getIndex();
            if (idx == index) {
                hi = (lo = mid);
            }
            else if (idx < index) {
                lo = mid + 1;
            }
            else {
                hi = mid - 1;
            }
        }
        final SuspensionInfo si2 = this.suspensionList.get(lo);
        final int idx2 = si2.getIndex();
        if (idx2 != index) {
            return true;
        }
        this.suspensionList.remove(lo);
        if (this.suspensionList.size() == 0) {
            this.allResumeTime = -1L;
            this.resetRepaintTimer();
        }
        else {
            final long resumeTime = si2.getResumeMilli();
            if (resumeTime == this.allResumeTime) {
                this.allResumeTime = this.findNewAllResumeTime();
                this.resetRepaintTimer();
            }
        }
        return true;
    }
    
    long findNewAllResumeTime() {
        long ret = -1L;
        for (final Object aSuspensionList : this.suspensionList) {
            final SuspensionInfo si = (SuspensionInfo)aSuspensionList;
            final long t = si.getResumeMilli();
            if (t > ret) {
                ret = t;
            }
        }
        return ret;
    }
    
    public void addUpdateManagerListener(final UpdateManagerListener l) {
        this.listeners.add(l);
    }
    
    public void removeUpdateManagerListener(final UpdateManagerListener l) {
        this.listeners.remove(l);
    }
    
    protected void fireEvent(final EventDispatcher.Dispatcher dispatcher, final Object event) {
        EventDispatcher.fireEvent(dispatcher, this.listeners, event, false);
    }
    
    protected RunnableQueue.RunHandler createRunHandler() {
        return new UpdateManagerRunHander();
    }
    
    static {
        int value = 20;
        try {
            final String s = System.getProperty("org.apache.batik.min_repaint_time", "20");
            value = Integer.parseInt(s);
        }
        catch (SecurityException se) {}
        catch (NumberFormatException nfe) {}
        finally {
            MIN_REPAINT_TIME = value;
        }
        UpdateManager.startedDispatcher = new EventDispatcher.Dispatcher() {
            @Override
            public void dispatch(final Object listener, final Object event) {
                ((UpdateManagerListener)listener).managerStarted((UpdateManagerEvent)event);
            }
        };
        UpdateManager.stoppedDispatcher = new EventDispatcher.Dispatcher() {
            @Override
            public void dispatch(final Object listener, final Object event) {
                ((UpdateManagerListener)listener).managerStopped((UpdateManagerEvent)event);
            }
        };
        UpdateManager.suspendedDispatcher = new EventDispatcher.Dispatcher() {
            @Override
            public void dispatch(final Object listener, final Object event) {
                ((UpdateManagerListener)listener).managerSuspended((UpdateManagerEvent)event);
            }
        };
        UpdateManager.resumedDispatcher = new EventDispatcher.Dispatcher() {
            @Override
            public void dispatch(final Object listener, final Object event) {
                ((UpdateManagerListener)listener).managerResumed((UpdateManagerEvent)event);
            }
        };
        UpdateManager.updateStartedDispatcher = new EventDispatcher.Dispatcher() {
            @Override
            public void dispatch(final Object listener, final Object event) {
                ((UpdateManagerListener)listener).updateStarted((UpdateManagerEvent)event);
            }
        };
        UpdateManager.updateCompletedDispatcher = new EventDispatcher.Dispatcher() {
            @Override
            public void dispatch(final Object listener, final Object event) {
                ((UpdateManagerListener)listener).updateCompleted((UpdateManagerEvent)event);
            }
        };
        UpdateManager.updateFailedDispatcher = new EventDispatcher.Dispatcher() {
            @Override
            public void dispatch(final Object listener, final Object event) {
                ((UpdateManagerListener)listener).updateFailed((UpdateManagerEvent)event);
            }
        };
    }
    
    protected static class SuspensionInfo
    {
        int index;
        long resumeMilli;
        
        public SuspensionInfo(final int index, final long resumeMilli) {
            this.index = index;
            this.resumeMilli = resumeMilli;
        }
        
        public int getIndex() {
            return this.index;
        }
        
        public long getResumeMilli() {
            return this.resumeMilli;
        }
    }
    
    protected static class RepaintTimerTask extends TimerTask
    {
        UpdateManager um;
        
        RepaintTimerTask(final UpdateManager um) {
            this.um = um;
        }
        
        @Override
        public void run() {
            final RunnableQueue rq = this.um.getUpdateRunnableQueue();
            if (rq == null) {
                return;
            }
            rq.invokeLater(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
    }
    
    protected class UpdateManagerRunHander extends RunnableQueue.RunHandlerAdapter
    {
        @Override
        public void runnableStart(final RunnableQueue rq, final Runnable r) {
            if (UpdateManager.this.running && !(r instanceof NoRepaintRunnable) && UpdateManager.this.outOfDateTime == 0L) {
                UpdateManager.this.outOfDateTime = System.currentTimeMillis();
            }
        }
        
        @Override
        public void runnableInvoked(final RunnableQueue rq, final Runnable r) {
            if (UpdateManager.this.running && !(r instanceof NoRepaintRunnable)) {
                UpdateManager.this.repaint();
            }
        }
        
        @Override
        public void executionSuspended(final RunnableQueue rq) {
            synchronized (UpdateManager.this) {
                if (UpdateManager.this.suspendCalled) {
                    UpdateManager.this.running = false;
                    final UpdateManagerEvent ev = new UpdateManagerEvent(this, null, null);
                    UpdateManager.this.fireEvent(UpdateManager.suspendedDispatcher, ev);
                }
            }
        }
        
        @Override
        public void executionResumed(final RunnableQueue rq) {
            synchronized (UpdateManager.this) {
                if (UpdateManager.this.suspendCalled && !UpdateManager.this.running) {
                    UpdateManager.this.running = true;
                    UpdateManager.this.suspendCalled = false;
                    final UpdateManagerEvent ev = new UpdateManagerEvent(this, null, null);
                    UpdateManager.this.fireEvent(UpdateManager.resumedDispatcher, ev);
                }
            }
        }
    }
}
