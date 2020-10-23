// 
// Decompiled by Procyon v0.5.36
// 

package app.loading.aliases;

import app.display.dialogs.editor.EditorHelpData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AliasesData
{
    private static final String RESOURCE_PATH = "/help/Aliases.txt";
    private static AliasesData data;
    private final Map<String, List<String>> gameAliases;
    
    private AliasesData() {
        this.gameAliases = new HashMap<>();
    }
    
    public List<String> aliasesForGame(final String gamePath) {
        return this.gameAliases.get(gamePath);
    }
    
    public List<String> aliasesForGameName(final String gameNameInput) {
        for (final Map.Entry<String, List<String>> entry : this.gameAliases.entrySet()) {
            final String[] pathSplit = entry.getKey().split("/");
            String gameName = pathSplit[pathSplit.length - 1];
            gameName = gameName.substring(0, gameName.length() - 4);
            if (gameName.toLowerCase().equals(gameNameInput)) {
                return this.gameAliases.get(entry.getKey());
            }
        }
        return this.gameAliases.get(gameNameInput);
    }
    
    public static AliasesData loadData() {
        if (AliasesData.data == null) {
            AliasesData.data = new AliasesData();
            try (final InputStream resource = EditorHelpData.class.getResourceAsStream("/help/Aliases.txt")) {
                if (resource != null) {
                    try (final InputStreamReader isr = new InputStreamReader(resource);
                         final BufferedReader rdr = new BufferedReader(isr)) {
                        String currGame = null;
                        String line;
                        while ((line = rdr.readLine()) != null) {
                            if (line.startsWith("/lud/") && line.endsWith(".lud")) {
                                currGame = line;
                            }
                            else {
                                if (!AliasesData.data.gameAliases.containsKey(currGame)) {
                                    AliasesData.data.gameAliases.put(currGame, new ArrayList<>());
                                }
                                AliasesData.data.gameAliases.get(currGame).add(line);
                            }
                        }
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return AliasesData.data;
    }
    
    static {
        AliasesData.data = null;
    }
}
