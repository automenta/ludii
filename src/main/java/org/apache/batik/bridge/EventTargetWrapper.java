// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.dom.AbstractNode;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Undefined;
import java.lang.ref.SoftReference;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.apache.batik.script.ScriptEventWrapper;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import java.util.Map;
import org.mozilla.javascript.Function;
import org.w3c.dom.events.EventTarget;
import org.mozilla.javascript.Scriptable;
import java.util.WeakHashMap;
import org.mozilla.javascript.NativeJavaObject;

class EventTargetWrapper extends NativeJavaObject
{
    protected static WeakHashMap mapOfListenerMap;
    public static final String ADD_NAME = "addEventListener";
    public static final String ADDNS_NAME = "addEventListenerNS";
    public static final String REMOVE_NAME = "removeEventListener";
    public static final String REMOVENS_NAME = "removeEventListenerNS";
    protected RhinoInterpreter interpreter;
    
    EventTargetWrapper(final Scriptable scope, final EventTarget object, final RhinoInterpreter interpreter) {
        super(scope, (Object)object, (Class)null);
        this.interpreter = interpreter;
    }
    
    public Object get(final String name, final Scriptable start) {
        Object method = super.get(name, start);
        if (name.equals("addEventListener")) {
            method = new FunctionAddProxy(this.interpreter, (Function)method, this.initMap());
        }
        else if (name.equals("removeEventListener")) {
            method = new FunctionRemoveProxy((Function)method, this.initMap());
        }
        else if (name.equals("addEventListenerNS")) {
            method = new FunctionAddNSProxy(this.interpreter, (Function)method, this.initMap());
        }
        else if (name.equals("removeEventListenerNS")) {
            method = new FunctionRemoveNSProxy((Function)method, this.initMap());
        }
        return method;
    }
    
    public Map initMap() {
        Map map = null;
        if (EventTargetWrapper.mapOfListenerMap == null) {
            EventTargetWrapper.mapOfListenerMap = new WeakHashMap(10);
        }
        if ((map = EventTargetWrapper.mapOfListenerMap.get(this.unwrap())) == null) {
            EventTargetWrapper.mapOfListenerMap.put(this.unwrap(), map = new WeakHashMap(2));
        }
        return map;
    }
    
    static class FunctionEventListener implements EventListener
    {
        protected Function function;
        protected RhinoInterpreter interpreter;
        
        FunctionEventListener(final Function f, final RhinoInterpreter i) {
            this.function = f;
            this.interpreter = i;
        }
        
        @Override
        public void handleEvent(final Event evt) {
            Object event;
            if (evt instanceof ScriptEventWrapper) {
                event = ((ScriptEventWrapper)evt).getEventObject();
            }
            else {
                event = evt;
            }
            this.interpreter.callHandler(this.function, event);
        }
    }
    
    static class HandleEventListener implements EventListener
    {
        public static final String HANDLE_EVENT = "handleEvent";
        public Scriptable scriptable;
        public Object[] array;
        public RhinoInterpreter interpreter;
        
        HandleEventListener(final Scriptable s, final RhinoInterpreter interpreter) {
            this.array = new Object[1];
            this.scriptable = s;
            this.interpreter = interpreter;
        }
        
        @Override
        public void handleEvent(final Event evt) {
            if (evt instanceof ScriptEventWrapper) {
                this.array[0] = ((ScriptEventWrapper)evt).getEventObject();
            }
            else {
                this.array[0] = evt;
            }
            final ContextAction handleEventAction = (ContextAction)new ContextAction() {
                public Object run(final Context cx) {
                    ScriptableObject.callMethod(HandleEventListener.this.scriptable, "handleEvent", HandleEventListener.this.array);
                    return null;
                }
            };
            this.interpreter.call(handleEventAction);
        }
    }
    
    abstract static class FunctionProxy implements Function
    {
        protected Function delegate;
        
        public FunctionProxy(final Function delegate) {
            this.delegate = delegate;
        }
        
        public Scriptable construct(final Context cx, final Scriptable scope, final Object[] args) {
            return this.delegate.construct(cx, scope, args);
        }
        
        public String getClassName() {
            return this.delegate.getClassName();
        }
        
        public Object get(final String name, final Scriptable start) {
            return this.delegate.get(name, start);
        }
        
        public Object get(final int index, final Scriptable start) {
            return this.delegate.get(index, start);
        }
        
        public boolean has(final String name, final Scriptable start) {
            return this.delegate.has(name, start);
        }
        
