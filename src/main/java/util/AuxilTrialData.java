// 
// Decompiled by Procyon v0.5.36
// 

package util;

import game.rules.play.moves.Moves;
import gnu.trove.list.array.TIntArrayList;
import util.state.State;

import java.util.ArrayList;
import java.util.List;

public class AuxilTrialData
{
    protected transient boolean storeStates;
    protected transient boolean storeLegalMovesHistory;
    protected transient boolean storeLegalMovesHistorySizes;
    protected List<State> states;
    protected List<List<Move>> legalMovesHistory;
    protected TIntArrayList legalMovesHistorySizes;
    
    public AuxilTrialData() {
        this.storeStates = false;
        this.storeLegalMovesHistory = false;
        this.storeLegalMovesHistorySizes = false;
        this.states = null;
        this.legalMovesHistory = null;
        this.legalMovesHistorySizes = null;
    }
    
    public List<State> stateHistory() {
        return this.states;
    }
    
    public void saveState(final State state) {
        if (this.storeStates) {
            this.states.add(new State(state));
        }
    }
    
    public void storeStates() {
        if (!this.storeStates) {
            this.storeStates = true;
            this.states = new ArrayList<>();
        }
    }
    
    public void storeLegalMovesHistory() {
        if (!this.storeLegalMovesHistory) {
            this.storeLegalMovesHistory = true;
            this.legalMovesHistory = new ArrayList<>();
        }
    }
    
    public void storeLegalMovesHistorySizes() {
        if (!this.storeLegalMovesHistorySizes) {
            this.storeLegalMovesHistorySizes = true;
            this.legalMovesHistorySizes = new TIntArrayList();
        }
    }
    
    public void setLegalMovesHistory(final List<List<Move>> legalMovesHistory) {
        this.legalMovesHistory = legalMovesHistory;
    }
    
    public void setLegalMovesHistorySizes(final TIntArrayList legalMovesHistorySizes) {
        this.legalMovesHistorySizes = legalMovesHistorySizes;
    }
    
    public List<List<Move>> legalMovesHistory() {
        return this.legalMovesHistory;
    }
    
    public TIntArrayList legalMovesHistorySizes() {
        return this.legalMovesHistorySizes;
    }
    
    public void clear() {
        if (this.states != null) {
            this.states.clear();
        }
        if (this.legalMovesHistory != null) {
            this.legalMovesHistory.clear();
        }
        if (this.legalMovesHistorySizes != null) {
            this.legalMovesHistorySizes.clear();
        }
    }
    
    public void updateNewLegalMoves(final Moves legalMoves, final Context context) {
        final Trial trial = context.trial();
        if (this.storeLegalMovesHistory) {
            if (this.legalMovesHistory.size() == trial.moves().size() - trial.numInitialPlacementMoves() + 1) {
                this.legalMovesHistory.remove(this.legalMovesHistory.size() - 1);
            }
            if (this.legalMovesHistory.size() == trial.moves().size() - trial.numInitialPlacementMoves()) {
                final List<Move> historyList = new ArrayList<>();
                for (final Move move : legalMoves.moves()) {
                    final Move moveToAdd = new Move(move.getAllActions(context));
                    moveToAdd.setFromNonDecision(move.fromNonDecision());
                    moveToAdd.setToNonDecision(move.toNonDecision());
                    moveToAdd.setMover(move.mover());
                    historyList.add(moveToAdd);
                }
                this.legalMovesHistory.add(historyList);
            }
        }
        if (this.storeLegalMovesHistorySizes) {
            if (this.legalMovesHistorySizes.size() == trial.moves().size() - trial.numInitialPlacementMoves() + 1) {
                this.legalMovesHistorySizes.removeAt(this.legalMovesHistorySizes.size() - 1);
            }
            if (this.legalMovesHistorySizes.size() == trial.moves().size() - trial.numInitialPlacementMoves()) {
                this.legalMovesHistorySizes.add(legalMoves.moves().size());
            }
        }
    }
    
    public void updateFromSubtrial(final Trial subtrial) {
        if (this.storeLegalMovesHistory) {
            for (final List<Move> movesList : subtrial.auxilTrialData().legalMovesHistory()) {
                this.legalMovesHistory.add(new ArrayList<>(movesList));
            }
        }
        if (this.storeLegalMovesHistorySizes) {
            this.legalMovesHistorySizes.addAll(subtrial.auxilTrialData().legalMovesHistorySizes());
        }
    }
}
