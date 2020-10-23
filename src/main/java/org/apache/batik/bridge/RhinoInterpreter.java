// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.Locale;
import java.io.Writer;
import org.w3c.dom.events.EventTarget;
import org.mozilla.javascript.Function;
import java.security.AccessController;
import org.mozilla.javascript.Script;
import java.io.StringReader;
import java.security.PrivilegedAction;
import org.mozilla.javascript.JavaScriptException;
import org.apache.batik.script.InterpreterException;
import org.mozilla.javascript.WrappedException;
import java.io.IOException;
import java.io.Reader;
import java.security.AccessControlContext;
import java.util.Iterator;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ClassCache;
import org.mozilla.javascript.ContextAction;
import org.apache.batik.script.ImportInfo;
import org.apache.batik.script.rhino.BatikSecurityController;
import org.apache.batik.script.rhino.RhinoClassShutter;
import java.net.URL;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.SecurityController;
import org.apache.batik.script.rhino.RhinoClassLoader;
import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.WrapFactory;
import java.util.LinkedList;
import org.mozilla.javascript.ScriptableObject;
import java.util.List;
import org.apache.batik.script.Interpreter;

public class RhinoInterpreter implements Interpreter
{
    private static final int MAX_CACHED_SCRIPTS = 32;
    public static final String SOURCE_NAME_SVG = "<SVG>";
    public static final String BIND_NAME_WINDOW = "window";
    protected static List contexts;
    protected Window window;
    protected ScriptableObject globalObject;
    protected LinkedList compiledScripts;
    protected WrapFactory wrapFactory;
    protected ClassShutter classShutter;
    protected RhinoClassLoader rhinoClassLoader;
    protected SecurityController securityController;
    protected ContextFactory contextFactory;
    protected Context defaultContext;
    
    public RhinoInterpreter(final URL documentURL) {
        this.globalObject = null;
        this.compiledScripts = new LinkedList();
        this.wrapFactory = new BatikWrapFactory(this);
        this.classShutter = (ClassShutter)new RhinoClassShutter();
        this.securityController = new BatikSecurityController();
        this.contextFactory = new Factory();
        this.init(documentURL, null);
    }
    
    public RhinoInterpreter(final URL documentURL, final ImportInfo imports) {
        this.globalObject = null;
        this.compiledScripts = new LinkedList();
        this.wrapFactory = new BatikWrapFactory(this);
        this.classShutter = (ClassShutter)new RhinoClassShutter();
        this.securityController = new BatikSecurityController();
        this.contextFactory = new Factory();
        this.init(documentURL, imports);
    }
    
    protected void init(final URL documentURL, final ImportInfo imports) {
        try {
            this.rhinoClassLoader = new RhinoClassLoader(documentURL, this.getClass().getClassLoader());
        }
        catch (SecurityException se) {
            this.rhinoClassLoader = null;
        }
        final ContextAction initAction = (ContextAction)new ContextAction() {
            public Object run(final Context cx) {
                final Scriptable scriptable = (Scriptable)cx.initStandardObjects((ScriptableObject)null, false);
                RhinoInterpreter.this.defineGlobalWrapperClass(scriptable);
                RhinoInterpreter.this.globalObject = RhinoInterpreter.this.createGlobalObject(cx);
                final ClassCache cache = ClassCache.get((Scriptable)RhinoInterpreter.this.globalObject);
                cache.setCachingEnabled(RhinoInterpreter.this.rhinoClassLoader != null);
                ImportInfo ii = imports;
                if (ii == null) {
                    ii = ImportInfo.getImports();
                }
                final StringBuffer sb = new StringBuffer();
                Iterator iter = ii.getPackages();
                while (iter.hasNext()) {
                    final String pkg = iter.next();
                    sb.append("importPackage(Packages.");
                    sb.append(pkg);
                    sb.append(");");
                }
                iter = ii.getClasses();
                while (iter.hasNext()) {
                    final String cls = iter.next();
                    sb.append("importClass(Packages.");
                    sb.append(cls);
                    sb.append(");");
                }
                cx.evaluateString((Scriptable)RhinoInterpreter.this.globalObject, sb.toString(), (String)null, 0, (Object)RhinoInterpreter.this.rhinoClassLoader);
                return null;
            }
        };
        this.contextFactory.call(initAction);
    }
    
