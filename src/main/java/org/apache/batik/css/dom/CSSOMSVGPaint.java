// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.dom;

import org.apache.batik.css.engine.value.svg.ICCColor;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.Value;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGPaint;

public class CSSOMSVGPaint extends CSSOMSVGColor implements SVGPaint
{
    public CSSOMSVGPaint(final ValueProvider vp) {
        super(vp);
    }
    
    @Override
    public void setModificationHandler(final ModificationHandler h) {
        if (!(h instanceof PaintModificationHandler)) {
            throw new IllegalArgumentException();
        }
        super.setModificationHandler(h);
    }
    
    @Override
    public short getColorType() {
        throw new DOMException((short)15, "");
    }
    
    @Override
    public short getPaintType() {
        final Value value = this.valueProvider.getValue();
        Label_0270: {
            switch (value.getCssValueType()) {
                case 1: {
                    switch (value.getPrimitiveType()) {
                        case 21: {
                            final String str = value.getStringValue();
                            if (str.equalsIgnoreCase("none")) {
                                return 101;
                            }
                            if (str.equalsIgnoreCase("currentcolor")) {
                                return 102;
                            }
                            return 1;
                        }
                        case 25: {
                            return 1;
                        }
                        case 20: {
                            return 107;
                        }
                        default: {
                            break Label_0270;
                        }
                    }
                    break;
                }
                case 2: {
                    final Value v0 = value.item(0);
                    final Value v2 = value.item(1);
                    switch (v0.getPrimitiveType()) {
                        case 21: {
                            return 2;
                        }
                        case 20: {
                            if (v2.getCssValueType() == 2) {
                                return 106;
                            }
                            switch (v2.getPrimitiveType()) {
                                case 21: {
                                    final String str2 = v2.getStringValue();
                                    if (str2.equalsIgnoreCase("none")) {
                                        return 103;
                                    }
                                    if (str2.equalsIgnoreCase("currentcolor")) {
                                        return 104;
                                    }
                                    return 105;
                                }
                                case 25: {
                                    return 105;
                                }
                                default: {
                                    return 2;
                                }
                            }
                            break;
                        }
                        case 25: {
                            return 2;
                        }
                        default: {
                            break Label_0270;
                        }
                    }
                    break;
                }
            }
        }
        return 0;
    }
    
    @Override
    public String getUri() {
        switch (this.getPaintType()) {
            case 107: {
                return this.valueProvider.getValue().getStringValue();
            }
            case 103:
            case 104:
            case 105:
            case 106: {
                return this.valueProvider.getValue().item(0).getStringValue();
            }
            default: {
                throw new InternalError();
            }
        }
    }
    
    @Override
    public void setUri(final String uri) {
        if (this.handler == null) {
            throw new DOMException((short)7, "");
        }
        ((PaintModificationHandler)this.handler).uriChanged(uri);
    }
    
    @Override
    public void setPaint(final short paintType, final String uri, final String rgbColor, final String iccColor) {
        if (this.handler == null) {
            throw new DOMException((short)7, "");
        }
        ((PaintModificationHandler)this.handler).paintChanged(paintType, uri, rgbColor, iccColor);
    }
    
    public abstract class AbstractModificationHandler implements PaintModificationHandler
    {
        protected abstract Value getValue();
        
