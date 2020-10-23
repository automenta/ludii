// 
// Decompiled by Procyon v0.5.36
// 

package app.loading;

import app.DesktopApp;
import app.display.MainWindow;
import app.display.SVGWindow;
import app.display.util.SVGUtil;
import app.game.GameRestart;
import app.menu.MainMenu;
import graphics.svg.SVGtoImage;
import manager.Manager;
import manager.ai.AIMenuName;
import manager.ai.AIUtil;
import manager.referee.Referee;
import manager.utils.ContextSnapshot;
import manager.utils.SettingsManager;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import supplementary.game_logs.MatchRecord;
import tournament.Tournament;
import util.Context;
import util.Move;
import util.SettingsColour;
import util.SettingsVC;
import util.locations.FullLocation;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MiscLoading
{
    public static void loadSVG(final MainWindow view) {
        JFrame svgFrame = null;
        SVGWindow svgView = null;
        final String fileName = FileLoading.selectFile(DesktopApp.frame(), true, "/../Common/img/svg/", "SVG files (*.svg)", view, "svg");
        if (fileName == null) {
            return;
        }
        svgView = new SVGWindow();
        svgFrame = new JFrame("SVG Viewer");
        svgFrame.add(svgView);
        final int sz = Math.min(DesktopApp.frame().getWidth() / 2, DesktopApp.frame().getHeight() - 40) - 20;
        svgFrame.setSize((sz + 20) * 2, sz + 60);
        svgFrame.setLocationRelativeTo(DesktopApp.frame());
        final SVGGraphics2D image1 = renderImageSVGExternal(sz, fileName, ContextSnapshot.getContext(), 1);
        final SVGGraphics2D image2 = renderImageSVGExternal(sz, fileName, ContextSnapshot.getContext(), 2);
        final BufferedImage img1 = SVGUtil.createSVGImage(image1.getSVGDocument(), sz, sz);
        final BufferedImage img2 = SVGUtil.createSVGImage(image2.getSVGDocument(), sz, sz);
        svgView.setImages(img1, img2);
        svgFrame.setVisible(true);
        svgView.repaint();
    }
    
    public static SVGGraphics2D renderImageSVGInternal(final int pixels, final String filePath1, final Context context, final int playerNumber) {
        final Color playerColour = SettingsColour.playerColour(playerNumber, context);
        final Color edgeColour1 = Color.BLACK;
        final InputStream in = DesktopApp.playerApp().getClass().getResourceAsStream(filePath1);
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            final SVGGraphics2D g2d = new SVGGraphics2D(pixels, pixels);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            SVGtoImage.loadFromReader(g2d, reader, pixels, edgeColour1, playerColour, true);
            return g2d;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static SVGGraphics2D renderImageSVGExternal(final int pixels, final String filePath1, final Context context, final int playerNumber) {
        final Color playerColour = SettingsColour.playerColour(playerNumber, context);
        final Color edgeColour1 = Color.BLACK;
        try (final BufferedReader reader = new BufferedReader(new FileReader(filePath1))) {
            final SVGGraphics2D g2d = new SVGGraphics2D(pixels, pixels);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            SVGtoImage.loadFromReader(g2d, reader, pixels, edgeColour1, playerColour, true);
            return g2d;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void loadDemo(final JSONObject jsonDemo) {
        SettingsManager.agentsPaused = true;
        final Referee ref = Manager.ref();
        final String gameName = jsonDemo.getString("Game");
        final List<String> gameOptions = new ArrayList<>();
        final JSONArray optionsArray = jsonDemo.optJSONArray("Options");
        if (optionsArray != null) {
            for (final Object object : optionsArray) {
                gameOptions.add((String)object);
            }
        }
        GameLoading.loadGameFromName(gameName, gameOptions, false);
        for (int p = 1; p <= ref.context().game().players().count(); ++p) {
            final JSONObject jsonPlayer = jsonDemo.optJSONObject("Player " + p);
            if (jsonPlayer != null) {
                if (jsonPlayer.has("AI")) {
                    AIUtil.updateSelectedAI(jsonPlayer, p, AIMenuName.getAIMenuName(jsonPlayer.getJSONObject("AI").getString("algorithm")));
                }
                if (jsonPlayer.has("Time Limit")) {
                    Manager.aiSelected[p].setThinkTime(jsonPlayer.getDouble("Time Limit"));
                }
            }
        }
        final JSONObject jsonSettings = jsonDemo.optJSONObject("Settings");
        if (jsonSettings != null && jsonSettings.has("Show AI Distribution")) {
            SettingsManager.showAIDistribution = jsonSettings.getBoolean("Show AI Distribution");
        }
        DesktopApp.frame().setJMenuBar(new MainMenu());
        if (jsonDemo.has("Trial")) {
            final String trialFile = jsonDemo.getString("Trial").replaceAll(Pattern.quote("\\"), "/");
            try {
                final MatchRecord loadedRecord = MatchRecord.loadMatchRecordFromInputStream(new InputStreamReader(MainMenu.class.getResourceAsStream(trialFile)), ref.context().game());
                DesktopApp.setSavedTrial(loadedRecord.trial());
                SettingsManager.canSendToDatabase = false;
                final List<Move> tempActions = new ArrayList<>(Manager.savedTrial().moves());
                Manager.setCurrGameStartRngState(loadedRecord.rngState());
                GameRestart.clearBoard();
                for (int i = ref.context().trial().moves().size(); i < tempActions.size(); ++i) {
                    ref.makeSavedMove(tempActions.get(i));
                }
                DesktopApp.setSavedTrial(null);
                SettingsVC.selectedLocation = new FullLocation(-1);
                SettingsManager.canSendToDatabase = false;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void loadTournamentFile() {
        GameRestart.restartGame(false);
        final int fcReturnVal = DesktopApp.loadTournamentFileChooser().showOpenDialog(DesktopApp.frame());
        if (fcReturnVal == 0) {
            final File file = DesktopApp.loadTournamentFileChooser().getSelectedFile();
            try (final InputStream inputStream = new FileInputStream(file)) {
                final JSONObject json = new JSONObject(new JSONTokener(inputStream));
                DesktopApp.setTournament(new Tournament(json));
                DesktopApp.tournament().setupTournament();
                DesktopApp.tournament().startNextTournamentGame();
            }
            catch (Exception e1) {
                System.out.println("Tournament file is not formatted correctly");
            }
        }
    }
}
