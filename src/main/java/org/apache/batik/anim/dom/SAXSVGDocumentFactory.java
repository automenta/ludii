// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import java.util.MissingResourceException;
import java.io.StringReader;
import org.xml.sax.SAXException;
import org.w3c.dom.DOMImplementation;
import java.net.MalformedURLException;
import org.apache.batik.dom.AbstractDocument;
import org.xml.sax.InputSource;
import java.util.Iterator;
import org.apache.batik.util.MimeTypeConstants;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Document;
import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;
import org.w3c.dom.svg.SVGDocument;
import java.util.Properties;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.dom.util.SAXDocumentFactory;

public class SAXSVGDocumentFactory extends SAXDocumentFactory implements SVGDocumentFactory
{
    public static final Object LOCK;
    public static final String KEY_PUBLIC_IDS = "publicIds";
    public static final String KEY_SKIPPABLE_PUBLIC_IDS = "skippablePublicIds";
    public static final String KEY_SKIP_DTD = "skipDTD";
    public static final String KEY_SYSTEM_ID = "systemId.";
    protected static final String DTDIDS = "org.apache.batik.anim.dom.resources.dtdids";
    protected static final String HTTP_CHARSET = "charset";
    protected static String dtdids;
    protected static String skippable_dtdids;
    protected static String skip_dtd;
    protected static Properties dtdProps;
    
    public SAXSVGDocumentFactory(final String parser) {
        super(SVGDOMImplementation.getDOMImplementation(), parser);
    }
    
    public SAXSVGDocumentFactory(final String parser, final boolean dd) {
        super(SVGDOMImplementation.getDOMImplementation(), parser, dd);
    }
    
    @Override
    public SVGDocument createSVGDocument(final String uri) throws IOException {
        return (SVGDocument)this.createDocument(uri);
    }
    
    @Override
    public SVGDocument createSVGDocument(final String uri, final InputStream inp) throws IOException {
        return (SVGDocument)this.createDocument(uri, inp);
    }
    
    @Override
    public SVGDocument createSVGDocument(final String uri, final Reader r) throws IOException {
        return (SVGDocument)this.createDocument(uri, r);
    }
    
    @Override
    public Document createDocument(String uri) throws IOException {
        final ParsedURL purl = new ParsedURL(uri);
        final InputStream is = purl.openStream(MimeTypeConstants.MIME_TYPES_SVG_LIST.iterator());
        uri = purl.getPostConnectionURL();
        final InputSource isrc = new InputSource(is);
        String contentType = purl.getContentType();
        int cindex = -1;
        if (contentType != null) {
            contentType = contentType.toLowerCase();
            cindex = contentType.indexOf("charset");
        }
        String charset = null;
        if (cindex != -1) {
            final int i = cindex + "charset".length();
            int eqIdx = contentType.indexOf(61, i);
            if (eqIdx != -1) {
                ++eqIdx;
                int idx = contentType.indexOf(44, eqIdx);
                final int semiIdx = contentType.indexOf(59, eqIdx);
                if (semiIdx != -1 && (semiIdx < idx || idx == -1)) {
                    idx = semiIdx;
                }
                if (idx != -1) {
                    charset = contentType.substring(eqIdx, idx);
                }
                else {
                    charset = contentType.substring(eqIdx);
                }
                charset = charset.trim();
                isrc.setEncoding(charset);
            }
        }
        isrc.setSystemId(uri);
        final SVGOMDocument doc = (SVGOMDocument)super.createDocument("http://www.w3.org/2000/svg", "svg", uri, isrc);
        doc.setParsedURL(new ParsedURL(uri));
        doc.setDocumentInputEncoding(charset);
        doc.setXmlStandalone(this.isStandalone);
        doc.setXmlVersion(this.xmlVersion);
        return doc;
    }
    
    @Override
    public Document createDocument(final String uri, final InputStream inp) throws IOException {
        final InputSource is = new InputSource(inp);
        is.setSystemId(uri);
        Document doc;
        try {
            doc = super.createDocument("http://www.w3.org/2000/svg", "svg", uri, is);
            if (uri != null) {
                ((SVGOMDocument)doc).setParsedURL(new ParsedURL(uri));
            }
            final AbstractDocument d = (AbstractDocument)doc;
            d.setDocumentURI(uri);
            d.setXmlStandalone(this.isStandalone);
            d.setXmlVersion(this.xmlVersion);
        }
        catch (MalformedURLException e) {
            throw new IOException(e.getMessage());
        }
        return doc;
    }
    
