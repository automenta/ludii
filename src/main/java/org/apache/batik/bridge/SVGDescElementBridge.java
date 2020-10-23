// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

public class SVGDescElementBridge extends SVGDescriptiveElementBridge
{
    @Override
    public String getLocalName() {
        return "desc";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGDescElementBridge();
    }
}
