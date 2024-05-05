/**
 * A custom render to display markers on the map
 * @author Varun Mange
 * Collaborators: Azfar Islam, Martin Steiger
 * Teacher Name: Ms. Bailey
 * Period: 5th
 * Due Date: 5/10/2024
 */

package com.example.vidguessr.vidguessr.map;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

import com.example.vidguessr.vidguessr.Main;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointRenderer;

public class MyWaypointRenderer implements WaypointRenderer<Waypoint>
{
    private static final Log log = LogFactory.getLog(MyWaypointRenderer.class);

    private BufferedImage origImage;

    /**
     * Instantiates a new MyWaypointRenderer with the given image
     * @param imageId the image of the marker to use
     */
    public MyWaypointRenderer(String imageId)
    {
        URL resource = Main.class.getResource("images/" + imageId + "MapMarker.png");

        try
        {
            assert resource != null;
            origImage = ImageIO.read(resource);
        }
        catch (Exception e)
        {
            log.warn("couldn't read playerMapMarker.png", e);
        }
    }

    /**
     * Shows the marker on the given location
     * @param graphics2D the graphics2D object
     * @param jxMapViewer the map
     * @param waypoint the waypoint
     */
    @Override
    public void paintWaypoint(Graphics2D graphics2D, JXMapViewer jxMapViewer, Waypoint waypoint)
    {
        Point2D point = jxMapViewer.getTileFactory().geoToPixel(
                waypoint.getPosition(), jxMapViewer.getZoom());

        int x = (int)point.getX();
        int y = (int)point.getY();

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );

        graphics2D.setRenderingHints(rh);

        graphics2D.drawImage(origImage, x - origImage.getWidth() / 2,
                y - origImage.getHeight(), null);
        graphics2D.dispose();
    }
}