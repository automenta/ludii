// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.svg2svg;

import java.util.List;
import java.util.LinkedList;
import java.io.IOException;
import org.apache.batik.xml.XMLException;
import org.apache.batik.transcoder.TranscoderException;
import java.io.Reader;
import org.apache.batik.transcoder.ErrorHandler;
import java.io.Writer;
import org.apache.batik.xml.XMLScanner;

public class PrettyPrinter
{
    public static final int DOCTYPE_CHANGE = 0;
    public static final int DOCTYPE_REMOVE = 1;
    public static final int DOCTYPE_KEEP_UNCHANGED = 2;
    protected XMLScanner scanner;
    protected OutputManager output;
    protected Writer writer;
    protected ErrorHandler errorHandler;
    protected String newline;
    protected boolean format;
    protected int tabulationWidth;
    protected int documentWidth;
    protected int doctypeOption;
    protected String publicId;
    protected String systemId;
    protected String xmlDeclaration;
    protected int type;
    
    public PrettyPrinter() {
        this.errorHandler = SVGTranscoder.DEFAULT_ERROR_HANDLER;
        this.newline = "\n";
        this.format = true;
        this.tabulationWidth = 4;
        this.documentWidth = 80;
        this.doctypeOption = 2;
    }
    
    public void setXMLDeclaration(final String s) {
        this.xmlDeclaration = s;
    }
    
    public void setDoctypeOption(final int i) {
        this.doctypeOption = i;
    }
    
    public void setPublicId(final String s) {
        this.publicId = s;
    }
    
    public void setSystemId(final String s) {
        this.systemId = s;
    }
    
    public void setNewline(final String s) {
        this.newline = s;
    }
    
    public String getNewline() {
        return this.newline;
    }
    
    public void setFormat(final boolean b) {
        this.format = b;
    }
    
    public boolean getFormat() {
        return this.format;
    }
    
    public void setTabulationWidth(final int i) {
        this.tabulationWidth = Math.max(i, 0);
    }
    
    public int getTabulationWidth() {
        return this.tabulationWidth;
    }
    
    public void setDocumentWidth(final int i) {
        this.documentWidth = Math.max(i, 0);
    }
    
    public int getDocumentWidth() {
        return this.documentWidth;
    }
    
