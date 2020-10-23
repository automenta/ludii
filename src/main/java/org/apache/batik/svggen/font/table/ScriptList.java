// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ScriptList
{
    private int scriptCount;
    private ScriptRecord[] scriptRecords;
    private Script[] scripts;
    
    protected ScriptList(final RandomAccessFile raf, final int offset) throws IOException {
        this.scriptCount = 0;
        raf.seek(offset);
        this.scriptCount = raf.readUnsignedShort();
        this.scriptRecords = new ScriptRecord[this.scriptCount];
        this.scripts = new Script[this.scriptCount];
        for (int i = 0; i < this.scriptCount; ++i) {
            this.scriptRecords[i] = new ScriptRecord(raf);
        }
        for (int i = 0; i < this.scriptCount; ++i) {
            this.scripts[i] = new Script(raf, offset + this.scriptRecords[i].getOffset());
        }
    }
    
    public int getScriptCount() {
        return this.scriptCount;
    }
    
    public ScriptRecord getScriptRecord(final int i) {
        return this.scriptRecords[i];
    }
    
    public Script findScript(final String tag) {
        if (tag.length() != 4) {
            return null;
        }
        final int tagVal = tag.charAt(0) << 24 | tag.charAt(1) << 16 | tag.charAt(2) << 8 | tag.charAt(3);
        for (int i = 0; i < this.scriptCount; ++i) {
            if (this.scriptRecords[i].getTag() == tagVal) {
                return this.scripts[i];
            }
        }
        return null;
    }
}
