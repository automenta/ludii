// 
// Decompiled by Procyon v0.5.36
// 

package game.util.graph;

import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import main.math.MathRoutines;
import main.math.Point3D;
import main.math.Vector;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Trajectories
{
    private Steps[][] steps;
    private Radials[][] radials;
    private final BitSet totalDirections;
    
    public Trajectories() {
        this.totalDirections = new BitSet();
    }
    
    public List<Step> steps(final SiteType fromType, final int siteId, final SiteType toType) {
        return this.steps[fromType.ordinal()][siteId].toSiteType(toType);
    }
    
    public List<Step> steps(final SiteType fromType, final int siteId, final AbsoluteDirection dirn) {
        return this.steps[fromType.ordinal()][siteId].inDirection(dirn);
    }
    
    public List<Step> steps(final SiteType fromType, final int siteId, final SiteType toType, final AbsoluteDirection dirn) {
        return this.steps[fromType.ordinal()][siteId].toSiteTypeInDirection(toType, dirn);
    }
    
    public Radials radials(final SiteType fromType, final int siteId) {
        return this.radials[fromType.ordinal()][siteId];
    }
    
    public List<Radial> radials(final SiteType fromType, final int siteId, final AbsoluteDirection dirn) {
        return this.radials[fromType.ordinal()][siteId].inDirection(dirn);
    }
    
    public BitSet totalDirections() {
        return this.totalDirections;
    }
    
    public void create(final Graph graph) {
        final int numSiteTypes = SiteType.values().length;
        this.steps = new Steps[numSiteTypes][];
        this.radials = new Radials[numSiteTypes][];
        for (final SiteType siteType : SiteType.values()) {
            final int st = siteType.ordinal();
            this.steps[st] = new Steps[graph.elements(siteType).size()];
            this.radials[st] = new Radials[graph.elements(siteType).size()];
        }
        this.generateSteps(graph);
        this.generateRadials(graph);
    }
    
    void generateSteps(final Graph graph) {
        for (final SiteType siteType : SiteType.values()) {
            for (final GraphElement from : graph.elements(siteType)) {
                final int st = siteType.ordinal();
                final int id = from.id();
                final Steps stepsFrom = new Steps(siteType, id);
                from.stepsTo(stepsFrom);
                this.steps[st][id] = stepsFrom;
            }
        }
        this.setDirections(graph);
        for (int st2 = 0; st2 < SiteType.values().length; ++st2) {
            for (final Steps stepList : this.steps[st2]) {
                this.totalDirections.or(stepList.totalDirections());
                stepList.sort();
            }
        }
    }
    
    void setDirections(final Graph graph) {
        this.setCompassDirections(graph);
        this.setCircularDirections(graph);
        for (final SiteType siteType : SiteType.values()) {
            final List<? extends GraphElement> elements = graph.elements(siteType);
            final int st = siteType.ordinal();
            for (int id = 0; id < elements.size(); ++id) {
                for (final AbsoluteDirection dirn : AbsoluteDirection.values()) {
                    final int dirnIndex = dirn.ordinal();
                    for (final Step step : this.steps[st][id].inDirection(AbsoluteDirection.values()[dirnIndex])) {
                        step.directions().set(dirnIndex, true);
                        steps[st][id].addToSiteTypeInDirection(step.to.siteType(), dirn, step);
                        totalDirections.set(dirnIndex, true);
                    }
                }
            }
        }
    }
    
    void setCompassDirections(final Graph graph) {
        for (final SiteType siteType : SiteType.values()) {
            final List<? extends GraphElement> elements = graph.elements(siteType);
            for (final GraphElement element : elements) {
                this.setCompassDirections(graph, element);
            }
        }
    }
    
    void setCompassDirections(final Graph graph, final GraphElement element) {
        final int st = element.siteType().ordinal();
        final List<Step> stepList = this.steps[st][element.id()].steps();
        final double unit = graph.averageEdgeLength();
        final BitSet used = new BitSet();
        boolean collision = false;
        for (final Step step : stepList) {
            final int dirn = mapAngleToAbsoluteDirection(step, unit, false).ordinal();
            if (used.get(dirn)) {
                collision = true;
                break;
            }
            used.set(dirn, true);
        }
        for (final Step step : stepList) {
            final AbsoluteDirection dirn2 = mapAngleToAbsoluteDirection(step, unit, collision);
            this.steps[st][element.id()].addInDirection(AbsoluteDirection.values()[dirn2.ordinal()], step);
            if (dirn2 == AbsoluteDirection.N || dirn2 == AbsoluteDirection.E || dirn2 == AbsoluteDirection.S || dirn2 == AbsoluteDirection.W || dirn2 == AbsoluteDirection.NE || dirn2 == AbsoluteDirection.SE || dirn2 == AbsoluteDirection.SW || dirn2 == AbsoluteDirection.NW || dirn2 == AbsoluteDirection.NNE || dirn2 == AbsoluteDirection.ENE || dirn2 == AbsoluteDirection.ESE || dirn2 == AbsoluteDirection.SSE || dirn2 == AbsoluteDirection.SSW || dirn2 == AbsoluteDirection.WSW || dirn2 == AbsoluteDirection.WNW || dirn2 == AbsoluteDirection.NNW) {
                this.steps[st][element.id()].addInDirection(AbsoluteDirection.SameLayer, step);
            }
            if (dirn2 == AbsoluteDirection.U || dirn2 == AbsoluteDirection.UN || dirn2 == AbsoluteDirection.UE || dirn2 == AbsoluteDirection.US || dirn2 == AbsoluteDirection.UW || dirn2 == AbsoluteDirection.UNE || dirn2 == AbsoluteDirection.USE || dirn2 == AbsoluteDirection.USW || dirn2 == AbsoluteDirection.UNW) {
                this.steps[st][element.id()].addInDirection(AbsoluteDirection.Upward, step);
            }
            if (dirn2 == AbsoluteDirection.D || dirn2 == AbsoluteDirection.DN || dirn2 == AbsoluteDirection.DE || dirn2 == AbsoluteDirection.DS || dirn2 == AbsoluteDirection.DW || dirn2 == AbsoluteDirection.DNE || dirn2 == AbsoluteDirection.DSE || dirn2 == AbsoluteDirection.DSW || dirn2 == AbsoluteDirection.DNW) {
                this.steps[st][element.id()].addInDirection(AbsoluteDirection.Downward, step);
            }
        }
    }
    
    public static AbsoluteDirection mapAngleToAbsoluteDirection(final Step step, final double unit, final boolean intercardinal) {
        final Point3D ptA = step.from().pt();
        final Point3D ptB = step.to().pt();
        int elevation = 0;
        if (ptB.z() - ptA.z() < -0.1 * unit) {
            elevation = -1;
        }
        else if (ptB.z() - ptA.z() > 0.1 * unit) {
            elevation = 1;
        }
        if (elevation != 0 && MathRoutines.distance(ptA.x(), ptA.y(), ptB.x(), ptB.y()) < 0.1 * unit) {
            return (elevation < 0) ? AbsoluteDirection.D : AbsoluteDirection.U;
        }
        double angle;
        for (angle = Math.atan2(ptB.y() - ptA.y(), ptB.x() - ptA.x()); angle < 0.0; angle += 6.283185307179586) {}
        while (angle > 6.283185307179586) {
            angle -= 6.283185307179586;
        }
        if (!intercardinal) {
            final double off = 0.39269908169872414;
            if (elevation == 0) {
                if (angle < 0.39269908169872414) {
                    return AbsoluteDirection.E;
                }
                if (angle < 1.1780972450961724) {
                    return AbsoluteDirection.NE;
                }
                if (angle < 1.9634954084936207) {
                    return AbsoluteDirection.N;
                }
                if (angle < 2.748893571891069) {
                    return AbsoluteDirection.NW;
                }
                if (angle < 3.5342917352885173) {
                    return AbsoluteDirection.W;
                }
                if (angle < 4.319689898685965) {
                    return AbsoluteDirection.SW;
                }
                if (angle < 5.105088062083414) {
                    return AbsoluteDirection.S;
                }
                if (angle < 5.890486225480862) {
                    return AbsoluteDirection.SE;
                }
                return AbsoluteDirection.E;
            }
            else if (elevation < 0) {
                if (angle < 0.39269908169872414) {
                    return AbsoluteDirection.DE;
                }
                if (angle < 1.1780972450961724) {
                    return AbsoluteDirection.DNE;
                }
                if (angle < 1.9634954084936207) {
                    return AbsoluteDirection.DN;
                }
                if (angle < 2.748893571891069) {
                    return AbsoluteDirection.DNW;
                }
                if (angle < 3.5342917352885173) {
                    return AbsoluteDirection.DW;
                }
                if (angle < 4.319689898685965) {
                    return AbsoluteDirection.DSW;
                }
                if (angle < 5.105088062083414) {
                    return AbsoluteDirection.DS;
                }
                if (angle < 5.890486225480862) {
                    return AbsoluteDirection.DSE;
                }
                return AbsoluteDirection.DE;
            }
            else {
                if (angle < 0.39269908169872414) {
                    return AbsoluteDirection.UE;
                }
                if (angle < 1.1780972450961724) {
                    return AbsoluteDirection.UNE;
                }
                if (angle < 1.9634954084936207) {
                    return AbsoluteDirection.UN;
                }
                if (angle < 2.748893571891069) {
                    return AbsoluteDirection.UNW;
                }
                if (angle < 3.5342917352885173) {
                    return AbsoluteDirection.UW;
                }
                if (angle < 4.319689898685965) {
                    return AbsoluteDirection.USW;
                }
                if (angle < 5.105088062083414) {
                    return AbsoluteDirection.US;
                }
                if (angle < 5.890486225480862) {
                    return AbsoluteDirection.USE;
                }
                return AbsoluteDirection.UE;
            }
        }
        else {
            final double off = 0.19634954084936207;
            if (elevation == 0) {
                if (angle < 0.19634954084936207) {
                    return AbsoluteDirection.E;
                }
                if (angle < 0.5890486225480862) {
                    return AbsoluteDirection.ENE;
                }
                if (angle < 0.9817477042468103) {
                    return AbsoluteDirection.NE;
                }
                if (angle < 1.3744467859455345) {
                    return AbsoluteDirection.NNE;
                }
                if (angle < 1.7671458676442586) {
                    return AbsoluteDirection.N;
                }
                if (angle < 2.1598449493429825) {
                    return AbsoluteDirection.NNW;
                }
                if (angle < 2.552544031041707) {
                    return AbsoluteDirection.NW;
                }
                if (angle < 2.945243112740431) {
                    return AbsoluteDirection.WNW;
                }
                if (angle < 3.3379421944391554) {
                    return AbsoluteDirection.W;
                }
                if (angle < 3.730641276137879) {
                    return AbsoluteDirection.WSW;
                }
                if (angle < 4.123340357836604) {
                    return AbsoluteDirection.SW;
                }
                if (angle < 4.516039439535327) {
                    return AbsoluteDirection.SSW;
                }
                if (angle < 4.908738521234052) {
                    return AbsoluteDirection.S;
                }
                if (angle < 5.3014376029327765) {
                    return AbsoluteDirection.SSE;
                }
                if (angle < 5.6941366846315) {
                    return AbsoluteDirection.SE;
                }
                if (angle < 6.086835766330224) {
                    return AbsoluteDirection.ESE;
                }
                return AbsoluteDirection.E;
            }
            else if (elevation < 0) {
                if (angle < 0.19634954084936207) {
                    return AbsoluteDirection.DE;
                }
                if (angle < 0.5890486225480862) {
                    return AbsoluteDirection.DNE;
                }
                if (angle < 0.9817477042468103) {
                    return AbsoluteDirection.DNE;
                }
                if (angle < 1.3744467859455345) {
                    return AbsoluteDirection.DNE;
                }
                if (angle < 1.7671458676442586) {
                    return AbsoluteDirection.DN;
                }
                if (angle < 2.1598449493429825) {
                    return AbsoluteDirection.DNW;
                }
                if (angle < 2.552544031041707) {
                    return AbsoluteDirection.DNW;
                }
                if (angle < 2.945243112740431) {
                    return AbsoluteDirection.DNW;
                }
                if (angle < 3.3379421944391554) {
                    return AbsoluteDirection.DW;
                }
                if (angle < 3.730641276137879) {
                    return AbsoluteDirection.DSW;
                }
                if (angle < 4.123340357836604) {
                    return AbsoluteDirection.DSW;
                }
                if (angle < 4.516039439535327) {
                    return AbsoluteDirection.DSW;
                }
                if (angle < 4.908738521234052) {
                    return AbsoluteDirection.DS;
                }
                if (angle < 5.3014376029327765) {
                    return AbsoluteDirection.DSE;
                }
                if (angle < 5.6941366846315) {
                    return AbsoluteDirection.DSE;
                }
                if (angle < 6.086835766330224) {
                    return AbsoluteDirection.DSE;
                }
                return AbsoluteDirection.DE;
            }
            else {
                if (angle < 0.19634954084936207) {
                    return AbsoluteDirection.UE;
                }
                if (angle < 0.5890486225480862) {
                    return AbsoluteDirection.UNE;
                }
                if (angle < 0.9817477042468103) {
                    return AbsoluteDirection.UNE;
                }
                if (angle < 1.3744467859455345) {
                    return AbsoluteDirection.UNE;
                }
                if (angle < 1.7671458676442586) {
                    return AbsoluteDirection.UN;
                }
                if (angle < 2.1598449493429825) {
                    return AbsoluteDirection.UNW;
                }
                if (angle < 2.552544031041707) {
                    return AbsoluteDirection.UNW;
                }
                if (angle < 2.945243112740431) {
                    return AbsoluteDirection.UNW;
                }
                if (angle < 3.3379421944391554) {
                    return AbsoluteDirection.UW;
                }
                if (angle < 3.730641276137879) {
                    return AbsoluteDirection.USW;
                }
                if (angle < 4.123340357836604) {
                    return AbsoluteDirection.USW;
                }
                if (angle < 4.516039439535327) {
                    return AbsoluteDirection.USW;
                }
                if (angle < 4.908738521234052) {
                    return AbsoluteDirection.US;
                }
                if (angle < 5.3014376029327765) {
                    return AbsoluteDirection.USE;
                }
                if (angle < 5.6941366846315) {
                    return AbsoluteDirection.USE;
                }
                if (angle < 6.086835766330224) {
                    return AbsoluteDirection.USE;
                }
                return AbsoluteDirection.UE;
            }
        }
    }
    
    void setCircularDirections(final Graph graph) {
        final int vertexTypeId = SiteType.Vertex.ordinal();
        final BitSet pivotIds = new BitSet();
        for (final Vertex vertex : graph.vertices()) {
            if (vertex.pivot() != null) {
                pivotIds.set(vertex.pivot().id());
            }
        }
        for (int id = pivotIds.nextSetBit(0); id >= 0; id = pivotIds.nextSetBit(id + 1)) {
            for (final Step step : this.steps[vertexTypeId][id].steps()) {
                this.steps[vertexTypeId][id].addInDirection(AbsoluteDirection.Out, step);
                this.steps[vertexTypeId][id].addInDirection(AbsoluteDirection.Rotational, step);
            }
        }
        for (final SiteType siteType : SiteType.values()) {
            final List<? extends GraphElement> elements = graph.elements(siteType);
            final int st = siteType.ordinal();
            for (int id2 = 0; id2 < elements.size(); ++id2) {
                if (siteType != SiteType.Vertex || !pivotIds.get(id2)) {
                    final GraphElement from = elements.get(id2);
                    final Vertex pivot = from.pivot();
                    if (pivot != null) {
                        for (final Step step2 : this.steps[st][id2].steps()) {
                            final Vector vecAB = new Vector(from.pt(), step2.to().pt());
                            final Vector vecAP = new Vector(from.pt(), pivot.pt());
                            vecAB.normalise();
                            vecAP.normalise();
                            if (step2.directions().get(AbsoluteDirection.Diagonal.ordinal())) {
                                continue;
                            }
                            final double dot = vecAB.dotProduct(vecAP);
                            if (dot > 0.9) {
                                this.steps[st][id2].addInDirection(AbsoluteDirection.In, step2);
                                this.steps[st][id2].addInDirection(AbsoluteDirection.Rotational, step2);
                            }
                            else if (dot < -0.9) {
                                this.steps[st][id2].addInDirection(AbsoluteDirection.Out, step2);
                                this.steps[st][id2].addInDirection(AbsoluteDirection.Rotational, step2);
                            }
                            else {
                                final Edge curvedEdge = graph.findEdge(from.id(), step2.to().id(), true);
                                if (curvedEdge == null) {
                                    continue;
                                }
                                if (MathRoutines.whichSide(from.pt2D(), pivot.pt2D(), step2.to().pt2D()) > 0) {
                                    this.steps[st][id2].addInDirection(AbsoluteDirection.CW, step2);
                                    this.steps[st][id2].addInDirection(AbsoluteDirection.Rotational, step2);
                                }
                                else {
                                    this.steps[st][id2].addInDirection(AbsoluteDirection.CCW, step2);
                                    this.steps[st][id2].addInDirection(AbsoluteDirection.Rotational, step2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    void generateRadials(final Graph graph) {
        for (final SiteType siteType : SiteType.values()) {
            final List<? extends GraphElement> elements = graph.elements(siteType);
            final int st = siteType.ordinal();
            for (int id = 0; id < elements.size(); ++id) {
                this.radials[st][id] = new Radials(siteType, id);
                for (final AbsoluteDirection dirn : AbsoluteDirection.values()) {
                    for (final Step step : this.steps[st][id].inDirection(dirn)) {
                        if (step.to().siteType() != siteType) {
                            continue;
                        }
                        final RadialWIP radial = new RadialWIP(step.from(), dirn);
                        this.followRadial(graph, radial, siteType, dirn, step.to());
                        this.radials[st][id].addSafe(radial.toRadial());
                    }
                }
            }
        }
        for (final SiteType siteType : SiteType.values()) {
            final List<? extends GraphElement> elements = graph.elements(siteType);
            final int st = siteType.ordinal();
            for (int id = 0; id < elements.size(); ++id) {
                for (final Radial radial2 : this.radials[st][id].radials()) {
                    radial2.removeOppositeSubsets();
                }
            }
        }
        for (final SiteType siteType : SiteType.values()) {
            final List<? extends GraphElement> elements = graph.elements(siteType);
            final int st = siteType.ordinal();
            for (int id = 0; id < elements.size(); ++id) {
                this.steps[st][id].clearInDirection(AbsoluteDirection.Rotational);
                for (final Radial radial2 : this.radials[st][id].inDirection(AbsoluteDirection.In)) {
                    this.radials[st][id].addInDirection(AbsoluteDirection.Rotational, radial2);
                }
                for (final Radial radial2 : this.radials[st][id].inDirection(AbsoluteDirection.Out)) {
                    this.radials[st][id].addInDirection(AbsoluteDirection.Rotational, radial2);
                }
                for (final Radial radial2 : this.radials[st][id].inDirection(AbsoluteDirection.CW)) {
                    this.radials[st][id].addInDirection(AbsoluteDirection.Rotational, radial2);
                }
                for (final Radial radial2 : this.radials[st][id].inDirection(AbsoluteDirection.CCW)) {
                    this.radials[st][id].addInDirection(AbsoluteDirection.Rotational, radial2);
                }
                this.radials[st][id].removeSubsetsInDirection(AbsoluteDirection.Rotational);
            }
        }
        for (int st2 = 0; st2 < SiteType.values().length; ++st2) {
            for (final Radials radialSet : this.radials[st2]) {
                radialSet.setDistinct();
                radialSet.sort();
            }
        }
    }
    
    void followRadial(final Graph graph, final RadialWIP radial, final SiteType siteType, final AbsoluteDirection dirn, final GraphElement current) {
        final double threshold = 0.25;
        final GraphElement previous = radial.lastStep();
        radial.addStep(current);
        final Vector trajectory = new Vector(previous.pt(), current.pt());
        trajectory.normalise();
        if (current.id() >= this.steps[siteType.ordinal()].length) {
            System.out.println("** Trajectories.followRadial(): " + siteType + " " + current.id() + " not in steps[][][] array.");
            return;
        }
        final List<Step> nextSteps = this.steps[siteType.ordinal()][current.id()].inDirection(dirn);
        double bestDiff = 0.25;
        GraphElement bestNextTo = null;
        final int dirnOrdinal = dirn.ordinal();
        if (dirn == AbsoluteDirection.CW || dirn == AbsoluteDirection.CCW || dirn == AbsoluteDirection.In || dirn == AbsoluteDirection.Out) {
            for (final Step next : nextSteps) {
                final GraphElement nextTo = next.to();
                if (nextTo.siteType() != siteType) {
                    continue;
                }
                if (next.directions().get(dirnOrdinal)) {
                    bestNextTo = nextTo;
                    break;
                }
            }
        }
        else {
            for (final Step next : nextSteps) {
                final GraphElement nextTo = next.to();
                if (nextTo.siteType() != siteType) {
                    continue;
                }
                if (!next.directions().get(dirnOrdinal)) {
                    continue;
                }
                final double diff = Math.abs(MathRoutines.angleDifference(previous.pt2D(), current.pt2D(), nextTo.pt2D()));
                if (diff >= bestDiff) {
                    continue;
                }
                bestNextTo = nextTo;
                bestDiff = diff;
                if (bestDiff == 0.0) {
                    break;
                }
            }
        }
        if (bestNextTo != null && !radial.steps().contains(bestNextTo)) {
            this.followRadial(graph, radial, siteType, dirn, bestNextTo);
        }
    }
    
    public void report(final Graph graph) {
        System.out.println(graph);
        System.out.println("\nRadials:");
        for (final SiteType siteType : SiteType.values()) {
            final List<? extends GraphElement> elements = graph.elements(siteType);
            final int st = siteType.ordinal();
            for (int id = 0; id < elements.size(); ++id) {
                final GraphElement element = elements.get(id);
                System.out.println("\nSteps from " + element.label() + ":");
                for (final Step step : this.steps[st][id].steps()) {
                    System.out.println(" " + step);
                }
                System.out.println("\n" + this.radials[st][id]);
            }
        }
        System.out.println("\nDirections used:");
        for (int d = this.totalDirections.nextSetBit(0); d >= 0; d = this.totalDirections.nextSetBit(d + 1)) {
            System.out.println("- " + AbsoluteDirection.values()[d]);
        }
        System.out.println();
    }
    
    private static final class RadialWIP
    {
        private final List<GraphElement> steps;
        private final AbsoluteDirection direction;
        
        public RadialWIP(final GraphElement start, final AbsoluteDirection direction) {
            this.steps = new ArrayList<>();
            this.direction = direction;
            this.steps.add(start);
        }
        
        public List<GraphElement> steps() {
            return this.steps;
        }
        
        public void addStep(final GraphElement to) {
            this.steps.add(to);
        }
        
        public GraphElement lastStep() {
            return this.steps.get(this.steps.size() - 1);
        }
        
        public Radial toRadial() {
            return new Radial(this.steps.toArray(new GraphElement[this.steps.size()]), this.direction);
        }
    }
}
