// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.io.IOException;
import org.apache.batik.xml.XMLUtilities;

public class FragmentIdentifierParser extends NumberParser
{
    protected char[] buffer;
    protected int bufferSize;
    protected FragmentIdentifierHandler fragmentIdentifierHandler;
    
    public FragmentIdentifierParser() {
        this.buffer = new char[16];
        this.fragmentIdentifierHandler = DefaultFragmentIdentifierHandler.INSTANCE;
    }
    
    public void setFragmentIdentifierHandler(final FragmentIdentifierHandler handler) {
        this.fragmentIdentifierHandler = handler;
    }
    
    public FragmentIdentifierHandler getFragmentIdentifierHandler() {
        return this.fragmentIdentifierHandler;
    }
    
    @Override
    protected void doParse() throws ParseException, IOException {
        this.bufferSize = 0;
        this.current = this.reader.read();
        this.fragmentIdentifierHandler.startFragmentIdentifier();
        String id = null;
        Label_0892: {
            switch (this.current) {
                case 120: {
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 112) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 111) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 105) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 110) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 116) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 101) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 114) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 40) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferSize = 0;
                    this.current = this.reader.read();
                    if (this.current != 105) {
                        this.reportCharacterExpectedError('i', this.current);
                        break Label_0892;
                    }
                    this.current = this.reader.read();
                    if (this.current != 100) {
                        this.reportCharacterExpectedError('d', this.current);
                        break Label_0892;
                    }
                    this.current = this.reader.read();
                    if (this.current != 40) {
                        this.reportCharacterExpectedError('(', this.current);
                        break Label_0892;
                    }
                    this.current = this.reader.read();
                    if (this.current != 34 && this.current != 39) {
                        this.reportCharacterExpectedError('\'', this.current);
                        break Label_0892;
                    }
                    final char q = (char)this.current;
                    this.current = this.reader.read();
                    this.parseIdentifier();
                    id = this.getBufferContent();
                    this.bufferSize = 0;
                    this.fragmentIdentifierHandler.idReference(id);
                    if (this.current != q) {
                        this.reportCharacterExpectedError(q, this.current);
                        break Label_0892;
                    }
                    this.current = this.reader.read();
                    if (this.current != 41) {
                        this.reportCharacterExpectedError(')', this.current);
                        break Label_0892;
                    }
                    this.current = this.reader.read();
                    if (this.current != 41) {
                        this.reportCharacterExpectedError(')', this.current);
                    }
                    break Label_0892;
                }
                case 115: {
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 118) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 103) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 86) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 105) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 101) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 119) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 40) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferSize = 0;
                    this.current = this.reader.read();
                    this.parseViewAttributes();
                    if (this.current != 41) {
                        this.reportCharacterExpectedError(')', this.current);
                    }
                    break Label_0892;
                }
                default: {
                    if (this.current == -1) {
                        break Label_0892;
                    }
                    if (!XMLUtilities.isXMLNameFirstCharacter((char)this.current)) {
                        break Label_0892;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    this.parseIdentifier();
                    break;
                }
            }
            id = this.getBufferContent();
            this.fragmentIdentifierHandler.idReference(id);
        }
        this.fragmentIdentifierHandler.endFragmentIdentifier();
    }
    
    protected void parseViewAttributes() throws ParseException, IOException {
        boolean first = true;
        Label_3019: {
            Block_74: {
                Label_2972: {
                    Block_73: {
                        Block_72: {
                            Block_71: {
                                Block_70: {
                                    Block_69: {
                                        Block_68: {
                                            Block_67: {
                                                Block_66: {
                                                    Block_65: {
                                                        Block_64: {
                                                            Block_63: {
                                                                Block_62: {
                                                                    Block_61: {
                                                                        Block_60: {
                                                                            Block_59: {
                                                                                Block_58: {
                                                                                    Block_57: {
                                                                                        Block_56: {
                                                                                            Block_55: {
                                                                                                Block_54: {
                                                                                                    Block_53: {
                                                                                                        Block_52: {
                                                                                                            Block_50: {
                                                                                                                Block_49: {
                                                                                                                    Block_48: {
                                                                                                                        Block_47: {
                                                                                                                            Block_46: {
                                                                                                                                Block_45: {
                                                                                                                                    Block_44: {
                                                                                                                                        Block_43: {
                                                                                                                                            Block_42: {
                                                                                                                                                Block_41: {
                                                                                                                                                    Block_40: {
                                                                                                                                                        Block_39: {
                                                                                                                                                            Block_38: {
                                                                                                                                                                Block_37: {
                                                                                                                                                                    Block_36: {
                                                                                                                                                                        Block_35: {
                                                                                                                                                                            Block_34: {
                                                                                                                                                                                Block_33: {
                                                                                                                                                                                    Block_32: {
                                                                                                                                                                                        Block_31: {
                                                                                                                                                                                            Block_30: {
                                                                                                                                                                                                Block_29: {
                                                                                                                                                                                                    Block_28: {
                                                                                                                                                                                                        Block_27: {
                                                                                                                                                                                                            Block_26: {
                                                                                                                                                                                                                Block_25: {
                                                                                                                                                                                                                    Block_24: {
                                                                                                                                                                                                                        Block_23: {
                                                                                                                                                                                                                            Block_22: {
                                                                                                                                                                                                                                Label_0958: {
                                                                                                                                                                                                                                    Label_0932: {
                                                                                                                                                                                                                                        Label_0812: {
                                                                                                                                                                                                                                            Block_20: {
                                                                                                                                                                                                                                                Block_19: {
                                                                                                                                                                                                                                                    Block_18: {
                                                                                                                                                                                                                                                        Block_17: {
                                                                                                                                                                                                                                                            Block_16: {
                                                                                                                                                                                                                                                                Block_15: {
                                                                                                                                                                                                                                                                    Block_14: {
                                                                                                                                                                                                                                                                        Block_12: {
                                                                                                                                                                                                                                                                            Block_11: {
                                                                                                                                                                                                                                                                                Block_10: {
                                                                                                                                                                                                                                                                                    Block_9: {
                                                                                                                                                                                                                                                                                        Block_8: {
                                                                                                                                                                                                                                                                                            Block_7: {
                                                                                                                                                                                                                                                                                                Block_6: {
                                                                                                                                                                                                                                                                                                    Block_5: {
                                                                                                                                                                                                                                                                                                        Block_4: {
                                                                                                                                                                                                                                                                                                            Block_3: {
                                                                                                                                                                                                                                                                                                                Block_2: {
                                                                                                                                                                                                                                                                                                                Label_0087:
                                                                                                                                                                                                                                                                                                                    while (true) {
                                                                                                                                                                                                                                                                                                                        switch (this.current) {
                                                                                                                                                                                                                                                                                                                            case -1:
                                                                                                                                                                                                                                                                                                                            case 41: {
                                                                                                                                                                                                                                                                                                                                if (first) {
                                                                                                                                                                                                                                                                                                                                    this.reportUnexpectedCharacterError(this.current);
                                                                                                                                                                                                                                                                                                                                    break Label_3019;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                break Label_0087;
                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                            default: {
                                                                                                                                                                                                                                                                                                                                break Label_0087;
                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                            case 59: {
                                                                                                                                                                                                                                                                                                                                if (first) {
                                                                                                                                                                                                                                                                                                                                    break Block_2;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                continue;
                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                            case 118: {
                                                                                                                                                                                                                                                                                                                                first = false;
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 105) {
                                                                                                                                                                                                                                                                                                                                    break Block_3;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 101) {
                                                                                                                                                                                                                                                                                                                                    break Block_4;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 119) {
                                                                                                                                                                                                                                                                                                                                    break Block_5;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                Label_0969: {
                                                                                                                                                                                                                                                                                                                                    switch (this.current = this.reader.read()) {
                                                                                                                                                                                                                                                                                                                                        case 66: {
                                                                                                                                                                                                                                                                                                                                            this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                            if (this.current != 111) {
                                                                                                                                                                                                                                                                                                                                                break Block_6;
                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                            this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                            if (this.current != 120) {
                                                                                                                                                                                                                                                                                                                                                break Block_7;
                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                            this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                            if (this.current != 40) {
                                                                                                                                                                                                                                                                                                                                                break Block_8;
                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                            this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                            final float x = this.parseFloat();
                                                                                                                                                                                                                                                                                                                                            if (this.current != 44) {
                                                                                                                                                                                                                                                                                                                                                break Block_9;
                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                            this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                            final float y = this.parseFloat();
                                                                                                                                                                                                                                                                                                                                            if (this.current != 44) {
                                                                                                                                                                                                                                                                                                                                                break Block_10;
                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                            this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                            final float w = this.parseFloat();
                                                                                                                                                                                                                                                                                                                                            if (this.current != 44) {
                                                                                                                                                                                                                                                                                                                                                break Block_11;
                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                            this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                            final float h = this.parseFloat();
                                                                                                                                                                                                                                                                                                                                            if (this.current != 41) {
                                                                                                                                                                                                                                                                                                                                                break Block_12;
                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                            this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                            this.fragmentIdentifierHandler.viewBox(x, y, w, h);
                                                                                                                                                                                                                                                                                                                                            if (this.current != 41 && this.current != 59) {
                                                                                                                                                                                                                                                                                                                                                break Block_14;
                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                            continue;
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                        case 84: {
                                                                                                                                                                                                                                                                                                                                            this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                            if (this.current != 97) {
                                                                                                                                                                                                                                                                                                                                                break Block_15;
                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                            this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                            if (this.current != 114) {
                                                                                                                                                                                                                                                                                                                                                break Block_16;
                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                            this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                            if (this.current != 103) {
                                                                                                                                                                                                                                                                                                                                                break Block_17;
                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                            this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                            if (this.current != 101) {
                                                                                                                                                                                                                                                                                                                                                break Block_18;
                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                            this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                            if (this.current != 116) {
                                                                                                                                                                                                                                                                                                                                                break Block_19;
                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                            this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                            if (this.current != 40) {
                                                                                                                                                                                                                                                                                                                                                break Block_20;
                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                            this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                            this.fragmentIdentifierHandler.startViewTarget();
                                                                                                                                                                                                                                                                                                                                            while (true) {
                                                                                                                                                                                                                                                                                                                                                this.bufferSize = 0;
                                                                                                                                                                                                                                                                                                                                                if (this.current == -1 || !XMLUtilities.isXMLNameFirstCharacter((char)this.current)) {
                                                                                                                                                                                                                                                                                                                                                    break Label_0812;
                                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                                this.bufferize();
                                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                                this.parseIdentifier();
                                                                                                                                                                                                                                                                                                                                                final String s = this.getBufferContent();
                                                                                                                                                                                                                                                                                                                                                this.fragmentIdentifierHandler.viewTarget(s);
                                                                                                                                                                                                                                                                                                                                                this.bufferSize = 0;
                                                                                                                                                                                                                                                                                                                                                switch (this.current) {
                                                                                                                                                                                                                                                                                                                                                    case 41: {
                                                                                                                                                                                                                                                                                                                                                        this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                                        this.fragmentIdentifierHandler.endViewTarget();
                                                                                                                                                                                                                                                                                                                                                        break Label_0969;
                                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                                    case 44:
                                                                                                                                                                                                                                                                                                                                                    case 59: {
                                                                                                                                                                                                                                                                                                                                                        this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                                        continue;
                                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                                    default: {
                                                                                                                                                                                                                                                                                                                                                        break Label_0932;
                                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                            break;
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                        default: {
                                                                                                                                                                                                                                                                                                                                            break Label_0958;
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                continue;
                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                            case 112: {
                                                                                                                                                                                                                                                                                                                                first = false;
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 114) {
                                                                                                                                                                                                                                                                                                                                    break Block_22;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 101) {
                                                                                                                                                                                                                                                                                                                                    break Block_23;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 115) {
                                                                                                                                                                                                                                                                                                                                    break Block_24;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 101) {
                                                                                                                                                                                                                                                                                                                                    break Block_25;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 114) {
                                                                                                                                                                                                                                                                                                                                    break Block_26;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 118) {
                                                                                                                                                                                                                                                                                                                                    break Block_27;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 101) {
                                                                                                                                                                                                                                                                                                                                    break Block_28;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 65) {
                                                                                                                                                                                                                                                                                                                                    break Block_29;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 115) {
                                                                                                                                                                                                                                                                                                                                    break Block_30;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 112) {
                                                                                                                                                                                                                                                                                                                                    break Block_31;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 101) {
                                                                                                                                                                                                                                                                                                                                    break Block_32;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 99) {
                                                                                                                                                                                                                                                                                                                                    break Block_33;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 116) {
                                                                                                                                                                                                                                                                                                                                    break Block_34;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 82) {
                                                                                                                                                                                                                                                                                                                                    break Block_35;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 97) {
                                                                                                                                                                                                                                                                                                                                    break Block_36;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 116) {
                                                                                                                                                                                                                                                                                                                                    break Block_37;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 105) {
                                                                                                                                                                                                                                                                                                                                    break Block_38;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 111) {
                                                                                                                                                                                                                                                                                                                                    break Block_39;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 40) {
                                                                                                                                                                                                                                                                                                                                    break Block_40;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                this.parsePreserveAspectRatio();
                                                                                                                                                                                                                                                                                                                                if (this.current != 41) {
                                                                                                                                                                                                                                                                                                                                    break Block_41;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                continue;
                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                            case 116: {
                                                                                                                                                                                                                                                                                                                                first = false;
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 114) {
                                                                                                                                                                                                                                                                                                                                    break Block_42;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 97) {
                                                                                                                                                                                                                                                                                                                                    break Block_43;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 110) {
                                                                                                                                                                                                                                                                                                                                    break Block_44;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 115) {
                                                                                                                                                                                                                                                                                                                                    break Block_45;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 102) {
                                                                                                                                                                                                                                                                                                                                    break Block_46;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 111) {
                                                                                                                                                                                                                                                                                                                                    break Block_47;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 114) {
                                                                                                                                                                                                                                                                                                                                    break Block_48;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 109) {
                                                                                                                                                                                                                                                                                                                                    break Block_49;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 40) {
                                                                                                                                                                                                                                                                                                                                    break Block_50;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.fragmentIdentifierHandler.startTransformList();
                                                                                                                                                                                                                                                                                                                            Label_1960_Outer:
                                                                                                                                                                                                                                                                                                                                while (true) {
                                                                                                                                                                                                                                                                                                                                    while (true) {
                                                                                                                                                                                                                                                                                                                                        try {
                                                                                                                                                                                                                                                                                                                                            while (true) {
                                                                                                                                                                                                                                                                                                                                                switch (this.current = this.reader.read()) {
                                                                                                                                                                                                                                                                                                                                                    case 44: {
                                                                                                                                                                                                                                                                                                                                                        continue Label_1960_Outer;
                                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                                    case 109: {
                                                                                                                                                                                                                                                                                                                                                        this.parseMatrix();
                                                                                                                                                                                                                                                                                                                                                        continue Label_1960_Outer;
                                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                                    case 114: {
                                                                                                                                                                                                                                                                                                                                                        this.parseRotate();
                                                                                                                                                                                                                                                                                                                                                        continue Label_1960_Outer;
                                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                                    case 116: {
                                                                                                                                                                                                                                                                                                                                                        this.parseTranslate();
                                                                                                                                                                                                                                                                                                                                                        continue Label_1960_Outer;
                                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                                    case 115: {
                                                                                                                                                                                                                                                                                                                                                        switch (this.current = this.reader.read()) {
                                                                                                                                                                                                                                                                                                                                                            case 99: {
                                                                                                                                                                                                                                                                                                                                                                this.parseScale();
                                                                                                                                                                                                                                                                                                                                                                continue Label_1960_Outer;
                                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                                            case 107: {
                                                                                                                                                                                                                                                                                                                                                                this.parseSkew();
                                                                                                                                                                                                                                                                                                                                                                continue Label_1960_Outer;
                                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                                            default: {
                                                                                                                                                                                                                                                                                                                                                                this.reportUnexpectedCharacterError(this.current);
                                                                                                                                                                                                                                                                                                                                                                this.skipTransform();
                                                                                                                                                                                                                                                                                                                                                                continue Label_1960_Outer;
                                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                                        break;
                                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                                    default: {
                                                                                                                                                                                                                                                                                                                                                        break Label_1960_Outer;
                                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                        catch (ParseException e) {
                                                                                                                                                                                                                                                                                                                                            this.errorHandler.error(e);
                                                                                                                                                                                                                                                                                                                                            this.skipTransform();
                                                                                                                                                                                                                                                                                                                                            continue Label_1960_Outer;
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                        continue;
                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.fragmentIdentifierHandler.endTransformList();
                                                                                                                                                                                                                                                                                                                                continue;
                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                            case 122: {
                                                                                                                                                                                                                                                                                                                                first = false;
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 111) {
                                                                                                                                                                                                                                                                                                                                    break Block_52;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 111) {
                                                                                                                                                                                                                                                                                                                                    break Block_53;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 109) {
                                                                                                                                                                                                                                                                                                                                    break Block_54;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 65) {
                                                                                                                                                                                                                                                                                                                                    break Block_55;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 110) {
                                                                                                                                                                                                                                                                                                                                    break Block_56;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 100) {
                                                                                                                                                                                                                                                                                                                                    break Block_57;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 80) {
                                                                                                                                                                                                                                                                                                                                    break Block_58;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 97) {
                                                                                                                                                                                                                                                                                                                                    break Block_59;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 110) {
                                                                                                                                                                                                                                                                                                                                    break Block_60;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                if (this.current != 40) {
                                                                                                                                                                                                                                                                                                                                    break Block_61;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                switch (this.current = this.reader.read()) {
                                                                                                                                                                                                                                                                                                                                    case 109: {
                                                                                                                                                                                                                                                                                                                                        this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                        if (this.current != 97) {
                                                                                                                                                                                                                                                                                                                                            break Block_62;
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                        this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                        if (this.current != 103) {
                                                                                                                                                                                                                                                                                                                                            break Block_63;
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                        this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                        if (this.current != 110) {
                                                                                                                                                                                                                                                                                                                                            break Block_64;
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                        this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                        if (this.current != 105) {
                                                                                                                                                                                                                                                                                                                                            break Block_65;
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                        this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                        if (this.current != 102) {
                                                                                                                                                                                                                                                                                                                                            break Block_66;
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                        this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                        if (this.current != 121) {
                                                                                                                                                                                                                                                                                                                                            break Block_67;
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                        this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                        this.fragmentIdentifierHandler.zoomAndPan(true);
                                                                                                                                                                                                                                                                                                                                        break;
                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                    case 100: {
                                                                                                                                                                                                                                                                                                                                        this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                        if (this.current != 105) {
                                                                                                                                                                                                                                                                                                                                            break Block_68;
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                        this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                        if (this.current != 115) {
                                                                                                                                                                                                                                                                                                                                            break Block_69;
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                        this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                        if (this.current != 97) {
                                                                                                                                                                                                                                                                                                                                            break Block_70;
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                        this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                        if (this.current != 98) {
                                                                                                                                                                                                                                                                                                                                            break Block_71;
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                        this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                        if (this.current != 108) {
                                                                                                                                                                                                                                                                                                                                            break Block_72;
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                        this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                        if (this.current != 101) {
                                                                                                                                                                                                                                                                                                                                            break Block_73;
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                        this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                        this.fragmentIdentifierHandler.zoomAndPan(false);
                                                                                                                                                                                                                                                                                                                                        break;
                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                    default: {
                                                                                                                                                                                                                                                                                                                                        break Label_2972;
                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                if (this.current != 41) {
                                                                                                                                                                                                                                                                                                                                    break Block_74;
                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                this.current = this.reader.read();
                                                                                                                                                                                                                                                                                                                                continue;
                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                    return;
                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                this.reportUnexpectedCharacterError(this.current);
                                                                                                                                                                                                                                                                                                                return;
                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                            this.reportCharacterExpectedError('i', this.current);
                                                                                                                                                                                                                                                                                                            return;
                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                        this.reportCharacterExpectedError('e', this.current);
                                                                                                                                                                                                                                                                                                        return;
                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                    this.reportCharacterExpectedError('w', this.current);
                                                                                                                                                                                                                                                                                                    return;
                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                this.reportCharacterExpectedError('o', this.current);
                                                                                                                                                                                                                                                                                                return;
                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                            this.reportCharacterExpectedError('x', this.current);
                                                                                                                                                                                                                                                                                            return;
                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                        this.reportCharacterExpectedError('(', this.current);
                                                                                                                                                                                                                                                                                        return;
                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                    this.reportCharacterExpectedError(',', this.current);
                                                                                                                                                                                                                                                                                    return;
                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                this.reportCharacterExpectedError(',', this.current);
                                                                                                                                                                                                                                                                                return;
                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                            this.reportCharacterExpectedError(',', this.current);
                                                                                                                                                                                                                                                                            return;
                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                        this.reportCharacterExpectedError(')', this.current);
                                                                                                                                                                                                                                                                        return;
                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                    this.reportCharacterExpectedError(')', this.current);
                                                                                                                                                                                                                                                                    return;
                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                this.reportCharacterExpectedError('a', this.current);
                                                                                                                                                                                                                                                                return;
                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                            this.reportCharacterExpectedError('r', this.current);
                                                                                                                                                                                                                                                            return;
                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                        this.reportCharacterExpectedError('g', this.current);
                                                                                                                                                                                                                                                        return;
                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                    this.reportCharacterExpectedError('e', this.current);
                                                                                                                                                                                                                                                    return;
                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                this.reportCharacterExpectedError('t', this.current);
                                                                                                                                                                                                                                                return;
                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                            this.reportCharacterExpectedError('(', this.current);
                                                                                                                                                                                                                                            return;
                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                        this.reportUnexpectedCharacterError(this.current);
                                                                                                                                                                                                                                        return;
                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                    this.reportUnexpectedCharacterError(this.current);
                                                                                                                                                                                                                                    return;
                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                this.reportUnexpectedCharacterError(this.current);
                                                                                                                                                                                                                                return;
                                                                                                                                                                                                                            }
                                                                                                                                                                                                                            this.reportCharacterExpectedError('r', this.current);
                                                                                                                                                                                                                            return;
                                                                                                                                                                                                                        }
                                                                                                                                                                                                                        this.reportCharacterExpectedError('e', this.current);
                                                                                                                                                                                                                        return;
                                                                                                                                                                                                                    }
                                                                                                                                                                                                                    this.reportCharacterExpectedError('s', this.current);
                                                                                                                                                                                                                    return;
                                                                                                                                                                                                                }
                                                                                                                                                                                                                this.reportCharacterExpectedError('e', this.current);
                                                                                                                                                                                                                return;
                                                                                                                                                                                                            }
                                                                                                                                                                                                            this.reportCharacterExpectedError('r', this.current);
                                                                                                                                                                                                            return;
                                                                                                                                                                                                        }
                                                                                                                                                                                                        this.reportCharacterExpectedError('v', this.current);
                                                                                                                                                                                                        return;
                                                                                                                                                                                                    }
                                                                                                                                                                                                    this.reportCharacterExpectedError('e', this.current);
                                                                                                                                                                                                    return;
                                                                                                                                                                                                }
                                                                                                                                                                                                this.reportCharacterExpectedError('A', this.current);
                                                                                                                                                                                                return;
                                                                                                                                                                                            }
                                                                                                                                                                                            this.reportCharacterExpectedError('s', this.current);
                                                                                                                                                                                            return;
                                                                                                                                                                                        }
                                                                                                                                                                                        this.reportCharacterExpectedError('p', this.current);
                                                                                                                                                                                        return;
                                                                                                                                                                                    }
                                                                                                                                                                                    this.reportCharacterExpectedError('e', this.current);
                                                                                                                                                                                    return;
                                                                                                                                                                                }
                                                                                                                                                                                this.reportCharacterExpectedError('c', this.current);
                                                                                                                                                                                return;
                                                                                                                                                                            }
                                                                                                                                                                            this.reportCharacterExpectedError('t', this.current);
                                                                                                                                                                            return;
                                                                                                                                                                        }
                                                                                                                                                                        this.reportCharacterExpectedError('R', this.current);
                                                                                                                                                                        return;
                                                                                                                                                                    }
                                                                                                                                                                    this.reportCharacterExpectedError('a', this.current);
                                                                                                                                                                    return;
                                                                                                                                                                }
                                                                                                                                                                this.reportCharacterExpectedError('t', this.current);
                                                                                                                                                                return;
                                                                                                                                                            }
                                                                                                                                                            this.reportCharacterExpectedError('i', this.current);
                                                                                                                                                            return;
                                                                                                                                                        }
                                                                                                                                                        this.reportCharacterExpectedError('o', this.current);
                                                                                                                                                        return;
                                                                                                                                                    }
                                                                                                                                                    this.reportCharacterExpectedError('(', this.current);
                                                                                                                                                    return;
                                                                                                                                                }
                                                                                                                                                this.reportCharacterExpectedError(')', this.current);
                                                                                                                                                return;
                                                                                                                                            }
                                                                                                                                            this.reportCharacterExpectedError('r', this.current);
                                                                                                                                            return;
                                                                                                                                        }
                                                                                                                                        this.reportCharacterExpectedError('a', this.current);
                                                                                                                                        return;
                                                                                                                                    }
                                                                                                                                    this.reportCharacterExpectedError('n', this.current);
                                                                                                                                    return;
                                                                                                                                }
                                                                                                                                this.reportCharacterExpectedError('s', this.current);
                                                                                                                                return;
                                                                                                                            }
                                                                                                                            this.reportCharacterExpectedError('f', this.current);
                                                                                                                            return;
                                                                                                                        }
                                                                                                                        this.reportCharacterExpectedError('o', this.current);
                                                                                                                        return;
                                                                                                                    }
                                                                                                                    this.reportCharacterExpectedError('r', this.current);
                                                                                                                    return;
                                                                                                                }
                                                                                                                this.reportCharacterExpectedError('m', this.current);
                                                                                                                return;
                                                                                                            }
                                                                                                            this.reportCharacterExpectedError('(', this.current);
                                                                                                            return;
                                                                                                        }
                                                                                                        this.reportCharacterExpectedError('o', this.current);
                                                                                                        return;
                                                                                                    }
                                                                                                    this.reportCharacterExpectedError('o', this.current);
                                                                                                    return;
                                                                                                }
                                                                                                this.reportCharacterExpectedError('m', this.current);
                                                                                                return;
                                                                                            }
                                                                                            this.reportCharacterExpectedError('A', this.current);
                                                                                            return;
                                                                                        }
                                                                                        this.reportCharacterExpectedError('n', this.current);
                                                                                        return;
                                                                                    }
                                                                                    this.reportCharacterExpectedError('d', this.current);
                                                                                    return;
                                                                                }
                                                                                this.reportCharacterExpectedError('P', this.current);
                                                                                return;
                                                                            }
                                                                            this.reportCharacterExpectedError('a', this.current);
                                                                            return;
                                                                        }
                                                                        this.reportCharacterExpectedError('n', this.current);
                                                                        return;
                                                                    }
                                                                    this.reportCharacterExpectedError('(', this.current);
                                                                    return;
                                                                }
                                                                this.reportCharacterExpectedError('a', this.current);
                                                                return;
                                                            }
                                                            this.reportCharacterExpectedError('g', this.current);
                                                            return;
                                                        }
                                                        this.reportCharacterExpectedError('n', this.current);
                                                        return;
                                                    }
                                                    this.reportCharacterExpectedError('i', this.current);
                                                    return;
                                                }
                                                this.reportCharacterExpectedError('f', this.current);
                                                return;
                                            }
                                            this.reportCharacterExpectedError('y', this.current);
                                            return;
                                        }
                                        this.reportCharacterExpectedError('i', this.current);
                                        return;
                                    }
                                    this.reportCharacterExpectedError('s', this.current);
                                    return;
                                }
                                this.reportCharacterExpectedError('a', this.current);
                                return;
                            }
                            this.reportCharacterExpectedError('b', this.current);
                            return;
                        }
                        this.reportCharacterExpectedError('l', this.current);
                        return;
                    }
                    this.reportCharacterExpectedError('e', this.current);
                    return;
                }
                this.reportUnexpectedCharacterError(this.current);
                return;
            }
            this.reportCharacterExpectedError(')', this.current);
        }
    }
    
    protected void parseIdentifier() throws ParseException, IOException {
        while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current)) {
            this.bufferize();
            this.current = this.reader.read();
        }
    }
    
    protected String getBufferContent() {
        return new String(this.buffer, 0, this.bufferSize);
    }
    
    protected void bufferize() {
        if (this.bufferSize >= this.buffer.length) {
            final char[] t = new char[this.buffer.length * 2];
            System.arraycopy(this.buffer, 0, t, 0, this.bufferSize);
            this.buffer = t;
        }
        this.buffer[this.bufferSize++] = (char)this.current;
    }
    
    @Override
    protected void skipSpaces() throws IOException {
        if (this.current == 44) {
            this.current = this.reader.read();
        }
    }
    
    @Override
    protected void skipCommaSpaces() throws IOException {
        if (this.current == 44) {
            this.current = this.reader.read();
        }
    }
    
    protected void parseMatrix() throws ParseException, IOException {
        this.current = this.reader.read();
        if (this.current != 97) {
            this.reportCharacterExpectedError('a', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 116) {
            this.reportCharacterExpectedError('t', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 114) {
            this.reportCharacterExpectedError('r', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 105) {
            this.reportCharacterExpectedError('i', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 120) {
            this.reportCharacterExpectedError('x', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        this.skipSpaces();
        if (this.current != 40) {
            this.reportCharacterExpectedError('(', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        this.skipSpaces();
        final float a = this.parseFloat();
        this.skipCommaSpaces();
        final float b = this.parseFloat();
        this.skipCommaSpaces();
        final float c = this.parseFloat();
        this.skipCommaSpaces();
        final float d = this.parseFloat();
        this.skipCommaSpaces();
        final float e = this.parseFloat();
        this.skipCommaSpaces();
        final float f = this.parseFloat();
        this.skipSpaces();
        if (this.current != 41) {
            this.reportCharacterExpectedError(')', this.current);
            this.skipTransform();
            return;
        }
        this.fragmentIdentifierHandler.matrix(a, b, c, d, e, f);
    }
    
    protected void parseRotate() throws ParseException, IOException {
        this.current = this.reader.read();
        if (this.current != 111) {
            this.reportCharacterExpectedError('o', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 116) {
            this.reportCharacterExpectedError('t', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 97) {
            this.reportCharacterExpectedError('a', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 116) {
            this.reportCharacterExpectedError('t', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 101) {
            this.reportCharacterExpectedError('e', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        this.skipSpaces();
        if (this.current != 40) {
            this.reportCharacterExpectedError('(', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        this.skipSpaces();
        final float theta = this.parseFloat();
        this.skipSpaces();
        switch (this.current) {
            case 41: {
                this.fragmentIdentifierHandler.rotate(theta);
                return;
            }
            case 44: {
                this.current = this.reader.read();
                this.skipSpaces();
                break;
            }
        }
        final float cx = this.parseFloat();
        this.skipCommaSpaces();
        final float cy = this.parseFloat();
        this.skipSpaces();
        if (this.current != 41) {
            this.reportCharacterExpectedError(')', this.current);
            this.skipTransform();
            return;
        }
        this.fragmentIdentifierHandler.rotate(theta, cx, cy);
    }
    
    protected void parseTranslate() throws ParseException, IOException {
        this.current = this.reader.read();
        if (this.current != 114) {
            this.reportCharacterExpectedError('r', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 97) {
            this.reportCharacterExpectedError('a', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 110) {
            this.reportCharacterExpectedError('n', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 115) {
            this.reportCharacterExpectedError('s', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 108) {
            this.reportCharacterExpectedError('l', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 97) {
            this.reportCharacterExpectedError('a', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 116) {
            this.reportCharacterExpectedError('t', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 101) {
            this.reportCharacterExpectedError('e', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        this.skipSpaces();
        if (this.current != 40) {
            this.reportCharacterExpectedError('(', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        this.skipSpaces();
        final float tx = this.parseFloat();
        this.skipSpaces();
        switch (this.current) {
            case 41: {
                this.fragmentIdentifierHandler.translate(tx);
                return;
            }
            case 44: {
                this.current = this.reader.read();
                this.skipSpaces();
                break;
            }
        }
        final float ty = this.parseFloat();
        this.skipSpaces();
        if (this.current != 41) {
            this.reportCharacterExpectedError(')', this.current);
            this.skipTransform();
            return;
        }
        this.fragmentIdentifierHandler.translate(tx, ty);
    }
    
    protected void parseScale() throws ParseException, IOException {
        this.current = this.reader.read();
        if (this.current != 97) {
            this.reportCharacterExpectedError('a', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 108) {
            this.reportCharacterExpectedError('l', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 101) {
            this.reportCharacterExpectedError('e', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        this.skipSpaces();
        if (this.current != 40) {
            this.reportCharacterExpectedError('(', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        this.skipSpaces();
        final float sx = this.parseFloat();
        this.skipSpaces();
        switch (this.current) {
            case 41: {
                this.fragmentIdentifierHandler.scale(sx);
                return;
            }
            case 44: {
                this.current = this.reader.read();
                this.skipSpaces();
                break;
            }
        }
        final float sy = this.parseFloat();
        this.skipSpaces();
        if (this.current != 41) {
            this.reportCharacterExpectedError(')', this.current);
            this.skipTransform();
            return;
        }
        this.fragmentIdentifierHandler.scale(sx, sy);
    }
    
    protected void parseSkew() throws ParseException, IOException {
        this.current = this.reader.read();
        if (this.current != 101) {
            this.reportCharacterExpectedError('e', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 119) {
            this.reportCharacterExpectedError('w', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        boolean skewX = false;
        switch (this.current) {
            case 88: {
                skewX = true;
            }
            case 89: {
                this.current = this.reader.read();
                this.skipSpaces();
                if (this.current != 40) {
                    this.reportCharacterExpectedError('(', this.current);
                    this.skipTransform();
                    return;
                }
                this.current = this.reader.read();
                this.skipSpaces();
                final float sk = this.parseFloat();
                this.skipSpaces();
                if (this.current != 41) {
                    this.reportCharacterExpectedError(')', this.current);
                    this.skipTransform();
                    return;
                }
                if (skewX) {
                    this.fragmentIdentifierHandler.skewX(sk);
                }
                else {
                    this.fragmentIdentifierHandler.skewY(sk);
                }
            }
            default: {
                this.reportCharacterExpectedError('X', this.current);
                this.skipTransform();
            }
        }
    }
    
    protected void skipTransform() throws IOException {
    Label_0046:
        do {
            switch (this.current = this.reader.read()) {
                case 41: {
                    break Label_0046;
                }
                default: {
                    continue;
                }
            }
        } while (this.current != -1);
    }
    
    protected void parsePreserveAspectRatio() throws ParseException, IOException {
        this.fragmentIdentifierHandler.startPreserveAspectRatio();
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
                        this.reportCharacterExpectedError('n', this.current);
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
                    this.fragmentIdentifierHandler.none();
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
                                        this.fragmentIdentifierHandler.xMaxYMax();
                                        this.current = this.reader.read();
                                        break;
                                    }
                                    case 105: {
                                        switch (this.current = this.reader.read()) {
                                            case 100: {
                                                this.fragmentIdentifierHandler.xMaxYMid();
                                                this.current = this.reader.read();
                                                break Label_0569;
                                            }
                                            case 110: {
                                                this.fragmentIdentifierHandler.xMaxYMin();
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
                                                this.fragmentIdentifierHandler.xMidYMax();
                                                this.current = this.reader.read();
                                                break;
                                            }
                                            case 105: {
                                                switch (this.current = this.reader.read()) {
                                                    case 100: {
                                                        this.fragmentIdentifierHandler.xMidYMid();
                                                        this.current = this.reader.read();
                                                        break Label_0889;
                                                    }
                                                    case 110: {
                                                        this.fragmentIdentifierHandler.xMidYMin();
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
                                                this.fragmentIdentifierHandler.xMinYMax();
                                                this.current = this.reader.read();
                                                break;
                                            }
                                            case 105: {
                                                switch (this.current = this.reader.read()) {
                                                    case 100: {
                                                        this.fragmentIdentifierHandler.xMinYMid();
                                                        this.current = this.reader.read();
                                                        break Label_1169;
                                                    }
                                                    case 110: {
                                                        this.fragmentIdentifierHandler.xMinYMin();
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
                this.fragmentIdentifierHandler.meet();
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
                this.fragmentIdentifierHandler.slice();
                this.current = this.reader.read();
                break;
            }
        }
        this.fragmentIdentifierHandler.endPreserveAspectRatio();
    }
    
    protected void skipIdentifier() throws IOException {
    Label_0075:
        while (true) {
            switch (this.current = this.reader.read()) {
                case 9:
                case 10:
                case 13:
                case 32: {
                    this.current = this.reader.read();
                }
                case -1: {
                    break Label_0075;
                }
                default: {
                    continue;
                }
            }
        }
    }
}
