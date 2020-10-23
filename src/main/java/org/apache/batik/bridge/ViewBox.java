// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.parser.AWTTransformProducer;
import java.util.StringTokenizer;
import org.w3c.dom.svg.SVGRect;
import org.apache.batik.anim.dom.SVGOMAnimatedRect;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGPreserveAspectRatio;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PreserveAspectRatioHandler;
import org.apache.batik.parser.PreserveAspectRatioParser;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.parser.FragmentIdentifierHandler;
import org.apache.batik.parser.FragmentIdentifierParser;
import java.awt.geom.AffineTransform;
import org.w3c.dom.Element;
import org.apache.batik.util.SVGConstants;

public abstract class ViewBox implements SVGConstants, ErrorConstants
{
    protected ViewBox() {
    }
    
    public static AffineTransform getViewTransform(final String ref, final Element e, final float w, final float h, final BridgeContext ctx) {
        if (ref == null || ref.length() == 0) {
            return getPreserveAspectRatioTransform(e, w, h, ctx);
        }
        final ViewHandler vh = new ViewHandler();
        final FragmentIdentifierParser p = new FragmentIdentifierParser();
        p.setFragmentIdentifierHandler(vh);
        p.parse(ref);
        Element viewElement = e;
        if (vh.hasId) {
            final Document document = e.getOwnerDocument();
            viewElement = document.getElementById(vh.id);
        }
        if (viewElement == null) {
            throw new BridgeException(ctx, e, "uri.malformed", new Object[] { ref });
        }
        final Element ancestorSVG = getClosestAncestorSVGElement(e);
        if (!viewElement.getNamespaceURI().equals("http://www.w3.org/2000/svg") || !viewElement.getLocalName().equals("view")) {
            viewElement = ancestorSVG;
        }
        float[] vb;
        if (vh.hasViewBox) {
            vb = vh.viewBox;
        }
        else {
            Element elt;
            if (DOMUtilities.isAttributeSpecifiedNS(viewElement, null, "viewBox")) {
                elt = viewElement;
            }
            else {
                elt = ancestorSVG;
            }
            final String viewBoxStr = elt.getAttributeNS(null, "viewBox");
            vb = parseViewBoxAttribute(elt, viewBoxStr, ctx);
        }
        short align;
        boolean meet;
        if (vh.hasPreserveAspectRatio) {
            align = vh.align;
            meet = vh.meet;
        }
        else {
            Element elt2;
            if (DOMUtilities.isAttributeSpecifiedNS(viewElement, null, "preserveAspectRatio")) {
                elt2 = viewElement;
            }
            else {
                elt2 = ancestorSVG;
            }
            final String aspectRatio = elt2.getAttributeNS(null, "preserveAspectRatio");
            final PreserveAspectRatioParser pp = new PreserveAspectRatioParser();
            final ViewHandler ph = new ViewHandler();
            pp.setPreserveAspectRatioHandler(ph);
            try {
                pp.parse(aspectRatio);
            }
            catch (ParseException pEx) {
                throw new BridgeException(ctx, elt2, pEx, "attribute.malformed", new Object[] { "preserveAspectRatio", aspectRatio, pEx });
            }
            align = ph.align;
            meet = ph.meet;
        }
        final AffineTransform transform = getPreserveAspectRatioTransform(vb, align, meet, w, h);
        if (vh.hasTransform) {
            transform.concatenate(vh.getAffineTransform());
        }
        return transform;
    }
    
    private static Element getClosestAncestorSVGElement(final Element e) {
        for (Node n = e; n != null && n.getNodeType() == 1; n = n.getParentNode()) {
            final Element tmp = (Element)n;
            if (tmp.getNamespaceURI().equals("http://www.w3.org/2000/svg") && tmp.getLocalName().equals("svg")) {
                return tmp;
            }
        }
        return null;
    }
    
    @Deprecated
    public static AffineTransform getPreserveAspectRatioTransform(final Element e, final float w, final float h) {
        return getPreserveAspectRatioTransform(e, w, h, null);
    }
    
    public static AffineTransform getPreserveAspectRatioTransform(final Element e, final float w, final float h, final BridgeContext ctx) {
        final String viewBox = e.getAttributeNS(null, "viewBox");
        final String aspectRatio = e.getAttributeNS(null, "preserveAspectRatio");
        return getPreserveAspectRatioTransform(e, viewBox, aspectRatio, w, h, ctx);
    }
    