    @Override
    public String[] getMimeTypes() {
        return RhinoInterpreterFactory.RHINO_MIMETYPES;
    }
    
    public Window getWindow() {
        return this.window;
    }
    
    public ContextFactory getContextFactory() {
        return this.contextFactory;
    }
    
    protected void defineGlobalWrapperClass(final Scriptable global) {
        try {
            ScriptableObject.defineClass(global, (Class)WindowWrapper.class);
        }
        catch (Exception ex) {}
    }
    
    protected ScriptableObject createGlobalObject(final Context ctx) {
        return (ScriptableObject)new WindowWrapper(ctx);
    }
    
    public AccessControlContext getAccessControlContext() {
        if (this.rhinoClassLoader == null) {
            return null;
        }
        return this.rhinoClassLoader.getAccessControlContext();
    }
    
    protected ScriptableObject getGlobalObject() {
        return this.globalObject;
    }
    
    @Override
    public Object evaluate(final Reader scriptreader) throws IOException {
        return this.evaluate(scriptreader, "<SVG>");
    }
    
    @Override
    public Object evaluate(final Reader scriptReader, final String description) throws IOException {
        final ContextAction evaluateAction = (ContextAction)new ContextAction() {
            public Object run(final Context cx) {
                try {
                    return cx.evaluateReader((Scriptable)RhinoInterpreter.this.globalObject, scriptReader, description, 1, (Object)RhinoInterpreter.this.rhinoClassLoader);
                }
                catch (IOException ioe) {
                    throw new WrappedException((Throwable)ioe);
                }
            }
        };
        try {
            return this.contextFactory.call(evaluateAction);
        }
        catch (JavaScriptException e) {
            final Object value = e.getValue();
            final Exception ex = (value instanceof Exception) ? ((Exception)value) : e;
            throw new InterpreterException(ex, ex.getMessage(), -1, -1);
        }
        catch (WrappedException we) {
            final Throwable w = we.getWrappedException();
            if (w instanceof Exception) {
                throw new InterpreterException((Exception)w, w.getMessage(), -1, -1);
            }
            throw new InterpreterException(w.getMessage(), -1, -1);
        }
        catch (InterruptedBridgeException ibe) {
            throw ibe;
        }
        catch (RuntimeException re) {
            throw new InterpreterException(re, re.getMessage(), -1, -1);
        }
    }
    
    @Override
    public Object evaluate(final String scriptStr) {
        final ContextAction evalAction = (ContextAction)new ContextAction() {
            public Object run(final Context cx) {
                Script script = null;
                Entry entry = null;
                final Iterator it = RhinoInterpreter.this.compiledScripts.iterator();
                while (it.hasNext()) {
                    if ((entry = it.next()).str.equals(scriptStr)) {
                        script = entry.script;
                        it.remove();
                        break;
                    }
                }
                if (script == null) {
                    final PrivilegedAction compile = new PrivilegedAction() {
                        @Override
                        public Object run() {
                            try {
                                return cx.compileReader((Reader)new StringReader(scriptStr), "<SVG>", 1, (Object)RhinoInterpreter.this.rhinoClassLoader);
                            }
                            catch (IOException ioEx) {
                                throw new RuntimeException(ioEx.getMessage());
                            }
                        }
                    };
                    script = AccessController.doPrivileged((PrivilegedAction<Script>)compile);
                    if (RhinoInterpreter.this.compiledScripts.size() + 1 > 32) {
                        RhinoInterpreter.this.compiledScripts.removeFirst();
                    }
                    RhinoInterpreter.this.compiledScripts.addLast(new Entry(scriptStr, script));
                }
                else {
                    RhinoInterpreter.this.compiledScripts.addLast(entry);
                }
                return script.exec(cx, (Scriptable)RhinoInterpreter.this.globalObject);
            }
        };
        try {
            return this.contextFactory.call(evalAction);
        }
        catch (InterpreterException ie) {
            throw ie;
        }
        catch (JavaScriptException e) {
            final Object value = e.getValue();
            final Exception ex = (value instanceof Exception) ? ((Exception)value) : e;
            throw new InterpreterException(ex, ex.getMessage(), -1, -1);
        }
        catch (WrappedException we) {
            final Throwable w = we.getWrappedException();
            if (w instanceof Exception) {
                throw new InterpreterException((Exception)w, w.getMessage(), -1, -1);
            }
            throw new InterpreterException(w.getMessage(), -1, -1);
        }
        catch (RuntimeException re) {
            throw new InterpreterException(re, re.getMessage(), -1, -1);
        }
    }
    
