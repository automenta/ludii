// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public interface LookupSubtableFactory
{
    LookupSubtable read(final int p0, final RandomAccessFile p1, final int p2) throws IOException;
}
