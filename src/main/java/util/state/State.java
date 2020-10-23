// 
// Decompiled by Procyon v0.5.36
// 

package util.state;

import annotations.Hide;
import game.Game;
import game.equipment.container.Container;
import game.equipment.container.other.Dice;
import game.rules.phase.Phase;
import game.types.board.SiteType;
import game.types.play.ModeType;
import game.types.play.RoleType;
import game.util.equipment.Region;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TIntHashSet;
import main.collections.FastTIntArrayList;
import util.state.containerState.ContainerState;
import util.state.containerState.ContainerStateFactory;
import util.state.onTrack.OnTrackIndices;
import util.state.owned.Owned;
import util.state.owned.OwnedFactory;
import util.symmetry.SymmetryValidator;
import util.zhash.HashedBitSet;
import util.zhash.ZobristHashGenerator;
import util.zhash.ZobristHashUtilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Hide
public class State implements Serializable
{
    private static final int TURN_MAX_HASH = 1024;
    private static final int SCORE_MAX_HASH = 1024;
    private static final int AMOUNT_MAX_HASH = 1024;
    private static final long serialVersionUID = 1L;
    private int numPlayers;
    private int mover;
    private int next;
    private int prev;
    private int triggered;
    private int stalemated;
    private final ContainerState[] containerStates;
    private int counter;
    private int tempValue;
    private TIntHashSet pendingValues;
    private int[] amount;
    private int moneyPot;
    private int[] currentPhase;
    private int[] sumDice;
    private int[][] currentDice;
    private boolean diceAllEqual;
    private int numTurnSamePlayer;
    private int numTurn;
    private int trumpSuit;
    private final List<String> propositions;
    private final List<String> votes;
    private TIntIntHashMap valuesPlayer;
    private String isDecided;
    private TIntObjectMap<TIntObjectMap<String>> notes;
    protected transient Owned owned;
    private transient OnTrackIndices onTrackIndices;
    private HashedBitSet visited;
    private HashedBitSet pieceToRemove;
    private int[] teams;
    private int[] playerOrder;
    private FastTIntArrayList remainingDominoes;
    private long storedState;
    private long stateHash;
    private long moverHash;
    private long nextHash;
    private long prevHash;
    private long activeHash;
    private long checkmatedHash;
    private long stalematedHash;
    private long pendingHash;
    private long scoreHash;
    private long amountHash;
    private final long[] moverHashes;
    private final long[] nextHashes;
    private final long[] prevHashes;
    private final long[] activeHashes;
    private final long[] checkmatedHashes;
    private final long[] stalematedHashes;
    private final long[][] lowScoreHashes;
    private final long[][] highScoreHashes;
    private final long[][] lowAmountHashes;
    private final long[][] highAmountHashes;
    private final long[][] phaseHashes;
    private final long[] isPendingHashes;
    private final long[] tempHashes;
    private final long[][] playerOrderHashes;
    private final long[][] consecutiveTurnHashes;
    private final long[][] playerSwitchHashes;
    private final long[][] teamHashes;
    
    public void updateStateHash(final long delta) {
        this.stateHash ^= delta;
    }
    
    public long canonicalHash(final SymmetryValidator validator, final boolean whoOnly) {
        final ContainerState boardState = this.containerStates[0];
        final long canonicalBoardHash = boardState.canonicalHash(validator, this, whoOnly);
        return (canonicalBoardHash == 0L) ? this.stateHash : canonicalBoardHash;
    }
    
    @Deprecated
    public long canonicalHash(final SymmetryValidator validator) {
        return this.canonicalHash(validator, false);
    }
    
    public long stateHash() {
        return this.stateHash;
    }
    
    public long pendingHash() {
        return this.pendingHash;
    }
    
    public long consecutiveTurnHash() {
        return (this.numTurnSamePlayer < 16) ? this.consecutiveTurnHashes[0][this.numTurnSamePlayer] : this.consecutiveTurnHashes[1][this.numTurnSamePlayer % 16];
    }
    
    public long playerNumSwitchesHash() {
        return (this.numTurn < 1024) ? this.playerSwitchHashes[0][this.numTurn] : this.playerSwitchHashes[1][this.numTurn % 1024];
    }
    
    public long fullHash() {
        return this.moverHash ^ this.nextHash ^ this.prevHash ^ this.activeHash ^ this.checkmatedHash ^ this.stalematedHash ^ this.pendingHash() ^ this.stateHash() ^ this.consecutiveTurnHash() ^ this.scoreHash ^ this.amountHash;
    }
    
