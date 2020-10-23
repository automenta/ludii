// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

public interface DomExtension
{
    float getPriority();
    
    String getAuthor();
    
    String getContactAddress();
    
    String getURL();
    
    String getDescription();
    
    void registerTags(final ExtensibleDOMImplementation p0);
}
