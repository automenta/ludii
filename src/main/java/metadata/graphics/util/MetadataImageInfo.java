// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.util;

import game.types.board.SiteType;

import java.awt.*;
import java.util.List;

public class MetadataImageInfo
{
    public Integer[] line;
    public int site;
    public SiteType element;
    public String path;
    public float scale;
    public Color mainColour;
    public Color secondaryColour;
    public BoardGraphicsType boardGraphicsType;
    public float offestX;
    public float offestY;
    public int rotation;
    public Float[] curve;
    
    public MetadataImageInfo(final int site, final SiteType element, final String path, final float scale) {
        this.site = -1;
        this.offestX = 0.0f;
        this.offestY = 0.0f;
        this.rotation = 0;
        this.site = site;
        this.element = element;
        this.path = path;
        this.scale = scale;
    }
    
    public MetadataImageInfo(final Integer[] line, final Color mainColour, final float scale) {
        this.site = -1;
        this.offestX = 0.0f;
        this.offestY = 0.0f;
        this.rotation = 0;
        this.line = line;
        this.mainColour = mainColour;
        this.scale = scale;
    }
    
    public MetadataImageInfo(final Integer[] line, final Color mainColour, final float scale, final Float[] curve) {
        this.site = -1;
        this.offestX = 0.0f;
        this.offestY = 0.0f;
        this.rotation = 0;
        this.line = line;
        this.mainColour = mainColour;
        this.scale = scale;
        this.curve = curve;
    }
    
    public MetadataImageInfo(final int site, final SiteType element, final String path, final float scale, final Color mainColour) {
        this.site = -1;
        this.offestX = 0.0f;
        this.offestY = 0.0f;
        this.rotation = 0;
        this.site = site;
        this.element = element;
        this.path = path;
        this.scale = scale;
        this.mainColour = mainColour;
    }
    
    public MetadataImageInfo(final int site, final SiteType element, final String path, final float scale, final Color mainColour, final Color secondaryColour) {
        this.site = -1;
        this.offestX = 0.0f;
        this.offestY = 0.0f;
        this.rotation = 0;
        this.site = site;
        this.element = element;
        this.path = path;
        this.scale = scale;
        this.mainColour = mainColour;
        this.secondaryColour = secondaryColour;
    }
    
    public MetadataImageInfo(final int site, final SiteType element, final String path, final float scale, final Color mainColour, final Color secondaryColour, final int rotation) {
        this.site = -1;
        this.offestX = 0.0f;
        this.offestY = 0.0f;
        this.rotation = 0;
        this.site = site;
        this.element = element;
        this.path = path;
        this.scale = scale;
        this.mainColour = mainColour;
        this.secondaryColour = secondaryColour;
        this.rotation = rotation;
    }
    
    public MetadataImageInfo(final int site, final SiteType element, final String path, final float scale, final Color mainColour, final Color secondaryColour, final int rotation, final float offsetX, final float offsetY) {
        this.site = -1;
        this.offestX = 0.0f;
        this.offestY = 0.0f;
        this.rotation = 0;
        this.site = site;
        this.element = element;
        this.path = path;
        this.scale = scale;
        this.mainColour = mainColour;
        this.secondaryColour = secondaryColour;
        this.rotation = rotation;
        this.offestX = offsetX;
        this.offestY = offsetY;
    }
    
    public MetadataImageInfo(final int site, final SiteType element, final BoardGraphicsType boardGraphicsType, final Color mainColour) {
        this.site = -1;
        this.offestX = 0.0f;
        this.offestY = 0.0f;
        this.rotation = 0;
        this.site = site;
        this.element = element;
        this.mainColour = mainColour;
        this.boardGraphicsType = boardGraphicsType;
    }
    
    public static boolean containsSite(final List<MetadataImageInfo> drawImageInfos, final int i, final SiteType e, final BoardGraphicsType b) {
        for (final MetadataImageInfo drawImageInfo : drawImageInfos) {
            if (drawImageInfo.site == i && (e == null || drawImageInfo.element == null || drawImageInfo.element == e) && (b == null || drawImageInfo.boardGraphicsType == null || drawImageInfo.boardGraphicsType == b)) {
                return true;
            }
        }
        return false;
    }
    
    public static Color getMainColour(final List<MetadataImageInfo> drawImageInfos, final int i, final SiteType e, final BoardGraphicsType b) {
        for (final MetadataImageInfo drawImageInfo : drawImageInfos) {
            if (drawImageInfo.site == i && (e == null || drawImageInfo.element == null || drawImageInfo.element == e) && (b == null || drawImageInfo.boardGraphicsType == null || drawImageInfo.boardGraphicsType == b)) {
                return drawImageInfo.mainColour;
            }
        }
        return null;
    }
    
    public static Color getSecondaryColour(final List<MetadataImageInfo> drawImageInfos, final int i, final SiteType e, final BoardGraphicsType b) {
        for (final MetadataImageInfo drawImageInfo : drawImageInfos) {
            if (drawImageInfo.site == i && (e == null || drawImageInfo.element == null || drawImageInfo.element == e) && (b == null || drawImageInfo.boardGraphicsType == null || drawImageInfo.boardGraphicsType == b)) {
                return drawImageInfo.secondaryColour;
            }
        }
        return null;
    }
}
