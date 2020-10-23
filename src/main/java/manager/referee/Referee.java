// 
// Decompiled by Procyon v0.5.36
// 

package manager.referee;

import game.Game;
import game.rules.play.moves.Moves;
import game.types.play.ModeType;
import manager.Manager;
import manager.ai.AIDetails;
import manager.ai.AIMenuName;
import manager.ai.AIUtil;
import manager.game.GameSetup;
import manager.network.DatabaseFunctions;
import manager.network.SettingsNetwork;
import manager.utils.AnimationUtil;
import manager.utils.ContextSnapshot;
import manager.utils.SettingsManager;
import org.json.JSONObject;
import util.*;
import util.action.Action;
import util.action.others.ActionSwap;
import util.locations.FullLocation;
import util.model.Model;
import utils.AIUtils;
import utils.RandomAI;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class Referee
{
    private Context context;
    protected Context intermediaryContext;
    final AtomicBoolean allowHumanBasedStepStart;
    volatile boolean waitingForAnim;
    private final AtomicBoolean wantNextMoveCall;
    public static final int AI_VIS_UPDATE_TIME = 40;
    
    public Referee() {
        this.intermediaryContext = null;
        this.allowHumanBasedStepStart = new AtomicBoolean(true);
        this.waitingForAnim = false;
        this.wantNextMoveCall = new AtomicBoolean(false);
    }
    
    public Context context() {
        return this.context;
    }
    
    public void setContext(final Context newContext) {
        this.context = newContext;
    }
    
    public Referee setGame(final Game game) {
        ContextSnapshot.setContext(this.context = new Context(game, new Trial(game)));
        Manager.updateCurrentGameRngInternalState();
        return this;
    }
    
    public void makeSavedMove(final Move m) {
        this.context.game().apply(this.context, m);
    }
    
    public void applyHumanMoveToGame(final Move move) {
        SettingsVC.selectedLocation = new FullLocation(-1);
        if (SettingsNetwork.getActiveGameId() != 0) {
            if (SettingsNetwork.getNetworkPlayerNumber() != move.mover()) {
                Manager.app.setVolatileMessage("Wait your turn!");
                return;
            }
            for (int i = 1; i <= this.context.game().players().count(); ++i) {
                if (Manager.aiSelected()[i].name().trim().isEmpty()) {
                    Manager.app.setVolatileMessage("Not all players have joined yet.");
                    return;
                }
            }
        }
        if (this.waitingForAnim) {
            return;
        }
        if (!SettingsVC.pieceBeingDragged) {
            AnimationUtil.saveMoveAnimationDetails(move);
        }
        final boolean autoPass = move.isPass() && this.context.game().moves(this.context).moves().isEmpty();
        final Model model = this.context.model();
        if (model.isReady() && !this.nextMove(true)) {
            return;
        }
        if (!autoPass) {
            while (true) {
                if (!model.isReady()) {
                    if (model.isRunning()) {
                        break;
                    }
                }
                try {
                    Thread.sleep(10L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        long timeUntilMoveMade = 0L;
        if (!SettingsVC.pieceBeingDragged) {
            timeUntilMoveMade = (MoveUtil.animatePieceMovement(move) ? 435L : 0L);
        }

        final Runnable runnable = () -> {
            String tempMessage;

            String tempMessage2;
            String tempMessage3;
            Move appliedMove = model.applyHumanMove(this.context(), move, move.mover());
            if (model.movesPerPlayer() != null) {
                ArrayList<Integer> playerIdsWaitingFor = new ArrayList<>();
                for (int j = 1; j < model.movesPerPlayer().length; ++j) {
                    Move m = model.movesPerPlayer()[j];
                    if (m == null) {
                        playerIdsWaitingFor.add(j);
                    }
                }
                if (!playerIdsWaitingFor.isEmpty()) {
                    tempMessage = "Waiting for moves from";
                    for (int index : playerIdsWaitingFor) {
                        tempMessage = tempMessage + " P" + index + " and";
                    }
                    tempMessage2 = tempMessage.substring(0, tempMessage.length() - 4);
                    tempMessage3 = tempMessage2 + ".\n";
                    Manager.app.addTextToStatusPanel(tempMessage3);
                }
            }
            if (appliedMove != null) {
                this.postMoveApplication(appliedMove, true);
            }
            this.waitingForAnim = false;
        };
        if (timeUntilMoveMade > 0L) {
            final Timer t = new Timer();
            this.waitingForAnim = true;
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    runnable.run();
                }
            }, timeUntilMoveMade);
        }
        else {
            runnable.run();
        }
    }
    
    public void applyNetworkMoveToGame(final Move move) {
        final Model model = this.context.model();
        if (model.isReady() && !this.nextMove(true)) {
            return;
        }
        Move realMoveToApply = move;
        boolean validMove = false;
        final Moves legal = this.context.game().moves(this.context);
        for (final Move m : legal.moves()) {
            validMove = true;
            if (move.getAllActions(this.context).size() > m.getAllActions(this.context).size()) {
                validMove = false;
            }
            else {
                for (int i = 0; i < move.getAllActions(this.context).size(); ++i) {
                    if (!m.getAllActions(this.context).get(i).equals(move.getAllActions(this.context).get(i))) {
                        validMove = false;
                    }
                }
            }
            if (validMove) {
                realMoveToApply = m;
                break;
            }
        }
        if (legal.moves().isEmpty() && realMoveToApply.isPass()) {
            validMove = true;
        }
        if (!validMove) {
            System.out.println("received move was not legal: " + move);
            return;
        }
        final Move appliedMove = this.context.model().applyHumanMove(this.context, realMoveToApply, realMoveToApply.mover());
        if (appliedMove != null) {
            this.postMoveApplication(appliedMove, false);
        }
        this.checkNetworkSwap(realMoveToApply);
    }
    
    public void randomMove() {
        if (this.waitingForAnim) {
            return;
        }
        final Model model = this.context.model();
        final int numMovesBeforeStep = this.context.trial().numMoves();
        if (model.isReady() && !this.nextMove(true)) {
            return;
        }
        if (this.context.trial().numMoves() > numMovesBeforeStep) {
            return;
        }
        while (true) {
            if (!model.isReady()) {
                if (model.isRunning()) {
                    break;
                }
            }
            try {
                Thread.sleep(10L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                Referee.this.context().model().randomStep(Referee.this.context(), move -> {
                    Referee.this.preAIMoveApplication(move);
                    final long waitTime = MoveUtil.animatePieceMovement(move) ? 430L : 0L;
                    if (waitTime > 0L) {
                        Referee.this.waitingForAnim = true;
                    }
                    return waitTime;
                }, move -> {
                    Referee.this.postMoveApplication(move, true);
                    return -1L;
                });
                Referee.this.waitingForAnim = false;
            }
        }, 0L);
    }
    
    public int timeRandomPlayouts() {
        final Context timingContext = new Context(this.context);
        final Game game = timingContext.game();
        long stopAt = 0L;
        long start = System.nanoTime();
        for (double abortAt = start + 1.0E10; stopAt < abortAt; stopAt = System.nanoTime()) {
            game.start(timingContext);
            game.playout(timingContext, null, 1.0, null, null, 0, -1, -1.0f, ThreadLocalRandom.current());
        }
        stopAt = 0L;
        System.gc();
        start = System.nanoTime();
        double abortAt;
        int playouts;
        int moveDone;
        for (abortAt = start + 3.0E10, playouts = 0, moveDone = 0; stopAt < abortAt; stopAt = System.nanoTime(), moveDone += timingContext.trial().moves().size(), ++playouts) {
            game.start(timingContext);
            game.playout(timingContext, null, 1.0, null, null, 0, -1, -1.0f, ThreadLocalRandom.current());
        }
        final double secs = (stopAt - start) / 1.0E9;
        final int rate = (int)(playouts / secs);
        final int rateMove = (int)(moveDone / secs);
        System.out.println(rate + "p/s");
        System.out.println(rateMove + "m/s");
        return rate;
    }
    
    public void randomPlayout() {
        if (!this.context.trial().over()) {
            this.interruptAI();
            if (Manager.savedTrial() != null) {
                final List<Move> tempActions = new ArrayList<>(this.context.trial().moves());
                Manager.app.restartGame(false);
                for (int i = this.context.trial().moves().size(); i < tempActions.size(); ++i) {
                    this.makeSavedMove(tempActions.get(i));
                }
            }
            final Game gameToPlayout = this.context.game();
            gameToPlayout.playout(this.context, null, 1.0, null, null, 0, -1, -1.0f, ThreadLocalRandom.current());
            if (this.context().isAMatch()) {
                final List<Trial> completedTrials = Manager.ref().context().completedTrials();
                Manager.setInstanceTrialsSoFar(new ArrayList<>(completedTrials));
                Manager.setCurrentGameIndexForMatch(completedTrials.size());
                Manager.app.cleanUpAfterLoading("", this.context().currentInstanceContext().game(), false);
                Manager.app.updateFrameTitle();
                ContextSnapshot.setContext(this.context);
                GameSetup.setMVC();
            }
            Manager.app.updateTabs(this.context);
            SettingsManager.canSendToDatabase = false;
            EventQueue.invokeLater(() -> Manager.app.repaint());
        }
    }
    
    public void randomPlayoutSingleInstance() {
        if (!this.context().isAMatch()) {
            return;
        }
        final Context instanceContext = this.context.currentInstanceContext();
        final Trial instanceTrial = instanceContext.trial();
        if (!instanceTrial.over()) {
            this.interruptAI();
            final Trial startInstanceTrial = this.context.currentInstanceContext().trial();
            int currentMovesMade = startInstanceTrial.moves().size();
            if (Manager.savedTrial() != null) {
                final List<Move> tempActions = new ArrayList<>(this.context.trial().moves());
                Manager.app.restartGame(false);
                for (int i = this.context.trial().moves().size(); i < tempActions.size(); ++i) {
                    this.makeSavedMove(tempActions.get(i));
                }
            }
            final Game gameToPlayout = instanceContext.game();
            gameToPlayout.playout(instanceContext, null, 1.0, null, null, 0, -1, -1.0f, ThreadLocalRandom.current());
            final List<Move> subtrialMoves = instanceContext.trial().moves();
            final int numMovesAfterPlayout = subtrialMoves.size();
            for (int numMovesToAppend = numMovesAfterPlayout - currentMovesMade, j = 0; j < numMovesToAppend; ++j) {
                this.context.trial().moves().add(subtrialMoves.get(subtrialMoves.size() - numMovesToAppend + j));
            }
            if (instanceTrial.over()) {
                final Moves legalMatchMoves = this.context.game().moves(this.context);
                assert legalMatchMoves.moves().size() == 1;
                assert legalMatchMoves.moves().get(0).containsNextInstance();
                this.context.game().apply(this.context, legalMatchMoves.moves().get(0));
            }
            final List<Trial> completedTrials = Manager.ref().context().completedTrials();
            Manager.setInstanceTrialsSoFar(new ArrayList<>(completedTrials));
            Manager.setCurrentGameIndexForMatch(completedTrials.size());
            Manager.app.cleanUpAfterLoading("", this.context().currentInstanceContext().game(), false);
            Manager.app.updateFrameTitle();
            if (this.context().currentInstanceContext().trial() != startInstanceTrial) {
                currentMovesMade = this.context().currentInstanceContext().trial().numInitialPlacementMoves();
            }
            ContextSnapshot.setContext(this.context);
            GameSetup.setMVC();
            Manager.app.updateTabs(this.context);
            SettingsManager.canSendToDatabase = false;
            EventQueue.invokeLater(() -> Manager.app.repaint());
        }
    }
    
    public synchronized boolean nextMove(final boolean humanBasedStepStart) {
        this.wantNextMoveCall.set(false);
        if (!this.allowHumanBasedStepStart.get() && humanBasedStepStart) {
            return false;
        }
        try {
            if (!this.context().trial().over()) {
                final Model model = this.context.model();
                if (this.context.game().mode().mode() == ModeType.Simulation) {
                    final List<AI> list = new ArrayList<>();
                    list.add(new RandomAI());
                    model.unpauseAgents(this.context, list, new double[] { SettingsManager.tickLength }, -1, -1, 0.0, null, null);
                    this.postMoveApplication(this.context.trial().lastMove(), true);
                }
                if (!model.isReady() && model.isRunning() && !SettingsManager.agentsPaused) {
                    final double[] thinkTime = AIDetails.convertToThinkTimeArray(Manager.aiSelected);
                    List<AI> agents = null;
                    if (!SettingsManager.agentsPaused) {
                        agents = AIDetails.convertToAIList(Manager.aiSelected);
                    }
                    if (agents != null) {
                        AIUtil.checkIfAgentsAllowed(this.context);
                    }
                    model.unpauseAgents(this.context, agents, thinkTime, -1, -1, 0.4, move -> {
                        Referee.this.preAIMoveApplication(move);
                        return MoveUtil.animatePieceMovement(move) ? 430L : 0L;
                    }, move -> {
                        Referee.this.postMoveApplication(move, true);
                        return -1L;
                    });
                }
                else {
                    this.allowHumanBasedStepStart.set(model.expectsHumanInput());

                    final Thread thread = new Thread(() -> {
                        int p;


                        JSONObject json;
                        List<AI> liveAIs;
                        Exception e;



                        double[] thinkTime2 = AIDetails.convertToThinkTimeArray(Manager.aiSelected);
                        List<AI> agents2 = null;
                        if (!SettingsManager.agentsPaused) {
                            agents2 = AIDetails.convertToAIList(Manager.aiSelected);
                        }
                        if (agents2 != null) {
                            for (p = 1; p < agents2.size(); ++p) {
                                if (agents2.get(p) != null) {
                                    if (!agents2.get(p).supportsGame(this.context.game())) {
                                        AI oldAI = Manager.aiSelected[p].ai();
                                        AI newAI = AIUtils.defaultAiForGame(this.context.game());
                                        json = new JSONObject().put("AI", new JSONObject().put("algorithm", newAI.friendlyName));
                                        Manager.aiSelected[p] = new AIDetails(json, p, AIMenuName.LudiiAI);
                                        EventQueue.invokeLater(() -> Manager.app.addTextToStatusPanel(oldAI.friendlyName + " does not support this game. Switching to default AI for this game: " + newAI.friendlyName + ".\n"));
                                    }
                                    agents2.get(p).initIfNeeded(this.context.game(), p);
                                }
                            }
                        }
                        Trial startInstanceTrial = this.context.currentInstanceContext().trial();
                        model.startNewStep(this.context, agents2, thinkTime2, -1, -1, 0.5, false, true, false, move -> {
                            Referee.this.preAIMoveApplication(move);
                            return MoveUtil.animatePieceMovement(move) ? 430L : 0L;
                        }, move -> {
                            Referee.this.postMoveApplication(move, true);
                            return -1L;
                        });
                        while (!model.isReady()) {
                            Manager.setLiveAIs(model.getLiveAIs());
                            this.allowHumanBasedStepStart.set(model.expectsHumanInput());
                            try {
                                liveAIs = Manager.liveAIs();
                                if (liveAIs != null && !liveAIs.isEmpty()) {
                                    EventQueue.invokeAndWait(() -> Manager.app.repaint());
                                }
                                Thread.sleep(40L);
                            }
                            catch (InterruptedException | InvocationTargetException ex2) {
                                e = ex2;
                                e.printStackTrace();
                            }
                        }
                        EventQueue.invokeLater(() -> Manager.app.repaint());
                        this.allowHumanBasedStepStart.set(false);
                        Manager.setLiveAIs(null);
                        if (startInstanceTrial != this.context.currentInstanceContext().trial()) {
                            SettingsManager.agentsPaused = true;
                        }
                        if (!SettingsManager.agentsPaused) {
                            EventQueue.invokeLater(() -> {
                                List<AI> ais = model.getLastStepAIs();
                                if (ais!=null) {
                                    for (AI ai : ais) {
                                        if (ai != null) {
                                            String analysisReport = ai.generateAnalysisReport();
                                            if (analysisReport != null) {
                                                Manager.app.addTextToAnalysisPanel(analysisReport + "\n");
                                            }
                                        }
                                    }
                                }
                            });
                            if (!this.context().trial().over()) {
                                this.wantNextMoveCall.set(true);
                                this.nextMove(false);
                            }
                            else {
                                this.allowHumanBasedStepStart.set(true);
                            }
                        }
                        else {
                            this.allowHumanBasedStepStart.set(true);
                        }
                    });
                    thread.setDaemon(true);
                    thread.start();
                    while (!this.wantNextMoveCall.get() && thread.isAlive()) {
                        if (!model.isReady()) {
                            if (!model.isRunning()) {
                                continue;
                            }
                            break;
                        }
                    }
                }
                return true;
            }
            return false;
        }
        finally {
            if (!humanBasedStepStart && !this.context.model().isRunning()) {
                this.allowHumanBasedStepStart.set(true);
            }
        }
    }
    
    void preAIMoveApplication(final Move move) {
        if (!this.context.model().verifyMoveLegal(this.context, move)) {
            System.err.println("AI selected illegal move: " + move.getAllActions(this.context));
        }
        AnimationUtil.saveMoveAnimationDetails(move);
    }
    
    void postMoveApplication(final Move move, final boolean sendMove) {
        Manager.setSavedTrial(null);
        Manager.app.setTemporaryMessage("");
        SettingsVC.selectedLocation = new FullLocation(-1);
        SettingsManager.nextFrameIsAnimated = false;
        AnimationUtil.animationTimer.cancel();
        String scoreString = "";
        if (this.context.game().requiresScore()) {
            for (int i = 1; i <= this.context.game().players().count(); ++i) {
                scoreString = scoreString + this.context.score(this.context.state().playerToAgent(i)) + ",";
            }
        }
        if (SettingsNetwork.getActiveGameId() != 0 && sendMove) {
            System.out.println("sending to db");
            System.out.println(move.actions());
            DatabaseFunctions.sendMoveToDatabase(move, this.context.state().mover(), scoreString);
            this.checkNetworkSwap(move);
        }
        if (this.context.model().isReady()) {
            final int moveNumber = this.context.currentInstanceContext().trial().numMoves() - 1;
            if (this.context().trial().over() || (this.context().isAMatch() && moveNumber < this.context.currentInstanceContext().trial().numInitialPlacementMoves())) {
                Manager.app.gameOverTasks();
            }
            Manager.app.updateTabs(this.context);
            EventQueue.invokeLater(() -> {
                if (SettingsManager.moveSoundEffect && (Manager.aiSelected()[this.context.state().playerToAgent(move.mover())].ai() != null || !sendMove)) {
                    Manager.app.playSound("Pling-KevanGC-1485374730");
                }
                this.checkInstantPass();
            });
        }
        EventQueue.invokeLater(() -> Manager.app.repaint());
    }
    
    private void checkInstantPass() {
        final Moves legal = this.context.game().moves(this.context);
        if (Manager.aiSelected()[this.context.state().mover()].ai() == null && legal.moves().size() == 1 && legal.moves().get(0).isPass() && !this.context.game().isStochasticGame()) {
            this.applyHumanMoveToGame(legal.moves().get(0));
        }
    }
    
    public void interruptAI() {
        this.context.model().interruptAIs();
        Manager.setLiveAIs(null);
        this.allowHumanBasedStepStart.set(true);
    }
    
    public void setIntermediaryContext(final Context context) {
        this.intermediaryContext = context;
    }
    
    public Context intermediaryContext() {
        return this.intermediaryContext;
    }
    
    public void checkNetworkSwap(final Move move) {
        if (move.isSwap()) {
            for (final Action a : move.actions()) {
                if (a.isSwap()) {
                    final int p1 = ((ActionSwap)a).player1();
                    final int p2 = ((ActionSwap)a).player2();
                    if (SettingsNetwork.getNetworkPlayerNumber() == p1) {
                        SettingsNetwork.setNetworkPlayerNumber(p2);
                    }
                    else {
                        if (SettingsNetwork.getNetworkPlayerNumber() != p2) {
                            continue;
                        }
                        SettingsNetwork.setNetworkPlayerNumber(p1);
                    }
                }
            }
        }
    }
}
