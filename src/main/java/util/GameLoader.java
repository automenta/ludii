// 
// Decompiled by Procyon v0.5.36
// 

package util;

import game.Game;
import game.match.Subgame;
import grammar.Description;
import grammar.Report;
import language.compiler.Compiler;
import main.FileHandling;
import options.GameOptions;
import options.Option;
import options.UserSelections;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class GameLoader
{
    public static Game loadGameFromName(final String name) {
        return loadGameFromName(name, new ArrayList<>());
    }
    
    public static Game loadGameFromName(final String name, final List<String> options) {
        InputStream in = GameLoader.class.getResourceAsStream(name.startsWith("/lud/") ? name : ("/lud/" + name));
        if (in == null) {
            final String[] allGameNames = FileHandling.listGames();
            int shortestNonMatchLength = Integer.MAX_VALUE;
            String bestMatchFilepath = null;
            final String givenName = name.toLowerCase().replaceAll(Pattern.quote("\\"), "/");
            for (final String gameName : allGameNames) {
                final String str = gameName.toLowerCase().replaceAll(Pattern.quote("\\"), "/");
                if (str.endsWith(givenName)) {
                    final int nonMatchLength = str.length() - givenName.length();
                    final String[] strSplit = str.split(Pattern.quote("/"));
                    if (strSplit[strSplit.length - 1].equals(givenName)) {
                        bestMatchFilepath = "..\\Common\\res\\" + gameName;
                        break;
                    }
                    if (nonMatchLength < shortestNonMatchLength) {
                        shortestNonMatchLength = nonMatchLength;
                        bestMatchFilepath = "..\\Common\\res\\" + gameName;
                    }
                }
            }
            String resourceStr = bestMatchFilepath.replaceAll(Pattern.quote("\\"), "/");
            resourceStr = resourceStr.substring(resourceStr.indexOf("/lud/"));
            in = GameLoader.class.getResourceAsStream(resourceStr);
        }
        final StringBuilder sb = new StringBuilder();
        try (final BufferedReader rdr = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = rdr.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        final Game game = Compiler.compile(new Description(sb.toString()), new UserSelections(options), new Report(), false);
        if (game.hasSubgames()) {
            for (final Subgame instance : game.instances()) {
                final ArrayList<String> option = new ArrayList<>();
                if (instance.optionName() != null) {
                    option.add(instance.optionName());
                }
                instance.setGame(loadGameFromName(instance.gameName() + ".lud", option));
            }
        }
        try {
            in.close();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        return game;
    }
    
    public static Game loadGameFromFile(final File file) {
        return loadGameFromFile(file, new ArrayList<>());
    }
    
    public static Game loadGameFromFile(final File file, final List<String> options) {
        final StringBuilder sb = new StringBuilder();
        if (file != null) {
            try (final BufferedReader rdr = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                String line;
                while ((line = rdr.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        final Game game = Compiler.compile(new Description(sb.toString()), new UserSelections(options), new Report(), false);
        if (game.hasSubgames()) {
            for (final Subgame instance : game.instances()) {
                final ArrayList<String> option = new ArrayList<>();
                if (instance.optionName() != null) {
                    option.add(instance.optionName());
                }
                instance.setGame(loadGameFromName(instance.gameName() + ".lud", option));
            }
        }
        return game;
    }
    
    public static int[] convertStringsToOptions(final List<String> optionStrings, final GameOptions gameOptions) {
        final int[] optionSelections = new int[10];
        for (final String optionStr : optionStrings) {
            final String[] headings = optionStr.split(Pattern.quote("/"));
            boolean foundMatch = false;
            for (int cat = 0; cat < gameOptions.numCategories(); ++cat) {
                final List<Option> optionsList = gameOptions.categories().get(cat).options();
                for (int i = 0; i < optionsList.size(); ++i) {
                    final Option option = optionsList.get(i);
                    final List<String> optionHeadings = option.menuHeadings();
                    if (optionHeadings.size() == headings.length) {
                        boolean allMatch = true;
                        for (int j = 0; j < headings.length; ++j) {
                            if (!headings[j].equalsIgnoreCase(optionHeadings.get(j))) {
                                allMatch = false;
                                break;
                            }
                        }
                        if (allMatch) {
                            foundMatch = true;
                            optionSelections[cat] = i;
                            break;
                        }
                    }
                }
                if (foundMatch) {
                    break;
                }
            }
            if (!foundMatch) {
                System.err.println("Warning! GameLoader::convertStringToOptions() could not resolve option: " + optionStr);
            }
        }
        return optionSelections;
    }
    
    public static void compileInstance(final Subgame instance) {
        final ArrayList<String> option = new ArrayList<>();
        if (instance.optionName() != null) {
            option.add(instance.optionName());
        }
        instance.setGame(loadGameFromName(instance.gameName() + ".lud", option));
        if (instance.optionName() != null) {
            final GameOptions instanceObjectOptions = new GameOptions();
            final Option optionInstance = new Option();
            final List<String> headings = new ArrayList<>();
            headings.add(instance.optionName());
            optionInstance.setHeadings(headings);
            final List<Option>[] optionsAvailable = (List<Option>[])new ArrayList[] { null };
            final ArrayList<Option> optionList = new ArrayList<>();
            optionList.add(optionInstance);
            optionsAvailable[0] = optionList;
            instanceObjectOptions.setOptionCategories(optionsAvailable);
        }
    }
}
