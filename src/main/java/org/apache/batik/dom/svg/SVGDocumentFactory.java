// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;
import org.w3c.dom.svg.SVGDocument;
import org.apache.batik.dom.util.DocumentFactory;

public interface SVGDocumentFactory extends DocumentFactory
{
    SVGDocument createSVGDocument(final String p0) throws IOException;
    
    SVGDocument createSVGDocument(final String p0, final InputStream p1) throws IOException;
    
    SVGDocument createSVGDocument(final String p0, final Reader p1) throws IOException;
}
