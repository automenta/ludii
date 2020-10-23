package root;/*
 * Decompiled with CFR 0.150.
 */

import java.util.ArrayList;

public class EloCalculations {
    static float Probability(float rating1, float rating2) {
        return 1.0f / (1.0f + 1.0f * (float)Math.pow(10.0, 1.0f * (rating1 - rating2) / 400.0f));
    }

    static double[] EloRating(float Ra, float Rb, int K, boolean d) {
        float RaNew = Ra;
        float RbNew = Rb;
        float Pb = EloCalculations.Probability(Ra, Rb);
        float Pa = EloCalculations.Probability(Rb, Ra);
        if (d) {
            RaNew = Ra + (float)K * (1.0f - Pa);
            RbNew = Rb + (float)K * (0.0f - Pb);
        } else {
            RaNew = Ra + (float)K * (0.0f - Pa);
            RbNew = Rb + (float)K * (1.0f - Pb);
        }
        double[] eloUpdated = new double[]{(double)Math.round((double)RaNew * 1000000.0) / 1000000.0, (double)Math.round((double)RbNew * 1000000.0) / 1000000.0};
        return eloUpdated;
    }

    public static void calculateElo(double[] rankings) {
        int K = 30;
        ArrayList<Float> newEloRankings = new ArrayList<>();
        for (int playerId = 0; playerId < rankings.length; ++playerId) {
            newEloRankings.add(Float.valueOf(1000.0f));
        }
        for (int playerNumberA = 0; playerNumberA < rankings.length; ++playerNumberA) {
            int playerAELORanking = Math.round(newEloRankings.get(playerNumberA).floatValue());
            double sum = 0.0;
            for (int playerNumberB = 0; playerNumberB < rankings.length; ++playerNumberB) {
                if (playerNumberA == playerNumberB || rankings[playerNumberA] == rankings[playerNumberB]) continue;
                int player2ELORanking = Math.round(newEloRankings.get(playerNumberB).floatValue());
                boolean player1Won = false;
                if (rankings[playerNumberA] > rankings[playerNumberB]) {
                    player1Won = true;
                }
                sum += EloCalculations.EloRating(playerAELORanking, player2ELORanking, 30, player1Won)[0] - (double)playerAELORanking;
            }
            double delta = sum / (double)(rankings.length - 1);
            double player1EloRatingNew = (double)playerAELORanking + delta;
            System.out.println("Player " + playerNumberA + " new ELO ranking: " + player1EloRatingNew);
        }
    }
}

