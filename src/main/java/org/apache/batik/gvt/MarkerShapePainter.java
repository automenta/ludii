// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt;

import java.awt.geom.Arc2D;
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;
import java.util.ArrayList;
import java.awt.geom.AffineTransform;
import org.apache.batik.ext.awt.geom.ExtendedPathIterator;
import java.util.List;
import java.awt.geom.Point2D;
import java.awt.Graphics2D;
import org.apache.batik.ext.awt.geom.ShapeExtender;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.geom.ExtendedShape;

public class MarkerShapePainter implements ShapePainter
{
    protected ExtendedShape extShape;
    protected Marker startMarker;
    protected Marker middleMarker;
    protected Marker endMarker;
    private ProxyGraphicsNode startMarkerProxy;
    private ProxyGraphicsNode[] middleMarkerProxies;
    private ProxyGraphicsNode endMarkerProxy;
    private CompositeGraphicsNode markerGroup;
    private Rectangle2D dPrimitiveBounds;
    private Rectangle2D dGeometryBounds;
    
    public MarkerShapePainter(final Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException();
        }
        if (shape instanceof ExtendedShape) {
            this.extShape = (ExtendedShape)shape;
        }
        else {
            this.extShape = new ShapeExtender(shape);
        }
    }
    
    @Override
    public void paint(final Graphics2D g2d) {
        if (this.markerGroup == null) {
            this.buildMarkerGroup();
        }
        if (this.markerGroup.getChildren().size() > 0) {
            this.markerGroup.paint(g2d);
        }
    }
    
    @Override
    public Shape getPaintedArea() {
        if (this.markerGroup == null) {
            this.buildMarkerGroup();
        }
        return this.markerGroup.getOutline();
    }
    
    @Override
    public Rectangle2D getPaintedBounds2D() {
        if (this.markerGroup == null) {
            this.buildMarkerGroup();
        }
        return this.markerGroup.getPrimitiveBounds();
    }
    
    @Override
    public boolean inPaintedArea(final Point2D pt) {
        if (this.markerGroup == null) {
            this.buildMarkerGroup();
        }
        final GraphicsNode gn = this.markerGroup.nodeHitAt(pt);
        return gn != null;
    }
    
    @Override
    public Shape getSensitiveArea() {
        return null;
    }
    
    @Override
    public Rectangle2D getSensitiveBounds2D() {
        return null;
    }
    
    @Override
    public boolean inSensitiveArea(final Point2D pt) {
        return false;
    }
    
    @Override
    public void setShape(final Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException();
        }
        if (shape instanceof ExtendedShape) {
            this.extShape = (ExtendedShape)shape;
        }
        else {
            this.extShape = new ShapeExtender(shape);
        }
        this.startMarkerProxy = null;
        this.middleMarkerProxies = null;
        this.endMarkerProxy = null;
        this.markerGroup = null;
    }
    
    public ExtendedShape getExtShape() {
        return this.extShape;
    }
    
    @Override
    public Shape getShape() {
        return this.extShape;
    }
    
    public Marker getStartMarker() {
        return this.startMarker;
    }
    
    public void setStartMarker(final Marker startMarker) {
        this.startMarker = startMarker;
        this.startMarkerProxy = null;
        this.markerGroup = null;
    }
    
    public Marker getMiddleMarker() {
        return this.middleMarker;
    }
    
    public void setMiddleMarker(final Marker middleMarker) {
        this.middleMarker = middleMarker;
        this.middleMarkerProxies = null;
        this.markerGroup = null;
    }
    
    public Marker getEndMarker() {
        return this.endMarker;
    }
    
    public void setEndMarker(final Marker endMarker) {
        this.endMarker = endMarker;
        this.endMarkerProxy = null;
        this.markerGroup = null;
    }
    
    protected void buildMarkerGroup() {
        if (this.startMarker != null && this.startMarkerProxy == null) {
            this.startMarkerProxy = this.buildStartMarkerProxy();
        }
        if (this.middleMarker != null && this.middleMarkerProxies == null) {
            this.middleMarkerProxies = this.buildMiddleMarkerProxies();
        }
        if (this.endMarker != null && this.endMarkerProxy == null) {
            this.endMarkerProxy = this.buildEndMarkerProxy();
        }
        final CompositeGraphicsNode group = new CompositeGraphicsNode();
        final List children = group.getChildren();
        if (this.startMarkerProxy != null) {
            children.add(this.startMarkerProxy);
        }
        if (this.middleMarkerProxies != null) {
            for (final ProxyGraphicsNode middleMarkerProxy : this.middleMarkerProxies) {
                children.add(middleMarkerProxy);
            }
        }
        if (this.endMarkerProxy != null) {
            children.add(this.endMarkerProxy);
        }
        this.markerGroup = group;
    }
    
    protected ProxyGraphicsNode buildStartMarkerProxy() {
        final ExtendedPathIterator iter = this.getExtShape().getExtendedPathIterator();
        final double[] coords = new double[7];
        int segType = 0;
        if (iter.isDone()) {
            return null;
        }
        segType = iter.currentSegment(coords);
        if (segType != 0) {
            return null;
        }
        iter.next();
        final Point2D markerPosition = new Point2D.Double(coords[0], coords[1]);
        double rotation = this.startMarker.getOrient();
        if (Double.isNaN(rotation) && !iter.isDone()) {
            final double[] next = new double[7];
            int nextSegType = 0;
            nextSegType = iter.currentSegment(next);
            if (nextSegType == 4) {
                nextSegType = 1;
                next[0] = coords[0];
                next[1] = coords[1];
            }
            rotation = this.computeRotation(null, 0, coords, segType, next, nextSegType);
        }
        final AffineTransform markerTxf = this.computeMarkerTransform(this.startMarker, markerPosition, rotation);
        final ProxyGraphicsNode gn = new ProxyGraphicsNode();
        gn.setSource(this.startMarker.getMarkerNode());
        gn.setTransform(markerTxf);
        return gn;
    }
    
    protected ProxyGraphicsNode buildEndMarkerProxy() {
        final ExtendedPathIterator iter = this.getExtShape().getExtendedPathIterator();
        int nPoints = 0;
        if (iter.isDone()) {
            return null;
        }
        final double[] coords = new double[7];
        final double[] moveTo = new double[2];
        int segType = 0;
        segType = iter.currentSegment(coords);
        if (segType != 0) {
            return null;
        }
        ++nPoints;
        moveTo[0] = coords[0];
        moveTo[1] = coords[1];
        iter.next();
        double[] lastButOne = new double[7];
        double[] last = { coords[0], coords[1], coords[2], coords[3], coords[4], coords[5], coords[6] };
        double[] tmp = null;
        int lastSegType = segType;
        int lastButOneSegType = 0;
        while (!iter.isDone()) {
            tmp = lastButOne;
            lastButOne = last;
            last = tmp;
            lastButOneSegType = lastSegType;
            lastSegType = iter.currentSegment(last);
            if (lastSegType == 0) {
                moveTo[0] = last[0];
                moveTo[1] = last[1];
            }
            else if (lastSegType == 4) {
                lastSegType = 1;
                last[0] = moveTo[0];
                last[1] = moveTo[1];
            }
            iter.next();
            ++nPoints;
        }
        if (nPoints < 2) {
            return null;
        }
        final Point2D markerPosition = this.getSegmentTerminatingPoint(last, lastSegType);
        double rotation = this.endMarker.getOrient();
        if (Double.isNaN(rotation)) {
            rotation = this.computeRotation(lastButOne, lastButOneSegType, last, lastSegType, null, 0);
        }
        final AffineTransform markerTxf = this.computeMarkerTransform(this.endMarker, markerPosition, rotation);
        final ProxyGraphicsNode gn = new ProxyGraphicsNode();
        gn.setSource(this.endMarker.getMarkerNode());
        gn.setTransform(markerTxf);
        return gn;
    }
    
    protected ProxyGraphicsNode[] buildMiddleMarkerProxies() {
        final ExtendedPathIterator iter = this.getExtShape().getExtendedPathIterator();
        double[] prev = new double[7];
        double[] curr = new double[7];
        double[] next = new double[7];
        double[] tmp = null;
        int prevSegType = 0;
        int currSegType = 0;
        int nextSegType = 0;
        if (iter.isDone()) {
            return null;
        }
        prevSegType = iter.currentSegment(prev);
        final double[] moveTo = new double[2];
        if (prevSegType != 0) {
            return null;
        }
        moveTo[0] = prev[0];
        moveTo[1] = prev[1];
        iter.next();
        if (iter.isDone()) {
            return null;
        }
        currSegType = iter.currentSegment(curr);
        if (currSegType == 0) {
            moveTo[0] = curr[0];
            moveTo[1] = curr[1];
        }
        else if (currSegType == 4) {
            currSegType = 1;
            curr[0] = moveTo[0];
            curr[1] = moveTo[1];
        }
        iter.next();
        final List proxies = new ArrayList();
        while (!iter.isDone()) {
            nextSegType = iter.currentSegment(next);
            if (nextSegType == 0) {
                moveTo[0] = next[0];
                moveTo[1] = next[1];
            }
            else if (nextSegType == 4) {
                nextSegType = 1;
                next[0] = moveTo[0];
                next[1] = moveTo[1];
            }
            proxies.add(this.createMiddleMarker(prev, prevSegType, curr, currSegType, next, nextSegType));
            tmp = prev;
            prev = curr;
            prevSegType = currSegType;
            curr = next;
            currSegType = nextSegType;
            next = tmp;
            iter.next();
        }
        final ProxyGraphicsNode[] gn = new ProxyGraphicsNode[proxies.size()];
        proxies.toArray(gn);
        return gn;
    }
    
    private ProxyGraphicsNode createMiddleMarker(final double[] prev, final int prevSegType, final double[] curr, final int currSegType, final double[] next, final int nextSegType) {
        final Point2D markerPosition = this.getSegmentTerminatingPoint(curr, currSegType);
        double rotation = this.middleMarker.getOrient();
        if (Double.isNaN(rotation)) {
            rotation = this.computeRotation(prev, prevSegType, curr, currSegType, next, nextSegType);
        }
        final AffineTransform markerTxf = this.computeMarkerTransform(this.middleMarker, markerPosition, rotation);
        final ProxyGraphicsNode gn = new ProxyGraphicsNode();
        gn.setSource(this.middleMarker.getMarkerNode());
        gn.setTransform(markerTxf);
        return gn;
    }
    
    private double computeRotation(final double[] prev, final int prevSegType, final double[] curr, final int currSegType, final double[] next, final int nextSegType) {
        double[] inSlope = this.computeInSlope(prev, prevSegType, curr, currSegType);
        double[] outSlope = this.computeOutSlope(curr, currSegType, next, nextSegType);
        if (inSlope == null) {
            inSlope = outSlope;
        }
        if (outSlope == null) {
            outSlope = inSlope;
        }
        if (inSlope == null) {
            return 0.0;
        }
        final double dx = inSlope[0] + outSlope[0];
        final double dy = inSlope[1] + outSlope[1];
        if (dx == 0.0 && dy == 0.0) {
            return Math.toDegrees(Math.atan2(inSlope[1], inSlope[0])) + 90.0;
        }
        return Math.toDegrees(Math.atan2(dy, dx));
    }
    
    private double[] computeInSlope(final double[] prev, final int prevSegType, final double[] curr, final int currSegType) {
        final Point2D currEndPoint = this.getSegmentTerminatingPoint(curr, currSegType);
        double dx = 0.0;
        double dy = 0.0;
        switch (currSegType) {
            case 1: {
                final Point2D prevEndPoint = this.getSegmentTerminatingPoint(prev, prevSegType);
                dx = currEndPoint.getX() - prevEndPoint.getX();
                dy = currEndPoint.getY() - prevEndPoint.getY();
                break;
            }
            case 2: {
                dx = currEndPoint.getX() - curr[0];
                dy = currEndPoint.getY() - curr[1];
                break;
            }
            case 3: {
                dx = currEndPoint.getX() - curr[2];
                dy = currEndPoint.getY() - curr[3];
                break;
            }
            case 4321: {
                final Point2D prevEndPoint = this.getSegmentTerminatingPoint(prev, prevSegType);
                final boolean large = curr[3] != 0.0;
                final boolean goLeft = curr[4] != 0.0;
                final Arc2D arc = ExtendedGeneralPath.computeArc(prevEndPoint.getX(), prevEndPoint.getY(), curr[0], curr[1], curr[2], large, goLeft, curr[5], curr[6]);
                double theta = arc.getAngleStart() + arc.getAngleExtent();
                theta = Math.toRadians(theta);
                dx = -arc.getWidth() / 2.0 * Math.sin(theta);
                dy = arc.getHeight() / 2.0 * Math.cos(theta);
                if (curr[2] != 0.0) {
                    final double ang = Math.toRadians(-curr[2]);
                    final double sinA = Math.sin(ang);
                    final double cosA = Math.cos(ang);
                    final double tdx = dx * cosA - dy * sinA;
                    final double tdy = dx * sinA + dy * cosA;
                    dx = tdx;
                    dy = tdy;
                }
                if (goLeft) {
                    dx = -dx;
                }
                else {
                    dy = -dy;
                }
                break;
            }
            case 4: {
                throw new RuntimeException("should not have SEG_CLOSE here");
            }
            default: {
                return null;
            }
        }
        if (dx == 0.0 && dy == 0.0) {
            return null;
        }
        return this.normalize(new double[] { dx, dy });
    }
    
    private double[] computeOutSlope(final double[] curr, final int currSegType, final double[] next, final int nextSegType) {
        final Point2D currEndPoint = this.getSegmentTerminatingPoint(curr, currSegType);
        double dx = 0.0;
        double dy = 0.0;
        switch (nextSegType) {
            case 4: {
                break;
            }
            case 1:
            case 2:
            case 3: {
                dx = next[0] - currEndPoint.getX();
                dy = next[1] - currEndPoint.getY();
                break;
            }
            case 4321: {
                final boolean large = next[3] != 0.0;
                final boolean goLeft = next[4] != 0.0;
                final Arc2D arc = ExtendedGeneralPath.computeArc(currEndPoint.getX(), currEndPoint.getY(), next[0], next[1], next[2], large, goLeft, next[5], next[6]);
                double theta = arc.getAngleStart();
                theta = Math.toRadians(theta);
                dx = -arc.getWidth() / 2.0 * Math.sin(theta);
                dy = arc.getHeight() / 2.0 * Math.cos(theta);
                if (next[2] != 0.0) {
                    final double ang = Math.toRadians(-next[2]);
                    final double sinA = Math.sin(ang);
                    final double cosA = Math.cos(ang);
                    final double tdx = dx * cosA - dy * sinA;
                    final double tdy = dx * sinA + dy * cosA;
                    dx = tdx;
                    dy = tdy;
                }
                if (goLeft) {
                    dx = -dx;
                }
                else {
                    dy = -dy;
                }
                break;
            }
            default: {
                return null;
            }
        }
        if (dx == 0.0 && dy == 0.0) {
            return null;
        }
        return this.normalize(new double[] { dx, dy });
    }
    
    public double[] normalize(final double[] v) {
        final double n = Math.sqrt(v[0] * v[0] + v[1] * v[1]);
        final int n2 = 0;
        v[n2] /= n;
        final int n3 = 1;
        v[n3] /= n;
        return v;
    }
    
    private AffineTransform computeMarkerTransform(final Marker marker, final Point2D markerPosition, final double rotation) {
        final Point2D ref = marker.getRef();
        final AffineTransform txf = new AffineTransform();
        txf.translate(markerPosition.getX() - ref.getX(), markerPosition.getY() - ref.getY());
        if (!Double.isNaN(rotation)) {
            txf.rotate(Math.toRadians(rotation), ref.getX(), ref.getY());
        }
        return txf;
    }
    
    protected Point2D getSegmentTerminatingPoint(final double[] coords, final int segType) {
        switch (segType) {
            case 3: {
                return new Point2D.Double(coords[4], coords[5]);
            }
            case 1: {
                return new Point2D.Double(coords[0], coords[1]);
            }
            case 0: {
                return new Point2D.Double(coords[0], coords[1]);
            }
            case 2: {
                return new Point2D.Double(coords[2], coords[3]);
            }
            case 4321: {
                return new Point2D.Double(coords[5], coords[6]);
            }
            default: {
                throw new RuntimeException("invalid segmentType:" + segType);
            }
        }
    }
}
