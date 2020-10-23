// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.print;

import org.apache.batik.transcoder.keys.StringKey;
import org.apache.batik.transcoder.keys.LengthKey;
import org.apache.batik.transcoder.keys.BooleanKey;
import java.util.StringTokenizer;
import java.io.File;
import org.apache.batik.transcoder.Transcoder;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.util.Collection;
import java.awt.Graphics;
import java.awt.print.PrinterException;
import java.awt.print.Paper;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import org.apache.batik.transcoder.TranscoderException;
import org.w3c.dom.Document;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscoderInput;
import java.util.ArrayList;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.bridge.BridgeContext;
import java.util.List;
import java.awt.print.Printable;
import org.apache.batik.transcoder.SVGAbstractTranscoder;

public class PrintTranscoder extends SVGAbstractTranscoder implements Printable
{
    public static final String KEY_AOI_STR = "aoi";
    public static final String KEY_HEIGHT_STR = "height";
    public static final String KEY_LANGUAGE_STR = "language";
    public static final String KEY_MARGIN_BOTTOM_STR = "marginBottom";
    public static final String KEY_MARGIN_LEFT_STR = "marginLeft";
    public static final String KEY_MARGIN_RIGHT_STR = "marginRight";
    public static final String KEY_MARGIN_TOP_STR = "marginTop";
    public static final String KEY_PAGE_HEIGHT_STR = "pageHeight";
    public static final String KEY_PAGE_ORIENTATION_STR = "pageOrientation";
    public static final String KEY_PAGE_WIDTH_STR = "pageWidth";
    public static final String KEY_PIXEL_TO_MM_STR = "pixelToMm";
    public static final String KEY_SCALE_TO_PAGE_STR = "scaleToPage";
    public static final String KEY_SHOW_PAGE_DIALOG_STR = "showPageDialog";
    public static final String KEY_SHOW_PRINTER_DIALOG_STR = "showPrinterDialog";
    public static final String KEY_USER_STYLESHEET_URI_STR = "userStylesheet";
    public static final String KEY_WIDTH_STR = "width";
    public static final String KEY_XML_PARSER_CLASSNAME_STR = "xmlParserClassName";
    public static final String VALUE_MEDIA_PRINT = "print";
    public static final String VALUE_PAGE_ORIENTATION_LANDSCAPE = "landscape";
    public static final String VALUE_PAGE_ORIENTATION_PORTRAIT = "portrait";
    public static final String VALUE_PAGE_ORIENTATION_REVERSE_LANDSCAPE = "reverseLandscape";
    private List inputs;
    private List printedInputs;
    private int curIndex;
    private BridgeContext theCtx;
    public static final TranscodingHints.Key KEY_SHOW_PAGE_DIALOG;
    public static final TranscodingHints.Key KEY_SHOW_PRINTER_DIALOG;
    public static final TranscodingHints.Key KEY_PAGE_WIDTH;
    public static final TranscodingHints.Key KEY_PAGE_HEIGHT;
    public static final TranscodingHints.Key KEY_MARGIN_TOP;
    public static final TranscodingHints.Key KEY_MARGIN_RIGHT;
    public static final TranscodingHints.Key KEY_MARGIN_BOTTOM;
    public static final TranscodingHints.Key KEY_MARGIN_LEFT;
    public static final TranscodingHints.Key KEY_PAGE_ORIENTATION;
    public static final TranscodingHints.Key KEY_SCALE_TO_PAGE;
    public static final String USAGE = "java org.apache.batik.transcoder.print.PrintTranscoder <svgFileToPrint>";
    
    public PrintTranscoder() {
        this.inputs = new ArrayList();
        this.printedInputs = null;
        this.curIndex = -1;
        this.hints.put(PrintTranscoder.KEY_MEDIA, "print");
    }
    
    @Override
    public void transcode(final TranscoderInput in, final TranscoderOutput out) {
        if (in != null) {
            this.inputs.add(in);
        }
    }
    
    @Override
    protected void transcode(final Document document, final String uri, final TranscoderOutput output) throws TranscoderException {
        super.transcode(document, uri, output);
        this.theCtx = this.ctx;
        this.ctx = null;
    }
    
