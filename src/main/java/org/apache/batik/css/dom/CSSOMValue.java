// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.dom;

import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.StringValue;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.Value;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.css.Rect;
import org.w3c.dom.css.Counter;
import org.w3c.dom.css.CSSValueList;
import org.w3c.dom.css.CSSPrimitiveValue;

public class CSSOMValue implements CSSPrimitiveValue, CSSValueList, Counter, Rect, RGBColor
{
    protected ValueProvider valueProvider;
    protected ModificationHandler handler;
    protected LeftComponent leftComponent;
    protected RightComponent rightComponent;
    protected BottomComponent bottomComponent;
    protected TopComponent topComponent;
    protected RedComponent redComponent;
    protected GreenComponent greenComponent;
    protected BlueComponent blueComponent;
    protected CSSValue[] items;
    
    public CSSOMValue(final ValueProvider vp) {
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
        this.handler.textChanged(cssText);
    }
    
    @Override
    public short getCssValueType() {
        return this.valueProvider.getValue().getCssValueType();
    }
    
    @Override
    public short getPrimitiveType() {
        return this.valueProvider.getValue().getPrimitiveType();
    }
    
    @Override
    public void setFloatValue(final short unitType, final float floatValue) throws DOMException {
        if (this.handler == null) {
            throw new DOMException((short)7, "");
        }
        this.handler.floatValueChanged(unitType, floatValue);
    }
    
    @Override
    public float getFloatValue(final short unitType) throws DOMException {
        return convertFloatValue(unitType, this.valueProvider.getValue());
    }
    
