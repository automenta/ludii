// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.spi;

public class ImageWriterParams
{
    private Integer resolution;
    private Float jpegQuality;
    private Boolean jpegForceBaseline;
    private String compressionMethod;
    
    public Integer getResolution() {
        return this.resolution;
    }
    
    public Float getJPEGQuality() {
        return this.jpegQuality;
    }
    
    public Boolean getJPEGForceBaseline() {
        return this.jpegForceBaseline;
    }
    
    public String getCompressionMethod() {
        return this.compressionMethod;
    }
    
    public void setResolution(final int dpi) {
        this.resolution = dpi;
    }
    
    public void setJPEGQuality(final float quality, final boolean forceBaseline) {
        this.jpegQuality = quality;
        this.jpegForceBaseline = (forceBaseline ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setCompressionMethod(final String method) {
        this.compressionMethod = method;
    }
}
