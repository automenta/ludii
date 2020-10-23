// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.util;

import java.io.Reader;
import org.xml.sax.XMLReader;
import java.io.InputStream;
import java.io.IOException;
import org.w3c.dom.Document;

public interface DocumentFactory
{
    void setValidating(final boolean p0);
    
    boolean isValidating();
    
    Document createDocument(final String p0, final String p1, final String p2) throws IOException;
    
    Document createDocument(final String p0, final String p1, final String p2, final InputStream p3) throws IOException;
    
    Document createDocument(final String p0, final String p1, final String p2, final XMLReader p3) throws IOException;
    
    Document createDocument(final String p0, final String p1, final String p2, final Reader p3) throws IOException;
    
    DocumentDescriptor getDocumentDescriptor();
}