        public boolean has(final int index, final Scriptable start) {
            return this.delegate.has(index, start);
        }
        
        public void put(final String name, final Scriptable start, final Object value) {
            this.delegate.put(name, start, value);
        }
        
        public void put(final int index, final Scriptable start, final Object value) {
            this.delegate.put(index, start, value);
        }
        
        public void delete(final String name) {
            this.delegate.delete(name);
        }
        
        public void delete(final int index) {
            this.delegate.delete(index);
        }
        
        public Scriptable getPrototype() {
            return this.delegate.getPrototype();
        }
        
        public void setPrototype(final Scriptable prototype) {
            this.delegate.setPrototype(prototype);
        }
        
        public Scriptable getParentScope() {
            return this.delegate.getParentScope();
        }
        
        public void setParentScope(final Scriptable parent) {
            this.delegate.setParentScope(parent);
        }
        
        public Object[] getIds() {
            return this.delegate.getIds();
        }
        
        public Object getDefaultValue(final Class hint) {
            return this.delegate.getDefaultValue(hint);
        }
        
        public boolean hasInstance(final Scriptable instance) {
            return this.delegate.hasInstance(instance);
        }
    }
    
    static class FunctionAddProxy extends FunctionProxy
    {
        protected Map listenerMap;
        protected RhinoInterpreter interpreter;
        
        FunctionAddProxy(final RhinoInterpreter interpreter, final Function delegate, final Map listenerMap) {
            super(delegate);
            this.listenerMap = listenerMap;
            this.interpreter = interpreter;
        }
        
        public Object call(final Context ctx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
            final NativeJavaObject njo = (NativeJavaObject)thisObj;
            if (args[1] instanceof Function) {
                EventListener evtListener = null;
                final SoftReference sr = this.listenerMap.get(args[1]);
                if (sr != null) {
                    evtListener = sr.get();
                }
                if (evtListener == null) {
                    evtListener = new FunctionEventListener((Function)args[1], this.interpreter);
                    this.listenerMap.put(args[1], new SoftReference<EventListener>(evtListener));
                }
                final Class[] paramTypes = { String.class, Function.class, Boolean.TYPE };
                for (int i = 0; i < args.length; ++i) {
                    args[i] = Context.jsToJava(args[i], paramTypes[i]);
                }
                ((EventTarget)njo.unwrap()).addEventListener((String)args[0], evtListener, (boolean)args[2]);
                return Undefined.instance;
            }
            if (args[1] instanceof NativeObject) {
                EventListener evtListener = null;
                final SoftReference sr = this.listenerMap.get(args[1]);
                if (sr != null) {
                    evtListener = sr.get();
                }
                if (evtListener == null) {
                    evtListener = new HandleEventListener((Scriptable)args[1], this.interpreter);
                    this.listenerMap.put(args[1], new SoftReference<EventListener>(evtListener));
                }
                final Class[] paramTypes = { String.class, Scriptable.class, Boolean.TYPE };
                for (int i = 0; i < args.length; ++i) {
                    args[i] = Context.jsToJava(args[i], paramTypes[i]);
                }
                ((EventTarget)njo.unwrap()).addEventListener((String)args[0], evtListener, (boolean)args[2]);
                return Undefined.instance;
            }
            return this.delegate.call(ctx, scope, thisObj, args);
        }
    }
    
    static class FunctionRemoveProxy extends FunctionProxy
    {
        public Map listenerMap;
        
        FunctionRemoveProxy(final Function delegate, final Map listenerMap) {
            super(delegate);
            this.listenerMap = listenerMap;
        }
        
        public Object call(final Context ctx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
            final NativeJavaObject njo = (NativeJavaObject)thisObj;
            if (args[1] instanceof Function) {
                final SoftReference sr = this.listenerMap.get(args[1]);
                if (sr == null) {
                    return Undefined.instance;
                }
                final EventListener el = sr.get();
                if (el == null) {
                    return Undefined.instance;
                }
                final Class[] paramTypes = { String.class, Function.class, Boolean.TYPE };
                for (int i = 0; i < args.length; ++i) {
                    args[i] = Context.jsToJava(args[i], paramTypes[i]);
                }
                ((EventTarget)njo.unwrap()).removeEventListener((String)args[0], el, (boolean)args[2]);
                return Undefined.instance;
            }
            else {
                if (!(args[1] instanceof NativeObject)) {
                    return this.delegate.call(ctx, scope, thisObj, args);
                }
                final SoftReference sr = this.listenerMap.get(args[1]);
                if (sr == null) {
                    return Undefined.instance;
                }
                final EventListener el = sr.get();
                if (el == null) {
                    return Undefined.instance;
                }
                final Class[] paramTypes = { String.class, Scriptable.class, Boolean.TYPE };
                for (int i = 0; i < args.length; ++i) {
                    args[i] = Context.jsToJava(args[i], paramTypes[i]);
                }
                ((EventTarget)njo.unwrap()).removeEventListener((String)args[0], el, (boolean)args[2]);
                return Undefined.instance;
            }
        }
    }
    
