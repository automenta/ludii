// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.directions;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.component.Component;
import game.functions.ints.last.LastFrom;
import game.functions.ints.last.LastTo;
import game.types.board.RelationType;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.CompassDirection;
import game.util.directions.DirectionFacing;
import game.util.directions.RelativeDirection;
import game.util.graph.Radial;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.state.containerState.ContainerState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Directions extends DirectionsFunction implements Serializable
{
    private static final long serialVersionUID = 1L;
    final RelativeDirection[] relativeDirections;
    private final RelationType relativeDirectionType;
    private final boolean bySite;
    private List<AbsoluteDirection> precomputedDirectionsToReturn;
    final AbsoluteDirection[] absoluteDirections;
    
    public Directions(@Or final AbsoluteDirection absoluteDirection, @Or final AbsoluteDirection[] absoluteDirections) {
        this.precomputedDirectionsToReturn = null;
        int numNonNull = 0;
        if (absoluteDirection != null) {
            ++numNonNull;
        }
        if (absoluteDirections != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Only one Or parameter can be non-null.");
        }
        this.relativeDirections = null;
        this.absoluteDirections = ((absoluteDirections != null) ? absoluteDirections : new AbsoluteDirection[] { absoluteDirection });
        this.relativeDirectionType = RelationType.Adjacent;
        this.bySite = false;
    }
    
    public Directions(@Opt @Or final RelativeDirection relativeDirection, @Opt @Or final RelativeDirection[] relativeDirections, @Opt @Name final RelationType of, @Opt @Name final Boolean bySite) {
        this.precomputedDirectionsToReturn = null;
        int numNonNull = 0;
        if (relativeDirection != null) {
            ++numNonNull;
        }
        if (relativeDirections != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Only one Or parameter can be non-null.");
        }
        this.absoluteDirections = null;
        if (relativeDirections != null) {
            this.relativeDirections = relativeDirections;
        }
        else {
            this.relativeDirections = ((relativeDirection == null) ? new RelativeDirection[] { RelativeDirection.Forward } : new RelativeDirection[] { relativeDirection });
        }
        this.relativeDirectionType = ((of != null) ? of : RelationType.Adjacent);
        this.bySite = (bySite != null && bySite);
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public boolean isStatic() {
        return this.absoluteDirections != null;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.isStatic()) {
            this.precomputedDirectionsToReturn = this.convertToAbsolute(null, null, null, null, null, null);
        }
    }
    
    public boolean isAll() {
        return this.absoluteDirections != null && this.absoluteDirections[0].equals(AbsoluteDirection.All);
    }
    
    @Override
    public RelativeDirection[] getRelativeDirections() {
        return this.relativeDirections;
    }
    
    public AbsoluteDirection absoluteDirection() {
        if (this.absoluteDirections == null) {
            return null;
        }
        return this.absoluteDirections[0];
    }
    
    @Override
    public List<AbsoluteDirection> convertToAbsolute(final SiteType graphType, final TopologyElement element, final Component newComponent, final DirectionFacing newFacing, final Integer newRotation, final Context context) {
        if (this.precomputedDirectionsToReturn != null) {
            return this.precomputedDirectionsToReturn;
        }
        if (this.absoluteDirections != null) {
            return Arrays.asList(this.absoluteDirections);
        }
        if (element == null) {
            return new ArrayList<>();
        }
        final List<AbsoluteDirection> directionsToReturn = new ArrayList<>();
        final Topology topology = context.topology();
        final int site = element.index();
        final int containerId = (graphType != SiteType.Cell) ? 0 : context.containerId()[site];
        final ContainerState cs = context.containerState(containerId);
        final int what = cs.what(site, graphType);
        if (what < 1 && newComponent == null) {
            return directionsToReturn;
        }
        final Component component = (newComponent != null) ? newComponent : context.components()[what];
        final List<DirectionFacing> directionsSupported = this.bySite ? element.supportedDirections(this.relativeDirectionType) : topology.supportedDirections(this.relativeDirectionType, graphType);
        DirectionFacing facingDirection = (newFacing != null) ? newFacing : ((component.getDirn() != null) ? component.getDirn() : CompassDirection.N);
        for (int rotation = (newRotation == null) ? cs.rotation(site, graphType) : newRotation; rotation != 0; --rotation) {
            facingDirection = RelativeDirection.FR.directions(facingDirection, directionsSupported).get(0);
        }
        for (final RelativeDirection relativeDirection : this.relativeDirections) {
            if (relativeDirection.equals(RelativeDirection.SameDirection)) {
                final int lastFrom = new LastFrom(null).eval(context);
                final int lastTo = new LastTo(null).eval(context);
                boolean found = false;
                for (final DirectionFacing direction : directionsSupported) {
                    final AbsoluteDirection absDirection = direction.toAbsolute();
                    final List<Radial> radials = topology.trajectories().radials(graphType, lastFrom, absDirection);
                    for (final Radial radial : radials) {
                        for (int toIdx = 1; toIdx < radial.steps().length; ++toIdx) {
                            final int to = radial.steps()[toIdx].id();
                            if (to == lastTo) {
                                directionsToReturn.add(absDirection);
                                found = true;
                                break;
                            }
                        }
                    }
                    if (found) {
                        break;
                    }
                }
            }
            else if (relativeDirection.equals(RelativeDirection.OppositeDirection)) {
                final int lastFrom = new LastFrom(null).eval(context);
                final int lastTo = new LastTo(null).eval(context);
                boolean found = false;
                for (final DirectionFacing direction : directionsSupported) {
                    final AbsoluteDirection absDirection = direction.toAbsolute();
                    final List<Radial> radials = topology.trajectories().radials(graphType, lastTo, absDirection);
                    for (final Radial radial : radials) {
                        for (int toIdx = 1; toIdx < radial.steps().length; ++toIdx) {
                            final int to = radial.steps()[toIdx].id();
                            if (to == lastFrom) {
                                directionsToReturn.add(absDirection);
                                found = true;
                                break;
                            }
                        }
                    }
                    if (found) {
                        break;
                    }
                }
            }
            else {
                final List<DirectionFacing> supportedDirections = this.bySite ? element.supportedDirections(this.relativeDirectionType) : topology.supportedDirections(this.relativeDirectionType, graphType);
                final List<DirectionFacing> directions = relativeDirection.directions(facingDirection, supportedDirections);
                for (final DirectionFacing direction2 : directions) {
                    directionsToReturn.add(direction2.toAbsolute());
                }
            }
        }
        return directionsToReturn;
    }
    
    @Override
    public String toString() {
        String str = "DirectionChoice(";
        if (this.absoluteDirections != null) {
            for (final AbsoluteDirection absoluteDirection : this.absoluteDirections) {
                str = str + absoluteDirection.name() + ", ";
            }
        }
        else {
            for (final RelativeDirection relativeDirection : this.relativeDirections) {
                str = str + relativeDirection.name() + ", ";
            }
        }
        str += ")";
        return str;
    }
}