    public State(final Game game, final Game.StateConstructorLock stateConstructorLock) {
        this.numPlayers = 0;
        this.mover = 0;
        this.next = 0;
        this.prev = 0;
        this.triggered = 0;
        this.stalemated = 0;
        this.counter = -1;
        this.tempValue = -1;
        this.pendingValues = new TIntHashSet();
        this.amount = null;
        this.moneyPot = 0;
        this.diceAllEqual = false;
        this.numTurnSamePlayer = 0;
        this.numTurn = 1;
        this.trumpSuit = -1;
        this.valuesPlayer = new TIntIntHashMap();
        this.isDecided = "";
        this.notes = null;
        this.visited = null;
        this.pieceToRemove = null;
        this.teams = null;
        this.storedState = 0L;
        Objects.requireNonNull(stateConstructorLock, "Only Game.java should call this constructor! Other callers can copy the game's stateReference instead using the copy constructor.");
        this.numPlayers = game.players().count();
        final ZobristHashGenerator generator = ZobristHashUtilities.getHashGenerator();
        this.lowScoreHashes = ((game.gameFlags() & 0x100000L) != 0x0L) ? ZobristHashUtilities.getSequence(generator, this.numPlayers + 1, 1025) : null;
        this.highScoreHashes = ((game.gameFlags() & 0x100000L) != 0x0L) ? ZobristHashUtilities.getSequence(generator, this.numPlayers + 1, 1025) : null;
        this.lowAmountHashes = ((game.gameFlags() & 0x200000L) != 0x0L) ? ZobristHashUtilities.getSequence(generator, this.numPlayers + 1, 1025) : null;
        this.highAmountHashes = ((game.gameFlags() & 0x200000L) != 0x0L) ? ZobristHashUtilities.getSequence(generator, this.numPlayers + 1, 1025) : null;
        this.phaseHashes = ((game.gameFlags() & 0x400000L) != 0x0L) ? ZobristHashUtilities.getSequence(generator, this.numPlayers + 1, 17) : null;
        this.moverHashes = ZobristHashUtilities.getSequence(generator, this.numPlayers + 1);
        this.nextHashes = ZobristHashUtilities.getSequence(generator, this.numPlayers + 1);
        this.prevHashes = ZobristHashUtilities.getSequence(generator, this.numPlayers + 1);
        this.activeHashes = ZobristHashUtilities.getSequence(generator, this.numPlayers + 1);
        this.checkmatedHashes = ZobristHashUtilities.getSequence(generator, this.numPlayers + 1);
        this.stalematedHashes = ZobristHashUtilities.getSequence(generator, this.numPlayers + 1);
        this.tempHashes = ZobristHashUtilities.getSequence(generator, game.equipment().totalDefaultSites() + 3);
        this.playerOrderHashes = ZobristHashUtilities.getSequence(generator, this.numPlayers + 1, this.numPlayers + 1);
        this.consecutiveTurnHashes = ZobristHashUtilities.getSequence(generator, 2, 16);
        this.playerSwitchHashes = ZobristHashUtilities.getSequence(generator, 2, 1024);
        this.teamHashes = game.requiresTeams() ? ZobristHashUtilities.getSequence(generator, game.players().count() + 1, 16) : null;
        this.playerOrder = new int[game.players().count() + 1];
        for (int i = 1; i < this.playerOrder.length; ++i) {
            this.playerOrder[i] = i;
            this.updateStateHash(this.playerOrderHashes[i][this.playerOrder[i]]);
        }
        assert !game.hasSubgames();
        this.moneyPot = 0;
        this.isPendingHashes = ZobristHashUtilities.getSequence(generator, game.equipment().totalDefaultSites() + 2);
        this.stateHash = 0L;
        this.scoreHash = 0L;
        this.amountHash = 0L;
        this.containerStates = new ContainerState[game.equipment().containers().length];
        if (game.requiresBet()) {
            this.amount = new int[this.numPlayers + 1];
        }
        int id = 0;
        for (final Container container : game.equipment().containers()) {
            this.containerStates[id++] = ContainerStateFactory.createStateForContainer(generator, game, container);
        }
        this.initPhase(game);
        if (game.hasHandDice()) {
            this.sumDice = new int[game.handDice().size()];
            this.currentDice = new int[game.handDice().size()][];
            for (int j = 0; j < game.handDice().size(); ++j) {
                final Dice d = game.handDice().get(j);
                this.currentDice[j] = new int[d.numLocs()];
            }
        }
        this.owned = OwnedFactory.createOwned(game);
        if (game.requiresVisited()) {
            this.visited = new HashedBitSet(generator, game.board().numSites());
        }
        if (game.hasSequenceCapture()) {
            this.pieceToRemove = new HashedBitSet(generator, game.board().numSites());
        }
        if (game.requiresTeams()) {
            this.teams = new int[game.players().size()];
        }
        if (game.usesVote()) {
            this.propositions = new ArrayList<>();
            this.votes = new ArrayList<>();
        }
        else {
            this.propositions = null;
            this.votes = null;
        }
        if (game.usesNote()) {
            this.notes = new TIntObjectHashMap<>();
        }
        if (game.hasTrack() && game.hasInternalLoopInTrack()) {
            this.onTrackIndices = new OnTrackIndices(game.board().tracks(), game.equipment().components().length);
        }
        if (game.hasDominoes()) {
            this.remainingDominoes = new FastTIntArrayList();
        }
    }
    