    public void print() throws PrinterException {
        final PrinterJob printerJob = PrinterJob.getPrinterJob();
        PageFormat pageFormat = printerJob.defaultPage();
        final Paper paper = pageFormat.getPaper();
        final Float pageWidth = (Float)this.hints.get(PrintTranscoder.KEY_PAGE_WIDTH);
        final Float pageHeight = (Float)this.hints.get(PrintTranscoder.KEY_PAGE_HEIGHT);
        if (pageWidth != null) {
            paper.setSize(pageWidth, paper.getHeight());
        }
        if (pageHeight != null) {
            paper.setSize(paper.getWidth(), pageHeight);
        }
        float x = 0.0f;
        float y = 0.0f;
        float width = (float)paper.getWidth();
        float height = (float)paper.getHeight();
        final Float leftMargin = (Float)this.hints.get(PrintTranscoder.KEY_MARGIN_LEFT);
        final Float topMargin = (Float)this.hints.get(PrintTranscoder.KEY_MARGIN_TOP);
        final Float rightMargin = (Float)this.hints.get(PrintTranscoder.KEY_MARGIN_RIGHT);
        final Float bottomMargin = (Float)this.hints.get(PrintTranscoder.KEY_MARGIN_BOTTOM);
        if (leftMargin != null) {
            x = leftMargin;
            width -= leftMargin;
        }
        if (topMargin != null) {
            y = topMargin;
            height -= topMargin;
        }
        if (rightMargin != null) {
            width -= rightMargin;
        }
        if (bottomMargin != null) {
            height -= bottomMargin;
        }
        paper.setImageableArea(x, y, width, height);
        final String pageOrientation = (String)this.hints.get(PrintTranscoder.KEY_PAGE_ORIENTATION);
        if ("portrait".equalsIgnoreCase(pageOrientation)) {
            pageFormat.setOrientation(1);
        }
        else if ("landscape".equalsIgnoreCase(pageOrientation)) {
            pageFormat.setOrientation(0);
        }
        else if ("reverseLandscape".equalsIgnoreCase(pageOrientation)) {
            pageFormat.setOrientation(2);
        }
        pageFormat.setPaper(paper);
        pageFormat = printerJob.validatePage(pageFormat);
        final Boolean showPageFormat = (Boolean)this.hints.get(PrintTranscoder.KEY_SHOW_PAGE_DIALOG);
        if (showPageFormat != null && showPageFormat) {
            final PageFormat tmpPageFormat = printerJob.pageDialog(pageFormat);
            if (tmpPageFormat == pageFormat) {
                return;
            }
            pageFormat = tmpPageFormat;
        }
        printerJob.setPrintable(this, pageFormat);
        final Boolean showPrinterDialog = (Boolean)this.hints.get(PrintTranscoder.KEY_SHOW_PRINTER_DIALOG);
        if (showPrinterDialog != null && showPrinterDialog && !printerJob.printDialog()) {
            return;
        }
        printerJob.print();
    }
    
