// 
// Decompiled by Procyon v0.5.36
// 

package features.instances;

import features.features.Feature;
import game.types.board.SiteType;
import main.collections.ChunkSet;
import main.math.BitTwiddling;
import util.state.State;
import util.state.containerState.ContainerState;

import java.util.*;

public final class FeatureInstance implements BitwiseTest
{
    protected final Feature parentFeature;
    protected final int anchorSite;
    protected final int reflection;
    protected final float rotation;
    protected final SiteType graphElementType;
    protected ChunkSet mustEmpty;
    protected ChunkSet mustNotEmpty;
    protected ChunkSet mustWho;
    protected ChunkSet mustWhoMask;
    protected ChunkSet mustNotWho;
    protected ChunkSet mustNotWhoMask;
    protected ChunkSet mustWhat;
    protected ChunkSet mustWhatMask;
    protected ChunkSet mustNotWhat;
    protected ChunkSet mustNotWhatMask;
    protected transient boolean allRestrictionsNull;
    protected int toPosition;
    protected int fromPosition;
    protected int lastToPosition;
    protected int lastFromPosition;
    
    public FeatureInstance(final Feature parentFeature, final int anchorSite, final int reflection, final float rotation, final SiteType graphElementType) {
        this.parentFeature = parentFeature;
        this.anchorSite = anchorSite;
        this.reflection = reflection;
        this.rotation = rotation;
        this.graphElementType = graphElementType;
        this.mustEmpty = null;
        this.mustNotEmpty = null;
        this.mustWho = null;
        this.mustWhoMask = null;
        this.mustNotWho = null;
        this.mustNotWhoMask = null;
        this.mustWhat = null;
        this.mustWhatMask = null;
        this.mustNotWhat = null;
        this.mustNotWhatMask = null;
        this.allRestrictionsNull = true;
        this.toPosition = -1;
        this.fromPosition = -1;
        this.lastToPosition = -1;
        this.lastFromPosition = -1;
    }
    
    public FeatureInstance(final FeatureInstance other) {
        this.parentFeature = other.parentFeature;
        this.anchorSite = other.anchorSite;
        this.reflection = other.reflection;
        this.rotation = other.rotation;
        this.graphElementType = other.graphElementType;
        this.mustEmpty = ((other.mustEmpty == null) ? null : other.mustEmpty.clone());
        this.mustNotEmpty = ((other.mustNotEmpty == null) ? null : other.mustNotEmpty.clone());
        this.mustWho = ((other.mustWho == null) ? null : other.mustWho.clone());
        this.mustWhoMask = ((other.mustWhoMask == null) ? null : other.mustWhoMask.clone());
        this.mustNotWho = ((other.mustNotWho == null) ? null : other.mustNotWho.clone());
        this.mustNotWhoMask = ((other.mustNotWhoMask == null) ? null : other.mustNotWhoMask.clone());
        this.mustWhat = ((other.mustWhat == null) ? null : other.mustWhat.clone());
        this.mustWhatMask = ((other.mustWhatMask == null) ? null : other.mustWhatMask.clone());
        this.mustNotWhat = ((other.mustNotWhat == null) ? null : other.mustNotWhat.clone());
        this.mustNotWhatMask = ((other.mustNotWhatMask == null) ? null : other.mustNotWhatMask.clone());
        this.allRestrictionsNull = other.allRestrictionsNull;
        this.toPosition = other.toPosition;
        this.fromPosition = other.fromPosition;
        this.lastToPosition = other.lastToPosition;
        this.lastFromPosition = other.lastFromPosition;
    }
    
    public boolean addTest(final ContainerState container, final Feature.BitSetTypes bitSetType, final int testSite, final boolean active) {
        return this.addTest(container, bitSetType, testSite, active, -1);
    }
    
