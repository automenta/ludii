// 
// Decompiled by Procyon v0.5.36
// 

package distance;

import distance.zhang_shasha.Tree;
import game.Game;

import java.io.IOException;
import java.util.List;

public class ZhangShasha implements DistanceMetric
{
    @Override
    public Score distance(final Game gameA, final Game gameB) {
        final int numTokensA = gameA.description().tokenForest().tokenTree().count();
        final int numTokensB = gameB.description().tokenForest().tokenTree().count();
        final int maxTokens = Math.max(numTokensA, numTokensB);
        String strA = gameA.description().tokenForest().tokenTree().formatZhangShasha("", 0, false, true);
        strA = format(strA);
        String strB = gameB.description().tokenForest().tokenTree().formatZhangShasha("", 0, false, true);
        strB = format(strB);
        if (strA.equals(strB)) {
            return new Score(0.0);
        }
        Tree treeA = null;
        Tree treeB = null;
        try {
            treeA = new Tree(strA);
            treeB = new Tree(strB);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        final int edits = Tree.ZhangShasha(treeA, treeB);
        final double score = Math.min(1.0, edits / (double)maxTokens);
        return new Score(score);
    }
    
    @Override
    public Score distance(final Game gameA, final List<Game> gameB, final int numberTrials, final int maxTurns, final double thinkTime, final String AIName) {
        return this.distance(gameA, gameB.get(0));
    }
    
    private static String format(final String strIn) {
        String str;
        for (str = strIn, str = str.replaceAll("==\\(", "equals\\("), str = str.replaceAll("!=\\(", "notEquals\\("), str = str.replaceAll("<=\\(", "le\\("), str = str.replaceAll(">=\\(", "ge\\("), str = str.replaceAll("<\\(", "lt\\("), str = str.replaceAll(">\\(", "gt\\("), str = str.replaceAll("=\\(", "setEquals\\("), str = str.replaceAll(":", ""); str.indexOf("( ") >= 0; str = str.replaceAll("\\( ", "\\(")) {}
        while (str.indexOf(" )") >= 0) {
            str = str.replaceAll(" \\)", "\\)");
        }
        while (str.indexOf("()") >= 0) {
            str = str.replaceAll("\\(\\)", "");
        }
        return str;
    }
}