    @Override
    public Document createDocument(final String uri, final Reader r) throws IOException {
        final InputSource is = new InputSource(r);
        is.setSystemId(uri);
        Document doc;
        try {
            doc = super.createDocument("http://www.w3.org/2000/svg", "svg", uri, is);
            if (uri != null) {
                ((SVGOMDocument)doc).setParsedURL(new ParsedURL(uri));
            }
            final AbstractDocument d = (AbstractDocument)doc;
            d.setDocumentURI(uri);
            d.setXmlStandalone(this.isStandalone);
            d.setXmlVersion(this.xmlVersion);
        }
        catch (MalformedURLException e) {
            throw new IOException(e.getMessage());
        }
        return doc;
    }
    
    @Override
    public Document createDocument(final String ns, final String root, final String uri) throws IOException {
        if (!"http://www.w3.org/2000/svg".equals(ns) || !"svg".equals(root)) {
            throw new RuntimeException("Bad root element");
        }
        return this.createDocument(uri);
    }
    
    @Override
    public Document createDocument(final String ns, final String root, final String uri, final InputStream is) throws IOException {
        if (!"http://www.w3.org/2000/svg".equals(ns) || !"svg".equals(root)) {
            throw new RuntimeException("Bad root element");
        }
        return this.createDocument(uri, is);
    }
    
    @Override
    public Document createDocument(final String ns, final String root, final String uri, final Reader r) throws IOException {
        if (!"http://www.w3.org/2000/svg".equals(ns) || !"svg".equals(root)) {
            throw new RuntimeException("Bad root element");
        }
        return this.createDocument(uri, r);
    }
    
    @Override
    public DOMImplementation getDOMImplementation(final String ver) {
        if (ver == null || ver.length() == 0 || ver.equals("1.0") || ver.equals("1.1")) {
            return SVGDOMImplementation.getDOMImplementation();
        }
        if (ver.equals("1.2")) {
            return SVG12DOMImplementation.getDOMImplementation();
        }
        throw new RuntimeException("Unsupport SVG version '" + ver + "'");
    }
    
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException {
        try {
            synchronized (SAXSVGDocumentFactory.LOCK) {
                if (SAXSVGDocumentFactory.dtdProps == null) {
                    SAXSVGDocumentFactory.dtdProps = new Properties();
                    try {
                        final Class cls = SAXSVGDocumentFactory.class;
                        final InputStream is = cls.getResourceAsStream("resources/dtdids.properties");
                        SAXSVGDocumentFactory.dtdProps.load(is);
                    }
                    catch (IOException ioe) {
                        throw new SAXException(ioe);
                    }
                }
                if (SAXSVGDocumentFactory.dtdids == null) {
                    SAXSVGDocumentFactory.dtdids = SAXSVGDocumentFactory.dtdProps.getProperty("publicIds");
                }
                if (SAXSVGDocumentFactory.skippable_dtdids == null) {
                    SAXSVGDocumentFactory.skippable_dtdids = SAXSVGDocumentFactory.dtdProps.getProperty("skippablePublicIds");
                }
                if (SAXSVGDocumentFactory.skip_dtd == null) {
                    SAXSVGDocumentFactory.skip_dtd = SAXSVGDocumentFactory.dtdProps.getProperty("skipDTD");
                }
            }
            if (publicId == null) {
                return null;
            }
            if (!this.isValidating && SAXSVGDocumentFactory.skippable_dtdids.indexOf(publicId) != -1) {
                return new InputSource(new StringReader(SAXSVGDocumentFactory.skip_dtd));
            }
            if (SAXSVGDocumentFactory.dtdids.indexOf(publicId) != -1) {
                final String localSystemId = SAXSVGDocumentFactory.dtdProps.getProperty("systemId." + publicId.replace(' ', '_'));
                if (localSystemId != null && !"".equals(localSystemId)) {
                    return new InputSource(this.getClass().getResource(localSystemId).toString());
                }
            }
        }
        catch (MissingResourceException e) {
            throw new SAXException(e);
        }
        return null;
    }
    
    static {
        LOCK = new Object();
    }
}
