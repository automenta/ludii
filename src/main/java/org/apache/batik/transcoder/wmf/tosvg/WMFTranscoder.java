// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.wmf.tosvg;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.InputStream;
import java.io.BufferedInputStream;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.io.DataInputStream;
import java.awt.Dimension;
import java.awt.Graphics;
import org.apache.batik.svggen.SVGGraphics2D;
import java.io.IOException;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.ToSVGAbstractTranscoder;

public class WMFTranscoder extends ToSVGAbstractTranscoder
{
    public static final String WMF_EXTENSION = ".wmf";
    public static final String SVG_EXTENSION = ".svg";
    
    @Override
    public void transcode(final TranscoderInput input, final TranscoderOutput output) throws TranscoderException {
        final DataInputStream is = this.getCompatibleInput(input);
        final WMFRecordStore currentStore = new WMFRecordStore();
        try {
            currentStore.read(is);
        }
        catch (IOException e) {
            this.handler.fatalError(new TranscoderException(e));
            return;
        }
        float conv = 1.0f;
        float wmfwidth;
        float wmfheight;
        if (this.hints.containsKey(WMFTranscoder.KEY_INPUT_WIDTH)) {
            wmfwidth = (float)(int)this.hints.get(WMFTranscoder.KEY_INPUT_WIDTH);
            wmfheight = (float)(int)this.hints.get(WMFTranscoder.KEY_INPUT_HEIGHT);
        }
        else {
            wmfwidth = (float)currentStore.getWidthPixels();
            wmfheight = (float)currentStore.getHeightPixels();
        }
        float width = wmfwidth;
        float height = wmfheight;
        if (this.hints.containsKey(WMFTranscoder.KEY_WIDTH)) {
            width = (float)this.hints.get(WMFTranscoder.KEY_WIDTH);
            conv = width / wmfwidth;
            height = height * width / wmfwidth;
        }
        int xOffset = 0;
        int yOffset = 0;
        if (this.hints.containsKey(WMFTranscoder.KEY_XOFFSET)) {
            xOffset = (int)this.hints.get(WMFTranscoder.KEY_XOFFSET);
        }
        if (this.hints.containsKey(WMFTranscoder.KEY_YOFFSET)) {
            yOffset = (int)this.hints.get(WMFTranscoder.KEY_YOFFSET);
        }
        final float sizeFactor = currentStore.getUnitsToPixels() * conv;
        final int vpX = (int)(currentStore.getVpX() * sizeFactor);
        final int vpY = (int)(currentStore.getVpY() * sizeFactor);
        int vpW;
        int vpH;
        if (this.hints.containsKey(WMFTranscoder.KEY_INPUT_WIDTH)) {
            vpW = (int)((int)this.hints.get(WMFTranscoder.KEY_INPUT_WIDTH) * conv);
            vpH = (int)((int)this.hints.get(WMFTranscoder.KEY_INPUT_HEIGHT) * conv);
        }
        else {
            vpW = (int)(currentStore.getWidthUnits() * sizeFactor);
            vpH = (int)(currentStore.getHeightUnits() * sizeFactor);
        }
        final WMFPainter painter = new WMFPainter(currentStore, xOffset, yOffset, conv);
        final Document doc = this.createDocument(output);
        this.svgGenerator = new SVGGraphics2D(doc);
        this.svgGenerator.getGeneratorContext().setPrecision(4);
        painter.paint(this.svgGenerator);
        this.svgGenerator.setSVGCanvasSize(new Dimension(vpW, vpH));
        final Element svgRoot = this.svgGenerator.getRoot();
        svgRoot.setAttributeNS(null, "viewBox", String.valueOf(vpX) + ' ' + vpY + ' ' + vpW + ' ' + vpH);
        this.writeSVGToOutput(this.svgGenerator, svgRoot, output);
    }
    
    private DataInputStream getCompatibleInput(final TranscoderInput input) throws TranscoderException {
        if (input == null) {
            this.handler.fatalError(new TranscoderException(String.valueOf(65280)));
        }
        InputStream in = input.getInputStream();
        if (in != null) {
            return new DataInputStream(new BufferedInputStream(in));
        }
        final String uri = input.getURI();
        if (uri != null) {
            try {
                final URL url = new URL(uri);
                in = url.openStream();
                return new DataInputStream(new BufferedInputStream(in));
            }
            catch (MalformedURLException e) {
                this.handler.fatalError(new TranscoderException(e));
            }
            catch (IOException e2) {
                this.handler.fatalError(new TranscoderException(e2));
            }
        }
        this.handler.fatalError(new TranscoderException(String.valueOf(65281)));
        return null;
    }
    
    public static void main(final String[] args) throws TranscoderException {
        if (args.length < 1) {
            System.out.println("Usage : WMFTranscoder.main <file 1> ... <file n>");
            System.exit(1);
        }
        final WMFTranscoder transcoder = new WMFTranscoder();
        final int nFiles = args.length;
        for (final String fileName : args) {
            if (!fileName.toLowerCase().endsWith(".wmf")) {
                System.err.println(fileName + " does not have the " + ".wmf" + " extension. It is ignored");
            }
            else {
                System.out.print("Processing : " + fileName + "...");
                final String outputFileName = fileName.substring(0, fileName.toLowerCase().indexOf(".wmf")) + ".svg";
                final File inputFile = new File(fileName);
                final File outputFile = new File(outputFileName);
                try {
                    final TranscoderInput input = new TranscoderInput(inputFile.toURI().toURL().toString());
                    final TranscoderOutput output = new TranscoderOutput(new FileOutputStream(outputFile));
                    transcoder.transcode(input, output);
                }
                catch (MalformedURLException e) {
                    throw new TranscoderException(e);
                }
                catch (IOException e2) {
                    throw new TranscoderException(e2);
                }
                System.out.println(".... Done");
            }
        }
        System.exit(0);
    }
}
