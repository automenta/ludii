// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import java.util.Iterator;
import org.w3c.dom.svg.SVGMatrix;
import java.util.Collection;
import java.util.List;
import org.apache.batik.dom.svg.AbstractSVGTransform;
import org.apache.batik.anim.dom.AnimationTarget;
import java.util.Vector;
import org.apache.batik.dom.svg.SVGOMTransform;

public class AnimatableTransformListValue extends AnimatableValue
{
    protected static SVGOMTransform IDENTITY_SKEWX;
    protected static SVGOMTransform IDENTITY_SKEWY;
    protected static SVGOMTransform IDENTITY_SCALE;
    protected static SVGOMTransform IDENTITY_ROTATE;
    protected static SVGOMTransform IDENTITY_TRANSLATE;
    protected Vector transforms;
    
    protected AnimatableTransformListValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatableTransformListValue(final AnimationTarget target, final AbstractSVGTransform t) {
        super(target);
        (this.transforms = new Vector()).add(t);
    }
    
    public AnimatableTransformListValue(final AnimationTarget target, final List transforms) {
        super(target);
        this.transforms = new Vector(transforms);
    }
    
    @Override
    public AnimatableValue interpolate(final AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        final AnimatableTransformListValue toTransformList = (AnimatableTransformListValue)to;
        final AnimatableTransformListValue accTransformList = (AnimatableTransformListValue)accumulation;
        final int accSize = (accumulation == null) ? 0 : accTransformList.transforms.size();
        final int newSize = this.transforms.size() + accSize * multiplier;
        AnimatableTransformListValue res;
        if (result == null) {
            res = new AnimatableTransformListValue(this.target);
            (res.transforms = new Vector(newSize)).setSize(newSize);
        }
        else {
            res = (AnimatableTransformListValue)result;
            if (res.transforms == null) {
                (res.transforms = new Vector(newSize)).setSize(newSize);
            }
            else if (res.transforms.size() != newSize) {
                res.transforms.setSize(newSize);
            }
        }
        int index = 0;
        for (int j = 0; j < multiplier; ++j) {
            for (int i = 0; i < accSize; ++i, ++index) {
                res.transforms.setElementAt(accTransformList.transforms.elementAt(i), index);
            }
        }
        for (int k = 0; k < this.transforms.size() - 1; ++k, ++index) {
            res.transforms.setElementAt(this.transforms.elementAt(k), index);
        }
        if (to != null) {
            final AbstractSVGTransform tt = toTransformList.transforms.lastElement();
            AbstractSVGTransform ft = null;
            int type;
            if (this.transforms.isEmpty()) {
                type = tt.getType();
                switch (type) {
                    case 5: {
                        ft = AnimatableTransformListValue.IDENTITY_SKEWX;
                        break;
                    }
                    case 6: {
                        ft = AnimatableTransformListValue.IDENTITY_SKEWY;
                        break;
                    }
                    case 3: {
                        ft = AnimatableTransformListValue.IDENTITY_SCALE;
                        break;
                    }
                    case 4: {
                        ft = AnimatableTransformListValue.IDENTITY_ROTATE;
                        break;
                    }
                    case 2: {
                        ft = AnimatableTransformListValue.IDENTITY_TRANSLATE;
                        break;
                    }
                }
            }
            else {
                ft = this.transforms.lastElement();
                type = ft.getType();
            }
            if (type == tt.getType()) {
                AbstractSVGTransform t;
                if (res.transforms.isEmpty()) {
                    t = new SVGOMTransform();
                    res.transforms.add(t);
                }
                else {
                    t = res.transforms.elementAt(index);
                    if (t == null) {
                        t = new SVGOMTransform();
                        res.transforms.setElementAt(t, index);
                    }
                }
                float r = 0.0f;
                switch (type) {
                    case 5:
                    case 6: {
                        r = ft.getAngle();
                        r += interpolation * (tt.getAngle() - r);
                        if (type == 5) {
                            t.setSkewX(r);
                            break;
                        }
                        if (type == 6) {
                            t.setSkewY(r);
                            break;
                        }
                        break;
                    }
                    case 3: {
                        final SVGMatrix fm = ft.getMatrix();
                        final SVGMatrix tm = tt.getMatrix();
                        float x = fm.getA();
                        float y = fm.getD();
                        x += interpolation * (tm.getA() - x);
                        y += interpolation * (tm.getD() - y);
                        t.setScale(x, y);
                        break;
                    }
                    case 4: {
                        float x = ft.getX();
                        float y = ft.getY();
                        x += interpolation * (tt.getX() - x);
                        y += interpolation * (tt.getY() - y);
                        r = ft.getAngle();
                        r += interpolation * (tt.getAngle() - r);
                        t.setRotate(r, x, y);
                        break;
                    }
                    case 2: {
                        final SVGMatrix fm = ft.getMatrix();
                        final SVGMatrix tm = tt.getMatrix();
                        float x = fm.getE();
                        float y = fm.getF();
                        x += interpolation * (tm.getE() - x);
                        y += interpolation * (tm.getF() - y);
                        t.setTranslate(x, y);
                        break;
                    }
                }
            }
        }
        else {
            final AbstractSVGTransform ft2 = this.transforms.lastElement();
            AbstractSVGTransform t2 = res.transforms.elementAt(index);
            if (t2 == null) {
                t2 = new SVGOMTransform();
                res.transforms.setElementAt(t2, index);
            }
            t2.assign(ft2);
        }
        res.hasChanged = true;
        return res;
    }
    
