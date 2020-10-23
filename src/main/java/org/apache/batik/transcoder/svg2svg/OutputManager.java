// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.svg2svg;

import org.apache.batik.xml.XMLUtilities;
import java.util.Iterator;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.io.Writer;

public class OutputManager
{
    protected PrettyPrinter prettyPrinter;
    protected Writer writer;
    protected int level;
    protected StringBuffer margin;
    protected int line;
    protected int column;
    protected List xmlSpace;
    protected boolean canIndent;
    protected List startingLines;
    protected boolean lineAttributes;
    
    public OutputManager(final PrettyPrinter pp, final Writer w) {
        this.margin = new StringBuffer();
        this.line = 1;
        (this.xmlSpace = new LinkedList()).add(Boolean.FALSE);
        this.canIndent = true;
        this.startingLines = new LinkedList();
        this.lineAttributes = false;
        this.prettyPrinter = pp;
        this.writer = w;
    }
    
    public void printCharacter(final char c) throws IOException {
        if (c == '\n') {
            this.printNewline();
        }
        else {
            ++this.column;
            this.writer.write(c);
        }
    }
    
    public void printNewline() throws IOException {
        final String nl = this.prettyPrinter.getNewline();
        for (int i = 0; i < nl.length(); ++i) {
            this.writer.write(nl.charAt(i));
        }
        this.column = 0;
        ++this.line;
    }
    
    public void printString(final String s) throws IOException {
        for (int i = 0; i < s.length(); ++i) {
            this.printCharacter(s.charAt(i));
        }
    }
    
    public void printCharacters(final char[] ca) throws IOException {
        for (final char aCa : ca) {
            this.printCharacter(aCa);
        }
    }
    
    public void printSpaces(final char[] text, final boolean opt) throws IOException {
        if (this.prettyPrinter.getFormat()) {
            if (!opt) {
                this.printCharacter(' ');
            }
        }
        else {
            this.printCharacters(text);
        }
    }
    
    public void printTopSpaces(final char[] text) throws IOException {
        if (this.prettyPrinter.getFormat()) {
            for (int nl = this.newlines(text), i = 0; i < nl; ++i) {
                this.printNewline();
            }
        }
        else {
            this.printCharacters(text);
        }
    }
    
    public void printComment(final char[] text) throws IOException {
        if (this.prettyPrinter.getFormat()) {
            if (this.canIndent) {
                this.printNewline();
                this.printString(this.margin.toString());
            }
            this.printString("<!--");
            if (this.column + text.length + 3 < this.prettyPrinter.getDocumentWidth()) {
                this.printCharacters(text);
            }
            else {
                this.formatText(text, this.margin.toString(), false);
                this.printCharacter(' ');
            }
            if (this.column + 3 > this.prettyPrinter.getDocumentWidth()) {
                this.printNewline();
                this.printString(this.margin.toString());
            }
            this.printString("-->");
        }
        else {
            this.printString("<!--");
            this.printCharacters(text);
            this.printString("-->");
        }
    }
    
    public void printXMLDecl(final char[] space1, final char[] space2, final char[] space3, final char[] version, final char versionDelim, final char[] space4, final char[] space5, final char[] space6, final char[] encoding, final char encodingDelim, final char[] space7, final char[] space8, final char[] space9, final char[] standalone, final char standaloneDelim, final char[] space10) throws IOException {
        this.printString("<?xml");
        this.printSpaces(space1, false);
        this.printString("version");
        if (space2 != null) {
            this.printSpaces(space2, true);
        }
        this.printCharacter('=');
        if (space3 != null) {
            this.printSpaces(space3, true);
        }
        this.printCharacter(versionDelim);
        this.printCharacters(version);
        this.printCharacter(versionDelim);
        if (space4 != null) {
            this.printSpaces(space4, false);
            if (encoding != null) {
                this.printString("encoding");
                if (space5 != null) {
                    this.printSpaces(space5, true);
                }
                this.printCharacter('=');
                if (space6 != null) {
                    this.printSpaces(space6, true);
                }
                this.printCharacter(encodingDelim);
                this.printCharacters(encoding);
                this.printCharacter(encodingDelim);
                if (space7 != null) {
                    this.printSpaces(space7, standalone == null);
                }
            }
            if (standalone != null) {
                this.printString("standalone");
                if (space8 != null) {
                    this.printSpaces(space8, true);
                }
                this.printCharacter('=');
                if (space9 != null) {
                    this.printSpaces(space9, true);
                }
                this.printCharacter(standaloneDelim);
                this.printCharacters(standalone);
                this.printCharacter(standaloneDelim);
                if (space10 != null) {
                    this.printSpaces(space10, true);
                }
            }
        }
        this.printString("?>");
    }
    
