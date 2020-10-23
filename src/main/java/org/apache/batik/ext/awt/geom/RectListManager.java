// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.geom;

import java.util.NoSuchElementException;
import java.io.Serializable;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Comparator;
import java.awt.Rectangle;
import java.util.Collection;

public class RectListManager implements Collection
{
    Rectangle[] rects;
    int size;
    Rectangle bounds;
    public static Comparator comparator;
    
    public void dump() {
        System.err.println("RLM: " + this + " Sz: " + this.size);
        System.err.println("Bounds: " + this.getBounds());
        for (int i = 0; i < this.size; ++i) {
            final Rectangle r = this.rects[i];
            System.err.println("  [" + r.x + ", " + r.y + ", " + r.width + ", " + r.height + ']');
        }
    }
    
    public RectListManager(final Collection rects) {
        this.rects = null;
        this.size = 0;
        this.bounds = null;
        this.rects = new Rectangle[rects.size()];
        final Iterator i = rects.iterator();
        int j = 0;
        while (i.hasNext()) {
            this.rects[j++] = i.next();
        }
        this.size = this.rects.length;
        Arrays.sort(this.rects, RectListManager.comparator);
    }
    
    public RectListManager(final Rectangle[] rects) {
        this(rects, 0, rects.length);
    }
    
    public RectListManager(final Rectangle[] rects, final int off, final int sz) {
        this.rects = null;
        this.size = 0;
        this.bounds = null;
        this.size = sz;
        System.arraycopy(rects, off, this.rects = new Rectangle[sz], 0, sz);
        Arrays.sort(this.rects, RectListManager.comparator);
    }
    
    public RectListManager(final RectListManager rlm) {
        this(rlm.rects);
    }
    
    public RectListManager(final Rectangle rect) {
        this();
        this.add(rect);
    }
    
    public RectListManager() {
        this.rects = null;
        this.size = 0;
        this.bounds = null;
        this.rects = new Rectangle[10];
        this.size = 0;
    }
    
    public RectListManager(final int capacity) {
        this.rects = null;
        this.size = 0;
        this.bounds = null;
        this.rects = new Rectangle[capacity];
    }
    
    public Rectangle getBounds() {
        if (this.bounds != null) {
            return this.bounds;
        }
        if (this.size == 0) {
            return null;
        }
        this.bounds = new Rectangle(this.rects[0]);
        for (int i = 1; i < this.size; ++i) {
            final Rectangle r = this.rects[i];
            if (r.x < this.bounds.x) {
                this.bounds.width = this.bounds.x + this.bounds.width - r.x;
                this.bounds.x = r.x;
            }
            if (r.y < this.bounds.y) {
                this.bounds.height = this.bounds.y + this.bounds.height - r.y;
                this.bounds.y = r.y;
            }
            if (r.x + r.width > this.bounds.x + this.bounds.width) {
                this.bounds.width = r.x + r.width - this.bounds.x;
            }
            if (r.y + r.height > this.bounds.y + this.bounds.height) {
                this.bounds.height = r.y + r.height - this.bounds.y;
            }
        }
        return this.bounds;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return this.copy();
    }
    
