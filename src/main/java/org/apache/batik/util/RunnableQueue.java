// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import java.util.NoSuchElementException;
import java.util.Iterator;

public class RunnableQueue implements Runnable
{
    public static final RunnableQueueState RUNNING;
    public static final RunnableQueueState SUSPENDING;
    public static final RunnableQueueState SUSPENDED;
    protected volatile RunnableQueueState state;
    protected final Object stateLock;
    protected boolean wasResumed;
    private final DoublyLinkedList list;
    protected int preemptCount;
    protected RunHandler runHandler;
    protected volatile HaltingThread runnableQueueThread;
    private IdleRunnable idleRunnable;
    private long idleRunnableWaitTime;
    private static volatile int threadCount;
    
    public RunnableQueue() {
        this.stateLock = new Object();
        this.list = new DoublyLinkedList();
    }
    
    public static RunnableQueue createRunnableQueue() {
        final RunnableQueue result = new RunnableQueue();
        synchronized (result) {
            final HaltingThread ht = new HaltingThread(result, "RunnableQueue-" + RunnableQueue.threadCount++);
            ht.setDaemon(true);
            ht.start();
            while (result.getThread() == null) {
                try {
                    result.wait();
                }
                catch (InterruptedException ie) {}
            }
        }
        return result;
    }
    
    @Override
    public void run() {
        synchronized (this) {
            this.runnableQueueThread = (HaltingThread)Thread.currentThread();
            this.notify();
        }
        try {
            while (!HaltingThread.hasBeenHalted()) {
                boolean callSuspended = false;
                boolean callResumed = false;
                synchronized (this.stateLock) {
                    if (this.state != RunnableQueue.RUNNING) {
                        this.state = RunnableQueue.SUSPENDED;
                        callSuspended = true;
                    }
                }
                if (callSuspended) {
                    this.executionSuspended();
                }
                synchronized (this.stateLock) {
                    while (this.state != RunnableQueue.RUNNING) {
                        this.state = RunnableQueue.SUSPENDED;
                        this.stateLock.notifyAll();
                        try {
                            this.stateLock.wait();
                        }
                        catch (InterruptedException ie) {}
                    }
                    if (this.wasResumed) {
                        this.wasResumed = false;
                        callResumed = true;
                    }
                }
                if (callResumed) {
                    this.executionResumed();
                }
                final Link l;
                Runnable rable = null;
                synchronized (this.list) {
                    if (this.state == RunnableQueue.SUSPENDING) {
                        continue;
                    }
                    l = (Link)this.list.pop();
                    if (this.preemptCount != 0) {
                        --this.preemptCount;
                    }
                    Label_0335: {
                        if (l == null) {
                            if (this.idleRunnable != null) {
                                final long waitTime = this.idleRunnable.getWaitTime();
                                this.idleRunnableWaitTime = waitTime;
                                if (waitTime < System.currentTimeMillis()) {
                                    rable = this.idleRunnable;
                                    break Label_0335;
                                }
                            }
                            try {
                                if (this.idleRunnable != null && this.idleRunnableWaitTime != Long.MAX_VALUE) {
                                    final long t = this.idleRunnableWaitTime - System.currentTimeMillis();
                                    if (t <= 0L) {
                                        continue;
                                    }
                                    this.list.wait(t);
                                }
                                else {
                                    this.list.wait();
                                }
                            }
                            catch (InterruptedException ex) {}
                            continue;
                        }
                        rable = l.runnable;
                    }
                }
                try {
                    this.runnableStart(rable);
                    rable.run();
                }
                catch (ThreadDeath td) {
                    throw td;
                }
                catch (Throwable t2) {
                    t2.printStackTrace();
                }
                if (l != null) {
                    l.unlock();
                }
                try {
                    this.runnableInvoked(rable);
                }
                catch (ThreadDeath td) {
                    throw td;
                }
                catch (Throwable t2) {
                    t2.printStackTrace();
                }
            }
        }
        finally {
            while (true) {
                final Link l;
                synchronized (this.list) {
                    l = (Link)this.list.pop();
                }
                if (l == null) {
                    break;
                }
                l.unlock();
            }
            synchronized (this) {
                this.runnableQueueThread = null;
            }
        }
    }
    
    public HaltingThread getThread() {
        return this.runnableQueueThread;
    }
    
    public void invokeLater(final Runnable r) {
        if (this.runnableQueueThread == null) {
            throw new IllegalStateException("RunnableQueue not started or has exited");
        }
        synchronized (this.list) {
            this.list.push(new Link(r));
            this.list.notify();
        }
    }
    
    public void invokeAndWait(final Runnable r) throws InterruptedException {
        if (this.runnableQueueThread == null) {
            throw new IllegalStateException("RunnableQueue not started or has exited");
        }
        if (this.runnableQueueThread == Thread.currentThread()) {
            throw new IllegalStateException("Cannot be called from the RunnableQueue thread");
        }
        final LockableLink l = new LockableLink(r);
        synchronized (this.list) {
            this.list.push(l);
            this.list.notify();
        }
        l.lock();
    }
    
    public void preemptLater(final Runnable r) {
        if (this.runnableQueueThread == null) {
            throw new IllegalStateException("RunnableQueue not started or has exited");
        }
        synchronized (this.list) {
            this.list.add(this.preemptCount, new Link(r));
            ++this.preemptCount;
            this.list.notify();
        }
    }
    
