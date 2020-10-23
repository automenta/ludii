// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.util.Iterator;
import java.util.LinkedList;

public class PathArrayProducer implements PathHandler
{
    protected LinkedList ps;
    protected float[] p;
    protected LinkedList cs;
    protected short[] c;
    protected int cindex;
    protected int pindex;
    protected int ccount;
    protected int pcount;
    
    public short[] getPathCommands() {
        return this.c;
    }
    
    public float[] getPathParameters() {
        return this.p;
    }
    
    @Override
    public void startPath() throws ParseException {
        this.cs = new LinkedList();
        this.c = new short[11];
        this.ps = new LinkedList();
        this.p = new float[11];
        this.ccount = 0;
        this.pcount = 0;
        this.cindex = 0;
        this.pindex = 0;
    }
    
    @Override
    public void movetoRel(final float x, final float y) throws ParseException {
        this.command((short)3);
        this.param(x);
        this.param(y);
    }
    
    @Override
    public void movetoAbs(final float x, final float y) throws ParseException {
        this.command((short)2);
        this.param(x);
        this.param(y);
    }
    
    @Override
    public void closePath() throws ParseException {
        this.command((short)1);
    }
    
    @Override
    public void linetoRel(final float x, final float y) throws ParseException {
        this.command((short)5);
        this.param(x);
        this.param(y);
    }
    
    @Override
    public void linetoAbs(final float x, final float y) throws ParseException {
        this.command((short)4);
        this.param(x);
        this.param(y);
    }
    
    @Override
    public void linetoHorizontalRel(final float x) throws ParseException {
        this.command((short)13);
        this.param(x);
    }
    
    @Override
    public void linetoHorizontalAbs(final float x) throws ParseException {
        this.command((short)12);
        this.param(x);
    }
    
    @Override
    public void linetoVerticalRel(final float y) throws ParseException {
        this.command((short)15);
        this.param(y);
    }
    
    @Override
    public void linetoVerticalAbs(final float y) throws ParseException {
        this.command((short)14);
        this.param(y);
    }
    
    @Override
    public void curvetoCubicRel(final float x1, final float y1, final float x2, final float y2, final float x, final float y) throws ParseException {
        this.command((short)7);
        this.param(x1);
        this.param(y1);
        this.param(x2);
        this.param(y2);
        this.param(x);
        this.param(y);
    }
    
    @Override
    public void curvetoCubicAbs(final float x1, final float y1, final float x2, final float y2, final float x, final float y) throws ParseException {
        this.command((short)6);
        this.param(x1);
        this.param(y1);
        this.param(x2);
        this.param(y2);
        this.param(x);
        this.param(y);
    }
    
    @Override
    public void curvetoCubicSmoothRel(final float x2, final float y2, final float x, final float y) throws ParseException {
        this.command((short)17);
        this.param(x2);
        this.param(y2);
        this.param(x);
        this.param(y);
    }
    
    @Override
    public void curvetoCubicSmoothAbs(final float x2, final float y2, final float x, final float y) throws ParseException {
        this.command((short)16);
        this.param(x2);
        this.param(y2);
        this.param(x);
        this.param(y);
    }
    
    @Override
    public void curvetoQuadraticRel(final float x1, final float y1, final float x, final float y) throws ParseException {
        this.command((short)9);
        this.param(x1);
        this.param(y1);
        this.param(x);
        this.param(y);
    }
    
    @Override
    public void curvetoQuadraticAbs(final float x1, final float y1, final float x, final float y) throws ParseException {
        this.command((short)8);
        this.param(x1);
        this.param(y1);
        this.param(x);
        this.param(y);
    }
    
    @Override
    public void curvetoQuadraticSmoothRel(final float x, final float y) throws ParseException {
        this.command((short)19);
        this.param(x);
        this.param(y);
    }
    
    @Override
    public void curvetoQuadraticSmoothAbs(final float x, final float y) throws ParseException {
        this.command((short)18);
        this.param(x);
        this.param(y);
    }
    
    @Override
    public void arcRel(final float rx, final float ry, final float xAxisRotation, final boolean largeArcFlag, final boolean sweepFlag, final float x, final float y) throws ParseException {
        this.command((short)11);
        this.param(rx);
        this.param(ry);
        this.param(xAxisRotation);
        this.param(largeArcFlag ? 1.0f : 0.0f);
        this.param(sweepFlag ? 1.0f : 0.0f);
        this.param(x);
        this.param(y);
    }
    
    @Override
    public void arcAbs(final float rx, final float ry, final float xAxisRotation, final boolean largeArcFlag, final boolean sweepFlag, final float x, final float y) throws ParseException {
        this.command((short)10);
        this.param(rx);
        this.param(ry);
        this.param(xAxisRotation);
        this.param(largeArcFlag ? 1.0f : 0.0f);
        this.param(sweepFlag ? 1.0f : 0.0f);
        this.param(x);
        this.param(y);
    }
    
    protected void command(final short val) throws ParseException {
        if (this.cindex == this.c.length) {
            this.cs.add(this.c);
            this.c = new short[this.c.length * 2 + 1];
            this.cindex = 0;
        }
        this.c[this.cindex++] = val;
        ++this.ccount;
    }
    
    protected void param(final float val) throws ParseException {
        if (this.pindex == this.p.length) {
            this.ps.add(this.p);
            this.p = new float[this.p.length * 2 + 1];
            this.pindex = 0;
        }
        this.p[this.pindex++] = val;
        ++this.pcount;
    }
    
    @Override
    public void endPath() throws ParseException {
        final short[] allCommands = new short[this.ccount];
        int pos = 0;
        for (final short[] a : this.cs) {
            System.arraycopy(a, 0, allCommands, pos, a.length);
            pos += a.length;
        }
        System.arraycopy(this.c, 0, allCommands, pos, this.cindex);
        this.cs.clear();
        this.c = allCommands;
        final float[] allParams = new float[this.pcount];
        pos = 0;
        for (final float[] a2 : this.ps) {
            System.arraycopy(a2, 0, allParams, pos, a2.length);
            pos += a2.length;
        }
        System.arraycopy(this.p, 0, allParams, pos, this.pindex);
        this.ps.clear();
        this.p = allParams;
    }
}
