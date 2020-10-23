// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.component;

import annotations.Hide;
import game.equipment.Item;
import game.equipment.component.tile.Path;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.types.board.StepType;
import game.types.component.CardType;
import game.types.play.RoleType;
import game.util.directions.DirectionFacing;
import game.util.graph.Step;
import game.util.moves.Flips;
import gnu.trove.list.array.TIntArrayList;
import metadata.graphics.util.ComponentStyleType;
import topology.Topology;
import util.Context;
import util.ItemType;

import java.util.List;

public class Component extends Item implements Cloneable
{
    private final int value;
    private DirectionFacing dirn;
    private Moves generator;
    private boolean[][] potentialMoves;
    private final StepType[][] walk;
    protected String nameWithoutNumber;
    protected ComponentStyleType style;
    protected int[] bias;
    
    @Hide
    public Component(final String label, final RoleType role, final StepType[][] walk, final DirectionFacing dirn, final Integer value, final Moves generator) {
        super(label, -1, role);
        this.walk = walk;
        this.value = ((value == null) ? 1 : value);
        this.dirn = (dirn);
        this.generator = generator;
        this.setType(ItemType.Component);
    }
    
    public DirectionFacing getDirn() {
        return this.dirn;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public Flips getFlips() {
        return null;
    }
    
    public Moves generator() {
        return this.generator;
    }
    
    public Moves generate(final Context context) {
        if (this.generator != null) {
            return this.generator.eval(context);
        }
        return new BaseMoves(null);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Component)) {
            return false;
        }
        final Component comp = (Component)o;
        return this.name().equals(comp.name());
    }
    
    protected Component(final Component other) {
        super(other);
        this.dirn = other.dirn;
        this.generator = other.generator;
        this.value = other.value;
        if (other.bias != null) {
            this.bias = new int[other.bias.length];
            System.arraycopy(other.bias, 0, this.bias, 0, other.bias.length);
        }
        else {
            this.bias = null;
        }
        if (other.potentialMoves != null) {
            this.potentialMoves = new boolean[other.potentialMoves.length][];
            for (int i = 0; i < other.potentialMoves.length; ++i) {
                this.potentialMoves[i] = new boolean[other.potentialMoves[i].length];
                final int j = 0;
                while (0 < other.potentialMoves[i].length) {
                    this.potentialMoves[i][0] = other.potentialMoves[i][0];
                    ++i;
                }
            }
        }
        else {
            this.potentialMoves = null;
        }
        if (other.walk != null) {
            this.walk = new StepType[other.walk.length][];
            for (int i = 0; i < other.walk.length; ++i) {
                this.walk[i] = new StepType[other.walk[i].length];
                System.arraycopy(other.walk[i], 0, this.walk[i], 0, other.walk[i].length);
            }
        }
        else {
            this.walk = null;
        }
        this.style = other.style;
        this.nameWithoutNumber = other.nameWithoutNumber;
    }
    
    public Component clone() {
        return new Component(this);
    }
    
    public void setDirection(final DirectionFacing direction) {
        this.dirn = direction;
    }
    
    public void setMoves(final Moves generator) {
        this.generator = generator;
    }
    
    @Override
    public String toEnglish() {
        return "<Component>";
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    public boolean isLargePiece() {
        return this.walk != null;
    }
    
    public StepType[][] walk() {
        return this.walk;
    }
    
    public TIntArrayList locs(final Context context, final int startLoc, final int state, final Topology topology) {
        final int from = startLoc;
        if (from >= topology.cells().size()) {
            return new TIntArrayList();
        }
        final TIntArrayList sitesAfterWalk = new TIntArrayList();
        final int realState = Math.max(state, 0);
        final List<DirectionFacing> orthogonalSupported = topology.supportedOrthogonalDirections(SiteType.Cell);
        final DirectionFacing startDirection = orthogonalSupported.get(realState % orthogonalSupported.size());
        sitesAfterWalk.add(from);
        final StepType[] steps = this.walk[realState / orthogonalSupported.size()];
        int currentLoc = from;
        DirectionFacing currentDirection = startDirection;
        for (final StepType step : steps) {
            if (step == StepType.F) {
                final List<Step> stepsDirection = topology.trajectories().steps(SiteType.Cell, currentLoc, currentDirection.toAbsolute());
                int to = -1;
                for (final Step stepDirection : stepsDirection) {
                    if (stepDirection.from().siteType() != stepDirection.to().siteType()) {
                        continue;
                    }
                    to = stepDirection.to().id();
                }
                if (to == -1) {
                    return new TIntArrayList();
                }
                if (!sitesAfterWalk.contains(to)) {
                    sitesAfterWalk.add(to);
                }
                currentLoc = to;
            }
            else if (step == StepType.R) {
                for (currentDirection = currentDirection.right(); !orthogonalSupported.contains(currentDirection); currentDirection = currentDirection.right()) {}
            }
            else if (step == StepType.L) {
                for (currentDirection = currentDirection.left(); !orthogonalSupported.contains(currentDirection); currentDirection = currentDirection.left()) {}
            }
        }
        return sitesAfterWalk;
    }
    
    public int[] getBias() {
        return this.bias;
    }
    
    public void setBiased(final Integer[] biased) {
        if (biased != null) {
            this.bias = new int[biased.length];
            for (int i = 0; i < biased.length; ++i) {
                this.bias[i] = biased[i];
            }
        }
    }
    
    public boolean possibleMove(final int from, final int to) {
        return this.potentialMoves[from][to];
    }
    
    public boolean[][] possibleMoves() {
        return this.potentialMoves;
    }
    
    public void setPossibleMove(final boolean[][] possibleMoves) {
        this.potentialMoves = possibleMoves;
    }
    
    public boolean isDie() {
        return false;
    }
    
    public int[] getFaces() {
        return new int[0];
    }
    
    public int getNumFaces() {
        return -1;
    }
    
    public int roll(final Context context) {
        return -1;
    }
    
    public void setFaces(final Integer[] faces, final Integer start) {
    }
    
    public boolean isCard() {
        return false;
    }
    
    public int suit() {
        return -1;
    }
    
    public int trumpValue() {
        return -1;
    }
    
    public int rank() {
        return -1;
    }
    
    public int trumpRank() {
        return -1;
    }
    
    public CardType cardType() {
        return null;
    }
    
    public boolean isTile() {
        return false;
    }
    
    public int[] terminus() {
        return null;
    }
    
    public Integer numTerminus() {
        return -1;
    }
    
    public int numSides() {
        return -1;
    }
    
    public void setNumSides(final int numSides) {
    }
    
    public Path[] paths() {
        return null;
    }
    
    public boolean isDoubleDomino() {
        return false;
    }
    
    public int getValue2() {
        return -1;
    }
    
    public boolean isDomino() {
        return false;
    }
    
    public String getNameWithoutNumber() {
        return this.nameWithoutNumber;
    }
    
    public void setNameWithoutNumber(final String name) {
        this.nameWithoutNumber = name;
    }
    
    public ComponentStyleType style() {
        return this.style;
    }
    
    public void setStyle(final ComponentStyleType st) {
        this.style = st;
    }
    
    @Override
    public String credit() {
        if (this.nameWithoutNumber.equalsIgnoreCase("Sandbox")) {
            return this.nameWithoutNumber + " image from https://www.pngwing.com/en/free-png-nmodo.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Bear")) {
            return this.nameWithoutNumber + " image by Freepik from http://www.flaticon.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Seal")) {
            return this.nameWithoutNumber + " image by Freepik from http://www.flaticon.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Camel")) {
            return this.nameWithoutNumber + " image from https://www.pngrepo.com/svg/297513/camel.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Cat")) {
            return this.nameWithoutNumber + " image from http://getdrawings.com/cat-head-icon#cat-head-icon-8.png.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Chicken")) {
            return this.nameWithoutNumber + " image by Stila from https://favpng.com/png_view/.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Cow")) {
            return this.nameWithoutNumber + " image from https://www.nicepng.com/ourpic/u2w7o0t4e6y3a9u2_animals-chinese-new-year-icon/.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Dog")) {
            return this.nameWithoutNumber + " image from https://favpng.com/png_view/albatross-gray-wolf-clip-art-png/R6VmvfkC.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Crab")) {
            return this.nameWithoutNumber + " image by Freepik from http://www.flaticon.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Dog")) {
            return this.nameWithoutNumber + " image from https://www.pngwing.com/en/free-png-hwzbd.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Dove")) {
            return this.nameWithoutNumber + " image from https://www.pngwing.com/en/free-png-xxwye.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Dragon")) {
            return this.nameWithoutNumber + " image from https://ya-webdesign.com/imgdownload.html";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Duck")) {
            return this.nameWithoutNumber + " image from https://ya-webdesign.com/imgdownload.html";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Eagle")) {
            return this.nameWithoutNumber + " image from https://www.pngbarn.com/png-image-tgmlh.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Elephant")) {
            return this.nameWithoutNumber + " image from http://getdrawings.com/get-icon#elephant-icon-app-2.png.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Fish")) {
            return this.nameWithoutNumber + " image from https://www.svgrepo.com/svg/109765/fish.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Fox")) {
            return this.nameWithoutNumber + " image from https://www.svgrepo.com/svg/40267/fox.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Goat")) {
            return this.nameWithoutNumber + " image from https://ya-webdesign.com/imgdownload.html.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Goose")) {
            return this.nameWithoutNumber + " image from https://depositphotos.com/129413072/stock-illustration-web-goose-icon.html.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Hare")) {
            return this.nameWithoutNumber + " image by Freepik from https://www.flaticon.com/free-icon/.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Horse")) {
            return this.nameWithoutNumber + " image from https://commons.wikimedia.org/wiki/File:Chess_tile_nl.svg.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Jaguar")) {
            return this.nameWithoutNumber + " image from https://icons8.com/icons/set/jaguar.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Lamb")) {
            return this.nameWithoutNumber + " image from https://ya-webdesign.com/imgdownload.html.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Leopard")) {
            return this.nameWithoutNumber + " image from https://www.svgrepo.com/svg/297517/leopard.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Lion")) {
            return this.nameWithoutNumber + " image by Freepik from https://www.flaticon.com/free-icon/.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Lioness")) {
            return this.nameWithoutNumber + " image by Freepik from https://www.flaticon.com/free-icon/.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Monkey")) {
            return this.nameWithoutNumber + " image from https://www.pngbarn.com/png-image-eonln.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Mountainlion")) {
            return this.nameWithoutNumber + " image by Tae S Yang from https://icon-icons.com/nl/pictogram/puma-dier/123525.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Mouse")) {
            return this.nameWithoutNumber + " image by Freepik from https://www.flaticon.com/free-icon/mouse_235093.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Ox")) {
            return this.nameWithoutNumber + " image from https://www.svgrepo.com/svg/19280/cattle-skull.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Panther")) {
            return this.nameWithoutNumber + " image by Freepik from https://www.flaticon.com/free-icon/cat-face-outline_57104.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Penguin")) {
            return this.nameWithoutNumber + " image from https://ya-webdesign.com/imgdownload.html.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Prawn")) {
            return this.nameWithoutNumber + " image by Freepik from https://www.flaticon.com/free-icon/prawn_202274.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Puma")) {
            return this.nameWithoutNumber + " image by Tae S Yang from https://icon-icons.com/nl/pictogram/puma-dier/123525.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Rabbit")) {
            return this.nameWithoutNumber + " image from https://ya-webdesign.com/imgdownload.html.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Rat")) {
            return this.nameWithoutNumber + " image from https://webstockreview.net/image/clipart-rat-head-cartoon/642646.html.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Rhino")) {
            return this.nameWithoutNumber + " image by Freepik from https://www.flaticon.com/free-icon/.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Seal")) {
            return this.nameWithoutNumber + " image by Freepik from https://www.flaticon.com/free-icon/.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Sheep")) {
            return this.nameWithoutNumber + " image from https://www.pngwing.com/en/free-png-nirzv.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Snake")) {
            return this.nameWithoutNumber + " image by Freepik from https://www.flaticon.com/free-icon/.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Tiger")) {
            return this.nameWithoutNumber + " image from https://www.pngwing.com/en/free-png-hbgdy.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Wolf")) {
            return this.nameWithoutNumber + " image by Freepik from https://www.flaticon.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Jack")) {
            return this.nameWithoutNumber + " image by popicon from https://www.shutterstock.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Joker")) {
            return this.nameWithoutNumber + " image \"Joker Icon\" from https://icons8.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("King")) {
            return this.nameWithoutNumber + " image from https://www.pngwing.com/en/free-png-ptuag.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Queen")) {
            return this.nameWithoutNumber + " image from https://www.pngguru.com/free-transparent-background-png-clipart-tlaxu.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Card-suit-club")) {
            return this.nameWithoutNumber + " image from https://en.wikipedia.org/wiki/File:Card_club.svg.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Card-suit-diamond")) {
            return this.nameWithoutNumber + " image from https://en.wikipedia.org/wiki/File:Card_diamond.svg.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Card-suit-heart")) {
            return this.nameWithoutNumber + " image from https://en.wikipedia.org/wiki/File:Card_heart.svg.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Card-suit-spade")) {
            return this.nameWithoutNumber + " image from https://en.wikipedia.org/wiki/File:Card_spade.svg.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("CardBack")) {
            return this.nameWithoutNumber + " image from Symbola TTF font.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("counter") || this.nameWithoutNumber.equalsIgnoreCase("counterstar") || this.nameWithoutNumber.equalsIgnoreCase("doublecounter")) {
            return this.nameWithoutNumber + " image from Symbola TTF font.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Bishop") || this.nameWithoutNumber.equalsIgnoreCase("King") || this.nameWithoutNumber.equalsIgnoreCase("Knight") || this.nameWithoutNumber.equalsIgnoreCase("Pawn") || this.nameWithoutNumber.equalsIgnoreCase("Queen") || this.nameWithoutNumber.equalsIgnoreCase("Rook")) {
            return this.nameWithoutNumber + " images from the Casefont, Arial Unicode MS, PragmataPro and Symbola TTF fonts.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("symbola_cool") || this.nameWithoutNumber.equalsIgnoreCase("symbola_happy") || this.nameWithoutNumber.equalsIgnoreCase("symbola_neutral") || this.nameWithoutNumber.equalsIgnoreCase("symbola_pleased") || this.nameWithoutNumber.equalsIgnoreCase("symbola_sad") || this.nameWithoutNumber.equalsIgnoreCase("symbola_scared") || this.nameWithoutNumber.equalsIgnoreCase("symbola_worried")) {
            return this.nameWithoutNumber + " image from Symbola TTF font.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Amazon")) {
            return this.nameWithoutNumber + " image from images from the Arial Unicode MS, PragmataPro and Symbola \\n\"\nTTF fonts and https://www.pngwing.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Bishop_noCross")) {
            return this.nameWithoutNumber + " images from the Arial Unicode MS, PragmataPro and Symbola \\n\"\nTTF fonts and https://www.pngwing.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Boat")) {
            return this.nameWithoutNumber + " image by Freepik from https://www.flaticon.com/free-icon/viking-ship_22595.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Cannon")) {
            return this.nameWithoutNumber + " image from https://www.pngbarn.com/.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Chariot")) {
            return this.nameWithoutNumber + " image by Freepik from https://www.flaticon.com/free-icon/wheel_317722.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Commoner")) {
            return this.nameWithoutNumber + " image by Sunny3113 from \nhttps://commons.wikimedia.org/wiki/File:Commoner_Transparent.svg \nunder license https://creativecommons.org/licenses/by-sa/4.0/deed.en.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Ferz_noCross")) {
            return this.nameWithoutNumber + " images from the Arial Unicode MS, PragmataPro and Symbola \nTTF fonts and https://www.pngwing.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Ferz")) {
            return this.nameWithoutNumber + " images from the Arial Unicode MS, PragmataPro and Symbola \nTTF fonts and https://www.pngwing.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Flag")) {
            return this.nameWithoutNumber + " image from https://www.pngwing.com/en/free-png-siuwt.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Fool")) {
            return this.nameWithoutNumber + " image by Mykola Dolgalov based on Omega Chess Advanced from \nhttps://commons.wikimedia.org/wiki/File:Chess_tll45.svg under \nlicense https://creativecommons.org/licenses/by-sa/3.0/deed.en.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Giraffe")) {
            return this.nameWithoutNumber + " image from https://www.pngfuel.com/free-png/tfali.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("King_noCross")) {
            return this.nameWithoutNumber + " images from the Arial Unicode MS, PragmataPro and Symbola \\n\"\nTTF fonts and https://www.pngwing.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Knight_bishop")) {
            return this.nameWithoutNumber + " image by OMega Chess Fan derivative work of NikNaks93 from \nhttps://en.wikipedia.org/wiki/Princess_(chess)#/media/File:Chess_alt45.svg under \nlicense https://creativecommons.org/licenses/by-sa/3.0/.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Knight_king")) {
            return this.nameWithoutNumber + " image from https://www.pngwing.com/en/free-png-ynnmd.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Knight_queen")) {
            return this.nameWithoutNumber + " image bu NikNaks from https://commons.wikimedia.org/wiki/File:Chess_Alt26.svg \nunder license https://creativecommons.org/licenses/by-sa/3.0/deed.en.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Knight_rook")) {
            return this.nameWithoutNumber + " image byfrom https://en.wikipedia.org/wiki/Empress_(chess)#/media/File:Chess_clt45.svg.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Knight-rotated")) {
            return this.nameWithoutNumber + " image from the Arial Unicode MS, PragmataPro and Symbola \\n\"\nTTF fonts and https://www.pngwing.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Mann")) {
            return this.nameWithoutNumber + " image by CheChe from the original by LithiumFlash from \nhttps://commons.wikimedia.org/wiki/File:Chess_Mlt45.svg.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Moon")) {
            return this.nameWithoutNumber + " image from https://www.freeiconspng.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Unicorn")) {
            return this.nameWithoutNumber + " image by CBurnett and Francois-Pier from \nhttps://commons.wikimedia.org/wiki/File:Chess_Ult45.svg under \nlicense https://www.gnu.org/licenses/gpl-3.0.html.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Wazir")) {
            return this.nameWithoutNumber + " images from the Arial Unicode MS, PragmataPro and Symbola \\n\"\nTTF fonts and https://www.pngwing.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Zebra-neck")) {
            return this.nameWithoutNumber + " image from https://imgbin.com/png/qH6bNDwM/.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Zebra")) {
            return this.nameWithoutNumber + " image by Francois-PIer after CBurnett from \nhttps://commons.wikimedia.org/wiki/File:Chess_Zlt45.svg under \nlicense https://creativecommons.org/licenses/by-sa/3.0/deed.en.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("hand0") || this.nameWithoutNumber.equalsIgnoreCase("hand1") || this.nameWithoutNumber.equalsIgnoreCase("hand2") || this.nameWithoutNumber.equalsIgnoreCase("hand3") || this.nameWithoutNumber.equalsIgnoreCase("hand4") || this.nameWithoutNumber.equalsIgnoreCase("hand5") || this.nameWithoutNumber.equalsIgnoreCase("paper") || this.nameWithoutNumber.equalsIgnoreCase("rock") || this.nameWithoutNumber.equalsIgnoreCase("scissors")) {
            return this.nameWithoutNumber + " image based on \"Click - Index Finger Clip Art\" by Adanteh \nfrom https://favpng.com/png_view/click-index-finger-clip-art-png/NJXExGMM.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("2human_knee") || this.nameWithoutNumber.equalsIgnoreCase("2human") || this.nameWithoutNumber.equalsIgnoreCase("3ankh_side") || this.nameWithoutNumber.equalsIgnoreCase("3ankh") || this.nameWithoutNumber.equalsIgnoreCase("3bird") || this.nameWithoutNumber.equalsIgnoreCase("3nefer") || this.nameWithoutNumber.equalsIgnoreCase("ankh_waset") || this.nameWithoutNumber.equalsIgnoreCase("water") || this.nameWithoutNumber.equalsIgnoreCase("senetpiece") || this.nameWithoutNumber.equalsIgnoreCase("senetpiece2")) {
            return this.nameWithoutNumber + " image part of the AegyptusSubset TTF font , from:\nhttps://mjn.host.cs.st-andrews.ac.uk/egyptian/fonts/newgardiner.html.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Byeong") || this.nameWithoutNumber.equalsIgnoreCase("Cha") || this.nameWithoutNumber.equalsIgnoreCase("Cho") || this.nameWithoutNumber.equalsIgnoreCase("Han") || this.nameWithoutNumber.equalsIgnoreCase("Jol") || this.nameWithoutNumber.equalsIgnoreCase("Majanggi") || this.nameWithoutNumber.equalsIgnoreCase("Po") || this.nameWithoutNumber.equalsIgnoreCase("Sa") || this.nameWithoutNumber.equalsIgnoreCase("Sang")) {
            return this.nameWithoutNumber + " image created by Matthew Stephenson for Ludii from the Casefont TTF font.";
        }
        if (this.nameWithoutNumber.length() == 1) {
            final char ch = Character.toUpperCase(this.nameWithoutNumber.charAt(0));
            if (ch >= 'A' && ch <= 'Z') {
                return this.nameWithoutNumber + " image from the Arial TTF font.";
            }
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("BambooOne") || this.nameWithoutNumber.equalsIgnoreCase("BambooTwo") || this.nameWithoutNumber.equalsIgnoreCase("BambooThree") || this.nameWithoutNumber.equalsIgnoreCase("BambooFour") || this.nameWithoutNumber.equalsIgnoreCase("BambooFive") || this.nameWithoutNumber.equalsIgnoreCase("BambooSix") || this.nameWithoutNumber.equalsIgnoreCase("BambooSeven") || this.nameWithoutNumber.equalsIgnoreCase("BambooEight") || this.nameWithoutNumber.equalsIgnoreCase("BambooNine") || this.nameWithoutNumber.equalsIgnoreCase("CharacterOne") || this.nameWithoutNumber.equalsIgnoreCase("CharacterTwo") || this.nameWithoutNumber.equalsIgnoreCase("CharacterThree") || this.nameWithoutNumber.equalsIgnoreCase("CharacterFour") || this.nameWithoutNumber.equalsIgnoreCase("CharacterFive") || this.nameWithoutNumber.equalsIgnoreCase("CharacterSix") || this.nameWithoutNumber.equalsIgnoreCase("CharacterSeven") || this.nameWithoutNumber.equalsIgnoreCase("CharacterEight") || this.nameWithoutNumber.equalsIgnoreCase("CharacterNine") || this.nameWithoutNumber.equalsIgnoreCase("CircleOne") || this.nameWithoutNumber.equalsIgnoreCase("CircleTwo") || this.nameWithoutNumber.equalsIgnoreCase("CircleThree") || this.nameWithoutNumber.equalsIgnoreCase("CircleFour") || this.nameWithoutNumber.equalsIgnoreCase("CircleFive") || this.nameWithoutNumber.equalsIgnoreCase("CircleSix") || this.nameWithoutNumber.equalsIgnoreCase("CircleSeven") || this.nameWithoutNumber.equalsIgnoreCase("CircleEight") || this.nameWithoutNumber.equalsIgnoreCase("CircleNine") || this.nameWithoutNumber.equalsIgnoreCase("DragonGreen") || this.nameWithoutNumber.equalsIgnoreCase("DragonRed") || this.nameWithoutNumber.equalsIgnoreCase("DragonWhite") || this.nameWithoutNumber.equalsIgnoreCase("FlowerBamboo") || this.nameWithoutNumber.equalsIgnoreCase("FlowerChrysanthemum") || this.nameWithoutNumber.equalsIgnoreCase("FlowerOrchid") || this.nameWithoutNumber.equalsIgnoreCase("FlowerPlum") || this.nameWithoutNumber.equalsIgnoreCase("SeasonAutumn") || this.nameWithoutNumber.equalsIgnoreCase("SeasonSpring") || this.nameWithoutNumber.equalsIgnoreCase("SeasonSummer") || this.nameWithoutNumber.equalsIgnoreCase("SeasonWinter") || this.nameWithoutNumber.equalsIgnoreCase("TileBack") || this.nameWithoutNumber.equalsIgnoreCase("TileJoker") || this.nameWithoutNumber.equalsIgnoreCase("WindEast") || this.nameWithoutNumber.equalsIgnoreCase("WindNorth") || this.nameWithoutNumber.equalsIgnoreCase("WindSouth") || this.nameWithoutNumber.equalsIgnoreCase("WindWest")) {
            return this.nameWithoutNumber + " image from the Symbola TTF font.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("bean")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("bike")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("bread")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("castle")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("corn")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("cross")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("diamond")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("disc")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("discDouble")) {
            return this.nameWithoutNumber + " edited image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("discDoubleStick")) {
            return this.nameWithoutNumber + " edited image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("discStick")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("dot")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("egyptLion")) {
            return this.nameWithoutNumber + " image part of the AegyptusSubset TTF font, from:\nhttps://mjn.host.cs.st-andrews.ac.uk/egyptian/fonts/newgardiner.html.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("flower")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("flowerHalf1")) {
            return this.nameWithoutNumber + " edited image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("flowerHalf2")) {
            return this.nameWithoutNumber + " edited image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("hex")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("hexE")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("none")) {
            return this.nameWithoutNumber + " edited image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("octagon")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("paddle")) {
            return this.nameWithoutNumber + " edited image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("pentagon")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("pyramid")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("rectangle")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("square")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("star")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("starOutline")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("thinCross")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("triangle")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("urpiece")) {
            return this.nameWithoutNumber + " image created by Matthew Stephenson for Ludii.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("waves")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("oldMan")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("boy")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("theseus")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("minotaur")) {
            return this.nameWithoutNumber + " image from https://www.flaticon.com/free-icon/minotaur_1483069.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("robot")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("door")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("human")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("rubble")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Commander") || this.nameWithoutNumber.equalsIgnoreCase("LanceT") || this.nameWithoutNumber.equalsIgnoreCase("LanceW") || this.nameWithoutNumber.equalsIgnoreCase("LanceY") || this.nameWithoutNumber.equalsIgnoreCase("ProbeBigV") || this.nameWithoutNumber.equalsIgnoreCase("ProbeMinV") || this.nameWithoutNumber.equalsIgnoreCase("Shield")) {
            return this.nameWithoutNumber + " image created by Matthew Stephenson for Ludii.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("Salta1Dot") || this.nameWithoutNumber.equalsIgnoreCase("Salta2Dot") || this.nameWithoutNumber.equalsIgnoreCase("Salta3Dot") || this.nameWithoutNumber.equalsIgnoreCase("Salta4Dot") || this.nameWithoutNumber.equalsIgnoreCase("Salta5Dot") || this.nameWithoutNumber.equalsIgnoreCase("Salta1Moon") || this.nameWithoutNumber.equalsIgnoreCase("Salta2Moon") || this.nameWithoutNumber.equalsIgnoreCase("Salta3Moon") || this.nameWithoutNumber.equalsIgnoreCase("Salta4Moon") || this.nameWithoutNumber.equalsIgnoreCase("Salta5Moon") || this.nameWithoutNumber.equalsIgnoreCase("Salta1Star") || this.nameWithoutNumber.equalsIgnoreCase("Salta2Star") || this.nameWithoutNumber.equalsIgnoreCase("Salta3Star") || this.nameWithoutNumber.equalsIgnoreCase("Salta4Star") || this.nameWithoutNumber.equalsIgnoreCase("Salta5Star")) {
            return this.nameWithoutNumber + " image created by Matthew Stephenson for Ludii.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("fuhyo") || this.nameWithoutNumber.equalsIgnoreCase("ginsho") || this.nameWithoutNumber.equalsIgnoreCase("hisha") || this.nameWithoutNumber.equalsIgnoreCase("kakugyo") || this.nameWithoutNumber.equalsIgnoreCase("keima") || this.nameWithoutNumber.equalsIgnoreCase("kinsho") || this.nameWithoutNumber.equalsIgnoreCase("kyosha") || this.nameWithoutNumber.equalsIgnoreCase("narigin") || this.nameWithoutNumber.equalsIgnoreCase("narikei") || this.nameWithoutNumber.equalsIgnoreCase("narikyo") || this.nameWithoutNumber.equalsIgnoreCase("osho") || this.nameWithoutNumber.equalsIgnoreCase("osho1") || this.nameWithoutNumber.equalsIgnoreCase("ryuma") || this.nameWithoutNumber.equalsIgnoreCase("ryuo") || this.nameWithoutNumber.equalsIgnoreCase("tokin")) {
            return this.nameWithoutNumber + " image created by Matthew Stephenson for Ludii, using the Quivira and Arial TTF fonts.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("shogi_blank")) {
            return this.nameWithoutNumber + " image from the Quivira TTF font.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("oldMan0") || this.nameWithoutNumber.equalsIgnoreCase("oldMan1") || this.nameWithoutNumber.equalsIgnoreCase("oldWoman0") || this.nameWithoutNumber.equalsIgnoreCase("oldWoman1") || this.nameWithoutNumber.equalsIgnoreCase("youngMan0") || this.nameWithoutNumber.equalsIgnoreCase("youngMan1") || this.nameWithoutNumber.equalsIgnoreCase("youngWoman0") || this.nameWithoutNumber.equalsIgnoreCase("youngWoman1")) {
            return this.nameWithoutNumber + " image created by Matthew Stephenson for Ludii.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("bomb") || this.nameWithoutNumber.equalsIgnoreCase("captain") || this.nameWithoutNumber.equalsIgnoreCase("colonel") || this.nameWithoutNumber.equalsIgnoreCase("flag") || this.nameWithoutNumber.equalsIgnoreCase("general") || this.nameWithoutNumber.equalsIgnoreCase("lieutenant") || this.nameWithoutNumber.equalsIgnoreCase("major") || this.nameWithoutNumber.equalsIgnoreCase("marshal") || this.nameWithoutNumber.equalsIgnoreCase("miner") || this.nameWithoutNumber.equalsIgnoreCase("scout") || this.nameWithoutNumber.equalsIgnoreCase("sergeant") || this.nameWithoutNumber.equalsIgnoreCase("spy")) {
            return this.nameWithoutNumber + " image courtesy of Sjoerd Langkemper.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("jarl") || this.nameWithoutNumber.equalsIgnoreCase("thrall")) {
            return this.nameWithoutNumber + " image from chess.medium OTF font.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("knotSquare")) {
            return this.nameWithoutNumber + " by Smeshinka from https://www.dreamstime.com/.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("knotTriangle")) {
            return this.nameWithoutNumber + " image from https://www.flaticon.com/free-icon/triquetra_1151995.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("bow") || this.nameWithoutNumber.equalsIgnoreCase("catapult") || this.nameWithoutNumber.equalsIgnoreCase("crossbow") || this.nameWithoutNumber.equalsIgnoreCase("knife") || this.nameWithoutNumber.equalsIgnoreCase("scimitar") || this.nameWithoutNumber.equalsIgnoreCase("smallSword") || this.nameWithoutNumber.equalsIgnoreCase("sword")) {
            return this.nameWithoutNumber + " image from svgrepo.com.";
        }
        if (this.nameWithoutNumber.equalsIgnoreCase("jiang") || this.nameWithoutNumber.equalsIgnoreCase("ju") || this.nameWithoutNumber.equalsIgnoreCase("ma") || this.nameWithoutNumber.equalsIgnoreCase("pao") || this.nameWithoutNumber.equalsIgnoreCase("shi") || this.nameWithoutNumber.equalsIgnoreCase("xiang") || this.nameWithoutNumber.equalsIgnoreCase("zu")) {
            return this.nameWithoutNumber + " image from BabelStoneXiangqi, Casefont, Arial Unicode MS, PragmataPro and Symbola TTF fonts.";
        }
        return null;
    }
    
    public int maxStepsForward() {
        int maxStepsForward = 0;
        for (int i = 0; i < this.walk().length; ++i) {
            int stepsForward = 0;
            for (int j = 0; j < this.walk()[i].length; ++j) {
                if (this.walk()[i][j] == StepType.F) {
                    ++stepsForward;
                }
            }
            if (stepsForward > maxStepsForward) {
                maxStepsForward = stepsForward;
            }
        }
        return maxStepsForward;
    }
}
