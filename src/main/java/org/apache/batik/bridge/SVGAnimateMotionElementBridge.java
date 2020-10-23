// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.values.AnimatableMotionPointValue;
import org.apache.batik.parser.LengthListHandler;
import org.apache.batik.parser.LengthArrayProducer;
import org.apache.batik.parser.LengthPairListParser;
import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.apache.batik.parser.PathParser;
import org.apache.batik.parser.PathHandler;
import org.apache.batik.dom.svg.SVGAnimatedPathDataSupport;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.anim.dom.SVGOMPathElement;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.MotionAnimation;
import org.w3c.dom.Element;
import org.apache.batik.parser.AngleParser;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.AngleHandler;
import org.apache.batik.anim.AbstractAnimation;
import org.apache.batik.anim.dom.AnimationTarget;

public class SVGAnimateMotionElementBridge extends SVGAnimateElementBridge
{
    @Override
    public String getLocalName() {
        return "animateMotion";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGAnimateMotionElementBridge();
    }
    
    @Override
    protected AbstractAnimation createAnimation(final AnimationTarget target) {
        this.animationType = 2;
        this.attributeLocalName = "motion";
        final AnimatableValue from = this.parseLengthPair("from");
        final AnimatableValue to = this.parseLengthPair("to");
        final AnimatableValue by = this.parseLengthPair("by");
        boolean rotateAuto = false;
        boolean rotateAutoReverse = false;
        float rotateAngle = 0.0f;
        short rotateAngleUnit = 0;
        final String rotateString = this.element.getAttributeNS(null, "rotate");
        if (rotateString.length() != 0) {
            class Handler implements AngleHandler
            {
                float theAngle;
                short theUnit;
                
                Handler() {
                    this.theUnit = 1;
                }
                
                @Override
                public void startAngle() throws ParseException {
                }
                
                @Override
                public void angleValue(final float v) throws ParseException {
                    this.theAngle = v;
                }
                
                @Override
                public void deg() throws ParseException {
                    this.theUnit = 2;
                }
                
                @Override
                public void grad() throws ParseException {
                    this.theUnit = 4;
                }
                
                @Override
                public void rad() throws ParseException {
                    this.theUnit = 3;
                }
                
                @Override
                public void endAngle() throws ParseException {
                }
            }
            if (rotateString.equals("auto")) {
                rotateAuto = true;
            }
            else if (rotateString.equals("auto-reverse")) {
                rotateAuto = true;
                rotateAutoReverse = true;
            }
            else {
                final AngleParser ap = new AngleParser();
                final Handler h = new Handler();
                ap.setAngleHandler(h);
                try {
                    ap.parse(rotateString);
                }
                catch (ParseException pEx) {
                    throw new BridgeException(this.ctx, this.element, pEx, "attribute.malformed", new Object[] { "rotate", rotateString });
                }
                rotateAngle = h.theAngle;
                rotateAngleUnit = h.theUnit;
            }
        }
        return new MotionAnimation(this.timedElement, this, this.parseCalcMode(), this.parseKeyTimes(), this.parseKeySplines(), this.parseAdditive(), this.parseAccumulate(), this.parseValues(), from, to, by, this.parsePath(), this.parseKeyPoints(), rotateAuto, rotateAutoReverse, rotateAngle, rotateAngleUnit);
    }
    
    protected ExtendedGeneralPath parsePath() {
        Node n = this.element.getFirstChild();
        while (n != null) {
            if (n.getNodeType() == 1 && "http://www.w3.org/2000/svg".equals(n.getNamespaceURI()) && "mpath".equals(n.getLocalName())) {
                final String uri = XLinkSupport.getXLinkHref((Element)n);
                final Element path = this.ctx.getReferencedElement(this.element, uri);
                if (!"http://www.w3.org/2000/svg".equals(path.getNamespaceURI()) || !"path".equals(path.getLocalName())) {
                    throw new BridgeException(this.ctx, this.element, "uri.badTarget", new Object[] { uri });
                }
                final SVGOMPathElement pathElt = (SVGOMPathElement)path;
                final AWTPathProducer app = new AWTPathProducer();
                SVGAnimatedPathDataSupport.handlePathSegList(pathElt.getPathSegList(), app);
                return (ExtendedGeneralPath)app.getShape();
            }
            else {
                n = n.getNextSibling();
            }
        }
        final String pathString = this.element.getAttributeNS(null, "path");
        if (pathString.length() == 0) {
            return null;
        }
        try {
            final AWTPathProducer app2 = new AWTPathProducer();
            final PathParser pp = new PathParser();
            pp.setPathHandler(app2);
            pp.parse(pathString);
            return (ExtendedGeneralPath)app2.getShape();
        }
        catch (ParseException pEx) {
            throw new BridgeException(this.ctx, this.element, pEx, "attribute.malformed", new Object[] { "path", pathString });
        }
    }
    