    public void preemptAndWait(final Runnable r) throws InterruptedException {
        if (this.runnableQueueThread == null) {
            throw new IllegalStateException("RunnableQueue not started or has exited");
        }
        if (this.runnableQueueThread == Thread.currentThread()) {
            throw new IllegalStateException("Cannot be called from the RunnableQueue thread");
        }
        final LockableLink l = new LockableLink(r);
        synchronized (this.list) {
            this.list.add(this.preemptCount, l);
            ++this.preemptCount;
            this.list.notify();
        }
        l.lock();
    }
    
    public RunnableQueueState getQueueState() {
        synchronized (this.stateLock) {
            return this.state;
        }
    }
    
    public void suspendExecution(final boolean waitTillSuspended) {
        if (this.runnableQueueThread == null) {
            throw new IllegalStateException("RunnableQueue not started or has exited");
        }
        synchronized (this.stateLock) {
            this.wasResumed = false;
            if (this.state == RunnableQueue.SUSPENDED) {
                this.stateLock.notifyAll();
                return;
            }
            if (this.state == RunnableQueue.RUNNING) {
                this.state = RunnableQueue.SUSPENDING;
                synchronized (this.list) {
                    this.list.notify();
                }
            }
            if (waitTillSuspended) {
                while (this.state == RunnableQueue.SUSPENDING) {
                    try {
                        this.stateLock.wait();
                    }
                    catch (InterruptedException ie) {}
                }
            }
        }
    }
    
    public void resumeExecution() {
        if (this.runnableQueueThread == null) {
            throw new IllegalStateException("RunnableQueue not started or has exited");
        }
        synchronized (this.stateLock) {
            this.wasResumed = true;
            if (this.state != RunnableQueue.RUNNING) {
                this.state = RunnableQueue.RUNNING;
                this.stateLock.notifyAll();
            }
        }
    }
    
    public Object getIteratorLock() {
        return this.list;
    }
    
    public Iterator iterator() {
        return new Iterator() {
            Link head = (Link)RunnableQueue.this.list.getHead();
            Link link;
            
            @Override
            public boolean hasNext() {
                return this.head != null && (this.link == null || this.link != this.head);
            }
            
            @Override
            public Object next() {
                if (this.head == null || this.head == this.link) {
                    throw new NoSuchElementException();
                }
                if (this.link == null) {
                    this.link = (Link)this.head.getNext();
                    return this.head.runnable;
                }
                final Object result = this.link.runnable;
                this.link = (Link)this.link.getNext();
                return result;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    public synchronized void setRunHandler(final RunHandler rh) {
        this.runHandler = rh;
    }
    
    public synchronized RunHandler getRunHandler() {
        return this.runHandler;
    }
    
    public void setIdleRunnable(final IdleRunnable r) {
        synchronized (this.list) {
            this.idleRunnable = r;
            this.idleRunnableWaitTime = 0L;
            this.list.notify();
        }
    }
    
    protected synchronized void executionSuspended() {
        if (this.runHandler != null) {
            this.runHandler.executionSuspended(this);
        }
    }
    
    protected synchronized void executionResumed() {
        if (this.runHandler != null) {
            this.runHandler.executionResumed(this);
        }
    }
    
    protected synchronized void runnableStart(final Runnable rable) {
        if (this.runHandler != null) {
            this.runHandler.runnableStart(this, rable);
        }
    }
    
    protected synchronized void runnableInvoked(final Runnable rable) {
        if (this.runHandler != null) {
            this.runHandler.runnableInvoked(this, rable);
        }
    }
    
    static {
        RUNNING = new RunnableQueueState("Running");
        SUSPENDING = new RunnableQueueState("Suspending");
        SUSPENDED = new RunnableQueueState("Suspended");
    }
    
    public static final class RunnableQueueState
    {
        private final String value;
        
        private RunnableQueueState(final String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
        
        @Override
        public String toString() {
            return "[RunnableQueueState: " + this.value + ']';
        }
    }
    
    public static class RunHandlerAdapter implements RunHandler
    {
        @Override
        public void runnableStart(final RunnableQueue rq, final Runnable r) {
        }
        
        @Override
        public void runnableInvoked(final RunnableQueue rq, final Runnable r) {
        }
        
        @Override
        public void executionSuspended(final RunnableQueue rq) {
        }
        
        @Override
        public void executionResumed(final RunnableQueue rq) {
        }
    }
    
    protected static class Link extends DoublyLinkedList.Node
    {
        private final Runnable runnable;
        
        public Link(final Runnable r) {
            this.runnable = r;
        }
        
        public void unlock() {
        }
    }
    
    protected static class LockableLink extends Link
    {
        private volatile boolean locked;
        
        public LockableLink(final Runnable r) {
            super(r);
        }
        
        public boolean isLocked() {
            return this.locked;
        }
        
        public synchronized void lock() throws InterruptedException {
            this.locked = true;
            this.notify();
            this.wait();
        }
        
        @Override
        public synchronized void unlock() {
            while (!this.locked) {
                try {
                    this.wait();
                }
                catch (InterruptedException ie) {}
            }
            this.locked = false;
            this.notify();
        }
    }
    
    public interface RunHandler
    {
        void runnableStart(final RunnableQueue p0, final Runnable p1);
        
        void runnableInvoked(final RunnableQueue p0, final Runnable p1);
        
        void executionSuspended(final RunnableQueue p0);
        
        void executionResumed(final RunnableQueue p0);
    }
    
    public interface IdleRunnable extends Runnable
    {
        long getWaitTime();
    }
}
