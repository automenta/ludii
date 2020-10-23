// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import org.apache.batik.util.io.StringNormalizingReader;
import java.io.InputStream;
import java.io.IOException;
import org.apache.batik.util.io.StreamNormalizingReader;
import java.io.Reader;
import java.util.MissingResourceException;
import java.util.Locale;
import org.apache.batik.util.io.NormalizingReader;
import org.apache.batik.i18n.LocalizableSupport;

public abstract class AbstractParser implements Parser
{
    public static final String BUNDLE_CLASSNAME = "org.apache.batik.parser.resources.Messages";
    protected ErrorHandler errorHandler;
    protected LocalizableSupport localizableSupport;
    protected NormalizingReader reader;
    protected int current;
    
    public AbstractParser() {
        this.errorHandler = new DefaultErrorHandler();
        this.localizableSupport = new LocalizableSupport("org.apache.batik.parser.resources.Messages", AbstractParser.class.getClassLoader());
    }
    
    public int getCurrent() {
        return this.current;
    }
    
    @Override
    public void setLocale(final Locale l) {
        this.localizableSupport.setLocale(l);
    }
    
    @Override
    public Locale getLocale() {
        return this.localizableSupport.getLocale();
    }
    
    @Override
    public String formatMessage(final String key, final Object[] args) throws MissingResourceException {
        return this.localizableSupport.formatMessage(key, args);
    }
    
    @Override
    public void setErrorHandler(final ErrorHandler handler) {
        this.errorHandler = handler;
    }
    
    @Override
    public void parse(final Reader r) throws ParseException {
        try {
            this.reader = new StreamNormalizingReader(r);
            this.doParse();
        }
        catch (IOException e) {
            this.errorHandler.error(new ParseException(this.createErrorMessage("io.exception", null), e));
        }
    }
    
    public void parse(final InputStream is, final String enc) throws ParseException {
        try {
            this.reader = new StreamNormalizingReader(is, enc);
            this.doParse();
        }
        catch (IOException e) {
            this.errorHandler.error(new ParseException(this.createErrorMessage("io.exception", null), e));
        }
    }
    
    @Override
    public void parse(final String s) throws ParseException {
        try {
            this.reader = new StringNormalizingReader(s);
            this.doParse();
        }
        catch (IOException e) {
            this.errorHandler.error(new ParseException(this.createErrorMessage("io.exception", null), e));
        }
    }
    
    protected abstract void doParse() throws ParseException, IOException;
    
    protected void reportError(final String key, final Object[] args) throws ParseException {
        this.errorHandler.error(new ParseException(this.createErrorMessage(key, args), this.reader.getLine(), this.reader.getColumn()));
    }
    
    protected void reportCharacterExpectedError(final char expectedChar, final int currentChar) {
        this.reportError("character.expected", new Object[] { expectedChar, currentChar });
    }
    
    protected void reportUnexpectedCharacterError(final int currentChar) {
        this.reportError("character.unexpected", new Object[] { currentChar });
    }
    
    protected String createErrorMessage(final String key, final Object[] args) {
        try {
            return this.formatMessage(key, args);
        }
        catch (MissingResourceException e) {
            return key;
        }
    }
    
    protected String getBundleClassName() {
        return "org.apache.batik.parser.resources.Messages";
    }
    
    protected void skipSpaces() throws IOException {
        while (true) {
            switch (this.current) {
                default: {}
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
    
    protected void skipCommaSpaces() throws IOException {
        while (true) {
            switch (this.current) {
                default: {
                    Label_0134: {
                        if (this.current == 44) {
                            while (true) {
                                switch (this.current = this.reader.read()) {
                                    default: {
                                        break Label_0134;
                                    }
                                    case 9:
                                    case 10:
                                    case 13:
                                    case 32: {
                                        continue;
                                    }
                                }
                            }
                        }
                    }
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
