// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

import java.util.StringTokenizer;
import java.io.InputStream;
import java.io.Reader;
import org.apache.batik.util.ParsedURL;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.CSSParseException;
import java.io.IOException;
import org.w3c.css.sac.InputSource;
import java.util.MissingResourceException;
import org.w3c.css.sac.CSSException;
import java.util.Locale;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.DocumentHandler;
import org.apache.batik.i18n.LocalizableSupport;
import org.apache.batik.i18n.Localizable;

public class Parser implements ExtendedParser, Localizable
{
    public static final String BUNDLE_CLASSNAME = "org.apache.batik.css.parser.resources.Messages";
    protected LocalizableSupport localizableSupport;
    protected Scanner scanner;
    protected int current;
    protected DocumentHandler documentHandler;
    protected SelectorFactory selectorFactory;
    protected ConditionFactory conditionFactory;
    protected ErrorHandler errorHandler;
    protected String pseudoElement;
    protected String documentURI;
    
    public Parser() {
        this.localizableSupport = new LocalizableSupport("org.apache.batik.css.parser.resources.Messages", Parser.class.getClassLoader());
        this.documentHandler = DefaultDocumentHandler.INSTANCE;
        this.selectorFactory = DefaultSelectorFactory.INSTANCE;
        this.conditionFactory = DefaultConditionFactory.INSTANCE;
        this.errorHandler = DefaultErrorHandler.INSTANCE;
    }
    
    @Override
    public String getParserVersion() {
        return "http://www.w3.org/TR/REC-CSS2";
    }
    
