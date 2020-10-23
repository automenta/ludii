// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util.io;

import java.io.IOException;
import java.io.Reader;

public abstract class NormalizingReader extends Reader
{
    @Override
    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        int c = this.read();
        if (c == -1) {
            return -1;
        }
        int result = 0;
        do {
            cbuf[result + off] = (char)c;
            ++result;
            c = this.read();
        } while (c != -1 && result < len);
        return result;
    }
    
    public abstract int getLine();
    
    public abstract int getColumn();
}
