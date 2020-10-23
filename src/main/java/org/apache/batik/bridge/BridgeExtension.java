// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.Element;
import java.util.Iterator;

public interface BridgeExtension
{
    float getPriority();
    
    Iterator getImplementedExtensions();
    
    String getAuthor();
    
    String getContactAddress();
    
    String getURL();
    
    String getDescription();
    
    void registerTags(final BridgeContext p0);
    
    boolean isDynamicElement(final Element p0);
}
