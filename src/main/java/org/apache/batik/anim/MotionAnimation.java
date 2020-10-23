// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim;

import java.awt.geom.Point2D;
import org.apache.batik.ext.awt.geom.Cubic;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.ext.awt.geom.ExtendedPathIterator;
import java.awt.Shape;
import org.apache.batik.anim.values.AnimatableMotionPointValue;
import org.apache.batik.anim.values.AnimatableAngleValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.ext.awt.geom.PathLength;
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;

public class MotionAnimation extends InterpolatingAnimation
{
    protected ExtendedGeneralPath path;
    protected PathLength pathLength;
    protected float[] keyPoints;
    protected boolean rotateAuto;
    protected boolean rotateAutoReverse;
    protected float rotateAngle;
    
    public MotionAnimation(final TimedElement timedElement, final AnimatableElement animatableElement, final int calcMode, final float[] keyTimes, final float[] keySplines, final boolean additive, final boolean cumulative, final AnimatableValue[] values, final AnimatableValue from, final AnimatableValue to, final AnimatableValue by, ExtendedGeneralPath path, float[] keyPoints, final boolean rotateAuto, final boolean rotateAutoReverse, final float rotateAngle, final short rotateAngleUnit) {
        super(timedElement, animatableElement, calcMode, keyTimes, keySplines, additive, cumulative);
        this.rotateAuto = rotateAuto;
        this.rotateAutoReverse = rotateAutoReverse;
        this.rotateAngle = AnimatableAngleValue.rad(rotateAngle, rotateAngleUnit);
        if (path == null) {
            path = new ExtendedGeneralPath();
            if (values == null || values.length == 0) {
                if (from != null) {
                    final AnimatableMotionPointValue fromPt = (AnimatableMotionPointValue)from;
                    final float x = fromPt.getX();
                    final float y = fromPt.getY();
                    path.moveTo(x, y);
                    if (to != null) {
                        final AnimatableMotionPointValue toPt = (AnimatableMotionPointValue)to;
                        path.lineTo(toPt.getX(), toPt.getY());
                    }
                    else {
                        if (by == null) {
                            throw timedElement.createException("values.to.by.path.missing", new Object[] { null });
                        }
                        final AnimatableMotionPointValue byPt = (AnimatableMotionPointValue)by;
                        path.lineTo(x + byPt.getX(), y + byPt.getY());
                    }
                }
                else if (to != null) {
                    final AnimatableMotionPointValue unPt = (AnimatableMotionPointValue)animatableElement.getUnderlyingValue();
                    final AnimatableMotionPointValue toPt2 = (AnimatableMotionPointValue)to;
                    path.moveTo(unPt.getX(), unPt.getY());
                    path.lineTo(toPt2.getX(), toPt2.getY());
                    this.cumulative = false;
                }
                else {
                    if (by == null) {
                        throw timedElement.createException("values.to.by.path.missing", new Object[] { null });
                    }
                    final AnimatableMotionPointValue byPt2 = (AnimatableMotionPointValue)by;
                    path.moveTo(0.0f, 0.0f);
                    path.lineTo(byPt2.getX(), byPt2.getY());
                    this.additive = true;
                }
            }
            else {
                AnimatableMotionPointValue pt = (AnimatableMotionPointValue)values[0];
                path.moveTo(pt.getX(), pt.getY());
                for (int i = 1; i < values.length; ++i) {
                    pt = (AnimatableMotionPointValue)values[i];
                    path.lineTo(pt.getX(), pt.getY());
                }
            }
        }
        this.path = path;
        this.pathLength = new PathLength(path);
        int segments = 0;
        ExtendedPathIterator epi = path.getExtendedPathIterator();
        while (!epi.isDone()) {
            final int type = epi.currentSegment();
            if (type != 0) {
                ++segments;
            }
            epi.next();
        }
        final int count = (keyPoints == null) ? (segments + 1) : keyPoints.length;
        final float totalLength = this.pathLength.lengthOfPath();
        if (this.keyTimes != null && calcMode != 2) {
            if (this.keyTimes.length != count) {
                throw timedElement.createException("attribute.malformed", new Object[] { null, "keyTimes" });
            }
        }
        else if (calcMode == 1 || calcMode == 3) {
            this.keyTimes = new float[count];
            for (int j = 0; j < count; ++j) {
                this.keyTimes[j] = j / (float)(count - 1);
            }
        }
        else if (calcMode == 0) {
            this.keyTimes = new float[count];
            for (int j = 0; j < count; ++j) {
                this.keyTimes[j] = j / (float)count;
            }
        }
        else {
            epi = path.getExtendedPathIterator();
            this.keyTimes = new float[count];
            int k = 0;
            for (int l = 0; l < count - 1; ++l) {
                while (epi.currentSegment() == 0) {
                    ++k;
                    epi.next();
                }
                this.keyTimes[l] = this.pathLength.getLengthAtSegment(k) / totalLength;
                ++k;
                epi.next();
            }
            this.keyTimes[count - 1] = 1.0f;
        }
        if (keyPoints != null) {
            if (keyPoints.length != this.keyTimes.length) {
                throw timedElement.createException("attribute.malformed", new Object[] { null, "keyPoints" });
            }
        }
        else {
            epi = path.getExtendedPathIterator();
            keyPoints = new float[count];
            int k = 0;
            for (int l = 0; l < count - 1; ++l) {
                while (epi.currentSegment() == 0) {
                    ++k;
                    epi.next();
                }
                keyPoints[l] = this.pathLength.getLengthAtSegment(k) / totalLength;
                ++k;
                epi.next();
            }
            keyPoints[count - 1] = 1.0f;
        }
        this.keyPoints = keyPoints;
    }
    
