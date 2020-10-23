/*
 * Decompiled with CFR 0.150.
 */
package math;

import java.io.Serializable;

public final class RCL
implements Serializable {
    private static final long serialVersionUID = 1L;
    private int row = -1;
    private int column = -1;
    private int layer = -1;

    public RCL() {
    }

    public RCL(int r, int c) {
        this.row = r;
        this.column = c;
        this.layer = 0;
    }

    public RCL(int r, int c, int l) {
        this.row = r;
        this.column = c;
        this.layer = l;
    }

    public int row() {
        return this.row;
    }

    public void setRow(int r) {
        this.row = r;
    }

    public int column() {
        return this.column;
    }

    public void setColumn(int c) {
        this.column = c;
    }

    public int layer() {
        return this.layer;
    }

    public void setLayer(int l) {
        this.layer = l;
    }

    public void set(int r, int c, int l) {
        this.row = r;
        this.column = c;
        this.layer = l;
    }

    public void set(RCL other) {
        this.row = other.row;
        this.column = other.column;
        this.layer = other.layer;
    }

    public String toString() {
        return "row = " + this.row + ", column = " + this.column + ", layer = " + this.layer;
    }
}

