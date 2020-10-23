// 
// Decompiled by Procyon v0.5.36
// 

package util.action;

import annotations.Hide;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.DirectionFacing;
import game.util.graph.Radial;
import topology.Cell;
import topology.Topology;
import util.Context;
import util.state.containerState.ContainerState;

import java.util.List;

@Hide
public abstract class BaseAction implements Action
{
    private static final long serialVersionUID = 1L;
    public boolean decision;
    
    public BaseAction() {
        this.decision = false;
    }
    
    @Override
    public int from() {
        return -1;
    }
    
    @Override
    public SiteType fromType() {
        return SiteType.Cell;
    }
    
    @Override
    public int levelFrom() {
        return 0;
    }
    
    @Override
    public int to() {
        return -1;
    }
    
    @Override
    public SiteType toType() {
        return SiteType.Cell;
    }
    
    @Override
    public int levelTo() {
        return 0;
    }
    
    @Override
    public int what() {
        return 0;
    }
    
    @Override
    public int state() {
        return 0;
    }
    
    @Override
    public int rotation() {
        return 0;
    }
    
    @Override
    public int count() {
        return 0;
    }
    
    @Override
    public boolean isStacking() {
        return false;
    }
    
    @Override
    public boolean[] hidden() {
        return null;
    }
    
    @Override
    public int who() {
        return 0;
    }
    
    @Override
    public boolean isDecision() {
        return this.decision;
    }
    
    @Override
    public boolean isPass() {
        return false;
    }
    
    @Override
    public boolean isForfeit() {
        return false;
    }
    
    @Override
    public boolean isSwap() {
        return false;
    }
    
    @Override
    public String proposition() {
        return null;
    }
    
    @Override
    public String vote() {
        return null;
    }
    
    @Override
    public String message() {
        return null;
    }
    
    @Override
    public void setDecision(final boolean decision) {
        this.decision = decision;
    }
    
    @Override
    public Action withDecision(final boolean dec) {
        this.decision = dec;
        return this;
    }
    
    @Override
    public ActionType actionType() {
        return null;
    }
    
    @Override
    public boolean matchesUserMove(final int siteA, final int levelA, final SiteType graphElementTypeA, final int siteB, final int levelB, final SiteType graphElementTypeB) {
        return this.from() == siteA && this.levelFrom() == levelA && this.fromType() == graphElementTypeA && this.to() == siteB && this.levelTo() == levelB && this.toType() == graphElementTypeB;
    }
    
