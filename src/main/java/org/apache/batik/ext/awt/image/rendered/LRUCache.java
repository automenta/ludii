// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import org.apache.batik.util.DoublyLinkedList;

public class LRUCache
{
    private DoublyLinkedList free;
    private DoublyLinkedList used;
    private int maxSize;
    
    public LRUCache(int size) {
        this.free = null;
        this.used = null;
        this.maxSize = 0;
        if (size <= 0) {
            size = 1;
        }
        this.maxSize = size;
        this.free = new DoublyLinkedList();
        this.used = new DoublyLinkedList();
        while (size > 0) {
            this.free.add(new LRUNode());
            --size;
        }
    }
    
    public int getUsed() {
        return this.used.getSize();
    }
    
    public synchronized void setSize(final int newSz) {
        if (this.maxSize < newSz) {
            for (int i = this.maxSize; i < newSz; ++i) {
                this.free.add(new LRUNode());
            }
        }
        else if (this.maxSize > newSz) {
            for (int i = this.used.getSize(); i > newSz; --i) {
                final LRUNode nde = (LRUNode)this.used.getTail();
                this.used.remove(nde);
                nde.setObj(null);
            }
        }
        this.maxSize = newSz;
    }
    
    public synchronized void flush() {
        while (this.used.getSize() > 0) {
            final LRUNode nde = (LRUNode)this.used.pop();
            nde.setObj(null);
            this.free.add(nde);
        }
    }
    
    public synchronized void remove(final LRUObj obj) {
        final LRUNode nde = obj.lruGet();
        if (nde == null) {
            return;
        }
        this.used.remove(nde);
        nde.setObj(null);
        this.free.add(nde);
    }
    
    public synchronized void touch(final LRUObj obj) {
        final LRUNode nde = obj.lruGet();
        if (nde == null) {
            return;
        }
        this.used.touch(nde);
    }
    
    public synchronized void add(final LRUObj obj) {
        LRUNode nde = obj.lruGet();
        if (nde != null) {
            this.used.touch(nde);
            return;
        }
        if (this.free.getSize() > 0) {
            nde = (LRUNode)this.free.pop();
            nde.setObj(obj);
            this.used.add(nde);
        }
        else {
            nde = (LRUNode)this.used.getTail();
            nde.setObj(obj);
            this.used.touch(nde);
        }
    }
    
    protected synchronized void print() {
        System.out.println("In Use: " + this.used.getSize() + " Free: " + this.free.getSize());
        LRUNode nde = (LRUNode)this.used.getHead();
        if (nde == null) {
            return;
        }
        do {
            System.out.println(nde.getObj());
            nde = (LRUNode)nde.getNext();
        } while (nde != this.used.getHead());
    }
    
    public static class LRUNode extends DoublyLinkedList.Node
    {
        private LRUObj obj;
        
        public LRUNode() {
            this.obj = null;
        }
        
        public LRUObj getObj() {
            return this.obj;
        }
        
        protected void setObj(final LRUObj newObj) {
            if (this.obj != null) {
                this.obj.lruRemove();
            }
            this.obj = newObj;
            if (this.obj != null) {
                this.obj.lruSet(this);
            }
        }
    }
    
    public interface LRUObj
    {
        void lruSet(final LRUNode p0);
        
        LRUNode lruGet();
        
        void lruRemove();
    }
}