    public static AnimatableTransformListValue interpolate(AnimatableTransformListValue res, final AnimatableTransformListValue value1, final AnimatableTransformListValue value2, final AnimatableTransformListValue to1, final AnimatableTransformListValue to2, final float interpolation1, final float interpolation2, final AnimatableTransformListValue accumulation, final int multiplier) {
        final int accSize = (accumulation == null) ? 0 : accumulation.transforms.size();
        final int newSize = accSize * multiplier + 1;
        if (res == null) {
            res = new AnimatableTransformListValue(to1.target);
            (res.transforms = new Vector(newSize)).setSize(newSize);
        }
        else if (res.transforms == null) {
            (res.transforms = new Vector(newSize)).setSize(newSize);
        }
        else if (res.transforms.size() != newSize) {
            res.transforms.setSize(newSize);
        }
        int index = 0;
        for (int j = 0; j < multiplier; ++j) {
            for (int i = 0; i < accSize; ++i, ++index) {
                res.transforms.setElementAt(accumulation.transforms.elementAt(i), index);
            }
        }
        final AbstractSVGTransform ft1 = value1.transforms.lastElement();
        final AbstractSVGTransform ft2 = value2.transforms.lastElement();
        AbstractSVGTransform t = res.transforms.elementAt(index);
        if (t == null) {
            t = new SVGOMTransform();
            res.transforms.setElementAt(t, index);
        }
        final int type = ft1.getType();
        float x;
        float y;
        if (type == 3) {
            x = ft1.getMatrix().getA();
            y = ft2.getMatrix().getD();
        }
        else {
            x = ft1.getMatrix().getE();
            y = ft2.getMatrix().getF();
        }
        if (to1 != null) {
            final AbstractSVGTransform tt1 = to1.transforms.lastElement();
            final AbstractSVGTransform tt2 = to2.transforms.lastElement();
            if (type == 3) {
                x += interpolation1 * (tt1.getMatrix().getA() - x);
                y += interpolation2 * (tt2.getMatrix().getD() - y);
            }
            else {
                x += interpolation1 * (tt1.getMatrix().getE() - x);
                y += interpolation2 * (tt2.getMatrix().getF() - y);
            }
        }
        if (type == 3) {
            t.setScale(x, y);
        }
        else {
            t.setTranslate(x, y);
        }
        res.hasChanged = true;
        return res;
    }
    