    static class FunctionAddNSProxy extends FunctionProxy
    {
        protected Map listenerMap;
        protected RhinoInterpreter interpreter;
        
        FunctionAddNSProxy(final RhinoInterpreter interpreter, final Function delegate, final Map listenerMap) {
            super(delegate);
            this.listenerMap = listenerMap;
            this.interpreter = interpreter;
        }
        
        public Object call(final Context ctx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
            final NativeJavaObject njo = (NativeJavaObject)thisObj;
            if (args[2] instanceof Function) {
                final EventListener evtListener = new FunctionEventListener((Function)args[2], this.interpreter);
                this.listenerMap.put(args[2], new SoftReference<EventListener>(evtListener));
                final Class[] paramTypes = { String.class, String.class, Function.class, Boolean.TYPE, Object.class };
                for (int i = 0; i < args.length; ++i) {
                    args[i] = Context.jsToJava(args[i], paramTypes[i]);
                }
                final AbstractNode target = (AbstractNode)njo.unwrap();
                target.addEventListenerNS((String)args[0], (String)args[1], evtListener, (boolean)args[3], args[4]);
                return Undefined.instance;
            }
            if (args[2] instanceof NativeObject) {
                final EventListener evtListener = new HandleEventListener((Scriptable)args[2], this.interpreter);
                this.listenerMap.put(args[2], new SoftReference<EventListener>(evtListener));
                final Class[] paramTypes = { String.class, String.class, Scriptable.class, Boolean.TYPE, Object.class };
                for (int i = 0; i < args.length; ++i) {
                    args[i] = Context.jsToJava(args[i], paramTypes[i]);
                }
                final AbstractNode target = (AbstractNode)njo.unwrap();
                target.addEventListenerNS((String)args[0], (String)args[1], evtListener, (boolean)args[3], args[4]);
                return Undefined.instance;
            }
            return this.delegate.call(ctx, scope, thisObj, args);
        }
    }
    
    static class FunctionRemoveNSProxy extends FunctionProxy
    {
        protected Map listenerMap;
        
        FunctionRemoveNSProxy(final Function delegate, final Map listenerMap) {
            super(delegate);
            this.listenerMap = listenerMap;
        }
        
        public Object call(final Context ctx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
            final NativeJavaObject njo = (NativeJavaObject)thisObj;
            if (args[2] instanceof Function) {
                final SoftReference sr = this.listenerMap.get(args[2]);
                if (sr == null) {
                    return Undefined.instance;
                }
                final EventListener el = sr.get();
                if (el == null) {
                    return Undefined.instance;
                }
                final Class[] paramTypes = { String.class, String.class, Function.class, Boolean.TYPE };
                for (int i = 0; i < args.length; ++i) {
                    args[i] = Context.jsToJava(args[i], paramTypes[i]);
                }
                final AbstractNode target = (AbstractNode)njo.unwrap();
                target.removeEventListenerNS((String)args[0], (String)args[1], el, (boolean)args[3]);
                return Undefined.instance;
            }
            else {
                if (!(args[2] instanceof NativeObject)) {
                    return this.delegate.call(ctx, scope, thisObj, args);
                }
                final SoftReference sr = this.listenerMap.get(args[2]);
                if (sr == null) {
                    return Undefined.instance;
                }
                final EventListener el = sr.get();
                if (el == null) {
                    return Undefined.instance;
                }
                final Class[] paramTypes = { String.class, String.class, Scriptable.class, Boolean.TYPE };
                for (int i = 0; i < args.length; ++i) {
                    args[i] = Context.jsToJava(args[i], paramTypes[i]);
                }
                final AbstractNode target = (AbstractNode)njo.unwrap();
                target.removeEventListenerNS((String)args[0], (String)args[1], el, (boolean)args[3]);
                return Undefined.instance;
            }
        }
    }
}
