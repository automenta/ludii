// 
// Decompiled by Procyon v0.5.36
// 

package manager.utils;

import game.types.board.SiteType;
import manager.referee.MoveUtil;
import util.Move;
import util.SettingsVC;
import util.locations.FullLocation;
import util.locations.Location;

import java.util.Timer;
import java.util.TimerTask;

public class AnimationUtil
{
    public static Timer animationTimer;
    public static final boolean SLOW_IN_SLOW_OUT = true;
    public static final int MOVE_PIECE_FRAMES = 30;
    public static final int FLASH_LENGTH = 10;
    public static final int ANIMATION_FRAME_LENGTH = 15;
    public static final long ANIMATION_WAIT_TIME = 435L;
    public static int drawingMovingPieceTime;
    public static Location animationFromLocation;
    public static Location animationToLocation;
    public static boolean fadeIn;
    
    public static void saveMoveAnimationDetails(final Move move) {
        if (MoveUtil.animatePieceMovement(move)) {
            try {
                AnimationUtil.animationFromLocation = move.getFromLocation();
                AnimationUtil.animationToLocation = move.getToLocation();
                SettingsVC.animatedLocation = AnimationUtil.animationFromLocation;
                AnimationUtil.drawingMovingPieceTime = 0;
                SettingsManager.nextFrameIsAnimated = true;
                (AnimationUtil.animationTimer = new Timer()).scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        ++AnimationUtil.drawingMovingPieceTime;
                    }
                }, 0L, 15L);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void resetAnimationValues() {
        AnimationUtil.drawingMovingPieceTime = 30;
        AnimationUtil.animationFromLocation = new FullLocation(-1, 0, SiteType.Cell);
        AnimationUtil.animationToLocation = new FullLocation(-1, 0, SiteType.Cell);
        AnimationUtil.fadeIn = false;
        SettingsVC.pieceBeingDragged = false;
        SettingsVC.thisFrameIsAnimated = false;
        AnimationUtil.animationTimer.cancel();
    }
    
    static {
        AnimationUtil.animationTimer = new Timer();
        AnimationUtil.drawingMovingPieceTime = 30;
        AnimationUtil.animationFromLocation = new FullLocation(-1, 0, SiteType.Cell);
        AnimationUtil.animationToLocation = new FullLocation(-1, 0, SiteType.Cell);
        AnimationUtil.fadeIn = false;
    }
}