    public static AnimatableTransformListValue interpolate(AnimatableTransformListValue res, final AnimatableTransformListValue value1, final AnimatableTransformListValue value2, final AnimatableTransformListValue value3, final AnimatableTransformListValue to1, final AnimatableTransformListValue to2, final AnimatableTransformListValue to3, final float interpolation1, final float interpolation2, final float interpolation3, final AnimatableTransformListValue accumulation, final int multiplier) {
        final int accSize = (accumulation == null) ? 0 : accumulation.transforms.size();
        final int newSize = accSize * multiplier + 1;
        if (res == null) {
            res = new AnimatableTransformListValue(to1.target);
            (res.transforms = new Vector(newSize)).setSize(newSize);
        }
        else if (res.transforms == null) {
            (res.transforms = new Vector(newSize)).setSize(newSize);
        }
        else if (res.transforms.size() != newSize) {
            res.transforms.setSize(newSize);
        }
        int index = 0;
        for (int j = 0; j < multiplier; ++j) {
            for (int i = 0; i < accSize; ++i, ++index) {
                res.transforms.setElementAt(accumulation.transforms.elementAt(i), index);
            }
        }
        final AbstractSVGTransform ft1 = value1.transforms.lastElement();
        final AbstractSVGTransform ft2 = value2.transforms.lastElement();
        final AbstractSVGTransform ft3 = value3.transforms.lastElement();
        AbstractSVGTransform t = res.transforms.elementAt(index);
        if (t == null) {
            t = new SVGOMTransform();
            res.transforms.setElementAt(t, index);
        }
        float r = ft1.getAngle();
        float x = ft2.getX();
        float y = ft3.getY();
        if (to1 != null) {
            final AbstractSVGTransform tt1 = to1.transforms.lastElement();
            final AbstractSVGTransform tt2 = to2.transforms.lastElement();
            final AbstractSVGTransform tt3 = to3.transforms.lastElement();
            r += interpolation1 * (tt1.getAngle() - r);
            x += interpolation2 * (tt2.getX() - x);
            y += interpolation3 * (tt3.getY() - y);
        }
        t.setRotate(r, x, y);
        res.hasChanged = true;
        return res;
    }
    
    public Iterator getTransforms() {
        return this.transforms.iterator();
    }
    
    @Override
    public boolean canPace() {
        return true;
    }
    
    @Override
    public float distanceTo(final AnimatableValue other) {
        final AnimatableTransformListValue o = (AnimatableTransformListValue)other;
        if (this.transforms.isEmpty() || o.transforms.isEmpty()) {
            return 0.0f;
        }
        final AbstractSVGTransform t1 = this.transforms.lastElement();
        final AbstractSVGTransform t2 = o.transforms.lastElement();
        final short type1 = t1.getType();
        if (type1 != t2.getType()) {
            return 0.0f;
        }
        final SVGMatrix m1 = t1.getMatrix();
        final SVGMatrix m2 = t2.getMatrix();
        switch (type1) {
            case 2: {
                return Math.abs(m1.getE() - m2.getE()) + Math.abs(m1.getF() - m2.getF());
            }
            case 3: {
                return Math.abs(m1.getA() - m2.getA()) + Math.abs(m1.getD() - m2.getD());
            }
            case 4:
            case 5:
            case 6: {
                return Math.abs(t1.getAngle() - t2.getAngle());
            }
            default: {
                return 0.0f;
            }
        }
    }
    
    public float distanceTo1(final AnimatableValue other) {
        final AnimatableTransformListValue o = (AnimatableTransformListValue)other;
        if (this.transforms.isEmpty() || o.transforms.isEmpty()) {
            return 0.0f;
        }
        final AbstractSVGTransform t1 = this.transforms.lastElement();
        final AbstractSVGTransform t2 = o.transforms.lastElement();
        final short type1 = t1.getType();
        if (type1 != t2.getType()) {
            return 0.0f;
        }
        final SVGMatrix m1 = t1.getMatrix();
        final SVGMatrix m2 = t2.getMatrix();
        switch (type1) {
            case 2: {
                return Math.abs(m1.getE() - m2.getE());
            }
            case 3: {
                return Math.abs(m1.getA() - m2.getA());
            }
            case 4:
            case 5:
            case 6: {
                return Math.abs(t1.getAngle() - t2.getAngle());
            }
            default: {
                return 0.0f;
            }
        }
    }
    
