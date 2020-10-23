// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

import java.util.StringTokenizer;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SelectorList;
import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.CSSException;
import java.util.Locale;
import org.w3c.css.sac.Parser;

public class ExtendedParserWrapper implements ExtendedParser
{
    public Parser parser;
    
    public static ExtendedParser wrap(final Parser p) {
        if (p instanceof ExtendedParser) {
            return (ExtendedParser)p;
        }
        return new ExtendedParserWrapper(p);
    }
    
    public ExtendedParserWrapper(final Parser parser) {
        this.parser = parser;
    }
    
    @Override
    public String getParserVersion() {
        return this.parser.getParserVersion();
    }
    
    @Override
    public void setLocale(final Locale locale) throws CSSException {
        this.parser.setLocale(locale);
    }
    
    @Override
    public void setDocumentHandler(final DocumentHandler handler) {
        this.parser.setDocumentHandler(handler);
    }
    
    @Override
    public void setSelectorFactory(final SelectorFactory selectorFactory) {
        this.parser.setSelectorFactory(selectorFactory);
    }
    
    @Override
    public void setConditionFactory(final ConditionFactory conditionFactory) {
        this.parser.setConditionFactory(conditionFactory);
    }
    
    @Override
    public void setErrorHandler(final ErrorHandler handler) {
        this.parser.setErrorHandler(handler);
    }
    
    @Override
    public void parseStyleSheet(final InputSource source) throws CSSException, IOException {
        this.parser.parseStyleSheet(source);
    }
    
    @Override
    public void parseStyleSheet(final String uri) throws CSSException, IOException {
        this.parser.parseStyleSheet(uri);
    }
    
    @Override
    public void parseStyleDeclaration(final InputSource source) throws CSSException, IOException {
        this.parser.parseStyleDeclaration(source);
    }
    
    @Override
    public void parseStyleDeclaration(final String source) throws CSSException, IOException {
        this.parser.parseStyleDeclaration(new InputSource(new StringReader(source)));
    }
    
    @Override
    public void parseRule(final InputSource source) throws CSSException, IOException {
        this.parser.parseRule(source);
    }
    
    @Override
    public void parseRule(final String source) throws CSSException, IOException {
        this.parser.parseRule(new InputSource(new StringReader(source)));
    }
    
    @Override
    public SelectorList parseSelectors(final InputSource source) throws CSSException, IOException {
        return this.parser.parseSelectors(source);
    }
    
    @Override
    public SelectorList parseSelectors(final String source) throws CSSException, IOException {
        return this.parser.parseSelectors(new InputSource(new StringReader(source)));
    }
    
    @Override
    public LexicalUnit parsePropertyValue(final InputSource source) throws CSSException, IOException {
        return this.parser.parsePropertyValue(source);
    }
    
    @Override
    public LexicalUnit parsePropertyValue(final String source) throws CSSException, IOException {
        return this.parser.parsePropertyValue(new InputSource(new StringReader(source)));
    }
    
    @Override
    public boolean parsePriority(final InputSource source) throws CSSException, IOException {
        return this.parser.parsePriority(source);
    }
    
    @Override
    public SACMediaList parseMedia(final String mediaText) throws CSSException, IOException {
        final CSSSACMediaList result = new CSSSACMediaList();
        if (!"all".equalsIgnoreCase(mediaText)) {
            final StringTokenizer st = new StringTokenizer(mediaText, " ,");
            while (st.hasMoreTokens()) {
                result.append(st.nextToken());
            }
        }
        return result;
    }
    
    @Override
    public boolean parsePriority(final String source) throws CSSException, IOException {
        return this.parser.parsePriority(new InputSource(new StringReader(source)));
    }
}
