// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.script;

import java.net.URL;

public interface InterpreterFactory
{
    String[] getMimeTypes();
    
    Interpreter createInterpreter(final URL p0, final boolean p1, final ImportInfo p2);
    
    Interpreter createInterpreter(final URL p0, final boolean p1);
}
