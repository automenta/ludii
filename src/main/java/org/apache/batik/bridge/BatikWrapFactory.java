// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.events.EventTarget;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.WrapFactory;

class BatikWrapFactory extends WrapFactory
{
    private RhinoInterpreter interpreter;
    
    public BatikWrapFactory(final RhinoInterpreter interp) {
        this.interpreter = interp;
        this.setJavaPrimitiveWrap(false);
    }
    
    public Object wrap(final Context ctx, final Scriptable scope, final Object obj, final Class staticType) {
        if (obj instanceof EventTarget) {
            return this.interpreter.buildEventTargetWrapper((EventTarget)obj);
        }
        return super.wrap(ctx, scope, obj, staticType);
    }
}
