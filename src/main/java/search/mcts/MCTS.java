// 
// Decompiled by Procyon v0.5.36
// 

package search.mcts;

import expert_iteration.ExItExperience;
import expert_iteration.ExpertPolicy;
import game.Game;
import collections.FVector;
import collections.FastArrayList;
import metadata.ai.features.Features;
import org.json.JSONObject;
import policies.softmax.SoftmaxFromMetadata;
import policies.softmax.SoftmaxPolicy;
import search.mcts.backpropagation.Backpropagation;
import search.mcts.finalmoveselection.FinalMoveSelectionStrategy;
import search.mcts.finalmoveselection.MaxAvgScore;
import search.mcts.finalmoveselection.ProportionalExpVisitCount;
import search.mcts.finalmoveselection.RobustChild;
import search.mcts.nodes.BaseNode;
import search.mcts.nodes.Node;
import search.mcts.nodes.OpenLoopNode;
import search.mcts.playout.PlayoutStrategy;
import search.mcts.playout.RandomPlayout;
import search.mcts.selection.AG0Selection;
import search.mcts.selection.SelectionStrategy;
import search.mcts.selection.UCB1;
import util.AI;
import util.Context;
import util.Move;
import util.Trial;
import utils.AIUtils;

import java.util.List;

public class MCTS extends ExpertPolicy
{
    protected BaseNode rootNode;
    protected SelectionStrategy selectionStrategy;
    protected PlayoutStrategy playoutStrategy;
    protected Backpropagation backpropagation;
    protected FinalMoveSelectionStrategy finalMoveSelectionStrategy;
    protected QInit qInit;
    protected int backpropFlags;
    protected double autoPlaySeconds;
    protected long currentGameFlags;
    protected int lastNumMctsIterations;
    protected int lastNumPlayoutActions;
    protected double lastReturnedMoveValueEst;
    protected String analysisReport;
    protected boolean preserveRootNode;
    protected boolean treeReuse;
    protected int lastActionHistorySize;
    protected SoftmaxPolicy learnedSelectionPolicy;
    
    public static MCTS createUCT() {
        return createUCT(Math.sqrt(2.0));
    }
    
    public static MCTS createUCT(final double explorationConstant) {
        final MCTS uct = new MCTS(new UCB1(explorationConstant), new RandomPlayout(200), new RobustChild());
        uct.friendlyName = "UCT";
        return uct;
    }
    
    public static MCTS createBiasedMCTS(final boolean biasPlayouts) {
        final SoftmaxPolicy softmax = new SoftmaxFromMetadata();
        final MCTS mcts = new MCTS(new AG0Selection(), biasPlayouts ? softmax : new RandomPlayout(200), new RobustChild());
        mcts.setLearnedSelectionPolicy(softmax);
        mcts.friendlyName = (biasPlayouts ? "Biased MCTS" : "Biased MCTS (Uniform Playouts)");
        return mcts;
    }
    
    public static MCTS createBiasedMCTS(final Features features, final boolean biasPlayouts) {
        final SoftmaxPolicy softmax = new SoftmaxPolicy(features);
        final MCTS mcts = new MCTS(new AG0Selection(), biasPlayouts ? softmax : new RandomPlayout(200), new RobustChild());
        mcts.setLearnedSelectionPolicy(softmax);
        mcts.friendlyName = (biasPlayouts ? "Biased MCTS" : "Biased MCTS (Uniform Playouts)");
        return mcts;
    }
    
    public MCTS(final SelectionStrategy selectionStrategy, final PlayoutStrategy playoutStrategy, final FinalMoveSelectionStrategy finalMoveSelectionStrategy) {
        this.rootNode = null;
        this.qInit = QInit.PARENT;
        this.backpropFlags = 0;
        this.autoPlaySeconds = 0.0;
        this.currentGameFlags = 0L;
        this.lastNumMctsIterations = -1;
        this.lastNumPlayoutActions = -1;
        this.lastReturnedMoveValueEst = 0.0;
        this.analysisReport = null;
        this.preserveRootNode = false;
        this.treeReuse = true;
        this.lastActionHistorySize = 0;
        this.learnedSelectionPolicy = null;
        this.selectionStrategy = selectionStrategy;
        this.playoutStrategy = playoutStrategy;
        this.backpropFlags = selectionStrategy.backpropFlags();
        this.backpropagation = new Backpropagation(this.backpropFlags);
        this.finalMoveSelectionStrategy = finalMoveSelectionStrategy;
    }
    