    @Override
    protected void sampledAtUnitTime(final float unitTime, final int repeatIteration) {
        float interpolation = 0.0f;
        AnimatableValue value;
        if (unitTime != 1.0f) {
            int keyTimeIndex;
            for (keyTimeIndex = 0; keyTimeIndex < this.keyTimes.length - 1 && unitTime >= this.keyTimes[keyTimeIndex + 1]; ++keyTimeIndex) {}
            if (keyTimeIndex == this.keyTimes.length - 1 && this.calcMode == 0) {
                keyTimeIndex = this.keyTimes.length - 2;
                interpolation = 1.0f;
            }
            else if (this.calcMode == 1 || this.calcMode == 2 || this.calcMode == 3) {
                if (unitTime == 0.0f) {
                    interpolation = 0.0f;
                }
                else {
                    interpolation = (unitTime - this.keyTimes[keyTimeIndex]) / (this.keyTimes[keyTimeIndex + 1] - this.keyTimes[keyTimeIndex]);
                }
                if (this.calcMode == 3 && unitTime != 0.0f) {
                    final Cubic c = this.keySplineCubics[keyTimeIndex];
                    final float tolerance = 0.001f;
                    float min = 0.0f;
                    float max = 1.0f;
                    Point2D.Double p;
                    while (true) {
                        final float t = (min + max) / 2.0f;
                        p = c.eval(t);
                        final double x = p.getX();
                        if (Math.abs(x - interpolation) < tolerance) {
                            break;
                        }
                        if (x < interpolation) {
                            min = t;
                        }
                        else {
                            max = t;
                        }
                    }
                    interpolation = (float)p.getY();
                }
            }
            float point = this.keyPoints[keyTimeIndex];
            if (interpolation != 0.0f) {
                point += interpolation * (this.keyPoints[keyTimeIndex + 1] - this.keyPoints[keyTimeIndex]);
            }
            point *= this.pathLength.lengthOfPath();
            final Point2D p2 = this.pathLength.pointAtLength(point);
            float ang;
            if (this.rotateAuto) {
                ang = this.pathLength.angleAtLength(point);
                if (this.rotateAutoReverse) {
                    ang += (float)3.141592653589793;
                }
            }
            else {
                ang = this.rotateAngle;
            }
            value = new AnimatableMotionPointValue(null, (float)p2.getX(), (float)p2.getY(), ang);
        }
        else {
            final Point2D p3 = this.pathLength.pointAtLength(this.pathLength.lengthOfPath());
            float ang2;
            if (this.rotateAuto) {
                ang2 = this.pathLength.angleAtLength(this.pathLength.lengthOfPath());
                if (this.rotateAutoReverse) {
                    ang2 += (float)3.141592653589793;
                }
            }
            else {
                ang2 = this.rotateAngle;
            }
            value = new AnimatableMotionPointValue(null, (float)p3.getX(), (float)p3.getY(), ang2);
        }
        AnimatableValue accumulation;
        if (this.cumulative) {
            final Point2D p3 = this.pathLength.pointAtLength(this.pathLength.lengthOfPath());
            float ang2;
            if (this.rotateAuto) {
                ang2 = this.pathLength.angleAtLength(this.pathLength.lengthOfPath());
                if (this.rotateAutoReverse) {
                    ang2 += (float)3.141592653589793;
                }
            }
            else {
                ang2 = this.rotateAngle;
            }
            accumulation = new AnimatableMotionPointValue(null, (float)p3.getX(), (float)p3.getY(), ang2);
        }
        else {
            accumulation = null;
        }
        this.value = value.interpolate(this.value, null, interpolation, accumulation, repeatIteration);
        if (this.value.hasChanged()) {
            this.markDirty();
        }
    }
}
