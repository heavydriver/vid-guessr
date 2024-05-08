/**
 * The map component of the game. Uses JXMapViewer2 library
 * @author Varun Mange
 * Collaborators: Azfar Islam, Martin Steiger
 * Teacher Name: Ms. Bailey
 * Period: 5th
 * Due Date: 5/10/2024
 */

package com.example.vidguessr.vidguessr.map;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

import javafx.scene.control.Button;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.*;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

public class MyMap extends JComponent
{
    public static final String MAP_BASE_URL = "https://api.maptiler.com/maps/streets-v2/256";
    public static final double EARTH_RADIUS = 6371.0;
    public static final double DEFAULT_LAT = 37.0902;
    public static final double DEFAULT_LNG = -95.7129;
    public static final double ZOOM_MAX_FRACTION = 0.7;
    public static final String PLAYER_MARKER_IMAGE_ID = "player";
    public static final String ACTUAL_LOCATION_MARKER_IMAGE_ID = "actualLocation";

    private JXMapViewer mapViewer;
    private double markerLat;
    private double markerLng;
    private Button confirmButton;

    /**
     * Instantiates a new Map component
     * @param button the "confirm" button
     */
    public MyMap(Button button)
    {
        confirmButton = button;
        drawMap();
    }

    /**
     * Draws the map component
     */
    private void drawMap()
    {
        TileFactoryInfo info = new MyMapTileFactoryInfo("", MAP_BASE_URL);
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);

        // Setup local file cache
        File cacheDir = new File(System.getProperty("user.home") +
                File.separator + ".jxmapviewer2");
        tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));

        // Setup JXMapViewer
        mapViewer = new JXMapViewer();
        mapViewer.setTileFactory(tileFactory);

        // the default lat lng
        GeoPosition defaultLocation = new GeoPosition(DEFAULT_LAT, DEFAULT_LNG);

        // sets the default zoom level
        mapViewer.setZoom(15);

        // set the map view over the default location
        mapViewer.setAddressLocation(defaultLocation);

        // add the pan, zoom, move functionality to the map
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        // listens for the user click on the map
        mapViewer.addMouseListener(new MapClickListener(mapViewer)
        {
            /**
             * Places a marker where the user clicked on the map
             * @param geoPosition The GeoPosition of the click event
             */
            @Override
            public void mapClicked(GeoPosition geoPosition)
            {
                GeoPosition marker = new GeoPosition(
                        geoPosition.getLatitude(), geoPosition.getLongitude());

                markerLat = geoPosition.getLatitude();
                markerLng = geoPosition.getLongitude();

                Set<Waypoint> waypoints = new HashSet<Waypoint>(
                        List.of(new DefaultWaypoint(marker)));

                WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
                waypointPainter.setWaypoints(waypoints);
                waypointPainter.setRenderer(new MyWaypointRenderer(PLAYER_MARKER_IMAGE_ID));

                mapViewer.setOverlayPainter(waypointPainter);
                confirmButton.setDisable(false);
            }
        });
    }

    /**
     * Gets the map to be displayed in the game
     * @return the map component
     */
    public JXMapViewer getMap()
    {
        return mapViewer;
    }

    /**
     * Gets the position the marker placed by the user
     * @return a GeoPosition object with the latitude and longitude of the location
     * where the user clicked on the map.
     */
    public GeoPosition getMarkerPos()
    {
        return new GeoPosition(markerLat, markerLng);
    }

    /**
     * Calculates and returns the distance between two geographical coordinates
     * based on the Haversine formula
     * @param markerPos the GeoPosition of the marker placed by the user
     * @param actualPos the GeoPosition of the video playing in the game
     * @return the distance between the two GeoPositions in kilometers
     */
    public double calculateDistance(GeoPosition markerPos, GeoPosition actualPos)
    {
        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(markerPos.getLatitude());
        double lon1Rad = Math.toRadians(markerPos.getLongitude());
        double lat2Rad = Math.toRadians(actualPos.getLatitude());
        double lon2Rad = Math.toRadians(actualPos.getLongitude());

        // Calculate the differences in coordinates
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        // Haversine formula
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(lat1Rad) *
                Math.cos(lat2Rad) * Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate the distance
        return EARTH_RADIUS * c;
    }

    /**
     * Draws a dotted line between the marker placed by the user and
     * the actual location of the video
     * @param markerPos the GeoPosition of the marker placed by the user
     * @param actualPos the GeoPosition of the video playing in the game
     */
    public void drawRoute(GeoPosition markerPos, GeoPosition actualPos)
    {
        List<GeoPosition> track = Arrays.asList(markerPos, actualPos);
        MyRoutePainter routePainter = new MyRoutePainter(track);

        // Set the focus
        mapViewer.zoomToBestFit(new HashSet<GeoPosition>(track), ZOOM_MAX_FRACTION);

        // Create waypoints from the geo-positions
        Set<Waypoint> playerWaypoints = new HashSet<Waypoint>(
                List.of(new DefaultWaypoint(markerPos)));
        WaypointPainter<Waypoint> playerWaypointsPainter = new WaypointPainter<Waypoint>();
        playerWaypointsPainter.setWaypoints(playerWaypoints);
        playerWaypointsPainter.setRenderer(new MyWaypointRenderer(PLAYER_MARKER_IMAGE_ID));

        Set<Waypoint> actualWaypoints = new HashSet<Waypoint>(
                List.of(new DefaultWaypoint(actualPos)));
        WaypointPainter<Waypoint> actualWaypointsPainter = new WaypointPainter<Waypoint>();
        actualWaypointsPainter.setWaypoints(actualWaypoints);
        actualWaypointsPainter.setRenderer(
                new MyWaypointRenderer(ACTUAL_LOCATION_MARKER_IMAGE_ID));

        // Create a compound painter that uses both the route-painter and the waypoint-painter
        List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
        painters.add(routePainter);
        painters.add(playerWaypointsPainter);
        painters.add(actualWaypointsPainter);

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
        mapViewer.setOverlayPainter(painter);
    }
}