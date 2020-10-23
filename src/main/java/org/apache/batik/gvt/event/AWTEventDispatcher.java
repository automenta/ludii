// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.event;

import java.awt.Point;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.lang.reflect.Array;
import java.util.EventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.util.EventObject;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.EventListenerList;
import java.awt.geom.AffineTransform;
import org.apache.batik.gvt.GraphicsNode;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;

public class AWTEventDispatcher implements EventDispatcher, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
{
    protected GraphicsNode root;
    protected AffineTransform baseTransform;
    protected EventListenerList glisteners;
    protected GraphicsNode lastHit;
    protected GraphicsNode currentKeyEventTarget;
    protected List eventQueue;
    protected boolean eventDispatchEnabled;
    protected int eventQueueMaxSize;
    static final int MAX_QUEUE_SIZE = 10;
    private int nodeIncrementEventID;
    private int nodeIncrementEventCode;
    private int nodeIncrementEventModifiers;
    private int nodeDecrementEventID;
    private int nodeDecrementEventCode;
    private int nodeDecrementEventModifiers;
    
    public AWTEventDispatcher() {
        this.eventQueue = new LinkedList();
        this.eventDispatchEnabled = true;
        this.eventQueueMaxSize = 10;
        this.nodeIncrementEventID = 401;
        this.nodeIncrementEventCode = 9;
        this.nodeIncrementEventModifiers = 0;
        this.nodeDecrementEventID = 401;
        this.nodeDecrementEventCode = 9;
        this.nodeDecrementEventModifiers = 64;
    }
    
    @Override
    public void setRootNode(final GraphicsNode root) {
        if (this.root != root) {
            this.eventQueue.clear();
        }
        this.root = root;
    }
    
    @Override
    public GraphicsNode getRootNode() {
        return this.root;
    }
    
    @Override
    public void setBaseTransform(final AffineTransform t) {
        if (this.baseTransform != t && (this.baseTransform == null || !this.baseTransform.equals(t))) {
            this.eventQueue.clear();
        }
        this.baseTransform = t;
    }
    
    @Override
    public AffineTransform getBaseTransform() {
        return new AffineTransform(this.baseTransform);
    }
    
    @Override
    public void mousePressed(final MouseEvent evt) {
        this.dispatchEvent(evt);
    }
    
    @Override
    public void mouseReleased(final MouseEvent evt) {
        this.dispatchEvent(evt);
    }
    
    @Override
    public void mouseEntered(final MouseEvent evt) {
        this.dispatchEvent(evt);
    }
    
    @Override
    public void mouseExited(final MouseEvent evt) {
        this.dispatchEvent(evt);
    }
    
    @Override
    public void mouseClicked(final MouseEvent evt) {
        this.dispatchEvent(evt);
    }
    
    @Override
    public void mouseMoved(final MouseEvent evt) {
        this.dispatchEvent(evt);
    }
    
    @Override
    public void mouseDragged(final MouseEvent evt) {
        this.dispatchEvent(evt);
    }
    
    @Override
    public void mouseWheelMoved(final MouseWheelEvent evt) {
        this.dispatchEvent(evt);
    }
    
    @Override
    public void keyPressed(final KeyEvent evt) {
        this.dispatchEvent(evt);
    }
    
    @Override
    public void keyReleased(final KeyEvent evt) {
        this.dispatchEvent(evt);
    }
    
    @Override
    public void keyTyped(final KeyEvent evt) {
        this.dispatchEvent(evt);
    }
    
    @Override
    public void addGraphicsNodeMouseListener(final GraphicsNodeMouseListener l) {
        if (this.glisteners == null) {
            this.glisteners = new EventListenerList();
        }
        this.glisteners.add(GraphicsNodeMouseListener.class, l);
    }
    
    @Override
    public void removeGraphicsNodeMouseListener(final GraphicsNodeMouseListener l) {
        if (this.glisteners != null) {
            this.glisteners.remove(GraphicsNodeMouseListener.class, l);
        }
    }
    
    @Override
    public void addGraphicsNodeMouseWheelListener(final GraphicsNodeMouseWheelListener l) {
        if (this.glisteners == null) {
            this.glisteners = new EventListenerList();
        }
        this.glisteners.add(GraphicsNodeMouseWheelListener.class, l);
    }
    
