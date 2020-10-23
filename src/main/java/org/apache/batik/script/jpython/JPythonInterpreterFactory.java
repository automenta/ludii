// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.script.jpython;

import org.apache.batik.script.ImportInfo;
import org.apache.batik.script.Interpreter;
import java.net.URL;
import org.apache.batik.script.InterpreterFactory;

public class JPythonInterpreterFactory implements InterpreterFactory
{
    public static final String[] JPYTHON_MIMETYPES;
    
    @Override
    public String[] getMimeTypes() {
        return JPythonInterpreterFactory.JPYTHON_MIMETYPES;
    }
    
    @Override
    public Interpreter createInterpreter(final URL documentURL, final boolean svg12) {
        return new JPythonInterpreter();
    }
    
    @Override
    public Interpreter createInterpreter(final URL documentURL, final boolean svg12, final ImportInfo imports) {
        return new JPythonInterpreter();
    }
    
    static {
        JPYTHON_MIMETYPES = new String[] { "text/python" };
    }
}
