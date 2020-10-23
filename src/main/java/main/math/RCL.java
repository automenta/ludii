// 
// Decompiled by Procyon v0.5.36
// 

package main.math;

import java.io.Serializable;

public final class RCL implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int row;
    private int column;
    private int layer;
    
    public RCL() {
        this.row = -1;
        this.column = -1;
        this.layer = -1;
    }
    
    public RCL(final int r, final int c) {
        this.row = -1;
        this.column = -1;
        this.layer = -1;
        this.row = r;
        this.column = c;
        this.layer = 0;
    }
    
    public RCL(final int r, final int c, final int l) {
        this.row = -1;
        this.column = -1;
        this.layer = -1;
        this.row = r;
        this.column = c;
        this.layer = l;
    }
    
    public int row() {
        return this.row;
    }
    
    public void setRow(final int r) {
        this.row = r;
    }
    
    public int column() {
        return this.column;
    }
    
    public void setColumn(final int c) {
        this.column = c;
    }
    
    public int layer() {
        return this.layer;
    }
    
    public void setLayer(final int l) {
        this.layer = l;
    }
    
    public void set(final int r, final int c, final int l) {
        this.row = r;
        this.column = c;
        this.layer = l;
    }
    
    public void set(final RCL other) {
        this.row = other.row;
        this.column = other.column;
        this.layer = other.layer;
    }
    
    @Override
    public String toString() {
        return "row = " + this.row + ", column = " + this.column + ", layer = " + this.layer;
    }
}
