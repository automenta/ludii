// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.script.ImportInfo;
import org.apache.batik.script.Interpreter;
import java.net.URL;
import org.apache.batik.script.InterpreterFactory;

public class RhinoInterpreterFactory implements InterpreterFactory
{
    public static final String[] RHINO_MIMETYPES;
    
    @Override
    public String[] getMimeTypes() {
        return RhinoInterpreterFactory.RHINO_MIMETYPES;
    }
    
    @Override
    public Interpreter createInterpreter(final URL documentURL, final boolean svg12) {
        return this.createInterpreter(documentURL, svg12, null);
    }
    
    @Override
    public Interpreter createInterpreter(final URL documentURL, final boolean svg12, final ImportInfo imports) {
        if (svg12) {
            return new SVG12RhinoInterpreter(documentURL, imports);
        }
        return new RhinoInterpreter(documentURL, imports);
    }
    
    static {
        RHINO_MIMETYPES = new String[] { "application/ecmascript", "application/javascript", "text/ecmascript", "text/javascript" };
    }
}
