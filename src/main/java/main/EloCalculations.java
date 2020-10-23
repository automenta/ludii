// 
// Decompiled by Procyon v0.5.36
// 

package main;

import java.util.ArrayList;

public class EloCalculations
{
    static float Probability(final float rating1, final float rating2) {
        return 1.0f / (1.0f + 1.0f * (float)Math.pow(10.0, 1.0f * (rating1 - rating2) / 400.0f));
    }
    
    static double[] EloRating(final float Ra, final float Rb, final int K, final boolean d) {
        float RaNew = Ra;
        float RbNew = Rb;
        final float Pb = Probability(Ra, Rb);
        final float Pa = Probability(Rb, Ra);
        if (d) {
            RaNew = Ra + K * (1.0f - Pa);
            RbNew = Rb + K * (0.0f - Pb);
        }
        else {
            RaNew = Ra + K * (0.0f - Pa);
            RbNew = Rb + K * (1.0f - Pb);
        }
        final double[] eloUpdated = { Math.round(RaNew * 1000000.0) / 1000000.0, Math.round(RbNew * 1000000.0) / 1000000.0 };
        return eloUpdated;
    }
    
    public static void calculateElo(final double[] rankings) {
        final int K = 30;
        final ArrayList<Float> newEloRankings = new ArrayList<>();
        for (int playerId = 0; playerId < rankings.length; ++playerId) {
            newEloRankings.add(1000.0f);
        }
        for (int playerNumberA = 0; playerNumberA < rankings.length; ++playerNumberA) {
            final int playerAELORanking = Math.round(newEloRankings.get(playerNumberA));
            double sum = 0.0;
            for (int playerNumberB = 0; playerNumberB < rankings.length; ++playerNumberB) {
                if (playerNumberA != playerNumberB) {
                    if (rankings[playerNumberA] != rankings[playerNumberB]) {
                        final int player2ELORanking = Math.round(newEloRankings.get(playerNumberB));
                        boolean player1Won = false;
                        if (rankings[playerNumberA] > rankings[playerNumberB]) {
                            player1Won = true;
                        }
                        sum += EloRating((float)playerAELORanking, (float)player2ELORanking, 30, player1Won)[0] - playerAELORanking;
                    }
                }
            }
            final double delta = sum / (rankings.length - 1);
            final double player1EloRatingNew = playerAELORanking + delta;
            System.out.println("Player " + playerNumberA + " new ELO ranking: " + player1EloRatingNew);
        }
    }
}