    public State(final State other) {
        this.numPlayers = 0;
        this.mover = 0;
        this.next = 0;
        this.prev = 0;
        this.triggered = 0;
        this.stalemated = 0;
        this.counter = -1;
        this.tempValue = -1;
        this.pendingValues = new TIntHashSet();
        this.amount = null;
        this.moneyPot = 0;
        this.diceAllEqual = false;
        this.numTurnSamePlayer = 0;
        this.numTurn = 1;
        this.trumpSuit = -1;
        this.valuesPlayer = new TIntIntHashMap();
        this.isDecided = "";
        this.notes = null;
        this.visited = null;
        this.pieceToRemove = null;
        this.teams = null;
        this.storedState = 0L;
        this.lowScoreHashes = other.lowScoreHashes;
        this.highScoreHashes = other.highScoreHashes;
        this.lowAmountHashes = other.lowAmountHashes;
        this.highAmountHashes = other.highAmountHashes;
        this.phaseHashes = other.phaseHashes;
        this.isPendingHashes = other.isPendingHashes;
        this.moverHashes = other.moverHashes;
        this.nextHashes = other.nextHashes;
        this.prevHashes = other.prevHashes;
        this.activeHashes = other.activeHashes;
        this.checkmatedHashes = other.checkmatedHashes;
        this.stalematedHashes = other.stalematedHashes;
        this.tempHashes = other.tempHashes;
        this.playerOrderHashes = other.playerOrderHashes;
        this.consecutiveTurnHashes = other.consecutiveTurnHashes;
        this.playerSwitchHashes = other.playerSwitchHashes;
        this.teamHashes = other.teamHashes;
        this.playerOrder = Arrays.copyOf(other.playerOrder, other.playerOrder.length);
        this.moneyPot = other.moneyPot;
        this.numPlayers = other.numPlayers;
        this.stateHash = other.stateHash;
        this.moverHash = other.moverHash;
        this.nextHash = other.nextHash;
        this.prevHash = other.prevHash;
        this.activeHash = other.activeHash;
        this.checkmatedHash = other.checkmatedHash;
        this.stalematedHash = other.stalematedHash;
        this.pendingHash = other.pendingHash;
        this.scoreHash = other.scoreHash;
        this.amountHash = other.amountHash;
        this.trumpSuit = other.trumpSuit;
        this.mover = other.mover;
        this.next = other.next;
        this.prev = other.prev;
        this.triggered = other.triggered;
        this.stalemated = other.stalemated;
        if (other.containerStates == null) {
            this.containerStates = null;
        }
        else {
            this.containerStates = new ContainerState[other.containerStates.length];
            for (int is = 0; is < this.containerStates.length; ++is) {
                if (other.containerStates[is] == null) {
                    this.containerStates[is] = null;
                }
                else {
                    this.containerStates[is] = other.containerStates[is].deepClone();
                }
            }
        }
        this.setCounter(other.counter);
        this.setTemp(other.tempValue);
        this.pendingValues = new TIntHashSet(other.pendingValues());
        if (other.amount != null) {
            this.amount = Arrays.copyOf(other.amount, other.amount.length);
        }
        if (other.currentPhase != null) {
            this.currentPhase = Arrays.copyOf(other.currentPhase, other.currentPhase.length);
        }
        if (other.sumDice != null) {
            this.sumDice = Arrays.copyOf(other.sumDice, other.sumDice.length);
        }
        if (other.currentDice != null) {
            this.currentDice = new int[other.currentDice.length][];
            for (int i = 0; i < this.currentDice.length; ++i) {
                this.currentDice[i] = Arrays.copyOf(other.currentDice[i], other.currentDice[i].length);
            }
        }
        if (other.visited != null) {
            this.visited = other.visited.clone();
        }
        if (other.pieceToRemove != null) {
            this.pieceToRemove = other.pieceToRemove.clone();
        }
        if (other.teams != null) {
            this.teams = Arrays.copyOf(other.teams, other.teams.length);
        }
        if (other.votes != null) {
            this.votes = new ArrayList<>(other.votes);
            this.propositions = new ArrayList<>(other.propositions);
            this.isDecided = other.isDecided;
        }
        else {
            this.votes = null;
            this.propositions = null;
            this.isDecided = other.isDecided;
        }
        this.valuesPlayer = new TIntIntHashMap(other.valuesPlayer);
        if (other.notes != null) {
            this.notes = new TIntObjectHashMap<>(other.notes);
        }
        this.numTurnSamePlayer = other.numTurnSamePlayer;
        this.numTurn = other.numTurn;
        if (other.owned == null) {
            this.owned = null;
        }
        else {
            this.owned = other.owned.copy();
        }
        this.diceAllEqual = other.diceAllEqual;
        this.onTrackIndices = this.copyOnTrackIndices(other.onTrackIndices);
        if (other.remainingDominoes != null) {
            this.remainingDominoes = new FastTIntArrayList(other.remainingDominoes);
        }
        this.storedState = other.storedState;
    }
    
