// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

public class DoublyLinkedList
{
    private Node head;
    private int size;
    
    public DoublyLinkedList() {
        this.head = null;
        this.size = 0;
    }
    
    public synchronized int getSize() {
        return this.size;
    }
    
    public synchronized void empty() {
        while (this.size > 0) {
            this.pop();
        }
    }
    
    public Node getHead() {
        return this.head;
    }
    
    public Node getTail() {
        return this.head.getPrev();
    }
    
    public void touch(final Node nde) {
        if (nde == null) {
            return;
        }
        nde.insertBefore(this.head);
        this.head = nde;
    }
    
    public void add(int index, final Node nde) {
        if (nde == null) {
            return;
        }
        if (index == 0) {
            nde.insertBefore(this.head);
            this.head = nde;
        }
        else if (index == this.size) {
            nde.insertBefore(this.head);
        }
        else {
            Node after = this.head;
            while (index != 0) {
                after = after.getNext();
                --index;
            }
            nde.insertBefore(after);
        }
        ++this.size;
    }
    
    public void add(final Node nde) {
        if (nde == null) {
            return;
        }
        nde.insertBefore(this.head);
        this.head = nde;
        ++this.size;
    }
    
    public void remove(final Node nde) {
        if (nde == null) {
            return;
        }
        if (nde == this.head) {
            if (this.head.getNext() == this.head) {
                this.head = null;
            }
            else {
                this.head = this.head.getNext();
            }
        }
        nde.unlink();
        --this.size;
    }
    
    public Node pop() {
        if (this.head == null) {
            return null;
        }
        final Node nde = this.head;
        this.remove(nde);
        return nde;
    }
    
    public Node unpush() {
        if (this.head == null) {
            return null;
        }
        final Node nde = this.getTail();
        this.remove(nde);
        return nde;
    }
    
    public void push(final Node nde) {
        nde.insertBefore(this.head);
        if (this.head == null) {
            this.head = nde;
        }
        ++this.size;
    }
    
    public void unpop(final Node nde) {
        nde.insertBefore(this.head);
        this.head = nde;
        ++this.size;
    }
    
    public static class Node
    {
        private Node next;
        private Node prev;
        
        public Node() {
            this.next = null;
            this.prev = null;
        }
        
        public final Node getNext() {
            return this.next;
        }
        
        public final Node getPrev() {
            return this.prev;
        }
        
        protected final void setNext(final Node newNext) {
            this.next = newNext;
        }
        
        protected final void setPrev(final Node newPrev) {
            this.prev = newPrev;
        }
        
        protected final void unlink() {
            if (this.getNext() != null) {
                this.getNext().setPrev(this.getPrev());
            }
            if (this.getPrev() != null) {
                this.getPrev().setNext(this.getNext());
            }
            this.setNext(null);
            this.setPrev(null);
        }
        
        protected final void insertBefore(final Node nde) {
            if (this == nde) {
                return;
            }
            if (this.getPrev() != null) {
                this.unlink();
            }
            if (nde == null) {
                this.setNext(this);
                this.setPrev(this);
            }
            else {
                this.setNext(nde);
                this.setPrev(nde.getPrev());
                nde.setPrev(this);
                if (this.getPrev() != null) {
                    this.getPrev().setNext(this);
                }
            }
        }
    }
}
