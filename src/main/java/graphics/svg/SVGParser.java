// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg;

import graphics.svg.element.BaseElement;
import graphics.svg.element.Element;
import graphics.svg.element.ElementFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SVGParser
{
    private String fileName;
    private final SVG svg;
    
    public SVGParser() {
        this.fileName = "";
        this.svg = new SVG();
    }
    
    public SVGParser(final String filePath) {
        this.fileName = "";
        this.svg = new SVG();
        try {
            this.loadAndParse(filePath);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String fileName() {
        return this.fileName;
    }
    
    public SVG svg() {
        return this.svg;
    }
    
    public void loadAndParse(final String fname) throws IOException {
        this.fileName = fname;
        String content = "";
        final InputStream in = this.getClass().getResourceAsStream(this.fileName);
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                content += line;
            }
        }
        this.parse(content);
    }
    
    public boolean parse(final String content) {
        this.svg.clear();
        for (final Element prototype : ElementFactory.get().prototypes()) {
            final String label = prototype.label();
            int to;
            for (int pos = 0; pos < content.length(); pos = to) {
                pos = content.indexOf("<" + label, pos);
                if (pos == -1) {
                    break;
                }
                to = content.indexOf('>', pos);
                if (to == -1) {
                    System.out.println("* Failed to close expression: " + content.substring(pos));
                    break;
                }
                String expr;
                for (expr = content.substring(pos, to + 1), expr = expr.replaceAll(",", " "), expr = expr.replaceAll(";", " "), expr = expr.replaceAll("\n", " "), expr = expr.replaceAll("\r", " "), expr = expr.replaceAll("\t", " "), expr = expr.replaceAll("\b", " "), expr = expr.replaceAll("\f", " "), expr = expr.replaceAll("-", " -"); expr.contains("  "); expr = expr.replaceAll(" {2}", " ")) {}
                final Element element = ElementFactory.get().generate(label);
                if (!element.load(expr)) {
                    return false;
                }
                ((BaseElement)element).setFilePos(pos);
                this.svg.elements().add(element);
            }
        }
        this.sortElements();
        this.svg.setBounds();
        return true;
    }
    
    void sortElements() {
        this.svg.elements().sort((a, b) -> {
            final int filePosA = ((BaseElement) a).filePos();
            final int filePosB = ((BaseElement) b).filePos();
            return Integer.compare(filePosA, filePosB);
        });
    }
    
    public static boolean isNumeric(final char ch) {
        return (ch >= '0' && ch <= '9') || ch == '-' || ch == '.';
    }
    
    public static Double extractDoubleAt(final String expr, final int from) {
        int c;
        for (c = from; c < expr.length() && !isNumeric(expr.charAt(c)); ++c) {}
        int cc;
        for (cc = c + 1; cc < expr.length() && isNumeric(expr.charAt(cc)); ++cc) {}
        final String sub = expr.substring(c, cc);
        Double result = null;
        try {
            result = Double.parseDouble(sub);
        }
        catch (Exception ex) {}
        return result;
    }
    
    public static Double extractDouble(final String expr, final String heading) {
        int c;
        for (c = 0; c < expr.length() && !isNumeric(expr.charAt(c)); ++c) {}
        int cc;
        for (cc = c + 1; cc < expr.length() && isNumeric(expr.charAt(cc)); ++cc) {}
        final String sub = expr.substring(c, cc);
        Double result = null;
        try {
            result = Double.parseDouble(sub);
        }
        catch (Exception ex) {}
        return result;
    }
    
    public static String extractStringAt(final String str, final int pos) {
        final StringBuilder sb = new StringBuilder();
        if (str.charAt(pos) == '\"') {
            for (int c = pos + 1; c < str.length() && str.charAt(c) != '\"'; ++c) {
                sb.append(str.charAt(c));
            }
        }
        else {
            for (int c = pos; c < str.length() && str.charAt(c) != ';' && str.charAt(c) != ' ' && str.charAt(c) != '\"'; ++c) {
                sb.append(str.charAt(c));
            }
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.fileName).append(" has ");
        sb.append(this.svg);
        return sb.toString();
    }
}
