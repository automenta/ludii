// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.w3c.dom.Location;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.NativeObject;
import org.w3c.dom.Node;
import java.security.AccessControlContext;
import java.security.AccessController;
import org.w3c.dom.Document;
import java.security.PrivilegedAction;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;

public class WindowWrapper extends ImporterTopLevel
{
    private static final Object[] EMPTY_ARGUMENTS;
    protected RhinoInterpreter interpreter;
    protected Window window;
    
    public WindowWrapper(final Context context) {
        super(context);
        final String[] names = { "setInterval", "setTimeout", "clearInterval", "clearTimeout", "parseXML", "printNode", "getURL", "postURL", "alert", "confirm", "prompt" };
        this.defineFunctionProperties(names, (Class)WindowWrapper.class, 2);
        this.defineProperty("location", (Class)WindowWrapper.class, 4);
    }
    
    public String getClassName() {
        return "Window";
    }
    
    public String toString() {
        return "[object Window]";
    }
    
    public static Object setInterval(final Context cx, final Scriptable thisObj, final Object[] args, final Function funObj) {
        final int len = args.length;
        final WindowWrapper ww = (WindowWrapper)thisObj;
        final Window window = ww.window;
        if (len < 2) {
            throw Context.reportRuntimeError("invalid argument count");
        }
        final long to = (long)Context.jsToJava(args[1], (Class)Long.TYPE);
        if (args[0] instanceof Function) {
            final RhinoInterpreter interp = (RhinoInterpreter)window.getInterpreter();
            final FunctionWrapper fw = new FunctionWrapper(interp, (Function)args[0], WindowWrapper.EMPTY_ARGUMENTS);
            return Context.toObject(window.setInterval(fw, to), thisObj);
        }
        final String script = (String)Context.jsToJava(args[0], (Class)String.class);
        return Context.toObject(window.setInterval(script, to), thisObj);
    }
    
    public static Object setTimeout(final Context cx, final Scriptable thisObj, final Object[] args, final Function funObj) {
        final int len = args.length;
        final WindowWrapper ww = (WindowWrapper)thisObj;
        final Window window = ww.window;
        if (len < 2) {
            throw Context.reportRuntimeError("invalid argument count");
        }
        final long to = (long)Context.jsToJava(args[1], (Class)Long.TYPE);
        if (args[0] instanceof Function) {
            final RhinoInterpreter interp = (RhinoInterpreter)window.getInterpreter();
            final FunctionWrapper fw = new FunctionWrapper(interp, (Function)args[0], WindowWrapper.EMPTY_ARGUMENTS);
            return Context.toObject(window.setTimeout(fw, to), thisObj);
        }
        final String script = (String)Context.jsToJava(args[0], (Class)String.class);
        return Context.toObject(window.setTimeout(script, to), thisObj);
    }
    
    public static void clearInterval(final Context cx, final Scriptable thisObj, final Object[] args, final Function funObj) {
        final int len = args.length;
        final WindowWrapper ww = (WindowWrapper)thisObj;
        final Window window = ww.window;
        if (len >= 1) {
            window.clearInterval(Context.jsToJava(args[0], (Class)Object.class));
        }
    }
    
    public static void clearTimeout(final Context cx, final Scriptable thisObj, final Object[] args, final Function funObj) {
        final int len = args.length;
        final WindowWrapper ww = (WindowWrapper)thisObj;
        final Window window = ww.window;
        if (len >= 1) {
            window.clearTimeout(Context.jsToJava(args[0], (Class)Object.class));
        }
    }
    
    public static Object parseXML(final Context cx, final Scriptable thisObj, final Object[] args, final Function funObj) {
        final int len = args.length;
        final WindowWrapper ww = (WindowWrapper)thisObj;
        final Window window = ww.window;
        if (len < 2) {
            throw Context.reportRuntimeError("invalid argument count");
        }
        final RhinoInterpreter interp = (RhinoInterpreter)window.getInterpreter();
        final AccessControlContext acc = interp.getAccessControlContext();
        final PrivilegedAction pa = new PrivilegedAction() {
            @Override
            public Object run() {
                return window.parseXML((String)Context.jsToJava(args[0], (Class)String.class), (Document)Context.jsToJava(args[1], (Class)Document.class));
            }
        };
        Object ret;
        if (acc != null) {
            ret = AccessController.doPrivileged((PrivilegedAction<Object>)pa, acc);
        }
        else {
            ret = AccessController.doPrivileged((PrivilegedAction<Object>)pa);
        }
        return Context.toObject(ret, thisObj);
    }
    