    public boolean addTest(final ContainerState container, final Feature.BitSetTypes bitSetType, final int testSite, final boolean active, final int value) {
        if (bitSetType == Feature.BitSetTypes.Empty) {
            if (active) {
                if (this.mustNotEmpty != null && this.mustNotEmpty.get(testSite)) {
                    return false;
                }
                if (this.mustEmpty == null) {
                    switch (this.graphElementType) {
                        case Cell: {
                            this.mustEmpty = new ChunkSet(1, container.emptyChunkSetCell().size());
                            break;
                        }
                        case Vertex: {
                            this.mustEmpty = new ChunkSet(1, container.emptyChunkSetVertex().size());
                            break;
                        }
                        case Edge: {
                            this.mustEmpty = new ChunkSet(1, container.emptyChunkSetEdge().size());
                            break;
                        }
                    }
                }
                this.mustEmpty.set(testSite);
                this.allRestrictionsNull = false;
            }
            else {
                if (this.mustEmpty != null && this.mustEmpty.get(testSite)) {
                    return false;
                }
                if (this.mustNotEmpty == null) {
                    switch (this.graphElementType) {
                        case Cell: {
                            this.mustNotEmpty = new ChunkSet(1, container.emptyChunkSetCell().size());
                            break;
                        }
                        case Vertex: {
                            this.mustNotEmpty = new ChunkSet(1, container.emptyChunkSetVertex().size());
                            break;
                        }
                        case Edge: {
                            this.mustNotEmpty = new ChunkSet(1, container.emptyChunkSetEdge().size());
                            break;
                        }
                    }
                }
                this.mustNotEmpty.set(testSite);
                this.allRestrictionsNull = false;
            }
        }
        else if (bitSetType == Feature.BitSetTypes.Who) {
            if (active) {
                if (this.mustWhoMask != null && this.mustWhoMask.getChunk(testSite) != 0) {
                    return this.mustWho.getChunk(testSite) == value;
                }
                if (this.mustNotWhoMask != null && this.mustNotWho.getChunk(testSite) == value) {
                    return false;
                }
                if (this.mustWho == null) {
                    int chunkSize = 0;
                    int numChunks = 0;
                    switch (this.graphElementType) {
                        case Cell: {
                            chunkSize = container.chunkSizeWhoCell();
                            numChunks = container.numChunksWhoCell();
                            break;
                        }
                        case Edge: {
                            chunkSize = container.chunkSizeWhoEdge();
                            numChunks = container.numChunksWhoEdge();
                            break;
                        }
                        case Vertex: {
                            chunkSize = container.chunkSizeWhoVertex();
                            numChunks = container.numChunksWhoVertex();
                            break;
                        }
                        default: {
                            chunkSize = -1;
                            numChunks = -1;
                            break;
                        }
                    }
                    this.mustWho = new ChunkSet(chunkSize, numChunks);
                    this.mustWhoMask = new ChunkSet(chunkSize, numChunks);
                }
                this.mustWho.setChunk(testSite, value);
                this.mustWhoMask.setChunk(testSite, BitTwiddling.maskI(this.mustWhoMask.chunkSize()));
                this.allRestrictionsNull = false;
            }
            else {
                if (this.mustNotWhoMask != null && this.mustNotWhoMask.getChunk(testSite) != 0) {
                    return this.mustNotWho.getChunk(testSite) == value;
                }
                if (this.mustWhoMask != null && this.mustWho.getChunk(testSite) == value) {
                    return false;
                }
                if (this.mustNotWho == null) {
                    int chunkSize = 0;
                    int numChunks = 0;
                    switch (this.graphElementType) {
                        case Cell: {
                            chunkSize = container.chunkSizeWhoCell();
                            numChunks = container.numChunksWhoCell();
                            break;
                        }
                        case Edge: {
                            chunkSize = container.chunkSizeWhoEdge();
                            numChunks = container.numChunksWhoEdge();
                            break;
                        }
                        case Vertex: {
                            chunkSize = container.chunkSizeWhoVertex();
                            numChunks = container.numChunksWhoVertex();
                            break;
                        }
                        default: {
                            chunkSize = -1;
                            numChunks = -1;
                            break;
                        }
                    }
                    this.mustNotWho = new ChunkSet(chunkSize, numChunks);
                    this.mustNotWhoMask = new ChunkSet(chunkSize, numChunks);
                }
                this.mustNotWho.setChunk(testSite, value);
                this.mustNotWhoMask.setChunk(testSite, BitTwiddling.maskI(this.mustNotWhoMask.chunkSize()));
                this.allRestrictionsNull = false;
            }
        }
        else {
            if (bitSetType != Feature.BitSetTypes.What) {
                System.err.println("Warning: bitSetType " + bitSetType + " not supported by FeatureInstance.addTest()!");
                return false;
            }
            if (active) {
                if (this.mustWhatMask != null && this.mustWhatMask.getChunk(testSite) != 0) {
                    return this.mustWhat.getChunk(testSite) == value;
                }
                if (this.mustNotWhatMask != null && this.mustNotWhat.getChunk(testSite) == value) {
                    return false;
                }
                if (this.mustWhat == null) {
                    int chunkSize = 0;
                    int numChunks = 0;
                    switch (this.graphElementType) {
                        case Cell: {
                            chunkSize = container.chunkSizeWhatCell();
                            numChunks = container.numChunksWhatCell();
                            break;
                        }
                        case Edge: {
                            chunkSize = container.chunkSizeWhatEdge();
                            numChunks = container.numChunksWhatEdge();
                            break;
                        }
                        case Vertex: {
                            chunkSize = container.chunkSizeWhatVertex();
                            numChunks = container.numChunksWhatVertex();
                            break;
                        }
                        default: {
                            chunkSize = -1;
                            numChunks = -1;
                            break;
                        }
                    }
                    this.mustWhat = new ChunkSet(chunkSize, numChunks);
                    this.mustWhatMask = new ChunkSet(chunkSize, numChunks);
                }
                this.mustWhat.setChunk(testSite, value);
                this.mustWhatMask.setChunk(testSite, BitTwiddling.maskI(this.mustWhatMask.chunkSize()));
                this.allRestrictionsNull = false;
            }
            else {
                if (this.mustNotWhatMask != null && this.mustNotWhatMask.getChunk(testSite) != 0) {
                    return this.mustNotWhat.getChunk(testSite) == value;
                }
                if (this.mustWhatMask != null && this.mustWhat.getChunk(testSite) == value) {
                    return false;
                }
                if (this.mustNotWhat == null) {
                    int chunkSize = 0;
                    int numChunks = 0;
                    switch (this.graphElementType) {
                        case Cell: {
                            chunkSize = container.chunkSizeWhatCell();
                            numChunks = container.numChunksWhatCell();
                            break;
                        }
                        case Edge: {
                            chunkSize = container.chunkSizeWhatEdge();
                            numChunks = container.numChunksWhatEdge();
                            break;
                        }
                        case Vertex: {
                            chunkSize = container.chunkSizeWhatVertex();
                            numChunks = container.numChunksWhatVertex();
                            break;
                        }
                        default: {
                            chunkSize = -1;
                            numChunks = -1;
                            break;
                        }
                    }
                    this.mustNotWhat = new ChunkSet(chunkSize, numChunks);
                    this.mustNotWhatMask = new ChunkSet(chunkSize, numChunks);
                }
                this.mustNotWhat.setChunk(testSite, value);
                this.mustNotWhatMask.setChunk(testSite, BitTwiddling.maskI(this.mustNotWhatMask.chunkSize()));
                this.allRestrictionsNull = false;
            }
        }
        return true;
    }
    
