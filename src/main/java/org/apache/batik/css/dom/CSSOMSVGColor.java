// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.dom;

import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.Rect;
import org.w3c.dom.css.Counter;
import org.apache.batik.css.engine.value.FloatValue;
import org.w3c.dom.svg.SVGNumber;
import org.apache.batik.css.engine.value.svg.ICCColor;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.apache.batik.css.engine.value.Value;
import org.w3c.dom.DOMException;
import java.util.ArrayList;
import org.w3c.dom.svg.SVGNumberList;
import org.w3c.dom.svg.SVGICCColor;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.svg.SVGColor;

public class CSSOMSVGColor implements SVGColor, RGBColor, SVGICCColor, SVGNumberList
{
    protected ValueProvider valueProvider;
    protected ModificationHandler handler;
    protected RedComponent redComponent;
    protected GreenComponent greenComponent;
    protected BlueComponent blueComponent;
    protected ArrayList<SVGNumber> iccColors;
    
    public CSSOMSVGColor(final ValueProvider vp) {
        this.valueProvider = vp;
    }
    
    public void setModificationHandler(final ModificationHandler h) {
        this.handler = h;
    }
    
    @Override
    public String getCssText() {
        return this.valueProvider.getValue().getCssText();
    }
    
    @Override
    public void setCssText(final String cssText) throws DOMException {
        if (this.handler == null) {
            throw new DOMException((short)7, "");
        }
        this.iccColors = null;
        this.handler.textChanged(cssText);
    }
    
    @Override
    public short getCssValueType() {
        return 3;
    }
    
    @Override
    public short getColorType() {
        final Value value = this.valueProvider.getValue();
        final int cssValueType = value.getCssValueType();
        switch (cssValueType) {
            case 1: {
                final int primitiveType = value.getPrimitiveType();
                switch (primitiveType) {
                    case 21: {
                        if (value.getStringValue().equalsIgnoreCase("currentcolor")) {
                            return 3;
                        }
                        return 1;
                    }
                    case 25: {
                        return 1;
                    }
                    default: {
                        throw new IllegalStateException("Found unexpected PrimitiveType:" + primitiveType);
                    }
                }
            }
            case 2: {
                return 2;
            }
            default: {
                throw new IllegalStateException("Found unexpected CssValueType:" + cssValueType);
            }
        }
    }
    
    @Override
    public RGBColor getRGBColor() {
        return this;
    }
    
    public RGBColor getRgbColor() {
        return this;
    }
    
    @Override
    public void setRGBColor(final String color) {
        if (this.handler == null) {
            throw new DOMException((short)7, "");
        }
        this.handler.rgbColorChanged(color);
    }
    
    @Override
    public SVGICCColor getICCColor() {
        return this;
    }
    
    public SVGICCColor getIccColor() {
        return this;
    }
    
    @Override
    public void setRGBColorICCColor(final String rgb, final String icc) {
        if (this.handler == null) {
            throw new DOMException((short)7, "");
        }
        this.iccColors = null;
        this.handler.rgbColorICCColorChanged(rgb, icc);
    }
    
    @Override
    public void setColor(final short type, final String rgb, final String icc) {
        if (this.handler == null) {
            throw new DOMException((short)7, "");
        }
        this.iccColors = null;
        this.handler.colorChanged(type, rgb, icc);
    }
    
    @Override
    public CSSPrimitiveValue getRed() {
        this.valueProvider.getValue().getRed();
        if (this.redComponent == null) {
            this.redComponent = new RedComponent();
        }
        return this.redComponent;
    }
    
    @Override
    public CSSPrimitiveValue getGreen() {
        this.valueProvider.getValue().getGreen();
        if (this.greenComponent == null) {
            this.greenComponent = new GreenComponent();
        }
        return this.greenComponent;
    }
    
    @Override
    public CSSPrimitiveValue getBlue() {
        this.valueProvider.getValue().getBlue();
        if (this.blueComponent == null) {
            this.blueComponent = new BlueComponent();
        }
        return this.blueComponent;
    }
    
