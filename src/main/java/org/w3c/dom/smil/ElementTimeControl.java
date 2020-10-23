// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.smil;

import org.w3c.dom.DOMException;

public interface ElementTimeControl
{
    boolean beginElement() throws DOMException;
    
    boolean beginElementAt(final float p0) throws DOMException;
    
    boolean endElement() throws DOMException;
    
    boolean endElementAt(final float p0) throws DOMException;
}
