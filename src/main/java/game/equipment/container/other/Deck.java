// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.container.other;

import annotations.Name;
import annotations.Opt;
import game.equipment.component.Component;
import game.equipment.container.Container;
import game.functions.dim.DimConstant;
import game.functions.graph.generators.basis.hex.RectangleOnHex;
import game.functions.graph.generators.basis.square.RectangleOnSquare;
import game.functions.graph.generators.basis.tri.RectangleOnTri;
import game.types.board.SiteType;
import game.types.component.CardType;
import game.types.play.RoleType;
import game.util.equipment.Card;
import game.util.graph.Face;
import game.util.graph.Graph;
import game.util.graph.Vertex;
import gnu.trove.list.array.TIntArrayList;
import metadata.graphics.util.ContainerStyleType;
import topology.Cell;
import topology.Topology;
import util.ItemType;

import java.util.ArrayList;
import java.util.List;

public class Deck extends Container
{
    private static final long serialVersionUID = 1L;
    private final Integer cardsBySuit;
    private final Integer suits;
    private final Integer[] ranks;
    private final Integer[] values;
    private final Integer[] trumpRanks;
    private final Integer[] trumpValues;
    private final Integer[] biased;
    private final CardType[] types;
    private final TIntArrayList indexComponent;
    protected int numLocs;
    
    public Deck(@Opt final RoleType role, @Opt @Name final Integer cardsBySuit, @Opt @Name final Integer suits, @Opt final Card[] cards) {
        super(null, -1, (role == null) ? RoleType.Shared : role);
        final String className = this.getClass().toString();
        final String containerName = className.substring(className.lastIndexOf(46) + 1);
        final RoleType realRole = (role == null) ? RoleType.Shared : role;
        if (realRole.owner() > 0 && realRole.owner() <= 16) {
            if (this.name() == null) {
                this.setName(containerName + realRole.owner());
            }
        }
        else if (realRole == RoleType.Neutral) {
            if (this.name() == null) {
                this.setName(containerName + realRole.owner());
            }
        }
        else if (realRole == RoleType.Shared && this.name() == null) {
            this.setName(containerName + realRole.owner());
        }
        this.numLocs = 1;
        this.style = ContainerStyleType.Hand;
        this.setType(ItemType.Hand);
        this.cardsBySuit = ((cardsBySuit == null && cards == null) ? Integer.valueOf(13) : ((cards != null) ? Integer.valueOf(cards.length) : cardsBySuit));
        this.suits = ((suits == null) ? Integer.valueOf(4) : suits);
        final Integer[] valuesOfCards = (cards == null) ? null : new Integer[cards.length];
        final Integer[] trumpValuesOfCards = (cards == null) ? null : new Integer[cards.length];
        final Integer[] ranksOfCards = (cards == null) ? null : new Integer[cards.length];
        final Integer[] trumpRanksOfCards = (cards == null) ? null : new Integer[cards.length];
        final Integer[] biasedValuesOfCards = (cards == null) ? null : new Integer[cards.length];
        CardType[] typeOfCards = (cards == null) ? null : new CardType[cards.length];
        if (cards != null) {
            for (int i = 0; i < cards.length; ++i) {
                final Card card = cards[i];
                valuesOfCards[i] = card.value();
                trumpValuesOfCards[i] = card.trumpValue();
                ranksOfCards[i] = card.rank();
                biasedValuesOfCards[i] = card.biased();
                trumpRanksOfCards[i] = card.trumpRank();
                typeOfCards[i] = card.type();
            }
        }
        final Integer[] val = (valuesOfCards == null) ? new Integer[(int)this.cardsBySuit] : valuesOfCards;
        if (val[0] == null) {
            for (int j = 1; j <= this.cardsBySuit; ++j) {
                val[j - 1] = j;
            }
        }
        if (typeOfCards == null) {
            typeOfCards = new CardType[(int)this.cardsBySuit];
            for (int j = 1; j <= this.cardsBySuit; ++j) {
                typeOfCards[j - 1] = CardType.values()[j];
            }
        }
        this.types = typeOfCards;
        this.values = val;
        this.trumpValues = ((trumpValuesOfCards == null) ? val : trumpValuesOfCards);
        this.trumpRanks = ((trumpRanksOfCards == null) ? val : trumpRanksOfCards);
        this.ranks = ((ranksOfCards == null) ? val : ranksOfCards);
        this.biased = biasedValuesOfCards;
        this.indexComponent = new TIntArrayList();
    }
    
