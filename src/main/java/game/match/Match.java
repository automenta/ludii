// 
// Decompiled by Procyon v0.5.36
// 

package game.match;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.equipment.Equipment;
import game.equipment.container.board.Board;
import game.equipment.container.other.Dice;
import game.players.Players;
import game.rules.end.End;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import collections.FVector;
import grammar.Description;
import util.*;
import util.action.others.ActionNextInstance;

import java.util.List;
import java.util.Random;

public class Match extends Game
{
    private static final long serialVersionUID = 1L;
    private End end;
    protected Subgame[] instances;
    
    public Match(final String name, @Opt final Players players, final Games games, final End end) {
        super(name, players, null, null, null);
        final List<Subgame> subgames = games.games();
        this.instances = new Subgame[subgames.size()];
        for (int i = 0; i < subgames.size(); ++i) {
            this.instances[i] = subgames.get(i);
        }
        if (this.instances.length == 0) {
            throw new IllegalArgumentException("A match needs at least one game.");
        }
        this.stateReference = null;
        (this.end = end).setMatch(true);
    }
    
    @Hide
    public Match(final String name, final Description gameDescription) {
        super(name, gameDescription);
    }
    
    @Override
    public Move apply(final Context context, final Move move) {
        context.lock().lock();
        try {
            if (!move.containsNextInstance()) {
                final Context subcontext = context.subcontext();
                final Trial subtrial = subcontext.trial();
                final int numMovesBeforeApply = subtrial.numMoves();
                final Move appliedMove = subcontext.game().apply(subcontext, move);
                final List<Move> subtrialMoves = subtrial.moves();
                final int numMovesAfterApply = subtrialMoves.size();
                for (int numMovesToAppend = numMovesAfterApply - numMovesBeforeApply, i = 0; i < numMovesToAppend; ++i) {
                    context.trial().moves().add(subtrialMoves.get(subtrialMoves.size() - 1 - i));
                }
                return appliedMove;
            }
            assert context.subcontext().trial().over();
            assert move.actions().size() == 1 && move.actions().get(0) instanceof ActionNextInstance;
            context.currentInstanceContext().trial().moves().add(move);
            context.trial().moves().add(move);
            context.advanceInstance();
            return move;
        }
        finally {
            context.lock().unlock();
        }
    }
    
    @Override
    public Moves moves(final Context context) {
        context.lock().lock();
        try {
            final Context subcontext = context.subcontext();
            Moves moves;
            if (subcontext.trial().over()) {
                moves = new BaseMoves(null);
                if (context.trial().over()) {
                    return moves;
                }
                final ActionNextInstance action = new ActionNextInstance();
                action.setDecision(true);
                final Move move = new Move(action);
                move.setDecision(true);
                move.setMover(subcontext.state().mover());
                moves.moves().add(move);
            }
            else {
                moves = subcontext.game().moves(subcontext);
            }
            if (context.trial().auxilTrialData() != null) {
                context.trial().auxilTrialData().updateNewLegalMoves(moves, context);
            }
            return moves;
        }
        finally {
            context.lock().unlock();
        }
    }
    
    @Override
    public End endRules() {
        return this.end;
    }
    
    @Override
    public Subgame[] instances() {
        return this.instances;
    }
    
    @Override
    public boolean hasSubgames() {
        return true;
    }
    
    @Override
    public boolean hasCustomPlayouts() {
        for (final Subgame instance : this.instances) {
            if (instance.getGame().hasCustomPlayouts()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void disableMemorylessPlayouts() {
        for (final Subgame instance : this.instances) {
            instance.disableMemorylessPlayouts();
        }
    }
    
    @Override
    public boolean usesStateComparison() {
        return false;
    }
    
    @Override
    public boolean usesStateComparisonWithinATurn() {
        return false;
    }
    
    @Override
    public boolean requiresScore() {
        return true;
    }
    
    @Override
    public boolean automove() {
        return false;
    }
    
    @Override
    public Board board() {
        System.err.println("Match.board() always returns null! Should probably call context.board() instead.");
        return null;
    }
    
    @Override
    public Equipment equipment() {
        System.err.println("Match.equipment() always returns null! Should probably call context.equipment() instead.");
        return null;
    }
    
    @Override
    public boolean hasSharedPlayer() {
        System.err.println("Match.hasSharedPlayer() always returns false! Should probably call context.hasSharedPlayer() instead.");
        return false;
    }
    
    @Override
    public List<Dice> handDice() {
        System.err.println("Match.handDice() always returns null! Should probably call context.handDice() instead.");
        return null;
    }
    
    @Override
    public int numContainers() {
        System.err.println("Match.numContainers() always returns -1! Should probably call context.numContainers() instead.");
        return -1;
    }
    
    @Override
    public int numComponents() {
        System.err.println("Match.numComponents() always returns -1! Should probably call context.numComponents() instead.");
        return -1;
    }
    
    @Override
    public void create() {
        if (this.finishedPreprocessing) {
            System.err.println("Warning! Match.create() has already previously been called on " + this.name());
        }
        GameLoader.compileInstance(this.instances()[0]);
        this.finishedPreprocessing = true;
    }
    
    @Override
    public void start(final Context context) {
        context.lock().lock();
        try {
            final Context subcontext = context.subcontext();
            final Trial subtrial = subcontext.trial();
            final int numMovesBeforeStart = subtrial.numMoves();
            this.instances()[0].getGame().start(subcontext);
            final List<Move> subtrialMoves = subtrial.moves();
            final int numMovesAfterStart = subtrialMoves.size();
            for (int numMovesToAppend = numMovesAfterStart - numMovesBeforeStart, i = 0; i < numMovesToAppend; ++i) {
                context.trial().moves().add(subtrialMoves.get(subtrialMoves.size() - 1 - i));
            }
            if (!context.trial().over() && context.game().isStochasticGame()) {
                context.game().moves(context);
            }
        }
        finally {
            context.lock().unlock();
        }
    }
    
    @Override
    public Trial playout(final Context context, final List<AI> ais, final double thinkingTime, final FeatureSetInterface[] featureSets, final FVector[] weights, final int maxNumBiasedActions, final int maxNumPlayoutActions, final float autoPlayThreshold, final Random random) {
        return context.model().playout(context, ais, thinkingTime, featureSets, weights, maxNumBiasedActions, maxNumPlayoutActions, autoPlayThreshold, random);
    }
    
    @Override
    public boolean isGraphGame() {
        System.err.println("Match.isGraphGame() always returns false! Should probably call context.isGraphGame() instead.");
        return false;
    }
    
    @Override
    public boolean isVertexGame() {
        System.err.println("Match.isVertexGame() always returns false! Should probably call context.isVertexGame() instead.");
        return false;
    }
    
    @Override
    public boolean isEdgeGame() {
        System.err.println("Match.isEdgeGame() always returns false! Should probably call context.isEdgeGame() instead.");
        return false;
    }
    
    @Override
    public boolean isCellGame() {
        System.err.println("Match.isCellGame() always returns false! Should probably call context.isCellGame() instead.");
        return false;
    }
}
