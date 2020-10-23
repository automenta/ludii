// 
// Decompiled by Procyon v0.5.36
// 

package util;

import annotations.Hide;
import collections.FastArrayList;
import game.rules.play.moves.Moves;
import game.types.board.RelationType;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.Direction;
import game.util.directions.DirectionFacing;
import game.util.graph.Radial;
import org.apache.commons.rng.core.RandomProviderDefaultState;
import topology.Topology;
import topology.TopologyElement;
import util.action.Action;
import util.action.BaseAction;
import util.action.cards.ActionSetTrumpSuit;
import util.action.die.ActionSetDiceAllEqual;
import util.action.die.ActionUpdateDice;
import util.action.die.ActionUseDie;
import util.action.graph.ActionSetCost;
import util.action.graph.ActionSetPhase;
import util.action.hidden.ActionSetInvisible;
import util.action.hidden.ActionSetMasked;
import util.action.hidden.ActionSetVisible;
import util.action.move.*;
import util.action.others.*;
import util.action.puzzle.ActionReset;
import util.action.puzzle.ActionSet;
import util.action.puzzle.ActionToggle;
import util.action.state.*;
import util.locations.FullLocation;
import util.zhash.HashedBitSet;

import java.util.ArrayList;
import java.util.List;

@Hide
public class Move extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private int from;
    private int to;
    private int state;
    private boolean oriented;
    private int edge;
    private int mover;
    private int levelMin;
    private int levelMax;
    private final List<Action> actions;
    private final List<Moves> then;
    
    public Move(final List<Action> actions) {
        this.state = -1;
        this.oriented = true;
        this.edge = -1;
        this.mover = 0;
        this.levelMin = 0;
        this.levelMax = 0;
        this.then = new ArrayList<>();
        this.actions = actions;
    }
    
    public Move(final Action a) {
        this.state = -1;
        this.oriented = true;
        this.edge = -1;
        this.mover = 0;
        this.levelMin = 0;
        this.levelMax = 0;
        this.then = new ArrayList<>();
        (this.actions = new ArrayList<>(1)).add(a);
        this.from = a.from();
        this.to = a.to();
    }
    
    public Move(final Action a, final Action b) {
        this.state = -1;
        this.oriented = true;
        this.edge = -1;
        this.mover = 0;
        this.levelMin = 0;
        this.levelMax = 0;
        this.then = new ArrayList<>();
        (this.actions = new ArrayList<>(2)).add(a);
        this.actions.add(b);
        this.from = a.from();
        this.to = a.to();
    }
    
    public Move(final Move other) {
        this.state = -1;
        this.oriented = true;
        this.edge = -1;
        this.mover = 0;
        this.levelMin = 0;
        this.levelMax = 0;
        this.then = new ArrayList<>();
        this.from = other.from;
        this.to = other.to;
        this.actions = new ArrayList<>(other.actions);
    }
    
    public Move(final Action a, final FastArrayList<Move> list) {
        this.state = -1;
        this.oriented = true;
        this.edge = -1;
        this.mover = 0;
        this.levelMin = 0;
        this.levelMax = 0;
        this.then = new ArrayList<>();
        (this.actions = new ArrayList<>()).add(a);
        for (final Action b : list) {
            this.actions.add(b);
        }
        if (!this.actions.isEmpty()) {
            this.from = this.actions.get(0).from();
            this.to = this.actions.get(0).to();
        }
    }
    
    public Move(final FastArrayList<Move> list, final Action b) {
        this.state = -1;
        this.oriented = true;
        this.edge = -1;
        this.mover = 0;
        this.levelMin = 0;
        this.levelMax = 0;
        this.then = new ArrayList<>();
        this.actions = new ArrayList<>();
        for (final Action a : list) {
            this.actions.add(a);
        }
        this.actions.add(b);
        if (!this.actions.isEmpty()) {
            this.from = this.actions.get(0).from();
            this.to = this.actions.get(0).to();
        }
    }
    
    public Move(final FastArrayList<Move> list) {
        this.state = -1;
        this.oriented = true;
        this.edge = -1;
        this.mover = 0;
        this.levelMin = 0;
        this.levelMax = 0;
        this.then = new ArrayList<>();
        this.actions = new ArrayList<>();
        for (final Action a : list) {
            this.actions.add(a);
        }
        if (!this.actions.isEmpty()) {
            this.from = this.actions.get(0).from();
            this.to = this.actions.get(0).to();
        }
    }
    
    public Move(final String detailedString) {
        this.state = -1;
        this.oriented = true;
        this.edge = -1;
        this.mover = 0;
        this.levelMin = 0;
        this.levelMax = 0;
        this.then = new ArrayList<>();
        assert detailedString.startsWith("[Move:");
        final String strBeforeActions = detailedString.substring(0, detailedString.indexOf("actions="));
        final String strFrom = Action.extractData(strBeforeActions, "from");
        this.from = (strFrom.isEmpty() ? -1 : Integer.parseInt(strFrom));
        final String strTo = Action.extractData(strBeforeActions, "to");
        this.to = (strTo.isEmpty() ? -1 : Integer.parseInt(strTo));
        final String strState = Action.extractData(strBeforeActions, "state");
        this.state = (strState.isEmpty() ? -1 : Integer.parseInt(strState));
        final String strOriented = Action.extractData(strBeforeActions, "oriented");
        this.oriented = (strOriented.isEmpty() || Boolean.parseBoolean(strOriented));
        final String strEdge = Action.extractData(strBeforeActions, "edge");
        this.edge = (strEdge.isEmpty() ? -1 : Integer.parseInt(strEdge));
        final String strMover = Action.extractData(strBeforeActions, "mover");
        this.mover = Integer.parseInt(strMover);
        final String strLvlMin = Action.extractData(strBeforeActions, "levelMin");
        this.levelMin = (strLvlMin.isEmpty() ? 0 : Integer.parseInt(strLvlMin));
        final String strLvlMax = Action.extractData(strBeforeActions, "levelMax");
        this.levelMax = (strLvlMax.isEmpty() ? 0 : Integer.parseInt(strLvlMax));
        String str = detailedString;
        this.actions = new ArrayList<>();
        int closingIdx;
        for (str = str.substring(str.indexOf(",actions=") + ",actions=".length()); !str.isEmpty(); str = str.substring(closingIdx + 1)) {
            int numOpenBrackets;
            int idx;
            for (numOpenBrackets = 1, idx = 1; numOpenBrackets > 0 && idx < str.length(); ++idx) {
                if (str.charAt(idx) == '[') {
                    ++numOpenBrackets;
                }
                else if (str.charAt(idx) == ']') {
                    --numOpenBrackets;
                }
            }
            if (numOpenBrackets > 0) {
                return;
            }
            closingIdx = idx;
            final String actionStr = str.substring(0, closingIdx);
            if (actionStr.startsWith("[Move:") && actionStr.contains("actions=")) {
                this.actions.add(new Move(actionStr));
            }
            else if (actionStr.startsWith("[Add:")) {
                this.actions.add(new ActionAdd(actionStr));
            }
            else if (actionStr.startsWith("[Insert:")) {
                this.actions.add(new ActionInsert(actionStr));
            }
            else if (actionStr.startsWith("[SetStateAndUpdateDice:")) {
                this.actions.add(new ActionUpdateDice(actionStr));
            }
            else if (actionStr.startsWith("[SetInvisible:")) {
                this.actions.add(new ActionSetInvisible(actionStr));
            }
            else if (actionStr.startsWith("[Move:") && actionStr.contains("count=")) {
                this.actions.add(new ActionMoveN(actionStr));
            }
            else if (actionStr.startsWith("[Move:")) {
                this.actions.add(new ActionMove(actionStr));
            }
            else if (actionStr.startsWith("[StackMove:")) {
                this.actions.add(new ActionStackMove(actionStr));
            }
            else if (actionStr.startsWith("[SetValueOfPlayer:")) {
                this.actions.add(new ActionSetValueOfPlayer(actionStr));
            }
            else if (actionStr.startsWith("[SetAmount:")) {
                this.actions.add(new ActionSetAmount(actionStr));
            }
            else if (actionStr.startsWith("[Note:")) {
                this.actions.add(new ActionNote(actionStr));
            }
            else if (actionStr.startsWith("[SetPot:")) {
                this.actions.add(new ActionSetPot(actionStr));
            }
            else if (actionStr.startsWith("[Bet:")) {
                this.actions.add(new ActionBet(actionStr));
            }
            else if (actionStr.startsWith("[SetVisible:")) {
                this.actions.add(new ActionSetVisible(actionStr));
            }
            else if (actionStr.startsWith("[SetMasked:")) {
                this.actions.add(new ActionSetMasked(actionStr));
            }
            else if (actionStr.startsWith("[Pass:")) {
                this.actions.add(new ActionPass(actionStr));
            }
            else if (actionStr.startsWith("[SetTrumpSuit:")) {
                this.actions.add(new ActionSetTrumpSuit(actionStr));
            }
            else if (actionStr.startsWith("[SetPending:")) {
                this.actions.add(new ActionSetPending(actionStr));
            }
            else if (actionStr.startsWith("[Promote:")) {
                this.actions.add(new ActionPromote(actionStr));
            }
            else if (actionStr.startsWith("[Reset:")) {
                this.actions.add(new ActionReset(actionStr));
            }
            else if (actionStr.startsWith("[SetScore:")) {
                this.actions.add(new ActionSetScore(actionStr));
            }
            else if (actionStr.startsWith("[SetCost:")) {
                this.actions.add(new ActionSetCost(actionStr));
            }
            else if (actionStr.startsWith("[Propose:")) {
                this.actions.add(new ActionPropose(actionStr));
            }
            else if (actionStr.startsWith("[SetDiceAllEqual:")) {
                this.actions.add(new ActionSetDiceAllEqual(actionStr));
            }
            else if (actionStr.startsWith("[Vote:")) {
                this.actions.add(new ActionVote(actionStr));
            }
            else if (actionStr.startsWith("[Select:")) {
                this.actions.add(new ActionSelect(actionStr));
            }
            else if (actionStr.startsWith("[Set:")) {
                this.actions.add(new ActionSet(actionStr));
            }
            else if (actionStr.startsWith("[SetPhase:")) {
                this.actions.add(new ActionSetPhase(actionStr));
            }
            else if (actionStr.startsWith("[SetCount:")) {
                this.actions.add(new ActionSetCount(actionStr));
            }
            else if (actionStr.startsWith("[SetCounter:")) {
                this.actions.add(new ActionSetCounter(actionStr));
            }
            else if (actionStr.startsWith("[SetTemp:")) {
                this.actions.add(new ActionSetTemp(actionStr));
            }
            else if (actionStr.startsWith("[SetState:")) {
                this.actions.add(new ActionSetState(actionStr));
            }
            else if (actionStr.startsWith("[Toggle:")) {
                this.actions.add(new ActionToggle(actionStr));
            }
            else if (actionStr.startsWith("[Trigger:")) {
                this.actions.add(new ActionTrigger(actionStr));
            }
            else if (actionStr.startsWith("[Remove:")) {
                this.actions.add(new ActionRemove(actionStr));
            }
            else if (actionStr.startsWith("[SetNextPlayer:")) {
                this.actions.add(new ActionSetNextPlayer(actionStr));
            }
            else if (actionStr.startsWith("[Forfeit:")) {
                this.actions.add(new ActionForfeit(actionStr));
            }
            else if (actionStr.startsWith("[Swap:")) {
                this.actions.add(new ActionSwap(actionStr));
            }
            else if (actionStr.startsWith("[SetRotation:")) {
                this.actions.add(new ActionSetRotation(actionStr));
            }
            else if (actionStr.startsWith("[UseDie:")) {
                this.actions.add(new ActionUseDie(actionStr));
            }
            else if (actionStr.startsWith("[StoreStateInContext:")) {
                this.actions.add(new ActionStoreStateInContext(actionStr));
            }
            else if (actionStr.startsWith("[AddPlayerToTeam:")) {
                this.actions.add(new ActionAddPlayerToTeam(actionStr));
            }
            else if (actionStr.startsWith("[Noop")) {
                this.actions.add(new ActionNoop(actionStr));
            }
            else if (actionStr.startsWith("[NextInstance")) {
                this.actions.add(new ActionNextInstance(actionStr));
            }
            else if (actionStr.startsWith("[Copy")) {
                this.actions.add(new ActionCopy(actionStr));
            }
            else {
                System.err.println("Move constructor does not recognise action: " + str);
            }
        }
    }
    
    public int mover() {
        return this.mover;
    }
    
    public void setMover(final int who) {
        this.mover = who;
    }
    
    public List<Action> getAllActions(final Context context) {
        final Context contextCopy = new TempContext(context);
        final RandomProviderDefaultState realRngState = (RandomProviderDefaultState)context.rng().saveState();
        contextCopy.rng().restoreState(realRngState);
        return contextCopy.game().apply(contextCopy, this).actions();
    }
    
    public List<Action> getAllBaseActions(final Context context) {
        final Context contextCopy = new Context(context);
        final RandomProviderDefaultState realRngState = (RandomProviderDefaultState)context.rng().saveState();
        contextCopy.rng().restoreState(realRngState);
        return ((Move)this.apply(contextCopy, true)).actions();
    }
    
    public List<Action> actions() {
        return this.actions;
    }
    
    public List<Moves> then() {
        return this.then;
    }
    
    @Override
    public final Action apply(final Context context, final boolean store) {
        final List<Action> returnActions = new ArrayList<>(this.actions.size());
        final Trial trial = context.trial();
        for (final Action action : this.actions) {
            final Action returnAction = action.apply(context, false);
            if (returnAction instanceof Move) {
                returnActions.addAll(((Move)returnAction).actions);
            }
            else {
                returnActions.add(returnAction);
            }
        }
        if (store) {
            trial.addMove(this);
        }
        for (final Moves consequent : this.then) {
            final FastArrayList<Move> postActions = consequent.eval(context).moves();
            for (final Move m : postActions) {
                final Action innerApplied = m.apply(context, false);
                if (innerApplied instanceof Move) {
                    returnActions.addAll(((Move)innerApplied).actions);
                }
                else {
                    returnActions.add(innerApplied);
                }
            }
        }
        if (store && context.game().hasSequenceCapture() && !this.containsReplayAction(returnActions)) {
            final HashedBitSet pieceToRemove = context.state().piecesToRemove();
            for (int site = pieceToRemove.nextSetBit(0); site >= 0; site = pieceToRemove.nextSetBit(site + 1)) {
                final ActionRemove remove = new ActionRemove(context.board().defaultSite(), site, true);
                if (!returnActions.contains(remove)) {
                    remove.apply(context, false);
                    returnActions.add(0, remove);
                }
            }
            context.state().reInitCapturedPiece();
        }
        final Move returnMove = new Move(returnActions);
        returnMove.setFromNonDecision(this.from);
        returnMove.setToNonDecision(this.to);
        returnMove.setMover(this.mover);
        if (store) {
            trial.moves().set(trial.numMoves() - 1, returnMove);
        }
        return returnMove;
    }
    
    public boolean containsReplayAction(final List<Action> actionsList) {
        for (final Action action : actionsList) {
            if (action instanceof ActionSetNextPlayer) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isPass() {
        boolean foundPass = false;
        for (final Action a : this.actions) {
            if (a.isDecision() && !a.isPass()) {
                return false;
            }
            foundPass = (foundPass || a.isPass());
        }
        return foundPass;
    }
    
    @Override
    public boolean isForfeit() {
        boolean foundForfeit = false;
        for (final Action a : this.actions) {
            if (a.isDecision() && !a.isForfeit()) {
                return false;
            }
            foundForfeit = (foundForfeit || a.isForfeit());
        }
        return foundForfeit;
    }
    
    @Override
    public boolean isSwap() {
        boolean foundSwap = false;
        for (final Action a : this.actions) {
            if (a.isDecision() && !a.isSwap()) {
                return false;
            }
            foundSwap = (foundSwap || a.isSwap());
        }
        return foundSwap;
    }
    
    @Override
    public int playerSelected() {
        for (final Action a : this.actions) {
            if (a.isDecision()) {
                return a.playerSelected();
            }
        }
        return -1;
    }
    
    @Override
    public boolean isOtherMove() {
        boolean foundOtherMove = false;
        for (final Action a : this.actions) {
            if (a.isDecision() && !a.isOtherMove()) {
                return false;
            }
            foundOtherMove = (foundOtherMove || a.isOtherMove());
        }
        return foundOtherMove;
    }
    
    @Override
    public boolean containsNextInstance() {
        for (final Action a : this.actions) {
            if (a.containsNextInstance()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Action a : this.actions) {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append(a.toString());
        }
        if (this.actions.size() > 1) {
            sb.insert(0, '[');
            sb.append(']');
        }
        else if (this.actions.isEmpty()) {
            sb.append("[Empty Move]");
        }
        if (!this.then.isEmpty()) {
            sb.append(this.then.toString());
        }
        return sb.toString();
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        for (final Action a : this.actions) {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append(a.toTurnFormat(context));
        }
        if (this.actions.size() > 1) {
            sb.insert(0, '[');
            sb.append(']');
        }
        else if (this.actions.isEmpty()) {
            sb.append("[Empty Move]");
        }
        if (!this.then.isEmpty()) {
            sb.append(this.then.toString());
        }
        return sb.toString();
    }
    
    @Override
    public int from() {
        for (final Action a : this.actions) {
            if (a.isDecision()) {
                return a.from();
            }
        }
        return -1;
    }
    
    public int fromAfterSubsequents() {
        if (!this.actions.isEmpty()) {
            int i = 1;
            while (this.actions.get(this.actions.size() - i).from() == -1) {
                ++i;
                if (this.actions.size() - i < 0) {
                    return -1;
                }
            }
            return this.actions.get(this.actions.size() - i).from();
        }
        return -1;
    }
    
    @Override
    public int levelFrom() {
        for (final Action a : this.actions) {
            if (a.isDecision()) {
                return a.levelFrom();
            }
        }
        return 0;
    }
    
    @Override
    public int to() {
        for (final Action a : this.actions) {
            if (a.isDecision()) {
                return a.to();
            }
        }
        return -1;
    }
    
    public int toAfterSubsequents() {
        if (!this.actions.isEmpty()) {
            int i = 1;
            while (this.actions.get(this.actions.size() - i).to() == -1) {
                ++i;
                if (this.actions.size() - i < 0) {
                    return -1;
                }
            }
            return this.actions.get(this.actions.size() - i).to();
        }
        return -1;
    }
    
    @Override
    public int levelTo() {
        for (final Action a : this.actions) {
            if (a.isDecision()) {
                return a.levelTo();
            }
        }
        return 0;
    }
    
    @Override
    public int what() {
        for (final Action a : this.actions) {
            if (a.isDecision()) {
                return a.what();
            }
        }
        return 0;
    }
    
    @Override
    public int state() {
        for (final Action a : this.actions) {
            if (a.isDecision()) {
                return a.state();
            }
        }
        return 0;
    }
    
    @Override
    public int count() {
        for (final Action a : this.actions) {
            if (a.isDecision()) {
                return a.count();
            }
        }
        return 1;
    }
    
    @Override
    public boolean isStacking() {
        for (final Action a : this.actions) {
            if (a.isDecision()) {
                return a.isStacking();
            }
        }
        return false;
    }
    
    @Override
    public boolean[] hidden() {
        for (final Action a : this.actions) {
            if (a.isDecision()) {
                return a.hidden();
            }
        }
        return null;
    }
    
    @Override
    public int who() {
        for (final Action a : this.actions) {
            if (a.isDecision()) {
                return a.who();
            }
        }
        return 0;
    }
    
    @Override
    public boolean isDecision() {
        for (final Action a : this.actions) {
            if (a.isDecision()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public final void setDecision(final boolean decision) {
        for (final Action a : this.actions()) {
            a.setDecision(decision);
        }
    }
    
    @Override
    public final Move withDecision(final boolean dec) {
        return this;
    }
    
    @Override
    public boolean matchesUserMove(final int siteA, final int levelA, final SiteType graphElementTypeA, final int siteB, final int levelB, final SiteType graphElementTypeB) {
        return this.fromNonDecision() == siteA && this.levelFrom() == levelA && this.fromType() == graphElementTypeA && this.toNonDecision() == siteB && this.levelTo() == levelB && this.toType() == graphElementTypeB;
    }
    
    public int fromNonDecision() {
        return this.from;
    }
    
    public void setFromNonDecision(final int from) {
        this.from = from;
    }
    
    public int toNonDecision() {
        return this.to;
    }
    
    public void setToNonDecision(final int to) {
        this.to = to;
    }
    
    public Move withFrom(final int fromW) {
        this.from = fromW;
        return this;
    }
    
    public Move withTo(final int toW) {
        this.to = toW;
        return this;
    }
    
    public int levelMinNonDecision() {
        return this.levelMin;
    }
    
    public void setLevelMinNonDecision(final int levelMin) {
        this.levelMin = levelMin;
    }
    
    public int levelMaxNonDecision() {
        return this.levelMax;
    }
    
    public void setLevelMaxNonDecision(final int levelMax) {
        this.levelMax = levelMax;
    }
    
    public boolean isOrientedMove() {
        return this.oriented;
    }
    
    public void setEdgeMove(final int edge) {
        this.edge = edge;
    }
    
    public int isEdgeMove() {
        return this.edge;
    }
    
    public void setStateNonDecision(final int state) {
        this.state = state;
    }
    
    public int stateNonDecision() {
        return this.state;
    }
    
    public void setOrientedMove(final boolean oriented) {
        this.oriented = oriented;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.actions == null) ? 0 : this.actions.hashCode());
        result = 31 * result + ((this.then == null) ? 0 : this.then.hashCode());
        result = 31 * result + (this.from + this.to);
        result = 31 * result + this.levelMax;
        result = 31 * result + this.levelMin;
        result = 31 * result + this.state;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Move)) {
            return false;
        }
        final Move other = (Move)obj;
        if (this.actions == null) {
            if (other.actions != null) {
                return false;
            }
        }
        else if (!this.actions.equals(other.actions)) {
            return false;
        }
        if (this.then == null) {
            if (other.then != null) {
                return false;
            }
        }
        else if (!this.then.equals(other.then)) {
            return false;
        }
        if (this.oriented || other.oriented) {
            return this.from == other.from && this.levelMax == other.levelMax && this.levelMin == other.levelMin && this.to == other.to && this.state == other.state;
        }
        return (this.from == other.from || this.from == other.to) && this.levelMax == other.levelMax && this.levelMin == other.levelMin && (this.to == other.from || this.to == other.to) && this.state == other.state;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Move:");
        sb.append("mover=").append(this.mover);
        if (this.from != -1) {
            sb.append(",from=").append(this.from);
        }
        if (this.to != -1) {
            sb.append(",to=").append(this.to);
        }
        if (this.state != -1) {
            sb.append(",state=").append(this.state);
        }
        if (!this.oriented) {
            sb.append(",oriented=").append(this.oriented);
        }
        if (this.edge != -1) {
            sb.append(",edge=").append(this.edge);
        }
        if (this.levelMin != 0 && this.levelMax != 0) {
            sb.append(",levelMin=").append(this.levelMin);
            sb.append(",levelMax=").append(this.levelMax);
        }
        final List<Action> allActions = (context == null) ? this.actions : this.getAllActions(context);
        sb.append(",actions=");
        for (int i = 0; i < allActions.size(); ++i) {
            sb.append(allActions.get(i).toTrialFormat(context));
            if (i < allActions.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public String toEnglishString(final Context context) {
        return this.toTurnFormat(context);
    }
    
    @Override
    public String getDescription() {
        return "Move";
    }
    
    @Override
    public int rotation() {
        for (final Action a : this.actions) {
            if (a.isDecision()) {
                return a.rotation();
            }
        }
        return 0;
    }
    
    @Override
    public SiteType fromType() {
        for (final Action a : this.actions) {
            if (a.isDecision()) {
                return a.fromType();
            }
        }
        return SiteType.Cell;
    }
    
    @Override
    public SiteType toType() {
        for (final Action a : this.actions) {
            if (a.isDecision()) {
                return a.toType();
            }
        }
        return SiteType.Cell;
    }
    
    public FullLocation getFromLocation() {
        return new FullLocation(this.from, this.levelFrom(), this.fromType());
    }
    
    public FullLocation getToLocation() {
        return new FullLocation(this.to, this.levelTo(), this.toType());
    }
    
    public Direction direction(final Context context) {
        final Topology topo = context.topology();
        if (this.from == this.to || this.from == -1 || this.to == -1 || this.fromType() != this.toType()) {
            return null;
        }
        final SiteType type = this.toType();
        if (this.from >= topo.getGraphElements(type).size()) {
            return null;
        }
        if (this.to >= topo.getGraphElements(type).size()) {
            return null;
        }
        final TopologyElement fromElement = topo.getGraphElement(type, this.from);
        final TopologyElement toElement = topo.getGraphElement(type, this.to);
        final List<DirectionFacing> directionsSupported = topo.supportedDirections(RelationType.All, type);
        for (final DirectionFacing direction : directionsSupported) {
            final AbsoluteDirection absDirection = direction.toAbsolute();
            final List<Radial> radials = topo.trajectories().radials(type, fromElement.index(), absDirection);
            for (final Radial radial : radials) {
                for (int toIdx = 1; toIdx < radial.steps().length; ++toIdx) {
                    final int toRadial = radial.steps()[toIdx].id();
                    if (toRadial == toElement.index()) {
                        return absDirection;
                    }
                }
            }
        }
        return null;
    }
}