    public ContainerState[] containerStates() {
        return this.containerStates;
    }
    
    public int numPlayers() {
        return this.numPlayers;
    }
    
    public int mover() {
        return this.mover;
    }
    
    public void setMover(final int who) {
        this.moverHash ^= this.moverHashes[this.mover];
        this.mover = who;
        this.moverHash ^= this.moverHashes[this.mover];
    }
    
    public int next() {
        return this.next;
    }
    
    public void setNext(final int who) {
        this.nextHash ^= this.nextHashes[this.next];
        this.next = who;
        this.nextHash ^= this.nextHashes[this.next];
    }
    
    public int prev() {
        return this.prev;
    }
    
    public void setPrev(final int who) {
        this.prevHash ^= this.prevHashes[this.prev];
        this.prev = who;
        this.prevHash ^= this.prevHashes[this.prev];
    }
    
    public int setActive(final int who, final boolean newActive, final int active) {
        int ret = active;
        final int whoBit = 1 << who - 1;
        final boolean wasActive = (active & whoBit) != 0x0;
        if (wasActive && !newActive) {
            this.activeHash ^= this.activeHashes[who];
            ret &= ~whoBit;
        }
        else if (!wasActive && newActive) {
            this.activeHash ^= this.activeHashes[who];
            ret |= whoBit;
        }
        return ret;
    }
    
    public void updateHashAllPlayersInactive() {
        this.activeHash = 0L;
    }
    
    public boolean isTriggered(final String event, final int who) {
        return (this.triggered & 1 << who - 1) != 0x0;
    }
    
    public void triggers(final int who, final boolean triggerValue) {
        final int whoBit = 1 << who - 1;
        final boolean wasCheckmated = (this.triggered & whoBit) != 0x0;
        if (wasCheckmated && !triggerValue) {
            this.checkmatedHash ^= this.checkmatedHashes[who];
            this.triggered &= ~whoBit;
        }
        else if (!wasCheckmated && triggerValue) {
            this.checkmatedHash ^= this.checkmatedHashes[who];
            this.triggered |= whoBit;
        }
    }
    
    public void clearTriggers() {
        this.checkmatedHash = 0L;
        this.triggered = 0;
    }
    
    public boolean isStalemated(final int who) {
        return (this.stalemated & 1 << who - 1) != 0x0;
    }
    
    public void setStalemated(final int who, final boolean newStalemated) {
        final int whoBit = 1 << who - 1;
        final boolean wasStalemated = (this.stalemated & whoBit) != 0x0;
        if (wasStalemated && !newStalemated) {
            this.stalematedHash ^= this.stalematedHashes[who];
            this.stalemated &= ~whoBit;
        }
        else if (!wasStalemated && newStalemated) {
            this.stalematedHash ^= this.stalematedHashes[who];
            this.stalemated |= whoBit;
        }
    }
    