    public void printPI(final char[] target, final char[] space, final char[] data) throws IOException {
        if (this.prettyPrinter.getFormat() && this.canIndent) {
            this.printNewline();
            this.printString(this.margin.toString());
        }
        this.printString("<?");
        this.printCharacters(target);
        this.printSpaces(space, false);
        this.printCharacters(data);
        this.printString("?>");
    }
    
    public void printDoctypeStart(final char[] space1, final char[] root, final char[] space2, final String externalId, final char[] space3, final char[] string1, final char string1Delim, final char[] space4, final char[] string2, final char string2Delim, final char[] space5) throws IOException {
        if (this.prettyPrinter.getFormat()) {
            this.printString("<!DOCTYPE");
            this.printCharacter(' ');
            this.printCharacters(root);
            if (space2 != null) {
                this.printCharacter(' ');
                this.printString(externalId);
                this.printCharacter(' ');
                this.printCharacter(string1Delim);
                this.printCharacters(string1);
                this.printCharacter(string1Delim);
                if (space4 != null && string2 != null) {
                    if (this.column + string2.length + 3 > this.prettyPrinter.getDocumentWidth()) {
                        this.printNewline();
                        for (int i = 0; i < this.prettyPrinter.getTabulationWidth(); ++i) {
                            this.printCharacter(' ');
                        }
                    }
                    else {
                        this.printCharacter(' ');
                    }
                    this.printCharacter(string2Delim);
                    this.printCharacters(string2);
                    this.printCharacter(string2Delim);
                    this.printCharacter(' ');
                }
            }
        }
        else {
            this.printString("<!DOCTYPE");
            this.printSpaces(space1, false);
            this.printCharacters(root);
            if (space2 != null) {
                this.printSpaces(space2, false);
                this.printString(externalId);
                this.printSpaces(space3, false);
                this.printCharacter(string1Delim);
                this.printCharacters(string1);
                this.printCharacter(string1Delim);
                if (space4 != null) {
                    this.printSpaces(space4, string2 == null);
                    if (string2 != null) {
                        this.printCharacter(string2Delim);
                        this.printCharacters(string2);
                        this.printCharacter(string2Delim);
                        if (space5 != null) {
                            this.printSpaces(space5, true);
                        }
                    }
                }
            }
        }
    }
    
    public void printDoctypeEnd(final char[] space) throws IOException {
        if (space != null) {
            this.printSpaces(space, true);
        }
        this.printCharacter('>');
    }
    
    public void printParameterEntityReference(final char[] name) throws IOException {
        this.printCharacter('%');
        this.printCharacters(name);
        this.printCharacter(';');
    }
    
    public void printEntityReference(final char[] name, final boolean first) throws IOException {
        if (this.prettyPrinter.getFormat() && this.xmlSpace.get(0) != Boolean.TRUE && first) {
            this.printNewline();
            this.printString(this.margin.toString());
        }
        this.printCharacter('&');
        this.printCharacters(name);
        this.printCharacter(';');
    }
    
    public void printCharacterEntityReference(final char[] code, final boolean first, final boolean preceedingSpace) throws IOException {
        if (this.prettyPrinter.getFormat() && this.xmlSpace.get(0) != Boolean.TRUE) {
            if (first) {
                this.printNewline();
                this.printString(this.margin.toString());
            }
            else if (preceedingSpace) {
                final int endCol = this.column + code.length + 3;
                if (endCol > this.prettyPrinter.getDocumentWidth()) {
                    this.printNewline();
                    this.printString(this.margin.toString());
                }
                else {
                    this.printCharacter(' ');
                }
            }
        }
        this.printString("&#");
        this.printCharacters(code);
        this.printCharacter(';');
    }
    
