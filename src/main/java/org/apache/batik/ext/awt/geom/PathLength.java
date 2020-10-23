// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.geom;

import java.awt.geom.PathIterator;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.awt.Shape;

public class PathLength
{
    protected Shape path;
    protected List segments;
    protected int[] segmentIndexes;
    protected float pathLength;
    protected boolean initialised;
    
    public PathLength(final Shape path) {
        this.setPath(path);
    }
    
    public Shape getPath() {
        return this.path;
    }
    
    public void setPath(final Shape v) {
        this.path = v;
        this.initialised = false;
    }
    
    public float lengthOfPath() {
        if (!this.initialised) {
            this.initialise();
        }
        return this.pathLength;
    }
    
    protected void initialise() {
        this.pathLength = 0.0f;
        final PathIterator pi = this.path.getPathIterator(new AffineTransform());
        final SingleSegmentPathIterator sspi = new SingleSegmentPathIterator();
        this.segments = new ArrayList(20);
        final List indexes = new ArrayList(20);
        int index = 0;
        int origIndex = -1;
        float lastMoveX = 0.0f;
        float lastMoveY = 0.0f;
        float currentX = 0.0f;
        float currentY = 0.0f;
        final float[] seg = new float[6];
        this.segments.add(new PathSegment(0, 0.0f, 0.0f, 0.0f, origIndex));
        while (!pi.isDone()) {
            ++origIndex;
            indexes.add(index);
            int segType = pi.currentSegment(seg);
            switch (segType) {
                case 0: {
                    this.segments.add(new PathSegment(segType, seg[0], seg[1], this.pathLength, origIndex));
                    currentX = seg[0];
                    currentY = seg[1];
                    lastMoveX = currentX;
                    lastMoveY = currentY;
                    ++index;
                    pi.next();
                    continue;
                }
                case 1: {
                    this.pathLength += (float)Point2D.distance(currentX, currentY, seg[0], seg[1]);
                    this.segments.add(new PathSegment(segType, seg[0], seg[1], this.pathLength, origIndex));
                    currentX = seg[0];
                    currentY = seg[1];
                    ++index;
                    pi.next();
                    continue;
                }
                case 4: {
                    this.pathLength += (float)Point2D.distance(currentX, currentY, lastMoveX, lastMoveY);
                    this.segments.add(new PathSegment(1, lastMoveX, lastMoveY, this.pathLength, origIndex));
                    currentX = lastMoveX;
                    currentY = lastMoveY;
                    ++index;
                    pi.next();
                    continue;
                }
                default: {
                    sspi.setPathIterator(pi, currentX, currentY);
                    final FlatteningPathIterator fpi = new FlatteningPathIterator(sspi, 0.009999999776482582);
                    while (!fpi.isDone()) {
                        segType = fpi.currentSegment(seg);
                        if (segType == 1) {
                            this.pathLength += (float)Point2D.distance(currentX, currentY, seg[0], seg[1]);
                            this.segments.add(new PathSegment(segType, seg[0], seg[1], this.pathLength, origIndex));
                            currentX = seg[0];
                            currentY = seg[1];
                            ++index;
                        }
                        fpi.next();
                    }
                    continue;
                }
            }
        }
        this.segmentIndexes = new int[indexes.size()];
        for (int i = 0; i < this.segmentIndexes.length; ++i) {
            this.segmentIndexes[i] = indexes.get(i);
        }
        this.initialised = true;
    }
    
    public int getNumberOfSegments() {
        if (!this.initialised) {
            this.initialise();
        }
        return this.segmentIndexes.length;
    }
    
    public float getLengthAtSegment(final int index) {
        if (!this.initialised) {
            this.initialise();
        }
        if (index <= 0) {
            return 0.0f;
        }
        if (index >= this.segmentIndexes.length) {
            return this.pathLength;
        }
        final PathSegment seg = this.segments.get(this.segmentIndexes[index]);
        return seg.getLength();
    }
    
    public int segmentAtLength(final float length) {
        final int upperIndex = this.findUpperIndex(length);
        if (upperIndex == -1) {
            return -1;
        }
        if (upperIndex == 0) {
            final PathSegment upper = this.segments.get(upperIndex);
            return upper.getIndex();
        }
        final PathSegment lower = this.segments.get(upperIndex - 1);
        return lower.getIndex();
    }
    
    public Point2D pointAtLength(final int index, final float proportion) {
        if (!this.initialised) {
            this.initialise();
        }
        if (index < 0 || index >= this.segmentIndexes.length) {
            return null;
        }
        PathSegment seg = this.segments.get(this.segmentIndexes[index]);
        final float start = seg.getLength();
        float end;
        if (index == this.segmentIndexes.length - 1) {
            end = this.pathLength;
        }
        else {
            seg = this.segments.get(this.segmentIndexes[index + 1]);
            end = seg.getLength();
        }
        return this.pointAtLength(start + (end - start) * proportion);
    }
    
