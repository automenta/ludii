// 
// Decompiled by Procyon v0.5.36
// 

package search.pns;

import game.Game;
import util.AI;
import util.Context;
import util.Move;

import java.util.concurrent.ThreadLocalRandom;

public class ProofNumberSearch extends AI
{
    protected final ProofGoals proofGoal;
    protected int proofPlayer;
    protected double bestPossibleRank;
    protected double worstPossibleRank;
    
    public ProofNumberSearch() {
        this(ProofGoals.PROVE_WIN);
    }
    
    public ProofNumberSearch(final ProofGoals proofGoal) {
        this.proofPlayer = -1;
        this.bestPossibleRank = -1.0;
        this.worstPossibleRank = -1.0;
        this.friendlyName = "Proof-Number Search";
        this.proofGoal = proofGoal;
    }
    
    @Override
    public Move selectAction(final Game game, final Context context, final double maxSeconds, final int maxIterations, final int maxDepth) {
        this.bestPossibleRank = context.computeNextWinRank();
        this.worstPossibleRank = context.computeNextLossRank();
        if (this.proofPlayer != context.state().mover()) {
            System.err.println("Warning: Current mover = " + context.state().mover() + ", but proof player = " + this.proofPlayer + "!");
        }
        final PNSNode root = new PNSNode(null, new Context(context), this.proofGoal, this.proofPlayer);
        this.evaluate(root);
        setProofAndDisproofNumbers(root);
        PNSNode currentNode = root;
        while (root.proofNumber() != 0 && root.disproofNumber() != 0) {
            final PNSNode mostProvingNode = selectMostProvingNode(currentNode);
            this.expandNode(mostProvingNode);
            currentNode = updateAncestors(mostProvingNode);
        }
        if (this.proofGoal == ProofGoals.PROVE_WIN) {
            if (root.proofNumber() == 0) {
                System.out.println("Proved a win!");
            }
            else {
                System.out.println("Disproved a win!");
            }
        }
        else if (root.proofNumber() == 0) {
            System.out.println("Proved a loss!");
        }
        else {
            System.out.println("Disproved a loss!");
        }
        return root.legalMoves[ThreadLocalRandom.current().nextInt(root.legalMoves.length)];
    }
    
    private void evaluate(final PNSNode node) {
        final Context context = node.context();
        if (context.trial().over()) {
            final double rank = context.trial().ranking()[this.proofPlayer];
            if (rank == this.bestPossibleRank) {
                if (this.proofGoal == ProofGoals.PROVE_WIN) {
                    node.setValue(PNSNode.PNSNodeValues.TRUE);
                }
                else {
                    node.setValue(PNSNode.PNSNodeValues.FALSE);
                }
            }
            else if (rank == this.worstPossibleRank) {
                if (this.proofGoal == ProofGoals.PROVE_WIN) {
                    node.setValue(PNSNode.PNSNodeValues.FALSE);
                }
                else {
                    node.setValue(PNSNode.PNSNodeValues.TRUE);
                }
            }
            else {
                node.setValue(PNSNode.PNSNodeValues.FALSE);
            }
        }
        else {
            node.setValue(PNSNode.PNSNodeValues.UNKNOWN);
        }
    }
    
