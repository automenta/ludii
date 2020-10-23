// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.awt.Rectangle;
import java.util.ListIterator;
import java.util.Collection;
import java.util.Iterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import org.apache.batik.util.HaltingThread;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class CompositeGraphicsNode extends AbstractGraphicsNode implements List
{
    public static final Rectangle2D VIEWPORT;
    public static final Rectangle2D NULL_RECT;
    protected GraphicsNode[] children;
    protected volatile int count;
    protected volatile int modCount;
    protected Rectangle2D backgroundEnableRgn;
    private volatile Rectangle2D geometryBounds;
    private volatile Rectangle2D primitiveBounds;
    private volatile Rectangle2D sensitiveBounds;
    private Shape outline;
    
    public CompositeGraphicsNode() {
        this.backgroundEnableRgn = null;
    }
    
    public List getChildren() {
        return this;
    }
    
    public void setBackgroundEnable(final Rectangle2D bgRgn) {
        this.backgroundEnableRgn = bgRgn;
    }
    
    public Rectangle2D getBackgroundEnable() {
        return this.backgroundEnableRgn;
    }
    
    @Override
    public void setVisible(final boolean isVisible) {
        this.isVisible = isVisible;
    }
    
    @Override
    public void primitivePaint(final Graphics2D g2d) {
        if (this.count == 0) {
            return;
        }
        final Thread currentThread = Thread.currentThread();
        for (int i = 0; i < this.count; ++i) {
            if (HaltingThread.hasBeenHalted(currentThread)) {
                return;
            }
            final GraphicsNode node = this.children[i];
            if (node != null) {
                node.paint(g2d);
            }
        }
    }
    
    @Override
    protected void invalidateGeometryCache() {
        super.invalidateGeometryCache();
        this.geometryBounds = null;
        this.primitiveBounds = null;
        this.sensitiveBounds = null;
        this.outline = null;
    }
    
    @Override
    public Rectangle2D getPrimitiveBounds() {
        if (this.primitiveBounds != null) {
            if (this.primitiveBounds == CompositeGraphicsNode.NULL_RECT) {
                return null;
            }
            return this.primitiveBounds;
        }
        else {
            final Thread currentThread = Thread.currentThread();
            int i = 0;
            Rectangle2D bounds = null;
            while (bounds == null && i < this.count) {
                bounds = this.children[i++].getTransformedBounds(CompositeGraphicsNode.IDENTITY);
                if ((i & 0xF) == 0x0 && HaltingThread.hasBeenHalted(currentThread)) {
                    break;
                }
            }
            if (HaltingThread.hasBeenHalted(currentThread)) {
                this.invalidateGeometryCache();
                return null;
            }
            if (bounds == null) {
                this.primitiveBounds = CompositeGraphicsNode.NULL_RECT;
                return null;
            }
            this.primitiveBounds = bounds;
            while (i < this.count) {
                final Rectangle2D ctb = this.children[i++].getTransformedBounds(CompositeGraphicsNode.IDENTITY);
                if (ctb != null) {
                    if (this.primitiveBounds == null) {
                        return null;
                    }
                    this.primitiveBounds.add(ctb);
                }
                if ((i & 0xF) == 0x0 && HaltingThread.hasBeenHalted(currentThread)) {
                    break;
                }
            }
            if (HaltingThread.hasBeenHalted(currentThread)) {
                this.invalidateGeometryCache();
            }
            return this.primitiveBounds;
        }
    }
    
    public static Rectangle2D getTransformedBBox(final Rectangle2D r2d, final AffineTransform t) {
        if (t == null || r2d == null) {
            return r2d;
        }
        double x = r2d.getX();
        final double w = r2d.getWidth();
        double y = r2d.getY();
        final double h = r2d.getHeight();
        double sx = t.getScaleX();
        double sy = t.getScaleY();
        if (sx < 0.0) {
            x = -(x + w);
            sx = -sx;
        }
        if (sy < 0.0) {
            y = -(y + h);
            sy = -sy;
        }
        return new Rectangle2D.Float((float)(x * sx + t.getTranslateX()), (float)(y * sy + t.getTranslateY()), (float)(w * sx), (float)(h * sy));
    }
    
    @Override
    public Rectangle2D getTransformedPrimitiveBounds(final AffineTransform txf) {
        AffineTransform t = txf;
        if (this.transform != null) {
            t = new AffineTransform(txf);
            t.concatenate(this.transform);
        }
        if (t == null || (t.getShearX() == 0.0 && t.getShearY() == 0.0)) {
            return getTransformedBBox(this.getPrimitiveBounds(), t);
        }
        int i;
        Rectangle2D tpb;
        for (i = 0, tpb = null; tpb == null && i < this.count; tpb = this.children[i++].getTransformedBounds(t)) {}
        while (i < this.count) {
            final Rectangle2D ctb = this.children[i++].getTransformedBounds(t);
            if (ctb != null) {
                tpb.add(ctb);
            }
        }
        return tpb;
    }
    
    @Override
    public Rectangle2D getGeometryBounds() {
        if (this.geometryBounds == null) {
            int i;
            for (i = 0; this.geometryBounds == null && i < this.count; this.geometryBounds = this.children[i++].getTransformedGeometryBounds(CompositeGraphicsNode.IDENTITY)) {}
            while (i < this.count) {
                final Rectangle2D cgb = this.children[i++].getTransformedGeometryBounds(CompositeGraphicsNode.IDENTITY);
                if (cgb != null) {
                    if (this.geometryBounds == null) {
                        return this.getGeometryBounds();
                    }
                    this.geometryBounds.add(cgb);
                }
            }
        }
        return this.geometryBounds;
    }
    
    @Override
    public Rectangle2D getTransformedGeometryBounds(final AffineTransform txf) {
        AffineTransform t = txf;
        if (this.transform != null) {
            t = new AffineTransform(txf);
            t.concatenate(this.transform);
        }
        if (t == null || (t.getShearX() == 0.0 && t.getShearY() == 0.0)) {
            return getTransformedBBox(this.getGeometryBounds(), t);
        }
        Rectangle2D gb;
        int i;
        for (gb = null, i = 0; gb == null && i < this.count; gb = this.children[i++].getTransformedGeometryBounds(t)) {}
        Rectangle2D cgb = null;
        while (i < this.count) {
            cgb = this.children[i++].getTransformedGeometryBounds(t);
            if (cgb != null) {
                gb.add(cgb);
            }
        }
        return gb;
    }
    
    @Override
    public Rectangle2D getSensitiveBounds() {
        if (this.sensitiveBounds != null) {
            return this.sensitiveBounds;
        }
        int i;
        for (i = 0; this.sensitiveBounds == null && i < this.count; this.sensitiveBounds = this.children[i++].getTransformedSensitiveBounds(CompositeGraphicsNode.IDENTITY)) {}
        while (i < this.count) {
            final Rectangle2D cgb = this.children[i++].getTransformedSensitiveBounds(CompositeGraphicsNode.IDENTITY);
            if (cgb != null) {
                if (this.sensitiveBounds == null) {
                    return this.getSensitiveBounds();
                }
                this.sensitiveBounds.add(cgb);
            }
        }
        return this.sensitiveBounds;
    }
    
    @Override
    public Rectangle2D getTransformedSensitiveBounds(final AffineTransform txf) {
        AffineTransform t = txf;
        if (this.transform != null) {
            t = new AffineTransform(txf);
            t.concatenate(this.transform);
        }
        if (t == null || (t.getShearX() == 0.0 && t.getShearY() == 0.0)) {
            return getTransformedBBox(this.getSensitiveBounds(), t);
        }
        Rectangle2D sb;
        int i;
        for (sb = null, i = 0; sb == null && i < this.count; sb = this.children[i++].getTransformedSensitiveBounds(t)) {}
        while (i < this.count) {
            final Rectangle2D csb = this.children[i++].getTransformedSensitiveBounds(t);
            if (csb != null) {
                sb.add(csb);
            }
        }
        return sb;
    }
    
    @Override
    public boolean contains(final Point2D p) {
        final Rectangle2D bounds = this.getSensitiveBounds();
        if (this.count > 0 && bounds != null && bounds.contains(p)) {
            Point2D pt = null;
            Point2D cp = null;
            for (int i = 0; i < this.count; ++i) {
                final AffineTransform t = this.children[i].getInverseTransform();
                if (t != null) {
                    pt = (cp = t.transform(p, pt));
                }
                else {
                    cp = p;
                }
                if (this.children[i].contains(cp)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public GraphicsNode nodeHitAt(final Point2D p) {
        final Rectangle2D bounds = this.getSensitiveBounds();
        if (this.count > 0 && bounds != null && bounds.contains(p)) {
            Point2D pt = null;
            Point2D cp = null;
            for (int i = this.count - 1; i >= 0; --i) {
                final AffineTransform t = this.children[i].getInverseTransform();
                if (t != null) {
                    pt = (cp = t.transform(p, pt));
                }
                else {
                    cp = p;
                }
                final GraphicsNode node = this.children[i].nodeHitAt(cp);
                if (node != null) {
                    return node;
                }
            }
        }
        return null;
    }
    
    @Override
    public Shape getOutline() {
        if (this.outline != null) {
            return this.outline;
        }
        this.outline = new GeneralPath();
        for (int i = 0; i < this.count; ++i) {
            final Shape childOutline = this.children[i].getOutline();
            if (childOutline != null) {
                final AffineTransform tr = this.children[i].getTransform();
                if (tr != null) {
                    ((GeneralPath)this.outline).append(tr.createTransformedShape(childOutline), false);
                }
                else {
                    ((GeneralPath)this.outline).append(childOutline, false);
                }
            }
        }
        return this.outline;
    }
    
    @Override
    protected void setRoot(final RootGraphicsNode newRoot) {
        super.setRoot(newRoot);
        for (int i = 0; i < this.count; ++i) {
            final GraphicsNode node = this.children[i];
            ((AbstractGraphicsNode)node).setRoot(newRoot);
        }
    }
    
    @Override
    public int size() {
        return this.count;
    }
    
    @Override
    public boolean isEmpty() {
        return this.count == 0;
    }
    
    @Override
    public boolean contains(final Object node) {
        return this.indexOf(node) >= 0;
    }
    
    @Override
    public Iterator iterator() {
        return new Itr();
    }
    
    @Override
    public Object[] toArray() {
        final GraphicsNode[] result = new GraphicsNode[this.count];
        System.arraycopy(this.children, 0, result, 0, this.count);
        return result;
    }
    
    @Override
    public Object[] toArray(Object[] a) {
        if (a.length < this.count) {
            a = new GraphicsNode[this.count];
        }
        System.arraycopy(this.children, 0, a, 0, this.count);
        if (a.length > this.count) {
            a[this.count] = null;
        }
        return a;
    }
    
    @Override
    public Object get(final int index) {
        this.checkRange(index);
        return this.children[index];
    }
    
    @Override
    public Object set(final int index, final Object o) {
        if (!(o instanceof GraphicsNode)) {
            throw new IllegalArgumentException(o + " is not a GraphicsNode");
        }
        this.checkRange(index);
        final GraphicsNode node = (GraphicsNode)o;
        this.fireGraphicsNodeChangeStarted(node);
        if (node.getParent() != null) {
            node.getParent().getChildren().remove(node);
        }
        final GraphicsNode oldNode = this.children[index];
        this.children[index] = node;
        ((AbstractGraphicsNode)node).setParent(this);
        ((AbstractGraphicsNode)oldNode).setParent(null);
        ((AbstractGraphicsNode)node).setRoot(this.getRoot());
        ((AbstractGraphicsNode)oldNode).setRoot(null);
        this.invalidateGeometryCache();
        this.fireGraphicsNodeChangeCompleted();
        return oldNode;
    }
    
    @Override
    public boolean add(final Object o) {
        if (!(o instanceof GraphicsNode)) {
            throw new IllegalArgumentException(o + " is not a GraphicsNode");
        }
        final GraphicsNode node = (GraphicsNode)o;
        this.fireGraphicsNodeChangeStarted(node);
        if (node.getParent() != null) {
            node.getParent().getChildren().remove(node);
        }
        this.ensureCapacity(this.count + 1);
        this.children[this.count++] = node;
        ((AbstractGraphicsNode)node).setParent(this);
        ((AbstractGraphicsNode)node).setRoot(this.getRoot());
        this.invalidateGeometryCache();
        this.fireGraphicsNodeChangeCompleted();
        return true;
    }
    
    @Override
    public void add(final int index, final Object o) {
        if (!(o instanceof GraphicsNode)) {
            throw new IllegalArgumentException(o + " is not a GraphicsNode");
        }
        if (index > this.count || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.count);
        }
        final GraphicsNode node = (GraphicsNode)o;
        this.fireGraphicsNodeChangeStarted(node);
        if (node.getParent() != null) {
            node.getParent().getChildren().remove(node);
        }
        this.ensureCapacity(this.count + 1);
        System.arraycopy(this.children, index, this.children, index + 1, this.count - index);
        this.children[index] = node;
        ++this.count;
        ((AbstractGraphicsNode)node).setParent(this);
        ((AbstractGraphicsNode)node).setRoot(this.getRoot());
        this.invalidateGeometryCache();
        this.fireGraphicsNodeChangeCompleted();
    }
    
    @Override
    public boolean addAll(final Collection c) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAll(final int index, final Collection c) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean remove(final Object o) {
        if (!(o instanceof GraphicsNode)) {
            throw new IllegalArgumentException(o + " is not a GraphicsNode");
        }
        final GraphicsNode node = (GraphicsNode)o;
        if (node.getParent() != this) {
            return false;
        }
        int index;
        for (index = 0; node != this.children[index]; ++index) {}
        this.remove(index);
        return true;
    }
    
    @Override
    public Object remove(final int index) {
        this.checkRange(index);
        final GraphicsNode oldNode = this.children[index];
        this.fireGraphicsNodeChangeStarted(oldNode);
        ++this.modCount;
        final int numMoved = this.count - index - 1;
        if (numMoved > 0) {
            System.arraycopy(this.children, index + 1, this.children, index, numMoved);
        }
        this.children[--this.count] = null;
        if (this.count == 0) {
            this.children = null;
        }
        ((AbstractGraphicsNode)oldNode).setParent(null);
        ((AbstractGraphicsNode)oldNode).setRoot(null);
        this.invalidateGeometryCache();
        this.fireGraphicsNodeChangeCompleted();
        return oldNode;
    }
    
    @Override
    public boolean removeAll(final Collection c) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean retainAll(final Collection c) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean containsAll(final Collection c) {
        for (final Object aC : c) {
            if (!this.contains(aC)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int indexOf(final Object node) {
        if (node == null || !(node instanceof GraphicsNode)) {
            return -1;
        }
        if (((GraphicsNode)node).getParent() == this) {
            final int iCount = this.count;
            final GraphicsNode[] workList = this.children;
            for (int i = 0; i < iCount; ++i) {
                if (node == workList[i]) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    @Override
    public int lastIndexOf(final Object node) {
        if (node == null || !(node instanceof GraphicsNode)) {
            return -1;
        }
        if (((GraphicsNode)node).getParent() == this) {
            for (int i = this.count - 1; i >= 0; --i) {
                if (node == this.children[i]) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    @Override
    public ListIterator listIterator() {
        return this.listIterator(0);
    }
    
    @Override
    public ListIterator listIterator(final int index) {
        if (index < 0 || index > this.count) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        return new ListItr(index);
    }
    
    @Override
    public List subList(final int fromIndex, final int toIndex) {
        throw new UnsupportedOperationException();
    }
    
    private void checkRange(final int index) {
        if (index >= this.count || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.count);
        }
    }
    
    public void ensureCapacity(final int minCapacity) {
        if (this.children == null) {
            this.children = new GraphicsNode[4];
        }
        ++this.modCount;
        final int oldCapacity = this.children.length;
        if (minCapacity > oldCapacity) {
            final GraphicsNode[] oldData = this.children;
            int newCapacity = oldCapacity + oldCapacity / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            System.arraycopy(oldData, 0, this.children = new GraphicsNode[newCapacity], 0, this.count);
        }
    }
    
    static {
        VIEWPORT = new Rectangle();
        NULL_RECT = new Rectangle();
    }
    
    private class Itr implements Iterator
    {
        int cursor;
        int lastRet;
        int expectedModCount;
        
        private Itr() {
            this.cursor = 0;
            this.lastRet = -1;
            this.expectedModCount = CompositeGraphicsNode.this.modCount;
        }
        
        @Override
        public boolean hasNext() {
            return this.cursor != CompositeGraphicsNode.this.count;
        }
        
        @Override
        public Object next() {
            try {
                final Object next = CompositeGraphicsNode.this.get(this.cursor);
                this.checkForComodification();
                this.lastRet = this.cursor++;
                return next;
            }
            catch (IndexOutOfBoundsException e) {
                this.checkForComodification();
                throw new NoSuchElementException();
            }
        }
        
        @Override
        public void remove() {
            if (this.lastRet == -1) {
                throw new IllegalStateException();
            }
            this.checkForComodification();
            try {
                CompositeGraphicsNode.this.remove(this.lastRet);
                if (this.lastRet < this.cursor) {
                    --this.cursor;
                }
                this.lastRet = -1;
                this.expectedModCount = CompositeGraphicsNode.this.modCount;
            }
            catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
        
        final void checkForComodification() {
            if (CompositeGraphicsNode.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
    
    private class ListItr extends Itr implements ListIterator
    {
        ListItr(final int index) {
            this.cursor = index;
        }
        
        @Override
        public boolean hasPrevious() {
            return this.cursor != 0;
        }
        
        @Override
        public Object previous() {
            try {
                final CompositeGraphicsNode this$0 = CompositeGraphicsNode.this;
                final int n = this.cursor - 1;
                this.cursor = n;
                final Object previous = this$0.get(n);
                this.checkForComodification();
                this.lastRet = this.cursor;
                return previous;
            }
            catch (IndexOutOfBoundsException e) {
                this.checkForComodification();
                throw new NoSuchElementException();
            }
        }
        
        @Override
        public int nextIndex() {
            return this.cursor;
        }
        
        @Override
        public int previousIndex() {
            return this.cursor - 1;
        }
        
        @Override
        public void set(final Object o) {
            if (this.lastRet == -1) {
                throw new IllegalStateException();
            }
            this.checkForComodification();
            try {
                CompositeGraphicsNode.this.set(this.lastRet, o);
                this.expectedModCount = CompositeGraphicsNode.this.modCount;
            }
            catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
        
        @Override
        public void add(final Object o) {
            this.checkForComodification();
            try {
                CompositeGraphicsNode.this.add(this.cursor++, o);
                this.lastRet = -1;
                this.expectedModCount = CompositeGraphicsNode.this.modCount;
            }
            catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