    public static float convertFloatValue(final short unitType, final Value value) {
        switch (unitType) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 18: {
                if (value.getPrimitiveType() == unitType) {
                    return value.getFloatValue();
                }
                break;
            }
            case 6: {
                return toCentimeters(value);
            }
            case 7: {
                return toMillimeters(value);
            }
            case 8: {
                return toInches(value);
            }
            case 9: {
                return toPoints(value);
            }
            case 10: {
                return toPicas(value);
            }
            case 11: {
                return toDegrees(value);
            }
            case 12: {
                return toRadians(value);
            }
            case 13: {
                return toGradians(value);
            }
            case 14: {
                return toMilliseconds(value);
            }
            case 15: {
                return toSeconds(value);
            }
            case 16: {
                return toHertz(value);
            }
            case 17: {
                return tokHertz(value);
            }
        }
        throw new DOMException((short)15, "");
    }
    
    protected static float toCentimeters(final Value value) {
        switch (value.getPrimitiveType()) {
            case 6: {
                return value.getFloatValue();
            }
            case 7: {
                return value.getFloatValue() / 10.0f;
            }
            case 8: {
                return value.getFloatValue() * 2.54f;
            }
            case 9: {
                return value.getFloatValue() * 2.54f / 72.0f;
            }
            case 10: {
                return value.getFloatValue() * 2.54f / 6.0f;
            }
            default: {
                throw new DOMException((short)15, "");
            }
        }
    }
    
    protected static float toInches(final Value value) {
        switch (value.getPrimitiveType()) {
            case 6: {
                return value.getFloatValue() / 2.54f;
            }
            case 7: {
                return value.getFloatValue() / 25.4f;
            }
            case 8: {
                return value.getFloatValue();
            }
            case 9: {
                return value.getFloatValue() / 72.0f;
            }
            case 10: {
                return value.getFloatValue() / 6.0f;
            }
            default: {
                throw new DOMException((short)15, "");
            }
        }
    }
    
    protected static float toMillimeters(final Value value) {
        switch (value.getPrimitiveType()) {
            case 6: {
                return value.getFloatValue() * 10.0f;
            }
            case 7: {
                return value.getFloatValue();
            }
            case 8: {
                return value.getFloatValue() * 25.4f;
            }
            case 9: {
                return value.getFloatValue() * 25.4f / 72.0f;
            }
            case 10: {
                return value.getFloatValue() * 25.4f / 6.0f;
            }
            default: {
                throw new DOMException((short)15, "");
            }
        }
    }
    
    protected static float toPoints(final Value value) {
        switch (value.getPrimitiveType()) {
            case 6: {
                return value.getFloatValue() * 72.0f / 2.54f;
            }
            case 7: {
                return value.getFloatValue() * 72.0f / 25.4f;
            }
            case 8: {
                return value.getFloatValue() * 72.0f;
            }
            case 9: {
                return value.getFloatValue();
            }
            case 10: {
                return value.getFloatValue() * 12.0f;
            }
            default: {
                throw new DOMException((short)15, "");
            }
        }
    }
    
    protected static float toPicas(final Value value) {
        switch (value.getPrimitiveType()) {
            case 6: {
                return value.getFloatValue() * 6.0f / 2.54f;
            }
            case 7: {
                return value.getFloatValue() * 6.0f / 25.4f;
            }
            case 8: {
                return value.getFloatValue() * 6.0f;
            }
            case 9: {
                return value.getFloatValue() / 12.0f;
            }
            case 10: {
                return value.getFloatValue();
            }
            default: {
                throw new DOMException((short)15, "");
            }
        }
    }
    
    protected static float toDegrees(final Value value) {
        switch (value.getPrimitiveType()) {
            case 11: {
                return value.getFloatValue();
            }
            case 12: {
                return (float)Math.toDegrees(value.getFloatValue());
            }
            case 13: {
                return value.getFloatValue() * 9.0f / 5.0f;
            }
            default: {
                throw new DOMException((short)15, "");
            }
        }
    }
    
    protected static float toRadians(final Value value) {
        switch (value.getPrimitiveType()) {
            case 11: {
                return value.getFloatValue() * 5.0f / 9.0f;
            }
            case 12: {
                return value.getFloatValue();
            }
            case 13: {
                return (float)(value.getFloatValue() * 100.0f / 3.141592653589793);
            }
            default: {
                throw new DOMException((short)15, "");
            }
        }
    }
    
    protected static float toGradians(final Value value) {
        switch (value.getPrimitiveType()) {
            case 11: {
                return (float)(value.getFloatValue() * 3.141592653589793 / 180.0);
            }
            case 12: {
                return (float)(value.getFloatValue() * 3.141592653589793 / 100.0);
            }
            case 13: {
                return value.getFloatValue();
            }
            default: {
                throw new DOMException((short)15, "");
            }
        }
    }
    
    protected static float toMilliseconds(final Value value) {
        switch (value.getPrimitiveType()) {
            case 14: {
                return value.getFloatValue();
            }
            case 15: {
                return value.getFloatValue() * 1000.0f;
            }
            default: {
                throw new DOMException((short)15, "");
            }
        }
    }
    
    protected static float toSeconds(final Value value) {
        switch (value.getPrimitiveType()) {
            case 14: {
                return value.getFloatValue() / 1000.0f;
            }
            case 15: {
                return value.getFloatValue();
            }
            default: {
                throw new DOMException((short)15, "");
            }
        }
    }
    
    protected static float toHertz(final Value value) {
        switch (value.getPrimitiveType()) {
            case 16: {
                return value.getFloatValue();
            }
            case 17: {
                return value.getFloatValue() / 1000.0f;
            }
            default: {
                throw new DOMException((short)15, "");
            }
        }
    }
    
    protected static float tokHertz(final Value value) {
        switch (value.getPrimitiveType()) {
            case 16: {
                return value.getFloatValue() * 1000.0f;
            }
            case 17: {
                return value.getFloatValue();
            }
            default: {
                throw new DOMException((short)15, "");
            }
        }
    }
    
    @Override
    public void setStringValue(final short stringType, final String stringValue) throws DOMException {
        if (this.handler == null) {
            throw new DOMException((short)7, "");
        }
        this.handler.stringValueChanged(stringType, stringValue);
    }
    
    @Override
    public String getStringValue() throws DOMException {
        return this.valueProvider.getValue().getStringValue();
    }
    
    @Override
    public Counter getCounterValue() throws DOMException {
        return this;
    }
    
    @Override
    public Rect getRectValue() throws DOMException {
        return this;
    }
    
    @Override
    public RGBColor getRGBColorValue() throws DOMException {
        return this;
    }
    
    @Override
    public int getLength() {
        return this.valueProvider.getValue().getLength();
    }
    
    @Override
    public CSSValue item(final int index) {
        final int len = this.valueProvider.getValue().getLength();
        if (index < 0 || index >= len) {
            return null;
        }
        if (this.items == null) {
            this.items = new CSSValue[this.valueProvider.getValue().getLength()];
        }
        else if (this.items.length < len) {
            final CSSValue[] nitems = new CSSValue[len];
            System.arraycopy(this.items, 0, nitems, 0, this.items.length);
            this.items = nitems;
        }
        CSSValue result = this.items[index];
        if (result == null) {
            result = (this.items[index] = new ListComponent(index));
        }
        return result;
    }
    
    @Override
    public String getIdentifier() {
        return this.valueProvider.getValue().getIdentifier();
    }
    
    @Override
    public String getListStyle() {
        return this.valueProvider.getValue().getListStyle();
    }
    
    @Override
    public String getSeparator() {
        return this.valueProvider.getValue().getSeparator();
    }
    
    @Override
    public CSSPrimitiveValue getTop() {
        this.valueProvider.getValue().getTop();
        if (this.topComponent == null) {
            this.topComponent = new TopComponent();
        }
        return this.topComponent;
    }
    
    @Override
    public CSSPrimitiveValue getRight() {
        this.valueProvider.getValue().getRight();
        if (this.rightComponent == null) {
            this.rightComponent = new RightComponent();
        }
        return this.rightComponent;
    }
    
    @Override
    public CSSPrimitiveValue getBottom() {
        this.valueProvider.getValue().getBottom();
        if (this.bottomComponent == null) {
            this.bottomComponent = new BottomComponent();
        }
        return this.bottomComponent;
    }
    
    @Override
    public CSSPrimitiveValue getLeft() {
        this.valueProvider.getValue().getLeft();
        if (this.leftComponent == null) {
            this.leftComponent = new LeftComponent();
        }
        return this.leftComponent;
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
    
    public abstract static class AbstractModificationHandler implements ModificationHandler
    {
        protected abstract Value getValue();
        
        @Override
        public void floatValueChanged(final short unit, final float value) throws DOMException {
            this.textChanged(FloatValue.getCssText(unit, value));
        }
        
        @Override
        public void stringValueChanged(final short type, final String value) throws DOMException {
            this.textChanged(StringValue.getCssText(type, value));
        }
        
        @Override
        public void leftTextChanged(String text) throws DOMException {
            final Value val = this.getValue();
            text = "rect(" + val.getTop().getCssText() + ", " + val.getRight().getCssText() + ", " + val.getBottom().getCssText() + ", " + text + ')';
            this.textChanged(text);
        }
        
        @Override
        public void leftFloatValueChanged(final short unit, final float value) throws DOMException {
            final Value val = this.getValue();
            final String text = "rect(" + val.getTop().getCssText() + ", " + val.getRight().getCssText() + ", " + val.getBottom().getCssText() + ", " + FloatValue.getCssText(unit, value) + ')';
            this.textChanged(text);
        }
        
        @Override
        public void topTextChanged(String text) throws DOMException {
            final Value val = this.getValue();
            text = "rect(" + text + ", " + val.getRight().getCssText() + ", " + val.getBottom().getCssText() + ", " + val.getLeft().getCssText() + ')';
            this.textChanged(text);
        }
        
        @Override
        public void topFloatValueChanged(final short unit, final float value) throws DOMException {
            final Value val = this.getValue();
            final String text = "rect(" + FloatValue.getCssText(unit, value) + ", " + val.getRight().getCssText() + ", " + val.getBottom().getCssText() + ", " + val.getLeft().getCssText() + ')';
            this.textChanged(text);
        }
        
        @Override
        public void rightTextChanged(String text) throws DOMException {
            final Value val = this.getValue();
            text = "rect(" + val.getTop().getCssText() + ", " + text + ", " + val.getBottom().getCssText() + ", " + val.getLeft().getCssText() + ')';
            this.textChanged(text);
        }
        
        @Override
        public void rightFloatValueChanged(final short unit, final float value) throws DOMException {
            final Value val = this.getValue();
            final String text = "rect(" + val.getTop().getCssText() + ", " + FloatValue.getCssText(unit, value) + ", " + val.getBottom().getCssText() + ", " + val.getLeft().getCssText() + ')';
            this.textChanged(text);
        }
        
        @Override
        public void bottomTextChanged(String text) throws DOMException {
            final Value val = this.getValue();
            text = "rect(" + val.getTop().getCssText() + ", " + val.getRight().getCssText() + ", " + text + ", " + val.getLeft().getCssText() + ')';
            this.textChanged(text);
        }
        
        @Override
        public void bottomFloatValueChanged(final short unit, final float value) throws DOMException {
            final Value val = this.getValue();
            final String text = "rect(" + val.getTop().getCssText() + ", " + val.getRight().getCssText() + ", " + FloatValue.getCssText(unit, value) + ", " + val.getLeft().getCssText() + ')';
            this.textChanged(text);
        }
        
        @Override
        public void redTextChanged(String text) throws DOMException {
            final Value val = this.getValue();
            text = "rgb(" + text + ", " + val.getGreen().getCssText() + ", " + val.getBlue().getCssText() + ')';
            this.textChanged(text);
        }
        
        @Override
        public void redFloatValueChanged(final short unit, final float value) throws DOMException {
            final Value val = this.getValue();
            final String text = "rgb(" + FloatValue.getCssText(unit, value) + ", " + val.getGreen().getCssText() + ", " + val.getBlue().getCssText() + ')';
            this.textChanged(text);
        }
        
        @Override
        public void greenTextChanged(String text) throws DOMException {
            final Value val = this.getValue();
            text = "rgb(" + val.getRed().getCssText() + ", " + text + ", " + val.getBlue().getCssText() + ')';
            this.textChanged(text);
        }
        
        @Override
        public void greenFloatValueChanged(final short unit, final float value) throws DOMException {
            final Value val = this.getValue();
            final String text = "rgb(" + val.getRed().getCssText() + ", " + FloatValue.getCssText(unit, value) + ", " + val.getBlue().getCssText() + ')';
            this.textChanged(text);
        }
        
        @Override
        public void blueTextChanged(String text) throws DOMException {
            final Value val = this.getValue();
            text = "rgb(" + val.getRed().getCssText() + ", " + val.getGreen().getCssText() + ", " + text + ')';
            this.textChanged(text);
        }
        
        @Override
        public void blueFloatValueChanged(final short unit, final float value) throws DOMException {
            final Value val = this.getValue();
            final String text = "rgb(" + val.getRed().getCssText() + ", " + val.getGreen().getCssText() + ", " + FloatValue.getCssText(unit, value) + ')';
            this.textChanged(text);
        }
        
        @Override
        public void listTextChanged(final int idx, String text) throws DOMException {
            final ListValue lv = (ListValue)this.getValue();
            final int len = lv.getLength();
            final StringBuffer sb = new StringBuffer(len * 8);
            for (int i = 0; i < idx; ++i) {
                sb.append(lv.item(i).getCssText());
                sb.append(lv.getSeparatorChar());
            }
            sb.append(text);
            for (int i = idx + 1; i < len; ++i) {
                sb.append(lv.getSeparatorChar());
                sb.append(lv.item(i).getCssText());
            }
            text = sb.toString();
            this.textChanged(text);
        }
        
        @Override
        public void listFloatValueChanged(final int idx, final short unit, final float value) throws DOMException {
            final ListValue lv = (ListValue)this.getValue();
            final int len = lv.getLength();
            final StringBuffer sb = new StringBuffer(len * 8);
            for (int i = 0; i < idx; ++i) {
                sb.append(lv.item(i).getCssText());
                sb.append(lv.getSeparatorChar());
            }
            sb.append(FloatValue.getCssText(unit, value));
            for (int i = idx + 1; i < len; ++i) {
                sb.append(lv.getSeparatorChar());
                sb.append(lv.item(i).getCssText());
            }
            this.textChanged(sb.toString());
        }
        
        @Override
        public void listStringValueChanged(final int idx, final short unit, final String value) throws DOMException {
            final ListValue lv = (ListValue)this.getValue();
            final int len = lv.getLength();
            final StringBuffer sb = new StringBuffer(len * 8);
            for (int i = 0; i < idx; ++i) {
                sb.append(lv.item(i).getCssText());
                sb.append(lv.getSeparatorChar());
            }
            sb.append(StringValue.getCssText(unit, value));
            for (int i = idx + 1; i < len; ++i) {
                sb.append(lv.getSeparatorChar());
                sb.append(lv.item(i).getCssText());
            }
            this.textChanged(sb.toString());
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
            return CSSOMValue.this.valueProvider.getValue().getStringValue();
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
    
    protected class LeftComponent extends FloatComponent
    {
        @Override
        protected Value getValue() {
            return CSSOMValue.this.valueProvider.getValue().getLeft();
        }
        
        @Override
        public void setCssText(final String cssText) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.leftTextChanged(cssText);
        }
        
        @Override
        public void setFloatValue(final short unitType, final float floatValue) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.leftFloatValueChanged(unitType, floatValue);
        }
    }
    
    protected class TopComponent extends FloatComponent
    {
        @Override
        protected Value getValue() {
            return CSSOMValue.this.valueProvider.getValue().getTop();
        }
        
        @Override
        public void setCssText(final String cssText) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.topTextChanged(cssText);
        }
        
        @Override
        public void setFloatValue(final short unitType, final float floatValue) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.topFloatValueChanged(unitType, floatValue);
        }
    }
    
    protected class RightComponent extends FloatComponent
    {
        @Override
        protected Value getValue() {
            return CSSOMValue.this.valueProvider.getValue().getRight();
        }
        
        @Override
        public void setCssText(final String cssText) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.rightTextChanged(cssText);
        }
        
        @Override
        public void setFloatValue(final short unitType, final float floatValue) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.rightFloatValueChanged(unitType, floatValue);
        }
    }
    
    protected class BottomComponent extends FloatComponent
    {
        @Override
        protected Value getValue() {
            return CSSOMValue.this.valueProvider.getValue().getBottom();
        }
        
        @Override
        public void setCssText(final String cssText) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.bottomTextChanged(cssText);
        }
        
        @Override
        public void setFloatValue(final short unitType, final float floatValue) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.bottomFloatValueChanged(unitType, floatValue);
        }
    }
    
    protected class RedComponent extends FloatComponent
    {
        @Override
        protected Value getValue() {
            return CSSOMValue.this.valueProvider.getValue().getRed();
        }
        
        @Override
        public void setCssText(final String cssText) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.redTextChanged(cssText);
        }
        
        @Override
        public void setFloatValue(final short unitType, final float floatValue) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.redFloatValueChanged(unitType, floatValue);
        }
    }
    
    protected class GreenComponent extends FloatComponent
    {
        @Override
        protected Value getValue() {
            return CSSOMValue.this.valueProvider.getValue().getGreen();
        }
        
        @Override
        public void setCssText(final String cssText) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.greenTextChanged(cssText);
        }
        
        @Override
        public void setFloatValue(final short unitType, final float floatValue) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.greenFloatValueChanged(unitType, floatValue);
        }
    }
    
    protected class BlueComponent extends FloatComponent
    {
        @Override
        protected Value getValue() {
            return CSSOMValue.this.valueProvider.getValue().getBlue();
        }
        
        @Override
        public void setCssText(final String cssText) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.blueTextChanged(cssText);
        }
        
        @Override
        public void setFloatValue(final short unitType, final float floatValue) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.blueFloatValueChanged(unitType, floatValue);
        }
    }
    
    protected class ListComponent extends AbstractComponent
    {
        protected int index;
        
        public ListComponent(final int idx) {
            this.index = idx;
        }
        
        @Override
        protected Value getValue() {
            if (this.index >= CSSOMValue.this.valueProvider.getValue().getLength()) {
                throw new DOMException((short)7, "");
            }
            return CSSOMValue.this.valueProvider.getValue().item(this.index);
        }
        
        @Override
        public void setCssText(final String cssText) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.listTextChanged(this.index, cssText);
        }
        
        @Override
        public void setFloatValue(final short unitType, final float floatValue) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.listFloatValueChanged(this.index, unitType, floatValue);
        }
        
        @Override
        public void setStringValue(final short stringType, final String stringValue) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException((short)7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.listStringValueChanged(this.index, stringType, stringValue);
        }
    }
    
    public interface ValueProvider
    {
        Value getValue();
    }
    
    public interface ModificationHandler
    {
        void textChanged(final String p0) throws DOMException;
        
        void floatValueChanged(final short p0, final float p1) throws DOMException;
        
        void stringValueChanged(final short p0, final String p1) throws DOMException;
        
        void leftTextChanged(final String p0) throws DOMException;
        
        void leftFloatValueChanged(final short p0, final float p1) throws DOMException;
        
        void topTextChanged(final String p0) throws DOMException;
        
        void topFloatValueChanged(final short p0, final float p1) throws DOMException;
        
        void rightTextChanged(final String p0) throws DOMException;
        
        void rightFloatValueChanged(final short p0, final float p1) throws DOMException;
        
        void bottomTextChanged(final String p0) throws DOMException;
        
        void bottomFloatValueChanged(final short p0, final float p1) throws DOMException;
        
        void redTextChanged(final String p0) throws DOMException;
        
        void redFloatValueChanged(final short p0, final float p1) throws DOMException;
        
        void greenTextChanged(final String p0) throws DOMException;
        
        void greenFloatValueChanged(final short p0, final float p1) throws DOMException;
        
        void blueTextChanged(final String p0) throws DOMException;
        
        void blueFloatValueChanged(final short p0, final float p1) throws DOMException;
        
        void listTextChanged(final int p0, final String p1) throws DOMException;
        
        void listFloatValueChanged(final int p0, final short p1, final float p2) throws DOMException;
        
        void listStringValueChanged(final int p0, final short p1, final String p2) throws DOMException;
    }
}
