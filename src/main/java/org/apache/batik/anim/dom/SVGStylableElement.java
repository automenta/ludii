// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.apache.batik.css.dom.CSSOMValue.ValueProvider;
import org.apache.batik.css.engine.StyleDeclaration;
import org.apache.batik.css.dom.CSSOMStoredStyleDeclaration;
import org.apache.batik.css.dom.CSSOMSVGPaint;
import org.apache.batik.css.dom.CSSOMSVGColor;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.apache.batik.css.dom.CSSOMValue;
import org.w3c.dom.svg.SVGAnimatedString;
import org.apache.batik.css.engine.value.svg.SVGColorManager;
import org.apache.batik.css.engine.value.svg.SVGPaintManager;
import org.w3c.dom.css.CSSValue;
import org.apache.batik.dom.svg.LiveAttributeValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.css.engine.StyleDeclarationProvider;
import org.w3c.dom.Node;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.util.DoublyIndexedTable;
import org.apache.batik.css.engine.CSSStylableElement;

public abstract class SVGStylableElement extends SVGOMElement implements CSSStylableElement
{
    protected static DoublyIndexedTable xmlTraitInformation;
    protected StyleMap computedStyleMap;
    protected OverrideStyleDeclaration overrideStyleDeclaration;
    protected SVGOMAnimatedString className;
    protected StyleDeclaration style;
    
    protected SVGStylableElement() {
    }
    