    @Override
    public final boolean matches(final State state) {
        if (this.allRestrictionsNull) {
            return true;
        }
        final ContainerState container = state.containerStates()[0];
        switch (this.graphElementType) {
            case Cell: {
                if (this.mustEmpty != null && !container.emptyChunkSetCell().matches(this.mustEmpty, this.mustEmpty)) {
                    return false;
                }
                if (this.mustNotEmpty != null && container.emptyChunkSetCell().violatesNot(this.mustNotEmpty, this.mustNotEmpty)) {
                    return false;
                }
                if (this.mustWho != null && !container.matchesWhoCell(this.mustWhoMask, this.mustWho)) {
                    return false;
                }
                if (this.mustNotWho != null && container.violatesNotWhoCell(this.mustNotWhoMask, this.mustNotWho)) {
                    return false;
                }
                if (this.mustWhat != null && !container.matchesWhatCell(this.mustWhatMask, this.mustWhat)) {
                    return false;
                }
                if (this.mustNotWhat != null && container.violatesNotWhatCell(this.mustNotWhatMask, this.mustNotWhat)) {
                    return false;
                }
                break;
            }
            case Vertex: {
                if (this.mustEmpty != null && !container.emptyChunkSetVertex().matches(this.mustEmpty, this.mustEmpty)) {
                    return false;
                }
                if (this.mustNotEmpty != null && container.emptyChunkSetVertex().violatesNot(this.mustNotEmpty, this.mustNotEmpty)) {
                    return false;
                }
                if (this.mustWho != null && !container.matchesWhoVertex(this.mustWhoMask, this.mustWho)) {
                    return false;
                }
                if (this.mustNotWho != null && container.violatesNotWhoVertex(this.mustNotWhoMask, this.mustNotWho)) {
                    return false;
                }
                if (this.mustWhat != null && !container.matchesWhatVertex(this.mustWhatMask, this.mustWhat)) {
                    return false;
                }
                if (this.mustNotWhat != null && container.violatesNotWhatVertex(this.mustNotWhatMask, this.mustNotWhat)) {
                    return false;
                }
                break;
            }
            case Edge: {
                if (this.mustEmpty != null && !container.emptyChunkSetEdge().matches(this.mustEmpty, this.mustEmpty)) {
                    return false;
                }
                if (this.mustNotEmpty != null && container.emptyChunkSetEdge().violatesNot(this.mustNotEmpty, this.mustNotEmpty)) {
                    return false;
                }
                if (this.mustWho != null && !container.matchesWhoEdge(this.mustWhoMask, this.mustWho)) {
                    return false;
                }
                if (this.mustNotWho != null && container.violatesNotWhoEdge(this.mustNotWhoMask, this.mustNotWho)) {
                    return false;
                }
                if (this.mustWhat != null && !container.matchesWhatEdge(this.mustWhatMask, this.mustWhat)) {
                    return false;
                }
                if (this.mustNotWhat != null && container.violatesNotWhatEdge(this.mustNotWhatMask, this.mustNotWhat)) {
                    return false;
                }
                break;
            }
        }
        return true;
    }
    
