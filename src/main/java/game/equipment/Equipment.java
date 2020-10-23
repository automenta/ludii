// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment;

import game.Game;
import game.equipment.component.Component;
import game.equipment.component.Die;
import game.equipment.component.Piece;
import game.equipment.component.tile.Domino;
import game.equipment.container.Container;
import game.equipment.container.board.Board;
import game.equipment.container.board.Track;
import game.equipment.container.other.Deck;
import game.equipment.container.other.Dice;
import game.equipment.container.other.Hand;
import game.equipment.other.Dominoes;
import game.equipment.other.Hints;
import game.equipment.other.Map;
import game.equipment.other.Regions;
import game.functions.dim.DimConstant;
import game.functions.graph.generators.basis.square.RectangleOnSquare;
import game.functions.region.RegionFunction;
import game.types.board.RelationType;
import game.types.board.SiteType;
import game.types.play.RoleType;
import main.SettingsGeneral;
import topology.Topology;
import topology.TopologyElement;
import util.BaseLudeme;
import util.ItemType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class Equipment extends BaseLudeme implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Container[] containers;
    private Component[] components;
    private Regions[] regions;
    private Map[] maps;
    private int totalDefaultSites;
    private int[] containerId;
    private int[] offset;
    private int[] sitesFrom;
    private Integer[][] vertexWithHints;
    private Integer[][] cellWithHints;
    private Integer[][] edgeWithHints;
    private Integer[] vertexHints;
    private Integer[] cellHints;
    private Integer[] edgeHints;
    private Item[] itemsToCreate;
    
    public Equipment(final Item[] items) {
        this.containers = null;
        this.components = null;
        this.regions = null;
        this.maps = null;
        this.totalDefaultSites = 0;
        this.vertexWithHints = new Integer[0][0];
        this.cellWithHints = new Integer[0][0];
        this.edgeWithHints = new Integer[0][0];
        this.vertexHints = new Integer[0];
        this.cellHints = new Integer[0];
        this.edgeHints = new Integer[0];
        this.itemsToCreate = items;
    }
    
    public void createItems(final Game game) {
        final List<Component> componentsWIP = new ArrayList<>();
        final List<Container> containersWIP = new ArrayList<>();
        final List<Regions> regionsWIP = new ArrayList<>();
        final List<Map> mapsWIP = new ArrayList<>();
        componentsWIP.add(null);
        if (this.itemsToCreate != null) {
            final Item[] sortItems = sort(this.itemsToCreate);
            int indexDie = 1;
            int indexCard = 1;
            for (final Item item : sortItems) {
                if (ItemType.isContainer(item.type())) {
                    final Container c = (Container)item;
                    if (item.type().ordinal() >= ItemType.Hand.ordinal() && item.type().ordinal() <= ItemType.Dice.ordinal()) {
                        if (c.role() != null && c.role() == RoleType.Each) {
                            final Hand hand = (Hand)c;
                            for (int idPlayer = 1; idPlayer <= game.players().count(); ++idPlayer) {
                                final Hand newHand = hand.clone();
                                if (hand.name() == null) {
                                    newHand.setName("Hand");
                                }
                                newHand.setName(newHand.name() + idPlayer);
                                newHand.setRoleFromPlayerId(idPlayer);
                                containersWIP.add(newHand);
                            }
                        }
                        else if (c.type() == ItemType.Dice) {
                            final int indexSameDice = multiDiceSameOwner((Dice)c, containersWIP);
                            if (indexSameDice == -1) {
                                containersWIP.add(c);
                                final Dice dice = (Dice)c;
                                for (int i = indexDie; i <= dice.numLocs() + indexDie - 1; ++i) {
                                    final Die die = new Die("Die" + i, dice.role(), dice.getNumFaces(), null, null, null);
                                    die.setBiased(dice.getBiased());
                                    die.setFaces(dice.getFaces()[i - indexDie], dice.getStart());
                                    componentsWIP.add(die);
                                }
                                indexDie += dice.numLocs();
                            }
                            else {
                                final Dice dice = (Dice)c;
                                for (int i = indexDie; i <= dice.numLocs() + indexDie - 1; ++i) {
                                    final Die die = new Die("Die" + i, dice.role(), dice.getNumFaces(), null, null, null);
                                    die.setBiased(dice.getBiased());
                                    die.setFaces(dice.getFaces()[i - indexDie], dice.getStart());
                                    componentsWIP.add(die);
                                }
                                indexDie += dice.numLocs();
                                containersWIP.set(indexSameDice, new Dice(dice.getNumFaces(), null, dice.getFaces(), null, dice.role(),
                                        ((Dice)containersWIP.get(indexSameDice)).numLocs() + dice.numLocs(), null));
                            }
                        }
                        else if (c.isDeck()) {
                            containersWIP.add(c);
                            final Deck deck = (Deck)c;
                            final List<Component> cards = deck.generateCards(indexCard, componentsWIP.size());
                            componentsWIP.addAll(cards);
                            indexCard += cards.size();
                        }
                        else {
                            if (c.name() == null) {
                                c.setName("Hand" + c.owner());
                            }
                            containersWIP.add(c);
                        }
                    }
                    else {
                        containersWIP.add(c);
                    }
                    if (game.isDeductionPuzzle()) {
                        final Board puzzleBoard = (Board)c;
                        if (puzzleBoard.cellRange().max() != 0) {
                            for (int num = puzzleBoard.cellRange().min(); num <= puzzleBoard.cellRange().max(); ++num) {
                                final Piece number = new Piece(String.valueOf((Object) num), RoleType.P1, null, null, null, null);
                                SettingsGeneral.setMaxNumberValue(puzzleBoard.cellRange().max());
                                componentsWIP.add(number);
                            }
                        }
                    }
                }
                else if (ItemType.isComponent(item.type())) {
                    if (item.type() == ItemType.Component) {
                        final Component comp = (Component)item;
                        if (isNumber(comp) && comp.getValue() > SettingsGeneral.getMaxNumberValue()) {
                            SettingsGeneral.setMaxNumberValue(comp.getValue());
                        }
                        if (comp.role() != null && comp.role() == RoleType.Each) {
                            for (int idPlayer2 = 1; idPlayer2 <= game.players().count(); ++idPlayer2) {
                                final Component compCopy = comp.clone();
                                if (comp.name() == null) {
                                    final String className = comp.getClass().toString();
                                    final String componentName = className.substring(className.lastIndexOf(46) + 1);
                                    compCopy.setName(componentName);
                                }
                                compCopy.setRoleFromPlayerId(idPlayer2);
                                compCopy.setName(compCopy.name());
                                compCopy.setIndex(componentsWIP.size() - 1);
                                componentsWIP.add(compCopy);
                            }
                        }
                        else {
                            if (comp.name() == null) {
                                final String className2 = comp.getClass().toString();
                                final String componentName2 = className2.substring(className2.lastIndexOf(46) + 1);
                                comp.setName(componentName2 + comp.owner());
                            }
                            componentsWIP.add(comp);
                        }
                    }
                    else if (item.type() == ItemType.Dominoes) {
                        final Dominoes dominoes = (Dominoes)item;
                        final ArrayList<Domino> listDominoes = dominoes.generateDominoes();
                        componentsWIP.addAll(listDominoes);
                    }
                }
                else if (ItemType.isRegion(item.type())) {
                    regionsWIP.add((Regions)item);
                }
                else if (ItemType.isMap(item.type())) {
                    mapsWIP.add((Map)item);
                }
                else if (ItemType.isHints(item.type())) {
                    final Hints hints = (Hints)item;
                    final int minSize = Math.min(hints.where().length, hints.values().length);
                    final SiteType puzzleType = hints.getType();
                    if (puzzleType == SiteType.Vertex) {
                        this.setVertexWithHints(new Integer[minSize][]);
                        this.setVertexHints(new Integer[minSize]);
                    }
                    else if (puzzleType == SiteType.Edge) {
                        this.setEdgeWithHints(new Integer[minSize][]);
                        this.setEdgeHints(new Integer[minSize]);
                    }
                    else if (puzzleType == SiteType.Cell) {
                        this.setCellWithHints(new Integer[minSize][]);
                        this.setCellHints(new Integer[minSize]);
                    }
                    for (int i = 0; i < minSize; ++i) {
                        if (puzzleType == SiteType.Vertex) {
                            this.verticesWithHints()[i] = hints.where()[i];
                            this.vertexHints()[i] = hints.values()[i];
                        }
                        else if (puzzleType == SiteType.Edge) {
                            this.edgesWithHints()[i] = hints.where()[i];
                            this.edgeHints()[i] = hints.values()[i];
                        }
                        else if (puzzleType == SiteType.Cell) {
                            this.cellsWithHints()[i] = hints.where()[i];
                            this.cellHints()[i] = hints.values()[i];
                        }
                    }
                }
            }
        }
        if (containersWIP.isEmpty()) {
            containersWIP.add(new Board(new RectangleOnSquare(new DimConstant(3), null, null, null), null, null, null, null, null));
        }
        if (componentsWIP.size() == 1 && !game.isDeductionPuzzle()) {
            for (int pid = 1; pid <= game.players().count(); ++pid) {
                final String name = "Ball" + pid;
                final Component piece = new Piece(name, RoleType.values()[pid], null, null, null, null);
                componentsWIP.add(piece);
            }
        }
        this.containers = containersWIP.toArray(new Container[0]);
        this.components = componentsWIP.toArray(new Component[0]);
        this.regions = regionsWIP.toArray(new Regions[0]);
        this.maps = mapsWIP.toArray(new Map[0]);
        this.initContainerAndParameters(game);
        for (final Container cont : this.containers) {
            if (cont != null) {
                cont.create(game);
            }
        }
        for (final Component comp2 : this.components) {
            if (comp2 != null) {
                comp2.create(game);
            }
        }
        for (final Regions reg : this.regions) {
            reg.create(game);
        }
        for (final Map map : this.maps) {
            map.create(game);
        }
        this.itemsToCreate = null;
        if (game.hasTrack()) {
            for (int j = 0; j < game.board().tracks().size(); ++j) {
                game.board().tracks().get(j).setTrackIdx(j);
            }
            final Track[][] ownedTracks = new Track[game.players().size() + 1][];
            for (int k = 0; k < ownedTracks.length; ++k) {
                final List<Track> ownedTrack = new ArrayList<>();
                for (final Track track : game.board().tracks()) {
                    if (track.owner() == k) {
                        ownedTrack.add(track);
                    }
                }
                final Track[] ownedTrackArray = new Track[ownedTrack.size()];
                for (int l = 0; l < ownedTrackArray.length; ++l) {
                    ownedTrackArray[l] = ownedTrack.get(l);
                }
                ownedTracks[k] = ownedTrackArray;
            }
            game.board().setOwnedTrack(ownedTracks);
        }
    }
    
    private static Item[] sort(final Item[] items) {
        final Item[] sortedItem = new Item[items.length];
        final int maxOrdinalValue = ItemType.values().length;
        int indexSortedItem = 0;
        for (int ordinalValue = 0; ordinalValue < maxOrdinalValue; ++ordinalValue) {
            for (final Item item : items) {
                if (item.type().ordinal() == ordinalValue) {
                    sortedItem[indexSortedItem] = item;
                    ++indexSortedItem;
                }
            }
        }
        return sortedItem;
    }
    
    private static int multiDiceSameOwner(final Dice c, final List<Container> containers) {
        for (int i = 0; i < containers.size(); ++i) {
            final Container container = containers.get(i);
            if (container.isDice()) {
                final Dice containerDice = (Dice)container;
                if (containerDice.owner() == c.owner() && !containerDice.equals(c)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    private static boolean isNumber(final Component c) {
        for (int i = 0; i < c.name().length(); ++i) {
            if (c.name().charAt(i) < '0' || c.name().charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }
    
    public void initContainerAndParameters(final Game game) {
        final long gameFlags = game.computeGameFlags();
        int index = 0;
        for (int e = 0; e < this.containers.length; ++e) {
            final Container cont = this.containers[e];
            cont.createTopology(index, (cont.index() == 0) ? -1 : this.containers[0].topology().numEdges());
            final Topology topology = cont.topology();
            for (final SiteType type : SiteType.values()) {
                topology.computeRelation(type);
                topology.computeSupportedDirection(type);
                for (final TopologyElement element : topology.getGraphElements(type)) {
                    topology.convertPropertiesToList(type, element);
                }
                final boolean threeDimensions = (gameFlags & 0x800L) != 0x0L;
                topology.computeRows(type, threeDimensions);
                topology.computeColumns(type, threeDimensions);
                if (!cont.isBoardless()) {
                    topology.computeLayers(type);
                    topology.computeCoordinates(type);
                    topology.preGenerateDistanceTables(type);
                }
                if ((gameFlags & 0x400000000L) != 0x0L) {
                    topology.preGenerateDistanceToEachElementToEachOther(type, RelationType.Adjacent);
                }
                else if ((gameFlags & 0x4000000000L) != 0x0L) {
                    topology.preGenerateDistanceToEachElementToEachOther(type, RelationType.All);
                }
                else if ((gameFlags & 0x2000000000L) != 0x0L) {
                    topology.preGenerateDistanceToEachElementToEachOther(type, RelationType.OffDiagonal);
                }
                else if ((gameFlags & 0x1000000000L) != 0x0L) {
                    topology.preGenerateDistanceToEachElementToEachOther(type, RelationType.Diagonal);
                }
                else if ((gameFlags & 0x800000000L) != 0x0L) {
                    topology.preGenerateDistanceToEachElementToEachOther(type, RelationType.Orthogonal);
                }
                topology.computeDoesCross();
            }
            if (e == 0) {
                topology.pregenerateFeaturesData(game, cont);
            }
            cont.setIndex(e);
            index += ((cont.index() == 0) ? Math.max(cont.topology().cells().size(), cont.topology().getGraphElements(cont.defaultSite()).size()) : cont.numSites());
            topology.optimiseMemory();
        }
        for (final Container cont2 : this.containers) {
            this.totalDefaultSites += cont2.numSites();
        }
        int fakeTotalDefaultSite;
        final int maxSiteMainBoard = fakeTotalDefaultSite = Math.max(this.containers[0].topology().cells().size(), this.containers[0].topology().getGraphElements(this.containers[0].defaultSite()).size());
        for (int i = 1; i < this.containers.length; ++i) {
            fakeTotalDefaultSite += this.containers[i].topology().cells().size();
        }
        this.offset = new int[fakeTotalDefaultSite];
        int accumulatedOffset = 0;
        for (int j = 0; j < this.containers.length; ++j) {
            final Container cont3 = this.containers[j];
            if (j == 0) {
                for (int k = 0; k < maxSiteMainBoard; ++k) {
                    this.offset[k + accumulatedOffset] = k;
                }
                accumulatedOffset += maxSiteMainBoard;
            }
            else {
                for (int k = 0; k < cont3.numSites(); ++k) {
                    this.offset[k + accumulatedOffset] = k;
                }
                accumulatedOffset += cont3.numSites();
            }
        }
        this.sitesFrom = new int[this.containers.length];
        int count = 0;
        for (int l = 0; l < this.containers.length; ++l) {
            this.sitesFrom[l] = count;
            count += ((l == 0) ? maxSiteMainBoard : this.containers[l].numSites());
        }
        this.containerId = new int[fakeTotalDefaultSite];
        count = 0;
        int idBoard = 0;
        for (int m = 0; m < this.containers.length - 1; ++m) {
            for (int j2 = this.sitesFrom[m]; j2 < this.sitesFrom[m + 1]; ++j2) {
                this.containerId[count] = idBoard;
                ++count;
            }
            ++idBoard;
        }
        for (int k = this.sitesFrom[this.sitesFrom.length - 1]; k < this.sitesFrom[this.sitesFrom.length - 1] + this.containers[idBoard].numSites(); ++k) {
            this.containerId[count] = idBoard;
            ++count;
        }
    }
    
    public List<Regions> computeStaticRegions() {
        final List<Regions> staticRegions = new ArrayList<>();
        for (final Regions region : this.regions) {
            Label_0124: {
                if (region.region() != null) {
                    boolean allStatic = true;
                    for (final RegionFunction regionFunc : region.region()) {
                        if (!regionFunc.isStatic()) {
                            allStatic = false;
                            break;
                        }
                    }
                    if (!allStatic) {
                        break Label_0124;
                    }
                }
                else if (region.sites() == null) {
                    break Label_0124;
                }
                staticRegions.add(region);
            }
        }
        return staticRegions;
    }
    
    public Container[] containers() {
        return this.containers;
    }
    
    public Component[] components() {
        return this.components;
    }
    
    public void clearComponents() {
        this.components = new Component[] { null };
    }
    
    public Regions[] regions() {
        return this.regions;
    }
    
    public Map[] maps() {
        return this.maps;
    }
    
    public int totalDefaultSites() {
        return this.totalDefaultSites;
    }
    
    public int[] containerId() {
        return this.containerId;
    }
    
    public int[] offset() {
        return this.offset;
    }
    
    public int[] sitesFrom() {
        return this.sitesFrom;
    }
    
    public Integer[][] verticesWithHints() {
        return this.vertexWithHints;
    }
    
    public void setVertexWithHints(final Integer[][] regionWithHints) {
        this.vertexWithHints = regionWithHints;
    }
    
    public Integer[][] cellsWithHints() {
        return this.cellWithHints;
    }
    
    public void setCellWithHints(final Integer[][] cellWithHints) {
        this.cellWithHints = cellWithHints;
    }
    
    public Integer[][] edgesWithHints() {
        return this.edgeWithHints;
    }
    
    public void setEdgeWithHints(final Integer[][] edgeWithHints) {
        this.edgeWithHints = edgeWithHints;
    }
    
    public Integer[] vertexHints() {
        return this.vertexHints;
    }
    
    public void setVertexHints(final Integer[] hints) {
        this.vertexHints = hints;
    }
    
    public Integer[] cellHints() {
        return this.cellHints;
    }
    
    public void setCellHints(final Integer[] hints) {
        this.cellHints = hints;
    }
    
    public Integer[] edgeHints() {
        return this.edgeHints;
    }
    
    public void setEdgeHints(final Integer[] hints) {
        this.edgeHints = hints;
    }
    
    public Integer[] hints(final SiteType type) {
        switch (type) {
            case Edge -> {
                return this.edgeHints();
            }
            case Vertex -> {
                return this.vertexHints();
            }
            case Cell -> {
                return this.cellHints();
            }
            default -> {
                return new Integer[0];
            }
        }
    }
    
    public Integer[][] withHints(final SiteType type) {
        switch (type) {
            case Edge -> {
                return this.edgesWithHints();
            }
            case Vertex -> {
                return this.verticesWithHints();
            }
            case Cell -> {
                return this.cellsWithHints();
            }
            default -> {
                return new Integer[0][0];
            }
        }
    }
}
