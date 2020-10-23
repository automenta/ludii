package root;/*
 * Decompiled with CFR 0.150.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class UnixPrintWriter
extends PrintWriter {
    public UnixPrintWriter(File file) throws FileNotFoundException {
        super(file);
    }

    public UnixPrintWriter(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }

    @Override
    public void println() {
        this.write(10);
    }
}