    @Override
    public int print(final Graphics _g, final PageFormat pageFormat, final int pageIndex) {
        if (this.printedInputs == null) {
            this.printedInputs = new ArrayList(this.inputs);
        }
        if (pageIndex >= this.printedInputs.size()) {
            this.curIndex = -1;
            if (this.theCtx != null) {
                this.theCtx.dispose();
            }
            this.userAgent.displayMessage("Done");
            return 1;
        }
        if (this.curIndex != pageIndex) {
            if (this.theCtx != null) {
                this.theCtx.dispose();
            }
            try {
                this.width = (float)(int)pageFormat.getImageableWidth();
                this.height = (float)(int)pageFormat.getImageableHeight();
                super.transcode(this.printedInputs.get(pageIndex), null);
                this.curIndex = pageIndex;
            }
            catch (TranscoderException e) {
                this.drawError(_g, e);
                return 0;
            }
        }
        final Graphics2D g = (Graphics2D)_g;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING, "Printing");
        final AffineTransform t = g.getTransform();
        final Shape clip = g.getClip();
        g.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g.transform(this.curTxf);
        try {
            this.root.paint(g);
        }
        catch (Exception e2) {
            g.setTransform(t);
            g.setClip(clip);
            this.drawError(_g, e2);
        }
        g.setTransform(t);
        g.setClip(clip);
        return 0;
    }
    
    @Override
    protected void setImageSize(final float docWidth, final float docHeight) {
        final Boolean scaleToPage = (Boolean)this.hints.get(PrintTranscoder.KEY_SCALE_TO_PAGE);
        if (scaleToPage != null && !scaleToPage) {
            float w = docWidth;
            float h = docHeight;
            if (this.hints.containsKey(PrintTranscoder.KEY_AOI)) {
                final Rectangle2D aoi = (Rectangle2D)this.hints.get(PrintTranscoder.KEY_AOI);
                w = (float)aoi.getWidth();
                h = (float)aoi.getHeight();
            }
            super.setImageSize(w, h);
        }
    }
    
    private void drawError(final Graphics g, final Exception e) {
        this.userAgent.displayError(e);
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("java org.apache.batik.transcoder.print.PrintTranscoder <svgFileToPrint>");
            System.exit(0);
        }
        final PrintTranscoder transcoder = new PrintTranscoder();
        setTranscoderFloatHint(transcoder, "language", PrintTranscoder.KEY_LANGUAGE);
        setTranscoderFloatHint(transcoder, "userStylesheet", PrintTranscoder.KEY_USER_STYLESHEET_URI);
        setTranscoderStringHint(transcoder, "xmlParserClassName", PrintTranscoder.KEY_XML_PARSER_CLASSNAME);
        setTranscoderBooleanHint(transcoder, "scaleToPage", PrintTranscoder.KEY_SCALE_TO_PAGE);
        setTranscoderRectangleHint(transcoder, "aoi", PrintTranscoder.KEY_AOI);
        setTranscoderFloatHint(transcoder, "width", PrintTranscoder.KEY_WIDTH);
        setTranscoderFloatHint(transcoder, "height", PrintTranscoder.KEY_HEIGHT);
        setTranscoderFloatHint(transcoder, "pixelToMm", PrintTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER);
        setTranscoderStringHint(transcoder, "pageOrientation", PrintTranscoder.KEY_PAGE_ORIENTATION);
        setTranscoderFloatHint(transcoder, "pageWidth", PrintTranscoder.KEY_PAGE_WIDTH);
        setTranscoderFloatHint(transcoder, "pageHeight", PrintTranscoder.KEY_PAGE_HEIGHT);
        setTranscoderFloatHint(transcoder, "marginTop", PrintTranscoder.KEY_MARGIN_TOP);
        setTranscoderFloatHint(transcoder, "marginRight", PrintTranscoder.KEY_MARGIN_RIGHT);
        setTranscoderFloatHint(transcoder, "marginBottom", PrintTranscoder.KEY_MARGIN_BOTTOM);
        setTranscoderFloatHint(transcoder, "marginLeft", PrintTranscoder.KEY_MARGIN_LEFT);
        setTranscoderBooleanHint(transcoder, "showPageDialog", PrintTranscoder.KEY_SHOW_PAGE_DIALOG);
        setTranscoderBooleanHint(transcoder, "showPrinterDialog", PrintTranscoder.KEY_SHOW_PRINTER_DIALOG);
        for (final String arg : args) {
            transcoder.transcode(new TranscoderInput(new File(arg).toURI().toURL().toString()), null);
        }
        transcoder.print();
        System.exit(0);
    }
    
    public static void setTranscoderFloatHint(final Transcoder transcoder, final String property, final TranscodingHints.Key key) {
        final String str = System.getProperty(property);
        if (str != null) {
            try {
                final Float value = Float.parseFloat(str);
                transcoder.addTranscodingHint(key, value);
            }
            catch (NumberFormatException e) {
                handleValueError(property, str);
            }
        }
    }
    
    public static void setTranscoderRectangleHint(final Transcoder transcoder, final String property, final TranscodingHints.Key key) {
        final String str = System.getProperty(property);
        if (str != null) {
            final StringTokenizer st = new StringTokenizer(str, " ,");
            if (st.countTokens() != 4) {
                handleValueError(property, str);
            }
            try {
                final String x = st.nextToken();
                final String y = st.nextToken();
                final String width = st.nextToken();
                final String height = st.nextToken();
                final Rectangle2D r = new Rectangle2D.Float(Float.parseFloat(x), Float.parseFloat(y), Float.parseFloat(width), Float.parseFloat(height));
                transcoder.addTranscodingHint(key, r);
            }
            catch (NumberFormatException e) {
                handleValueError(property, str);
            }
        }
    }
    
    public static void setTranscoderBooleanHint(final Transcoder transcoder, final String property, final TranscodingHints.Key key) {
        final String str = System.getProperty(property);
        if (str != null) {
            final Boolean value = "true".equalsIgnoreCase(str) ? Boolean.TRUE : Boolean.FALSE;
            transcoder.addTranscodingHint(key, value);
        }
    }
    
    public static void setTranscoderStringHint(final Transcoder transcoder, final String property, final TranscodingHints.Key key) {
        final String str = System.getProperty(property);
        if (str != null) {
            transcoder.addTranscodingHint(key, str);
        }
    }
    
    public static void handleValueError(final String property, final String value) {
        System.err.println("Invalid " + property + " value : " + value);
        System.exit(1);
    }
    
    static {
        KEY_SHOW_PAGE_DIALOG = new BooleanKey();
        KEY_SHOW_PRINTER_DIALOG = new BooleanKey();
        KEY_PAGE_WIDTH = new LengthKey();
        KEY_PAGE_HEIGHT = new LengthKey();
        KEY_MARGIN_TOP = new LengthKey();
        KEY_MARGIN_RIGHT = new LengthKey();
        KEY_MARGIN_BOTTOM = new LengthKey();
        KEY_MARGIN_LEFT = new LengthKey();
        KEY_PAGE_ORIENTATION = new StringKey();
        KEY_SCALE_TO_PAGE = new BooleanKey();
    }
}