    public RectListManager copy() {
        return new RectListManager(this.rects);
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    @Override
    public void clear() {
        Arrays.fill(this.rects, null);
        this.size = 0;
        this.bounds = null;
    }
    
    @Override
    public Iterator iterator() {
        return new RLMIterator();
    }
    
    public ListIterator listIterator() {
        return new RLMIterator();
    }
    
    @Override
    public Object[] toArray() {
        final Object[] ret = new Rectangle[this.size];
        System.arraycopy(this.rects, 0, ret, 0, this.size);
        return ret;
    }
    
    @Override
    public Object[] toArray(Object[] a) {
        final Class t = a.getClass().getComponentType();
        if (t != Object.class && t != Rectangle.class) {
            Arrays.fill(a, null);
            return a;
        }
        if (a.length < this.size) {
            a = new Rectangle[this.size];
        }
        System.arraycopy(this.rects, 0, a, 0, this.size);
        Arrays.fill(a, this.size, a.length, null);
        return a;
    }
    
    @Override
    public boolean add(final Object o) {
        this.add((Rectangle)o);
        return true;
    }
    
    public void add(final Rectangle rect) {
        this.add(rect, 0, this.size - 1);
    }
    
    protected void add(final Rectangle rect, int l, int r) {
        this.ensureCapacity(this.size + 1);
        int idx = l;
        while (l <= r) {
            for (idx = (l + r) / 2; this.rects[idx] == null && idx < r; ++idx) {}
            if (this.rects[idx] == null) {
                r = (l + r) / 2;
                idx = (l + r) / 2;
                if (l > r) {
                    idx = l;
                }
                while (this.rects[idx] == null && idx > l) {
                    --idx;
                }
                if (this.rects[idx] == null) {
                    this.rects[idx] = rect;
                    return;
                }
            }
            if (rect.x == this.rects[idx].x) {
                break;
            }
            if (rect.x < this.rects[idx].x) {
                if (idx == 0) {
                    break;
                }
                if (this.rects[idx - 1] != null && rect.x >= this.rects[idx - 1].x) {
                    break;
                }
                r = idx - 1;
            }
            else {
                if (idx == this.size - 1) {
                    ++idx;
                    break;
                }
                if (this.rects[idx + 1] != null && rect.x <= this.rects[idx + 1].x) {
                    ++idx;
                    break;
                }
                l = idx + 1;
            }
        }
        if (idx < this.size) {
            System.arraycopy(this.rects, idx, this.rects, idx + 1, this.size - idx);
        }
        this.rects[idx] = rect;
        ++this.size;
        this.bounds = null;
    }
    
    @Override
    public boolean addAll(final Collection c) {
        if (c instanceof RectListManager) {
            this.add((RectListManager)c);
        }
        else {
            this.add(new RectListManager(c));
        }
        return c.size() != 0;
    }
    
    @Override
    public boolean contains(final Object o) {
        final Rectangle rect = (Rectangle)o;
        int l = 0;
        int r = this.size - 1;
        int idx = 0;
        while (l <= r) {
            idx = l + r >>> 1;
            if (rect.x == this.rects[idx].x) {
                break;
            }
            if (rect.x < this.rects[idx].x) {
                if (idx == 0) {
                    break;
                }
                if (rect.x >= this.rects[idx - 1].x) {
                    break;
                }
                r = idx - 1;
            }
            else {
                if (idx == this.size - 1) {
                    ++idx;
                    break;
                }
                if (rect.x <= this.rects[idx + 1].x) {
                    ++idx;
                    break;
                }
                l = idx + 1;
            }
        }
        if (this.rects[idx].x != rect.x) {
            return false;
        }
        for (int i = idx; i >= 0; --i) {
            if (this.rects[idx].equals(rect)) {
                return true;
            }
            if (this.rects[idx].x != rect.x) {
                break;
            }
        }
        for (int i = idx + 1; i < this.size; ++i) {
            if (this.rects[idx].equals(rect)) {
                return true;
            }
            if (this.rects[idx].x != rect.x) {
                break;
            }
        }
        return false;
    }
    
    @Override
    public boolean containsAll(final Collection c) {
        if (c instanceof RectListManager) {
            return this.containsAll((RectListManager)c);
        }
        return this.containsAll(new RectListManager(c));
    }
    
    public boolean containsAll(final RectListManager rlm) {
        int xChange = 0;
        int j = 0;
        int i = 0;
        while (j < rlm.size) {
            i = xChange;
            while (this.rects[i].x < rlm.rects[j].x) {
                if (++i == this.size) {
                    return false;
                }
            }
            xChange = i;
            final int x = this.rects[i].x;
            while (!rlm.rects[j].equals(this.rects[i])) {
                if (++i == this.size) {
                    return false;
                }
                if (x != this.rects[i].x) {
                    return false;
                }
            }
            ++j;
        }
        return true;
    }
    
    @Override
    public boolean remove(final Object o) {
        return this.remove((Rectangle)o);
    }
    
    public boolean remove(final Rectangle rect) {
        int l = 0;
        int r = this.size - 1;
        int idx = 0;
        while (l <= r) {
            idx = l + r >>> 1;
            if (rect.x == this.rects[idx].x) {
                break;
            }
            if (rect.x < this.rects[idx].x) {
                if (idx == 0) {
                    break;
                }
                if (rect.x >= this.rects[idx - 1].x) {
                    break;
                }
                r = idx - 1;
            }
            else {
                if (idx == this.size - 1) {
                    ++idx;
                    break;
                }
                if (rect.x <= this.rects[idx + 1].x) {
                    ++idx;
                    break;
                }
                l = idx + 1;
            }
        }
        if (this.rects[idx].x != rect.x) {
            return false;
        }
        for (int i = idx; i >= 0; --i) {
            if (this.rects[idx].equals(rect)) {
                System.arraycopy(this.rects, idx + 1, this.rects, idx, this.size - idx);
                --this.size;
                this.bounds = null;
                return true;
            }
            if (this.rects[idx].x != rect.x) {
                break;
            }
        }
        for (int i = idx + 1; i < this.size; ++i) {
            if (this.rects[idx].equals(rect)) {
                System.arraycopy(this.rects, idx + 1, this.rects, idx, this.size - idx);
                --this.size;
                this.bounds = null;
                return true;
            }
            if (this.rects[idx].x != rect.x) {
                break;
            }
        }
        return false;
    }
    
    @Override
    public boolean removeAll(final Collection c) {
        if (c instanceof RectListManager) {
            return this.removeAll((RectListManager)c);
        }
        return this.removeAll(new RectListManager(c));
    }
    
    public boolean removeAll(final RectListManager rlm) {
        int xChange = 0;
        boolean ret = false;
        int j = 0;
        int i = 0;
        while (j < rlm.size) {
            i = xChange;
            while ((this.rects[i] == null || this.rects[i].x < rlm.rects[j].x) && ++i != this.size) {}
            if (i == this.size) {
                break;
            }
            xChange = i;
            final int x = this.rects[i].x;
            while (true) {
                if (this.rects[i] == null) {
                    if (++i == this.size) {
                        break;
                    }
                    continue;
                }
                else {
                    if (rlm.rects[j].equals(this.rects[i])) {
                        this.rects[i] = null;
                        ret = true;
                    }
                    if (++i == this.size) {
                        break;
                    }
                    if (x != this.rects[i].x) {
                        break;
                    }
                    continue;
                }
            }
            ++j;
        }
        if (ret) {
            j = 0;
            for (i = 0; i < this.size; ++i) {
                if (this.rects[i] != null) {
                    this.rects[j++] = this.rects[i];
                }
            }
            this.size = j;
            this.bounds = null;
        }
        return ret;
    }
    
    @Override
    public boolean retainAll(final Collection c) {
        if (c instanceof RectListManager) {
            return this.retainAll((RectListManager)c);
        }
        return this.retainAll(new RectListManager(c));
    }
    
    public boolean retainAll(final RectListManager rlm) {
        int xChange = 0;
        boolean ret = false;
        int j = 0;
        int i = 0;
    Label_0011:
        while (j < this.size) {
            i = xChange;
            while (rlm.rects[i].x < this.rects[j].x && ++i != rlm.size) {}
            if (i == rlm.size) {
                ret = true;
                for (int k = j; k < this.size; ++k) {
                    this.rects[k] = null;
                }
                this.size = j;
                break;
            }
            xChange = i;
            final int x = rlm.rects[i].x;
            while (true) {
                while (!this.rects[j].equals(rlm.rects[i])) {
                    if (++i == rlm.size || x != rlm.rects[i].x) {
                        this.rects[j] = null;
                        ret = true;
                        ++j;
                        continue Label_0011;
                    }
                }
                continue;
            }
        }
        if (ret) {
            j = 0;
            for (i = 0; i < this.size; ++i) {
                if (this.rects[i] != null) {
                    this.rects[j++] = this.rects[i];
                }
            }
            this.size = j;
            this.bounds = null;
        }
        return ret;
    }
    
    public void add(final RectListManager rlm) {
        if (rlm.size == 0) {
            return;
        }
        Rectangle[] dst = this.rects;
        if (this.rects.length < this.size + rlm.size) {
            dst = new Rectangle[this.size + rlm.size];
        }
        if (this.size == 0) {
            System.arraycopy(rlm.rects, 0, dst, this.size, rlm.size);
            this.size = rlm.size;
            this.bounds = null;
            return;
        }
        final Rectangle[] src1 = rlm.rects;
        final int src1Sz = rlm.size;
        int src1I = src1Sz - 1;
        final Rectangle[] src2 = this.rects;
        final int src2Sz = this.size;
        int src2I = src2Sz - 1;
        int dstI = this.size + rlm.size - 1;
        int x1 = src1[src1I].x;
        int x2 = src2[src2I].x;
        while (dstI >= 0) {
            if (x1 <= x2) {
                dst[dstI] = src2[src2I];
                if (src2I == 0) {
                    System.arraycopy(src1, 0, dst, 0, src1I + 1);
                    break;
                }
                --src2I;
                x2 = src2[src2I].x;
            }
            else {
                dst[dstI] = src1[src1I];
                if (src1I == 0) {
                    System.arraycopy(src2, 0, dst, 0, src2I + 1);
                    break;
                }
                --src1I;
                x1 = src1[src1I].x;
            }
            --dstI;
        }
        this.rects = dst;
        this.size += rlm.size;
        this.bounds = null;
    }
    
    public void mergeRects(final int overhead, final int lineOverhead) {
        if (this.size == 0) {
            return;
        }
        final Rectangle[] splits = new Rectangle[4];
        for (int i = 0; i < this.size; ++i) {
            Rectangle r = this.rects[i];
            if (r != null) {
                int cost1 = overhead + r.height * lineOverhead + r.height * r.width;
                int j;
                do {
                    final int maxX = r.x + r.width + overhead / r.height;
                    for (j = i + 1; j < this.size; ++j) {
                        final Rectangle cr = this.rects[j];
                        if (cr != null) {
                            if (cr != r) {
                                if (cr.x >= maxX) {
                                    j = this.size;
                                    break;
                                }
                                final int cost2 = overhead + cr.height * lineOverhead + cr.height * cr.width;
                                final Rectangle mr = r.union(cr);
                                final int cost3 = overhead + mr.height * lineOverhead + mr.height * mr.width;
                                if (cost3 <= cost1 + cost2) {
                                    final Rectangle[] rects = this.rects;
                                    final int n = i;
                                    final Rectangle rectangle = mr;
                                    rects[n] = rectangle;
                                    r = rectangle;
                                    this.rects[j] = null;
                                    cost1 = cost3;
                                    j = -1;
                                    break;
                                }
                                if (r.intersects(cr)) {
                                    this.splitRect(cr, r, splits);
                                    int splitCost = 0;
                                    int l = 0;
                                    for (int k = 0; k < 4; ++k) {
                                        if (splits[k] != null) {
                                            final Rectangle sr = splits[k];
                                            if (k < 3) {
                                                splits[l++] = sr;
                                            }
                                            splitCost += overhead + sr.height * lineOverhead + sr.height * sr.width;
                                        }
                                    }
                                    if (splitCost < cost2) {
                                        if (l == 0) {
                                            this.rects[j] = null;
                                            if (splits[3] != null) {
                                                this.add(splits[3], j, this.size - 1);
                                            }
                                        }
                                        else {
                                            this.rects[j] = splits[0];
                                            if (l > 1) {
                                                this.insertRects(splits, 1, j + 1, l - 1);
                                            }
                                            if (splits[3] != null) {
                                                this.add(splits[3], j, this.size - 1);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } while (j != this.size);
            }
        }
        int j = 0;
        int i = 0;
        float area = 0.0f;
        while (i < this.size) {
            if (this.rects[i] != null) {
                final Rectangle r = this.rects[i];
                this.rects[j++] = r;
                area += overhead + r.height * lineOverhead + r.height * r.width;
            }
            ++i;
        }
        this.size = j;
        this.bounds = null;
        Rectangle r = this.getBounds();
        if (r == null) {
            return;
        }
        if (overhead + r.height * lineOverhead + r.height * r.width < area) {
            this.rects[0] = r;
            this.size = 1;
        }
    }
    
    public void subtract(final RectListManager rlm, final int overhead, final int lineOverhead) {
        int jMin = 0;
        final Rectangle[] splits = new Rectangle[4];
        for (int i = 0; i < this.size; ++i) {
            Rectangle r = this.rects[i];
            int cost = overhead + r.height * lineOverhead + r.height * r.width;
            for (int j = jMin; j < rlm.size; ++j) {
                final Rectangle sr = rlm.rects[j];
                if (sr.x + sr.width < r.x) {
                    if (j == jMin) {
                        ++jMin;
                    }
                }
                else {
                    if (sr.x > r.x + r.width) {
                        break;
                    }
                    if (r.intersects(sr)) {
                        this.splitRect(r, sr, splits);
                        int splitCost = 0;
                        for (int k = 0; k < 4; ++k) {
                            final Rectangle tmpR = splits[k];
                            if (tmpR != null) {
                                splitCost += overhead + tmpR.height * lineOverhead + tmpR.height * tmpR.width;
                            }
                        }
                        if (splitCost < cost) {
                            int l = 0;
                            for (int m = 0; m < 3; ++m) {
                                if (splits[m] != null) {
                                    splits[l++] = splits[m];
                                }
                            }
                            if (l == 0) {
                                this.rects[i].width = 0;
                                if (splits[3] != null) {
                                    this.add(splits[3], i, this.size - 1);
                                    break;
                                }
                                break;
                            }
                            else {
                                r = splits[0];
                                this.rects[i] = r;
                                cost = overhead + r.height * lineOverhead + r.height * r.width;
                                if (l > 1) {
                                    this.insertRects(splits, 1, i + 1, l - 1);
                                }
                                if (splits[3] != null) {
                                    this.add(splits[3], i + l, this.size - 1);
                                }
                            }
                        }
                    }
                }
            }
        }
        int j2 = 0;
        for (int i2 = 0; i2 < this.size; ++i2) {
            if (this.rects[i2].width == 0) {
                this.rects[i2] = null;
            }
            else {
                this.rects[j2++] = this.rects[i2];
            }
        }
        this.size = j2;
        this.bounds = null;
    }
    
    protected void splitRect(final Rectangle r, final Rectangle sr, final Rectangle[] splits) {
        final int rx0 = r.x;
        final int rx2 = rx0 + r.width - 1;
        int ry0 = r.y;
        int ry2 = ry0 + r.height - 1;
        final int srx0 = sr.x;
        final int srx2 = srx0 + sr.width - 1;
        final int sry0 = sr.y;
        final int sry2 = sry0 + sr.height - 1;
        if (ry0 < sry0 && ry2 >= sry0) {
            splits[0] = new Rectangle(rx0, ry0, r.width, sry0 - ry0);
            ry0 = sry0;
        }
        else {
            splits[0] = null;
        }
        if (ry0 <= sry2 && ry2 > sry2) {
            splits[1] = new Rectangle(rx0, sry2 + 1, r.width, ry2 - sry2);
            ry2 = sry2;
        }
        else {
            splits[1] = null;
        }
        if (rx0 < srx0 && rx2 >= srx0) {
            splits[2] = new Rectangle(rx0, ry0, srx0 - rx0, ry2 - ry0 + 1);
        }
        else {
            splits[2] = null;
        }
        if (rx0 <= srx2 && rx2 > srx2) {
            splits[3] = new Rectangle(srx2 + 1, ry0, rx2 - srx2, ry2 - ry0 + 1);
        }
        else {
            splits[3] = null;
        }
    }
    
    protected void insertRects(final Rectangle[] rects, final int srcPos, final int dstPos, final int len) {
        if (len == 0) {
            return;
        }
        this.ensureCapacity(this.size + len);
        for (int i = this.size - 1; i >= dstPos; --i) {
            this.rects[i + len] = this.rects[i];
        }
        System.arraycopy(rects, srcPos, this.rects, dstPos, len);
        this.size += len;
    }
    
    public void ensureCapacity(final int sz) {
        if (sz <= this.rects.length) {
            return;
        }
        int nSz;
        for (nSz = this.rects.length + (this.rects.length >> 1) + 1; nSz < sz; nSz += (nSz >> 1) + 1) {}
        final Rectangle[] nRects = new Rectangle[nSz];
        System.arraycopy(this.rects, 0, nRects, 0, this.size);
        this.rects = nRects;
    }
    
    static {
        RectListManager.comparator = new RectXComparator();
    }
    
    private static class RectXComparator implements Comparator, Serializable
    {
        RectXComparator() {
        }
        
        @Override
        public final int compare(final Object o1, final Object o2) {
            return ((Rectangle)o1).x - ((Rectangle)o2).x;
        }
    }
    
    private class RLMIterator implements ListIterator
    {
        int idx;
        boolean removeOk;
        boolean forward;
        
        RLMIterator() {
            this.idx = 0;
            this.removeOk = false;
            this.forward = true;
        }
        
        @Override
        public boolean hasNext() {
            return this.idx < RectListManager.this.size;
        }
        
        @Override
        public int nextIndex() {
            return this.idx;
        }
        
        @Override
        public Object next() {
            if (this.idx >= RectListManager.this.size) {
                throw new NoSuchElementException("No Next Element");
            }
            this.forward = true;
            this.removeOk = true;
            return RectListManager.this.rects[this.idx++];
        }
        
        @Override
        public boolean hasPrevious() {
            return this.idx > 0;
        }
        
        @Override
        public int previousIndex() {
            return this.idx - 1;
        }
        
        @Override
        public Object previous() {
            if (this.idx <= 0) {
                throw new NoSuchElementException("No Previous Element");
            }
            this.forward = false;
            this.removeOk = true;
            final Rectangle[] rects = RectListManager.this.rects;
            final int idx = this.idx - 1;
            this.idx = idx;
            return rects[idx];
        }
        
        @Override
        public void remove() {
            if (!this.removeOk) {
                throw new IllegalStateException("remove can only be called directly after next/previous");
            }
            if (this.forward) {
                --this.idx;
            }
            if (this.idx != RectListManager.this.size - 1) {
                System.arraycopy(RectListManager.this.rects, this.idx + 1, RectListManager.this.rects, this.idx, RectListManager.this.size - (this.idx + 1));
            }
            final RectListManager this$0 = RectListManager.this;
            --this$0.size;
            RectListManager.this.rects[RectListManager.this.size] = null;
            this.removeOk = false;
        }
        
        @Override
        public void set(final Object o) {
            final Rectangle r = (Rectangle)o;
            if (!this.removeOk) {
                throw new IllegalStateException("set can only be called directly after next/previous");
            }
            if (this.forward) {
                --this.idx;
            }
            if (this.idx + 1 < RectListManager.this.size && RectListManager.this.rects[this.idx + 1].x < r.x) {
                throw new UnsupportedOperationException("RectListManager entries must be sorted");
            }
            if (this.idx >= 0 && RectListManager.this.rects[this.idx - 1].x > r.x) {
                throw new UnsupportedOperationException("RectListManager entries must be sorted");
            }
            RectListManager.this.rects[this.idx] = r;
            this.removeOk = false;
        }
        
        @Override
        public void add(final Object o) {
            final Rectangle r = (Rectangle)o;
            if (this.idx < RectListManager.this.size && RectListManager.this.rects[this.idx].x < r.x) {
                throw new UnsupportedOperationException("RectListManager entries must be sorted");
            }
            if (this.idx != 0 && RectListManager.this.rects[this.idx - 1].x > r.x) {
                throw new UnsupportedOperationException("RectListManager entries must be sorted");
            }
            RectListManager.this.ensureCapacity(RectListManager.this.size + 1);
            if (this.idx != RectListManager.this.size) {
                System.arraycopy(RectListManager.this.rects, this.idx, RectListManager.this.rects, this.idx + 1, RectListManager.this.size - this.idx);
            }
            RectListManager.this.rects[this.idx] = r;
            ++this.idx;
            this.removeOk = false;
        }
    }
}