    public static AffineTransform getPreserveAspectRatioTransform(final Element e, final String viewBox, final String aspectRatio, final float w, final float h, final BridgeContext ctx) {
        if (viewBox.length() == 0) {
            return new AffineTransform();
        }
        final float[] vb = parseViewBoxAttribute(e, viewBox, ctx);
        final PreserveAspectRatioParser p = new PreserveAspectRatioParser();
        final ViewHandler ph = new ViewHandler();
        p.setPreserveAspectRatioHandler(ph);
        try {
            p.parse(aspectRatio);
        }
        catch (ParseException pEx) {
            throw new BridgeException(ctx, e, pEx, "attribute.malformed", new Object[] { "preserveAspectRatio", aspectRatio, pEx });
        }
        return getPreserveAspectRatioTransform(vb, ph.align, ph.meet, w, h);
    }
    
    public static AffineTransform getPreserveAspectRatioTransform(final Element e, final float[] vb, final float w, final float h, final BridgeContext ctx) {
        final String aspectRatio = e.getAttributeNS(null, "preserveAspectRatio");
        final PreserveAspectRatioParser p = new PreserveAspectRatioParser();
        final ViewHandler ph = new ViewHandler();
        p.setPreserveAspectRatioHandler(ph);
        try {
            p.parse(aspectRatio);
        }
        catch (ParseException pEx) {
            throw new BridgeException(ctx, e, pEx, "attribute.malformed", new Object[] { "preserveAspectRatio", aspectRatio, pEx });
        }
        return getPreserveAspectRatioTransform(vb, ph.align, ph.meet, w, h);
    }
    
    public static AffineTransform getPreserveAspectRatioTransform(final Element e, final float[] vb, final float w, final float h, final SVGAnimatedPreserveAspectRatio aPAR, final BridgeContext ctx) {
        try {
            final SVGPreserveAspectRatio pAR = aPAR.getAnimVal();
            final short align = pAR.getAlign();
            final boolean meet = pAR.getMeetOrSlice() == 1;
            return getPreserveAspectRatioTransform(vb, align, meet, w, h);
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }
    
    public static AffineTransform getPreserveAspectRatioTransform(final Element e, final SVGAnimatedRect aViewBox, final SVGAnimatedPreserveAspectRatio aPAR, final float w, final float h, final BridgeContext ctx) {
        if (!((SVGOMAnimatedRect)aViewBox).isSpecified()) {
            return new AffineTransform();
        }
        final SVGRect viewBox = aViewBox.getAnimVal();
        final float[] vb = { viewBox.getX(), viewBox.getY(), viewBox.getWidth(), viewBox.getHeight() };
        return getPreserveAspectRatioTransform(e, vb, w, h, aPAR, ctx);
    }
    
    public static float[] parseViewBoxAttribute(final Element e, final String value, final BridgeContext ctx) {
        if (value.length() == 0) {
            return null;
        }
        int i = 0;
        final float[] vb = new float[4];
        final StringTokenizer st = new StringTokenizer(value, " ,");
        try {
            while (i < 4 && st.hasMoreTokens()) {
                vb[i] = Float.parseFloat(st.nextToken());
                ++i;
            }
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, e, nfEx, "attribute.malformed", new Object[] { "viewBox", value, nfEx });
        }
        if (i != 4) {
            throw new BridgeException(ctx, e, "attribute.malformed", new Object[] { "viewBox", value });
        }
        if (vb[2] < 0.0f || vb[3] < 0.0f) {
            throw new BridgeException(ctx, e, "attribute.malformed", new Object[] { "viewBox", value });
        }
        if (vb[2] == 0.0f || vb[3] == 0.0f) {
            return null;
        }
        return vb;
    }
    