    public void printElementStart(final char[] name, final List attributes, final char[] space) throws IOException {
        this.xmlSpace.add(0, this.xmlSpace.get(0));
        this.startingLines.add(0, this.line);
        if (this.prettyPrinter.getFormat() && this.canIndent) {
            this.printNewline();
            this.printString(this.margin.toString());
        }
        this.printCharacter('<');
        this.printCharacters(name);
        if (this.prettyPrinter.getFormat()) {
            final Iterator it = attributes.iterator();
            if (it.hasNext()) {
                final AttributeInfo ai = it.next();
                if (ai.isAttribute("xml:space")) {
                    this.xmlSpace.set(0, ai.value.equals("preserve") ? Boolean.TRUE : Boolean.FALSE);
                }
                this.printCharacter(' ');
                this.printCharacters(ai.name);
                this.printCharacter('=');
                this.printCharacter(ai.delimiter);
                this.printString(ai.value);
                this.printCharacter(ai.delimiter);
            }
            while (it.hasNext()) {
                final AttributeInfo ai = it.next();
                if (ai.isAttribute("xml:space")) {
                    this.xmlSpace.set(0, ai.value.equals("preserve") ? Boolean.TRUE : Boolean.FALSE);
                }
                final int len = ai.name.length + ai.value.length() + 4;
                if (this.lineAttributes || len + this.column > this.prettyPrinter.getDocumentWidth()) {
                    this.printNewline();
                    this.printString(this.margin.toString());
                    for (int i = 0; i < name.length + 2; ++i) {
                        this.printCharacter(' ');
                    }
                }
                else {
                    this.printCharacter(' ');
                }
                this.printCharacters(ai.name);
                this.printCharacter('=');
                this.printCharacter(ai.delimiter);
                this.printString(ai.value);
                this.printCharacter(ai.delimiter);
            }
        }
        else {
            for (final Object attribute : attributes) {
                final AttributeInfo ai2 = (AttributeInfo)attribute;
                if (ai2.isAttribute("xml:space")) {
                    this.xmlSpace.set(0, ai2.value.equals("preserve") ? Boolean.TRUE : Boolean.FALSE);
                }
                this.printSpaces(ai2.space, false);
                this.printCharacters(ai2.name);
                if (ai2.space1 != null) {
                    this.printSpaces(ai2.space1, true);
                }
                this.printCharacter('=');
                if (ai2.space2 != null) {
                    this.printSpaces(ai2.space2, true);
                }
                this.printCharacter(ai2.delimiter);
                this.printString(ai2.value);
                this.printCharacter(ai2.delimiter);
            }
        }
        if (space != null) {
            this.printSpaces(space, true);
        }
        ++this.level;
        for (int j = 0; j < this.prettyPrinter.getTabulationWidth(); ++j) {
            this.margin.append(' ');
        }
        this.canIndent = true;
    }
    
    public void printElementEnd(final char[] name, final char[] space) throws IOException {
        for (int i = 0; i < this.prettyPrinter.getTabulationWidth(); ++i) {
            this.margin.deleteCharAt(0);
        }
        --this.level;
        if (name != null) {
            if (this.prettyPrinter.getFormat() && this.xmlSpace.get(0) != Boolean.TRUE && (this.line != this.startingLines.get(0) || this.column + name.length + 3 >= this.prettyPrinter.getDocumentWidth())) {
                this.printNewline();
                this.printString(this.margin.toString());
            }
            this.printString("</");
            this.printCharacters(name);
            if (space != null) {
                this.printSpaces(space, true);
            }
            this.printCharacter('>');
        }
        else {
            this.printString("/>");
        }
        this.startingLines.remove(0);
        this.xmlSpace.remove(0);
    }
    
    public boolean printCharacterData(final char[] data, final boolean first, final boolean preceedingSpace) throws IOException {
        if (!this.prettyPrinter.getFormat()) {
            this.printCharacters(data);
            return false;
        }
        this.canIndent = true;
        if (this.isWhiteSpace(data)) {
            for (int nl = this.newlines(data), i = 0; i < nl - 1; ++i) {
                this.printNewline();
            }
            return true;
        }
        if (this.xmlSpace.get(0) == Boolean.TRUE) {
            this.printCharacters(data);
            return this.canIndent = false;
        }
        if (first) {
            this.printNewline();
            this.printString(this.margin.toString());
        }
        return this.formatText(data, this.margin.toString(), preceedingSpace);
    }
    
    public void printCDATASection(final char[] data) throws IOException {
        this.printString("<![CDATA[");
        this.printCharacters(data);
        this.printString("]]>");
    }
    
