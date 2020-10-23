// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.operators.foreach.group;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.directions.Directions;
import game.functions.directions.DirectionsFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.Direction;
import game.util.equipment.Region;
import game.util.graph.Step;
import gnu.trove.list.array.TIntArrayList;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.Move;
import util.MoveUtilities;
import util.state.containerState.ContainerState;

import java.util.List;

@Hide
public final class ForEachGroup extends Effect
{
    private static final long serialVersionUID = 1L;
    private final BooleanFunction condition;
    private final DirectionsFunction dirnChoice;
    private final Moves movesToApply;
    private SiteType type;
    
    public ForEachGroup(@Opt final SiteType type, @Opt final Direction directions, @Opt @Name final BooleanFunction If, final Moves moves, @Opt final Then then) {
        super(then);
        this.movesToApply = moves;
        this.type = type;
        this.condition = If;
        this.dirnChoice = ((directions != null) ? directions.directionsFunctions() : new Directions(AbsoluteDirection.Adjacent, null));
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final Topology topology = context.topology();
        final int maxIndexElement = context.topology().getGraphElements(this.type).size();
        final ContainerState cs = context.containerState(0);
        final int origFrom = context.from();
        final int origTo = context.to();
        final Region origRegion = context.region();
        final int who = context.state().mover();
        final TIntArrayList sitesToCheck = new TIntArrayList();
        if (this.condition != null) {
            for (int i = 1; i < context.game().players().size(); ++i) {
                final TIntArrayList allSites = context.state().owned().sites(i);
                for (int j = 0; j < allSites.size(); ++j) {
                    final int site = allSites.get(j);
                    if (site < maxIndexElement) {
                        sitesToCheck.add(site);
                    }
                }
            }
        }
        else {
            for (int k = 0; k < context.state().owned().sites(who).size(); ++k) {
                final int site2 = context.state().owned().sites(who).get(k);
                if (site2 < maxIndexElement) {
                    sitesToCheck.add(site2);
                }
            }
        }
        final TIntArrayList sitesChecked = new TIntArrayList();
        for (int l = 0; l < sitesToCheck.size(); ++l) {
            final int from = sitesToCheck.get(l);
            if (!sitesChecked.contains(from)) {
                final TIntArrayList groupSites = new TIntArrayList();
                context.setFrom(from);
                if ((who == cs.who(from, this.type) && this.condition == null) || (this.condition != null && this.condition.eval(context))) {
                    groupSites.add(from);
                }
                if (groupSites.size() > 0) {
                    context.setFrom(from);
                    final TIntArrayList sitesExplored = new TIntArrayList();
                    int m = 0;
                    while (sitesExplored.size() != groupSites.size()) {
                        final int site3 = groupSites.get(m);
                        final TopologyElement siteElement = topology.getGraphElements(this.type).get(site3);
                        final List<AbsoluteDirection> directions = this.dirnChoice.convertToAbsolute(this.type, siteElement, null, null, null, context);
                        for (final AbsoluteDirection direction : directions) {
                            final List<Step> steps = topology.trajectories().steps(this.type, siteElement.index(), this.type, direction);
                            for (final Step step : steps) {
                                final int to = step.to().id();
                                if (groupSites.contains(to)) {
                                    continue;
                                }
                                context.setTo(to);
                                if ((this.condition != null || who != cs.who(to, this.type)) && (this.condition == null || !this.condition.eval(context))) {
                                    continue;
                                }
                                groupSites.add(to);
                            }
                        }
                        sitesExplored.add(site3);
                        ++m;
                    }
                    context.setRegion(new Region(groupSites.toArray()));
                    final Moves movesApplied = this.movesToApply.eval(context);
                    for (final Move m2 : movesApplied.moves()) {
                        final int saveFrom = context.from();
                        final int saveTo = context.to();
                        context.setFrom(-1);
                        context.setTo(-1);
                        MoveUtilities.chainRuleCrossProduct(context, moves, null, m2, false);
                        context.setTo(saveTo);
                        context.setFrom(saveFrom);
                    }
                    sitesChecked.addAll(groupSites);
                }
            }
        }
        context.setTo(origTo);
        context.setFrom(origFrom);
        context.setRegion(origRegion);
        if (this.then() != null) {
            for (int j2 = 0; j2 < moves.moves().size(); ++j2) {
                moves.moves().get(j2).then().add(this.then().moves());
            }
        }
        return moves;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = super.gameFlags(game);
        gameFlags |= SiteType.stateFlags(this.type);
        gameFlags |= this.movesToApply.gameFlags(game);
        if (this.condition != null) {
            gameFlags |= this.condition.gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        this.type = SiteType.use(this.type, game);
        if (this.condition != null) {
            this.condition.preprocess(game);
        }
    }
    
    @Override
    public String toEnglish() {
        return "ForEachGroup";
    }
}