    @Override
    public void removeGraphicsNodeMouseWheelListener(final GraphicsNodeMouseWheelListener l) {
        if (this.glisteners != null) {
            this.glisteners.remove(GraphicsNodeMouseWheelListener.class, l);
        }
    }
    
    @Override
    public void addGraphicsNodeKeyListener(final GraphicsNodeKeyListener l) {
        if (this.glisteners == null) {
            this.glisteners = new EventListenerList();
        }
        this.glisteners.add(GraphicsNodeKeyListener.class, l);
    }
    
    @Override
    public void removeGraphicsNodeKeyListener(final GraphicsNodeKeyListener l) {
        if (this.glisteners != null) {
            this.glisteners.remove(GraphicsNodeKeyListener.class, l);
        }
    }
    
    @Override
    public EventListener[] getListeners(final Class listenerType) {
        final Object array = Array.newInstance(listenerType, this.glisteners.getListenerCount(listenerType));
        final Object[] pairElements = this.glisteners.getListenerList();
        int i = 0;
        int j = 0;
        while (i < pairElements.length - 1) {
            if (pairElements[i].equals(listenerType)) {
                Array.set(array, j, pairElements[i + 1]);
                ++j;
            }
            i += 2;
        }
        return (EventListener[])array;
    }
    
    public void setEventDispatchEnabled(final boolean b) {
        this.eventDispatchEnabled = b;
        if (this.eventDispatchEnabled) {
            while (this.eventQueue.size() > 0) {
                final EventObject evt = this.eventQueue.remove(0);
                this.dispatchEvent(evt);
            }
        }
    }
    
    public void setEventQueueMaxSize(final int n) {
        this.eventQueueMaxSize = n;
        if (n == 0) {
            this.eventQueue.clear();
        }
        while (this.eventQueue.size() > this.eventQueueMaxSize) {
            this.eventQueue.remove(0);
        }
    }
    
    @Override
    public void dispatchEvent(final EventObject evt) {
        if (this.root == null) {
            return;
        }
        if (!this.eventDispatchEnabled) {
            if (this.eventQueueMaxSize > 0) {
                this.eventQueue.add(evt);
                while (this.eventQueue.size() > this.eventQueueMaxSize) {
                    this.eventQueue.remove(0);
                }
            }
            return;
        }
        if (evt instanceof MouseWheelEvent) {
            this.dispatchMouseWheelEvent((MouseWheelEvent)evt);
        }
        else if (evt instanceof MouseEvent) {
            this.dispatchMouseEvent((MouseEvent)evt);
        }
        else if (evt instanceof KeyEvent) {
            final InputEvent e = (InputEvent)evt;
            if (this.isNodeIncrementEvent(e)) {
                this.incrementKeyTarget();
            }
            else if (this.isNodeDecrementEvent(e)) {
                this.decrementKeyTarget();
            }
            else {
                this.dispatchKeyEvent((KeyEvent)evt);
            }
        }
    }
    
    protected int getCurrentLockState() {
        final Toolkit t = Toolkit.getDefaultToolkit();
        int lockState = 0;
        try {
            if (t.getLockingKeyState(262)) {
                ++lockState;
            }
        }
        catch (UnsupportedOperationException ex) {}
        lockState <<= 1;
        try {
            if (t.getLockingKeyState(145)) {
                ++lockState;
            }
        }
        catch (UnsupportedOperationException ex2) {}
        lockState <<= 1;
        try {
            if (t.getLockingKeyState(144)) {
                ++lockState;
            }
        }
        catch (UnsupportedOperationException ex3) {}
        lockState <<= 1;
        try {
            if (t.getLockingKeyState(20)) {
                ++lockState;
            }
        }
        catch (UnsupportedOperationException ex4) {}
        return lockState;
    }
    