    public float distanceTo2(final AnimatableValue other) {
        final AnimatableTransformListValue o = (AnimatableTransformListValue)other;
        if (this.transforms.isEmpty() || o.transforms.isEmpty()) {
            return 0.0f;
        }
        final AbstractSVGTransform t1 = this.transforms.lastElement();
        final AbstractSVGTransform t2 = o.transforms.lastElement();
        final short type1 = t1.getType();
        if (type1 != t2.getType()) {
            return 0.0f;
        }
        final SVGMatrix m1 = t1.getMatrix();
        final SVGMatrix m2 = t2.getMatrix();
        switch (type1) {
            case 2: {
                return Math.abs(m1.getF() - m2.getF());
            }
            case 3: {
                return Math.abs(m1.getD() - m2.getD());
            }
            case 4: {
                return Math.abs(t1.getX() - t2.getX());
            }
            default: {
                return 0.0f;
            }
        }
    }
    
    public float distanceTo3(final AnimatableValue other) {
        final AnimatableTransformListValue o = (AnimatableTransformListValue)other;
        if (this.transforms.isEmpty() || o.transforms.isEmpty()) {
            return 0.0f;
        }
        final AbstractSVGTransform t1 = this.transforms.lastElement();
        final AbstractSVGTransform t2 = o.transforms.lastElement();
        final short type1 = t1.getType();
        if (type1 != t2.getType()) {
            return 0.0f;
        }
        if (type1 == 4) {
            return Math.abs(t1.getY() - t2.getY());
        }
        return 0.0f;
    }
    
    @Override
    public AnimatableValue getZeroValue() {
        return new AnimatableTransformListValue(this.target, new Vector(5));
    }
    
    @Override
    public String toStringRep() {
        final StringBuffer sb = new StringBuffer();
        final Iterator i = this.transforms.iterator();
        while (i.hasNext()) {
            final AbstractSVGTransform t = i.next();
            if (t == null) {
                sb.append("null");
            }
            else {
                final SVGMatrix m = t.getMatrix();
                switch (t.getType()) {
                    case 2: {
                        sb.append("translate(");
                        sb.append(m.getE());
                        sb.append(',');
                        sb.append(m.getF());
                        sb.append(')');
                        break;
                    }
                    case 3: {
                        sb.append("scale(");
                        sb.append(m.getA());
                        sb.append(',');
                        sb.append(m.getD());
                        sb.append(')');
                        break;
                    }
                    case 5: {
                        sb.append("skewX(");
                        sb.append(t.getAngle());
                        sb.append(')');
                        break;
                    }
                    case 6: {
                        sb.append("skewY(");
                        sb.append(t.getAngle());
                        sb.append(')');
                        break;
                    }
                    case 4: {
                        sb.append("rotate(");
                        sb.append(t.getAngle());
                        sb.append(',');
                        sb.append(t.getX());
                        sb.append(',');
                        sb.append(t.getY());
                        sb.append(')');
                        break;
                    }
                }
            }
            if (i.hasNext()) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }
    
    static {
        AnimatableTransformListValue.IDENTITY_SKEWX = new SVGOMTransform();
        AnimatableTransformListValue.IDENTITY_SKEWY = new SVGOMTransform();
        AnimatableTransformListValue.IDENTITY_SCALE = new SVGOMTransform();
        AnimatableTransformListValue.IDENTITY_ROTATE = new SVGOMTransform();
        AnimatableTransformListValue.IDENTITY_TRANSLATE = new SVGOMTransform();
        AnimatableTransformListValue.IDENTITY_SKEWX.setSkewX(0.0f);
        AnimatableTransformListValue.IDENTITY_SKEWY.setSkewY(0.0f);
        AnimatableTransformListValue.IDENTITY_SCALE.setScale(0.0f, 0.0f);
        AnimatableTransformListValue.IDENTITY_ROTATE.setRotate(0.0f, 0.0f, 0.0f);
        AnimatableTransformListValue.IDENTITY_TRANSLATE.setTranslate(0.0f, 0.0f);
    }
}