    public boolean generalises(final FeatureInstance other) {
        if (other.mustEmpty == null) {
            if (this.mustEmpty != null) {
                return false;
            }
        }
        else if (this.mustEmpty != null && !other.mustEmpty.matches(this.mustEmpty, this.mustEmpty)) {
            return false;
        }
        if (other.mustNotEmpty == null) {
            if (this.mustNotEmpty != null) {
                return false;
            }
        }
        else if (this.mustNotEmpty != null && !other.mustNotEmpty.matches(this.mustNotEmpty, this.mustNotEmpty)) {
            return false;
        }
        if (other.mustWho == null) {
            if (this.mustWho != null) {
                return false;
            }
        }
        else if (this.mustWho != null && !other.mustWho.matches(this.mustWhoMask, this.mustWho)) {
            return false;
        }
        if (other.mustNotWho == null) {
            if (this.mustNotWho != null) {
                return false;
            }
        }
        else if (this.mustNotWho != null && !other.mustNotWho.matches(this.mustNotWhoMask, this.mustNotWho)) {
            return false;
        }
        if (other.mustWhat == null) {
            if (this.mustWhat != null) {
                return false;
            }
        }
        else if (this.mustWhat != null && !other.mustWhat.matches(this.mustWhatMask, this.mustWhat)) {
            return false;
        }
        if (other.mustNotWhat == null) {
            return this.mustNotWhat == null;
        }
        else return this.mustNotWhat == null || other.mustNotWhat.matches(this.mustNotWhatMask, this.mustNotWhat);
    }
    