    @Override
    public Move selectAction(final Game game, final Context context, final double maxSeconds, final int maxIterations, final int maxDepth) {
        final long startTime = System.currentTimeMillis();
        long stopTime = (maxSeconds > 0.0) ? (startTime + (long)(maxSeconds * 1000.0)) : Long.MAX_VALUE;
        final int maxIts = (maxIterations >= 0) ? maxIterations : Integer.MAX_VALUE;
        int numIterations = 0;
        if (this.treeReuse && this.rootNode != null) {
            final List<Move> actionHistory = context.trial().moves();
            int offsetActionToTraverse = actionHistory.size() - this.lastActionHistorySize;
            if (offsetActionToTraverse < 0) {
                this.rootNode = null;
            }
            while (offsetActionToTraverse > 0) {
                final Move move = actionHistory.get(actionHistory.size() - offsetActionToTraverse);
                this.rootNode = this.rootNode.findChildForMove(move);
                if (this.rootNode == null) {
                    break;
                }
                --offsetActionToTraverse;
            }
        }
        if (this.rootNode == null || !this.treeReuse) {
            this.rootNode = this.createNode(this, null, null, null, context);
        }
        else {
            this.rootNode.setParent(null);
        }
        this.rootNode.rootInit(context);
        if (this.rootNode.numLegalMoves() == 1 && this.autoPlaySeconds >= 0.0 && this.autoPlaySeconds < maxSeconds) {
            stopTime = startTime + (long)(this.autoPlaySeconds * 1000.0);
        }
        this.lastActionHistorySize = context.trial().moves().size();
        this.lastNumPlayoutActions = 0;
        while (numIterations < maxIts && System.currentTimeMillis() < stopTime && !this.wantsInterrupt) {
            BaseNode current = this.rootNode;
            current.startNewIteration(context);
            while (current.contextRef().trial().status() == null) {
                final int selectedIdx = this.selectionStrategy.select(current);
                BaseNode nextNode = current.childForNthLegalMove(selectedIdx);
                final Context newContext = current.traverse(selectedIdx);
                if (nextNode == null) {
                    nextNode = this.createNode(this, current, newContext.trial().lastMove(), current.nthLegalMove(selectedIdx), newContext);
                    current.addChild(nextNode, selectedIdx);
                    current = nextNode;
                    current.updateContextRef();
                    break;
                }
                current = nextNode;
                current.updateContextRef();
            }
            final Context playoutContext = current.playoutContext();
            Trial endTrial = current.contextRef().trial();
            int numPlayoutActions = 0;
            if (!endTrial.over()) {
                final int numActionsBeforePlayout = current.contextRef().trial().moves().size();
                endTrial = this.playoutStrategy.runPlayout(playoutContext);
                numPlayoutActions = endTrial.moves().size() - numActionsBeforePlayout;
                this.lastNumPlayoutActions += playoutContext.trial().moves().size() - numActionsBeforePlayout;
            }
            this.backpropagation.update(current, playoutContext, AIUtils.agentUtilities(playoutContext), numPlayoutActions);
            ++numIterations;
        }
        this.lastNumMctsIterations = numIterations;
        final Move returnMove = this.finalMoveSelectionStrategy.selectMove(this.rootNode);
        if (!this.wantsInterrupt) {
            int moveVisits = -1;
            for (int i = 0; i < this.rootNode.numLegalMoves(); ++i) {
                final BaseNode child = this.rootNode.childForNthLegalMove(i);
                if (child != null && this.rootNode.nthLegalMove(i).equals(returnMove)) {
                    final int mover = this.rootNode.deterministicContextRef().state().mover();
                    moveVisits = child.numVisits();
                    this.lastReturnedMoveValueEst = child.averageScore(mover, this.rootNode.deterministicContextRef().state());
                    break;
                }
            }
            final int numRootIts = this.rootNode.numVisits();
            this.analysisReport = this.friendlyName + " made move after " + numRootIts + " iterations (selected child visits = " + moveVisits + ", value = " + this.lastReturnedMoveValueEst + ").";
        }
        else {
            this.analysisReport = null;
        }
        if (!this.preserveRootNode) {
            if (!this.treeReuse) {
                this.rootNode = null;
            }
            else if (!this.wantsInterrupt) {
                this.rootNode = this.rootNode.findChildForMove(returnMove);
                if (this.rootNode != null) {
                    this.rootNode.setParent(null);
                    ++this.lastActionHistorySize;
                }
            }
        }
        return returnMove;
    }
    
