// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Script
{
    private int defaultLangSysOffset;
    private int langSysCount;
    private LangSysRecord[] langSysRecords;
    private LangSys defaultLangSys;
    private LangSys[] langSys;
    
    protected Script(final RandomAccessFile raf, final int offset) throws IOException {
        raf.seek(offset);
        this.defaultLangSysOffset = raf.readUnsignedShort();
        this.langSysCount = raf.readUnsignedShort();
        if (this.langSysCount > 0) {
            this.langSysRecords = new LangSysRecord[this.langSysCount];
            for (int i = 0; i < this.langSysCount; ++i) {
                this.langSysRecords[i] = new LangSysRecord(raf);
            }
        }
        if (this.langSysCount > 0) {
            this.langSys = new LangSys[this.langSysCount];
            for (int i = 0; i < this.langSysCount; ++i) {
                raf.seek(offset + this.langSysRecords[i].getOffset());
                this.langSys[i] = new LangSys(raf);
            }
        }
        if (this.defaultLangSysOffset > 0) {
            raf.seek(offset + this.defaultLangSysOffset);
            this.defaultLangSys = new LangSys(raf);
        }
    }
    
    public LangSys getDefaultLangSys() {
        return this.defaultLangSys;
    }
}
