// 
// Decompiled by Procyon v0.5.36
// 

package graphics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class FormatReader extends BufferedReader
{
    private static String ls;
    private int remain;
    
    public FormatReader(final Reader in) {
        super(in);
        this.remain = -1;
        this.setup();
    }
    
    public FormatReader(final Reader in, final int sz) {
        super(in, sz);
        this.remain = -1;
        this.setup();
    }
    
    public Object[] scanf(final String format) throws IOException, ParseException {
        final List<Object> data = new ArrayList<>();
        final TapeReader fmt = new TapeReader(format.replace("%n", FormatReader.ls));
        final int c = this.read();
        if (c == -1) {
            return null;
        }
        this.parse(c, fmt, data);
        while (!fmt.end()) {
            if (this.remain != -1) {
                this.parse(this.remain, fmt, data);
                this.remain = -1;
            }
            this.parse(this.read(), fmt, data);
        }
        return data.toArray();
    }
    
    private void parse(final int c, final TapeReader fmt, final List<Object> data) throws IOException, ParseException {
        final int f = fmt.next();
        if (c == -1 || fmt.end()) {
            return;
        }
        if (f == 37) {
            final char t = (char)fmt.next();
            switch (t) {
                case 'S', 's' -> {
                    data.add(this.getString(c, fmt));
                    break;
                }
                case 'd' -> {
                    data.add(this.getInteger(c, fmt, 10));
                    break;
                }
                case 'o' -> {
                    data.add(this.getInteger(c, fmt, 8));
                    break;
                }
                case 'X', 'x' -> {
                    data.add(this.getInteger(c, fmt, 16));
                    break;
                }
                case 'f' -> {
                    data.add(this.getDouble(c, fmt));
                    break;
                }
                case 'c' -> {
                    if (c != -1) {
                        data.add((char) c);
                        break;
                    }
                    break;
                }
                case '%' -> {
                    test(c, 37, fmt.head());
                    break;
                }
                default -> {
                    throw new ParseException("Invalid format specifier: " + t, fmt.head());
                }
            }
            return;
        }
        test(f, c, fmt.head());
    }
    
    private static void test(final int actual, final int expected, final int n) throws ParseException {
        if (expected != actual) {
            throw new ParseException((char)expected + " expected, got " + (char)actual, n);
        }
    }
    
    private String getString(int c, final TapeReader fmt) throws IOException {
        final StringBuilder b = new StringBuilder();
        for (int end = fmt.next(); c != end && c > -1; c = this.read()) {
            b.append((char)c);
        }
        fmt.prev();
        this.remain = c;
        return b.toString();
    }
    
    private long getInteger(final int c, final TapeReader fmt, final int base) throws IOException {
        return Long.parseLong(this.getString(c, fmt), base);
    }
    
    private double getDouble(final int c, final TapeReader fmt) throws IOException {
        return Double.parseDouble(this.getString(c, fmt));
    }
    
    private void setup() {
    }
    
    static {
        FormatReader.ls = System.getProperty("line.separator");
    }
    
    private static class TapeReader
    {
        private final char[] tape;
        private int head;
        
        public TapeReader(final String str) {
            this.tape = str.toCharArray();
            this.head = 0;
        }
        
        public int current() {
            return this.tape[this.head];
        }
        
        public int next() {
            if (this.head == this.tape.length) {
                return -1;
            }
            return this.tape[this.head++];
        }
        
        public int prev() {
            if (this.head == -1) {
                ++this.head;
            }
            --this.head;
            return this.tape[this.head];
        }
        
        public boolean end() {
            return this.head == this.tape.length;
        }
        
        public int head() {
            return this.head;
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.tape[this.head]);
        }
    }
}
