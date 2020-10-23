// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.io.IOException;

public abstract class NumberParser extends AbstractParser
{
    private static final double[] pow10;
    
    protected float parseFloat() throws ParseException, IOException {
        int mant = 0;
        int mantDig = 0;
        boolean mantPos = true;
        boolean mantRead = false;
        int exp = 0;
        int expDig = 0;
        int expAdj = 0;
        boolean expPos = true;
        switch (this.current) {
            case 45: {
                mantPos = false;
            }
            case 43: {
                this.current = this.reader.read();
                break;
            }
        }
        Label_0287: {
            switch (this.current) {
                default: {
                    this.reportUnexpectedCharacterError(this.current);
                    return 0.0f;
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
                                break Label_0287;
                            }
                            case 46:
                            case 69:
                            case 101: {
                                break Label_0287;
                            }
                            default: {
                                return 0.0f;
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
                                break Label_0287;
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
        Label_0730: {
            if (this.current == 46) {
                Label_0629: {
                    switch (this.current = this.reader.read()) {
                        default: {
                            if (!mantRead) {
                                this.reportUnexpectedCharacterError(this.current);
                                return 0.0f;
                            }
                            break;
                        }
                        case 48: {
                            if (mantDig != 0) {
                                break Label_0629;
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
                                        break Label_0629;
                                    }
                                    default: {
                                        if (!mantRead) {
                                            return 0.0f;
                                        }
                                        break Label_0730;
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
                                        break Label_0730;
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
        Label_1178: {
            switch (this.current) {
                case 69:
                case 101: {
                    Label_0942: {
                        switch (this.current = this.reader.read()) {
                            default: {
                                this.reportUnexpectedCharacterError(this.current);
                                return 0.0f;
                            }
                            case 45: {
                                expPos = false;
                            }
                            case 43: {
                                switch (this.current = this.reader.read()) {
                                    default: {
                                        this.reportUnexpectedCharacterError(this.current);
                                        return 0.0f;
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
                                        break Label_0942;
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
                                Label_1077: {
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
                                                        break Label_1077;
                                                    }
                                                    default: {
                                                        break Label_1178;
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
                                                        break Label_1178;
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
        return buildFloat(mant, exp);
    }
    
    public static float buildFloat(int mant, final int exp) {
        if (exp < -125 || mant == 0) {
            return 0.0f;
        }
        if (exp >= 128) {
            return (mant > 0) ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
        }
        if (exp == 0) {
            return (float)mant;
        }
        if (mant >= 67108864) {
            ++mant;
        }
        return (float)((exp > 0) ? (mant * NumberParser.pow10[exp]) : (mant / NumberParser.pow10[-exp]));
    }
    
    static {
        pow10 = new double[128];
        for (int i = 0; i < NumberParser.pow10.length; ++i) {
            NumberParser.pow10[i] = Math.pow(10.0, i);
        }
    }
}
