// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.game_logs;

import game.Game;
import gnu.trove.list.array.TIntArrayList;
import main.Status;
import org.apache.commons.rng.core.RandomProviderDefaultState;
import util.Context;
import util.Move;
import util.Trial;
import util.state.State;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MatchRecord implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final Trial trial;
    protected final RandomProviderDefaultState rngState;
    
    public MatchRecord(final Trial trial, final RandomProviderDefaultState rngState, final String loadedGameName) {
        this.trial = trial;
        this.rngState = rngState;
    }
    
    public Trial trial() {
        return this.trial;
    }
    
    public RandomProviderDefaultState rngState() {
        return this.rngState;
    }
    
    public static MatchRecord loadMatchRecordFromTextFile(final File file, final Game game) throws IOException {
        return loadMatchRecordFromInputStream(new InputStreamReader(new FileInputStream(file)), game);
    }
    
    public static MatchRecord loadMatchRecordFromInputStream(final InputStreamReader inputStreamReader, final Game game) throws IOException {
        try (final BufferedReader reader = new BufferedReader(inputStreamReader)) {
            final String gameNameLine = reader.readLine();
            final String loadedGameName = gameNameLine.substring("game=".length());
            String nextLine = reader.readLine();
            while (true) {
            Label_0308_Outer:
                while (nextLine != null) {
                    if (!nextLine.startsWith("RNG internal state=")) {
                        if (!nextLine.startsWith("NEW LEGAL MOVES LIST")) {
                            if (!nextLine.startsWith("winner=")) {
                                if (!nextLine.startsWith("rankings=")) {
                                    nextLine = reader.readLine();
                                    continue Label_0308_Outer;
                                }
                            }
                        }
                    }
                    if (!nextLine.startsWith("RNG internal state=")) {
                        System.err.println("ERROR: MatchRecord::loadMatchRecordFromTextFile expected to read RNG internal state!");
                        return null;
                    }
                    final String rngInternalStateLine = nextLine;
                    final String[] byteStrings = rngInternalStateLine.substring("RNG internal state=".length()).split(Pattern.quote(","));
                    final byte[] bytes = new byte[byteStrings.length];
                    for (int i = 0; i < byteStrings.length; ++i) {
                        bytes[i] = Byte.parseByte(byteStrings[i]);
                    }
                    final RandomProviderDefaultState rngState = new RandomProviderDefaultState(bytes);
                    final Trial trial = new Trial(game);
                    while (true) {
                        for (nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine()) {
                            if (!nextLine.startsWith("Move=")) {
                                final TIntArrayList legalMovesHistorySizes = new TIntArrayList();
                                while (true) {
                                    while (nextLine != null) {
                                        if (!nextLine.startsWith("NEW LEGAL MOVES LIST")) {
                                            if (!nextLine.startsWith("winner=")) {
                                                if (!nextLine.startsWith("rankings=")) {
                                                    if (nextLine.startsWith("LEGAL MOVES LIST SIZE = ")) {
                                                        legalMovesHistorySizes.add(Integer.parseInt(nextLine.substring("LEGAL MOVES LIST SIZE = ".length())));
                                                    }
                                                    nextLine = reader.readLine();
                                                    continue Label_0308_Outer;
                                                }
                                            }
                                        }
                                        final List<List<Move>> legalMovesHistory = new ArrayList<>();
                                        while (true) {
                                            while (nextLine != null) {
                                                if (!nextLine.startsWith("winner=")) {
                                                    if (!nextLine.startsWith("rankings=")) {
                                                        if (nextLine.equals("NEW LEGAL MOVES LIST")) {
                                                            legalMovesHistory.add(new ArrayList<>());
                                                        }
                                                        else if (!nextLine.equals("END LEGAL MOVES LIST")) {
                                                            legalMovesHistory.get(legalMovesHistory.size() - 1).add(new Move(nextLine));
                                                        }
                                                        nextLine = reader.readLine();
                                                        continue Label_0308_Outer;
                                                    }
                                                }
                                                if (!legalMovesHistory.isEmpty()) {
                                                    trial.storeLegalMovesHistory();
                                                    trial.setLegalMovesHistory(legalMovesHistory);
                                                }
                                                if (!legalMovesHistorySizes.isEmpty()) {
                                                    trial.storeLegalMovesHistorySizes();
                                                    trial.setLegalMovesHistorySizes(legalMovesHistorySizes);
                                                }
                                                if (nextLine != null && nextLine.startsWith("winner=")) {
                                                    trial.setStatus(new Status(Integer.parseInt(nextLine.substring("winner=".length()))));
                                                    nextLine = reader.readLine();
                                                }
                                                if (nextLine != null && nextLine.startsWith("rankings=")) {
                                                    final String[] rankingStrings = nextLine.substring("rankings=".length()).split(Pattern.quote(","));
                                                    for (int j = 0; j < rankingStrings.length; ++j) {
                                                        trial.ranking()[j] = Double.parseDouble(rankingStrings[j]);
                                                    }
                                                }
                                                return new MatchRecord(trial, rngState, loadedGameName);
                                            }
                                            continue;
                                        }
                                    }
                                    continue;
                                }
                            }
                            final Move move = new Move(nextLine.substring("Move=".length()));
                            trial.moves().add(move);
                        }
                        continue;
                    }
                }
                continue;
            }
        }
    }
    
    public void testIntegrity(final Game game) {
        final Context context = new Context(game, new Trial(game));
        context.rng().restoreState(this.rngState);
        final List<State> stateHistory = this.trial.auxilTrialData().stateHistory();
        final List<Move> actionHistory = this.trial.moves();
        game.start(context);
        for (int currActionIndex = 0; currActionIndex < actionHistory.size(); ++currActionIndex) {
            final State currentState = context.state();
            if (stateHistory != null) {
                final State historicState = stateHistory.get(currActionIndex);
                if (!historicState.equals(currentState)) {
                    System.err.println("State " + currActionIndex + " in history not equal to state in re-played game!");
                    return;
                }
            }
            if (context.trial().over()) {
                System.err.println("Re-played game ended faster than game in history did!");
                return;
            }
            final Move actionToPlay = actionHistory.get(currActionIndex);
            if (!game.moves(context).moves().contains(actionToPlay)) {
                System.err.println("Action to play according to history is not legal!");
                return;
            }
            game.apply(context, actionToPlay);
        }
        if (stateHistory != null && !stateHistory.get(stateHistory.size() - 1).equals(context.state())) {
            System.err.println("Last state of history not equal to state in re-play!");
            return;
        }
        if (!this.trial.status().equals(context.trial().status())) {
            System.err.println("Final Status in history does not equal final Status in re-play!");
        }
    }
}
