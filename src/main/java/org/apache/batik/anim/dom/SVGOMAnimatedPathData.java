// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.apache.batik.dom.svg.SVGPathSegItem;
import java.util.Iterator;
import org.w3c.dom.svg.SVGPathSeg;
import org.apache.batik.dom.svg.AbstractSVGNormPathSegList;
import org.apache.batik.parser.ParseException;
import java.util.ArrayList;
import org.apache.batik.dom.svg.ListHandler;
import org.apache.batik.dom.svg.AbstractSVGList;
import org.apache.batik.dom.svg.ListBuilder;
import org.apache.batik.dom.svg.SVGItem;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.DOMException;
import org.apache.batik.dom.svg.AbstractSVGPathSegList;
import org.w3c.dom.Attr;
import org.apache.batik.anim.values.AnimatablePathDataValue;
import org.apache.batik.parser.PathHandler;
import org.apache.batik.dom.svg.SVGAnimatedPathDataSupport;
import org.apache.batik.parser.PathArrayProducer;
import org.apache.batik.anim.values.AnimatableValue;
import org.w3c.dom.Element;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.w3c.dom.svg.SVGPathSegList;
import org.w3c.dom.svg.SVGAnimatedPathData;

public class SVGOMAnimatedPathData extends AbstractSVGAnimatedValue implements SVGAnimatedPathData
{
    protected boolean changing;
    protected BaseSVGPathSegList pathSegs;
    protected NormalizedBaseSVGPathSegList normalizedPathSegs;
    protected AnimSVGPathSegList animPathSegs;
    protected String defaultValue;
    
    public SVGOMAnimatedPathData(final AbstractElement elt, final String ns, final String ln, final String defaultValue) {
        super(elt, ns, ln);
        this.defaultValue = defaultValue;
    }
    
    @Override
    public SVGPathSegList getAnimatedNormalizedPathSegList() {
        throw new UnsupportedOperationException("SVGAnimatedPathData.getAnimatedNormalizedPathSegList is not implemented");
    }
    
    @Override
    public SVGPathSegList getAnimatedPathSegList() {
        if (this.animPathSegs == null) {
            this.animPathSegs = new AnimSVGPathSegList();
        }
        return this.animPathSegs;
    }
    
    @Override
    public SVGPathSegList getNormalizedPathSegList() {
        if (this.normalizedPathSegs == null) {
            this.normalizedPathSegs = new NormalizedBaseSVGPathSegList();
        }
        return this.normalizedPathSegs;
    }
    
    @Override
    public SVGPathSegList getPathSegList() {
        if (this.pathSegs == null) {
            this.pathSegs = new BaseSVGPathSegList();
        }
        return this.pathSegs;
    }
    
    public void check() {
        if (!this.hasAnimVal) {
            if (this.pathSegs == null) {
                this.pathSegs = new BaseSVGPathSegList();
            }
            this.pathSegs.revalidate();
            if (this.pathSegs.missing) {
                throw new LiveAttributeException(this.element, this.localName, (short)0, null);
            }
            if (this.pathSegs.malformed) {
                throw new LiveAttributeException(this.element, this.localName, (short)1, this.pathSegs.getValueAsString());
            }
        }
    }
    
    @Override
    public AnimatableValue getUnderlyingValue(final AnimationTarget target) {
        final SVGPathSegList psl = this.getPathSegList();
        final PathArrayProducer pp = new PathArrayProducer();
        SVGAnimatedPathDataSupport.handlePathSegList(psl, pp);
        return new AnimatablePathDataValue(target, pp.getPathCommands(), pp.getPathParameters());
    }
    
