// 
// Decompiled by Procyon v0.5.36
// 

package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class UnixPrintWriter extends PrintWriter
{
    public UnixPrintWriter(final File file) throws FileNotFoundException {
        super(file);
    }
    
    public UnixPrintWriter(final File file, final String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }
    
    @Override
    public void println() {
        this.write(10);
    }
}
