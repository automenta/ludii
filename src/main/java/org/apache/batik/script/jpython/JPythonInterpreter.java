// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.script.jpython;

import java.util.Locale;
import java.io.Writer;
import org.python.core.PyException;
import org.apache.batik.script.InterpreterException;
import java.io.IOException;
import java.io.Reader;
import org.python.util.PythonInterpreter;
import org.apache.batik.script.Interpreter;

public class JPythonInterpreter implements Interpreter
{
    private PythonInterpreter interpreter;
    
    public JPythonInterpreter() {
        this.interpreter = null;
        this.interpreter = new PythonInterpreter();
    }
    
    @Override
    public String[] getMimeTypes() {
        return JPythonInterpreterFactory.JPYTHON_MIMETYPES;
    }
    
    @Override
    public Object evaluate(final Reader scriptreader) throws IOException {
        return this.evaluate(scriptreader, "");
    }
    
    @Override
    public Object evaluate(final Reader scriptreader, final String description) throws IOException {
        final StringBuffer sbuffer = new StringBuffer();
        final char[] buffer = new char[1024];
        int val = 0;
        while ((val = scriptreader.read(buffer)) != -1) {
            sbuffer.append(buffer, 0, val);
        }
        final String str = sbuffer.toString();
        return this.evaluate(str);
    }
    
    @Override
    public Object evaluate(final String script) {
        try {
            this.interpreter.exec(script);
        }
        catch (PyException e) {
            throw new InterpreterException((Exception)e, e.getMessage(), -1, -1);
        }
        catch (RuntimeException re) {
            throw new InterpreterException(re, re.getMessage(), -1, -1);
        }
        return null;
    }
    
    @Override
    public void dispose() {
    }
    
    @Override
    public void bindObject(final String name, final Object object) {
        this.interpreter.set(name, object);
    }
    
    @Override
    public void setOut(final Writer out) {
        this.interpreter.setOut(out);
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
}
