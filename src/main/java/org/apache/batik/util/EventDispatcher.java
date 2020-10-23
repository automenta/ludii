// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import java.lang.reflect.InvocationTargetException;
import java.awt.EventQueue;
import java.util.List;

public class EventDispatcher
{
    public static void fireEvent(final Dispatcher dispatcher, final List listeners, final Object evt, final boolean useEventQueue) {
        if (useEventQueue && !EventQueue.isDispatchThread()) {
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    EventDispatcher.fireEvent(dispatcher, listeners, evt, useEventQueue);
                }
            };
            try {
                EventQueue.invokeAndWait(r);
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            catch (InterruptedException e2) {}
            catch (ThreadDeath td) {
                throw td;
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
            return;
        }
        Object[] ll = null;
        Throwable err = null;
        int retryCount = 10;
        while (--retryCount != 0) {
            try {
                synchronized (listeners) {
                    if (listeners.size() == 0) {
                        return;
                    }
                    ll = listeners.toArray();
                    break;
                }
            }
            catch (Throwable t2) {
                err = t2;
                continue;
            }
            break;
        }
        if (ll == null) {
            if (err != null) {
                err.printStackTrace();
            }
            return;
        }
        dispatchEvent(dispatcher, ll, evt);
    }
    
    protected static void dispatchEvent(final Dispatcher dispatcher, final Object[] ll, final Object evt) {
        ThreadDeath td = null;
        try {
            for (int i = 0; i < ll.length; ++i) {
                try {
                    final Object l;
                    synchronized (ll) {
                        l = ll[i];
                        if (l == null) {
                            continue;
                        }
                        ll[i] = null;
                    }
                    dispatcher.dispatch(l, evt);
                }
                catch (ThreadDeath t) {
                    td = t;
                }
                catch (Throwable t2) {
                    t2.printStackTrace();
                }
            }
        }
        catch (ThreadDeath t3) {
            td = t3;
        }
        catch (Throwable t4) {
            if (ll[ll.length - 1] != null) {
                dispatchEvent(dispatcher, ll, evt);
            }
            t4.printStackTrace();
        }
        if (td != null) {
            throw td;
        }
    }
    
    public interface Dispatcher
    {
        void dispatch(final Object p0, final Object p1);
    }
}