    @Override
    public String getColorProfile() {
        if (this.getColorType() != 2) {
            throw new DOMException((short)12, "");
        }
        final Value value = this.valueProvider.getValue();
        return ((ICCColor)value.item(1)).getColorProfile();
    }
    
    @Override
    public void setColorProfile(final String colorProfile) throws DOMException {
        if (this.handler == null) {
            throw new DOMException((short)7, "");
        }
        this.handler.colorProfileChanged(colorProfile);
    }
    
    @Override
    public SVGNumberList getColors() {
        return this;
    }
    
    @Override
    public int getNumberOfItems() {
        if (this.getColorType() != 2) {
            throw new DOMException((short)12, "");
        }
        final Value value = this.valueProvider.getValue();
        return ((ICCColor)value.item(1)).getNumberOfColors();
    }
    
    @Override
    public void clear() throws DOMException {
        if (this.handler == null) {
            throw new DOMException((short)7, "");
        }
        this.iccColors = null;
        this.handler.colorsCleared();
    }
    
    @Override
    public SVGNumber initialize(final SVGNumber newItem) throws DOMException {
        if (this.handler == null) {
            throw new DOMException((short)7, "");
        }
        final float f = newItem.getValue();
        this.iccColors = new ArrayList();
        final SVGNumber result = new ColorNumber(f);
        this.iccColors.add(result);
        this.handler.colorsInitialized(f);
        return result;
    }
    
    @Override
    public SVGNumber getItem(final int index) throws DOMException {
        if (this.getColorType() != 2) {
            throw new DOMException((short)1, "");
        }
        final int n = this.getNumberOfItems();
        if (index < 0 || index >= n) {
            throw new DOMException((short)1, "");
        }
        if (this.iccColors == null) {
            this.iccColors = new ArrayList(n);
            for (int i = this.iccColors.size(); i < n; ++i) {
                this.iccColors.add(null);
            }
        }
        final Value value = this.valueProvider.getValue().item(1);
        final float f = ((ICCColor)value).getColor(index);
        final SVGNumber result = new ColorNumber(f);
        this.iccColors.set(index, result);
        return result;
    }
    
    @Override
    public SVGNumber insertItemBefore(final SVGNumber newItem, final int index) throws DOMException {
        if (this.handler == null) {
            throw new DOMException((short)7, "");
        }
        final int n = this.getNumberOfItems();
        if (index < 0 || index > n) {
            throw new DOMException((short)1, "");
        }
        if (this.iccColors == null) {
            this.iccColors = new ArrayList(n);
            for (int i = this.iccColors.size(); i < n; ++i) {
                this.iccColors.add(null);
            }
        }
        final float f = newItem.getValue();
        final SVGNumber result = new ColorNumber(f);
        this.iccColors.add(index, result);
        this.handler.colorInsertedBefore(f, index);
        return result;
    }
    
    @Override
    public SVGNumber replaceItem(final SVGNumber newItem, final int index) throws DOMException {
        if (this.handler == null) {
            throw new DOMException((short)7, "");
        }
        final int n = this.getNumberOfItems();
        if (index < 0 || index >= n) {
            throw new DOMException((short)1, "");
        }
        if (this.iccColors == null) {
            this.iccColors = new ArrayList(n);
            for (int i = this.iccColors.size(); i < n; ++i) {
                this.iccColors.add(null);
            }
        }
        final float f = newItem.getValue();
        final SVGNumber result = new ColorNumber(f);
        this.iccColors.set(index, result);
        this.handler.colorReplaced(f, index);
        return result;
    }
    
    @Override
    public SVGNumber removeItem(final int index) throws DOMException {
        if (this.handler == null) {
            throw new DOMException((short)7, "");
        }
        final int n = this.getNumberOfItems();
        if (index < 0 || index >= n) {
            throw new DOMException((short)1, "");
        }
        SVGNumber result = null;
        if (this.iccColors != null) {
            result = this.iccColors.get(index);
        }
        if (result == null) {
            final Value value = this.valueProvider.getValue().item(1);
            result = new ColorNumber(((ICCColor)value).getColor(index));
        }
        this.handler.colorRemoved(index);
        return result;
    }
    
