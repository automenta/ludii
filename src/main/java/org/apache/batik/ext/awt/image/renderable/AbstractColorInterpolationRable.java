// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import java.awt.image.RenderedImage;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import java.awt.color.ColorSpace;
import java.util.List;
import java.util.Map;

public abstract class AbstractColorInterpolationRable extends AbstractRable
{
    protected boolean csLinear;
    
    protected AbstractColorInterpolationRable() {
        this.csLinear = true;
    }
    
    protected AbstractColorInterpolationRable(final Filter src) {
        super(src);
        this.csLinear = true;
    }
    
    protected AbstractColorInterpolationRable(final Filter src, final Map props) {
        super(src, props);
        this.csLinear = true;
    }
    
    protected AbstractColorInterpolationRable(final List srcs) {
        super(srcs);
        this.csLinear = true;
    }
    
    protected AbstractColorInterpolationRable(final List srcs, final Map props) {
        super(srcs, props);
        this.csLinear = true;
    }
    
    public boolean isColorSpaceLinear() {
        return this.csLinear;
    }
    
    public void setColorSpaceLinear(final boolean csLinear) {
        this.touch();
        this.csLinear = csLinear;
    }
    
    public ColorSpace getOperationColorSpace() {
        if (this.csLinear) {
            return ColorSpace.getInstance(1004);
        }
        return ColorSpace.getInstance(1000);
    }
    
    protected CachableRed convertSourceCS(final CachableRed cr) {
        if (this.csLinear) {
            return GraphicsUtil.convertToLsRGB(cr);
        }
        return GraphicsUtil.convertTosRGB(cr);
    }
    
    protected CachableRed convertSourceCS(final RenderedImage ri) {
        return this.convertSourceCS(GraphicsUtil.wrap(ri));
    }
}