    protected void dispatchKeyEvent(final KeyEvent evt) {
        this.currentKeyEventTarget = this.lastHit;
        final GraphicsNode target = (this.currentKeyEventTarget == null) ? this.root : this.currentKeyEventTarget;
        this.processKeyEvent(new GraphicsNodeKeyEvent(target, evt.getID(), evt.getWhen(), evt.getModifiersEx(), this.getCurrentLockState(), evt.getKeyCode(), evt.getKeyChar(), evt.getKeyLocation()));
    }
    
    protected void dispatchMouseEvent(final MouseEvent evt) {
        Point2D gnp;
        final Point2D p = gnp = new Point2D.Float((float)evt.getX(), (float)evt.getY());
        if (this.baseTransform != null) {
            gnp = this.baseTransform.transform(p, null);
        }
        final GraphicsNode node = this.root.nodeHitAt(gnp);
        if (node != null) {
            try {
                node.getGlobalTransform().createInverse().transform(gnp, gnp);
            }
            catch (NoninvertibleTransformException ex) {}
        }
        Point screenPos;
        if (!evt.getComponent().isShowing()) {
            screenPos = new Point(0, 0);
        }
        else {
            final Point locationOnScreen;
            screenPos = (locationOnScreen = evt.getComponent().getLocationOnScreen());
            locationOnScreen.x += evt.getX();
            final Point point = screenPos;
            point.y += evt.getY();
        }
        final int currentLockState = this.getCurrentLockState();
        if (this.lastHit != node) {
            if (this.lastHit != null) {
                final GraphicsNodeMouseEvent gvtevt = new GraphicsNodeMouseEvent(this.lastHit, 505, evt.getWhen(), evt.getModifiersEx(), currentLockState, evt.getButton(), (float)gnp.getX(), (float)gnp.getY(), (int)Math.floor(p.getX()), (int)Math.floor(p.getY()), screenPos.x, screenPos.y, evt.getClickCount(), node);
                this.processMouseEvent(gvtevt);
            }
            if (node != null) {
                final GraphicsNodeMouseEvent gvtevt = new GraphicsNodeMouseEvent(node, 504, evt.getWhen(), evt.getModifiersEx(), currentLockState, evt.getButton(), (float)gnp.getX(), (float)gnp.getY(), (int)Math.floor(p.getX()), (int)Math.floor(p.getY()), screenPos.x, screenPos.y, evt.getClickCount(), this.lastHit);
                this.processMouseEvent(gvtevt);
            }
        }
        if (node != null) {
            final GraphicsNodeMouseEvent gvtevt = new GraphicsNodeMouseEvent(node, evt.getID(), evt.getWhen(), evt.getModifiersEx(), currentLockState, evt.getButton(), (float)gnp.getX(), (float)gnp.getY(), (int)Math.floor(p.getX()), (int)Math.floor(p.getY()), screenPos.x, screenPos.y, evt.getClickCount(), null);
            this.processMouseEvent(gvtevt);
        }
        else {
            final GraphicsNodeMouseEvent gvtevt = new GraphicsNodeMouseEvent(this.root, evt.getID(), evt.getWhen(), evt.getModifiersEx(), currentLockState, evt.getButton(), (float)gnp.getX(), (float)gnp.getY(), (int)Math.floor(p.getX()), (int)Math.floor(p.getY()), screenPos.x, screenPos.y, evt.getClickCount(), null);
            this.processMouseEvent(gvtevt);
        }
        this.lastHit = node;
    }
    
    protected void dispatchMouseWheelEvent(final MouseWheelEvent evt) {
        if (this.lastHit != null) {
            this.processMouseWheelEvent(new GraphicsNodeMouseWheelEvent(this.lastHit, evt.getID(), evt.getWhen(), evt.getModifiersEx(), this.getCurrentLockState(), evt.getWheelRotation()));
        }
    }
    