    protected float[] parseKeyPoints() {
        final String keyPointsString = this.element.getAttributeNS(null, "keyPoints");
        int len = keyPointsString.length();
        if (len == 0) {
            return null;
        }
        final List keyPoints = new ArrayList(7);
        int i = 0;
        int start = 0;
    Label_0202:
        while (i < len) {
            while (keyPointsString.charAt(i) == ' ') {
                if (++i == len) {
                    break Label_0202;
                }
            }
            start = i++;
            if (i != len) {
                for (char c = keyPointsString.charAt(i); c != ' ' && c != ';' && c != ','; c = keyPointsString.charAt(i)) {
                    if (++i == len) {
                        break;
                    }
                }
            }
            final int end = i++;
            try {
                final float keyPointCoord = Float.parseFloat(keyPointsString.substring(start, end));
                keyPoints.add(keyPointCoord);
                continue;
            }
            catch (NumberFormatException nfEx) {
                throw new BridgeException(this.ctx, this.element, nfEx, "attribute.malformed", new Object[] { "keyPoints", keyPointsString });
            }
            break;
        }
        len = keyPoints.size();
        final float[] ret = new float[len];
        for (int j = 0; j < len; ++j) {
            ret[j] = keyPoints.get(j);
        }
        return ret;
    }
    
    @Override
    protected int getDefaultCalcMode() {
        return 2;
    }
    
    @Override
    protected AnimatableValue[] parseValues() {
        final String valuesString = this.element.getAttributeNS(null, "values");
        final int len = valuesString.length();
        if (len == 0) {
            return null;
        }
        return this.parseValues(valuesString);
    }
    
    protected AnimatableValue[] parseValues(final String s) {
        try {
            final LengthPairListParser lplp = new LengthPairListParser();
            final LengthArrayProducer lap = new LengthArrayProducer();
            lplp.setLengthListHandler(lap);
            lplp.parse(s);
            final short[] types = lap.getLengthTypeArray();
            final float[] values = lap.getLengthValueArray();
            final AnimatableValue[] ret = new AnimatableValue[types.length / 2];
            for (int i = 0; i < types.length; i += 2) {
                final float x = this.animationTarget.svgToUserSpace(values[i], types[i], (short)1);
                final float y = this.animationTarget.svgToUserSpace(values[i + 1], types[i + 1], (short)2);
                ret[i / 2] = new AnimatableMotionPointValue(this.animationTarget, x, y, 0.0f);
            }
            return ret;
        }
        catch (ParseException pEx) {
            throw new BridgeException(this.ctx, this.element, pEx, "attribute.malformed", new Object[] { "values", s });
        }
    }
    
    protected AnimatableValue parseLengthPair(final String ln) {
        final String s = this.element.getAttributeNS(null, ln);
        if (s.length() == 0) {
            return null;
        }
        return this.parseValues(s)[0];
    }
    
    @Override
    public AnimatableValue getUnderlyingValue() {
        return new AnimatableMotionPointValue(this.animationTarget, 0.0f, 0.0f, 0.0f);
    }
    
    @Override
    protected void initializeAnimation() {
        final String uri = XLinkSupport.getXLinkHref(this.element);
        Node t;
        if (uri.length() == 0) {
            t = this.element.getParentNode();
        }
        else {
            t = this.ctx.getReferencedElement(this.element, uri);
            if (t.getOwnerDocument() != this.element.getOwnerDocument()) {
                throw new BridgeException(this.ctx, this.element, "uri.badTarget", new Object[] { uri });
            }
        }
        this.animationTarget = null;
        if (t instanceof SVGOMElement) {
            this.targetElement = (SVGOMElement)t;
            this.animationTarget = this.targetElement;
        }
        if (this.animationTarget == null) {
            throw new BridgeException(this.ctx, this.element, "uri.badTarget", new Object[] { uri });
        }
        this.timedElement = this.createTimedElement();
        this.animation = this.createAnimation(this.animationTarget);
        this.eng.addAnimation(this.animationTarget, (short)2, this.attributeNamespaceURI, this.attributeLocalName, this.animation);
    }
}