    public void removeTests(final FeatureInstance other) {
        if (other.mustEmpty != null) {
            this.mustEmpty.andNot(other.mustEmpty);
            if (this.mustEmpty.cardinality() == 0) {
                this.mustEmpty = null;
            }
        }
        if (other.mustNotEmpty != null) {
            this.mustNotEmpty.andNot(other.mustNotEmpty);
            if (this.mustNotEmpty.cardinality() == 0) {
                this.mustNotEmpty = null;
            }
        }
        if (other.mustWho != null) {
            this.mustWho.andNot(other.mustWho);
            this.mustWhoMask.andNot(other.mustWhoMask);
            if (this.mustWho.cardinality() == 0) {
                this.mustWho = null;
                this.mustWhoMask = null;
            }
        }
        if (other.mustNotWho != null) {
            this.mustNotWho.andNot(other.mustNotWho);
            this.mustNotWhoMask.andNot(other.mustNotWhoMask);
            if (this.mustNotWho.cardinality() == 0) {
                this.mustNotWho = null;
                this.mustNotWhoMask = null;
            }
        }
        if (other.mustWhat != null) {
            this.mustWhat.andNot(other.mustWhat);
            this.mustWhatMask.andNot(other.mustWhatMask);
            if (this.mustWhat.cardinality() == 0) {
                this.mustWhat = null;
                this.mustWhatMask = null;
            }
        }
        if (other.mustNotWhat != null) {
            this.mustNotWhat.andNot(other.mustNotWhat);
            this.mustNotWhatMask.andNot(other.mustNotWhatMask);
            if (this.mustNotWhat.cardinality() == 0) {
                this.mustNotWhat = null;
                this.mustNotWhatMask = null;
            }
        }
        this.allRestrictionsNull = this.hasNoTests();
    }
    
    @Override
    public final boolean hasNoTests() {
        return this.mustEmpty == null && this.mustNotEmpty == null && this.mustWho == null && this.mustWhoMask == null && this.mustNotWho == null && this.mustNotWhoMask == null && this.mustWhat == null && this.mustWhatMask == null && this.mustNotWhat == null && this.mustNotWhatMask == null;
    }
    
    @Override
    public final boolean onlyRequiresSingleMustEmpty() {
        return this.mustEmpty != null && this.mustNotEmpty == null && this.mustWho == null && this.mustWhoMask == null && this.mustNotWho == null && this.mustNotWhoMask == null && this.mustWhat == null && this.mustWhatMask == null && this.mustNotWhat == null && this.mustNotWhatMask == null && this.mustEmpty.numNonZeroChunks() == 1;
    }
    
    @Override
    public final boolean onlyRequiresSingleMustWho() {
        return this.mustEmpty == null && this.mustNotEmpty == null && this.mustWho != null && this.mustWhoMask != null && this.mustNotWho == null && this.mustNotWhoMask == null && this.mustWhat == null && this.mustWhatMask == null && this.mustNotWhat == null && this.mustNotWhatMask == null && this.mustWhoMask.numNonZeroChunks() == 1;
    }
    
    @Override
    public final boolean onlyRequiresSingleMustWhat() {
        return this.mustEmpty == null && this.mustNotEmpty == null && this.mustWho == null && this.mustWhoMask == null && this.mustNotWho == null && this.mustNotWhoMask == null && this.mustWhat != null && this.mustWhatMask != null && this.mustNotWhat == null && this.mustNotWhatMask == null && this.mustWhatMask.numNonZeroChunks() == 1;
    }
    
    public final Feature feature() {
        return this.parentFeature;
    }
    
    public final int anchorSite() {
        return this.anchorSite;
    }
    
    public final int reflection() {
        return this.reflection;
    }
    
    public final float rotation() {
        return this.rotation;
    }
    
    @Override
    public final SiteType graphElementType() {
        return this.graphElementType;
    }
    
    public final int from() {
        return this.fromPosition;
    }
    
    public final int to() {
        return this.toPosition;
    }
    
