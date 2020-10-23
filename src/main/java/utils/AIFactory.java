// 
// Decompiled by Procyon v0.5.36
// 

package utils;

import game.Game;
import org.json.JSONObject;
import org.json.JSONTokener;
import policies.GreedyPolicy;
import policies.softmax.SoftmaxPolicy;
import search.flat.FlatMonteCarlo;
import search.mcts.MCTS;
import search.mcts.finalmoveselection.RobustChild;
import search.mcts.playout.RandomPlayout;
import search.mcts.selection.McGRAVE;
import search.mcts.selection.UCB1;
import search.mcts.selection.UCB1GRAVE;
import search.minimax.AlphaBetaSearch;
import util.AI;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class AIFactory
{
    private static Map<String, List<Class<?>>> thirdPartyAIClasses;
    
    private AIFactory() {
    }
    
    public static AI createAI(final String string) {
        if (string.equalsIgnoreCase("Random")) {
            return new RandomAI();
        }
        if (string.equalsIgnoreCase("Monte Carlo (flat)") || string.equalsIgnoreCase("Flat MC")) {
            return new FlatMonteCarlo();
        }
        if (string.equalsIgnoreCase("Alpha-Beta") || string.equalsIgnoreCase("AlphaBeta")) {
            return AlphaBetaSearch.createAlphaBeta();
        }
        if (string.equalsIgnoreCase("UCT") || string.equalsIgnoreCase("MCTS")) {
            return MCTS.createUCT();
        }
        if (string.equalsIgnoreCase("MC-GRAVE")) {
            final MCTS mcGRAVE = new MCTS(new McGRAVE(), new RandomPlayout(200), new RobustChild());
            mcGRAVE.setQInit(MCTS.QInit.INF);
            mcGRAVE.friendlyName = "MC-GRAVE";
            return mcGRAVE;
        }
        if (string.equalsIgnoreCase("UCB1-GRAVE")) {
            final MCTS ucb1GRAVE = new MCTS(new UCB1GRAVE(), new RandomPlayout(200), new RobustChild());
            ucb1GRAVE.friendlyName = "UCB1-GRAVE";
            return ucb1GRAVE;
        }
        if (string.equalsIgnoreCase("Biased MCTS")) {
            return MCTS.createBiasedMCTS(true);
        }
        if (string.equalsIgnoreCase("Biased MCTS (Uniform Playouts)") || string.equalsIgnoreCase("MCTS (Biased Selection)")) {
            return MCTS.createBiasedMCTS(false);
        }
        final URL aiURL = AIFactory.class.getResource(string);
        File aiFile = aiURL != null ? new File(aiURL.getFile()) : new File(string);
        String[] lines = new String[0];
        if (aiFile.exists()) {
            try (final BufferedReader reader = new BufferedReader(new FileReader(aiFile))) {
                final List<String> linesList = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    linesList.add(line);
                }
                lines = linesList.toArray(lines);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            lines = string.split(";");
        }
        final String firstLine = lines[0];
        if (firstLine.startsWith("algorithm=")) {
            final String algName = firstLine.substring("algorithm=".length());
            if (algName.equalsIgnoreCase("MCTS") || algName.equalsIgnoreCase("UCT")) {
                return MCTS.fromLines(lines);
            }
            if (algName.equalsIgnoreCase("AlphaBeta") || algName.equalsIgnoreCase("Alpha-Beta")) {
                return AlphaBetaSearch.fromLines(lines);
            }
            if (algName.equalsIgnoreCase("Softmax") || algName.equalsIgnoreCase("SoftmaxPolicy")) {
                return SoftmaxPolicy.fromLines(lines);
            }
            if (algName.equalsIgnoreCase("Greedy") || algName.equalsIgnoreCase("GreedyPolicy")) {
                return GreedyPolicy.fromLines(lines);
            }
            if (algName.equalsIgnoreCase("Random")) {
                return new RandomAI();
            }
            System.err.println("Unknown algorithm name: " + algName);
        }
        else {
            System.err.println("Expecting AI file to start with \"algorithm=\", but it starts with " + firstLine);
        }
        System.err.printf("Warning: cannot convert string \"%s\" to AI; defaulting to random.%n", string);
        return null;
    }
    
    public static AI fromJsonFile(final File file) {
        try (final InputStream inputStream = new FileInputStream(file)) {
            final JSONObject json = new JSONObject(new JSONTokener(inputStream));
            return fromJson(json);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.err.println("WARNING: Failed to construct AI from JSON file: " + file.getAbsolutePath());
            return null;
        }
    }
    
    public static AI fromJson(final JSONObject json) {
        final JSONObject aiObj = json.getJSONObject("AI");
        final String algName = aiObj.getString("algorithm");
        if (algName.equalsIgnoreCase("Ludii") || algName.equalsIgnoreCase("Ludii AI") || algName.equalsIgnoreCase("Default")) {
            return new LudiiAI();
        }
        if (algName.equalsIgnoreCase("Random")) {
            return new RandomAI();
        }
        if (algName.equalsIgnoreCase("Monte Carlo (flat)") || algName.equalsIgnoreCase("Flat MC")) {
            return new FlatMonteCarlo();
        }
        if (algName.equalsIgnoreCase("UCT")) {
            return MCTS.createUCT();
        }
        if (algName.equalsIgnoreCase("UCT (Uncapped)")) {
            final MCTS uct = new MCTS(new UCB1(Math.sqrt(2.0)), new RandomPlayout(), new RobustChild());
            uct.friendlyName = "UCT (Uncapped)";
            return uct;
        }
        if (algName.equalsIgnoreCase("MCTS")) {
            return MCTS.fromJson(aiObj);
        }
        if (algName.equalsIgnoreCase("MC-GRAVE")) {
            final MCTS mcGRAVE = new MCTS(new McGRAVE(), new RandomPlayout(200), new RobustChild());
            mcGRAVE.setQInit(MCTS.QInit.INF);
            mcGRAVE.friendlyName = "MC-GRAVE";
            return mcGRAVE;
        }
        if (algName.equalsIgnoreCase("UCB1-GRAVE")) {
            final MCTS ucb1GRAVE = new MCTS(new UCB1GRAVE(), new RandomPlayout(200), new RobustChild());
            ucb1GRAVE.friendlyName = "UCB1-GRAVE";
            return ucb1GRAVE;
        }
        if (algName.equalsIgnoreCase("Biased MCTS")) {
            return MCTS.createBiasedMCTS(true);
        }
        if (algName.equalsIgnoreCase("Biased MCTS (Uniform Playouts)") || algName.equalsIgnoreCase("MCTS (Biased Selection)")) {
            return MCTS.createBiasedMCTS(false);
        }
        if (algName.equalsIgnoreCase("Alpha-Beta") || algName.equalsIgnoreCase("AlphaBeta")) {
            return AlphaBetaSearch.createAlphaBeta();
        }
        if (algName.equalsIgnoreCase("From JAR")) {
            final File jarFile = new File(aiObj.getString("JAR File"));
            final String className = aiObj.getString("Class Name");
            try {
                for (final Class<?> clazz : loadThirdPartyAIClasses(jarFile)) {
                    if (clazz.getName().equals(className)) {
                        return (AI)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.err.println("WARNING: Failed to construct AI from JSON: " + json.toString(4));
        return null;
    }
    
    public static AI fromMetadata(final Game game) {
        String bestAgent;
        if (!game.isAlternatingMoveGame()) {
            bestAgent = "Flat MC";
        }
        else {
            bestAgent = "UCT";
        }
        if (game.metadata().ai().bestAgent() != null) {
            bestAgent = game.metadata().ai().bestAgent().agent();
        }
        final AI ai = createAI(bestAgent);
        return ai;
    }
    
    public static List<Class<?>> loadThirdPartyAIClasses(final File jarFile) {
        List<Class<?>> classes = null;
        try {
            if (AIFactory.thirdPartyAIClasses.containsKey(jarFile.getAbsolutePath())) {
                classes = AIFactory.thirdPartyAIClasses.get(jarFile.getAbsolutePath());
            }
            else {
                classes = new ArrayList<>();
                final URL[] urls = { new URL("jar:file:" + jarFile.getAbsolutePath() + "!/") };
                try (final URLClassLoader classLoader = URLClassLoader.newInstance(urls);
                     final JarFile jar = new JarFile(jarFile)) {
                    final Enumeration<? extends JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        final ZipEntry entry = entries.nextElement();
                        try {
                            if (!entry.getName().endsWith(".class")) {
                                continue;
                            }
                            final String className = entry.getName().replace(".class", "").replace("/", ".");
                            final Class<?> clazz = classLoader.loadClass(className);
                            if (!AI.class.isAssignableFrom(clazz)) {
                                continue;
                            }
                            classes.add(clazz);
                        }
                        catch (NoClassDefFoundError exception) {}
                    }
                }
                classes.sort(Comparator.comparing(Class::getName));
                AIFactory.thirdPartyAIClasses.put(jarFile.getAbsolutePath(), classes);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }
    
    static {
        AIFactory.thirdPartyAIClasses = new HashMap<>();
    }
}
