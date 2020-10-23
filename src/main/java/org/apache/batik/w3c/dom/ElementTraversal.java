// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.w3c.dom;

import org.w3c.dom.Element;

public interface ElementTraversal
{
    Element getFirstElementChild();
    
    Element getLastElementChild();
    
    Element getNextElementSibling();
    
    Element getPreviousElementSibling();
    
    int getChildElementCount();
}