    @Override
    public SVGNumber appendItem(final SVGNumber newItem) throws DOMException {
        if (this.handler == null) {
            throw new DOMException((short)7, "");
        }
        if (this.iccColors == null) {
            final int n = this.getNumberOfItems();
            this.iccColors = new ArrayList(n);
            for (int i = 0; i < n; ++i) {
                this.iccColors.add(null);
            }
        }
        final float f = newItem.getValue();
        final SVGNumber result = new ColorNumber(f);
        this.iccColors.add(result);
        this.handler.colorAppend(f);
        return result;
    }
    
    protected class ColorNumber implements SVGNumber
    {
        protected float value;
        
        public ColorNumber(final float f) {
            this.value = f;
        }
        
        @Override
        public float getValue() {
            if (CSSOMSVGColor.this.iccColors == null) {
                return this.value;
            }
            final int idx = CSSOMSVGColor.this.iccColors.indexOf(this);
            if (idx == -1) {
                return this.value;
            }
            final Value value = CSSOMSVGColor.this.valueProvider.getValue().item(1);
            return ((ICCColor)value).getColor(idx);
        }
        
        @Override
        public void setValue(final float f) {
            this.value = f;
            if (CSSOMSVGColor.this.iccColors == null) {
                return;
            }
            final int idx = CSSOMSVGColor.this.iccColors.indexOf(this);
            if (idx == -1) {
                return;
            }
            if (CSSOMSVGColor.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            CSSOMSVGColor.this.handler.colorReplaced(f, idx);
        }
    }
    
    public abstract class AbstractModificationHandler implements ModificationHandler
    {
        protected abstract Value getValue();
        
