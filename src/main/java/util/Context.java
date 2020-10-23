// 
// Decompiled by Procyon v0.5.36
// 

package util;

import collections.ArrayUtils;
import game.Game;
import game.equipment.Equipment;
import game.equipment.component.Component;
import game.equipment.container.Container;
import game.equipment.container.board.Board;
import game.equipment.container.board.Track;
import game.equipment.container.other.Dice;
import game.equipment.other.Regions;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.region.RegionFunction;
import game.match.Subgame;
import game.players.Player;
import game.rules.end.End;
import game.types.board.RegionTypeDynamic;
import game.types.play.RoleType;
import game.util.equipment.Region;
import gnu.trove.list.array.TIntArrayList;
import math.BitTwiddling;
import metadata.Metadata;
import org.apache.commons.rng.core.source64.SplitMix64;
import topology.Topology;
import util.model.MatchModel;
import util.model.Model;
import util.state.State;
import util.state.containerState.ContainerState;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Context
{
    private static SplitMix64 sharedRNG;
    private final Game game;
    private final Context parentContext;
    private Context subcontext;
    protected final transient State state;
    private int currentSubgameIdx;
    private final Model[] models;
    private final Trial trial;
    private final List<Trial> completedTrials;
    private final SplitMix64 rng;
    private int from;
    private int level;
    private int to;
    private int between;
    private int pipCount;
    private int iterator;
    private int track;
    private Region region;
    private RegionFunction hintRegion;
    private int hint;
    private int edge;
    private final Map<String, Integer> valueMap;
    private boolean ringFlagCalled;
    private boolean recursiveCalled;
    private int numEndResultsDecided;
    private final int[] scores;
    private int active;
    private final TIntArrayList winners;
    private boolean haveStarted;
    private final transient ReentrantLock lock;
    
    public Context(final Game game, final Trial trial) {
        this(game, trial, new SplitMix64(), null);
    }
    
    private Context(final Game game, final Trial trial, final SplitMix64 rng, final Context parentContext) {
        this.currentSubgameIdx = 0;
        this.from = -1;
        this.level = -1;
        this.to = -1;
        this.between = -1;
        this.pipCount = -1;
        this.iterator = -1;
        this.track = -1;
        this.region = null;
        this.hintRegion = null;
        this.hint = -1;
        this.edge = -1;
        this.ringFlagCalled = false;
        this.recursiveCalled = false;
        this.numEndResultsDecided = 0;
        this.active = 0;
        this.haveStarted = false;
        this.lock = new ReentrantLock();
        this.game = game;
        this.parentContext = parentContext;
        this.trial = trial;
        this.completedTrials = new ArrayList<>(1);
        this.rng = rng;
        if (game.hasSubgames()) {
            this.state = null;
            final Game subgame = game.instances()[0].getGame();
            this.subcontext = new Context(subgame, new Trial(subgame), rng, this);
            (this.models = new Model[1])[0] = new MatchModel();
            this.valueMap = null;
        }
        else {
            this.state = ((game.stateReference() != null) ? new State(game.stateReference()) : null);
            this.subcontext = null;
            this.models = new Model[game.rules().phases().length];
            for (int i = 0; i < game.rules().phases().length; ++i) {
                if (game.rules().phases()[i].mode() != null) {
                    this.models[i] = game.rules().phases()[i].mode().createModel();
                }
                else {
                    this.models[i] = game.mode().createModel();
                }
            }
            this.valueMap = new HashMap<>();
        }
        if (game.requiresScore()) {
            this.scores = new int[game.players().count() + 1];
        }
        else {
            this.scores = null;
        }
        for (int p = 1; p <= game.players().count(); ++p) {
            this.setActive(p, true);
        }
        this.winners = new TIntArrayList(game.players().count());
    }
    
    public Context(final Context other) {
        this(other, null);
    }
    
    private Context(final Context other, final Context otherParentCopy) {
        this.currentSubgameIdx = 0;
        this.from = -1;
        this.level = -1;
        this.to = -1;
        this.between = -1;
        this.pipCount = -1;
        this.iterator = -1;
        this.track = -1;
        this.region = null;
        this.hintRegion = null;
        this.hint = -1;
        this.edge = -1;
        this.ringFlagCalled = false;
        this.recursiveCalled = false;
        this.numEndResultsDecided = 0;
        this.active = 0;
        this.haveStarted = false;
        this.lock = new ReentrantLock();
        other.lock().lock();
        try {
            this.game = other.game;
            this.parentContext = otherParentCopy;
            this.state = this.copyState(other.state);
            this.trial = new Trial(other.trial);
            this.completedTrials = new ArrayList<>(other.completedTrials);
            this.rng = Context.sharedRNG;
            this.subcontext = ((other.subcontext == null) ? null : new Context(other.subcontext, this));
            this.currentSubgameIdx = other.currentSubgameIdx;
            this.models = new Model[other.models.length];
            for (int i = 0; i < this.models.length; ++i) {
                this.models[i] = other.models[i].copy();
            }
            this.valueMap = ((other.valueMap == null) ? null : new HashMap<>(other.valueMap));
            this.setFrom(other.from());
            this.setTo(other.to());
            this.setLevel(other.level());
            this.setBetween(other.between());
            this.setPipCount(other.pipCount());
            this.setIterator(other.iterator());
            this.setHint(other.hint());
            this.setEdge(other.edge());
            this.setTrack(other.track());
            this.setRegion(other.region);
            this.setHintRegion(other.hintRegion);
            this.setNumEndResultsDecided(other.numEndResultsDecided());
            this.ringFlagCalled = other.ringFlagCalled;
            this.recursiveCalled = other.recursiveCalled;
            if (other.scores != null) {
                this.scores = Arrays.copyOf(other.scores, other.scores.length);
            }
            else {
                this.scores = null;
            }
            this.active = other.active;
            this.winners = new TIntArrayList(other.winners);
        }
        finally {
            other.lock().unlock();
        }
    }
    
    protected State copyState(final State otherState) {
        return (otherState == null) ? null : new State(otherState);
    }
    
    public Context deepCopy() {
        return new Context(this, null);
    }
    
    public void reset() {
        if (this.state != null) {
            this.state.resetStateTo(this.game.stateReference(), this.game);
        }
        this.trial.reset(this.game);
        if (this.scores != null) {
            Arrays.fill(this.scores, 0);
        }
        this.active = 0;
        for (int p = 1; p <= this.game.players().count(); ++p) {
            this.setActive(p, true);
        }
        this.winners.reset();
        this.haveStarted = true;
        if (this.subcontext != null) {
            final Game subgame = this.game.instances()[0].getGame();
            this.subcontext = new Context(subgame, new Trial(subgame), this.rng, this);
            this.completedTrials.clear();
        }
        this.currentSubgameIdx = 0;
    }
    
    public boolean active(final int who) {
        return (this.active & 1 << who - 1) != 0x0;
    }
    
    public int onlyOneActive() {
        if (BitTwiddling.exactlyOneBitSet(this.active)) {
            return BitTwiddling.lowBitPos(this.active) + 1;
        }
        return 0;
    }
    
    public int onlyOneTeamActive() {
        final TIntArrayList activePlayers = new TIntArrayList();
        for (int i = 1; i <= this.game.players().count(); ++i) {
            if (this.active(i)) {
                activePlayers.add(i);
            }
        }
        final TIntArrayList activeTeam = new TIntArrayList();
        for (int j = 0; j < activePlayers.size(); ++j) {
            final int pid = activePlayers.getQuick(j);
            final int tid = this.state.getTeam(pid);
            if (!activeTeam.contains(tid)) {
                activeTeam.add(tid);
            }
        }
        if (activeTeam.size() != 1) {
            return 0;
        }
        return activeTeam.getQuick(0);
    }
    
    public void addWinner(final int idPlayer) {
        this.winners.add(idPlayer);
    }
    
    public int numWinners() {
        return this.winners.size();
    }
    
    public TIntArrayList winners() {
        return this.winners;
    }
    
    public int score(final int pid) {
        return this.scores[pid];
    }
    
    public void setScore(final int pid, final int scoreToSet) {
        if (this.state != null) {
            this.state.setScore(pid, scoreToSet, this.scores);
        }
        else {
            this.scores[pid] = scoreToSet;
        }
    }
    
    public void setActive(final int who, final boolean newActive) {
        if (this.state != null) {
            this.active = this.state.setActive(who, newActive, this.active);
        }
        else {
            final int whoBit = 1 << who - 1;
            final boolean wasActive = (this.active & whoBit) != 0x0;
            if (wasActive && !newActive) {
                this.active &= ~whoBit;
            }
            else if (!wasActive && newActive) {
                this.active |= whoBit;
            }
        }
    }
    
    public boolean active() {
        return this.active != 0;
    }
    
    public void setAllInactive() {
        this.active = 0;
        if (this.state != null) {
            this.state.updateHashAllPlayersInactive();
        }
    }
    
    public int numActive() {
        return Integer.bitCount(this.active);
    }
    
    public double computeNextWinRank() {
        final double[] ranking = this.trial.ranking();
        double winRankLowerBound = 0.0;
        while (true) {
            double minAssignedRank = Double.MAX_VALUE;
            for (int i = 1; i < ranking.length; ++i) {
                final double rank = ranking[i];
                if (rank < minAssignedRank && rank > winRankLowerBound) {
                    minAssignedRank = ranking[i];
                }
            }
            if (minAssignedRank == Double.MAX_VALUE) {
                return winRankLowerBound + 1.0;
            }
            final int numMinRankOccurrences = ArrayUtils.numOccurrences(ranking, minAssignedRank);
            final int minOccupied = (int)(minAssignedRank - (numMinRankOccurrences - 1) * 0.5);
            if (minOccupied > winRankLowerBound + 1.0) {
                return winRankLowerBound + 1.0;
            }
            winRankLowerBound += numMinRankOccurrences;
        }
    }
    
    public double computeNextLossRank() {
        double sumAssignedLossRanks = 0.0;
        for (double rank = this.trial.ranking().length - 1.0; rank > 0.0; --rank) {
            if (ArrayUtils.contains(this.trial.ranking(), rank)) {
                sumAssignedLossRanks += rank;
            }
            else {
                if (!ArrayUtils.contains(this.trial.ranking(), rank - 0.5)) {
                    break;
                }
                sumAssignedLossRanks += (rank - 0.5) * ArrayUtils.numOccurrences(this.trial.ranking(), rank - 0.5);
            }
        }
        int cumulativeRanks = 0;
        int rank2 = this.trial.ranking().length - 1;
        while (true) {
            cumulativeRanks += rank2;
            if (cumulativeRanks > sumAssignedLossRanks) {
                break;
            }
            --rank2;
        }
        return rank2;
    }
    
    public double computeNextDrawRank() {
        return (this.numActive() + 1) / 2.0 + this.numWinners();
    }
    
    public Game game() {
        return this.game;
    }
    
    public boolean isAMatch() {
        return this.game.hasSubgames();
    }
    
    public Model model() {
        if (this.models.length == 1) {
            return this.models[0];
        }
        return this.models[this.state.currentPhase(this.state.mover())];
    }
    
    public List<Player> players() {
        return this.game.players().players();
    }
    
    public Trial trial() {
        return this.trial;
    }
    
    public Context subcontext() {
        return this.subcontext;
    }
    
    public Context currentInstanceContext() {
        Context context;
        for (context = this; context.isAMatch(); context = context.subcontext()) {}
        return context;
    }
    
    public SplitMix64 rng() {
        return this.rng;
    }
    
    public int from() {
        return this.from;
    }
    
    public void setValue(final String key, final int value) {
        this.valueMap.put(key, value);
    }
    
    public int getValue(final String key) {
        final Integer temp = this.valueMap.get(key);
        if (temp == null) {
            return -1;
        }
        return temp;
    }
    
    public void setTrack(final int val) {
        this.track = val;
    }
    
    public int track() {
        return this.track;
    }
    
    public void setFrom(final int val) {
        this.from = val;
    }
    
    public int to() {
        return this.to;
    }
    
    public void setTo(final int val) {
        this.to = val;
    }
    
    public int between() {
        return this.between;
    }
    
    public void setBetween(final int val) {
        this.between = val;
    }
    
    public int pipCount() {
        return this.pipCount;
    }
    
    public void setPipCount(final int val) {
        this.pipCount = val;
    }
    
    public int level() {
        return this.level;
    }
    
    public void setLevel(final int val) {
        this.level = val;
    }
    
    public int hint() {
        return this.hint;
    }
    
    public void setHint(final int val) {
        this.hint = val;
    }
    
    public int edge() {
        return this.edge;
    }
    
    public void setEdge(final int val) {
        this.edge = val;
    }
    
    public Region region() {
        return this.region;
    }
    
    public void setRegion(final Region region) {
        this.region = region;
    }
    
    public RegionFunction hintRegion() {
        return this.hintRegion;
    }
    
    public void setHintRegion(final RegionFunction region) {
        this.hintRegion = region;
    }
    
    public int numEndResultsDecided() {
        return this.numEndResultsDecided;
    }
    
    public void setNumEndResultsDecided(final int numEndResultsDecided) {
        this.numEndResultsDecided = numEndResultsDecided;
    }
    
    public boolean haveStarted() {
        return this.haveStarted;
    }
    
    public int iterator() {
        return this.iterator;
    }
    
    public void setIterator(final int val) {
        this.iterator = val;
    }
    
    public void resetIterator() {
        this.iterator = -1;
    }
    
    public boolean ringFlagCalled() {
        return this.ringFlagCalled;
    }
    
    public void setRingFlagCalled(final boolean called) {
        this.ringFlagCalled = called;
    }
    
    public Container[] containers() {
        if (this.subcontext != null) {
            return this.subcontext.containers();
        }
        return this.game.equipment().containers();
    }
    
    public Component[] components() {
        if (this.subcontext != null) {
            return this.subcontext.components();
        }
        return this.game.equipment().components();
    }
    
    public List<Track> tracks() {
        if (this.subcontext != null) {
            return this.subcontext.tracks();
        }
        return this.game.board().tracks();
    }
    
    public Regions[] regions() {
        if (this.subcontext != null) {
            return this.subcontext.regions();
        }
        return this.game.equipment().regions();
    }
    
    public int[] containerId() {
        if (this.subcontext != null) {
            return this.subcontext.containerId();
        }
        return this.game.equipment().containerId();
    }
    
    public int[] sitesFrom() {
        if (this.subcontext != null) {
            return this.subcontext.sitesFrom();
        }
        return this.game.equipment().sitesFrom();
    }
    
    public Board board() {
        if (this.subcontext != null) {
            return this.subcontext.board();
        }
        return this.game.board();
    }
    
    public Metadata metadata() {
        if (this.subcontext != null) {
            return this.subcontext.metadata();
        }
        return this.game.metadata();
    }
    
    public Equipment equipment() {
        if (this.subcontext != null) {
            return this.subcontext.equipment();
        }
        return this.game.equipment();
    }
    
    public List<Dice> handDice() {
        if (this.subcontext != null) {
            return this.subcontext.handDice();
        }
        return this.game.handDice();
    }
    
    public Topology topology() {
        if (this.subcontext != null) {
            return this.subcontext.topology();
        }
        return this.game.board().topology();
    }
    
    public boolean hasSharedPlayer() {
        if (this.subcontext != null) {
            return this.subcontext.hasSharedPlayer();
        }
        return this.game.hasSharedPlayer();
    }
    
    public int numContainers() {
        if (this.subcontext != null) {
            return this.subcontext.numContainers();
        }
        return this.game.numContainers();
    }
    
    public int numComponents() {
        if (this.subcontext != null) {
            return this.subcontext.numComponents();
        }
        return this.game.numComponents();
    }
    
    public State state() {
        if (this.subcontext != null) {
            return this.subcontext.state();
        }
        return this.state;
    }
    
    public String getPlayerName(final int player) {
        if (this.subcontext != null) {
            return this.subcontext.getPlayerName(player);
        }
        return this.game.players().players().get(this.state().playerToAgent(player)).name();
    }
    
    public boolean allPass() {
        if (this.subcontext != null) {
            return this.subcontext.allPass();
        }
        final int numPlayers = this.game.players().count();
        final List<Move> moves = this.trial.moves();
        if (moves.size() < numPlayers) {
            return false;
        }
        for (int i = 1; i <= numPlayers; ++i) {
            if (!moves.get(moves.size() - i).isPass()) {
                return false;
            }
        }
        return true;
    }
    
    public Context parentContext() {
        return this.parentContext;
    }
    
    public List<Trial> completedTrials() {
        return this.completedTrials;
    }
    
    public TIntArrayList convertRegion(final RegionTypeDynamic dynamicRegion) {
        final TIntArrayList indexRegion = new TIntArrayList();
        final int moverId = this.state.mover();
        if (dynamicRegion == RegionTypeDynamic.Empty) {
            indexRegion.add(0);
        }
        else if (dynamicRegion == RegionTypeDynamic.NotEmpty) {
            for (int i = 1; i < this.game.players().count() + 2; ++i) {
                indexRegion.add(i);
            }
        }
        else if (dynamicRegion == RegionTypeDynamic.AllPlayers) {
            for (int i = 0; i < this.game.players().count() + 2; ++i) {
                indexRegion.add(i);
            }
        }
        else if (dynamicRegion == RegionTypeDynamic.Own) {
            indexRegion.add(moverId);
        }
        else if (dynamicRegion == RegionTypeDynamic.Enemy) {
            for (int i = 1; i < this.game.players().count() + 1; ++i) {
                if (i != moverId) {
                    indexRegion.add(i);
                }
            }
        }
        else if (dynamicRegion == RegionTypeDynamic.NotEnemy) {
            indexRegion.add(moverId);
            indexRegion.add(0);
        }
        else if (dynamicRegion == RegionTypeDynamic.NotOwn) {
            for (int i = 0; i < this.game.players().count() + 1; ++i) {
                if (i != moverId) {
                    indexRegion.add(i);
                }
            }
        }
        return indexRegion;
    }
    
    public TIntArrayList convertRole(final RoleType role) {
        TIntArrayList indexPlayer = new TIntArrayList();
        final int moverId = this.state.mover();
        if (role == RoleType.Enemy) {
            indexPlayer = this.game.players().players().get(moverId).enemies();
        }
        else if (role == RoleType.Shared) {
            for (int i = 1; i <= this.game.players().count(); ++i) {
                indexPlayer.add(i);
            }
        }
        else {
            indexPlayer.add(new Id(null, role).eval(this));
        }
        return indexPlayer;
    }
    
    public ContainerState containerState(final int cid) {
        if (this.subcontext != null) {
            return this.subcontext.containerState(cid);
        }
        return this.state().containerStates()[cid];
    }
    
    public boolean recursiveCalled() {
        return this.recursiveCalled;
    }
    
    public void setRecursiveCalled(final boolean value) {
        this.recursiveCalled = value;
    }
    
    public int fromStartOfTurn() {
        final List<Move> moves = this.trial.moves();
        if (moves.isEmpty()) {
            return -1;
        }
        final int mover = this.state.mover();
        int indexMove = moves.size() - 1;
        if (mover != moves.get(indexMove).mover()) {
            return -1;
        }
        int fromStartOfTurn = moves.get(indexMove).fromNonDecision();
        while (indexMove > 0 && moves.get(indexMove).mover() == mover) {
            fromStartOfTurn = moves.get(indexMove).fromNonDecision();
            --indexMove;
        }
        return fromStartOfTurn;
    }
    
    public int currentSubgameIdx() {
        return this.currentSubgameIdx;
    }
    
    public void advanceInstance() {
        final int numPlayers = this.trial.ranking().length - 1;
        final Subgame currentInstance = this.game.instances()[this.currentSubgameIdx];
        for (int p = 1; p <= numPlayers; ++p) {
            final int currentMatchScore = this.score(p);
            int scoreToAdd;
            if (currentInstance.result() != null && this.winners().contains(p)) {
                scoreToAdd = currentInstance.result().eval(this);
            }
            else if (numPlayers > 1) {
                scoreToAdd = numPlayers - (int)this.subcontext.trial().ranking()[p];
            }
            else {
                scoreToAdd = ((this.subcontext.trial().ranking()[p] == 1.0) ? 1 : 0);
            }
            this.setScore(p, currentMatchScore + scoreToAdd);
        }
        this.completedTrials.add(this.subcontext.trial());
        final End end = this.game.endRules();
        end.eval(this);
        if (!this.trial.over()) {
            final IntFunction nextFunc = currentInstance.next();
            if (nextFunc == null) {
                ++this.currentSubgameIdx;
            }
            else {
                this.currentSubgameIdx = nextFunc.eval(this);
            }
            final Subgame nextInstance = this.game.instances()[this.currentSubgameIdx];
            if (nextInstance.getGame() == null) {
                GameLoader.compileInstance(nextInstance);
            }
            final Game nextGame = nextInstance.getGame();
            final Trial nextTrial = new Trial(nextGame);
            this.subcontext = new Context(nextGame, nextTrial, this.rng, this);
            ((MatchModel)this.model()).resetCurrentInstanceModel();
            if (this.trial().auxilTrialData() != null) {
                if (this.trial().auxilTrialData().legalMovesHistory() != null) {
                    nextTrial.storeLegalMovesHistory();
                }
                if (this.trial().auxilTrialData().legalMovesHistorySizes() != null) {
                    nextTrial.storeLegalMovesHistorySizes();
                }
            }
            nextGame.start(this.subcontext);
        }
    }
    
    public ReentrantLock lock() {
        return this.lock;
    }
    
    public void setMoverAndImpliedPrevAndNext(final int newMover) {
        this.state.setMover(newMover);
        int next;
        for (next = newMover % this.game().players().count() + 1; !this.active(next); next = 1) {
            if (++next > this.game().players().count()) {}
        }
        this.state.setNext(next);
        int prev = newMover - 1;
        if (prev < 1) {
            prev = this.game().players().count();
        }
        while (!this.active(prev)) {
            if (--prev < 1) {
                prev = this.game().players().count();
            }
        }
        this.state.setPrev(prev);
    }
    
    public boolean isGraphGame() {
        if (this.subcontext != null) {
            return this.subcontext.isGraphGame();
        }
        return this.game.isGraphGame();
    }
    
    public boolean isVertexGame() {
        if (this.subcontext != null) {
            return this.subcontext.isVertexGame();
        }
        return this.game.isVertexGame();
    }
    
    public boolean isEdgeGame() {
        if (this.subcontext != null) {
            return this.subcontext.isEdgeGame();
        }
        return this.game.isEdgeGame();
    }
    
    public boolean isCellGame() {
        if (this.subcontext != null) {
            return this.subcontext.isCellGame();
        }
        return this.game.isCellGame();
    }
    
    static {
        Context.sharedRNG = new SplitMix64();
    }
}