    public final int lastFrom() {
        return this.lastFromPosition;
    }
    
    public final int lastTo() {
        return this.lastToPosition;
    }
    
    public void setAction(final int toPos, final int fromPos) {
        this.toPosition = toPos;
        this.fromPosition = fromPos;
    }
    
    public void setLastAction(final int lastToPos, final int lastFromPos) {
        this.lastToPosition = lastToPos;
        this.lastFromPosition = lastFromPos;
    }
    
    public final ChunkSet mustEmpty() {
        return this.mustEmpty;
    }
    
    public final ChunkSet mustNotEmpty() {
        return this.mustNotEmpty;
    }
    
    public final ChunkSet mustWho() {
        return this.mustWho;
    }
    
    public final ChunkSet mustNotWho() {
        return this.mustNotWho;
    }
    
    public final ChunkSet mustWhoMask() {
        return this.mustWhoMask;
    }
    
    public final ChunkSet mustNotWhoMask() {
        return this.mustNotWhoMask;
    }
    
    public final ChunkSet mustWhat() {
        return this.mustWhat;
    }
    
    public final ChunkSet mustNotWhat() {
        return this.mustNotWhat;
    }
    
    public final ChunkSet mustWhatMask() {
        return this.mustWhatMask;
    }
    
    public final ChunkSet mustNotWhatMask() {
        return this.mustNotWhatMask;
    }
    
