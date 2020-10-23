// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.text.CharacterIterator;
import java.util.Iterator;
import org.apache.batik.gvt.event.SelectionListener;
import java.awt.geom.Point2D;
import org.apache.batik.gvt.event.GraphicsNodeChangeListener;
import java.awt.Shape;
import org.apache.batik.gvt.event.SelectionEvent;
import org.apache.batik.gvt.Selectable;
import org.apache.batik.gvt.event.GraphicsNodeChangeEvent;
import org.apache.batik.gvt.event.GraphicsNodeKeyEvent;
import org.apache.batik.gvt.event.GraphicsNodeEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.apache.batik.gvt.RootGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import java.util.ArrayList;
import org.apache.batik.gvt.Selector;

public class ConcreteTextSelector implements Selector
{
    private ArrayList listeners;
    private GraphicsNode selectionNode;
    private RootGraphicsNode selectionNodeRoot;
    
    @Override
    public void mouseClicked(final GraphicsNodeMouseEvent evt) {
        this.checkSelectGesture(evt);
    }
    
    @Override
    public void mouseDragged(final GraphicsNodeMouseEvent evt) {
        this.checkSelectGesture(evt);
    }
    
    @Override
    public void mouseEntered(final GraphicsNodeMouseEvent evt) {
        this.checkSelectGesture(evt);
    }
    
    @Override
    public void mouseExited(final GraphicsNodeMouseEvent evt) {
        this.checkSelectGesture(evt);
    }
    
    @Override
    public void mouseMoved(final GraphicsNodeMouseEvent evt) {
    }
    
    @Override
    public void mousePressed(final GraphicsNodeMouseEvent evt) {
        this.checkSelectGesture(evt);
    }
    
    @Override
    public void mouseReleased(final GraphicsNodeMouseEvent evt) {
        this.checkSelectGesture(evt);
    }
    
    @Override
    public void keyPressed(final GraphicsNodeKeyEvent evt) {
        this.report(evt, "keyPressed");
    }
    
    @Override
    public void keyReleased(final GraphicsNodeKeyEvent evt) {
        this.report(evt, "keyReleased");
    }
    
    @Override
    public void keyTyped(final GraphicsNodeKeyEvent evt) {
        this.report(evt, "keyTyped");
    }
    
    @Override
    public void changeStarted(final GraphicsNodeChangeEvent gnce) {
    }
    
    @Override
    public void changeCompleted(final GraphicsNodeChangeEvent gnce) {
        if (this.selectionNode == null) {
            return;
        }
        final Shape newShape = ((Selectable)this.selectionNode).getHighlightShape();
        this.dispatchSelectionEvent(new SelectionEvent(this.getSelection(), 1, newShape));
    }
    
    public void setSelection(final Mark begin, final Mark end) {
        final TextNode node = begin.getTextNode();
        if (node != end.getTextNode()) {
            throw new RuntimeException("Markers not from same TextNode");
        }
        node.setSelection(begin, end);
        this.selectionNode = node;
        this.selectionNodeRoot = node.getRoot();
        final Object selection = this.getSelection();
        final Shape shape = node.getHighlightShape();
        this.dispatchSelectionEvent(new SelectionEvent(selection, 2, shape));
    }
    
    public void clearSelection() {
        if (this.selectionNode == null) {
            return;
        }
        this.dispatchSelectionEvent(new SelectionEvent(null, 3, null));
        this.selectionNode = null;
        this.selectionNodeRoot = null;
    }
    