    protected static void lineOfPlayDominoes(final Context context, final int site1, final int site2, final AbsoluteDirection dirn, final boolean doubleDomino, final boolean leftOrientation) {
        final ContainerState cs = context.containerState(0);
        final Topology topology = context.topology();
        final Cell c1 = context.topology().cells().get(site1);
        final Cell c2 = context.topology().cells().get(site2);
        final List<Radial> radialsC1 = topology.trajectories().radials(SiteType.Cell, c1.index(), dirn);
        final List<Radial> radialsC2 = topology.trajectories().radials(SiteType.Cell, c2.index(), dirn);
        if (!radialsC1.isEmpty() && !radialsC2.isEmpty()) {
            final Radial radialC1 = radialsC1.get(0);
            final Radial radialC2 = radialsC2.get(0);
            if (radialC1.steps().length > 2 && radialC2.steps().length > 2 && cs.isEmpty(radialC1.steps()[1].id(), SiteType.Cell) && cs.isEmpty(radialC2.steps()[1].id(), SiteType.Cell)) {
                for (int i = 1; i < radialC1.steps().length && i < radialC2.steps().length && i < 5; ++i) {
                    final int to = radialC1.steps()[i].id();
                    final int to2 = radialC2.steps()[i].id();
                    if (!cs.isEmpty(to, SiteType.Cell) || !cs.isEmpty(to2, SiteType.Cell)) {
                        return;
                    }
                    cs.setPlayable(context.state(), to, true);
                    cs.setPlayable(context.state(), to2, true);
                    if (!doubleDomino && i < 3) {
                        final DirectionFacing direction = AbsoluteDirection.convert(dirn);
                        final DirectionFacing leftDirection = direction.left().left();
                        final AbsoluteDirection absoluteLeftDirection = leftDirection.toAbsolute();
                        final DirectionFacing rightDirection = direction.right().right();
                        final AbsoluteDirection absoluteRightDirection = rightDirection.toAbsolute();
                        if (leftOrientation) {
                            final List<Radial> radialsC1Left = topology.trajectories().radials(SiteType.Cell, c1.index(), absoluteLeftDirection);
                            final List<Radial> radialsC2Right = topology.trajectories().radials(SiteType.Cell, c2.index(), absoluteRightDirection);
                            if (!radialsC1Left.isEmpty() && radialsC1Left.get(0).steps().length > 1) {
                                final int leftOfTo = radialsC1Left.get(0).steps()[1].id();
                                if (cs.isEmpty(leftOfTo, SiteType.Cell)) {
                                    cs.setPlayable(context.state(), leftOfTo, true);
                                }
                            }
                            if (!radialsC2Right.isEmpty() && radialsC2Right.get(0).steps().length > 1) {
                                final int leftOfTo = radialsC2Right.get(0).steps()[1].id();
                                if (cs.isEmpty(leftOfTo, SiteType.Cell)) {
                                    cs.setPlayable(context.state(), leftOfTo, true);
                                }
                            }
                        }
                        else {
                            final List<Radial> radialsC1Right = topology.trajectories().radials(SiteType.Cell, c1.index(), absoluteRightDirection);
                            final List<Radial> radialsC2Left = topology.trajectories().radials(SiteType.Cell, c2.index(), absoluteLeftDirection);
                            if (!radialsC1Right.isEmpty() && radialsC1Right.get(0).steps().length > 1) {
                                final int leftOfTo = radialsC1Right.get(0).steps()[1].id();
                                if (cs.isEmpty(leftOfTo, SiteType.Cell)) {
                                    cs.setPlayable(context.state(), leftOfTo, true);
                                }
                            }
                            if (!radialsC2Left.isEmpty() && radialsC2Left.get(0).steps().length > 1) {
                                final int leftOfTo = radialsC2Left.get(0).steps()[1].id();
                                if (cs.isEmpty(leftOfTo, SiteType.Cell)) {
                                    cs.setPlayable(context.state(), leftOfTo, true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected AbsoluteDirection getDirnDomino(final int side, final int state) {
        Label_0241: {
            switch (side) {
                case 0: {
                    switch (state) {
                        case 0 -> {
                            return AbsoluteDirection.W;
                        }
                        case 1 -> {
                            return AbsoluteDirection.N;
                        }
                        case 2 -> {
                            return AbsoluteDirection.E;
                        }
                        case 3 -> {
                            return AbsoluteDirection.S;
                        }
                        default -> {
                            break Label_0241;
                        }
                    }
                }
                case 1: {
                    switch (state) {
                        case 0 -> {
                            return AbsoluteDirection.N;
                        }
                        case 1 -> {
                            return AbsoluteDirection.E;
                        }
                        case 2 -> {
                            return AbsoluteDirection.S;
                        }
                        case 3 -> {
                            return AbsoluteDirection.W;
                        }
                        default -> {
                            break Label_0241;
                        }
                    }
                }
                case 2: {
                    switch (state) {
                        case 0 -> {
                            return AbsoluteDirection.E;
                        }
                        case 1 -> {
                            return AbsoluteDirection.S;
                        }
                        case 2 -> {
                            return AbsoluteDirection.W;
                        }
                        case 3 -> {
                            return AbsoluteDirection.N;
                        }
                        default -> {
                            break Label_0241;
                        }
                    }
                }
                case 3: {
                    switch (state) {
                        case 0 -> {
                            return AbsoluteDirection.S;
                        }
                        case 1 -> {
                            return AbsoluteDirection.W;
                        }
                        case 2 -> {
                            return AbsoluteDirection.N;
                        }
                        case 3 -> {
                            return AbsoluteDirection.E;
                        }
                        default -> {
                            break Label_0241;
                        }
                    }
                }
                default: {
                    return null;
                }
            }
        }
        return null;
    }
    
    @Override
    public void setLevelFrom(final int levelA) {
    }
    
    @Override
    public void setLevelTo(final int levelB) {
    }
    
    @Override
    public boolean isOtherMove() {
        return false;
    }
    
    @Override
    public boolean containsNextInstance() {
        return false;
    }
    
    @Override
    public int playerSelected() {
        return -1;
    }
    
    @Override
    public String toString() {
        return this.toTrialFormat(null);
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        return this.toTrialFormat(context);
    }
    
    @Override
    public String toEnglishString(final Context context) {
        return "???";
    }
}