    private BaseNode createNode(final MCTS mcts, final BaseNode parent, final Move parentMove, final Move parentMoveWithoutConseq, final Context context) {
        if ((this.currentGameFlags & 0x40L) == 0x0L) {
            return new Node(mcts, parent, parentMove, parentMoveWithoutConseq, context);
        }
        return new OpenLoopNode(mcts, parent, parentMove, parentMoveWithoutConseq, context.game());
    }
    
    public void setAutoPlaySeconds(final double seconds) {
        this.autoPlaySeconds = seconds;
    }
    
    public void setTreeReuse(final boolean treeReuse) {
        this.treeReuse = treeReuse;
    }
    
    public int backpropFlags() {
        return this.backpropFlags;
    }
    
    public SoftmaxPolicy learnedSelectionPolicy() {
        return this.learnedSelectionPolicy;
    }
    
    public PlayoutStrategy playoutStrategy() {
        return this.playoutStrategy;
    }
    
    public QInit qInit() {
        return this.qInit;
    }
    
    public BaseNode rootNode() {
        return this.rootNode;
    }
    
    public void setLearnedSelectionPolicy(final SoftmaxPolicy policy) {
        this.learnedSelectionPolicy = policy;
    }
    
    public void setQInit(final QInit init) {
        this.qInit = init;
    }
    
    public void setPreserveRootNode(final boolean preserveRootNode) {
        this.preserveRootNode = preserveRootNode;
    }
    
    public int getNumMctsIterations() {
        return this.lastNumMctsIterations;
    }
    
    public int getNumPlayoutActions() {
        return this.lastNumPlayoutActions;
    }
    
    @Override
    public void initAI(final Game game, final int playerID) {
        this.currentGameFlags = game.gameFlags();
        this.lastNumMctsIterations = -1;
        this.lastNumPlayoutActions = -1;
        this.rootNode = null;
        this.lastActionHistorySize = 0;
        if (this.learnedSelectionPolicy != null) {
            this.learnedSelectionPolicy.initAI(game, playerID);
        }
        if (this.playoutStrategy instanceof AI && this.playoutStrategy != this.learnedSelectionPolicy) {
            final AI aiPlayout = (AI)this.playoutStrategy;
            aiPlayout.initAI(game, playerID);
        }
        this.lastReturnedMoveValueEst = 0.0;
        this.analysisReport = null;
    }
    
    @Override
    public boolean supportsGame(final Game game) {
        final long gameFlags = game.gameFlags();
        return (gameFlags & 0x400L) == 0x0L && (this.learnedSelectionPolicy == null || this.learnedSelectionPolicy.supportsGame(game)) && this.playoutStrategy.playoutSupportsGame(game);
    }
    
    @Override
    public double estimateValue() {
        return this.lastReturnedMoveValueEst;
    }
    
    @Override
    public String generateAnalysisReport() {
        return this.analysisReport;
    }
    
    @Override
    public AIVisualisationData aiVisualisationData() {
        if (this.rootNode == null) {
            return null;
        }
        if (this.rootNode.numVisits() == 0) {
            return null;
        }
        final int numChildren = this.rootNode.numLegalMoves();
        final FVector aiDistribution = new FVector(numChildren);
        final FVector valueEstimates = new FVector(numChildren);
        final int mover = this.rootNode.contextRef().state().mover();
        final FastArrayList<Move> moves = new FastArrayList<>();
        for (int i = 0; i < numChildren; ++i) {
            final BaseNode child = this.rootNode.childForNthLegalMove(i);
            if (child == null) {
                aiDistribution.set(i, 0.0f);
                if (this.rootNode.numVisits() == 0) {
                    valueEstimates.set(i, 0.0f);
                }
                else {
                    valueEstimates.set(i, (float)this.rootNode.valueEstimateUnvisitedChildren(mover, this.rootNode.contextRef().state()));
                }
            }
            else {
                aiDistribution.set(i, child.numVisits());
                valueEstimates.set(i, (float)child.averageScore(mover, this.rootNode.contextRef().state()));
            }
            if (valueEstimates.get(i) > 1.0f) {
                valueEstimates.set(i, 1.0f);
            }
            else if (valueEstimates.get(i) < -1.0f) {
                valueEstimates.set(i, -1.0f);
            }
            moves.add(this.rootNode.nthLegalMove(i));
        }
        return new AIVisualisationData(aiDistribution, valueEstimates, moves);
    }
    
