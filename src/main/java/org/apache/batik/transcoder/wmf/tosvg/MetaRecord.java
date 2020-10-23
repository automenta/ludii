// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.wmf.tosvg;

import java.util.ArrayList;
import java.util.List;

public class MetaRecord
{
    public int functionId;
    public int numPoints;
    private final List ptVector;
    
    public MetaRecord() {
        this.ptVector = new ArrayList();
    }
    
    public void EnsureCapacity(final int cc) {
    }
    
    public void AddElement(final Object obj) {
        this.ptVector.add(obj);
    }
    
    public final void addElement(final int iValue) {
        this.ptVector.add(iValue);
    }
    
    public Integer ElementAt(final int offset) {
        return this.ptVector.get(offset);
    }
    
    public final int elementAt(final int offset) {
        return this.ptVector.get(offset);
    }
    
    public static class ByteRecord extends MetaRecord
    {
        public final byte[] bstr;
        
        public ByteRecord(final byte[] bstr) {
            this.bstr = bstr;
        }
    }
    
    public static class StringRecord extends MetaRecord
    {
        public final String text;
        
        public StringRecord(final String newText) {
            this.text = newText;
        }
    }
}
