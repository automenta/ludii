// 
// Decompiled by Procyon v0.5.36
// 

package view.component;

import game.equipment.component.Component;
import graphics.ImageProcessing;
import graphics.svg.SVGtoImage;
import metadata.graphics.Graphics;
import metadata.graphics.util.ValueLocationType;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class BaseComponentStyle implements ComponentStyle
{
    protected Component component;
    protected String svgName;
    protected Color colour;
    protected Color secondaryColour;
    protected double scale;
    protected ArrayList<SVGGraphics2D> imageSVG;
    protected Color edgeColour;
    protected boolean flipHorizontal;
    protected boolean flipVertical;
    protected int degreesRotation;
    protected ValueLocationType showValue;
    protected ValueLocationType showLocalState;
    protected String backgroundPath;
    protected double backgroundScale;
    protected Color backgroundColour;
    protected Color backgroundEdgeColour;
    protected String foregroundPath;
    protected double foregroundScale;
    protected Color foregroundColour;
    protected Color foregroundEdgeColour;
    
    public BaseComponentStyle(final Component component) {
        this.secondaryColour = Color.BLACK;
        this.scale = 1.0;
        this.imageSVG = new ArrayList<>();
        this.edgeColour = Color.BLACK;
        this.flipHorizontal = false;
        this.flipVertical = false;
        this.degreesRotation = 0;
        this.showValue = ValueLocationType.None;
        this.showLocalState = ValueLocationType.None;
        this.backgroundPath = "";
        this.backgroundScale = 1.0;
        this.backgroundColour = null;
        this.backgroundEdgeColour = null;
        this.foregroundPath = "";
        this.foregroundScale = 1.0;
        this.foregroundColour = null;
        this.foregroundEdgeColour = null;
        this.component = component;
    }
    
    @Override
    public void renderImageSVG(final Context context, final int imageSize, final int localState, final boolean secondary, final int maskedValue) {
        this.edgeColour = new Color(0, 0, 0);
        this.colour = null;
        this.backgroundColour = null;
        this.backgroundEdgeColour = null;
        this.foregroundColour = null;
        this.foregroundEdgeColour = null;
        final int g2dSize = (int)(imageSize * this.scale());
        SVGGraphics2D g2d = new SVGGraphics2D(g2dSize, g2dSize);
        String SVGNameLocal = this.component.getNameWithoutNumber();
        SVGNameLocal = this.genericMetadataChecks(context, localState);
        final String SVGPath = ImageUtil.getImageFullPath(SVGNameLocal);
        if (maskedValue == 1) {
            this.edgeColour = Color.BLACK;
            this.colour = Color.BLACK;
            this.secondaryColour = Color.BLACK;
            this.backgroundColour = Color.BLACK;
            this.backgroundEdgeColour = Color.BLACK;
            this.foregroundColour = Color.BLACK;
            this.foregroundEdgeColour = Color.BLACK;
            this.renderImage(g2d, SVGPath, context, imageSize, localState);
            g2d = this.imageSVG.get(localState);
            Color maskedColour = SettingsColour.playerColour(this.component.owner(), context);
            if (context.game().metadata().graphics().noMaskedColour()) {
                maskedColour = new Color(100, 100, 100);
            }
            this.colour = maskedColour;
            this.secondaryColour = maskedColour;
            this.edgeColour = maskedColour;
            this.backgroundColour = maskedColour;
            this.backgroundEdgeColour = maskedColour;
            this.foregroundColour = maskedColour;
            this.foregroundEdgeColour = maskedColour;
            this.renderImage(g2d, SVGPath, context, (int)(imageSize * 0.9), localState);
        }
        else {
            this.renderImage(g2d, SVGPath, context, imageSize, localState);
        }
    }
    
    private void renderImage(final SVGGraphics2D g2d, final String SVGPath, final Context context, final int imageSize, final int localState) {
        if (SVGPath != null) {
            this.renderImageSVGFromPath(g2d, context, imageSize, SVGPath, localState);
        }
        else {
            try {
                Integer.parseInt(this.svgName);
                this.createStringSVG(context, imageSize, localState, this.svgName);
            }
            catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public String genericMetadataChecks(final Context context, final int localState) {
        this.svgName = this.component.getNameWithoutNumber();
        final Graphics metadataGraphics = context.game().metadata().graphics();
        this.scale = metadataGraphics.pieceScale(this.component.owner(), this.component.name(), context);
        if (metadataGraphics.scalePiecesByValue()) {
            double maxValue = 1.0;
            for (final Component c : context.game().equipment().components()) {
                if (c != null && c.getValue() > maxValue) {
                    maxValue = c.getValue();
                }
            }
            this.scale = this.component.getValue() / maxValue;
        }
        final double scale = this.scale;
        this.foregroundScale = scale;
        this.backgroundScale = scale;
        final String nameExtension = metadataGraphics.pieceNameExtension(this.component.owner(), this.component.name(), context, localState);
        if (nameExtension != null) {
            this.svgName += nameExtension;
        }
        final String nameReplacement = metadataGraphics.pieceNameReplacement(this.component.owner(), this.component.name(), context, localState);
        if (nameReplacement != null) {
            this.svgName = nameReplacement;
        }
        final boolean addLocalStateToName = metadataGraphics.addStateToName(this.component.owner(), this.component.name(), context, localState);
        if (addLocalStateToName) {
            this.svgName += localState;
        }
        final Color pieceColour = metadataGraphics.pieceFillColour(this.component.owner(), this.component.name(), context, localState);
        if (pieceColour != null) {
            this.colour = pieceColour;
        }
        final Color pieceSecondaryColour = metadataGraphics.pieceSecondaryColour(this.component.owner(), this.component.name(), context, localState);
        if (pieceSecondaryColour != null) {
            this.secondaryColour = pieceSecondaryColour;
        }
        final Color pieceEdgeColour = metadataGraphics.pieceEdgeColour(this.component.owner(), this.component.name(), context, localState);
        if (pieceEdgeColour != null) {
            this.edgeColour = pieceEdgeColour;
        }
        this.flipHorizontal = metadataGraphics.pieceFlipHorizontal(this.component.owner(), this.component.name(), context);
        this.flipVertical = metadataGraphics.pieceFlipVertical(this.component.owner(), this.component.name(), context);
        this.degreesRotation = metadataGraphics.pieceRotate(this.component.owner(), this.component.name(), context);
        this.showValue = metadataGraphics.displayPieceValue(this.component.owner(), this.component.name(), context);
        this.showLocalState = metadataGraphics.displayPieceState(this.component.owner(), this.component.name(), context);
        if (this.component.isDie()) {
            this.showLocalState = ValueLocationType.None;
        }
        if (!this.component.isDie() && (SettingsVC.getPieceFamily(context.game().name()).equals("Abstract") || (SettingsVC.getPieceFamily(context.game().name()).equals("") && metadataGraphics.pieceFamilies() != null && Arrays.asList(metadataGraphics.pieceFamilies()).contains("Abstract") && SettingsVC.abstractPriority))) {
            this.svgName = ImageConstants.customImageKeywords[0];
        }
        SettingsVC.pieceStyleExtension = SettingsVC.getPieceFamily(context.game().name());
        if (SettingsVC.pieceStyleExtension.equals("") && metadataGraphics.pieceFamilies() != null) {
            SettingsVC.pieceStyleExtension = metadataGraphics.pieceFamilies()[0];
            if (SettingsVC.pieceStyleExtension.equals("Abstract") && !SettingsVC.abstractPriority) {
                SettingsVC.pieceStyleExtension = metadataGraphics.pieceFamilies()[1];
            }
        }
        if (!Arrays.asList(ImageConstants.defaultFamilyKeywords).contains(SettingsVC.pieceStyleExtension) && !SettingsVC.pieceStyleExtension.equals("Abstract") && !SettingsVC.pieceStyleExtension.equals("")) {
            this.svgName = this.svgName + "_" + SettingsVC.pieceStyleExtension;
        }
        if (this.colour == null) {
            this.colour = SettingsColour.playerColour(this.component.owner(), context);
        }
        if (this.svgName.length() == 1) {
            this.edgeColour = this.colour;
            this.colour = null;
        }
        if (metadataGraphics.pieceBackground(this.component.owner(), this.component.name(), context, localState) != null) {
            this.backgroundPath = metadataGraphics.pieceBackground(this.component.owner(), this.component.name(), context, localState).path;
            this.backgroundPath = ImageUtil.getImageFullPath(this.backgroundPath);
            this.backgroundScale = metadataGraphics.pieceBackground(this.component.owner(), this.component.name(), context, localState).scale;
            this.backgroundColour = metadataGraphics.pieceBackground(this.component.owner(), this.component.name(), context, localState).mainColour;
            this.backgroundEdgeColour = metadataGraphics.pieceBackground(this.component.owner(), this.component.name(), context, localState).secondaryColour;
            if (this.backgroundColour == null) {
                this.backgroundColour = SettingsColour.playerColour(this.component.owner(), context);
            }
            if (this.backgroundEdgeColour == null) {
                this.backgroundEdgeColour = Color.BLACK;
            }
        }
        if (metadataGraphics.pieceForeground(this.component.owner(), this.component.name(), context, localState) != null) {
            this.foregroundPath = metadataGraphics.pieceForeground(this.component.owner(), this.component.name(), context, localState).path;
            this.foregroundPath = ImageUtil.getImageFullPath(this.foregroundPath);
            this.foregroundScale = metadataGraphics.pieceForeground(this.component.owner(), this.component.name(), context, localState).scale;
            this.foregroundColour = metadataGraphics.pieceForeground(this.component.owner(), this.component.name(), context, localState).mainColour;
            this.foregroundEdgeColour = metadataGraphics.pieceForeground(this.component.owner(), this.component.name(), context, localState).secondaryColour;
            if (this.foregroundColour == null) {
                this.foregroundColour = SettingsColour.playerColour(this.component.owner(), context);
            }
            if (this.foregroundEdgeColour == null) {
                this.foregroundEdgeColour = Color.BLACK;
            }
        }
        return this.svgName;
    }
    
    public void renderImageSVGFromPath(final SVGGraphics2D g2d, final Context context, final int imageSize, final String filePathImage, final int localState) {
        while (this.imageSVG.size() <= localState) {
            this.imageSVG.add(null);
        }
        this.imageSVG.set(localState, this.getSVGImageFromFilePath(g2d, imageSize, filePathImage, localState, this.colour, this.edgeColour));
    }
    
    public SVGGraphics2D getSVGImageFromFilePath(final SVGGraphics2D g2dOriginal, final int dim, final String filePath1, final int localState, final Color fill, final Color edge) {
        SVGGraphics2D g2d = g2dOriginal;
        int imageSize = (int)(dim * this.scale);
        if (imageSize % 2 == 1) {
            --imageSize;
        }
        if (this.backgroundPath.length() > 0) {
            g2d = this.getBackground(g2d, dim);
        }
        if (Arrays.asList(ImageConstants.customImageKeywords).contains(filePath1)) {
            if (filePath1.equalsIgnoreCase("ball") || filePath1.equalsIgnoreCase("seed")) {
                if (imageSize > 1) {
                    ImageProcessing.ballImage(g2d, 0, 0, imageSize / 2, fill);
                }
            }
            else if (filePath1.equalsIgnoreCase("ring")) {
                if (imageSize > 1) {
                    ImageProcessing.ringImage(g2d, 0, 0, imageSize, fill);
                }
            }
            else if (filePath1.equalsIgnoreCase("chocolate") && imageSize > 1) {
                ImageProcessing.chocolateImage(g2d, imageSize, 4, fill);
            }
        }
        else {
            SVGtoImage.loadFromString(g2d, filePath1, imageSize, 0, 0, edge, fill, true);
        }
        if (this.foregroundPath.length() > 0) {
            g2d = this.getForeground(g2d, dim);
        }
        final Font valueFontCorner = new Font("Arial", 1, dim / 4);
        g2d.setColor(this.secondaryColour);
        g2d.setFont(valueFontCorner);
        if (this.showValue == ValueLocationType.Corner) {
            final String printvalue = Integer.toString(this.component.getValue());
            final Rectangle2D rect = valueFontCorner.getStringBounds(printvalue, g2d.getFontRenderContext());
            g2d.drawString(printvalue, (int)(dim * 0.1), (int)rect.getHeight());
        }
        if (this.showLocalState == ValueLocationType.Corner && localState != 100) {
            final String printvalue = Integer.toString(localState);
            final Rectangle2D rect = valueFontCorner.getStringBounds(printvalue, g2d.getFontRenderContext());
            g2d.drawString(printvalue, (int)(dim * 0.1), (int)rect.getHeight());
        }
        final Font valueFontMiddle = new Font("Arial", 1, dim / 3);
        g2d.setColor(this.secondaryColour);
        g2d.setFont(valueFontMiddle);
        if (this.showValue == ValueLocationType.Middle) {
            final String printvalue2 = Integer.toString(this.component.getValue());
            final Rectangle2D rect2 = valueFontMiddle.getStringBounds(printvalue2, g2d.getFontRenderContext());
            g2d.drawString(printvalue2, (int)(dim * 0.5 - rect2.getWidth() / 1.5), (int)(dim * 0.5 + rect2.getHeight() / 4.0));
        }
        if (this.showLocalState == ValueLocationType.Middle && localState != 100) {
            final String printvalue2 = Integer.toString(localState);
            final Rectangle2D rect2 = valueFontMiddle.getStringBounds(printvalue2, g2d.getFontRenderContext());
            g2d.drawString(printvalue2, (int)(dim * 0.5 - rect2.getWidth() / 1.5), (int)(dim * 0.5 + rect2.getHeight() / 4.0));
        }
        return g2d;
    }
    
    protected void createStringSVG(final Context context, final int imageSize, final int localState, final String text) {
        this.setImageSVG(localState, new SVGGraphics2D(imageSize, imageSize, new StringBuilder()));
        final Graphics2D g2d = this.imageSVG.get(localState);
        final Font valueFont = new Font("Arial", 1, imageSize);
        g2d.setColor(Color.BLACK);
        g2d.setFont(valueFont);
        final Rectangle2D rect = valueFont.getStringBounds(text, g2d.getFontRenderContext());
        g2d.drawString(text, (int)(this.imageSVG.get(localState).getWidth() / 2 - rect.getWidth() / 2.0), (int)(this.imageSVG.get(localState).getHeight() / 2 + rect.getHeight() / 3.0));
    }
    
    @Override
    public ArrayList<SVGGraphics2D> getAllImageSVGs() {
        return this.imageSVG;
    }
    
    @Override
    public SVGGraphics2D getImageSVG(final int localState) {
        if (localState < this.imageSVG.size()) {
            return this.imageSVG.get(localState);
        }
        if (this.imageSVG.size() > 0) {
            return this.imageSVG.get(0);
        }
        return null;
    }
    
    @Override
    public void setImageSVG(final int index, final SVGGraphics2D g) {
        while (this.imageSVG.size() <= index) {
            this.imageSVG.add(null);
        }
        this.imageSVG.set(index, g);
    }
    
    protected SVGGraphics2D getBackground(final SVGGraphics2D g2d, final int dim) {
        final int tileSize = (int)(dim * this.backgroundScale);
        SVGtoImage.loadFromString(g2d, this.backgroundPath, tileSize, 0, 0, this.backgroundEdgeColour, this.backgroundColour, true);
        return g2d;
    }
    
    protected SVGGraphics2D getForeground(final SVGGraphics2D g2d, final int dim) {
        final int tileSize = (int)(dim * this.foregroundScale);
        SVGtoImage.loadFromString(g2d, this.foregroundPath, tileSize, 0, 0, this.foregroundEdgeColour, this.foregroundColour, true);
        return g2d;
    }
    
    @Override
    public double scale() {
        return Math.max(Math.max(this.scale, this.backgroundScale), this.foregroundScale);
    }
    
    @Override
    public ArrayList<Point> origin() {
        return new ArrayList<>();
    }
    
    @Override
    public ArrayList<Point2D> getLargeOffsets() {
        return new ArrayList<>();
    }
    
    @Override
    public Point largePieceSize() {
        return new Point();
    }
    
    @Override
    public boolean flipHorizontal() {
        return this.flipHorizontal;
    }
    
    @Override
    public boolean flipVertical() {
        return this.flipVertical;
    }
    
    @Override
    public int rotationDegrees() {
        return this.degreesRotation;
    }
    
    public Color getColour() {
        return this.colour;
    }
    
    @Override
    public void setColour(final Color c) {
        this.colour = c;
    }
    
    @Override
    public Color getSecondaryColour() {
        return this.secondaryColour;
    }
    
    public void setSecondaryColour(final Color c) {
        this.secondaryColour = c;
    }
    
    @Override
    public void setName(final String name) {
        this.svgName = name;
    }
}
