// 
// Decompiled by Procyon v0.5.36
// 

package utils;

import game.Game;
import game.equipment.component.Component;
import game.equipment.container.Container;
import main.FileHandling;
import main.math.MathRoutines;
import topology.TopologyElement;
import util.GameLoader;
import util.Move;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public final class LudiiGameWrapper
{
    protected static final double EPSILON = 1.0E-5;
    protected static final int NUM_STACK_CHANNELS = 10;
    protected static final int NUM_LOCAL_STATE_CHANNELS = 6;
    protected static final int DEFAULT_MOVE_TENSOR_DIST_CLIP = 3;
    protected static final int MOVE_TENSOR_LEVEL_CLIP = 2;
    protected final Game game;
    protected int[] xCoords;
    protected int[] yCoords;
    protected int tensorDimX;
    protected int tensorDimY;
    protected int stateTensorNumChannels;
    protected String[] stateTensorChannelNames;
    protected final int moveTensorDistClip;
    protected int MOVE_PASS_CHANNEL_IDX;
    protected int MOVE_SWAP_CHANNEL_IDX;
    protected float[] ALL_ONES_CHANNEL_FLAT;
    protected float[] CONTAINER_POSITION_CHANNELS;
    
    public LudiiGameWrapper(final String gameName) {
        this.game = GameLoader.loadGameFromName(gameName);
        if ((this.game.gameFlags() & 0x1L) == 0x0L) {
            this.moveTensorDistClip = 0;
        }
        else {
            this.moveTensorDistClip = 3;
        }
        this.computeTensorCoords();
    }
    
    public LudiiGameWrapper(final String gameName, final String... gameOptions) {
        this.game = GameLoader.loadGameFromName(gameName, Arrays.asList(gameOptions));
        if ((this.game.gameFlags() & 0x1L) == 0x0L) {
            this.moveTensorDistClip = 0;
        }
        else {
            this.moveTensorDistClip = 3;
        }
        this.computeTensorCoords();
    }
    
    public LudiiGameWrapper(final File file) {
        this.game = GameLoader.loadGameFromFile(file);
        if ((this.game.gameFlags() & 0x1L) == 0x0L) {
            this.moveTensorDistClip = 0;
        }
        else {
            this.moveTensorDistClip = 3;
        }
        this.computeTensorCoords();
    }
    
    public LudiiGameWrapper(final File file, final String... gameOptions) {
        this.game = GameLoader.loadGameFromFile(file, Arrays.asList(gameOptions));
        if ((this.game.gameFlags() & 0x1L) == 0x0L) {
            this.moveTensorDistClip = 0;
        }
        else {
            this.moveTensorDistClip = 3;
        }
        this.computeTensorCoords();
    }
    
    public LudiiGameWrapper(final Game game) {
        this.game = game;
        if ((game.gameFlags() & 0x1L) == 0x0L) {
            this.moveTensorDistClip = 0;
        }
        else {
            this.moveTensorDistClip = 3;
        }
        this.computeTensorCoords();
    }
    
    public static String ludiiVersion() {
        return "1.0.8";
    }
    
    public boolean isSimultaneousMoveGame() {
        return !this.game.isAlternatingMoveGame();
    }
    
    public boolean isStochasticGame() {
        return this.game.isStochasticGame();
    }
    
    public boolean isImperfectInformationGame() {
        return this.game.hiddenInformation();
    }
    
    public String name() {
        return this.game.name();
    }
    
    public int numPlayers() {
        return this.game.players().count();
    }
    
    public int[] tensorCoordsX() {
        return this.xCoords;
    }
    
    public int[] tensorCoordsY() {
        return this.yCoords;
    }
    
    public int tensorDimX() {
        return this.tensorDimX;
    }
    
    public int tensorDimY() {
        return this.tensorDimY;
    }
    
    public int[] moveTensorsShape() {
        return new int[] { this.MOVE_SWAP_CHANNEL_IDX + 1, this.tensorDimX(), this.tensorDimY() };
    }
    
    public int[] stateTensorsShape() {
        return new int[] { this.stateTensorNumChannels, this.tensorDimX(), this.tensorDimY() };
    }
    
    public String[] stateTensorChannelNames() {
        return this.stateTensorChannelNames;
    }
    
    public int[] moveToTensor(final Move move) {
        if (move.isPass()) {
            return new int[] { this.MOVE_PASS_CHANNEL_IDX, 0, 0 };
        }
        if (move.isSwap()) {
            return new int[] { this.MOVE_SWAP_CHANNEL_IDX, 0, 0 };
        }
        final int from = move.fromNonDecision();
        final int to = move.toNonDecision();
        final int levelMin = move.levelMinNonDecision();
        final int levelMax = move.levelMaxNonDecision();
        assert to >= 0;
        final int fromX = (from != -1) ? this.xCoords[from] : -1;
        final int fromY = (from != -1) ? this.yCoords[from] : -1;
        final int toX = this.xCoords[to];
        final int toY = this.yCoords[to];
        final int diffX = (from != -1) ? (toX - fromX) : 0;
        final int diffY = (from != -1) ? (toY - fromY) : 0;
        int channelIdx = MathRoutines.clip(diffX, -this.moveTensorDistClip, this.moveTensorDistClip) + this.moveTensorDistClip;
        channelIdx *= this.moveTensorDistClip * 2 + 1;
        channelIdx += MathRoutines.clip(diffY, -this.moveTensorDistClip, this.moveTensorDistClip) + this.moveTensorDistClip;
        if (this.game.isStacking()) {
            channelIdx *= 3;
            channelIdx += MathRoutines.clip(levelMin, 0, 2);
            channelIdx *= 3;
            channelIdx += MathRoutines.clip(levelMax - levelMin, 0, 2);
        }
        return new int[] { channelIdx, toX, toY };
    }
    
    public int moveTensorToInt(final int[] moveTensor) {
        final int[] moveTensorsShape = this.moveTensorsShape();
        return moveTensorsShape[1] * moveTensorsShape[2] * moveTensor[0] + moveTensorsShape[2] * moveTensor[1] + moveTensor[2];
    }
    
    public int moveToInt(final Move move) {
        return this.moveTensorToInt(this.moveToTensor(move));
    }
    
    public int numDistinctActions() {
        final int[] moveTensorsShape = this.moveTensorsShape();
        return moveTensorsShape[1] * moveTensorsShape[2] * moveTensorsShape[3];
    }
    
    public int maxGameLength() {
        return this.game.getMaxMoveLimit();
    }
    
    public float[] allOnesChannelFlat() {
        return this.ALL_ONES_CHANNEL_FLAT;
    }
    
    public float[] containerPositionChannels() {
        return this.CONTAINER_POSITION_CHANNELS;
    }
    
    private void computeTensorCoords() {
        if (this.game.hasSubgames()) {
            System.err.println("Computing tensors for Matches is not yet supported.");
            return;
        }
        final Container[] containers = this.game.equipment().containers();
        final List<? extends TopologyElement> graphElements = this.game.graphPlayElements();
        this.xCoords = new int[this.game.equipment().totalDefaultSites()];
        this.yCoords = new int[this.game.equipment().totalDefaultSites()];
        final int numBoardSites = graphElements.size();
        final List<? extends TopologyElement> sortedGraphElements = new ArrayList<TopologyElement>(graphElements);
        sortedGraphElements.sort((Comparator<TopologyElement>) (o1, o2) -> {
            if (o1.centroid().getX() < o2.centroid().getX()) {
                return -1;
            }
            if (o1.centroid().getX() == o2.centroid().getX()) {
                return 0;
            }
            return 1;
        });
        int currIdx = 0;
        double currXPos = sortedGraphElements.get(0).centroid().getX();
        for (final TopologyElement e : sortedGraphElements) {
            final double xPos = e.centroid().getX();
            if (xPos - 1.0E-5 > currXPos) {
                ++currIdx;
                currXPos = xPos;
            }
            this.xCoords[e.index()] = currIdx;
        }
        final int maxBoardIndexX = currIdx;
        sortedGraphElements.sort((Comparator<TopologyElement>) (o1, o2) -> {
            if (o1.centroid().getY() < o2.centroid().getY()) {
                return -1;
            }
            if (o1.centroid().getY() == o2.centroid().getY()) {
                return 0;
            }
            return 1;
        });
        currIdx = 0;
        double currYPos = sortedGraphElements.get(0).centroid().getY();
        for (final TopologyElement e2 : sortedGraphElements) {
            final double yPos = e2.centroid().getY();
            if (yPos - 1.0E-5 > currYPos) {
                ++currIdx;
                currYPos = yPos;
            }
            this.yCoords[e2.index()] = currIdx;
        }
        final int maxBoardIndexY = currIdx;
        this.tensorDimX = maxBoardIndexX + 1;
        this.tensorDimY = maxBoardIndexY + 1;
        final int numContainers = this.game.numContainers();
        if (numContainers > 1) {
            int maxNonBoardContIdx = -1;
            for (int c = 1; c < numContainers; ++c) {
                maxNonBoardContIdx = Math.max(containers[c].numSites() - 1, maxNonBoardContIdx);
            }
            boolean handsAsRows = false;
            if (maxBoardIndexX < maxBoardIndexY && maxNonBoardContIdx <= maxBoardIndexX) {
                handsAsRows = true;
            }
            else if (maxNonBoardContIdx > maxBoardIndexX && maxBoardIndexX > maxBoardIndexY) {
                handsAsRows = true;
            }
            if (handsAsRows) {
                ++this.tensorDimY;
                this.tensorDimY += numContainers - 1;
                if (maxNonBoardContIdx > maxBoardIndexX) {
                    this.tensorDimX += maxNonBoardContIdx - maxBoardIndexX;
                }
                int nextContStartIdx = numBoardSites;
                for (int c2 = 1; c2 < numContainers; ++c2) {
                    final Container cont = containers[c2];
                    for (int site = 0; site < cont.numSites(); ++site) {
                        this.xCoords[site + nextContStartIdx] = site;
                        this.yCoords[site + nextContStartIdx] = maxBoardIndexY + 1 + c2;
                    }
                    nextContStartIdx += cont.numSites();
                }
            }
            else {
                ++this.tensorDimX;
                this.tensorDimX += numContainers - 1;
                if (maxNonBoardContIdx > maxBoardIndexY) {
                    this.tensorDimY += maxNonBoardContIdx - maxBoardIndexY;
                }
                for (int c3 = 1; c3 < numContainers; ++c3) {
                    final Container cont2 = containers[c3];
                    int nextContStartIdx2 = numBoardSites;
                    for (int site = 0; site < cont2.numSites(); ++site) {
                        this.xCoords[site + nextContStartIdx2] = maxBoardIndexX + 1 + c3;
                        this.yCoords[site + nextContStartIdx2] = site;
                    }
                    nextContStartIdx2 += cont2.numSites();
                }
            }
        }
        final Component[] components = this.game.equipment().components();
        final int numPlayers = this.game.players().count();
        final int numPieceTypes = components.length - 1;
        final boolean stacking = this.game.isStacking();
        final boolean usesCount = this.game.requiresCount();
        final boolean usesAmount = this.game.requiresBet();
        final boolean usesState = this.game.requiresLocalState();
        final boolean usesSwap = this.game.usesSwapRule();
        final List<String> channelNames = new ArrayList<>();
        this.stateTensorNumChannels = (stacking ? (10 * numPieceTypes) : numPieceTypes);
        if (!stacking) {
            for (int e3 = 1; e3 <= numPieceTypes; ++e3) {
                channelNames.add("Piece Type " + e3 + " (" + components[e3].name() + ")");
            }
        }
        else {
            for (int e3 = 1; e3 <= numPieceTypes; ++e3) {
                for (int i = 0; i < 5; ++i) {
                    channelNames.add("Piece Type " + e3 + " (" + components[e3].name() + ") at level " + i + " from stack bottom.");
                }
                for (int i = 0; i < 5; ++i) {
                    channelNames.add("Piece Type " + e3 + " (" + components[e3].name() + ") at level " + i + " from stack top.");
                }
            }
        }
        if (stacking) {
            ++this.stateTensorNumChannels;
            channelNames.add("Stack sizes (non-binary channel!)");
        }
        if (usesCount) {
            ++this.stateTensorNumChannels;
            channelNames.add("Counts (non-binary channel!)");
        }
        if (usesAmount) {
            this.stateTensorNumChannels += numPlayers;
            for (int p = 1; p <= numPlayers; ++p) {
                channelNames.add("Amount for Player " + p);
            }
        }
        if (numPlayers > 1) {
            this.stateTensorNumChannels += numPlayers;
            for (int p = 1; p <= numPlayers; ++p) {
                channelNames.add("Is Player " + p + " the current mover?");
            }
        }
        if (usesState) {
            this.stateTensorNumChannels += 6;
            for (int j = 0; j < 6; ++j) {
                if (j + 1 == 6) {
                    channelNames.add("Local state >= " + j);
                }
                else {
                    channelNames.add("Local state == " + j);
                }
            }
        }
        if (usesSwap) {
            ++this.stateTensorNumChannels;
            channelNames.add("Did Swap Occur?");
        }
        this.stateTensorNumChannels += numContainers;
        for (int c4 = 0; c4 < numContainers; ++c4) {
            channelNames.add("Does position exist in container " + c4 + " (" + containers[c4].name() + ")?");
        }
        this.stateTensorNumChannels += 4;
        channelNames.add("Last move's from-position");
        channelNames.add("Last move's to-position");
        channelNames.add("Second-to-last move's from-position");
        channelNames.add("Second-to-last move's to-position");
        assert channelNames.size() == this.stateTensorNumChannels;
        this.stateTensorChannelNames = channelNames.toArray(new String[this.stateTensorNumChannels]);
        this.MOVE_PASS_CHANNEL_IDX = this.computeMovePassChannelIdx();
        this.MOVE_SWAP_CHANNEL_IDX = this.MOVE_PASS_CHANNEL_IDX + 1;
        Arrays.fill(this.ALL_ONES_CHANNEL_FLAT = new float[this.tensorDimX * this.tensorDimY], 1.0f);
        this.CONTAINER_POSITION_CHANNELS = new float[containers.length * this.tensorDimX * this.tensorDimY];
        for (int c4 = 0; c4 < containers.length; ++c4) {
            final Container cont3 = containers[c4];
            final int contStartSite = this.game.equipment().sitesFrom()[c4];
            for (int site2 = 0; site2 < cont3.numSites(); ++site2) {
                this.CONTAINER_POSITION_CHANNELS[this.yCoords[contStartSite + site2] + this.tensorDimY * (this.xCoords[contStartSite + site2] + c4 * this.tensorDimX)] = 1.0f;
            }
        }
    }
    
    private int computeMovePassChannelIdx() {
        final int numValsDiffX = 2 * this.moveTensorDistClip + 1;
        final int numValsDiffY = numValsDiffX * (2 * this.moveTensorDistClip + 1);
        if (!this.game.isStacking()) {
            return numValsDiffY;
        }
        final int numValsLevelMin = numValsDiffY * 3;
        final int numValsLevelMax = numValsLevelMin * 3;
        return numValsLevelMax;
    }
    
    public static void main(final String[] args) {
        final String[] listGames;
        final String[] gameNames = listGames = FileHandling.listGames();
        for (final String name : listGames) {
            if (!name.replaceAll(Pattern.quote("\\"), "/").contains("/wip/")) {
                if (!name.replaceAll(Pattern.quote("\\"), "/").contains("/wishlist/")) {
                    if (!name.replaceAll(Pattern.quote("\\"), "/").contains("/test/")) {
                        if (!name.replaceAll(Pattern.quote("\\"), "/").contains("/bad_playout/")) {
                            if (!name.replaceAll(Pattern.quote("\\"), "/").contains("/bad/")) {
                                if (!name.replaceAll(Pattern.quote("\\"), "/").contains("/plex/")) {
                                    if (!name.replaceAll(Pattern.quote("\\"), "/").contains("/math/graph/")) {
                                        System.out.println("name = " + name);
                                        final LudiiGameWrapper game = new LudiiGameWrapper(name);
                                        if (!game.game.hasSubgames()) {
                                            System.out.println("State tensor shape = " + Arrays.toString(game.stateTensorsShape()));
                                            System.out.println("Moves tensor shape = " + Arrays.toString(game.moveTensorsShape()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
