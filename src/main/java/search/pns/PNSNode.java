// 
// Decompiled by Procyon v0.5.36
// 

package search.pns;

import main.collections.FastArrayList;
import util.Context;
import util.Move;

import java.util.Arrays;

public class PNSNode
{
    protected final PNSNode parent;
    protected final PNSNodeTypes nodeType;
    protected final Context context;
    protected final PNSNode[] children;
    protected final Move[] legalMoves;
    private boolean expanded;
    private int proofNumber;
    private int disproofNumber;
    private PNSNodeValues value;
    
    public PNSNode(final PNSNode parent, final Context context, final ProofNumberSearch.ProofGoals proofGoal, final int proofPlayer) {
        this.expanded = false;
        this.proofNumber = -1;
        this.disproofNumber = -1;
        this.value = PNSNodeValues.UNKNOWN;
        this.parent = parent;
        this.context = context;
        final int mover = context.state().mover();
        if (mover == proofPlayer) {
            if (proofGoal == ProofNumberSearch.ProofGoals.PROVE_WIN) {
                this.nodeType = PNSNodeTypes.OR_NODE;
            }
            else {
                this.nodeType = PNSNodeTypes.AND_NODE;
            }
        }
        else if (proofGoal == ProofNumberSearch.ProofGoals.PROVE_WIN) {
            this.nodeType = PNSNodeTypes.AND_NODE;
        }
        else {
            this.nodeType = PNSNodeTypes.OR_NODE;
        }
        if (context.trial().over()) {
            this.legalMoves = new Move[0];
        }
        else {
            final FastArrayList<Move> actions = context.game().moves(context).moves();
            actions.toArray(this.legalMoves = new Move[actions.size()]);
        }
        this.children = new PNSNode[this.legalMoves.length];
    }
    
    public PNSNode[] children() {
        return this.children;
    }
    
    public Context context() {
        return this.context;
    }
    
    public void deleteSubtree() {
        Arrays.fill(this.children, null);
    }
    
    public int disproofNumber() {
        assert this.disproofNumber >= 0;
        return this.disproofNumber;
    }
    
    public boolean isExpanded() {
        return this.expanded;
    }
    
    public PNSNodeTypes nodeType() {
        return this.nodeType;
    }
    
    public int proofNumber() {
        assert this.proofNumber >= 0;
        return this.proofNumber;
    }
    
    public void setDisproofNumber(final int disproofNumber) {
        this.disproofNumber = disproofNumber;
    }
    
    public void setExpanded(final boolean expanded) {
        this.expanded = expanded;
    }
    
    public void setProofNumber(final int proofNumber) {
        this.proofNumber = proofNumber;
    }
    
    public void setValue(final PNSNodeValues value) {
        this.value = value;
    }
    
    public PNSNodeValues value() {
        return this.value;
    }
    
    public enum PNSNodeTypes
    {
        OR_NODE, 
        AND_NODE
    }
    
    public enum PNSNodeValues
    {
        TRUE, 
        FALSE, 
        UNKNOWN
    }
}
