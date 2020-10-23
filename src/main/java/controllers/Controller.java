// 
// Decompiled by Procyon v0.5.36
// 

package controllers;

import util.Context;
import util.locations.Location;

import java.awt.*;

public interface Controller
{
    Location calculateNearestLocationAll(final Context p0, final Point p1);
    
    Location calculateNearestLocation(final Context p0, final Point p1, final boolean p2);
}
