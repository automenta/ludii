// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.io.IOException;

public class PathParser extends NumberParser
{
    protected PathHandler pathHandler;
    
    public PathParser() {
        this.pathHandler = DefaultPathHandler.INSTANCE;
    }
    
    public void setPathHandler(final PathHandler handler) {
        this.pathHandler = handler;
    }
    
    public PathHandler getPathHandler() {
        return this.pathHandler;
    }
    
    @Override
    protected void doParse() throws ParseException, IOException {
        this.pathHandler.startPath();
        this.current = this.reader.read();
    Label_0020_Outer:
        while (true) {
            while (true) {
                try {
                Label_0399:
                    while (true) {
                        switch (this.current) {
                            case 9:
                            case 10:
                            case 13:
                            case 32: {
                                this.current = this.reader.read();
                                continue Label_0020_Outer;
                            }
                            case 90:
                            case 122: {
                                this.current = this.reader.read();
                                this.pathHandler.closePath();
                                continue Label_0020_Outer;
                            }
                            case 109: {
                                this.parsem();
                                continue Label_0020_Outer;
                            }
                            case 77: {
                                this.parseM();
                                continue Label_0020_Outer;
                            }
                            case 108: {
                                this.parsel();
                                continue Label_0020_Outer;
                            }
                            case 76: {
                                this.parseL();
                                continue Label_0020_Outer;
                            }
                            case 104: {
                                this.parseh();
                                continue Label_0020_Outer;
                            }
                            case 72: {
                                this.parseH();
                                continue Label_0020_Outer;
                            }
                            case 118: {
                                this.parsev();
                                continue Label_0020_Outer;
                            }
                            case 86: {
                                this.parseV();
                                continue Label_0020_Outer;
                            }
                            case 99: {
                                this.parsec();
                                continue Label_0020_Outer;
                            }
                            case 67: {
                                this.parseC();
                                continue Label_0020_Outer;
                            }
                            case 113: {
                                this.parseq();
                                continue Label_0020_Outer;
                            }
                            case 81: {
                                this.parseQ();
                                continue Label_0020_Outer;
                            }
                            case 115: {
                                this.parses();
                                continue Label_0020_Outer;
                            }
                            case 83: {
                                this.parseS();
                                continue Label_0020_Outer;
                            }
                            case 116: {
                                this.parset();
                                continue Label_0020_Outer;
                            }
                            case 84: {
                                this.parseT();
                                continue Label_0020_Outer;
                            }
                            case 97: {
                                this.parsea();
                                continue Label_0020_Outer;
                            }
                            case 65: {
                                this.parseA();
                                continue Label_0020_Outer;
                            }
                            case -1: {
                                break Label_0399;
                            }
                            default: {
                                this.reportUnexpected(this.current);
                                continue Label_0020_Outer;
                            }
                        }
                    }
                    break;
                }
                catch (ParseException e) {
                    this.errorHandler.error(e);
                    this.skipSubPath();
                    continue Label_0020_Outer;
                }
                continue;
            }
        }
        this.skipSpaces();
        if (this.current != -1) {
            this.reportError("end.of.stream.expected", new Object[] { this.current });
        }
        this.pathHandler.endPath();
    }
    
