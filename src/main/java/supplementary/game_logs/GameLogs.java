// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.game_logs;

import game.Game;
import game.equipment.container.Container;
import org.apache.commons.rng.core.RandomProviderDefaultState;
import util.Trial;
import util.state.State;
import util.state.containerState.ContainerState;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameLogs
{
    protected final String gameName;
    protected final Game game;
    protected final List<MatchRecord> matchRecords;
    
    public GameLogs(final Game game) {
        this.matchRecords = new ArrayList<>();
        this.gameName = game.name();
        this.game = game;
    }
    
    public void addMatchRecord(final MatchRecord matchRecord) {
        this.matchRecords.add(matchRecord);
    }
    
    public List<MatchRecord> matchRecords() {
        return this.matchRecords;
    }
    
    public Game game() {
        return this.game;
    }
    
    public void testIntegrity() {
        for (final MatchRecord matchRecord : this.matchRecords) {
            matchRecord.testIntegrity(this.game);
        }
    }
    
    public static GameLogs fromFile(final File file, final Game game) {
        GameLogs gameLogs = null;
        try (final ObjectInputStream reader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            final String gameName = reader.readUTF();
            gameLogs = new GameLogs(game);
            while (reader.available() > 0) {
                final int numRngStateBytes = reader.readInt();
                final byte[] rngStateBytes = new byte[numRngStateBytes];
                final int numBytesRead = reader.read(rngStateBytes);
                if (numBytesRead != numRngStateBytes) {
                    System.err.println("Warning: GameLogs.fromFile() expected " + numRngStateBytes + " bytes, but only read " + numBytesRead + " bytes!");
                }
                final RandomProviderDefaultState rngState = new RandomProviderDefaultState(rngStateBytes);
                final Trial trial = (Trial)reader.readObject();
                final List<State> states = trial.auxilTrialData().stateHistory();
                if (states != null) {
                    for (final State state : states) {
                        final ContainerState[] containerStates;
                        final ContainerState[] itemStates = containerStates = state.containerStates();
                        for (final ContainerState itemState : containerStates) {
                            if (itemState != null) {
                                final String containerName = itemState.nameFromFile();
                                for (final Container container : game.equipment().containers()) {
                                    if (container != null && container.name().equals(containerName)) {
                                        itemState.setContainer(container);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                gameLogs.addMatchRecord(new MatchRecord(trial, rngState, gameName));
            }
        }
        catch (IOException | ClassNotFoundException ex2) {
            ex2.printStackTrace();
        }
        return gameLogs;
    }
}
