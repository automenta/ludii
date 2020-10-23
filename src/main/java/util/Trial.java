// 
// Decompiled by Procyon v0.5.36
// 

package util;

import game.Game;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TLongArrayList;
import main.Status;
import main.collections.FastTLongArrayList;
import org.apache.commons.rng.core.RandomProviderDefaultState;
import util.state.State;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Trial implements Serializable
{
    private static final long serialVersionUID = 1L;
    private List<Move> moves;
    private int numInitialPlacementMoves;
    private final List<TIntArrayList> startingPos;
    protected Status status;
    protected Moves legalMoves;
    private FastTLongArrayList previousStates;
    private FastTLongArrayList previousStatesWithinATurn;
    private int numSubmovesPlayed;
    private final double[] ranking;
    protected transient AuxilTrialData auxilTrialData;
    
    public Trial(final Game game) {
        this.numInitialPlacementMoves = 0;
        this.status = null;
        this.numSubmovesPlayed = 0;
        this.auxilTrialData = null;
        if (game.hasSubgames()) {
            this.startingPos = null;
            this.legalMoves = null;
            this.previousStates = null;
            this.previousStatesWithinATurn = null;
        }
        else {
            this.startingPos = new ArrayList<>();
            this.legalMoves = new BaseMoves(null);
            this.previousStates = new FastTLongArrayList();
            this.previousStatesWithinATurn = new FastTLongArrayList();
        }
        this.moves = new ArrayList<>();
        this.ranking = new double[game.players().count() + 1];
    }
    
    public Trial(final Trial other) {
        this.numInitialPlacementMoves = 0;
        this.status = null;
        this.numSubmovesPlayed = 0;
        this.auxilTrialData = null;
        this.moves = new ArrayList<>(other.moves);
        this.numInitialPlacementMoves = other.numInitialPlacementMoves;
        this.startingPos = ((other.startingPos == null) ? null : new ArrayList<>(other.startingPos));
        this.status = other.status();
        if (other.legalMoves != null) {
            this.legalMoves = new BaseMoves(null);
            this.legalMoves.moves().addAll(other.legalMoves.moves());
        }
        else {
            other.legalMoves = null;
        }
        if (other.previousStates != null) {
            this.previousStates = new FastTLongArrayList(other.previousStates);
        }
        if (other.previousStatesWithinATurn != null) {
            this.previousStatesWithinATurn = new FastTLongArrayList(other.previousStatesWithinATurn);
        }
        this.numSubmovesPlayed = other.numSubmovesPlayed;
        this.ranking = Arrays.copyOf(other.ranking, other.ranking.length);
    }
    
    public List<Move> moves() {
        return this.moves;
    }
    
    public AuxilTrialData auxilTrialData() {
        return this.auxilTrialData;
    }
    
    public void addMove(final Move move) {
        this.moves.add(move);
    }
    
    public Status status() {
        return this.status;
    }
    
    public void setStatus(final Status res) {
        this.status = res;
    }
    
    public Moves cachedLegalMoves() {
        if (this.over()) {
            return new BaseMoves(null);
        }
        return this.legalMoves;
    }
    
    public void setLegalMoves(final Moves legalMoves, final Context context) {
        for (int i = 0; i < legalMoves.moves().size(); ++i) {
            final Move m = legalMoves.moves().get(i);
            if (!Game.satisfiesStateComparison(context, m)) {
                legalMoves.moves().removeSwap(i--);
            }
        }
        this.legalMoves = legalMoves;
        if (this.auxilTrialData != null) {
            this.auxilTrialData.updateNewLegalMoves(legalMoves, context);
        }
    }
    
    public boolean over() {
        return this.status != null;
    }
    
    public int numInitialPlacementMoves() {
        return this.numInitialPlacementMoves;
    }
    
    public void setNumInitialPlacementMoves(final int numInitialPlacementMoves) {
        this.numInitialPlacementMoves = numInitialPlacementMoves;
    }
    
    public void clearLegalMoves() {
        this.legalMoves.moves().clear();
    }
    
    public void reset(final Game game) {
        this.moves.clear();
        this.numInitialPlacementMoves = 0;
        if (this.startingPos != null) {
            this.startingPos.clear();
        }
        this.status = null;
        if (this.legalMoves != null) {
            this.clearLegalMoves();
        }
        if (this.previousStates != null) {
            this.previousStates.clear();
        }
        if (this.previousStatesWithinATurn != null) {
            this.previousStatesWithinATurn.clear();
        }
        this.numSubmovesPlayed = 0;
        if (this.auxilTrialData != null) {
            this.auxilTrialData.clear();
        }
        Arrays.fill(this.ranking, 0.0);
    }
    
    public Move lastMove() {
        final int size = this.moves.size();
        return (size == 0) ? null : this.moves.get(size - 1);
    }
    
    public Move lastMove(final int pid) {
        for (int i = this.moves.size() - 1; i >= 0; --i) {
            final Move m = this.moves.get(i);
            if (m.mover() == pid) {
                return m;
            }
        }
        return null;
    }
    
    public int numMoves() {
        return this.moves.size();
    }
    
    public int numInitPlacement() {
        return this.numInitialPlacementMoves;
    }
    
    public void addInitPlacement() {
        ++this.numInitialPlacementMoves;
    }
    
    public double[] ranking() {
        return this.ranking;
    }
    
    public void saveState(final State state) {
        if (this.auxilTrialData != null) {
            this.auxilTrialData.saveState(state);
        }
    }
    
    public void storeStates() {
        if (this.auxilTrialData == null) {
            this.auxilTrialData = new AuxilTrialData();
        }
        this.auxilTrialData.storeStates();
    }
    
    public void storeLegalMovesHistory() {
        if (this.auxilTrialData == null) {
            this.auxilTrialData = new AuxilTrialData();
        }
        this.auxilTrialData.storeLegalMovesHistory();
    }
    
    public void storeLegalMovesHistorySizes() {
        if (this.auxilTrialData == null) {
            this.auxilTrialData = new AuxilTrialData();
        }
        this.auxilTrialData.storeLegalMovesHistorySizes();
    }
    
    public void setLegalMovesHistory(final List<List<Move>> legalMovesHistory) {
        if (this.auxilTrialData == null) {
            this.auxilTrialData = new AuxilTrialData();
        }
        this.auxilTrialData.setLegalMovesHistory(legalMovesHistory);
    }
    
    public void setLegalMovesHistorySizes(final TIntArrayList legalMovesHistorySizes) {
        if (this.auxilTrialData == null) {
            this.auxilTrialData = new AuxilTrialData();
        }
        this.auxilTrialData.setLegalMovesHistorySizes(legalMovesHistorySizes);
    }
    
    public int numSubmovesPlayed() {
        return this.numSubmovesPlayed;
    }
    
    public void setNumSubmovesPlayed(final int numSubmovesPlayed) {
        this.numSubmovesPlayed = numSubmovesPlayed;
    }
    
    public int moveNumber() {
        return this.numMoves() - this.numInitPlacement();
    }
    
    public void saveTrialToFile(final File file, final String gameName, final RandomProviderDefaultState gameStartRngState) throws IOException {
        if (file != null) {
            file.getParentFile().mkdirs();
            if (!file.exists()) {
                file.createNewFile();
            }
            try (final ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file.getAbsoluteFile())))) {
                out.writeUTF(gameName);
                out.writeInt(gameStartRngState.getState().length);
                out.write(gameStartRngState.getState());
                out.writeObject(this);
                out.reset();
                out.flush();
                out.close();
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    
    public void saveTrialToTextFile(final File file, final String gameName, final List<String> gameOptions, final RandomProviderDefaultState gameStartRngState) throws IOException {
        this.saveTrialToTextFile(file, gameName, gameOptions, gameStartRngState, false);
    }
    
    public void saveTrialToTextFile(final File file, final String gameName, final List<String> gameOptions, final RandomProviderDefaultState gameStartRngState, final boolean trialContainsSandbox) throws IOException {
        if (file != null) {
            file.getParentFile().mkdirs();
            if (!file.exists()) {
                file.createNewFile();
            }
            try (final PrintWriter writer = new PrintWriter(file)) {
                writer.print(this.convertTrialToString(gameName, gameOptions, gameStartRngState));
                writer.println("SANDBOX=" + trialContainsSandbox);
                writer.println("LUDII_VERSION=1.0.8");
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    
    public String convertTrialToString(final String gameName, final List<String> gameOptions, final RandomProviderDefaultState gameStartRngState) throws IOException {
        final StringBuilder sb = new StringBuilder();
        sb.append("game=" + gameName + "\n");
        sb.append("START GAME OPTIONS\n");
        for (final String option : gameOptions) {
            sb.append(option + "\n");
        }
        sb.append("END GAME OPTIONS\n");
        sb.append("RNG internal state=");
        final byte[] bytes = gameStartRngState.getState();
        for (int i = 0; i < bytes.length; ++i) {
            sb.append(bytes[i]);
            if (i < bytes.length - 1) {
                sb.append(",");
            }
        }
        sb.append("\n");
        for (int i = 0; i < this.moves.size(); ++i) {
            sb.append("Move=" + this.moves.get(i).toTrialFormat(null) + "\n");
        }
        if (this.auxilTrialData != null) {
            if (this.auxilTrialData.storeLegalMovesHistorySizes) {
                for (int i = 0; i < this.auxilTrialData.legalMovesHistorySizes.size(); ++i) {
                    sb.append("LEGAL MOVES LIST SIZE = " + this.auxilTrialData.legalMovesHistorySizes.getQuick(i) + "\n");
                }
            }
            if (this.auxilTrialData.storeLegalMovesHistory) {
                for (final List<Move> legal : this.auxilTrialData.legalMovesHistory) {
                    sb.append("NEW LEGAL MOVES LIST\n");
                    for (int j = 0; j < legal.size(); ++j) {
                        sb.append(legal.get(j).toTrialFormat(null) + "\n");
                    }
                    sb.append("END LEGAL MOVES LIST\n");
                }
            }
        }
        if (this.status != null) {
            sb.append("winner=" + this.status.winner() + "\n");
        }
        if (this.ranking != null) {
            sb.append("rankings=");
            for (int i = 0; i < this.ranking.length; ++i) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(this.ranking[i]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public int numberOfTurns() {
        int currentPlayerNumber = -1;
        int numberTurns = 0;
        for (final Move m : this.moves) {
            if (m.mover() != currentPlayerNumber) {
                currentPlayerNumber = m.mover();
                ++numberTurns;
            }
        }
        return numberTurns;
    }
    
    public TLongArrayList previousState() {
        return this.previousStates;
    }
    
    public TLongArrayList previousStateWithinATurn() {
        return this.previousStatesWithinATurn;
    }
    
    public void setMoves(final List<Move> moves) {
        this.moves = moves;
    }
    
    public TIntArrayList startingPos(final int idComponent) {
        return this.startingPos.get(idComponent);
    }
    
    public List<TIntArrayList> startingPos() {
        return this.startingPos;
    }
}
