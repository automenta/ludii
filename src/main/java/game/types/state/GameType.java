// 
// Decompiled by Procyon v0.5.36
// 

package game.types.state;

import game.Game;

import java.io.Serializable;

public interface GameType extends Serializable
{
    long UsesFromPositions = 1L;
    long SiteState = 2L;
    long Count = 4L;
    long HiddenInfo = 8L;
    long Stacking = 16L;
    long Boardless = 32L;
    long Stochastic = 64L;
    long DeductionPuzzle = 128L;
    long Score = 256L;
    long Visited = 512L;
    long Simultaneous = 1024L;
    long ThreeDimensions = 2048L;
    long NotAllPass = 4096L;
    long Card = 8192L;
    long LargePiece = 16384L;
    long SequenceCapture = 32768L;
    long Track = 65536L;
    long Rotation = 131072L;
    long Team = 262144L;
    long Bet = 524288L;
    long HashScores = 1048576L;
    long HashAmounts = 2097152L;
    long HashPhases = 4194304L;
    long Graph = 8388608L;
    long Vertex = 16777216L;
    long Cell = 33554432L;
    long Edge = 67108864L;
    long Dominoes = 134217728L;
    long LineOfPlay = 268435456L;
    long MoveAgain = 536870912L;
    long NotMarkovGame = 1073741824L;
    long Vote = 2147483648L;
    long Note = 4294967296L;
    long Loops = 8589934592L;
    long StepAdjacentDistance = 17179869184L;
    long StepOrthogonalDistance = 34359738368L;
    long StepDiagonalDistance = 68719476736L;
    long StepOffDistance = 137438953472L;
    long StepAllDistance = 274877906944L;
    long InternalLoopInTrack = 549755813888L;
    long UsesSwapRule = 1099511627776L;
    long RepeatInGame = 2199023255552L;
    long RepeatInTurn = 4398046511104L;
    
    long gameFlags(final Game game);
    
    boolean isStatic();
    
    void preprocess(final Game game);
}