    protected SVGStylableElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }
    
    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }
    
    private void initializeLiveAttributes() {
        this.className = this.createLiveAnimatedString(null, "class");
    }
    
    public CSSStyleDeclaration getOverrideStyle() {
        if (this.overrideStyleDeclaration == null) {
            final CSSEngine eng = ((SVGOMDocument)this.getOwnerDocument()).getCSSEngine();
            this.overrideStyleDeclaration = new OverrideStyleDeclaration(eng);
        }
        return this.overrideStyleDeclaration;
    }
    
    @Override
    public StyleMap getComputedStyleMap(final String pseudoElement) {
        return this.computedStyleMap;
    }
    
    @Override
    public void setComputedStyleMap(final String pseudoElement, final StyleMap sm) {
        this.computedStyleMap = sm;
    }
    
    @Override
    public String getXMLId() {
        return this.getAttributeNS(null, "id");
    }
    
    @Override
    public String getCSSClass() {
        return this.getAttributeNS(null, "class");
    }
    
    @Override
    public ParsedURL getCSSBase() {
        if (this.getXblBoundElement() != null) {
            return null;
        }
        final String bu = this.getBaseURI();
        return (bu == null) ? null : new ParsedURL(bu);
    }
    
    @Override
    public boolean isPseudoInstanceOf(final String pseudoClass) {
        if (pseudoClass.equals("first-child")) {
            Node n;
            for (n = this.getPreviousSibling(); n != null && n.getNodeType() != 1; n = n.getPreviousSibling()) {}
            return n == null;
        }
        return false;
    }
    
    @Override
    public StyleDeclarationProvider getOverrideStyleDeclarationProvider() {
        return (StyleDeclarationProvider)this.getOverrideStyle();
    }
    
    @Override
    public void updatePropertyValue(final String pn, final AnimatableValue val) {
        final CSSStyleDeclaration over = this.getOverrideStyle();
        if (val == null) {
            over.removeProperty(pn);
        }
        else {
            over.setProperty(pn, val.getCssText(), "");
        }
    }
    
    @Override
    public boolean useLinearRGBColorInterpolation() {
        final CSSEngine eng = ((SVGOMDocument)this.getOwnerDocument()).getCSSEngine();
        final Value v = eng.getComputedStyle(this, null, 6);
        return v.getStringValue().charAt(0) == 'l';
    }
    
    @Override
    public void addTargetListener(final String ns, final String an, final boolean isCSS, final AnimationTargetListener l) {
        if (isCSS) {
            if (this.svgContext != null) {
                final SVGAnimationTargetContext actx = (SVGAnimationTargetContext)this.svgContext;
                actx.addTargetListener(an, l);
            }
        }
        else {
            super.addTargetListener(ns, an, isCSS, l);
        }
    }
    
    @Override
    public void removeTargetListener(final String ns, final String an, final boolean isCSS, final AnimationTargetListener l) {
        if (isCSS) {
            if (this.svgContext != null) {
                final SVGAnimationTargetContext actx = (SVGAnimationTargetContext)this.svgContext;
                actx.removeTargetListener(an, l);
            }
        }
        else {
            super.removeTargetListener(ns, an, isCSS, l);
        }
    }
    
    public CSSStyleDeclaration getStyle() {
        if (this.style == null) {
            final CSSEngine eng = ((SVGOMDocument)this.getOwnerDocument()).getCSSEngine();
            this.putLiveAttributeValue(null, "style", this.style = new StyleDeclaration(eng));
        }
        return this.style;
    }
    
    public CSSValue getPresentationAttribute(final String name) {
        CSSValue result = (CSSValue)this.getLiveAttributeValue(null, name);
        if (result != null) {
            return result;
        }
        final CSSEngine eng = ((SVGOMDocument)this.getOwnerDocument()).getCSSEngine();
        final int idx = eng.getPropertyIndex(name);
        if (idx == -1) {
            return null;
        }
        if (idx > 59) {
            if (eng.getValueManagers()[idx] instanceof SVGPaintManager) {
                result = new PresentationAttributePaintValue(eng, name);
            }
            if (eng.getValueManagers()[idx] instanceof SVGColorManager) {
                result = new PresentationAttributeColorValue(eng, name);
            }
        }
        else {
            switch (idx) {
                case 15:
                case 45: {
                    result = new PresentationAttributePaintValue(eng, name);
                    break;
                }
                case 19:
                case 33:
                case 43: {
                    result = new PresentationAttributeColorValue(eng, name);
                    break;
                }
                default: {
                    result = new PresentationAttributeValue(eng, name);
                    break;
                }
            }
        }
        this.putLiveAttributeValue(null, name, (LiveAttributeValue)result);
        if (this.getAttributeNS(null, name).length() == 0) {
            return null;
        }
        return result;
    }
    
    public SVGAnimatedString getClassName() {
        return this.className;
    }
    
    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return SVGStylableElement.xmlTraitInformation;
    }
    
    static {
        final DoublyIndexedTable t = new DoublyIndexedTable(SVGOMElement.xmlTraitInformation);
        t.put(null, "class", new TraitInformation(true, 16));
        SVGStylableElement.xmlTraitInformation = t;
    }
    
    public class PresentationAttributeValue extends CSSOMValue implements LiveAttributeValue, ValueProvider
    {
        protected CSSEngine cssEngine;
        protected String property;
        protected Value value;
        protected boolean mutate;
        
        public PresentationAttributeValue(final CSSEngine eng, final String prop) {
            super(null);
            ((CSSOMValue)(this.valueProvider = this)).setModificationHandler(new AbstractModificationHandler() {
                @Override
                protected Value getValue() {
                    return PresentationAttributeValue.this.getValue();
                }
                
                @Override
                public void textChanged(final String text) throws DOMException {
                    PresentationAttributeValue.this.value = PresentationAttributeValue.this.cssEngine.parsePropertyValue(SVGStylableElement.this, PresentationAttributeValue.this.property, text);
                    PresentationAttributeValue.this.mutate = true;
                    SVGStylableElement.this.setAttributeNS(null, PresentationAttributeValue.this.property, text);
                    PresentationAttributeValue.this.mutate = false;
                }
            });
            this.cssEngine = eng;
            this.property = prop;
            final Attr attr = SVGStylableElement.this.getAttributeNodeNS(null, prop);
            if (attr != null) {
                this.value = this.cssEngine.parsePropertyValue(SVGStylableElement.this, prop, attr.getValue());
            }
        }
        
        @Override
        public Value getValue() {
            if (this.value == null) {
                throw new DOMException((short)11, "");
            }
            return this.value;
        }
        
        @Override
        public void attrAdded(final Attr node, final String newv) {
            if (!this.mutate) {
                this.value = this.cssEngine.parsePropertyValue(SVGStylableElement.this, this.property, newv);
            }
        }
        
        @Override
        public void attrModified(final Attr node, final String oldv, final String newv) {
            if (!this.mutate) {
                this.value = this.cssEngine.parsePropertyValue(SVGStylableElement.this, this.property, newv);
            }
        }
        
        @Override
        public void attrRemoved(final Attr node, final String oldv) {
            if (!this.mutate) {
                this.value = null;
            }
        }
    }
    
    public class PresentationAttributeColorValue extends CSSOMSVGColor implements LiveAttributeValue, CSSOMSVGColor.ValueProvider
    {
        protected CSSEngine cssEngine;
        protected String property;
        protected Value value;
        protected boolean mutate;
        
        public PresentationAttributeColorValue(final CSSEngine eng, final String prop) {
            super(null);
            ((CSSOMSVGColor)(this.valueProvider = this)).setModificationHandler(new AbstractModificationHandler() {
                @Override
                protected Value getValue() {
                    return PresentationAttributeColorValue.this.getValue();
                }
                
                @Override
                public void textChanged(final String text) throws DOMException {
                    PresentationAttributeColorValue.this.value = PresentationAttributeColorValue.this.cssEngine.parsePropertyValue(SVGStylableElement.this, PresentationAttributeColorValue.this.property, text);
                    PresentationAttributeColorValue.this.mutate = true;
                    SVGStylableElement.this.setAttributeNS(null, PresentationAttributeColorValue.this.property, text);
                    PresentationAttributeColorValue.this.mutate = false;
                }
            });
            this.cssEngine = eng;
            this.property = prop;
            final Attr attr = SVGStylableElement.this.getAttributeNodeNS(null, prop);
            if (attr != null) {
                this.value = this.cssEngine.parsePropertyValue(SVGStylableElement.this, prop, attr.getValue());
            }
        }
        
        @Override
        public Value getValue() {
            if (this.value == null) {
                throw new DOMException((short)11, "");
            }
            return this.value;
        }
        
        @Override
        public void attrAdded(final Attr node, final String newv) {
            if (!this.mutate) {
                this.value = this.cssEngine.parsePropertyValue(SVGStylableElement.this, this.property, newv);
            }
        }
        
        @Override
        public void attrModified(final Attr node, final String oldv, final String newv) {
            if (!this.mutate) {
                this.value = this.cssEngine.parsePropertyValue(SVGStylableElement.this, this.property, newv);
            }
        }
        
        @Override
        public void attrRemoved(final Attr node, final String oldv) {
            if (!this.mutate) {
                this.value = null;
            }
        }
    }
    
    public class PresentationAttributePaintValue extends CSSOMSVGPaint implements LiveAttributeValue, CSSOMSVGColor.ValueProvider
    {
        protected CSSEngine cssEngine;
        protected String property;
        protected Value value;
        protected boolean mutate;
        
        public PresentationAttributePaintValue(final CSSEngine eng, final String prop) {
            super(null);
            ((CSSOMSVGPaint)(this.valueProvider = this)).setModificationHandler(new CSSOMSVGPaint.AbstractModificationHandler() {
                @Override
                protected Value getValue() {
                    return PresentationAttributePaintValue.this.getValue();
                }
                
                @Override
                public void textChanged(final String text) throws DOMException {
                    PresentationAttributePaintValue.this.value = PresentationAttributePaintValue.this.cssEngine.parsePropertyValue(SVGStylableElement.this, PresentationAttributePaintValue.this.property, text);
                    PresentationAttributePaintValue.this.mutate = true;
                    SVGStylableElement.this.setAttributeNS(null, PresentationAttributePaintValue.this.property, text);
                    PresentationAttributePaintValue.this.mutate = false;
                }
            });
            this.cssEngine = eng;
            this.property = prop;
            final Attr attr = SVGStylableElement.this.getAttributeNodeNS(null, prop);
            if (attr != null) {
                this.value = this.cssEngine.parsePropertyValue(SVGStylableElement.this, prop, attr.getValue());
            }
        }
        
        @Override
        public Value getValue() {
            if (this.value == null) {
                throw new DOMException((short)11, "");
            }
            return this.value;
        }
        
        @Override
        public void attrAdded(final Attr node, final String newv) {
            if (!this.mutate) {
                this.value = this.cssEngine.parsePropertyValue(SVGStylableElement.this, this.property, newv);
            }
        }
        
        @Override
        public void attrModified(final Attr node, final String oldv, final String newv) {
            if (!this.mutate) {
                this.value = this.cssEngine.parsePropertyValue(SVGStylableElement.this, this.property, newv);
            }
        }
        
        @Override
        public void attrRemoved(final Attr node, final String oldv) {
            if (!this.mutate) {
                this.value = null;
            }
        }
    }
    
    public class StyleDeclaration extends CSSOMStoredStyleDeclaration implements LiveAttributeValue, CSSEngine.MainPropertyReceiver
    {
        protected boolean mutate;
        
        public StyleDeclaration(final CSSEngine eng) {
            super(eng);
            this.declaration = this.cssEngine.parseStyleDeclaration(SVGStylableElement.this, SVGStylableElement.this.getAttributeNS(null, "style"));
        }
        
        @Override
        public void attrAdded(final Attr node, final String newv) {
            if (!this.mutate) {
                this.declaration = this.cssEngine.parseStyleDeclaration(SVGStylableElement.this, newv);
            }
        }
        
        @Override
        public void attrModified(final Attr node, final String oldv, final String newv) {
            if (!this.mutate) {
                this.declaration = this.cssEngine.parseStyleDeclaration(SVGStylableElement.this, newv);
            }
        }
        
        @Override
        public void attrRemoved(final Attr node, final String oldv) {
            if (!this.mutate) {
                this.declaration = new org.apache.batik.css.engine.StyleDeclaration();
            }
        }
        
        @Override
        public void textChanged(final String text) throws DOMException {
            this.declaration = this.cssEngine.parseStyleDeclaration(SVGStylableElement.this, text);
            this.mutate = true;
            SVGStylableElement.this.setAttributeNS(null, "style", text);
            this.mutate = false;
        }
        
        @Override
        public void propertyRemoved(final String name) throws DOMException {
            final int idx = this.cssEngine.getPropertyIndex(name);
            for (int i = 0; i < this.declaration.size(); ++i) {
                if (idx == this.declaration.getIndex(i)) {
                    this.declaration.remove(i);
                    this.mutate = true;
                    SVGStylableElement.this.setAttributeNS(null, "style", this.declaration.toString(this.cssEngine));
                    this.mutate = false;
                    return;
                }
            }
        }
        
        @Override
        public void propertyChanged(final String name, final String value, final String prio) throws DOMException {
            final boolean important = prio != null && prio.length() > 0;
            this.cssEngine.setMainProperties(SVGStylableElement.this, this, name, value, important);
            this.mutate = true;
            SVGStylableElement.this.setAttributeNS(null, "style", this.declaration.toString(this.cssEngine));
            this.mutate = false;
        }
        
        @Override
        public void setMainProperty(final String name, final Value v, final boolean important) {
            final int idx = this.cssEngine.getPropertyIndex(name);
            if (idx == -1) {
                return;
            }
            int i;
            for (i = 0; i < this.declaration.size() && idx != this.declaration.getIndex(i); ++i) {}
            if (i < this.declaration.size()) {
                this.declaration.put(i, v, idx, important);
            }
            else {
                this.declaration.append(v, idx, important);
            }
        }
    }
    
    protected class OverrideStyleDeclaration extends CSSOMStoredStyleDeclaration
    {
        protected OverrideStyleDeclaration(final CSSEngine eng) {
            super(eng);
            this.declaration = new org.apache.batik.css.engine.StyleDeclaration();
        }
        
        @Override
        public void textChanged(final String text) throws DOMException {
            ((SVGOMDocument)SVGStylableElement.this.ownerDocument).overrideStyleTextChanged(SVGStylableElement.this, text);
        }
        
        @Override
        public void propertyRemoved(final String name) throws DOMException {
            ((SVGOMDocument)SVGStylableElement.this.ownerDocument).overrideStylePropertyRemoved(SVGStylableElement.this, name);
        }
        
        @Override
        public void propertyChanged(final String name, final String value, final String prio) throws DOMException {
            ((SVGOMDocument)SVGStylableElement.this.ownerDocument).overrideStylePropertyChanged(SVGStylableElement.this, name, value, prio);
        }
    }
}
