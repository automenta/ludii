// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.activation.viewers;

import javax.activation.CommandObject;
import javax.activation.DataHandler;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageViewer extends Panel implements CommandObject
{
    private ImageViewerCanvas canvas;
    private Image image;
    private DataHandler _dh;
    private final boolean DEBUG;
    
    public ImageViewer() {
        this.canvas = null;
        this.image = null;
        this._dh = null;
        this.DEBUG = false;
        this.add(this.canvas = new ImageViewerCanvas());
    }
    
    public void setCommandContext(final String verb, final DataHandler dh) throws IOException {
        this._dh = dh;
        this.setInputStream(this._dh.getInputStream());
    }
    
    private void setInputStream(final InputStream ins) throws IOException {
        final MediaTracker mt = new MediaTracker(this);
        int bytes_read = 0;
        final byte[] data = new byte[1024];
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((bytes_read = ins.read(data)) > 0) {
            baos.write(data, 0, bytes_read);
        }
        ins.close();
        mt.addImage(this.image = this.getToolkit().createImage(baos.toByteArray()), 0);
        try {
            mt.waitForID(0);
            mt.waitForAll();
            if (mt.statusID(0, true) != 8) {
                System.out.println("Error occured in image loading = " + mt.getErrorsID(0));
            }
        }
        catch (InterruptedException e) {
            throw new IOException("Error reading image data");
        }
        this.canvas.setImage(this.image);
        if (this.DEBUG) {
            System.out.println("calling invalidate");
        }
    }
    
    public void addNotify() {
        super.addNotify();
        this.invalidate();
        this.validate();
        this.doLayout();
    }
    
    public Dimension getPreferredSize() {
        return this.canvas.getPreferredSize();
    }
}