    public static List<FeatureInstance> deduplicate(final List<FeatureInstance> instances) {
        final Set<FeatureInstance> deduplicated = new HashSet<>();
        deduplicated.addAll(instances);
        return new ArrayList<>(deduplicated);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.anchorSite;
        result = 31 * result + this.fromPosition;
        result = 31 * result + this.lastFromPosition;
        result = 31 * result + this.lastToPosition;
        result = 31 * result + ((this.mustEmpty == null) ? 0 : this.mustEmpty.hashCode());
        result = 31 * result + ((this.mustNotEmpty == null) ? 0 : this.mustNotEmpty.hashCode());
        result = 31 * result + ((this.mustNotWhat == null) ? 0 : this.mustNotWhat.hashCode());
        result = 31 * result + ((this.mustNotWhatMask == null) ? 0 : this.mustNotWhatMask.hashCode());
        result = 31 * result + ((this.mustNotWho == null) ? 0 : this.mustNotWho.hashCode());
        result = 31 * result + ((this.mustNotWhoMask == null) ? 0 : this.mustNotWhoMask.hashCode());
        result = 31 * result + ((this.mustWhat == null) ? 0 : this.mustWhat.hashCode());
        result = 31 * result + ((this.mustWhatMask == null) ? 0 : this.mustWhatMask.hashCode());
        result = 31 * result + ((this.mustWho == null) ? 0 : this.mustWho.hashCode());
        result = 31 * result + ((this.mustWhoMask == null) ? 0 : this.mustWhoMask.hashCode());
        result = 31 * result + this.reflection;
        result = 31 * result + Float.floatToIntBits(this.rotation);
        result = 31 * result + this.toPosition;
        return result;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof FeatureInstance)) {
            return false;
        }
        final FeatureInstance otherInstance = (FeatureInstance)other;
        return this.toPosition == otherInstance.toPosition && this.fromPosition == otherInstance.fromPosition && this.lastToPosition == otherInstance.lastToPosition && this.lastFromPosition == otherInstance.lastFromPosition && this.anchorSite == otherInstance.anchorSite && this.rotation == otherInstance.rotation && this.reflection == otherInstance.reflection && Objects.equals(this.mustEmpty, otherInstance.mustEmpty) && Objects.equals(this.mustNotEmpty, otherInstance.mustNotEmpty) && Objects.equals(this.mustWho, otherInstance.mustWho) && Objects.equals(this.mustWhoMask, otherInstance.mustWhoMask) && Objects.equals(this.mustNotWho, otherInstance.mustNotWho) && Objects.equals(this.mustNotWhoMask, otherInstance.mustNotWhoMask) && Objects.equals(this.mustWhat, otherInstance.mustWhat) && Objects.equals(this.mustWhatMask, otherInstance.mustWhatMask) && Objects.equals(this.mustNotWhat, otherInstance.mustNotWhat) && Objects.equals(this.mustNotWhatMask, otherInstance.mustNotWhatMask);
    }
    
    public boolean functionallyEquals(final FeatureInstance other) {
        return this.toPosition == other.toPosition && this.fromPosition == other.fromPosition && this.lastToPosition == other.lastToPosition && this.lastFromPosition == other.lastFromPosition && Objects.equals(this.mustEmpty, other.mustEmpty) && Objects.equals(this.mustNotEmpty, other.mustNotEmpty) && Objects.equals(this.mustWho, other.mustWho) && Objects.equals(this.mustWhoMask, other.mustWhoMask) && Objects.equals(this.mustNotWho, other.mustNotWho) && Objects.equals(this.mustNotWhoMask, other.mustNotWhoMask) && Objects.equals(this.mustWhat, other.mustWhat) && Objects.equals(this.mustWhatMask, other.mustWhatMask) && Objects.equals(this.mustNotWhat, other.mustNotWhat) && Objects.equals(this.mustNotWhatMask, other.mustNotWhatMask);
    }
    
    @Override
    public String toString() {
        String requirementsStr = "";
        if (this.fromPosition >= 0) {
            requirementsStr += String.format("Move from %s to %s: ", this.fromPosition, this.toPosition);
        }
        else {
            requirementsStr += String.format("Move to %s: ", this.toPosition);
        }
        if (this.mustEmpty != null) {
            for (int i = this.mustEmpty.nextSetBit(0); i >= 0; i = this.mustEmpty.nextSetBit(i + 1)) {
                requirementsStr = requirementsStr + i + " must be empty, ";
            }
        }
        if (this.mustNotEmpty != null) {
            for (int i = this.mustNotEmpty.nextSetBit(0); i >= 0; i = this.mustNotEmpty.nextSetBit(i + 1)) {
                requirementsStr = requirementsStr + i + " must NOT be empty, ";
            }
        }
        if (this.mustWho != null) {
            for (int i = 0; i < this.mustWho.numChunks(); ++i) {
                if (this.mustWhoMask.getChunk(i) != 0) {
                    requirementsStr = requirementsStr + i + " must belong to " + this.mustWho.getChunk(i) + ", ";
                }
            }
        }
        if (this.mustNotWho != null) {
            for (int i = 0; i < this.mustNotWho.numChunks(); ++i) {
                if (this.mustNotWhoMask.getChunk(i) != 0) {
                    requirementsStr = requirementsStr + i + " must NOT belong to " + this.mustNotWho.getChunk(i) + ", ";
                }
            }
        }
        if (this.mustWhat != null) {
            for (int i = 0; i < this.mustWhat.numChunks(); ++i) {
                if (this.mustWhatMask.getChunk(i) != 0) {
                    requirementsStr = requirementsStr + i + " must contain " + this.mustWhat.getChunk(i) + ", ";
                }
            }
        }
        if (this.mustNotWhat != null) {
            for (int i = 0; i < this.mustNotWhat.numChunks(); ++i) {
                if (this.mustNotWhatMask.getChunk(i) != 0) {
                    requirementsStr = requirementsStr + i + " must NOT contain " + this.mustNotWhat.getChunk(i) + ", ";
                }
            }
        }
        if (this.lastToPosition >= 0) {
            if (this.lastFromPosition >= 0) {
                requirementsStr = requirementsStr + " (response to last move from " + this.lastFromPosition + " to " + this.lastToPosition + ")";
            }
            else {
                requirementsStr = requirementsStr + " (response to last move to " + this.lastToPosition + ")";
            }
        }
        final String metaStr = String.format("anchor=%d, ref=%d, rot=%.2f", this.anchorSite, this.reflection, this.rotation);
        return String.format("Feature Instance [%s] [%s] [%s]", requirementsStr, metaStr, this.parentFeature);
    }
}
