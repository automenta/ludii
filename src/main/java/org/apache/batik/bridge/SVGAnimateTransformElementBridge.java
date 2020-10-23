// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.ArrayList;
import org.apache.batik.dom.svg.AbstractSVGTransform;
import org.apache.batik.anim.values.AnimatableTransformListValue;
import org.apache.batik.dom.svg.SVGOMTransform;
import org.w3c.dom.Element;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.TransformAnimation;
import org.apache.batik.anim.AbstractAnimation;
import org.apache.batik.anim.dom.AnimationTarget;

public class SVGAnimateTransformElementBridge extends SVGAnimateElementBridge
{
    @Override
    public String getLocalName() {
        return "animateTransform";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGAnimateTransformElementBridge();
    }
    
    @Override
    protected AbstractAnimation createAnimation(final AnimationTarget target) {
        final short type = this.parseType();
        AnimatableValue from = null;
        AnimatableValue to = null;
        AnimatableValue by = null;
        if (this.element.hasAttributeNS(null, "from")) {
            from = this.parseValue(this.element.getAttributeNS(null, "from"), type, target);
        }
        if (this.element.hasAttributeNS(null, "to")) {
            to = this.parseValue(this.element.getAttributeNS(null, "to"), type, target);
        }
        if (this.element.hasAttributeNS(null, "by")) {
            by = this.parseValue(this.element.getAttributeNS(null, "by"), type, target);
        }
        return new TransformAnimation(this.timedElement, this, this.parseCalcMode(), this.parseKeyTimes(), this.parseKeySplines(), this.parseAdditive(), this.parseAccumulate(), this.parseValues(type, target), from, to, by, type);
    }
    
    protected short parseType() {
        final String typeString = this.element.getAttributeNS(null, "type");
        if (typeString.equals("translate")) {
            return 2;
        }
        if (typeString.equals("scale")) {
            return 3;
        }
        if (typeString.equals("rotate")) {
            return 4;
        }
        if (typeString.equals("skewX")) {
            return 5;
        }
        if (typeString.equals("skewY")) {
            return 6;
        }
        throw new BridgeException(this.ctx, this.element, "attribute.malformed", new Object[] { "type", typeString });
    }
    
    protected AnimatableValue parseValue(final String s, final short type, final AnimationTarget target) {
        float val2 = 0.0f;
        float val3 = 0.0f;
        int i = 0;
        char c = ',';
        int len;
        for (len = s.length(); i < len; ++i) {
            c = s.charAt(i);
            if (c == ' ') {
                break;
            }
            if (c == ',') {
                break;
            }
        }
        final float val4 = Float.parseFloat(s.substring(0, i));
        if (i < len) {
            ++i;
        }
        int count = 1;
        if (i < len && c == ' ') {
            while (i < len) {
                c = s.charAt(i);
                if (c != ' ') {
                    break;
                }
                ++i;
            }
            if (c == ',') {
                ++i;
            }
        }
        while (i < len && s.charAt(i) == ' ') {
            ++i;
        }
        int j;
        if ((j = i) < len && type != 5 && type != 6) {
            while (i < len) {
                c = s.charAt(i);
                if (c == ' ') {
                    break;
                }
                if (c == ',') {
                    break;
                }
                ++i;
            }
            val2 = Float.parseFloat(s.substring(j, i));
            if (i < len) {
                ++i;
            }
            ++count;
            if (i < len && c == ' ') {
                while (i < len) {
                    c = s.charAt(i);
                    if (c != ' ') {
                        break;
                    }
                    ++i;
                }
                if (c == ',') {
                    ++i;
                }
            }
            while (i < len && s.charAt(i) == ' ') {
                ++i;
            }
            if ((j = i) < len && type == 4) {
                while (i < len) {
                    c = s.charAt(i);
                    if (c == ',') {
                        break;
                    }
                    if (c == ' ') {
                        break;
                    }
                    ++i;
                }
                val3 = Float.parseFloat(s.substring(j, i));
                if (i < len) {
                    ++i;
                }
                ++count;
                while (i < len && s.charAt(i) == ' ') {
                    ++i;
                }
            }
        }
        if (i != len) {
            return null;
        }
        final SVGOMTransform t = new SVGOMTransform();
        switch (type) {
            case 2: {
                if (count == 2) {
                    t.setTranslate(val4, val2);
                    break;
                }
                t.setTranslate(val4, 0.0f);
                break;
            }
            case 3: {
                if (count == 2) {
                    t.setScale(val4, val2);
                    break;
                }
                t.setScale(val4, val4);
                break;
            }
            case 4: {
                if (count == 3) {
                    t.setRotate(val4, val2, val3);
                    break;
                }
                t.setRotate(val4, 0.0f, 0.0f);
                break;
            }
            case 5: {
                t.setSkewX(val4);
                break;
            }
            case 6: {
                t.setSkewY(val4);
                break;
            }
        }
        return new AnimatableTransformListValue(target, t);
    }
    
    protected AnimatableValue[] parseValues(final short type, final AnimationTarget target) {
        final String valuesString = this.element.getAttributeNS(null, "values");
        final int len = valuesString.length();
        if (len == 0) {
            return null;
        }
        final ArrayList values = new ArrayList(7);
        int i = 0;
        int start = 0;
    Label_0199:
        while (i < len) {
            while (valuesString.charAt(i) == ' ') {
                if (++i == len) {
                    break Label_0199;
                }
            }
            start = i++;
            if (i < len) {
                for (char c = valuesString.charAt(i); c != ';'; c = valuesString.charAt(i)) {
                    if (++i == len) {
                        break;
                    }
                }
            }
            final int end = i++;
            final String valueString = valuesString.substring(start, end);
            final AnimatableValue value = this.parseValue(valueString, type, target);
            if (value == null) {
                throw new BridgeException(this.ctx, this.element, "attribute.malformed", new Object[] { "values", valuesString });
            }
            values.add(value);
        }
        final AnimatableValue[] ret = new AnimatableValue[values.size()];
        return values.toArray(ret);
    }
    
    @Override
    protected boolean canAnimateType(final int type) {
        return type == 9;
    }
}
