// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class TableFactory
{
    public static Table create(final DirectoryEntry de, final RandomAccessFile raf) throws IOException {
        Table t = null;
        switch (de.getTag()) {
            case 1111577413: {}
            case 1128678944: {}
            case 1146308935: {}
            case 1161970772: {}
            case 1161972803: {}
            case 1161974595: {}
            case 1196445523: {
                t = new GposTable(de, raf);
                break;
            }
            case 1196643650: {
                t = new GsubTable(de, raf);
            }
            case 1246975046: {}
            case 1280594760: {}
            case 1296909912: {}
            case 1330851634: {
                t = new Os2Table(de, raf);
            }
            case 1346587732: {}
            case 1668112752: {
                t = new CmapTable(de, raf);
                break;
            }
            case 1668707360: {
                t = new CvtTable(de, raf);
                break;
            }
            case 1718642541: {
                t = new FpgmTable(de, raf);
            }
            case 1719034226: {}
            case 1735162214: {
                t = new GlyfTable(de, raf);
            }
            case 1751474532: {
                t = new HeadTable(de, raf);
                break;
            }
            case 1751672161: {
                t = new HheaTable(de, raf);
                break;
            }
            case 1752003704: {
                t = new HmtxTable(de, raf);
                break;
            }
            case 1801810542: {
                t = new KernTable(de, raf);
                break;
            }
            case 1819239265: {
                t = new LocaTable(de, raf);
                break;
            }
            case 1835104368: {
                t = new MaxpTable(de, raf);
                break;
            }
            case 1851878757: {
                t = new NameTable(de, raf);
                break;
            }
            case 1886545264: {
                t = new PrepTable(de, raf);
                break;
            }
            case 1886352244: {
                t = new PostTable(de, raf);
            }
        }
        return t;
    }
}