    public static Object printNode(final Context cx, final Scriptable thisObj, final Object[] args, final Function funObj) {
        if (args.length != 1) {
            throw Context.reportRuntimeError("invalid argument count");
        }
        final WindowWrapper ww = (WindowWrapper)thisObj;
        final Window window = ww.window;
        final AccessControlContext acc = ((RhinoInterpreter)window.getInterpreter()).getAccessControlContext();
        final Object ret = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                return window.printNode((Node)Context.jsToJava(args[0], (Class)Node.class));
            }
        }, acc);
        return Context.toString(ret);
    }
    
    public static void getURL(final Context cx, final Scriptable thisObj, final Object[] args, final Function funObj) {
        final int len = args.length;
        final WindowWrapper ww = (WindowWrapper)thisObj;
        final Window window = ww.window;
        if (len < 2) {
            throw Context.reportRuntimeError("invalid argument count");
        }
        final RhinoInterpreter interp = (RhinoInterpreter)window.getInterpreter();
        final String uri = (String)Context.jsToJava(args[0], (Class)String.class);
        Window.URLResponseHandler urlHandler = null;
        if (args[1] instanceof Function) {
            urlHandler = new GetURLFunctionWrapper(interp, (Function)args[1], ww);
        }
        else {
            urlHandler = new GetURLObjectWrapper(interp, (ScriptableObject)args[1], ww);
        }
        final Window.URLResponseHandler fw = urlHandler;
        final AccessControlContext acc = ((RhinoInterpreter)window.getInterpreter()).getAccessControlContext();
        if (len == 2) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                @Override
                public Object run() {
                    window.getURL(uri, fw);
                    return null;
                }
            }, acc);
        }
        else {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                @Override
                public Object run() {
                    window.getURL(uri, fw, (String)Context.jsToJava(args[2], (Class)String.class));
                    return null;
                }
            }, acc);
        }
    }
    
    public static void postURL(final Context cx, final Scriptable thisObj, final Object[] args, final Function funObj) {
        final int len = args.length;
        final WindowWrapper ww = (WindowWrapper)thisObj;
        final Window window = ww.window;
        if (len < 3) {
            throw Context.reportRuntimeError("invalid argument count");
        }
        final RhinoInterpreter interp = (RhinoInterpreter)window.getInterpreter();
        final String uri = (String)Context.jsToJava(args[0], (Class)String.class);
        final String content = (String)Context.jsToJava(args[1], (Class)String.class);
        Window.URLResponseHandler urlHandler = null;
        if (args[2] instanceof Function) {
            urlHandler = new GetURLFunctionWrapper(interp, (Function)args[2], ww);
        }
        else {
            urlHandler = new GetURLObjectWrapper(interp, (ScriptableObject)args[2], ww);
        }
        final Window.URLResponseHandler fw = urlHandler;
        final AccessControlContext acc = interp.getAccessControlContext();
        switch (len) {
            case 3: {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                    @Override
                    public Object run() {
                        window.postURL(uri, content, fw);
                        return null;
                    }
                }, acc);
                break;
            }
            case 4: {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                    @Override
                    public Object run() {
                        window.postURL(uri, content, fw, (String)Context.jsToJava(args[3], (Class)String.class));
                        return null;
                    }
                }, acc);
                break;
            }
            default: {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                    @Override
                    public Object run() {
                        window.postURL(uri, content, fw, (String)Context.jsToJava(args[3], (Class)String.class), (String)Context.jsToJava(args[4], (Class)String.class));
                        return null;
                    }
                }, acc);
                break;
            }
        }
    }
    
    public static void alert(final Context cx, final Scriptable thisObj, final Object[] args, final Function funObj) {
        final int len = args.length;
        final WindowWrapper ww = (WindowWrapper)thisObj;
        final Window window = ww.window;
        if (len >= 1) {
            final String message = (String)Context.jsToJava(args[0], (Class)String.class);
            window.alert(message);
        }
    }
    
    public static Object confirm(final Context cx, final Scriptable thisObj, final Object[] args, final Function funObj) {
        final int len = args.length;
        final WindowWrapper ww = (WindowWrapper)thisObj;
        final Window window = ww.window;
        if (len < 1) {
            return Context.toObject((Object)Boolean.FALSE, thisObj);
        }
        final String message = (String)Context.jsToJava(args[0], (Class)String.class);
        if (window.confirm(message)) {
            return Context.toObject((Object)Boolean.TRUE, thisObj);
        }
        return Context.toObject((Object)Boolean.FALSE, thisObj);
    }
    
    public static Object prompt(final Context cx, final Scriptable thisObj, final Object[] args, final Function funObj) {
        final WindowWrapper ww = (WindowWrapper)thisObj;
        final Window window = ww.window;
        Object result = null;
        switch (args.length) {
            case 0: {
                result = "";
                break;
            }
            case 1: {
                final String message = (String)Context.jsToJava(args[0], (Class)String.class);
                result = window.prompt(message);
                break;
            }
            default: {
                final String message = (String)Context.jsToJava(args[0], (Class)String.class);
                final String defVal = (String)Context.jsToJava(args[1], (Class)String.class);
                result = window.prompt(message, defVal);
                break;
            }
        }
        if (result == null) {
            return null;
        }
        return Context.toString(result);
    }
    
    public Location getLocation() {
        return this.window.getLocation();
    }
    
    public void setLocation(final Object val) {
        final String url = (String)Context.jsToJava(val, (Class)String.class);
        this.window.getLocation().assign(url);
    }
    
    static {
        EMPTY_ARGUMENTS = new Object[0];
    }
    
    protected static class FunctionWrapper implements Runnable
    {
        protected RhinoInterpreter interpreter;
        protected Function function;
        protected Object[] arguments;
        
        public FunctionWrapper(final RhinoInterpreter ri, final Function f, final Object[] args) {
            this.interpreter = ri;
            this.function = f;
            this.arguments = args;
        }
        
        @Override
        public void run() {
            this.interpreter.callHandler(this.function, this.arguments);
        }
    }
    
    protected static class GetURLFunctionWrapper implements Window.URLResponseHandler
    {
        protected RhinoInterpreter interpreter;
        protected Function function;
        protected WindowWrapper windowWrapper;
        
        public GetURLFunctionWrapper(final RhinoInterpreter ri, final Function fct, final WindowWrapper ww) {
            this.interpreter = ri;
            this.function = fct;
            this.windowWrapper = ww;
        }
        
        @Override
        public void getURLDone(final boolean success, final String mime, final String content) {
            this.interpreter.callHandler(this.function, new GetURLDoneArgBuilder(success, mime, content, this.windowWrapper));
        }
    }
    
    private static class GetURLObjectWrapper implements Window.URLResponseHandler
    {
        private RhinoInterpreter interpreter;
        private ScriptableObject object;
        private WindowWrapper windowWrapper;
        private static final String COMPLETE = "operationComplete";
        
        public GetURLObjectWrapper(final RhinoInterpreter ri, final ScriptableObject obj, final WindowWrapper ww) {
            this.interpreter = ri;
            this.object = obj;
            this.windowWrapper = ww;
        }
        
        @Override
        public void getURLDone(final boolean success, final String mime, final String content) {
            this.interpreter.callMethod(this.object, "operationComplete", new GetURLDoneArgBuilder(success, mime, content, this.windowWrapper));
        }
    }
    
    static class GetURLDoneArgBuilder implements RhinoInterpreter.ArgumentsBuilder
    {
        boolean success;
        String mime;
        String content;
        WindowWrapper windowWrapper;
        
        public GetURLDoneArgBuilder(final boolean success, final String mime, final String content, final WindowWrapper ww) {
            this.success = success;
            this.mime = mime;
            this.content = content;
            this.windowWrapper = ww;
        }
        
        @Override
        public Object[] buildArguments() {
            final ScriptableObject so = (ScriptableObject)new NativeObject();
            so.put("success", (Scriptable)so, (Object)(this.success ? Boolean.TRUE : Boolean.FALSE));
            if (this.mime != null) {
                so.put("contentType", (Scriptable)so, (Object)Context.toObject((Object)this.mime, (Scriptable)this.windowWrapper));
            }
            if (this.content != null) {
                so.put("content", (Scriptable)so, (Object)Context.toObject((Object)this.content, (Scriptable)this.windowWrapper));
            }
            return new Object[] { so };
        }
    }
}
