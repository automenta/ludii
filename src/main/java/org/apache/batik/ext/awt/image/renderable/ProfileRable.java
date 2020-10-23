// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.ProfileRed;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.util.Map;
import org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent;

public class ProfileRable extends AbstractRable
{
    private ICCColorSpaceWithIntent colorSpace;
    
    public ProfileRable(final Filter src, final ICCColorSpaceWithIntent colorSpace) {
        super(src);
        this.colorSpace = colorSpace;
    }
    
    public void setSource(final Filter src) {
        this.init(src, null);
    }
    
    public Filter getSource() {
        return this.getSources().get(0);
    }
    
    public void setColorSpace(final ICCColorSpaceWithIntent colorSpace) {
        this.touch();
        this.colorSpace = colorSpace;
    }
    
    public ICCColorSpaceWithIntent getColorSpace() {
        return this.colorSpace;
    }
    
    @Override
    public RenderedImage createRendering(final RenderContext rc) {
        final RenderedImage srcRI = this.getSource().createRendering(rc);
        if (srcRI == null) {
            return null;
        }
        final CachableRed srcCR = GraphicsUtil.wrap(srcRI);
        return new ProfileRed(srcCR, this.colorSpace);
    }
}
