// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class CmapIndexEntry
{
    private int platformId;
    private int encodingId;
    private int offset;
    
    protected CmapIndexEntry(final RandomAccessFile raf) throws IOException {
        this.platformId = raf.readUnsignedShort();
        this.encodingId = raf.readUnsignedShort();
        this.offset = raf.readInt();
    }
    
    public int getEncodingId() {
        return this.encodingId;
    }
    
    public int getOffset() {
        return this.offset;
    }
    
    public int getPlatformId() {
        return this.platformId;
    }
    
    @Override
    public String toString() {
        String encoding = "";
        String platform = null;
        switch (this.platformId) {
            case 1: {
                platform = " (Macintosh)";
                break;
            }
            case 3: {
                platform = " (Windows)";
                break;
            }
            default: {
                platform = "";
                break;
            }
        }
        if (this.platformId == 3) {
            switch (this.encodingId) {
                case 0: {
                    encoding = " (Symbol)";
                    break;
                }
                case 1: {
                    encoding = " (Unicode)";
                    break;
                }
                case 2: {
                    encoding = " (ShiftJIS)";
                    break;
                }
                case 3: {
                    encoding = " (Big5)";
                    break;
                }
                case 4: {
                    encoding = " (PRC)";
                    break;
                }
                case 5: {
                    encoding = " (Wansung)";
                    break;
                }
                case 6: {
                    encoding = " (Johab)";
                    break;
                }
                default: {
                    encoding = "";
                    break;
                }
            }
        }
        return new StringBuffer().append("platform id: ").append(this.platformId).append(platform).append(", encoding id: ").append(this.encodingId).append(encoding).append(", offset: ").append(this.offset).toString();
    }
}
