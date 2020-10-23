// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.SimpleAnimation;
import org.apache.batik.anim.AbstractAnimation;
import org.apache.batik.anim.dom.AnimationTarget;

public class SVGAnimateElementBridge extends SVGAnimationElementBridge
{
    @Override
    public String getLocalName() {
        return "animate";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGAnimateElementBridge();
    }
    
    @Override
    protected AbstractAnimation createAnimation(final AnimationTarget target) {
        final AnimatableValue from = this.parseAnimatableValue("from");
        final AnimatableValue to = this.parseAnimatableValue("to");
        final AnimatableValue by = this.parseAnimatableValue("by");
        return new SimpleAnimation(this.timedElement, this, this.parseCalcMode(), this.parseKeyTimes(), this.parseKeySplines(), this.parseAdditive(), this.parseAccumulate(), this.parseValues(), from, to, by);
    }
    
    protected int parseCalcMode() {
        if ((this.animationType == 1 && !this.targetElement.isPropertyAdditive(this.attributeLocalName)) || (this.animationType == 0 && !this.targetElement.isAttributeAdditive(this.attributeNamespaceURI, this.attributeLocalName))) {
            return 0;
        }
        final String calcModeString = this.element.getAttributeNS(null, "calcMode");
        if (calcModeString.length() == 0) {
            return this.getDefaultCalcMode();
        }
        if (calcModeString.equals("linear")) {
            return 1;
        }
        if (calcModeString.equals("discrete")) {
            return 0;
        }
        if (calcModeString.equals("paced")) {
            return 2;
        }
        if (calcModeString.equals("spline")) {
            return 3;
        }
        throw new BridgeException(this.ctx, this.element, "attribute.malformed", new Object[] { "calcMode", calcModeString });
    }
    
    protected boolean parseAdditive() {
        final String additiveString = this.element.getAttributeNS(null, "additive");
        if (additiveString.length() == 0 || additiveString.equals("replace")) {
            return false;
        }
        if (additiveString.equals("sum")) {
            return true;
        }
        throw new BridgeException(this.ctx, this.element, "attribute.malformed", new Object[] { "additive", additiveString });
    }
    
    protected boolean parseAccumulate() {
        final String accumulateString = this.element.getAttributeNS(null, "accumulate");
        if (accumulateString.length() == 0 || accumulateString.equals("none")) {
            return false;
        }
        if (accumulateString.equals("sum")) {
            return true;
        }
        throw new BridgeException(this.ctx, this.element, "attribute.malformed", new Object[] { "accumulate", accumulateString });
    }
    
    protected AnimatableValue[] parseValues() {
        final boolean isCSS = this.animationType == 1;
        final String valuesString = this.element.getAttributeNS(null, "values");
        final int len = valuesString.length();
        if (len == 0) {
            return null;
        }
        final ArrayList values = new ArrayList(7);
        int i = 0;
        int start = 0;
    Label_0225:
        while (i < len) {
            while (valuesString.charAt(i) == ' ') {
                if (++i == len) {
                    break Label_0225;
                }
            }
            start = i++;
            if (i != len) {
                for (char c = valuesString.charAt(i); c != ';'; c = valuesString.charAt(i)) {
                    if (++i == len) {
                        break;
                    }
                }
            }
            final int end = i++;
            final AnimatableValue val = this.eng.parseAnimatableValue(this.element, this.animationTarget, this.attributeNamespaceURI, this.attributeLocalName, isCSS, valuesString.substring(start, end));
            if (!this.checkValueType(val)) {
                throw new BridgeException(this.ctx, this.element, "attribute.malformed", new Object[] { "values", valuesString });
            }
            values.add(val);
        }
        final AnimatableValue[] ret = new AnimatableValue[values.size()];
        return values.toArray(ret);
    }
    
    protected float[] parseKeyTimes() {
        final String keyTimesString = this.element.getAttributeNS(null, "keyTimes");
        int len = keyTimesString.length();
        if (len == 0) {
            return null;
        }
        final ArrayList keyTimes = new ArrayList(7);
        int i = 0;
        int start = 0;
    Label_0193:
        while (i < len) {
            while (keyTimesString.charAt(i) == ' ') {
                if (++i == len) {
                    break Label_0193;
                }
            }
            start = i++;
            if (i != len) {
                for (char c = keyTimesString.charAt(i); c != ' ' && c != ';'; c = keyTimesString.charAt(i)) {
                    if (++i == len) {
                        break;
                    }
                }
            }
            final int end = i++;
            try {
                final float keyTime = Float.parseFloat(keyTimesString.substring(start, end));
                keyTimes.add(keyTime);
                continue;
            }
            catch (NumberFormatException nfEx) {
                throw new BridgeException(this.ctx, this.element, nfEx, "attribute.malformed", new Object[] { "keyTimes", keyTimesString });
            }
            break;
        }
        len = keyTimes.size();
        final float[] ret = new float[len];
        for (int j = 0; j < len; ++j) {
            ret[j] = keyTimes.get(j);
        }
        return ret;
    }
    
    protected float[] parseKeySplines() {
        final String keySplinesString = this.element.getAttributeNS(null, "keySplines");
        int len = keySplinesString.length();
        if (len == 0) {
            return null;
        }
        final List keySplines = new ArrayList(7);
        int count = 0;
        int i = 0;
        int start = 0;
    Label_0319:
        while (i < len) {
            while (keySplinesString.charAt(i) == ' ') {
                if (++i == len) {
                    break Label_0319;
                }
            }
            start = i++;
            int end;
            if (i != len) {
                char c;
                for (c = keySplinesString.charAt(i); c != ' ' && c != ',' && c != ';' && ++i != len; c = keySplinesString.charAt(i)) {}
                end = i++;
                Label_0193: {
                    if (c == ' ') {
                        while (true) {
                            while (i != len) {
                                c = keySplinesString.charAt(i++);
                                if (c != ' ') {
                                    if (c != ';' && c != ',') {
                                        --i;
                                    }
                                    break Label_0193;
                                }
                            }
                            continue;
                        }
                    }
                }
                if (c == ';') {
                    if (count != 3) {
                        throw new BridgeException(this.ctx, this.element, "attribute.malformed", new Object[] { "keySplines", keySplinesString });
                    }
                    count = 0;
                }
                else {
                    ++count;
                }
            }
            else {
                end = i++;
            }
            try {
                final float keySplineValue = Float.parseFloat(keySplinesString.substring(start, end));
                keySplines.add(keySplineValue);
                continue;
            }
            catch (NumberFormatException nfEx) {
                throw new BridgeException(this.ctx, this.element, nfEx, "attribute.malformed", new Object[] { "keySplines", keySplinesString });
            }
            break;
        }
        len = keySplines.size();
        final float[] ret = new float[len];
        for (int j = 0; j < len; ++j) {
            ret[j] = keySplines.get(j);
        }
        return ret;
    }
    
    protected int getDefaultCalcMode() {
        return 1;
    }
    
    @Override
    protected boolean canAnimateType(final int type) {
        return true;
    }
}
