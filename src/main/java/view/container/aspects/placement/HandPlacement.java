// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.placement;

import topology.Cell;
import topology.Vertex;
import util.Context;
import view.container.BaseContainerStyle;

import java.awt.*;
import java.util.List;

public class HandPlacement extends ContainerPlacement
{
    public HandPlacement(final BaseContainerStyle containerStyle) {
        super(containerStyle);
    }
    
    @Override
    public void setPlacement(final Context context, final Rectangle placement) {
        this.placement = placement;
        this.setCellRadiusPixels((int)(0.6 * placement.height) / 2);
        this.setCellRadius(this.cellRadiusPixels() / (double)placement.width);
        if (this.cellRadiusPixels() > placement.width / this.container().numSites() / 2) {
            this.setCellRadiusPixels(placement.width / this.container().numSites() / 2);
            this.setCellRadius(1.0 / this.container().numSites() / 2.0);
        }
        this.setHandLocations(context);
    }
    
    protected void setHandLocations(final Context context) {
        int persistentSiteCount = 0;
        final List<Cell> sites = this.container().topology().cells();
        double xBuffer = 0.0;
        if (this.container().owner() > context.game().players().count()) {
            final double totalContainerCellWidth = this.cellRadiusPixels() * 2.0 * sites.size();
            final double difference = this.placement.getWidth() - totalContainerCellWidth;
            xBuffer = difference / 2.0 / this.placement.getWidth();
        }
        for (int site = 0; site < sites.size(); ++site) {
            final double xPosn = this.cellRadius() * 2.0 * (persistentSiteCount + 0.5) + xBuffer;
            final double yPosn = 0.0;
            this.topology().cells().get(site).setCentroid(xPosn, 0.0, 0.0);
            double minX = 99999.0;
            double minY = 99999.0;
            double maxX = -99999.0;
            double maxY = -99999.0;
            for (final Vertex vertex : this.topology().cells().get(site).vertices()) {
                if (vertex.centroid().getX() < minX) {
                    minX = vertex.centroid().getX();
                }
                if (vertex.centroid().getX() > maxX) {
                    maxX = vertex.centroid().getX();
                }
                if (vertex.centroid().getY() < minY) {
                    minY = vertex.centroid().getY();
                }
                if (vertex.centroid().getY() > maxY) {
                    maxY = vertex.centroid().getY();
                }
            }
            for (final Vertex vertex : this.topology().cells().get(site).vertices()) {
                final double normalisedVx = (vertex.centroid().getX() - minX) / (maxX - minX);
                final double normalisedVy = (vertex.centroid().getY() - minY) / (maxY - minY);
                vertex.setCentroid(normalisedVx, normalisedVy, 0.0);
            }
            final double widthToHeightRatio = this.cellRadius() * (this.placement.getWidth() / this.placement.getHeight());
            for (final Vertex vertex2 : this.topology().cells().get(site).vertices()) {
                final double vx = vertex2.centroid().getX();
                final double vy = vertex2.centroid().getY();
                vertex2.setCentroid(xPosn - this.cellRadius() + vx * this.cellRadius() * 2.0, 0.0 + widthToHeightRatio - vy * widthToHeightRatio * 2.0, 0.0);
            }
            ++persistentSiteCount;
        }
    }
}
