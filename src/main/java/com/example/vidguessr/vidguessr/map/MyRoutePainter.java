/**
 * Paints a dotted line between the location of the marker placed by the user and
 * the actual location of the video
 * @author Varun Mange
 * Collaborators: Azfar Islam, Martin Steiger
 * Teacher Name: Ms. Bailey
 * Period: 5th
 * Due Date: 5/10/2024
 */

package com.example.vidguessr.vidguessr.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.painter.Painter;

public class MyRoutePainter implements Painter<JXMapViewer>
{
    private boolean antiAlias = true;

    private List<GeoPosition> track;

    /**
     * Instantiates the MyRouterPainter with the given track
     * @param track the track
     */
    public MyRoutePainter(List<GeoPosition> track)
    {
        // copy the list so that changes in the
        // original list do not have an effect here
        this.track = new ArrayList<GeoPosition>(track);
    }

    /**
     * Uses graphics 2D to paint the route
     * @param g The Graphics2D to render to.
     * @param map an optional configuration parameter.
     * @param w width of the area to paint.
     * @param h height of the area to paint.
     */
    @Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int h)
    {
        g = (Graphics2D) g.create();

        // convert from viewport to world bitmap
        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        if (antiAlias)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // do the drawing
        g.setColor(Color.BLACK);
        final float[] dash1 = {10.0f};
        g.setStroke(new BasicStroke(1.5f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND,
                10.0f, dash1, 0.0f));

        drawRoute(g, map);

        g.dispose();
    }

    /**
     * Draws the dotted line between two points
     * @param g the graphics object
     * @param map the map
     */
    private void drawRoute(Graphics2D g, JXMapViewer map)
    {
        int lastX = 0;
        int lastY = 0;

        boolean first = true;

        for (GeoPosition gp : track)
        {
            // convert geo-coordinate to world bitmap pixel
            Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());

            if (first)
            {
                first = false;
            }
            else
            {
                g.drawLine(lastX, lastY, (int) pt.getX(), (int) pt.getY());
            }

            lastX = (int) pt.getX();
            lastY = (int) pt.getY();
        }
    }
}
