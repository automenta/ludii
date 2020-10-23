// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.events.EventTarget;
import org.mozilla.javascript.NativeJavaObject;
import org.apache.batik.dom.svg12.SVGGlobal;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Context;

public class GlobalWrapper extends WindowWrapper
{
    public GlobalWrapper(final Context context) {
        super(context);
        final String[] names = { "startMouseCapture", "stopMouseCapture" };
        this.defineFunctionProperties(names, (Class)GlobalWrapper.class, 2);
    }
    
    @Override
    public String getClassName() {
        return "SVGGlobal";
    }
    
    @Override
    public String toString() {
        return "[object SVGGlobal]";
    }
    
    public static void startMouseCapture(final Context cx, final Scriptable thisObj, final Object[] args, final Function funObj) {
        final int len = args.length;
        final GlobalWrapper gw = (GlobalWrapper)thisObj;
        final SVGGlobal global = (SVGGlobal)gw.window;
        if (len >= 3) {
            EventTarget et = null;
            if (args[0] instanceof NativeJavaObject) {
                final Object o = ((NativeJavaObject)args[0]).unwrap();
                if (o instanceof EventTarget) {
                    et = (EventTarget)o;
                }
            }
            if (et == null) {
                throw Context.reportRuntimeError("First argument to startMouseCapture must be an EventTarget");
            }
            final boolean sendAll = Context.toBoolean(args[1]);
            final boolean autoRelease = Context.toBoolean(args[2]);
            global.startMouseCapture(et, sendAll, autoRelease);
        }
    }
    
    public static void stopMouseCapture(final Context cx, final Scriptable thisObj, final Object[] args, final Function funObj) {
        final GlobalWrapper gw = (GlobalWrapper)thisObj;
        final SVGGlobal global = (SVGGlobal)gw.window;
        global.stopMouseCapture();
    }
}
