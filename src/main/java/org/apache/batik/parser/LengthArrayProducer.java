// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.util.Iterator;
import java.util.LinkedList;

public class LengthArrayProducer extends DefaultLengthListHandler
{
    protected LinkedList vs;
    protected float[] v;
    protected LinkedList us;
    protected short[] u;
    protected int index;
    protected int count;
    protected short currentUnit;
    
    public short[] getLengthTypeArray() {
        return this.u;
    }
    
    public float[] getLengthValueArray() {
        return this.v;
    }
    
    @Override
    public void startLengthList() throws ParseException {
        this.us = new LinkedList();
        this.u = new short[11];
        this.vs = new LinkedList();
        this.v = new float[11];
        this.count = 0;
        this.index = 0;
    }
    
    public void numberValue(final float v) throws ParseException {
    }
    
    @Override
    public void lengthValue(final float val) throws ParseException {
        if (this.index == this.v.length) {
            this.vs.add(this.v);
            this.v = new float[this.v.length * 2 + 1];
            this.us.add(this.u);
            this.u = new short[this.u.length * 2 + 1];
            this.index = 0;
        }
        this.v[this.index] = val;
    }
    
    @Override
    public void startLength() throws ParseException {
        this.currentUnit = 1;
    }
    
    @Override
    public void endLength() throws ParseException {
        this.u[this.index++] = this.currentUnit;
        ++this.count;
    }
    
    @Override
    public void em() throws ParseException {
        this.currentUnit = 3;
    }
    
    @Override
    public void ex() throws ParseException {
        this.currentUnit = 4;
    }
    
    @Override
    public void in() throws ParseException {
        this.currentUnit = 8;
    }
    
    @Override
    public void cm() throws ParseException {
        this.currentUnit = 6;
    }
    
    @Override
    public void mm() throws ParseException {
        this.currentUnit = 7;
    }
    
    @Override
    public void pc() throws ParseException {
        this.currentUnit = 10;
    }
    
    @Override
    public void pt() throws ParseException {
        this.currentUnit = 9;
    }
    
    @Override
    public void px() throws ParseException {
        this.currentUnit = 5;
    }
    
    @Override
    public void percentage() throws ParseException {
        this.currentUnit = 2;
    }
    
    @Override
    public void endLengthList() throws ParseException {
        final float[] allValues = new float[this.count];
        int pos = 0;
        for (final float[] a : this.vs) {
            System.arraycopy(a, 0, allValues, pos, a.length);
            pos += a.length;
        }
        System.arraycopy(this.v, 0, allValues, pos, this.index);
        this.vs.clear();
        this.v = allValues;
        final short[] allUnits = new short[this.count];
        pos = 0;
        for (final short[] a2 : this.us) {
            System.arraycopy(a2, 0, allUnits, pos, a2.length);
            pos += a2.length;
        }
        System.arraycopy(this.u, 0, allUnits, pos, this.index);
        this.us.clear();
        this.u = allUnits;
    }
}
