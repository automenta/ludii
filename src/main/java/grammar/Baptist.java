/*
 * Decompiled with CFR 0.150.
 */
package grammar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Baptist {
    private final List<String> names = new ArrayList<>();
    final char[] chars = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '.'};
    private final int DOT = this.chars.length - 1;
    private final int[][][] counts = new int[this.chars.length][this.chars.length][this.chars.length];
    private final int[][] totals = new int[this.chars.length][this.chars.length];
    private static volatile Baptist singleton = null;

    private Baptist() {
        this.loadNames("../Common/src/main/grammar/npp-names-2.txt");
        this.processNames();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static Baptist baptist() {
        if (singleton != null) return singleton;
        Class<Baptist> class_ = Baptist.class;
        synchronized (Baptist.class) {
            singleton = new Baptist();
            // ** MonitorExit[var0] (shouldn't be in output)
            return singleton;
        }
    }

    void loadNames(String filePath) {
        this.names.clear();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))){
            String line;
            while ((line = reader.readLine()) != null) {
                this.names.add(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    void processNames() {
        for (String name : this.names) {
            this.processName(name);
        }
    }

    void processName(String name) {
        String str = ".." + name.toLowerCase() + "..";
        for (int c = 0; c < str.length() - 3; ++c) {
            int ch0 = str.charAt(c) - 97;
            int ch1 = str.charAt(c + 1) - 97;
            int ch2 = str.charAt(c + 2) - 97;
            if (ch0 < 0 || ch0 >= 26) {
                ch0 = this.DOT;
            }
            if (ch1 < 0 || ch1 >= 26) {
                ch1 = this.DOT;
            }
            if (ch2 < 0 || ch2 >= 26) {
                ch2 = this.DOT;
            }
            int[] arrn = this.counts[ch0][ch1];
            int n = ch2;
            arrn[n] = arrn[n] + 1;
            int[] arrn2 = this.totals[ch0];
            int n2 = ch1;
            arrn2[n2] = arrn2[n2] + 1;
        }
    }

    public String name(long seed, int minLength) {
        String result = "";
        Random rng = new Random(seed);
        rng.nextInt();
        do {
            if (result == "") continue;
            result = result + " ";
        } while ((result = result + this.name(rng)).length() < minLength);
        return result;
    }

    public String name(Random rng) {
        int[] token = new int[]{this.DOT, this.DOT, this.DOT};
        String str = "";
        block0: while (true) {
            if (token[2] != this.DOT) {
                str = str + (str == "" ? Character.toUpperCase(this.chars[token[2]]) : this.chars[token[2]]);
            }
            token[0] = token[1];
            token[1] = token[2];
            int total = this.totals[token[0]][token[1]];
            if (total == 0) break;
            int target = rng.nextInt(total) + 1;
            int tally = 0;
            int n = 0;
            while (true) {
                if (n >= this.chars.length) continue block0;
                if (this.counts[token[0]][token[1]][n] != 0 && (tally += this.counts[token[0]][token[1]][n]) >= target) {
                    token[2] = n;
                    continue block0;
                }
                ++n;
            }
        }
        return str;
    }

    public static void main(String[] args) {
        int n;
        for (int n2 = 0; n2 < 20; ++n2) {
            System.out.println(Baptist.baptist().name(n2, 5));
        }
        System.out.println();
        String str = "Yavalath";
        System.out.println("'" + str + "' is called: " + Baptist.baptist().name(str.hashCode(), 5));
        str = "Cameron";
        System.out.println("'" + str + "' is called: " + Baptist.baptist().name(str.hashCode(), 5));
        System.out.println();
        for (n = 0; n < 100; ++n) {
            System.out.println(Baptist.baptist().name((int)System.nanoTime(), 5));
        }
        System.out.println();
        for (n = 0; n < 10000000; ++n) {
            int seed = (int)System.nanoTime();
            String name = Baptist.baptist().name(seed, 5);
            if (!name.equals("Yavalath")) continue;
            System.out.println(name + " found after " + n + " tries (seed = " + seed + ").");
            break;
        }
        System.out.println("Done.");
    }
}

