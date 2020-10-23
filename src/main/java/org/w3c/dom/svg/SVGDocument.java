// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.Document;
import org.w3c.dom.events.DocumentEvent;

public interface SVGDocument extends Document, DocumentEvent
{
    String getTitle();
    
    String getReferrer();
    
    String getDomain();
    
    String getURL();
    
    SVGSVGElement getRootElement();
}
