// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import java.lang.ref.PhantomReference;
import java.lang.ref.WeakReference;
import java.lang.ref.SoftReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

public class CleanerThread extends Thread
{
    static volatile ReferenceQueue queue;
    static CleanerThread thread;
    
    public static ReferenceQueue getReferenceQueue() {
        if (CleanerThread.queue == null) {
            synchronized (CleanerThread.class) {
                CleanerThread.queue = new ReferenceQueue();
                CleanerThread.thread = new CleanerThread();
            }
        }
        return CleanerThread.queue;
    }
    
    protected CleanerThread() {
        super("Batik CleanerThread");
        this.setDaemon(true);
        this.start();
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    Reference ref;
                    try {
                        ref = CleanerThread.queue.remove();
                    }
                    catch (InterruptedException ie) {
                        continue;
                    }
                    if (ref instanceof ReferenceCleared) {
                        final ReferenceCleared rc = (ReferenceCleared)ref;
                        rc.cleared();
                    }
                }
            }
            catch (ThreadDeath td) {
                throw td;
            }
            catch (Throwable t) {
                t.printStackTrace();
                continue;
            }
            break;
        }
    }
    
    static {
        CleanerThread.queue = null;
        CleanerThread.thread = null;
    }
    
    public abstract static class SoftReferenceCleared extends SoftReference implements ReferenceCleared
    {
        public SoftReferenceCleared(final Object o) {
            super(o, CleanerThread.getReferenceQueue());
        }
    }
    
    public abstract static class WeakReferenceCleared extends WeakReference implements ReferenceCleared
    {
        public WeakReferenceCleared(final Object o) {
            super(o, CleanerThread.getReferenceQueue());
        }
    }
    
    public abstract static class PhantomReferenceCleared extends PhantomReference implements ReferenceCleared
    {
        public PhantomReferenceCleared(final Object o) {
            super(o, CleanerThread.getReferenceQueue());
        }
    }
    
    public interface ReferenceCleared
    {
        void cleared();
    }
}
