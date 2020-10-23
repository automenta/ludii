// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.util;

import org.w3c.dom.DOMException;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.w3c.dom.DOMImplementation;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;
import org.w3c.dom.NodeList;
import java.io.StringWriter;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import java.io.IOException;
import org.apache.batik.dom.AbstractDocument;
import java.io.Writer;
import org.w3c.dom.Document;
import org.apache.batik.constants.XMLConstants;
import org.apache.batik.xml.XMLUtilities;

public class DOMUtilities extends XMLUtilities implements XMLConstants
{
    protected static final String[] LOCK_STRINGS;
    protected static final String[] MODIFIER_STRINGS;
    
    protected DOMUtilities() {
    }
    
    public static void writeDocument(final Document doc, final Writer w) throws IOException {
        final AbstractDocument d = (AbstractDocument)doc;
        if (doc.getDocumentElement() == null) {
            throw new IOException("No document element");
        }
        final NSMap m = NSMap.create();
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            writeNode(n, w, m, "1.1".equals(d.getXmlVersion()));
        }
    }
    
    protected static void writeNode(final Node n, final Writer w, NSMap m, final boolean isXML11) throws IOException {
        switch (n.getNodeType()) {
            case 1: {
                if (n.hasAttributes()) {
                    final NamedNodeMap attr = n.getAttributes();
                    for (int len = attr.getLength(), i = 0; i < len; ++i) {
                        final Attr a = (Attr)attr.item(i);
                        final String name = a.getNodeName();
                        if (name.startsWith("xmlns")) {
                            if (name.length() == 5) {
                                m = m.declare("", a.getNodeValue());
                            }
                            else {
                                final String prefix = name.substring(6);
                                m = m.declare(prefix, a.getNodeValue());
                            }
                        }
                    }
                }
                w.write(60);
                final String ns = n.getNamespaceURI();
                String tagName;
                if (ns == null) {
                    tagName = n.getNodeName();
                    w.write(tagName);
                    if (!"".equals(m.getNamespace(""))) {
                        w.write(" xmlns=\"\"");
                        m = m.declare("", "");
                    }
                }
                else {
                    String prefix2 = n.getPrefix();
                    if (prefix2 == null) {
                        prefix2 = "";
                    }
                    if (ns.equals(m.getNamespace(prefix2))) {
                        tagName = n.getNodeName();
                        w.write(tagName);
                    }
                    else {
                        prefix2 = m.getPrefixForElement(ns);
                        if (prefix2 == null) {
                            prefix2 = m.getNewPrefix();
                            tagName = prefix2 + ':' + n.getLocalName();
                            w.write(tagName + " xmlns:" + prefix2 + "=\"" + contentToString(ns, isXML11) + '\"');
                            m = m.declare(prefix2, ns);
                        }
                        else {
                            if (prefix2.equals("")) {
                                tagName = n.getLocalName();
                            }
                            else {
                                tagName = prefix2 + ':' + n.getLocalName();
                            }
                            w.write(tagName);
                        }
                    }
                }
                if (n.hasAttributes()) {
                    final NamedNodeMap attr2 = n.getAttributes();
                    for (int len2 = attr2.getLength(), j = 0; j < len2; ++j) {
                        final Attr a2 = (Attr)attr2.item(j);
                        String name2 = a2.getNodeName();
                        String prefix3 = a2.getPrefix();
                        final String ans = a2.getNamespaceURI();
                        if (ans != null && !"xmlns".equals(prefix3) && !name2.equals("xmlns") && ((prefix3 != null && !ans.equals(m.getNamespace(prefix3))) || prefix3 == null)) {
                            prefix3 = m.getPrefixForAttr(ans);
                            if (prefix3 == null) {
                                prefix3 = m.getNewPrefix();
                                m = m.declare(prefix3, ans);
                                w.write(" xmlns:" + prefix3 + "=\"" + contentToString(ans, isXML11) + '\"');
                            }
                            name2 = prefix3 + ':' + a2.getLocalName();
                        }
                        w.write(' ' + name2 + "=\"" + contentToString(a2.getNodeValue(), isXML11) + '\"');
                    }
                }
                Node c = n.getFirstChild();
                if (c != null) {
                    w.write(62);
                    do {
                        writeNode(c, w, m, isXML11);
                        c = c.getNextSibling();
                    } while (c != null);
                    w.write("</" + tagName + '>');
                    break;
                }
                w.write("/>");
                break;
            }
            case 3: {
                w.write(contentToString(n.getNodeValue(), isXML11));
                break;
            }
            case 4: {
                final String data = n.getNodeValue();
                if (data.indexOf("]]>") != -1) {
                    throw new IOException("Unserializable CDATA section node");
                }
                w.write("<![CDATA[" + assertValidCharacters(data, isXML11) + "]]>");
                break;
            }
            case 5: {
                w.write('&' + n.getNodeName() + ';');
                break;
            }
            case 7: {
                final String target = n.getNodeName();
                final String data2 = n.getNodeValue();
                if (target.equalsIgnoreCase("xml") || target.indexOf(58) != -1 || data2.indexOf("?>") != -1) {
                    throw new IOException("Unserializable processing instruction node");
                }
                w.write("<?" + target + ' ' + data2 + "?>");
                break;
            }
            case 8: {
                w.write("<!--");
                final String data = n.getNodeValue();
                final int len = data.length();
                if ((len != 0 && data.charAt(len - 1) == '-') || data.indexOf("--") != -1) {
                    throw new IOException("Unserializable comment node");
                }
                w.write(data);
                w.write("-->");
                break;
            }
            case 10: {
                final DocumentType dt = (DocumentType)n;
                w.write("<!DOCTYPE " + n.getOwnerDocument().getDocumentElement().getNodeName());
                final String pubID = dt.getPublicId();
                if (pubID != null) {
                    final char q = getUsableQuote(pubID);
                    if (q == '\0') {
                        throw new IOException("Unserializable DOCTYPE node");
                    }
                    w.write(" PUBLIC " + q + pubID + q);
                }
                final String sysID = dt.getSystemId();
                if (sysID != null) {
                    final char q2 = getUsableQuote(sysID);
                    if (q2 == '\0') {
                        throw new IOException("Unserializable DOCTYPE node");
                    }
                    if (pubID == null) {
                        w.write(" SYSTEM");
                    }
                    w.write(" " + q2 + sysID + q2);
                }
                final String subset = dt.getInternalSubset();
                if (subset != null) {
                    w.write('[' + subset + ']');
                }
                w.write(62);
                break;
            }
            default: {
                throw new IOException("Unknown DOM node type " + n.getNodeType());
            }
        }
    }
    
    public static void writeNode(final Node n, final Writer w) throws IOException {
        if (n.getNodeType() == 9) {
            writeDocument((Document)n, w);
        }
        else {
            final AbstractDocument d = (AbstractDocument)n.getOwnerDocument();
            writeNode(n, w, NSMap.create(), d != null && "1.1".equals(d.getXmlVersion()));
        }
    }
    
    private static char getUsableQuote(final String s) {
        char ret = '\0';
        for (int i = s.length() - 1; i >= 0; --i) {
            final char c = s.charAt(i);
            if (c == '\"') {
                if (ret != '\0') {
                    return '\0';
                }
                ret = '\'';
            }
            else if (c == '\'') {
                if (ret != '\0') {
                    return '\0';
                }
                ret = '\"';
            }
        }
        return (ret == '\0') ? '\"' : ret;
    }
    
    public static String getXML(final Node n) {
        final Writer writer = new StringWriter();
        try {
            writeNode(n, writer);
            writer.close();
        }
        catch (IOException ex) {
            return "";
        }
        return writer.toString();
    }
    
    protected static String assertValidCharacters(final String s, final boolean isXML11) throws IOException {
        for (int len = s.length(), i = 0; i < len; ++i) {
            final char c = s.charAt(i);
            if ((!isXML11 && !XMLUtilities.isXMLCharacter(c)) || (isXML11 && !XMLUtilities.isXML11Character(c))) {
                throw new IOException("Invalid character");
            }
        }
        return s;
    }
    
    public static String contentToString(final String s, final boolean isXML11) throws IOException {
        final StringBuffer result = new StringBuffer(s.length());
        for (int len = s.length(), i = 0; i < len; ++i) {
            final char c = s.charAt(i);
            if ((!isXML11 && !XMLUtilities.isXMLCharacter(c)) || (isXML11 && !XMLUtilities.isXML11Character(c))) {
                throw new IOException("Invalid character");
            }
            switch (c) {
                case '<': {
                    result.append("&lt;");
                    break;
                }
                case '>': {
                    result.append("&gt;");
                    break;
                }
                case '&': {
                    result.append("&amp;");
                    break;
                }
                case '\"': {
                    result.append("&quot;");
                    break;
                }
                case '\'': {
                    result.append("&apos;");
                    break;
                }
                default: {
                    result.append(c);
                    break;
                }
            }
        }
        return result.toString();
    }
    
    public static int getChildIndex(final Node child, final Node parent) {
        if (child == null || child.getParentNode() != parent || child.getParentNode() == null) {
            return -1;
        }
        return getChildIndex(child);
    }
    
    public static int getChildIndex(final Node child) {
        final NodeList children = child.getParentNode().getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node currentChild = children.item(i);
            if (currentChild == child) {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean isAnyNodeAncestorOf(final ArrayList ancestorNodes, final Node node) {
        final int n = ancestorNodes.size();
        for (final Object ancestorNode : ancestorNodes) {
            final Node ancestor = (Node)ancestorNode;
            if (isAncestorOf(ancestor, node)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isAncestorOf(final Node node, final Node descendant) {
        if (node == null || descendant == null) {
            return false;
        }
        for (Node currentNode = descendant.getParentNode(); currentNode != null; currentNode = currentNode.getParentNode()) {
            if (currentNode == node) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isParentOf(final Node node, final Node parentNode) {
        return node != null && parentNode != null && node.getParentNode() == parentNode;
    }
    
    public static boolean canAppend(final Node node, final Node parentNode) {
        return node != null && parentNode != null && node != parentNode && !isAncestorOf(node, parentNode);
    }
    
    public static boolean canAppendAny(final ArrayList children, final Node parentNode) {
        if (!canHaveChildren(parentNode)) {
            return false;
        }
        final int n = children.size();
        for (final Object aChildren : children) {
            final Node child = (Node)aChildren;
            if (canAppend(child, parentNode)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean canHaveChildren(final Node parentNode) {
        if (parentNode == null) {
            return false;
        }
        switch (parentNode.getNodeType()) {
            case 3:
            case 4:
            case 7:
            case 8:
            case 9: {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    public static Node parseXML(final String text, final Document doc, final String uri, final Map prefixes, final String wrapperElementName, final SAXDocumentFactory documentFactory) {
        String wrapperElementPrefix = "";
        String wrapperElementSuffix = "";
        if (wrapperElementName != null) {
            wrapperElementPrefix = "<" + wrapperElementName;
            if (prefixes != null) {
                wrapperElementPrefix += " ";
                for (final Object o : prefixes.entrySet()) {
                    final Map.Entry e = (Map.Entry)o;
                    final String currentKey = e.getKey();
                    final String currentValue = e.getValue();
                    wrapperElementPrefix = wrapperElementPrefix + currentKey + "=\"" + currentValue + "\" ";
                }
            }
            wrapperElementPrefix += ">";
            wrapperElementSuffix = wrapperElementSuffix + "</" + wrapperElementName + '>';
        }
        if (wrapperElementPrefix.trim().length() == 0 && wrapperElementSuffix.trim().length() == 0) {
            try {
                final Document d = documentFactory.createDocument(uri, new StringReader(text));
                if (doc == null) {
                    return d;
                }
                final Node result = doc.createDocumentFragment();
                result.appendChild(doc.importNode(d.getDocumentElement(), true));
                return result;
            }
            catch (Exception ex) {}
        }
        final StringBuffer sb = new StringBuffer(wrapperElementPrefix.length() + text.length() + wrapperElementSuffix.length());
        sb.append(wrapperElementPrefix);
        sb.append(text);
        sb.append(wrapperElementSuffix);
        final String newText = sb.toString();
        try {
            final Document d2 = documentFactory.createDocument(uri, new StringReader(newText));
            if (doc == null) {
                return d2;
            }
            for (Node node = d2.getDocumentElement().getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node.getNodeType() == 1) {
                    node = doc.importNode(node, true);
                    final Node result2 = doc.createDocumentFragment();
                    result2.appendChild(node);
                    return result2;
                }
            }
        }
        catch (Exception ex2) {}
        return null;
    }
    
    public static Document deepCloneDocument(final Document doc, final DOMImplementation impl) {
        final Element root = doc.getDocumentElement();
        final Document result = impl.createDocument(root.getNamespaceURI(), root.getNodeName(), null);
        final Element rroot = result.getDocumentElement();
        boolean before = true;
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n == root) {
                before = false;
                if (root.hasAttributes()) {
                    final NamedNodeMap attr = root.getAttributes();
                    for (int len = attr.getLength(), i = 0; i < len; ++i) {
                        rroot.setAttributeNode((Attr)result.importNode(attr.item(i), true));
                    }
                }
                for (Node c = root.getFirstChild(); c != null; c = c.getNextSibling()) {
                    rroot.appendChild(result.importNode(c, true));
                }
            }
            else if (n.getNodeType() != 10) {
                if (before) {
                    result.insertBefore(result.importNode(n, true), rroot);
                }
                else {
                    result.appendChild(result.importNode(n, true));
                }
            }
        }
        return result;
    }
    
    public static boolean isValidName(final String s) {
        final int len = s.length();
        if (len == 0) {
            return false;
        }
        char c = s.charAt(0);
        int d = c / ' ';
        int m = c % ' ';
        if ((DOMUtilities.NAME_FIRST_CHARACTER[d] & 1 << m) == 0x0) {
            return false;
        }
        for (int i = 1; i < len; ++i) {
            c = s.charAt(i);
            d = c / ' ';
            m = c % ' ';
            if ((DOMUtilities.NAME_CHARACTER[d] & 1 << m) == 0x0) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isValidName11(final String s) {
        final int len = s.length();
        if (len == 0) {
            return false;
        }
        char c = s.charAt(0);
        int d = c / ' ';
        int m = c % ' ';
        if ((DOMUtilities.NAME11_FIRST_CHARACTER[d] & 1 << m) == 0x0) {
            return false;
        }
        for (int i = 1; i < len; ++i) {
            c = s.charAt(i);
            d = c / ' ';
            m = c % ' ';
            if ((DOMUtilities.NAME11_CHARACTER[d] & 1 << m) == 0x0) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isValidPrefix(final String s) {
        return s.indexOf(58) == -1;
    }
    
    public static String getPrefix(final String s) {
        final int i = s.indexOf(58);
        return (i == -1 || i == s.length() - 1) ? null : s.substring(0, i);
    }
    
    public static String getLocalName(final String s) {
        final int i = s.indexOf(58);
        return (i == -1 || i == s.length() - 1) ? s : s.substring(i + 1);
    }
    
    public static void parseStyleSheetPIData(final String data, final HashMap<String, String> table) {
        int i;
        for (i = 0; i < data.length(); ++i) {
            final char c = data.charAt(i);
            if (!XMLUtilities.isXMLSpace(c)) {
                break;
            }
        }
        while (i < data.length()) {
            char c = data.charAt(i);
            int d = c / ' ';
            int m = c % ' ';
            if ((DOMUtilities.NAME_FIRST_CHARACTER[d] & 1 << m) == 0x0) {
                throw new DOMException((short)5, "Wrong name initial:  " + c);
            }
            final StringBuffer ident = new StringBuffer();
            ident.append(c);
            while (++i < data.length()) {
                c = data.charAt(i);
                d = c / ' ';
                m = c % ' ';
                if ((DOMUtilities.NAME_CHARACTER[d] & 1 << m) == 0x0) {
                    break;
                }
                ident.append(c);
            }
            if (i >= data.length()) {
                throw new DOMException((short)12, "Wrong xml-stylesheet data: " + data);
            }
            while (i < data.length()) {
                c = data.charAt(i);
                if (!XMLUtilities.isXMLSpace(c)) {
                    break;
                }
                ++i;
            }
            if (i >= data.length()) {
                throw new DOMException((short)12, "Wrong xml-stylesheet data: " + data);
            }
            if (data.charAt(i) != '=') {
                throw new DOMException((short)12, "Wrong xml-stylesheet data: " + data);
            }
            ++i;
            while (i < data.length()) {
                c = data.charAt(i);
                if (!XMLUtilities.isXMLSpace(c)) {
                    break;
                }
                ++i;
            }
            if (i >= data.length()) {
                throw new DOMException((short)12, "Wrong xml-stylesheet data: " + data);
            }
            c = data.charAt(i);
            ++i;
            final StringBuffer value = new StringBuffer();
            if (c == '\'') {
                while (i < data.length()) {
                    c = data.charAt(i);
                    if (c == '\'') {
                        break;
                    }
                    value.append(c);
                    ++i;
                }
                if (i >= data.length()) {
                    throw new DOMException((short)12, "Wrong xml-stylesheet data: " + data);
                }
            }
            else {
                if (c != '\"') {
                    throw new DOMException((short)12, "Wrong xml-stylesheet data: " + data);
                }
                while (i < data.length()) {
                    c = data.charAt(i);
                    if (c == '\"') {
                        break;
                    }
                    value.append(c);
                    ++i;
                }
                if (i >= data.length()) {
                    throw new DOMException((short)12, "Wrong xml-stylesheet data: " + data);
                }
            }
            table.put(ident.toString().intern(), value.toString());
            ++i;
            while (i < data.length()) {
                c = data.charAt(i);
                if (!XMLUtilities.isXMLSpace(c)) {
                    break;
                }
                ++i;
            }
        }
    }
    
    public static String getModifiersList(final int lockState, int modifiersEx) {
        if ((modifiersEx & 0x2000) != 0x0) {
            modifiersEx = (0x10 | (modifiersEx >> 6 & 0xF));
        }
        else {
            modifiersEx = (modifiersEx >> 6 & 0xF);
        }
        final String s = DOMUtilities.LOCK_STRINGS[lockState & 0xF];
        if (s.length() != 0) {
            return s + ' ' + DOMUtilities.MODIFIER_STRINGS[modifiersEx];
        }
        return DOMUtilities.MODIFIER_STRINGS[modifiersEx];
    }
    
    public static boolean isAttributeSpecifiedNS(final Element e, final String namespaceURI, final String localName) {
        final Attr a = e.getAttributeNodeNS(namespaceURI, localName);
        return a != null && a.getSpecified();
    }
    
    static {
        LOCK_STRINGS = new String[] { "", "CapsLock", "NumLock", "NumLock CapsLock", "Scroll", "Scroll CapsLock", "Scroll NumLock", "Scroll NumLock CapsLock", "KanaMode", "KanaMode CapsLock", "KanaMode NumLock", "KanaMode NumLock CapsLock", "KanaMode Scroll", "KanaMode Scroll CapsLock", "KanaMode Scroll NumLock", "KanaMode Scroll NumLock CapsLock" };
        MODIFIER_STRINGS = new String[] { "", "Shift", "Control", "Control Shift", "Meta", "Meta Shift", "Control Meta", "Control Meta Shift", "Alt", "Alt Shift", "Alt Control", "Alt Control Shift", "Alt Meta", "Alt Meta Shift", "Alt Control Meta", "Alt Control Meta Shift", "AltGraph", "AltGraph Shift", "AltGraph Control", "AltGraph Control Shift", "AltGraph Meta", "AltGraph Meta Shift", "AltGraph Control Meta", "AltGraph Control Meta Shift", "Alt AltGraph", "Alt AltGraph Shift", "Alt AltGraph Control", "Alt AltGraph Control Shift", "Alt AltGraph Meta", "Alt AltGraph Meta Shift", "Alt AltGraph Control Meta", "Alt AltGraph Control Meta Shift" };
    }
    
    private static final class NSMap
    {
        private String prefix;
        private String ns;
        private NSMap next;
        private int nextPrefixNumber;
        
        public static NSMap create() {
            return new NSMap().declare("xml", "http://www.w3.org/XML/1998/namespace").declare("xmlns", "http://www.w3.org/2000/xmlns/");
        }
        
        public NSMap declare(final String prefix, final String ns) {
            final NSMap m = new NSMap();
            m.prefix = prefix;
            m.ns = ns;
            m.next = this;
            m.nextPrefixNumber = this.nextPrefixNumber;
            return m;
        }
        
        public String getNewPrefix() {
            String prefix;
            do {
                prefix = "a" + this.nextPrefixNumber++;
            } while (this.getNamespace(prefix) != null);
            return prefix;
        }
        
        public String getNamespace(final String prefix) {
            for (NSMap m = this; m.next != null; m = m.next) {
                if (m.prefix.equals(prefix)) {
                    return m.ns;
                }
            }
            return null;
        }
        
        public String getPrefixForElement(final String ns) {
            for (NSMap m = this; m.next != null; m = m.next) {
                if (ns.equals(m.ns)) {
                    return m.prefix;
                }
            }
            return null;
        }
        
        public String getPrefixForAttr(final String ns) {
            for (NSMap m = this; m.next != null; m = m.next) {
                if (ns.equals(m.ns) && !m.prefix.equals("")) {
                    return m.prefix;
                }
            }
            return null;
        }
    }
}
