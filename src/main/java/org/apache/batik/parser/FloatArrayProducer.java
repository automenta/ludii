// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.util.Iterator;
import java.util.LinkedList;

public class FloatArrayProducer extends DefaultNumberListHandler implements PointsHandler
{
    protected LinkedList as;
    protected float[] a;
    protected int index;
    protected int count;
    
    public float[] getFloatArray() {
        return this.a;
    }
    
    @Override
    public void startNumberList() throws ParseException {
        this.as = new LinkedList();
        this.a = new float[11];
        this.count = 0;
        this.index = 0;
    }
    
    @Override
    public void numberValue(final float v) throws ParseException {
        if (this.index == this.a.length) {
            this.as.add(this.a);
            this.a = new float[this.a.length * 2 + 1];
            this.index = 0;
        }
        this.a[this.index++] = v;
        ++this.count;
    }
    
    @Override
    public void endNumberList() throws ParseException {
        final float[] all = new float[this.count];
        int pos = 0;
        for (final Object a1 : this.as) {
            final float[] b = (float[])a1;
            System.arraycopy(b, 0, all, pos, b.length);
            pos += b.length;
        }
        System.arraycopy(this.a, 0, all, pos, this.index);
        this.as.clear();
        this.a = all;
    }
    
    @Override
    public void startPoints() throws ParseException {
        this.startNumberList();
    }
    
    @Override
    public void point(final float x, final float y) throws ParseException {
        this.numberValue(x);
        this.numberValue(y);
    }
    
    @Override
    public void endPoints() throws ParseException {
        this.endNumberList();
    }
}
