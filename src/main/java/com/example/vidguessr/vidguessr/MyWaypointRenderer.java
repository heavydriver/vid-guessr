package com.example.vidguessr.vidguessr;/*
 * WaypointRenderer.java
 *
 * Created on March 30, 2006, 5:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointRenderer;

/**
 * A fancy waypoint painter
 * @author Martin Steiger
 */
public class MyWaypointRenderer implements WaypointRenderer<Waypoint>
{
    private static final Log log = LogFactory.getLog(MyWaypointRenderer.class);

    private BufferedImage origImage;

    /**
     * Uses a default waypoint image
     */
    public MyWaypointRenderer(String imageId)
    {
        URL resource = getClass().getResource("images/" + imageId + "MapMarker.png");

        try
        {
            assert resource != null;
            origImage = ImageIO.read(resource);
        }
        catch (Exception ex)
        {
            System.out.println(resource);
            log.warn("couldn't read playerMapMarker.png", ex);
        }
    }

    @Override
    public void paintWaypoint(Graphics2D graphics2D, JXMapViewer jxMapViewer, Waypoint waypoint) {
        Point2D point = jxMapViewer.getTileFactory().geoToPixel(waypoint.getPosition(), jxMapViewer.getZoom());

        int x = (int)point.getX();
        int y = (int)point.getY();

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );
        graphics2D.setRenderingHints(rh);

        graphics2D.drawImage(origImage, x - origImage.getWidth() / 2, y - origImage.getHeight(), null);
        graphics2D.dispose();
    }
}