    @Override
    protected void updateAnimatedValue(final AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        }
        else {
            this.hasAnimVal = true;
            final AnimatablePathDataValue animPath = (AnimatablePathDataValue)val;
            if (this.animPathSegs == null) {
                this.animPathSegs = new AnimSVGPathSegList();
            }
            this.animPathSegs.setAnimatedValue(animPath.getCommands(), animPath.getParameters());
        }
        this.fireAnimatedAttributeListeners();
    }
    
    @Override
    public void attrAdded(final Attr node, final String newv) {
        if (!this.changing) {
            if (this.pathSegs != null) {
                this.pathSegs.invalidate();
            }
            if (this.normalizedPathSegs != null) {
                this.normalizedPathSegs.invalidate();
            }
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }
    
    @Override
    public void attrModified(final Attr node, final String oldv, final String newv) {
        if (!this.changing) {
            if (this.pathSegs != null) {
                this.pathSegs.invalidate();
            }
            if (this.normalizedPathSegs != null) {
                this.normalizedPathSegs.invalidate();
            }
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }
    
    @Override
    public void attrRemoved(final Attr node, final String oldv) {
        if (!this.changing) {
            if (this.pathSegs != null) {
                this.pathSegs.invalidate();
            }
            if (this.normalizedPathSegs != null) {
                this.normalizedPathSegs.invalidate();
            }
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }
    
    public class BaseSVGPathSegList extends AbstractSVGPathSegList
    {
        protected boolean missing;
        protected boolean malformed;
        
        @Override
        protected DOMException createDOMException(final short type, final String key, final Object[] args) {
            return SVGOMAnimatedPathData.this.element.createDOMException(type, key, args);
        }
        
        @Override
        protected SVGException createSVGException(final short type, final String key, final Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedPathData.this.element).createSVGException(type, key, args);
        }
        
        @Override
        protected String getValueAsString() {
            final Attr attr = SVGOMAnimatedPathData.this.element.getAttributeNodeNS(SVGOMAnimatedPathData.this.namespaceURI, SVGOMAnimatedPathData.this.localName);
            if (attr == null) {
                return SVGOMAnimatedPathData.this.defaultValue;
            }
            return attr.getValue();
        }
        
        @Override
        protected void setAttributeValue(final String value) {
            try {
                SVGOMAnimatedPathData.this.changing = true;
                SVGOMAnimatedPathData.this.element.setAttributeNS(SVGOMAnimatedPathData.this.namespaceURI, SVGOMAnimatedPathData.this.localName, value);
            }
            finally {
                SVGOMAnimatedPathData.this.changing = false;
            }
        }
        
        @Override
        protected void resetAttribute() {
            super.resetAttribute();
            this.missing = false;
            this.malformed = false;
        }
        
        @Override
        protected void resetAttribute(final SVGItem item) {
            super.resetAttribute(item);
            this.missing = false;
            this.malformed = false;
        }
        
        @Override
        protected void revalidate() {
            if (this.valid) {
                return;
            }
            this.valid = true;
            this.missing = false;
            this.malformed = false;
            final String s = this.getValueAsString();
            if (s == null) {
                this.missing = true;
                return;
            }
            try {
                final ListBuilder builder = new ListBuilder(this);
                this.doParse(s, builder);
                if (builder.getList() != null) {
                    this.clear(this.itemList);
                }
                this.itemList = builder.getList();
            }
            catch (ParseException e) {
                this.itemList = new ArrayList(1);
                this.malformed = true;
            }
        }
    }
    
    public class NormalizedBaseSVGPathSegList extends AbstractSVGNormPathSegList
    {
        protected boolean missing;
        protected boolean malformed;
        
        @Override
        protected DOMException createDOMException(final short type, final String key, final Object[] args) {
            return SVGOMAnimatedPathData.this.element.createDOMException(type, key, args);
        }
        
        @Override
        protected SVGException createSVGException(final short type, final String key, final Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedPathData.this.element).createSVGException(type, key, args);
        }
        
        @Override
        protected String getValueAsString() throws SVGException {
            final Attr attr = SVGOMAnimatedPathData.this.element.getAttributeNodeNS(SVGOMAnimatedPathData.this.namespaceURI, SVGOMAnimatedPathData.this.localName);
            if (attr == null) {
                return SVGOMAnimatedPathData.this.defaultValue;
            }
            return attr.getValue();
        }
        
        @Override
        protected void setAttributeValue(final String value) {
            try {
                SVGOMAnimatedPathData.this.changing = true;
                SVGOMAnimatedPathData.this.element.setAttributeNS(SVGOMAnimatedPathData.this.namespaceURI, SVGOMAnimatedPathData.this.localName, value);
            }
            finally {
                SVGOMAnimatedPathData.this.changing = false;
            }
        }
        
        @Override
        protected void revalidate() {
            if (this.valid) {
                return;
            }
            this.valid = true;
            this.missing = false;
            this.malformed = false;
            final String s = this.getValueAsString();
            if (s == null) {
                this.missing = true;
                return;
            }
            try {
                final ListBuilder builder = new ListBuilder(this);
                this.doParse(s, builder);
                if (builder.getList() != null) {
                    this.clear(this.itemList);
                }
                this.itemList = builder.getList();
            }
            catch (ParseException e) {
                this.itemList = new ArrayList(1);
                this.malformed = true;
            }
        }
    }
    
    public class AnimSVGPathSegList extends AbstractSVGPathSegList
    {
        private int[] parameterIndex;
        
        public AnimSVGPathSegList() {
            this.parameterIndex = new int[1];
            this.itemList = new ArrayList(1);
        }
        
        @Override
        protected DOMException createDOMException(final short type, final String key, final Object[] args) {
            return SVGOMAnimatedPathData.this.element.createDOMException(type, key, args);
        }
        
        @Override
        protected SVGException createSVGException(final short type, final String key, final Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedPathData.this.element).createSVGException(type, key, args);
        }
        
        @Override
        public int getNumberOfItems() {
            if (SVGOMAnimatedPathData.this.hasAnimVal) {
                return super.getNumberOfItems();
            }
            return SVGOMAnimatedPathData.this.getPathSegList().getNumberOfItems();
        }
        
        @Override
        public SVGPathSeg getItem(final int index) throws DOMException {
            if (SVGOMAnimatedPathData.this.hasAnimVal) {
                return super.getItem(index);
            }
            return SVGOMAnimatedPathData.this.getPathSegList().getItem(index);
        }
        
        @Override
        protected String getValueAsString() {
            if (this.itemList.size() == 0) {
                return "";
            }
            final StringBuffer sb = new StringBuffer(this.itemList.size() * 8);
            final Iterator i = this.itemList.iterator();
            if (i.hasNext()) {
                sb.append(i.next().getValueAsString());
            }
            while (i.hasNext()) {
                sb.append(this.getItemSeparator());
                sb.append(i.next().getValueAsString());
            }
            return sb.toString();
        }
        
        @Override
        protected void setAttributeValue(final String value) {
        }
        
        @Override
        public void clear() throws DOMException {
            throw SVGOMAnimatedPathData.this.element.createDOMException((short)7, "readonly.pathseg.list", null);
        }
        
        @Override
        public SVGPathSeg initialize(final SVGPathSeg newItem) throws DOMException, SVGException {
            throw SVGOMAnimatedPathData.this.element.createDOMException((short)7, "readonly.pathseg.list", null);
        }
        
        @Override
        public SVGPathSeg insertItemBefore(final SVGPathSeg newItem, final int index) throws DOMException, SVGException {
            throw SVGOMAnimatedPathData.this.element.createDOMException((short)7, "readonly.pathseg.list", null);
        }
        
        @Override
        public SVGPathSeg replaceItem(final SVGPathSeg newItem, final int index) throws DOMException, SVGException {
            throw SVGOMAnimatedPathData.this.element.createDOMException((short)7, "readonly.pathseg.list", null);
        }
        
        @Override
        public SVGPathSeg removeItem(final int index) throws DOMException {
            throw SVGOMAnimatedPathData.this.element.createDOMException((short)7, "readonly.pathseg.list", null);
        }
        
        @Override
        public SVGPathSeg appendItem(final SVGPathSeg newItem) throws DOMException {
            throw SVGOMAnimatedPathData.this.element.createDOMException((short)7, "readonly.pathseg.list", null);
        }
        
        protected SVGPathSegItem newItem(final short command, final float[] parameters, final int[] j) {
            switch (command) {
                case 10:
                case 11: {
                    return new SVGPathSegArcItem(command, AnimSVGPathSegList.PATHSEG_LETTERS[command], parameters[j[0]++], parameters[j[0]++], parameters[j[0]++], parameters[j[0]++] != 0.0f, parameters[j[0]++] != 0.0f, parameters[j[0]++], parameters[j[0]++]);
                }
                case 1: {
                    return new SVGPathSegItem(command, AnimSVGPathSegList.PATHSEG_LETTERS[command]);
                }
                case 6:
                case 7: {
                    return new SVGPathSegCurvetoCubicItem(command, AnimSVGPathSegList.PATHSEG_LETTERS[command], parameters[j[0]++], parameters[j[0]++], parameters[j[0]++], parameters[j[0]++], parameters[j[0]++], parameters[j[0]++]);
                }
                case 16:
                case 17: {
                    return new SVGPathSegCurvetoCubicSmoothItem(command, AnimSVGPathSegList.PATHSEG_LETTERS[command], parameters[j[0]++], parameters[j[0]++], parameters[j[0]++], parameters[j[0]++]);
                }
                case 8:
                case 9: {
                    return new SVGPathSegCurvetoQuadraticItem(command, AnimSVGPathSegList.PATHSEG_LETTERS[command], parameters[j[0]++], parameters[j[0]++], parameters[j[0]++], parameters[j[0]++]);
                }
                case 18:
                case 19: {
                    return new SVGPathSegCurvetoQuadraticSmoothItem(command, AnimSVGPathSegList.PATHSEG_LETTERS[command], parameters[j[0]++], parameters[j[0]++]);
                }
                case 2:
                case 3:
                case 4:
                case 5: {
                    return new SVGPathSegMovetoLinetoItem(command, AnimSVGPathSegList.PATHSEG_LETTERS[command], parameters[j[0]++], parameters[j[0]++]);
                }
                case 12:
                case 13: {
                    return new SVGPathSegLinetoHorizontalItem(command, AnimSVGPathSegList.PATHSEG_LETTERS[command], parameters[j[0]++]);
                }
                case 14:
                case 15: {
                    return new SVGPathSegLinetoVerticalItem(command, AnimSVGPathSegList.PATHSEG_LETTERS[command], parameters[j[0]++]);
                }
                default: {
                    return null;
                }
            }
        }
        
        protected void setAnimatedValue(final short[] commands, final float[] parameters) {
            int size = this.itemList.size();
            int i = 0;
            final int[] j = this.parameterIndex;
            j[0] = 0;
            while (i < size && i < commands.length) {
                SVGPathSeg s = this.itemList.get(i);
                if (s.getPathSegType() != commands[i]) {
                    s = this.newItem(commands[i], parameters, j);
                }
                else {
                    switch (commands[i]) {
                        case 10:
                        case 11: {
                            final SVGPathSegArcItem ps = (SVGPathSegArcItem)s;
                            ps.setR1(parameters[j[0]++]);
                            ps.setR2(parameters[j[0]++]);
                            ps.setAngle(parameters[j[0]++]);
                            ps.setLargeArcFlag(parameters[j[0]++] != 0.0f);
                            ps.setSweepFlag(parameters[j[0]++] != 0.0f);
                            ps.setX(parameters[j[0]++]);
                            ps.setY(parameters[j[0]++]);
                        }
                        case 6:
                        case 7: {
                            final SVGPathSegCurvetoCubicItem ps2 = (SVGPathSegCurvetoCubicItem)s;
                            ps2.setX1(parameters[j[0]++]);
                            ps2.setY1(parameters[j[0]++]);
                            ps2.setX2(parameters[j[0]++]);
                            ps2.setY2(parameters[j[0]++]);
                            ps2.setX(parameters[j[0]++]);
                            ps2.setY(parameters[j[0]++]);
                            break;
                        }
                        case 16:
                        case 17: {
                            final SVGPathSegCurvetoCubicSmoothItem ps3 = (SVGPathSegCurvetoCubicSmoothItem)s;
                            ps3.setX2(parameters[j[0]++]);
                            ps3.setY2(parameters[j[0]++]);
                            ps3.setX(parameters[j[0]++]);
                            ps3.setY(parameters[j[0]++]);
                            break;
                        }
                        case 8:
                        case 9: {
                            final SVGPathSegCurvetoQuadraticItem ps4 = (SVGPathSegCurvetoQuadraticItem)s;
                            ps4.setX1(parameters[j[0]++]);
                            ps4.setY1(parameters[j[0]++]);
                            ps4.setX(parameters[j[0]++]);
                            ps4.setY(parameters[j[0]++]);
                            break;
                        }
                        case 18:
                        case 19: {
                            final SVGPathSegCurvetoQuadraticSmoothItem ps5 = (SVGPathSegCurvetoQuadraticSmoothItem)s;
                            ps5.setX(parameters[j[0]++]);
                            ps5.setY(parameters[j[0]++]);
                            break;
                        }
                        case 2:
                        case 3:
                        case 4:
                        case 5: {
                            final SVGPathSegMovetoLinetoItem ps6 = (SVGPathSegMovetoLinetoItem)s;
                            ps6.setX(parameters[j[0]++]);
                            ps6.setY(parameters[j[0]++]);
                            break;
                        }
                        case 12:
                        case 13: {
                            final SVGPathSegLinetoHorizontalItem ps7 = (SVGPathSegLinetoHorizontalItem)s;
                            ps7.setX(parameters[j[0]++]);
                            break;
                        }
                        case 14:
                        case 15: {
                            final SVGPathSegLinetoVerticalItem ps8 = (SVGPathSegLinetoVerticalItem)s;
                            ps8.setY(parameters[j[0]++]);
                            break;
                        }
                    }
                }
                ++i;
            }
            while (i < commands.length) {
                this.appendItemImpl(this.newItem(commands[i], parameters, j));
                ++i;
            }
            while (size > commands.length) {
                this.removeItemImpl(--size);
            }
        }
        
        @Override
        protected void resetAttribute() {
        }
        
        @Override
        protected void resetAttribute(final SVGItem item) {
        }
        
        @Override
        protected void revalidate() {
            this.valid = true;
        }
    }
}
