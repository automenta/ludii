// 
// Decompiled by Procyon v0.5.36
// 

package distance.zhang_shasha;

import java.util.ArrayList;

public class Node
{
    public String label;
    public int index;
    public ArrayList<Node> children;
    public Node leftmost;
    
    public Node() {
        this.children = new ArrayList<>();
    }
    
    public Node(final String label) {
        this.children = new ArrayList<>();
        this.label = label;
    }
}
