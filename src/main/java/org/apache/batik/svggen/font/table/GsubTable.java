// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class GsubTable implements Table, LookupSubtableFactory
{
    private ScriptList scriptList;
    private FeatureList featureList;
    private LookupList lookupList;
    
    protected GsubTable(final DirectoryEntry de, final RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        raf.readInt();
        final int scriptListOffset = raf.readUnsignedShort();
        final int featureListOffset = raf.readUnsignedShort();
        final int lookupListOffset = raf.readUnsignedShort();
        this.scriptList = new ScriptList(raf, de.getOffset() + scriptListOffset);
        this.featureList = new FeatureList(raf, de.getOffset() + featureListOffset);
        this.lookupList = new LookupList(raf, de.getOffset() + lookupListOffset, this);
    }
    
    @Override
    public LookupSubtable read(final int type, final RandomAccessFile raf, final int offset) throws IOException {
        LookupSubtable s = null;
        switch (type) {
            case 1: {
                s = SingleSubst.read(raf, offset);
            }
            case 2: {}
            case 4: {
                s = LigatureSubst.read(raf, offset);
            }
        }
        return s;
    }
    
    @Override
    public int getType() {
        return 1196643650;
    }
    
    public ScriptList getScriptList() {
        return this.scriptList;
    }
    
    public FeatureList getFeatureList() {
        return this.featureList;
    }
    
    public LookupList getLookupList() {
        return this.lookupList;
    }
    
    @Override
    public String toString() {
        return "GSUB";
    }
}
