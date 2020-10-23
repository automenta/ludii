// 
// Decompiled by Procyon v0.5.36
// 

package utils;

import collections.FastArrayList;
import game.equipment.container.Container;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.Move;
import util.Trial;
import util.state.State;
import util.state.containerStackingState.BaseContainerStateStacking;
import util.state.containerState.ContainerState;
import util.state.owned.Owned;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class LudiiStateWrapper
{
    protected final LudiiGameWrapper game;
    protected final Context context;
    protected final Trial trial;
    
    public LudiiStateWrapper(final LudiiGameWrapper game) {
        this.game = game;
        this.trial = new Trial(game.game);
        this.context = new Context(game.game, this.trial);
        game.game.start(this.context);
    }
    
    public LudiiStateWrapper(final LudiiStateWrapper other) {
        this.game = other.game;
        this.context = new Context(other.context);
        this.trial = this.context.trial();
    }
    
    public String actionToString(final int actionID, final int player) {
        FastArrayList<Move> legalMoves;
        if (this.game.isSimultaneousMoveGame()) {
            legalMoves = AIUtils.extractMovesForMover(this.game.game.moves(this.context).moves(), player + 1);
        }
        else {
            legalMoves = this.game.game.moves(this.context).moves();
        }
        final List<Move> moves = new ArrayList<>();
        for (final Move move : legalMoves) {
            if (this.game.moveToInt(move) == actionID) {
                moves.add(move);
            }
        }
        if (moves.isEmpty()) {
            return "[Ludii found no move for ID: " + actionID + "!]";
        }
        if (moves.size() == 1) {
            return moves.get(0).toTrialFormat(this.context);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("[Multiple Ludii moves for ID=").append(actionID).append(": ");
        sb.append(moves);
        sb.append("]");
        return sb.toString();
    }
    
    public void applyNthMove(final int n) {
        final FastArrayList<Move> legalMoves = this.game.game.moves(this.context).moves();
        final Move moveToApply = legalMoves.get(n);
        this.game.game.apply(this.context, moveToApply);
    }
    
    public void applyIntAction(final int action, final int player) {
        FastArrayList<Move> legalMoves;
        if (this.game.isSimultaneousMoveGame()) {
            legalMoves = AIUtils.extractMovesForMover(this.game.game.moves(this.context).moves(), player + 1);
        }
        else {
            legalMoves = this.game.game.moves(this.context).moves();
        }
        final List<Move> moves = new ArrayList<>();
        for (final Move move : legalMoves) {
            if (this.game.moveToInt(move) == action) {
                moves.add(move);
            }
        }
        this.game.game.apply(this.context, moves.get(ThreadLocalRandom.current().nextInt(moves.size())));
    }
    
    public LudiiStateWrapper clone() {
        return new LudiiStateWrapper(this);
    }
    
    public int currentPlayer() {
        return this.context.state().playerToAgent(this.context.state().mover()) - 1;
    }
    
    public boolean isTerminal() {
        return this.trial.over();
    }
    
    public long fullZobristHash() {
        return this.context.state().fullHash();
    }
    
    public void reset() {
        this.game.game.start(this.context);
    }
    
    public int[] legalMoveIndices() {
        final FastArrayList<Move> moves = this.game.game.moves(this.context).moves();
        final int[] indices = new int[moves.size()];
        for (int i = 0; i < indices.length; ++i) {
            indices[i] = i;
        }
        return indices;
    }
    
    public int numLegalMoves() {
        return Math.max(1, this.game.game.moves(this.context).moves().size());
    }
    
    public int[] legalMoveInts() {
        final FastArrayList<Move> moves = this.game.game.moves(this.context).moves();
        final TIntArrayList moveInts = new TIntArrayList(moves.size());
        for (final Move move : moves) {
            final int toAdd = this.game.moveToInt(move);
            if (!moveInts.contains(toAdd)) {
                moveInts.add(toAdd);
            }
        }
        moveInts.sort();
        return moveInts.toArray();
    }
    
    public int[] legalMoveIntsPlayer(final int player) {
        FastArrayList<Move> legalMoves;
        if (this.game.isSimultaneousMoveGame()) {
            legalMoves = AIUtils.extractMovesForMover(this.game.game.moves(this.context).moves(), player + 1);
        }
        else {
            legalMoves = this.game.game.moves(this.context).moves();
        }
        final TIntArrayList moveInts = new TIntArrayList(legalMoves.size());
        for (final Move move : legalMoves) {
            final int toAdd = this.game.moveToInt(move);
            if (!moveInts.contains(toAdd)) {
                moveInts.add(toAdd);
            }
        }
        moveInts.sort();
        return moveInts.toArray();
    }
    
    public int[][] legalMovesTensors() {
        final FastArrayList<Move> moves = this.game.game.moves(this.context).moves();
        int[][] movesTensors;
        if (moves.isEmpty()) {
            movesTensors = new int[][] { { this.game.MOVE_PASS_CHANNEL_IDX, 0, 0 } };
        }
        else {
            movesTensors = new int[moves.size()][];
            for (int i = 0; i < moves.size(); ++i) {
                movesTensors[i] = this.game.moveToTensor(moves.get(i));
            }
        }
        return movesTensors;
    }
    
    public double[] returns() {
        if (!this.isTerminal()) {
            return new double[this.game.numPlayers()];
        }
        final double[] returns = AIUtils.agentUtilities(this.context);
        return Arrays.copyOfRange(returns, 1, returns.length);
    }
    
    public double returns(final int player) {
        if (!this.isTerminal()) {
            return 0.0;
        }
        final double[] returns = AIUtils.agentUtilities(this.context);
        return returns[player + 1];
    }
    
    public void undoLastMove() {
        final List<Move> moves = new ArrayList<>(this.context.trial().moves());
        this.reset();
        for (int i = this.context.trial().numInitialPlacementMoves(); i < moves.size() - 1; ++i) {
            this.game.game.apply(this.context, moves.get(i));
        }
    }
    
    public float[] toTensorFlat() {
        final Container[] containers = this.game.game.equipment().containers();
        final int numPlayers = this.game.game.players().count();
        final int numPieceTypes = this.game.game.equipment().components().length - 1;
        final boolean stacking = this.game.game.isStacking();
        final boolean usesCount = this.game.game.requiresCount();
        final boolean usesAmount = this.game.game.requiresBet();
        final boolean usesState = this.game.game.requiresLocalState();
        final boolean usesSwap = this.game.game.usesSwapRule();
        final int[] xCoords = this.game.tensorCoordsX();
        final int[] yCoords = this.game.tensorCoordsY();
        final int tensorDimX = this.game.tensorDimX();
        final int tensorDimY = this.game.tensorDimY();
        final int numChannels = this.game.stateTensorNumChannels;
        final float[] flatTensor = new float[numChannels * tensorDimX * tensorDimY];
        int currentChannel = 0;
        if (!stacking) {
            final Owned owned = this.context.state().owned();
            for (int e = 1; e <= numPieceTypes; ++e) {
                for (int p = 1; p <= numPlayers + 1; ++p) {
                    final TIntArrayList sites = owned.sites(p, e);
                    for (int i = 0; i < sites.size(); ++i) {
                        final int site = sites.getQuick(i);
                        flatTensor[yCoords[site] + tensorDimY * (xCoords[site] + currentChannel * tensorDimX)] = 1.0f;
                    }
                }
                ++currentChannel;
            }
        }
        else {
            for (int c = 0; c < containers.length; ++c) {
                final Container cont = containers[c];
                final BaseContainerStateStacking cs = (BaseContainerStateStacking)this.context.state().containerStates()[c];
                final int contStartSite = this.game.game.equipment().sitesFrom()[c];
                for (int site2 = 0; site2 < cont.numSites(); ++site2) {
                    final int stackSize = cs.sizeStackCell(contStartSite + site2);
                    if (stackSize > 0) {
                        for (int j = 0; j < 5 && j < stackSize; ++j) {
                            final int what = cs.whatCell(contStartSite + site2, j);
                            final int channel = currentChannel + ((what - 1) * 10 + j);
                            flatTensor[yCoords[contStartSite + site2] + tensorDimY * (xCoords[contStartSite + site2] + channel * tensorDimX)] = 1.0f;
                        }
                        for (int j = 0; j < 5 && j < stackSize; ++j) {
                            final int what = cs.whatCell(contStartSite + site2, stackSize - 1 - j);
                            final int channel = currentChannel + ((what - 1) * 10 + 5 + j);
                            flatTensor[yCoords[contStartSite + site2] + tensorDimY * (xCoords[contStartSite + site2] + channel * tensorDimX)] = 1.0f;
                        }
                        final int channel2 = currentChannel + 10 * numPieceTypes;
                        flatTensor[yCoords[contStartSite + site2] + tensorDimY * (xCoords[contStartSite + site2] + channel2 * tensorDimX)] = stackSize;
                    }
                }
            }
            currentChannel += 10 * numPieceTypes + 1;
        }
        if (usesCount) {
            for (int c = 0; c < containers.length; ++c) {
                final Container cont = containers[c];
                final ContainerState cs2 = this.context.state().containerStates()[c];
                final int contStartSite = this.game.game.equipment().sitesFrom()[c];
                for (int site2 = 0; site2 < cont.numSites(); ++site2) {
                    flatTensor[yCoords[contStartSite + site2] + tensorDimY * (xCoords[contStartSite + site2] + currentChannel * tensorDimX)] = cs2.countCell(contStartSite + site2);
                }
            }
            ++currentChannel;
        }
        if (usesAmount) {
            for (int p2 = 1; p2 <= numPlayers; ++p2) {
                final int amount = this.context.state().amount(p2);
                final int startFill = tensorDimY * currentChannel * tensorDimX;
                final int endFill = startFill + tensorDimY * tensorDimX;
                Arrays.fill(flatTensor, startFill, endFill, amount);
                ++currentChannel;
            }
        }
        if (numPlayers > 1) {
            final int mover = this.context.state().playerToAgent(this.context.state().mover());
            final int startFill2 = tensorDimY * (currentChannel + mover - 1) * tensorDimX;
            System.arraycopy(this.game.allOnesChannelFlat(), 0, flatTensor, startFill2, tensorDimY * tensorDimX);
            currentChannel += numPlayers;
        }
        if (usesState) {
            for (int c = 0; c < containers.length; ++c) {
                final Container cont = containers[c];
                final int contStartSite2 = this.game.game.equipment().sitesFrom()[c];
                final ContainerState cs3 = this.context.state().containerStates()[c];
                for (int site2 = 0; site2 < cont.numSites(); ++site2) {
                    final int state = Math.min(cs3.stateCell(contStartSite2 + site2), 5);
                    flatTensor[yCoords[contStartSite2 + site2] + tensorDimY * (xCoords[contStartSite2 + site2] + (currentChannel + state) * tensorDimX)] = 1.0f;
                }
            }
            currentChannel += 6;
        }
        if (usesSwap) {
            if (this.context.state().orderHasChanged()) {
                final int startFill3 = tensorDimY * currentChannel * tensorDimX;
                System.arraycopy(this.game.allOnesChannelFlat(), 0, flatTensor, startFill3, tensorDimY * tensorDimX);
            }
            ++currentChannel;
        }
        final int startFill3 = tensorDimY * currentChannel * tensorDimX;
        System.arraycopy(this.game.containerPositionChannels(), 0, flatTensor, startFill3, containers.length * tensorDimY * tensorDimX);
        currentChannel += containers.length;
        if (this.trial.moves().size() - this.trial.numInitialPlacementMoves() > 0) {
            final Move lastMove = this.trial.moves().get(this.trial.moves().size() - 1);
            final int from = lastMove.fromNonDecision();
            if (from != -1) {
                flatTensor[yCoords[from] + tensorDimY * (xCoords[from] + currentChannel * tensorDimX)] = 1.0f;
            }
            ++currentChannel;
            final int to = lastMove.toNonDecision();
            if (to != -1) {
                flatTensor[yCoords[to] + tensorDimY * (xCoords[to] + currentChannel * tensorDimX)] = 1.0f;
            }
            ++currentChannel;
        }
        else {
            currentChannel += 2;
        }
        if (this.trial.moves().size() - this.trial.numInitialPlacementMoves() > 1) {
            final Move lastLastMove = this.trial.moves().get(this.trial.moves().size() - 2);
            final int from = lastLastMove.fromNonDecision();
            if (from != -1) {
                flatTensor[yCoords[from] + tensorDimY * (xCoords[from] + currentChannel * tensorDimX)] = 1.0f;
            }
            ++currentChannel;
            final int to = lastLastMove.toNonDecision();
            if (to != -1) {
                flatTensor[yCoords[to] + tensorDimY * (xCoords[to] + currentChannel * tensorDimX)] = 1.0f;
            }
            ++currentChannel;
        }
        else {
            currentChannel += 2;
        }
        assert currentChannel == numChannels;
        return flatTensor;
    }
    
    public float[][][] toTensor() {
        final int tensorDimX = this.game.tensorDimX();
        final int tensorDimY = this.game.tensorDimY();
        final int numChannels = this.game.stateTensorNumChannels;
        final float[] flatTensor = this.toTensorFlat();
        final float[][][] tensor = new float[numChannels][tensorDimX][tensorDimY];
        for (int c = 0; c < numChannels; ++c) {
            for (int x = 0; x < tensorDimX; ++x) {
                System.arraycopy(flatTensor, tensorDimY * (x + c * tensorDimX), tensor[c][x], 0, tensorDimY);
            }
        }
        return tensor;
    }
    
    public Trial trial() {
        return this.trial;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final State state = this.context.state();
        sb.append("BEGIN LUDII STATE\n");
        sb.append("Mover colour = ").append(state.mover()).append("\n");
        sb.append("Mover player/agent = ").append(state.playerToAgent(state.mover())).append("\n");
        sb.append("Next = ").append(state.next()).append("\n");
        sb.append("Previous = ").append(state.prev()).append("\n");
        for (int p = 1; p <= state.numPlayers(); ++p) {
            sb.append("Player ").append(p).append(" active = ").append(this.context.active(p)).append("\n");
        }
        sb.append("State hash = ").append(state.stateHash()).append("\n");
        if (this.game.game.requiresScore()) {
            for (int p = 1; p <= state.numPlayers(); ++p) {
                sb.append("Player ").append(p).append(" score = ").append(this.context.score(p)).append("\n");
            }
        }
        for (int p = 1; p <= state.numPlayers(); ++p) {
            sb.append("Player ").append(p).append(" ranking = ").append(this.context.trial().ranking()[p]).append("\n");
        }
        for (int i = 0; i < state.containerStates().length; ++i) {
            final ContainerState cs = state.containerStates()[i];
            sb.append("BEGIN CONTAINER STATE ").append(i).append("\n");
            sb.append(cs.toString()).append("\n");
            sb.append("END CONTAINER STATE ").append(i).append("\n");
        }
        sb.append("END LUDII GAME STATE\n");
        return sb.toString();
    }
}