    public void clearStalemates() {
        this.stalematedHash = 0L;
        this.stalemated = 0;
    }
    
    public int playerToAgent(final int playerIdx) {
        for (int p = 0; p < this.playerOrder.length; ++p) {
            if (this.playerOrder[p] == playerIdx) {
                return p;
            }
        }
        if (playerIdx >= this.playerOrder.length) {
            return playerIdx;
        }
        return -1;
    }
    
    public void resetStateTo(final State other, final Game game) {
        assert !game.hasSubgames();
        this.mover = other.mover;
        this.next = other.next;
        this.prev = other.prev;
        this.moneyPot = other.moneyPot;
        this.triggered = other.triggered;
        this.stalemated = other.stalemated;
        if (this.containerStates.length != other.containerStates.length) {
            throw new UnsupportedOperationException("Number of state items should be invariant.");
        }
        for (int is = 0; is < this.containerStates.length; ++is) {
            this.containerStates[is] = ((other.containerStates[is] == null) ? null : other.containerStates[is].deepClone());
        }
        this.setCounter(other.counter);
        this.setTemp(other.tempValue);
        this.pendingValues = new TIntHashSet(other.pendingValues());
        if (this.amount != null) {
            for (int index = 0; index < other.amount.length; ++index) {
                this.amount[index] = other.amount[index];
            }
        }
        this.moverHash = other.moverHash;
        this.nextHash = other.nextHash;
        this.prevHash = other.prevHash;
        this.activeHash = other.activeHash;
        this.checkmatedHash = other.checkmatedHash;
        this.stalematedHash = other.stalematedHash;
        this.pendingHash = other.pendingHash;
        this.stateHash = other.stateHash;
        this.playerOrder = Arrays.copyOf(other.playerOrder, other.playerOrder.length);
        if (other.currentPhase != null) {
            this.currentPhase = Arrays.copyOf(other.currentPhase, other.currentPhase.length);
        }
        if (this.sumDice != null) {
            this.sumDice = Arrays.copyOf(other.sumDice, other.sumDice.length);
        }
        if (this.currentDice != null) {
            this.currentDice = new int[other.currentDice.length][];
            for (int i = 0; i < this.currentDice.length; ++i) {
                this.currentDice[i] = Arrays.copyOf(other.currentDice[i], other.currentDice[i].length);
            }
        }
        this.owned = other.owned.copy();
        if (this.visited != null) {
            this.visited.clear(this);
        }
        if (this.pieceToRemove != null) {
            this.pieceToRemove.clear(this);
        }
        if (this.votes != null) {
            this.votes.clear();
        }
        if (this.propositions != null) {
            this.propositions.clear();
        }
        this.isDecided = "";
        if (other.teams != null) {
            this.teams = Arrays.copyOf(other.teams, other.teams.length);
        }
        this.numTurnSamePlayer = other.numTurnSamePlayer;
        this.numTurn = other.numTurn;
        this.trumpSuit = other.trumpSuit;
        this.valuesPlayer = new TIntIntHashMap(other.valuesPlayer);
        if (this.notes != null) {
            this.notes = new TIntObjectHashMap<>(other.notes);
        }
        if (game.isBoardless()) {
            this.containerStates[0].setPlayable(this, game.board().topology().centre(SiteType.Cell).get(0).index(), true);
        }
        this.diceAllEqual = other.diceAllEqual;
        this.onTrackIndices = this.copyOnTrackIndices(other.onTrackIndices);
        if (this.remainingDominoes != null) {
            this.remainingDominoes.clear();
        }
        this.storedState = 0L;
    }
    
    protected OnTrackIndices copyOnTrackIndices(final OnTrackIndices otherOnTrackIndices) {
        return (otherOnTrackIndices == null) ? null : new OnTrackIndices(otherOnTrackIndices);
    }
    
