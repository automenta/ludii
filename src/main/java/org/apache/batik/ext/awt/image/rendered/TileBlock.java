// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.util.List;
import java.util.ArrayList;

public class TileBlock
{
    int occX;
    int occY;
    int occW;
    int occH;
    int xOff;
    int yOff;
    int w;
    int h;
    int benefit;
    boolean[] occupied;
    
    TileBlock(final int occX, final int occY, final int occW, final int occH, final boolean[] occupied, final int xOff, final int yOff, final int w, final int h) {
        this.occX = occX;
        this.occY = occY;
        this.occW = occW;
        this.occH = occH;
        this.xOff = xOff;
        this.yOff = yOff;
        this.w = w;
        this.h = h;
        this.occupied = occupied;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                if (!occupied[x + xOff + occW * (y + yOff)]) {
                    ++this.benefit;
                }
            }
        }
    }
    
    @Override
    public String toString() {
        String ret = "";
        for (int y = 0; y < this.occH; ++y) {
            for (int x = 0; x < this.occW + 1; ++x) {
                if (x == this.xOff || x == this.xOff + this.w) {
                    if (y == this.yOff || y == this.yOff + this.h - 1) {
                        ret += "+";
                    }
                    else if (y > this.yOff && y < this.yOff + this.h - 1) {
                        ret += "|";
                    }
                    else {
                        ret += " ";
                    }
                }
                else if (y == this.yOff && x > this.xOff && x < this.xOff + this.w) {
                    ret += "-";
                }
                else if (y == this.yOff + this.h - 1 && x > this.xOff && x < this.xOff + this.w) {
                    ret += "_";
                }
                else {
                    ret += " ";
                }
                if (x != this.occW) {
                    if (this.occupied[x + y * this.occW]) {
                        ret += "*";
                    }
                    else {
                        ret += ".";
                    }
                }
            }
            ret += "\n";
        }
        return ret;
    }
    
    int getXLoc() {
        return this.occX + this.xOff;
    }
    
    int getYLoc() {
        return this.occY + this.yOff;
    }
    
    int getWidth() {
        return this.w;
    }
    
    int getHeight() {
        return this.h;
    }
    
    int getBenefit() {
        return this.benefit;
    }
    
    int getWork() {
        return this.w * this.h + 1;
    }
    
    static int getWork(final TileBlock[] blocks) {
        int ret = 0;
        for (final TileBlock block : blocks) {
            ret += block.getWork();
        }
        return ret;
    }
    
    TileBlock[] getBestSplit() {
        if (this.simplify()) {
            return null;
        }
        if (this.benefit == this.w * this.h) {
            return new TileBlock[] { this };
        }
        return this.splitOneGo();
    }
    
    public TileBlock[] splitOneGo() {
        final boolean[] filled = this.occupied.clone();
        final List items = new ArrayList();
        for (int y = this.yOff; y < this.yOff + this.h; ++y) {
            for (int x = this.xOff; x < this.xOff + this.w; ++x) {
                if (!filled[x + y * this.occW]) {
                    int cw = this.xOff + this.w - x;
                    for (int cx = x; cx < x + cw; ++cx) {
                        if (filled[cx + y * this.occW]) {
                            cw = cx - x;
                        }
                        else {
                            filled[cx + y * this.occW] = true;
                        }
                    }
                    int ch = 1;
                    for (int cy = y + 1; cy < this.yOff + this.h; ++cy) {
                        int cx2;
                        for (cx2 = x; cx2 < x + cw && !filled[cx2 + cy * this.occW]; ++cx2) {}
                        if (cx2 != x + cw) {
                            break;
                        }
                        for (cx2 = x; cx2 < x + cw; ++cx2) {
                            filled[cx2 + cy * this.occW] = true;
                        }
                        ++ch;
                    }
                    items.add(new TileBlock(this.occX, this.occY, this.occW, this.occH, this.occupied, x, y, cw, ch));
                    x += cw - 1;
                }
            }
        }
        final TileBlock[] ret = new TileBlock[items.size()];
        items.toArray(ret);
        return ret;
    }
    
    public boolean simplify() {
        final boolean[] workOccupied = this.occupied;
        for (int y = 0; y < this.h; --y, --this.h, ++y) {
            int x;
            for (x = 0; x < this.w && workOccupied[x + this.xOff + this.occW * (y + this.yOff)]; ++x) {}
            if (x != this.w) {
                break;
            }
            ++this.yOff;
        }
        if (this.h == 0) {
            return true;
        }
        for (int y = this.h - 1; y >= 0; --y) {
            int x;
            for (x = 0; x < this.w && workOccupied[x + this.xOff + this.occW * (y + this.yOff)]; ++x) {}
            if (x != this.w) {
                break;
            }
            --this.h;
        }
        for (int x2 = 0; x2 < this.w; --x2, --this.w, ++x2) {
            int y2;
            for (y2 = 0; y2 < this.h && workOccupied[x2 + this.xOff + this.occW * (y2 + this.yOff)]; ++y2) {}
            if (y2 != this.h) {
                break;
            }
            ++this.xOff;
        }
        for (int x2 = this.w - 1; x2 >= 0; --x2) {
            int y2;
            for (y2 = 0; y2 < this.h && workOccupied[x2 + this.xOff + this.occW * (y2 + this.yOff)]; ++y2) {}
            if (y2 != this.h) {
                break;
            }
            --this.w;
        }
        return false;
    }
}