        @Override
        public void redTextChanged(final String text) throws DOMException {
            final StringBuffer sb = new StringBuffer(40);
            final Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 1: {
                    sb.append("rgb(");
                    sb.append(text);
                    sb.append(',');
                    sb.append(value.getGreen().getCssText());
                    sb.append(',');
                    sb.append(value.getBlue().getCssText());
                    sb.append(')');
                    break;
                }
                case 2: {
                    sb.append("rgb(");
                    sb.append(text);
                    sb.append(',');
                    sb.append(value.item(0).getGreen().getCssText());
                    sb.append(',');
                    sb.append(value.item(0).getBlue().getCssText());
                    sb.append(')');
                    sb.append(value.item(1).getCssText());
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
            this.textChanged(sb.toString());
        }
        
        @Override
        public void redFloatValueChanged(final short unit, final float fValue) throws DOMException {
            final StringBuffer sb = new StringBuffer(40);
            final Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 1: {
                    sb.append("rgb(");
                    sb.append(FloatValue.getCssText(unit, fValue));
                    sb.append(',');
                    sb.append(value.getGreen().getCssText());
                    sb.append(',');
                    sb.append(value.getBlue().getCssText());
                    sb.append(')');
                    break;
                }
                case 2: {
                    sb.append("rgb(");
                    sb.append(FloatValue.getCssText(unit, fValue));
                    sb.append(',');
                    sb.append(value.item(0).getGreen().getCssText());
                    sb.append(',');
                    sb.append(value.item(0).getBlue().getCssText());
                    sb.append(')');
                    sb.append(value.item(1).getCssText());
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
            this.textChanged(sb.toString());
        }
        
        @Override
        public void greenTextChanged(final String text) throws DOMException {
            final StringBuffer sb = new StringBuffer(40);
            final Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 1: {
                    sb.append("rgb(");
                    sb.append(value.getRed().getCssText());
                    sb.append(',');
                    sb.append(text);
                    sb.append(',');
                    sb.append(value.getBlue().getCssText());
                    sb.append(')');
                    break;
                }
                case 2: {
                    sb.append("rgb(");
                    sb.append(value.item(0).getRed().getCssText());
                    sb.append(',');
                    sb.append(text);
                    sb.append(',');
                    sb.append(value.item(0).getBlue().getCssText());
                    sb.append(')');
                    sb.append(value.item(1).getCssText());
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
            this.textChanged(sb.toString());
        }
        
        @Override
        public void greenFloatValueChanged(final short unit, final float fValue) throws DOMException {
            final StringBuffer sb = new StringBuffer(40);
            final Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 1: {
                    sb.append("rgb(");
                    sb.append(value.getRed().getCssText());
                    sb.append(',');
                    sb.append(FloatValue.getCssText(unit, fValue));
                    sb.append(',');
                    sb.append(value.getBlue().getCssText());
                    sb.append(')');
                    break;
                }
                case 2: {
                    sb.append("rgb(");
                    sb.append(value.item(0).getRed().getCssText());
                    sb.append(',');
                    sb.append(FloatValue.getCssText(unit, fValue));
                    sb.append(',');
                    sb.append(value.item(0).getBlue().getCssText());
                    sb.append(')');
                    sb.append(value.item(1).getCssText());
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
            this.textChanged(sb.toString());
        }
        
        @Override
        public void blueTextChanged(final String text) throws DOMException {
            final StringBuffer sb = new StringBuffer(40);
            final Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 1: {
                    sb.append("rgb(");
                    sb.append(value.getRed().getCssText());
                    sb.append(',');
                    sb.append(value.getGreen().getCssText());
                    sb.append(',');
                    sb.append(text);
                    sb.append(')');
                    break;
                }
                case 2: {
                    sb.append("rgb(");
                    sb.append(value.item(0).getRed().getCssText());
                    sb.append(',');
                    sb.append(value.item(0).getGreen().getCssText());
                    sb.append(',');
                    sb.append(text);
                    sb.append(')');
                    sb.append(value.item(1).getCssText());
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
            this.textChanged(sb.toString());
        }
        
        @Override
        public void blueFloatValueChanged(final short unit, final float fValue) throws DOMException {
            final StringBuffer sb = new StringBuffer(40);
            final Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 1: {
                    sb.append("rgb(");
                    sb.append(value.getRed().getCssText());
                    sb.append(',');
                    sb.append(value.getGreen().getCssText());
                    sb.append(',');
                    sb.append(FloatValue.getCssText(unit, fValue));
                    sb.append(')');
                    break;
                }
                case 2: {
                    sb.append("rgb(");
                    sb.append(value.item(0).getRed().getCssText());
                    sb.append(',');
                    sb.append(value.item(0).getGreen().getCssText());
                    sb.append(',');
                    sb.append(FloatValue.getCssText(unit, fValue));
                    sb.append(')');
                    sb.append(value.item(1).getCssText());
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
            this.textChanged(sb.toString());
        }
        
        @Override
        public void rgbColorChanged(String text) throws DOMException {
            switch (CSSOMSVGColor.this.getColorType()) {
                case 1: {
                    break;
                }
                case 2: {
                    text += this.getValue().item(1).getCssText();
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
            this.textChanged(text);
        }
        
        @Override
        public void rgbColorICCColorChanged(final String rgb, final String icc) throws DOMException {
            switch (CSSOMSVGColor.this.getColorType()) {
                case 2: {
                    this.textChanged(rgb + ' ' + icc);
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
        }
        
        @Override
        public void colorChanged(final short type, final String rgb, final String icc) throws DOMException {
            switch (type) {
                case 3: {
                    this.textChanged("currentcolor");
                    break;
                }
                case 1: {
                    this.textChanged(rgb);
                    break;
                }
                case 2: {
                    this.textChanged(rgb + ' ' + icc);
                    break;
                }
                default: {
                    throw new DOMException((short)9, "");
                }
            }
        }
        
        @Override
        public void colorProfileChanged(final String cp) throws DOMException {
            final Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 2: {
                    final StringBuffer sb = new StringBuffer(value.item(0).getCssText());
                    sb.append(" icc-color(");
                    sb.append(cp);
                    final ICCColor iccc = (ICCColor)value.item(1);
                    for (int i = 0; i < iccc.getLength(); ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    sb.append(')');
                    this.textChanged(sb.toString());
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
        }
        
        @Override
        public void colorsCleared() throws DOMException {
            final Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 2: {
                    final StringBuffer sb = new StringBuffer(value.item(0).getCssText());
                    sb.append(" icc-color(");
                    final ICCColor iccc = (ICCColor)value.item(1);
                    sb.append(iccc.getColorProfile());
                    sb.append(')');
                    this.textChanged(sb.toString());
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
        }
        
        @Override
        public void colorsInitialized(final float f) throws DOMException {
            final Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 2: {
                    final StringBuffer sb = new StringBuffer(value.item(0).getCssText());
                    sb.append(" icc-color(");
                    final ICCColor iccc = (ICCColor)value.item(1);
                    sb.append(iccc.getColorProfile());
                    sb.append(',');
                    sb.append(f);
                    sb.append(')');
                    this.textChanged(sb.toString());
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
        }
        
        @Override
        public void colorInsertedBefore(final float f, final int idx) throws DOMException {
            final Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 2: {
                    final StringBuffer sb = new StringBuffer(value.item(0).getCssText());
                    sb.append(" icc-color(");
                    final ICCColor iccc = (ICCColor)value.item(1);
                    sb.append(iccc.getColorProfile());
                    for (int i = 0; i < idx; ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    sb.append(',');
                    sb.append(f);
                    for (int i = idx; i < iccc.getLength(); ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    sb.append(')');
                    this.textChanged(sb.toString());
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
        }
        
        @Override
        public void colorReplaced(final float f, final int idx) throws DOMException {
            final Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 2: {
                    final StringBuffer sb = new StringBuffer(value.item(0).getCssText());
                    sb.append(" icc-color(");
                    final ICCColor iccc = (ICCColor)value.item(1);
                    sb.append(iccc.getColorProfile());
                    for (int i = 0; i < idx; ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    sb.append(',');
                    sb.append(f);
                    for (int i = idx + 1; i < iccc.getLength(); ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    sb.append(')');
                    this.textChanged(sb.toString());
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
        }
        
        @Override
        public void colorRemoved(final int idx) throws DOMException {
            final Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 2: {
                    final StringBuffer sb = new StringBuffer(value.item(0).getCssText());
                    sb.append(" icc-color(");
                    final ICCColor iccc = (ICCColor)value.item(1);
                    sb.append(iccc.getColorProfile());
                    for (int i = 0; i < idx; ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    for (int i = idx + 1; i < iccc.getLength(); ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    sb.append(')');
                    this.textChanged(sb.toString());
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
        }
        
        @Override
        public void colorAppend(final float f) throws DOMException {
            final Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 2: {
                    final StringBuffer sb = new StringBuffer(value.item(0).getCssText());
                    sb.append(" icc-color(");
                    final ICCColor iccc = (ICCColor)value.item(1);
                    sb.append(iccc.getColorProfile());
                    for (int i = 0; i < iccc.getLength(); ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    sb.append(',');
                    sb.append(f);
                    sb.append(')');
                    this.textChanged(sb.toString());
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
        }
    }
    
    protected abstract class AbstractComponent implements CSSPrimitiveValue
    {
        protected abstract Value getValue();
        
        @Override
        public String getCssText() {
            return this.getValue().getCssText();
        }
        
        @Override
        public short getCssValueType() {
            return this.getValue().getCssValueType();
        }
        
        @Override
        public short getPrimitiveType() {
            return this.getValue().getPrimitiveType();
        }
        
        @Override
        public float getFloatValue(final short unitType) throws DOMException {
            return CSSOMValue.convertFloatValue(unitType, this.getValue());
        }
        
        @Override
        public String getStringValue() throws DOMException {
            return CSSOMSVGColor.this.valueProvider.getValue().getStringValue();
        }
        
        @Override
        public Counter getCounterValue() throws DOMException {
            throw new DOMException((short)15, "");
        }
        
        @Override
        public Rect getRectValue() throws DOMException {
            throw new DOMException((short)15, "");
        }
        
        @Override
        public RGBColor getRGBColorValue() throws DOMException {
            throw new DOMException((short)15, "");
        }
        
        public int getLength() {
            throw new DOMException((short)15, "");
        }
        
        public CSSValue item(final int index) {
            throw new DOMException((short)15, "");
        }
    }
    
    protected abstract class FloatComponent extends AbstractComponent
    {
        @Override
        public void setStringValue(final short stringType, final String stringValue) throws DOMException {
            throw new DOMException((short)15, "");
        }
    }
    
    protected class RedComponent extends FloatComponent
    {
        @Override
        protected Value getValue() {
            return CSSOMSVGColor.this.valueProvider.getValue().getRed();
        }
        
        @Override
        public void setCssText(final String cssText) throws DOMException {
            if (CSSOMSVGColor.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMSVGColor.this.handler.redTextChanged(cssText);
        }
        
        @Override
        public void setFloatValue(final short unitType, final float floatValue) throws DOMException {
            if (CSSOMSVGColor.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMSVGColor.this.handler.redFloatValueChanged(unitType, floatValue);
        }
    }
    
    protected class GreenComponent extends FloatComponent
    {
        @Override
        protected Value getValue() {
            return CSSOMSVGColor.this.valueProvider.getValue().getGreen();
        }
        
        @Override
        public void setCssText(final String cssText) throws DOMException {
            if (CSSOMSVGColor.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMSVGColor.this.handler.greenTextChanged(cssText);
        }
        
        @Override
        public void setFloatValue(final short unitType, final float floatValue) throws DOMException {
            if (CSSOMSVGColor.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMSVGColor.this.handler.greenFloatValueChanged(unitType, floatValue);
        }
    }
    
    protected class BlueComponent extends FloatComponent
    {
        @Override
        protected Value getValue() {
            return CSSOMSVGColor.this.valueProvider.getValue().getBlue();
        }
        
        @Override
        public void setCssText(final String cssText) throws DOMException {
            if (CSSOMSVGColor.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMSVGColor.this.handler.blueTextChanged(cssText);
        }
        
        @Override
        public void setFloatValue(final short unitType, final float floatValue) throws DOMException {
            if (CSSOMSVGColor.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMSVGColor.this.handler.blueFloatValueChanged(unitType, floatValue);
        }
    }
    
    public interface ValueProvider
    {
        Value getValue();
    }
    
    public interface ModificationHandler
    {
        void textChanged(final String p0) throws DOMException;
        
        void redTextChanged(final String p0) throws DOMException;
        
        void redFloatValueChanged(final short p0, final float p1) throws DOMException;
        
        void greenTextChanged(final String p0) throws DOMException;
        
        void greenFloatValueChanged(final short p0, final float p1) throws DOMException;
        
        void blueTextChanged(final String p0) throws DOMException;
        
        void blueFloatValueChanged(final short p0, final float p1) throws DOMException;
        
        void rgbColorChanged(final String p0) throws DOMException;
        
        void rgbColorICCColorChanged(final String p0, final String p1) throws DOMException;
        
        void colorChanged(final short p0, final String p1, final String p2) throws DOMException;
        
        void colorProfileChanged(final String p0) throws DOMException;
        
        void colorsCleared() throws DOMException;
        
        void colorsInitialized(final float p0) throws DOMException;
        
        void colorInsertedBefore(final float p0, final int p1) throws DOMException;
        
        void colorReplaced(final float p0, final int p1) throws DOMException;
        
        void colorRemoved(final int p0) throws DOMException;
        
        void colorAppend(final float p0) throws DOMException;
    }
}
