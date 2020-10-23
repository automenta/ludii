// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Text;
import org.w3c.dom.Comment;
import java.io.IOException;
import org.w3c.dom.Attr;
import org.apache.batik.util.SVGConstants;

class XmlWriter implements SVGConstants
{
    private static String EOL;
    private static final String TAG_END = "/>";
    private static final String TAG_START = "</";
    private static final char[] SPACES;
    private static final int SPACES_LEN;
    
    private static void writeXml(final Attr attr, final IndentWriter out, final boolean escaped) throws IOException {
        final String name = attr.getName();
        out.write(name);
        out.write("=\"");
        writeChildrenXml(attr, out, escaped);
        out.write(34);
    }
    
    private static void writeChildrenXml(final Attr attr, final IndentWriter out, final boolean escaped) throws IOException {
        final char[] data = attr.getValue().toCharArray();
        if (data == null) {
            return;
        }
        final int length = data.length;
        int start = 0;
        int last;
        for (last = 0; last < length; ++last) {
            final char c = data[last];
            switch (c) {
                case '<': {
                    out.write(data, start, last - start);
                    start = last + 1;
                    out.write("&lt;");
                    break;
                }
                case '>': {
                    out.write(data, start, last - start);
                    start = last + 1;
                    out.write("&gt;");
                    break;
                }
                case '&': {
                    out.write(data, start, last - start);
                    start = last + 1;
                    out.write("&amp;");
                    break;
                }
                case '\"': {
                    out.write(data, start, last - start);
                    start = last + 1;
                    out.write("&quot;");
                    break;
                }
                default: {
                    if (escaped && c > '\u007f') {
                        out.write(data, start, last - start);
                        final String hex = "0000" + Integer.toHexString(c);
                        out.write("&#x" + hex.substring(hex.length() - 4) + ";");
                        start = last + 1;
                        break;
                    }
                    break;
                }
            }
        }
        out.write(data, start, last - start);
    }
    
    private static void writeXml(final Comment comment, final IndentWriter out, final boolean escaped) throws IOException {
        final char[] data = comment.getData().toCharArray();
        if (data == null) {
            out.write("<!---->");
            return;
        }
        out.write("<!--");
        boolean sawDash = false;
        final int length = data.length;
        int start = 0;
        int last;
        for (last = 0; last < length; ++last) {
            final char c = data[last];
            if (c == '-') {
                if (sawDash) {
                    out.write(data, start, last - start);
                    start = last;
                    out.write(32);
                }
                sawDash = true;
            }
            else {
                sawDash = false;
            }
        }
        out.write(data, start, last - start);
        if (sawDash) {
            out.write(32);
        }
        out.write("-->");
    }
    
    private static void writeXml(final Text text, final IndentWriter out, final boolean escaped) throws IOException {
        writeXml(text, out, false, escaped);
    }
    