    protected void parsem() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        final float x = this.parseFloat();
        this.skipCommaSpaces();
        final float y = this.parseFloat();
        this.pathHandler.movetoRel(x, y);
        final boolean expectNumber = this.skipCommaSpaces2();
        this._parsel(expectNumber);
    }
    
    protected void parseM() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        final float x = this.parseFloat();
        this.skipCommaSpaces();
        final float y = this.parseFloat();
        this.pathHandler.movetoAbs(x, y);
        final boolean expectNumber = this.skipCommaSpaces2();
        this._parseL(expectNumber);
    }
    
    protected void parsel() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        this._parsel(true);
    }
    
    protected void _parsel(boolean expectNumber) throws ParseException, IOException {
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                }
                case 43:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    final float x = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y = this.parseFloat();
                    this.pathHandler.linetoRel(x, y);
                    expectNumber = this.skipCommaSpaces2();
                    continue;
                }
            }
        }
    }
    
    protected void parseL() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        this._parseL(true);
    }
    
    protected void _parseL(boolean expectNumber) throws ParseException, IOException {
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                }
                case 43:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    final float x = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y = this.parseFloat();
                    this.pathHandler.linetoAbs(x, y);
                    expectNumber = this.skipCommaSpaces2();
                    continue;
                }
            }
        }
    }
    
    protected void parseh() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                }
                case 43:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    final float x = this.parseFloat();
                    this.pathHandler.linetoHorizontalRel(x);
                    expectNumber = this.skipCommaSpaces2();
                    continue;
                }
            }
        }
    }
    
    protected void parseH() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                }
                case 43:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    final float x = this.parseFloat();
                    this.pathHandler.linetoHorizontalAbs(x);
                    expectNumber = this.skipCommaSpaces2();
                    continue;
                }
            }
        }
    }
    
    protected void parsev() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                }
                case 43:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    final float x = this.parseFloat();
                    this.pathHandler.linetoVerticalRel(x);
                    expectNumber = this.skipCommaSpaces2();
                    continue;
                }
            }
        }
    }
    
    protected void parseV() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                }
                case 43:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    final float x = this.parseFloat();
                    this.pathHandler.linetoVerticalAbs(x);
                    expectNumber = this.skipCommaSpaces2();
                    continue;
                }
            }
        }
    }
    
    protected void parsec() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                }
                case 43:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    final float x1 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y1 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float x2 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y2 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float x3 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y3 = this.parseFloat();
                    this.pathHandler.curvetoCubicRel(x1, y1, x2, y2, x3, y3);
                    expectNumber = this.skipCommaSpaces2();
                    continue;
                }
            }
        }
    }
    
    protected void parseC() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                }
                case 43:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    final float x1 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y1 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float x2 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y2 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float x3 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y3 = this.parseFloat();
                    this.pathHandler.curvetoCubicAbs(x1, y1, x2, y2, x3, y3);
                    expectNumber = this.skipCommaSpaces2();
                    continue;
                }
            }
        }
    }
    
    protected void parseq() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                }
                case 43:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    final float x1 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y1 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float x2 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y2 = this.parseFloat();
                    this.pathHandler.curvetoQuadraticRel(x1, y1, x2, y2);
                    expectNumber = this.skipCommaSpaces2();
                    continue;
                }
            }
        }
    }
    
    protected void parseQ() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                }
                case 43:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    final float x1 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y1 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float x2 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y2 = this.parseFloat();
                    this.pathHandler.curvetoQuadraticAbs(x1, y1, x2, y2);
                    expectNumber = this.skipCommaSpaces2();
                    continue;
                }
            }
        }
    }
    
    protected void parses() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                }
                case 43:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    final float x2 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y2 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float x3 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y3 = this.parseFloat();
                    this.pathHandler.curvetoCubicSmoothRel(x2, y2, x3, y3);
                    expectNumber = this.skipCommaSpaces2();
                    continue;
                }
            }
        }
    }
    
    protected void parseS() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                }
                case 43:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    final float x2 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y2 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float x3 = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y3 = this.parseFloat();
                    this.pathHandler.curvetoCubicSmoothAbs(x2, y2, x3, y3);
                    expectNumber = this.skipCommaSpaces2();
                    continue;
                }
            }
        }
    }
    
    protected void parset() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                }
                case 43:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    final float x = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y = this.parseFloat();
                    this.pathHandler.curvetoQuadraticSmoothRel(x, y);
                    expectNumber = this.skipCommaSpaces2();
                    continue;
                }
            }
        }
    }
    
    protected void parseT() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                }
                case 43:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    final float x = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y = this.parseFloat();
                    this.pathHandler.curvetoQuadraticSmoothAbs(x, y);
                    expectNumber = this.skipCommaSpaces2();
                    continue;
                }
            }
        }
    }
    
    protected void parsea() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                }
                case 43:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    final float rx = this.parseFloat();
                    this.skipCommaSpaces();
                    final float ry = this.parseFloat();
                    this.skipCommaSpaces();
                    final float ax = this.parseFloat();
                    this.skipCommaSpaces();
                    boolean laf = false;
                    switch (this.current) {
                        default: {
                            this.reportUnexpected(this.current);
                            return;
                        }
                        case 48: {
                            laf = false;
                            break;
                        }
                        case 49: {
                            laf = true;
                            break;
                        }
                    }
                    this.current = this.reader.read();
                    this.skipCommaSpaces();
                    boolean sf = false;
                    switch (this.current) {
                        default: {
                            this.reportUnexpected(this.current);
                            return;
                        }
                        case 48: {
                            sf = false;
                            break;
                        }
                        case 49: {
                            sf = true;
                            break;
                        }
                    }
                    this.current = this.reader.read();
                    this.skipCommaSpaces();
                    final float x = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y = this.parseFloat();
                    this.pathHandler.arcRel(rx, ry, ax, laf, sf, x, y);
                    expectNumber = this.skipCommaSpaces2();
                    continue;
                }
            }
        }
    }
    
    protected void parseA() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                }
                case 43:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    final float rx = this.parseFloat();
                    this.skipCommaSpaces();
                    final float ry = this.parseFloat();
                    this.skipCommaSpaces();
                    final float ax = this.parseFloat();
                    this.skipCommaSpaces();
                    boolean laf = false;
                    switch (this.current) {
                        default: {
                            this.reportUnexpected(this.current);
                            return;
                        }
                        case 48: {
                            laf = false;
                            break;
                        }
                        case 49: {
                            laf = true;
                            break;
                        }
                    }
                    this.current = this.reader.read();
                    this.skipCommaSpaces();
                    boolean sf = false;
                    switch (this.current) {
                        default: {
                            this.reportUnexpected(this.current);
                            return;
                        }
                        case 48: {
                            sf = false;
                            break;
                        }
                        case 49: {
                            sf = true;
                            break;
                        }
                    }
                    this.current = this.reader.read();
                    this.skipCommaSpaces();
                    final float x = this.parseFloat();
                    this.skipCommaSpaces();
                    final float y = this.parseFloat();
                    this.pathHandler.arcAbs(rx, ry, ax, laf, sf, x, y);
                    expectNumber = this.skipCommaSpaces2();
                    continue;
                }
            }
        }
    }
    
    protected void skipSubPath() throws ParseException, IOException {
    Label_0040:
        while (true) {
            switch (this.current) {
                case -1:
                case 77:
                case 109: {
                    break Label_0040;
                }
                default: {
                    this.current = this.reader.read();
                    continue;
                }
            }
        }
    }
    
    protected void reportUnexpected(final int ch) throws ParseException, IOException {
        this.reportUnexpectedCharacterError(this.current);
        this.skipSubPath();
    }
    
    protected boolean skipCommaSpaces2() throws IOException {
        while (true) {
            switch (this.current) {
                default: {
                    if (this.current != 44) {
                        return false;
                    }
                    while (true) {
                        switch (this.current = this.reader.read()) {
                            default: {
                                return true;
                            }
                            case 9:
                            case 10:
                            case 13:
                            case 32: {
                                continue;
                            }
                        }
                    }
                    break;
                }
                case 9:
                case 10:
                case 13:
                case 32: {
                    this.current = this.reader.read();
                    continue;
                }
            }
        }
    }
}
