// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.io.IOException;

public class PreserveAspectRatioParser extends AbstractParser
{
    protected PreserveAspectRatioHandler preserveAspectRatioHandler;
    
    public PreserveAspectRatioParser() {
        this.preserveAspectRatioHandler = DefaultPreserveAspectRatioHandler.INSTANCE;
    }
    
    public void setPreserveAspectRatioHandler(final PreserveAspectRatioHandler handler) {
        this.preserveAspectRatioHandler = handler;
    }
    
    public PreserveAspectRatioHandler getPreserveAspectRatioHandler() {
        return this.preserveAspectRatioHandler;
    }
    
    @Override
    protected void doParse() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        this.parsePreserveAspectRatio();
    }
    
    protected void parsePreserveAspectRatio() throws ParseException, IOException {
        this.preserveAspectRatioHandler.startPreserveAspectRatio();
        Label_1222: {
            switch (this.current) {
                case 110: {
                    this.current = this.reader.read();
                    if (this.current != 111) {
                        this.reportCharacterExpectedError('o', this.current);
                        this.skipIdentifier();
                        break;
                    }
                    this.current = this.reader.read();
                    if (this.current != 110) {
                        this.reportCharacterExpectedError('o', this.current);
                        this.skipIdentifier();
                        break;
                    }
                    this.current = this.reader.read();
                    if (this.current != 101) {
                        this.reportCharacterExpectedError('e', this.current);
                        this.skipIdentifier();
                        break;
                    }
                    this.current = this.reader.read();
                    this.skipSpaces();
                    this.preserveAspectRatioHandler.none();
                    break;
                }
                case 120: {
                    this.current = this.reader.read();
                    if (this.current != 77) {
                        this.reportCharacterExpectedError('M', this.current);
                        this.skipIdentifier();
                        break;
                    }
                    switch (this.current = this.reader.read()) {
                        case 97: {
                            this.current = this.reader.read();
                            if (this.current != 120) {
                                this.reportCharacterExpectedError('x', this.current);
                                this.skipIdentifier();
                                break Label_1222;
                            }
                            this.current = this.reader.read();
                            if (this.current != 89) {
                                this.reportCharacterExpectedError('Y', this.current);
                                this.skipIdentifier();
                                break Label_1222;
                            }
                            this.current = this.reader.read();
                            if (this.current != 77) {
                                this.reportCharacterExpectedError('M', this.current);
                                this.skipIdentifier();
                                break Label_1222;
                            }
                            Label_0569: {
                                switch (this.current = this.reader.read()) {
                                    case 97: {
                                        this.current = this.reader.read();
                                        if (this.current != 120) {
                                            this.reportCharacterExpectedError('x', this.current);
                                            this.skipIdentifier();
                                            break;
                                        }
                                        this.preserveAspectRatioHandler.xMaxYMax();
                                        this.current = this.reader.read();
                                        break;
                                    }
                                    case 105: {
                                        switch (this.current = this.reader.read()) {
                                            case 100: {
                                                this.preserveAspectRatioHandler.xMaxYMid();
                                                this.current = this.reader.read();
                                                break Label_0569;
                                            }
                                            case 110: {
                                                this.preserveAspectRatioHandler.xMaxYMin();
                                                this.current = this.reader.read();
                                                break Label_0569;
                                            }
                                            default: {
                                                this.reportUnexpectedCharacterError(this.current);
                                                this.skipIdentifier();
                                                break Label_1222;
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                            break Label_1222;
                        }
                        case 105: {
                            switch (this.current = this.reader.read()) {
                                case 100: {
                                    this.current = this.reader.read();
                                    if (this.current != 89) {
                                        this.reportCharacterExpectedError('Y', this.current);
                                        this.skipIdentifier();
                                        break Label_1222;
                                    }
                                    this.current = this.reader.read();
                                    if (this.current != 77) {
                                        this.reportCharacterExpectedError('M', this.current);
                                        this.skipIdentifier();
                                        break Label_1222;
                                    }
                                    Label_0889: {
                                        switch (this.current = this.reader.read()) {
                                            case 97: {
                                                this.current = this.reader.read();
                                                if (this.current != 120) {
                                                    this.reportCharacterExpectedError('x', this.current);
                                                    this.skipIdentifier();
                                                    break;
                                                }
                                                this.preserveAspectRatioHandler.xMidYMax();
                                                this.current = this.reader.read();
                                                break;
                                            }
                                            case 105: {
                                                switch (this.current = this.reader.read()) {
                                                    case 100: {
                                                        this.preserveAspectRatioHandler.xMidYMid();
                                                        this.current = this.reader.read();
                                                        break Label_0889;
                                                    }
                                                    case 110: {
                                                        this.preserveAspectRatioHandler.xMidYMin();
                                                        this.current = this.reader.read();
                                                        break Label_0889;
                                                    }
                                                    default: {
                                                        this.reportUnexpectedCharacterError(this.current);
                                                        this.skipIdentifier();
                                                        break Label_1222;
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    break Label_1222;
                                }
                                case 110: {
                                    this.current = this.reader.read();
                                    if (this.current != 89) {
                                        this.reportCharacterExpectedError('Y', this.current);
                                        this.skipIdentifier();
                                        break Label_1222;
                                    }
                                    this.current = this.reader.read();
                                    if (this.current != 77) {
                                        this.reportCharacterExpectedError('M', this.current);
                                        this.skipIdentifier();
                                        break Label_1222;
                                    }
                                    Label_1169: {
                                        switch (this.current = this.reader.read()) {
                                            case 97: {
                                                this.current = this.reader.read();
                                                if (this.current != 120) {
                                                    this.reportCharacterExpectedError('x', this.current);
                                                    this.skipIdentifier();
                                                    break;
                                                }
                                                this.preserveAspectRatioHandler.xMinYMax();
                                                this.current = this.reader.read();
                                                break;
                                            }
                                            case 105: {
                                                switch (this.current = this.reader.read()) {
                                                    case 100: {
                                                        this.preserveAspectRatioHandler.xMinYMid();
                                                        this.current = this.reader.read();
                                                        break Label_1169;
                                                    }
                                                    case 110: {
                                                        this.preserveAspectRatioHandler.xMinYMin();
                                                        this.current = this.reader.read();
                                                        break Label_1169;
                                                    }
                                                    default: {
                                                        this.reportUnexpectedCharacterError(this.current);
                                                        this.skipIdentifier();
                                                        break Label_1222;
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    break Label_1222;
                                }
                                default: {
                                    this.reportUnexpectedCharacterError(this.current);
                                    this.skipIdentifier();
                                    break Label_1222;
                                }
                            }
                            break;
                        }
                        default: {
                            this.reportUnexpectedCharacterError(this.current);
                            this.skipIdentifier();
                            break Label_1222;
                        }
                    }
                    break;
                }
                default: {
                    if (this.current != -1) {
                        this.reportUnexpectedCharacterError(this.current);
                        this.skipIdentifier();
                        break;
                    }
                    break;
                }
            }
        }
        this.skipCommaSpaces();
        switch (this.current) {
            case 109: {
                this.current = this.reader.read();
                if (this.current != 101) {
                    this.reportCharacterExpectedError('e', this.current);
                    this.skipIdentifier();
                    break;
                }
                this.current = this.reader.read();
                if (this.current != 101) {
                    this.reportCharacterExpectedError('e', this.current);
                    this.skipIdentifier();
                    break;
                }
                this.current = this.reader.read();
                if (this.current != 116) {
                    this.reportCharacterExpectedError('t', this.current);
                    this.skipIdentifier();
                    break;
                }
                this.preserveAspectRatioHandler.meet();
                this.current = this.reader.read();
                break;
            }
            case 115: {
                this.current = this.reader.read();
                if (this.current != 108) {
                    this.reportCharacterExpectedError('l', this.current);
                    this.skipIdentifier();
                    break;
                }
                this.current = this.reader.read();
                if (this.current != 105) {
                    this.reportCharacterExpectedError('i', this.current);
                    this.skipIdentifier();
                    break;
                }
                this.current = this.reader.read();
                if (this.current != 99) {
                    this.reportCharacterExpectedError('c', this.current);
                    this.skipIdentifier();
                    break;
                }
                this.current = this.reader.read();
                if (this.current != 101) {
                    this.reportCharacterExpectedError('e', this.current);
                    this.skipIdentifier();
                    break;
                }
                this.preserveAspectRatioHandler.slice();
                this.current = this.reader.read();
                break;
            }
            default: {
                if (this.current != -1) {
                    this.reportUnexpectedCharacterError(this.current);
                    this.skipIdentifier();
                    break;
                }
                break;
            }
        }
        this.skipSpaces();
        if (this.current != -1) {
            this.reportError("end.of.stream.expected", new Object[] { this.current });
        }
        this.preserveAspectRatioHandler.endPreserveAspectRatio();
    }
    
    protected void skipIdentifier() throws IOException {
    Label_0081:
        do {
            switch (this.current = this.reader.read()) {
                case 9:
                case 10:
                case 13:
                case 32: {
                    this.current = this.reader.read();
                    break Label_0081;
                }
                default: {
                    continue;
                }
            }
        } while (this.current != -1);
    }
}