    public void initialise(final Game game) {
        this.moverHash = 0L;
        this.nextHash = 0L;
        this.prevHash = 0L;
        this.activeHash = 0L;
        this.checkmatedHash = 0L;
        this.stalematedHash = 0L;
        this.pendingHash = 0L;
        this.stateHash = 0L;
        this.scoreHash = 0L;
        this.amountHash = 0L;
        this.mover = 0;
        this.next = 0;
        this.prev = 0;
        this.triggered = 0;
        this.stalemated = 0;
        this.moneyPot = 0;
        this.numPlayers = game.players().count();
        if (game.mode().mode() != ModeType.Simulation) {
            this.setMover(1);
            if (this.numPlayers > 1) {
                this.setNext(2);
            }
            else {
                this.setNext(1);
            }
            this.setPrev(0);
        }
        else {
            this.setMover(0);
            this.setNext(0);
            this.setPrev(0);
        }
        for (final ContainerState is : this.containerStates) {
            if (is != null) {
                is.reset(this, game);
            }
        }
        if (this.amount != null) {
            for (int index = 0; index < this.amount.length; ++index) {
                this.amount[index] = 0;
            }
        }
        this.valuesPlayer = new TIntIntHashMap();
        if (game.usesNote()) {
            this.notes = new TIntObjectHashMap<>();
        }
        this.initPhase(game);
        if (game.usesVote()) {
            this.isDecided = "";
            this.votes.clear();
            this.propositions.clear();
        }
        if (this.teams != null) {
            for (int i = 1; i < this.teams.length; ++i) {
                this.teams[i] = i;
            }
        }
        this.diceAllEqual = false;
    }
    
    @Override
    public String toString() {
        String str = "";
        str = str + "info: num=" + this.numPlayers() + ", mvr=" + this.mover() + ", nxt=" + this.next() + ", prv=" + this.prev() + ".\n";
        str = str + Arrays.toString(this.containerStates) + "\n";
        return str;
    }
    
    public int amount(final int player) {
        return this.amount[player];
    }
    
    public int pot() {
        return this.moneyPot;
    }
    
    public void setPot(final int pot) {
        this.moneyPot = pot;
    }
    
    public void setValueForPlayer(final int player, final int value) {
        this.valuesPlayer.put(player, value);
    }
    
    public int getValue(final int player) {
        return this.valuesPlayer.get(player);
    }
    
    public void addNote(final int move, final int player, final String message) {
        if (this.notes == null) {
            System.out.println("** State.addNote(): Null notes.");
            return;
        }
        final TIntObjectMap<String> notesForMove = this.notes.get(move);
        if (notesForMove == null) {
            this.notes.put(move, new TIntObjectHashMap<>());
        }
        this.notes.get(move).put(player, message);
    }
    
    public String getNote(final int move, final int player) {
        final TIntObjectMap<String> notesForMove = this.notes.get(move);
        if (notesForMove == null) {
            return null;
        }
        return this.notes.get(move).get(player);
    }
    
    public boolean troveValueUndefined(final int value) {
        return value == this.valuesPlayer.getNoEntryValue();
    }
    
    public void setAmount(final int player, final int newAmount) {
        if (player > 0 && player < this.amount.length) {
            this.updateAmountHash(player);
            this.amount[player] = newAmount;
            this.updateAmountHash(player);
        }
    }
    
    private void updateAmountHash(final int player) {
        if (this.lowAmountHashes != null) {
            if (this.amount[player] <= 1024) {
                this.amountHash ^= this.lowAmountHashes[player][this.amount[player]];
            }
            else {
                this.amountHash ^= this.highAmountHashes[player][this.amount[player] % 1024];
            }
        }
    }
    
    public void setScore(final int player, final int score, final int[] scoreArray) {
        this.updateScoreHash(player, scoreArray);
        scoreArray[player] = score;
        this.updateScoreHash(player, scoreArray);
    }
    
    private void updateScoreHash(final int player, final int[] scoreArray) {
        if (this.lowScoreHashes != null) {
            if (scoreArray[player] > 1024) {
                this.scoreHash ^= this.highScoreHashes[player][scoreArray[player] % 1024];
            }
            else if (scoreArray[player] < 0) {
                this.scoreHash ^= this.highScoreHashes[player][-scoreArray[player] % 1024];
            }
            else {
                this.scoreHash ^= this.lowScoreHashes[player][scoreArray[player]];
            }
        }
    }
    
    private void updatePendingHash(final int pendingVal) {
        final int idx = pendingVal + 1;
        if (idx < this.isPendingHashes.length) {
            this.pendingHash ^= this.isPendingHashes[idx];
        }
        else {
            this.pendingHash ^= this.isPendingHashes[idx % this.isPendingHashes.length];
        }
    }
    