    private static void setProofAndDisproofNumbers(final PNSNode node) {
        if (node.isExpanded()) {
            if (node.nodeType() == PNSNode.PNSNodeTypes.AND_NODE) {
                node.setProofNumber(0);
                node.setDisproofNumber(Integer.MAX_VALUE);
                for (final PNSNode child : node.children()) {
                    if (node.proofNumber() == Integer.MAX_VALUE || child.proofNumber() == Integer.MAX_VALUE) {
                        node.setProofNumber(Integer.MAX_VALUE);
                    }
                    else {
                        node.setProofNumber(node.proofNumber() + child.proofNumber());
                    }
                    if (child != null && child.disproofNumber() < node.disproofNumber()) {
                        node.setDisproofNumber(child.disproofNumber());
                    }
                }
            }
            else {
                node.setProofNumber(Integer.MAX_VALUE);
                node.setDisproofNumber(0);
                for (final PNSNode child : node.children()) {
                    if (node.disproofNumber() == Integer.MAX_VALUE || child.disproofNumber() == Integer.MAX_VALUE) {
                        node.setDisproofNumber(Integer.MAX_VALUE);
                    }
                    else {
                        node.setDisproofNumber(node.disproofNumber() + child.disproofNumber());
                    }
                    if (child != null && child.proofNumber() < node.proofNumber()) {
                        node.setProofNumber(child.proofNumber());
                    }
                }
            }
        }
        else {
            switch (node.value()) {
                case FALSE: {
                    node.setProofNumber(Integer.MAX_VALUE);
                    node.setDisproofNumber(0);
                    break;
                }
                case TRUE: {
                    node.setProofNumber(0);
                    node.setDisproofNumber(Integer.MAX_VALUE);
                    break;
                }
                case UNKNOWN: {
                    if (node.nodeType() == PNSNode.PNSNodeTypes.AND_NODE) {
                        node.setProofNumber(Math.max(1, node.children.length));
                        node.setDisproofNumber(1);
                        break;
                    }
                    node.setProofNumber(1);
                    node.setDisproofNumber(Math.max(1, node.children.length));
                    break;
                }
            }
        }
    }
    
    private static PNSNode selectMostProvingNode(final PNSNode inCurrentNode) {
        PNSNode current;
        PNSNode next;
        for (current = inCurrentNode; current.isExpanded(); current = next) {
            final PNSNode[] children = current.children();
            int nextIdx = 0;
            next = children[nextIdx];
            if (current.nodeType() == PNSNode.PNSNodeTypes.OR_NODE) {
                while (next == null || next.proofNumber() != current.proofNumber()) {
                    if (++nextIdx >= children.length) {
                        break;
                    }
                    next = children[nextIdx];
                }
            }
            else {
                while (next == null || next.disproofNumber() != current.disproofNumber()) {
                    if (++nextIdx >= children.length) {
                        break;
                    }
                    next = children[nextIdx];
                }
            }
        }
        return current;
    }
    
    private void expandNode(final PNSNode node) {
        final PNSNode[] children = node.children();
        for (int i = 0; i < children.length; ++i) {
            final Context newContext = new Context(node.context());
            newContext.game().apply(newContext, node.legalMoves[i]);
            final PNSNode child = new PNSNode(node, newContext, this.proofGoal, this.proofPlayer);
            this.evaluate(children[i] = child);
            setProofAndDisproofNumbers(child);
            if (node.nodeType() == PNSNode.PNSNodeTypes.OR_NODE && child.proofNumber() == 0) {
                break;
            }
            if (node.nodeType() == PNSNode.PNSNodeTypes.AND_NODE && child.disproofNumber() == 0) {
                break;
            }
        }
        node.setExpanded(true);
    }
    
    private static PNSNode updateAncestors(final PNSNode inNode) {
        PNSNode node = inNode;
        while (true) {
            final int oldProof = node.proofNumber();
            final int oldDisproof = node.disproofNumber();
            setProofAndDisproofNumbers(node);
            if (node.proofNumber() == oldProof && node.disproofNumber() == oldDisproof) {
                return node;
            }
            if (node.proofNumber() == 0 || node.disproofNumber() == 0) {
                node.deleteSubtree();
            }
            if (node.parent == null) {
                return node;
            }
            node = node.parent;
        }
    }
    
    @Override
    public void initAI(final Game game, final int playerID) {
        this.proofPlayer = playerID;
    }
    
    @Override
    public boolean supportsGame(final Game game) {
        return game.players().count() == 2 && !game.isStochasticGame() && !game.hiddenInformation() && game.isAlternatingMoveGame();
    }
    
    public enum ProofGoals
    {
        PROVE_WIN, 
        PROVE_LOSS
    }
}