    @Override
    public void dispose() {
        if (this.rhinoClassLoader != null) {
            final ClassCache cache = ClassCache.get((Scriptable)this.globalObject);
            cache.setCachingEnabled(false);
        }
    }
    
    @Override
    public void bindObject(final String name, final Object object) {
        this.contextFactory.call((ContextAction)new ContextAction() {
            public Object run(final Context cx) {
                Object o = object;
                if (name.equals("window") && object instanceof Window) {
                    ((WindowWrapper)RhinoInterpreter.this.globalObject).window = (Window)object;
                    RhinoInterpreter.this.window = (Window)object;
                    o = RhinoInterpreter.this.globalObject;
                }
                final Scriptable jsObject = Context.toObject(o, (Scriptable)RhinoInterpreter.this.globalObject);
                RhinoInterpreter.this.globalObject.put(name, (Scriptable)RhinoInterpreter.this.globalObject, (Object)jsObject);
                return null;
            }
        });
    }
    
    void callHandler(final Function handler, final Object arg) {
        this.contextFactory.call((ContextAction)new ContextAction() {
            public Object run(final Context cx) {
                final Object a = Context.toObject(arg, (Scriptable)RhinoInterpreter.this.globalObject);
                final Object[] args = { a };
                handler.call(cx, (Scriptable)RhinoInterpreter.this.globalObject, (Scriptable)RhinoInterpreter.this.globalObject, args);
                return null;
            }
        });
    }
    
    void callMethod(final ScriptableObject obj, final String methodName, final ArgumentsBuilder ab) {
        this.contextFactory.call((ContextAction)new ContextAction() {
            public Object run(final Context cx) {
                ScriptableObject.callMethod((Scriptable)obj, methodName, ab.buildArguments());
                return null;
            }
        });
    }
    
    void callHandler(final Function handler, final Object[] args) {
        this.contextFactory.call((ContextAction)new ContextAction() {
            public Object run(final Context cx) {
                handler.call(cx, (Scriptable)RhinoInterpreter.this.globalObject, (Scriptable)RhinoInterpreter.this.globalObject, args);
                return null;
            }
        });
    }
    
    void callHandler(final Function handler, final ArgumentsBuilder ab) {
        this.contextFactory.call((ContextAction)new ContextAction() {
            public Object run(final Context cx) {
                final Object[] args = ab.buildArguments();
                handler.call(cx, handler.getParentScope(), (Scriptable)RhinoInterpreter.this.globalObject, args);
                return null;
            }
        });
    }
    
    Object call(final ContextAction action) {
        return this.contextFactory.call(action);
    }
    
    Scriptable buildEventTargetWrapper(final EventTarget obj) {
        return (Scriptable)new EventTargetWrapper((Scriptable)this.globalObject, obj, this);
    }
    
    @Override
    public void setOut(final Writer out) {
    }
    
    @Override
    public Locale getLocale() {
        return null;
    }
    
    @Override
    public void setLocale(final Locale locale) {
    }
    
    @Override
    public String formatMessage(final String key, final Object[] args) {
        return null;
    }
    
    static {
        RhinoInterpreter.contexts = new LinkedList();
    }
    
    protected static class Entry
    {
        public String str;
        public Script script;
        
        public Entry(final String str, final Script script) {
            this.str = str;
            this.script = script;
        }
    }
    
    protected class Factory extends ContextFactory
    {
        protected Context makeContext() {
            final Context cx = super.makeContext();
            cx.setWrapFactory(RhinoInterpreter.this.wrapFactory);
            cx.setSecurityController(RhinoInterpreter.this.securityController);
            cx.setClassShutter(RhinoInterpreter.this.classShutter);
            if (RhinoInterpreter.this.rhinoClassLoader == null) {
                cx.setOptimizationLevel(-1);
            }
            return cx;
        }
    }
    
    public interface ArgumentsBuilder
    {
        Object[] buildArguments();
    }
}