    public int counter() {
        return this.counter;
    }
    
    public void incrCounter() {
        ++this.counter;
    }
    
    public void setCounter(final int counter) {
        this.counter = counter;
    }
    
    public int temp() {
        return this.tempValue;
    }
    
    public void setTemp(final int tempValue) {
        this.updateStateHash(this.tempHashes[this.tempValue + 2]);
        this.tempValue = tempValue;
        this.updateStateHash(this.tempHashes[this.tempValue + 2]);
    }
    
    public TIntHashSet pendingValues() {
        return this.pendingValues;
    }
    
    public List<String> propositions() {
        return this.propositions;
    }
    
    public void clearPropositions() {
        this.propositions.clear();
    }
    
    public void clearVotes() {
        this.votes.clear();
    }
    
    public List<String> votes() {
        return this.votes;
    }
    
    public String isDecided() {
        return this.isDecided;
    }
    
    public void setIsDecided(final String isDecided) {
        this.isDecided = isDecided;
    }
    
    public void setPending(final int value) {
        final int pendingValue = (value == -1) ? 1 : value;
        this.updatePendingHash(pendingValue);
        this.pendingValues.add(pendingValue);
    }
    
    public boolean isPending() {
        return !this.pendingValues.isEmpty();
    }
    
    public void rebootPending() {
        final TIntIterator it = this.pendingValues.iterator();
        while (it.hasNext()) {
            this.updatePendingHash(it.next());
        }
        this.pendingValues.clear();
    }
    
    public int currentPhase(final int indexPlayer) {
        return (this.currentPhase != null) ? this.currentPhase[indexPlayer] : 0;
    }
    
    public void setPhase(final int indexPlayer, final int newPhase) {
        if (this.phaseHashes != null) {
            this.updateStateHash(this.phaseHashes[indexPlayer][this.currentPhase[indexPlayer]]);
        }
        this.currentPhase[indexPlayer] = newPhase;
        if (this.phaseHashes != null) {
            this.updateStateHash(this.phaseHashes[indexPlayer][this.currentPhase[indexPlayer]]);
        }
    }
    
    public int sumDice(final int index) {
        return this.sumDice[index];
    }
    
    public int[] sumDice() {
        return this.sumDice;
    }
    
    public void setSumDice(final int[] sumDice) {
        this.sumDice = sumDice;
    }
    
    public void reinitSumDice() {
        for (int i = 0; i < this.sumDice.length; ++i) {
            this.sumDice[i] = 0;
        }
    }
    
    public int[] currentDice(final int index) {
        return this.currentDice[index];
    }
    
    public void setDiceAllEqual(final boolean value) {
        this.diceAllEqual = value;
    }
    
    public boolean isDiceAllEqual() {
        return this.diceAllEqual;
    }
    
    public int[][] currentDice() {
        return this.currentDice;
    }
    
    public void setCurrentDice(final int[][] currentDice) {
        this.currentDice = currentDice;
    }
    
    public void reinitCurrentDice() {
        for (int i = 0; i < this.currentDice.length; ++i) {
            for (int j = 0; j < this.currentDice[i].length; ++j) {
                this.currentDice[i][j] = 0;
            }
        }
    }
    
    public Owned owned() {
        return this.owned;
    }
    
    public void updateSumDice(final int dieValue, final int indexHand) {
        final int[] sumDice = this.sumDice;
        sumDice[indexHand] += dieValue;
    }
    
    public void updateCurrentDice(final int dieValue, final int dieIndex, final int indexHand) {
        this.currentDice[indexHand][dieIndex] = dieValue;
    }
    
    public void reInitVisited() {
        this.visited.clear(this);
    }
    
    public boolean isVisited(final int site) {
        return this.visited.get(site);
    }
    
    public void visit(final int site) {
        this.visited.set(this, site, true);
    }
    
    public void reInitCapturedPiece() {
        this.pieceToRemove.clear(this);
    }
    
    public boolean isPieceToRemove(final int site) {
        return this.pieceToRemove.get(site);
    }
    
    public void setPieceToRemove(final int site) {
        this.pieceToRemove.set(this, site, true);
    }
    
    public HashedBitSet piecesToRemove() {
        return this.pieceToRemove;
    }
    
    public boolean playerInTeam(final int pid, final int tid) {
        return this.teams != null && pid < this.teams.length && this.teams[pid] == tid;
    }
    
