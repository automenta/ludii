// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.geom;

import java.awt.geom.Point2D;
import java.util.Arrays;

public abstract class AbstractSegment implements Segment
{
    static final double eps = 3.552713678800501E-15;
    static final double tol = 1.4210854715202004E-14;
    
    protected abstract int findRoots(final double p0, final double[] p1);
    
    @Override
    public SplitResults split(final double y) {
        final double[] roots = { 0.0, 0.0, 0.0 };
        final int numSol = this.findRoots(y, roots);
        if (numSol == 0) {
            return null;
        }
        Arrays.sort(roots, 0, numSol);
        final double[] segs = new double[numSol + 2];
        int numSegments = 0;
        segs[numSegments++] = 0.0;
        for (final double r : roots) {
            if (r > 0.0) {
                if (r >= 1.0) {
                    break;
                }
                if (segs[numSegments - 1] != r) {
                    segs[numSegments++] = r;
                }
            }
        }
        segs[numSegments++] = 1.0;
        if (numSegments == 2) {
            return null;
        }
        final Segment[] parts = new Segment[numSegments];
        double pT = 0.0;
        int pIdx = 0;
        boolean firstAbove = false;
        boolean prevAbove = false;
        for (int j = 1; j < numSegments; ++j) {
            parts[pIdx] = this.getSegment(segs[j - 1], segs[j]);
            final Point2D.Double pt = parts[pIdx].eval(0.5);
            if (pIdx == 0) {
                ++pIdx;
                prevAbove = (firstAbove = (pt.y < y));
            }
            else {
                final boolean above = pt.y < y;
                if (prevAbove == above) {
                    parts[pIdx - 1] = this.getSegment(pT, segs[j]);
                }
                else {
                    ++pIdx;
                    pT = segs[j - 1];
                    prevAbove = above;
                }
            }
        }
        if (pIdx == 1) {
            return null;
        }
        Segment[] above2;
        Segment[] below;
        if (firstAbove) {
            above2 = new Segment[(pIdx + 1) / 2];
            below = new Segment[pIdx / 2];
        }
        else {
            above2 = new Segment[pIdx / 2];
            below = new Segment[(pIdx + 1) / 2];
        }
        int ai = 0;
        int bi = 0;
        for (int k = 0; k < pIdx; ++k) {
            if (firstAbove) {
                above2[ai++] = parts[k];
            }
            else {
                below[bi++] = parts[k];
            }
            firstAbove = !firstAbove;
        }
        return new SplitResults(below, above2);
    }
    
    @Override
    public Segment splitBefore(final double t) {
        return this.getSegment(0.0, t);
    }
    
    @Override
    public Segment splitAfter(final double t) {
        return this.getSegment(t, 1.0);
    }
    
    public static int solveLine(final double a, final double b, final double[] roots) {
        if (a != 0.0) {
            roots[0] = -b / a;
            return 1;
        }
        if (b != 0.0) {
            return 0;
        }
        roots[0] = 0.0;
        return 1;
    }
    
    public static int solveQuad(final double a, final double b, final double c, final double[] roots) {
        if (a == 0.0) {
            return solveLine(b, c, roots);
        }
        double det = b * b - 4.0 * a * c;
        if (Math.abs(det) <= 1.4210854715202004E-14 * b * b) {
            roots[0] = -b / (2.0 * a);
            return 1;
        }
        if (det < 0.0) {
            return 0;
        }
        det = Math.sqrt(det);
        final double w = -(b + matchSign(det, b));
        roots[0] = 2.0 * c / w;
        roots[1] = w / (2.0 * a);
        return 2;
    }
    
    public static double matchSign(final double a, final double b) {
        if (b < 0.0) {
            return (a < 0.0) ? a : (-a);
        }
        return (a > 0.0) ? a : (-a);
    }
    
    public static int solveCubic(final double a3, final double a2, final double a1, final double a0, final double[] roots) {
        final double[] dRoots = { 0.0, 0.0 };
        final int dCnt = solveQuad(3.0 * a3, 2.0 * a2, a1, dRoots);
        final double[] yVals = { 0.0, 0.0, 0.0, 0.0 };
        final double[] tVals = { 0.0, 0.0, 0.0, 0.0 };
        int yCnt = 0;
        yVals[yCnt] = a0;
        tVals[yCnt++] = 0.0;
        switch (dCnt) {
            case 1: {
                final double r = dRoots[0];
                if (r > 0.0 && r < 1.0) {
                    yVals[yCnt] = ((a3 * r + a2) * r + a1) * r + a0;
                    tVals[yCnt++] = r;
                    break;
                }
                break;
            }
            case 2: {
                if (dRoots[0] > dRoots[1]) {
                    final double t = dRoots[0];
                    dRoots[0] = dRoots[1];
                    dRoots[1] = t;
                }
                double r = dRoots[0];
                if (r > 0.0 && r < 1.0) {
                    yVals[yCnt] = ((a3 * r + a2) * r + a1) * r + a0;
                    tVals[yCnt++] = r;
                }
                r = dRoots[1];
                if (r > 0.0 && r < 1.0) {
                    yVals[yCnt] = ((a3 * r + a2) * r + a1) * r + a0;
                    tVals[yCnt++] = r;
                    break;
                }
                break;
            }
        }
        yVals[yCnt] = a3 + a2 + a1 + a0;
        tVals[yCnt++] = 1.0;
        int ret = 0;
        for (int i = 0; i < yCnt - 1; ++i) {
            double y0 = yVals[i];
            double t2 = tVals[i];
            double y2 = yVals[i + 1];
            double t3 = tVals[i + 1];
            if (y0 >= 0.0 || y2 >= 0.0) {
                if (y0 <= 0.0 || y2 <= 0.0) {
                    if (y0 > y2) {
                        double t4 = y0;
                        y0 = y2;
                        y2 = t4;
                        t4 = t2;
                        t2 = t3;
                        t3 = t4;
                    }
                    if (-y0 < 1.4210854715202004E-14 * y2) {
                        roots[ret++] = t2;
                    }
                    else if (y2 < -1.4210854715202004E-14 * y0) {
                        roots[ret++] = t3;
                        ++i;
                    }
                    else {
                        final double epsZero = 1.4210854715202004E-14 * (y2 - y0);
                        int cnt;
                        for (cnt = 0; cnt < 20; ++cnt) {
                            final double dt = t3 - t2;
                            final double dy = y2 - y0;
                            final double t5 = t2 + (Math.abs(y0 / dy) * 99.0 + 0.5) * dt / 100.0;
                            final double v = ((a3 * t5 + a2) * t5 + a1) * t5 + a0;
                            if (Math.abs(v) < epsZero) {
                                roots[ret++] = t5;
                                break;
                            }
                            if (v < 0.0) {
                                t2 = t5;
                                y0 = v;
                            }
                            else {
                                t3 = t5;
                                y2 = v;
                            }
                        }
                        if (cnt == 20) {
                            roots[ret++] = (t2 + t3) / 2.0;
                        }
                    }
                }
            }
        }
        return ret;
    }
}