    protected Deck(final Deck other) {
        super(other);
        this.cardsBySuit = other.cardsBySuit;
        this.suits = other.suits;
        this.indexComponent = other.indexComponent;
        if (other.biased != null) {
            this.biased = new Integer[other.biased.length];
            for (int i = 0; i < other.biased.length; ++i) {
                this.biased[i] = other.biased[i];
            }
        }
        else {
            this.biased = null;
        }
        if (other.ranks != null) {
            this.ranks = new Integer[other.ranks.length];
            for (int i = 0; i < other.ranks.length; ++i) {
                this.ranks[i] = other.ranks[i];
            }
        }
        else {
            this.ranks = null;
        }
        if (other.values != null) {
            this.values = new Integer[other.values.length];
            for (int i = 0; i < other.values.length; ++i) {
                this.values[i] = other.values[i];
            }
        }
        else {
            this.values = null;
        }
        if (other.trumpRanks != null) {
            this.trumpRanks = new Integer[other.trumpRanks.length];
            for (int i = 0; i < other.trumpRanks.length; ++i) {
                this.trumpRanks[i] = other.trumpRanks[i];
            }
        }
        else {
            this.trumpRanks = null;
        }
        if (other.trumpValues != null) {
            this.trumpValues = new Integer[other.trumpValues.length];
            for (int i = 0; i < other.trumpValues.length; ++i) {
                this.trumpValues[i] = other.trumpValues[i];
            }
        }
        else {
            this.trumpValues = null;
        }
        if (other.types != null) {
            this.types = new CardType[other.types.length];
            for (int i = 0; i < other.types.length; ++i) {
                this.types[i] = other.types[i];
            }
        }
        else {
            this.types = null;
        }
    }
    
    @Override
    public Deck clone() {
        return new Deck(this);
    }
    
    public List<Component> generateCards(final int indexCard, final int cid) {
        final List<Component> cards = new ArrayList<>();
        int i = cid;
        int cardIndex = indexCard;
        for (int indexSuit = 1; indexSuit <= this.suits(); ++indexSuit) {
            for (int indexCardSuit = 0; indexCardSuit < this.cardsBySuits(); ++indexCardSuit) {
                final game.equipment.component.Card card = new game.equipment.component.Card("Card" + cardIndex, this.role(), this.types()[indexCardSuit], this.ranks()[indexCardSuit], this.values()[indexCardSuit], this.trumpRanks()[indexCardSuit], this.trumpValues()[indexCardSuit], indexSuit, null);
                card.setBiased(this.getBiased());
                cards.add(card);
                this.indexComponent().add(i);
                ++i;
                ++cardIndex;
            }
        }
        return cards;
    }
    
    public Integer[] getBiased() {
        return this.biased;
    }
    
    public Integer[] ranks() {
        return this.ranks;
    }
    
    public Integer[] values() {
        return this.values;
    }
    
    public Integer suits() {
        return this.suits;
    }
    
    public CardType[] types() {
        return this.types;
    }
    
    public Integer cardsBySuits() {
        return this.cardsBySuit;
    }
    
    public Integer[] trumpValues() {
        return this.trumpValues;
    }
    
    public Integer[] trumpRanks() {
        return this.trumpRanks;
    }
    
    public TIntArrayList indexComponent() {
        return this.indexComponent;
    }
    
    @Override
    public boolean isDeck() {
        return true;
    }
    
    @Override
    public boolean isHand() {
        return true;
    }
    
    @Override
    public void createTopology(final int beginIndex, final int numEdge) {
        final double unit = 1.0 / this.numLocs;
        this.topology = new Topology();
        final int realNumEdge = (numEdge == -1) ? 4 : numEdge;
        Graph graph = null;
        if (realNumEdge == 6) {
            graph = new RectangleOnHex(new DimConstant(1), new DimConstant(this.numLocs)).eval(null, SiteType.Cell);
        }
        else if (realNumEdge == 3) {
            graph = new RectangleOnTri(new DimConstant(1), new DimConstant(this.numLocs)).eval(null, SiteType.Cell);
        }
        else {
            graph = new RectangleOnSquare(new DimConstant(1), new DimConstant(this.numLocs), null, null).eval(null, SiteType.Cell);
        }
        for (int i = 0; i < graph.faces().size(); ++i) {
            final Face face = graph.faces().get(i);
            final Cell cell = new Cell(face.id() + beginIndex, face.pt().x() + i * unit, face.pt().y(), face.pt().z());
            cell.setCoord(cell.row(), cell.col(), 0);
            cell.setCentroid(face.pt().x(), face.pt().y(), 0.0);
            this.topology.cells().add(cell);
            for (final Vertex v : face.vertices()) {
                final double x = v.pt().x();
                final double y = v.pt().y();
                final double z = v.pt().z();
                final topology.Vertex vertex = new topology.Vertex(-1, x, y, z);
                cell.vertices().add(vertex);
            }
        }
        this.numSites = this.topology.cells().size();
    }
    
    public static int numLocs() {
        return 1;
    }
}
