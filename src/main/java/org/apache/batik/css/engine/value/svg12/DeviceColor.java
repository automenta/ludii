// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg12;

import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.value.AbstractValue;

public class DeviceColor extends AbstractValue
{
    public static final String DEVICE_GRAY_COLOR_FUNCTION = "device-gray";
    public static final String DEVICE_RGB_COLOR_FUNCTION = "device-rgb";
    public static final String DEVICE_CMYK_COLOR_FUNCTION = "device-cmyk";
    public static final String DEVICE_NCHANNEL_COLOR_FUNCTION = "device-nchannel";
    protected boolean nChannel;
    protected int count;
    protected float[] colors;
    
    public DeviceColor(final boolean nChannel) {
        this.colors = new float[5];
        this.nChannel = nChannel;
    }
    
    @Override
    public short getCssValueType() {
        return 3;
    }
    
    public boolean isNChannel() {
        return this.nChannel;
    }
    
    public int getNumberOfColors() throws DOMException {
        return this.count;
    }
    
    public float getColor(final int i) throws DOMException {
        return this.colors[i];
    }
    
    @Override
    public String getCssText() {
        final StringBuffer sb = new StringBuffer(this.count * 8);
        if (this.nChannel) {
            sb.append("device-nchannel");
        }
        else {
            switch (this.count) {
                case 1: {
                    sb.append("device-gray");
                    break;
                }
                case 3: {
                    sb.append("device-rgb");
                    break;
                }
                case 4: {
                    sb.append("device-cmyk");
                    break;
                }
                default: {
                    throw new IllegalStateException("Invalid number of components encountered");
                }
            }
        }
        sb.append('(');
        for (int i = 0; i < this.count; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.colors[i]);
        }
        sb.append(')');
        return sb.toString();
    }
    
    public void append(final float c) {
        if (this.count == this.colors.length) {
            final float[] t = new float[this.count * 2];
            System.arraycopy(this.colors, 0, t, 0, this.count);
            this.colors = t;
        }
        this.colors[this.count++] = c;
    }
    
    @Override
    public String toString() {
        return this.getCssText();
    }
}