    public static MCTS fromJson(final JSONObject json) {
        final SelectionStrategy selection = SelectionStrategy.fromJson(json.getJSONObject("selection"));
        final PlayoutStrategy playout = PlayoutStrategy.fromJson(json.getJSONObject("playout"));
        final FinalMoveSelectionStrategy finalMove = FinalMoveSelectionStrategy.fromJson(json.getJSONObject("final_move"));
        final MCTS mcts = new MCTS(selection, playout, finalMove);
        if (json.has("tree_reuse")) {
            mcts.setTreeReuse(json.getBoolean("tree_reuse"));
        }
        if (json.has("friendly_name")) {
            mcts.friendlyName = json.getString("friendly_name");
        }
        return mcts;
    }
    
    @Override
    public FastArrayList<Move> lastSearchRootMoves() {
        return this.rootNode.movesFromNode();
    }
    
    @Override
    public FVector computeExpertPolicy(final double tau) {
        return this.rootNode.computeVisitCountPolicy(tau);
    }
    
    @Override
    public ExItExperience generateExItExperience() {
        return this.rootNode.generateExItExperience();
    }
    
    public static MCTS fromLines(final String[] lines) {
        SelectionStrategy selection = new UCB1();
        PlayoutStrategy playout = new RandomPlayout(200);
        FinalMoveSelectionStrategy finalMove = new RobustChild();
        boolean treeReuse = false;
        SoftmaxPolicy learnedSelectionPolicy = null;
        String friendlyName = "MCTS";
        for (final String line : lines) {
            final String[] lineParts = line.split(",");
            if (lineParts[0].toLowerCase().startsWith("selection=")) {
                if (lineParts[0].toLowerCase().endsWith("ucb1")) {
                    selection = new UCB1();
                    selection.customise(lineParts);
                }
                else if (lineParts[0].toLowerCase().endsWith("ag0selection") || lineParts[0].toLowerCase().endsWith("alphago0selection")) {
                    selection = new AG0Selection();
                    selection.customise(lineParts);
                }
                else {
                    System.err.println("Unknown selection strategy: " + line);
                }
            }
            else if (lineParts[0].toLowerCase().startsWith("playout=")) {
                playout = PlayoutStrategy.constructPlayoutStrategy(lineParts);
            }
            else if (lineParts[0].toLowerCase().startsWith("final_move=")) {
                if (lineParts[0].toLowerCase().endsWith("maxavgscore")) {
                    finalMove = new MaxAvgScore();
                    finalMove.customize(lineParts);
                }
                else if (lineParts[0].toLowerCase().endsWith("robustchild")) {
                    finalMove = new RobustChild();
                    finalMove.customize(lineParts);
                }
                else if (lineParts[0].toLowerCase().endsWith("proportional") || lineParts[0].toLowerCase().endsWith("proportionalexpvisitcount")) {
                    finalMove = new ProportionalExpVisitCount(1.0);
                    finalMove.customize(lineParts);
                }
                else {
                    System.err.println("Unknown final move selection strategy: " + line);
                }
            }
            else if (lineParts[0].toLowerCase().startsWith("tree_reuse=")) {
                if (lineParts[0].toLowerCase().endsWith("true")) {
                    treeReuse = true;
                }
                else if (lineParts[0].toLowerCase().endsWith("false")) {
                    treeReuse = false;
                }
                else {
                    System.err.println("Error in line: " + line);
                }
            }
            else if (lineParts[0].toLowerCase().startsWith("learned_selection_policy=")) {
                if (lineParts[0].toLowerCase().endsWith("playout")) {
                    learnedSelectionPolicy = (SoftmaxPolicy)playout;
                }
                else if (lineParts[0].toLowerCase().endsWith("softmax") || lineParts[0].toLowerCase().endsWith("softmaxplayout")) {
                    learnedSelectionPolicy = new SoftmaxPolicy();
                    learnedSelectionPolicy.customise(lineParts);
                }
            }
            else if (lineParts[0].toLowerCase().startsWith("friendly_name=")) {
                friendlyName = lineParts[0].substring("friendly_name=".length());
            }
        }
        final MCTS mcts = new MCTS(selection, playout, finalMove);
        mcts.setTreeReuse(treeReuse);
        mcts.setLearnedSelectionPolicy(learnedSelectionPolicy);
        mcts.friendlyName = friendlyName;
        return mcts;
    }
    
    public enum QInit
    {
        INF, 
        LOSS, 
        DRAW, 
        WIN, 
        PARENT
    }
}
