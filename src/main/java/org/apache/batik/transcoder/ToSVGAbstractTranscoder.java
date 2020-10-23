// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder;

import org.apache.batik.transcoder.keys.BooleanKey;
import org.apache.batik.transcoder.keys.IntegerKey;
import org.apache.batik.transcoder.keys.FloatKey;
import org.apache.batik.util.Platform;
import java.net.URLConnection;
import java.io.OutputStream;
import org.xml.sax.XMLFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.Writer;
import java.io.OutputStreamWriter;
import org.w3c.dom.Element;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.w3c.dom.Document;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.util.SVGConstants;

public abstract class ToSVGAbstractTranscoder extends AbstractTranscoder implements SVGConstants
{
    public static float PIXEL_TO_MILLIMETERS;
    public static float PIXEL_PER_INCH;
    public static final int TRANSCODER_ERROR_BASE = 65280;
    public static final int ERROR_NULL_INPUT = 65280;
    public static final int ERROR_INCOMPATIBLE_INPUT_TYPE = 65281;
    public static final int ERROR_INCOMPATIBLE_OUTPUT_TYPE = 65282;
    public static final TranscodingHints.Key KEY_WIDTH;
    public static final TranscodingHints.Key KEY_HEIGHT;
    public static final TranscodingHints.Key KEY_INPUT_WIDTH;
    public static final TranscodingHints.Key KEY_INPUT_HEIGHT;
    public static final TranscodingHints.Key KEY_XOFFSET;
    public static final TranscodingHints.Key KEY_YOFFSET;
    public static final TranscodingHints.Key KEY_ESCAPED;
    protected SVGGraphics2D svgGenerator;
    
    protected Document createDocument(final TranscoderOutput output) {
        Document doc;
        if (output.getDocument() == null) {
            final DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();
            doc = domImpl.createDocument("http://www.w3.org/2000/svg", "svg", null);
        }
        else {
            doc = output.getDocument();
        }
        return doc;
    }
    
    public SVGGraphics2D getGraphics2D() {
        return this.svgGenerator;
    }
    
    protected void writeSVGToOutput(final SVGGraphics2D svgGenerator, final Element svgRoot, final TranscoderOutput output) throws TranscoderException {
        final Document doc = output.getDocument();
        if (doc != null) {
            return;
        }
        final XMLFilter xmlFilter = output.getXMLFilter();
        if (xmlFilter != null) {
            this.handler.fatalError(new TranscoderException("65282"));
        }
        try {
            boolean escaped = false;
            if (this.hints.containsKey(ToSVGAbstractTranscoder.KEY_ESCAPED)) {
                escaped = (boolean)this.hints.get(ToSVGAbstractTranscoder.KEY_ESCAPED);
            }
            OutputStream os = output.getOutputStream();
            if (os != null) {
                svgGenerator.stream(svgRoot, new OutputStreamWriter(os), false, escaped);
                return;
            }
            final Writer wr = output.getWriter();
            if (wr != null) {
                svgGenerator.stream(svgRoot, wr, false, escaped);
                return;
            }
            final String uri = output.getURI();
            if (uri != null) {
                try {
                    final URL url = new URL(uri);
                    final URLConnection urlCnx = url.openConnection();
                    os = urlCnx.getOutputStream();
                    svgGenerator.stream(svgRoot, new OutputStreamWriter(os), false, escaped);
                    return;
                }
                catch (MalformedURLException e) {
                    this.handler.fatalError(new TranscoderException(e));
                }
                catch (IOException e2) {
                    this.handler.fatalError(new TranscoderException(e2));
                }
            }
        }
        catch (IOException e3) {
            throw new TranscoderException(e3);
        }
        throw new TranscoderException("65282");
    }
    
    static {
        ToSVGAbstractTranscoder.PIXEL_TO_MILLIMETERS = 25.4f / Platform.getScreenResolution();
        ToSVGAbstractTranscoder.PIXEL_PER_INCH = (float)Platform.getScreenResolution();
        KEY_WIDTH = new FloatKey();
        KEY_HEIGHT = new FloatKey();
        KEY_INPUT_WIDTH = new IntegerKey();
        KEY_INPUT_HEIGHT = new IntegerKey();
        KEY_XOFFSET = new IntegerKey();
        KEY_YOFFSET = new IntegerKey();
        KEY_ESCAPED = new BooleanKey();
    }
}
