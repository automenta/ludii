// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.wmf.tosvg;

public class GdiObject
{
    int id;
    boolean used;
    Object obj;
    int type;
    
    GdiObject(final int _id, final boolean _used) {
        this.type = 0;
        this.id = _id;
        this.used = _used;
        this.type = 0;
    }
    
    public void clear() {
        this.used = false;
        this.type = 0;
    }
    
    public void Setup(final int _type, final Object _obj) {
        this.obj = _obj;
        this.type = _type;
        this.used = true;
    }
    
    public boolean isUsed() {
        return this.used;
    }
    
    public int getType() {
        return this.type;
    }
    
    public Object getObject() {
        return this.obj;
    }
    
    public int getID() {
        return this.id;
    }
}