    protected void processMouseEvent(final GraphicsNodeMouseEvent evt) {
        if (this.glisteners != null) {
            final GraphicsNodeMouseListener[] listeners = (GraphicsNodeMouseListener[])this.getListeners(GraphicsNodeMouseListener.class);
            switch (evt.getID()) {
                case 503: {
                    for (final GraphicsNodeMouseListener listener6 : listeners) {
                        listener6.mouseMoved(evt);
                    }
                    break;
                }
                case 506: {
                    for (final GraphicsNodeMouseListener listener7 : listeners) {
                        listener7.mouseDragged(evt);
                    }
                    break;
                }
                case 504: {
                    for (final GraphicsNodeMouseListener listener8 : listeners) {
                        listener8.mouseEntered(evt);
                    }
                    break;
                }
                case 505: {
                    for (final GraphicsNodeMouseListener listener9 : listeners) {
                        listener9.mouseExited(evt);
                    }
                    break;
                }
                case 500: {
                    for (final GraphicsNodeMouseListener listener10 : listeners) {
                        listener10.mouseClicked(evt);
                    }
                    break;
                }
                case 501: {
                    for (final GraphicsNodeMouseListener listener11 : listeners) {
                        listener11.mousePressed(evt);
                    }
                    break;
                }
                case 502: {
                    for (final GraphicsNodeMouseListener listener12 : listeners) {
                        listener12.mouseReleased(evt);
                    }
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unknown Mouse Event type: " + evt.getID());
                }
            }
        }
    }
    
    protected void processMouseWheelEvent(final GraphicsNodeMouseWheelEvent evt) {
        if (this.glisteners != null) {
            final GraphicsNodeMouseWheelListener[] arr$;
            final GraphicsNodeMouseWheelListener[] listeners = arr$ = (GraphicsNodeMouseWheelListener[])this.getListeners(GraphicsNodeMouseWheelListener.class);
            for (final GraphicsNodeMouseWheelListener listener : arr$) {
                listener.mouseWheelMoved(evt);
            }
        }
    }
    
    public void processKeyEvent(final GraphicsNodeKeyEvent evt) {
        if (this.glisteners != null) {
            final GraphicsNodeKeyListener[] listeners = (GraphicsNodeKeyListener[])this.getListeners(GraphicsNodeKeyListener.class);
            switch (evt.getID()) {
                case 401: {
                    for (final GraphicsNodeKeyListener listener2 : listeners) {
                        listener2.keyPressed(evt);
                    }
                    break;
                }
                case 402: {
                    for (final GraphicsNodeKeyListener listener3 : listeners) {
                        listener3.keyReleased(evt);
                    }
                    break;
                }
                case 400: {
                    for (final GraphicsNodeKeyListener listener4 : listeners) {
                        listener4.keyTyped(evt);
                    }
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unknown Key Event type: " + evt.getID());
                }
            }
        }
        evt.consume();
    }
    
    private void incrementKeyTarget() {
        throw new UnsupportedOperationException("Increment not implemented.");
    }
    
    private void decrementKeyTarget() {
        throw new UnsupportedOperationException("Decrement not implemented.");
    }
    
    @Override
    public void setNodeIncrementEvent(final InputEvent e) {
        this.nodeIncrementEventID = e.getID();
        if (e instanceof KeyEvent) {
            this.nodeIncrementEventCode = ((KeyEvent)e).getKeyCode();
        }
        this.nodeIncrementEventModifiers = e.getModifiersEx();
    }
    
    @Override
    public void setNodeDecrementEvent(final InputEvent e) {
        this.nodeDecrementEventID = e.getID();
        if (e instanceof KeyEvent) {
            this.nodeDecrementEventCode = ((KeyEvent)e).getKeyCode();
        }
        this.nodeDecrementEventModifiers = e.getModifiersEx();
    }
    
    protected boolean isNodeIncrementEvent(final InputEvent e) {
        return e.getID() == this.nodeIncrementEventID && (!(e instanceof KeyEvent) || ((KeyEvent)e).getKeyCode() == this.nodeIncrementEventCode) && (e.getModifiersEx() & this.nodeIncrementEventModifiers) != 0x0;
    }
    
    protected boolean isNodeDecrementEvent(final InputEvent e) {
        return e.getID() == this.nodeDecrementEventID && (!(e instanceof KeyEvent) || ((KeyEvent)e).getKeyCode() == this.nodeDecrementEventCode) && (e.getModifiersEx() & this.nodeDecrementEventModifiers) != 0x0;
    }
    
    protected static boolean isMetaDown(final int modifiers) {
        return (modifiers & 0x100) != 0x0;
    }
}
