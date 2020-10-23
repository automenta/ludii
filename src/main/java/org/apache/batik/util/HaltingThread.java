// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

public class HaltingThread extends Thread
{
    protected boolean beenHalted;
    
    public HaltingThread() {
        this.beenHalted = false;
    }
    
    public HaltingThread(final Runnable r) {
        super(r);
        this.beenHalted = false;
    }
    
    public HaltingThread(final String name) {
        super(name);
        this.beenHalted = false;
    }
    
    public HaltingThread(final Runnable r, final String name) {
        super(r, name);
        this.beenHalted = false;
    }
    
    public boolean isHalted() {
        synchronized (this) {
            return this.beenHalted;
        }
    }
    
    public void halt() {
        synchronized (this) {
            this.beenHalted = true;
        }
    }
    
    public void clearHalted() {
        synchronized (this) {
            this.beenHalted = false;
        }
    }
    
    public static void haltThread() {
        haltThread(Thread.currentThread());
    }
    
    public static void haltThread(final Thread t) {
        if (t instanceof HaltingThread) {
            ((HaltingThread)t).halt();
        }
    }
    
    public static boolean hasBeenHalted() {
        return hasBeenHalted(Thread.currentThread());
    }
    
    public static boolean hasBeenHalted(final Thread t) {
        return t instanceof HaltingThread && ((HaltingThread)t).isHalted();
    }
}