    private static void writeXml(final Text text, final IndentWriter out, final boolean trimWS, final boolean escaped) throws IOException {
        final char[] data = text.getData().toCharArray();
        if (data == null) {
            System.err.println("Null text data??");
            return;
        }
        final int length = data.length;
        int start = 0;
        int last = 0;
        if (trimWS) {
        Label_0109:
            while (last < length) {
                final char c = data[last];
                switch (c) {
                    case '\t':
                    case '\n':
                    case '\r':
                    case ' ': {
                        ++last;
                        continue;
                    }
                    default: {
                        break Label_0109;
                    }
                }
            }
            start = last;
        }
        while (last < length) {
            final char c = data[last];
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    if (!trimWS) {
                        break;
                    }
                    final int wsStart = last;
                    ++last;
                Label_0269:
                    while (last < length) {
                        switch (data[last]) {
                            case '\t':
                            case '\n':
                            case '\r':
                            case ' ': {
                                ++last;
                                continue;
                            }
                            default: {
                                break Label_0269;
                            }
                        }
                    }
                    if (last == length) {
                        out.write(data, start, wsStart - start);
                        return;
                    }
                    continue;
                }
                case '<': {
                    out.write(data, start, last - start);
                    start = last + 1;
                    out.write("&lt;");
                    break;
                }
                case '>': {
                    out.write(data, start, last - start);
                    start = last + 1;
                    out.write("&gt;");
                    break;
                }
                case '&': {
                    out.write(data, start, last - start);
                    start = last + 1;
                    out.write("&amp;");
                    break;
                }
                default: {
                    if (escaped && c > '\u007f') {
                        out.write(data, start, last - start);
                        final String hex = "0000" + Integer.toHexString(c);
                        out.write("&#x" + hex.substring(hex.length() - 4) + ";");
                        start = last + 1;
                        break;
                    }
                    break;
                }
            }
            ++last;
        }
        out.write(data, start, last - start);
    }
    
    private static void writeXml(final CDATASection cdataSection, final IndentWriter out, final boolean escaped) throws IOException {
        final char[] data = cdataSection.getData().toCharArray();
        if (data == null) {
            out.write("<![CDATA[]]>");
            return;
        }
        out.write("<![CDATA[");
        final int length = data.length;
        int start = 0;
        int last = 0;
        while (last < length) {
            final char c = data[last];
            if (c == ']' && last + 2 < data.length && data[last + 1] == ']' && data[last + 2] == '>') {
                out.write(data, start, last - start);
                start = last + 1;
                out.write("]]]]><![CDATA[>");
            }
            else {
                ++last;
            }
        }
        out.write(data, start, last - start);
        out.write("]]>");
    }
    
    private static void writeXml(final Element element, final IndentWriter out, final boolean escaped) throws IOException, SVGGraphics2DIOException {
        out.write("</", 0, 1);
        out.write(element.getTagName());
        final NamedNodeMap attributes = element.getAttributes();
        if (attributes != null) {
            for (int nAttr = attributes.getLength(), i = 0; i < nAttr; ++i) {
                final Attr attr = (Attr)attributes.item(i);
                out.write(32);
                writeXml(attr, out, escaped);
            }
        }
        final boolean lastElem = element.getParentNode().getLastChild() == element;
        if (!element.hasChildNodes()) {
            if (lastElem) {
                out.setIndentLevel(out.getIndentLevel() - 2);
            }
            out.printIndent();
            out.write("/>", 0, 2);
            return;
        }
        final Node child = element.getFirstChild();
        out.printIndent();
        out.write("/>", 1, 1);
        if (child.getNodeType() != 3 || element.getLastChild() != child) {
            out.setIndentLevel(out.getIndentLevel() + 2);
        }
        writeChildrenXml(element, out, escaped);
        out.write("</", 0, 2);
        out.write(element.getTagName());
        if (lastElem) {
            out.setIndentLevel(out.getIndentLevel() - 2);
        }
        out.printIndent();
        out.write("/>", 1, 1);
    }
    
    private static void writeChildrenXml(final Element element, final IndentWriter out, final boolean escaped) throws IOException, SVGGraphics2DIOException {
        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            writeXml(child, out, escaped);
        }
    }
    
    private static void writeDocumentHeader(final IndentWriter out) throws IOException {
        String encoding = null;
        if (out.getProxied() instanceof OutputStreamWriter) {
            final OutputStreamWriter osw = (OutputStreamWriter)out.getProxied();
            encoding = java2std(osw.getEncoding());
        }
        out.write("<?xml version=\"1.0\"");
        if (encoding != null) {
            out.write(" encoding=\"");
            out.write(encoding);
            out.write(34);
        }
        out.write("?>");
        out.write(XmlWriter.EOL);
        out.write("<!DOCTYPE svg PUBLIC '");
        out.write("-//W3C//DTD SVG 1.0//EN");
        out.write("'");
        out.write(XmlWriter.EOL);
        out.write("          '");
        out.write("http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd");
        out.write("'");
        out.write(">");
        out.write(XmlWriter.EOL);
    }
    
    private static void writeXml(final Document document, final IndentWriter out, final boolean escaped) throws IOException, SVGGraphics2DIOException {
        writeDocumentHeader(out);
        final NodeList childList = document.getChildNodes();
        writeXml(childList, out, escaped);
    }
    
    private static void writeXml(final NodeList childList, final IndentWriter out, final boolean escaped) throws IOException, SVGGraphics2DIOException {
        final int length = childList.getLength();
        if (length == 0) {
            return;
        }
        for (int i = 0; i < length; ++i) {
            final Node child = childList.item(i);
            writeXml(child, out, escaped);
            out.write(XmlWriter.EOL);
        }
    }
    
    static String java2std(final String encodingName) {
        if (encodingName == null) {
            return null;
        }
        if (encodingName.startsWith("ISO8859_")) {
            return "ISO-8859-" + encodingName.substring(8);
        }
        if (encodingName.startsWith("8859_")) {
            return "ISO-8859-" + encodingName.substring(5);
        }
        if ("ASCII7".equalsIgnoreCase(encodingName) || "ASCII".equalsIgnoreCase(encodingName)) {
            return "US-ASCII";
        }
        if ("UTF8".equalsIgnoreCase(encodingName)) {
            return "UTF-8";
        }
        if (encodingName.startsWith("Unicode")) {
            return "UTF-16";
        }
        if ("SJIS".equalsIgnoreCase(encodingName)) {
            return "Shift_JIS";
        }
        if ("JIS".equalsIgnoreCase(encodingName)) {
            return "ISO-2022-JP";
        }
        if ("EUCJIS".equalsIgnoreCase(encodingName)) {
            return "EUC-JP";
        }
        return "UTF-8";
    }
    
    public static void writeXml(final Node node, final Writer writer, final boolean escaped) throws SVGGraphics2DIOException {
        try {
            IndentWriter out = null;
            if (writer instanceof IndentWriter) {
                out = (IndentWriter)writer;
            }
            else {
                out = new IndentWriter(writer);
            }
            switch (node.getNodeType()) {
                case 2: {
                    writeXml((Attr)node, out, escaped);
                    break;
                }
                case 8: {
                    writeXml((Comment)node, out, escaped);
                    break;
                }
                case 3: {
                    writeXml((Text)node, out, escaped);
                    break;
                }
                case 4: {
                    writeXml((CDATASection)node, out, escaped);
                    break;
                }
                case 9: {
                    writeXml((Document)node, out, escaped);
                    break;
                }
                case 11: {
                    writeDocumentHeader(out);
                    final NodeList childList = node.getChildNodes();
                    writeXml(childList, out, escaped);
                    break;
                }
                case 1: {
                    writeXml((Element)node, out, escaped);
                    break;
                }
                default: {
                    throw new SVGGraphics2DRuntimeException("Unable to write node of type " + node.getClass().getName());
                }
            }
        }
        catch (IOException io) {
            throw new SVGGraphics2DIOException(io);
        }
    }
    
    static {
        SPACES = new char[] { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };
        SPACES_LEN = XmlWriter.SPACES.length;
        String temp;
        try {
            temp = System.getProperty("line.separator", "\n");
        }
        catch (SecurityException e) {
            temp = "\n";
        }
        XmlWriter.EOL = temp;
    }
    
    static class IndentWriter extends Writer
    {
        protected Writer proxied;
        protected int indentLevel;
        protected int column;
        
        public IndentWriter(final Writer proxied) {
            if (proxied == null) {
                throw new SVGGraphics2DRuntimeException("proxy should not be null");
            }
            this.proxied = proxied;
        }
        
        public void setIndentLevel(final int indentLevel) {
            this.indentLevel = indentLevel;
        }
        
        public int getIndentLevel() {
            return this.indentLevel;
        }
        
        public void printIndent() throws IOException {
            this.proxied.write(XmlWriter.EOL);
            for (int temp = this.indentLevel; temp > 0; temp -= XmlWriter.SPACES_LEN) {
                if (temp <= XmlWriter.SPACES_LEN) {
                    this.proxied.write(XmlWriter.SPACES, 0, temp);
                    break;
                }
                this.proxied.write(XmlWriter.SPACES, 0, XmlWriter.SPACES_LEN);
            }
            this.column = this.indentLevel;
        }
        
        public Writer getProxied() {
            return this.proxied;
        }
        
        public int getColumn() {
            return this.column;
        }
        
        @Override
        public void write(final int c) throws IOException {
            ++this.column;
            this.proxied.write(c);
        }
        
        @Override
        public void write(final char[] cbuf) throws IOException {
            this.column += cbuf.length;
            this.proxied.write(cbuf);
        }
        
        @Override
        public void write(final char[] cbuf, final int off, final int len) throws IOException {
            this.column += len;
            this.proxied.write(cbuf, off, len);
        }
        
        @Override
        public void write(final String str) throws IOException {
            this.column += str.length();
            this.proxied.write(str);
        }
        
        @Override
        public void write(final String str, final int off, final int len) throws IOException {
            this.column += len;
            this.proxied.write(str, off, len);
        }
        
        @Override
        public void flush() throws IOException {
            this.proxied.flush();
        }
        
        @Override
        public void close() throws IOException {
            this.column = -1;
            this.proxied.close();
        }
    }
}