    public static AffineTransform getPreserveAspectRatioTransform(final float[] vb, final short align, final boolean meet, final float w, final float h) {
        if (vb == null) {
            return new AffineTransform();
        }
        final AffineTransform result = new AffineTransform();
        final float vpar = vb[2] / vb[3];
        final float svgar = w / h;
        if (align == 1) {
            result.scale(w / vb[2], h / vb[3]);
            result.translate(-vb[0], -vb[1]);
        }
        else if ((vpar < svgar && meet) || (vpar >= svgar && !meet)) {
            final float sf = h / vb[3];
            result.scale(sf, sf);
            switch (align) {
                case 2:
                case 5:
                case 8: {
                    result.translate(-vb[0], -vb[1]);
                    break;
                }
                case 3:
                case 6:
                case 9: {
                    result.translate(-vb[0] - (vb[2] - w * vb[3] / h) / 2.0f, -vb[1]);
                    break;
                }
                default: {
                    result.translate(-vb[0] - (vb[2] - w * vb[3] / h), -vb[1]);
                    break;
                }
            }
        }
        else {
            final float sf = w / vb[2];
            result.scale(sf, sf);
            switch (align) {
                case 2:
                case 3:
                case 4: {
                    result.translate(-vb[0], -vb[1]);
                    break;
                }
                case 5:
                case 6:
                case 7: {
                    result.translate(-vb[0], -vb[1] - (vb[3] - h * vb[2] / w) / 2.0f);
                    break;
                }
                default: {
                    result.translate(-vb[0], -vb[1] - (vb[3] - h * vb[2] / w));
                    break;
                }
            }
        }
        return result;
    }
    
    protected static class ViewHandler extends AWTTransformProducer implements FragmentIdentifierHandler
    {
        public boolean hasTransform;
        public boolean hasId;
        public boolean hasViewBox;
        public boolean hasViewTargetParams;
        public boolean hasZoomAndPanParams;
        public String id;
        public float[] viewBox;
        public String viewTargetParams;
        public boolean isMagnify;
        public boolean hasPreserveAspectRatio;
        public short align;
        public boolean meet;
        
        protected ViewHandler() {
            this.meet = true;
        }
        
        @Override
        public void endTransformList() throws ParseException {
            super.endTransformList();
            this.hasTransform = true;
        }
        
        @Override
        public void startFragmentIdentifier() throws ParseException {
        }
        
        @Override
        public void idReference(final String s) throws ParseException {
            this.id = s;
            this.hasId = true;
        }
        
        @Override
        public void viewBox(final float x, final float y, final float width, final float height) throws ParseException {
            this.hasViewBox = true;
            (this.viewBox = new float[4])[0] = x;
            this.viewBox[1] = y;
            this.viewBox[2] = width;
            this.viewBox[3] = height;
        }
        
        @Override
        public void startViewTarget() throws ParseException {
        }
        
        @Override
        public void viewTarget(final String name) throws ParseException {
            this.viewTargetParams = name;
            this.hasViewTargetParams = true;
        }
        
        @Override
        public void endViewTarget() throws ParseException {
        }
        
        @Override
        public void zoomAndPan(final boolean magnify) {
            this.isMagnify = magnify;
            this.hasZoomAndPanParams = true;
        }
        
        @Override
        public void endFragmentIdentifier() throws ParseException {
        }
        
        @Override
        public void startPreserveAspectRatio() throws ParseException {
        }
        
        @Override
        public void none() throws ParseException {
            this.align = 1;
        }
        
        @Override
        public void xMaxYMax() throws ParseException {
            this.align = 10;
        }
        
        @Override
        public void xMaxYMid() throws ParseException {
            this.align = 7;
        }
        
        @Override
        public void xMaxYMin() throws ParseException {
            this.align = 4;
        }
        
        @Override
        public void xMidYMax() throws ParseException {
            this.align = 9;
        }
        
        @Override
        public void xMidYMid() throws ParseException {
            this.align = 6;
        }
        
        @Override
        public void xMidYMin() throws ParseException {
            this.align = 3;
        }
        
        @Override
        public void xMinYMax() throws ParseException {
            this.align = 8;
        }
        
        @Override
        public void xMinYMid() throws ParseException {
            this.align = 5;
        }
        
        @Override
        public void xMinYMin() throws ParseException {
            this.align = 2;
        }
        
        @Override
        public void meet() throws ParseException {
            this.meet = true;
        }
        
        @Override
        public void slice() throws ParseException {
            this.meet = false;
        }
        
        @Override
        public void endPreserveAspectRatio() throws ParseException {
            this.hasPreserveAspectRatio = true;
        }
    }
}