        @Override
        public void redTextChanged(String text) throws DOMException {
            switch (CSSOMSVGPaint.this.getPaintType()) {
                case 1: {
                    text = "rgb(" + text + ", " + this.getValue().getGreen().getCssText() + ", " + this.getValue().getBlue().getCssText() + ')';
                    break;
                }
                case 2: {
                    text = "rgb(" + text + ", " + this.getValue().item(0).getGreen().getCssText() + ", " + this.getValue().item(0).getBlue().getCssText() + ") " + this.getValue().item(1).getCssText();
                    break;
                }
                case 105: {
                    text = this.getValue().item(0) + " rgb(" + text + ", " + this.getValue().item(1).getGreen().getCssText() + ", " + this.getValue().item(1).getBlue().getCssText() + ')';
                    break;
                }
                case 106: {
                    text = this.getValue().item(0) + " rgb(" + text + ", " + this.getValue().item(1).getGreen().getCssText() + ", " + this.getValue().item(1).getBlue().getCssText() + ") " + this.getValue().item(2).getCssText();
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
            this.textChanged(text);
        }
        
        @Override
        public void redFloatValueChanged(final short unit, final float value) throws DOMException {
            String text = null;
            switch (CSSOMSVGPaint.this.getPaintType()) {
                case 1: {
                    text = "rgb(" + FloatValue.getCssText(unit, value) + ", " + this.getValue().getGreen().getCssText() + ", " + this.getValue().getBlue().getCssText() + ')';
                    break;
                }
                case 2: {
                    text = "rgb(" + FloatValue.getCssText(unit, value) + ", " + this.getValue().item(0).getGreen().getCssText() + ", " + this.getValue().item(0).getBlue().getCssText() + ") " + this.getValue().item(1).getCssText();
                    break;
                }
                case 105: {
                    text = this.getValue().item(0) + " rgb(" + FloatValue.getCssText(unit, value) + ", " + this.getValue().item(1).getGreen().getCssText() + ", " + this.getValue().item(1).getBlue().getCssText() + ')';
                    break;
                }
                case 106: {
                    text = this.getValue().item(0) + " rgb(" + FloatValue.getCssText(unit, value) + ", " + this.getValue().item(1).getGreen().getCssText() + ", " + this.getValue().item(1).getBlue().getCssText() + ") " + this.getValue().item(2).getCssText();
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
            this.textChanged(text);
        }
        
        @Override
        public void greenTextChanged(String text) throws DOMException {
            switch (CSSOMSVGPaint.this.getPaintType()) {
                case 1: {
                    text = "rgb(" + this.getValue().getRed().getCssText() + ", " + text + ", " + this.getValue().getBlue().getCssText() + ')';
                    break;
                }
                case 2: {
                    text = "rgb(" + this.getValue().item(0).getRed().getCssText() + ", " + text + ", " + this.getValue().item(0).getBlue().getCssText() + ") " + this.getValue().item(1).getCssText();
                    break;
                }
                case 105: {
                    text = this.getValue().item(0) + " rgb(" + this.getValue().item(1).getRed().getCssText() + ", " + text + ", " + this.getValue().item(1).getBlue().getCssText() + ')';
                    break;
                }
                case 106: {
                    text = this.getValue().item(0) + " rgb(" + this.getValue().item(1).getRed().getCssText() + ", " + text + ", " + this.getValue().item(1).getBlue().getCssText() + ") " + this.getValue().item(2).getCssText();
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
            this.textChanged(text);
        }
        
        @Override
        public void greenFloatValueChanged(final short unit, final float value) throws DOMException {
            String text = null;
            switch (CSSOMSVGPaint.this.getPaintType()) {
                case 1: {
                    text = "rgb(" + this.getValue().getRed().getCssText() + ", " + FloatValue.getCssText(unit, value) + ", " + this.getValue().getBlue().getCssText() + ')';
                    break;
                }
                case 2: {
                    text = "rgb(" + this.getValue().item(0).getRed().getCssText() + ", " + FloatValue.getCssText(unit, value) + ", " + this.getValue().item(0).getBlue().getCssText() + ") " + this.getValue().item(1).getCssText();
                    break;
                }
                case 105: {
                    text = this.getValue().item(0) + " rgb(" + this.getValue().item(1).getRed().getCssText() + ", " + FloatValue.getCssText(unit, value) + ", " + this.getValue().item(1).getBlue().getCssText() + ')';
                    break;
                }
                case 106: {
                    text = this.getValue().item(0) + " rgb(" + this.getValue().item(1).getRed().getCssText() + ", " + FloatValue.getCssText(unit, value) + ", " + this.getValue().item(1).getBlue().getCssText() + ") " + this.getValue().item(2).getCssText();
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
            this.textChanged(text);
        }
        
        @Override
        public void blueTextChanged(String text) throws DOMException {
            switch (CSSOMSVGPaint.this.getPaintType()) {
                case 1: {
                    text = "rgb(" + this.getValue().getRed().getCssText() + ", " + this.getValue().getGreen().getCssText() + ", " + text + ')';
                    break;
                }
                case 2: {
                    text = "rgb(" + this.getValue().item(0).getRed().getCssText() + ", " + this.getValue().item(0).getGreen().getCssText() + ", " + text + ") " + this.getValue().item(1).getCssText();
                    break;
                }
                case 105: {
                    text = this.getValue().item(0) + " rgb(" + this.getValue().item(1).getRed().getCssText() + ", " + this.getValue().item(1).getGreen().getCssText() + ", " + text + ")";
                    break;
                }
                case 106: {
                    text = this.getValue().item(0) + " rgb(" + this.getValue().item(1).getRed().getCssText() + ", " + this.getValue().item(1).getGreen().getCssText() + ", " + text + ") " + this.getValue().item(2).getCssText();
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
            this.textChanged(text);
        }
        
        @Override
        public void blueFloatValueChanged(final short unit, final float value) throws DOMException {
            String text = null;
            switch (CSSOMSVGPaint.this.getPaintType()) {
                case 1: {
                    text = "rgb(" + this.getValue().getRed().getCssText() + ", " + this.getValue().getGreen().getCssText() + ", " + FloatValue.getCssText(unit, value) + ')';
                    break;
                }
                case 2: {
                    text = "rgb(" + this.getValue().item(0).getRed().getCssText() + ", " + this.getValue().item(0).getGreen().getCssText() + ", " + FloatValue.getCssText(unit, value) + ") " + this.getValue().item(1).getCssText();
                    break;
                }
                case 105: {
                    text = this.getValue().item(0) + " rgb(" + this.getValue().item(1).getRed().getCssText() + ", " + this.getValue().item(1).getGreen().getCssText() + ", " + FloatValue.getCssText(unit, value) + ')';
                    break;
                }
                case 106: {
                    text = this.getValue().item(0) + " rgb(" + this.getValue().item(1).getRed().getCssText() + ", " + this.getValue().item(1).getGreen().getCssText() + ", " + FloatValue.getCssText(unit, value) + ") " + this.getValue().item(2).getCssText();
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
            this.textChanged(text);
        }
        
        @Override
        public void rgbColorChanged(String text) throws DOMException {
            switch (CSSOMSVGPaint.this.getPaintType()) {
                case 1: {
                    break;
                }
                case 2: {
                    text += this.getValue().item(1).getCssText();
                    break;
                }
                case 105: {
                    text = this.getValue().item(0).getCssText() + ' ' + text;
                    break;
                }
                case 106: {
                    text = this.getValue().item(0).getCssText() + ' ' + text + ' ' + this.getValue().item(2).getCssText();
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
            switch (CSSOMSVGPaint.this.getPaintType()) {
                case 2: {
                    this.textChanged(rgb + ' ' + icc);
                    break;
                }
                case 106: {
                    this.textChanged(this.getValue().item(0).getCssText() + ' ' + rgb + ' ' + icc);
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
        }
        
        @Override
        public void colorChanged(final short type, final String rgb, final String icc) throws DOMException {
            switch (type) {
                case 102: {
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
            switch (CSSOMSVGPaint.this.getPaintType()) {
                case 2: {
                    final StringBuffer sb = new StringBuffer(this.getValue().item(0).getCssText());
                    sb.append(" icc-color(");
                    sb.append(cp);
                    final ICCColor iccc = (ICCColor)this.getValue().item(1);
                    for (int i = 0; i < iccc.getLength(); ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    sb.append(')');
                    this.textChanged(sb.toString());
                    break;
                }
                case 106: {
                    final StringBuffer sb = new StringBuffer(this.getValue().item(0).getCssText());
                    sb.append(' ');
                    sb.append(this.getValue().item(1).getCssText());
                    sb.append(" icc-color(");
                    sb.append(cp);
                    final ICCColor iccc = (ICCColor)this.getValue().item(1);
                    for (int i = 0; i < iccc.getLength(); ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    sb.append(')');
                    this.textChanged(sb.toString());
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
        }
        
        @Override
        public void colorsCleared() throws DOMException {
            switch (CSSOMSVGPaint.this.getPaintType()) {
                case 2: {
                    final StringBuffer sb = new StringBuffer(this.getValue().item(0).getCssText());
                    sb.append(" icc-color(");
                    final ICCColor iccc = (ICCColor)this.getValue().item(1);
                    sb.append(iccc.getColorProfile());
                    sb.append(')');
                    this.textChanged(sb.toString());
                    break;
                }
                case 106: {
                    final StringBuffer sb = new StringBuffer(this.getValue().item(0).getCssText());
                    sb.append(' ');
                    sb.append(this.getValue().item(1).getCssText());
                    sb.append(" icc-color(");
                    final ICCColor iccc = (ICCColor)this.getValue().item(1);
                    sb.append(iccc.getColorProfile());
                    sb.append(')');
                    this.textChanged(sb.toString());
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
        }
        
        @Override
        public void colorsInitialized(final float f) throws DOMException {
            switch (CSSOMSVGPaint.this.getPaintType()) {
                case 2: {
                    final StringBuffer sb = new StringBuffer(this.getValue().item(0).getCssText());
                    sb.append(" icc-color(");
                    final ICCColor iccc = (ICCColor)this.getValue().item(1);
                    sb.append(iccc.getColorProfile());
                    sb.append(',');
                    sb.append(f);
                    sb.append(')');
                    this.textChanged(sb.toString());
                    break;
                }
                case 106: {
                    final StringBuffer sb = new StringBuffer(this.getValue().item(0).getCssText());
                    sb.append(' ');
                    sb.append(this.getValue().item(1).getCssText());
                    sb.append(" icc-color(");
                    final ICCColor iccc = (ICCColor)this.getValue().item(1);
                    sb.append(iccc.getColorProfile());
                    sb.append(',');
                    sb.append(f);
                    sb.append(')');
                    this.textChanged(sb.toString());
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
        }
        
        @Override
        public void colorInsertedBefore(final float f, final int idx) throws DOMException {
            switch (CSSOMSVGPaint.this.getPaintType()) {
                case 2: {
                    final StringBuffer sb = new StringBuffer(this.getValue().item(0).getCssText());
                    sb.append(" icc-color(");
                    final ICCColor iccc = (ICCColor)this.getValue().item(1);
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
                    break;
                }
                case 106: {
                    final StringBuffer sb = new StringBuffer(this.getValue().item(0).getCssText());
                    sb.append(' ');
                    sb.append(this.getValue().item(1).getCssText());
                    sb.append(" icc-color(");
                    final ICCColor iccc = (ICCColor)this.getValue().item(1);
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
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
        }
        
        @Override
        public void colorReplaced(final float f, final int idx) throws DOMException {
            switch (CSSOMSVGPaint.this.getPaintType()) {
                case 2: {
                    final StringBuffer sb = new StringBuffer(this.getValue().item(0).getCssText());
                    sb.append(" icc-color(");
                    final ICCColor iccc = (ICCColor)this.getValue().item(1);
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
                    break;
                }
                case 106: {
                    final StringBuffer sb = new StringBuffer(this.getValue().item(0).getCssText());
                    sb.append(' ');
                    sb.append(this.getValue().item(1).getCssText());
                    sb.append(" icc-color(");
                    final ICCColor iccc = (ICCColor)this.getValue().item(1);
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
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
        }
        
        @Override
        public void colorRemoved(final int idx) throws DOMException {
            switch (CSSOMSVGPaint.this.getPaintType()) {
                case 2: {
                    final StringBuffer sb = new StringBuffer(this.getValue().item(0).getCssText());
                    sb.append(" icc-color(");
                    final ICCColor iccc = (ICCColor)this.getValue().item(1);
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
                    break;
                }
                case 106: {
                    final StringBuffer sb = new StringBuffer(this.getValue().item(0).getCssText());
                    sb.append(' ');
                    sb.append(this.getValue().item(1).getCssText());
                    sb.append(" icc-color(");
                    final ICCColor iccc = (ICCColor)this.getValue().item(1);
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
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
        }
        
        @Override
        public void colorAppend(final float f) throws DOMException {
            switch (CSSOMSVGPaint.this.getPaintType()) {
                case 2: {
                    final StringBuffer sb = new StringBuffer(this.getValue().item(0).getCssText());
                    sb.append(" icc-color(");
                    final ICCColor iccc = (ICCColor)this.getValue().item(1);
                    sb.append(iccc.getColorProfile());
                    for (int i = 0; i < iccc.getLength(); ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    sb.append(',');
                    sb.append(f);
                    sb.append(')');
                    this.textChanged(sb.toString());
                    break;
                }
                case 106: {
                    final StringBuffer sb = new StringBuffer(this.getValue().item(0).getCssText());
                    sb.append(' ');
                    sb.append(this.getValue().item(1).getCssText());
                    sb.append(" icc-color(");
                    final ICCColor iccc = (ICCColor)this.getValue().item(1);
                    sb.append(iccc.getColorProfile());
                    for (int i = 0; i < iccc.getLength(); ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    sb.append(',');
                    sb.append(f);
                    sb.append(')');
                    this.textChanged(sb.toString());
                    break;
                }
                default: {
                    throw new DOMException((short)7, "");
                }
            }
        }
        
        @Override
        public void uriChanged(final String uri) {
            this.textChanged("url(" + uri + ") none");
        }
        
        @Override
        public void paintChanged(final short type, final String uri, final String rgb, final String icc) {
            switch (type) {
                case 101: {
                    this.textChanged("none");
                    break;
                }
                case 102: {
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
                case 107: {
                    this.textChanged("url(" + uri + ')');
                    break;
                }
                case 103: {
                    this.textChanged("url(" + uri + ") none");
                    break;
                }
                case 104: {
                    this.textChanged("url(" + uri + ") currentcolor");
                    break;
                }
                case 105: {
                    this.textChanged("url(" + uri + ") " + rgb);
                    break;
                }
                case 106: {
                    this.textChanged("url(" + uri + ") " + rgb + ' ' + icc);
                    break;
                }
            }
        }
    }
    
    public interface PaintModificationHandler extends ModificationHandler
    {
        void uriChanged(final String p0);
        
        void paintChanged(final short p0, final String p1, final String p2, final String p3);
    }
}
