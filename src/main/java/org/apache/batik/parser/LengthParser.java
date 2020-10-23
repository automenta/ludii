// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.io.IOException;

public class LengthParser extends AbstractParser
{
    protected LengthHandler lengthHandler;
    
    public LengthParser() {
        this.lengthHandler = DefaultLengthHandler.INSTANCE;
    }
    
    public void setLengthHandler(final LengthHandler handler) {
        this.lengthHandler = handler;
    }
    
    public LengthHandler getLengthHandler() {
        return this.lengthHandler;
    }
    
    @Override
    protected void doParse() throws ParseException, IOException {
        this.lengthHandler.startLength();
        this.current = this.reader.read();
        this.skipSpaces();
        this.parseLength();
        this.skipSpaces();
        if (this.current != -1) {
            this.reportError("end.of.stream.expected", new Object[] { this.current });
        }
        this.lengthHandler.endLength();
    }
    
    protected void parseLength() throws ParseException, IOException {
        int mant = 0;
        int mantDig = 0;
        boolean mantPos = true;
        boolean mantRead = false;
        int exp = 0;
        int expDig = 0;
        int expAdj = 0;
        boolean expPos = true;
        int unitState = 0;
        switch (this.current) {
            case 45: {
                mantPos = false;
            }
            case 43: {
                this.current = this.reader.read();
                break;
            }
        }
        Label_0229: {
            switch (this.current) {
                default: {
                    this.reportUnexpectedCharacterError(this.current);
                    return;
                }
                case 46: {
                    break;
                }
                case 48: {
                    mantRead = true;
                    while (true) {
                        switch (this.current = this.reader.read()) {
                            case 49:
                            case 50:
                            case 51:
                            case 52:
                            case 53:
                            case 54:
                            case 55:
                            case 56:
                            case 57: {
                                break Label_0229;
                            }
                            default: {
                                break Label_0229;
                            }
                            case 48: {
                                continue;
                            }
                        }
                    }
                    break;
                }
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    mantRead = true;
                    while (true) {
                        if (mantDig < 9) {
                            ++mantDig;
                            mant = mant * 10 + (this.current - 48);
                        }
                        else {
                            ++expAdj;
                        }
                        switch (this.current = this.reader.read()) {
                            default: {
                                break Label_0229;
                            }
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
                                continue;
                            }
                        }
                    }
                    break;
                }
            }
        }
        Label_0666: {
            if (this.current == 46) {
                Label_0565: {
                    switch (this.current = this.reader.read()) {
                        default: {
                            if (!mantRead) {
                                this.reportUnexpectedCharacterError(this.current);
                                return;
                            }
                            break;
                        }
                        case 48: {
                            if (mantDig != 0) {
                                break Label_0565;
                            }
                            while (true) {
                                this.current = this.reader.read();
                                --expAdj;
                                switch (this.current) {
                                    case 49:
                                    case 50:
                                    case 51:
                                    case 52:
                                    case 53:
                                    case 54:
                                    case 55:
                                    case 56:
                                    case 57: {
                                        break Label_0565;
                                    }
                                    default: {
                                        break Label_0666;
                                    }
                                    case 48: {
                                        continue;
                                    }
                                }
                            }
                            break;
                        }
                        case 49:
                        case 50:
                        case 51:
                        case 52:
                        case 53:
                        case 54:
                        case 55:
                        case 56:
                        case 57: {
                            while (true) {
                                if (mantDig < 9) {
                                    ++mantDig;
                                    mant = mant * 10 + (this.current - 48);
                                    --expAdj;
                                }
                                switch (this.current = this.reader.read()) {
                                    default: {
                                        break Label_0666;
                                    }
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
                                        continue;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        boolean le = false;
        Label_1206: {
            switch (this.current) {
                case 101: {
                    le = true;
                }
                case 69: {
                    Label_0969: {
                        switch (this.current = this.reader.read()) {
                            default: {
                                this.reportUnexpectedCharacterError(this.current);
                                return;
                            }
                            case 109: {
                                if (!le) {
                                    this.reportUnexpectedCharacterError(this.current);
                                    return;
                                }
                                unitState = 1;
                                break Label_1206;
                            }
                            case 120: {
                                if (!le) {
                                    this.reportUnexpectedCharacterError(this.current);
                                    return;
                                }
                                unitState = 2;
                                break Label_1206;
                            }
                            case 45: {
                                expPos = false;
                            }
                            case 43: {
                                switch (this.current = this.reader.read()) {
                                    default: {
                                        this.reportUnexpectedCharacterError(this.current);
                                        return;
                                    }
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
                                        break Label_0969;
                                    }
                                }
                                break;
                            }
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
                                Label_1105: {
                                    switch (this.current) {
                                        case 48: {
                                            while (true) {
                                                switch (this.current = this.reader.read()) {
                                                    case 49:
                                                    case 50:
                                                    case 51:
                                                    case 52:
                                                    case 53:
                                                    case 54:
                                                    case 55:
                                                    case 56:
                                                    case 57: {
                                                        break Label_1105;
                                                    }
                                                    default: {
                                                        break Label_1206;
                                                    }
                                                    case 48: {
                                                        continue;
                                                    }
                                                }
                                            }
                                            break;
                                        }
                                        case 49:
                                        case 50:
                                        case 51:
                                        case 52:
                                        case 53:
                                        case 54:
                                        case 55:
                                        case 56:
                                        case 57: {
                                            while (true) {
                                                if (expDig < 3) {
                                                    ++expDig;
                                                    exp = exp * 10 + (this.current - 48);
                                                }
                                                switch (this.current = this.reader.read()) {
                                                    default: {
                                                        break Label_1206;
                                                    }
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
                                                        continue;
                                                    }
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
        if (!expPos) {
            exp = -exp;
        }
        exp += expAdj;
        if (!mantPos) {
            mant = -mant;
        }
        this.lengthHandler.lengthValue(NumberParser.buildFloat(mant, exp));
        switch (unitState) {
            case 1: {
                this.lengthHandler.em();
                this.current = this.reader.read();
            }
            case 2: {
                this.lengthHandler.ex();
                this.current = this.reader.read();
            }
            default: {
                Label_1792: {
                    switch (this.current) {
                        case 101: {
                            switch (this.current = this.reader.read()) {
                                case 109: {
                                    this.lengthHandler.em();
                                    this.current = this.reader.read();
                                    break Label_1792;
                                }
                                case 120: {
                                    this.lengthHandler.ex();
                                    this.current = this.reader.read();
                                    break Label_1792;
                                }
                                default: {
                                    this.reportUnexpectedCharacterError(this.current);
                                    break Label_1792;
                                }
                            }
                            break;
                        }
                        case 112: {
                            switch (this.current = this.reader.read()) {
                                case 99: {
                                    this.lengthHandler.pc();
                                    this.current = this.reader.read();
                                    break Label_1792;
                                }
                                case 116: {
                                    this.lengthHandler.pt();
                                    this.current = this.reader.read();
                                    break Label_1792;
                                }
                                case 120: {
                                    this.lengthHandler.px();
                                    this.current = this.reader.read();
                                    break Label_1792;
                                }
                                default: {
                                    this.reportUnexpectedCharacterError(this.current);
                                    break Label_1792;
                                }
                            }
                            break;
                        }
                        case 105: {
                            this.current = this.reader.read();
                            if (this.current != 110) {
                                this.reportCharacterExpectedError('n', this.current);
                                break;
                            }
                            this.lengthHandler.in();
                            this.current = this.reader.read();
                            break;
                        }
                        case 99: {
                            this.current = this.reader.read();
                            if (this.current != 109) {
                                this.reportCharacterExpectedError('m', this.current);
                                break;
                            }
                            this.lengthHandler.cm();
                            this.current = this.reader.read();
                            break;
                        }
                        case 109: {
                            this.current = this.reader.read();
                            if (this.current != 109) {
                                this.reportCharacterExpectedError('m', this.current);
                                break;
                            }
                            this.lengthHandler.mm();
                            this.current = this.reader.read();
                            break;
                        }
                        case 37: {
                            this.lengthHandler.percentage();
                            this.current = this.reader.read();
                            break;
                        }
                    }
                }
            }
        }
    }
}
