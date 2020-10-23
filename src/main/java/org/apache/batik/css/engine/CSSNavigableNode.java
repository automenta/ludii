// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine;

import org.w3c.dom.Node;

public interface CSSNavigableNode
{
    Node getCSSParentNode();
    
    Node getCSSPreviousSibling();
    
    Node getCSSNextSibling();
    
    Node getCSSFirstChild();
    
    Node getCSSLastChild();
    
    boolean isHiddenFromSelectors();
}
