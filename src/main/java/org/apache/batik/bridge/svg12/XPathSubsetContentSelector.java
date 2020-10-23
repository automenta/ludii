// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import java.io.IOException;
import org.apache.batik.parser.ParseException;
import org.apache.batik.xml.XMLUtilities;
import org.apache.batik.parser.AbstractScanner;
import org.w3c.dom.Node;
import java.util.ArrayList;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.apache.batik.anim.dom.XBLOMContentElement;

public class XPathSubsetContentSelector extends AbstractContentSelector
{
    protected static final int SELECTOR_INVALID = -1;
    protected static final int SELECTOR_ANY = 0;
    protected static final int SELECTOR_QNAME = 1;
    protected static final int SELECTOR_ID = 2;
    protected int selectorType;
    protected String prefix;
    protected String localName;
    protected int index;
    protected SelectedNodes selectedContent;
    
    public XPathSubsetContentSelector(final ContentManager cm, final XBLOMContentElement content, final Element bound, final String selector) {
        super(cm, content, bound);
        this.parseSelector(selector);
    }
    
    protected void parseSelector(final String selector) {
        this.selectorType = -1;
        final Scanner scanner = new Scanner(selector);
        int token = scanner.next();
        if (token == 1) {
            final String name1 = scanner.getStringValue();
            token = scanner.next();
            if (token == 0) {
                this.selectorType = 1;
                this.prefix = null;
                this.localName = name1;
                this.index = 0;
                return;
            }
            if (token == 2) {
                token = scanner.next();
                if (token == 1) {
                    final String name2 = scanner.getStringValue();
                    token = scanner.next();
                    if (token == 0) {
                        this.selectorType = 1;
                        this.prefix = name1;
                        this.localName = name2;
                        this.index = 0;
                        return;
                    }
                    if (token == 3) {
                        token = scanner.next();
                        if (token == 8) {
                            final int number = Integer.parseInt(scanner.getStringValue());
                            token = scanner.next();
                            if (token == 4) {
                                token = scanner.next();
                                if (token == 0) {
                                    this.selectorType = 1;
                                    this.prefix = name1;
                                    this.localName = name2;
                                    this.index = number;
                                }
                            }
                        }
                    }
                }
                else if (token == 3) {
                    token = scanner.next();
                    if (token == 8) {
                        final int number2 = Integer.parseInt(scanner.getStringValue());
                        token = scanner.next();
                        if (token == 4) {
                            token = scanner.next();
                            if (token == 0) {
                                this.selectorType = 1;
                                this.prefix = null;
                                this.localName = name1;
                                this.index = number2;
                            }
                        }
                    }
                }
                else if (token == 5 && name1.equals("id")) {
                    token = scanner.next();
                    if (token == 7) {
                        final String id = scanner.getStringValue();
                        token = scanner.next();
                        if (token == 6) {
                            token = scanner.next();
                            if (token == 0) {
                                this.selectorType = 2;
                                this.localName = id;
                            }
                        }
                    }
                }
            }
        }
        else if (token == 9) {
            token = scanner.next();
            if (token == 0) {
                this.selectorType = 0;
                return;
            }
            if (token == 3) {
                token = scanner.next();
                if (token == 8) {
                    final int number3 = Integer.parseInt(scanner.getStringValue());
                    token = scanner.next();
                    if (token == 4) {
                        token = scanner.next();
                        if (token == 0) {
                            this.selectorType = 0;
                            this.index = number3;
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public NodeList getSelectedContent() {
        if (this.selectedContent == null) {
            this.selectedContent = new SelectedNodes();
        }
        return this.selectedContent;
    }
    
    @Override
    boolean update() {
        if (this.selectedContent == null) {
            this.selectedContent = new SelectedNodes();
            return true;
        }
        return this.selectedContent.update();
    }
    
    protected class SelectedNodes implements NodeList
    {
        protected ArrayList nodes;
        
        public SelectedNodes() {
            this.nodes = new ArrayList(10);
            this.update();
        }
        
        protected boolean update() {
            final ArrayList oldNodes = (ArrayList)this.nodes.clone();
            this.nodes.clear();
            int nth = 0;
            for (Node n = XPathSubsetContentSelector.this.boundElement.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n.getNodeType() == 1) {
                    final Element e = (Element)n;
                    boolean matched = XPathSubsetContentSelector.this.selectorType == 0;
                    switch (XPathSubsetContentSelector.this.selectorType) {
                        case 2: {
                            matched = e.getAttributeNS(null, "id").equals(XPathSubsetContentSelector.this.localName);
                            break;
                        }
                        case 1: {
                            if (XPathSubsetContentSelector.this.prefix == null) {
                                matched = (e.getNamespaceURI() == null);
                            }
                            else {
                                final String ns = XPathSubsetContentSelector.this.contentElement.lookupNamespaceURI(XPathSubsetContentSelector.this.prefix);
                                if (ns != null) {
                                    matched = e.getNamespaceURI().equals(ns);
                                }
                            }
                            matched = (matched && XPathSubsetContentSelector.this.localName.equals(e.getLocalName()));
                            break;
                        }
                    }
                    if (XPathSubsetContentSelector.this.selectorType == 0 || XPathSubsetContentSelector.this.selectorType == 1) {
                        matched = (matched && (XPathSubsetContentSelector.this.index == 0 || ++nth == XPathSubsetContentSelector.this.index));
                    }
                    if (matched && !XPathSubsetContentSelector.this.isSelected(n)) {
                        this.nodes.add(e);
                    }
                }
            }
            final int nodesSize = this.nodes.size();
            if (oldNodes.size() != nodesSize) {
                return true;
            }
            for (int i = 0; i < nodesSize; ++i) {
                if (oldNodes.get(i) != this.nodes.get(i)) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public Node item(final int index) {
            if (index < 0 || index >= this.nodes.size()) {
                return null;
            }
            return this.nodes.get(index);
        }
        
        @Override
        public int getLength() {
            return this.nodes.size();
        }
    }
    
    protected static class Scanner extends AbstractScanner
    {
        public static final int EOF = 0;
        public static final int NAME = 1;
        public static final int COLON = 2;
        public static final int LEFT_SQUARE_BRACKET = 3;
        public static final int RIGHT_SQUARE_BRACKET = 4;
        public static final int LEFT_PARENTHESIS = 5;
        public static final int RIGHT_PARENTHESIS = 6;
        public static final int STRING = 7;
        public static final int NUMBER = 8;
        public static final int ASTERISK = 9;
        
        public Scanner(final String s) {
            super(s);
        }
        
        @Override
        protected int endGap() {
            return (this.current != -1) ? 1 : 0;
        }
        
        @Override
        protected void nextToken() throws ParseException {
            try {
                switch (this.current) {
                    case -1: {
                        this.type = 0;
                    }
                    case 58: {
                        this.nextChar();
                        this.type = 2;
                    }
                    case 91: {
                        this.nextChar();
                        this.type = 3;
                    }
                    case 93: {
                        this.nextChar();
                        this.type = 4;
                    }
                    case 40: {
                        this.nextChar();
                        this.type = 5;
                    }
                    case 41: {
                        this.nextChar();
                        this.type = 6;
                    }
                    case 42: {
                        this.nextChar();
                        this.type = 9;
                    }
                    case 9:
                    case 10:
                    case 12:
                    case 13:
                    case 32: {
                        do {
                            this.nextChar();
                        } while (XMLUtilities.isXMLSpace((char)this.current));
                        this.nextToken();
                    }
                    case 39: {
                        this.type = this.string1();
                    }
                    case 34: {
                        this.type = this.string2();
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
                        this.type = this.number();
                    }
                    default: {
                        if (XMLUtilities.isXMLNameFirstCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && this.current != 58 && XMLUtilities.isXMLNameCharacter((char)this.current));
                            this.type = 1;
                            return;
                        }
                        this.nextChar();
                        throw new ParseException("identifier.character", this.reader.getLine(), this.reader.getColumn());
                    }
                }
            }
            catch (IOException e) {
                throw new ParseException(e);
            }
        }
        
        protected int string1() throws IOException {
            this.start = this.position;
            while (true) {
                switch (this.nextChar()) {
                    case -1: {
                        throw new ParseException("eof", this.reader.getLine(), this.reader.getColumn());
                    }
                    case 39: {
                        this.nextChar();
                        return 7;
                    }
                    default: {
                        continue;
                    }
                }
            }
        }
        
        protected int string2() throws IOException {
            this.start = this.position;
            while (true) {
                switch (this.nextChar()) {
                    case -1: {
                        throw new ParseException("eof", this.reader.getLine(), this.reader.getColumn());
                    }
                    case 34: {
                        this.nextChar();
                        return 7;
                    }
                    default: {
                        continue;
                    }
                }
            }
        }
        
        protected int number() throws IOException {
            while (true) {
                switch (this.nextChar()) {
                    case 46: {
                        switch (this.nextChar()) {
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
                                return this.dotNumber();
                            }
                            default: {
                                throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                            }
                        }
                        break;
                    }
                    default: {
                        return 8;
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
        }
        
        protected int dotNumber() throws IOException {
            while (true) {
                switch (this.nextChar()) {
                    default: {
                        return 8;
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
        }
    }
}