    public void print(final Reader r, final Writer w) throws TranscoderException, IOException {
        Label_0442: {
            try {
                this.scanner = new XMLScanner(r);
                this.output = new OutputManager(this, w);
                this.writer = w;
                this.type = this.scanner.next();
                this.printXMLDecl();
                while (true) {
                    switch (this.type) {
                        case 1: {
                            this.output.printTopSpaces(this.getCurrentValue());
                            this.scanner.clearBuffer();
                            this.type = this.scanner.next();
                            continue;
                        }
                        case 4: {
                            this.output.printComment(this.getCurrentValue());
                            this.scanner.clearBuffer();
                            this.type = this.scanner.next();
                            continue;
                        }
                        case 5: {
                            this.printPI();
                            continue;
                        }
                        default: {
                            this.printDoctype();
                            while (true) {
                                this.scanner.clearBuffer();
                                switch (this.type) {
                                    case 1: {
                                        this.output.printTopSpaces(this.getCurrentValue());
                                        this.scanner.clearBuffer();
                                        this.type = this.scanner.next();
                                        continue;
                                    }
                                    case 4: {
                                        this.output.printComment(this.getCurrentValue());
                                        this.scanner.clearBuffer();
                                        this.type = this.scanner.next();
                                        continue;
                                    }
                                    case 5: {
                                        this.printPI();
                                        continue;
                                    }
                                    default: {
                                        if (this.type != 9) {
                                            throw this.fatalError("element", null);
                                        }
                                        this.printElement();
                                        while (true) {
                                            switch (this.type) {
                                                case 1: {
                                                    this.output.printTopSpaces(this.getCurrentValue());
                                                    this.scanner.clearBuffer();
                                                    this.type = this.scanner.next();
                                                    continue;
                                                }
                                                case 4: {
                                                    this.output.printComment(this.getCurrentValue());
                                                    this.scanner.clearBuffer();
                                                    this.type = this.scanner.next();
                                                    continue;
                                                }
                                                case 5: {
                                                    this.printPI();
                                                    continue;
                                                }
                                                default: {
                                                    break Label_0442;
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
            }
            catch (XMLException e) {
                this.errorHandler.fatalError(new TranscoderException(e.getMessage()));
            }
        }
    }
    
    protected void printXMLDecl() throws TranscoderException, XMLException, IOException {
        if (this.xmlDeclaration == null) {
            if (this.type == 2) {
                if (this.scanner.next() != 1) {
                    throw this.fatalError("space", null);
                }
                final char[] space1 = this.getCurrentValue();
                if (this.scanner.next() != 22) {
                    throw this.fatalError("token", new Object[] { "version" });
                }
                this.type = this.scanner.next();
                char[] space2 = null;
                if (this.type == 1) {
                    space2 = this.getCurrentValue();
                    this.type = this.scanner.next();
                }
                if (this.type != 15) {
                    throw this.fatalError("token", new Object[] { "=" });
                }
                this.type = this.scanner.next();
                char[] space3 = null;
                if (this.type == 1) {
                    space3 = this.getCurrentValue();
                    this.type = this.scanner.next();
                }
                if (this.type != 25) {
                    throw this.fatalError("string", null);
                }
                final char[] version = this.getCurrentValue();
                final char versionDelim = this.scanner.getStringDelimiter();
                char[] space4 = null;
                char[] space5 = null;
                char[] space6 = null;
                char[] encoding = null;
                char encodingDelim = '\0';
                char[] space7 = null;
                char[] space8 = null;
                char[] space9 = null;
                char[] standalone = null;
                char standaloneDelim = '\0';
                char[] space10 = null;
                this.type = this.scanner.next();
                if (this.type == 1) {
                    space4 = this.getCurrentValue();
                    this.type = this.scanner.next();
                    if (this.type == 23) {
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            space5 = this.getCurrentValue();
                            this.type = this.scanner.next();
                        }
                        if (this.type != 15) {
                            throw this.fatalError("token", new Object[] { "=" });
                        }
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            space6 = this.getCurrentValue();
                            this.type = this.scanner.next();
                        }
                        if (this.type != 25) {
                            throw this.fatalError("string", null);
                        }
                        encoding = this.getCurrentValue();
                        encodingDelim = this.scanner.getStringDelimiter();
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            space7 = this.getCurrentValue();
                            this.type = this.scanner.next();
                        }
                    }
                    if (this.type == 24) {
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            space8 = this.getCurrentValue();
                            this.type = this.scanner.next();
                        }
                        if (this.type != 15) {
                            throw this.fatalError("token", new Object[] { "=" });
                        }
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            space9 = this.getCurrentValue();
                            this.type = this.scanner.next();
                        }
                        if (this.type != 25) {
                            throw this.fatalError("string", null);
                        }
                        standalone = this.getCurrentValue();
                        standaloneDelim = this.scanner.getStringDelimiter();
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            space10 = this.getCurrentValue();
                            this.type = this.scanner.next();
                        }
                    }
                }
                if (this.type != 7) {
                    throw this.fatalError("pi.end", null);
                }
                this.output.printXMLDecl(space1, space2, space3, version, versionDelim, space4, space5, space6, encoding, encodingDelim, space7, space8, space9, standalone, standaloneDelim, space10);
                this.type = this.scanner.next();
            }
        }
        else {
            this.output.printString(this.xmlDeclaration);
            this.output.printNewline();
            if (this.type == 2) {
                if (this.scanner.next() != 1) {
                    throw this.fatalError("space", null);
                }
                if (this.scanner.next() != 22) {
                    throw this.fatalError("token", new Object[] { "version" });
                }
                this.type = this.scanner.next();
                if (this.type == 1) {
                    this.type = this.scanner.next();
                }
                if (this.type != 15) {
                    throw this.fatalError("token", new Object[] { "=" });
                }
                this.type = this.scanner.next();
                if (this.type == 1) {
                    this.type = this.scanner.next();
                }
                if (this.type != 25) {
                    throw this.fatalError("string", null);
                }
                this.type = this.scanner.next();
                if (this.type == 1) {
                    this.type = this.scanner.next();
                    if (this.type == 23) {
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            this.type = this.scanner.next();
                        }
                        if (this.type != 15) {
                            throw this.fatalError("token", new Object[] { "=" });
                        }
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            this.type = this.scanner.next();
                        }
                        if (this.type != 25) {
                            throw this.fatalError("string", null);
                        }
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            this.type = this.scanner.next();
                        }
                    }
                    if (this.type == 24) {
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            this.type = this.scanner.next();
                        }
                        if (this.type != 15) {
                            throw this.fatalError("token", new Object[] { "=" });
                        }
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            this.type = this.scanner.next();
                        }
                        if (this.type != 25) {
                            throw this.fatalError("string", null);
                        }
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            this.type = this.scanner.next();
                        }
                    }
                }
                if (this.type != 7) {
                    throw this.fatalError("pi.end", null);
                }
                this.type = this.scanner.next();
            }
        }
    }
    
    protected void printPI() throws TranscoderException, XMLException, IOException {
        final char[] target = this.getCurrentValue();
        this.type = this.scanner.next();
        char[] space = new char[0];
        if (this.type == 1) {
            space = this.getCurrentValue();
            this.type = this.scanner.next();
        }
        if (this.type != 6) {
            throw this.fatalError("pi.data", null);
        }
        final char[] data = this.getCurrentValue();
        this.type = this.scanner.next();
        if (this.type != 7) {
            throw this.fatalError("pi.end", null);
        }
        this.output.printPI(target, space, data);
        this.type = this.scanner.next();
    }
    
    protected void printDoctype() throws TranscoderException, XMLException, IOException {
        switch (this.doctypeOption) {
            default: {
                if (this.type == 3) {
                    this.type = this.scanner.next();
                    if (this.type != 1) {
                        throw this.fatalError("space", null);
                    }
                    final char[] space1 = this.getCurrentValue();
                    this.type = this.scanner.next();
                    if (this.type != 14) {
                        throw this.fatalError("name", null);
                    }
                    final char[] root = this.getCurrentValue();
                    char[] space2 = null;
                    String externalId = null;
                    char[] space3 = null;
                    char[] string1 = null;
                    char string1Delim = '\0';
                    char[] space4 = null;
                    char[] string2 = null;
                    char string2Delim = '\0';
                    char[] space5 = null;
                    this.type = this.scanner.next();
                    if (this.type == 1) {
                        space2 = this.getCurrentValue();
                        switch (this.type = this.scanner.next()) {
                            case 27: {
                                externalId = "PUBLIC";
                                this.type = this.scanner.next();
                                if (this.type != 1) {
                                    throw this.fatalError("space", null);
                                }
                                space3 = this.getCurrentValue();
                                this.type = this.scanner.next();
                                if (this.type != 25) {
                                    throw this.fatalError("string", null);
                                }
                                string1 = this.getCurrentValue();
                                string1Delim = this.scanner.getStringDelimiter();
                                this.type = this.scanner.next();
                                if (this.type != 1) {
                                    throw this.fatalError("space", null);
                                }
                                space4 = this.getCurrentValue();
                                this.type = this.scanner.next();
                                if (this.type != 25) {
                                    throw this.fatalError("string", null);
                                }
                                string2 = this.getCurrentValue();
                                string2Delim = this.scanner.getStringDelimiter();
                                this.type = this.scanner.next();
                                if (this.type == 1) {
                                    space5 = this.getCurrentValue();
                                    this.type = this.scanner.next();
                                    break;
                                }
                                break;
                            }
                            case 26: {
                                externalId = "SYSTEM";
                                this.type = this.scanner.next();
                                if (this.type != 1) {
                                    throw this.fatalError("space", null);
                                }
                                space3 = this.getCurrentValue();
                                this.type = this.scanner.next();
                                if (this.type != 25) {
                                    throw this.fatalError("string", null);
                                }
                                string1 = this.getCurrentValue();
                                string1Delim = this.scanner.getStringDelimiter();
                                this.type = this.scanner.next();
                                if (this.type == 1) {
                                    space4 = this.getCurrentValue();
                                    this.type = this.scanner.next();
                                    break;
                                }
                                break;
                            }
                        }
                    }
                    if (this.doctypeOption == 0) {
                        if (this.publicId != null) {
                            externalId = "PUBLIC";
                            string1 = this.publicId.toCharArray();
                            string1Delim = '\"';
                            if (this.systemId != null) {
                                string2 = this.systemId.toCharArray();
                                string2Delim = '\"';
                            }
                        }
                        else if (this.systemId != null) {
                            externalId = "SYSTEM";
                            string1 = this.systemId.toCharArray();
                            string1Delim = '\"';
                            string2 = null;
                        }
                    }
                    this.output.printDoctypeStart(space1, root, space2, externalId, space3, string1, string1Delim, space4, string2, string2Delim, space5);
                    Label_0986: {
                        if (this.type == 28) {
                            this.output.printCharacter('[');
                            this.type = this.scanner.next();
                            while (true) {
                                switch (this.type) {
                                    case 1: {
                                        this.output.printSpaces(this.getCurrentValue(), true);
                                        this.scanner.clearBuffer();
                                        this.type = this.scanner.next();
                                        continue;
                                    }
                                    case 4: {
                                        this.output.printComment(this.getCurrentValue());
                                        this.scanner.clearBuffer();
                                        this.type = this.scanner.next();
                                        continue;
                                    }
                                    case 5: {
                                        this.printPI();
                                        continue;
                                    }
                                    case 34: {
                                        this.output.printParameterEntityReference(this.getCurrentValue());
                                        this.scanner.clearBuffer();
                                        this.type = this.scanner.next();
                                        continue;
                                    }
                                    case 30: {
                                        this.scanner.clearBuffer();
                                        this.printElementDeclaration();
                                        continue;
                                    }
                                    case 31: {
                                        this.scanner.clearBuffer();
                                        this.printAttlist();
                                        continue;
                                    }
                                    case 33: {
                                        this.scanner.clearBuffer();
                                        this.printNotation();
                                        continue;
                                    }
                                    case 32: {
                                        this.scanner.clearBuffer();
                                        this.printEntityDeclaration();
                                        continue;
                                    }
                                    case 29: {
                                        this.output.printCharacter(']');
                                        this.scanner.clearBuffer();
                                        this.type = this.scanner.next();
                                        break Label_0986;
                                    }
                                    default: {
                                        throw this.fatalError("xml", null);
                                    }
                                }
                            }
                        }
                    }
                    char[] endSpace = null;
                    if (this.type == 1) {
                        endSpace = this.getCurrentValue();
                        this.type = this.scanner.next();
                    }
                    if (this.type != 20) {
                        throw this.fatalError("end", null);
                    }
                    this.type = this.scanner.next();
                    this.output.printDoctypeEnd(endSpace);
                    break;
                }
                else {
                    if (this.doctypeOption == 0) {
                        String externalId2 = "PUBLIC";
                        char[] string3 = "-//W3C//DTD SVG 1.0//EN".toCharArray();
                        char[] string4 = "http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd".toCharArray();
                        if (this.publicId != null) {
                            string3 = this.publicId.toCharArray();
                            if (this.systemId != null) {
                                string4 = this.systemId.toCharArray();
                            }
                        }
                        else if (this.systemId != null) {
                            externalId2 = "SYSTEM";
                            string3 = this.systemId.toCharArray();
                            string4 = null;
                        }
                        this.output.printDoctypeStart(new char[] { ' ' }, new char[] { 's', 'v', 'g' }, new char[] { ' ' }, externalId2, new char[] { ' ' }, string3, '\"', new char[] { ' ' }, string4, '\"', null);
                        this.output.printDoctypeEnd(null);
                        break;
                    }
                    break;
                }
                break;
            }
            case 1: {
                if (this.type == 3) {
                    this.type = this.scanner.next();
                    if (this.type != 1) {
                        throw this.fatalError("space", null);
                    }
                    this.type = this.scanner.next();
                    if (this.type != 14) {
                        throw this.fatalError("name", null);
                    }
                    this.type = this.scanner.next();
                    if (this.type == 1) {
                        switch (this.type = this.scanner.next()) {
                            case 27: {
                                this.type = this.scanner.next();
                                if (this.type != 1) {
                                    throw this.fatalError("space", null);
                                }
                                this.type = this.scanner.next();
                                if (this.type != 25) {
                                    throw this.fatalError("string", null);
                                }
                                this.type = this.scanner.next();
                                if (this.type != 1) {
                                    throw this.fatalError("space", null);
                                }
                                this.type = this.scanner.next();
                                if (this.type != 25) {
                                    throw this.fatalError("string", null);
                                }
                                this.type = this.scanner.next();
                                if (this.type == 1) {
                                    this.type = this.scanner.next();
                                    break;
                                }
                                break;
                            }
                            case 26: {
                                this.type = this.scanner.next();
                                if (this.type != 1) {
                                    throw this.fatalError("space", null);
                                }
                                this.type = this.scanner.next();
                                if (this.type != 25) {
                                    throw this.fatalError("string", null);
                                }
                                this.type = this.scanner.next();
                                if (this.type == 1) {
                                    this.type = this.scanner.next();
                                    break;
                                }
                                break;
                            }
                        }
                    }
                    if (this.type == 28) {
                        do {
                            this.type = this.scanner.next();
                        } while (this.type != 29);
                    }
                    if (this.type == 1) {
                        this.type = this.scanner.next();
                    }
                    if (this.type != 20) {
                        throw this.fatalError("end", null);
                    }
                }
                this.type = this.scanner.next();
                break;
            }
        }
    }
    
    protected String printElement() throws TranscoderException, XMLException, IOException {
        char[] name = this.getCurrentValue();
        final String nameStr = new String(name);
        final List attributes = new LinkedList();
        char[] space = null;
        this.type = this.scanner.next();
    Label_0036:
        while (true) {
            while (this.type == 1) {
                space = this.getCurrentValue();
                this.type = this.scanner.next();
                if (this.type == 14) {
                    final char[] attName = this.getCurrentValue();
                    char[] space2 = null;
                    this.type = this.scanner.next();
                    if (this.type == 1) {
                        space2 = this.getCurrentValue();
                        this.type = this.scanner.next();
                    }
                    if (this.type != 15) {
                        throw this.fatalError("token", new Object[] { "=" });
                    }
                    this.type = this.scanner.next();
                    char[] space3 = null;
                    if (this.type == 1) {
                        space3 = this.getCurrentValue();
                        this.type = this.scanner.next();
                    }
                    if (this.type != 25 && this.type != 16) {
                        throw this.fatalError("string", null);
                    }
                    final char valueDelim = this.scanner.getStringDelimiter();
                    boolean hasEntityRef = false;
                    final StringBuffer sb = new StringBuffer();
                    sb.append(this.getCurrentValue());
                    while (true) {
                        this.scanner.clearBuffer();
                        switch (this.type = this.scanner.next()) {
                            case 16:
                            case 17:
                            case 18:
                            case 25: {
                                sb.append(this.getCurrentValue());
                                continue;
                            }
                            case 12: {
                                hasEntityRef = true;
                                sb.append("&#");
                                sb.append(this.getCurrentValue());
                                sb.append(";");
                                continue;
                            }
                            case 13: {
                                hasEntityRef = true;
                                sb.append("&");
                                sb.append(this.getCurrentValue());
                                sb.append(";");
                                continue;
                            }
                            default: {
                                attributes.add(new OutputManager.AttributeInfo(space, attName, space2, space3, new String(sb), valueDelim, hasEntityRef));
                                space = null;
                                continue Label_0036;
                            }
                        }
                    }
                }
            }
            break;
        }
        this.output.printElementStart(name, attributes, space);
        switch (this.type) {
            default: {
                throw this.fatalError("xml", null);
            }
            case 19: {
                this.output.printElementEnd(null, null);
                break;
            }
            case 20: {
                this.output.printCharacter('>');
                this.type = this.scanner.next();
                this.printContent(this.allowSpaceAtStart(nameStr));
                if (this.type != 10) {
                    throw this.fatalError("end.tag", null);
                }
                name = this.getCurrentValue();
                this.type = this.scanner.next();
                space = null;
                if (this.type == 1) {
                    space = this.getCurrentValue();
                    this.type = this.scanner.next();
                }
                this.output.printElementEnd(name, space);
                if (this.type != 20) {
                    throw this.fatalError("end", null);
                }
                break;
            }
        }
        this.type = this.scanner.next();
        return nameStr;
    }
    
    boolean allowSpaceAtStart(final String tagName) {
        return true;
    }
    
    protected void printContent(boolean spaceAtStart) throws TranscoderException, XMLException, IOException {
        boolean preceedingSpace = false;
        while (true) {
            switch (this.type) {
                case 4: {
                    this.output.printComment(this.getCurrentValue());
                    this.scanner.clearBuffer();
                    this.type = this.scanner.next();
                    preceedingSpace = false;
                    continue;
                }
                case 5: {
                    this.printPI();
                    preceedingSpace = false;
                    continue;
                }
                case 8: {
                    preceedingSpace = this.output.printCharacterData(this.getCurrentValue(), spaceAtStart, preceedingSpace);
                    this.scanner.clearBuffer();
                    this.type = this.scanner.next();
                    spaceAtStart = false;
                    continue;
                }
                case 11: {
                    this.type = this.scanner.next();
                    if (this.type != 8) {
                        throw this.fatalError("character.data", null);
                    }
                    this.output.printCDATASection(this.getCurrentValue());
                    if (this.scanner.next() != 21) {
                        throw this.fatalError("section.end", null);
                    }
                    this.scanner.clearBuffer();
                    this.type = this.scanner.next();
                    preceedingSpace = false;
                    spaceAtStart = false;
                    continue;
                }
                case 9: {
                    final String name = this.printElement();
                    spaceAtStart = this.allowSpaceAtStart(name);
                    continue;
                }
                case 12: {
                    this.output.printCharacterEntityReference(this.getCurrentValue(), spaceAtStart, preceedingSpace);
                    this.scanner.clearBuffer();
                    this.type = this.scanner.next();
                    spaceAtStart = false;
                    preceedingSpace = false;
                    continue;
                }
                case 13: {
                    this.output.printEntityReference(this.getCurrentValue(), spaceAtStart);
                    this.scanner.clearBuffer();
                    this.type = this.scanner.next();
                    spaceAtStart = false;
                    preceedingSpace = false;
                    continue;
                }
                default: {}
            }
        }
    }
    
    protected void printNotation() throws TranscoderException, XMLException, IOException {
        int t = this.scanner.next();
        if (t != 1) {
            throw this.fatalError("space", null);
        }
        final char[] space1 = this.getCurrentValue();
        t = this.scanner.next();
        if (t != 14) {
            throw this.fatalError("name", null);
        }
        final char[] name = this.getCurrentValue();
        t = this.scanner.next();
        if (t != 1) {
            throw this.fatalError("space", null);
        }
        final char[] space2 = this.getCurrentValue();
        t = this.scanner.next();
        String externalId = null;
        char[] space3 = null;
        char[] string1 = null;
        char string1Delim = '\0';
        char[] space4 = null;
        char[] string2 = null;
        char string2Delim = '\0';
        switch (t) {
            default: {
                throw this.fatalError("notation.definition", null);
            }
            case 27: {
                externalId = "PUBLIC";
                t = this.scanner.next();
                if (t != 1) {
                    throw this.fatalError("space", null);
                }
                space3 = this.getCurrentValue();
                t = this.scanner.next();
                if (t != 25) {
                    throw this.fatalError("string", null);
                }
                string1 = this.getCurrentValue();
                string1Delim = this.scanner.getStringDelimiter();
                t = this.scanner.next();
                if (t != 1) {
                    break;
                }
                space4 = this.getCurrentValue();
                t = this.scanner.next();
                if (t == 25) {
                    string2 = this.getCurrentValue();
                    string2Delim = this.scanner.getStringDelimiter();
                    t = this.scanner.next();
                    break;
                }
                break;
            }
            case 26: {
                externalId = "SYSTEM";
                t = this.scanner.next();
                if (t != 1) {
                    throw this.fatalError("space", null);
                }
                space3 = this.getCurrentValue();
                t = this.scanner.next();
                if (t != 25) {
                    throw this.fatalError("string", null);
                }
                string1 = this.getCurrentValue();
                string1Delim = this.scanner.getStringDelimiter();
                t = this.scanner.next();
                break;
            }
        }
        char[] space5 = null;
        if (t == 1) {
            space5 = this.getCurrentValue();
            t = this.scanner.next();
        }
        if (t != 20) {
            throw this.fatalError("end", null);
        }
        this.output.printNotation(space1, name, space2, externalId, space3, string1, string1Delim, space4, string2, string2Delim, space5);
        this.scanner.next();
    }
    
    protected void printAttlist() throws TranscoderException, XMLException, IOException {
        this.type = this.scanner.next();
        if (this.type != 1) {
            throw this.fatalError("space", null);
        }
        char[] space = this.getCurrentValue();
        this.type = this.scanner.next();
        if (this.type != 14) {
            throw this.fatalError("name", null);
        }
        char[] name = this.getCurrentValue();
        this.type = this.scanner.next();
        this.output.printAttlistStart(space, name);
        while (this.type == 1) {
            space = this.getCurrentValue();
            this.type = this.scanner.next();
            if (this.type != 14) {
                break;
            }
            name = this.getCurrentValue();
            this.type = this.scanner.next();
            if (this.type != 1) {
                throw this.fatalError("space", null);
            }
            char[] space2 = this.getCurrentValue();
            this.type = this.scanner.next();
            this.output.printAttName(space, name, space2);
            Label_0977: {
                switch (this.type) {
                    case 45:
                    case 46:
                    case 47:
                    case 48:
                    case 49:
                    case 50:
                    case 51:
                    case 52: {
                        this.output.printCharacters(this.getCurrentValue());
                        this.type = this.scanner.next();
                        break;
                    }
                    case 57: {
                        this.output.printCharacters(this.getCurrentValue());
                        this.type = this.scanner.next();
                        if (this.type != 1) {
                            throw this.fatalError("space", null);
                        }
                        this.output.printSpaces(this.getCurrentValue(), false);
                        this.type = this.scanner.next();
                        if (this.type != 40) {
                            throw this.fatalError("left.brace", null);
                        }
                        this.type = this.scanner.next();
                        final List names = new LinkedList();
                        space = null;
                        if (this.type == 1) {
                            space = this.getCurrentValue();
                            this.type = this.scanner.next();
                        }
                        if (this.type != 14) {
                            throw this.fatalError("name", null);
                        }
                        name = this.getCurrentValue();
                        this.type = this.scanner.next();
                        space2 = null;
                        if (this.type == 1) {
                            space2 = this.getCurrentValue();
                            this.type = this.scanner.next();
                        }
                        names.add(new OutputManager.NameInfo(space, name, space2));
                        while (true) {
                            switch (this.type) {
                                default: {
                                    if (this.type != 41) {
                                        throw this.fatalError("right.brace", null);
                                    }
                                    this.output.printEnumeration(names);
                                    this.type = this.scanner.next();
                                    break Label_0977;
                                }
                                case 42: {
                                    this.type = this.scanner.next();
                                    space = null;
                                    if (this.type == 1) {
                                        space = this.getCurrentValue();
                                        this.type = this.scanner.next();
                                    }
                                    if (this.type != 14) {
                                        throw this.fatalError("name", null);
                                    }
                                    name = this.getCurrentValue();
                                    this.type = this.scanner.next();
                                    space2 = null;
                                    if (this.type == 1) {
                                        space2 = this.getCurrentValue();
                                        this.type = this.scanner.next();
                                    }
                                    names.add(new OutputManager.NameInfo(space, name, space2));
                                    continue;
                                }
                            }
                        }
                        break;
                    }
                    case 40: {
                        this.type = this.scanner.next();
                        final List names = new LinkedList();
                        space = null;
                        if (this.type == 1) {
                            space = this.getCurrentValue();
                            this.type = this.scanner.next();
                        }
                        if (this.type != 56) {
                            throw this.fatalError("nmtoken", null);
                        }
                        name = this.getCurrentValue();
                        this.type = this.scanner.next();
                        space2 = null;
                        if (this.type == 1) {
                            space2 = this.getCurrentValue();
                            this.type = this.scanner.next();
                        }
                        names.add(new OutputManager.NameInfo(space, name, space2));
                        while (true) {
                            switch (this.type) {
                                default: {
                                    if (this.type != 41) {
                                        throw this.fatalError("right.brace", null);
                                    }
                                    this.output.printEnumeration(names);
                                    this.type = this.scanner.next();
                                    break Label_0977;
                                }
                                case 42: {
                                    this.type = this.scanner.next();
                                    space = null;
                                    if (this.type == 1) {
                                        space = this.getCurrentValue();
                                        this.type = this.scanner.next();
                                    }
                                    if (this.type != 56) {
                                        throw this.fatalError("nmtoken", null);
                                    }
                                    name = this.getCurrentValue();
                                    this.type = this.scanner.next();
                                    space2 = null;
                                    if (this.type == 1) {
                                        space2 = this.getCurrentValue();
                                        this.type = this.scanner.next();
                                    }
                                    names.add(new OutputManager.NameInfo(space, name, space2));
                                    continue;
                                }
                            }
                        }
                        break;
                    }
                }
            }
            if (this.type == 1) {
                this.output.printSpaces(this.getCurrentValue(), true);
                this.type = this.scanner.next();
            }
            Label_1391: {
                switch (this.type) {
                    default: {
                        throw this.fatalError("default.decl", null);
                    }
                    case 53:
                    case 54: {
                        this.output.printCharacters(this.getCurrentValue());
                        this.type = this.scanner.next();
                        break;
                    }
                    case 55: {
                        this.output.printCharacters(this.getCurrentValue());
                        this.type = this.scanner.next();
                        if (this.type != 1) {
                            throw this.fatalError("space", null);
                        }
                        this.output.printSpaces(this.getCurrentValue(), false);
                        this.type = this.scanner.next();
                        if (this.type != 25 && this.type != 16) {
                            throw this.fatalError("space", null);
                        }
                    }
                    case 16:
                    case 25: {
                        this.output.printCharacter(this.scanner.getStringDelimiter());
                        this.output.printCharacters(this.getCurrentValue());
                        while (true) {
                            switch (this.type = this.scanner.next()) {
                                case 16:
                                case 17:
                                case 18:
                                case 25: {
                                    this.output.printCharacters(this.getCurrentValue());
                                    continue;
                                }
                                case 12: {
                                    this.output.printString("&#");
                                    this.output.printCharacters(this.getCurrentValue());
                                    this.output.printCharacter(';');
                                    continue;
                                }
                                case 13: {
                                    this.output.printCharacter('&');
                                    this.output.printCharacters(this.getCurrentValue());
                                    this.output.printCharacter(';');
                                    continue;
                                }
                                default: {
                                    this.output.printCharacter(this.scanner.getStringDelimiter());
                                    break Label_1391;
                                }
                            }
                        }
                        break;
                    }
                }
            }
            space = null;
        }
        if (this.type != 20) {
            throw this.fatalError("end", null);
        }
        this.output.printAttlistEnd(space);
        this.type = this.scanner.next();
    }
    
    protected void printEntityDeclaration() throws TranscoderException, XMLException, IOException {
        this.writer.write("<!ENTITY");
        this.type = this.scanner.next();
        if (this.type != 1) {
            throw this.fatalError("space", null);
        }
        this.writer.write(this.getCurrentValue());
        this.type = this.scanner.next();
        boolean pe = false;
        switch (this.type) {
            default: {
                throw this.fatalError("xml", null);
            }
            case 14: {
                this.writer.write(this.getCurrentValue());
                this.type = this.scanner.next();
                break;
            }
            case 58: {
                pe = true;
                this.writer.write(37);
                this.type = this.scanner.next();
                if (this.type != 1) {
                    throw this.fatalError("space", null);
                }
                this.writer.write(this.getCurrentValue());
                this.type = this.scanner.next();
                if (this.type != 14) {
                    throw this.fatalError("name", null);
                }
                this.writer.write(this.getCurrentValue());
                this.type = this.scanner.next();
                break;
            }
        }
        if (this.type != 1) {
            throw this.fatalError("space", null);
        }
        this.writer.write(this.getCurrentValue());
        switch (this.type = this.scanner.next()) {
            case 16:
            case 25: {
                final char sd = this.scanner.getStringDelimiter();
                this.writer.write(sd);
                while (true) {
                    switch (this.type) {
                        case 16:
                        case 17:
                        case 18:
                        case 25: {
                            this.writer.write(this.getCurrentValue());
                            break;
                        }
                        case 13: {
                            this.writer.write(38);
                            this.writer.write(this.getCurrentValue());
                            this.writer.write(59);
                            break;
                        }
                        case 34: {
                            this.writer.write(38);
                            this.writer.write(this.getCurrentValue());
                            this.writer.write(59);
                            break;
                        }
                        default: {
                            this.writer.write(sd);
                            if (this.type == 1) {
                                this.writer.write(this.getCurrentValue());
                                this.type = this.scanner.next();
                            }
                            if (this.type != 20) {
                                throw this.fatalError("end", null);
                            }
                            this.writer.write(">");
                            this.type = this.scanner.next();
                            return;
                        }
                    }
                    this.type = this.scanner.next();
                }
                break;
            }
            case 27: {
                this.writer.write("PUBLIC");
                this.type = this.scanner.next();
                if (this.type != 1) {
                    throw this.fatalError("space", null);
                }
                this.type = this.scanner.next();
                if (this.type != 25) {
                    throw this.fatalError("string", null);
                }
                this.writer.write(" \"");
                this.writer.write(this.getCurrentValue());
                this.writer.write("\" \"");
                this.type = this.scanner.next();
                if (this.type != 1) {
                    throw this.fatalError("space", null);
                }
                this.type = this.scanner.next();
                if (this.type != 25) {
                    throw this.fatalError("string", null);
                }
                this.writer.write(this.getCurrentValue());
                this.writer.write(34);
                break;
            }
            case 26: {
                this.writer.write("SYSTEM");
                this.type = this.scanner.next();
                if (this.type != 1) {
                    throw this.fatalError("space", null);
                }
                this.type = this.scanner.next();
                if (this.type != 25) {
                    throw this.fatalError("string", null);
                }
                this.writer.write(" \"");
                this.writer.write(this.getCurrentValue());
                this.writer.write(34);
                break;
            }
        }
        this.type = this.scanner.next();
        if (this.type == 1) {
            this.writer.write(this.getCurrentValue());
            this.type = this.scanner.next();
            if (!pe && this.type == 59) {
                this.writer.write("NDATA");
                this.type = this.scanner.next();
                if (this.type != 1) {
                    throw this.fatalError("space", null);
                }
                this.writer.write(this.getCurrentValue());
                this.type = this.scanner.next();
                if (this.type != 14) {
                    throw this.fatalError("name", null);
                }
                this.writer.write(this.getCurrentValue());
                this.type = this.scanner.next();
            }
            if (this.type == 1) {
                this.writer.write(this.getCurrentValue());
                this.type = this.scanner.next();
            }
        }
        if (this.type != 20) {
            throw this.fatalError("end", null);
        }
        this.writer.write(62);
        this.type = this.scanner.next();
    }
    
    protected void printElementDeclaration() throws TranscoderException, XMLException, IOException {
        this.writer.write("<!ELEMENT");
        this.type = this.scanner.next();
        if (this.type != 1) {
            throw this.fatalError("space", null);
        }
        this.writer.write(this.getCurrentValue());
        switch (this.type = this.scanner.next()) {
            default: {
                throw this.fatalError("name", null);
            }
            case 14: {
                this.writer.write(this.getCurrentValue());
                this.type = this.scanner.next();
                if (this.type != 1) {
                    throw this.fatalError("space", null);
                }
                this.writer.write(this.getCurrentValue());
                Label_0690: {
                    switch (this.type = this.scanner.next()) {
                        case 35: {
                            this.writer.write("EMPTY");
                            this.type = this.scanner.next();
                            break;
                        }
                        case 36: {
                            this.writer.write("ANY");
                            this.type = this.scanner.next();
                            break;
                        }
                        case 40: {
                            this.writer.write(40);
                            this.type = this.scanner.next();
                            if (this.type == 1) {
                                this.writer.write(this.getCurrentValue());
                                this.type = this.scanner.next();
                            }
                            switch (this.type) {
                                case 44: {
                                    this.writer.write("#PCDATA");
                                    this.type = this.scanner.next();
                                    while (true) {
                                        switch (this.type) {
                                            case 1: {
                                                this.writer.write(this.getCurrentValue());
                                                this.type = this.scanner.next();
                                                continue;
                                            }
                                            case 42: {
                                                this.writer.write(124);
                                                this.type = this.scanner.next();
                                                if (this.type == 1) {
                                                    this.writer.write(this.getCurrentValue());
                                                    this.type = this.scanner.next();
                                                }
                                                if (this.type != 14) {
                                                    throw this.fatalError("name", null);
                                                }
                                                this.writer.write(this.getCurrentValue());
                                                this.type = this.scanner.next();
                                                continue;
                                            }
                                            case 41: {
                                                this.writer.write(41);
                                                this.type = this.scanner.next();
                                                break Label_0690;
                                            }
                                        }
                                    }
                                    break;
                                }
                                case 14:
                                case 40: {
                                    this.printChildren();
                                    if (this.type != 41) {
                                        throw this.fatalError("right.brace", null);
                                    }
                                    this.writer.write(41);
                                    this.type = this.scanner.next();
                                    if (this.type == 1) {
                                        this.writer.write(this.getCurrentValue());
                                        this.type = this.scanner.next();
                                    }
                                    switch (this.type) {
                                        case 37: {
                                            this.writer.write(63);
                                            this.type = this.scanner.next();
                                            break Label_0690;
                                        }
                                        case 39: {
                                            this.writer.write(42);
                                            this.type = this.scanner.next();
                                            break Label_0690;
                                        }
                                        case 38: {
                                            this.writer.write(43);
                                            this.type = this.scanner.next();
                                            break Label_0690;
                                        }
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
                if (this.type == 1) {
                    this.writer.write(this.getCurrentValue());
                    this.type = this.scanner.next();
                }
                if (this.type != 20) {
                    throw this.fatalError("end", null);
                }
                this.writer.write(62);
                this.scanner.next();
            }
        }
    }
    
    protected void printChildren() throws TranscoderException, XMLException, IOException {
        int op = 0;
        while (true) {
            switch (this.type) {
                default: {
                    throw new RuntimeException("Invalid XML");
                }
                case 14: {
                    this.writer.write(this.getCurrentValue());
                    this.type = this.scanner.next();
                    break;
                }
                case 40: {
                    this.writer.write(40);
                    this.type = this.scanner.next();
                    if (this.type == 1) {
                        this.writer.write(this.getCurrentValue());
                        this.type = this.scanner.next();
                    }
                    this.printChildren();
                    if (this.type != 41) {
                        throw this.fatalError("right.brace", null);
                    }
                    this.writer.write(41);
                    this.type = this.scanner.next();
                    break;
                }
            }
            if (this.type == 1) {
                this.writer.write(this.getCurrentValue());
                this.type = this.scanner.next();
            }
            switch (this.type) {
                case 41: {
                    return;
                }
                case 39: {
                    this.writer.write(42);
                    this.type = this.scanner.next();
                    break;
                }
                case 37: {
                    this.writer.write(63);
                    this.type = this.scanner.next();
                    break;
                }
                case 38: {
                    this.writer.write(43);
                    this.type = this.scanner.next();
                    break;
                }
            }
            if (this.type == 1) {
                this.writer.write(this.getCurrentValue());
                this.type = this.scanner.next();
            }
            switch (this.type) {
                case 42: {
                    if (op != 0 && op != this.type) {
                        throw new RuntimeException("Invalid XML");
                    }
                    this.writer.write(124);
                    op = this.type;
                    this.type = this.scanner.next();
                    break;
                }
                case 43: {
                    if (op != 0 && op != this.type) {
                        throw new RuntimeException("Invalid XML");
                    }
                    this.writer.write(44);
                    op = this.type;
                    this.type = this.scanner.next();
                    break;
                }
            }
            if (this.type == 1) {
                this.writer.write(this.getCurrentValue());
                this.type = this.scanner.next();
            }
        }
    }
    
    protected char[] getCurrentValue() {
        final int off = this.scanner.getStart() + this.scanner.getStartOffset();
        final int len = this.scanner.getEnd() + this.scanner.getEndOffset() - off;
        final char[] result = new char[len];
        final char[] buffer = this.scanner.getBuffer();
        System.arraycopy(buffer, off, result, 0, len);
        return result;
    }
    
    protected TranscoderException fatalError(final String key, final Object[] params) throws TranscoderException {
        final TranscoderException result = new TranscoderException(key);
        this.errorHandler.fatalError(result);
        return result;
    }
}
