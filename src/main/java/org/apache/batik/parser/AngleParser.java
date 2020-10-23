// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.io.IOException;

public class AngleParser extends NumberParser
{
    protected AngleHandler angleHandler;
    
    public AngleParser() {
        this.angleHandler = DefaultAngleHandler.INSTANCE;
    }
    
    public void setAngleHandler(final AngleHandler handler) {
        this.angleHandler = handler;
    }
    
    public AngleHandler getAngleHandler() {
        return this.angleHandler;
    }
    
    @Override
    protected void doParse() throws ParseException, IOException {
        this.angleHandler.startAngle();
        this.current = this.reader.read();
        this.skipSpaces();
        try {
            final float f = this.parseFloat();
            this.angleHandler.angleValue(f);
            Label_0440: {
                if (this.current != -1) {
                    switch (this.current) {
                        case 9:
                        case 10:
                        case 13:
                        case 32: {
                            break;
                        }
                        default: {
                            switch (this.current) {
                                case 100: {
                                    this.current = this.reader.read();
                                    if (this.current != 101) {
                                        this.reportCharacterExpectedError('e', this.current);
                                        break Label_0440;
                                    }
                                    this.current = this.reader.read();
                                    if (this.current != 103) {
                                        this.reportCharacterExpectedError('g', this.current);
                                        break Label_0440;
                                    }
                                    this.angleHandler.deg();
                                    this.current = this.reader.read();
                                    break Label_0440;
                                }
                                case 103: {
                                    this.current = this.reader.read();
                                    if (this.current != 114) {
                                        this.reportCharacterExpectedError('r', this.current);
                                        break Label_0440;
                                    }
                                    this.current = this.reader.read();
                                    if (this.current != 97) {
                                        this.reportCharacterExpectedError('a', this.current);
                                        break Label_0440;
                                    }
                                    this.current = this.reader.read();
                                    if (this.current != 100) {
                                        this.reportCharacterExpectedError('d', this.current);
                                        break Label_0440;
                                    }
                                    this.angleHandler.grad();
                                    this.current = this.reader.read();
                                    break Label_0440;
                                }
                                case 114: {
                                    this.current = this.reader.read();
                                    if (this.current != 97) {
                                        this.reportCharacterExpectedError('a', this.current);
                                        break Label_0440;
                                    }
                                    this.current = this.reader.read();
                                    if (this.current != 100) {
                                        this.reportCharacterExpectedError('d', this.current);
                                        break Label_0440;
                                    }
                                    this.angleHandler.rad();
                                    this.current = this.reader.read();
                                    break Label_0440;
                                }
                                default: {
                                    this.reportUnexpectedCharacterError(this.current);
                                    break Label_0440;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            this.skipSpaces();
            if (this.current != -1) {
                this.reportError("end.of.stream.expected", new Object[] { this.current });
            }
        }
        catch (NumberFormatException e) {
            this.reportUnexpectedCharacterError(this.current);
        }
        this.angleHandler.endAngle();
    }
}
