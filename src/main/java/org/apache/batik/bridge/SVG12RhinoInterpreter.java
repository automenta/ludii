// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Scriptable;
import org.apache.batik.script.ImportInfo;
import java.net.URL;

public class SVG12RhinoInterpreter extends RhinoInterpreter
{
    public SVG12RhinoInterpreter(final URL documentURL) {
        super(documentURL);
    }
    
    public SVG12RhinoInterpreter(final URL documentURL, final ImportInfo imports) {
        super(documentURL, imports);
    }
    
    @Override
    protected void defineGlobalWrapperClass(final Scriptable global) {
        try {
            ScriptableObject.defineClass(global, (Class)GlobalWrapper.class);
        }
        catch (Exception ex) {}
    }
    
    @Override
    protected ScriptableObject createGlobalObject(final Context ctx) {
        return (ScriptableObject)new GlobalWrapper(ctx);
    }
}
