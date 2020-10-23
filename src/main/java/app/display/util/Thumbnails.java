// 
// Decompiled by Procyon v0.5.36
// 

package app.display.util;

import app.DesktopApp;
import bridge.Bridge;
import game.equipment.container.board.Board;
import manager.Manager;
import util.Context;
import util.PlaneType;
import util.Trial;
import view.container.ContainerStyle;
import view.container.styles.board.BoardlessStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class Thumbnails
{
    public static void generateThumbnails() {
        final int imageSize = DesktopApp.view().boardSize();
        BufferedImage image = new BufferedImage(imageSize, imageSize, 2);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        final Board board = Manager.ref().context().board();
        final Trial trial = new Trial(Manager.ref().context().game());
        final Context context = new Context(Manager.ref().context().game(), trial);
        Manager.ref().context().game().start(context);
        boolean boardEmptyAtStart = true;
        for (int i = 0; i < Manager.ref().context().board().topology().cells().size(); ++i) {
            if (context.containerState(0).whatCell(i) != 0) {
                boardEmptyAtStart = false;
            }
        }
        for (int i = 0; i < Manager.ref().context().board().topology().vertices().size(); ++i) {
            if (context.containerState(0).whatVertex(i) != 0) {
                boardEmptyAtStart = false;
            }
        }
        for (int i = 0; i < Manager.ref().context().board().topology().edges().size(); ++i) {
            if (context.containerState(0).whatEdge(i) != 0) {
                boardEmptyAtStart = false;
            }
        }
        final ContainerStyle boardStyle = Bridge.getContainerStyle(board.index());
        if (!Manager.ref().context().game().metadata().graphics().boardHidden()) {
            boardStyle.render(PlaneType.BOARD, context);
            final String svg = boardStyle.containerSVGImage();
            final BufferedImage img = SVGUtil.createSVGImage(svg, imageSize, imageSize);
            if (!(boardStyle instanceof BoardlessStyle)) {
                g2d.drawImage(img, 0, 0, imageSize, imageSize, 0, 0, img.getWidth(), img.getHeight(), null);
            }
        }
        Bridge.getContainerStyle(context.board().index()).draw(g2d, PlaneType.COMPONENTS, context);
        Bridge.getContainerStyle(context.board().index()).draw(g2d, PlaneType.HINTS, context);
        Bridge.getContainerStyle(context.board().index()).draw(g2d, PlaneType.COSTS, context);
        image = new BufferedImage(imageSize, imageSize, 2);
        g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (!Manager.ref().context().game().metadata().graphics().boardHidden()) {
            boardStyle.render(PlaneType.BOARD, context);
            final String svg = boardStyle.containerSVGImage();
            final BufferedImage img = SVGUtil.createSVGImage(svg, imageSize, imageSize);
            if (!(boardStyle instanceof BoardlessStyle)) {
                g2d.drawImage(img, 0, 0, imageSize, imageSize, 0, 0, img.getWidth(), img.getHeight(), null);
            }
        }
        Bridge.getContainerStyle(context.board().index()).draw(g2d, PlaneType.COMPONENTS, context);
        Bridge.getContainerStyle(context.board().index()).draw(g2d, PlaneType.HINTS, context);
        Bridge.getContainerStyle(context.board().index()).draw(g2d, PlaneType.COSTS, context);
        try {
            final File outputfile = new File("./thumb-" + Manager.ref().context().game().name() + "-a.png");
            ImageIO.write(image, "png", outputfile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        BufferedImage image2;
        if (boardEmptyAtStart) {
            image2 = generateEndPosition(imageSize, true);
        }
        else {
            image2 = generateEndPosition(imageSize, false);
        }
        if (boardEmptyAtStart) {
            try {
                final File outputfileBig = new File("./thumb-" + Manager.ref().context().game().name() + "-d.png");
                ImageIO.write(image2, "png", outputfileBig);
                final File outputfile2 = new File("./thumb-" + Manager.ref().context().game().name() + "-c.png");
                ImageIO.write(BufferedImageUtil.resize(image2, 100, 100), "png", outputfile2);
                final File outputfileSmall = new File("./thumb-" + Manager.ref().context().game().name() + "-e.png");
                ImageIO.write(BufferedImageUtil.resize(image2, 30, 30), "png", outputfileSmall);
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        else {
            try {
                final File outputfileBig = new File("./thumb-" + Manager.ref().context().game().name() + "-d.png");
                ImageIO.write(image, "png", outputfileBig);
                final File outputfile2 = new File("./thumb-" + Manager.ref().context().game().name() + "-c.png");
                ImageIO.write(BufferedImageUtil.resize(image, 100, 100), "png", outputfile2);
                final File outputfileSmall = new File("./thumb-" + Manager.ref().context().game().name() + "-e.png");
                ImageIO.write(BufferedImageUtil.resize(image, 30, 30), "png", outputfileSmall);
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        GraphicsCache.clearAllCachedImages();
        Manager.app.repaint();
    }
    
    private static BufferedImage generateEndPosition(final int imageSize, final boolean notEmpty) {
        BufferedImage image2 = new BufferedImage(imageSize, imageSize, 2);
        Graphics2D g2d2 = image2.createGraphics();
        g2d2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        final Board board2 = Manager.ref().context().board();
        final int moveLimit = 200;
        boolean boardEmptyAtEnd = true;
        Trial trial2 = new Trial(Manager.ref().context().game());
        Context context2 = new Context(Manager.ref().context().game(), trial2);
        Manager.ref().context().game().start(context2);
        Manager.ref().context().game().playout(context2, null, 1.0, null, null, 0, 200, -1.0f, ThreadLocalRandom.current());
        for (int i = 0; i < Manager.ref().context().board().topology().cells().size(); ++i) {
            if (context2.containerState(0).whatCell(i) != 0) {
                boardEmptyAtEnd = false;
            }
        }
        for (int i = 0; i < Manager.ref().context().board().topology().vertices().size(); ++i) {
            if (context2.containerState(0).whatVertex(i) != 0) {
                boardEmptyAtEnd = false;
            }
        }
        for (int i = 0; i < Manager.ref().context().board().topology().edges().size(); ++i) {
            if (context2.containerState(0).whatEdge(i) != 0) {
                boardEmptyAtEnd = false;
            }
        }
        if (notEmpty) {
            for (int counter = 0; boardEmptyAtEnd && counter < 50; ++counter) {
                trial2 = new Trial(Manager.ref().context().game());
                context2 = new Context(Manager.ref().context().game(), trial2);
                Manager.ref().context().game().start(context2);
                Manager.ref().context().game().playout(context2, null, 1.0, null, null, 0, 200, -1.0f, ThreadLocalRandom.current());
                for (int j = 0; j < Manager.ref().context().board().topology().cells().size(); ++j) {
                    if (context2.containerState(0).whatCell(j) != 0) {
                        boardEmptyAtEnd = false;
                    }
                }
                for (int j = 0; j < Manager.ref().context().board().topology().vertices().size(); ++j) {
                    if (context2.containerState(0).whatVertex(j) != 0) {
                        boardEmptyAtEnd = false;
                    }
                }
                for (int j = 0; j < Manager.ref().context().board().topology().edges().size(); ++j) {
                    if (context2.containerState(0).whatEdge(j) != 0) {
                        boardEmptyAtEnd = false;
                    }
                }
            }
        }
        final ContainerStyle boardStyle = Bridge.getContainerStyle(board2.index());
        if (!Manager.ref().context().game().metadata().graphics().boardHidden()) {
            boardStyle.render(PlaneType.BOARD, context2);
            final String svg = boardStyle.containerSVGImage();
            final BufferedImage img = SVGUtil.createSVGImage(svg, imageSize, imageSize);
            if (!(boardStyle instanceof BoardlessStyle)) {
                g2d2.drawImage(img, 0, 0, imageSize, imageSize, 0, 0, img.getWidth(), img.getHeight(), null);
            }
        }
        Bridge.getContainerStyle(context2.board().index()).draw(g2d2, PlaneType.COMPONENTS, context2);
        Bridge.getContainerStyle(context2.board().index()).draw(g2d2, PlaneType.HINTS, context2);
        Bridge.getContainerStyle(context2.board().index()).draw(g2d2, PlaneType.COSTS, context2);
        image2 = new BufferedImage(imageSize, imageSize, 2);
        g2d2 = image2.createGraphics();
        g2d2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (!Manager.ref().context().game().metadata().graphics().boardHidden()) {
            boardStyle.render(PlaneType.BOARD, context2);
            final String svg = boardStyle.containerSVGImage();
            final BufferedImage img = SVGUtil.createSVGImage(svg, imageSize, imageSize);
            if (!(boardStyle instanceof BoardlessStyle)) {
                g2d2.drawImage(img, 0, 0, imageSize, imageSize, 0, 0, img.getWidth(), img.getHeight(), null);
            }
        }
        Bridge.getContainerStyle(context2.board().index()).draw(g2d2, PlaneType.COMPONENTS, context2);
        Bridge.getContainerStyle(context2.board().index()).draw(g2d2, PlaneType.HINTS, context2);
        Bridge.getContainerStyle(context2.board().index()).draw(g2d2, PlaneType.COSTS, context2);
        try {
            final File outputfile = new File("./thumb-" + Manager.ref().context().game().name() + "-b.png");
            ImageIO.write(image2, "png", outputfile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return image2;
    }
    
    public static void generateBoardThumbnail() {
        final int imageSize = DesktopApp.view().boardSize();
        BufferedImage image = new BufferedImage(imageSize, imageSize, 2);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        final Board board = Manager.ref().context().board();
        final Trial trial = new Trial(Manager.ref().context().game());
        final Context context = new Context(Manager.ref().context().game(), trial);
        Manager.ref().context().game().start(context);
        final ContainerStyle boardStyle = Bridge.getContainerStyle(board.index());
        if (!Manager.ref().context().game().metadata().graphics().boardHidden()) {
            boardStyle.render(PlaneType.BOARD, context);
            final String svg = boardStyle.containerSVGImage();
            final BufferedImage img = SVGUtil.createSVGImage(svg, imageSize, imageSize);
            if (!(boardStyle instanceof BoardlessStyle)) {
                g2d.drawImage(img, 0, 0, imageSize, imageSize, 0, 0, img.getWidth(), img.getHeight(), null);
            }
        }
        image = new BufferedImage(imageSize, imageSize, 2);
        g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (!Manager.ref().context().game().metadata().graphics().boardHidden()) {
            boardStyle.render(PlaneType.BOARD, context);
            final String svg = boardStyle.containerSVGImage();
            final BufferedImage img = SVGUtil.createSVGImage(svg, imageSize, imageSize);
            if (!(boardStyle instanceof BoardlessStyle)) {
                g2d.drawImage(img, 0, 0, imageSize, imageSize, 0, 0, img.getWidth(), img.getHeight(), null);
            }
        }
        try {
            final File outputfile = new File("./thumb-Board_" + Manager.ref().context().game().name() + ".png");
            ImageIO.write(image, "png", outputfile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        GraphicsCache.clearAllCachedImages();
        Manager.app.repaint();
    }
}
