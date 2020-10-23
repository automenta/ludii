// 
// Decompiled by Procyon v0.5.36
// 

package distance.zhang_shasha;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;

public class Tree
{
    Node root;
    ArrayList<Integer> l;
    ArrayList<Integer> keyroots;
    ArrayList<String> labels;
    static int[][] TD;
    
    public Tree(final String s) throws IOException {
        this.root = new Node();
        this.l = new ArrayList<>();
        this.keyroots = new ArrayList<>();
        this.labels = new ArrayList<>();
        final StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(s));
        tokenizer.nextToken();
        this.root = parseString(this.root, tokenizer);
        if (tokenizer.ttype != -1) {
            throw new RuntimeException("Leftover token: " + tokenizer.ttype);
        }
    }
    
    private static Node parseString(final Node node, final StreamTokenizer tokenizer) throws IOException {
        node.label = tokenizer.sval;
        tokenizer.nextToken();
        if (tokenizer.ttype == 40) {
            tokenizer.nextToken();
            do {
                node.children.add(parseString(new Node(), tokenizer));
            } while (tokenizer.ttype != 41);
            tokenizer.nextToken();
        }
        return node;
    }
    
    public void traverse() {
        traverse(this.root, this.labels);
    }
    
    private static void traverse(final Node node, final ArrayList<String> labels) {
        for (int i = 0; i < node.children.size(); ++i) {
            traverse(node.children.get(i), labels);
        }
        labels.add(node.label);
    }
    
    public void index() {
        index(this.root, 0);
    }
    
    private static int index(final Node node, final int indexIn) {
        int index = indexIn;
        for (int i = 0; i < node.children.size(); ++i) {
            index = index(node.children.get(i), index);
        }
        ++index;
        return node.index = index;
    }
    
    public void l() {
        this.leftmost();
        this.l = new ArrayList<>();
        this.l(this.root, this.l);
    }
    
    private void l(final Node node, final ArrayList<Integer> ll) {
        for (int i = 0; i < node.children.size(); ++i) {
            this.l(node.children.get(i), ll);
        }
        ll.add(node.leftmost.index);
    }
    
    private void leftmost() {
        leftmost(this.root);
    }
    
    private static void leftmost(final Node node) {
        if (node == null) {
            return;
        }
        for (int i = 0; i < node.children.size(); ++i) {
            leftmost(node.children.get(i));
        }
        if (node.children.size() == 0) {
            node.leftmost = node;
        }
        else {
            node.leftmost = node.children.get(0).leftmost;
        }
    }
    
    public void keyroots() {
        for (int i = 0; i < this.l.size(); ++i) {
            int flag = 0;
            for (int j = i + 1; j < this.l.size(); ++j) {
                if (this.l.get(j) == this.l.get(i)) {
                    flag = 1;
                }
            }
            if (flag == 0) {
                this.keyroots.add(i + 1);
            }
        }
    }
    
    public static int ZhangShasha(final Tree tree1, final Tree tree2) {
        tree1.index();
        tree1.l();
        tree1.keyroots();
        tree1.traverse();
        tree2.index();
        tree2.l();
        tree2.keyroots();
        tree2.traverse();
        final ArrayList<Integer> l1 = tree1.l;
        final ArrayList<Integer> keyroots1 = tree1.keyroots;
        final ArrayList<Integer> l2 = tree2.l;
        final ArrayList<Integer> keyroots2 = tree2.keyroots;
        Tree.TD = new int[l1.size() + 1][l2.size() + 1];
        for (int i1 = 1; i1 < keyroots1.size() + 1; ++i1) {
            for (int j1 = 1; j1 < keyroots2.size() + 1; ++j1) {
                final int k = keyroots1.get(i1 - 1);
                final int m = keyroots2.get(j1 - 1);
                Tree.TD[k][m] = treedist(l1, l2, k, m, tree1, tree2);
            }
        }
        return Tree.TD[l1.size()][l2.size()];
    }
    
    private static int treedist(final ArrayList<Integer> l1, final ArrayList<Integer> l2, final int i, final int j, final Tree tree1, final Tree tree2) {
        final int[][] forestdist = new int[i + 1][j + 1];
        final int Delete = 1;
        final int Insert = 1;
        final int Relabel = 1;
        forestdist[0][0] = 0;
        for (int i2 = l1.get(i - 1); i2 <= i; ++i2) {
            forestdist[i2][0] = forestdist[i2 - 1][0] + Delete;
        }
        for (int j2 = l2.get(j - 1); j2 <= j; ++j2) {
            forestdist[0][j2] = forestdist[0][j2 - 1] + Insert;
        }
        for (int i2 = l1.get(i - 1); i2 <= i; ++i2) {
            for (int j3 = l2.get(j - 1); j3 <= j; ++j3) {
                final int i_temp = (l1.get(i - 1) > i2 - 1) ? 0 : (i2 - 1);
                final int j_temp = (l2.get(j - 1) > j3 - 1) ? 0 : (j3 - 1);
                if (l1.get(i2 - 1) == l1.get(i - 1) && l2.get(j3 - 1) == l2.get(j - 1)) {
                    final int Cost = tree1.labels.get(i2 - 1).equals(tree2.labels.get(j3 - 1)) ? 0 : Relabel;
                    forestdist[i2][j3] = Math.min(Math.min(forestdist[i_temp][j3] + Delete, forestdist[i2][j_temp] + Insert), forestdist[i_temp][j_temp] + Cost);
                    Tree.TD[i2][j3] = forestdist[i2][j3];
                }
                else {
                    final int i1_temp = l1.get(i2 - 1) - 1;
                    final int j1_temp = l2.get(j3 - 1) - 1;
                    final int i_temp2 = (l1.get(i - 1) > i1_temp) ? 0 : i1_temp;
                    final int j_temp2 = (l2.get(j - 1) > j1_temp) ? 0 : j1_temp;
                    forestdist[i2][j3] = Math.min(Math.min(forestdist[i_temp][j3] + Delete, forestdist[i2][j_temp] + Insert), forestdist[i_temp2][j_temp2] + Tree.TD[i2][j3]);
                }
            }
        }
        return forestdist[i][j];
    }
}
