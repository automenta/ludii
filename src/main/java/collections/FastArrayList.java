// 
// Decompiled by Procyon v0.5.36
// 

package collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Objects;

public class FastArrayList<E> implements Iterable<E>, Serializable
{
    private static final long serialVersionUID = 1L;
    protected transient Object[] data;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;
    protected transient int modCount;
    
    public FastArrayList() {
        this(DEFAULT_CAPACITY);
    }
    
    public FastArrayList(final int initialCapacity) {
        this.modCount = 0;
        this.data = new Object[initialCapacity];
    }
    
    public FastArrayList(final FastArrayList<E> other) {
        this.modCount = 0;
        this.data = Arrays.copyOf(other.data, other.size);
        this.size = this.data.length;
    }
    
    @SafeVarargs
    public FastArrayList(final E... elements) {
        this.modCount = 0;
        this.data = Arrays.copyOf(elements, elements.length);
        this.size = this.data.length;
    }
    
    public void add(final E e) {
        ++this.modCount;
        this.ensureCapacityInternal(this.size + 1);
        this.data[this.size++] = e;
    }
    
    public void add(final int index, final E e) {
        ++this.modCount;
        final int s;
        if ((s = this.size) == this.data.length) {
            this.grow(this.size + 1);
        }
        System.arraycopy(this.data, index, this.data, index + 1, s - index);
        this.data[index] = e;
        this.size = s + 1;
    }
    
    public void addAll(final FastArrayList<E> other) {
        final Object[] otherData = other.data;
        ++this.modCount;
        final int numNew = other.size();
        this.ensureCapacityInternal(this.size + numNew);
        System.arraycopy(otherData, 0, this.data, this.size, numNew);
        this.size += numNew;
    }
    
    public E remove(final int index) {
        final E r = (E)this.data[index];
        ++this.modCount;
        final int size = this.size - 1;
        this.size = size;
        if (index != size) {
            System.arraycopy(this.data, index + 1, this.data, index, this.size - index);
        }
        this.data[this.size] = null;
        return r;
    }
    
    public E removeSwap(final int index) {
        final E r = (E)this.data[index];
        ++this.modCount;
        final int size = this.size - 1;
        this.size = size;
        if (index != size) {
            this.data[index] = this.data[this.size];
        }
        this.data[this.size] = null;
        return r;
    }
    
    public void clear() {
        ++this.modCount;
        for (int i = 0; i < this.size; ++i) {
            this.data[i] = null;
        }
        this.size = 0;
    }
    
    public boolean contains(final Object o) {
        return this.indexOf(o) >= 0;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FastArrayList)) {
            return false;
        }
        final int expectedModCount = this.modCount;
        try {
            final FastArrayList<E> other = (FastArrayList<E>)o;
            if (this.size != other.size) {
                return false;
            }
            for (int i = 0; i < this.size; ++i) {
                if (!Objects.equals(this.data[i], other.data[i])) {
                    return false;
                }
            }
        }
        finally {
            this.checkForComodification(expectedModCount);
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final int expectedModCount = this.modCount;
        int hash = 1;
        for (int i = 0; i < this.size; ++i) {
            final Object e = this.data[i];
            hash = 31 * hash + ((e == null) ? 0 : e.hashCode());
        }
        this.checkForComodification(expectedModCount);
        return hash;
    }
    
    public E get(final int i) {
        return (E)this.data[i];
    }
    
    public int indexOf(final Object o) {
        if (o == null) {
            for (int i = 0; i < this.size; ++i) {
                if (this.data[i] == null) {
                    return i;
                }
            }
        }
        else {
            for (int i = 0; i < this.size; ++i) {
                if (o.equals(this.data[i])) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    public void retainAll(final FastArrayList other) {
        this.batchRemove(other, true);
    }
    
    public int size() {
        return this.size;
    }
    
    public Object[] toArray() {
        return Arrays.copyOf(this.data, this.size);
    }
    
    public <T> T[] toArray(final T[] a) {
        if (a.length < this.size) {
            return Arrays.copyOf(this.data, this.size, (Class<? extends T[]>)a.getClass());
        }
        System.arraycopy(this.data, 0, a, 0, this.size);
        if (a.length > this.size) {
            a[this.size] = null;
        }
        return a;
    }
    
    @Override
    public String toString() {
        final int iMax = this.size - 1;
        if (iMax == -1) {
            return "[]";
        }
        final StringBuilder b = new StringBuilder();
        b.append('[');
        int i = 0;
        while (true) {
            b.append(this.data[i]);
            if (i == iMax) {
                break;
            }
            b.append(", ");
            ++i;
        }
        return b.append(']').toString();
    }
    
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }
    
    private void batchRemove(final FastArrayList<E> other, final boolean complement) {
        final Object[] dataN = this.data;
        int r = 0;
        int w = 0;
        try {
            while (r < this.size) {
                if (other.contains(dataN[r]) == complement) {
                    dataN[w++] = dataN[r];
                }
                ++r;
            }
        }
        finally {
            this.modCount += this.size - w;
            if (r != this.size) {
                System.arraycopy(dataN, r, dataN, w, this.size - r);
                w += this.size - r;
            }
            if (w != this.size) {
                for (int i = w; i < this.size; ++i) {
                    dataN[i] = null;
                }
                this.size = w;
            }
        }
    }
    
    private void checkForComodification(final int expectedModCount) {
        if (this.modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }
    
    private void ensureCapacityInternal(final int minCapacity) {
        if (minCapacity - this.data.length > 0) {
            this.grow(minCapacity);
        }
    }
    
    private void grow(final int minCapacity) {
        final int oldCapacity = this.data.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        this.data = Arrays.copyOf(this.data, newCapacity);
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        final int expectedModCount = this.modCount;
        s.defaultWriteObject();
        s.writeInt(this.size);
        for (int i = 0; i < this.size; ++i) {
            s.writeObject(this.data[i]);
        }
        if (this.modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        this.data = new Object[10];
        s.defaultReadObject();
        s.readInt();
        if (this.size > 0) {
            this.ensureCapacityInternal(this.size);
            final Object[] a = this.data;
            for (int i = 0; i < this.size; ++i) {
                a[i] = s.readObject();
            }
        }
    }
    
    private class Itr implements Iterator<E>
    {
        private int cursor;
        
        private Itr() {
            this.cursor = 0;
        }
        
        @Override
        public boolean hasNext() {
            return this.cursor != FastArrayList.this.size;
        }
        
        @Override
        public E next() {
            if (this.cursor >= FastArrayList.this.data.length) {
                throw new ConcurrentModificationException();
            }
            return (E)FastArrayList.this.data[this.cursor++];
        }
    }
}