    protected void checkSelectGesture(final GraphicsNodeEvent evt) {
        GraphicsNodeMouseEvent mevt = null;
        if (evt instanceof GraphicsNodeMouseEvent) {
            mevt = (GraphicsNodeMouseEvent)evt;
        }
        final GraphicsNode source = evt.getGraphicsNode();
        if (this.isDeselectGesture(evt)) {
            if (this.selectionNode != null) {
                this.selectionNodeRoot.removeTreeGraphicsNodeChangeListener(this);
            }
            this.clearSelection();
        }
        else if (mevt != null) {
            final Point2D p = mevt.getPoint2D();
            if (source instanceof Selectable && this.isSelectStartGesture(evt)) {
                if (this.selectionNode != source) {
                    if (this.selectionNode != null) {
                        this.selectionNodeRoot.removeTreeGraphicsNodeChangeListener(this);
                    }
                    if ((this.selectionNode = source) != null) {
                        (this.selectionNodeRoot = source.getRoot()).addTreeGraphicsNodeChangeListener(this);
                    }
                }
                ((Selectable)source).selectAt(p.getX(), p.getY());
                this.dispatchSelectionEvent(new SelectionEvent(null, 4, null));
            }
            else if (this.isSelectEndGesture(evt)) {
                if (this.selectionNode == source) {
                    ((Selectable)source).selectTo(p.getX(), p.getY());
                }
                final Object oldSelection = this.getSelection();
                if (this.selectionNode != null) {
                    final Shape newShape = ((Selectable)this.selectionNode).getHighlightShape();
                    this.dispatchSelectionEvent(new SelectionEvent(oldSelection, 2, newShape));
                }
            }
            else if (this.isSelectContinueGesture(evt)) {
                if (this.selectionNode == source) {
                    final boolean result = ((Selectable)source).selectTo(p.getX(), p.getY());
                    if (result) {
                        final Shape newShape = ((Selectable)this.selectionNode).getHighlightShape();
                        this.dispatchSelectionEvent(new SelectionEvent(null, 1, newShape));
                    }
                }
            }
            else if (source instanceof Selectable && this.isSelectAllGesture(evt)) {
                if (this.selectionNode != source) {
                    if (this.selectionNode != null) {
                        this.selectionNodeRoot.removeTreeGraphicsNodeChangeListener(this);
                    }
                    if ((this.selectionNode = source) != null) {
                        (this.selectionNodeRoot = source.getRoot()).addTreeGraphicsNodeChangeListener(this);
                    }
                }
                ((Selectable)source).selectAll(p.getX(), p.getY());
                final Object oldSelection = this.getSelection();
                final Shape newShape = ((Selectable)source).getHighlightShape();
                this.dispatchSelectionEvent(new SelectionEvent(oldSelection, 2, newShape));
            }
        }
    }
    
    private boolean isDeselectGesture(final GraphicsNodeEvent evt) {
        return evt.getID() == 500 && ((GraphicsNodeMouseEvent)evt).getClickCount() == 1;
    }
    
    private boolean isSelectStartGesture(final GraphicsNodeEvent evt) {
        return evt.getID() == 501;
    }
    
    private boolean isSelectEndGesture(final GraphicsNodeEvent evt) {
        return evt.getID() == 502;
    }
    
    private boolean isSelectContinueGesture(final GraphicsNodeEvent evt) {
        return evt.getID() == 506;
    }
    
    private boolean isSelectAllGesture(final GraphicsNodeEvent evt) {
        return evt.getID() == 500 && ((GraphicsNodeMouseEvent)evt).getClickCount() == 2;
    }
    
    @Override
    public Object getSelection() {
        Object value = null;
        if (this.selectionNode instanceof Selectable) {
            value = ((Selectable)this.selectionNode).getSelection();
        }
        return value;
    }
    
    @Override
    public boolean isEmpty() {
        return this.getSelection() == null;
    }
    
    public void dispatchSelectionEvent(final SelectionEvent e) {
        if (this.listeners != null) {
            final Iterator iter = this.listeners.iterator();
            switch (e.getID()) {
                case 2: {
                    while (iter.hasNext()) {
                        iter.next().selectionDone(e);
                    }
                    break;
                }
                case 1: {
                    while (iter.hasNext()) {
                        iter.next().selectionChanged(e);
                    }
                    break;
                }
                case 3: {
                    while (iter.hasNext()) {
                        iter.next().selectionCleared(e);
                    }
                    break;
                }
                case 4: {
                    while (iter.hasNext()) {
                        iter.next().selectionStarted(e);
                    }
                    break;
                }
            }
        }
    }
    
    @Override
    public void addSelectionListener(final SelectionListener l) {
        if (this.listeners == null) {
            this.listeners = new ArrayList();
        }
        this.listeners.add(l);
    }
    
    @Override
    public void removeSelectionListener(final SelectionListener l) {
        if (this.listeners != null) {
            this.listeners.remove(l);
        }
    }
    
    private void report(final GraphicsNodeEvent evt, final String message) {
        final GraphicsNode source = evt.getGraphicsNode();
        String label = "(non-text node)";
        if (source instanceof TextNode) {
            final CharacterIterator iter = ((TextNode)source).getAttributedCharacterIterator();
            final char[] cbuff = new char[iter.getEndIndex()];
            if (cbuff.length > 0) {
                cbuff[0] = iter.first();
            }
            for (int i = 1; i < cbuff.length; ++i) {
                cbuff[i] = iter.next();
            }
            label = new String(cbuff);
        }
        System.out.println("Mouse " + message + " in " + label);
    }
}
