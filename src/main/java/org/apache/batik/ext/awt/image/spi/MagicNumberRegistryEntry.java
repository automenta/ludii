// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.spi;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.io.InputStream;

public abstract class MagicNumberRegistryEntry extends AbstractRegistryEntry implements StreamRegistryEntry
{
    public static final float PRIORITY = 1000.0f;
    MagicNumber[] magicNumbers;
    
    public MagicNumberRegistryEntry(final String name, final float priority, final String ext, final String mimeType, final int offset, final byte[] magicNumber) {
        super(name, priority, ext, mimeType);
        (this.magicNumbers = new MagicNumber[1])[0] = new MagicNumber(offset, magicNumber);
    }
    
    public MagicNumberRegistryEntry(final String name, final String ext, final String mimeType, final int offset, final byte[] magicNumber) {
        this(name, 1000.0f, ext, mimeType, offset, magicNumber);
    }
    
    public MagicNumberRegistryEntry(final String name, final float priority, final String ext, final String mimeType, final MagicNumber[] magicNumbers) {
        super(name, priority, ext, mimeType);
        this.magicNumbers = magicNumbers;
    }
    
    public MagicNumberRegistryEntry(final String name, final String ext, final String mimeType, final MagicNumber[] magicNumbers) {
        this(name, 1000.0f, ext, mimeType, magicNumbers);
    }
    
    public MagicNumberRegistryEntry(final String name, final float priority, final String[] exts, final String[] mimeTypes, final int offset, final byte[] magicNumber) {
        super(name, priority, exts, mimeTypes);
        (this.magicNumbers = new MagicNumber[1])[0] = new MagicNumber(offset, magicNumber);
    }
    
    public MagicNumberRegistryEntry(final String name, final String[] exts, final String[] mimeTypes, final int offset, final byte[] magicNumbers) {
        this(name, 1000.0f, exts, mimeTypes, offset, magicNumbers);
    }
    
    public MagicNumberRegistryEntry(final String name, final float priority, final String[] exts, final String[] mimeTypes, final MagicNumber[] magicNumbers) {
        super(name, priority, exts, mimeTypes);
        this.magicNumbers = magicNumbers;
    }
    
    public MagicNumberRegistryEntry(final String name, final String[] exts, final String[] mimeTypes, final MagicNumber[] magicNumbers) {
        this(name, 1000.0f, exts, mimeTypes, magicNumbers);
    }
    
    public MagicNumberRegistryEntry(final String name, final String[] exts, final String[] mimeTypes, final MagicNumber[] magicNumbers, final float priority) {
        super(name, priority, exts, mimeTypes);
        this.magicNumbers = magicNumbers;
    }
    
    @Override
    public int getReadlimit() {
        int maxbuf = 0;
        for (final MagicNumber magicNumber : this.magicNumbers) {
            final int req = magicNumber.getReadlimit();
            if (req > maxbuf) {
                maxbuf = req;
            }
        }
        return maxbuf;
    }
    
    @Override
    public boolean isCompatibleStream(final InputStream is) throws StreamCorruptedException {
        for (final MagicNumber magicNumber : this.magicNumbers) {
            if (magicNumber.isMatch(is)) {
                return true;
            }
        }
        return false;
    }
    
    public static class MagicNumber
    {
        int offset;
        byte[] magicNumber;
        byte[] buffer;
        
        public MagicNumber(final int offset, final byte[] magicNumber) {
            this.offset = offset;
            this.magicNumber = magicNumber.clone();
            this.buffer = new byte[magicNumber.length];
        }
        
        int getReadlimit() {
            return this.offset + this.magicNumber.length;
        }
        
        boolean isMatch(final InputStream is) throws StreamCorruptedException {
            int idx = 0;
            is.mark(this.getReadlimit());
            try {
                while (idx < this.offset) {
                    final int rn = (int)is.skip(this.offset - idx);
                    if (rn == -1) {
                        return false;
                    }
                    idx += rn;
                }
                int rn;
                for (idx = 0; idx < this.buffer.length; idx += rn) {
                    rn = is.read(this.buffer, idx, this.buffer.length - idx);
                    if (rn == -1) {
                        return false;
                    }
                }
                for (int i = 0; i < this.magicNumber.length; ++i) {
                    if (this.magicNumber[i] != this.buffer[i]) {
                        return false;
                    }
                }
            }
            catch (IOException ioe2) {
                return false;
            }
            finally {
                try {
                    is.reset();
                }
                catch (IOException ioe) {
                    throw new StreamCorruptedException(ioe.getMessage());
                }
            }
            return true;
        }
    }
}
