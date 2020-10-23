// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.script;

import java.io.Writer;
import java.io.IOException;
import java.io.Reader;
import org.apache.batik.i18n.Localizable;

public interface Interpreter extends Localizable
{
    String[] getMimeTypes();
    
    Object evaluate(final Reader p0, final String p1) throws InterpreterException, IOException;
    
    Object evaluate(final Reader p0) throws InterpreterException, IOException;
    
    Object evaluate(final String p0) throws InterpreterException;
    
    void bindObject(final String p0, final Object p1);
    
    void setOut(final Writer p0);
    
    void dispose();
}
