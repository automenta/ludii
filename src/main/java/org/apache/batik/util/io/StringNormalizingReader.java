// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util.io;

import java.io.IOException;

public class StringNormalizingReader extends NormalizingReader
{
    protected String string;
    protected int length;
    protected int next;
    protected int line;
    protected int column;
    
    public StringNormalizingReader(final String s) {
        this.line = 1;
        this.string = s;
        this.length = s.length();
    }
    
    @Override
    public int read() throws IOException {
        final int result = (this.length == this.next) ? -1 : this.string.charAt(this.next++);
        if (result <= 13) {
            switch (result) {
                case 13: {
                    this.column = 0;
                    ++this.line;
                    final int c = (this.length == this.next) ? -1 : this.string.charAt(this.next);
                    if (c == 10) {
                        ++this.next;
                    }
                    return 10;
                }
                case 10: {
                    this.column = 0;
                    ++this.line;
                    break;
                }
            }
        }
        return result;
    }
    
    @Override
    public int getLine() {
        return this.line;
    }
    
    @Override
    public int getColumn() {
        return this.column;
    }
    
    @Override
    public void close() throws IOException {
        this.string = null;
    }
}
