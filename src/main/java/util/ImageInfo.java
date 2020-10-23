// 
// Decompiled by Procyon v0.5.36
// 

package util;

import game.equipment.component.Component;
import game.types.board.SiteType;

import java.awt.*;

public class ImageInfo
{
    private final Point drawPosn;
    private final double transparency;
    private final int rotation;
    private final int site;
    private final int level;
    private final SiteType graphElementType;
    private final Component component;
    private final int localState;
    private final int containerIndex;
    private final int imageSize;
    private final int count;
    
    public ImageInfo(final Point drawPosn, final int site, final int level, final SiteType graphElementType, final Component component, final int localState, final double transparency, final int rotation, final int containerIndex, final int imageSize, final int count) {
        this.drawPosn = drawPosn;
        this.transparency = transparency;
        this.rotation = rotation;
        this.site = site;
        this.level = level;
        this.graphElementType = graphElementType;
        this.component = component;
        this.localState = localState;
        this.containerIndex = containerIndex;
        this.imageSize = imageSize;
        this.count = count;
    }
    
    public ImageInfo(final Point drawPosn, final int site, final int level, final SiteType graphElementType) {
        this.drawPosn = drawPosn;
        this.transparency = 0.0;
        this.rotation = 0;
        this.site = site;
        this.level = level;
        this.graphElementType = graphElementType;
        this.component = null;
        this.localState = 0;
        this.containerIndex = 0;
        this.imageSize = 0;
        this.count = 0;
    }
    
    public Point drawPosn() {
        return this.drawPosn;
    }
    
    public double transparency() {
        return this.transparency;
    }
    
    public int rotation() {
        return this.rotation;
    }
    
    public int site() {
        return this.site;
    }
    
    public int level() {
        return this.level;
    }
    
    public SiteType graphElementType() {
        return this.graphElementType;
    }
    
    public Component component() {
        return this.component;
    }
    
    public int localState() {
        return this.localState;
    }
    
    public int containerIndex() {
        return this.containerIndex;
    }
    
    public int imageSize() {
        return this.imageSize;
    }
    
    public int count() {
        return this.count;
    }
}
