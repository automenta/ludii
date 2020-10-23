// 
// Decompiled by Procyon v0.5.36
// 

package main.grammar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Baptist
{
    private final List<String> names;
    final char[] chars;
    private final int DOT;
    private final int[][][] counts;
    private final int[][] totals;
    private static volatile Baptist singleton;
    
    private Baptist() {
        this.names = new ArrayList<>();
        this.chars = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '.' };
        this.DOT = this.chars.length - 1;
        this.counts = new int[this.chars.length][this.chars.length][this.chars.length];
        this.totals = new int[this.chars.length][this.chars.length];
        this.loadNames("../Common/src/main/grammar/npp-names-2.txt");
        this.processNames();
    }
    
    public static Baptist baptist() {
        if (Baptist.singleton == null) {
            synchronized (Baptist.class) {
                Baptist.singleton = new Baptist();
            }
        }
        return Baptist.singleton;
    }
    
    void loadNames(final String filePath) {
        this.names.clear();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            while (true) {
                final String line = reader.readLine();
                if (line == null) {
                    break;
                }
                this.names.add(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    void processNames() {
        for (final String name : this.names) {
            this.processName(name);
        }
    }
    
    void processName(final String name) {
        final String str = ".." + name.toLowerCase() + "..";
        for (int c = 0; c < str.length() - 3; ++c) {
            int ch0 = str.charAt(c) - 'a';
            int ch2 = str.charAt(c + 1) - 'a';
            int ch3 = str.charAt(c + 2) - 'a';
            if (ch0 < 0 || ch0 >= 26) {
                ch0 = this.DOT;
            }
            if (ch2 < 0 || ch2 >= 26) {
                ch2 = this.DOT;
            }
            if (ch3 < 0 || ch3 >= 26) {
                ch3 = this.DOT;
            }
            final int[] array = this.counts[ch0][ch2];
            final int n = ch3;
            ++array[n];
            final int[] array2 = this.totals[ch0];
            final int n2 = ch2;
            ++array2[n2];
        }
    }
    
    public String name(final long seed, final int minLength) {
        String result = "";
        final Random rng = new Random(seed);
        rng.nextInt();
        do {
            if (result != "") {
                result += " ";
            }
            result += this.name(rng);
        } while (result.length() < minLength);
        return result;
    }
    
    public String name(final Random rng) {
        final int[] token = { this.DOT, this.DOT, this.DOT };
        String str = "";
        while (true) {
            if (token[2] != this.DOT) {
                str += ((str == "") ? Character.toUpperCase(this.chars[token[2]]) : this.chars[token[2]]);
            }
            token[0] = token[1];
            token[1] = token[2];
            final int total = this.totals[token[0]][token[1]];
            if (total == 0) {
                break;
            }
            final int target = rng.nextInt(total) + 1;
            int tally = 0;
            for (int n = 0; n < this.chars.length; ++n) {
                if (this.counts[token[0]][token[1]][n] != 0) {
                    tally += this.counts[token[0]][token[1]][n];
                    if (tally >= target) {
                        token[2] = n;
                        break;
                    }
                }
            }
        }
        return str;
    }
    
    public static void main(final String[] args) {
        for (int n = 0; n < 20; ++n) {
            System.out.println(baptist().name(n, 5));
        }
        System.out.println();
        String str = "Yavalath";
        System.out.println("'" + str + "' is called: " + baptist().name(str.hashCode(), 5));
        str = "Cameron";
        System.out.println("'" + str + "' is called: " + baptist().name(str.hashCode(), 5));
        System.out.println();
        for (int n2 = 0; n2 < 100; ++n2) {
            System.out.println(baptist().name((int)System.nanoTime(), 5));
        }
        System.out.println();
        for (int n2 = 0; n2 < 10000000; ++n2) {
            final int seed = (int)System.nanoTime();
            final String name = baptist().name(seed, 5);
            if (name.equals("Yavalath")) {
                System.out.println(name + " found after " + n2 + " tries (seed = " + seed + ").");
                break;
            }
        }
        System.out.println("Done.");
    }
    
    static {
        Baptist.singleton = null;
    }
}
