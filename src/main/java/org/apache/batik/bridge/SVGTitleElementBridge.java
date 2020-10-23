// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

public class SVGTitleElementBridge extends SVGDescriptiveElementBridge
{
    @Override
    public String getLocalName() {
        return "title";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGTitleElementBridge();
    }
}