    public void setPlayerToTeam(final int pid, final int tid) {
        this.updateStateHash(this.teamHashes[pid][this.teams[pid]]);
        this.teams[pid] = tid;
        this.updateStateHash(this.teamHashes[pid][this.teams[pid]]);
    }
    
    public int getTeam(final int pid) {
        if (this.teams == null || pid >= this.teams.length) {
            return -1;
        }
        return this.teams[pid];
    }
    
    public Region regionToRemove() {
        if (this.pieceToRemove == null) {
            return new Region();
        }
        final TIntArrayList sitesToRemove = new TIntArrayList();
        for (int site = this.pieceToRemove.nextSetBit(0); site >= 0; site = this.pieceToRemove.nextSetBit(site + 1)) {
            sitesToRemove.add(site);
        }
        return new Region(sitesToRemove.toArray());
    }
    
    public int numTurnSamePlayer() {
        return this.numTurnSamePlayer;
    }
    
    public void reinitNumTurnSamePlayer() {
        this.numTurnSamePlayer = 0;
        ++this.numTurn;
    }
    
    public void incrementNumTurnSamePlayer() {
        ++this.numTurnSamePlayer;
    }
    
    public int numTurn() {
        return this.numTurn;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof State)) {
            return false;
        }
        final State otherState = (State)other;
        return this.fullHash() == otherState.fullHash();
    }
    
    @Override
    public int hashCode() {
        return (int)(this.fullHash() & -1L);
    }
    
    public void initPhase(final Game game) {
        if (game.rules() != null && game.rules().phases() != null) {
            this.currentPhase = new int[game.players().count() + 1];
            for (int pid = 1; pid <= game.players().count(); ++pid) {
                for (int indexPhase = 0; indexPhase < game.rules().phases().length; ++indexPhase) {
                    final Phase phase = game.rules().phases()[indexPhase];
                    final RoleType roleType = phase.owner();
                    if (roleType != null) {
                        final int phaseOwner = roleType.owner();
                        if (phaseOwner == pid || roleType == RoleType.Shared) {
                            this.currentPhase[pid] = indexPhase;
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public int trumpSuit() {
        return this.trumpSuit;
    }
    
    public void setTrumpSuit(final int trumpSuit) {
        this.trumpSuit = trumpSuit;
    }
    
    public OnTrackIndices onTrackIndices() {
        return this.onTrackIndices;
    }
    
    public void swapPlayerOrder(final int player1, final int player2) {
        int currentIndex1 = 0;
        int currentindex2 = 0;
        for (int i = 1; i < this.playerOrder.length; ++i) {
            if (this.playerOrder[i] == player1) {
                currentIndex1 = i;
            }
            if (this.playerOrder[i] == player2) {
                currentindex2 = i;
            }
        }
        final int temp = this.playerOrder[currentIndex1];
        this.updateStateHash(this.playerOrderHashes[currentIndex1][this.playerOrder[currentIndex1]]);
        this.playerOrder[currentIndex1] = this.playerOrder[currentindex2];
        this.updateStateHash(this.playerOrderHashes[currentIndex1][this.playerOrder[currentIndex1]]);
        this.updateStateHash(this.playerOrderHashes[currentindex2][this.playerOrder[currentindex2]]);
        this.playerOrder[currentindex2] = temp;
        this.updateStateHash(this.playerOrderHashes[currentindex2][this.playerOrder[currentindex2]]);
    }
    
    public int currentPlayerOrder(final int playerId) {
        return this.playerOrder[playerId];
    }
    
    public int originalPlayerOrder(final int playerId) {
        for (int p = 1; p < this.playerOrder.length; ++p) {
            if (this.playerOrder[p] == playerId) {
                return p;
            }
        }
        for (int po = 0; po < this.playerOrder.length; ++po) {
            System.out.println("playerOrder[" + po + "] = " + this.playerOrder[po]);
        }
        throw new RuntimeException("Player " + playerId + " has disappeared after swapping!");
    }
    
    public boolean orderHasChanged() {
        for (int p = 1; p < this.playerOrder.length; ++p) {
            if (this.playerOrder[p] != p) {
                return true;
            }
        }
        return false;
    }
    
    public TIntArrayList remainingDominoes() {
        return this.remainingDominoes;
    }
    
    public long storedState() {
        return this.storedState;
    }
    
    public void storeCurrentState(final State state) {
        this.storedState = state.stateHash();
    }
}