    @Override
    public void setLocale(final Locale locale) throws CSSException {
        this.localizableSupport.setLocale(locale);
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
    public void setDocumentHandler(final DocumentHandler handler) {
        this.documentHandler = handler;
    }
    
    @Override
    public void setSelectorFactory(final SelectorFactory factory) {
        this.selectorFactory = factory;
    }
    
    @Override
    public void setConditionFactory(final ConditionFactory factory) {
        this.conditionFactory = factory;
    }
    
    @Override
    public void setErrorHandler(final ErrorHandler handler) {
        this.errorHandler = handler;
    }
    
    @Override
    public void parseStyleSheet(final InputSource source) throws CSSException, IOException {
        this.scanner = this.createScanner(source);
        try {
            this.documentHandler.startDocument(source);
            switch (this.current = this.scanner.next()) {
                case 30: {
                    if (this.nextIgnoreSpaces() != 19) {
                        this.reportError("charset.string");
                        break;
                    }
                    if (this.nextIgnoreSpaces() != 8) {
                        this.reportError("semicolon");
                    }
                    this.next();
                    break;
                }
                case 18: {
                    this.documentHandler.comment(this.scanner.getStringValue());
                    break;
                }
            }
            this.skipSpacesAndCDOCDC();
            while (this.current == 28) {
                this.nextIgnoreSpaces();
                this.parseImportRule();
                this.nextIgnoreSpaces();
            }
        Label_0252:
            while (true) {
                switch (this.current) {
                    case 33: {
                        this.nextIgnoreSpaces();
                        this.parsePageRule();
                        break;
                    }
                    case 32: {
                        this.nextIgnoreSpaces();
                        this.parseMediaRule();
                        break;
                    }
                    case 31: {
                        this.nextIgnoreSpaces();
                        this.parseFontFaceRule();
                        break;
                    }
                    case 29: {
                        this.nextIgnoreSpaces();
                        this.parseAtRule();
                        break;
                    }
                    case 0: {
                        break Label_0252;
                    }
                    default: {
                        this.parseRuleSet();
                        break;
                    }
                }
                this.skipSpacesAndCDOCDC();
            }
        }
        finally {
            this.documentHandler.endDocument(source);
            this.scanner.close();
            this.scanner = null;
        }
    }
    
    @Override
    public void parseStyleSheet(final String uri) throws CSSException, IOException {
        this.parseStyleSheet(new InputSource(uri));
    }
    
    @Override
    public void parseStyleDeclaration(final InputSource source) throws CSSException, IOException {
        this.scanner = this.createScanner(source);
        this.parseStyleDeclarationInternal();
    }
    
    protected void parseStyleDeclarationInternal() throws CSSException, IOException {
        this.nextIgnoreSpaces();
        try {
            this.parseStyleDeclaration(false);
        }
        catch (CSSParseException e) {
            this.reportError(e);
        }
        finally {
            this.scanner.close();
            this.scanner = null;
        }
    }
    
    @Override
    public void parseRule(final InputSource source) throws CSSException, IOException {
        this.scanner = this.createScanner(source);
        this.parseRuleInternal();
    }
    
    protected void parseRuleInternal() throws CSSException, IOException {
        this.nextIgnoreSpaces();
        this.parseRule();
        this.scanner.close();
        this.scanner = null;
    }
    
    @Override
    public SelectorList parseSelectors(final InputSource source) throws CSSException, IOException {
        this.scanner = this.createScanner(source);
        return this.parseSelectorsInternal();
    }
    
    protected SelectorList parseSelectorsInternal() throws CSSException, IOException {
        this.nextIgnoreSpaces();
        final SelectorList ret = this.parseSelectorList();
        this.scanner.close();
        this.scanner = null;
        return ret;
    }
    
    @Override
    public LexicalUnit parsePropertyValue(final InputSource source) throws CSSException, IOException {
        this.scanner = this.createScanner(source);
        return this.parsePropertyValueInternal();
    }
    
    protected LexicalUnit parsePropertyValueInternal() throws CSSException, IOException {
        this.nextIgnoreSpaces();
        LexicalUnit exp = null;
        try {
            exp = this.parseExpression(false);
        }
        catch (CSSParseException e) {
            this.reportError(e);
            throw e;
        }
        CSSParseException exception = null;
        if (this.current != 0) {
            exception = this.createCSSParseException("eof.expected");
        }
        this.scanner.close();
        this.scanner = null;
        if (exception != null) {
            this.errorHandler.fatalError(exception);
        }
        return exp;
    }
    
    @Override
    public boolean parsePriority(final InputSource source) throws CSSException, IOException {
        this.scanner = this.createScanner(source);
        return this.parsePriorityInternal();
    }
    
    protected boolean parsePriorityInternal() throws CSSException, IOException {
        this.nextIgnoreSpaces();
        this.scanner.close();
        this.scanner = null;
        switch (this.current) {
            case 0: {
                return false;
            }
            case 28: {
                return true;
            }
            default: {
                this.reportError("token", new Object[] { this.current });
                return false;
            }
        }
    }
    
    protected void parseRule() {
        switch (this.scanner.getType()) {
            case 28: {
                this.nextIgnoreSpaces();
                this.parseImportRule();
                break;
            }
            case 29: {
                this.nextIgnoreSpaces();
                this.parseAtRule();
                break;
            }
            case 31: {
                this.nextIgnoreSpaces();
                this.parseFontFaceRule();
                break;
            }
            case 32: {
                this.nextIgnoreSpaces();
                this.parseMediaRule();
                break;
            }
            case 33: {
                this.nextIgnoreSpaces();
                this.parsePageRule();
                break;
            }
            default: {
                this.parseRuleSet();
                break;
            }
        }
    }
    
    protected void parseAtRule() {
        this.scanner.scanAtRule();
        this.documentHandler.ignorableAtRule(this.scanner.getStringValue());
        this.nextIgnoreSpaces();
    }
    
    protected void parseImportRule() {
        String uri = null;
        switch (this.current) {
            default: {
                this.reportError("string.or.uri");
            }
            case 19:
            case 51: {
                uri = this.scanner.getStringValue();
                this.nextIgnoreSpaces();
                CSSSACMediaList ml;
                if (this.current != 20) {
                    ml = new CSSSACMediaList();
                    ml.append("all");
                }
                else {
                    ml = this.parseMediaList();
                }
                this.documentHandler.importStyle(uri, ml, null);
                if (this.current != 8) {
                    this.reportError("semicolon");
                }
                else {
                    this.next();
                }
            }
        }
    }
    
    protected CSSSACMediaList parseMediaList() {
        final CSSSACMediaList result = new CSSSACMediaList();
        result.append(this.scanner.getStringValue());
        this.nextIgnoreSpaces();
        while (this.current == 6) {
            this.nextIgnoreSpaces();
            switch (this.current) {
                default: {
                    this.reportError("identifier");
                    continue;
                }
                case 20: {
                    result.append(this.scanner.getStringValue());
                    this.nextIgnoreSpaces();
                    continue;
                }
            }
        }
        return result;
    }
    
    protected void parseFontFaceRule() {
        try {
            this.documentHandler.startFontFace();
            if (this.current != 1) {
                this.reportError("left.curly.brace");
            }
            else {
                this.nextIgnoreSpaces();
                try {
                    this.parseStyleDeclaration(true);
                }
                catch (CSSParseException e) {
                    this.reportError(e);
                }
            }
        }
        finally {
            this.documentHandler.endFontFace();
        }
    }
    
    protected void parsePageRule() {
        String page = null;
        String ppage = null;
        if (this.current == 20) {
            page = this.scanner.getStringValue();
            this.nextIgnoreSpaces();
            if (this.current == 16) {
                this.nextIgnoreSpaces();
                if (this.current != 20) {
                    this.reportError("identifier");
                    return;
                }
                ppage = this.scanner.getStringValue();
                this.nextIgnoreSpaces();
            }
        }
        try {
            this.documentHandler.startPage(page, ppage);
            if (this.current != 1) {
                this.reportError("left.curly.brace");
            }
            else {
                this.nextIgnoreSpaces();
                try {
                    this.parseStyleDeclaration(true);
                }
                catch (CSSParseException e) {
                    this.reportError(e);
                }
            }
        }
        finally {
            this.documentHandler.endPage(page, ppage);
        }
    }
    
    protected void parseMediaRule() {
        if (this.current != 20) {
            this.reportError("identifier");
            return;
        }
        final CSSSACMediaList ml = this.parseMediaList();
        try {
            this.documentHandler.startMedia(ml);
            if (this.current != 1) {
                this.reportError("left.curly.brace");
            }
            else {
                this.nextIgnoreSpaces();
            Label_0084:
                while (true) {
                    switch (this.current) {
                        case 0:
                        case 2: {
                            break Label_0084;
                        }
                        default: {
                            this.parseRuleSet();
                            continue;
                        }
                    }
                }
                this.nextIgnoreSpaces();
            }
        }
        finally {
            this.documentHandler.endMedia(ml);
        }
    }
    
    protected void parseRuleSet() {
        SelectorList sl = null;
        try {
            sl = this.parseSelectorList();
        }
        catch (CSSParseException e) {
            this.reportError(e);
            return;
        }
        try {
            this.documentHandler.startSelector(sl);
            if (this.current != 1) {
                this.reportError("left.curly.brace");
                if (this.current == 2) {
                    this.nextIgnoreSpaces();
                }
            }
            else {
                this.nextIgnoreSpaces();
                try {
                    this.parseStyleDeclaration(true);
                }
                catch (CSSParseException e) {
                    this.reportError(e);
                }
            }
        }
        finally {
            this.documentHandler.endSelector(sl);
        }
    }
    
    protected SelectorList parseSelectorList() {
        final CSSSelectorList result = new CSSSelectorList();
        result.append(this.parseSelector());
        while (this.current == 6) {
            this.nextIgnoreSpaces();
            result.append(this.parseSelector());
        }
        return result;
    }
    
    protected Selector parseSelector() {
        this.pseudoElement = null;
        Selector result = this.parseSimpleSelector();
        while (true) {
            switch (this.current) {
                default: {
                    if (this.pseudoElement != null) {
                        result = this.selectorFactory.createChildSelector(result, this.selectorFactory.createPseudoElementSelector(null, this.pseudoElement));
                    }
                    return result;
                }
                case 7:
                case 11:
                case 13:
                case 16:
                case 20:
                case 27: {
                    if (this.pseudoElement != null) {
                        throw this.createCSSParseException("pseudo.element.position");
                    }
                    result = this.selectorFactory.createDescendantSelector(result, this.parseSimpleSelector());
                    continue;
                }
                case 4: {
                    if (this.pseudoElement != null) {
                        throw this.createCSSParseException("pseudo.element.position");
                    }
                    this.nextIgnoreSpaces();
                    result = this.selectorFactory.createDirectAdjacentSelector((short)1, result, this.parseSimpleSelector());
                    continue;
                }
                case 9: {
                    if (this.pseudoElement != null) {
                        throw this.createCSSParseException("pseudo.element.position");
                    }
                    this.nextIgnoreSpaces();
                    result = this.selectorFactory.createChildSelector(result, this.parseSimpleSelector());
                    continue;
                }
            }
        }
    }
    
    protected SimpleSelector parseSimpleSelector() {
        SimpleSelector result = null;
        Label_0075: {
            switch (this.current) {
                case 20: {
                    result = this.selectorFactory.createElementSelector(null, this.scanner.getStringValue());
                    this.next();
                    break Label_0075;
                }
                case 13: {
                    this.next();
                    break;
                }
            }
            result = this.selectorFactory.createElementSelector(null, null);
        }
        Condition cond = null;
        while (true) {
            Condition c = null;
            switch (this.current) {
                case 27: {
                    c = this.conditionFactory.createIdCondition(this.scanner.getStringValue());
                    this.next();
                    break;
                }
                case 7: {
                    if (this.next() != 20) {
                        throw this.createCSSParseException("identifier");
                    }
                    c = this.conditionFactory.createClassCondition(null, this.scanner.getStringValue());
                    this.next();
                    break;
                }
                case 11: {
                    if (this.nextIgnoreSpaces() != 20) {
                        throw this.createCSSParseException("identifier");
                    }
                    final String name = this.scanner.getStringValue();
                    final int op = this.nextIgnoreSpaces();
                    Label_0458: {
                        switch (op) {
                            default: {
                                throw this.createCSSParseException("right.bracket");
                            }
                            case 12: {
                                this.next();
                                c = this.conditionFactory.createAttributeCondition(name, null, false, null);
                                break;
                            }
                            case 3:
                            case 25:
                            case 26: {
                                String val = null;
                                switch (this.nextIgnoreSpaces()) {
                                    default: {
                                        throw this.createCSSParseException("identifier.or.string");
                                    }
                                    case 19:
                                    case 20: {
                                        val = this.scanner.getStringValue();
                                        this.nextIgnoreSpaces();
                                        if (this.current != 12) {
                                            throw this.createCSSParseException("right.bracket");
                                        }
                                        this.next();
                                        switch (op) {
                                            case 3: {
                                                c = this.conditionFactory.createAttributeCondition(name, null, false, val);
                                                break Label_0458;
                                            }
                                            case 26: {
                                                c = this.conditionFactory.createOneOfAttributeCondition(name, null, false, val);
                                                break Label_0458;
                                            }
                                            default: {
                                                c = this.conditionFactory.createBeginHyphenAttributeCondition(name, null, false, val);
                                                break Label_0458;
                                            }
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                case 16: {
                    switch (this.nextIgnoreSpaces()) {
                        case 20: {
                            final String val = this.scanner.getStringValue();
                            if (this.isPseudoElement(val)) {
                                if (this.pseudoElement != null) {
                                    throw this.createCSSParseException("duplicate.pseudo.element");
                                }
                                this.pseudoElement = val;
                            }
                            else {
                                c = this.conditionFactory.createPseudoClassCondition(null, val);
                            }
                            this.next();
                            break;
                        }
                        case 52: {
                            final String func = this.scanner.getStringValue();
                            if (this.nextIgnoreSpaces() != 20) {
                                throw this.createCSSParseException("identifier");
                            }
                            final String lang = this.scanner.getStringValue();
                            if (this.nextIgnoreSpaces() != 15) {
                                throw this.createCSSParseException("right.brace");
                            }
                            if (!func.equalsIgnoreCase("lang")) {
                                throw this.createCSSParseException("pseudo.function");
                            }
                            c = this.conditionFactory.createLangCondition(lang);
                            this.next();
                            break;
                        }
                        default: {
                            throw this.createCSSParseException("identifier");
                        }
                    }
                    break;
                }
                default: {
                    this.skipSpaces();
                    if (cond != null) {
                        result = this.selectorFactory.createConditionalSelector(result, cond);
                    }
                    return result;
                }
            }
            if (c != null) {
                if (cond == null) {
                    cond = c;
                }
                else {
                    cond = this.conditionFactory.createAndCondition(cond, c);
                }
            }
        }
    }
    
    protected boolean isPseudoElement(final String s) {
        switch (s.charAt(0)) {
            case 'A':
            case 'a': {
                return s.equalsIgnoreCase("after");
            }
            case 'B':
            case 'b': {
                return s.equalsIgnoreCase("before");
            }
            case 'F':
            case 'f': {
                return s.equalsIgnoreCase("first-letter") || s.equalsIgnoreCase("first-line");
            }
            default: {
                return false;
            }
        }
    }
    
    protected void parseStyleDeclaration(final boolean inSheet) throws CSSException {
        while (true) {
            switch (this.current) {
                case 0: {
                    if (inSheet) {
                        throw this.createCSSParseException("eof");
                    }
                }
                case 2: {
                    if (!inSheet) {
                        throw this.createCSSParseException("eof.expected");
                    }
                    this.nextIgnoreSpaces();
                }
                case 8: {
                    this.nextIgnoreSpaces();
                    continue;
                }
                default: {
                    throw this.createCSSParseException("identifier");
                }
                case 20: {
                    final String name = this.scanner.getStringValue();
                    if (this.nextIgnoreSpaces() != 16) {
                        throw this.createCSSParseException("colon");
                    }
                    this.nextIgnoreSpaces();
                    LexicalUnit exp = null;
                    try {
                        exp = this.parseExpression(false);
                    }
                    catch (CSSParseException e) {
                        this.reportError(e);
                    }
                    if (exp == null) {
                        continue;
                    }
                    boolean important = false;
                    if (this.current == 23) {
                        important = true;
                        this.nextIgnoreSpaces();
                    }
                    this.documentHandler.property(name, exp, important);
                    continue;
                }
            }
        }
    }
    
    protected LexicalUnit parseExpression(final boolean param) {
        LexicalUnit curr;
        final LexicalUnit result = curr = this.parseTerm(null);
        while (true) {
            boolean op = false;
            switch (this.current) {
                case 6: {
                    op = true;
                    curr = CSSLexicalUnit.createSimple((short)0, curr);
                    this.nextIgnoreSpaces();
                    break;
                }
                case 10: {
                    op = true;
                    curr = CSSLexicalUnit.createSimple((short)4, curr);
                    this.nextIgnoreSpaces();
                    break;
                }
            }
            if (param) {
                if (this.current == 15) {
                    if (op) {
                        throw this.createCSSParseException("token", new Object[] { this.current });
                    }
                    return result;
                }
                else {
                    curr = this.parseTerm(curr);
                }
            }
            else {
                switch (this.current) {
                    case 0:
                    case 2:
                    case 8:
                    case 23: {
                        if (op) {
                            throw this.createCSSParseException("token", new Object[] { this.current });
                        }
                        return result;
                    }
                    default: {
                        curr = this.parseTerm(curr);
                        continue;
                    }
                }
            }
        }
    }
    
    protected LexicalUnit parseTerm(final LexicalUnit prev) {
        boolean plus = true;
        boolean sgn = false;
        switch (this.current) {
            case 5: {
                plus = false;
            }
            case 4: {
                this.next();
                sgn = true;
                break;
            }
        }
        switch (this.current) {
            case 24: {
                String sval = this.scanner.getStringValue();
                if (!plus) {
                    sval = "-" + sval;
                }
                final long lVal = Long.parseLong(sval);
                if (lVal >= -2147483648L && lVal <= 2147483647L) {
                    final int iVal = (int)lVal;
                    this.nextIgnoreSpaces();
                    return CSSLexicalUnit.createInteger(iVal, prev);
                }
                return CSSLexicalUnit.createFloat((short)14, this.number(plus), prev);
            }
            case 54: {
                return CSSLexicalUnit.createFloat((short)14, this.number(plus), prev);
            }
            case 42: {
                return CSSLexicalUnit.createFloat((short)23, this.number(plus), prev);
            }
            case 45: {
                return CSSLexicalUnit.createFloat((short)21, this.number(plus), prev);
            }
            case 44: {
                return CSSLexicalUnit.createFloat((short)22, this.number(plus), prev);
            }
            case 46: {
                return CSSLexicalUnit.createFloat((short)17, this.number(plus), prev);
            }
            case 37: {
                return CSSLexicalUnit.createFloat((short)19, this.number(plus), prev);
            }
            case 38: {
                return CSSLexicalUnit.createFloat((short)20, this.number(plus), prev);
            }
            case 39: {
                return CSSLexicalUnit.createFloat((short)18, this.number(plus), prev);
            }
            case 36: {
                return CSSLexicalUnit.createFloat((short)15, this.number(plus), prev);
            }
            case 35: {
                return CSSLexicalUnit.createFloat((short)16, this.number(plus), prev);
            }
            case 47: {
                return CSSLexicalUnit.createFloat((short)28, this.number(plus), prev);
            }
            case 48: {
                return CSSLexicalUnit.createFloat((short)30, this.number(plus), prev);
            }
            case 49: {
                return CSSLexicalUnit.createFloat((short)29, this.number(plus), prev);
            }
            case 43: {
                return CSSLexicalUnit.createFloat((short)32, this.number(plus), prev);
            }
            case 40: {
                return CSSLexicalUnit.createFloat((short)31, this.number(plus), prev);
            }
            case 41: {
                return CSSLexicalUnit.createFloat((short)33, this.number(plus), prev);
            }
            case 50: {
                return CSSLexicalUnit.createFloat((short)34, this.number(plus), prev);
            }
            case 34: {
                return this.dimension(plus, prev);
            }
            case 52: {
                return this.parseFunction(plus, prev);
            }
            default: {
                if (sgn) {
                    throw this.createCSSParseException("token", new Object[] { this.current });
                }
                switch (this.current) {
                    case 19: {
                        final String val = this.scanner.getStringValue();
                        this.nextIgnoreSpaces();
                        return CSSLexicalUnit.createString((short)36, val, prev);
                    }
                    case 20: {
                        final String val = this.scanner.getStringValue();
                        this.nextIgnoreSpaces();
                        if (val.equalsIgnoreCase("inherit")) {
                            return CSSLexicalUnit.createSimple((short)12, prev);
                        }
                        return CSSLexicalUnit.createString((short)35, val, prev);
                    }
                    case 51: {
                        final String val = this.scanner.getStringValue();
                        this.nextIgnoreSpaces();
                        return CSSLexicalUnit.createString((short)24, val, prev);
                    }
                    case 27: {
                        return this.hexcolor(prev);
                    }
                    default: {
                        throw this.createCSSParseException("token", new Object[] { this.current });
                    }
                }
                break;
            }
        }
    }
    
    protected LexicalUnit parseFunction(final boolean positive, final LexicalUnit prev) {
        final String name = this.scanner.getStringValue();
        this.nextIgnoreSpaces();
        final LexicalUnit params = this.parseExpression(true);
        if (this.current != 15) {
            throw this.createCSSParseException("token", new Object[] { this.current });
        }
        this.nextIgnoreSpaces();
        Label_1810: {
            switch (name.charAt(0)) {
                case 'R':
                case 'r': {
                    if (name.equalsIgnoreCase("rgb")) {
                        LexicalUnit lu = params;
                        if (lu == null) {
                            break;
                        }
                        switch (lu.getLexicalUnitType()) {
                            default: {
                                break Label_1810;
                            }
                            case 13:
                            case 23: {
                                lu = lu.getNextLexicalUnit();
                                if (lu == null) {
                                    break Label_1810;
                                }
                                switch (lu.getLexicalUnitType()) {
                                    default: {
                                        break Label_1810;
                                    }
                                    case 0: {
                                        lu = lu.getNextLexicalUnit();
                                        if (lu == null) {
                                            break Label_1810;
                                        }
                                        switch (lu.getLexicalUnitType()) {
                                            default: {
                                                break Label_1810;
                                            }
                                            case 13:
                                            case 23: {
                                                lu = lu.getNextLexicalUnit();
                                                if (lu == null) {
                                                    break Label_1810;
                                                }
                                                switch (lu.getLexicalUnitType()) {
                                                    default: {
                                                        break Label_1810;
                                                    }
                                                    case 0: {
                                                        lu = lu.getNextLexicalUnit();
                                                        if (lu == null) {
                                                            break Label_1810;
                                                        }
                                                        switch (lu.getLexicalUnitType()) {
                                                            default: {
                                                                break Label_1810;
                                                            }
                                                            case 13:
                                                            case 23: {
                                                                lu = lu.getNextLexicalUnit();
                                                                if (lu != null) {
                                                                    break Label_1810;
                                                                }
                                                                return CSSLexicalUnit.createPredefinedFunction((short)27, params, prev);
                                                            }
                                                        }
                                                        break;
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    else {
                        if (!name.equalsIgnoreCase("rect")) {
                            break;
                        }
                        LexicalUnit lu = params;
                        if (lu == null) {
                            break;
                        }
                        switch (lu.getLexicalUnitType()) {
                            default: {
                                break Label_1810;
                            }
                            case 13: {
                                if (lu.getIntegerValue() != 0) {
                                    break Label_1810;
                                }
                                lu = lu.getNextLexicalUnit();
                                break;
                            }
                            case 35: {
                                if (!lu.getStringValue().equalsIgnoreCase("auto")) {
                                    break Label_1810;
                                }
                                lu = lu.getNextLexicalUnit();
                                break;
                            }
                            case 15:
                            case 16:
                            case 17:
                            case 18:
                            case 19:
                            case 20:
                            case 21:
                            case 22:
                            case 23: {
                                lu = lu.getNextLexicalUnit();
                                break;
                            }
                        }
                        if (lu == null) {
                            break;
                        }
                        switch (lu.getLexicalUnitType()) {
                            default: {
                                break Label_1810;
                            }
                            case 0: {
                                lu = lu.getNextLexicalUnit();
                                if (lu == null) {
                                    break Label_1810;
                                }
                                switch (lu.getLexicalUnitType()) {
                                    default: {
                                        break Label_1810;
                                    }
                                    case 13: {
                                        if (lu.getIntegerValue() != 0) {
                                            break Label_1810;
                                        }
                                        lu = lu.getNextLexicalUnit();
                                        break;
                                    }
                                    case 35: {
                                        if (!lu.getStringValue().equalsIgnoreCase("auto")) {
                                            break Label_1810;
                                        }
                                        lu = lu.getNextLexicalUnit();
                                        break;
                                    }
                                    case 15:
                                    case 16:
                                    case 17:
                                    case 18:
                                    case 19:
                                    case 20:
                                    case 21:
                                    case 22:
                                    case 23: {
                                        lu = lu.getNextLexicalUnit();
                                        break;
                                    }
                                }
                                if (lu == null) {
                                    break Label_1810;
                                }
                                switch (lu.getLexicalUnitType()) {
                                    default: {
                                        break Label_1810;
                                    }
                                    case 0: {
                                        lu = lu.getNextLexicalUnit();
                                        if (lu == null) {
                                            break Label_1810;
                                        }
                                        switch (lu.getLexicalUnitType()) {
                                            default: {
                                                break Label_1810;
                                            }
                                            case 13: {
                                                if (lu.getIntegerValue() != 0) {
                                                    break Label_1810;
                                                }
                                                lu = lu.getNextLexicalUnit();
                                                break;
                                            }
                                            case 35: {
                                                if (!lu.getStringValue().equalsIgnoreCase("auto")) {
                                                    break Label_1810;
                                                }
                                                lu = lu.getNextLexicalUnit();
                                                break;
                                            }
                                            case 15:
                                            case 16:
                                            case 17:
                                            case 18:
                                            case 19:
                                            case 20:
                                            case 21:
                                            case 22:
                                            case 23: {
                                                lu = lu.getNextLexicalUnit();
                                                break;
                                            }
                                        }
                                        if (lu == null) {
                                            break Label_1810;
                                        }
                                        switch (lu.getLexicalUnitType()) {
                                            default: {
                                                break Label_1810;
                                            }
                                            case 0: {
                                                lu = lu.getNextLexicalUnit();
                                                if (lu == null) {
                                                    break Label_1810;
                                                }
                                                switch (lu.getLexicalUnitType()) {
                                                    default: {
                                                        break Label_1810;
                                                    }
                                                    case 13: {
                                                        if (lu.getIntegerValue() != 0) {
                                                            break Label_1810;
                                                        }
                                                        lu = lu.getNextLexicalUnit();
                                                        break;
                                                    }
                                                    case 35: {
                                                        if (!lu.getStringValue().equalsIgnoreCase("auto")) {
                                                            break Label_1810;
                                                        }
                                                        lu = lu.getNextLexicalUnit();
                                                        break;
                                                    }
                                                    case 15:
                                                    case 16:
                                                    case 17:
                                                    case 18:
                                                    case 19:
                                                    case 20:
                                                    case 21:
                                                    case 22:
                                                    case 23: {
                                                        lu = lu.getNextLexicalUnit();
                                                        break;
                                                    }
                                                }
                                                if (lu != null) {
                                                    break Label_1810;
                                                }
                                                return CSSLexicalUnit.createPredefinedFunction((short)38, params, prev);
                                            }
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                case 'C':
                case 'c': {
                    if (name.equalsIgnoreCase("counter")) {
                        LexicalUnit lu = params;
                        if (lu == null) {
                            break;
                        }
                        switch (lu.getLexicalUnitType()) {
                            default: {
                                break Label_1810;
                            }
                            case 35: {
                                lu = lu.getNextLexicalUnit();
                                if (lu == null) {
                                    break Label_1810;
                                }
                                switch (lu.getLexicalUnitType()) {
                                    default: {
                                        break Label_1810;
                                    }
                                    case 0: {
                                        lu = lu.getNextLexicalUnit();
                                        if (lu == null) {
                                            break Label_1810;
                                        }
                                        switch (lu.getLexicalUnitType()) {
                                            default: {
                                                break Label_1810;
                                            }
                                            case 35: {
                                                lu = lu.getNextLexicalUnit();
                                                if (lu != null) {
                                                    break Label_1810;
                                                }
                                                return CSSLexicalUnit.createPredefinedFunction((short)25, params, prev);
                                            }
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    else {
                        if (!name.equalsIgnoreCase("counters")) {
                            break;
                        }
                        LexicalUnit lu = params;
                        if (lu == null) {
                            break;
                        }
                        switch (lu.getLexicalUnitType()) {
                            default: {
                                break Label_1810;
                            }
                            case 35: {
                                lu = lu.getNextLexicalUnit();
                                if (lu == null) {
                                    break Label_1810;
                                }
                                switch (lu.getLexicalUnitType()) {
                                    default: {
                                        break Label_1810;
                                    }
                                    case 0: {
                                        lu = lu.getNextLexicalUnit();
                                        if (lu == null) {
                                            break Label_1810;
                                        }
                                        switch (lu.getLexicalUnitType()) {
                                            default: {
                                                break Label_1810;
                                            }
                                            case 36: {
                                                lu = lu.getNextLexicalUnit();
                                                if (lu == null) {
                                                    break Label_1810;
                                                }
                                                switch (lu.getLexicalUnitType()) {
                                                    default: {
                                                        break Label_1810;
                                                    }
                                                    case 0: {
                                                        lu = lu.getNextLexicalUnit();
                                                        if (lu == null) {
                                                            break Label_1810;
                                                        }
                                                        switch (lu.getLexicalUnitType()) {
                                                            default: {
                                                                break Label_1810;
                                                            }
                                                            case 35: {
                                                                lu = lu.getNextLexicalUnit();
                                                                if (lu != null) {
                                                                    break Label_1810;
                                                                }
                                                                return CSSLexicalUnit.createPredefinedFunction((short)26, params, prev);
                                                            }
                                                        }
                                                        break;
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                case 'A':
                case 'a': {
                    if (!name.equalsIgnoreCase("attr")) {
                        break;
                    }
                    LexicalUnit lu = params;
                    if (lu == null) {
                        break;
                    }
                    switch (lu.getLexicalUnitType()) {
                        default: {
                            break Label_1810;
                        }
                        case 35: {
                            lu = lu.getNextLexicalUnit();
                            if (lu != null) {
                                break Label_1810;
                            }
                            return CSSLexicalUnit.createString((short)37, params.getStringValue(), prev);
                        }
                    }
                    break;
                }
            }
        }
        return CSSLexicalUnit.createFunction(name, params, prev);
    }
    
    protected LexicalUnit hexcolor(final LexicalUnit prev) {
        final String val = this.scanner.getStringValue();
        final int len = val.length();
        LexicalUnit params = null;
        switch (len) {
            case 3: {
                final char rc = Character.toLowerCase(val.charAt(0));
                final char gc = Character.toLowerCase(val.charAt(1));
                final char bc = Character.toLowerCase(val.charAt(2));
                if (!ScannerUtilities.isCSSHexadecimalCharacter(rc) || !ScannerUtilities.isCSSHexadecimalCharacter(gc) || !ScannerUtilities.isCSSHexadecimalCharacter(bc)) {
                    throw this.createCSSParseException("rgb.color", new Object[] { val });
                }
                int r;
                int t = r = ((rc >= '0' && rc <= '9') ? (rc - '0') : (rc - 'a' + 10));
                t <<= 4;
                r |= t;
                int g;
                t = (g = ((gc >= '0' && gc <= '9') ? (gc - '0') : (gc - 'a' + 10)));
                t <<= 4;
                g |= t;
                int b;
                t = (b = ((bc >= '0' && bc <= '9') ? (bc - '0') : (bc - 'a' + 10)));
                t <<= 4;
                b |= t;
                params = CSSLexicalUnit.createInteger(r, null);
                LexicalUnit tmp = CSSLexicalUnit.createSimple((short)0, params);
                tmp = CSSLexicalUnit.createInteger(g, tmp);
                tmp = CSSLexicalUnit.createSimple((short)0, tmp);
                tmp = CSSLexicalUnit.createInteger(b, tmp);
                break;
            }
            case 6: {
                final char rc2 = Character.toLowerCase(val.charAt(0));
                final char rc3 = Character.toLowerCase(val.charAt(1));
                final char gc2 = Character.toLowerCase(val.charAt(2));
                final char gc3 = Character.toLowerCase(val.charAt(3));
                final char bc2 = Character.toLowerCase(val.charAt(4));
                final char bc3 = Character.toLowerCase(val.charAt(5));
                if (!ScannerUtilities.isCSSHexadecimalCharacter(rc2) || !ScannerUtilities.isCSSHexadecimalCharacter(rc3) || !ScannerUtilities.isCSSHexadecimalCharacter(gc2) || !ScannerUtilities.isCSSHexadecimalCharacter(gc3) || !ScannerUtilities.isCSSHexadecimalCharacter(bc2) || !ScannerUtilities.isCSSHexadecimalCharacter(bc3)) {
                    throw this.createCSSParseException("rgb.color");
                }
                int r = (rc2 >= '0' && rc2 <= '9') ? (rc2 - '0') : (rc2 - 'a' + 10);
                r <<= 4;
                r |= ((rc3 >= '0' && rc3 <= '9') ? (rc3 - '0') : (rc3 - 'a' + 10));
                int g = (gc2 >= '0' && gc2 <= '9') ? (gc2 - '0') : (gc2 - 'a' + 10);
                g <<= 4;
                g |= ((gc3 >= '0' && gc3 <= '9') ? (gc3 - '0') : (gc3 - 'a' + 10));
                int b = (bc2 >= '0' && bc2 <= '9') ? (bc2 - '0') : (bc2 - 'a' + 10);
                b <<= 4;
                b |= ((bc3 >= '0' && bc3 <= '9') ? (bc3 - '0') : (bc3 - 'a' + 10));
                params = CSSLexicalUnit.createInteger(r, null);
                LexicalUnit tmp = CSSLexicalUnit.createSimple((short)0, params);
                tmp = CSSLexicalUnit.createInteger(g, tmp);
                tmp = CSSLexicalUnit.createSimple((short)0, tmp);
                tmp = CSSLexicalUnit.createInteger(b, tmp);
                break;
            }
            default: {
                throw this.createCSSParseException("rgb.color", new Object[] { val });
            }
        }
        this.nextIgnoreSpaces();
        return CSSLexicalUnit.createPredefinedFunction((short)27, params, prev);
    }
    
    protected Scanner createScanner(final InputSource source) {
        this.documentURI = source.getURI();
        if (this.documentURI == null) {
            this.documentURI = "";
        }
        final Reader r = source.getCharacterStream();
        if (r != null) {
            return new Scanner(r);
        }
        InputStream is = source.getByteStream();
        if (is != null) {
            return new Scanner(is, source.getEncoding());
        }
        final String uri = source.getURI();
        if (uri == null) {
            throw new CSSException(this.formatMessage("empty.source", null));
        }
        try {
            final ParsedURL purl = new ParsedURL(uri);
            is = purl.openStreamRaw("text/css");
            return new Scanner(is, source.getEncoding());
        }
        catch (IOException e) {
            throw new CSSException(e);
        }
    }
    
    protected int skipSpaces() {
        int lex;
        for (lex = this.scanner.getType(); lex == 17; lex = this.next()) {}
        return lex;
    }
    
    protected int skipSpacesAndCDOCDC() {
        while (true) {
            switch (this.current) {
                default: {
                    return this.current;
                }
                case 17:
                case 18:
                case 21:
                case 22: {
                    this.scanner.clearBuffer();
                    this.next();
                    continue;
                }
            }
        }
    }
    
    protected float number(final boolean positive) {
        try {
            final float sgn = positive ? 1.0f : -1.0f;
            final String val = this.scanner.getStringValue();
            this.nextIgnoreSpaces();
            return sgn * Float.parseFloat(val);
        }
        catch (NumberFormatException e) {
            throw this.createCSSParseException("number.format");
        }
    }
    
    protected LexicalUnit dimension(final boolean positive, final LexicalUnit prev) {
        try {
            final float sgn = positive ? 1.0f : -1.0f;
            final String val = this.scanner.getStringValue();
            int i = 0;
        Label_0113:
            while (i < val.length()) {
                switch (val.charAt(i)) {
                    default: {
                        break Label_0113;
                    }
                    case '.':
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9': {
                        ++i;
                        continue;
                    }
                }
            }
            this.nextIgnoreSpaces();
            return CSSLexicalUnit.createDimension(sgn * Float.parseFloat(val.substring(0, i)), val.substring(i), prev);
        }
        catch (NumberFormatException e) {
            throw this.createCSSParseException("number.format");
        }
    }
    
    protected int next() {
        try {
            while (true) {
                this.scanner.clearBuffer();
                this.current = this.scanner.next();
                if (this.current != 18) {
                    break;
                }
                this.documentHandler.comment(this.scanner.getStringValue());
            }
            return this.current;
        }
        catch (ParseException e) {
            this.reportError(e.getMessage());
            return this.current;
        }
    }
    
    protected int nextIgnoreSpaces() {
        try {
            while (true) {
                this.scanner.clearBuffer();
                switch (this.current = this.scanner.next()) {
                    case 18: {
                        this.documentHandler.comment(this.scanner.getStringValue());
                        continue;
                    }
                    default: {
                        return this.current;
                    }
                    case 17: {
                        continue;
                    }
                }
            }
        }
        catch (ParseException e) {
            this.errorHandler.error(this.createCSSParseException(e.getMessage()));
            return this.current;
        }
    }
    
    protected void reportError(final String key) {
        this.reportError(key, null);
    }
    
    protected void reportError(final String key, final Object[] params) {
        this.reportError(this.createCSSParseException(key, params));
    }
    
    protected void reportError(final CSSParseException e) {
        this.errorHandler.error(e);
        int cbraces = 1;
        while (true) {
            switch (this.current) {
                case 0: {
                    return;
                }
                case 2:
                case 8: {
                    if (--cbraces == 0) {
                        this.nextIgnoreSpaces();
                        return;
                    }
                }
                case 1: {
                    ++cbraces;
                    break;
                }
            }
            this.nextIgnoreSpaces();
        }
    }
    
    protected CSSParseException createCSSParseException(final String key) {
        return this.createCSSParseException(key, null);
    }
    
    protected CSSParseException createCSSParseException(final String key, final Object[] params) {
        return new CSSParseException(this.formatMessage(key, params), this.documentURI, this.scanner.getLine(), this.scanner.getColumn());
    }
    
    @Override
    public void parseStyleDeclaration(final String source) throws CSSException, IOException {
        this.scanner = new Scanner(source);
        this.parseStyleDeclarationInternal();
    }
    
    @Override
    public void parseRule(final String source) throws CSSException, IOException {
        this.scanner = new Scanner(source);
        this.parseRuleInternal();
    }
    
    @Override
    public SelectorList parseSelectors(final String source) throws CSSException, IOException {
        this.scanner = new Scanner(source);
        return this.parseSelectorsInternal();
    }
    
    @Override
    public LexicalUnit parsePropertyValue(final String source) throws CSSException, IOException {
        this.scanner = new Scanner(source);
        return this.parsePropertyValueInternal();
    }
    
    @Override
    public boolean parsePriority(final String source) throws CSSException, IOException {
        this.scanner = new Scanner(source);
        return this.parsePriorityInternal();
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
}
