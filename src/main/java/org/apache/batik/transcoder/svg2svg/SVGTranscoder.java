// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.svg2svg;

import org.apache.batik.transcoder.keys.StringKey;
import org.apache.batik.transcoder.keys.IntegerKey;
import org.apache.batik.transcoder.keys.BooleanKey;
import org.apache.batik.transcoder.TranscoderException;
import org.w3c.dom.Document;
import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;
import java.io.Writer;
import org.apache.batik.dom.util.DOMUtilities;
import java.io.StringWriter;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.ErrorHandler;
import org.apache.batik.transcoder.AbstractTranscoder;

public class SVGTranscoder extends AbstractTranscoder
{
    public static final ErrorHandler DEFAULT_ERROR_HANDLER;
    public static final TranscodingHints.Key KEY_NEWLINE;
    public static final NewlineValue VALUE_NEWLINE_CR;
    public static final NewlineValue VALUE_NEWLINE_CR_LF;
    public static final NewlineValue VALUE_NEWLINE_LF;
    public static final TranscodingHints.Key KEY_FORMAT;
    public static final Boolean VALUE_FORMAT_ON;
    public static final Boolean VALUE_FORMAT_OFF;
    public static final TranscodingHints.Key KEY_TABULATION_WIDTH;
    public static final TranscodingHints.Key KEY_DOCUMENT_WIDTH;
    public static final TranscodingHints.Key KEY_DOCTYPE;
    public static final DoctypeValue VALUE_DOCTYPE_CHANGE;
    public static final DoctypeValue VALUE_DOCTYPE_REMOVE;
    public static final DoctypeValue VALUE_DOCTYPE_KEEP_UNCHANGED;
    public static final TranscodingHints.Key KEY_PUBLIC_ID;
    public static final TranscodingHints.Key KEY_SYSTEM_ID;
    public static final TranscodingHints.Key KEY_XML_DECLARATION;
    
    public SVGTranscoder() {
        this.setErrorHandler(SVGTranscoder.DEFAULT_ERROR_HANDLER);
    }
    
    @Override
    public void transcode(final TranscoderInput input, final TranscoderOutput output) throws TranscoderException {
        Reader r = input.getReader();
        final Writer w = output.getWriter();
        if (r == null) {
            final Document d = input.getDocument();
            if (d == null) {
                throw new RuntimeException("Reader or Document expected");
            }
            final StringWriter sw = new StringWriter(1024);
            try {
                DOMUtilities.writeDocument(d, sw);
            }
            catch (IOException ioEx) {
                throw new RuntimeException("IO:" + ioEx.getMessage());
            }
            r = new StringReader(sw.toString());
        }
        if (w == null) {
            throw new RuntimeException("Writer expected");
        }
        this.prettyPrint(r, w);
    }
    
    protected void prettyPrint(final Reader in, final Writer out) throws TranscoderException {
        try {
            final PrettyPrinter pp = new PrettyPrinter();
            final NewlineValue nlv = (NewlineValue)this.hints.get(SVGTranscoder.KEY_NEWLINE);
            if (nlv != null) {
                pp.setNewline(nlv.getValue());
            }
            final Boolean b = (Boolean)this.hints.get(SVGTranscoder.KEY_FORMAT);
            if (b != null) {
                pp.setFormat(b);
            }
            Integer i = (Integer)this.hints.get(SVGTranscoder.KEY_TABULATION_WIDTH);
            if (i != null) {
                pp.setTabulationWidth(i);
            }
            i = (Integer)this.hints.get(SVGTranscoder.KEY_DOCUMENT_WIDTH);
            if (i != null) {
                pp.setDocumentWidth(i);
            }
            final DoctypeValue dtv = (DoctypeValue)this.hints.get(SVGTranscoder.KEY_DOCTYPE);
            if (dtv != null) {
                pp.setDoctypeOption(dtv.getValue());
            }
            String s = (String)this.hints.get(SVGTranscoder.KEY_PUBLIC_ID);
            if (s != null) {
                pp.setPublicId(s);
            }
            s = (String)this.hints.get(SVGTranscoder.KEY_SYSTEM_ID);
            if (s != null) {
                pp.setSystemId(s);
            }
            s = (String)this.hints.get(SVGTranscoder.KEY_XML_DECLARATION);
            if (s != null) {
                pp.setXMLDeclaration(s);
            }
            pp.print(in, out);
            out.flush();
        }
        catch (IOException e) {
            this.getErrorHandler().fatalError(new TranscoderException(e.getMessage()));
        }
    }
    
    static {
        DEFAULT_ERROR_HANDLER = new ErrorHandler() {
            @Override
            public void error(final TranscoderException ex) throws TranscoderException {
                throw ex;
            }
            
            @Override
            public void fatalError(final TranscoderException ex) throws TranscoderException {
                throw ex;
            }
            
            @Override
            public void warning(final TranscoderException ex) throws TranscoderException {
            }
        };
        KEY_NEWLINE = new NewlineKey();
        VALUE_NEWLINE_CR = new NewlineValue("\r");
        VALUE_NEWLINE_CR_LF = new NewlineValue("\r\n");
        VALUE_NEWLINE_LF = new NewlineValue("\n");
        KEY_FORMAT = new BooleanKey();
        VALUE_FORMAT_ON = Boolean.TRUE;
        VALUE_FORMAT_OFF = Boolean.FALSE;
        KEY_TABULATION_WIDTH = new IntegerKey();
        KEY_DOCUMENT_WIDTH = new IntegerKey();
        KEY_DOCTYPE = new DoctypeKey();
        VALUE_DOCTYPE_CHANGE = new DoctypeValue(0);
        VALUE_DOCTYPE_REMOVE = new DoctypeValue(1);
        VALUE_DOCTYPE_KEEP_UNCHANGED = new DoctypeValue(2);
        KEY_PUBLIC_ID = new StringKey();
        KEY_SYSTEM_ID = new StringKey();
        KEY_XML_DECLARATION = new StringKey();
    }
    
    protected static class NewlineKey extends TranscodingHints.Key
    {
        @Override
        public boolean isCompatibleValue(final Object v) {
            return v instanceof NewlineValue;
        }
    }
    
    protected static class NewlineValue
    {
        protected final String value;
        
        protected NewlineValue(final String val) {
            this.value = val;
        }
        
        public String getValue() {
            return this.value;
        }
    }
    
    protected static class DoctypeKey extends TranscodingHints.Key
    {
        @Override
        public boolean isCompatibleValue(final Object v) {
            return v instanceof DoctypeValue;
        }
    }
    
    protected static class DoctypeValue
    {
        final int value;
        
        protected DoctypeValue(final int value) {
            this.value = value;
        }
        
        public int getValue() {
            return this.value;
        }
    }
}