    public Point2D pointAtLength(final float length) {
        final int upperIndex = this.findUpperIndex(length);
        if (upperIndex == -1) {
            return null;
        }
        final PathSegment upper = this.segments.get(upperIndex);
        if (upperIndex == 0) {
            return new Point2D.Float(upper.getX(), upper.getY());
        }
        final PathSegment lower = this.segments.get(upperIndex - 1);
        final float offset = length - lower.getLength();
        final double theta = Math.atan2(upper.getY() - lower.getY(), upper.getX() - lower.getX());
        final float xPoint = (float)(lower.getX() + offset * Math.cos(theta));
        final float yPoint = (float)(lower.getY() + offset * Math.sin(theta));
        return new Point2D.Float(xPoint, yPoint);
    }
    
    public float angleAtLength(final int index, final float proportion) {
        if (!this.initialised) {
            this.initialise();
        }
        if (index < 0 || index >= this.segmentIndexes.length) {
            return 0.0f;
        }
        PathSegment seg = this.segments.get(this.segmentIndexes[index]);
        final float start = seg.getLength();
        float end;
        if (index == this.segmentIndexes.length - 1) {
            end = this.pathLength;
        }
        else {
            seg = this.segments.get(this.segmentIndexes[index + 1]);
            end = seg.getLength();
        }
        return this.angleAtLength(start + (end - start) * proportion);
    }
    
    public float angleAtLength(final float length) {
        int upperIndex = this.findUpperIndex(length);
        if (upperIndex == -1) {
            return 0.0f;
        }
        final PathSegment upper = this.segments.get(upperIndex);
        if (upperIndex == 0) {
            upperIndex = 1;
        }
        final PathSegment lower = this.segments.get(upperIndex - 1);
        return (float)Math.atan2(upper.getY() - lower.getY(), upper.getX() - lower.getX());
    }
    
    public int findUpperIndex(final float length) {
        if (!this.initialised) {
            this.initialise();
        }
        if (length < 0.0f || length > this.pathLength) {
            return -1;
        }
        int lb = 0;
        int ub = this.segments.size() - 1;
        while (lb != ub) {
            final int curr = lb + ub >> 1;
            final PathSegment ps = this.segments.get(curr);
            if (ps.getLength() >= length) {
                ub = curr;
            }
            else {
                lb = curr + 1;
            }
        }
        while (true) {
            final PathSegment ps2 = this.segments.get(ub);
            if (ps2.getSegType() != 0 || ub == this.segments.size() - 1) {
                break;
            }
            ++ub;
        }
        int upperIndex = -1;
        for (int currentIndex = 0, numSegments = this.segments.size(); upperIndex <= 0 && currentIndex < numSegments; ++currentIndex) {
            final PathSegment ps3 = this.segments.get(currentIndex);
            if (ps3.getLength() >= length && ps3.getSegType() != 0) {
                upperIndex = currentIndex;
            }
        }
        return upperIndex;
    }
    
    protected static class SingleSegmentPathIterator implements PathIterator
    {
        protected PathIterator it;
        protected boolean done;
        protected boolean moveDone;
        protected double x;
        protected double y;
        
        public void setPathIterator(final PathIterator it, final double x, final double y) {
            this.it = it;
            this.x = x;
            this.y = y;
            this.done = false;
            this.moveDone = false;
        }
        
        @Override
        public int currentSegment(final double[] coords) {
            final int type = this.it.currentSegment(coords);
            if (!this.moveDone) {
                coords[0] = this.x;
                coords[1] = this.y;
                return 0;
            }
            return type;
        }
        
        @Override
        public int currentSegment(final float[] coords) {
            final int type = this.it.currentSegment(coords);
            if (!this.moveDone) {
                coords[0] = (float)this.x;
                coords[1] = (float)this.y;
                return 0;
            }
            return type;
        }
        
        @Override
        public int getWindingRule() {
            return this.it.getWindingRule();
        }
        
        @Override
        public boolean isDone() {
            return this.done || this.it.isDone();
        }
        
        @Override
        public void next() {
            if (!this.done) {
                if (!this.moveDone) {
                    this.moveDone = true;
                }
                else {
                    this.it.next();
                    this.done = true;
                }
            }
        }
    }
    
    protected static class PathSegment
    {
        protected final int segType;
        protected float x;
        protected float y;
        protected float length;
        protected int index;
        
        PathSegment(final int segType, final float x, final float y, final float len, final int idx) {
            this.segType = segType;
            this.x = x;
            this.y = y;
            this.length = len;
            this.index = idx;
        }
        
        public int getSegType() {
            return this.segType;
        }
        
        public float getX() {
            return this.x;
        }
        
        public void setX(final float v) {
            this.x = v;
        }
        
        public float getY() {
            return this.y;
        }
        
        public void setY(final float v) {
            this.y = v;
        }
        
        public float getLength() {
            return this.length;
        }
        
        public void setLength(final float v) {
            this.length = v;
        }
        
        public int getIndex() {
            return this.index;
        }
        
        public void setIndex(final int v) {
            this.index = v;
        }
    }
}