    public void printNotation(final char[] space1, final char[] name, final char[] space2, final String externalId, final char[] space3, final char[] string1, final char string1Delim, final char[] space4, final char[] string2, final char string2Delim, final char[] space5) throws IOException {
        this.writer.write("<!NOTATION");
        this.printSpaces(space1, false);
        this.writer.write(name);
        this.printSpaces(space2, false);
        this.writer.write(externalId);
        this.printSpaces(space3, false);
        this.writer.write(string1Delim);
        this.writer.write(string1);
        this.writer.write(string1Delim);
        if (space4 != null) {
            this.printSpaces(space4, false);
            if (string2 != null) {
                this.writer.write(string2Delim);
                this.writer.write(string2);
                this.writer.write(string2Delim);
            }
        }
        if (space5 != null) {
            this.printSpaces(space5, true);
        }
        this.writer.write(62);
    }
    
    public void printAttlistStart(final char[] space, final char[] name) throws IOException {
        this.writer.write("<!ATTLIST");
        this.printSpaces(space, false);
        this.writer.write(name);
    }
    
    public void printAttlistEnd(final char[] space) throws IOException {
        if (space != null) {
            this.printSpaces(space, false);
        }
        this.writer.write(62);
    }
    
    public void printAttName(final char[] space1, final char[] name, final char[] space2) throws IOException {
        this.printSpaces(space1, false);
        this.writer.write(name);
        this.printSpaces(space2, false);
    }
    
    public void printEnumeration(final List names) throws IOException {
        this.writer.write(40);
        final Iterator it = names.iterator();
        NameInfo ni = it.next();
        if (ni.space1 != null) {
            this.printSpaces(ni.space1, true);
        }
        this.writer.write(ni.name);
        if (ni.space2 != null) {
            this.printSpaces(ni.space2, true);
        }
        while (it.hasNext()) {
            this.writer.write(124);
            ni = it.next();
            if (ni.space1 != null) {
                this.printSpaces(ni.space1, true);
            }
            this.writer.write(ni.name);
            if (ni.space2 != null) {
                this.printSpaces(ni.space2, true);
            }
        }
        this.writer.write(41);
    }
    
    protected int newlines(final char[] text) {
        int result = 0;
        for (final char aText : text) {
            if (aText == '\n') {
                ++result;
            }
        }
        return result;
    }
    
    protected boolean isWhiteSpace(final char[] text) {
        for (final char aText : text) {
            if (!XMLUtilities.isXMLSpace(aText)) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean formatText(final char[] text, final String margin, final boolean preceedingSpace) throws IOException {
        int i = 0;
        boolean startsWithSpace = preceedingSpace;
    Label_0006:
        while (i < text.length) {
            while (i < text.length) {
                if (!XMLUtilities.isXMLSpace(text[i])) {
                    final StringBuffer sb = new StringBuffer();
                    while (i < text.length && !XMLUtilities.isXMLSpace(text[i])) {
                        sb.append(text[i++]);
                    }
                    if (sb.length() == 0) {
                        return startsWithSpace;
                    }
                    if (startsWithSpace) {
                        final int endCol = this.column + sb.length();
                        if (endCol >= this.prettyPrinter.getDocumentWidth() - 1 && (margin.length() + sb.length() < this.prettyPrinter.getDocumentWidth() - 1 || margin.length() < this.column)) {
                            this.printNewline();
                            this.printString(margin);
                        }
                        else if (this.column > margin.length()) {
                            this.printCharacter(' ');
                        }
                    }
                    this.printString(sb.toString());
                    startsWithSpace = false;
                    continue Label_0006;
                }
                else {
                    startsWithSpace = true;
                    ++i;
                }
            }
            break;
        }
        return startsWithSpace;
    }
    
    public static class NameInfo
    {
        public char[] space1;
        public char[] name;
        public char[] space2;
        
        public NameInfo(final char[] sp1, final char[] nm, final char[] sp2) {
            this.space1 = sp1;
            this.name = nm;
            this.space2 = sp2;
        }
    }
    
    public static class AttributeInfo
    {
        public char[] space;
        public char[] name;
        public char[] space1;
        public char[] space2;
        public String value;
        public char delimiter;
        public boolean entityReferences;
        
        public AttributeInfo(final char[] sp, final char[] n, final char[] sp1, final char[] sp2, final String val, final char delim, final boolean entity) {
            this.space = sp;
            this.name = n;
            this.space1 = sp1;
            this.space2 = sp2;
            this.value = val;
            this.delimiter = delim;
            this.entityReferences = entity;
        }
        
        public boolean isAttribute(final String s) {
            if (this.name.length == s.length()) {
                for (int i = 0; i < this.name.length; ++i) {
                    if (this.name[i] != s.charAt(i)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }
}
