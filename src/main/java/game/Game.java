// 
// Decompiled by Procyon v0.5.36
// 

package game;

import annotations.Hide;
import annotations.Opt;
import game.equipment.Equipment;
import game.equipment.component.Component;
import game.equipment.container.Container;
import game.equipment.container.board.Board;
import game.equipment.container.board.Track;
import game.equipment.container.other.Deck;
import game.equipment.container.other.Dice;
import game.equipment.other.Regions;
import game.functions.booleans.BooleanFunction;
import game.functions.booleans.deductionPuzzle.ForAll;
import game.functions.booleans.is.Is;
import game.functions.booleans.is.IsLineType;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.ints.state.Mover;
import game.functions.region.RegionFunction;
import game.functions.region.sites.index.SitesEmpty;
import game.match.Subgame;
import game.mode.Mode;
import game.players.Player;
import game.players.Players;
import game.rules.Rules;
import game.rules.end.End;
import game.rules.end.If;
import game.rules.end.Result;
import game.rules.meta.MetaRule;
import game.rules.phase.NextPhase;
import game.rules.phase.Phase;
import game.rules.play.Play;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.*;
import game.rules.play.moves.nonDecision.effect.requirement.Do;
import game.rules.play.moves.nonDecision.effect.state.swap.players.SwapPlayers;
import game.rules.play.moves.nonDecision.operators.logical.Or;
import game.rules.start.StartRule;
import game.rules.start.place.item.PlaceItem;
import game.rules.start.place.stack.PlaceCustomStack;
import game.types.board.RegionTypeStatic;
import game.types.board.SiteType;
import game.types.play.ModeType;
import game.types.play.RepetitionType;
import game.types.play.ResultType;
import game.types.play.RoleType;
import game.util.directions.DirectionFacing;
import game.util.moves.To;
import gnu.trove.list.array.TIntArrayList;
import main.ReflectionUtils;
import main.Status;
import collections.FVector;
import collections.FastArrayList;
import grammar.Description;
import metadata.Metadata;
import topology.SiteFinder;
import topology.TopologyElement;
import util.*;
import util.action.Action;
import util.action.others.ActionPass;
import util.action.others.ActionSwap;
import util.playout.AddToEmpty;
import util.playout.FilterPlayout;
import util.playout.NoRepetitionPlayout;
import util.state.State;
import util.state.containerState.ContainerState;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Game extends BaseLudeme implements API, Serializable
{
    private static final long serialVersionUID = 1L;
    protected final String name;
    private final Mode mode;
    protected final Players players;
    protected final Equipment equipment;
    private final Rules rules;
    private Description description;
    protected int maxTurnLimit;
    protected int maxMovesLimit;
    private int numStartingAction;
    private long gameFlags;
    protected State stateReference;
    protected boolean finishedPreprocessing;
    private final Map<String, Container> mapContainer;
    private final Map<String, Component> mapComponent;
    private final List<Dice> handDice;
    private final List<Deck> handDeck;
    private final TIntArrayList constraintVariables;
    private boolean automove;
    private boolean usesSwapRule;
    private RepetitionType repetitionType;
    private Metadata metadata;
    private int gameStartCount;
    
    public Game(final String name, @Opt final Players players, @Opt final Mode mode, @Opt final Equipment equipment, @Opt final Rules rules) {
        this.description = new Description("Unprocessed");
        this.maxTurnLimit = 1250;
        this.maxMovesLimit = 10000;
        this.numStartingAction = 0;
        this.finishedPreprocessing = false;
        this.mapContainer = new HashMap<>();
        this.mapComponent = new HashMap<>();
        this.handDice = new ArrayList<>();
        this.handDeck = new ArrayList<>();
        this.constraintVariables = new TIntArrayList();
        this.automove = false;
        this.usesSwapRule = false;
        this.repetitionType = null;
        this.metadata = null;
        this.gameStartCount = 0;
        this.name = name;
        this.players = ((players == null) ? new Players(2, null) : players);
        if (mode != null) {
            this.mode = mode;
        }
        else if (this.players.count() == 0) {
            this.mode = new Mode(ModeType.Simulation);
        }
        else {
            this.mode = new Mode(null);
        }
        if (equipment != null) {
            this.equipment = equipment;
        }
        else {
            this.equipment = new Equipment(null);
        }
        if (rules != null) {
            this.rules = rules;
        }
        else {
            this.rules = new Rules(null, null, new Play(new Add(null, new To(null, SitesEmpty.construct(null, null), null, null, null, null, null), null, null, null)), new End(new If(Is.construct(IsLineType.Line, null, new IntConstant(3), null, null, null, null, null, null, null, null, null), null, null, new Result(RoleType.Mover, ResultType.Win)), null));
        }
    }
    
    @Hide
    public Game(final String name, final Description gameDescription) {
        this.description = new Description("Unprocessed");
        this.maxTurnLimit = 1250;
        this.maxMovesLimit = 10000;
        this.numStartingAction = 0;
        this.finishedPreprocessing = false;
        this.mapContainer = new HashMap<>();
        this.mapComponent = new HashMap<>();
        this.handDice = new ArrayList<>();
        this.handDeck = new ArrayList<>();
        this.constraintVariables = new TIntArrayList();
        this.automove = false;
        this.usesSwapRule = false;
        this.repetitionType = null;
        this.metadata = null;
        this.gameStartCount = 0;
        this.name = name;
        this.description = gameDescription;
        this.mode = null;
        this.players = null;
        this.equipment = null;
        this.rules = null;
    }
    
    public String name() {
        return this.name;
    }
    
    public Metadata metadata() {
        return this.metadata;
    }
    
    public void setMetadata(final Metadata md) {
        this.metadata = md;
    }
    
    public Mode mode() {
        return this.mode;
    }
    
    public Players players() {
        return this.players;
    }
    
    public Equipment equipment() {
        return this.equipment;
    }
    
    public Rules rules() {
        return this.rules;
    }
    
    public Subgame[] instances() {
        return null;
    }
    
    public Map<String, Container> mapContainer() {
        return Collections.unmodifiableMap(this.mapContainer);
    }
    
    public int numContainers() {
        return this.equipment().containers().length;
    }
    
    public int numComponents() {
        return this.equipment().components().length - 1;
    }
    
    public Component getComponent(final String nameC) {
        return this.mapComponent.get(nameC);
    }
    
    public Board board() {
        return (Board)this.equipment().containers()[0];
    }
    
    public TIntArrayList constraintVariables() {
        return this.constraintVariables;
    }
    
    public List<Dice> handDice() {
        return Collections.unmodifiableList(this.handDice);
    }
    
    public List<Deck> handDeck() {
        return Collections.unmodifiableList(this.handDeck);
    }
    
    public Dice getHandDice(final int index) {
        return this.handDice.get(index);
    }
    
    public Description description() {
        return this.description;
    }
    
    public void setDescription(final Description gd) {
        this.description = gd;
    }
    
    public int trackNameToIndex(final String trackName) {
        final List<Track> tracks = this.board().tracks();
        for (int i = 0; i < tracks.size(); ++i) {
            if (tracks.get(i).name().equals(trackName)) {
                return i;
            }
        }
        return -1;
    }
    
    public boolean hasSubgames() {
        return false;
    }
    
    public boolean isAlternatingMoveGame() {
        return this.mode.mode() == ModeType.Alternating;
    }
    
    public boolean isSimulationMoveGame() {
        return this.mode.mode() == ModeType.Simulation;
    }
    
    public boolean isStochasticGame() {
        return (this.gameFlags & 0x40L) != 0x0L;
    }
    
    public boolean isGraphGame() {
        return (this.gameFlags & 0x800000L) != 0x0L;
    }
    
    public boolean isVertexGame() {
        return (this.gameFlags & 0x1000000L) != 0x0L;
    }
    
    public boolean isEdgeGame() {
        return (this.gameFlags & 0x4000000L) != 0x0L;
    }
    
    public boolean isCellGame() {
        return (this.gameFlags & 0x2000000L) != 0x0L;
    }
    
    public boolean isDeductionPuzzle() {
        return (gameFlags() & 0x80L) != 0x0L;
    }
    
    public boolean usesVote() {
        return (gameFlags() & 0x80000000L) != 0x0L;
    }
    
    public boolean usesNote() {
        return (gameFlags() & 0x100000000L) != 0x0L;
    }
    
    public boolean requiresVisited() {
        return (gameFlags() & 0x200L) != 0x0L;
    }
    
    public boolean requiresScore() {
        return (gameFlags() & 0x100L) != 0x0L;
    }
    
    public boolean requiresBet() {
        return (gameFlags() & 0x80000L) != 0x0L;
    }
    
    public boolean requiresLocalState() {
        return (gameFlags() & 0x2L) != 0x0L || this.hasLargePiece();
    }
    
    public boolean requiresRotation() {
        return (gameFlags() & 0x20000L) != 0x0L;
    }
    
    public boolean requiresTeams() {
        return (gameFlags() & 0x40000L) != 0x0L;
    }
    
    public boolean needTrackCache() {
        return (gameFlags() & 0x10000L) != 0x0L;
    }
    
    public boolean usesStateComparison() {
        return this.repetitionType == RepetitionType.InGame || (gameFlags() & 0x20000000000L) != 0x0L;
    }
    
    public boolean usesStateComparisonWithinATurn() {
        return this.repetitionType == RepetitionType.InTurn || (gameFlags() & 0x40000000000L) != 0x0L;
    }
    
    public boolean automove() {
        return this.automove;
    }
    
    public void setAutomove(final boolean automove) {
        this.automove = automove;
    }
    
    public boolean usesSwapRule() {
        return this.usesSwapRule;
    }
    
    public void setRepetitionType(final RepetitionType type) {
        this.repetitionType = type;
    }
    
    public RepetitionType repetitionType() {
        return this.repetitionType;
    }
    
    public void setUsesSwapRule(final boolean swap) {
        this.usesSwapRule = swap;
    }
    
    public boolean hasSequenceCapture() {
        return (this.gameFlags & 0x8000L) != 0x0L;
    }
    
    public boolean requiresCount() {
        for (final Container c : this.equipment.containers()) {
            if (c.isHand()) {
                return true;
            }
        }
        return (gameFlags() & 0x4L) != 0x0L;
    }
    
    public boolean hiddenInformation() {
        return (gameFlags() & 0x8L) != 0x0L;
    }
    
    public boolean requiresAllPass() {
        return (gameFlags() & 0x1000L) == 0x0L && this.mode.mode() != ModeType.Simulation;
    }
    
    public boolean hasCard() {
        return (this.gameFlags & 0x2000L) != 0x0L;
    }
    
    public boolean hasInternalLoopInTrack() {
        return (this.gameFlags & 0x8000000000L) != 0x0L;
    }
    
    public boolean hasLargePiece() {
        return (this.gameFlags & 0x4000L) != 0x0L;
    }
    
    public boolean isStacking() {
        return (gameFlags() & 0x10L) != 0x0L || this.hasCard();
    }
    
    public boolean usesLineOfPlay() {
        return (gameFlags() & 0x10000000L) != 0x0L;
    }
    
    public boolean usesMoveAgain() {
        return (gameFlags() & 0x20000000L) != 0x0L;
    }
    
    public boolean isMarkov() {
        return (gameFlags() & 0x40000000L) == 0x0L && this.repetitionType == null;
    }
    
    public boolean isBoardless() {
        return this.board().isBoardless();
    }
    
    public boolean hasHandDice() {
        return !this.handDice.isEmpty();
    }
    
    public boolean hasTrack() {
        return !this.board().tracks().isEmpty();
    }
    
    public boolean hasDominoes() {
        for (int i = 1; i < this.equipment.components().length; ++i) {
            if (this.equipment.components()[i].name().contains("Domino")) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasHandDeck() {
        return !this.handDeck.isEmpty();
    }
    
    public boolean requiresHand() {
        for (final Container c : this.equipment.containers()) {
            if (c.isHand()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasCustomPlayouts() {
        if (mode().playout() != null) {
            return true;
        }
        for (final Phase phase : rules().phases()) {
            if (phase.playout() != null) {
                return true;
            }
        }
        return false;
    }
    
    public State stateReference() {
        return this.stateReference;
    }
    
    public boolean requiresItemIndices() {
        if (this.players.count() + 1 < this.equipment().components().length) {
            return true;
        }
        for (int numPlayer = 0; numPlayer < this.players.count() + 1; ++numPlayer) {
            int nbComponent = 0;
            for (int i = 1; i < this.equipment().components().length; ++i) {
                final Component c = this.equipment().components()[i];
                if (c.owner() == numPlayer) {
                    ++nbComponent;
                }
                if (nbComponent > 1) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public int maxCount() {
        if (this.hasDominoes()) {
            return this.equipment.components().length;
        }
        int counter = 0;
        if (this.rules != null && this.rules.start() != null) {
            for (final StartRule s : this.rules.start().rules()) {
                counter += s.count() * s.howManyPlace(this);
            }
        }
        return Math.max(counter, this.equipment.totalDefaultSites());
    }
    
    public int maximalLocalStates() {
        int maxLocalState = 2;
        boolean localStateToCompute = true;
        for (int i = 1; i < this.equipment.components().length; ++i) {
            final Component c = this.equipment.components()[i];
            if (c.isDie()) {
                if (c.getNumFaces() > maxLocalState) {
                    maxLocalState = c.getNumFaces();
                    localStateToCompute = false;
                }
            }
            else if (c.isLargePiece()) {
                final int numSteps = c.walk().length * 4;
                if (numSteps > maxLocalState) {
                    maxLocalState = numSteps;
                    localStateToCompute = false;
                }
            }
        }
        if (localStateToCompute) {
            return this.players().size();
        }
        return maxLocalState;
    }
    
    public int maximalRotationStates() {
        return Math.max(this.equipment().containers()[0].topology().supportedDirections(SiteType.Cell).size(), this.equipment().containers()[0].topology().supportedDirections(SiteType.Vertex).size());
    }
    
    public List<? extends TopologyElement> graphPlayElements() {
        switch (this.board().defaultSite()) {
            case Cell -> {
                return this.board().topology().cells();
            }
            case Edge -> {
                return this.board().topology().edges();
            }
            case Vertex -> {
                return this.board().topology().vertices();
            }
            default -> {
                return null;
            }
        }
    }
    
    public int[] distancesToCentre() {
        if (this.board().defaultSite() == SiteType.Vertex) {
            return this.board().topology().distancesToCentre(SiteType.Vertex);
        }
        return this.board().topology().distancesToCentre(SiteType.Cell);
    }
    
    public int[] distancesToCorners() {
        if (this.board().defaultSite() == SiteType.Vertex) {
            return this.board().topology().distancesToCorners(SiteType.Vertex);
        }
        return this.board().topology().distancesToCorners(SiteType.Cell);
    }
    
    public int[][] distancesToRegions() {
        if (this.board().defaultSite() == SiteType.Vertex) {
            return this.board().topology().distancesToRegions(SiteType.Vertex);
        }
        return this.board().topology().distancesToRegions(SiteType.Cell);
    }
    
    public int[] distancesToSides() {
        if (this.board().defaultSite() == SiteType.Vertex) {
            return this.board().topology().distancesToSides(SiteType.Vertex);
        }
        return this.board().topology().distancesToSides(SiteType.Cell);
    }
    
    public int gameStartCount() {
        return this.gameStartCount;
    }
    
    public void incrementGameStartCount() {
        ++this.gameStartCount;
    }
    
    public void removeFlag(final long flag) {
        this.gameFlags -= flag;
    }
    
    public void addFlag(final long flag) {
        this.gameFlags |= flag;
    }
    
    public long computeGameFlags() {
        long flags = 0L;
        try {
            flags |= SiteType.stateFlags(this.board().defaultSite());
            if (this.equipment().containers().length > 1) {
                flags |= 0x2000000L;
            }
            for (int i = 0; i < this.equipment().containers().length; ++i) {
                flags |= this.equipment().containers()[i].gameFlags(this);
            }
            for (int i = 1; i < this.equipment().components().length; ++i) {
                flags |= this.equipment().components()[i].gameFlags(this);
            }
            if (this.rules.meta() != null) {
                for (final MetaRule meta : this.rules.meta().rules()) {
                    flags |= meta.gameFlags(this);
                }
            }
            if (this.rules.start() != null) {
                for (final StartRule start : this.rules.start().rules()) {
                    flags |= start.gameFlags(this);
                }
            }
            if (this.rules.end() != null) {
                flags |= this.rules.end().gameFlags(this);
            }
            for (final Phase phase : this.rules.phases()) {
                flags |= phase.gameFlags(this);
            }
            for (int e = 1; e < this.equipment.components().length; ++e) {
                if ((this.gameFlags & 0x40L) == 0x0L && this.equipment.components()[e].isDie()) {
                    flags |= 0x40L;
                }
                if ((this.gameFlags & 0x4000L) == 0x0L && this.equipment.components()[e].isLargePiece()) {
                    flags |= 0x4000L;
                }
            }
            if (this.mode.mode() == ModeType.Simultaneous) {
                flags |= 0x400L;
            }
            if (this.hasHandDice()) {
                flags |= 0x1000L;
            }
            if (this.hasTrack()) {
                flags |= 0x10000L;
                for (final Track track : this.board().tracks()) {
                    if (track.hasInternalLoop()) {
                        flags |= 0x8000000000L;
                        break;
                    }
                }
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        return flags;
    }
    
    public long gameFlags() {
        return this.gameFlags;
    }
    
    public static Move createSwapMove(final int mover, final int player1, final int player2) {
        final ActionSwap actionSwap = new ActionSwap(player1, player2);
        actionSwap.setDecision(true);
        final Move swapMpve = new Move(actionSwap);
        swapMpve.setMover(mover);
        return swapMpve;
    }
    
    public static Move createPassMove(final Context context) {
        return createPassMove(context.state().mover());
    }
    
    public static Move createPassMove(final int player) {
        final ActionPass actionPass = new ActionPass();
        actionPass.setDecision(true);
        final Move passMove = new Move(actionPass);
        passMove.setMover(player);
        return passMove;
    }
    
    @Override
    public void create() {
        if (this.finishedPreprocessing) {
            System.err.println("Warning! Game.create() has already previously been called on " + this.name());
        }
        this.equipment.createItems(this);
        for (int i = 1; i < this.equipment.components().length; ++i) {
            final Component component = this.equipment.components()[i];
            if (component.isTile() && component.numSides() == -1) {
                component.setNumSides(this.board().topology().cells().get(0).edges().size());
            }
            final String componentName = component.name();
            final RoleType role = component.role();
            if (this.players.count() != 1 && !componentName.contains("Domino") && !componentName.contains("Die") && (role == RoleType.Neutral || (role.owner() > 0 && role.owner() <= 16))) {
                component.setName(componentName + role.owner());
            }
            if (this.players.count() == 1 && !componentName.contains("Domino") && !componentName.contains("Die") && role == RoleType.Neutral) {
                component.setName(componentName + role.owner());
            }
        }
        for (final Track track : this.board().tracks()) {
            track.buildTrack(this);
        }
        for (final game.equipment.other.Map map : this.equipment.maps()) {
            map.computeMap(this);
        }
        for (int j = 1; j < this.players.players().size(); ++j) {
            final Player p = this.players.players().get(j);
            final DirectionFacing direction = p.direction();
            final int pid = p.index();
            if (direction != null) {
                for (int k = 1; k < this.equipment.components().length; ++k) {
                    final Component component2 = this.equipment.components()[k];
                    if (pid == component2.owner()) {
                        component2.setDirection(direction);
                    }
                }
            }
        }
        for (final Container c : this.equipment.containers()) {
            if (c.isDice()) {
                this.handDice.add((Dice)c);
            }
            else if (c.isDeck()) {
                this.handDeck.add((Deck)c);
            }
        }
        this.gameFlags = this.computeGameFlags();
        if ((this.gameFlags & 0x10000000000L) != 0x0L) {
            this.setUsesSwapRule(true);
        }
        this.stateReference = new State(this, StateConstructorLock.INSTANCE);
        if (this.isDeductionPuzzle()) {
            this.equipment.clearComponents();
        }
        this.mapContainer.clear();
        this.mapComponent.clear();
        for (int e = 0; e < this.equipment.containers().length; ++e) {
            final Container equip = this.equipment.containers()[e];
            this.mapContainer.put(equip.name(), equip);
        }
        for (int e = 1; e < this.equipment.components().length; ++e) {
            final Component equip2 = this.equipment.components()[e];
            equip2.setIndex(e);
            this.mapComponent.put(equip2.name(), equip2);
        }
        this.stateReference.initialise(this);
        final Regions[] regions2;
        final Regions[] regions = regions2 = this.equipment().regions();
        for (final Regions region : regions2) {
            region.preprocess(this);
        }
        if (this.rules.start() != null) {
            for (final StartRule start : this.rules.start().rules()) {
                start.preprocess(this);
            }
        }
        if (this.rules.end() != null) {
            this.rules.end().preprocess(this);
        }
        for (final Phase phase : this.rules.phases()) {
            phase.preprocess(this);
        }
        if (this.board().defaultSite() == SiteType.Cell) {
            this.equipment.containers()[0].topology().preGenerateDistanceToRegionsCells(this, regions);
        }
        else {
            this.equipment.containers()[0].topology().preGenerateDistanceToRegionsVertices(this, regions);
        }
        this.postCreation();
        this.addCustomPlayouts();
        this.finishedPreprocessing = true;
    }
    
    public End endRules() {
        return this.rules.end();
    }
    
    @Override
    public void start(final Context context) {
        context.lock().lock();
        try {
            context.reset();
            if (this.hasDominoes()) {
                for (int componentId = 1; componentId < this.equipment().components().length; ++componentId) {
                    context.state().remainingDominoes().add(componentId);
                }
            }
            for (final Dice c : context.game().handDice()) {
                for (int i = 1; i <= c.numLocs(); ++i) {
                    final StartRule rule = new PlaceItem("Die" + i, c.name(), SiteType.Cell, null, null, null, null, null, null, null);
                    rule.eval(context);
                }
            }
            for (final Deck d : context.game().handDeck()) {
                final TIntArrayList components = new TIntArrayList(d.indexComponent());
                final int nbCards = components.size();
                final RoleType[] masked = new RoleType[this.players.count()];
                for (int j = 0; j < masked.length; ++j) {
                    masked[j] = RoleType.roleForPlayerId(j + 1);
                }
                for (int j = 0; j < nbCards; ++j) {
                    final int k = context.rng().nextInt(components.size());
                    final int index = components.getQuick(k);
                    final StartRule rule2 = new PlaceCustomStack("Card" + index, null, d.name(), SiteType.Cell, null, null, null, null, null, null, masked);
                    components.remove(index);
                    rule2.eval(context);
                }
            }
            if (this.rules.start() != null) {
                this.rules.start().eval(context);
            }
            if (this.rules.meta() != null) {
                for (final MetaRule meta : this.rules.meta().rules()) {
                    meta.eval(context);
                }
            }
            for (int l = 0; l < context.components().length; ++l) {
                context.trial().startingPos().add(new TIntArrayList());
            }
            if (!this.isDeductionPuzzle()) {
                final ContainerState cs = context.containerState(0);
                for (int site = 0; site < this.board().topology().getGraphElements(this.board().defaultSite()).size(); ++site) {
                    final int what = cs.what(site, this.board().defaultSite());
                    context.trial().startingPos().get(what).add(site);
                }
            }
            else {
                final Satisfy set = (Satisfy)rules().phases()[0].play().moves();
                this.initConstraintVariables(set.constraints(), context);
            }
            if (this.hasHandDice()) {
                for (int l = 0; l < this.handDice().size(); ++l) {
                    final Dice dice = this.handDice().get(l);
                    final ContainerState cs2 = context.containerState(dice.index());
                    final int siteFrom = context.sitesFrom()[dice.index()];
                    final int siteTo = context.sitesFrom()[dice.index()] + dice.numSites();
                    int sum = 0;
                    for (int site2 = siteFrom; site2 < siteTo; ++site2) {
                        sum += context.components()[cs2.whatCell(site2)].getFaces()[cs2.stateCell(site2)];
                        context.state().currentDice()[l][site2 - siteFrom] = context.components()[cs2.whatCell(site2)].getFaces()[cs2.stateCell(site2)];
                    }
                    context.state().sumDice()[l] = sum;
                }
            }
            if (this.usesStateComparison() && context.state().mover() != context.state().prev()) {
                context.trial().previousState().add(context.state().stateHash());
            }
            if (this.usesStateComparisonWithinATurn()) {
                if (context.state().mover() == context.state().prev()) {
                    context.trial().previousStateWithinATurn().add(context.state().stateHash());
                }
                else {
                    context.trial().previousStateWithinATurn().clear();
                    context.trial().previousStateWithinATurn().add(context.state().stateHash());
                }
            }
            context.trial().saveState(context.state());
            this.numStartingAction = context.trial().numMoves();
            this.incrementGameStartCount();
            if (!context.trial().over() && context.game().isStochasticGame()) {
                context.game().moves(context);
            }
        }
        finally {
            context.lock().unlock();
        }
    }
    
    @Override
    public Moves moves(final Context context) {
        context.lock().lock();
        try {
            final Trial trial = context.trial();
            if (trial.cachedLegalMoves() == null || trial.cachedLegalMoves().moves().isEmpty()) {
                Moves legalMoves;
                if (trial.over()) {
                    legalMoves = new BaseMoves(null);
                }
                else if (this.isAlternatingMoveGame()) {
                    final int mover = context.state().mover();
                    final int indexPhase = context.state().currentPhase(mover);
                    final Phase phase = context.game().rules.phases()[indexPhase];
                    legalMoves = phase.play().moves().eval(context);
                    if (this.usesSwapRule && trial.moveNumber() == context.game().players.count() - 1) {
                        legalMoves.moves().addAll(new SwapPlayers(new IntConstant(1), null, new IntConstant(2), null, null).eval(context).moves());
                    }
                    if (legalMoves.moves().isEmpty()) {
                        legalMoves.moves().add(createPassMove(context));
                    }
                }
                else if (this.isSimulationMoveGame()) {
                    final Phase phase2 = context.game().rules.phases()[0];
                    final Moves moves = phase2.play().moves().eval(context);
                    legalMoves = new BaseMoves(null);
                    if (!moves.moves().isEmpty()) {
                        final Move singleMove = new Move(moves.get(0));
                        for (int i = 1; i < moves.moves().size(); ++i) {
                            for (final Action action : moves.get(i).actions()) {
                                singleMove.actions().add(action);
                            }
                        }
                        legalMoves.moves().add(singleMove);
                    }
                }
                else {
                    legalMoves = new BaseMoves(null);
                    for (int p = 1; p <= this.players.count(); ++p) {
                        final int indexPhase = context.state().currentPhase(p);
                        final Phase phase = context.game().rules.phases()[indexPhase];
                        final FastArrayList<Move> phaseMoves = phase.play().moves().eval(context).moves();
                        boolean addedMove = false;
                        for (final Move move : phaseMoves) {
                            if (move.mover() == p) {
                                legalMoves.moves().add(move);
                                addedMove = true;
                            }
                        }
                        if (!addedMove && context.active(p)) {
                            legalMoves.moves().add(createPassMove(p));
                        }
                    }
                    for (final Move move2 : legalMoves.moves()) {
                        if (move2.then().isEmpty() && legalMoves.then() != null) {
                            move2.then().add(legalMoves.then().moves());
                        }
                    }
                }
                trial.setLegalMoves(legalMoves, context);
                if (context.active()) {
                    context.state().setStalemated(context.state().mover(), legalMoves.moves().isEmpty());
                }
            }
            return trial.cachedLegalMoves();
        }
        finally {
            context.lock().unlock();
        }
    }
    
    @Override
    public Move apply(final Context context, final Move move) {
        context.lock().lock();
        try {
            if (context.game().automove()) {
                for (boolean repeatAutoMove = true; repeatAutoMove; repeatAutoMove = true) {
                    repeatAutoMove = false;
                    final Context newContext = new Context(context);
                    this.applyInternal(newContext, move);
                    final int mover = newContext.state().mover();
                    final int indexPhase = newContext.state().currentPhase(mover);
                    final Phase phase = newContext.game().rules().phases()[indexPhase];
                    final Moves newLegalMoves = phase.play().moves().eval(newContext);
                    for (int j = 0; j < newLegalMoves.moves().size() - 1; ++j) {
                        final Move newMove = newLegalMoves.get(j);
                        final int site = newMove.toNonDecision();
                        int cpt = 1;
                        for (int k = j + 1; k < newLegalMoves.moves().size(); ++k) {
                            if (site == newLegalMoves.moves().get(k).toNonDecision()) {
                                ++cpt;
                            }
                        }
                        if (cpt != 1) {
                            for (int k = 0; k < newLegalMoves.moves().size(); ++k) {
                                if (newLegalMoves.moves().get(k).toNonDecision() == site) {
                                    newLegalMoves.moves().remove(k);
                                    --k;
                                }
                            }
                            --j;
                        }
                    }
                    if (!newLegalMoves.moves().isEmpty()) {
                        final Moves forcedMoves = new BaseMoves(null);
                        for (int i = 0; i < newLegalMoves.moves().size(); ++i) {
                            MoveUtilities.chainRuleCrossProduct(newContext, forcedMoves, null, newLegalMoves.moves().get(i), false);
                        }
                        move.then().add(forcedMoves);
                    }
                }
            }
            return this.applyInternal(context, move);
        }
        finally {
            context.lock().unlock();
        }
    }
    
    private Move applyInternal(final Context context, final Move move) {
        final Trial trial = context.trial();
        final State state = context.state();
        final Game game = context.game();
        final int mover = state.mover();
        if (move.isPass() && !context.state().isStalemated(mover)) {
            this.computeStalemated(context);
        }
        state.rebootPending();
        final Move returnMove = (Move)move.apply(context, true);
        if (this.usesStateComparison() && context.state().mover() != context.state().prev()) {
            context.trial().previousState().add(context.state().stateHash());
        }
        if (this.usesStateComparisonWithinATurn()) {
            if (context.state().mover() == context.state().prev()) {
                context.trial().previousStateWithinATurn().add(context.state().stateHash());
            }
            else {
                context.trial().previousStateWithinATurn().clear();
                context.trial().previousStateWithinATurn().add(context.state().stateHash());
            }
        }
        final int players = game.players.count();
        if (trial.moves().size() > game.numStartingAction) {
            if (context.active() && game.rules.phases() != null) {
                final End endPhase = game.rules.phases()[state.currentPhase(state.mover())].end();
                if (endPhase != null) {
                    endPhase.eval(context);
                }
            }
            if (!trial.over()) {
                final End endRule = game.rules.end();
                if (endRule != null) {
                    endRule.eval(context);
                }
            }
            if (context.active() && this.checkMaxTurns(context)) {
                int winner = 0;
                if (game.players().count() > 1) {
                    final double score = context.computeNextDrawRank();
                    for (int player = 1; player < context.trial().ranking().length; ++player) {
                        if (context.trial().ranking()[player] == 0.0) {
                            context.trial().ranking()[player] = score;
                        }
                        else if (context.trial().ranking()[player] == 1.0) {
                            winner = player;
                        }
                    }
                }
                else {
                    context.trial().ranking()[1] = 0.0;
                }
                context.setAllInactive();
                trial.setStatus(new Status(winner));
            }
            if (!context.active()) {
                state.setPrev(mover);
            }
            else if (game.rules.phases() != null) {
                for (int pid = 1; pid <= game.players().count(); ++pid) {
                    final Phase phase = game.rules.phases()[state.currentPhase(pid)];
                    for (int i = 0; i < phase.nextPhase().length; ++i) {
                        final NextPhase cond = phase.nextPhase()[i];
                        final int who = cond.who().eval(context);
                        if (who == players + 1 || pid == who) {
                            final int nextPhase = cond.eval(context);
                            if (nextPhase != -1) {
                                state.setPhase(pid, nextPhase);
                                break;
                            }
                        }
                    }
                }
            }
            state.incrCounter();
        }
        if (context.active()) {
            if (this.requiresVisited()) {
                if (state.mover() != state.next()) {
                    context.state().reInitVisited();
                }
                else {
                    final int from = returnMove.fromNonDecision();
                    final int to = returnMove.toNonDecision();
                    context.state().visit(from);
                    context.state().visit(to);
                }
            }
            state.setPrev(mover);
            state.setMover(state.next());
            if (state.prev() == state.mover()) {
                context.state().incrementNumTurnSamePlayer();
            }
            else {
                context.state().reinitNumTurnSamePlayer();
            }
            int next;
            int remain = players - 1;
            for (next = state.mover() % players + 1; --remain > 0 && !context.active(next); next = 1) {
                if (++next > players) {}
            }
            state.setNext(next);
        }
        if (this.hasHandDice()) {
            for (int j = 0; j < this.handDice().size(); ++j) {
                final Dice dice = this.handDice().get(j);
                final ContainerState cs = context.containerState(dice.index());
                final int siteFrom = context.sitesFrom()[dice.index()];
                final int siteTo = context.sitesFrom()[dice.index()] + dice.numSites();
                int sum = 0;
                for (int site = siteFrom; site < siteTo; ++site) {
                    sum += context.components()[cs.whatCell(site)].getFaces()[cs.stateCell(site)];
                }
                state.sumDice()[j] = sum;
            }
        }
        trial.saveState(context.state());
        trial.clearLegalMoves();
        if (!(context instanceof TempContext) && !trial.over() && context.game().isStochasticGame()) {
            context.game().moves(context);
        }
        return returnMove;
    }
    
    public void computeStalemated(final Context context) {
        final Trial trial = context.trial();
        final State state = context.state();
        final Game game = context.game();
        final int mover = state.mover();
        if (this.isAlternatingMoveGame()) {
            if (context.active()) {
                final int indexPhase = state.currentPhase(mover);
                final Phase phase = context.game().rules().phases()[indexPhase];
                boolean foundLegalMove = false;
                foundLegalMove = ((this.usesSwapRule && trial.moveNumber() == context.game().players.count() - 1) || phase.play().moves().canMove(context));
                context.state().setStalemated(state.mover(), !foundLegalMove);
            }
        }
        else {
            game.moves(context);
        }
    }
    
    public static boolean satisfiesStateComparison(final Context context, final Move move) {
        final boolean trialWideTest = context.game().usesStateComparison();
        final boolean turnOnlyTest = context.game().usesStateComparisonWithinATurn();
        if ((trialWideTest || turnOnlyTest) && !move.isPass()) {
            final Context newContext = new TempContext(context);
            move.apply(newContext, true);
            if (turnOnlyTest && context.trial().previousStateWithinATurn().contains(newContext.state().stateHash())) {
                return false;
            }
            return !trialWideTest || !context.trial().previousState().contains(newContext.state().stateHash());
        }
        return true;
    }
    
    private void addCustomPlayouts() {
        if (this.mode.playout() == null) {
            for (final Phase phase : this.rules.phases()) {
                if (phase.playout() == null) {
                    final Moves moves = phase.play().moves();
                    if (!this.hasLargePiece() && moves instanceof Add && moves.then() == null) {
                        final Add to = (Add)moves;
                        if (to.components().length == 1 && to.components()[0] instanceof Mover && to.region() instanceof SitesEmpty.EmptyDefault && to.legal() == null && !to.onStack() && this.mode.mode() == ModeType.Alternating && !this.usesStateComparison() && !this.usesStateComparisonWithinATurn()) {
                            phase.setPlayout(new AddToEmpty(to.type()));
                        }
                    }
                    else if (moves instanceof Do) {
                        if (((Do)moves).ifAfter() != null) {
                            phase.setPlayout(new FilterPlayout());
                        }
                    }
                    else if (moves instanceof game.rules.play.moves.nonDecision.operators.logical.If) {
                        final game.rules.play.moves.nonDecision.operators.logical.If ifRule = (game.rules.play.moves.nonDecision.operators.logical.If)moves;
                        if (ifRule.elseList() instanceof Do && ((Do)ifRule.elseList()).ifAfter() != null) {
                            phase.setPlayout(new FilterPlayout());
                        }
                    }
                    else if (moves instanceof Or) {
                        final Or orRule = (Or)moves;
                        if (orRule.list().length == 2 && orRule.list()[0] instanceof Do && orRule.list()[1] instanceof Pass && ((Do)orRule.list()[0]).ifAfter() != null) {
                            phase.setPlayout(new FilterPlayout());
                        }
                    }
                    if (phase.playout() == null && (this.usesStateComparison() || this.usesStateComparisonWithinATurn())) {
                        phase.setPlayout(new NoRepetitionPlayout());
                    }
                }
            }
        }
    }
    
    private void postCreation() {
        checkAddMoveCaches(this, true, new HashMap<>());
    }
    
    private static void checkAddMoveCaches(final Ludeme ludeme, final boolean inAllowCache, final Map<Object, Set<String>> visited) {
        final Class<? extends Ludeme> clazz = ludeme.getClass();
        final List<Field> fields = ReflectionUtils.getAllFields(clazz);
        try {
            for (final Field field : fields) {
                if (field.getName().contains("$")) {
                    continue;
                }
                field.setAccessible(true);
                if ((field.getModifiers() & 0x8) != 0x0) {
                    continue;
                }
                if (visited.containsKey(ludeme) && visited.get(ludeme).contains(field.getName())) {
                    continue;
                }
                final Object value = field.get(ludeme);
                if (!visited.containsKey(ludeme)) {
                    visited.put(ludeme, new HashSet<>());
                }
                visited.get(ludeme).add(field.getName());
                if (value == null) {
                    continue;
                }
                final Class<?> valueClass = value.getClass();
                if (Enum.class.isAssignableFrom(valueClass)) {
                    continue;
                }
                if (Ludeme.class.isAssignableFrom(valueClass)) {
                    final Ludeme innerLudeme = (Ludeme)value;
                    final boolean allowCache = inAllowCache && allowsToActionCaches(ludeme, innerLudeme);
                    setActionCacheAllowed(innerLudeme, allowCache);
                    checkAddMoveCaches(innerLudeme, allowCache, visited);
                }
                else if (valueClass.isArray()) {
                    final Object[] castArray;
                    final Object[] array = castArray = ReflectionUtils.castArray(value);
                    for (final Object element : castArray) {
                        if (element != null) {
                            final Class<?> elementClass = element.getClass();
                            if (Ludeme.class.isAssignableFrom(elementClass)) {
                                final Ludeme innerLudeme2 = (Ludeme)element;
                                final boolean allowCache2 = inAllowCache && allowsToActionCaches(ludeme, innerLudeme2);
                                setActionCacheAllowed(innerLudeme2, allowCache2);
                                checkAddMoveCaches(innerLudeme2, allowCache2, visited);
                            }
                        }
                    }
                }
                else {
                    if (!Iterable.class.isAssignableFrom(valueClass)) {
                        continue;
                    }
                    final Iterable<?> iterable = (Iterable<?>)value;
                    for (final Object element2 : iterable) {
                        if (element2 != null) {
                            final Class<?> elementClass2 = element2.getClass();
                            if (!Ludeme.class.isAssignableFrom(elementClass2)) {
                                continue;
                            }
                            final Ludeme innerLudeme3 = (Ludeme)element2;
                            final boolean allowCache3 = inAllowCache && allowsToActionCaches(ludeme, innerLudeme3);
                            setActionCacheAllowed(innerLudeme3, allowCache3);
                            checkAddMoveCaches(innerLudeme3, allowCache3, visited);
                        }
                    }
                }
            }
        }
        catch (IllegalArgumentException | IllegalAccessException ex2) {
            ex2.printStackTrace();
        }
    }
    
    private static boolean allowsToActionCaches(final Ludeme outerLudeme, final Ludeme innerLudeme) {
        return !(outerLudeme instanceof Moves) || innerLudeme instanceof Then || ((Moves)outerLudeme).then() == null;
    }
    
    private static void setActionCacheAllowed(final Ludeme ludeme, final boolean allowed) {
        if (allowed) {
            return;
        }
        if (ludeme instanceof Add) {
            ((Add)ludeme).disableActionCache();
        }
        else if (ludeme instanceof Claim) {
            ((Claim)ludeme).disableActionCache();
        }
    }
    
    @Override
    public Trial playout(final Context context, final List<AI> ais, final double thinkingTime, final FeatureSetInterface[] featureSets, final FVector[] weights, final int maxNumBiasedActions, final int maxNumPlayoutActions, final float autoPlayThreshold, final Random random) {
        final Random rng = (random != null) ? random : ThreadLocalRandom.current();
        final Phase startPhase = rules().phases()[context.state().currentPhase(context.state().mover())];
        if (startPhase.playout() != null) {
            return startPhase.playout().playout(context, ais, thinkingTime, featureSets, weights, maxNumBiasedActions, maxNumPlayoutActions, autoPlayThreshold, rng);
        }
        if (context.game().mode().playout() != null) {
            return context.game().mode().playout().playout(context, ais, thinkingTime, featureSets, weights, maxNumBiasedActions, maxNumPlayoutActions, autoPlayThreshold, rng);
        }
        return context.model().playout(context, ais, thinkingTime, featureSets, weights, maxNumBiasedActions, maxNumPlayoutActions, autoPlayThreshold, rng);
    }
    
    public void initConstraintVariables(final BooleanFunction[] constraints, final Context context) {
        for (final BooleanFunction constraint : constraints) {
            if (constraint instanceof ForAll) {
                for (int numSite = context.topology().getGraphElements(context.board().defaultSite()).size(), index = 0; index < numSite; ++index) {
                    if (!this.constraintVariables.contains(index)) {
                        this.constraintVariables.add(index);
                    }
                }
                break;
            }
            if (constraint.staticRegion() != null && constraint.staticRegion() == RegionTypeStatic.Regions) {
                final Regions[] regions2;
                final Regions[] regions = regions2 = context.game().equipment().regions();
                for (final Regions region : regions2) {
                    if (region.regionTypes() != null) {
                        final RegionTypeStatic[] regionTypes;
                        final RegionTypeStatic[] areas = regionTypes = region.regionTypes();
                        for (final RegionTypeStatic area : regionTypes) {
                            final Integer[][] convertStaticRegionOnLocs;
                            final Integer[][] regionsList = convertStaticRegionOnLocs = region.convertStaticRegionOnLocs(area, context);
                            for (final Integer[] array : convertStaticRegionOnLocs) {
                                final Integer[] locs = array;
                                for (final Integer loc : array) {
                                    if (loc != null && !this.constraintVariables.contains(loc)) {
                                        this.constraintVariables.add(loc);
                                    }
                                }
                            }
                        }
                    }
                    else if (region.sites() != null) {
                        for (final int loc2 : region.sites()) {
                            if (!this.constraintVariables.contains(loc2)) {
                                this.constraintVariables.add(loc2);
                            }
                        }
                    }
                    else if (region.region() != null) {
                        for (final RegionFunction regionFn : region.region()) {
                            final int[] sites4;
                            final int[] sites = sites4 = regionFn.eval(context).sites();
                            for (final int loc3 : sites4) {
                                if (!this.constraintVariables.contains(loc3)) {
                                    this.constraintVariables.add(loc3);
                                }
                            }
                        }
                    }
                }
            }
            else if (constraint.locsConstraint() != null) {
                for (final IntFunction function : constraint.locsConstraint()) {
                    final int index2 = function.eval(context);
                    if (!this.constraintVariables.contains(index2)) {
                        this.constraintVariables.add(index2);
                    }
                }
            }
            else if (constraint.regionConstraint() != null) {
                final int[] sites5;
                final int[] sites2 = sites5 = constraint.regionConstraint().eval(context).sites();
                for (final int index2 : sites5) {
                    if (!this.constraintVariables.contains(index2)) {
                        this.constraintVariables.add(index2);
                    }
                }
            }
        }
    }
    
    public boolean checkMaxTurns(final Context context) {
        return context.state().numTurn() >= this.getMaxTurnLimit() * this.players.count() || context.trial().numMoves() - context.trial().numInitialPlacementMoves() >= this.getMaxMoveLimit();
    }
    
    public void setMaxTurns(final int limitTurn) {
        this.maxTurnLimit = limitTurn;
    }
    
    public int getMaxTurnLimit() {
        return this.maxTurnLimit;
    }
    
    public void setMaxMoveLimit(final int limitMoves) {
        this.maxMovesLimit = limitMoves;
    }
    
    public int getMaxMoveLimit() {
        return this.maxMovesLimit;
    }
    
    public void disableMemorylessPlayouts() {
        if (mode().playout() != null && !mode().playout().callsGameMoves()) {
            mode().setPlayout(null);
        }
        for (final Phase phase : rules().phases()) {
            if (phase.playout() != null && !phase.playout().callsGameMoves()) {
                phase.setPlayout(null);
            }
        }
    }
    
    public boolean finishedPreprocessing() {
        return this.finishedPreprocessing;
    }
    
    public boolean hasSharedPlayer() {
        for (final Container comp : this.equipment().containers()) {
            if (comp.owner() > this.players.count()) {
                return true;
            }
        }
        return false;
    }
    
    public List<Move> getMovesFromCoordinates(final String str, final Context context, final List<Move> originalMoves) {
        final String fromCoordinate = str.contains("-") ? str.substring(0, str.indexOf(45)) : str;
        final String toCoordinate = str.contains("-") ? str.substring(str.indexOf(45) + 1) : fromCoordinate;
        final TopologyElement fromElement = SiteFinder.find(context.board(), fromCoordinate, context.board().defaultSite());
        final TopologyElement toElement = SiteFinder.find(context.board(), toCoordinate, context.board().defaultSite());
        final List<Move> moves = new ArrayList<>();
        if (fromElement == null || toElement == null) {
            return moves;
        }
        for (final Move m : originalMoves) {
            if (m.fromNonDecision() == fromElement.index() && m.toNonDecision() == toElement.index()) {
                moves.add(m);
            }
        }
        return moves;
    }
    
    public static final class StateConstructorLock
    {
        protected static final StateConstructorLock INSTANCE;
        
        private StateConstructorLock() {
        }
        
        static {
            INSTANCE = new StateConstructorLock();
        }
    }
}
